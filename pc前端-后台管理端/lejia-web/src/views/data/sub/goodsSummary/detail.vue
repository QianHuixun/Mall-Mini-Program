<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字"
          :select-options="selectOptions"></search-bar>
        <el-select v-model="searchData.status" placeholder="订单状态" clearable @change="handleChange">
          <el-option v-for="item in statusList" :key="item.value" :value="item.value" :label="item.label"></el-option>
        </el-select>
        <el-select v-model="searchData.deliveryType" placeholder="配送方式" clearable @change="handleChange">
          <el-option v-for="item in deliveryTypeList" :key="item.value" :value="item.value" :label="item.label"></el-option>
        </el-select>
        <el-date-picker v-model="searchData.date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd">
        </el-date-picker>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">订单笔数</span>
            <span class="count-num-xs">{{ numData.orderCount }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">销售数量</span>
            <span class="count-num-xs">{{ numData.goodsCount }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">实付金额</span>
            <span class="count-num-xs">{{ numData.couponAmtSum }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">退款金额</span>
            <span class="count-num-xs">{{ numData.refundAmtSum }}</span>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel" :loading="downLoading">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table class="table-fixed" :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="订单号" prop="kcCode" min-width="100"></el-table-column>
        <el-table-column label="商品名称" prop="goodsName" min-width="100"></el-table-column>
        <el-table-column label="规格" prop="spaceName" min-width="100"></el-table-column>
        <el-table-column label="数量" prop="num" min-width="100"></el-table-column>
        <el-table-column label="商品金额" prop="amt" min-width="100"></el-table-column>
        <el-table-column label="优惠" prop="discount" min-width="100"></el-table-column>
        <el-table-column label="实付金额" prop="tradeAmt" min-width="100">
        </el-table-column>
        <el-table-column label="退款金额" prop="refundAmt" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.refundAmt || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="memberMobile" min-width="100"></el-table-column>
        <el-table-column label="配送方式" prop="deliveryTypeName" min-width="100"></el-table-column>
        <el-table-column label="订单状态" prop="statusName" min-width="100"></el-table-column>
        <el-table-column label="付款时间" prop="createdTime" min-width="100"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      goodsData: {},
      searchKey: 'kcCode',
      keywords: '', // 搜索关键字
      searchData: {
        kcCode: '',
        memberMobile: '',
        date: [],
        deliveryType: '',
        status: '',
      },
      tableData: [],
      numData: {},
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      marketList: [],
      selectOptions: [
        //搜索的选项
        {
          name: '订单号',
          key: 'kcCode',
        },
        {
          name: '手机号',
          key: 'memberMobile',
        },
      ],
      downLoading: false,
      statusList: [
        { label: '全部', value: '' },
        { label: '待发货', value: 'DELIVERED_ORDER' },
        { label: '已发货', value: 'SHIPPED_ORDER' },
        { label: '已到货', value: 'ARRIVED_ORDER' },
        { label: '已完成', value: 'CONFIRM_ORDER' },
        { label: '已退款', value: 'REFUNDED_ORDER' },
      ],
      deliveryTypeList: [
        { label: '全部', value: '' },
        { label: '配送', value: '1' },
        { label: '自提', value: '2' },
      ],
    };
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
  mounted() {
    console.log(this.$route);
    let date = this.$route.query.date
    if(date) {
      this.searchData.date = date
    }
    let goodsDetails = localStorage.getItem('goodsDetails')
    this.goodsData = JSON.parse(goodsDetails)
    this.getData();
    this.getCountData()
  },
  methods: {
    getData() {
      console.log(this.goodsData);
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        goods: this.goodsData.goods,
        goodsName: this.goodsData.goodsName,
        space: this.goodsData.space,
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        // kcCode: this.searchData.kcCode,
        // memberMobile: this.searchData.memberMobile,
        status: this.searchData.status,
        deliveryType: this.searchData.deliveryType,
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.data.goodsOrderLineQuery, this.$qs.stringify(params))
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    getCountData() {
      const params = {
        goods: this.goodsData.goods,
        goodsName: this.goodsData.goodsName,
        space: this.goodsData.space,
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        // kcCode: this.searchData.kcCode,
        // memberMobile: this.searchData.memberMobile,
        status: this.searchData.status,
        deliveryType: this.searchData.deliveryType,
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.data.goodsOrderLineSum, this.$qs.stringify(params))
        .then(response => {
          this.numData = response
        });
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        goods: this.goodsData.goods,
        goodsName: this.goodsData.goodsName,
        space: this.goodsData.space,
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        // kcCode: this.searchData.kcCode,
        // memberMobile: this.searchData.memberMobile,
        status: this.searchData.status,
        deliveryType: this.searchData.deliveryType,
      };
      params[this.searchKey] = this.keywords;
      let that = this;
      this.downLoading = true;
      axios
        .post(api.data.goodsOrderLineExport, this.$qs.stringify(params), {
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
              var disposition = res.headers['content-disposition'];
              var headersFileName = disposition ? disposition.split('=') : '';
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(
                  blob,
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'商品明细统计-明细'
                  }.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'商品明细统计-明细'
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
    handleChange() {
      this.page = 1;
      this.getData();
      this.getCountData();
    },
    startSearch({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
      this.getCountData();
    },
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
  }
}
</script>

<style lang="less">
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
</style>