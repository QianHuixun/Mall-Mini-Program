<!-- 
@name: Order.vue 
@description: 客如云订单
@author: sx
@url: /kry/order
@date: 2020/07/02
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
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="商户" prop="vendorName"></el-table-column>
        <el-table-column label="客如云ID" prop="uuid" width="120"></el-table-column>     
        <el-table-column label="订单编号" prop="code" width="120"></el-table-column>       
        <el-table-column label="交易金额" prop="custRealPay"></el-table-column>
        <el-table-column label="交易时间" prop="orderTime" width="150"></el-table-column>
        <el-table-column label="消费者手机" prop="mobile"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from "qs";

export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: "name",
      selectOptions: [
        { name: "商户", key: "name" }
      ],
      date: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条     
      keywords: "", // 搜索关键字
      total: 0, //总页数      
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
    startSearch: function({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
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
        endDate: this.date ? this.date[1] : ""
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.order.queryKruOrder, qs.stringify(params), {
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