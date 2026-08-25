<!-- 
@name: MerchantClerkAdd.vue 
@description: 商户管理 -- 新增店员
@author: crj
@date: 2022/2/10
-->

<template lang="html">
  <update-comp :title="'新增店员'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './MerchantClerkUpd.vue';

export default {
  data() {
    return {};
  },
  components: {
    updateComp,
  },
  methods: {
    show: function () {
      this.$refs.updateComp.show();
    },
    hide: function () {
      this.$refs.updateComp.hide();
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel,
        _this = this;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.market.insClerk, params, {
          headers: {
            Authorization: this.$store.state.token,
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
  },
};
</script>