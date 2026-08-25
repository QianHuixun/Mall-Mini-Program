<!-- 
@name: Kry.vue 
@description: 客如云商户管理
@author: sx
@url: /kry/kry
@date: 2020/07/13
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
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增客如云
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">

        <el-table-column label="商户名称" prop="name"></el-table-column>
        <el-table-column label="客如云ID" prop="uuid"></el-table-column>
        <el-table-column label="负责人" prop="manager"></el-table-column>
        <el-table-column label="商户手机号" prop="mobile" width="120"></el-table-column>
        <el-table-column label="token" prop="token"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime" width="150"></el-table-column>
        <el-table-column label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled"
              @change="handleStatus(scope.row.enabled,scope.row.pkey)" :disabled="isOnlyBrowse"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" v-if="!isOnlyBrowse">
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
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/KryAdd.vue';
import EditComp from './sub/KryEdit.vue';
export default {
  data() {
    return {
      loading: false,
      searchKey: 'name',
      selectOptions: [
        {
          name: '商户名称',
          key: 'name',
        },
      ],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
    };
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
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },

    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },

    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delKru, qs.stringify(params), {
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
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.market.startKru;
        text = '启用';
      } else {
        url = api.market.stopKru;
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
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
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.queryKru, qs.stringify(params), {
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