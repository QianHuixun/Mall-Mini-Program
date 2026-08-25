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
import updateComp from "./AdsUpdate.vue";
import qs from "qs";

export default {
  data() {
    return {
      visible: false
    };
  },
  components: {
    updateComp
  },
  methods: {
    show: function({ row }) {
      const inputModel = {
        pkey: row.pkey,
        name: row.name,
        photo: row.photo,
        position: row.position,
        urlType: row.urlType,
        objKey: row.objKey,
        sort: row.sort,
        enabled: row.enabled
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
        if (row.urlType == "GOODS") {
          this.$refs.updateComp.updateGoods({ goodsInfo: { name: row.goodsName, pkey: row.objKey } });
        }
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
      axios.post(api.mall.updImg, qs.stringify(params), {
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