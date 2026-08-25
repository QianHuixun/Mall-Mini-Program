<!-- 
@name: Problem.vue 
@description: 常见问题
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
      <div class="search-box-form">
        <el-select v-model="search.types" @change="handleChange" placeholder="问题分类" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typesList"></el-option>
        </el-select>
        <el-input v-model="search.content" placeholder="请输入关键字对问题/回答进行搜索"></el-input>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="clearSearch">重置</el-button>
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
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="序号" show-overflow-tooltip width="55">
          <template slot-scope="scope">
            {{ scope.$index + 1 + (page - 1) * pageSize }}
          </template>
        </el-table-column>
        <el-table-column label="问题分类" prop="typeName" show-overflow-tooltip></el-table-column>
        <el-table-column label="问题" prop="name" show-overflow-tooltip></el-table-column>
        <el-table-column label="回答" prop="answer" show-overflow-tooltip></el-table-column>
        <el-table-column label="是否启用" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" @change="handleEnabled(scope.row)"></el-switch>
          </template>
        </el-table-column>
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
      <add-comp ref="AddComp" @refresh="refresh"></add-comp>
      <edit-comp ref="EditComp" @refresh="refresh"></edit-comp>
    </div>
  </div>
</template>

<script>
import qs from "qs";
import AddComp from './problemComp/add.vue';
import EditComp from './problemComp/edit.vue';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      typeList: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      search: {
        types: '',
        content: '',
      },
      typesList: [],
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
    this.getTypesList()
  },
  methods: {
    handleChange: function () {
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
        .post(api.mall.problemDel, qs.stringify(params), {
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
     * 确认搜索
     */
    handleSearch() {
      this.page = 1
      this.getData()
    },
    /**
     * 重置搜索
     */
    clearSearch() {
      this.search = {
        types: '',
        content: '',
      }
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
        types: this.search.types,
        content: this.search.content
      };
      axios
        .post(api.mall.problemQuery, qs.stringify(params), {
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
    /**
     * 问题分类列表
     */
    getTypesList() {
      axios
        .post(api.mall.problemTypeList, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.typesList = response;
        });
    },
    /**
     * 切换启用状态
     */
    handleEnabled(row) {
      const params = {
        pkey: row.pkey,
        enabled: row.enabled
      }
      axios.post(api.mall.problemEnabled, this.$qs.stringify(params), {
        headers: {
          Authorization: this.$store.state.token,
        },
      })
      .then((response) => {
        this.$message.success('启停成功');
        this.getData()
      })
      .catch(() => {
        this.getData()
      })
    },
    refresh() {
      this.getData();
      this.getTypesList()
    }
  },
}
</script>

<style scoped>
.search-box-form .el-input {
  width: 300px;
}
.search-box-form .el-button {
  margin-left: 10px;
}
</style>