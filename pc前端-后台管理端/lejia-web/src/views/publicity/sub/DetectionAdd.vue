<!-- 
@name: DetectionAdd.vue 
@description: 检测信息 -- 新增组件 
@author: sx
@date: 2020/07/06
-->
<template lang="html">
  <update-comp :title="'新增检测信息'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./DetectionUpdate.vue";
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
      axios.post(api.market.insDetection, params, {
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