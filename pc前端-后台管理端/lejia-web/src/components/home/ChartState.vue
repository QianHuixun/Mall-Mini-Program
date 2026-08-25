<!-- 
@name: ChartState.vue 
@description: 图表以及概况
@author: crj
@date: 2020/08/19
-->


<template>
  <div class="container">
    <el-container>
      <el-aside width="30vh">
        <div class="aside-box">
          <div class="aside-title">销售概况</div>
          <div class="aside-item" v-for="(item,index) in dataList" :key="index">
            <div class="aside-item-title">{{item.title}}</div>
            <div class="aside-num" :style="{color:(item.type=='money'?'#409EFF':'#67C23A')}">
              <span v-if="item.type=='money'">¥</span>
              {{item.num}}
            </div>
            <div class="aside-ynum">昨日：{{item.yNum}}</div>
            <div class="aside-trend">
              {{item.trend==-1?'下降':'上升'}}：{{item.percentage}}
              <i
                v-if="item.trend!=0"
                class="iconfont"
                :class="item.trend==-1?'iconxiajiang':'iconshangsheng'"
                :style="{ color: (item.trend==-1?'#67C23A':'#F56C6C') }"
              />
            </div>
          </div>
        </div>
      </el-aside>
      <el-main id="echart-container">
        <div id="echart-box" class="echart-box"></div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  props: {
    realData: Object,
    chartData:Array
  },
  data() {
    return {
      dataList: [
        // {
        //   title: "今日销售额",
        //   type: "money",
        //   num: "450.00",
        //   yNum: "¥467.10",
        //   trend: false,
        //   percentage: "3.66%"
        // },
        // {
        //   title: "今日订单数",
        //   type: "order",
        //   num: "18",
        //   yNum: "18笔",
        //   trend: true,
        //   percentage: "3.66%"
        // }
      ],
      // chartData: []
    };
  },
  watch: {
    realData(newVal, oldVal) {
      if (newVal) {
        this.dataList = [
          {
            title: "今日销售额",
            type: "money",
            num: newVal.tSales,
            yNum: newVal.ySales,
            trend: newVal.signumSales,
            percentage: newVal.percentageSales
          },
          {
            title: "今日订单数",
            type: "order",
            num: newVal.tCount,
            yNum: newVal.yCount,
            trend: newVal.signumCount,
            percentage: newVal.percentageCount
          }
        ];
      }
    },
    chartData(newVal,oldVal){
      if (newVal){
       
        console.log(newVal)
        this.chartInit()
      }
      
    }
  },
  mounted() {
    // this.chartInit();
    // this.$nextTick(function() {
    //   console.log(111)
      // this.getChartData();
    // });
  },

  methods: {
    chartInit() {
      let myChart = this.$echarts.init(document.getElementById("echart-box")),
        _this = this;
      window.addEventListener("resize", function() {
        myChart.resize();
      });
      // this.intChart();
      let option = {
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: ["昨日销售额", "销售单数"],
          bottom: "10"
        },
        xAxis: {
          type: "category",
          data: this.chartData.map(item => {
            var date = new Date(parseInt(item.timeStamp));
            var hour = date.getHours();
            var minute = date.getMinutes();
            if (hour < 10) {
              hour = "0" + hour;
            }
            if (minute < 10) {
              minute = "0" + minute;
            }
            return `${hour}:${minute}~${parseInt(hour) + 1}:${minute}`;
          }),

          axisTick: {
            alignWithLabel: true
          },
          splitLine: {
            show: false
          }
        },
        yAxis: [
          {
            type: "value",
            name: "昨日销售额",
            min: 0,
            max: 500,
            interval: 100,
            axisLabel: {
              formatter: "{value}"
            }
          },
          {
            type: "value",
            name: "销售单数",
            min: 0,
            max: 100,
            interval: 20,
            axisLabel: {
              formatter: "{value} 笔"
            }
          }
        ],
        series: [
          {
            name: "昨日销售额",
            type: "bar",
            data: this.chartData.map(item => {
              return item.value;
            }),
            barWidth: 20,
            color: "#409EFF"
          },
          {
            name: "销售单数",
            type: "bar",
            yAxisIndex: "1",
            data: this.chartData.map(item => {
              return item.count;
            }),
            barWidth: 20,
            color: "#67C23A"
          }
        ]
      };
      myChart.setOption(option);
    },
    /**
     * 获取柱状图销售额数据
     */

    getChartData() {
      axios
        .post(api.index.querySales, "", {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.chartData = response;
          this.chartInit();
        });
    },
    intChart() {
      let myChart = document.getElementById("echart-box");
      console.log(myChart);
      myChart.style.width = document.body.offsetWidth - 500;
      console.log(document.body.offsetWidth - 500);
    }
  }
};
</script>

<style lang="less" scoped>
/deep/canvas {
  height: 290px !important;
}
/deep/ .el-container {
  padding: 10px 0;
  background: #fff;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.2);
  border-radius: 6px;
}
/deep/ .el-main {
  padding: 0px;
}
/deep/ .el-aside {
  padding: 0px 20px;
}
.container {
  border-radius: 5px;
  font-size: 16px;
  background: #f2f2f2;
  margin: 20px 20px;
  padding: 10px;
  .aside-box {
    .aside-title {
      color: #606266;
      padding-bottom: 20px;
    }
    .aside-item {
      padding-bottom: 20px;
      padding-left: 20px;
      .aside-item-title {
        color: #909399;
        padding-bottom: 6px;
      }
      .aside-num {
        font-size: 20px;
        padding-bottom: 6px;
      }
      .aside-ynum,
      .aside-trend {
        font-size: 14px;
        padding-bottom: 3px;
        color: #c0c4cc;
      }
    }
  }

  .echart-box {
    width: 100%;
    // width: 1400px;
    height: 280px;
    overflow: hidden;
  }
}
</style>