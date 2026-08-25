<template lang="html">
  <update-comp :title="'新增'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from "./upd.vue";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show() {
      this.$refs.updateComp.show();
    },
    /**
     * 保存数据
     */
    handleUpdate({ inputModel }) {
      const params = inputModel
      this.$refs.updateComp.loading = true;
      axios.post(api.goods.recommendAdd, params)
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