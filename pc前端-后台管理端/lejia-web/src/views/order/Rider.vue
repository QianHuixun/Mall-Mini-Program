<!-- 
@name: Rider.vue 
@description: 骑手订单
@author: crj
@url: /order/rider
@date: 2020/08/03
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
        <el-select v-model="courierName" @change="handleChange" placeholder="快递员" clearable>
          <el-option :value="item.name" :key="index" :label="item.name" v-for="(item, index) in courierList"></el-option>
        </el-select>
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="订单号" prop="code" min-width="120"></el-table-column>
        <el-table-column label="订单状态" prop="statusName" width="150"></el-table-column>
        <el-table-column label="快递员" prop="courierName" width="150"></el-table-column>
        <el-table-column label="派单时间" prop="pdTime" width="200"></el-table-column>
        <el-table-column label="接单时间" prop="jdTime" width="200"></el-table-column>
        <el-table-column label="到货时间" prop="qrTime" width="200"></el-table-column>
        <el-table-column label="菜场" prop="farmerName" width="150"></el-table-column>
        <el-table-column label="公司" prop="companyName" width="150"></el-table-column>
        <el-table-column label="建档时间" prop="createdTime" width="200"></el-table-column>
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
  import dropdown from "@/assets/js/dropdown";


  export default {
    data() {
      return {
        loading: false,
        numData: [],
        tableData: [],
        courierName:'',
        courierList:[],
        searchKey: "code",
        selectOptions: [{
          name: "订单编号",
          key: "orderId"
        }],
        statusList: [{
            pkey: "EXPRESS_ORDER",
            name: "已派单"
          },{
            pkey: " EXPRESS_GOODS",
            name: "已揽货"
          },{
            pkey: "EXPRESS_ARRIVED",
            name: "已到货"
          },{
            pkey: "EXPRESS_REJECT",
            name: "拒收"
          },
        ],
        
        date: "",
        status:"",
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条     
        keywords: "", // 搜索关键字
        total: 0, //总页数      
      };
    },
    mounted() {
      this.getData();
      dropdown.getCourier().then(result => {
        this.courierList = result.content;
      });
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
      startSearch: function ({
        key,
        keywords
      }) {
        
        this.keywords = keywords;
        this.searchKey = key;
        this.page = 1;
        this.getData();
      },
      handleChange: function () {
        this.page = 1;
        this.getData();
      },

      

      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          courierName: this.courierName,
          orderId:this.keywords,
          startTime: this.date ? this.date[0] : "",
          endTime: this.date ? this.date[1] : "",
          status: this.status
        };
        params[this.searchKey] = this.keywords;
        axios.post(api.order.riderOrder, qs.stringify(params), {
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
      }
    }
  }
</script>