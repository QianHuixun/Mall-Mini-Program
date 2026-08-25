<!-- 
@name: upd.vue 
@description: 常见问题--编辑模板 
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" append-to-body>
    <el-form>
      <el-form-item label="问题分类" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.type" ref="farmer" placeholder="请选择问题分类">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typesList"></el-option>
        </el-select>
        <el-button type="text" @click="handleTypeShow">分类管理</el-button>
      </el-form-item>
      <el-form-item label="问题" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" placeholder="请输入问题" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="回答" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.answer" type="textarea" placeholder="请输入回答" maxlength="200"></el-input>
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
    <type-comp ref="typeComp" @hide="getTypesList"></type-comp>
  </el-dialog>
</template>
<script>
import dropdown from "@/assets/js/dropdown";
import TypeComp from './type.vue'
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        type: '',
        name: '',
        answer: '',
      },
      typesList: [],
    };
  },
  components: {
    TypeComp
  },
  mounted() {
    this.getTypesList()
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        farmer: '',
        market: '',
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
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
      this.$emit("hide");
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.type) {
        this.$message.error("请选择问题分类");
        return;
      }

      if (!this.inputModel.name) {
        this.$message.error("请输入问题");
        return;
      }

      if (!this.inputModel.answer) {
        this.$message.error("请输入回答");
        return;
      }

      this.$emit("confirm", { inputModel: this.inputModel });
    },
    /**
     * 显示分类管理
     */
    handleTypeShow() {
      this.$refs.typeComp.show()
    },
    /**
     * 问题分类列表
     */
     getTypesList() {
      axios
        .post(api.mall.problemTypeList, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.typesList = response;
        });
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
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>