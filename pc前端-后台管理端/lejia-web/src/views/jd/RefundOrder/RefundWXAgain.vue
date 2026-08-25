<!--
 * @Author: 沙晓
 * @Date: 2026-07-02 16:24:24
 * @LastEditors: 沙晓
 * @LastEditTime: 2026-07-02 16:54:17
 * @Description: 微信退款
 * @FilePath: /lejia-web/src/views/jd/RefundOrder/RefundWXAgain.vue
-->
<template>
  <el-dialog title="微信退款" width="100px" :visible="visible" @hide="handleClose">
    <div class="refund-wx"> 
      退款金额：￥{{ refundAmount }}
    </div>
    <div slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>
<script>
import qs from "qs";
export default {
  data() {
    return {
      refundPkey: '',
      orderPkey: '',
      visible: false,
      refundAmount: 0
    }
  },
  mounted() {
  },
  methods: {
    show(row) {
      this.visible = true;
      this.refundPkey = row.pkey;
      this.refundAmount = row.refundWeixinAmt;
    },
    handleClose() {
      this.visible = false;
    },
    handleSubmit() {
      const params = {
        refundPkey: this.refundPkey,
      }
      axios
          .post(api.jd.refundAgainWX, qs.stringify(params)).then(res => {
            if (res) {
              this.$message.success("微信退款成功");
              this.$emit('refresh');
              this.handleClose();
            }
          });
    },
  },
  }
</script>