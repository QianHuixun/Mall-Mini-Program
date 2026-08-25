<!--
 * @Author: 沙晓
 * @Date: 2022-05-09 11:18:06
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-03-07 16:54:20
 * @Description: 弹窗广告-编辑组件
 * @FilePath: /lejia-web/src/views/ads/sub/ads/AdsEdit.vue
-->

<template lang="html">
  <update-comp
    :title="'编辑弹窗广告'"
    ref="updateComp"
    @hide="hide"
    @confirm="handleUpdate"
  ></update-comp>
</template>
<script>
import updateComp from "./AdsUpd.vue";
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
        farmer: row.farmer,
        name: row.name,
        photo: row.photo,
        startDate: row.startDate,
        endDate: row.endDate,
        urlType: row.urlType,
        objKey: row.objKey,
        subject: row.subject
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
        if (row.urlType == "GOODS") {
          this.$refs.updateComp.updateGoods({
            goodsInfo: { name: row.goodsName, pkey: row.objKey }
          });
        }
      }, 0);
    },
    hide: function() {
      this.$emit("refresh");
    },
    /**
     * 保存数据
     */
    handleUpdate: function({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.popup.updPopAds, params, {
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
