<!-- 
@name: PasswordUpd.vue 
@description: 修改密码模板 
@author: crj
@date: 2020/08/07
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="30%">
    <el-form>
      <el-form-item label="原密码" :label-width="labelWidth" :required="true">
        <el-input type="password" v-model="inputModel.oldpassword" ref="oldPasswordInput" placeholder="请输入原密码" :show-password="true"></el-input>
      </el-form-item>
      <el-form-item label="新密码" :label-width="labelWidth"  :required="true" :show-message="true" :inline-message="true">
        <el-input type="password" v-model="inputModel.newpassword" ref="newPasswordInput" :minlength="6" :maxlength="16" :show-password="true"  placeholder="请输入新密码"></el-input>
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
import qs from "qs";
import utils from "@/assets/js/utils";
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        newpassword: "",
        oldpassword: ""
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
        newpassword: "",
        oldpassword: ""
      };
    },

    show: function() {
      this.visible = true;
      this.clearData();
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
      if (this.inputModel.oldpassword === "") {
        this.$message.error("请输入原密码");
        return;
      }
      if (this.inputModel.newpassword === "") {
        this.$message.error("请输入新密码");
        return;
      }
      if (!utils.checkPassword(this.inputModel.newpassword)) {
        this.$message.error("请输入6-20个包含字母、数字或下划线的新密码");
        return;
      }
      if (this.inputModel.newpassword == this.inputModel.oldpassword) {
        this.$message.error("请输入两个不同的密码");
        return;
      }

      let params = this.inputModel;

      axios
        .post(api.login.modeifypwd, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          // localStorage.clear();
          console.log(response);
          this.$message.success("修改成功,请重新登录");
          this.loading = false;
          // this.$router.push("/login");
          this.hide();
        });
    }
   
  },

  props: {
    title: {
      type: String,
      default: "修改密码"
    }
  }
};
</script>