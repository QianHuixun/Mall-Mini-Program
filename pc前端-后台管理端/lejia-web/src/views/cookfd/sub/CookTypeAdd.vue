<!-- 
@name: CookTypeAdd.vue 
@description: 菜谱分类 -- 新增组件 
@author: crj
@date: 2020/08/12
-->
<template lang="html">
  <update-comp :title="'新增菜谱'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./CookTypeUpd.vue";
import qs from "qs";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show: function() {
      this.$refs.updateComp.show();
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel,
        _this = this;
      this.$refs.updateComp.loading = true;
      axios.post(api.cookfd.insCookType, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          }
        })
        .then(response => {
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