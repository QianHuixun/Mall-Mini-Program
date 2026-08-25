<!-- 
@name: ClassicUpdate.vue 
@description: 商品分类管理--编辑模板 
@author: sx
@date: 2020/06/28
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="分类名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入分类名称" 
        :disabled="inputModel.name=='优惠券'||inputModel.name=='礼券'"
        v-on:input="(val)=>{ val =(val=='优惠券'||val=='礼券'?'':val); inputModel.name =val;}"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
      <el-form-item label="所属类型" :label-width="labelWidth" :required="true" >
        <el-checkbox v-model="inputModel.showPoint" :readonly="inputModel.name=='优惠券'||inputModel.name=='礼券'">积分商城</el-checkbox>
        <el-checkbox v-model="inputModel.showMarket" :disabled="inputModel.name=='优惠券'||inputModel.name=='礼券'">市场</el-checkbox>
      </el-form-item>
        <el-form-item  v-if="inputModel.showPoint" label="积分排序" :label-width="labelWidth">
        <el-input v-model="inputModel.pointSort" ref="sortInput" pla ceholder="请输入积分排序"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.pointSort =val;}"></el-input>
      </el-form-item>
      <el-form-item v-if="inputModel.showMarket" label="市场排序" :label-width="labelWidth">
        <el-input v-model="inputModel.marketSort" ref="sortInput" placeholder="请输入市场排序"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.marketSort =val;}"></el-input>
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
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
import ImgUpload from '@/components/global/ImgUpload';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        name: '',
        showPoint: false,
        showMarket: false,
        photo: '',
        enabled: true,
        pointSort: 0,
        marketSort: 0,
      },
    };
  },
  mounted() {},
  components: {
    ImgUpload,
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
        name: '',
        showPoint: false,
        showMarket: false,
        photo: '',
        enabled: true,
        pointSort: 0,
        marketSort: 0,
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg(inputModel.photo);
      });
    },
    show: function () {
      this.visible = true;
      this.clearData();
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
      });
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
      if (!this.inputModel.name.replace(/(^\s*)|(\s*$)/g, '')) {
        this.$message.error('请输入名称');
        this.$refs.nameInput.focus();
        return;
      }
      if (!this.inputModel.showPoint && !this.inputModel.showMarket) {
        this.$message.error('请选择所属类型');
        return;
      }

      this.$emit('confirm', { inputModel: this.inputModel });
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
/deep/ .el-form {
  overflow: visible !important;
}
</style>