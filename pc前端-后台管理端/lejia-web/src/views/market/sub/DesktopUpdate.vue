<!--
* @description 桌位管理-编辑
* @fileName DesktopUpdate.vue
* @author zs
* @date 2024/07/03
!-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="桌位号" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入桌位号"></el-input>
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
  import utils from "@/assets/js/utils";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          name: "",
          pkey: "",
          createdTime:""
        },
      };
    },
    mounted() {},
    methods: {
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          name: "",
          pkey: "",
          createdTime:""
        };
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
      },
      show: function () {
        this.visible = true;
        this.clearData();
        this.$emit("hide");
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.name) {
          this.$message.error("请输入桌位号");
          this.$refs.nameInput.focus();
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
      },
      isEdit: {
        type: Boolean,
        default: false
      }
    }
  };
</script>