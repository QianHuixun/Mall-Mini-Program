<!-- 
@name: OurletUpdate.vue 
@description: 网点设置--编辑模板 
@author: sx
@date: 2020/03/24
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="角色名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="角色描述" :label-width="labelWidth">
        <el-input v-model="inputModel.description" ref="descriptionInput" placeholder="请输入名称"></el-input>
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
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        name: "",
        description: ""
      }
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        name: "",
        description: ""
      }
    },
    /**
     * 初始化数据
     */
    initData: function({ inputModel }) {
      this.inputModel = inputModel;
    },
    show: function() {
      this.visible = true;
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.visible = false;
      this.clearData();
      this.$emit("hide");
    },
    /**
     * 处理提交
     */
    handleSubmit: function() {
      if (!this.inputModel.name) {
        this.$message.error("请输入角色名称");
        this.$refs.nameInput.focus();
        return;
      }

      this.$emit("confirm", { inputModel: this.inputModel });
    }
  },
  props: {
    title: {
      type: String,
      default: "新增"
    }
  }
};
</script>