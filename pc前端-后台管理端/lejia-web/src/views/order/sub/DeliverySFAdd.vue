<!--
* @description 顺丰发货
* @fileName DeliverySFAdd.vue
* @author zs
* @date 2024/12/10
!-->
<template lang="html">
  <update-comp :title="'顺丰下单'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
  import updateComp from "./MallDeliverySFUpd.vue";
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
        params["pkey"] =  this.data.pkey;
        this.$refs.updateComp.loading = true;
        axios.post(api.order.sendOrderSF, qs.stringify(params), {
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