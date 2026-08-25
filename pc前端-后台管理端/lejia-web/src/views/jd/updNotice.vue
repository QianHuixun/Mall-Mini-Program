<!--
@name: refund.vue
@description: 退款管理
@author: zs
@url: /order/refund
@date: 2020/07/25
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
        <el-date-picker v-model="searchData.date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-select v-model="searchData.type" @change="handleChange" placeholder="变更类型" clearable style="width: 200px">
          <el-option :value="item.key" :key="index" :label="item.value" v-for="(item, index) in typeList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入内容" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button"></div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="变更类型" prop="typeName" min-width="120"></el-table-column>
        <el-table-column label="sku" prop="jdGoods" min-width="120"></el-table-column>
        <el-table-column label="商品名称" prop="title" min-width="240"></el-table-column>
        <el-table-column label="说明" prop="description" min-width="120"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime" min-width="120"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleUpdate(scope.row)">去修改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <goods-update ref="goodsUpdate"></goods-update>
  </div>
</template>
<script>
  import GoodsUpdate from './Goods/update.vue'
  import qs from "qs";
  export default {
    data() {
      return {
        loading: false,
        tableData: [],
        searchData: {
          title: null,
          skuId: null,
          type: null,
          date: [],
        },
        searchKey: "title",
        selectOptions: [
          {
            name: "标题",
            key: "title"
          },
          {
            name: "sku",
            key: "skuId"
          }
        ],
        typeList: [],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: "", // 搜索关键字
        total: 0, //总页数
      };
    },
    mounted() {
      this.getData();
      this.getTypeList()
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
      GoodsUpdate,
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
      startSearch: function ({
        key,
        keywords
      }) {
        this.selectOptions.forEach(item => {
          this.searchData[item.key] = null
        })
        this.searchData[key] = keywords;
        this.page = 1;
        this.getData();
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const { title, skuId, type , date } = this.searchData
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startDate: (Array.isArray(date) && date.length === 2) ? date[0] : null,
          endDate: (Array.isArray(date) && date.length === 2) ? date[1] : null,
          title,
          skuId,
          type,
        };
        axios
          .post(api.jd.updNoticeQuery, qs.stringify(params))
          .then(response => {
            this.tableData = response.content;
            this.total = response.total;
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },
      /**
       * 获取商品变更类型
       */
      getTypeList: function () {        
        axios
          .post(api.jd.updNoticeTypeList)
          .then(response => {
            console.log(response);
            this.typeList = Array.isArray(response) ? response : [];
          });
      },
      /**
       * 编辑sku
       */
      handleUpdate(row) {
        this.$refs.goodsUpdate.show(row)
      }
    }
  };
</script>
<style lang="less" scoped>
  .all-num {
    height: 100%;
    text-align: right;
    padding-right: 20px;
    line-height: 60px;
    border: 1px solid #ebeef5;
    border-top: none;

    .title {
      font-weight: bold;
    }

    span {
      display: inline-block;
      margin-right: 10px;
    }
  }
</style>