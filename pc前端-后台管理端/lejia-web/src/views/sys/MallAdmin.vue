<!-- 
@name: Role.vue 
@description: 商城管理员 
@author: hdc
@route: /sys/role
@date: 2025/01/08
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字"
          :select-options="selectOptions"></search-bar>
        <el-select v-model="role" clearable placeholder="角色" @change="handleChange">
          <el-option value="" label="全部"></el-option>
          <el-option value="COUPON_MANAGER" label="卡券管理"></el-option>
          <el-option value="ORDER_WRITE_OFF" label="订单核销"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="角色" prop="roleNames">
          <template slot-scope="scope">
            <el-tag type="info" class="role-tag" v-for="item in scope.row.roleNames">{{ item }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
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
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>

<script>
import qs from 'qs';
import AddComp from './sub/MallAdmin/Add.vue'
import EditComp from './sub/MallAdmin/Edit.vue'
export default {
  data() {
    return {
      loading: false,
      selectOptions: [{
        name: '手机号',
        key: 'mobile',
      }],
      role: '',
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
    }
  },
  components: {
    AddComp,
    EditComp,
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
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        role: this.role,
      };
      let url = api.sys.managerQuery
      params[this.searchKey] = this.keywords;
      axios.post(url, qs.stringify(params))
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
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
     * 
     */
    handleChange() {
      this.page = 1;
      this.getData();
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
    handelAdd() {
      this.$refs.AddComp.show();
    },
    handleEdit(row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },
    handleDelete(row) {
      let url = api.sys.managerDel
      let params = {pkey: row.pkey}
      axios.post(url, qs.stringify(params)).then(res => {
        console.log(res);
        this.$message.success('删除成功')
        this.getData()
      })
    }
  }
}
</script>

<style lang="less" scoped>
  .role-tag + .role-tag {
    margin-left: 4px;
  }
</style>
