<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar
          ref="searchBar"
          @search="startSearch"
          placeholder="请输入关键词"
          :select-options="selectOptions"
          :keywords="keywords"
        >
        </search-bar>
        时间
        <el-date-picker
          v-model="date"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
        >
        </el-date-picker>
        <!-- 操作按钮 -->
      </div>
      <div class="search-box-button">
        <el-button
          type="primary"
          icon="el-icon-download"
          size="medium"
          @click="handleImportExcel"
        >
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column
          label="订单编号"
          prop="code"
          min-width="140"
          align="center"
        ></el-table-column>
        <el-table-column
          label="商户名称"
          prop="displayName"
          align="center"
        ></el-table-column>
        <el-table-column
          label="摊位号"
          prop="booth"
          align="center"
        ></el-table-column>
        <el-table-column
          label="订单金额"
          prop="orderAmt"
          align="center"
        ></el-table-column>
        <el-table-column
          label="打包物料费"
          prop="packingCharge"
          align="center"
        ></el-table-column>
        <el-table-column
          label="结算金额"
          prop="amt"
          align="center"
        ></el-table-column>
        <el-table-column
          label="付款时间"
          prop="paymentTime"
          min-width="120"
          align="center"
        >
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>总订单数：{{ orderCount }}笔</span>
        <span>订单金额：{{ orderAmt }}元</span>
        <span>打包物料费：{{ packingCharge }}元</span>
        <span>结算金额：{{ amt }}元</span>
      </div>
      <!-- 页码 -->
      <el-pagination
        hide-on-single-page
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="page"
        :page-size="pageSize"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from "qs";

export default {
  data() {
    return {
      loading: false,
      tableData: [],
      searchKey: "code",
      date: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数,
      orderAmt: 0, //总金额
      orderCount: 0,
      packingCharge: 0,
      amt: 0,
      selectOptions: [
        {
          name: "订单编号",
          key: "code",
        },
        {
          name: "商户名称",
          key: "vendorName",
        },
        {
          name: "摊位号",
          key: "booth",
        },
      ],
    };
  },
  mounted() {
    this.getData();
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
    startSearch: function ({ key, keywords }) {
      this.searchKey = key;
      this.keywords = keywords;
      this.page = 1;
      this.getData();
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
      };
      params[this.searchKey] = this.keywords;
      let that = this;
      this.downLoading = true;
      axios
        .post(api.vendor.packingChargeExport, qs.stringify(params), {
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
              if (!!window.ActiveXObject || "ActiveXObject" in window) {
                window.navigator.msSaveOrOpenBlob(blob, "打包物料费明细.xlsx");
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", "打包物料费明细.xlsx");
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
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    /**搜索条件改变 */
    handleChange: function () {
      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.vendor.packingChargeQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.lines.content;
          this.total = response.lines.total;

          this.orderCount = response.orderCount;
          this.orderAmt = response.orderAmt;
          this.packingCharge = response.packingCharge;
          this.amt = response.amt;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
  },
};
</script>
<style lang="less" scoped>
.all-num {
  height: 100%;
  text-align: right;
  padding-right: 20px;
  line-height: 60px;
  border: 1px solid #ebeef5;
  border-top: none;

  .title {
    font-weight: bold;
  }

  span {
    display: inline-block;
    margin-right: 10px;
  }
}
</style>
