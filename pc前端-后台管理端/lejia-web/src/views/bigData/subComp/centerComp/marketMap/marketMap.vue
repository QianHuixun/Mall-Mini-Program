<!-- 
@name: tradeInfo.vue 
@description: 大数据-中间-市场地图
@author: 池仁杰
@date: 2022/01/17
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

.market-map {
  padding-top: (30 / @baseHeight) * 100vh;
  padding-bottom: (50 / @baseHeight) * 100vh;
  position: relative;
  #market-map {
    .widthfix(786);
    .heightfix(618);
  }
  .markert-count {
    .widthfix(200);
    .heightfix(74);
    position: absolute;
    top: (28 / @baseHeight) * 100vh;;
    background: url('../../../../../assets/images/bigData/makert_count.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    padding-left: (4 / @baseWidth) * 100vw;
  }
}
.markert-count {
  .title-box {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    color: #ACBFEF;
    img {
      .heightfix(42);
      .widthfix(42);
    }
  }
  .count-box {
    padding-left: (12 / @baseWidth) * 100vw;
    .count {
      .fontfix(24);
      font-weight: bold;
      color: #FFFFFF;
      margin-right: (8 / @baseWidth) * 100vw;;
    }
    .unit {
      .fontfix(14);
      color: #ACBFEF;
    }
  }
}
</style>
<template lang="html">
  <div class="market-map">
    <div id="market-map">
    </div>
    <div class="markert-count">
      <div class="title-box">
        <img src="../../../../../assets/images/bigData/makert_count_icon.png" alt="">
        <span>当前市场数</span>
      </div>
      <div class="count-box">
        <span class="count">{{dataList.length}}</span>
        <span class="unit">个</span>
      </div>
    </div>
  </div>
</template>
<script>
import {
  mapBackground,
  mapIcon,
  mapActiveIcon,
} from '../../../../../assets/images/bigData/image';
export default {
  data() {
    return {
      timer: '',
      chart: '',
      option: {},
      chartData: [],
      mapData: require('./mapJson/yiwu.json'),
      map: '', //高德地图
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
    dataList: {
      deep: true,
      handler(newVal, oldVal) {
        console.log('mapDataList', newVal);
        let chartData = [];
        if (this.chart) {
          this.chart.clear();
        }

        if (newVal && newVal.length) {
          newVal.map((item, index) => {
            chartData.push({
              name: item.marketName,
              value: [item.longitude, item.latitude],
            });
          });
          this.chartData = chartData;
        }

        this.initEchart();
      },
    },
  },
  mounted() {
    this.$echarts.registerMap('yiwu', this.mapData);
  },
  beforeDestroy() {
    this.stop();
  },
  methods: {
    /**
     * @desc 初始化地图
     */
    initEchart() {
      //地图背景贴图
      let mapBack = document.createElement('img');
      mapBack.style.height =
        mapBack.height =
        mapBack.width =
        mapBack.style.width =
          this.fontfix(8) + 'px';
      mapBack.src = mapBackground;
      let myChart = this.$echarts.init(document.getElementById('market-map')),
        that = this,
        option = {
          tooltip: {
            show: false,
          },
          geo: [
            //第一层样式
            {
              //外发光
              show: true,
              map: 'yiwu',
              label: {
                normal: {
                  show: false,
                },
                emphasis: {
                  show: false,
                },
              },
              z: 5,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false, //地图设置不可拖拽，固定的
              itemStyle: {
                normal: {
                  areaColor: 'rgba(39, 102, 200, 0.23)',
                  borderWidth: this.fontfix(2),
                  borderColor: '#349ED8',
                  shadowColor: 'rgba(10, 82, 202, 0.76)',
                  shadowBlur: 18,
                },
              },
            },
            {
              //内发光
              show: true,
              map: 'yiwu',
              label: {
                normal: {
                  show: false,
                },
                emphasis: {
                  show: false,
                },
              },
              z: 5,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false, //地图设置不可拖拽，固定的
              itemStyle: {
                normal: {
                  areaColor: 'rgba(39, 102, 200, 0)',
                  borderWidth: this.fontfix(2),
                  borderColor: '#349ED8',
                  shadowColor: 'rgba(23, 109, 251, 1)',
                  shadowBlur: 18,
                },
              },
            },
            //end 第一层样式
            //第二三 四层
            {
              show: true,
              map: 'yiwu',
              aspectScale: 0.75,
              layoutCenter: ['50.3%', '51.5%'],
              layoutSize: '86%',
              silent: true,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false,
              z: 4,
              itemStyle: {
                normal: {
                  areaColor: 'rgba(54, 128, 211, 0)',
                  shadowColor: 'rgba(14, 15, 112, 0.43)',
                  shadowBlur: 22,
                  shadowOffsetX: 9,
                  shadowOffsetY: 9,
                  borderColor: 'rgba(122, 179, 255, 0.8)',
                  borderWidth: this.fontfix(1),
                },
                emphasis: {},
              },
            },
            {
              show: true,
              map: 'yiwu',
              aspectScale: 0.75,
              layoutCenter: ['50.5%', '52%'],
              layoutSize: '86%',
              silent: true,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false,
              z: 3,
              itemStyle: {
                normal: {
                  areaColor: 'rgba(54, 128, 211, 0)',
                  shadowColor: 'rgba(14, 15, 112, 0.43)',
                  shadowBlur: 22,
                  shadowOffsetX: 9,
                  shadowOffsetY: 9,
                  borderColor: 'rgba(122, 179, 255, 0.8)',
                  borderWidth: this.fontfix(1),
                },
                emphasis: {},
              },
            },
            {
              show: true,
              map: 'yiwu',
              aspectScale: 0.75,
              layoutCenter: ['50.8%', '52.5%'],
              layoutSize: '86%',
              silent: true,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false,
              z: 2,
              itemStyle: {
                normal: {
                  areaColor: 'rgba(54, 128, 211, 0)',
                  shadowColor: 'rgba(14, 15, 112, 0.43)',
                  shadowBlur: 22,
                  shadowOffsetX: 9,
                  shadowOffsetY: 9,
                  borderColor: 'rgba(122, 179, 255, 0.8)',
                  borderWidth: this.fontfix(1),
                },
                emphasis: {},
              },
            },
            // end 第二三 四层
            //第五层
            {
              show: true,
              map: 'yiwu',
              aspectScale: 0.75,
              layoutCenter: ['50.8%', '52.7%'],
              layoutSize: '86%',
              silent: true,
              aspectScale: 1.2,
              zoom: 1.16,
              roam: false,
              z: 1,
              itemStyle: {
                normal: {
                  areaColor: 'rgba(39, 102, 200, 0.8)',
                  shadowColor: 'rgba(10, 82, 202, 0.76)',
                  shadowBlur: 18,
                  borderColor: 'rgba(0, 26, 137, 0.8)',
                  borderWidth: this.fontfix(2),
                },
                emphasis: {},
              },
            },
            {
              show: true,
              map: 'yiwu',
              aspectScale: 0.75,
              layoutCenter: ['50.8%', '52.7%'],
              layoutSize: '86%',
              silent: true,
              aspectScale: 1.2,
              zoom: 1.16,
              z: 1,
              roam: false,
              itemStyle: {
                normal: {
                  areaColor: 'rgba(39, 102, 200, 0.8)',
                  shadowColor: '7ab3ff',
                  shadowBlur: 10,
                  borderColor: 'rgba(0, 26, 137, 0.8)',
                  borderWidth: this.fontfix(2),
                },
                emphasis: {},
              },
            },
          ],
          series: [
            {
              type: 'map',
              map: 'yiwu',
              zoom: 1.16,
              roam: false,
              aspectScale: 1.2,
              z: 6,
              showLegendSymbol: false, // 存在legend时显示
              label: {
                normal: {
                  show: true,
                  position: ['100%', '100%'],
                  color: '#ACBFEF',
                  fontSize: this.fontfix(14),
                  fontFamily: 'Microsoft YaHei',
                  formatter(params) {
                    return params.name;
                  },
                },
                emphasis: {
                  show: true,
                  position: ['100%', '100%'],
                  color: '#ACBFEF',
                  fontSize: this.fontfix(14),
                  fontFamily: 'Microsoft YaHei',
                  formatter(params) {
                    return params.name;
                  },
                },
              },
              itemStyle: {
                normal: {
                  areaColor: {
                    image: mapBack,
                    repeat: 'repeat',
                  },
                  borderColor: '#349ED8',
                  borderWidth: this.fontfix(2),
                  shadowColor: 'rgba(10, 82, 202, 0.76)',
                  shadowBlur: 300,
                },
                emphasis: {
                  areaColor: {
                    image: mapBack,
                    repeat: 'repeat',
                  },
                  borderColor: '#349ED8',
                  borderWidth: this.fontfix(2),
                  // shadowColor: 'rgba(10, 82, 202, 1)',
                  // shadowBlur: 6,
                  // shadowOffsetX: 0,
                  // shadowOffsetY: 1,
                },
              },
            },
            {
              type: 'scatter',
              coordinateSystem: 'geo',
              data: this.chartData,
              symbolSize: function (val) {
                return val[2] / 20;
              },
              z: 8,
              label: {
                normal: {
                  show: false,
                },
                emphasis: {
                  show: false,
                },
              },
              symbol: function (value, params) {
                return params.data.img ? params.data.img : 'image://' + mapIcon;
              },
              symbolSize: this.fontfix(26),
              symbolOffset: [0, 0],
            },
            {
              type: 'lines',
              z: 7,
              coordinateSystem: 'geo',
              opacity: 1,
              label: {
                show: true,
                position: 'end',
                formatter: function (params) {
                  //文本提示框
                  let title = '{title|' + params.name + '}\n';
                  if (params.name.length > 9) {
                    title = '';
                    for (
                      let i = 0;
                      i <= Math.ceil(params.name.length / 9) - 1;
                      i++
                    ) {
                      if (i == Math.ceil(params.name.length / 9) - 1) {
                        title =
                          title +
                          `{title3|${params.name.substring(
                            i * 9 - (9 - (params.name.length % 9))
                          )}}\n`;
                      } else {
                        title =
                          title +
                          '{title2|' +
                          params.name.substring(i * 9, (i + 1) * 9) +
                          '}' +
                          '\n';
                      }
                    }
                  }
                  return `${title}{value1| 销量：}{num1| ${params.data.num}}\n{value2| 金额：}{num2| ${params.data.sales}}`;
                },
                backgroundColor: 'rgba(0,0,0,0)',
                borderWidth: 0,
                rich: {
                  //标题样式
                  title: {
                    align: 'left',
                    lineHeight: this.fontfix(32),
                    height: this.fontfix(32),
                    fontSize: this.widthfix(16),
                    color: '#fff',
                    backgroundColor: '#0086FF',
                    padding: [0, 0, 0, this.widthfix(12)],
                    width: this.widthfix(155),
                  },
                  title2: {
                    align: 'left',
                    lineHeight: this.fontfix(32),
                    height: this.fontfix(32),
                    fontSize: this.widthfix(16),
                    color: '#fff',
                    backgroundColor: '#0086FF',
                    padding: [0, 0, 0, this.widthfix(12)],
                    width: this.widthfix(155),
                  },
                  title3: {
                    align: 'left',
                    lineHeight: this.fontfix(16),
                    height: this.fontfix(24),
                    fontSize: this.widthfix(16),
                    color: '#fff',
                    backgroundColor: '#0086FF',
                    padding: [this.fontfix(8), 0, 0, this.widthfix(12)],
                    width: this.widthfix(155),
                  },
                  value1: {
                    //内容样式
                    align: 'left',
                    height: this.fontfix(21),
                    width: this.widthfix(34),
                    padding: [0, 0, this.fontfix(11), this.widthfix(11)],
                    color: '#fff',
                    fontSize: this.widthfix(14),
                    fontFamily: 'Microsoft YaHei',
                    borderWidth: 0,
                    backgroundColor: '#0a3987',
                  },
                  value2: {
                    //内容样式
                    align: 'left',
                    height: this.fontfix(32),
                    width: this.widthfix(34),
                    lineHeight: this.fontfix(32),
                    padding: [0, 0, 0, this.widthfix(11)],
                    color: '#fff',
                    fontSize: this.widthfix(14),
                    fontFamily: 'Microsoft YaHei',
                    borderWidth: 0,
                    backgroundColor: '#0a3987',
                  },
                  num1: {
                    align: 'left',
                    width: this.widthfix(105),
                    height: this.fontfix(21),
                    fontSize: this.widthfix(16),
                    fontFamily: 'Microsoft YaHei',
                    backgroundColor: '#0a3987',
                    color: '#FFCE37',
                    fontWeight: 'blod',
                    borderWidth: 0,
                    padding: [0, 0, this.fontfix(11), this.widthfix(17)],
                  },
                  num2: {
                    align: 'left',
                    width: this.widthfix(105),
                    height: this.fontfix(32),
                    lineHeight: this.fontfix(32),
                    fontSize: this.widthfix(16),
                    fontFamily: 'Microsoft YaHei',
                    backgroundColor: '#0a3987',
                    color: '#FFCE37',
                    fontWeight: 'blod',
                    borderWidth: 0,
                    padding: [0, 0, 0, this.widthfix(17)],
                  },
                },
              },
              lineStyle: {
                normal: {
                  //视觉引导线属性
                  type: 'solid',
                  width: this.fontfix(1),
                  opacity: 1,
                  color: '#FFCE37', //引导线颜色
                },
              },
              data: [],
            },
          ],
        };
      myChart.setOption(option);
      this.chart = myChart;
      this.option = option;
      this.stop();
      if (this.dataList.length) this.autoMove();
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
    widthfix(val) {
      let clientWidth = document.documentElement.clientWidth;
      return val * (clientWidth / 1920);
    },
    /**
     *@desc 自动轮播
     */
    autoMove() {
      let count = 0;
      let dataList = this.dataList;
      if (dataList.length == 1) {
        setTimeout(() => {
          this.option.series[2].data = [
            {
              name: dataList[count].marketName,
              coords: [
                [dataList[count].longitude, dataList[count].latitude],
                [120.2, 29.12],
              ],
              sales: dataList[count].sales,
              num: dataList[count].num,
            },
          ];
          this.option.series[1].data[count].img = 'image://' + mapActiveIcon;
          this.chart.setOption(this.option);
        }, 2000);
        setTimeout(() => {
          this.$emit('getData');
        }, 15000);
      } else {
        this.timer = setInterval(() => {
          if (count + 1 > this.dataList.length) {
            count = 0;
            this.$emit('getData');
          }
          this.option.series[2].data = [
            {
              name: dataList[count].marketName,
              coords: [
                [dataList[count].longitude, dataList[count].latitude],
                [120.2, 29.12],
              ],
              sales: dataList[count].sales,
              num: dataList[count].num,
            },
          ];

          if (
            count == 0 &&
            this.option.series[1].data[this.dataList.length - 1].img
          ) {
            this.option.series[1].data[this.dataList.length - 1].img = '';
          } else if (count != 0 && this.option.series[1].data[count - 1].img) {
            this.option.series[1].data[count - 1].img = '';
          }
          this.option.series[1].data[count].img = 'image://' + mapActiveIcon;
          count++;
          this.chart.setOption(this.option);
        }, 5000);
      }
    },
    /**
     *@desc 停止滚动
     **/
    stop() {
      if (this.timer) clearInterval(this.timer);
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