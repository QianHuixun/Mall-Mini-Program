<!-- 
@name: Role.vue 
@description: 角色设置 
@author: sx
@route: /sys/role
@date: 2020/03/25
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
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
        新增角色
      </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="角色名" prop="name"></el-table-column>
        <el-table-column label="角色描述" prop="description"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleSetEdit(scope.row)">
            配置权限
          </el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
            编辑
          </el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">
              删除
            </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <set-comp ref="SetComp"></set-comp>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/RoleAdd.vue';
import EditComp from './sub/RoleEdit.vue';
import SetComp from './sub/RoleSetEdit.vue';

export default {
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
    };
  },
  components: {
    AddComp,
    EditComp,
    SetComp,
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
     * 点击配置角色权限
     */
    handleSetEdit: function (row) {
      this.$refs.SetComp.show({ row: row });
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({ row: row });
    },
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.sys.delRole, qs.stringify(params), {
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
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
      };
      axios
        .post(api.sys.queryRole, qs.stringify(params), {
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