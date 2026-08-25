<template lang="html">
  <update-comp :title="'新增标签'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './TagManageUpdate.vue';

export default {
  data() {
    return {};
  },
  components: {
    updateComp,
  },
  methods: {
    show: function () {
      this.$refs.updateComp.show();
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.marketing.tagsIns, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then((response) => {
          this.$message.success('新增成功');
          this.$emit('refresh');
          this.$refs.updateComp.hide();
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  },
};
</script>
