<!--
* @description 发放记录
* @fileName CouponEventsRecords.vue
* @author zs
* @date 2024/04/26
!-->
<template lang="html">
  <el-dialog :title="row.name + '-发放记录'" :visible.sync="visible" :closeOnClickModal="false" width="1500px"
    :modal-append-to-body="false">
    <div class="table-container">
      <!-- <h1 class="title">
        库存管理
      </h1> -->
      <!-- 搜索栏 -->
      <div class="search-box">
        <!-- 搜索表单 -->
        <div class="search-box-form">
          <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
          </search-bar>
          <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
          </el-date-picker>
        </div>
        <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel"
            :loading="downLoading">
            导出
          </el-button>
        </div>
      </div>
      <!-- 表格框 -->
      <div class="table-box">
        <el-table :data="tableData" :loading="loading" border style="width: 100%">
          <el-table-column label="会员手机号" prop="memberMobile"></el-table-column>
          <el-table-column label="领取时间" prop="payTime"></el-table-column>
          <el-table-column label="付款金额" prop="amt"></el-table-column>
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize"
          @current-change="handleCurrentChange"></el-pagination>
      </div>
    </div>
  </el-dialog>
</template>
<script>
  import qs from "qs";
  export default {
    data() {
      return {
        visible: false,
        loading: false,
        tableData: [],
        total: 0, //总页数
        pageSize: 8, //一页的数量
        page: 1, //页数
        row: {},
        searchKey: 'memberMobile',
        selectOptions: [{
          name: '手机号',
          key: 'memberMobile',
        }, ],
        keywords: '', // 搜索关键字
        date: "",
        downLoading: false, //导出加载
      };
    },
    computed: {},
    components: {},
    mounted() {},
    methods: {
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
      },
      handleChange: function () {
        this.page = 1;
        this.getData();
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      /**清空数据 */
      clearData() {
        this.tableData = [];
        this.row = {};
      },
      /**显示弹窗 */
      show: function ({
        row
      }) {
        console.log(row)
        this.visible = true;
        this.clearData();
        this.row = row;
        setTimeout(() => {
          this.getData();
        }, 0);

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
          activity: this.row.pkey,
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
        };
        params[this.searchKey] = this.keywords;
        /**获取表格数据 */
        axios.post(api.marketing.activityIssueQuery, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.tableData = res.content;
            this.total = res.total;
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },

      /**列表导出 */
      handleImportExcel() {
        const params = {
          activity: this.row.pkey,
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
        };
        let that = this;
        params[this.searchKey] = this.keywords;
        this.downLoading = true;
        axios
          .post(api.marketing.activityIssueExport, qs.stringify(params), {
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
                    `${that.row.name + '发放记录'}.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${that.row.name + '发放记录'}.xlsx`
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
    }
  }
</script>
<style lang="less" scoped>
  /deep/.el-dialog {
    padding-bottom: 20px;
  }
</style>