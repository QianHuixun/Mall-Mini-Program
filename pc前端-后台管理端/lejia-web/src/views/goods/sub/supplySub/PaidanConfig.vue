<!-- 
@name: AddComp.vue 
@description: 商品供应商 -- 新增组件 
@author: crj
@date: 2021/10/09
-->
<template lang="html">
  <el-dialog title="派单配置" :visible.sync="visible" :closeOnClickModal="false" width="850px">
    <el-form>
      <el-form-item label-width="40px">
        <el-radio-group v-model="isOperation">
          <el-radio-button :label="true">统一配置</el-radio-button>
          <el-radio-button :label="false">市场自定义</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label-width="40px" v-if="isOperation">
        <el-radio-group v-model="automaticPurchase">
          <el-radio :label="false">人工指派</el-radio>
          <el-radio :label="true">系统自动派单</el-radio>
        </el-radio-group>
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
        automaticPurchase: false,
        isOperation: false
      };
    },
    components: {},
    methods: {
      /**
       * @desc 显示
       */
      show: function () {
        this.getData();
      },
       /**
       * @desc 隐藏
       */
      hide: function () {
        this.visible = false;
        this.clearData();
      },
      /**
       * @desc 清空数据
       */
      clearData() {
        this.automaticPurchase = false;
        this.isOperation = false;
      },
      /**
       * @desc 获取数据
       */
      getData() {
        axios.post(api.goods.querySupplyConfig).then(res => {
          this.automaticPurchase = res.automaticPurchase;
          this.isOperation = res.isOperation;
          this.visible = true;

        })
      },
      /**
       * @desc 提交
       */
      handleSubmit: function () {
        const params = {
            automaticPurchase: this.automaticPurchase,
            isOperation: this.isOperation,
          },
          _this = this;
        this.loading = true;
        axios.post(api.goods.updSupplyConfig, params, {
            headers: {
              "Content-Type": "application/json"
            }
          })
          .then(response => {
            this.$message.success("派单配置成功");
            this.$emit("refresh");
            this.hide();
          });
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    }
  };
</script>
<style lang="less" scoped>
  /deep/.el-dialog {
    width: 400px !important;
    min-width: 400px !important;
  }
</style>