<!-- 
@name: CookTypeUpd.vue 
@description: 菜谱分类--编辑模板 
@author: crj
@date: 2020/08/12
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="850px"
    class="CookfdUpdate">
    <el-form>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入菜谱分类名称"></el-input>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort =val;}"></el-input>
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
  import Sortable from "sortablejs";
  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          name: "",
          sort: 0
        },

        GoodsList: [],
        SpacesList: {}
      };
    },
    mounted() {},
    components: {},
    methods: {

      /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo1 = imgUrl;
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          name: "",
          sort: 0
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
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
        this.$emit("hide");
      },

      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.name) {
          this.$message.error("请输入名称");
          this.$refs.nameInput.focus();
          return;
        }
        if (!this.inputModel.sort) {
          this.inputModel.sort = 0;
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
<style lang="less">
  .CookfdUpdate {
    .el-row {
      display: flex;

      .el-input {
        flex: 1;
      }
    }
  }
</style>