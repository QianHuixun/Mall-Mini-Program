<template>
  <update-comp :title="'编辑搜索词'" ref="updateComp" :type="type" @confirm="handleUpdate"></update-comp>
</template>

<script>
import updateComp from './update.vue';
export default {
  props: ['type'],
  components: {
    updateComp
  },
  methods: {
    show(row) {
      this.$refs.updateComp.show();
      this.$refs.updateComp.update(row);
    },
    handleUpdate(inputModel) {
      axios.post(api.mall.keywordAdd, inputModel)
        .then(res => {
          if(res === true) {
            this.$emit('confirm')
            this.$message.success("编辑成功!")
            this.$refs.updateComp.handleClose()
          } else {
            this.$refs.updateComp.loading = false
          }
        })
    },
  }
}
</script>

<style>

</style>