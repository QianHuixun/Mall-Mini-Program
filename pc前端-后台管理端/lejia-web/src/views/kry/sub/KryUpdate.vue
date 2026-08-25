<!-- 
@name: KryUpdate.vue 
@description: 客如云商户管理--编辑模板 
@author: sx
@date: 2020/07/13
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="客如云ID" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.uuid" ref="uuidInput" placeholder="请输入客如云ID" v-on:input="limitInput($event)">
        </el-input>
      </el-form-item>
      <el-form-item label="商户名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入商户名称"></el-input>
      </el-form-item>
      <el-form-item label="手机号码" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入号码"></el-input>
      </el-form-item>
      <el-form-item label="负责人" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.manager" ref="managerInput" placeholder="请输入商户名称"></el-input>
      </el-form-item>
      <el-form-item label="Token" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.token" ref="tokenInput" placeholder="请输入Token"></el-input>
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
          manager: "",
          mobile: "",
          uuid: "",
          token: "",
          enabled: true
        },
      };
    },
    mounted() {},
    methods: {
      /**
       * 限制仅输入整数
       */
      limitInput(value) {
        value = value.replace(/[^\d]/g, "");
        this.inputModel.uuid = value;

      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          name: "",
          manager: "",
          mobile: "",
          uuid: "",
          token: "",
          enabled: true
        };
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
      },
      show: function () {
        this.visible = true;
        this.clearData();
        this.$emit("hide");
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        console.log(this.inputModel)
        if (!this.inputModel.uuid) {
          this.$message.error("请输入客如云ID");
          this.$refs.uuidInput.focus();
          return;
        }

        if (!this.inputModel.name) {
          this.$message.error("请输入商户名称");
          this.$refs.nameInput.focus();
          return;
        }

        if (!this.inputModel.manager) {
          this.$message.error("请输入负责人");
          this.$refs.managerInput.focus();
          return;
        }

        if (!this.inputModel.mobile) {
          this.$message.error("请输入手机号码");
          this.$refs.mobileInput.focus();
          return;
        }

        if (!utils.checkMobile(this.inputModel.mobile)) {
          this.$message.error("请输入正确的手机号码");
          this.$refs.mobileInput.focus();
          return;
        }

        if (!this.inputModel.token) {
          this.$message.error("请输入Token");
          this.$refs.tokenInput.focus();
          return;
        }



        this.$emit("confirm", {
          inputModel: this.inputModel
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