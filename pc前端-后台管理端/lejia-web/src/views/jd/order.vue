<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="searchData.status" @change="handleChange" placeholder="订单状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <el-date-picker v-model="searchData.date" type="daterange" clearable range-separator="至"
          start-placeholder="付款开始日期" end-placeholder="付款结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
        <el-select class="tags-select" v-model="searchData.tags" @change="handleChange" filterable multiple collapse-tags clearable placeholder="选择标签">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in tagList"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box"><span class="count-tit">订单笔数</span>
            <span class="count-num-rk">{{ orderCount }}</span>
          </div>
          <div class="count-box"><span class="count-tit">总金额</span>
            <span class="count-num-xs">{{ atmCount }}</span>
          </div>
        </div>
        <el-button type="primary" size="medium" @click="handleImportExcel">
          导出
        </el-button>
        <el-button type="primary" size="medium" @click="handleImportLineExcel">
          明细导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="订单号" prop="code" min-width="120"></el-table-column>
        <el-table-column label="京东订单号" prop="jdOrderId" min-width="120"></el-table-column>
        <el-table-column label="状态" prop="statusName" min-width="140">
          <template slot-scope="scope">
            <span>{{ scope.row.statusName }}</span>
            <span style="color:#D9001B;">{{ scope.row.refundInfo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="购买用户" prop="memberMobile" min-width="150"></el-table-column>
        <el-table-column label="用户标签" prop="tagName" min-width="100" show-overflow-tooltip></el-table-column>
        <el-table-column label="配送方式" prop="distributionTypeName" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.distributionType == 'PICKUP' ? '自提' : '配送' }}
          </template>
        </el-table-column>
        <el-table-column label="付款时间" prop="createdTime" min-width="150"></el-table-column>
        <el-table-column label="商品价格" prop="goodsPrice" min-width="100"></el-table-column>
        <el-table-column label="邮费" prop="postage" min-width="100"></el-table-column>
        <el-table-column label="总价" prop="amtall" min-width="100"></el-table-column>
        <el-table-column label="支付金额" prop="amtn" min-width="100"></el-table-column>
        <el-table-column label="退款金额" prop="refundAmt" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.refundAmt || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" prop="payTypeName" min-width="100"></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <order-detail ref="orderDetail"></order-detail>
  </div>
</template>

<script>
import OrderDetail from './Order/OrderDetail.vue'
export default {
  data() {
    return {
      loading: false,
      page: 1,
      pageSize: 10,
      total: 0,
      searchData: {
        status: null,
        date: [],
        code: null,
        mobile: null,
        tags: [],
      },
      statusList: [
        {
          pkey: '',
          name: '全部',
        },
        {
          pkey: 'UNPAID_ORDER',
          name: '未付款',
        },
        {
          pkey: 'DELIVERED_ORDER',
          name: '待发货',
        },
        {
          pkey: 'SHIPPED_ORDER',
          name: '已发货',
        },
        {
          pkey: 'ARRIVED_ORDER',
          name: '已到货',
        },
        {
          pkey: 'CONFIRM_ORDER',
          name: '已完成',
        },
        {
          pkey: 'REFUNDED_ORDER',
          name: '已退款',
        },
      ],
      selectOptions: [
        {
          name: '订单编号',
          key: 'code',
        },
        {
          name: '购买用户',
          key: 'mobile',
        }
      ],
      tagList:[],
      orderCount: 0,
      atmCount: 0,
      tableData: [],
    }
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
    OrderDetail,
  },
  mounted() {
    this.getTagData()
    this.getData()
    this.getSumData()
  },
  methods: {
    getData() {
      this.loading = true;
      const { date, status, code, mobile, tags } = this.searchData
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startDate: date && date.length ? date[0] : null,
        endDate: date && date.length ? date[1] : null,
        status,
        code,
        mobile,
        tags: tags.join(','),
      };
      let url = api.jd.orderQuery;
      axios.post(url, this.$qs.stringify(params))
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    getSumData() {
      const { date, status, code, mobile, tags } = this.searchData
      const params = {
        startDate: date && date.length ? date[0] : null,
        endDate: date && date.length ? date[1] : null,
        status,
        code,
        mobile,
        tags: tags.join(','),
      };
      let url = api.jd.orderQuerySum;
      axios.post(url, this.$qs.stringify(params))
        .then((response) => {
         console.log(response);
         this.orderCount = response.count
         this.atmCount = response.sum
        });
    },
    /**
     * 搜索
     */
    handleChange() {
      this.page = 1;
      this.getData();
      this.getSumData()
    },
    /**
     * 搜索
     */
    startSearch({ key, keywords }) {
      this.selectOptions.forEach(item => {
        this.searchData[item.key] = null
      })
      this.searchData[key] = keywords;
      this.page = 1;
      this.getData();
      this.getSumData()
    },
    /**
     * 列表导出excel
     */

    handleImportExcel() {
      const { date, status, code, mobile, tags } = this.searchData
      const params = {
        startDate: date && date.length ? date[0] : null,
        endDate: date && date.length ? date[1] : null,
        status,
        code,
        mobile,
        tags: tags.join(','),
      };
      let url = api.jd.orderQueryExport;
      axios.post(url, this.$qs.stringify(params), {
          responseType: 'blob',
        })
        .then((response) => {
          var _this = this;
          if (response.data.type == 'application/json') {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          var disposition = response.headers['content-disposition'];
          var headersFileName = disposition ? disposition.split('=') : '';
          var fileName = headersFileName && headersFileName.length != 0 ? decodeURI(headersFileName[1]) : '订单列表.xlsx'
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        });
    },
    /**
     * 明细导出excel
     */
    handleImportLineExcel() {
      const { date, status, code, mobile, tags } = this.searchData
      const params = {
        startDate: date && date.length ? date[0] : null,
        endDate: date && date.length ? date[1] : null,
        status,
        code,
        mobile,
        tags: tags.join(','),
      };
      let url = api.jd.orderLineQueryExport;
      axios.post(url, this.$qs.stringify(params), {
          responseType: 'blob',
        })
        .then((response) => {
          var _this = this;
          if (response.data.type == 'application/json') {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          var disposition = response.headers['content-disposition'];
          var headersFileName = disposition ? disposition.split('=') : '';
          var fileName = headersFileName && headersFileName.length != 0 ? decodeURI(headersFileName[1]) : '订单明细列表.xlsx'
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
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
    /**
     * @desc 获取标签列表
     */
     getTagData() {
      axios.post(api.marketing.tagsDrop).then((response) => {
        this.tagList = response;
      });
    },
    /**
     * 查看详情
     */
    handleDetail(row) {
      this.$refs.orderDetail.show({
        row: row,
      });
    }
  }
}
</script>

<style lang="less" scoped>
.search-box-form {
  /deep/ .tags-select {
    width: 180px !important;
  }
}

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
