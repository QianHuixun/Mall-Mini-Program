<template>
  <el-dialog title="运费配置" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="京东专区运费">
        <el-radio v-model="isConsumerPostage" :label="true">消费者承担</el-radio>
        <el-radio v-model="isConsumerPostage" :label="false">集团方承担</el-radio>
      </el-form-item>
    </el-form>
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
      isConsumerPostage: true
    }
  },
  methods: {
    show() {
      this.visible = true
      this.getData()
    },
    /**
     * 关闭弹窗
     */
    handleClose() {
      this.visible = false
    },
    /**
     * 获取服务内容
     */
    getData() {
      axios.post(api.jd.getPostageConfig)
        .then(res => {
          this.isConsumerPostage = res.isConsumerPostage
        })
    },
    /**
     * 确认提交
     */
    handleSubmit() {
      const params = {
        isConsumerPostage: this.isConsumerPostage
      }
      axios.post(api.jd.setPostageConfig, this.$qs.stringify(params))
        .then(res => {
          this.$message.success('设置成功')
          this.handleClose()
        })
    },
  }
}
</script>
