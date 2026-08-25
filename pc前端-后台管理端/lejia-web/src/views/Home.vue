<!-- 
@name: Home.vue 
@description: 概述 
@author: sx
@date: 2020/03/20 
-->
<template>
  <div id="home">
    <div class="title" v-if="loading">欢迎使用{{ saasName }}平台</div>
    <div v-show="!loading">
      <!---->
      <data-state :realData="realData"></data-state>
      <chart-state :realData="realData" :chartData="chartData"></chart-state>
      <div class="chart-box">
        <chart-analysis
          title="市场销售概况"
          echartType="bar"
          chartId="chartBar"
          :chartData="marketSaleData"
          :loading="loading"
          width="50%"
          v-if="userIdentity == 1"
        >
        </chart-analysis>
        <chart-analysis
          title="专区销量概况"
          echartType="pie"
          chartId="chartPie"
          :chartData="zoneSaleData"
          :loading="loading"
          :width="userIdentity == 1 ? '50%' : '100%'"
        ></chart-analysis>
      </div>
      <div class="from-box">
        <div class="from-container">
          <div class="from-title">销售排行</div>
          <div class="rank-from-box">
            <rank-from
              :tableContentData="goodsContent"
              :tableData="goodsRankData"
              title="商品销量排行TOP10"
              ref="goodsrankfrom"
            ></rank-from>
            <rank-from
              :tableContentData="storeContent"
              :tableData="storeRankData"
              title="库存预警"
              ref="storerankfrom"
            >
            </rank-from>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
import DataState from "@/components/home/DataState";
import ChartState from "@/components/home/ChartState";
import ChartAnalysis from "@/components/home/Chart";
import RankFrom from "@/components/home/RankFrom";
import utils from "@/assets/js/utils";
export default {
  data() {
    return {
      loading: true,
      goodsContent: [
        {
          propName: "index",
          label: "排行"
        },
        {
          propName: "name",
          label: "名称"
        },
        {
          propName: "SalesNum",
          label: "数量"
        },
        {
          propName: "Sales",
          label: "金额"
        }
      ], //商品排行表格内容
      storeContent: [
        {
          propName: "index",
          label: "排行"
        },
        {
          propName: "goodsName",
          label: "名称"
        },
        {
          propName: "kcNum",
          label: "库存"
        }
      ], //库存预警表格内容
      goodsRankData: [], //商品排行表格数据
      storeRankData: [], //库存预警表格数据
      marketSaleData: [], //市场销售柱状图
      zoneSaleData: [], //专区销售饼图
      realData: {}, //实时数据
      chartData: [] //销售柱状图
    };
  },
  components: {
    DataState,
    ChartState,
    ChartAnalysis,
    RankFrom
  },
  computed: {
    ...mapState({
      userinfo: state => state.userinfo
    }),
    userIdentity() {
      return this.$store.state.userIdentity;
    },
    saasName() {
      return this.$store.state.saasName
        ? this.$store.state.saasName
        : localStorage.getItem("saasName");
    }
  },
  mounted() {
    this.getMarketSaleData();
    this.getZoneSaleData();
    this.getgoodsRankData();
    this.getstoreRankData();
    this.getRealdata();
    this.getChartData();
  },
  methods: {
    /**
     * 获取柱状图市场销售数据
     */
    getMarketSaleData() {
      // this.marketSaleData;
      axios
        .post(api.index.queryFarmsales, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.marketSaleData = response;
        });
    },
    /**
     * 获取饼图专区销售数据
     */
    getZoneSaleData() {
      axios
        .post(api.index.queryZonesales, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.zoneSaleData = response;
        });
    },
    /**
     * 获取表格商品TOP10数据
     */
    getgoodsRankData() {
      axios
        .post(api.index.queryGoodsrank, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.goodsRankData = response;
        });
    },
    /**
     * 获取表格库存预警数据
     */
    getstoreRankData() {
      axios
        .post(api.index.queryKcwarn, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.storeRankData = response;
        });
    },
    /**
     * 获取实时概况数据
     */
    getRealdata() {
      axios
        .post(api.index.queryRealdata, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.realData = this.formatData(response);
        });
    },
    /**
     * 获取销售柱状图数据
     */
    getChartData() {
      axios
        .post(api.index.querySales, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.loading = false;
          this.chartData = response;
          // this.chartInit();
        });
    },
    /**
     * 处理数据
     */
    formatData(newVal) {
      let initData = function(data, num) {
        return data ? utils.formatMoney(data, num) : 0;
      };
      newVal.tSales = initData(newVal.tSales, 2);
      newVal.ySales = initData(newVal.ySales, 2);
      newVal.tAmtn = initData(newVal.tAmtn, 2);
      newVal.yAmtn = initData(newVal.yAmtn, 2);
      newVal.comms = initData(newVal.comms, 2);
      newVal.memberFeeNum = initData(newVal.memberFeeNum, 0);
      newVal.memberNum = initData(newVal.memberNum, 0);
      newVal.memberPayNum = initData(newVal.memberPayNum, 0);
      newVal.yAccessNum = initData(newVal.yAccessNum, 0);
      newVal.tAccessNum = initData(newVal.tAccessNum, 0);
      newVal.tComms = initData(newVal.tComms, 2);
      newVal.tMemberPayNum = initData(newVal.tMemberPayNum, 0);
      newVal.tMemberFeeNum = initData(newVal.tMemberFeeNum, 0);
      newVal.tMemberNum = initData(newVal.tMemberNum, 0);
      return newVal;
    }
  }
};
</script>
<style lang="less">
#home {
  .title {
    margin-top: 150px;
    font-size: 38px;
    text-align: center;

    color: #ddd;
  }

  .chart-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .from-box {
    border-radius: 5px;
    font-size: 16px;
    background: #f2f2f2;
    margin: 20px 20px;
    padding: 10px;

    .from-container {
      box-shadow: 0 3px 6px rgba(0, 0, 0, 0.2);
      border-radius: 5px;
      background: #fff;
      padding: 10px 20px 0 20px;

      .from-title {
        padding-bottom: 20px;
      }

      .rank-from-box {
        display: flex;
        justify-content: space-between;
      }
    }
  }
}
</style>
