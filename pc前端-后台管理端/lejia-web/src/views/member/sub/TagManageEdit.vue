<!--
 * @Author: 沙晓
 * @Date: 2025-06-20 09:56:03
 * @LastEditors: 沙晓
 * @LastEditTime: 2026-06-01 13:28:32
 * @Description: file content
 * @FilePath: /lejia-web/src/views/member/sub/TagManageEdit.vue
-->
<template lang="html">
  <update-comp :title="'修改标签'" ref="updateComp" @confirm="handleUpdate" @hide="hide"></update-comp>
</template>
<script>
import updateComp from './TagManageUpdate.vue';

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
        type: row.type,
        description: row.description
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
        .post(api.marketing.tagsUpd, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
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