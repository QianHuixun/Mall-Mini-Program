<!-- 
@name: AdsUpdate.vue 
@description: 广告管理--编辑模板 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="功能名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" maxlength="5" placeholder="请输入功能名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips">推荐尺寸：100*80</div>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序"
          v-on:input="limitInput($event)"></el-input>
      </el-form-item>
      <el-form-item label="可见用户" :label-width="labelWidth">
        <el-radio-group v-model="inputModel.visibleRange">
          <el-radio label="ALL">全部用户</el-radio>
          <el-radio label="TAG">
            指定标签
            <el-select v-model="inputModel.targerKeys" clearable filterable multiple collapse-tags ref="targerKeysSelect"
              placeholder="请选择" @change="handleTagsChange">
              <el-option value="all" label="全部"></el-option>
              <el-option v-for="item in TagsList" :key="item.pkey" :label="item.name" :value="item.pkey">
              </el-option>
            </el-select>
          </el-radio>
        </el-radio-group>
      </el-form-item>
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
import dropdown from '@/assets/js/dropdown';
import qs from 'qs';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        name: '',
        photos: '',
        urlType: 'NOT_URL',
        urlTypeName: '',
        objKey: '',
        objKeyName: '',
        rank: 0,
        enabled: true,
        targerKeys: [],
        visibleRange: "ALL",
      },
      TagsList: [],
      marketDisabled: false,
      marketList: [],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        checkStrictly: true,
      }, //级联选择器配置
      activityList: []
    };
  },
  mounted() { },
  components: {
    ImgUpload,
    ClickEffect
  },
  methods: {
    limitInput(val) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel.sort = val;
    },
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.photos = imgUrl[0];
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        name: '',
        photos: '',
        urlType: 'NOT_URL',
        urlTypeName: '',
        objKey: '',
        objKeyName: '',
        rank: 0,
        targerKeys: [],
        enabled: true,
        visibleRange: "ALL"
      };
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
      });
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      axios.post(api.mall.funmenuConfigGet,this.$qs.stringify({pkey: inputModel.pkey}))
        .then(res => {
          console.log(res);
          this.inputModel = res
          if (this.inputModel.urlType == 'GTYPE') {
            this.inputModel.objKey = this.inputModel.objKey
              .split(',')
              .map((item) => {
                return Number(item);
              });
          }
          this.$refs.ImgUpload.updateImg(this.inputModel.photos);
        })
    },
    show: function () {
      this.visible = true;
      dropdown.getTagsList().then((result) => {
        this.TagsList = result;
      });
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
      let inputModel = JSON.parse(JSON.stringify(this.inputModel));
      if (!this.inputModel.name) {
        this.$message.error('请输入功能名称');
        this.$refs.nameInput.focus();
        return;
      }
      if (!this.inputModel.photos) {
        this.$message.error('请选择图片');
        this.$refs.photosInput.focus();
        return;
      }
      if(this.$refs.ClickEffect.validate() === false) {
        return
      }
      if (this.inputModel.urlType == 'GTYPE') {
        inputModel.objKey = inputModel.objKey.join(',');
      }
      if (!this.inputModel.sort) {
        inputModel.sort = 0;
      }
      if(this.inputModel.visibleRange == 'TAG' && (!this.inputModel.targerKeys || !this.inputModel.targerKeys.length)) {
        this.$message.error('请选择指定标签用户');
        return;
      }
      this.$emit('confirm', {
        inputModel: inputModel,
      });
    },
    handleTagsChange(e) {
      if(e && e.length && e.includes('all')) {
        this.inputModel.targerKeys = this.TagsList.map(item => item.pkey)
      }
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
<style lang="less" scoped>
/deep/.el-select .el-input {
  height: auto !important;
}
.el-radio {
  display: block;

  .el-input {
    width: 80px;
    margin: 8px;
  }

  .el-select {
    display: inline-block !important;
    margin:8px;
    width: 300px;
  }

  .el-date-editor {
    width: 150px !important;
  }
}
</style>