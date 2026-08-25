<!-- 
@name: MallDetailEdit.vue 
@description: 详情 --修改组件
@author: zs
-->
<template lang="html">
  <update-comp :title="'订单详情'" ref="updateComp" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./MallDetailUpd.vue";
import qs from "qs";

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