<template>
  <update-comp :title="'新增推广'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>

<script>
import updateComp from './PromoteUpdate.vue';
export default {
  components: {
    updateComp
  },
  methods: {
    show() {
      this.$refs.updateComp.show();
    },
    hide() {
      this.$emit('refresh');
    },
    handleUpdate({ inputModel }) {
      const params = inputModel,
        _this = this;

      this.$refs.updateComp.loading = true;

      axios
        .post(api.mall.promoteins, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then((response) => {
          this.$message.success('新增成功');
          this.$emit('refresh');
          this.$refs.updateComp.hide();
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  }
}
</script>

<style>

</style>