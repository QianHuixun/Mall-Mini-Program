<!-- 
@name: MarketAdd.vue 
@description: 市场管理 -- 新增组件 
@author: sx
@date: 2020/06/26
-->
<template lang="html">
  <update-comp :title="'新增市场'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./MarketUpdate.vue";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show: function({ pkey }) {
      this.$nextTick(() => {
        setTimeout(() => {
          this.$refs.updateComp.show();
        }, 0);
      })

      this.$refs.updateComp.inputModel.comPkey = pkey;
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios.post(api.market.insMarket, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(() => {
          this.$message.success("新增成功");
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