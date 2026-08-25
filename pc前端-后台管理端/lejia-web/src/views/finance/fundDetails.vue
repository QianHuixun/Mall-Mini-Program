<!--
 * @Author: 沙晓
 * @Date: 2025-06-06 13:48:59
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-18 15:38:12
 * @Description: 资金明细
 * @FilePath: /lejia-web/src/views/finance/fundDetails.vue
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="top-info">
      <div class="top-info_title">账户金额</div>
      <div class="top-info_content">
        <div class="top-info_item">
          <label class="top-info_item__label">
            可提现金额
          </label>
          <div class="top-info_item__content">
            {{sumData.makePaymentAmt}}
            <el-button type="primary" size="mini" @click="showDialog" v-if="sumData.makePaymentAmt">
            提现
          </el-button>
          </div>
        </div>
        <div class="top-info_item">
          <label class="top-info_item__label">
            待结算金额
          </label>
          <div class="top-info_item__content">
            {{sumData.pendingSettlementAmt}}
          </div>
        </div>
      </div>
    </div>
  <div class="search-box">
      <div class="search-box-form">
        <el-select v-model="status" placeholder="请选择" @change="handleChange">
            <el-option label="全部" value=""></el-option>
          <el-option label="收入" value="INCOME"></el-option>
          <el-option label="调拨" value=" ALLOCATION"></el-option>
          <el-option label="提现" value=" MAKE_PAYMENT"></el-option>
        </el-select>
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
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="交易市场" prop="name" min-width="150"></el-table-column>
        <el-table-column label="类型" prop="statusName" min-width="150"></el-table-column>
        <el-table-column label="账单日期" prop="billDate" min-width="150"></el-table-column>
        <el-table-column label="交易金额" prop="comms" min-width="150"></el-table-column>
        <el-table-column label="余额" prop="balance" min-width="150"></el-table-column>
        <el-table-column label="备注" prop="remark" min-width="200"></el-table-column>
        <el-table-column label="交易时间" prop="withdrawTime" min-width="150"></el-table-column>
      </el-table>
      <el-pagination background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @size-change="handleSizeChange"
          @current-change="handleCurrentChange"></el-pagination>
    </div>
    <edit-comp ref="editComp" @refresh="getSumData"></edit-comp>
    </div>
</template>
<script>
import qs from 'qs';
import editComp from './subComp/fundDetails/editComp.vue';
export default {
  data() {
    return {
      loading: false,
      downLoading: false,
      status: "",
      date: [],
      sumData: {
        makePaymentAmt: "",
        pendingSettlementAmt: "",
        pan: "",
      },
      tableData: [],
      total: 0, //总页数
      pageSize: 8, //一页的数量
      page: 1, //页数
    }
  },
  mounted() {
    this.getData();
    this.getSumData();
  },
  components: {
    editComp
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
    showDialog:function() {
      this.$refs.editComp.show({sumData: this.sumData});
    },
    getSumData: function() {
      axios
        .post(api.market.financeEDetailsSum, qs.stringify({}), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.sumData = response;
        });
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
        status: this.status
      };
      axios
        .post(api.market.financeEDetailsQuery, qs.stringify(params), {
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
    /**列表导出 */
      handleImportExcel() {
          const params = {
            startDate: this.date ? this.date[0] : "",
            endDate: this.date ? this.date[1] : "",
            status: this.status
        };
        let that = this;
        this.downLoading = true;
        axios
        .post(api.market.financeEDetailsExport, qs.stringify(params), {
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
                  `资金明细记录.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  `资金明细记录.xlsx`
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
.top-info {
  margin: 0 8px 8px 8px;
  background: #f1f4f7;
  padding: 8px;
  border-radius: 5px;

  .top-info_title {
    font-size: 16px;
    font-weight: bold;
  }

  .top-info_item {
    display: flex;
    align-items: center;
    margin: 10px 0;

    .top-info_item__label {
      width: 6em;
    }
    .top-info_item__content {
      flex: 1;
      font-size: 16px;
      font-weight: bold;

      .el-button {
        margin-left: 10px;
      }
    }
  }
}
</style>