<template>
  <el-dialog :visible="visible" :title="title" @close="handleClose">
    <el-form label-width="120px">
      <el-form-item label="关键词" required>
        <el-input v-model="inputModel.keyword" placeholder="请输入关键词"></el-input>
      </el-form-item>
      <el-form-item label="排序">
        <el-input v-model="inputModel.sort" placeholder="请输入排序值"></el-input>
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
  props: ['type', 'title'],
  data() {
    return {
      visible: false,
      loading: false,
      inputModel: {
        pkey: null,
        keyword: null,
        sort: null,
        module: this.type
      },
    }
  },
  methods: {
    show() {
      this.visible = true
    },
    update({pkey}) {
      axios.post(api.mall.keywordGet, this.$qs.stringify({pkey}))
        .then(res => {
          this.inputModel = res
        })
    },
    handleClose() {
      this.visible = false
      this.loading = false
      this.inputModel = {
        pkey: null,
        keyword: null,
        sort: null,
        module: this.type
      }
    },
    handleSubmit() {
      if(!this.inputModel.keyword) {
        this.$message.warning("请输入关键词")
        return
      }
      this.loading = true
      this.$emit('confirm', this.inputModel)
    },
  }
}
</script>

<style></style>