<!-- 
@name: MemberPoints.vue 
@description: 会员积分
@author: sx
@url: /member/points
@date: 2020/07/09
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
        <el-select v-model="source" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in sourceList"></el-option>
        </el-select>
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
        <el-table-column label="会员昵称" prop="memberName"></el-table-column>
        <el-table-column label="手机号" prop="memberMobile" width="110"></el-table-column>
        <el-table-column label="积分变更" prop="points">
          <template slot-scope="scope">
            <span v-if="scope.row.direct" style="color: #F56C6C">+{{scope.row.points}}</span>
            <span v-else>-{{scope.row.points}}</span>
          </template>
        </el-table-column>
        <el-table-column label="积分余额" prop="balance"></el-table-column>
        <el-table-column label="时间" prop="createdTime" width="150"></el-table-column>
        <el-table-column label="类型" prop="sourceName">
        </el-table-column>
        <el-table-column label="其它信息" prop="remark"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from "qs";
import dropdown from "@/assets/js/dropdown";

export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: "name",
      selectOptions: [
        { name: "昵称", key: "name" },
        { name: "手机号码", key: "mobile" }
      ],
      sourceList: [
        { pkey: "", name: "类型" },
        { pkey: "POINTS_BUY", name: "购买" },
        { pkey: "POINTS_CONSUMPTION", name: "消费" },
        { pkey: "POINTS_ACTIVITY", name: "活动" },
        { pkey: "POINTS_MANUAL_ADD", name: "手动增加" },
        { pkey: "POINTS_MANUAL_LESS", name: "手动减少" },
        { pkey: "POINTS_GIFT", name: "礼品" },
        { pkey: "POINTS_REFUND", name: "积分退款"}
      ],
      member: "",
      date: "",
      source: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条     
      keywords: "", // 搜索关键字
      total: 0, //总页数      
    };
  },
  mounted() {
    this.member = this.$route.query.pkey;
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
      this.member = "";
      this.getData();
    },
    handleChange: function() {
      this.member = "";
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
        source: this.source,
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        member: this.member,
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.marketing.queryPoint, qs.stringify(params), {
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