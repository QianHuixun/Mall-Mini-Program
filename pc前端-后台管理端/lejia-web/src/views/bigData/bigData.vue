<!-- 
@name: bigData.vue 
@description: 大数据
@author: 池仁杰
@route: /bigData
@date: 2022/01/11
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
.bigData {
  user-select: none;
  font-family: Microsoft YaHei;
  background: url('../../assets/images/bigData/back.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  width: 100vw;
  height: 100vh;
  line-height: normal;
  .main {
    width: 100vw;
    .heightfix(994);
    padding-left: (16 / @baseWidth) * 100vw;
    padding-right: (32 / @baseWidth) * 100vw;
    display: flex;
    justify-content: space-between;

    .left-comp {
      .widthfix(524);
    }
    .center-comp {
      .widthfix(786);
    }
    .right-comp {
      .widthfix(508);
    }
  }
}
</style>
<template lang="html">
  <div class="bigData" id="bigData">
    <head-comp ref="headComp" :timeType.sync="timeType" @gerData="getClassifyAmt"></head-comp>
    <div class="main">
      <div class="left-comp">
        <classify-amt ref="classifyAmt" :dataList="classifyAmt" ></classify-amt>
        <good-price ref="goodPrice" :dataList="goodPrice" @getData="getGoodPrice"></good-price>
        <classify-count ref="classifyCount" :dataList="classifyCount" @getData="getClassifyCount"></classify-count>
        <good-count ref="goodCount" :dataList="goodCount" @getData="getGoodCount"></good-count>
      </div>
      <div class="center-comp">
        <market-map ref="marketMap" :dataList="marketMap" @getData="getMarketMap(1)"></market-map>
        <trade-info ref="tradeInfo" :dataList="tradeInfo" @getData="getTradeInfo(1)"></trade-info>
      </div>
      <div class="right-comp">
        <sale-amt ref="saleAmt" :dataList="saleAmt" ></sale-amt>
        <market-sale ref="marketSale"  :dataList="marketSale" @getData="getMarketMap(2)"></market-sale>
        <check-info ref="checkInfo" :dataList="checkInfo" @getData="getCheckInfo"></check-info>
      </div>
    </div>
  </div>
</template>
<script>
import headComp from './subComp/headComp';
import classifyAmt from './subComp/leftComp/classifyAmt'; //销量分类金额排行榜 TOP10
import goodPrice from './subComp/leftComp/goodPrice'; //销量商品金额排行榜 TOP20
import classifyCount from './subComp/leftComp/classifyCount'; //销量分类笔数排行榜 TOP10
import goodCount from './subComp/leftComp/goodCount'; //销量商品笔数排行榜 TOP20
import saleAmt from './subComp/rightComp/saleAmt/saleAmt'; //销售额 · 元
import marketSale from './subComp/rightComp/marketSale'; //市场销售详情
import checkInfo from './subComp/rightComp/checkInfo'; //检测信息
import marketMap from './subComp/centerComp/marketMap/marketMap'; //市场地图
import tradeInfo from './subComp/centerComp/tradeInfo'; //实时交易额

export default {
  data() {
    return {
      timeType: 'THE_DAY',
      classifyAmt: [],
      goodPrice: [],
      classifyCount: [],
      marketMap: [],
      checkInfo: [],
      marketSale: [],
      goodCount: [],
      tradeInfo: { orderNum: 0, rtsList: [], visitor: 0 },
      saleAmt: {},
      token: '',
      timer: '',
    };
  },
  components: {
    headComp,
    classifyAmt,
    goodPrice,
    classifyCount,
    goodCount,
    saleAmt,
    marketSale,
    checkInfo,
    marketMap,
    tradeInfo,
  },
  watch: {
    timeType(newVal, oldVal) {
      this.getMarketMap();
      this.getTradeInfo();
      this.getClassifyAmt();
      this.getGoodPrice();
      this.getClassifyCount();
      this.getGoodCount();
      this.getCheckInfo();
    },
  },
  mounted() {
    const pkey = this.$route.query.pkey
    console.log(localStorage.getItem('token'))
    if(!localStorage.getItem('token') || pkey) {
      this.getToken();
    } else {
      this.token = localStorage.getItem('token')
    }
    // this.getToken();
    this.timer = setInterval(() => {
      this.getTradeInfo(2);
      this.getClassifyAmt();
    }, 30000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    /**
     * @desc 获取大数据屏token
     */
    getToken() {
      axios.post(api.bigData.queryToken, {}).then((res) => {
        this.token = 'Bearer ' + res.accessToken.access_token;
        this.getMarketMap();
        this.getTradeInfo();
        this.getClassifyAmt();
        this.getGoodPrice();
        this.getClassifyCount();
        this.getGoodCount();
        this.getCheckInfo();
      });
    },
    /**
     * @desc 获取中间-地图数据 以及右边-市场销售详情
     */
    getMarketMap(type = 0) {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryMarketMap, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          // console.log('type', type);

          if (!type) {
            this.marketMap = res;
            this.marketSale = res;
          } else if (type == 1) {
            this.marketMap = res;
          } else {
            this.marketSale = res;
          }
        });
    },
    /**
     * @desc 获取右边-监测信息
     */
    getCheckInfo() {
      let params = {
        timeType: this.timeType,
        page: 0,
        pagesize: 9999999,
      };
      axios
        .post(api.bigData.queryCheckInfo, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          this.checkInfo = res.content;
        });
    },
    /**
     * @desc 获取左边-销量分类金额排行榜 TOP10
     */
    getClassifyAmt() {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryClassifyAmt, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          this.classifyAmt = res;
        });
    },
    /**
     * @desc 获取左边-销量商品金额排行榜 TOP20
     */
    getGoodPrice() {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryGoodPrice, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          this.goodPrice = res;
        });
    },
    /**
     * @desc 获取左边-销量分类笔数排行榜 TOP10
     */
    getClassifyCount() {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryClassifyCount, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          this.classifyCount = res;
        });
    },
    /**
     * @desc 获取左边-销量商品笔数排行榜 TOP20
     */
    getGoodCount() {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryGoodCount, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          this.goodCount = res;
        });
    },
    /**
     * @desc 获取中间 -实时交易额 以及  右边-销售额
     */
    getTradeInfo(type = 0) {
      let params = {
        timeType: this.timeType,
      };
      axios
        .post(api.bigData.queryTradeInfo, this.$qs.stringify(params), {
          headers: {
            Authorization: this.token,
          },
        })
        .then((res) => {
          if (!type) {
            this.tradeInfo = res;
            this.saleAmt = res;
          } else if (type == 1) {
            this.tradeInfo = res;
          } else {
            this.saleAmt = res;
          }
        });
    },
  },
};
</script>