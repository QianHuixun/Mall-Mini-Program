<!-- 
@name: rangeAmt.vue 
@description: 大数据-右边-销售额 · 元-金额滚动组件
@author: 池仁杰
@date: 2022/01/12
-->
<template>
  <div class="item-time-box">
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion}">
        <div class="time-list" :style="{top:getScrollTop.billion+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion&&!amtData.tenMillion}">
        <div class="time-list" :style="{top:getScrollTop.tenMillion+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box"  :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion}">
        <div class="time-list" :style="{top:getScrollTop.million+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <span :class="amtData.billion==0&&amtData.tenMillion==0&&amtData.million==0? 'split_warn': 'split_normal' ">,</span>
    <div class="time-cell">
      <div class="time-cell-box"  :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion&&amtData.oneHundredThousand==0}">
        <div class="time-list" :style="{top:getScrollTop.oneHundredThousand+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box"  :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion
      &&amtData.oneHundredThousand==0&&amtData.tenThousand==0}">
        <div class="time-list" :style="{top:getScrollTop.tenThousand+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion
      &&amtData.oneHundredThousand==0&&amtData.tenThousand==0&&amtData.thousand==0}" >
        <div class="time-list" :style="{top:getScrollTop.thousand+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <span :class="amtData.billion==0&&amtData.tenMillion==0&&amtData.million==0
      &&amtData.oneHundredThousand==0&&amtData.tenThousand==0&&amtData.thousand==0 ? 'split_warn': 'split_normal' ">,</span>
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion
      &&amtData.oneHundredThousand==0&&amtData.tenThousand==0&&amtData.thousand==0&&amtData.hundred==0}" >
        <div class="time-list" :class="{'clear-transtion':downReset.hour}" :style="{top:getScrollTop.hundred+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion
      &&amtData.oneHundredThousand==0&&amtData.tenThousand==0&&amtData.thousand==0&&amtData.hundred==0&&amtData.ten==0}" >
        <div class="time-list" :class="{'clear-transtion':downReset.hour}" :style="{top:getScrollTop.ten+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box" :class="{'nothing':!amtData.billion&&!amtData.million&&!amtData.tenMillion
        &&amtData.oneHundredThousand==0&&amtData.tenThousand==0&&amtData.thousand==0&&amtData.hundred==0
        &&amtData.ten==0&&amtData.one==0}">
        <div class="time-list" :class="{'clear-transtion':downReset.hour}" :style="{top:getScrollTop.one+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <span class="split_warn">.</span>
    <div class="time-cell">
      <div class="time-cell-box decimal"  >
        <div class="time-list" :style="{top:getScrollTop.decimalOne+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
    <div class="time-cell">
      <div class="time-cell-box  decimal" >
        <div class="time-list" :style="{top:getScrollTop.decimalTwo+'px'}">
          <div>9</div>
          <div v-for="(item,index) of 10" :key="index">
            {{index}}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    amt: {
      // 剩余时间，单位秒
      type: String,
      default: 0,
    },
  },
  computed: {
    getScrollTop() {
      let one = this.amtData.one * -this.fontfix(46) - this.fontfix(46);
      let ten = this.amtData.ten * -this.fontfix(46) - this.fontfix(46);
      let hundred = this.amtData.hundred * -this.fontfix(46) - this.fontfix(46);
      let thousand =
        this.amtData.thousand * -this.fontfix(46) - this.fontfix(46);
      let tenThousand =
        this.amtData.tenThousand * -this.fontfix(46) - this.fontfix(46);
      let oneHundredThousand =
        this.amtData.oneHundredThousand * -this.fontfix(46) - this.fontfix(46);
      let million = this.amtData.million * -this.fontfix(46) - this.fontfix(46);
      let tenMillion =
        this.amtData.tenMillion * -this.fontfix(46) - this.fontfix(46);
      let billion = this.amtData.billion * -this.fontfix(46) - this.fontfix(46);
      let decimalOne =
        this.amtData.decimalOne * -this.fontfix(38) - this.fontfix(38);
      let decimalTwo =
        this.amtData.decimalTwo * -this.fontfix(38) - this.fontfix(38);
      return {
        one,
        ten,
        hundred,
        thousand,
        tenThousand,
        oneHundredThousand,
        million,
        tenMillion,
        billion,
        decimalOne,
        decimalTwo,
      };
    },
  },
  data() {
    return {
      amtData: {
        // 金额拆分数据
        one: 0,
        ten: 0,
        hundred: 0,
        thousand: 0,
        tenThousand: 0,
        oneHundredThousand: 0,
        million: 0,
        tenMillion: 0,
        billion: 0,
        decimalOne: 0,
        decimalTwo: 0,
      },
      downReset: {
        // （时分）是否重回到最后一个
        hour: false,
        minute: false,
      },
      timer: null,
    };
  },
  watch: {
    amt: {
      immediate: true,
      handler(newVal, oldVal) {
        this.clearData();
        this.splitNumber(newVal);
      },
    },
  },
  mounted() {
    let that = this;
    window.addEventListener('resize', function () {
      that.clearData();
      that.splitNumber(that.amt);
    });
  },
  methods: {
    clearData() {
      this.amtData = {
        // 金额拆分数据
        one: 0,
        ten: 0,
        hundred: 0,
        thousand: 0,
        tenThousand: 0,
        oneHundredThousand: 0,
        million: 0,
        tenMillion: 0,
        billion: 0,
        decimalOne: 0,
        decimalTwo: 0,
      };
    },
    /**
     * @desc 自适应大小
     */
    fontfix(val) {
      let clientHeight = document.documentElement.clientHeight;
      return val * (clientHeight / 1080);
    },
    /**
     * @desc 拆分数字
     */
    splitNumber(num) {
      num = num.toString().replace('.', '').split('').reverse().join('');
      for (let i = 0; i < num.length; i++) {
        let item = num[i];
        if (i == 0) {
          this.amtData.decimalTwo = Number(item);
        } else if (i == 1) {
          this.amtData.decimalOne = Number(item);
        } else if (i == 2) {
          this.amtData.one = Number(item);
        } else if (i == 3) {
          this.amtData.ten = Number(item);
        } else if (i == 4) {
          this.amtData.hundred = Number(item);
        } else if (i == 5) {
          this.amtData.thousand = Number(item);
        } else if (i == 6) {
          this.amtData.tenThousand = Number(item);
        } else if (i == 7) {
          this.amtData.oneHundredThousand = Number(item);
        } else if (i == 8) {
          this.amtData.million = Number(item);
        } else if (i == 9) {
          this.amtData.tenMillion = Number(item);
        } else if (i == 10) {
          this.amtData.billion = Number(item);
        }
      }
    },
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer);
  },
};
</script>

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
.item-time-box {
  display: flex;
  align-items: flex-end;
  .split_normal,
  .split_warn {
    margin-left: (5 / @baseWidth) * 100vw;
    margin-right: (2 / @baseWidth) * 100vw;
    font-family: Microsoft YaHei;
    .fontfix(30);
    font-weight: bold;
  }
  .split_normal {
    color: #ffce37;
  }
  .split_warn {
    color: #2c5fb5;
  }
  .time-cell {
    text-align: center;
    + .time-cell {
      margin-left: (6 / @baseWidth) * 100vw;
    }
    .time-cell-box {
      position: relative;
      overflow: hidden;
      color: #ffce37;
      .fontfix(30);
      font-weight: bold;
      background: #122987;
      border: (2 / @baseWidth) * 100vw solid #2e76dc;
      .widthfix(36);
      .heightfix(46);
      .lhfix(46);
      border-radius: (4 / @baseWidth) * 100vw;

      .time-list {
        position: absolute;
        transition: all 0.3s;
        top: 0;
        left: 0;
        width: 100%;
        &.clear-transtion {
          transition: none;
        }
        > div {
          .heightfix(46);
          .lhfix(46);
        }
      }
      &.decimal {
        .widthfix(30);
        .heightfix(38);
        .fontfix(24);

        background: rgba(18, 41, 135, 0);
        border-color: #103691;
        .time-list {
          color: #4891ff;
          > div {
            .heightfix(38);
            .lhfix(38);
          }
        }
      }
      &.nothing {
        color: rgba(72, 145, 255, 0.2);
      }
    }
  }
}
</style>