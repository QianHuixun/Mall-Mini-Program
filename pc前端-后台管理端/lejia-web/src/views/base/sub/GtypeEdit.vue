<!--
 * @Author: 沙晓
 * @Date: 2024-01-25 14:52:29
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-03-07 16:53:32
 * @Description: 推广管理 --修改组件
 * @FilePath: /lejia-web/src/views/base/sub/PromoteEdit.vue
-->
<template lang="html">
  <update-comp :title="'修改分类'" ref="updateComp" @confirm="handleUpdate"></update-comp>
</template>
<script>
  import updateComp from "./GtypeUpd.vue";
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
      show: function ({
        row
      }) {
        const inputModel = {
          pkey: row.pkey,
          name: row.name,
          sort: row.sort,
          photo: row.photo,
          gtype: row.level == 1 ? '' : (row.level == 2 ? row.higherLevelPkey : row.gtype),
          gtypeTwo: row.level == 3 ? row.higherLevelPkey : "",
          sysTwoGtype: row.sysTwoGtype
        };
        this.$refs.updateComp.show();
        setTimeout(() => {
          this.$refs.updateComp.initData({
            inputModel: inputModel,
            level: row.level
          });
        }, 0);
      },
      /**
       * 保存数据
       */
      handleUpdate: function ({
        inputModel,
        level
      }) {
        const params = inputModel;
        this.$refs.updateComp.loading = true;
        let url = level == 1 ? api.mall.gtypeUpd : (level == 2 ? api.mall.gtypeTwoUpd : api.mall.gtypeThreeUpd);
        axios
          .post(url, params, {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.$message.success("修改成功");
            this.$emit("refresh",inputModel.pkey);
            this.$refs.updateComp.hide();
          });
        setTimeout(() => {
          this.$refs.updateComp.loading = false;
        }, 300);
      }
    }
  };
</script>