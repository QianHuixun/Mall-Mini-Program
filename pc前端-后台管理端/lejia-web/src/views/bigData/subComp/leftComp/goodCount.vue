<!-- 
@name: goodPrice.vue 
@description: 大数据-左边-销量商品笔数排行榜 TOP20
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
.good-count {
  line-height: normal;
  .title-box {
    margin-left: (16 / @baseWidth) * 100vw;
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
    #good-count {
      .heightfix(237);
      width: 100%;
    }
  }
  .no-data-box {
    width: 100%;
    .heightfix(237);
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
<template lang="html">
  <div class="good-count">
     <div class="title-box">
       <img src="../../../../assets/images/bigData/sale_icon.png"></img>
       <span>销量商品笔数排行榜 TOP20</span>
     </div>
     <div class="echart-box" v-if="chartData.length">
      <div id="good-count" ></div>
     </div>
      <div class="no-data-box" v-else>
        <img src="../../../../assets/images/bigData/no-data.png" />
        <div>当前无数据</div>
      </div>
  </div>
</template>
<script>
import { circleOne, circleTwo } from '../../../../assets/images/bigData/image';
export default {
  data() {
    return {
      chartData: [],
      option: {},
      chart: '',
      timer: '',
      app: {
        currentIndex: -1,
      },
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
      if (this.chart) {
        this.chart.clear();
      }
      if (newVal && newVal.length) {
        this.chartData = newVal.map((item) => {
          return {
            name: item.name,
            value: item.num,
          };
        });
        if (this.chartData.length == 1) {
          this.chartData[0].symbol = 'image://' + circleTwo;
          this.chartData[0].symbolSize = this.fontfix(32);
        }
        setTimeout(() => {
          this.initEchart();
        }, 200);
      } else {
        this.stop();
        this.chartData = [];
      }
    },
  },
  beforeDestroy() {
    this.stop();
  },
  methods: {
    initEchart() {
      let myChart = this.$echarts.init(document.getElementById('good-count')),
        _this = this,
        chartData = [],
        xchartData = [],
        barchartData = [],
        maxValue = 0;
      this.chartData.map((item) => {
        if (item.value > maxValue) {
          maxValue = item.value;
        }
        barchartData.push(100);
        xchartData.push(item.name);
        chartData.push({
          value: item.value,
        });
      });
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
            endValue: 9, // 一次性展示10个。
          },
        ],
        xAxis: [
          {
            type: 'category',
            data: xchartData,
            axisPointer: {
              type: 'shadow',
            },
            axisLabel: {
              color: '#9997F0',
              fontSize: this.fontfix(14),
              interval: 0,
              formatter: (value, index) => {
                if (value.length > 5) {
                  return (
                    value.substring(0, 3) + '\n' + value.substring(3, 5) + '...'
                  );
                } else if (value.length > 3) {
                  return (
                    value.substring(0, 3) +
                    '\n' +
                    value.substring(3, value.length)
                  );
                } else {
                  return value;
                }
              },
            },
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
          },
        ],
        yAxis: [
          {
            type: 'value',
            min: 0,
            max: Math.ceil(maxValue + maxValue / 2),
            interval: Math.ceil((maxValue + maxValue / 2) / 5),
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
            type: 'bar',
            barWidth: '96%',
            yAxisIndex: 1,
            itemStyle: {
              color: 'rgba(27, 33, 114, 0.5)',
            },
            data: barchartData,
            emphasis: {
              itemStyle: {
                color: '#232781',
              },
            },
          },
          {
            type: 'line',
            yAxisIndex: 0,
            lineStyle: {
              color: '#51699A',
            },
            symbol: 'image://' + circleOne,
            symbolSize: this.fontfix(22),
            data: chartData,
            itemStyle: {
              normal: {
                label: {
                  show: false,
                },
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: this.fontfix(14),
                  fontWeight: 'blod',
                  color: '#66ACFF',
                },
              },
            },
          },
        ],
      };
      this.option = option;
      myChart.setOption(this.option);
      this.chart = myChart;
      this.stop();
      if (this.chartData.length != 1) {
        this.autoMove();
      } else {
        this.chart.dispatchAction({
          type: 'highlight',
          seriesIndex: 0,
          dataIndex: 0,
        });
        this.timer = setInterval(() => {
          this.$emit('getData');
          this.stop();
        }, 15000);
      }

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
        var data = this.option.series[1].data;
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
        this.chart.dispatchAction({
          type: 'highlight',
          seriesIndex: 0,
          dataIndex: app.currentIndex,
        });

        if (app.currentIndex == 0 && data[data.length - 1].symbol) {
          data[app.currentIndex].symbol = 'image://' + circleTwo;
          data[app.currentIndex].symbolSize = this.fontfix(32);
          data[data.length - 1] = { value: data[data.length - 1].value };
        } else if (app.currentIndex == 0 && !data[data.length - 1].symbol) {
          data[app.currentIndex].symbol = 'image://' + circleTwo;
          data[app.currentIndex].symbolSize = this.fontfix(32);
        } else {
          data[app.currentIndex].symbol = 'image://' + circleTwo;
          data[app.currentIndex].symbolSize = this.fontfix(32);
          data[app.currentIndex - 1] = {
            value: data[app.currentIndex - 1].value,
          };
        }
        this.option.series[1].data = data;
        //end 高亮操作

        //轮播操作

        if (this.chartData.length > 10) {
          if (
            Number(this.option.dataZoom[0].endValue) ===
              this.chartData.length - 1 &&
            app.currentIndex == 0
          ) {
            this.option.dataZoom[0].endValue = 9;
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
        this.chart.setOption(this.option);
        this.chart.dispatchAction({
          type: 'highlight',
          seriesIndex: 1,
          dataIndex: app.currentIndex,
        });
        if (count == this.chartData.length) {
          setTimeout(() => {
            this.stop();
            this.chart.dispatchAction({
              type: 'downplay',
              seriesIndex: 0,
              dataIndex: app.currentIndex,
            });
            this.$emit('getData');
          }, 800);
        }
        //end 轮播操作
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