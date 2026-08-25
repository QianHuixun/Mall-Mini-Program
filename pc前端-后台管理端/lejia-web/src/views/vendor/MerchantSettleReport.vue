<!-- 
@name: MerchantSettleReport.vue 
@description: 结算报表-仅佣金
@author: crj
@url: /vendor/settlereport
@date: 2021/12/21
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- <el-tabs v-model="type" @tab-click="handleTypeClick">
      <el-tab-pane label="结算中" name="DOING"></el-tab-pane>
      <el-tab-pane label="结算异常" name="FAIL"></el-tab-pane>
      <el-tab-pane label="结算成功" name="SUCCESS"></el-tab-pane>
    </el-tabs> -->
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-if="userIdentity==1" v-model="searchData.marketKeys" @change="handleChange" placeholder="请选择市场" multiple collapse-tags filterable>
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchData.date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <!-- <el-select class="medium-select" v-model="settlementPkey" @change="handleChange" placeholder="报表日期">
          <el-option v-for="(item, index) in settlementList" :key="index" :value="item.pkey" :label="item.name"></el-option>
        </el-select> -->
        <!-- <el-input class="medium-input" v-model="keywords" placeholder="请输入商户名称或开户人名称进行搜索"></el-input>
        <el-button type="primary" size="medium" @click="handleChange">
          搜索
        </el-button> -->
      </div>
      <div class="search-box-button">
        <el-button v-if="type == 'FAIL' && total && $store.state.userIdentity == 1" type="primary" icon="el-icon-document-copy" size="medium" @click="handleSettle">
          结算
        </el-button>
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border @sort-change="handleSortChange" style="width: 100%">
        <el-table-column key="1" label="序号" prop="code" width="80" align="center">
          <template slot-scope='scope'>
            {{ scope.$index + 1 + (page - 1) * pageSize }}
          </template>
        </el-table-column>
        <el-table-column v-if="userIdentity==1" label="市场名称" prop="marketName" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.marketName || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="2" label="商户名称" prop="vendorName" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.vendorName || '--' }}
          </template>
        </el-table-column>
        <!-- <el-table-column key="3" label="开户银行名称" prop="bankname" min-width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankname || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="4" label="银行卡号" prop="bankcard" min-width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankcard || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="5" label="开户支行名称" prop="bankBranchName" min-width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankBranchName || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="6" label="开户行大额行号" prop="bankNo" min-width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankNo || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="7" label="开户人" prop="bankuser" min-width="80" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankuser || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="8" label="开户人身份证号" prop="bankuserIdentity" min-width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankuserIdentity || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="9" label="银行卡绑定手机" prop="bankuserMoblie" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.bankuserMoblie || '--' }}
          </template>
        </el-table-column> -->
        <el-table-column key="10" label="总采购笔数" prop="ORDERCOUNT_SORT" sortable="custom" min-width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.orderCount || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="11" label="总采购金额" prop="ORDERAMT_SORT" sortable="custom" min-width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.orderAmt || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="12" label="佣金费率" prop="COMMISSION_SORT" sortable="custom" min-width="125" align="center">
          <template slot-scope="scope">
            {{ scope.row.commission || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="13" label="总交易佣金" prop="ORDERCOMMISSION_SORT" sortable="custom" min-width="125" align="center">
          <template slot-scope="scope">
            {{ scope.row.orderCommission || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="14" label="结算总金额" prop="AMT" sortable="custom" min-width="125" align="center">
          <template slot-scope="scope">
            {{ scope.row.amt || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="15" label="结算周期" prop="time" min-width="125" align="center">
          <template slot-scope="scope">
            {{ scope.row.time || '--' }}
          </template>
        </el-table-column>
        <el-table-column key="16" label="结算操作时间" prop="createdTime" min-width="105" align="center">
          <template slot-scope="scope">
            {{ scope.row.createdTime || '--' }}
          </template>
        </el-table-column>
        <!-- <el-table-column v-if="type == 'FAIL'" key="15" label="异常原因" prop="rem" min-width="105" align="center">
          <template slot-scope="scope">
            {{ scope.row.rem || '--' }}
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="16" label="报表明细" fixed="right" min-width="80" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleLookDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column> -->
        <!-- <el-table-column key="17" label="结算流程" fixed="right" min-width="80" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleprocess(scope.row)">
              <img class="process-btn" :src="require('@/assets/images/process_btn.png')" />
            </el-button>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="handleLookDetail(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span :class="type == 'SUCCESS' ? '' : 'red-font'">
          {{ type == 'DOING' ? '结算中' : (type == 'FAIL' ? '结算失败' : '已结算') }}采购笔数：{{ settlementData.num }}笔</span>
        <span :class="type == 'SUCCESS' ? '' : 'red-font'">
          {{ type == 'DOING' ? '结算中' : (type == 'FAIL' ? '结算失败' : '已结算') }}采购金额：{{ settlementData.amtStr }}元</span>
      </div>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      <settlement ref="settlement" @confirm="getData" :inputModel="settlementData" :isReport="true"></settlement>
      <process ref="process" @confirm="getData"></process>

    </div>
  </div>
</template>
<script>
import qs from 'qs';

export default {
  data () {
    return {
      loading: false,
      tableData: [],
      settlementPkey: '',
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数,
      settlementList: [],
      settlementData: {
        amtStr: 0, //总采购价格
        awaitAmtStr: 0, //总结算金额
        num: 0, //总采购笔数
        total: 0,
        date: '',
        time: '',
      },
      type: 'DOING',
      marketList: [],
      searchData: {
        marketKeys: [],
        date: [],
      },
      sortData: {
        sort: '',
        sortType: ''
      },
    };
  },
  mounted () {
    axios.post(api.order.queryComReportList).then((response) => {
      this.settlementList = response;
      if (response.length) {
        this.settlementPkey = response[0].pkey;
      }
      this.getMarketData()
      this.getData();
    });
  },
  components: {
    settlement (resolve) {
      require(['./sub/MerchantComSettle/settlement.vue'], resolve);
    },
    process (resolve) {
      require(['./sub/MerchantSettleReport/process.vue'], resolve);
    },
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title () {
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
     * @desc 查看报表明细
     */
    handleLookDetail (row) {
      // let date = '';
      // for (let i in this.settlementList) {
      //   let item = this.settlementList[i];
      //   if (item.pkey == this.settlementPkey) {
      //     date = item.name;
      //     break;
      //   }
      // }
      this.$router.push({
        name: 'VendorComBill',
        params: { pkey: row.vendor, settlementPkey: row.settlementPkey, date: [row.startTime, row.endTime], type: this.type },
      });
    },
    /**
     * @desc 查看结算流程
     */
    handleprocess (row) {
      this.$refs.process.show(row.pkey);
    },
    /**
     * @desc 结算
     */
    handleSettle () {
      if (this.total) this.$refs.settlement.show();
    },
    /**
     * @desc 类型切换
     */
    handleTypeClick () {
      this.getData();
    },

    /**列表导出 */
    handleImportExcel () {
      const params = {
        // keyword: this.keywords,
        // settlementPkey: this.settlementPkey,
        // type: this.type,
        // settlementPkey: this.settlementPkey,
        marketKeys: this.searchData.marketKeys.join(','),
        startTime: this.searchData.date ? this.searchData.date[0] : '',
        endTime: this.searchData.date ? this.searchData.date[1] : '',
      };
      let that = this;
      this.downLoading = true;
      axios
        .post(api.order.exportSettleProcess, qs.stringify(params), {
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
                window.navigator.msSaveOrOpenBlob(blob, '结算报表.xlsx');
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', '结算报表.xlsx');
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
     * 页码改变事件
     */
    handleCurrentChange (val) {
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
        // keyword: this.keywords,
        // settlementPkey: this.settlementPkey,
        // type: this.type,
        marketKeys: this.searchData.marketKeys.join(','),
        sort: this.sortData.sort,
        sortType: this.sortData.sortType,
        startTime: this.searchData.date ? this.searchData.date[0] : '',
        endTime: this.searchData.date ? this.searchData.date[1] : '',
      };
      axios
        .post(api.order.querySettleReport, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.lines?.content;
          this.total = response.lines?.total;
          this.settlementData.amtStr = response.amtStr;
          this.settlementData.awaitAmtStr = response.awaitAmtStr;
          this.settlementData.num = response.num;
          this.settlementData.total = response.numMerchant;
          // this.settlementData.date = response.time.split(' - ');
          // this.settlementData.time = response.time;
          this.settlementData.settlementPkey = this.settlementPkey;
          console.log(this.settlementData);
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
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

/deep/.el-tabs {
  margin: 0 10px;

  .el-tabs__nav-wrap::after {
    display: none;
  }
}

.process-btn {
  width: 25px;
  height: 25px;
  border: none !important;
}

.medium-input {
  width: 300px !important;
}

.table-container>.search-box>.search-box-form>.el-input {
  margin: 5px;
}
.table-container > .search-box > .search-box-form > .el-select {
  width: 200px;
}
</style>