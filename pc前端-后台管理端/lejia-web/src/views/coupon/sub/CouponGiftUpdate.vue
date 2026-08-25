<!-- 
@name: CouponGiftUpdate.vue 
@description: 礼品券管理--编辑模板 
@author: sx
@date: 2020/07/08
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="礼品券图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
      <el-form-item label="礼品券名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.title" ref="titleInput" placeholder="请输入礼品券名称"></el-input>
      </el-form-item>
      <el-form-item label="总数" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.count" ref="countInput" placeholder="请输入礼品券总数"
          v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.count =val;}"></el-input>
      </el-form-item>
      <el-form-item label="使用规则" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.content" ref="contentInput" type="textarea" :rows="2" placeholder="请输入使用规则"></el-input>
      </el-form-item>
      <el-form-item label="有效期" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="validityType">
          <el-radio label="1">领券后<el-input v-model="inputModel.effective" ref="effectiveInput"
            v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.effective =val;}" >
            </el-input>天</el-radio>
          <el-radio label="2">
            起止时间
            <el-date-picker v-model="inputModel.startDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="开始日期" end-placeholder="结束日期" ref="startDateInput">
            </el-date-picker>
            <span>至</span>
            <el-date-picker v-model="inputModel.endDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="结束日期" ref="endDateInput" :picker-options="pickerOptions">
            </el-date-picker>
          </el-radio>
        </el-radio-group>
        <div class="tips">只有当前时间介于起始日期和截止日期时，此券才可以使用，如不填写则表示永久有效
        </div>
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
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      labelWidth: '120px',
      visible: false,
      loading: false,
      inputModel: {
        pkey: '',
        title: '',
        count: '',
        content:'',
        picture: '',
        effective: '',
        startDate: '',
        endDate: '',
        userFarmer: this.$store.state.marketPkey,
      },
      validityType: '1', //有效期类型
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        },
      },
      totalPages: 0, //商品列表总页数
      page: 0, //商品列表当前页数
      pageSize: 8, //商品列表一页大小
    };
  },
  computed: {
  },
  mounted() {
  },
  components: {
    ImgUpload,
  },
  methods: {
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.picture = imgUrl[0];
    },
    /**
     * @desc 格式化优惠券金额
     */
    formatCost(val) {
      val = val.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      val = val.replace(/^\./g, ''); //验证第一个字符是数字
      val = val.replace(/\.{2,}/g, '.'); //只保留第一个, 清除多余的
      val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      val = val.replace(/^(\-)*(\d+)\.(\d).*$/, '$1$2.$3'); //只能输入两个小数
      return val;
    },
    //格式化价格
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        pkey: '',
        title: '',
        count: '',
        content:'',
        picture: '',
        effective: '',
        startDate: '',
        endDate: '',
        userFarmer: this.$store.state.marketPkey,
      };
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
      });
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      console.log(inputModel);
      this.inputModel = inputModel;
      if (inputModel.effective) {
        this.validityType = '1';
      } else {
        this.validityType = '2';
      }
      this.$refs.ImgUpload.updateImg(this.inputModel.picture);
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
      if (this.inputModel.title === '') {
        this.$message.error('请输入礼品券名称');
        this.$refs.titleInput.focus();
        return;
      }
      if (this.inputModel.count === '') {
        this.$message.error('请输入礼品券总数');
        this.$refs.countInput.focus();
        return;
      }

      if (this.inputModel.content === '') {
        this.$message.error('请输入使用规则');
        this.$refs.contentInput.focus();
        return;
      }

      if (this.validityType == '1') {
        if (!this.inputModel.effective) {
          this.$message.error('请输入有效期');
          this.$refs.effectiveInput.focus();
          return;
        }
        this.inputModel.startDate = '';
        this.inputModel.endDate = '';
      } else {
        if (!this.inputModel.startDate) {
          this.$message.error('请输入起始时间');
          this.$refs.startDateInput.focus();
          return;
        }
        if (!this.inputModel.endDate) {
          this.$message.error('请输入截止时间');
          this.$refs.endDateInput.focus();
          return;
        }
        this.inputModel.effective = '';
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
<style lang="less" scoped>
.el-radio {
  display: block;

  .el-input {
    width: 80px;
    margin: 8px;
  }

  .el-date-editor {
    width: 150px !important;
  }
}
</style>