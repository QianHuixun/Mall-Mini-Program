<!-- 
@name: AccountUpdate.vue 
@description: 账号设置--编辑模板 
@author: sx
@date: 2020/04/01
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="姓名" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.nickname" ref="nameInput" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="登录手机" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入登录手机"></el-input>
      </el-form-item>
      <el-form-item label="账号角色" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.roleKey" ref="roleSelect" placeholder="请选择账号角色">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in roleList"></el-option>
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
        nickname: "",
        mobile: "",
        roleKey: "",
      },
      roleList: [] //角色列表
    };
  },
  mounted() {
    //获取角色下拉列表
    dropdown.getRole().then(result => {
      this.roleList = result.content;
    });
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        nickname: "",
        mobile: "",
        roleKey: ""
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
      if (!this.inputModel.nickname) {
        this.$message.error("请输入姓名");
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

      if (!this.inputModel.roleKey) {
        this.$message.error("请选择账号角色");
        this.$refs.roleSelect.focus();
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