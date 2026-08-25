<template lang="html">
  <div class="table-container">
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker v-model="startDate" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字"
          :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">商品总价：</span>
            <span class="count-num-xs">{{ numData.amt }}</span>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="商品名" prop="goodsName" show-overflow-tooltip></el-table-column>
        <el-table-column label="商品规格" prop="spaceName" show-overflow-tooltip></el-table-column>
        <el-table-column label="订单笔数" prop="orderCount" show-overflow-tooltip></el-table-column>
        <el-table-column label="销售数量" prop="goodsCount" show-overflow-tooltip></el-table-column>
        <el-table-column label="销售额" prop="amt" show-overflow-tooltip></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from "qs";
import utils from '@/assets/js/utils.js';
export default {
  data() {
    return {
      loading: false,
      searchKey: "goodsName",
      selectOptions: [{ name: "商品名称", key: "goodsName" }],
      tableData: [],
      startDate: [utils.getCustDate(30), utils.getNowDate()],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数
      downLoading: false,
      numData: {}
    };
  },
  computed: {},
  components: {},
  mounted() {
    this.getData();
    this.getDataNum();
  },
  methods: {
    handleChange: function () {
      this.page = 1;
      this.getData();
      this.getDataNum();
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
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
      this.getDataNum();
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
      };
      params[this.searchKey] = this.keywords;
      if (this.startDate && this.startDate.length == 2) {
        params['startDate'] = this.startDate[0];
        params['endDate'] = this.startDate[1];
      }
      axios
        .post(api.jd.reportByGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },

    getDataNum: function () {
      this.loading = true;
      const params = {};
      params[this.searchKey] = this.keywords;
      if (this.startDate && this.startDate.length == 2) {
        params['startDate'] = this.startDate[0];
        params['endDate'] = this.startDate[1];
      }
      axios
        .post(api.jd.reportByGoodsSum, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.numData = response;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },

    /**列表导出 */
    handleImportExcel() {
      const params = {};
      params[this.searchKey] = this.keywords;
      if (this.startDate && this.startDate.length == 2) {
        params['startDate'] = this.startDate[0];
        params['endDate'] = this.startDate[1];
      }
      let that = this;
      this.downLoading = true;
      axios
        .post(api.jd.reportByGoodsExport, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
          responseType: "blob",
          timeout: 0,
        })
        .then((res) => {
          let data = new Blob([res.data], {
            type: "application/json",
          });
          var reader = new FileReader();
          reader.addEventListener("loadend", function (e) {
            if (e.target.result.indexOf("result") > 0) {
              let result = JSON.parse(e.target.result);
              that.downLoading = false;
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: "application/vnd.ms-excel",
              });
              var disposition = res.headers['content-disposition'];
              var headersFileName = disposition ? disposition.split('=') : '';
              var fileName = headersFileName && headersFileName.length != 0 ? decodeURI(headersFileName[1]) : '商品统计.xlsx'
              if (!!window.ActiveXObject || "ActiveXObject" in window) {
                window.navigator.msSaveOrOpenBlob(blob, fileName);
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", fileName);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.downLoading = false;
              that.$message.success("导出成功");
            }
          });
          reader.readAsText(data);
        });
    },
  },
};
</script>

<style lang="less" scope>
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