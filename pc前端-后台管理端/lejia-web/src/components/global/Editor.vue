<!-- UEditor组件 -->
<style lang="less" scoped>
#UEditor {
  min-width: 500px;
}
</style>
<template lang='html'>
  <div id="UEditor">
    <script :id="idName" type="text/plain"></script>
  </div>
</template>
<script>
const httpHost = api.common.editorHost;
export default {
  data() {
    return {
      editor: null,
    };
  },
  created() {},
  mounted() {
    const _this = this;
    // this.editor = UE.getEditor('editor', this.config); // 初始化UE
    // this.editor.addListener("ready", function () {
    //   _this.editor.setContent(_this.defaultMsg); // 确保UE加载完成后，放入内容。
    // });
    this.initEditor();
    if (this.defaultMsg) {
      // this.editor.addListener("ready", function () {
      //   _this.editor.setContent(_this.defaultMsg); // 确保UE加载完成后，放入内容。
      // });
      this.editor.ready(function () {
        //this是当前创建的编辑器实例
        this.setContent(_this.defaultMsg);
      });
    }

    if (this.disabled) {
      this.editor.ready(function () {
        this.setDisabled();
      });
    }
  },
  components: {},
  methods: {
    getUEContent() {
      // 获取内容方法
      return this.editor.getContent();
    },
    updateUEContent(content) {
      !this.editor && this.initEditor();
      this.$nextTick(() => {
        this.editor.ready(function () {
          //this是当前创建的编辑器实例
          this.setContent(content);
        });
      });
    },
    /**
     * @desc 在光标处加入内容
     */
    insUEContent(content) {
      this.editor.focus();
      this.editor.execCommand('inserthtml', content);
    },
    initEditor() {
      this.config.initialFrameHeight = this.frameHeight;
      this.config.initialFrameWidth = this.frameWidth;

      //初始化UE
      // const _this = this;
      this.editor = UE.getEditor(this.idName, this.config);
      this.editor.addListener('beforeInsertImage', function (t, arg) {
        console.log('这是图片地址：', t, arg[0].src);
      });
    },
  },
  destroyed() {
    this.editor.destroy();
  },
  props: {
    defaultMsg: {
      type: String,
    },
    idName: {
      // 编辑器的id, 预防重名
      type: String,
      default: 'editor',
    },
    config: {
      type: Object,
      default: () => {
        return {
          toolbars: [
            [
              // 'fullscreen',
              'undo',
              'redo',
              'fontfamily', //字体
              'fontsize', //字号
              'removeformat',
              'formatmatch',
              'source',
              // 'simpleupload', //单图上传
              "insertimage", //多图上传
              // "link", //超链接
              'emotion', //表情
            ],
            [
              'bold',
              'italic',
              'underline',
              'fontborder',
              'strikethrough',
              '|',
              'justifyleft', //居左对齐
              'justifyright', //居右对齐
              'justifycenter', //居中对齐
              'justifyjustify', //两端对齐
              '|',
              'autotypeset',
              'blockquote',
              'pasteplain',
              '|',
              'forecolor',
              'backcolor',
              'insertorderedlist',
              'insertunorderedlist',
              'selectall',
              'cleardoc',
            ],
          ],
          autoHeightEnabled: false,
          autoFloatEnabled: false, //是否工具栏可浮动
          initialContent: '', //初始化编辑器的内容,也可以通过textarea/script给值，看官网例子
          autoClearinitialContent: true, //是否自动清除编辑器初始内容，注意：如果focus属性设置为true,这个也为真，那么编辑器一上来就会触发导致初始化的内容看不到了
          initialFrameWidth: '100%',
          initialFrameHeight: 250,
          UEDITOR_HOME_URL: (process.env.VUE_APP_TITLE === 'test' || process.env.VUE_APP_TITLE === 'development') ? '/zy/UEditor/' : '/UEditor/',
          // serverUrl: httpHost + '/file/v3/ue/config',//单图上传
          serverUrl: httpHost + '/file/v3/ue/uploadImages',//多图上传
          headers: { 
            Authorization: localStorage.getItem('token')  // 从 localStorage 获取 Token
          },
          allowDivTransToP: false,
          zIndex: 2500
        };
      },
    },
    frameHeight: {
      type: Number,
      default: 200,
    },
    frameWidth: {
      type: Number,
      default: 480,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
<style lang="less" scoped>
/deep/.edui-editor-toolbarbox {
  line-height: normal !important;
}
</style>