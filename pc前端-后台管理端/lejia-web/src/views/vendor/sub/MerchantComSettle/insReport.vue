<!-- 
@name: settlement.vue 
@description: 商户结算-结算弹窗
@author: crj
@date: 2021/12/20
-->
<template lang="html">
  <el-dialog title="生成报表" center :visible.sync="visible" :closeOnClickModal="false" @close="hide"
    :close-on-click-modal="false">
    <p class="title">选择日期</p>
    <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
      end-placeholder="结束日期" value-format="yyyy-MM-dd" :picker-options="pickerOptions">
    </el-date-picker>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import qs from 'qs';
export default {
  data() {
    return {
      visible: false,
      loading: false,
      date: '',
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now() - 8.64e7;
        },
      },
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.date = '';
    },
    /**
     * @desc  显示并初始化数据
     * @param {Array} pkeys 标识合集
     */
    show: function (pkeys) {
      this.getDate();
      this.pkeys = pkeys;
      this.visible = true;
    },
    getDate() {
      axios.post(api.order.querySettleDate, qs.stringify({
        marketKeys: this.marketKeys.join(',')
      })).then((res) => {
        console.log(res);
        this.pickerOptions = {
          disabledDate(time) {
            let isDisabled = false;
            if (time.getTime() > Date.now() - 8.64e7) {
              isDisabled = true;
            }
            for (let i in res) {
              let item = res[i];
              if (time.getTime() <= item.end && time.getTime() >= item.start) {
                isDisabled = true;
                break;
              }
            }

            return isDisabled;
          },
          cellClassName(time) {
            let className = '';
            for (let i in res) {
              let item = res[i];
              if (
                time.getTime() <= item.end &&
                time.getTime() >= item.start &&
                item.colour
              ) {
                className = 'red-date';
                break;
              }
            }
            return className;
          },
        };
      });
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.date) {
        this.$message.warning('请选择时间');
        return;
      }
      localStorage.setItem('comSettleDate', JSON.stringify(this.date));
      this.$emit('confirm');
      this.hide();
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    settlementMethod: {
      type: String,
      default: 'PURCHASE_SETTLEMENT',
    },
    marketKeys: {
      type: Array,
      default: [],
    }
  },
};
</script>
<style lang="less" scoped>
/deep/.el-dialog {
  width: 300px !important;
}
/deep/.el-dialog__body {
  text-align: center;
  .title {
    margin-bottom: 10px;
    font-weight: bold;
    text-align: left;
  }
}
</style>
<style lang="less">
.el-date-table td.red-date span {
  background-color: #f56c6c !important;
  color: #fff;
}
.el-date-table td.red-date.disabled div {
  background: transparent;
}
.el-date-table td.red-date.disabled.in-range div {
  background: #f5f7fa;
}
</style>