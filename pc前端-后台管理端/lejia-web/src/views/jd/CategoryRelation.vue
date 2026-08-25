<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <div class="search-box-form">
        <el-cascader v-model="searchData.jdCategoryId" :options="jdCategoryList" :props="jdProps" clearable placeholder="京东分类" @change="handleSearch"></el-cascader>
        <el-cascader v-model="searchData.mallCategoryId" :options="mallCategoryList" :props="mallProps" clearable placeholder="商城分类" @change="handleSearch"></el-cascader>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table class="table-fixed" :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="京东分类" prop="name" min-width="200"></el-table-column>
        <el-table-column label="商城分类" prop="mallCategoryName" min-width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.mallName || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 弹窗组件 -->
    <category-relation-update ref="categoryRelationUpdate" @refresh="getData"></category-relation-update>
  </div>
</template>

<script>
import dropdown from '@/assets/js/dropdown';
import CategoryRelationUpdate from './CategoryRelation/Update.vue';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      pageSize: 10,
      total: 0,
      searchData: {
        jdCategoryId: '',
        mallCategoryId: '',
      },
      jdCategoryList: [],
      mallCategoryList: [],
      jdProps: {
        value: 'pkey',
        label: 'categoryName',
        children: 'children',
        emitPath: false,
        checkStrictly: false
      },
      mallProps: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        emitPath: false,
        checkStrictly: false
      }
    }
  },
  computed: {
    title() {
      return this.$store.state.activeName;
    },
  },
  components: {
    CategoryRelationUpdate,
  },
  mounted() {
    this.getData();
    this.getJdCategoryList();
    this.getMallCategoryList();
  },
  methods: {
    /**
     * 查询列表数据
     */
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        jdCategory: this.searchData.jdCategoryId,
        mallGoodsMain: this.searchData.mallCategoryId,
      };
      let url = api.jd.categoryRelationQuery;
      axios.post(url, this.$qs.stringify(params))
        .then((response) => {
          this.tableData = response.content || [];
          this.total = response.total || 0;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        })
        .catch(() => {
          this.loading = false;
        });
    },
    /**
     * 获取京东分类下拉列表
     */
    getJdCategoryList() {
      axios.post(api.jd.categoryDrop, this.$qs.stringify({levels: 2}))
        .then((response) => {
          this.jdCategoryList = response || [];
        });
    },
    /**
     * 获取商城分类下拉列表
     */
    getMallCategoryList() {
      // axios.post(api.goods.sysGoodsList)
      //   .then((response) => {
      //     this.mallCategoryList = response || [];
      //   });
      dropdown.getType().then((result) => {
        this.mallCategoryList = result.content;
      });
    },
    /**
     * 搜索
     */
    handleSearch() {
      this.page = 1;
      this.getData();
    },
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.getData();
    },
    /**
     * 编辑关联
     */
    handleEdit(row) {
      this.$refs.categoryRelationUpdate.show(row);
    },
  },
};
</script>

<style lang="less" scoped>
.el-cascader + .el-cascader {
  margin-left: 12px;
}
</style>
