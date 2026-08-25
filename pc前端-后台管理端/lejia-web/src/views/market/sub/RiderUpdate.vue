<!-- 
@name: RiderUpdate.vue 
@description: 骑手管理--编辑模板 
@author: zs
@date: 2020/07/25
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="骑手姓名" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入骑手姓名"></el-input>
      </el-form-item>
      <el-form-item label="手机号码" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入手机号码" v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.mobile =val;}" ></el-input>
      </el-form-item>
      <el-form-item label="备注" :label-width="labelWidth" v-show="isEdit">
        <el-input v-model="inputModel.remark" type="textarea" ref="remarkInput" placeholder="请输入备注"></el-input>
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

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          mobile: "",
          name: "",
          pkey: "",
          remark:""
        },
      };
    },
    mounted() {},
    methods: {
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          mobile: "",
          name: "",
          pkey: "",
          remark:""
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
        if (!this.inputModel.name) {
          this.$message.error("请输入骑手姓名");
          this.$refs.nameInput.focus();
          return;
        }

        if (!this.inputModel.mobile) {
          this.$message.error("请输入骑手号码");
          this.$refs.mobileInput.focus();
          return;
        }

        if (!utils.checkMobile(this.inputModel.mobile)) {
        this.$message.error("请输入正确的登录手机");
        this.$refs.mobileInput.focus();
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
      },
      isEdit: {
        type: Boolean,
        default: false
      }
    }
  };
</script>