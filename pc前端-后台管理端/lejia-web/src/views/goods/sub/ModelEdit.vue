<template>
  <el-dialog title="模板编辑" center :visible.sync="visible" :closeOnClickModal="false" width="850px"  v-if="visible" >
    <editor ref="editor" :idName="'modelEditor'" :frameHeight="350" :frameWidth="700"></editor>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        保 存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import Editor from '@/components/global/Editor.vue';
import qs from 'qs';
export default {
  components: {
    Editor,
  },
  data() {
    return {
      visible: false,
      loading: false,
    };
  },
  methods: {
    show() {
      this.visible = true;
      this.$nextTick(() => {
        // this.$refs.editor.updateUEContent('');
        this.richTempGet();
      });
    },
    hide() {
      this.visible = false;
    },
    handleSubmit() {
      const editorStr = this.$refs.editor.getUEContent();
      // console.log(editorStr);
      // console.log(this.$store.state.token);
      // console.log(api.goods.richTempUpd);
      const params = {
        content: editorStr,
      };
      axios
        .post(api.goods.richTempUpd, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          // console.log(res);
          if (res) {
            this.$message.success('保存成功');
            this.hide();
          }
        });
    },
    richTempGet() {
      axios
        .post(api.goods.richTempGet, '', {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          if (!res.hasOwnProperty('result') && res) {
            this.$refs.editor.updateUEContent(res);
          }
        });
    },
  },
};
</script>

<style lang="less" scoped>
.editor_container {
  width: 100%;
  display: flex;
  align-items: center;
}

/deep/ .el-dialog .el-dialog__body {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>