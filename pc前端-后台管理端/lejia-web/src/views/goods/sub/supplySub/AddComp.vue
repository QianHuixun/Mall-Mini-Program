<!-- 
@name: AddComp.vue 
@description: 商品供应商 -- 新增组件 
@author: crj
@date: 2021/10/09
-->
<template lang="html">
  <update-comp :title="'新增'" ref="updateComp" @confirm="handleUpdate" @hide="hide" :needRefesh="false"></update-comp>
</template>
<script>
import updateComp from './UpdComp.vue';

export default {
  data() {
    return {};
  },
  components: {
    updateComp,
  },
  methods: {
    show: function (market) {
      this.$refs.updateComp.show(market);
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel,
        _this = this;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.goods.insSupply, params, {
          headers: {
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