<!-- 
@name: headComp.vue 
@description: 大数据-头部
@author: 池仁杰
@date: 2022/01/11
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
.bigData-head {
  .heightfix(86);
  padding: (32 / @baseHeight) * 100vh (32 / @baseWidth) * 100vw 0
    (32 / @baseWidth) * 100vw;
  display: flex;
  justify-content: space-between;
  line-height: normal;

  .date-box {
    font-weight: bold;
    cursor: pointer;
    .time {
      .fontfix(20);
      .lhfix(16);
      letter-spacing: (1.6 / @baseWidth) * 100vw;
      color: #ffffff;
      margin-bottom: (6 / @baseHeight) * 100vh;
    }
    .date {
      .fontfix(18);
      .lhfix(16);
      color: #819ee7;
    }
  }
  .select-box {
    .title {
      .fontfix(14);
      font-weight: 400;
      color: #819ee7;
      padding-right: (12 / @baseWidth) * 100vw;
    }
    /deep/ .el-select {
      .widthfix(125);

      border-radius: 2px;
      .el-input--suffix .el-input__inner {
        background: rgba(3, 12, 60, 0.1) !important;
        box-shadow: 0px 0px 10px 0px #2e76dc inset !important;
        border: none;
        .heightfix(40);
        .fontfix(16);
        font-weight: bold;
        color: #acbfef;
        text-align: center;
        padding-left: (15 / @baseWidth) * 100vw;
        padding-right: (30 / @baseWidth) * 100vw;
      }
      .el-input .el-select__caret::before {
        content: '\e78f' !important;
        position: absolute;
        .fontfix(12);
        width: 100%;
        top: 50%;
        color: #acbfef;
        transform: translate(-50%, -50%);
      }
    }

    /deep/.el-select-dropdown {
      background: rgba(3, 12, 60, 0.1) !important;
      box-shadow: 0px 0px 10px 0px #2e76dc inset !important;
      border: none;

      .el-select-dropdown__item {
        color: #acbfef;
        .widthfix(125);
        .heightfix(34);
        .fontfix(14);
        display: flex;
        align-items: center;
        justify-content: center;
        &.hover {
          background: transparent;
        }
      }
      .el-select-dropdown__wrap {
        margin-bottom: 0 !important;
        margin-right: 0 !important;
      }
      .el-select-dropdown__item:hover {
        background: transparent;
      }

      .popper__arrow {
        display: none;
      }
    }
  }
}
</style>
<template lang="html">
  <div class="bigData-head">
    <div class="date-box" @click="handleFull">
      <div class="time">{{nowDate.time}}</div>
      <div class="date">{{nowDate.date}}</div>
    </div>
    <div class="select-box">
      <span class="title">统计周期：</span>
      <el-select v-model="searchData.timeType" placeholder="选择周期" :popper-append-to-body="false" @change="timeTypeChange">
        <el-option label="全天" value="THE_DAY"></el-option>
        <el-option label="三日内" value="THREE_DAY"></el-option>
        <el-option label="七日内" value="WEEK"></el-option>
        <el-option label="一月内" value="MONTH"></el-option>
      </el-select>
    </div>
  </div>
</template>
<script>
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      nowDate: {
        date: utils.getNowDate(),
        time: utils.formatTimeInArr(new Date().getTime() / 1000, 'h:m:s'),
      },
      searchData: {
        timeType: 'THE_DAY',
      },
      timer: '',
      fullScreen: false,
    };
  },
  props: {
    timeType: {
      type: String,
      default: () => {
        return 'THE_DAY';
      },
    },
  },
  mounted() {
    this.timer = setInterval(() => {
      this.nowDate.time = utils.formatTimeInArr(
        new Date().getTime() / 1000,
        'h:m:s'
      );
      if (this.nowDate.time == '00:00:00') {
        this.nowDate.date = utils.getNowDate();
      }
    }, 1000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    /**
     * @desc 周期改变
     */
    timeTypeChange(val) {
      this.$emit('update:timeType', val);
    },
    /**
     * @desc 全屏
     */
    handleFull() {
      if (!this.fullScreen) {
        // console.log(document.getElementById('marketBigData'))
        this.requestFullScreen(document.getElementById('bigData'));
      } else {
        this.fullExdit();
      }
      this.fullScreen = !this.fullScreen;
    },
    //全屏事件
    requestFullScreen(element) {
      var requestMethod =
        element.requestFullScreen || //W3C
        element.webkitRequestFullScreen || //Chrome等
        element.mozRequestFullScreen || //FireFox
        element.msRequestFullScreen; //IE11
      if (requestMethod) {
        requestMethod.call(element);
      } else if (typeof window.ActiveXObject !== 'undefined') {
        //for Internet Explorer
        var wscript = new ActiveXObject('WScript.Shell');
        if (wscript !== null) {
          wscript.SendKeys('{F11}');
        }
      }
    },
    //退出全屏事件
    fullExdit() {
      var exitMethod =
        document.exitFullscreen || //W3C
        document.mozCancelFullScreen || //Chrome等
        document.webkitExitFullscreen || //FireFox
        document.webkitExitFullscreen; //IE11
      if (exitMethod) {
        exitMethod.call(document);
      } else if (typeof window.ActiveXObject !== 'undefined') {
        //for Internet Explorer
        var wscript = new ActiveXObject('WScript.Shell');
        if (wscript !== null) {
          wscript.SendKeys('{F11}');
        }
      }
    },
  },
};
</script>