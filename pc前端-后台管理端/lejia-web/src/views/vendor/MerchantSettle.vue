<!-- 
@name: MerchantSettle.vue 
@description: 商户结算
@author: crj
@url: /vendor/settle
@date: 2021/10/18
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
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <!-- <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar> -->
        <el-select v-model="vendor" @change="handleChange" placeholder="选择商户" clearable multiple collapse-tags
          filterable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in vendorList">
          </el-option>
        </el-select>
        <el-select v-model="status" @change="handleChange" placeholder="结算状态" clearable>
          <el-option value="NOT_START" label="未结算"></el-option>
          <el-option value="SUCCESS" label="已结算"></el-option>
        </el-select>
        <!-- 操作按钮 -->

      </div>
      <div class="search-box-button">
        <el-button type="primary" v-if="" icon="el-icon-document-copy" size="medium" @click="handleSettle">
          结算
        </el-button>
        <el-button type="primary" plain icon="el-icon-download" size="medium" @click="handleImportExcel">
          导出
        </el-button>

      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" key="1" :selectable="checkSelectable" width="70">
        </el-table-column>
        <el-table-column label="采购日期" key="2" prop="createdTime" width="100" align="center"></el-table-column>
        <el-table-column label="商户名称" key="3" prop="vendorName" width="100" align="center"></el-table-column>
        <el-table-column label="采购笔数" prop="tradeCount" width="110" align="center"></el-table-column>
        <el-table-column label="采购金额(元)" key="4" prop="amt" align="center">
        </el-table-column>
        <el-table-column label="开户银行名称" key="5" prop="bankname" min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.bankname || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="银行卡号" key="6" min-width="120" prop="bankcard" align="center">
          <template slot-scope="scope">
            {{scope.row.bankcard || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="开户支行名称" key="7" prop="bankBranchName" min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.bankBranchName || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="开户行大额行号" key="8" prop="bankNo" min-width="150" align="center">
          <template slot-scope="scope">
            {{scope.row.bankNo || '--'}}
          </template>
        </el-table-column>
        <el-table-column label="结算状态" key="14" prop="statusName" min-width="100" align="center">
          <template slot-scope="scope">
            <span :class="scope.row.status=='AWAIT_SETTLEMENT'?'blue-font':''">{{scope.row.statusName || '--'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="结算备注" key="15" prop="settlementRemark" min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.settlementRemark || '无'}}
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span >
          <span>总采购笔数：{{purchaseCount}}笔</span>
          <span>总采购金额：{{purchaseAmt}}元</span>
          <span>已结算采购笔数：{{alreadycount}}笔</span>
          <span>已结算采购金额：{{alreadyAmt}}元</span>
          <span>未结算采购笔数：{{awaitCount}}笔</span>
          <span>未结算采购金额：{{awaitAmt}}元 </span></span>
      </div>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <settlement ref="settlement" @confirm="getData"></settlement>
  </div>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
import settlement from './sub/MerchantSettle/settlement';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      multipleSelection: [],
      status: '',
      vendor: [],
      vendorList: [], //商户列表
      date: [utils.getCustDate(30), utils.getNowDate()],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数,
      alreadyAmt: 0,
      alreadycount: 0,
      awaitAmt: 0,
      awaitCount: 0,
      purchaseAmt: 0,
      purchaseCount: 0,
    };
  },
  mounted() {
    axios.post(api.data.queryMerchant).then((response) => {
      this.vendorList = response;
    });
    this.getData();
  },
  components: {
    settlement,
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
     * @desc 表格数据是否可选择
     */
    checkSelectable(row) {
      return true;
      return row.status == 'AWAIT_SETTLEMENT';
    },
    /**
     * @desc 结算
     */
    handleSettle() {
      if (!this.multipleSelection.length) {
        this.$message.warning('请选择要结算的数据');
      } else {
        for (let i in this.multipleSelection) {
          let item = this.multipleSelection[i];
          if (item.status == 'ALREADY_SETTLEMENT') {
            this.$message.warning('请勿选中已结算的数据');
            return;
          }
        }
        let pkeys = [];
        this.multipleSelection.map((item) => {
          pkeys = pkeys.concat(item.pkeys);
        });
        this.$refs.settlement.show(pkeys);
      }
    },

    /**列表导出 */
    handleImportExcel() {
      const params = {
        startDate: this.date ? this.date[0] : '',
        endDate: this.date ? this.date[1] : '',
        vendor: this.vendor.join(','),
        status: this.status,
        createTimeSort: false,
      };
      let that = this;
      axios
        .post(api.order.exportMerSettle, qs.stringify(params), {
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
    // 表格选中项
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    /**搜索条件改变 */
    handleChange: function () {
      this.page = 1;
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
        startDate: this.date ? this.date[0] : '',
        endDate: this.date ? this.date[1] : '',
        vendor: this.vendor.join(','),
        status: this.status,
        createTimeSort: true,
      };
      axios
        .post(api.order.queryMerSettle, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.pageList.content;
          this.total = response.pageList.total;
          this.alreadyAmt = response.alreadyAmt;
          this.alreadycount = response.alreadycount;
          this.awaitAmt = response.awaitAmt;
          this.awaitCount = response.awaitCount;
          this.purchaseAmt = response.purchaseAmt;
          this.purchaseCount = response.purchaseCount;

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