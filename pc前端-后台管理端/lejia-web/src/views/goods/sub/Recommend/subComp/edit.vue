<template lang="html">
  <update-comp :title="'编辑'" ref="updateComp" isEdit @confirm="handleUpdate"></update-comp>
</template>
<script>
import updateComp from "./upd.vue";

export default {
  props: ['sourceGoods'],
  data() {
    return {};
  },
  components: {
    updateComp
  },
  methods: {
    show(row) {
      this.$refs.updateComp.show();
      this.$refs.updateComp.initData(row)
    },
    /**
     * 保存数据
     */
    handleUpdate({ inputModel }) {
      const params = {
        ...inputModel,
        sourceGoods: this.sourceGoods,
        zones: ['GOODS_DETAIL']
      }
      this.$refs.updateComp.loading = true;
      axios.post(api.goods.recommendUpd, params)
        .then(response => {
          this.$message.success("编辑成功");
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