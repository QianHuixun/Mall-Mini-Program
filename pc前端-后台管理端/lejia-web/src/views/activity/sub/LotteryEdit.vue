<!-- 
@name: LotteryEdit.vue 
@description: 抽奖活动配置 --修改组件
@author: sx
@date: 2020/07/07
-->
<template lang="html">
  <update-comp :title="'修改配置'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./LotteryUpdate.vue";
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
    show: function({ row }) {
      const inputModel = {
        pkey: row.pkey,
        descp: row.descp,
        photo: row.photo,
        name: row.name,
        pvalue: row.pvalue,
        probability: row.probability,
        ptype: row.ptype
      };
      this.$refs.updateComp.show();
      setTimeout(() => {   
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
    },
    hide: function(){
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios.post(api.marketing.updLottery, params, {
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