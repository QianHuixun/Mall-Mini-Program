<!-- 
@name: ProgressDlog.vue 
@description: 全局组件--导入进度条组件 
@author: crj
@date: 2020/08/11
-->
<template>
  <div>
    <el-dialog
      :title="title"
      :visible.sync="visible"
      width="30%"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
     <el-progress class="progress" :text-inside="true" :stroke-width="18" :percentage="uploadPercent"></el-progress>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    uploadPercent: {
      value: 0,
      type: Number
    },
    title:String
  },
  data() {
    return {
      visible: false
    };
  },
  methods: {
    show: function() {
      this.visible = true;
    },
    hide: function() {
      this.visible = false;
      this.$emit("refresh");
    }
  }
};
</script>
<style lang="less" scoped>
.progress{
  padding-bottom: 15px;
}

</style>