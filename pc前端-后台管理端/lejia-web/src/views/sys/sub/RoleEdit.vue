<!-- 
@name: RoleEdit.vue 
@description: 角色设置 --修改组件
@author: sx
@date: 2020/03/25
-->
<template lang="html">
  <update-comp :title="'修改角色'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./RoleUpdate.vue";
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
        pkey: row.pkey,
        description: row.description,
        name: row.name
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
      this.loading = true;
      axios.post(api.sys.updRole, qs.stringify(params), {
        headers: {
          Authorization: this.$store.state.token,
          // "Content-Type": "application/json"
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