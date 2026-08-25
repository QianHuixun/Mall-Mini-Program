<template>
  <el-dialog title="提现" width="500px" :before-close="hide" :visible="visible">
    <div class="dialog-main">
      <div class="dialog-item">银行账号：{{sumData.pan}}</div>
      <div class="dialog-item">
        <el-input
            placeholder="输入提现金额"
            v-model="amt"
            v-on:input="(val)=>{amt = formatPrice(val)}"
            clearable>
</el-input>
      </div>
      <div class="dialog-item">可提现余额：{{ sumData.makePaymentAmt || 0 }}</div>
    </div>
    <span slot="footer" class="dialog-footer">
    <el-button @click="hide">取 消</el-button>
    <el-button  :loading="loading" type="primary" @click="submit">确 定</el-button>
  </span>
  </el-dialog>
</template>

<script>
import qs from "qs";
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      loading: false,
      visible: false,
      sumData: {},
      amt: ""

    }
  },
  methods: {
    //格式化价格
    formatPrice: function (price) {
      if(price > this.sumData.makePaymentAmt) {
        price = String(this.sumData.makePaymentAmt);
      }
      return utils.formatPrice(price);
    },
    show: function({sumData}) {
      this.visible = true;
      this.sumData = sumData;
    },
    hide: function() {
      this.visible = false;
      this.amt = "";
    },
    submit: function() {
      if(!this.amt) {
        this.$message.warning("请输入提现金额！");
        return;
      }
      const param = {
        amt: this.amt
      }
      this.loading = true;
      axios.post(api.market.financeEDetailsWithdraw, qs.stringify(param), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success("提现成功！");
          this.$emit("refresh");
          this.loading = false;
          this.hide();
        });
    }
  }
}
</script>

<style lang="less" scoped>
.dialog-item {
  margin: 8px 0;
  &:nth-child(1) {
    font-size: 16px;
  }
  .el-input  {
    height: 45px;
    /deep/.el-input__inner {
      font-size: 18px;
      height: inherit !important;
  }
  }

}
</style>