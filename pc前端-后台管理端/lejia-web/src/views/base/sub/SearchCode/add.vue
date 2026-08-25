<template>
  <update-comp :title="'新增搜索词'" ref="updateComp" :type="type" @confirm="handleUpdate"></update-comp>
</template>

<script>
import updateComp from './update.vue';
export default {
  props: ['type'],
  components: {
    updateComp
  },
  methods: {
    show() {
      this.$refs.updateComp.show();
    },
    handleUpdate(inputModel) {
      axios.post(api.mall.keywordAdd, inputModel)
        .then(res => {
          if(res === true) {
            this.$emit('confirm')
            this.$message.success("新增成功!")
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