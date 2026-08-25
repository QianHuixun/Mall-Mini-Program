<!-- 
@name: MerchantClerkUpd.vue 
@description: 商户管理 -- 修改店员组件
@author: crj
@date: 2022/2/10
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" append-to-body @close="hide">
    <el-form>
      <el-form-item label="市场" :label-width="labelWidth" :required="true"  v-if="$store.state.userIdentity==1" > 
        <el-select v-model="inputModel.farmer" ref="farmerInput"   placeholder="请选择市场"  filterable @change="marketChange">
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商户" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.vendor" ref="vendorInput"   placeholder="请选择商户"  filterable :disabled="$store.state.userIdentity==1&&!inputModel.farmer">
          <el-option v-for="(item,index) in vendorList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="店员名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入店员名称" maxlength="12"
         v-on:input="(val)=>{val =val.replace(/\s+/g,'');inputModel.name =val;}"></el-input>
      </el-form-item>
      <el-form-item label="手机号码" :label-width="labelWidth" :required="true"   >
        <el-input v-model="inputModel.mobile" ref="mobileInput" placeholder="请输入手机号码"  maxlength="11"
         v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.mobile =val;}" :disabled="isEdit"></el-input>
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
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';

export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        pkey: '',
        mobile: '',
        vendor: '',
        name: '',
        farmer: '',
      },
      marketList: [],
      vendorList: [],
      isEdit: false,
    };
  },
  mounted() {},
  methods: {
    marketChange() {
      this.inputModel.vendor = '';
      this.getMerData();
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      axios.post(api.dropdown.newMarketList).then((res) => {
        this.marketList = res;
      });
    },
    /**
     * @desc 获取商户数据
     */
    getMerData() {
      let params = {
        farmer: this.inputModel.farmer,
      };
      axios
        .post(api.data.queryMerchant, this.$qs.stringify(params))
        .then((response) => {
          this.vendorList = response;
        });
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        pkey: '',
        mobile: '',
        vendor: '',
        name: '',
        farmer: '',
      };
      this.isEdit = false;
      this.marketList = [];
      this.vendorList = [];
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      this.getMerData();
      this.isEdit = true;
    },
    show: function () {
      this.visible = true;
      if (this.$store.state.userIdentity == 1) this.getMarketData();
      if (!this.isEdit) this.getMerData();
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
      if (this.$store.state.userIdentity == 1 && !this.inputModel.farmer) {
        this.$message.error('请选择市场');
        this.$refs.farmerInput.focus();
        return;
      }
      if (!this.inputModel.vendor) {
        this.$message.error('请选择商户');
        this.$refs.vendorInput.focus();
        return;
      }
      if (!this.inputModel.name) {
        this.$message.error('请输入店员名称');
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.mobile) {
        this.$message.error('请输入手机号码');
        this.$refs.mobileInput.focus();
        return;
      }

      if (!utils.checkMobile(this.inputModel.mobile)) {
        this.$message.error('请输入正确的手机号码');
        this.$refs.mobileInput.focus();
        return;
      }

      this.$emit('confirm', { inputModel: this.inputModel });
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
    },
  },
};
</script>
<style lang="less" scoped>
/deep/.el-dialog {
  width: 500px !important;
}
.el-dialog__wrapper .el-dialog .el-dialog__body .el-form {
  overflow: hidden;
}
</style>