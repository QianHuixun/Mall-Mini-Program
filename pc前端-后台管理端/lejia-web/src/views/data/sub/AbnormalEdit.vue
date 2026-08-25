<!-- 
@name: AbnormalEdit.vue 
@description: 异常货物 --修改组件
@author: crj
@date: 2020/08/14
-->
<template lang="html">
  <update-comp :title="'交易异常设置'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./AbnormalUpd.vue";
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
  methods: {
    show: function(row) {
      const inputModel = row;
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData(inputModel);
      }, 0);
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

      axios
        .post(api.market.updMarket, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.$message.success("修改成功");
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