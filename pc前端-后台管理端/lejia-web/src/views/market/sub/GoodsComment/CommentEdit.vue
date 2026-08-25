<template lang="html">
  <el-dialog title="回复评价" center :visible.sync="visible" :closeOnClickModal="false" width="30%">
    <div class="dialog-main">
      <el-input type="textarea" v-model="replyContent" :rows="5" placeholder="请输入内容"></el-input>
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
      pkey: "",
      replyContent:"",
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
    show: function ({row}) {
      this.visible = true;
      this.pkey = row.pkey;
      this.replyContent = "";
    },
      /**
     * 处理提交
     */
    handleSubmit: function () {
      const params = {
        pkey: this.pkey,
        replyContent: this.replyContent
      }
      axios.post(api.market.CommentReply, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          },
        })
        .then(() => {
          this.$message.success('回复成功');
          this.hide();
          this.$emit("refresh");
          this.loading = false;
        }).catch(()=> {
          this.loading = false;
        });
    }
  }
}
</script>