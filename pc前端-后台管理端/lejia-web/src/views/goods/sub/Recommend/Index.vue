<template>
  <div class="table-container">
    <h1 class="title">
      推荐商品管理
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="searchData.goodsFarmer" filterable clearable placeholder="市场" @change="handleChange">
          <el-option v-for="(item, index) in marketList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
        <el-select v-model="searchData.mType" filterable clearable placeholder="类型"  @change="handleChange">
          <el-option label="市场" value="MARKET_GOODS"></el-option>
          <el-option label="特价" value="SPECIAL_GOODS"></el-option>
          <el-option label="商城" value="INTEGRAL_GOODS"></el-option>
          <el-option label="预售" value="INTEGRAL_PRESALE_GOODS"></el-option>
          <el-option label="滨农优品" value="INTEGRAL_BNYP_GOODS"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" :select-options="selectOptions" placeholder="请输入关键字" >
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
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="市场名称" prop="goodsFarmerName" show-overflow-tooltip></el-table-column>
        <el-table-column label="类型" prop="mtypeName" show-overflow-tooltip></el-table-column>
        <el-table-column label="商户" prop="vendorName" show-overflow-tooltip></el-table-column>
        <el-table-column label="商品图片" prop="photo1" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-image v-if="scope.row.photo1 && scope.row.photo1.length" :src="scope.row.photo1[0]" :preview-src-list="scope.row.photo1"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="title" show-overflow-tooltip></el-table-column>
        <el-table-column label="上下架" prop="enabledName" show-overflow-tooltip></el-table-column>
        <el-table-column label="推荐区域" prop="zoneNames" show-overflow-tooltip></el-table-column>
        <el-table-column label="排序" prop="sort" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200">
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
    <add-comp ref="addComp" :sourceGoods="pkey" @refresh="getData"></add-comp>
    <edit-comp ref="editComp" :sourceGoods="pkey" @refresh="getData"></edit-comp>
  </div>
</template>

<script>
import AddComp from './subComp/add.vue'
import EditComp from './subComp/edit.vue'
export default {
  data() {
    return {
      pkey: null,
      loading: false,
      searchData: {
        goodsFarmer: '',
        mType: '',
        zone: '',
      },
      marketList: [],
      searchKey: 'vendor',
      keywords: '', // 搜索关键字
      selectOptions: [
        {
          name: '商户',
          key: 'vendor',
        },
        {
          name: '商品名称',
          key: 'title',
        }
      ],
      page: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
    }
  },
  components: {
    AddComp,
    EditComp
  },
  mounted() {
    console.log(this.$route);
    this.pkey = this.$route.query.pkey
    this.getData()
    this.getMarketData()
  },
  methods: {
    getData() {
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        sourceGoods: this.pkey,
        ...this.searchData
      }
      params[this.searchKey] = this.keywords;
      axios.post(api.goods.recommendQuery, this.$qs.stringify(params))
        .then(res => {
          console.log(res);
          this.tableData = res.content
          this.total = res.total
        })
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      const params = {
        includeAscription: true
      }
      axios.post(api.dropdown.marketDrop, this.$qs.stringify(params)).then((res) => {
        this.marketList = res;
      });
    },
    startSearch({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**筛选 */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    handleCurrentChange(val) {
      this.page = val
      this.getData()
    },
    handelAdd() {
      this.$refs.addComp.show()
    },
    handleEdit(row) {
      this.$refs.editComp.show(row)
    },
    handleDelete(row) {
      const params = {
        pkey: row.pkey
      }
      params[this.searchKey] = this.keywords;
      axios.post(api.goods.recommendDel, this.$qs.stringify(params))
        .then(res => {
          console.log(res);
          this.$message.success('删除成功')
          this.getData()
        })
    },
  }
}
</script>

<style lang="less" scoped>
.table-container > .search-box > .search-box-form > .el-select {
  width: 200px !important;
}
.table-box .el-image {
  width: 50px;
  height: 50px;
}
</style>