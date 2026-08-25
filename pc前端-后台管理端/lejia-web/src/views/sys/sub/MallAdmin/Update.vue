<!-- 
@name: Update.vue 
@description: 商城管理员--编辑模板 
@author: hdc
@date: 2025/01/09
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="登录手机号" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入登录手机号"></el-input>
      </el-form-item>
      <el-form-item label="账号角色" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.roles" ref="roleSelect" multiple placeholder="请选择账号角色">
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
        pkey: "",
        mobile: "",
        roles: [],
      },
      roleList: [
        {pkey: 'COUPON_MANAGER', name: '卡券管理'},
        {pkey: 'ORDER_WRITE_OFF', name: '订单核销'},
      ] //角色列表
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
        pkey: "",
        mobile: "",
        roles: [],
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

      if (!this.inputModel.mobile) {
        this.$message.error("请输入登录手机号");
        this.$refs.mobileInput.focus();
        return;
      }

      if (!utils.checkMobile(this.inputModel.mobile)) {
        this.$message.error("请输入正确的登录手机号");
        this.$refs.mobileInput.focus();
        return;
      }

      if (!this.inputModel.roles || !this.inputModel.roles.length) {
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