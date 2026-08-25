<!--
 * @Author: 沙晓
 * @Date: 2024-02-26 15:46:11
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-17 14:48:16
 * @Description: 提现打款
 * @FilePath: /lejia-web/src/views/vendor/Withdrawal.vue
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
        申请日期
        <el-date-picker
          v-model="date"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
          placeholder="申请日期"
        >
        </el-date-picker>
        <el-select
          v-model="status"
          @change="handleChange"
          placeholder="打款状态"
          clearable
        >
          <el-option value="NO_PAYMENT" label="未打款"></el-option>
          <el-option value="PAYMENT" label="打款成功"></el-option>
        </el-select>
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
          prop="vendorName"
          min-width="120"
          align="center"
        ></el-table-column>
        <el-table-column
          label="摊位号"
          prop="booth"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="打款状态"
          prop="statusName"
          min-width="120"
          align="center"
        >
          <template slot-scope="scope">
            <span class="green-font" v-if="scope.row.status == 'PAYMENT'">{{
              scope.row.statusName
            }}</span>
            <span
              class="red-font"
              v-else-if="scope.row.status == 'NO_PAYMENT'"
              >{{ scope.row.statusName }}</span
            >
          </template>
        </el-table-column>
        <el-table-column
          label="提现金额"
          prop="amount"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="银行账号"
          prop="pan"
          min-width="120"
          align="center"
          v-if="isTianJin"
        >
        </el-table-column>
        <el-table-column
          label="提现银行"
          prop="bankname"
          min-width="120"
          align="center"
          v-if="!isTianJin"
        >
        </el-table-column>
        <el-table-column
          label="开户行"
          prop="bankBranchName"
          min-width="120"
          align="center"
          v-if="!isTianJin"
        >
        </el-table-column>
        <el-table-column
          label="持卡人姓名"
          prop="bankuser"
          min-width="120"
          align="center"
          v-if="!isTianJin"
        >
        </el-table-column>
        <el-table-column
          label="银行卡号"
          prop="bankcard"
          min-width="120"
          align="center"
          v-if="!isTianJin"
        >
        </el-table-column>
        <el-table-column
          label="申请时间"
          prop="createdTime"
          min-width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-popconfirm
              title="确定已打款？"
              placement="top"
              @onConfirm="handleUpd(scope.row)"
              v-if="scope.row.status == 'NO_PAYMENT' && !isTianJin"
            >
              <el-button slot="reference" size="mini" type="primary">
                打款成功
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>未打款：{{ num }}笔</span>
        <span>待提现金额：{{ amount }}元</span>
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
      isTianJin: localStorage.getItem("ascription") ==  (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
      loading: false,
      tableData: [],
      date: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数,
      status: "",
      vendorName: "",
      booth: "",
      searchKey: "vendorName",
      num: "",
      amount: "",
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
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        booth: this.booth,
        status: this.status,
        vendorName: this.vendorName
      };
      axios
        .post(api.vendor.withdrawalQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.withdrawalOnPage.content;
          this.total = response.withdrawalOnPage.total;
          this.num = response.num;
          this.amount = response.amount;
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
    handleUpd: function(row) {
      const params = {
        pkey: row.pkey
      };
      const _this = this;
      axios
        .post(api.vendor.withdrawalConfirm, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(() => {
          _this.$message.success("打款成功！");
          _this.getData();
        });
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        booth: this.booth,
        status: this.status,
        vendorName: this.vendorName
      };
      let that = this;
      this.downLoading = true;
      axios
        .post(api.vendor.withdrawalExport, qs.stringify(params), {
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
                window.navigator.msSaveOrOpenBlob(blob, "提现打款.xlsx");
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", "提现打款.xlsx");
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
