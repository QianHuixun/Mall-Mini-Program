<!--
* @description 卡券活动修改
* @fileName CouponEventsEdit.vue
* @author zs
* @date 2024/04/28
!-->
<template lang="html">
  <update-comp :title="'修改卡券活动'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from './CouponEventsUpd.vue';

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
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: row });
      }, 0);
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      if(inputModel.isNoLimitDailyNum){
        params.limitDailyNum = "";
      }
      if(inputModel.isNoLimitDailyCardNum){
        params.limitDailyCardNum = "";
      }
      if(inputModel.isNoLimitDailyGiftNum){
        params.limitDailyGiftNum = "";
      }
      params.startTime = inputModel.time[0];
      params.endTime = inputModel.time[1];
      this.$delete(params, 'time')
      this.$refs.updateComp.loading = true;
      axios
        .post(api.marketing.activityUpd, params, {
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