<!--
 * @Author: 沙晓
 * @Date: 2026-07-02 14:10:49
 * @LastEditors: 沙晓
 * @LastEditTime: 2026-07-02 14:46:05
 * @Description: file content
 * @FilePath: /lejia-web/src/views/member/MSD/configDialog.vue
-->
<template lang="html">
  <el-dialog title="热力豆配置" width="300px" center :visible.sync="visible" :closeOnClickModal="false" @close="hide">
    <el-form>
      <el-form-item>
        允许市场商品使用热力豆支付
        <el-switch v-model="configData.farmerGoods" @change="handleSubmit"></el-switch>
      </el-form-item>
      <el-form-item >
        允许自营、滨农、预售使用热力豆支付
        <el-switch v-model="configData.sysGoods"  @change="handleSubmit"></el-switch>
      </el-form-item>
      <div class="tips">开启后，热力豆消费资金，需提前在中心结算平台做预充值！</div>

    </el-form>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      loading: false,
      configData: {
        farmerGoods: false,
        sysGoods: false
      },
    };
  },
  mounted() {},
  components: {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.configData = {
        farmerGoods: false,
        sysGoods: false
      };
    },
    /**
     * 初始化数据
     */
    getData: function () {
      axios
        .post(api.marketing.MsdConfigGet)
        .then((response) => {
          this.configData = response
        });
    },
    show: function () {
      this.visible = true;
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.disabled = false;
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      const params = this.configData;
      axios.post(api.marketing.msdConfigSet, params)
        .then(() => {
          this.$message.success('配置成功！');
        })
        .finally(() => {
          this.loading = false;
        })
    },
  },
};
</script>
<style scoped>
.tips {
  color: rgb(153, 153, 153);
  margin-bottom: 20px;
}
</style>