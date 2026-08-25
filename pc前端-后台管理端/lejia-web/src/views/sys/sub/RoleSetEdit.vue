<!-- 
@name: RoleSetEdit.vue 
@description: 角色设置 --修改组件
@author: sx
@date: 2020/06/20
-->
<template lang="html">
  <update-comp :title="'修改角色权限'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./RoleSetUpdate.vue";
import qs from "qs";

export default {
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show: function({ row }) {
      const inputModel = {
        pkey: row.pkey
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
      const param = inputModel;
 
      axios.post(api.sys.setRole, param, {
        headers: {
          Authorization: this.$store.state.token,
          "Content-Type": "application/json;charset=UTF-8"
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