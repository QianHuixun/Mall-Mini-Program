<template lang="html">
  <update-comp :title="'新增供应商'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from "./supplierManagerUpdate.vue";

export default {
  data() {
    return {};
  },
  components: {
    updateComp,
  },
  methods: {
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.vendor.supplierIns, params, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          this.$message.success("新增成功");
          this.$router.push({
            path: "/vendor/supplierManager",
          });
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  },
};
</script>