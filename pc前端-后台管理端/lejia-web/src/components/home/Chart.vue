<!-- 
@name: Chart.vue 
@description: 图表分析 
@author: crj
@date: 2020/08/19
-->


<template>
  <div class="container" :style="{width:width}">
    <el-container>
      <el-aside width="15vh">
        <div class="aside-title">{{title}}</div>
        <div class="second-title">
          <i class="iconfont iconbingtu"></i>过去7日
        </div>
      </el-aside>
      <el-main v-show="chartData.length">
        <div :id="this.chartId" class="echart-box"></div>
      </el-main>
      <el-main v-show="!chartData.length">
        <div class="empty-state">
          <i class="iconfont iconxingzhuangjiehe empty-state-icon"> </i>
          <div class="empty-state-tit">暂无该数据</div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
  export default {
    props: {
      title: String, //标题
      echartType: String, //图表类型
      chartId: String, //图表id
      chartData: Array, //图表数据
      width: String,
      loading: { //加载变量
        value: true,
        type: Boolean
      }
    },
    data() {
      return {};
    },
    computed: {
      chartobject() {
        const {
          loading,
          chartData
        } = this;
        return {
          loading,
          chartData
        };
      }
    },
    watch: { //监听加载和数据的变化
      chartobject(newVal, oldVal) {
        if (newVal.chartData.length && !newVal.loading) {
          this.chartInit();
        }
      }
    },
    mounted() {
      // this.chartInit();
    },
    methods: {
      /**
       * 图表初始化
       */
      chartInit() {
        let myChart = this.$echarts.init(document.getElementById(this.chartId)),
          _this = this,
          option;
        this.$nextTick(() => {
          myChart.resize();
        })
        this.echartType == "bar" ?
          (option = {
            tooltip: {
              trigger: "axis"
            },
            xAxis: {
              type: "category",
              data: this.chartData.map(item => {
                return item.farmer;
              })
            },
            yAxis: {
              type: "value"
            },
            series: [{
              data: this.chartData.map(item => {
                return item.sales;
              }),
              type: "bar",
              barWidth: 30,
              itemStyle: {
                normal: {
                  color: function (params) {
                    var colorList = [
                      "#C33531",
                      "#EFE42A",
                      "#64BD3D",
                      "#EE9201",
                      "#29AAE3",
                      "#B74AE5",
                      "#0AAF9F",
                      "#E89589",
                      "#16A085",
                      "#4A235A"
                    ];
                    return colorList[params.dataIndex];
                  },
                  label: {
                    show: true,
                    position: "top"
                  }
                }
              }
            }]
          }) :
          (option = {
            tooltip: {
              trigger: "item",
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
              orient: "vertical",
              right: "40",
              data: this.chartData.map(item => {
                return item.name;
              })
            },
            series: [{
              name: "访问来源",
              radius: "80%",
              center: ["40%", "50%"],
              type: "pie",
              data: this.chartData.map(item => {
                return {
                  value: item.Sales,
                  name: item.name
                };
              }),
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: "rgba(0, 0, 0, 0.5)"
                }
              }
            }]
          });
        myChart.setOption(option);
        window.addEventListener("resize", function () {
          myChart.resize();
        });

      }
    }
  };
</script>

<style lang="less" scoped>
  .empty-state {
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;

    .empty-state-icon {
      font-size: 40px;

    }

    .empty-state-tit {
      padding-top: 20px;
    }
  }

  // /deep/canvas {
  //   height: 360px !important;

  // }
  /deep/ .el-container {
    padding: 10px 0;
    background: #fff;
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.2);
    border-radius: 6px;
  }

  /deep/ .el-aside {
    padding: 0px 20px;
  }

  /deep/ .el-main {
    padding: 20px 0px;
  }

  // /deep/ .el-aside {
  //   padding: 0px 20px;
  // }
  .container {
    border-radius: 5px;
    font-size: 16px;
    background: #ebf2f8;
    margin: 0px 20px;
    padding: 10px;

    .second-title {
      padding-top: 10px;
      font-size: 14px;
      color: #909399;
    }

    .echart-box {
      width: 100%;
      height: 300px;
    }
  }
</style>