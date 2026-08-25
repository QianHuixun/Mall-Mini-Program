<template>
  <el-dialog title="批量设置价格" center :visible.sync="visible" :closeOnClickModal="false" append-to-body>
    <div>
      商城销售价格 = 京东价格*（1+
      <el-input v-model="percentage" style="width: 200px;" v-on:input="(val)=>{val =val.replace(/[^\d]/g,''); percentage =val;}"></el-input>
      %
    </div>
    <div>如需加价销售，可填入加价百分比，无需加价填0。</div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="handleClose">
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
      percentage: null
    }
  },
  methods: {
    show() {
      this.visible = true
    },
    /**
     * 关闭弹窗
     */
    handleClose() {
      this.percentage = null
      this.visible = false
    },
    /**
     * 确认提交
     */
    handleSubmit() {
      if(!this.percentage) {
        this.$message.warning("请输入百分比")
        return
      }
      this.$emit('confirm', this.percentage)
    },
  }
}
</script>

<style></style>