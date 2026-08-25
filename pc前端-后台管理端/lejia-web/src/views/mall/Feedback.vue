<!-- 
@name: Feedback.vue 
@description: 客户回馈
@author: sx
@route: /mall/feedback
@date: 2020/07/08
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
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="用户" prop="memberName"></el-table-column>
        <el-table-column label="手机号码" prop="mobile"></el-table-column>
        <el-table-column label="反馈市场" prop="farmerName" v-if="roleId==1"></el-table-column>
        <el-table-column label="反馈内容" prop="content" ></el-table-column>
        <el-table-column label="时间" prop="createdTime"></el-table-column>
        <el-table-column label="操作" width="200" v-if="!isOnlyBrowse">
          <template slot-scope="scope">
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">
                删除
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from 'qs';

export default {
  data() {
    return {
      loading: false,
      searchKey: 'mobile',
      selectOptions: [
        {
          name: '手机号码',
          key: 'mobile',
        },
      ],
      keywords: '', // 搜索关键字
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
    };
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    /**
     * 角色id
     */
    roleId() {
      return this.$store.state.userIdentity;
    },
    /**是否为仅浏览 */
    isOnlyBrowse() {
      let hasBrowse = false;
      if (this.$store.state.activeName) {
        hasBrowse =
          this.$store.state.activeName.indexOf('仅浏览') > 0 ? true : false;
      }
      return hasBrowse;
    },
  },
  components: {},
  mounted() {
    this.getData();
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
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.delFeedback, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    /**
     * 获取列表
     * @return {[type]} [description]
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.mall.queryFeedback, qs.stringify(params), {
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
  },
};
</script>