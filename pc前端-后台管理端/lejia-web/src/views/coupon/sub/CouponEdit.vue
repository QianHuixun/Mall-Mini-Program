<!--
 * @Author: 沙晓
 * @Date: 2024-08-01 09:47:04
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-08-15 15:17:03
 * @Description: file content
 * @FilePath: /lejia-web/src/views/coupon/sub/CouponEdit.vue
-->
<!-- 
@name: LotteryEdit.vue 
@description: 卡券管理 --修改组件
@author: sx
@date: 2020/07/08
-->
<template lang="html">
  <update-comp :title="'修改优惠券'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './CouponUpdate.vue';

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
        title: row.title,
        type: row.type,
        count: row.count,
        cost: row.cost,
        limitCost: row.limitCost,
        cardType: row.cardType,
        userFarmer: row.userFarmer,
        userGoodsList: row.userGoodsList,
        userType: row.userType,
        userMtype: row.userMtype,
        effective: row.effective,
        endDate: row.endDate,
        startDate: row.startDate,
        expireChoose: row.expireChoose,
        userOrderType: row.userOrderType,
        avoidPostage: row.avoidPostage,
        tagKeys: row.tagKeys,
        visibleRange:  row.visibleRange
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
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
      axios
        .post(api.marketing.updCoupon, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
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