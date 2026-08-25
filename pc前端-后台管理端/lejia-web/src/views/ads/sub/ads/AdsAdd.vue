<!-- 
@name: AdsAdd.vue 
@description: 弹窗广告-新增组件
@author: crj
@date: 2020/09/22
-->
<template lang="html">
  <update-comp :title="'新增弹窗广告'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./AdsUpd.vue";
import qs from "qs";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show: function() {
      this.$nextTick(() => {
        setTimeout(() => {
          this.$refs.updateComp.show();
        }, 0);
      })
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel,
        _this = this;
      this.$refs.updateComp.loading = true;
      console.log(params)
      axios.post(api.popup.insPopAds, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
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