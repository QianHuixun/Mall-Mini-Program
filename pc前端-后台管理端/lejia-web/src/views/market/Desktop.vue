<!--
* @description 桌位码
* @fileName Desktop.vue
* @author zs
* @date 2024/07/03
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
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          添加桌位
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleDownload">
          下载桌位码
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="桌位号" prop="name"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="操作">
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
  import AddComp from './sub/DesktopAdd.vue';
  import EditComp from './sub/DesktopEdit.vue';
  export default {
    data() {
      return {
        loading: false,
        searchKey: 'name',
        selectOptions: [{
          name: '桌位码',
          key: 'name',
        }],
        tableData: [],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: '', // 搜索关键字
        total: 0, //总页数
      };
    },
    mounted() {
      this.getData();
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

      /**
       * 新增
       */
      handelAdd: function () {
        this.$refs.AddComp.show();
      },
      /**
       * 点击修改
       */
      handleEdit: function (row) {
        this.$refs.EditComp.show({
          row: row
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
          .post(api.market.delDesktop, qs.stringify(params), {
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
        params[this.searchKey] = this.keywords;
        axios
          .post(api.market.queryDesktop, qs.stringify(params), {
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
       * 桌位码下载
       */
      handleDownload: function () {
      const {ascription, marketPkey} = this.$store.state;
      location.href= `${api.market.downDesktop}?name=${this.keywords}&ascription=${ascription}&marketPkey=${marketPkey}`
      },
    },
  };
</script>