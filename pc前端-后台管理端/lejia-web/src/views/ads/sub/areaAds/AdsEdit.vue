<!-- 
@name: AdsEdit.vue 
@description: 广告管理 --修改组件
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <update-comp :title="'修改广告'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './AdsUpdate.vue';
import qs from 'qs';

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
      console.log(row);
      const inputModel = {
        farmers: row.farmers,
        pkey: row.pkey,
        name: row.name,
        photo: row.photo,
        position: row.position,
        urlType: row.urlType,
        objKey: row.objKey,
        objKeyName: row.objKeyName,
        sort: row.sort,
        enabled: row.enabled,
        locationType: row.locationType,
        goodsName: row.goodsName,
        visibleRange: row.visibleRange,
        targerKeys: row.targerKeys,
      };
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
      if (this.$store.state.userIdentity == 1)
        params.farmers = params.farmers.join(',');
        params.targerKeys = params.targerKeys.join(',');
      this.$refs.updateComp.loading = true;
      axios
        .post(api.mall.updImg, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
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