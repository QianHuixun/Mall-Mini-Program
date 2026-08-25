<!-- 
@name: LotteryUpdate.vue 
@description: 抽奖活动配置--编辑模板 
@author: sx
@date: 2020/07/07
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="奖品图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="类型" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.ptype" placeholder="请选择" @change="handleSelect">
          <el-option label="积分" value="INTEGRAL_PRIZE"></el-option>
          <el-option label="优惠券" value="CARD_PRIZE"></el-option>
          <el-option label="实物" value="GIFT_PRIZE"></el-option>
          <el-option label="谢谢惠顾" value="THANK_PRIZE"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="中奖积分" :label-width="labelWidth" v-if="inputModel.ptype=='INTEGRAL_PRIZE'">
        <el-input v-model="inputModel.pvalue" ref="pvalueInput" placeholder="请输入中奖积分"></el-input>
      </el-form-item>
      <el-form-item label="优惠券" :label-width="labelWidth" :required="true" v-if="inputModel.ptype=='CARD_PRIZE'">
        <el-select v-model="inputModel.pvalue" filterable placeholder="请选择">
          <el-option :label="item.title" :value="item.pkey" v-for="(item,index) in CouponList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="中奖提示" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.descp" ref="descpInput" placeholder="请输入中奖提示"></el-input>
      </el-form-item>
      <el-form-item label="中奖概率" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.probability" ref="probabilityInput" placeholder="请输入中奖概率"
          v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.probability =val;}"></el-input>
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
  import ImgUpload from "@/components/global/ImgUpload";
  import dropdown from "@/assets/js/dropdown";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          pkey: "",
          descp: "",
          photo: "",
          name: "",
          pvalue: "",
          probability: "",
          ptype: "INTEGRAL_PRIZE" //INTEGRAL_PRIZE(0, "积分"), CARD_PRIZE(1, "优惠券"),GIFT_PRIZE(2, "实物"),THANK_PRIZE(3, "谢谢惠顾");
        },
        CouponList: []
      };
    },
    mounted() {
      dropdown.getCoupon().then(result => {
        this.CouponList = result;
      });
    },
    components: {
      ImgUpload
    },
    methods: {
      /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo = imgUrl;
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          pkey: "",
          descp: "",
          photo: "",
          name: "",
          pvalue: "",
          probability: "",
          ptype: "INTEGRAL_PRIZE"
        };
        this.$nextTick(() => {
          setTimeout(() => {
            this.$refs.ImgUpload.updateImg('');
          }, 0);
        });
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
        this.$nextTick(() => {
          setTimeout(() => {
            this.$refs.ImgUpload.updateImg(this.inputModel.photo);
          }, 0);
        });
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
        this.$emit("hide")
      },
      /**
       * 下拉选择改变
       */
      handleSelect: function () {
        this.inputModel.pvalue = "";
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.photo || !this.inputModel.photo.length) {
          this.$message.error("请上传图片");
          return;
        }

        if (!this.inputModel.name) {
          this.$message.error("请输入名称");
          this.$refs.nameInput.focus();
          return;
        }

        if (this.inputModel.gtype == "INTEGRAL_PRIZE") {
          if (!this.inputModel.pvalue) {
            this.$message.error("请输入中奖积分");
            this.$refs.pvalueInput.focus();
            return;
          }
        }

        if (!this.inputModel.descp) {
          this.$message.error("请输入中奖提示");
          this.$refs.descpInput.focus();
          return;
        }

        if (!this.inputModel.probability) {
          this.$message.error("请输入中奖概率");
          this.$refs.probabilityInput.focus();
          return;
        }


        this.$emit("confirm", {
          inputModel: this.inputModel
        });
      }
    },
    props: {
      title: {
        type: String,
        default: "新增"
      }
    }
  };
</script>