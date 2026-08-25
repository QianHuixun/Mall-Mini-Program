<template>
  <el-dialog title="银行卡信息" width="500px" :before-close="hide" :visible="visible">
    <el-form  :label-width="labelWidth">
      <el-form-item label="银行账号" :required="true">
        <el-input
            placeholder="请输入银行账号"
            v-model="inputModel.pan"
            clearable></el-input>
      </el-form-item>
      <el-form-item label="开户银行联行号"  :required="true" >
        <el-input
            placeholder="请输入开户银行联行号"
            v-model="inputModel.panNum"></el-input>
      </el-form-item>
      <el-form-item label="银行预留手机号"  :required="true" >
        <el-input
            placeholder="请输入银行预留手机号"
            v-model="inputModel.bankPhone"></el-input>
      </el-form-item>
      <el-form-item label="银行账号类型" :required="inputModel.userType == 1 ? false :true">
        <el-select
          v-model="inputModel.acctType"
          placeholder="请选择"
          clearable
          v-if="inputModel.userType == 1"
        >
          <el-option
            :value="item.pkey"
            :key="item.pkey"
            :label="item.name"
            v-for="item in acctTypePersonalList"
          ></el-option>
          </el-select>
          <el-select
          v-model="inputModel.acctType"
          placeholder="请选择"
          clearable
          v-else
        >
          <el-option
            :value="item.pkey"
            :key="item.pkey"
            :label="item.name"
            v-for="item in acctTypeOtherList"
          ></el-option>
          </el-select>
      </el-form-item>
      </el-form>
    <span slot="footer" class="dialog-footer">
    <el-button @click="hide">取 消</el-button>
    <el-button  :loading="loading" type="primary" @click="submit">确 定</el-button>
  </span>
  </el-dialog>
</template>

<script>
import qs from "qs";
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      labelWidth: "150px",
      loading: false,
      visible: false,
      inputModel: {
        pkey: "",
        pan: "",
        panNum: "",
        bankPhone: "",
        acctType: "",
        authProtocolVersion: ""
      },
      acctTypePersonalList:[
        {pkey:"5",name: "中信个人存折"},
        // {pkey:"6",name: "他行个人存折"},
      ],
      acctTypeOtherList:[
        {pkey:"1",name: "中信个人账户"},
        {pkey:"2",name: "中信企业账户"},
        // {pkey:"3",name: "他行个人账户"},
        // {pkey:"4",name: "他行企业账户"},
      ],
    }
  },
  methods: {
    show: function( pkey = "") {
      this.getData(pkey);
      this.visible = true;
    },
    getData(pkey) {
      const params = {
          pkey: pkey
        }
      axios
        .post(api.market.financeBankInfoGet, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.inputModel = response;
        });
    },
    hide: function() {
      this.visible = false;
      this.inputModel= {
        pkey: "",
        pan: "",
        panNum: "",
        bankPhone: "",
        acctType: "",
        authProtocolVersion: ""
      }
    },
    submit: function() {
      if(!this.inputModel.pan) {
        this.$message.warning("请输入银行账号	！");
        return;
      }
      if(!this.inputModel.panNum) {
        this.$message.warning("请输入开户银行联行号！");
        return;
      }
      if(!this.inputModel.bankPhone) {
        this.$message.warning("请输入银行预留手机号！");
        return;
      }
      if (!utils.checkMobile(this.inputModel.bankPhone)) {
          this.$message.error("请输入正确的手机号");
          return;
        }
      if(!this.inputModel.acctType && this.inputModel.userType != 1) {
        this.$message.warning("请选择银行账户类型！");
        return;
      }

      const param = this.inputModel;
      this.loading = true;
      axios.post(api.market.financeBankInfoUpd, param, {
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