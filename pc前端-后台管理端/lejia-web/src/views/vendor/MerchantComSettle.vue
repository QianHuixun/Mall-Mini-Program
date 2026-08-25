<!-- 
@name: MerchantSettle.vue 
@description: 商户结算-佣金
@author: crj
@url: /vendor/comsettle
@date: 2021/12/20
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
        <el-select v-if="userIdentity==1" v-model="searchData.marketKeys" @change="handleChange" placeholder="请选择市场" multiple collapse-tags filterable>
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
      </div>
      <div class="search-box-button">
         <el-button type="primary"  size="medium" @click="handleInsReport">
          生成报表
        </el-button>
        <el-button type="primary" icon="el-icon-document-copy" size="medium" @click="handleSettle">
          结算
        </el-button>
        <el-button type="primary" plain icon="el-icon-download" size="medium" @click="handleImportExcel">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border @sort-change="handleSortChange" style="width: 100%">
        <el-table-column align="center"label="序号"  width="60">
          <template slot-scope='scope'>
            {{ scope.$index + 1 + (page - 1) * pageSize }}
          </template>
        </el-table-column>
        <el-table-column label="商户名称"   min-width="100" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.name || '--'}}
          </template>
        </el-table-column>
         <!-- <el-table-column label="开户银行名称"  min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankname || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="银行卡号"  min-width="160" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankcard || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="开户支行名称" min-width="140" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankBranchName || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="开户行大额行号"  min-width="160" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankNo || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="开户人"   min-width="80" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankuser || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="开户人身份证号"  min-width="160" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.zxIdentity || '--'}}
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="银行卡绑定手机"  min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.bankuserMoblie || '--'}}
          </template>
        </el-table-column> -->
        <el-table-column label="总采购笔数" prop="ORDERCOUNT_SORT" min-width="120" sortable="custom" align="center">
          <template slot-scope="scope">
            {{scope.row.purchaseNum || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="总采购金额" prop="ORDERAMT_SORT" min-width="120" sortable="custom" align="center">
          <template slot-scope="scope">
            {{scope.row.purchaseAmtStr || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="佣金费率" prop="COMMISSION_SORT" min-width="120" sortable="custom" align="center">
          <template slot-scope="scope">
            {{scope.row.vendorInfo.commission || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="总交易佣金" prop="ORDERCOMMISSION_SORT" min-width="120" sortable="custom" align="center">
          <template slot-scope="scope">
            {{scope.row.orderCommStr || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="结算总金额" prop="AMT" min-width="120" sortable="custom" align="center">
          <template slot-scope="scope">
          {{scope.row.settlementAmtStr	 || '--'}}
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <div class="report-date" v-if="settlementData.time">报表日期：{{settlementData.time}}</div>
        <div>
          <span class="title">合计</span>
          <span >
            <span>总交易笔数：{{settlementData.num}}笔</span>
            <span>总交易金额：{{settlementData.amtStr}}元</span>
            <span>待结算金额：{{settlementData.awaitAmtStr}}元</span>
          </span>
        </div>
      </div>
    </div>
       <!-- 页码 -->
     <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    <settlement ref="settlement" @confirm="confirm" :inputModel="settlementData"></settlement>
    <ins-report ref="insReport" :marketKeys="searchData.marketKeys" @confirm="getData" ></ins-report>
  </div>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      status: '',
      vendor: [],
      date: '',
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数,
      settlementData: {
        amtStr: 0, //待结算金额
        awaitAmtStr: 0, //总交易金额
        num: 0, //总交易笔数
        total: 0,
        date: '',
        time: '',
      },
      sortData: {
        sort: '',
        sortType: ''
      },
      marketList: [],
      searchData: {
        marketKeys: [],
      },
    };
  },
  mounted() {
    if (!localStorage.getItem('isFirstSettle')) {
      this.handleInsReport();
      localStorage.setItem('isFirstSettle', '1');
    }
    this.getMarketData();
    this.getData();
  },
  components: {
    settlement(resolve) {
      require(['./sub/MerchantComSettle/settlement.vue'], resolve);
    },
    insReport(resolve) {
      require(['./sub/MerchantComSettle/insReport.vue'], resolve);
    },
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    userIdentity() {
      return this.$store.state.userIdentity
        ? this.$store.state.userIdentity
        : localStorage.getItem("userIdentity");
    }
  },
  methods: {
    /**
     * @desc 生成报表
     */
    handleInsReport() {
      localStorage.setItem(
        'insReportDate',
        utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D')
      );

      this.$nextTick(() => {
        setTimeout(() => {
          this.$refs.insReport.show();
        }, 200);
      });
    },
    /**
     * @desc 结算
     */
    handleSettle() {
      if (!localStorage.getItem('comSettleDate')) {
        this.$message.warning('请先生成报表');
        return;
      }
      this.$refs.settlement.show();
    },
    /**列表导出 */
    handleImportExcel() {
      if (!localStorage.getItem('comSettleDate')) {
        this.$message.warning('请先生成报表');
        return;
      }
      const params = {
        startTime: this.date ? this.date[0] : '',
        endTime: this.date ? this.date[1] : '',
        marketKeys: this.searchData.marketKeys.join(',')
      };
      let that = this;
      axios
        .post(api.order.exportComMerSettle, qs.stringify(params), {
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
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, '商户结算清单.xlsx');
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', '商户结算清单.xlsx');
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.$message.success('导出成功');
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
    /**
     * @desc 结算回调事件
     */
    confirm() {
      localStorage.setItem('comSettleDate', '');
      this.tableData = [];
      this.total = 0;
      this.settlementData = {
        amtStr: 0, //待结算金额
        awaitAmtStr: 0, //总交易金额
        num: 0, //总交易笔数
        total: 0,
        date: '',
        time: '',
      };
    },
    /**
     * 获取列表
     */
    getData: function () {
      if (localStorage.getItem('comSettleDate')) {
        this.date = JSON.parse(localStorage.getItem('comSettleDate'));
        this.settlementData.date = this.date;
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startTime: this.date ? this.date[0] : '',
          endTime: this.date ? this.date[1] : '',
          sortType: this.sortData.sortType,
          sort: this.sortData.sort,
          marketKeys: this.searchData.marketKeys.join(',')
        };
        axios
          .post(api.order.queryMerSettleCom, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.tableData = response.lines ? response.lines.content : [];
            this.total = response.lines ? response.lines.total : 0;
            this.settlementData.amtStr = response.amtStr;
            this.settlementData.awaitAmtStr = response.awaitAmtStr;
            this.settlementData.num = response.num;
            this.settlementData.total = response.numMerchant;
            this.settlementData.time = response.time;

            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      }
    },
    /**
     * 列表排序
     */
    handleSortChange({ column, prop, order }) {
      console.log(column, prop, order);
      this.sortData.sortType = prop
      this.sortData.sort = order == null ? '' : order == 'ascending' ? true : false
      this.getData()
      
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      axios.post(api.dropdown.newMarketList).then((res) => {
        this.marketList = res;
      });
    },
    /**
     * @desc 筛选
     */
    handleChange() {
      this.page = 1;
      this.getData();
    },
  },
};
</script>
<style lang="less" scoped>
.all-num {
  height: 100%;
  text-align: right;
  padding: 0 20px;
  line-height: 60px;
  border: 1px solid #ebeef5;
  border-top: none;
  .report-date {
    float: left;
    font-weight: bold;
  }
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

.red-font {
  color: #f56c6c;
}

.table-container > .search-box > .search-box-form > .el-select {
  width: 200px;
}
</style>