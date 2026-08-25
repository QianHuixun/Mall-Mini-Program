
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="status" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <el-select v-model="types" @change="handleChange" placeholder="选择卡类型" clearable>
          <el-option value="NORMAL" key="NORMAL" label="普通充值"></el-option>
          <el-option value="MSD" key="MSD" label="热力豆充值"></el-option>
        </el-select>
        <el-date-picker v-model="createDate" type="daterange" range-separator="至" start-placeholder="创建开始日期"
          end-placeholder="创建结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-date-picker v-model="userDate" type="daterange" range-separator="至" start-placeholder="使用开始日期"
          end-placeholder="使用结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">共</span>
            <span class="count-num-xs">{{ sumData.num }}</span>
            <span class="count-tit">条</span>
            <span class="count-tit">面值</span>
            <span class="count-num-xs">{{ sumData.sumCost }}</span>
            <span class="count-tit">;</span>
          </div>
          <div class="count-box">
            <span class="count-tit">已用</span>
            <span class="count-num-xs">{{ sumData.useNum }}</span>
            <span class="count-tit">张 面值</span>
            <span class="count-num-xs">{{ sumData.sumUseCost }}</span>
            <span class="count-tit">元</span>
          </div>
        </div>
        <el-button type="primary" size="medium" @click="handleAdd">
          批量生成卡密
        </el-button>
        <el-button type="primary" size="medium" @click="handleDownLoad">
          充值模板
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".xlsx, .xls">
          <el-button type="primary" size="medium">
            导入卡密
          </el-button>
        </el-upload>
        <el-button type="primary" size="medium" @click="handleImportExcel" :loading="downLoading">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="面值" prop="cost"></el-table-column>
        <el-table-column label="卡号" prop="cardNumber" min-width="140"></el-table-column>
        <el-table-column label="卡密" prop="cardPassword"></el-table-column>
        <el-table-column label="卡类型" prop="typeName" min-width="100"></el-table-column>
        <el-table-column label="标签" prop="tagName"></el-table-column>
        <el-table-column label="截止时间" prop="deadline" min-width="160"></el-table-column>
        <el-table-column label="状态" prop="statusName"></el-table-column>
        <el-table-column label="使用人" prop="mobile" min-width="120"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"  min-width="160"></el-table-column>
        <el-table-column label="使用时间" prop="useTime"  min-width="160"></el-table-column>
        <el-table-column label="操作" width="120px" fixed="right">
          <template slot-scope="scope">
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" >
              <el-button slot="reference" size="mini" type="danger">作废</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <recharge-card-add ref="rechargeCardAdd" title="批量新增卡号" @refresh="handleChange"></recharge-card-add>
    <progress-dlog ref="ProgressDlog" :title="'批量充值'" @refresh="handleChange" :uploadPercent.sync="uploadPercent"></progress-dlog>
  </div>
</template>
<script>
  import qs from 'qs';
  import RechargeCardAdd from './sub/RechargeCardAdd.vue';
  import ProgressDlog from '@/components/global/ProgressDlog';
  export default {
    data() {
      return {
        loading: false,
        searchKey: 'cardNumber',
        selectOptions: [{ name: '卡号', key: 'cardNumber' }, { name: '使用人', key: 'mobile' }],
        tableData: [],
        sumData: {
          num: 0,
          sumCost: 0,
          useNum: 0,
          sumUseCost: 0,
        },
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: '', // 搜索关键字
        total: 0, //总页数
        downLoading: false,
        statusList: [{
            pkey: "",
            name: "全部"
          },
          {
            pkey: "UNUSED",
            name: "未使用"
          },
          {
            pkey: "USED",
            name: "已使用"
          },
          {
            pkey: "CANCEL",
            name: "已作废"
          }
        ],
        status: "",
        types: "",
        createDate: "",
        userDate: "",
        uploadPercent: 0,
      };
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
    components: { RechargeCardAdd, ProgressDlog },
    mounted() {
      this.getData();
      this.getSumData()
    },
    methods: {
      /**
       * 批量生成卡密
       */
      handleAdd() {
        this.$refs.rechargeCardAdd.show()
      },
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
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
        this.page = 1;
        this.getData();
        this.getSumData()
      },
      handleChange: function () {
        this.page = 1;
        this.getData();
        this.getSumData()
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
          status: this.status,
          types: this.types,
          useStart: this.userDate && this.userDate.length ? this.userDate[0] : '',
          useEnd: this.userDate && this.userDate.length ? this.userDate[1] : '',
          createdStart: this.createDate && this.createDate.length ? this.createDate[0] : '',
          createdEnd: this.createDate && this.createDate.length ? this.createDate[1] : '',
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.marketing.rechargeCardQuery, qs.stringify(params))
          .then((response) => {
            this.tableData = response.content;
            this.total = response.total;

            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },

      /**
       * 获取合计数据
       */
      getSumData() {
        this.loading = true;
        const params = {
          status: this.status,
          types: this.types,
          useStart: this.userDate && this.userDate.length ? this.userDate[0] : '',
          useEnd: this.userDate && this.userDate.length ? this.userDate[1] : '',
          createdStart: this.createDate && this.createDate.length ? this.createDate[0] : '',
          createdEnd: this.createDate && this.createDate.length ? this.createDate[1] : '',
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.marketing.rechargeCardQuerySum, qs.stringify(params))
          .then((response) => {
            this.sumData = response
          });
      },
      

      /**导入模板 */
      handleDownLoad() {
        axios
          .post(api.marketing.rechargeCardDownTemplate, '', {
            responseType: 'blob',
            timeout: 0,
          })
          .then((res) => {
            let that = this
            let data = new Blob([res.data], {
              type: 'application/json',
            });
            var reader = new FileReader();
            reader.addEventListener('loadend', function (e) {
              if (e.target.result.indexOf('result') > 0) {
                let result = JSON.parse(e.target.result);
                that.$message.error(result.codeMsg);
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
                    `${'导入模板'
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
                    `${'导入模板'
                    }.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.$message.success('下载成功');
              }
            });
            reader.readAsText(data);
          });
      },

      /**列表导出 */
      handleImportExcel() {
        const params = {
          status: this.status,
          types: this.types,
          useStart: this.userDate && this.userDate.length ? this.userDate[0] : '',
          useEnd: this.userDate && this.userDate.length ? this.userDate[1] : '',
          createdStart: this.createDate && this.createDate.length ? this.createDate[0] : '',
          createdEnd: this.createDate && this.createDate.length ? this.createDate[1] : '',
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
          .post(api.marketing.rechargeCardExport, qs.stringify(params), {
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
                var disposition = res.headers['content-disposition'];
                var headersFileName = disposition ? disposition.split('=') : '';
                if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                  window.navigator.msSaveOrOpenBlob(
                    blob,
                    headersFileName && headersFileName.length != 0 ?
                    decodeURI(headersFileName[1]) :
                    `${'充值卡密管理'
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
                    `${'充值卡密管理'
                    }.xlsx`
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
          axios
            .post(api.marketing.rechargeCardImport, params, {
              headers: {
                'Content-Type': 'multipart/form-data;charset=UTF-8',
                Authorization: this.$store.state.token,
              },
              responseType: 'blob',
            })
            .then(function (response) {
               // _this.leadingVisible = true;
                _this.$refs.ProgressDlog.show();
                const timer = setInterval(() => {
                  _this.uploadPercent = _this.uploadPercent + 1;
                  if (_this.uploadPercent >= 100) {
                    _this.uploadPercent = 0;
                    // _this.leadingVisible = false;
                    _this.$refs.ProgressDlog.hide();
                    clearInterval(timer);
                  }
                }, 50);
              console.log(response);
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
                _this.handleChange();
              } else {
                  const h = _this.$createElement;
                    _this.$msgbox({
                      title: '提示',
                      message: h('div', null, '导入出错，请下载出错数据重新导入'),
                      confirmButtonText: '确定',
                      callback: () => {
                        let objectUrl = URL.createObjectURL(blob);
                        let link = document.createElement('a');
                        link.style.display = 'none';
                        link.href = objectUrl;
                        link.setAttribute('download', '出错数据.xls');
                        document.body.appendChild(link);
                        link.click();
                        _this.handleChange();
                      },
                    });
              }
            })
            .catch(function () {});
      },

      /**
       * 作废
       */
      handleDelete(row) {
        const params = {
          keys: row.pkey
        }
        axios.post(api.marketing.rechargeCardCancel, this.$qs.stringify(params))
          .then(() => {
            this.$message.success('作废成功！')
            this.handleChange()
          })
      }
    },
  };
</script>

<style lang="less" scoped>
.count-container {
  display: inline-block;
  padding-right: 10px;

  .count-box {
    display: inline-block;
    padding: 0 10px;

    .count-tit {
      padding-right: 5px;
    }

    .count-num-rk {
      color: #67c23a;
    }

    .count-num-xs {
      color: #409eff;
    }
  }
}
</style>
