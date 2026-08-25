<!-- 
@name: CookfdEdit.vue 
@description: 菜谱 --修改组件
@author: sx
@date: 2020/07/02
-->
<template lang="html">
  <update-comp :title="'修改菜谱'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./CookfdUpdate.vue";
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
      console.log(row)
      const inputModel = JSON.parse(JSON.stringify(row));
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
      axios.post(api.mkt_marketing.updCookfd, params, {
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