<!-- 
@name: GoodsUpdate.vue 
@description: 推广管理--编辑模板 
@author: hdc
@date: 2022/05/31
-->
<template lang="html">
  <el-dialog
    :title="title"
    center
    :visible.sync="visible"
    :closeOnClickModal="false"
  >
    <el-form>
      <el-form-item label="市场" :label-width="labelWidth" :required="true">
        <el-select
          v-model="inputModel.farmer"
          @change="handleChange"
          placeholder="选择市场"
          clearable
        >
          <el-option
            :value="item.pkey"
            :key="index"
            :label="item.name"
            v-for="(item, index) in marketList"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="推广标题" :label-width="labelWidth" :required="true">
        <el-input
          v-model="inputModel.title"
          ref="nameInput"
          maxlength="16"
          show-word-limit
          placeholder="请输入推广标题"
        ></el-input>
      </el-form-item>
      <el-form-item label="推广内容" :label-width="labelWidth" :required="true">
        <el-input
          v-model="inputModel.content"
          ref="nameInput"
          maxlength="16"
          show-word-limit
          placeholder="请输入推广内容"
        ></el-input>
      </el-form-item>
      <el-form-item label="推广封面" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button
        size="medium"
        type="primary"
        @click="handleSubmit"
        :loading="loading"
      >
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import utils from "@/assets/js/utils";
import dropdown from "@/assets/js/dropdown";
import ImgUpload from "@/components/global/ImgUpload";
export default {
  components: {
    ImgUpload
  },
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        farmer: "",
        title: "",
        content: "",
        photo: ""
      },
      marketList: [], //市场列表
      typeList: []
    };
  },
  mounted() {
    dropdown.getType().then(result => {
      this.typeList = result.content;
    });
    dropdown.getMarket().then(result => {
      this.marketList = result.content;
    });
  },
  methods: {
    /**
     * 图片修改事件
     */
    changeImg: function(imgUrl) {
      this.inputModel.photo = imgUrl[0];
    },
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        farmer: "",
        title: "",
        content: "",
        photo: ""
      };
    },
    /**
     * 初始化数据
     */
    initData: function({ inputModel }) {
      this.inputModel = inputModel;
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg(inputModel.photo);
      });
    },
    show: function() {
      this.visible = true;
      this.clearData();
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg("");
      });
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
      this.$emit("hide");
    },
    /**
     * 处理提交
     */
    handleSubmit: function() {
      if (!this.inputModel.farmer) {
        this.$message.error("请选择市场");
        // this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.title) {
        this.$message.error("请输入推广标题");
        // this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.content) {
        this.$message.error("请输入推广内容");
        // this.$refs.gtypeSelect.focus();
        return;
      }

      if (!this.inputModel.photo) {
        this.$message.error("请上传推广封面");
        return;
      }

      this.$emit("confirm", { inputModel: this.inputModel });
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
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>
