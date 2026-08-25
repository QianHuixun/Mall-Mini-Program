<!-- 
@name: settlement.vue 
@description: 商户结算-结算弹窗
@author: crj
@date: 2020/10/18
-->
<template lang="html">
  <el-dialog title="采购结算" center :visible.sync="visible" :closeOnClickModal="false" @close="hide"
    :close-on-click-modal="false">
    <el-form>
      <p class="title">结算信息</p>
      <el-form-item label="采购日期：" :label-width="100">
        {{inputModel.startDate}} 至 {{inputModel.endDate}}
      </el-form-item>
      <el-form-item label="总商户数：" :label-width="100">
        {{inputModel.vendorCount}}人
      </el-form-item>
      <el-form-item label="总采购笔数：" :label-width="100">
        {{inputModel.purchaseCount}}笔
      </el-form-item>
      <el-form-item label="总采购金额：" :label-width="100">
        {{inputModel.purchaseAmt}}元
      </el-form-item>
      <p class="title">结算信息</p>
      <el-form-item :label-width="0">
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
export default {
  data() {
    return {
      visible: false,
      loading: false,
      inputModel: {
        endDate: '',
        pkeys: [],
        purchaseAmt: '',
        purchaseCount: '',
        settlementRemark: '',
        startDate: '',
        vendorCount: '',
      },
      remark: '',
      pkeys: [],
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {};
      this.remark = '';
    },
    /**
     * 初始化数据
     */
    initData: function () {
      let params = {
        pkeys: this.pkeys.join(','),
      };
      axios
        .post(api.order.querySettleDetail, this.$qs.stringify(params))
        .then((res) => {
          this.inputModel = res;
          this.visible = true;
        });
    },
    /**
     * @desc  显示并初始化数据
     * @param {Array} pkeys 标识合集
     */
    show: function (pkeys) {
      this.pkeys = pkeys;
      this.initData();
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
        pkeys: this.pkeys.join(','),
        settlementRemark: this.remark,
      };
      axios
        .post(api.order.updMerSettle, this.$qs.stringify(params))
        .then((res) => {
          this.$message.success('结算成功');
          this.hide();
          this.$emit('confirm');
        });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
  },
};
</script>
<style lang="less" scoped>
.title {
  margin-bottom: 10px;
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
</style>