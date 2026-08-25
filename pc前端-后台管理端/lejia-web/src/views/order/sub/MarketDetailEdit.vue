<!--
 * @Author: zs
 * @Date: 2022-05-09 11:13:38
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-06-21 14:45:54
 * @Description: 详情 --修改组件
 * @FilePath: /lejia-web/src/views/order/sub/MarketDetailEdit.vue
-->
<template lang="html">
  <update-comp :title="'订单详情'" ref="updateComp" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./MarketDetailUpd.vue";

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
        pkey: row.pkey
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
    },
    hide: function(){
      this.$emit("refresh");
    },
  }
};
</script>