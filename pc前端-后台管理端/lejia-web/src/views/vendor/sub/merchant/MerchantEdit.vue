<!-- 
@name: MerchantEdit.vue 
@description: 积分商户管理 --修改组件
@author: crj
@url: /vendor/merchant/edit
@date: 2021/10/18
-->
<template lang="html">
  <update-comp :title="'修改商户'" ref="updateComp" @confirm="handleUpdate" :isEdit="true"></update-comp>
</template>
<script>
  import updateComp from "./MerchantUpdate.vue";
  import qs from "qs";

  export default {
    data() {
      return {
        visible: false
      };
    },
    components: {
      updateComp
    },
    mounted() {},
    methods: {

      /**
       * 保存数据
       */
      handleUpdate: function ({
        inputModel
      }) {
        const params = inputModel;
        this.$refs.updateComp.loading = true;
        axios.post(api.market.updMerchant, params)
          .then(response => {
            this.$message.success("修改成功");
            this.$refs.updateComp.clearData();
            if (this.$store.state.userIdentity == 1) {
              this.$router.push('/vendor/marketMerchant');
            } else {
              this.$router.push('/vendor/merchant');
            }
          });
        setTimeout(() => {
          this.$refs.updateComp.loading = false;
        }, 300);
      }
    }
  };
</script>