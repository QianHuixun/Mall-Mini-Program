<!-- 
@name: Log.vue 
@description: 操作日志
@author: sx
@route: /sys/log
@date: 2020/06/25
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <span>时间筛选：</span>
        <el-date-picker v-model="dates" type="daterange" value-format="yyyy-MM-dd" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" @change="handleChange">
        </el-date-picker>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="公司" prop="company"></el-table-column>
        <el-table-column label="菜场" prop="market"></el-table-column>
        <el-table-column label="类型" prop="operation"></el-table-column>
        <el-table-column label="描述" prop="content"></el-table-column>
        <el-table-column label="创建时间" prop="beginTime"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
  import qs from "qs";

  export default {
    data() {
      return {
        loading: false,
        tableData: [],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        total: 0, //总页数
        dates: ""
      };
    },
    components: {},
    computed: {
      /**
       * 获取菜单标题
       * @return {[title]} [返回从state状态中获取的选中菜单名]
       */
      title() {
        return this.$store.state.activeName;
      }
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
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startTime:this.dates!=null? this.dates[0]: "",
          endTime: this.dates!=null?this.dates[1] : ""
        }
        axios.post(api.sys.queryLog, qs.stringify(params), {
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