<!-- 
@name: Deposit.vue 
@description: 充值中心
@author: zs
@url: /member/deposit
@date: 2020/07/30
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
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="序号" prop="orderNumber"></el-table-column>
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="支付方式" prop="payTypeName"></el-table-column>
        <el-table-column label="充值金额" prop="amt"></el-table-column>
        <el-table-column label="充值时间" prop="payTime"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
  import qs from "qs";
  export default {
    data() {
      return {
        loading: false,
        date: "",
        searchKey: "mobile",
        selectOptions: [{
          name: "手机号",
          key: "mobile"
        }, ],
        tableData: [],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条     
        keywords: "", // 搜索关键字
        total: 0 //总页数
      };
    },
    mounted() {
      this.getData();
    },
    components: {},
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
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      /**
       * 开始搜索
       */
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
        this.page = 1;
        this.getData();
      },

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
          startTime: this.date ? this.date[0] : "",
          endTime: this.date ? this.date[1] : "",
        };
        params[this.searchKey] = this.keywords;
        axios.post(api.data.queryPay, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.tableData = response.content;
            this.total = response.total;

            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      }
    }
  }
</script>