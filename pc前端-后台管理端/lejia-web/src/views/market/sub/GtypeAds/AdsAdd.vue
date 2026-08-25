<!-- 
@name: AdsAdd.vue 
@description: 广告管理 -- 新增组件 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <update-comp :title="'新增广告'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './AdsUpdate.vue';

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
      let url = api.mall.insImg
      axios
        .post(url, params)
        .then(() => {
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