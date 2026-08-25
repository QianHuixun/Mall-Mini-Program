<!-- 
@name: InfoChange.vue 
@description: 更改手机
@author: sx
@date: 2020/04/02
-->
<template>
  <el-dialog title="更改手机号码" center :visible.sync="visible" :closeOnClickModal="false">
    <el-steps :active="active" finish-status="success" simple>
      <el-step title="验证当前账号"></el-step>
      <el-step title="修改账号手机"></el-step>
    </el-steps>
    <el-form v-if="active == 1">
      <el-form-item label="手机号码" :label-width="labelWidth" class="el-form-item_btn">
        <el-input v-model="inputModel.account" ref="accountInput" placeholder="请输入手机号码" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="短信验证码" :label-width="labelWidth" class="el-form-item_btn">
        <el-input v-model="inputModel.captcha" ref="codeInput" placeholder="请输入验证码"></el-input>
        <el-button type="primary" @click="getCode" :disabled="isCodeDisabled">
          {{ codeText }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-form v-if="active == 2">
      <el-form-item label="手机号码" :label-width="labelWidth" class="el-form-item_btn">
        <el-input v-model="inputModel.phone" ref="accountInput" placeholder="请输入手机号码"></el-input>
      </el-form-item>
      <el-form-item label="短信验证码" :label-width="labelWidth" class="el-form-item_btn">
        <el-input v-model="inputModel.captcha" ref="codeInput" placeholder="请输入验证码"></el-input>
        <el-button type="primary" @click="getCode" :disabled="isCodeDisabled">
          {{ codeText }}
        </el-button>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleNext" :loading="loading" v-if="active == 1">
        下一步
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading" v-if="active == 2">
        更换手机号码
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import qs from "qs";
import utils from "@/assets/js/utils";

export default {
  data() {
    return {
      labelWidth: "90px",
      inputModel: {
        account: "",
        phone: "",
        captcha: "",
        code: ""
      },
      codeText: "获取验证码", //获取验证码按钮文本
      isCodeDisabled: false, //获取验证码按钮是否可点击
      limit: 60, //获取验证码倒计时
      loading: false, //登录按钮是否显示loading
      visible: false,
      loading: false,
      active: 1,
      startInterval: ""
    };
  },
  mounted() {
    this.inputModel.account = this.$store.state.userinfo.bindPhone;
  },
  methods: {
    show: function() {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.visible = false;
      this.clearData();
    },
    /**
     * 清空数据
     */
    clearData: function() {
      this.active = 1;
      this.inputModel = {
        account: this.$store.state.userinfo.bindPhone,
        phone: "",
        captcha: "",
        code: ""
      };
      this.isCodeDisabled = false;
      this.codeText = "获取验证码";
      this.limit = 60;
    },
    /**
     * 下一步
     */
    handleNext: function() {
      const params = {
        captcha: this.inputModel.captcha
      };

      if (!this.inputModel.captcha) {
        this.$message.error("请输入验证码");
        this.$refs.codeInput.focus();
        return;
      }

      axios.post(api.sets.step2, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
      .then(response => {
          this.$message.success("验证成功！");
          this.inputModel.code = response;
          this.inputModel.captcha = "";
          this.isCodeDisabled = false;
          clearInterval(this.startInterval);
          this.codeText = "获取验证码";
          this.limit = 60;
          this.active = 2;
        });
      this.isCodeDisabled = false;
    },

    /**
     * 获取旧手机验证码
     */
    getCode: function() {
      const _this = this;
      let params = {},
        url = "";
      if (this.active == 1) {
        url = api.sets.step1; //旧手机验证码
      } else {
        url = api.sets.step3; //新手机验证码
        params = {
          phone: this.inputModel.phone
        };

        if (!this.inputModel.phone) {
          this.$message.error("请输入手机号码");
          this.$refs.accountInput.focus();
          return;
        }

        if (!utils.checkMobile(this.inputModel.phone)) {
          this.$message.error("请输入正确的手机号码");
          this.$refs.accountInput.focus();
          return;
        }

      }

      this.isCodeDisabled = true;

      axios.post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
      .then(response => {
          this.$message.success("验证码发送成功！");
          this.startInterval = setInterval(function() {
            _this.codeText = --_this.limit + "s";
            if (_this.limit == 0) {
              clearInterval(_this.startInterval);
              _this.codeText = "发送验证码";
              _this.limit = 60;
              _this.isCodeDisabled = false;
            }
          }, 1000);
        });
      this.isCodeDisabled = false;
    },
    /**
     * 更改手机号码
     */
    handleSubmit: function() {
      const params = {
        code: this.inputModel.code,
        captcha: this.inputModel.captcha,
        phone: this.inputModel.phone
      }

      if (!this.inputModel.phone) {
        this.$message.error("请输入手机号码");
        this.$refs.accountInput.focus();
        return;
      }

      if (!utils.checkMobile(this.inputModel.phone)) {
        this.$message.error("请输入正确的手机号码");
        this.$refs.accountInput.focus();
        return;
      }

      if (!this.inputModel.captcha) {
        this.$message.error("请输入验证码");
        this.$refs.codeInput.focus();
        return;
      }

      axios.post(api.sets.step4, qs.stringify(params), {
        headers: {
          Authorization: this.$store.state.token
        }
      })
      .then(response => {
        this.$message.success("登录手机号码修改成功！");

        var userinfo = JSON.parse(localStorage.getItem("userinfo"));
        userinfo.bindPhone = this.inputModel.phone;
        this.$store.dispatch("SET_USERINFO", userinfo);
        clearInterval(this.startInterval);
        this.clearData();
        this.$emit("refresh");
        this.hide();
      });
      this.isCodeDisabled = false;
    },
  }
};
</script>