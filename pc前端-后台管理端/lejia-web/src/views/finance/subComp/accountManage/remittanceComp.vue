<template>
  <el-dialog title="划账" width="500px" :before-close="hide" :visible="visible">
    <el-form  :label-width="labelWidth">
      <el-form-item label="划账到" :required="true">
        <el-select
          v-model="inputModel.pkey"
          placeholder="请选择"
          clearable
        >
          <el-option
            :value="item.pkey"
            :key="index"
            :label="item.name"
            v-for="(item, index) in userList"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="划账金额" :required="true">
        <el-input
            placeholder="请输入划账金额"
            v-model="inputModel.amt"
            clearable></el-input>
            <div class='tips'>用于优惠券活动结束后，预充值金额划出</div>
      </el-form-item>
      <el-form-item label="备注"  :required="true" >
        <el-input
            placeholder="请输入备注"
            v-model="inputModel.remark"></el-input>
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
export default {
  data() {
    return {
      labelWidth: "80px",
      loading: false,
      visible: false,
      inputModel: {
        pkey: "",
        amt: "",
        remark: ""
      },
      userList:[],
    }
  },
  methods: {
    show: function() {
      this.visible = true;
      this.getList();
    },
    getList: function() {
      axios
        .post(api.market.financeUserDrop, qs.stringify({}), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.userList = response;
        });
    },
    hide: function() {
      this.visible = false;
      this.inputModel = {
        pkey: "",
        amt: "",
        remark: ""
      }
    },
    submit: function() {
      if(!this.inputModel.pkey) {
        this.$message.warning("请选择划账到！");
        return;
      }
      if(!this.inputModel.amt) {
        this.$message.warning("请输入提现金额！");
        return;
      }
      if(!this.inputModel.remark) {
        this.$message.warning("请输入备注！");
        return;
      }
      const param = this.inputModel;
      this.loading = true;
      axios.post(api.market.financeUserAllocation, qs.stringify(param), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success("划账成功！");
          this.loading = false;
          this.hide();
        });
    }
  }
}
</script>