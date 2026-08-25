<!--
 * @Author: 沙晓
 * @Date: 2024-02-26 15:46:11
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-03-04 14:27:52
 * @Description: 商户钱包
 * @FilePath: /lejia-web/src/views/vendor/Wallet.vue
-->
<template lang="html">
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
        >
        </search-bar>
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
          label="商户名称"
          prop="name"
          min-width="120"
          align="center"
        ></el-table-column>
        <el-table-column
          label="摊位号"
          prop="booth"
          min-width="120"
          align="center"
        ></el-table-column>
        <el-table-column
          label="可提现余额"
          prop="amount"
          min-width="120"
          align="center"
        ></el-table-column>
        <el-table-column
          label="待结算金额"
          prop="lockAmount"
          min-width="120"
          align="center"
        ></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>可提现余额：{{ walletAmt }}元</span>
        <span>待结算金额：{{ settlementAmt }}元</span>
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
    <wallet-detail ref="WalletDetail"></wallet-detail>
  </div>
</template>

<script>
import qs from "qs";
import WalletDetail from "./sub/Wallet/Detail.vue";
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      booth: "",
      vendorName: "",
      walletAmt: "",
      settlementAmt: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数,
      selectOptions: [
        {
          name: "商户名称",
          key: "vendorName"
        },
        {
          name: "摊位号",
          key: "booth"
        }
      ]
    };
  },
  components: {
    WalletDetail
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
  mounted() {
    this.getData();
  },
  methods: {
    startSearch: function({ key, keywords }) {
      this.vendorName = "";
      this.booth = "";
      this.searchKey = key;
      this[this.searchKey] = keywords;
      this.page = 1;
      this.getData();
    },
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        booth: this.booth,
        vendorName: this.vendorName
      };
      axios
        .post(api.vendor.walletQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.walletOnPage.content;
          this.total = response.walletOnPage.total;
          this.walletAmt = response.walletAmt;
          this.settlementAmt = response.settlementAmt;
          setTimeout(() => {
            this.loading = false;
          }, 300);
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
    handleDetail: function(row) {
      this.$refs.WalletDetail.show({
        row: row
      });
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        vendor: this.vendor,
        status: this.status,
        settlementPkey: this.settlementPkey
      };
      let that = this;
      this.downLoading = true;
      axios
        .post(api.vendor.walletExport, qs.stringify(params), {
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
                window.navigator.msSaveOrOpenBlob(blob, "商户钱包.xlsx");
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", "商户钱包.xlsx");
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
