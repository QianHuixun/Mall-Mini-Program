<!--
 * @Author: 沙晓
 * @Date: 2025-06-06 13:49:17
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-18 15:36:35
 * @Description: 结算账单
 * @FilePath: /lejia-web/src/views/finance/settleBill.vue
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="search-box">
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="userIdentity == '1' ? selectOptions : selectOptions2"></search-bar>
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
          </el-date-picker>
          <el-select v-model="searchData.settlementType" placeholder="请选择" @change="handleChange">
            <el-option label="全部" value=""></el-option>
          <!-- <el-option label="待确认" value="AWAIT_CONFIRM"></el-option> -->
          <el-option label="未结算" value="NOT_START"></el-option>
          <el-option label="结算中" value="DOING"></el-option>
          <el-option label="结算异常" value="FAIL"></el-option>
          <el-option label="已结算" value="SUCCESS"></el-option>
        </el-select>
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
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="交易市场" prop="farmerName" min-width="150"></el-table-column>
        <el-table-column label="订单号" prop="code" min-width="150"></el-table-column>
        <el-table-column label="商品金额" prop="amto" min-width="80"></el-table-column>
        <el-table-column label="配送费金额" prop="oldPostage" min-width="120"></el-table-column>
        <el-table-column label="商品优惠" prop="cardAmt" min-width="100"></el-table-column>
        <el-table-column label="配送费优惠" prop="cardPostageAmt" min-width="100"></el-table-column>
        <el-table-column label="商品退款" prop="refundAmt" min-width="80"></el-table-column>
        <el-table-column label="商品优惠退款" prop="refundCardAmt" min-width="120"></el-table-column>
        <el-table-column label="配送费退款" prop="refundPostageAmt" min-width="100"></el-table-column>
        <el-table-column label="应结金额" prop="needAmt" min-width="80"></el-table-column>
        <el-table-column label="实付金额" prop="actualPayment" min-width="80"></el-table-column>
        <el-table-column label="手续费" prop="payComm" min-width="80"></el-table-column>
        <el-table-column label="手续费承担方" prop="commissionTypeName" min-width="120"></el-table-column>
        <el-table-column label="商品结算">
          <el-table-column label="商品应结" prop="goodsNeedAmt" min-width="80"></el-table-column>
          <el-table-column label="集团抽佣" prop="sysCommissions" min-width="80"></el-table-column>
          <el-table-column label="市场抽佣" prop="marketCommissions" min-width="80"></el-table-column>
          <el-table-column label="商户结算" prop="amt" min-width="80"></el-table-column>
        </el-table-column>
        <el-table-column label="配送费结算">
          <el-table-column label="配送费应结" prop="needPostageAmt" min-width="100"></el-table-column>
          <el-table-column label="配送优惠" prop="cardPostageAmt2" min-width="80"></el-table-column>
          <el-table-column label="集团结算" prop="postageAmtSys" min-width="80"></el-table-column>
          <el-table-column label="市场结算" prop="postageAmtMarket" min-width="80"></el-table-column>
        </el-table-column>
        <el-table-column label="实际结算">
          <el-table-column label="集团结算" prop="actualAmtSys" min-width="80"></el-table-column>
          <el-table-column label="市场结算" prop="actualAmtMarket" min-width="80"></el-table-column>
          <el-table-column label="商户结算" prop="actualAmtVendor" min-width="80"></el-table-column>
        </el-table-column>
        <el-table-column label="结算状态" prop="settlementTypeName" min-width="120">
          <template slot-scope="scope">
            <span :class="scope.row.settlementType == 'FAIL' ? 'red_color' : (scope.row.settlementType == 'SUCCESS' ? 'green_color' : '') ">{{scope.row.settlementTypeName}}</span>
          </template>
        </el-table-column>
        <el-table-column label="交易时间" prop="createdTime" min-width="150"></el-table-column>
      </el-table>
      <el-pagination background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @size-change="handleSizeChange"
          @current-change="handleCurrentChange"></el-pagination>
    </div>
    </div>
</template>
<script>
import qs from 'qs';

export default {
  data() {
    return {
      loading: false,
      downLoading: false,
      date:[],
      tableData: [],
      total: 0, //总页数
      pageSize: 8, //一页的数量
      page: 1, //页数
      searchKey: "code",
      selectOptions: [
        { name: "订单号", key: "code" },
        { name: "市场", key: "farmer" },
      ],
      selectOptions2: [
        { name: "订单号", key: "code" },
      ],
      userIdentity: this.$store.state.userIdentity,
      searchData: {
        settlementType: ""
      }
    }
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
        settlementType: this.searchData.settlementType
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.financeQuery, qs.stringify(params), {
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
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loading = true;
      this.getData();
    },
    handleChange: function () {
      this.page = 1;
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
    /**列表导出 */
      handleImportExcel() {
          const params = {
            startDate: this.date ? this.date[0] : "",
            endDate: this.date ? this.date[1] : "",
            settlementType: this.searchData.settlementType
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
        .post(api.market.financeExport, qs.stringify(params), {
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
                  `结算账单.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  `结算账单.xlsx`
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

<style scoped>
.red_color {
  color: #F56C6C;
}
.green_color {
  color: #67C23A;
}
</style>