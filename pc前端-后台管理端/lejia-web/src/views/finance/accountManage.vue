<!--
 * @Author: 沙晓
 * @Date: 2025-06-06 13:48:23
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-18 15:43:46
 * @Description: file content
 * @FilePath: /lejia-web/src/views/finance/accountManage.vue
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="search-box">
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" size="medium" @click="handleRemittance">
            划账
          </el-button>
        </div>
    </div>
        <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="账户名称" prop="name" min-width="150"></el-table-column>
        <el-table-column label="类型" prop="typeName" min-width="100"></el-table-column>
        <el-table-column label="余额" prop="comms" min-width="80"></el-table-column>
        <el-table-column label="银行账户类型" min-width="150">
          <template slot-scope="scope">
            {{  userType[scope.row.userType]  }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="userPhone" min-width="120"></el-table-column>
        <el-table-column label="银行账户" prop="pan" min-width="200"></el-table-column>
        <el-table-column label="开户银行联行号" prop="panNum" min-width="150"></el-table-column>
        <el-table-column label="市场自动提现" min-width="120">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.marketAuto" @change="handleMarketStatus(scope.row.marketAuto,scope.row.pkey)" v-if="scope.row.marketAuto !== null"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="商户自动提现" min-width="120">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.vendorAuto" @change="handleMerchantStatus(scope.row.vendorAuto,scope.row.pkey)" v-if="scope.row.vendorAuto !== null"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleAccountInfo(scope.row)"  v-if="scope.row.type != 'SYSTEM' && scope.row.type != 'SELF_MARKET'">
              账户信息
            </el-button>
            <el-button type="text" size="small" @click="handleCardInfo(scope.row)" v-if="scope.row.type != 'SYSTEM' && scope.row.type != 'SELF_MARKET' ">
              银行卡信息
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @size-change="handleSizeChange"
          @current-change="handleCurrentChange"></el-pagination>
    </div>
    <remittance-comp ref="remittanceComp"></remittance-comp>
    <account-info-comp ref="accountInfoComp" @refresh="getData"></account-info-comp>
    <card-info-comp ref="cardInfoComp" @refresh="getData"></card-info-comp>
    </div>
</template>
<script>
import qs from 'qs';
import remittanceComp from "./subComp/accountManage/remittanceComp.vue";
import accountInfoComp from "./subComp/accountManage/accountInfoComp.vue";
import cardInfoComp from "./subComp/accountManage/cardInfoComp.vue";
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0, //总页数
      pageSize: 8, //一页的数量
      page: 1, //页数
      searchKey: "name",
      selectOptions: [
        { name: "账户名称", key: "name" },
      ],
      userType: {
        1: "个人",
        2: "企业",
        3: "个体工商户"
      }
    }
  },
  components: {
    remittanceComp,
    accountInfoComp,
    cardInfoComp
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
    handleRemittance:function() {
      this.$refs.remittanceComp.show();
    },
    handleAccountInfo: function(row) {
      this.$refs.accountInfoComp.show(row.pkey);
    },
    handleCardInfo:function(row){
      if(!row.registered) {
        this.$message.warning("请先完成账户信息!");
        return;
      }
      this.$refs.cardInfoComp.show(row.pkey);
    },
    handleMarketStatus: function(status,pkey) {
      let text = '';
      const params = {
          pkey: pkey,
          enabled: status
        },
        url = api.market.financeMarketEnable;
      if (status) {
        text = '启用';
      } else {
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success(text + '成功');
        });
    },
    handleMerchantStatus: function(status,pkey) {
      let text = '';
      const params = {
          pkey: pkey,
          enabled: status
        },
        url = api.market.financeVendorEnable;
      if (status) {
        text = '启用';
      } else {
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success(text + '成功');
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
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.financeUserQuery, qs.stringify(params), {
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
    
  }
}
</script>
