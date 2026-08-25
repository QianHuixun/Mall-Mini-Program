<!-- 
@name: add.vue 
@description: 精选商户管理 -- 新增组件
@author: hdc
@date: 2023/12/23
-->

<template lang="html">
  <update-comp :title="'新增精选商户'" ref="updateComp" @confirm="handleConfirm"></update-comp>
</template>
<script>
  import updateComp from "./update.vue";

  export default {
    data() {
      return {};
    },
    components: {
      updateComp
    },
    methods: {
      show(){
        this.$refs.updateComp.show()
      },
      /**
       * 保存数据
       */
       handleConfirm({ inputModel }) {
        const params = inputModel
        this.$refs.updateComp.loading = true;
        axios.post(api.vendor.boutiqueAdd, params)
          .then(response => {
            this.$message.success("新增成功");
            this.$refs.updateComp.handleCancel();
            this.$emit('confirm')
          });
        setTimeout(() => {
          this.$refs.updateComp.loading = false;
        }, 300);
      }
    }
  };
</script>
