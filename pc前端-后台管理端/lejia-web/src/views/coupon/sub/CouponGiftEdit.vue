<!--
 * @Author: 沙晓
 * @Date: 2024-04-17 15:30:56
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-19 15:10:51
 * @Description: 礼品券管理 --修改组件
 * @FilePath: /lejia-web/src/views/coupon/sub/CouponGiftEdit.vue
-->
<template lang="html">
  <update-comp :title="'修改卡券'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './CouponGiftUpdate.vue';
import qs from "qs";
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
      const params = {
        pkey: row.pkey
      };
      axios
        .post(api.marketing.giftGet, qs.stringify(params)
        )
        .then((res) => {
          this.$refs.updateComp.show();
          const inputModel= {
            pkey: res.pkey,
            title: res.title,
            count: res.count,
            content: res.content,
            picture: res.picture,
            effective: res.effective,
            startDate: res.startDate,
            endDate: res.endDate,
          }
        setTimeout(() => {

          this.$refs.updateComp.initData({ inputModel: inputModel });
        }, 0);
        });
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
        .post(api.marketing.giftUpd, params, {
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