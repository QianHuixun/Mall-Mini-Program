<!-- 
@name: Report.vue 
@description: 财务报表
@author: crj
@url: /order/report
@date: 2020/09/22
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- tab标签切换 -->
    <el-tabs v-model="activeName" @tab-click="handleTabChange">
      <el-tab-pane label="日汇总" name="day">
        <!-- 搜索栏 -->
        <div class="search-box">
          <!-- 搜索表单 -->
          <div class="search-box-form">
            <!-- <el-date-picker v-model="dayDate" type="month" value-format="yyyy-MM" @change="handleChange">
            </el-date-picker> -->
            <el-date-picker v-model="dayDate" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
            </el-date-picker>
            <el-select v-model="company" @change="handleCompanyChange($event)" placeholder="选择公司" value-key="pkey" clearable>
              <el-option value="" key="ALL" label="全部"></el-option>
              <el-option :value="item" :key="item.pkey" :label="item.name" v-for="(item,index) in companyList">
              </el-option>
            </el-select>
            <el-select v-model="marketPkey" @change="handleChange" placeholder="选择市场" clearable>
              <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList">
              </el-option>
            </el-select>
          </div>
          <!-- 操作按钮 -->
          <div class="search-box-button">
            <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel">
              导出
            </el-button>
          </div>
        </div>
        <!-- 表格框 -->
        <div class="table-box">
          <el-table :data="tableData" :loading="loading" border style="width: 100%">
            <el-table-column label="日期" prop="day" min-width="120"></el-table-column>
            <el-table-column label="收入（元）" prop="amt" width="100"></el-table-column>
            <el-table-column label="收入笔数" prop="amtNum" width="120"></el-table-column>
            <el-table-column label="手续费" prop="handlingFee" width="150"></el-table-column>
            <el-table-column label="支出（元）" prop="expenditure" width="100"></el-table-column>
            <el-table-column label="支出笔数" prop="expenditureNum" width="120"></el-table-column>
            <el-table-column label="收益（元）" prop="income" width="100"></el-table-column>
            <el-table-column label="查看明细" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleDetail(scope.row.day)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- 页码 -->
          <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
            :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
        </div>
      </el-tab-pane>
      <el-tab-pane label="月汇总" name="month">
        <!-- 搜索栏 -->
        <div class="search-box">
          <!-- 搜索表单 -->
          <div class="search-box-form">
            <!-- <el-date-picker v-model="monthDate" type="year" value-format="yyyy" @change="handleChange">
            </el-date-picker> -->
            <el-date-picker v-model="monthDate" type="monthrange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" value-format="yyyy-MM" @change="handleChange">
            </el-date-picker>
            <el-select v-model="company" @change="handleCompanyChange($event)" placeholder="选择公司" value-key="pkey">
              <el-option value="" key="ALL" label="全部"></el-option>
              <el-option :value="item" :key="item.pkey" :label="item.name" v-for="(item,index) in companyList">
              </el-option>
            </el-select>
            <el-select v-model="marketPkey" @change="handleChange" placeholder="选择市场">
              <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList">
              </el-option>
            </el-select>
          </div>
          <!-- 操作按钮 -->
          <div class="search-box-button">
            <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel">
              导出
            </el-button>
          </div>
        </div>
        <!-- 表格框 -->
        <div class="table-box">
          <el-table :data="tableData" :loading="loading" border style="width: 100%">
            <el-table-column label="日期" prop="day" min-width="120"></el-table-column>
            <el-table-column label="收入（元）" prop="amt" width="100"></el-table-column>
            <el-table-column label="收入笔数" prop="amtNum" width="120"></el-table-column>
            <el-table-column label="手续费" prop="handlingFee" width="150"></el-table-column>
            <el-table-column label="支出（元）" prop="expenditure" width="100"></el-table-column>
            <el-table-column label="支出笔数" prop="expenditureNum" width="120"></el-table-column>
            <el-table-column label="收益（元）" prop="income" width="100"></el-table-column>
            <el-table-column label="查看明细" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleDetail(scope.row.day)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- 页码 -->
          <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
            :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
        </div>

      </el-tab-pane>
    </el-tabs>


  </div>
</template>

<script>
  import utils from "@/assets/js/utils";
  import dropdown from "@/assets/js/dropdown";
  import qs from 'qs';
  export default {
    data() {
      return {
        loading: !1,
        tableData: [], //表格数据
        allTbleData: [], //所有表格数据
        activeName: 'day', //tab切换控量
        // date: '', 
        page: 1, //显示页码
        pageSize: 8, //表格一页显示几条     
        total: 0, //总页数,
        dayDate: '', //日期控量     
        monthDate: '', //月份日期控量
        marketList: [], //市场下拉列表
        companyList: [], //公司下拉列表
        companyPkey: '', //公司下拉列表选中项（pkey）
        marketPkey: '', //市场下拉列表选中项（pkey）
        company: '' //公司下拉列表选中对象
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
    mounted() {
      this.initData()
      this.getData();
      dropdown.getMarket().then(result => {
        this.marketList = result.content;
      });
      dropdown.getCompany().then(result => {
        this.companyList = result.content;
      });
    },
    methods: {
      /**
       * 公司下拉框发生改变
       */
      handleCompanyChange(e) {
        // console.log(e)
        this.companyPkey = e.pkey;
        this.marketPkey = '';
        if (e != "") {
          this.marketList = e.markets
        } else {
          dropdown.getMarket().then(result => {
            this.marketList = result.content;
          });
        }
        this.getData()
      },
      /**初始化日期 */
      initData() {
        let nowDate = new Date().getTime() / 1000;
        // this.dayDate = utils.formatTimeInArr(nowDate, 'Y-M');
        this.dayDateInit();
        this.monthDateInit();

        // this.monthDate = utils.formatTimeInArr(nowDate, 'Y');
        console.log(this.dayDate)
      },
      /**日汇总日期初始化 */
      dayDateInit() {
        var date = new Date(),
          currentMonth = date.getMonth(),
          nextMonth = ++currentMonth,
          nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1),
          oneDay = 1000 * 60 * 60 * 24;
        this.dayDate = [utils.formatTimeInArr(new Date(date.getFullYear(), date.getMonth(), 1).getTime() / 1000,
          'Y-M-D'), utils.formatTimeInArr(
          new Date(nextMonthFirstDay - oneDay).getTime() / 1000, 'Y-M-D')];
      },
      /**月汇总日期初始化 */
      monthDateInit() {
        var date = new Date(),
          currentMonth = date.getMonth(),
          prevYear = new Date(date.getFullYear() - 1, currentMonth, 1);
        this.monthDate = [utils.formatTimeInArr(prevYear.getTime() / 1000, 'Y-M'), utils.formatTimeInArr(date
          .getTime() / 1000, 'Y-M')];
      },
      /**excel导出 */
      handleImportExcel() {
        let params;
        if (this.activeName == 'day') {
          params = {
            startTime: this.dayDate[0],
            endTime: this.dayDate[1],
            companyPkey: this.companyPkey,
            marketPkey: this.marketPkey
          };
          location.href = `${api.order.downloadBillByDate}?${qs.stringify(params)}`;
        } else {
          params = {
            startTime: this.monthDate[0],
            endTime: this.monthDate[1],
            companyPkey: this.companyPkey,
            marketPkey: this.marketPkey
          };
          location.href = `${api.order.downloadBillByMonth}?${qs.stringify(params)}`;
        }
      },
      /**日期改变事件 */
      handleChange() {
        this.page = 1
        this.getData();
      },
      /**查看详情 */
      handleDetail(day) {
        console.log(day)
        let query;
        if (this.activeName == 'day') {
          let date = this.dayDate;
          query = {
            startTime: day,
            endTime: day
          };
        } else {
          let date = day.split("-");
          let nowMonth = Number(date[1]) - 1,
            nextMonth = Number(date[1]),
            lastDay = new Date(date[0], nextMonth, 1).getTime(),
            oneDay = 1000 * 60 * 60 * 24;
          // console.log(date, nowMonth)
          let startTime = utils.formatTimeInArr(new Date(date[0], nowMonth, 1) / 1000, 'Y-M-D'),
            endTime = utils.formatTimeInArr((lastDay - oneDay) / 1000, 'Y-M-D');
          query = {
            startTime,
            endTime
          };
        }
        this.$router.push({
          path: '/order/detail',
          query
        });
      },
      /**标签页改变事件 */
      handleTabChange(e) {
        this.date = ''
        this.page = 1
        this.getData();
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        let pageSize = this.pageSize,
          page = this.page;
        this.loading = true;
        this.tableData = this.allTableData.slice(pageSize * (page - 1), pageSize * page);
      },
      getData() {
        let params;
        if (this.activeName == 'day') {
          params = {
            startTime: this.dayDate[0],
            endTime: this.dayDate[1],
            companyPkey: this.companyPkey,
            marketPkey: this.marketPkey
          }
          axios.post(api.order.queryBillByDate, qs.stringify(params), {
              headers: {
                Authorization: this.$store.state.token
              }
            })
            .then(response => {
              let pageSize = this.pageSize,
                page = this.page;
              this.allTableData = response;
              this.tableData = this.allTableData.slice(pageSize * (page - 1), pageSize * page);
              this.total = response.length;
              setTimeout(() => {
                this.loading = false;
              }, 300);
            });
        } else {
          params = {
            startTime: this.monthDate[0],
            endTime: this.monthDate[1],
            companyPkey: this.companyPkey,
            marketPkey: this.marketPkey
          }
          axios.post(api.order.queryBillByMonth, qs.stringify(params), {
              headers: {
                Authorization: this.$store.state.token
              }
            })
            .then(response => {
              let pageSize = this.pageSize,
                page = this.page;
              this.allTableData = response;
              this.tableData = this.allTableData.slice(pageSize * (page - 1), pageSize * page);
              this.total = response.length;
              setTimeout(() => {
                this.loading = false;
              }, 300);
            });
        }

      }
    }
  }
</script>

<style lang="less" scoped>
  /deep/.el-tabs {
    margin: 0 10px;
    // padding: 5px;
  }

  .search-box {
    display: flex;
    margin: 0 10px;
    padding: 5px;
    border: 1px solid #e5e4e9;
    border-radius: 5px;

    .search-box-form {
      flex: 1;

      .el-date-editor {
        margin: 5px;
      }

      .el-select {
        margin: 5px;
        width: 110px;
      }
    }

    .search-bar {
      display: inline-block;
      width: 350px;
      height: 36px;
      margin: 5px;
    }
  }

  .table-box {
    margin: 10px;
  }
</style>