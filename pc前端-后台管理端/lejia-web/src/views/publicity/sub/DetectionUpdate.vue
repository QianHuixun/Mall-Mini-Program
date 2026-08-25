<!-- 
@name: DetectionUpdate.vue 
@description: 检测信息--编辑模板 
@author: sx
@date: 2020/07/06
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="商户" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.merchant" ref="merchantInput" placeholder="请输入商户"></el-input>
      </el-form-item>
      <el-form-item label="检测商品" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.goods" ref="goodsInput" placeholder="请输入检测商品"></el-input>
      </el-form-item>
      <el-form-item label="检测项目" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.entry" ref="entryInput" placeholder="请输入检测项目"></el-input>
      </el-form-item>
      <el-form-item label="检测时间" :label-width="labelWidth" :required="true">
        <el-date-picker v-model="inputModel.testDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss" placeholder="选择检测时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="检测结论" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.testResult" placeholder="请选择">
          <el-option label="合格" :value="true"></el-option>
          <el-option label="不合格" :value="false"></el-option>
        </el-select>
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
import utils from "@/assets/js/utils";
import dropdown from "@/assets/js/dropdown";

export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        testDate: "",
        testResult: true,
        merchant: "",
        goods: "",
        entry: ""

      },
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        testDate: "",
        testResult: true,
        merchant: "",
        goods: "",
        entry: ""
      };
    },
    /**
     * 初始化数据
     */
    initData: function({ inputModel }) {
      this.inputModel = inputModel;
    },
    show: function() {
      this.visible = true;
      this.clearData();
      this.$emit("hide");
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
    },
    /**
     * 处理提交
     */
    handleSubmit: function() {
      if (!this.inputModel.merchant.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入商户");
        this.$refs.merchantInput.focus();
        return;
      }

      if (!this.inputModel.goods.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入检测商品");
        this.$refs.goodsInput.focus();
        return;
      }

      if (!this.inputModel.entry.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入检测项目");
        this.$refs.entryInput.focus();
        return;
      }

      if (!this.inputModel.testDate) {
        this.$message.error("请选择检测时间");
        return;
      }

      if (!this.inputModel.testResult === "") {
        this.$message.error("请选择检测结论");
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