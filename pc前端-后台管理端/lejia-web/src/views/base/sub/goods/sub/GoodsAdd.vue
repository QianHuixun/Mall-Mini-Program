<!-- 
@name: GoodsAdd.vue 
@description: 商品库中心 -- 新增组件 
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <update-comp :title="'新增商品'" ref="updateComp" :type="type" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from "./GoodsUpdate.vue";
import qs from "qs";

export default {
  data() {
    return {
      type: 'two'
    };
  },
  components: {
    updateComp
  },
  methods: {
    show: function(type) {
      console.log(type);
      this.type = type
      this.$refs.updateComp.show();
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel,
        _this = this;
      this.$refs.updateComp.loading = true;
      const url = this.type === 'three' ?  api.mall.insThreeGoods : api.mall.insGoods
      axios.post(url, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.$message.success("新增成功");
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