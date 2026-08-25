<!-- 
@name: MarketCaigouAdd.vue 
@description: 采购 -- 新增组件 
@author: crj
-->
<template lang="html">
  <update-comp :title="'采购'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './MarketCaigouUpd.vue';
import qs from 'qs';

export default {
  data() {
    return {
      data: {},
    };
  },
  components: {
    updateComp,
  },
  methods: {
    show: function ({ row, orderPkey }) {
      // console.log(row)
      row.map((item) => {
        item.vendorObject = '';
        return item;
      });
      const tableData = row;
      this.$refs.updateComp.show();
      this.$nextTick(() => {
        setTimeout(() => {
          this.$refs.updateComp.initData({
            tableData: tableData,
            orderPkey: orderPkey,
          });
        }, 0);
      });
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      console.log(inputModel);
      const _this = this;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.order.confirmPurchase, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('采购成功');
          this.$emit('refresh');
          this.$refs.updateComp.hide();
        });

      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 1000);
    },
  },
};
</script>