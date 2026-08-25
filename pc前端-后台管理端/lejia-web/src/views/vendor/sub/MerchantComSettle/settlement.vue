<!-- 
@name: settlement.vue 
@description: 商户结算-结算弹窗
@author: crj
@date: 2021/12/20
-->
<template lang="html">
  <el-dialog title="采购结算" center :visible.sync="visible" :closeOnClickModal="false" @close="hide"
    :close-on-click-modal="false">
    <el-form>
      <p class="title">结算信息</p>
      <el-form-item label="结算日期：" label-width="100">
        {{inputModel.time.replace(' - ',' 至 ')}}
      </el-form-item> 
      <el-form-item label="总商户数：" label-width="100">
        {{inputModel.total}}人
      </el-form-item>
      <el-form-item label="总采购笔数：" label-width="100">
        {{inputModel.num}}笔
      </el-form-item>
      <el-form-item label="总采购金额：" label-width="100">
        {{inputModel.amtStr}}元
      </el-form-item>
      <el-form-item label="总结算金额：" label-width="100">
        {{inputModel.awaitAmtStr}}元
      </el-form-item>
      <p class="title">结算备注</p>
      <el-form-item label-width="0">
        <el-input type="textarea" placeholder="请输入结算备注，最多100字" v-model="remark" maxlength="100">
        </el-input>
      </el-form-item>
    </el-form>
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
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      visible: false,
      loading: false,
      remark: '',
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.remark = '';
    },

    /**
     * @desc  显示并初始化数据
     * @param {Array} pkeys 标识合集
     */
    show: function () {
      this.visible = true;
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
      let params = {
          rem: this.remark,
          startTime: this.inputModel.date ? this.inputModel.date[0] : '',
          endTime: this.inputModel.date ? this.inputModel.date[1] : '',
          queryTime:
            localStorage.getItem('insReportDate') ||
            utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D'),
        },
        url = api.order.updComMerSettle;
      if (this.isReport) {
        params = {
          settlementPkey: this.inputModel.settlementPkey,
        };
        url = api.order.updSettleProcess;
      }
      axios.post(url, this.$qs.stringify(params)).then((res) => {
        if (res.hasOwnProperty('result') && !res.result) {
          const h = this.$createElement;
          this.$msgbox({
            title: '无法结算提示',
            message: h('p', null, [
              h(
                'p',
                { style: 'font-weight:blod;text-align:center' },
                '请确认商户银行信息 '
              ),
              h(
                'p',
                { style: 'font-weight:blod;text-align:center' },
                '全部完善并正确后再次提交！'
              ),
            ]),
            customClass: 'settlement_msg',
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
          });
        } else {
          this.$message.success('结算成功');
          this.hide();
          this.$emit('confirm');
        }
      });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    inputModel: {
      type: Object,
      default: () => {
        return {
          amtStr: 0, //待结算金额
          awaitAmtStr: 0, //总交易金额
          num: 0, //总交易笔数
          total: 0,
          date: '',
        };
      },
    },
    isReport: {
      type: Boolean,
      default: () => {
        return false;
      },
    },
  },
};
</script>
<style lang="less" scoped>
.title {
  margin-bottom: 10px;
  font-weight: bold;
}

/deep/.el-form-item {
  margin-bottom: 5px;

  label {
    text-align: left;
    line-height: 30px;
  }

  .el-form-item__content {
    line-height: 30px;
  }
}

/deep/ .el-textarea > textarea {
  min-height: 90px !important;
}
::-webkit-scrollbar {
  display: none;
}
</style>
<style lang="less">
.settlement_msg .el-message-box__title {
  color: #f56c6c;
}
</style>