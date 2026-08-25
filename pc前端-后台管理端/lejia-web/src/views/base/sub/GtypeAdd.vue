<template>
  <update-comp :title="'新增分类'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>

<script>
  import updateComp from './GtypeUpd.vue';
  export default {
    components: {
      updateComp
    },
    methods: {
      show() {
        this.$refs.updateComp.show();
      },
      handleUpdate({
        inputModel,
        level
      }) {
        const params = inputModel;
        this.$refs.updateComp.loading = true;
        let url = level == 1 ? api.mall.gtypeAdd : (level == 2 ? api.mall.gtypeTwoAdd : api.mall.gtypeThreeAdd);
        axios
          .post(url, params, {
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