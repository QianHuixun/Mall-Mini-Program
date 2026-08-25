<!--
 * @Author: 沙晓
 * @Date: 2025-06-12 15:30:51
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-17 15:30:13
 * @Description: 账户信息
 * @FilePath: /lejia-web/src/views/finance/subComp/accountManage/accountInfoComp.vue
-->
<template>
   <el-dialog title="账户信息" width="500px" :before-close="hide" :visible="visible">
    <el-form  :label-width="labelWidth">
      <el-form-item label="用户类型" :required="true">
        <el-radio-group v-model="inputModel.userType" :disabled="!isEdit" @change="handleChange">
        <el-radio label="1">个人</el-radio>
        <el-radio label="2">企业</el-radio>
        <el-radio label="3">个体工商户</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="用户名称" :required="true" >
        <el-input
            placeholder="请输入用户名称"
            v-model="inputModel.userNm"
            clearable  :disabled="!isEdit"></el-input>
      </el-form-item>
      <el-form-item label="手机号"  :required="true" >
        <el-input
            placeholder="请输入手机号"
            v-model="inputModel.userPhone"  :disabled="!isEdit"></el-input>
      </el-form-item>
      <el-form-item label="证件类型" :required="true">
        <el-select
          v-model="inputModel.userIdType"
          placeholder="请选择"
          clearable v-if="inputModel.userType =='1'"  :disabled="!isEdit"
        >
          <el-option
            :value="item.pkey"
            :key="item.pkey"
            :label="item.name"
            v-for="item in cardTypeByPersonalList"
          ></el-option>
          </el-select>
          <el-select
          v-else
          v-model="inputModel.userIdType"
          placeholder="请选择"
          clearable  :disabled="!isEdit"
        >
          <el-option
            :value="item.pkey"
            :key="item.pkey"
            :label="item.name"
            v-for="item in cardTypeByOtherList"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="证件号码"  :required="true" >
        <el-input
            placeholder="请输入证件号码"
            v-model="inputModel.userIdNo"  :disabled="!isEdit"></el-input>
      </el-form-item>
      <div v-if="inputModel.userType != 1">
      <el-form-item label="法人姓名"  :required="true" >
        <el-input
            placeholder="请输入法人姓名"
            v-model="inputModel.corpNm"  :disabled="!isEdit"></el-input>
      </el-form-item>
      <el-form-item label="法人证件类型" :required="true">
        <el-select
          v-model="inputModel.corpIdType"
          placeholder="请选择"
          clearable  :disabled="!isEdit"
        >
          <el-option
            :value="item.pkey"
            :key="item.pkey"
            :label="item.name"
            v-for="item in cardTypeByPersonalList"
          ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="法人证件号码"  :required="true" >
        <el-input
            placeholder="请输入法人证件号码"
            v-model="inputModel.corpIdNo"  :disabled="!isEdit"></el-input>
      </el-form-item>
      </div>
      </el-form>
    <span slot="footer" class="dialog-footer">
    <el-button @click="hide">取 消</el-button>
    <el-button  :loading="loading" type="primary" @click="submit" v-if="isEdit">确 定</el-button>
  </span>
  </el-dialog>
</template>

<script>
import qs from "qs";
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      isEdit: true,
      labelWidth: "120px",
      loading: false,
      visible: false,
      inputModel: {
        pkey: "",
        userType: "1",
        userNm: "",
        userPhone: "",
        userIdType: "",
        userIdNo: "",
        corpNm: "",
        corpIdType: "",
        corpIdNo: ""
      },
      cardTypeByPersonalList:[
        {pkey:"01",name: "个人身份证"},
        {pkey:"22",name: "户口簿"},
        {pkey:"23",name: "外国护照"},
        {pkey:"25",name: "军人军官证"},
        {pkey:"26",name: "军人士兵证"},
        {pkey:"27",name: "武警军官证"},
        {pkey:"28",name: "港澳居民往来内地通行证（香港）"},
        {pkey:"29",name: "台湾居民往来大陆通行证"},
        {pkey:"30",name: "临时居民身份证"},
        {pkey:"31",name: "外国人永久居留证"},
        {pkey:"32",name: "中国护照"},
        {pkey:"33",name: "武警士兵证"},
        {pkey:"34",name: "港澳居民往来内地通行证（澳门）"},
        {pkey:"35",name: "边民出入境通行证"},
        {pkey:"36",name: "台湾居民旅行证"},
        {pkey:"37",name: "港澳居民居住证（香港）"},
        {pkey:"38",name: "港澳居民居住证（澳门）"},
        {pkey:"39",name: "台湾居民居住证"},
      ],
      cardTypeByOtherList:[
        {pkey:"02",name: "组织机构代码"},
        {pkey:"03",name: "统一社会信用代码"},
        {pkey:"04",name: "民办非企业登记证书"},
        {pkey:"05",name: "社会团体法人登记证书"},
        {pkey:"06",name: "事业单位法人登记证"},
        {pkey:"07",name: "营业执照号码"},
        {pkey:"08",name: "其他单位证件"},
      ]
    }
  },
  methods: {
    handleChange: function() {
      this.inputModel.userIdType = "";
    },
    show: function( pkey = "") {
        this.getData(pkey);
      this.visible = true;
    },
    getData(pkey) {
      const params = {
          pkey: pkey
        }
      axios
        .post(api.market.financeUserInfoGet, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.inputModel = response;
          if(response.registered) {
            this.isEdit = false;
          } else {
            this.isEdit = true;
            this.inputModel.userType = "1";
          }
        });
    },
    hide: function() {
      this.visible = false;
      this.inputModel= {
        pkey: "",
        userType: "1",
        userNm: "",
        userPhone: "",
        userIdType: "",
        userIdNo: "",
        corpNm: "",
        corpIdType: "",
        corpIdNo: ""
      }
    },
    submit: function() {
      if(!this.inputModel.userType) {
        this.$message.warning("请选择用户类型！");
        return;
      }
      if(!this.inputModel.userNm) {
        this.$message.warning("请输入用户名称！");
        return;
      }
      if(!this.inputModel.userPhone) {
        this.$message.warning("请输入手机号！");
        return;
      }
      if (!utils.checkMobile(this.inputModel.userPhone)) {
          this.$message.error("请输入正确的手机号");
          return;
        }
      if(!this.inputModel.userIdType) {
        this.$message.warning("请选择证件类型！");
        return;
      }
      if(!this.inputModel.userIdNo) {
        this.$message.warning("请输入证件号码！");
        return;
      }
      if(this.inputModel.userType != 1) {
        if(!this.inputModel.corpNm) {
        this.$message.warning("请输入法人姓名！");
        return;
      }
      if(!this.inputModel.corpIdType) {
        this.$message.warning("请输入法人证件类型！");
        return;
      }
      if(!this.inputModel.corpIdNo) {
        this.$message.warning("请输入法人证件号码！");
        return;
      }
      }
      const param = this.inputModel;
      this.loading = true;
      axios.post(api.market.financeUserInfoUpd, param, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success("编辑成功！");
          this.$emit("refresh");
          this.loading = false;
          this.hide();
        }).catch(()=>{
          this.loading = false;
        });
    }
  }
}
</script>