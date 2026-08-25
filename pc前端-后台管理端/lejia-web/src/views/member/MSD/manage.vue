<!--
 * @description 热力豆管理
 * @fileName TagManage.vue
 * @author zs
 * @date 2025/08/19
 !-->
<template lang="html">
  <div class="table-container">
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
        <el-select v-model="tags" multiple collapse-tags clearable placeholder="用户标签" style="width: 220px" @change="handleChange">
          <el-option v-for="item in tagsList" :key="item.pkey" :value="item.pkey" :label="item.name"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" size="medium" @click="handelConfig">
          热力豆配置
        </el-button>
        <el-button type="primary" size="medium" @click="handleDownLoad">
          充值模板
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".xlsx, .xls">
          <el-button type="primary" size="medium">
            批量充值
          </el-button>
        </el-upload>
        <el-button type="primary" size="medium" @click="handleExport">
          导出
        </el-button>
        <!-- <el-button type="primary" size="medium" @click="handelClear">
          清空热力豆
        </el-button> -->
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="用户标签" prop="tagName"></el-table-column>
        <el-table-column label="热力豆余额" prop="balance"></el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              调整余额
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <clear ref="clear" title="清空热力豆" @refresh="handleRefresh"></clear>
    <edit ref="edit" title="调整余额" @refresh="handleRefresh"></edit>
    <progress-dlog ref="ProgressDlog" :title="'批量充值'" @refresh="handleRefresh" :uploadPercent.sync="uploadPercent"></progress-dlog>
    <config-dialog ref="configDialog"></config-dialog>
  </div>
</template>
<script>
import clear from './clear.vue';
import edit from './edit.vue';
import ProgressDlog from '@/components/global/ProgressDlog';
import configDialog from "./configDialog.vue";
export default {
  data() {
    return {
      loading: false,
      searchKey: 'mobile',
      selectOptions: [{ name: '手机号', key: 'mobile' }],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      tags: [],
      tagsList: [],
      uploadPercent: 0,
    };
  },
  components: {
    clear,
    edit,
    ProgressDlog,
    configDialog
  },
  mounted() {
    this.getData();
    this.getTagList()
  },
  methods: {
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    handleChange() {
      this.getData()
    },
    /**
     * 下载充值模板
     */
    /**列表导出 */
    handleDownLoad() {
      axios
        .post(api.marketing.msdRechargeTemplate, '', {
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
              this.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              var disposition = res.headers['content-disposition'];
              var headersFileName = disposition ? disposition.split('=') : '';
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(
                  blob,
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'充值模板'
                  }.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'充值模板'
                  }.xlsx`
                );
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              this.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    /**
     * 导入批量充值
     */
    handleImport(file) {
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
        _this.$refs.ProgressDlog.show(); 
        axios
          .post(api.marketing.msdRechargeImport, params, {
            headers: {
              'Content-Type': 'multipart/form-data;charset=UTF-8',
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
            onUploadProgress(progress) { 
              _this.uploadPercent = Math.round((progress.loaded / progress.total) * 99); 
            }, 
          })
          .then(function (response) {
            console.log(response);
            _this.uploadPercent = 100 
            _this.$refs.ProgressDlog.hide(); 
            setTimeout(() => { 
              _this.uploadPercent = 0 
            }, 100) 
            if (response.data.type == "application/json") { 
              //适配不同的接口有些导入成功或者失败会返回json格式的数据 
              const reader = new FileReader(); 
              reader.onload = function () { 
                const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容 
                console.log(msgResult, "msgResult"); 
                if (msgResult.success) { 
                  _this.$message.success('恭喜你，导入成功'); 
                } else { 
                  _this.$message.warning(msgResult.msg || "文件错误"); 
                } 
                setTimeout(() => { 
                  _this.getData(); 
                }, 1000); 
              }; 
              reader.readAsText(response.data); 
              return; 
            }
            let blob = new Blob([response.data], {
              type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
            });

            if (blob.size == 0) {
              _this.$message.success('恭喜你，导入成功'); 
              _this.getData(); 
            } else {
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
          })
          .catch(function (error) {});
    },
    /**
     * 导出热力豆总览
     */
    handleExport() {
      const params = {};
      params[this.searchKey] = this.keywords;
      params.tags = this.tags.join(',');
      axios
        .post(api.marketing.msdExport, this.$qs.stringify(params), {
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
              this.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              var disposition = res.headers['content-disposition'];
              var headersFileName = disposition ? disposition.split('=') : '';
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(
                  blob,
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  '热力豆总览.xlsx'
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  '热力豆总览.xlsx'
                );
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              this.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    /**
     * 清空热力豆
     */
    handelClear() {
      this.$refs.clear.show()
    },

    /**
     * 热力豆配置按钮点击弹出对话框
     */
    handelConfig:function() {
      this.$refs.configDialog.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.edit.show(row);
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.marketing.tagsDel, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    handleRefresh() {
      this.getData()
      this.$emit('refresh')
    },
    /**
     * 获取列表
     * @return {[type]} [description]
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.msdQuery, this.$qs.stringify(params))
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    getTagList() {
      axios
        .post(api.marketing.msdTagDrop)
        .then((response) => {
          this.tagsList = response
        });
    }
  },
};
</script>
