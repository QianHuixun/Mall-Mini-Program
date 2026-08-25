<!--
* @description 经营数据统计
* @fileName Operating.vue
* @author zs
* @date 2024/04/28
!-->

<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-select v-if="type == 'operate'" v-model="marketPkey" @change="handleChange" placeholder="选择市场" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">访问人数</span>
            <span class="count-num-xs">{{ numData.accCount }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">成交订单</span>
            <span class="count-num-xs">{{ numData.orderCount }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">营收金额</span>
            <span class="count-num-xs">{{ numData.revenueAmt }}</span>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel" :loading="downLoading">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="市场" prop="farmerName" min-width="160"></el-table-column>
        <el-table-column label="时间" prop="yesterTime" min-width="160"></el-table-column>
        <el-table-column label="访问人数" prop="accCount" width="100">
        </el-table-column>
        <el-table-column label="支付人数" prop="ymemberPayNum" width="100">
        </el-table-column>
        <el-table-column label="成交订单" prop="orderCount" width="100">
        </el-table-column>
        <el-table-column label="商品金额" prop="amto" width="100">
        </el-table-column>
        <el-table-column label="配送费" prop="postage" width="100">
        </el-table-column>
        <el-table-column label="优惠金额" prop="cardAmt" width="100">
        </el-table-column>
        <el-table-column label="退款金额" prop="refundAmt" width="100">
        </el-table-column>
        <el-table-column label="营收金额" prop="revenueAmt" width="100">
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
    <!-- 打印内容 end -->
  </div>
</template>
<script>
  import qs from "qs";
  import dropdown from '@/assets/js/dropdown';
  // var LODOP;
  export default {
    data() {
      return {
        loading: false,
        numData: {},
        tableData: [],
        date: "",
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        total: 0, //总页数
        marketPkey: "",
        marketList: [], //市场列表
        downLoading: false,
        type: this.$route.params.type,
      };
    },
    mounted() {
      dropdown.getMarket().then((result) => {
        this.marketList = result.content;
      });
      this.getData();
      this.getCountData();
    },
    components: {},
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
      /**列表导出 */
      handleImportExcel() {
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
          farmer: this.marketPkey
        };
        let that = this;
        this.downLoading = true;
        axios
          .post(api.data.exportStatistics, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
            timeout: 0,
          })
          .then((res) => {
            let data = new Blob([res.data], {
              type: 'application/json',
            });
            var reader = new FileReader();
            reader.addEventListener('loadend', function (e) {
              if (e.target.result.indexOf('result') > 0) {
                let result = JSON.parse(e.target.result);
                that.downLoading = false;
                that.$message.error(result.codeMsg);
                return;
              } else {
                let blob = new Blob([res.data], {
                  type: 'application/vnd.ms-excel',
                });
                if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                  window.navigator.msSaveOrOpenBlob(
                    blob,
                    `${
                   '经营数据统计'
                  }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${
                   '经营数据统计'
                  }.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.downLoading = false;
                that.$message.success('导出成功');
              }
            });
            reader.readAsText(data);
          });
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.loading = true;
        this.getData();
      },
      handleChange: function () {
        this.page = 1;
        this.getData();
        this.getCountData();
      },

      // 获取订单信息统计金额和笔数
      getCountData() {
        const params = {
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
          farmer: this.marketPkey
        };
        axios
          .post(api.data.countStatistics, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.numData = res;
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
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
          farmer: this.marketPkey
        };
        axios
          .post(api.data.queryStatistics, qs.stringify(params), {
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
  };
</script>
<style lang="less" scope>
  .count-container {
    display: inline-block;
    padding-right: 10px;

    .count-box {
      display: inline-block;
      padding: 0 10px;

      .count-tit {
        padding-right: 5px;
      }

      .count-num-rk {
        color: #67c23a;
      }

      .count-num-xs {
        color: #409eff;
      }
    }
  }

  /deep/ .el-table__fixed-right {
    height: 100% !important; //设置高优先，以覆盖内联样式
  }
</style>
<style lang="less" scoped>
  /deep/.el-table .el-table__fixed-right {
    height: 100% !important; //设置高优先，以覆盖内联样式
  }
</style>