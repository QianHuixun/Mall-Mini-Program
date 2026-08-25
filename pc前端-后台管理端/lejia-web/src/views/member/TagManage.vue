<!--
* @description 标签管理
* @fileName TagManage.vue
* @author zs
* @date 2024/08/13
!-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="types" @change="handleChange" placeholder="选择标签类型" clearable>
          <el-option value="NORMAL" key="NORMAL" label="普通标签"></el-option>
          <el-option value="MSD" key="MSD" label="热力豆标签"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
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
        <el-table-column label="标签名称" prop="name"></el-table-column>
        <el-table-column label="标签类型" prop="typeName"></el-table-column>
        <el-table-column label="标签描述" prop="description">
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime">
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" v-if="scope.row.name!='优惠券'&&scope.row.name!='礼券'">
              <el-button slot="reference" size="mini" type="danger" :disabled="scope.row.name=='优惠券'||scope.row.name=='礼券'">
                删除
              </el-button>
            </el-popconfirm>
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
import qs from 'qs';
import AddComp from './sub/TagManageAdd.vue';
import EditComp from './sub/TagManageEdit.vue';
export default {
  data() {
    return {
      loading: false,
      searchKey: 'name',
      selectOptions: [{ name: '标签名称', key: 'name' },{ name: '标签描述', key: 'description' }],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      types: "",
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
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
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
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.marketing.tagsDel, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
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
        types: this.types
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.tagsQuery, qs.stringify(params), {
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