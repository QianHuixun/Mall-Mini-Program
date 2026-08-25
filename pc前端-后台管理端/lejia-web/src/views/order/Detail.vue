<!-- 
@name: Detail.vue 
@description: 账单明细
@author: crj
@url: /order/detail
@date: 2020/09/22
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker v-model="date" type="daterange" :clearable="false" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-checkbox-group v-model="checkList" @change="handleCheckedChange">
          <el-checkbox label="mobile">购物</el-checkbox>
          <el-checkbox label="recharge">充值</el-checkbox>
          <el-checkbox label="member">会员办理</el-checkbox>
          <el-checkbox label="withdraw">提现</el-checkbox>
        </el-checkbox-group>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="all-num">
          <span>笔数：{{num}}</span>
          <span>金额：{{amt}}元</span>
        </div>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="时间" prop="payTime"></el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="类型" prop="buyType"></el-table-column>
        <el-table-column label="金额" prop="amt"></el-table-column>
        <el-table-column label="手续费" prop="handlingFee"></el-table-column>
        <el-table-column label="支付方式" prop="payTypeN"></el-table-column>
        <el-table-column label="支付订单号" prop="orderNumber"></el-table-column>
        <el-table-column label="系统单号" prop="code" min-width="140px"></el-table-column>
        <el-table-column label="状态" prop="status"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>

<script>
  import utils from "@/assets/js/utils";
  import qs from 'qs'
  export default {
    data() {
      return {
        loading: !1,
        tableData: [], //表格数据
        date: '', //日期控量
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条     
        total: 0, //总页数  
        checkList: ['withdraw', 'recharge', 'mobile', 'member'],
        num: 0, //总笔数
        amt: 0, //总金额
      }
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
    mounted() {
      this.initData()

      this.loadNumData();
      this.getData();
    },
    methods: {
      /**初始化日期 */
      initData() {
        if (this.$route.query.startTime)
          this.date = [this.$route.query.startTime, this.$route.query.endTime];
        else {
          let nowDate = new Date(), //取下个月的第一天    
            year = nowDate.getFullYear(),
            month = nowDate.getMonth();
          let startTime = utils.formatTimeInArr(new Date(year, month, 1).getTime() / 1000, 'Y-M-D'), //当月的第一天 
            endTime = utils.formatTimeInArr(nowDate.getTime() / 1000, 'Y-M-D'); //当天
          this.date = [startTime, endTime]
        }
      },
      /**
       * 复选框改变事件
       */
      handleCheckedChange(e) {
        this.page = 1;
        this.getData();
        this.loadNumData();
      },

      /**日期改变事件 */
      handleChange() {
        if (this.date == null)
          return;
        this.page = 1;
        this.loadNumData();
        this.getData();

      },
      /**查看详情 */
      handleDetail(e) {
        console.log(e)
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      /**读取总数 */
      loadNumData() {
        console.log(this.date)
        let params = {
          startTime: this.date != null ? this.date[0] : '',
          endTime: this.date != null ? this.date[1] : '',
          withdraw: false,
          recharge: false,
          mobile: false,
          member: false,
        };
        this.checkList.forEach(item => {
          params[item] = true;
        });
        axios.post(api.order.queryDetailCount, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.num = res.num;
            this.amt = res.amt;
          });
      },
      /**读取列表数据 */
      getData() {
        let params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          startTime: this.date != null ? this.date[0] : '',
          endTime: this.date != null ? this.date[1] : '',
          withdraw: false,
          recharge: false,
          mobile: false,
          member: false,
        };
        this.checkList.forEach(item => {
          params[item] = true
        });
        axios.post(api.order.queryBillDetail, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.tableData = res.content;
            this.total = res.total;
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
    display: flex;
    align-items: center;

    span {
      display: inline-block;
      margin-right: 10px;
    }
  }

  /deep/.el-checkbox-group {
    display: inline-block;
    margin: 5px;
    vertical-align: middle;
  }
</style>