<!-- 
@name: Collage.vue 
@description: 团购订单
@author: crj
@url: /order/collage
@date: 2020/08/10
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
        <el-select v-model="status" @change="handleChange" placeholder="订单状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
       <el-select v-model="goodPkey" @change="handleChange" placeholder="商品" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.title" v-for="(item, index) in goodsList"></el-option>
        </el-select>
        <!-- <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar> -->
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="商品" prop="goodsName" min-width="120"></el-table-column>
        <el-table-column label="团号" prop="groupId" width="150"></el-table-column>
        <el-table-column label="状态" prop="statusName" width="150"></el-table-column>
        <el-table-column label="当前人数" prop="buyNum" width="200"></el-table-column>
        <el-table-column label="成团人数" prop="groupNum" width="200"></el-table-column>
        <el-table-column label="建团时间" prop="createdTime" width="200"></el-table-column>
          <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row.pkey)">
              查看订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <delivery-add ref="DeliveryAdd" @refresh="getData"></delivery-add >
     <edit-comp ref="EditComp"></edit-comp>
  </div>
</template>
<script>
import qs from "qs";
import DeliveryAdd from "./sub/MallDeliveryAdd";
import EditComp from "./sub/MallDetailEdit";

export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: "code",
      selectOptions: [
        {
          name: "订单编号",
          key: "orderId"
        }
      ],
      statusList: [
        {
          pkey: "NOT_GROUPS",
          name: "未成团"
        },
        {
          pkey: " INTO_GROUPS",
          name: "成团"
        }
      ],
      date: "",
      status: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0 ,//总页数
      goodPkey:'',
      goodsList:[]

    };
  },
  mounted() {
    this.getData();
    this.getDorpdownData()
  },
  components: {
    DeliveryAdd,
    EditComp
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    }
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
    startSearch: function({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    handleChange: function() {
      this.page = 1;
      this.getData();
    },
    handleDetail: function(pkey) {
      this.$router.push({
        path:'/order/market',
        query:{
          pkey
        }
      })
    },

    /**
     * 获取列表
     */
    getData: function() {
      console.log(typeof this.goodPkey)
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        goods: this.goodPkey,
        status: this.status
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.collageOrder, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    getDorpdownData: function() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: 9999,
        mType: 'COLLAGE_GOODS',
        // enabled: true,
      };
      axios.post(api.goods.queryGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          console.log(response.content)
          this.goodsList = response.content;
        });
    }
    
  }
};
</script>