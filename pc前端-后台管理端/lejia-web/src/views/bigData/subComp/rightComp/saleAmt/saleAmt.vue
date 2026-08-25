<!-- 
@name: saleAmt.vue 
@description: 大数据-右-销售额 · 元
@author: 池仁杰
@date: 2022/01/13
-->
<style lang="less" scoped>
@baseWidth: 1920px;
@baseHeight: 1080px;
.widthfix(@w) {
  width: (@w / @baseWidth) * 100vw;
}
.heightfix(@h) {
  height: (@h / @baseHeight) * 100vh;
}
.fontfix(@size) {
  font-size: (@size / @baseHeight) * 100vh;
}
.lhfix(@lh) {
  line-height: (@lh / @baseHeight) * 100vh;
}
.sale-amt {
  line-height: normal;
  background: url('../../../../../assets/images/bigData/saleamt_back.png');
  background-repeat: no-repeat;
  background-size: 100% 100%;
  .heightfix(254);
  .title-box {
    display: flex;
    align-items: center;
    .heightfix(42);
    .fontfix(16);
    font-weight: bold;
    color: #acbfef;
    padding-bottom: (2 / @baseHeight) * 100vh;
    padding-top: (5 / @baseHeight) * 100vh;
    img {
      .heightfix(42);
      .widthfix(42);
    }
    span {
      .lhfix(16);
    }
  }
  .sale-amt-main {
    padding: 0 (12 / @baseWidth) * 100vw;
    .double-row {
      margin-top: (16 / @baseHeight) * 100vh;
      display: flex;
      justify-content: space-between;
      .heightfix(64);

      .double-row-item {
        position: relative;
        background: url('../../../../../assets/images/bigData/saleamt_double_back.png');
        background-repeat: no-repeat;
        background-size: 100% 100%;
        .heightfix(64);
        .widthfix(238);
        background: rgba(27, 33, 114, 0.3);
        padding-top: (36 / @baseHeight) * 100vh;
        & > .title-box {
          padding-top: 0;
          position: absolute;
          top: (-4 / @baseHeight) * 100vh;
        }
        .num span {
          display: inline-block;
          .lhfix(20);
          margin-left: (14 / @baseWidth) * 100vw;
          .fontfix(20);
          font-weight: bold;
          color: #ffffff;
          font-family: Microsoft YaHei;
        }
      }
    }
    .single-row {
      margin-top: (16 / @baseHeight) * 100vh;
      display: flex;
      justify-content: space-between;
      .single-row-item {
        width: 25%;
        & > .title-box {
          display: flex;
          align-items: center;
          font-weight: bold;
          font-size: (16 / @baseWidth) * 100vw;
          color: #acbfef;
          padding-top: 0;
          padding-bottom: (12 / @baseHeight) * 100vh;
          img {
            .heightfix(12);
            .widthfix(12);
            margin-right: (8 / @baseWidth) * 100vw;
          }
          span {
            .lhfix(16);
          }
        }
        .num > span {
          display: inline-block;
          .lhfix(20);

          .fontfix(20);
          font-weight: bold;
          font-family: Microsoft YaHei;
          color: #48c9ff;
          &:nth-child(1) {
            margin-left: (14 / @baseWidth) * 100vw;
          }
          & > div {
            display: inline-block;
          }
        }
      }
    }
  }
}
</style>
<template lang="html">
  <div class="sale-amt">
    <div class="title-box">
      <img src="../../../../../assets/images/bigData/saleamt_amt_icon.png"></img>
      <span>销售额 · 元</span>
    </div>
    <div class="sale-amt-main">
      <range-amt  ref="rangeAmt" :amt="amtData.sales"></range-amt>
      <div class="double-row">
        <div class="double-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_visitor_icon.png"></img>
            <span>访客数 · 人</span>
          </div>
          <div class="num">
           <countTo :startVal='lastData.visitor' :endVal='amtData.visitor' :duration='1000'></countTo>
          </div>
        </div>
        <div class="double-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_order_icon.png"></img>
            <span>订单数</span>
          </div>
           <div class="num">
             <countTo :startVal='lastData.orderNum' :endVal='amtData.orderNum' :duration='1000'></countTo>
           </div>
        </div>
      </div>
      <div class="single-row">
        <div class="single-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_single_icon.png"></img>
            <span>退款金额 · 元</span>
          </div>
          <div class="num">
           <countTo :startVal='lastData.refund' :endVal='amtData.refund' :duration='1000'></countTo>
           <span v-if="amtData.refundd||amtData.refundds">.<div>
            <countTo :startVal='lastData.refundd' :endVal='amtData.refundd' :duration='1000'></countTo>
            </div>
            <div>
            <countTo :startVal='lastData.refundds' :endVal='amtData.refundds' :duration='1000'></countTo>
            </div>
           </span>
           
          </div>
        </div>
        <div class="single-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_single_icon.png"></img>
            <span>支付转化率</span>
          </div>
           <div class="num">
             <countTo :startVal='lastData.conversionRates' :endVal='amtData.conversionRates' :duration='1000'></countTo>
             <span v-if="amtData.conversionRatesd||amtData.conversionRatesds">.<div>
               <countTo :startVal='lastData.conversionRatesd' :endVal='amtData.conversionRatesd' :duration='1000'></countTo>
              </div>
              <div>
              <countTo :startVal='lastData.conversionRatesds' :endVal='amtData.conversionRatesds' :duration='1000'></countTo>
              </div>
             </span>
             <span class="unit">%</span>
           </div>
        </div>
        <div class="single-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_single_icon.png"></img>
            <span>客单价 · 元</span>
          </div>
           <div class="num">
             <countTo :startVal='lastData.customerPrice' :endVal='amtData.customerPrice' :duration='1000'></countTo>
             <span v-if="amtData.customerPriced||amtData.customerPriceds">.<div>
              <countTo :startVal='lastData.customerPriced' :endVal='amtData.customerPriced' :duration='1000'></countTo>
              </div>
              <div>
              <countTo :startVal='lastData.customerPriceds' :endVal='amtData.customerPriceds' :duration='1000'></countTo>
              </div>
             </span>
           </div>
        </div>
        <div class="single-row-item">
          <div class="title-box">
            <img src="../../../../../assets/images/bigData/saleamt_single_icon.png"></img>
            <span>复购率</span>
          </div>
           <div class="num">
             <countTo :startVal='lastData.repurchaseRate' :endVal='amtData.repurchaseRate' :duration='1000'></countTo>
             <span v-if="amtData.repurchaseRated||amtData.repurchaseRateds">.<div>
              <countTo :startVal='lastData.repurchaseRated' :endVal='amtData.repurchaseRated' :duration='1000'></countTo>
              </div>
              <div>
              <countTo :startVal='lastData.repurchaseRateds' :endVal='amtData.repurchaseRateds' :duration='1000'></countTo>
              </div>
             </span>
            
             <span  class="unit">%</span>
           </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import rangeAmt from './subComp/rangeAmt.vue'; //金额滚动组件
import countTo from 'vue-count-to';
export default {
  data() {
    return {
      amtData: {
        conversionRates: 0,
        conversionRatesd: 0,
        conversionRatesds: 0,

        customerPrice: 0,
        customerPriced: 0,
        customerPriceds: 0,

        refund: 0,
        refundd: 0,
        refundds: 0,

        repurchaseRate: 0,
        repurchaseRated: 0,
        repurchaseRateds: 0,
        orderNum: 0,

        sales: '0.00',
        visitor: 0,
      },
      lastData: {
        conversionRates: 0,
        conversionRatesd: 0,
        conversionRatesds: 0,

        customerPrice: 0,
        customerPriced: 0,
        customerPriceds: 0,

        refund: 0,
        refundd: 0,
        refundds: 0,

        repurchaseRate: 0,
        repurchaseRated: 0,
        repurchaseRateds: 0,
        orderNum: 0,

        sales: '0.00',
        visitor: 0,
      },
    };
  },
  props: {
    dataList: {
      type: Object,
      default: () => {
        return {
          conversionRates: '0.00',
          customerPrice: '0.00',
          orderNum: 0,
          refund: '0.00',
          repurchaseRate: '0.00',
          sales: '0.00',
          visitor: 0,
        };
      },
    },
  },
  watch: {
    dataList(newVal, oldVal) {
      if (newVal) {
        if (this.amtData.orderNum) {
          this.lastData = this.amtData;
        }
        this.amtData = {
          conversionRates: Number(newVal.conversionRates.split('.')[0]),
          conversionRatesd: Number(newVal.conversionRates.split('.')[1][0]),
          conversionRatesds: Number(newVal.conversionRates.split('.')[1][1]),

          customerPrice: Number(newVal.customerPrice.split('.')[0]),
          customerPriced: Number(newVal.customerPrice.split('.')[1][0]),
          customerPriceds: Number(newVal.customerPrice.split('.')[1][1]),

          refund: Number(newVal.refund.split('.')[0]),
          refundd: Number(newVal.refund.split('.')[1][0]),
          refundds: Number(newVal.refund.split('.')[1][1]),

          repurchaseRate: Number(newVal.repurchaseRate.split('.')[0]),
          repurchaseRated: Number(newVal.repurchaseRate.split('.')[1][0]),
          repurchaseRateds: Number(newVal.repurchaseRate.split('.')[1][1]),

          orderNum: newVal.orderNum,
          sales: newVal.sales,
          visitor: newVal.visitor,
        };
      }
    },
  },
  components: {
    rangeAmt,
    countTo,
  },
  mounted() {},
  methods: {
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
  },
};
</script>