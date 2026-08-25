<!-- 
@name: headComp.vue 
@description: 大数据-左边-销量分类笔数排行榜 TOP10
@author: 池仁杰
@date: 2022/01/12
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
.classify-count {
  margin-left: (16 / @baseWidth) * 100vw;
  line-height: normal;
  overflow: hidden;
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
  .echart-box {
    box-sizing: border-box;
    // overflow-x: scroll;
    // overflow-y: hidden;
    .heightfix(116);
    overflow: hidden;
    white-space: nowrap;
    margin: (18 / @baseHeight) * 100vh (16 / @baseWidth) * 100vw
      (26 / @baseHeight) * 100vh (14 / @baseWidth) * 100vw;
    .echart-box-item {
      display: inline-block;

      position: relative;
      .widthfix(90);
      .heightfix(115);
      & + .echart-box-item {
        margin-left: (39 / @baseWidth) * 100vw;
      }
      .echart-item {
        .widthfix(90);
        .heightfix(90);
      }
       .item-info {
        .widthfix(90);
        .heightfix(90);
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        .item-info-value {
          .fontfix(18);
          font-weight: bold;
          font-style: italic;
          color: #48c9ff;
          .lhfix(13);
          margin-bottom: (6 / @baseHeight) * 100vh;
        }
        img {
          .widthfix(60);
          .heightfix(2);
          margin-bottom: (7 / @baseHeight) * 100vh;
        }
        .item-info-percent {
          .fontfix(14);
          font-weight: 400;
          color: #ffffff;
          .lhfix(13);
        }
      }
      .item-name {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        .widthfix(90);
        text-align: center;
        .fontfix(14);
        font-weight: 400;
        color: #acbfef;
        .lhfix(14);
        margin-top: (10 / @baseHeight) * 100vh;
      }
    }
    .no-data-box {
      width: 100%;
      .heightfix(116);
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
}
</style>
<template lang="html">
  <div class="classify-count">
     <div class="title-box">
       <img src="../../../../assets/images/bigData/sale_icon.png"></img>
       <span>销量分类笔数排行榜 TOP10</span>
     </div>
     <div class="echart-box" ref="echart-box">
      <div class="classify-count-echart_box" v-show="chartData.length">
        <div class="echart-box-item" v-for="(item,index) in chartData" :key="index">
          <div :id="`classify-count-${index}`" class="echart-item"></div>
          <div class="item-info">
            <div class="item-info-value">{{item.value}}</div>
            <img src="../../../../assets/images/bigData/classifycount_border.png"/>
            <div class="item-info-percent">{{item.percent}}%</div>
          </div>
          <div class="item-name">{{item.name}}</div>
        </div>
      </div>
      <div class="no-data-box" v-show="!chartData.length">
        <img src="../../../../assets/images/bigData/no-data.png" />
        <div>当前无数据</div>
      </div>
     </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      colorList: [
        '#37E8FA',
        '#00C7FE',
        '#00A5FF',
        '#0086FF',
        '#0065FF',
        '#0048FF',
        '#3C00FF',
        '#A728F8',
        '#F360B0',
        '#F2C53E',
      ],
      chartData: [],
      dataLen: 0,
      timer: '',
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
  watch: {
    dataList(newVal, oldVal) {
      if ($('.classify-count-echart_box')) {
        $('.classify-count-echart_box').animate({
          marginLeft: `0px`,
        });
      }

      if (newVal && newVal.length) {
        this.dataLen = newVal.length;
        this.chartData = newVal.map((item) => {
          return {
            name: item.typeName,
            value: item.num,
            percent: item.proportion,
          };
        });
        if (this.dataLen > 4) {
          this.chartData = this.chartData.concat(this.chartData);
        }
      } else {
        this.chartData = [];
      }
      setTimeout(() => {
        this.initEchart();
      }, 200);
    },
  },
  mounted() {},
  beforeDestroy() {
    this.stop();
  },
  methods: {
    initEchart() {
      this.chartData.map((item, index) => {
        let myChart = this.$echarts.init(
            document.getElementById(`classify-count-${index}`)
          ),
          _this = this,
          option = {
            polar: {
              radius: ['100%', '80%'],
              center: ['50%', '50%'],
            },
            angleAxis: {
              max: 100,
              show: false,
            },
            radiusAxis: {
              type: 'category',
              show: true,
              axisLabel: {
                show: false,
              },
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
            },
            series: [
              {
                name: '',
                type: 'bar',
                roundCap: true,
                barWidth: 60,
                showBackground: true,
                backgroundStyle: {
                  color: 'rgba(50, 89, 126, 0.5)',
                },
                data: [item.percent],
                coordinateSystem: 'polar',
                itemStyle: {
                  normal: {
                    color:
                      this.colorList[
                        index > this.dataLen - 1
                          ? index - (this.dataLen - 1)
                          : index
                      ],
                  },
                },
              },
            ],
          };

        myChart.setOption(option);
        window.addEventListener('resize', function () {
          myChart.resize();
        });
      });
      this.stop();
      if (this.dataLen > 4) {
        this.autoMove();
      } else {
        this.timer = setInterval(() => {
          this.$emit('getData');
          this.stop();
        }, 15000);
      }
    },
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
    autoMove() {
      let dataLength = this.dataLen,
        width =
          dataLength * 129 * (document.documentElement.clientWidth / 1920),
        singleWidth = 129 * (document.documentElement.clientWidth / 1920);
      let distance = 0; // 位移距离
      this.timer = setInterval(() => {
        distance = distance - singleWidth;
        if (-distance > width) {
          distance = 0;
          document.getElementsByClassName(
            'classify-count-echart_box'
          )[0].style.marginLeft = '0';
          this.$emit('getData');
        }

        $('.classify-count-echart_box').animate({
          marginLeft: `${distance}px`,
        });
      }, 5000);
    },
    /**
     *@desc 停止滚动
     **/
    stop() {
      clearInterval(this.timer);
    },
  },
};
</script>