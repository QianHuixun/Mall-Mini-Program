<!-- 
@name: MerchantBill.vue 
@description: 商户对账-佣金
@author: crj
@url: /vendor/combill
@date: 2021/12/21
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        付款时间
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
        结算时间
        <el-date-picker
          v-model="date2"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
        >
        </el-date-picker>
        采购确认时间
        <el-date-picker
          v-model="date3"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
        >
        </el-date-picker>
        <search-bar
          ref="searchBar"
          @search="startSearch"
          placeholder="请输入关键词"
          :select-options="selectOptions"
          :keywords="keywords"
        >
        </search-bar>
        <el-select
          v-model="status"
          @change="handleChange"
          placeholder="结算状态"
          clearable
        >
          <el-option value="NOT_START" label="未结算"></el-option>
          <el-option value="SUCCESS" label="已结算"></el-option>
        </el-select>

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
          width="140"
          align="center"
        ></el-table-column>
        <el-table-column
          label="订单类型"
          prop="typeName"
          width="90"
          align="center"
        ></el-table-column>
        <el-table-column
          label="商户名称"
          prop="vendorName"
          width="110"
          align="center"
        ></el-table-column>
        <el-table-column
          label="摊位号"
          prop="booth"
          align="center"
        ></el-table-column>
        <el-table-column
          label="商品名"
          prop="goodsName"
          align="center"
        ></el-table-column>
        <el-table-column
          label="规格"
          prop="spaceName"
          align="center"
        ></el-table-column>
        <el-table-column
          label="数量"
          prop="num"
          width="80"
          align="center"
        ></el-table-column>
        <el-table-column
          label="商品单价"
          prop="goodsPrice"
          width="80"
          align="center"
        ></el-table-column>
        <el-table-column
          label="商品总价"
          prop="goodsTotalPrice"
          width="100"
          align="center"
        ></el-table-column>
        <el-table-column
          label="退款金额"
          prop="refundAmt"
          width="100"
          align="center"
        ></el-table-column>
        <el-table-column
          label="应结金额"
          prop="needAmt"
          width="100"
          align="center" v-if="isTianJin"
        ></el-table-column>
        <el-table-column
          label="佣金费率"
          prop="commissionRateStr"
          width="100"
          align="center"
        >
          <template slot-scope="scope">
            {{
              parseFloat(scope.row.commissionRateStr)
                ? scope.row.commissionRateStr
                : "--"
            }}
          </template>
        </el-table-column>
        <el-table-column
          label="交易佣金"
          prop="commissions"
          width="100"
          align="center"
        >
          <template slot-scope="scope">
            {{ scope.row.commissions || "--" }}
          </template>
        </el-table-column>
        <el-table-column
          label="手续费"
          prop="payComm"
          width="100"
          align="center" v-if="isTianJin"
        ></el-table-column>
        <el-table-column
          label="结算金额"
          prop="amt"
          width="100"
          align="center"
        ></el-table-column>
        <el-table-column
          label="付款时间"
          prop="endDate"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="采购确认时间"
          prop="buyDate"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="结算时间"
          prop="startDate"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="结算状态"
          prop="statusName"
          width="80"
          align="center"
        >
          <template slot-scope="scope">
            <span
              :class="
                scope.row.status == 'NOT_START'
                  ? 'blue-font'
                  : scope.row.status == 'DOING'
                  ? 'green-font'
                  : scope.row.status == 'FAIL'
                  ? 'red-font'
                  : ''
              "
            >
              {{ scope.row.statusName || "" }}</span
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>总订单数：{{ orderNum }}笔</span>
        <span>总采购数：{{ purchaseCount }}笔</span>
        <span>商品总价：{{ goodsTotalPrice }}元</span>
        <span>总采购金额数：{{ amt }}元</span>
        <span>总结算金额：{{ totalAmt }}元</span>
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
import utils from "@/assets/js/utils";

export default {
  data() {
    return {
      isTianJin: localStorage.getItem("ascription") ==  (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
      loading: false,
      numData: [],
      tableData: [],
      searchKey: "vendorName",
      status: "",
      vendorName: "",
      code: "",
      date: [utils.getCustDate(30), utils.getNowDate()],
      date2: [],
      date3: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数,
      amt: 0, //总金额
      goodsTotalPrice: 0,
      totalAmt: 0,
      purchaseCount: 0,
      orderNum: 0,
      selectOptions: [
        {
          name: "商户名称",
          key: "vendorName"
        },
        {
          name: "摊位号",
          key: "booth"
        },
        {
          name: "订单编号",
          key: "code"
        }
      ]
    };
  },
  mounted() {
    if (this.$route.params.type) {
      this.status = this.$route.params.type;
    }

    this.vendor = Number(this.$route.params.pkey) || "";
    if (this.$route.query.date && this.$route.query.name) {
      this.searchKey = "vendorName";
      this.$refs.searchBar.keywords = this.$route.query.name;
      this.vendorName = this.$route.query.name;
      this.date2 = [this.$route.query.settleDate, this.$route.query.settleDate];
      this.date = [this.$route.query.date, this.$route.query.date];
    }
    this.getData();
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    }
  },
  methods: {
    startSearch: function({ key, keywords }) {
      this.vendorName = "";
      this.booth = "";
      this.code = "";
      this.searchKey = key;
      this[this.searchKey] = keywords;
      this.page = 1;
      this.getData();
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        startSettlementDate: this.date2 ? this.date2[0] : "",
        endSettlementDate: this.date2 ? this.date2[1] : "",
        startVendorTime: this.date3 ? this.date3[0] : "",
        endVendorTime: this.date3 ? this.date3[1] : "",
        vendorName: this.vendorName,
        booth: this.booth,
        code: this.code,
        status: this.status
      };
      let that = this;
      this.downLoading = true;
      axios
        .post(api.order.exportComPurchase, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          },
          responseType: "blob",
          timeout: 0
        })
        .then(res => {
          let data = new Blob([res.data], {
            type: "application/json"
          });
          var reader = new FileReader();
          reader.addEventListener("loadend", function(e) {
            if (e.target.result.indexOf("result") > 0) {
              let result = JSON.parse(e.target.result);
              that.downLoading = false;
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: "application/vnd.ms-excel"
              });
              if (!!window.ActiveXObject || "ActiveXObject" in window) {
                window.navigator.msSaveOrOpenBlob(blob, "商户对账清单.xlsx");
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", "商户对账清单.xlsx");
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
    handleChange: function() {
      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        startSettlementDate: this.date2 ? this.date2[0] : "",
        endSettlementDate: this.date2 ? this.date2[1] : "",
        startVendorTime: this.date3 ? this.date3[0] : "",
        endVendorTime: this.date3 ? this.date3[1] : "",
        vendorName: this.vendorName,
        booth: this.booth,
        code: this.code,
        status: this.status
      };
      axios
        .post(api.order.queryComPurchase, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.lines.content;
          this.total = response.lines.total;
          this.amt = response.amt;
          this.purchaseCount = response.num;
          this.totalAmt = response.totalAmt;
          this.goodsTotalPrice = response.goodsTotalPrice;
          this.orderNum = response.orderNum;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    }
  }
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

.blue-font {
  color: #409eff;
}
.green-font {
  color: #33a954;
}
.red-font {
  color: #ff0000;
}
/deep/.medium-select {
  width: 200px !important;
}
/deep/.el-table .el-table__fixed-right {
  height: 100% !important; //设置高优先，以覆盖内联样式
}

.number-box {
  text-align: right;
  padding: 10px;
}
</style>
