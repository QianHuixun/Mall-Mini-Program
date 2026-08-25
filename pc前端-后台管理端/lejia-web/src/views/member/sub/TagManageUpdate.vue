<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="分类名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入标签名称"></el-input>
      </el-form-item>
      <el-form-item label="标签类型" :label-width="labelWidth"  :required="true">
        <el-select v-model="inputModel.type" @change="handleChange" placeholder="选择标签类型" ref="typeSelect" :disabled="type =='edit'">
          <el-option value="NORMAL" key="NORMAL" label="普通标签"></el-option>
          <el-option value="MSD" key="MSD" label="热力豆标签"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="标签描述" :label-width="labelWidth">
        <el-input v-model="inputModel.description" ref="descriptionInput" placeholder="请输入标签名称" type="textarea" :rows="2"></el-input>
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
      labelWidth: '100px',
      visible: false,
      loading: false,
      type: "add",
      inputModel: {
        name: '',
        type: "NORMAL",
        description: '',
        pkey: '',
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
        name: '',
        type: "NORMAL",
        description: '',
        pkey: '',
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.type = "edit";
      this.inputModel = inputModel;
    },
    show: function () {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('hide');
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.name) {
        this.$message.error('请输入标签名称');
        this.$refs.nameInput.focus();
        return;
      }
      if (!this.inputModel.type) {
        this.$message.error('请输入标签类型');
        this.$refs.typeSelect.focus();
        return;
      }

      this.$emit('confirm', { inputModel: this.inputModel });
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
/deep/ .el-form {
  overflow: visible !important;
}
</style>