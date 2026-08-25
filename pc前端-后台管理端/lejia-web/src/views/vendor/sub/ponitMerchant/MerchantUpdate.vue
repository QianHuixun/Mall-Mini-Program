<!-- 
@name: MerchantUpdate.vue 
@description: 积分商户管理--编辑模板 
@author: sx
@date: 2020/07/09
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="手机号" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput"  v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.mobile =val;}" placeholder="请输入手机"></el-input>
      </el-form-item>
      <el-form-item label="地址" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.addr" ref="addrInput" placeholder="请输入地址"></el-input>
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
        name: "",
        mobile: "",
        addr: "",
        enabled: true
      },
    };
  },
  mounted() {
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        name: "",
        mobile: "",
        addr: "",
        enabled: true
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
      if (!this.inputModel.name) {
        this.$message.error("请输入名称");
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.mobile) {
        this.$message.error("请输入登录手机");
        this.$refs.mobileInput.focus();
        return;
      }

      if (!utils.checkMobile(this.inputModel.mobile)) {
        this.$message.error("请输入正确的登录手机");
        this.$refs.mobileInput.focus();
        return;
      }


      if (!this.inputModel.addr) {
        this.$message.error("请输入地址");
        this.$refs.addrInput.focus();
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