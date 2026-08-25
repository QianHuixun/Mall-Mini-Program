<!-- 
@name: ClassicEdit.vue 
@description: 商品分类 --修改组件
@author: sx
@date: 2020/06/28
-->
<template lang="html">
  <update-comp :title="'修改分类'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './ClassicUpdate.vue';
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
      const inputModel = {
        pkey: row.pkey,
        name: row.name,
        showPoint: row.showPoint,
        showMarket: row.showMarket,
        enabled: row.enabled,
        photo: row.photo,
        pointSort: row.pointSort,
        marketSort: row.marketSort,
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
      this.$refs.updateComp.loading = true;
      axios
        .post(api.mall.updClassic, qs.stringify(params), {
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