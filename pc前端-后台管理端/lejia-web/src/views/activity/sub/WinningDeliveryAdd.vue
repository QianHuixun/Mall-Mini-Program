<!-- 
@name: WinningDeliveryAdd.vue 
@description: 发货 -- 新增组件 
@author: crj
-->
<template lang="html">
  <update-comp :title="'发货'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
  import updateComp from "./WinningDeliveryUpd.vue";
  import qs from "qs";

  export default {
    data() {
      return {
        data: {}
      };
    },
    components: {
      updateComp
    },
    methods: {
      show: function ({
        row
      }) {
        this.data = row;
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
        const params = inputModel;
        params["pkey"] = this.data.pkey;
        const _this = this;
        this.$refs.updateComp.loading = true;
        axios.post(api.marketing.updWinning, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            }
          })
          .then(response => {
            this.$message.success("发货成功");
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