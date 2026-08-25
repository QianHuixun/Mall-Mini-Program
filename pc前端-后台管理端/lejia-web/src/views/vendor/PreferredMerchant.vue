<!-- 
  @name: PreferredMerchant.vue 
  @description: 合作商户-商户管理
  @author: hdc
  @url: 
  @date: 2023/12/21
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
        <el-select v-model="searchData.enabled" @change="handleChange" clearable placeholder="请选择启用状态">
          <el-option  value="" label="全部" ></el-option>
          <el-option  value="true" label="启用" ></el-option>
          <el-option  value="false" label="未启用" ></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
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
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="商户" prop="name" min-width="120" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="展示名称" prop="displayName" min-width="120" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="商户标签" prop="labels" min-width="120" align="center">
          <template slot-scope="scope">
            {{scope.row.labels.join("、")}}
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort" min-width="80" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="启用状态" prop="enabled" min-width="120" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" @change="handleEnabled(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="添加时间" prop="createdTime" min-width="120" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total"
        :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange">
      </el-pagination>
    </div>
    <add-comp ref="addComp" @confirm="getData"></add-comp>
    <edit-comp ref="editComp" @confirm="getData"></edit-comp>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      searchData: {
        displayName: "",
        enabled: "",
        vendorName: "",
      },
      selectOptions: [
        {
          name: '商户名',
          key: 'vendorName',
        },
        {
          name: '展示名称',
          key: 'displayName',
        },
      ],      
      searchKey: 'vendorName',
      keywords: '', // 搜索关键字
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        enabled: this.searchData.enabled
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.vendor.boutiqueQuery, this.$qs.stringify(params))
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    handleChange() {
      this.page = 1;
      this.getData();
    },
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**
     * @desc 获取商户下拉列表
     */
    getVendorList() {
      let params = {
        marketPkey: this.inputModel.marketPkey,
      };
      axios
        .post(api.goods.supplyVendorList, this.$qs.stringify(params))
        .then((res) => {
          this.vendorList = res;
        });
    },
    handleEnabled({ pkey, enabled }) {
      const params = { pkey, enabled }
      axios.post(api.vendor.boutiqueEnabled, this.$qs.stringify(params))
        .then(response => {
          console.log(response);
          this.$message.success('启停成功')
          this.getData()
        })
    },
    handelAdd() {
      this.$refs.addComp.show()
    },
    handleEdit(row) {
      this.$refs.editComp.show(row)
    },
    handleDelete({pkey}) {
      axios.post(api.vendor.boutiqueDel, this.$qs.stringify({pkey}))
        .then(response => {
          console.log(response);
          this.$message.success('删除成功')
          this.getData()
        })
    },
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
  },
  components: {
    AddComp(resolve) {
      require(['./sub/PreferredMerchant/add.vue'], resolve);
    },
    EditComp(resolve) {
      require(['./sub/PreferredMerchant/edit.vue'], resolve);
    },
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
  }
}
</script>

<style scoped>
.table-container > .search-box > .search-box-form > .el-select {
  width: 200px !important;
}
</style>