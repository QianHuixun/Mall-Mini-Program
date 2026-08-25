<!-- 
@name: AccountAdd.vue 
@description: 账号设置 -- 新增组件 
@author: sx
@date: 2020/03/2
-->
<template lang="html">
  <update-comp :title="'新增管理员'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./Update.vue";
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
      const params = inputModel
      this.$refs.updateComp.loading = true;
      axios.post(api.sys.managerIns, params)
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