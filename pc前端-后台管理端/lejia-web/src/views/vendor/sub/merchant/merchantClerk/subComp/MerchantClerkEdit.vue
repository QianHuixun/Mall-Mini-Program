<!-- 
@name: MerchantClerkEdit.vue 
@description: 商户管理 -- 编辑店员
@author: crj
@date: 2022/2/10
-->
<template lang="html">
  <update-comp :title="'修改店员'" ref="updateComp" @confirm="handleUpdate" @hide="hide" :isEdit="true"></update-comp>
</template>
<script>
import updateComp from './MerchantClerkUpd.vue';

export default {
  data() {
    return {
      visible: false,
    };
  },
  components: {
    updateComp,
  },
  methods: {
    show: function ({ row }) {
      const inputModel = {
        pkey: row.pkey,
        mobile: row.mobile,
        vendor: row.vendor,
        name: row.name,
        farmer: row.farmer,
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
    },
    hide: function () {
      this.$refs.updateComp.hide();
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.market.updClerk, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('修改成功');
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