<!--
* @description 卡券新增
* @fileName CouponEventsAdd.vue
* @author zs
* @date 2024/04/26
!-->
<template lang="html">
  <update-comp :title="'新增活动卡券'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from "./CouponEventsUpd.vue";

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

    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
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
      axios.post(api.marketing.activityAdd, params, {
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