<!-- 
@name: classifyAmt.vue 
@description: 大数据-左边-销量分类金额排行榜 TOP10
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
.classify-amt {
  padding-left: (16 / @baseWidth) * 100vw;
  line-height: normal;
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
    display: flex;
    justify-content: space-between;
    padding: (20 / @baseHeight) * 100vh 0;
    .echart-container {
      display: flex;
      justify-content: space-between;
    }
    #classify-amt {
      .heightfix(200);
      .widthfix(300);
    }
    .item-box {
      display: flex;
      flex-wrap: wrap-reverse;
      flex-direction: column-reverse;
      justify-content: flex-end;
      align-content: flex-end;
      .widthfix(200);
      .heightfix(200);

      .item {
        .fontfix(14);
        .lhfix(14);
        .item-name {
          font-weight: 400;
          color: #acbfef;
          margin-bottom: (4 / @baseHeight) * 100vh;
          max-width: (98 / @baseWidth) * 100vw;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          span {
            display: inline-block;
            margin-right: (6 / @baseWidth) * 100vw;
            .widthfix(8);
            .heightfix(8);
            border-radius: 50%;
          }
        }
        .item-value {
          margin-left: (14 / @baseWidth) * 100vw;
          .fontfix(14);
          font-weight: bold;
          color: #48c9ff;
        }
        & + .item {
          margin-bottom: (8 / @baseHeight) * 100vh;
        }
        &:nth-child(6) {
          margin-top: 0 !important;
        }
      }
    }
    .no-data-box {
      width: 100%;
      .heightfix(200);
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
  <div class="classify-amt">
     <div class="title-box">
       <img src="../../../../assets/images/bigData/sale_icon.png"></img>
       <span>销量分类金额排行榜 TOP10</span>
     </div>
     <div class="echart-box">
      <div class="echart-container" v-show="chartData.length">
        <div id="classify-amt"></div>
          <div class="item-box">
            <div class="item" v-for="(item,index) in chartData" :key="index">
              <div class="item-name">
                <span :style="{'background':colorList[index]}"></span>{{item.name}}
              </div>
            <div class="item-value">{{item.value}}</div>
          </div>
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
      chartData: [],
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
      if (newVal && newVal.length) {
        this.chartData = newVal.map((item) => {
          return {
            name: item.typeName,
            value: item.sales,
            proportion: item.proportion,
          };
        });
      } else {
        this.chartData = [];
      }
      this.initEchart();
    },
  },
  methods: {
    initEchart() {
      // 解决南丁格尔图极差较大半径问题
      var pieData = this.chartData;
      var showData = [];
      var sum = 0,
        max = 0;
      pieData.forEach((item) => {
        let value = parseInt(item.value);
        sum += value;
        if (value >= max) max = value;
      });

      // 放大规则
      var number = Math.round(max);
      showData = pieData.map((item) => {
        return {
          value: number + parseInt(item.value),
          name: item.name,
          proportion: item.proportion,
        };
      });
      let myChart = this.$echarts.init(document.getElementById('classify-amt')),
        _this = this,
        option = {
          color: this.colorList,
          series: [
            {
              name: 'classifyAmt',
              type: 'pie',
              radius: ['20%', '80%'],
              center: ['50%', '50%'],
              roseType: 'area',
              itemStyle: {
                borderRadius: 8,
              },
              label: {
                //引导字的颜色
                color: '#ACBFEF',
                fontSize: this.fontfix(14),
                formatter: (data) => {
                  return data.data.proportion;
                },
              },
              labelLine: {
                //引导线的颜色
                show: true,
                lineStyle: {
                  color: '#3149BA',
                },
              },
              data: showData,
            },
          ],
        };

      myChart.setOption(option);
      window.addEventListener('resize', function () {
        myChart.resize();
      });
    },
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
  },
};
</script>