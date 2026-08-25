<!-- 
@name: tradeInfo.vue 
@description: 大数据-中间-实时交易额
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
.trade-info {
  line-height: normal;
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
    .title-num {
      display: flex;
      align-items: center;
      .num-title {
        .fontfix(14);
        font-weight: 400;
        color: #7877ba;
        padding-right: (12 / @baseWidth) * 100vw;
      }
      .num-title-content {
        display: flex;
        align-items: flex-end;
        .fontfix(20);
        .lhfix(20);

        font-weight: bold;
        color: #ffffff;
        span {
          font-family: Microsoft YaHei;
        }
        .title-num-decimal {
          display: flex;
          align-items: flex-end;
          padding-left: (3 / @baseWidth) * 100vw;
          .fontfix(16);
          .lhfix(16);
          font-weight: bold;
          color: #7877ba;
        }
      }
    }
    .title-num::after {
      content: '';
      background: url('../../../../assets/images/bigData/title_arrow.png');
      background-size: 100% 100%;
      background-repeat: no-repeat;
      margin-left: (12 / @baseWidth) * 100vw;
      .heightfix(15);
      .widthfix(17);
    }
  }
  .echart-box {
    display: flex;
    justify-content: space-between;
    padding: (20 / @baseHeight) * 100vh 0;
    #trade-info {
      .heightfix(250);
      .widthfix(786);
    }
    .no-data-box {
      width: 100%;
      .heightfix(250);
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
.trade-info-tooltip {
  border: 2px solid red;
}
</style>
<template lang="html">
  <div class="trade-info">
     <div class="title-box">
       <div class="title-container">
        <img src='../../../../assets/images/bigData/saleamt_amt_icon.png'></img>
        <span>实时交易额</span>
       </div>
       <div class="title-num">
         <span class="num-title">销售额:</span>
         <div class="num-title-content">
          <countTo :startVal='lastData.num' :endVal='nowData.num' :duration='1000'></countTo>
          <span class="title-num-decimal">. 
            <div>
              <countTo :startVal='lastData.decimalOne' :endVal='nowData.decimalOne' :duration='1000'></countTo>
            </div>
            <countTo :startVal='lastData.decimaltwo' :endVal='nowData.decimaltwo' :duration='1000'></countTo>
          </span>
         </div>
       </div>
     </div>
     <div class="echart-box">
      <div id="trade-info" v-show="chartData.length"></div>
      <div class="no-data-box" v-show="!chartData.length">
        <img src="../../../../assets/images/bigData/no-data.png" />
        <div>当前无数据</div>
      </div>
     </div>
  </div>
</template>
<script>
import countTo from 'vue-count-to';
export default {
  data() {
    return {
      lastData: {
        num: 0,
        decimalOne: 0,
        decimaltwo: 0,
      },
      nowData: {
        num: 0,
        decimalOne: 0,
        decimaltwo: 0,
      },
      chartData: [],
      timer: '',
      chart: '',
      option: {},
      app: {
        currentIndex: -1,
      },
    };
  },
  props: {
    dataList: {
      type: Object,
      default: () => {
        return {
          orderNum: 0,
          rtsList: [],
          visitor: 0,
        };
      },
    },
  },
  watch: {
    dataList(newVal, oldVal) {
      if (this.chart) {
        this.chart.clear();
      }
      if (newVal.sales) {
        if (this.nowData.num) {
          this.lastData = this.nowData;
        }
        this.nowData = {
          num: Number(newVal.sales.split('.')[0]),
          decimalOne: Number(newVal.sales.split('.')[1][0]),
          decimaltwo: Number(newVal.sales.split('.')[1][1]),
        };
        this.chartData = newVal.rtsList.map((item) => {
          return {
            time: item.time,
            value: Number(item.sales),
          };
        });
      } else {
        this.chartData = [];
      }
      this.initEchart();
    },
  },
  components: {
    countTo,
  },
  mounted() {},
  beforeDestroy() {
    this.stop();
  },
  methods: {
    initEchart() {
      let myChart = this.$echarts.init(document.getElementById('trade-info')),
        chartData = [],
        xchartData = [],
        maxValue = 0,
        that = this,
        barchartData = [];
      this.chartData.map((item) => {
        if (item.value > maxValue) {
          maxValue = item.value;
        }
        barchartData.push(100);
        xchartData.push(item.time);
        chartData.push({
          value: item.value,
        });
      });
      let tooltipModel = `<div style="display:flex;align-items:center;justify-content:center;font-size:${that.fontfix(
        14
      )}px;width:${that.fontfix(73)}px;height: ${that.fontfix(
        29
      )}px;background: rgba(17, 34, 96, 0.72);
               border: ${that.fontfix(
                 1
               )}px solid #368EF7;border-radius: ${that.fontfix(
        19
      )}px 0px ${that.fontfix(19)}px 0px; box-shadow: 0px 0px 5px rgba(54, 142, 247, 1) inset">`;
      let option = {
        grid: {
          containLabel: true,
          top: this.fontfix(16),
          bottom: this.fontfix(16),
          left: 0,
          right: 0,
        },
        dataZoom: [
          {
            show: false,
            type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
            startValue: 0, // 从头开始。
            endValue: 6, // 一次性展示10个。
          },
        ],
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0)',
          axisPointer: {
            type: 'line',
            lineStyle: {
              color: '#A3BDFF',
              type: 'dashed',
            },
          },
          position: 'top',
          textStyle: {
            color: '#66ACFF',
            fontFamily: 'Microsoft YaHei',
            fontWeight: 'blod',
          },
          formatter(data) {
            return tooltipModel + data[1].value + '</div>';
          },
        },
        xAxis: [
          {
            type: 'category',
            data: xchartData,
            axisPointer: {
              type: 'line',
            },
            axisLabel: {
              color: '#9997F0',
              fontSize: this.fontfix(14),
              interval: 0,
            },
            axisLine: {
              lineStyle: {
                color: '#7877BA',
              },
            },
            axisTick: {
              show: false,
            },
          },
        ],
        yAxis: [
          {
            type: 'value',
            max: Math.ceil(maxValue + 100),
            interval: Math.ceil((maxValue + 100) / 5),
            axisLabel: {
              color: '#9997F0',
              fontSize: this.fontfix(14),
            },
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            splitLine: {
              show: false,
            },
          },
          {
            type: 'value',
            min: 0,
            max: 100,
            interval: 100,
            axisLabel: {
              formatter: ' ',
              color: '#9997F0',
            },
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            splitLine: {
              show: false,
            },
          },
        ],
        series: [
          {
            name: 'Precipitation',
            type: 'bar',
            barWidth: '96%',
            yAxisIndex: 1,
            itemStyle: {
              color: 'rgba(27, 33, 114, 0)',
            },
            data: barchartData,
            emphasis: {
              itemStyle: {
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(86, 74, 237, 0)',
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(86, 74, 237, 0.24)',
                  },
                  {
                    offset: 1,
                    color: 'rgba(86, 74, 237, 0)',
                  },
                ]),
                borderWidth: this.fontfix(2),
              },
            },
          },
          {
            type: 'line',
            lineStyle: {
              color: '#368EF7',
            },
            itemStyle: {
              // color: '#368EF7',
              // borderColor: '#fff',
              // borderWidth: this.fontfix(2),
              normal: {
                label: {
                  show: true,
                  color: '#368EF7'
                },
                color: '#368EF7',
                borderColor: '#fff',
                borderWidth: this.fontfix(2),
              }
            },
            areaStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(54, 142, 247, 0.11)',
                },
                {
                  offset: 1,
                  color: 'rgba(54, 142, 247, 0)',
                },
              ]),
            },
            symbol: 'circle',
            symbolSize: this.fontfix(8),
            showSymbol: true,
            showAllSymbol: true,
            data: chartData,
          },
        ],
      };
      this.option = option;
      myChart.setOption(this.option);
      this.chart = myChart;
      this.stop();
      this.autoMove();

      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
    /**
     *@desc 自动滚动
     */
    autoMove() {
      let app = {
        currentIndex: -1,
      };
      this.app = app;
      let count = 0;
      this.timer = setInterval(() => {
        count++;
        //高亮操作
        var data = this.option.series[0].data;
        this.chart.dispatchAction({
          type: 'downplay',
          seriesIndex: 0,
          dataIndex: app.currentIndex,
        });
        this.chart.dispatchAction({
          type: 'downplay',
          seriesIndex: 1,
          dataIndex: app.currentIndex,
        });
        app.currentIndex = (app.currentIndex + 1) % data.length;
        this.app = app;
        console.log(app.currentIndex);
        //end 高亮操作
        //轮播操作
        if (this.chartData.length > 7) {
          if (
            Number(this.option.dataZoom[0].endValue) ===
              this.chartData.length - 1 &&
            app.currentIndex == 0
          ) {
            this.option.dataZoom[0].endValue = 6;
            this.option.dataZoom[0].startValue = 0;
          } else if (
            Number(this.option.dataZoom[0].endValue) == app.currentIndex &&
            app.currentIndex != this.chartData.length - 1
          ) {
            this.option.dataZoom[0].endValue =
              this.option.dataZoom[0].endValue + 1;
            this.option.dataZoom[0].startValue =
              this.option.dataZoom[0].startValue + 1;
          }
        }
        //end 轮播操作
        this.chart.setOption(this.option);

        this.chart.dispatchAction({
          type: 'highlight',
          seriesIndex: 1,
          dataIndex: app.currentIndex,
        });
        this.chart.dispatchAction({
          type: 'showTip',
          seriesIndex: 1,
          dataIndex: app.currentIndex,
        });
        if (count == this.chartData.length) {
          setTimeout(() => {
            this.stop();
            this.$emit('getData');
            this.chart.dispatchAction({
              type: 'downplay',
              seriesIndex: 0,
              dataIndex: app.currentIndex,
            });
          }, 800);
        }
      }, 2000);
    },
    /**
     *@desc 停止滚动
     **/
    stop() {
      clearInterval(this.timer);
    },
    /**
     *@desc 继续滚动
     **/
    goMove() {
      this.autoMove();
    },
  },
};
</script>