<!--
* @description 桌位号
* @fileName DesktopAdd.vue
* @author zs
* @date 2024/07/03
!-->
<template lang="html">
  <update-comp :title="'新增桌位'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
  import updateComp from "./DesktopUpdate.vue";
  import qs from "qs";

  export default {
    data() {
      return {};
    },
    components: {
      updateComp
    },
    methods: {
      show: function () {
        this.$nextTick(() => {
          setTimeout(() => {
            this.$refs.updateComp.show();
          }, 0);
        })
      },
      hide: function () {
        this.$emit("refresh");
      },
      /**
       * 保存数据
       */
      handleUpdate: function ({
        inputModel
      }) {
        const params = inputModel,
          _this = this;
        this.$refs.updateComp.loading = true;
        axios.post(api.market.insDesktop, params, {
            headers: {
              Authorization: this.$store.state.token,
              "Content-Type": "application/json"
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