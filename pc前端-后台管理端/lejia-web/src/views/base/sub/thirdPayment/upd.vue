<!-- 
@name: upd.vue 
@description: 第三方支付渠道--编辑模板 
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="saas市场" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.farmer" ref="farmer" placeholder="saas市场">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="云农贸市场" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.market" ref="market" filterable placeholder="云农贸市场">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in thridMarketList"></el-option>
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
import dropdown from "@/assets/js/dropdown";
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        farmer: '',
        market: '',
      },
      marketList: [],
      thridMarketList: [],
    };
  },
  mounted() {
    dropdown.getNewMarketList().then(result => {
      this.marketList = result || [];
    });
    dropdown.getThridMarketList().then(result => {
      let res = []
      for (let key in result) {
        res.push({
          pkey: parseInt(key),
          name: result[key]
        })
      }
      this.thridMarketList = res
      console.log(res);
    });
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        farmer: '',
        market: '',
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
    },
    show: function () {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit("hide");
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.farmer) {
        this.$message.error("请选择saas市场");
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.market) {
        this.$message.error("请选择云农贸市场");
        this.$refs.gtypeSelect.focus();
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
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>