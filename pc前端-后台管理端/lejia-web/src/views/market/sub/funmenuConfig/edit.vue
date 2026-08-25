<!--
 * @Author: 沙晓
 * @Date: 2025-07-10 15:09:44
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-08-11 10:45:16
 * @Description:  广告管理 --修改组件
 * @FilePath: /lejia-web/src/views/market/sub/GtypeAds/AdsEdit.vue
-->
<template lang="html">
  <update-comp :title="'修改'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './update.vue';

export default {
  data() {
    return {
      visible: false,
    };
  },
  components: {
    updateComp,
  },
  methods: {
    show: function ({ row }) {
      const inputModel = row;
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
      }, 0);
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      axios.post(api.mall.funmenuConfigUpd, params)
        .then(() => {
          this.$message.success('修改成功');
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