<!-- 
@name: CancelRecord.vue 
@description: 撤销记录
@author: crj
@url: /vendor/cancel
@date: 2021/10/18
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
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <!-- <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar> -->
        <el-select v-model="vendor" @change="handleChange" placeholder="选择商户" clearable multiple collapse-tags
          filterable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in vendorList">
          </el-option>
        </el-select>
        <!-- 操作按钮 -->
      </div>
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel">
          导出
        </el-button>

      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="订单编号" prop="code" width="140" align="center"></el-table-column>
        <el-table-column label="订单类型" prop="vendorOrderTypeName" width="100" align="center"></el-table-column>
        <el-table-column label="商户名称" prop="vendorName" width="110" align="center"></el-table-column>
        <el-table-column label="商品名" prop="goodsName" align="center"></el-table-column>
        <el-table-column label="数量" prop="num" width="80" align="center"></el-table-column>
        <el-table-column label="商品原价" prop="goodsPrice" width="80" align="center"></el-table-column>
        <el-table-column label="采购单价" v-if="settlementMethod=='PURCHASE_SETTLEMENT'" prop="price" width="80"
          align="center"></el-table-column>
        <el-table-column label="采购总价" v-if="settlementMethod=='PURCHASE_SETTLEMENT'" prop="totalPrice" width="80"
          align="center"></el-table-column>
        <el-table-column label="采购时间" prop="createdTime" min-width="120" align="center"></el-table-column>
        <el-table-column label="备注" prop="remark" min-width="200" align="center">
          <template slot-scope="scope">
            {{scope.row.remark || '无'}}
          </template>
        </el-table-column>
        <el-table-column label="撤销时间" prop="createdTime" min-width="120" align="center"></el-table-column>



      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>总订单数：{{orderCount}}笔</span>
        <span>总采购数：{{purchaseCount}}笔</span>
        <span> 总采购金额数：{{purchaseAmt}}元</span>
      </div>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
  import qs from "qs";
  import utils from "@/assets/js/utils";

  export default {
    data() {
      return {
        loading: false,
        tableData: [],
        vendor: [],
        vendorList: [], //商户列表
        date: [utils.getCustDate(30), utils.getNowDate()],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条     
        keywords: "", // 搜索关键字
        total: 0, //总页数,
        purchaseAmt: 0, //总采购金额
        purchaseCount: 0, //总采购数
        orderCount: 0, //总订单数
        settlementMethod: "PURCHASE_SETTLEMENT"

      };
    },
    mounted() {
      axios.post(api.data.queryMerchant)
        .then(response => {
          this.vendorList = response;
        });
      this.getSettlementMethod();
      this.getData();
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
    methods: {
      getSettlementMethod() {
        axios.post(api.common.querySettlementMethod).then(response => {
          this.settlementMethod = response.english;
        });
      },
      /**列表导出 */
      handleImportExcel() {
        const params = {
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
          vendor: this.vendor.join(','),
          createTimeSort: false,
        };
        let that = this;
        axios.post(api.order.exportCancelRecord, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
            responseType: 'blob',
            timeout: 0
          })
          .then(res => {
            let data = new Blob([res.data], {
              type: 'application/json'
            });
            var reader = new FileReader();
            reader.addEventListener('loadend', function (e) {
              if (e.target.result.indexOf('result') > 0) {
                let result = JSON.parse(e.target.result);
                that.$message.error(result.codeMsg)
                return
              } else {
                let blob = new Blob([res.data], {
                  type: 'application/vnd.ms-excel'
                });
                if (!!window.ActiveXObject || "ActiveXObject" in window) {
                  window.navigator.msSaveOrOpenBlob(blob, '撤销记录.xlsx');
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute('download', '撤销记录.xlsx');
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.$message.success('导出成功')
              }
            })
            reader.readAsText(data)

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

      /**搜索条件改变 */
      handleChange: function () {
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
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
          vendor: this.vendor.join(','),
          createTimeSort: true,
        };
        axios.post(api.order.queryCancelRecord, qs.stringify(params)).then(response => {
          this.tableData = response.pageList.content;
          this.total = response.pageList.total;
          this.orderCount = response.orderCount;
          this.purchaseCount = response.purchaseCount;
          this.purchaseAmt = response.purchaseAmt;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
      }
    }
  }
</script>
<style lang="less" scoped>
  .all-num {
    height: 100%;
    text-align: right;
    padding-right: 20px;
    line-height: 60px;
    border: 1px solid #EBEEF5;
    border-top: none;

    .title {
      font-weight: bold;
    }

    span {
      display: inline-block;
      margin-right: 10px;
    }
  }

  .blue-font {
    color: #409EFF;
  }

  .table-container>.search-box>.search-box-form>.el-select {
    width: 200px;
  }
</style>