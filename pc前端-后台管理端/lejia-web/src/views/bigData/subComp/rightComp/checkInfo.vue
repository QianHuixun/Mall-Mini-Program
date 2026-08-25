<!-- 
@name: checkInfo.vue 
@description: 大数据-右边-检测信息
@author: 池仁杰
@date: 2022/01/14
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
.check-info {
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
    justify-content: space-between;
    .fontfix(18);
    font-weight: bold;
    color: #acbfef;
    .title-container {
      display: flex;
      align-items: center;
    }
    img {
      .heightfix(40);
      .widthfix(40);
    }
    &::after {
      content: '';
      background: url('../../../../assets/images/bigData/title_arrow.png');
      background-size: 100% 100%;
      background-repeat: no-repeat;
      .heightfix(15);
      .widthfix(17);
    }
  }

  #check-info-table {
    .table-head {
      background: transparent !important;
      border: none;
      display: flex;
      .heightfix(40);
      padding-top: (16 / @baseHeight) * 100vh;
      padding-bottom: 0;
      > div {
        display: flex;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        .fontfix(14);
        font-family: Microsoft YaHei;
        font-weight: bold;
        color: #acbfef;
        .lhfix(14);
        .widthfix(110);
        &.name {
          .widthfix(128);
          padding-left: (14 / @baseWidth) * 100vw;
        }

        &.date {
          .widthfix(98);
        }
        &.reslut {
          .widthfix(61);
        }
      }
    }
    .table-content {
      .heightfix(266);
      overflow: hidden;
      .table-content-item {
        background: transparent;
        border: none;
        padding-top: 0;
        padding-bottom: 0;
        display: flex;
        &.stripe {
          background: rgba(86, 74, 237, 0.05);
        }
        &.red-item > div {
          color: #e95358 !important;
        }
        > div {
          text-align: right;
          word-break: break-all;
          text-overflow: ellipsis;
          box-sizing: border-box;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          /*这里可以设置文本显示的行数*/
          overflow: hidden;
          color: #acbfef;
          text-align: left;
          .fontfix(16);
          font-family: Microsoft YaHei;
          font-weight: 400;
          margin: (12 / @baseHeight) * 100vh 0;
          padding-right: (6 / @baseWidth) * 100vw;
          .lhfix(20);
          .widthfix(110);
          &.name {
            .widthfix(128);
            padding-left: (14 / @baseWidth) * 100vw;
          }
          &.date {
            .widthfix(98);
            padding: 0;
          }
          &.reslut {
            .widthfix(61);
            padding: 0;
          }
        }
      }
    }
  }
  .no-data-box {
    width: 100%;
    .heightfix(306);
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
  <div class='check-info'>
     <div class='title-box'>
       <div class="title-container">
        <img src='../../../../assets/images/bigData/checkinfo_icon.png'></img>
        <span>检测信息</span>
       </div>
     </div>
     <div id='check-info-table' v-if="tableData.length">
       <div class="table-head">
         <div class="name">市场名称</div>
         <div class="classify">品名</div>
         <div class="item">检测项目</div>
         <div class="date">检测日期</div>
         <div class="reslut">检测结果</div>
       </div>
       <div class="table-content">
         <div class="check-info-table_box">
          <div class="table-content-item" :class="{'stripe':index&&index%2==1,'red-item':item.test=='不合格'}" v-for="(item,index) in tableData" :key="index">
            <div class="name">{{item.marketName}}</div>
            <div class="classify">{{item.entry}}</div>
            <div class="item">{{item.goods}}</div>
            <div class="date">{{item.testDate}}</div>
            <div class="reslut">{{item.test}}</div>
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
      $('.check-info-table_box').animate({ marginTop: `0px` });
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
      let dataLength = this.datalen;
      let height = 0,
        nodeList = document
          .getElementsByClassName('check-info-table_box')[0]
          .getElementsByClassName('table-content-item');
      for (let i = 0; i < dataLength; i++) {
        height += nodeList[i].offsetHeight;
      }
      let distance = 0, // 位移距离
        count = 0;
      this.timer = setInterval(() => {
        if (-distance >= height) {
          distance = 0;
          count = 0;
          document.getElementsByClassName(
            'check-info-table_box'
          )[0].style.marginTop = '0';
          this.$emit('getData');
        }

        distance = distance - nodeList[count].offsetHeight;
        $('.check-info-table_box').animate({ marginTop: `${distance}px` });
        count++;
      }, 5000);
    },
  },
};
</script>