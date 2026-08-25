<template>
  <update-comp :title="'订单详情'" ref="updateComp" @hide="hide"></update-comp>
</template>

<script>
import updateComp from "./MarketDetailUpd.vue";
export default {
  components: {
    updateComp
  },
  data() {
    return {
      visible: false,
      goodData: [],
    }
  },
  methods: {
    show: function({ row }) {
      const inputModel = {
        pkey: row.pkey
      };
      this.$refs.updateComp.show('vrify');
      setTimeout(() => {   
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
    },
    hide: function(){
      this.$emit("refresh");
    },
  }
}
</script>

<style>

</style>