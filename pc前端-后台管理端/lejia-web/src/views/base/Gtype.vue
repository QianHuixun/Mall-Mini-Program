<!--
* @description 商品分类
* @fileName Gtype.vue
* @author zs
* @date 2024/05/10
!-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="enabled" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList">
          </el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" :loading="downLoading" @click="handleDownload">
          模板下载
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".xlsx, .xls">
          <el-button type="primary" size="medium">
            导入
          </el-button>
        </el-upload>
        <el-button type="primary" icon="el-icon-download" :loading="downLoading" size="medium"
          @click="handleImportExcel">
          导出
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <div class="title-div">
        <span>分类名称</span>
        <div class="title-edit-div">
          <span>状态</span>
          <span style="width:100px;">操作</span>
        </div>
      </div>
      <el-tree :data="tableData" node-key="pkey" @node-drop="handleDrop" draggable :allow-drop="allowDrop"
        :allow-drag="allowDrag" :expand-on-click-node="false" accordion :props="defaultProps" :default-expanded-keys="[expandedPkey]">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span class="title-span">
            <el-image v-if="data.level === 1 && data.photo" :src="data.photo" :preview-src-list="[data.photo]"></el-image>
            {{ node.label }}
          </span>
          <div class="edit-div">
            <el-switch active-color="#13ce66" v-model="data.enabled"
              @change="handleStatus(data.enabled,data.pkey,data.level)">
            </el-switch>
            <div class="button-div">
              <el-button type="text" size="mini" @click="handleEdit(data)">
                编辑
              </el-button>
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(node, data)">
                <el-button slot="reference" size="mini" type="danger">
                  删除
                </el-button>
              </el-popconfirm>
            </div>
          </div>

        </span>
      </el-tree>
    </div>
    <progress-dlog ref="ProgressDlog" :title="'导入'" @refresh="getData" :uploadPercent.sync="uploadPercent">
    </progress-dlog>
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
  import qs from 'qs';
  import ProgressDlog from '@/components/global/ProgressDlog';
  import AddComp from "./sub/GtypeAdd.vue";
  import EditComp from "./sub/GtypeEdit.vue";
  export default {
    data() {
      return {
        expandedPkey:"",
        loading: false,
        downLoading: false,
        uploadPercent: 0,
        tableData: [],
        defaultProps: {
          children: 'gtypeLowerList',
          label: 'name'
        },
        searchKey: 'name',
        selectOptions: [{
          name: '分类名称',
          key: 'name',
        }, ],
        enabledList: [{
            pkey: '',
            name: '启停',
          },
          {
            pkey: 'true',
            name: '启用',
          },
          {
            pkey: 'false',
            name: '停用',
          },
        ],
        enabled: '',
        keywords: '', // 搜索关键字
      };
    },
    mounted() {
      this.getData();
    },
    components: {
      ProgressDlog,
      AddComp,
      EditComp
    },
    computed: {
      /**
       * 获取菜单标题
       * @return {[title]} [返回从state状态中获取的选中菜单名]
       */
      title() {
        return this.$store.state.activeName;
      },
    },
    methods: {
      /**
       * 拖拽配置
       */
      handleDrop(draggingNode, dropNode, dropType, ev) {
        this.loading = true;
        const params = {
          pkey: draggingNode.data.pkey,
          level: draggingNode.data.level,
        };
        if (dropType == 'before') {
          params['agoPkey'] = dropNode.data.pkey;
        } else if (dropType == 'after') {
          params['afterPkey'] = dropNode.data.pkey;
        }
        axios
          .post(api.mall.gtypeSort, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
        console.log('tree drop: ', draggingNode, dropNode, dropType);
      },
      allowDrop(draggingNode, dropNode, type) {
        console.log(draggingNode, dropNode, type, 'allowDrop');
        if (type == 'inner') {
          return false;
        } else if (draggingNode.data.higherLevelPkey != dropNode.data.higherLevelPkey) {
          return false;
        }
        return true;
      },
      allowDrag(draggingNode) {
        console.log(draggingNode, 'draggingNode');
        return true;
      },

      handelAdd() {
        this.$refs.AddComp.show();
      },
      /**
       * 点击修改
       */
      handleEdit: function (row) {
        this.$refs.EditComp.show({
          row: row
        });
      },
      handleChange: function () {
        this.getData();
      },
      /**
       * 开始搜索
       */
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
        this.getData();
      },

      /**
       * 导入
       */
      async handleImport(file) {
        let acceptType = [
          '.csv',
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          'application/vnd.ms-excel',
        ];
        if (!acceptType.includes(file.file.type)) {
          this.$message.warning('请导入excel文件');
          return;
        }
        let _this = this;
        let params = {};
        params = new FormData();
        params.append('myfile', file.file);
        axios
          .post(api.mall.gtypeImportexcel, params, {
            headers: {
              'Content-Type': 'multipart/form-data;charset=UTF-8',
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
          })
          .then(function (response) {
            console.log(response);
            let blob = new Blob([response.data], {
              type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
            });

            if (blob.size == 0) {
              // _this.leadingVisible = true;
              _this.$refs.ProgressDlog.show();
              var timer = setInterval(() => {
                _this.uploadPercent = _this.uploadPercent + 1;
                if (_this.uploadPercent >= 100) {
                  _this.uploadPercent = 0;
                  // _this.leadingVisible = false;
                  _this.$refs.ProgressDlog.hide();
                  clearInterval(timer);
                  _this.$message.success('恭喜你，导入成功');
                  _this.getData();
                }
              }, 50);
            } else {
              _this.$refs.ProgressDlog.show();
              var timer = setInterval(() => {
                _this.uploadPercent = _this.uploadPercent + 1;
                if (_this.uploadPercent >= 100) {
                  _this.uploadPercent = 0;
                  _this.$refs.ProgressDlog.hide();
                  clearInterval(timer);
                  const h = _this.$createElement;
                  _this.$msgbox({
                    title: '提示',
                    message: h('div', null, '导入出错，请下载出错数据重新导入'),
                    confirmButtonText: '确定',
                    callback: (action) => {
                      let objectUrl = URL.createObjectURL(blob);
                      let link = document.createElement('a');
                      link.style.display = 'none';
                      link.href = objectUrl;
                      link.setAttribute('download', '出错数据.xls');
                      document.body.appendChild(link);
                      link.click();
                      _this.getData();
                    },
                  });
                }
              }, 50);
            }
          })
          .catch(function (error) {});
      },

      /**列表导出 */
      handleImportExcel() {
        const params = {
          enabled: this.enabled
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
          .post(api.mall.gtypeExportExcel, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
            timeout: 0,
          })
          .then((res) => {
            let data = new Blob([res.data], {
              type: 'application/json',
            });
            var reader = new FileReader();
            reader.addEventListener('loadend', function (e) {
              if (e.target.result.indexOf('result') > 0) {
                let result = JSON.parse(e.target.result);
                that.downLoading = false;
                that.$message.error(result.codeMsg);
                return;
              } else {
                let blob = new Blob([res.data], {
                  type: 'application/vnd.ms-excel',
                });
                if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                  window.navigator.msSaveOrOpenBlob(
                    blob,
                    `${'商品分类' }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${'商品分类'}.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.downLoading = false;
                that.$message.success('导出成功');
              }
            });
            reader.readAsText(data);
          });
      },

      /**
       * 模板下载
       */
      handleDownload: function () {
        let that = this;
        this.downLoading = true;
        axios
          .post(api.mall.gtypeDownTemplate, qs.stringify({}), {
            headers: {
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
            timeout: 0,
          })
          .then((res) => {
            let data = new Blob([res.data], {
              type: 'application/json',
            });
            var reader = new FileReader();
            reader.addEventListener('loadend', function (e) {
              if (e.target.result.indexOf('result') > 0) {
                let result = JSON.parse(e.target.result);
                that.downLoading = false;
                that.$message.error(result.codeMsg);
                return;
              } else {
                let blob = new Blob([res.data], {
                  type: 'application/vnd.ms-excel',
                });
                if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                  window.navigator.msSaveOrOpenBlob(
                    blob,
                    `${'商品分类模板 ' }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${'商品分类模板'}.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.downLoading = false;
                that.$message.success('下载成功');
              }
            });
            reader.readAsText(data);
          });
      },

      /**
       * 启停状态
       * @param  {[type]} status [新状态值]
       * @param  {[type]} pkey   [记录的pkey]
       * @return {[type]}        [description]
       */
      handleStatus: function (status, pkey, level) {
        let url = '',
          text = '',
          params = {
            pkey: pkey,
          };
        if (status) {
          url = level == 1 ? api.mall.startClassic : (level == 2 ? api.mall.startGoods : api.mall.startThreeGoods);
          text = '启用';
        } else {
          url = level == 1 ? api.mall.stopClassic : (level == 2 ? api.mall.stopGoods : api.mall.stopThreeGoods);
          text = '停用';
        }
        axios
          .post(url, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.$message.success(text + '成功');
          });
      },

      /**
       * 删除
       */
      handleDelete: function (node, row) {
        const params = {
          pkey: row.pkey,
        };
        let url = row.level == 1 ? api.mall.delClassic : (row.level == 2 ? api.mall.delGoods : api.mall
          .delThreeGoods);
        axios
          .post(url, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.$message.success('删除成功');
            const parent = node.parent;
            const children = parent.data.gtypeLowerList || parent.data;
            const index = children.findIndex(d => d.pkey === row.pkey);
            children.splice(index, 1);
          });
      },


      /**
       * 获取列表
       */
      getData: function (pkey) {
        this.loading = true;
        this.expandedPkey = "";
        const params = {
          enabled: this.enabled
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.mall.gtypeQuery, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.tableData = response;
            if(pkey){
              this.expandedPkey = pkey;
            }
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },
    },
  };
</script>
<style lang="less" scoped>
  /deep/ .el-switch {
    // border: green solid 1px !important;
    box-shadow: none;
  }

  .title-div {
    height: 50px;
    line-height: 50px;
    border: 1px solid #e5e4e9;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    font-weight: bold;
    padding-right: 8px;
    padding-left: 30px;
    background: #F5F7FA;

    .title-edit-div {
      width: 300px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }

  .el-tree {
    border-left: 1px solid #e5e4e9;
    border-right: 1px solid #e5e4e9;
  }

  /deep/ .el-tree-node__content {
    height: 40px;
    line-height: 40px;
    border-bottom: 1px solid #e5e4e9;
  }

  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;

    .edit-div {
      width: 300px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .button-div {
        display: flex;
        width: 100px;
        align-items: center;
        justify-content: space-between;
      }
    }
    .title-span {
      display: flex;
      align-items: center;
    }
    .el-image {
      width: 30px;
      height: 30px;
      margin: 0 8px
    }
  }
</style>