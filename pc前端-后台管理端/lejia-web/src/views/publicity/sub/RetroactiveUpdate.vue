<!-- 
@name: RetroactiveUpdate.vue 
@description: 溯源信息--编辑模板 
@author: sx
@date: 2020/07/06
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="溯源商品" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.goods" ref="goodsInput" placeholder="请输入溯源商品"></el-input>
      </el-form-item>
      <el-form-item label="溯源商户" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.merchant" ref="merchantInput" placeholder="请输入溯源商户"></el-input>
      </el-form-item>
      <el-form-item label="进货日期" :label-width="labelWidth" :required="true">
        <el-date-picker v-model="inputModel.oriDate" type="datetime" placeholder="选择检测时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="供应商" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.vendor" ref="vendorInput" placeholder="请输入供应商"></el-input>
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
        oriDate: "",
        merchant: "",
        goods: "",
        vendor: ""
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
        oriDate: "",
        merchant: "",
        goods: "",
        vendor: ""
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
      if (!this.inputModel.goods.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入溯源商品");
        this.$refs.goodsInput.focus();
        return;
      }

        if (!this.inputModel.merchant.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入溯源商户");
        this.$refs.merchantInput.focus();
        return;
      }

      if (!this.inputModel.oriDate) {
        this.$message.error("请输入进货日期");
        return;
      }

      if (!this.inputModel.vendor.replace(/(^\s*)|(\s*$)/g, "")) {
        this.$message.error("请输入供应商");
        this.$refs.vendorInput.focus();
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