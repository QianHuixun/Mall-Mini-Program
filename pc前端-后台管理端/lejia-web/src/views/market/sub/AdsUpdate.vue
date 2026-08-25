<!-- 
@name: AdsUpdate.vue 
@description: 广告管理--编辑模板 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
    </el-form>
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
import ImgUpload from '@/components/global/ImgUpload';
import ClickEffect from '@/components/global/ClickEffect';

export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        position: '',
        name: '',
        photo: '',
        urlType: 'NOT_URL',
        objKey: '',
        sort: 0,
        enabled: true,
      },
    };
  },
  mounted() {},
  components: {
    ImgUpload,
    ClickEffect,
  },
  methods: {
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.photo = imgUrl[0];
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        position: '',
        name: '',
        photo: '',
        urlType: 'GOODS',
        objKey: '',
        sort: 0,
        enabled: true,
      };
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
      });
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      this.$refs.ImgUpload.updateImg(this.inputModel.photo);
    },
    show: function () {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('hide');
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.name) {
        this.$message.error('请输入名称');
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.photo) {
        this.$message.error('请选择图片');
        this.$refs.photoInput.focus();
        return;
      }
      if(this.$refs.ClickEffect.validate() === false) {
        return
      }
      if (this.inputModel.urlType == 'GTYPE') {
        inputModel.objKey = inputModel.objKey.join(',');
      }
      if (!this.inputModel.sort) {
        this.inputModel.sort = 0;
      }
      this.$emit('confirm', {
        inputModel: this.inputModel,
      });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
  },
};
</script>