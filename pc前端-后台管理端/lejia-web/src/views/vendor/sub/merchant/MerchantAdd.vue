<!-- 
@name: MerchantAdd.vue 
@description: 积分商户管理 -- 新增组件
@author: crj
@url: /vendor/merchant/edit
@date: 2021/10/18
-->

<template lang="html">
  <update-comp :title="'新增商户'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
  import updateComp from "./MerchantUpdate.vue";
  import qs from "qs";

  export default {
    data() {
      return {};
    },
    components: {
      updateComp
    },
    methods: {
      /**
       * 保存数据
       */
      handleUpdate: function ({
        inputModel
      }) {
        const params = inputModel,
          _this = this;
        this.$refs.updateComp.loading = true;
        axios.post(api.market.insMerchant, params, {
            headers: {
              "Content-Type": "application/json"
            }
          })
          .then(response => {
            this.$message.success("新增成功");
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