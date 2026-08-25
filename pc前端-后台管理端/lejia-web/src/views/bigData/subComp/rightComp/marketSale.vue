<!-- 
@name: marketSale.vue 
@description: 大数据-右边-市场销售详情
@author: 池仁杰
@date: 2022/01/13
-->
<style lang='less' scoped>
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
.market-sale {
  line-height: normal;
  overflow: hidden;
  margin-top: (19 / @baseHeight) * 100vh;
  .title-box {
    background: url('../../../../assets/images/bigData/title_back.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    .heightfix(32);
    display: flex;
    align-items: center;
    .fontfix(18);
    font-weight: bold;
    color: #acbfef;
    img {
      .heightfix(40);
      .widthfix(40);
    }
  }

  #market-sale-table {
    .table-head {
      background: transparent !important;
      border: none;
      display: flex;
      .heightfix(40);
      padding-top: (16 / @baseHeight) * 100vh;
      padding-bottom: 0;
      > div {
        display: flex;
        justify-content: flex-end;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        .fontfix(14);
        font-family: Microsoft YaHei;
        font-weight: bold;
        color: #acbfef;
        .lhfix(14);
        padding: 0 (10 / @baseWidth) * 100vw;
        &.rank {
          .widthfix(80);
          justify-content: center;
        }
        &.name {
          .widthfix(128);
          justify-content: flex-start;
        }
        &.amt,
        &.count {
          flex: 1;
        }
      }
    }
    .table-content {
      .heightfix(240);
      overflow: hidden;
      .table-content-item {
        background: transparent;
        border: none;
        padding-top: 0;
        padding-bottom: 0;
        .heightfix(40);
        display: flex;
        &.stripe {
          background: rgba(86, 74, 237, 0.05);
        }
        > div {
          text-align: right;
          display: inline-block;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          .fontfix(16);
          font-family: Microsoft YaHei;
          font-weight: bold;
          color: #acbfef;
          padding: (12 / @baseHeight) * 100vh (10 / @baseWidth) * 100vw;
          .lhfix(16);
        }
        .rank {
          .widthfix(80);
          text-align: center;

          span {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            .heightfix(22);
            .widthfix(19);
            background: #0086ff;
            border-radius: (2 / @baseWidth) * 100vw;
            color: #fff;
            .fontfix(14);
            .lhfix(14);
          }
          &.rank1 span {
            background: #e95358;
          }
          &.rank2 span {
            background: #f58e21;
          }
          &.rank3 span {
            background: #009fe2;
          }
        }
        .name {
          justify-content: flex-start;
          text-align: left;
          font-weight: 400;
          color: #acbfef;
          .widthfix(128);
        }
        .amt,
        .count {
          flex: 1;
        }
      }
    }
  }
  .no-data-box {
    width: 100%;
    .heightfix(280);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    .fontfix(16);
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #6a94d8;
    img {
      .heightfix(72);
      .widthfix(87);
      margin-bottom: (15 / @baseHeight) * 100vh;
    }
  }
}
</style>
<template lang='html'>
  <div class='market-sale'>
     <div class='title-box'>
       <img src='../../../../assets/images/bigData/marketsale_icon.png'></img>
       <span>市场销售详情</span>
     </div>
     <div id='market-sale-table' v-if="tableData.length">
       <div class="table-head">
         <div class="rank">排名</div>
         <div class="name">市场名称</div>
         <div class="amt">销售金额</div>
         <div class="count">销售量</div>
       </div>
       <div class="table-content">
         <div class="market-sale-table_box">
          <div class="table-content-item" :class="{'stripe':index&&index%2==1}" v-for="(item,index) in tableData" :key="index">
            <div class="rank" :class='`rank${dataList.length>6?(index+1>datalen?index+1-datalen:index+1):index+1}`'>
              <span>{{datalen>6?(index+1>datalen?index+1-datalen:index+1):index+1}}</span></div>
            <div class="name">{{item.marketName}}</div>
            <div class="amt">{{item.sales}}</div>
            <div class="count">{{item.num}}</div>
          </div>
         </div>
       </div>
     </div>
       <div class="no-data-box" v-else>
        <img src="../../../../assets/images/bigData/no-data.png" />
        <div>当前无数据</div>
      </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      datalen: 0,
      timer: '',
      otherTimer: '',
      loading: false,
    };
  },
  props: {
    dataList: {
      type: Array,
      default: () => {
        return [];
      },
    },
  },

  computed: {
    tableData() {
      let dataList = JSON.parse(JSON.stringify(this.dataList));
      this.datalen = dataList.length;
      $('.market-sale-table_box').animate({ marginTop: `0px` });
      if (dataList.length > 6) {
        dataList = dataList.concat(this.dataList);
        setTimeout(() => {
          this.getScroll();
        }, 200);
      } else {
        this.stop();

        this.timer = setInterval(() => {
          this.$emit('getData');
          this.stop();
        }, 15000);
      }
      return dataList;
    },
  },
  mounted() {},
  beforeDestroy() {
    this.stop();
  },
  methods: {
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
    /**
     *@desc 停止滚动
     **/
    stop() {
      clearInterval(this.timer);
    },
    getScroll() {
      this.stop();
      let dataLength = this.datalen,
        height =
          dataLength * 40 * (document.documentElement.clientHeight / 1080);
      let distance = 0; // 位移距离
      this.timer = setInterval(() => {
        if (-distance >= height) {
          distance = 0;
          document.getElementsByClassName(
            'market-sale-table_box'
          )[0].style.marginTop = '0';
          this.$emit('getData');
        }
        distance =
          distance - 40 * (document.documentElement.clientHeight / 1080);
        $('.market-sale-table_box').animate({ marginTop: `${distance}px` });
      }, 5000);
    },
  },
};
</script>