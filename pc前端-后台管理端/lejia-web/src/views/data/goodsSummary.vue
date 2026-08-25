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
        <el-select v-if="marketType == 'undefined'" v-model="searchData.marketPkey" placeholder="选择市场" clearable @change="handleChange">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList"></el-option>
        </el-select>
        <el-date-picker v-model="searchData.date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
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
            <span class="count-tit">销售额</span>
            <span class="count-num-xs">{{ numData.actualAmtSum }}</span>
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
        <el-table-column label="商品名称" prop="goodsName" min-width="100"></el-table-column>
        <el-table-column label="规格" prop="spaceName" min-width="100"></el-table-column>
        <el-table-column label="订单笔数" prop="orderCount" min-width="100"></el-table-column>
        <el-table-column label="销售数量" prop="goodsCount" min-width="100"></el-table-column>
        <el-table-column label="销售额" prop="actualAmtSum" min-width="100"></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="handleGoDetail(scope.row)">明细</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
  </div>
</template>

<script>
import dropdown from '@/assets/js/dropdown';
export default {
  data() {
    return {
      marketType: localStorage.getItem('marketType'),
      loading: false,
      searchData: {
        goodsName: '',
        marketPkey: '',
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
          name: '商品名称',
          key: 'goodsName',
        },
      ],
      downLoading: false,
    };
  },
  mounted() {
    // dropdown.getMarket().then((result) => {
    //   this.marketList = result.content;
    // });
    this.getMarket()
    this.getData();
    this.getCountData();
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
  methods: {
    getMarket() {
      axios.post(api.data.farmerOptions)
        .then(res => {
          console.log(res);
          this.marketList = res;
        })
    },
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        goodsName: this.searchData.goodsName,
        farmer: this.searchData.marketPkey,
      };
      axios.post(api.data.goodsLineSummary, this.$qs.stringify(params))
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
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        goodsName: this.searchData.goodsName,
        farmer: this.searchData.marketPkey,
      };
      axios.post(api.data.goodsLineSum, this.$qs.stringify(params))
        .then(response => {
          this.numData = response
        });
    },
    handleChange() {
      this.page = 1;
      this.getData();
      this.getCountData();
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        startTime: this.searchData.date ? this.searchData.date[0] : "",
        endTime: this.searchData.date ? this.searchData.date[1] : "",
        goodsName: this.searchData.goodsName,
        farmer: this.searchData.marketPkey,
      };
      let that = this;
      this.downLoading = true;
      axios
        .post(api.data.goodsLineExport, this.$qs.stringify(params), {
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
                  `${'商品明细统计'
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
                  `${'商品明细统计'
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
    startSearch({ key, keywords }) {
      this.searchData[key] = keywords
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
    handleGoDetail(row) {
      console.log(row);
      localStorage.setItem('goodsDetails', JSON.stringify(row))
      this.$router.push({
        path: '/data/goodsSummary/detail',
        query: {date: this.searchData.date}
      })
    }
  }
}
</script>

<style lang="less" scoped>
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
