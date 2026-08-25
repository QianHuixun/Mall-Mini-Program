<!-- 
@name: add.vue 
@description: 精选商户管理 -- 编辑组件
@author: hdc
@date: 2023/12/23
-->

<template lang="html">
  <update-comp :title="'编辑精选商户'" ref="updateComp" @confirm="handleConfirm"></update-comp>
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
      show(row){
        this.$refs.updateComp.show()
        this.$refs.updateComp.update(row)
      },
      /**
       * 保存数据
       */
       handleConfirm({ inputModel }) {
        const params = inputModel
        this.$refs.updateComp.loading = true;
        axios.post(api.vendor.boutiqueUpd, params)
          .then(response => {
            this.$message.success("编辑成功");
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
