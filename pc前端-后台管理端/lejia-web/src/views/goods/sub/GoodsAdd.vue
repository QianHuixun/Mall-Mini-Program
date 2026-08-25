<!-- 
@name: GoodsAdd.vue 
@description: 商品维护 -- 新增组件 
@author: sx
@date: 2020/06/30
-->
<template lang="html">
  <update-comp :title="'新增商品'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './GoodsUpdate.vue';

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
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      let url = api.goods.insGoods;
      if (inputModel.mtype == 'COUPON_GOODS') {
        url = api.mall.insGoodsCoupon;
      }
      axios
        .post(url, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
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