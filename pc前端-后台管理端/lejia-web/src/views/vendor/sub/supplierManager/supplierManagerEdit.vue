<template lang="html">
  <update-comp :title="'编辑供应商'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from "./supplierManagerUpdate.vue";

export default {
  data() {
    return {};
  },
  components: {
    updateComp,
  },
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      const supplierManagerPkey =
        localStorage.getItem("supplierManagerPkey") || "";
      const params = {
        pkey: supplierManagerPkey,
      };
      axios
        .post(api.vendor.supplierGet, this.$qs.stringify(params))
        .then((res) => {
          if(res.allowedDelivery === null) res.allowedDelivery = true
          this.$refs.updateComp.initData({
            inputModel: res,
          });
        });
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.vendor.supplierUpd, params, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          this.$message.success("编辑成功");
          this.$router.push({
            path: "/vendor/supplierManager",
          });
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  },
};
</script>