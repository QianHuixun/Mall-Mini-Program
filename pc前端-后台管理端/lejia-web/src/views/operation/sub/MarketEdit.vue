<!--
 * @Author: 沙晓
 * @Date: 2024-01-25 14:52:29
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-16 11:09:59
 * @Description: file content
 * @FilePath: /lejia-web/src/views/operation/sub/MarketEdit.vue
-->
<!-- 
@name: MarketEdit.vue 
@description: 市场管理 --修改组件
@author: sx
@date: 2020/06/23
-->
<template lang="html">
  <update-comp :title="'修改市场'" ref="updateComp" @confirm="handleUpdate" @hide="hide" :isEdit="true"></update-comp>
</template>
<script>
import updateComp from "./MarketUpdate.vue";

export default {
  data() {
    return {
      visible: false
    };
  },
  components: {
    updateComp
  },
  methods: {
    show: function({ row }) {
      const inputModel = {
        pkey: row.pkey,
        comPkey: row.comPkey,
        name: row.name,
        code: row.code,
        manager: row.manager,
        mobile: row.mobile,
        type: row.type,
        config:row.config,
        enabled: row.enabled,
      };
      console.log("market",inputModel)
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
        this.$refs.updateComp.disabled = true;
      }, 0);
    },
    hide: function(){
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      console.log(inputModel);
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios.post(api.market.updMarket, params, {
        headers: {
          Authorization: this.$store.state.token,
          "Content-Type": "application/json"
        }
      })
      .then(() => {
        this.$message.success("修改成功");
        this.$emit("refresh");
        this.$refs.updateComp.hide();
      });
      setTimeout(() => {
          this.$refs.updateComp.loading = false;
        }, 300);
    }
  }
};
</script>