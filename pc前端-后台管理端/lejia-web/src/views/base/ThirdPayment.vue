<!-- 
@name: ThirdPayment.vue 
@description: 第三方支付渠道
@author: sx
@route: /base/thirdPayment
@date: 2020/06/28
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <!-- <div class="search-box-form">
      </div> -->
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="saas市场" prop="farmerName"></el-table-column>
        <el-table-column label="云农贸市场" prop="marketName"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <div>
              <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
                <el-button slot="reference" size="mini" type="danger">删除</el-button>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      <!-- 组件 -->
      <add-comp ref="AddComp" @refresh="getData"></add-comp>
      <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    </div>
  </div>
</template>

<script>
import qs from "qs";
import AddComp from './sub/thirdPayment/add.vue';
import EditComp from './sub/thirdPayment/edit.vue';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      typeList: [],
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
  },
  components: {
    AddComp,
    EditComp,
  },
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
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({ row: row });
    },
    /**
     * 删除公司
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.thridPaymentDel, qs.stringify(params), {
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
        .post(api.mall.thridPaymentQuery, qs.stringify(params), {
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
}
</script>

<style>

</style>