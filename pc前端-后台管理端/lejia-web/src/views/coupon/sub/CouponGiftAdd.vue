<!--
 * @Author: 沙晓
 * @Date: 2024-04-17 15:30:56
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-23 13:44:16
 * @Description: 礼品券管理 -- 新增组件
 * @FilePath: /lejia-web/src/views/coupon/sub/CouponGiftAdd.vue
-->
<template lang="html">
  <update-comp :title="'新增礼品券'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./CouponGiftUpdate.vue";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show: function() {
      this.$nextTick(() => {
        setTimeout(() => {
          this.$refs.updateComp.show();
        }, 0);
      })
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios.post(api.marketing.giftIns, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(() => {
          this.$message.success("新增成功");
          this.$emit("refresh");
          this.$refs.updateComp.hide();
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    }
  }
};
</script>