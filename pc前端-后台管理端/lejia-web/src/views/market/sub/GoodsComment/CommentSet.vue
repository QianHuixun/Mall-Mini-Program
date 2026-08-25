<!--
 * @Author: 沙晓
 * @Date: 2025-07-18 17:22:23
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-08-08 14:34:21
 * @Description: file content
 * @FilePath: /lejia-web/src/views/market/sub/GoodsComment/CommentSet.vue
-->
<template lang="html">
  <el-dialog title="评价功能" center :visible.sync="visible" :closeOnClickModal="false" width="30%">
    <div class="dialog-main">
      商城是否显示评价功能 <el-switch active-color="#13ce66" v-model="enableComment"></el-switch>
    </div>
      <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
    </el-dialog>
  </template>
<script>
export default {
  data() {
    return {
      enableComment:false,
      loading: false,
      visible: false,
    }
  },
    mounted() {},
    methods: {
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.visible = false;
    },
    show: function () {
      this.visible = true;
      this.getData();
    },
    getData:function() {
      axios.post(api.market.CommentConfigGet, this.$qs.stringify({}), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.enableComment = res.enableComment;
        });
    },
      /**
     * 处理提交
     */
    handleSubmit: function () {
      const params={
        enableComment: this.enableComment
      }
      axios.post(api.market.CommentConfigSet, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
          this.$message.success('设置成功');
          this.hide();
        });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    }
  }
}
</script>

<style lang="less" scoped>
.dialog-main {
  margin: 15px 0;
}
</style>
