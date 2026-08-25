<!-- 
@name: settlement.vue 
@description: 商户结算-结算弹窗
@author: crj
@date: 2020/10/18
-->
<template lang="html">
  <el-dialog title="银行账户信息" center :visible.sync="visible" :closeOnClickModal="false" @close="hide"
    :close-on-click-modal="false">
    <el-form>
      <el-form-item label="开户银行名称：" :label-width="100">
        {{inputModel.bankname ||'--'}}
      </el-form-item>
      <el-form-item label="银行卡号：" :label-width="100">
        {{inputModel.bankcard ||'--'}}
      </el-form-item>
      <el-form-item label="开户人：" :label-width="100">
        {{inputModel.bankuser ||'--'}}
      </el-form-item>
      <el-form-item label="开户人身份证号：" :label-width="100">
        {{inputModel.zxIdentity ||'--'}}
      </el-form-item>
      <el-form-item label="银行卡绑定手机：" :label-width="100">
        {{inputModel.bankuserMoblie ||'--'}}
      </el-form-item>
    </el-form>
  </el-dialog>
</template>
<script>
export default {
  data() {
    return {
      visible: false,
      inputModel: {},
      pkey: '',
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
        vendor: this.pkey,
      };
      axios
        .post(api.order.queryBankInfo, this.$qs.stringify(params))
        .then((res) => {
          this.inputModel = res;
          this.visible = true;
        });
    },
    /**
     * @desc  显示并初始化数据
     * @param {Array} pkeys 标识合集
     */
    show: function (pkey) {
      this.pkey = pkey;
      this.initData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
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
/deep/.el-dialog {
  width: 500px;
}
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