<template>
  <el-dialog title="商品服务内容" center :visible.sync="visible" :closeOnClickModal="false">
    <div class="item" v-for="(item, index) in list" :key="index">
      <el-input v-model="list[index]"></el-input>
      <el-button type="text" :disabled="list.length <= 1" @click="handleDelItem(index)">删除</el-button>
    </div>
    <el-button type="text" :disabled="list.length > 4" @click="handleAddItem">添加一项</el-button>
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
      list: [''],
      loading: false,
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
      this.list = ['']
    },
    /**
     * 增加空信息
     */
    handleAddItem() {
      this.list.push('')
    },
    /**
     * 删除信息项
     */
    handleDelItem(index) {
      this.list.splice(index, 1)
    },
    /**
     * 获取服务内容
     */
    getData() {
      axios.post(api.jd.getServiceContent)
        .then(res => {
          this.list = res
        })
    },
    /**
     * 确认提交
     */
    handleSubmit() {
      axios.post(api.jd.setServiceContent, this.list)
        .then(res => {
          this.$message.success('设置成功')
          this.handleClose()
        })
    },
  }
}
</script>

<style>
.item {
  display: flex;
  align-items: center;
}
</style>