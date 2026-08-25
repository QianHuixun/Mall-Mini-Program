<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" append-to-body @close="hide">
    <el-form label-width="120px">
      <el-form-item label="市场" required>
        <el-select v-model="inputModel.goodsFarmer" :disabled="isEdit" @change="handleMarketChange">
          <el-option v-for="item in marketList" :key="item.pkey" :value="item.pkey" :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品" required>
        <el-cascader v-model="inputModel.goods" :options="options" :props="props" filterable :disabled="isEdit">
        </el-cascader>
      </el-form-item>
      <el-form-item label="排序">
        <el-input v-model="inputModel.sort" placeholder="请输入排序" v-on:input="limitInput($event, 'sort')"></el-input>
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
export default {
  data() {
    return {
      visible: false,
      loading: false,
      options: [],
      props: {
        value: 'pkey',
        label: 'name',
        emitPath: false,
      },
      inputModel: {
        goodsFarmer: '',
        goods: '',
        sort: '',
      },
      marketList: [],
    };
  },
  mounted() {
    this.getMarketList()
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        goodsFarmer: '',
        goods: '',
        sort: '',
      };
      this.options = []
    },
    /**
     * 初始化数据
     */
    initData: function ({ pkey }) {
      axios.post(api.goods.recommendGet, this.$qs.stringify({ pkey }))
        .then(res => {
          console.log(res);
          this.inputModel = res
          this.getGoodsList()
        })
    },
    show: function () {
      this.visible = true;
      // this.clearData();
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
      if (!this.inputModel.goodsFarmer) {
        this.$message.error("请选择市场");
        return;
      }

      if (!this.inputModel.goods) {
        this.$message.error("请选择商品");
        return;
      }

      this.$emit("confirm", { inputModel: this.inputModel });
    },
    getMarketList() {
      const params = {
        includeAscription: true
      }
      axios.post(api.dropdown.marketDrop, this.$qs.stringify(params))
        .then(res => {
          console.log(res);
          this.marketList = res
        })
    },
    handleMarketChange(val) {
      console.log(val);
      this.getGoodsList()
    },
    getGoodsList() {
      const params = {
        farmer: this.inputModel.goodsFarmer
      }
      axios.post(api.mall.twoClassicGoods, this.$qs.stringify(params))
        .then(res => {
          console.log(res);
          this.options = res
        })
    },
    /**限制整数 */
    limitInput(val, name) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel[name] = val;
    },
  },
  props: {
    title: {
      type: String,
      default: "新增"
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  }
};
</script>
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>