<!-- 
@name: CouponUse.vue 
@description: 卡券使用查询
@author: zs
@url: /coupon/use
@date: 2020/07/27
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
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="使用开始日期"
          end-placeholder="使用结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-date-picker v-model="date2" type="daterange" range-separator="至" start-placeholder="领取开始日期"
          end-placeholder="领取结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-select v-model="status" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList">
          </el-option>
        </el-select>
        <el-select v-model="market" @change="handleChange" clearable placeholder="使用市场">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList">
          </el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel"
            :loading="downLoading">
            导出
          </el-button>
        </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="卡券名称" prop="cardName" min-width="150"></el-table-column>
        <el-table-column label="卡券编号" prop="cardNumber" min-width="150"></el-table-column>
        <el-table-column label="卡券面额" prop="cost" min-width="150"></el-table-column>
        <el-table-column label="卡券状态" prop="statusName" min-width="150"></el-table-column>
        <el-table-column label="领取人" prop="member" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.mobile || '--' }}
          </template>
        </el-table-column>
        <el-table-column label="领取时间" prop="createdTime" min-width="150"></el-table-column>
        <el-table-column label="使用市场" prop="userFarmerName" min-width="150"></el-table-column>
        <el-table-column label="使用时间" prop="userTime" min-width="150"></el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>总数：{{num}}张</span>
        <span>未使用：{{unusedNum}}张</span>
        <span>已使用：{{usedNum}}张</span>
        <span>已过期：{{expiredNum}}张</span>
        <!-- <span>已失效：{{invalidNum}}张</span> -->
      </div>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
  </div>
</template>
<script>
  import qs from "qs";
  import dropdown from "@/assets/js/dropdown";

  export default {
    data() {
      return {
        loading: false,
        tableData: [],
        marketList: [],
        market: "",
        date: "",
        date2: "",
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        total: 0, //总页数  
        searchKey: 'mobile',
        selectOptions: [{
          name: '手机号',
          key: 'mobile',
        }, {
          name: '卡券名称',
          key: 'title',
        }, ],
        statusList: [{
            pkey: '',
            name: '卡券状态',
          },
          {
            pkey: 'UNUSED',
            name: '未使用',
          },
          {
            pkey: 'USED',
            name: '已使用',
          },
          {
            pkey: 'INVALIC',
            name: '已失效',
          },
          {
            pkey: 'EXPIRED',
            name: '已过期',
          }
        ],
        keywords: '', // 搜索关键字 
        downLoading: false,
        num: 0,
        unusedNum: 0,
        usedNum: 0,
        expiredNum: 0,
        invalidNum: 0,

      };
    },
    mounted() {
      this.getData();
      this.getNumData();
      dropdown.getMarket().then(result => {
        this.marketList = result.content;
      });
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
      handleChange: function () {
        this.page = 1;
        this.getData();
        this.getNumData();
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
        this.getNumData();
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.loading = true;
        this.getData();
        this.getNumData();
      },
      /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
      this.getNumData();
    },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startTime: this.date ? this.date[0] : "",
          endTime: this.date ? this.date[1] : "",
          userFarmer: this.market ? this.market : "",
          st:  this.date2 ? this.date2[0] : "",
          et: this.date2 ? this.date2[1] : "",
          status: this.status != 'INVALIC' ? this.status : '',
          invalid: this.status == 'INVALIC' ? true : false
        };
        params[this.searchKey] = this.keywords;
        axios.post(api.marketing.useCoupon, qs.stringify(params), {
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
      getNumData: function(){
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startTime: this.date ? this.date[0] : "",
          endTime: this.date ? this.date[1] : "",
          userFarmer: this.market ? this.market : "",
          st:  this.date2 ? this.date2[0] : "",
          et: this.date2 ? this.date2[1] : "",
          status: this.status != 'INVALIC' ? this.status : '',
          invalid: this.status == 'INVALIC' ? true : false
        };
        params[this.searchKey] = this.keywords;
        axios.post(api.marketing.useCouponSum, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.num = response.sum;
            this.unusedNum = response.unusedNum;
            this.usedNum = response.usedNum;
            this.expiredNum = response.expiredNum;
            this.invalidNum = response.invalidNum;

          });

      },
      /**列表导出 */
      handleImportExcel() {
        const params = {
          startTime: this.date ? this.date[0] : "",
          endTime: this.date ? this.date[1] : "",
          userFarmer: this.market ? this.market : "",
          st:  this.date2 ? this.date2[0] : "",
          et: this.date2 ? this.date2[1] : "",
          status: this.status != 'INVALIC' ? this.status : '',
          invalid: this.status == 'INVALIC' ? true : false
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
          .post(api.marketing.useCouponExport, qs.stringify(params), {
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
                    `${'已使用优惠券' }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${'已使用优惠券'}.xlsx`
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
    }
  }
</script>
<style lang="less" scoped>
.all-num {
  height: 100%;
  text-align: right;
  padding-right: 20px;
  line-height: 40px;
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