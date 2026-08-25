<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="调整方式" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.direct">
          <el-radio label="true">增加</el-radio>
          <el-radio label="false">减少</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="调整金额" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.amt" maxlength="10" placeholder="请输入调整金额"
          @input="inputModel.amt = formatPrice(inputModel.amt)">
        </el-input>
      </el-form-item>
      <el-form-item label="备注" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.remark" maxlength="100" placeholder="请输入备注"></el-input>
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
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        pkey: '',
        direct: '',
        amt: '',
        remark: '',
      },
    };
  },
  mounted() {},
  components: {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        pkey: '',
        direct: '',
        amt: '',
        remark: '',
      };
    },
    show: function ({pkey}) {
      this.visible = true;
      this.inputModel.pkey = pkey
      console.log(this.inputModel);
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('refresh');
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.direct) {
        this.$message.error('请选择调整方式');
        return;
      }
      if (!this.inputModel.amt) {
        this.$message.error('请输入调整金额');
        return;
      }
      if (!this.inputModel.remark) {
        this.$message.error('请输入备注');
        return;
      }
      this.loading = true
      axios.post(api.marketing.msdBalanceAdjust, this.inputModel)
        .then(res => {
          this.$message.success('调整成功！')
          this.hide()
          this.$emit('refresh')
        })
        .finally(() => {
          this.loading = false
        })
    },
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
  },
  props: {
    title: {
      type: String,
      default: '清空热力豆',
    },
  },
};
</script>
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>