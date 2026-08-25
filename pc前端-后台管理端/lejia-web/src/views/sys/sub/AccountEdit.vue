<!-- 
@name: AccountEdit.vue 
@description: 网点设置 --修改组件
@author: sx
@date: 2020/04/01
-->
<template lang="html">
  <update-comp :title="'修改账号信息'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./AccountUpdate.vue";

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
        nickname: row.nickname,
        mobile: row.mobile,
        roleKey: row.roleKey,
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

      axios.post(api.sys.updUser, params, {
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