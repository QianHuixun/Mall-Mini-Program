<!--
  * @file:  SeasonDatePicker
  * @author: crj
  * @date: 2021-10-15
  * @description:季度选择控件
-->
<template>
  <div class="season-date-picker">
    <mark style="position:fixed;top:0;bottom:0;left:0;right:0;background:rgba(0,0,0,0);z-index:999;" v-show="showSeason"
      @click.stop="showSeason=false"></mark>
    <el-input :placeholder="placeholder||'请选择季度'" v-model="showValue" style="width:138px;" @focus="showSeason=true">
      <i slot="prefix" class="el-input__icon el-icon-date"></i>
    </el-input>
    <el-card class="box-card" style="width:322px;padding: 0 3px 20px;margin-top:10px;position:fixed;z-index:9999"
      v-show="showSeason">
      <div slot="header" class="clearfix" style="text-align:center;padding:0">
        <button type="button" aria-label="前一年"
          class="el-picker-panel__icon-btn el-date-picker__prev-btn el-icon-d-arrow-left" @click="prev"></button>
        <span role="button" class="el-date-picker__header-label">{{year}}年</span>
        <button type="button" aria-label="后一年" @click="next"
          class="el-picker-panel__icon-btn el-date-picker__next-btn el-icon-d-arrow-right"></button>
      </div>
      <div class="text item" style="text-align:center;">
        <el-button type="text" size="medium" class="left-btn"
          :style="{color:showValue==`${year}年1季度` ?'#409EFF':(disabledFunc(`${year}-01-01`)?'#c0c4cc':'')}" @click="selectSeason(0)"
          :disabled="disabledFunc(`${year}-01-01`)">
          第一季度
        </el-button>
        <el-button type="text" size="medium" class="right-btn"
          :style="{color:showValue==`${year}年2季度` ?'#409EFF':(disabledFunc(`${year}-04-01`)?'#c0c4cc':'')}" @click="selectSeason(1)"
          :disabled="disabledFunc(`${year}-04-01`)">
          第二季度
        </el-button>
      </div>
      <div class="text item" style="text-align:center;">
        <el-button type="text" size="medium" class="left-btn"
          :style="{color:showValue==`${year}年3季度` ?'#409EFF':(disabledFunc(`${year}-07-01`)?'#c0c4cc':'')}" @click="selectSeason(2)"
          :disabled="disabledFunc(`${year}-07-01`)">
          第三季度
        </el-button>
        <el-button type="text" size="medium" class="right-btn"
          :style="{color:showValue==`${year}年4季度` ?'#409EFF':(disabledFunc(`${year}-10-01`)?'#c0c4cc':'')}" @click="selectSeason(3)"
          :disabled="disabledFunc(`${year}-10-01`)">
          第四季度
        </el-button>
      </div>
    </el-card>
  </div>
</template>
<script>
  export default {
    props: {
      valueArr: {
        default: () => {
          return ['01-03', '04-06', '07-09', '10-12']
        },
        type: Array
      },
      getValue: {
        default: () => {},
        type: Function
      },
      defaultValue: {
        default: '',
        type: String
      },
      placeholder: {
        default: '',
        type: String
      },
      disabledType: { //为范围选择时判断是开始日期1 还是结束日期2
        default: -1,
        type: Number
      },
      disabledTime: { //禁用的时间界限
        default: '',
        type: String
      },
      time: {
        default: '',
        type: String
      }
    },
    data() {
      return {
        showSeason: false,
        season: '',
        year: new Date().getFullYear(),
        // showValue: '',
      }
    },
    computed: {
      showValue: {
        get() {
          return this.time;
        },
        set(val) {
          this.$emit("update:time", val);
        },
      },
    },
    created() {
      if (this.defaultValue) {
        let value = this.defaultValue
        let arr = value.split('-')
        this.year = arr[0].slice(0, 4)
        let str = arr[0].slice(4, 6) + '-' + arr[1].slice(4, 6)
        let arrAll = this.valueArr
        this.showValue = `${this.year}年${arrAll.indexOf(str) + 1}季度`
      }
    },
    watch: {
      defaultValue: function (value, oldValue) {
        let arr = value.split('-')
        this.year = arr[0].slice(0, 4)
        let str = arr[0].slice(4, 6) + '-' + arr[1].slice(4, 6)
        let arrAll = this.valueArr
        this.showValue = `${this.year}年${arrAll.indexOf(str) + 1}季度`
      }
    },
    methods: {
      /**
       * @desc 日期是否不能选择
       * @param {String} time 日期 
       */
      disabledFunc(time) {
        if (this.disabledType != -1) {
          if (this.disabledTime) {
            if (this.disabledType == 1) {
              return new Date(this.disabledTime).getTime() < new Date(time).getTime()
            } else {
              return new Date(this.disabledTime).getTime() > new Date(time).getTime()
            }
          } else {
            return false
          }
        } else {
          return false
        }

      },
      one() {
        this.showSeason = false
      },
      prev() {
        this.year = this.year * 1 - 1
      },
      next() {
        this.year = this.year * 1 + 1
      },
      selectSeason(i) {
        let that = this
        that.season = i + 1
        let arr = that.valueArr[i].split('-')
        that.getValue(that.year + arr[0] + '-' + that.year + arr[1])
        that.showSeason = false
        this.showValue = `${this.year}年${this.season}季度`
      }
    }
  }
</script>
<style lang="less" scoped>
  .left-btn {
    width: 40%;
    color: #606266;
    float: left;
  }

  .right-btn {
    width: 40%;
    color: #606266;
    float: left;
  }
</style>