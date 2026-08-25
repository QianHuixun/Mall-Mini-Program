<!-- 
@name: MarketUpdate.vue 
@description: 市场管理--编辑模板 
@author: sx
@date: 2020/06/24
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="菜场编码" :label-width="labelWidth">
        <el-input v-model="inputModel.code" ref="codeInput" placeholder="请输入菜场编码"></el-input>
      </el-form-item>
      <el-form-item label="管理员" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.manager" ref="managerInput" placeholder="请输入管理员名称" :disabled="disabled">
        </el-input>
      </el-form-item>
      <el-form-item label="登录手机" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入登录手机" :disabled="disabled"></el-input>
      </el-form-item>
      <el-form-item label="市场类别" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.type" :disabled="isEdit" @change="handleTypeChange">
          <el-radio label="MARKET_SHOPPING_MALL">市场商城</el-radio>
          <el-radio label="VENDOR_SHOPPING_MALL">商户商城</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="商户结算方式" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.config.settlementMethod">
          <el-radio label="PURCHASE_SETTLEMENT" :disabled="inputModel.type === 'VENDOR_SHOPPING_MALL'">采购价结算</el-radio>
          <el-radio label="COMMISSION_SETTLEMENT">佣金结算</el-radio>
        </el-radio-group>
      </el-form-item>
      <div v-if="isTianJin">
      <el-form-item label="结算配置"></el-form-item>
      <el-form-item label="市场类型" required :label-width="labelWidth">
        <el-radio-group v-model="inputModel.config.isEnterprise" @change="handleChange">
          <el-radio :label="false" >自营市场</el-radio>
          <el-radio :label="true">民营市场</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="手续费承担方" required :label-width="labelWidth">
        <el-radio-group v-model="inputModel.config.commissionType">
          <el-radio label="BLOC" >集团</el-radio>
          <el-radio label="MARKET" v-if="inputModel.config.isEnterprise == true">市场</el-radio>
          <el-radio label="MERCHANT">商户</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="佣金费率" required :label-width="labelWidth" v-if="inputModel.config.isEnterprise == true">
        <el-input placeholder="请输入" v-model="inputModel.config.commissionRate"  v-on:input="(val)=>{inputModel.config.commissionRate = formatPrice(val)}">
          <template slot="append">%</template>
        </el-input>
        <div class="tips">集团抽取民营市场的佣金费率</div>
      </el-form-item>
      </div>
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
import utils from '@/assets/js/utils';
// import ImgUpload from "@/components/global/ImgUpload";

export default {
  data() {
    return {
      isTianJin: localStorage.getItem("ascription") ==  (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
      labelWidth: '120px',
      visible: false,
      loading: false,
      disabled: false,
      inputModel: {
        comPkey: '',
        name: '',
        code: '',
        manager: '',
        mobile: '',
        enabled: true,
        type: 'MARKET_SHOPPING_MALL',
        config: {
          settlementMethod: 'COMMISSION_SETTLEMENT',
          commissionType: "BLOC",
          commissionRate: "",
          isEnterprise: false
        },
      },
      photos: [],
    };
  },
  mounted() {},
  components: {
    // ImgUpload
  },
  methods: {
    //格式化价格
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
    handleTypeChange(val) {
      console.log(val);
      if(val === 'VENDOR_SHOPPING_MALL') {
        this.inputModel.config.settlementMethod = 'COMMISSION_SETTLEMENT'
      }
    },
    handleChange() {
      if(!this.inputModel.config.isEnterprise && this.inputModel.config.commissionType == 'MARKET') {
        this.inputModel.config.commissionType = "BLOC";
      }
    },
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.photos = imgUrl;
    },
    /**
     * 图片修改事件
     */
    changeImg2: function (imgUrl) {
      this.inputModel.logo = imgUrl;
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        comPkey: '',
        name: '',
        code: '',
        manager: '',
        mobile: '',
        content: '',
        enabled: true,
        type: 'MARKET_SHOPPING_MALL',
        config: {
          settlementMethod: 'COMMISSION_SETTLEMENT',
          commissionType: "BLOC",
          commissionRate: "",
          isEnterprise: false
        },
      };
      // this.photos = [];
      // this.$refs.ImgUpload.updateImg([]);
      // this.$refs.ImgUpload2.updateImg('');
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.disabled = true;
      this.inputModel = inputModel;
    },
    show: function () {
      this.visible = true;
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('hide');
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.name) {
        this.$message.error('请输入名称');
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.manager) {
        this.$message.error('请输入管理员名称');
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.mobile) {
        this.$message.error('请输入登录手机');
        this.$refs.mobileInput.focus();
        return;
      }

      if (!utils.checkMobile(this.inputModel.mobile)) {
        this.$message.error('请输入正确的登录手机');
        this.$refs.mobileInput.focus();
        return;
      }
      if (!this.inputModel.config.settlementMethod) {
        this.$message.error("请选择商户结算方式");
        return;
      }

      if(this.isTianJin) {
        if (this.inputModel.config.isEnterprise === "" || this.inputModel.config.isEnterprise === null) {
          this.$message.error("请选择市场类型");
          return;
        }
        if (!this.inputModel.config.commissionType) {
          this.$message.error("请选择手续费承担方");
          return;
        }
        if(!this.inputModel.config.isEnterprise && this.inputModel.config.commissionType == 'MARKET') {
        this.$message.error("请选择手续费承担方");
          return;
        }
        if (this.inputModel.config.isEnterprise) {
          if (!this.inputModel.config.commissionRate) {
            this.$message.error("请输入佣金费率");
            return;
          }
        }
      }

      this.$emit('confirm', {
        inputModel: this.inputModel,
      });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    isEdit: {
      type: Boolean,
      default: false,
    }
  },
};
</script>