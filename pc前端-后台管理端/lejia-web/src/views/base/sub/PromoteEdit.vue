<!--
 * @Author: 沙晓
 * @Date: 2024-01-25 14:52:29
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-03-07 16:53:32
 * @Description: 推广管理 --修改组件
 * @FilePath: /lejia-web/src/views/base/sub/PromoteEdit.vue
-->
<template lang="html">
  <update-comp
    :title="'修改推广'"
    ref="updateComp"
    @confirm="handleUpdate"
    @hide="hide"
  ></update-comp>
</template>
<script>
import updateComp from "./PromoteUpdate.vue";
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
        title: row.title,
        content: row.content,
        photo: row.photo
      };
      this.$refs.updateComp.show();
      setTimeout(() => {
        this.$refs.updateComp.initData({ inputModel: inputModel });
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
        .post(api.mall.promoteupd, params, {
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
