<!-- 
@name: GoodsEdit.vue 
@description: 商品库中心 --修改组件
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <update-comp :title="'修改商品'" ref="updateComp" :type="type" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./GoodsUpdate.vue";
import qs from "qs";

export default {
  data() {
    return {
      visible: false,
      type: 'two'
    };
  },
  components: {
    updateComp
  },
  methods: {
    show: function({ row, type }) {
      this.type = type
      const inputModel = {
        pkey: row.pkey,
        name: row.name,
        gtype: row.gtype,
        sort: row.sort,
        enabled: row.enabled,
        twoGtype: row.twoGtype,
      };
      this.$refs.updateComp.show();
      setTimeout(() => {   
        this.$refs.updateComp.initData({ inputModel: inputModel, row });
      }, 0);
    },
    hide: function(){
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      const url = this.type === 'three' ? api.mall.updThreeGoods : api.mall.updGoods
      axios.post(url, qs.stringify(params), {
        headers: {
          Authorization: this.$store.state.token
        }
      })
      .then(response => {
        this.$message.success("修改成功");
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