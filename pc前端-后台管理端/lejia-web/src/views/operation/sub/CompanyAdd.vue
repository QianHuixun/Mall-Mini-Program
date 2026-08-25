<!-- 
@name: CompanyAdd.vue 
@description: 公司管理 -- 新增组件 
@author: sx
@date: 2020/06/26
-->
<template lang="html">
  <update-comp :title="'新增公司'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./CompanyUpdate.vue";
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
      this.$refs.updateComp.show();
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
      axios.post(api.market.insCom, params, {
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