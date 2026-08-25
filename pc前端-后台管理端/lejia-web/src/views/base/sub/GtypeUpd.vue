<!--
* @description 编辑
* @fileName GtypeUpd.vue
* @author zs
* @date 2024/05/14
!-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="分类层级" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="level" :disabled="isEdit">
          <el-radio :label="1">一级分类</el-radio>
          <el-radio :label="2">二级分类</el-radio>
          <el-radio :label="3">三级分类</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="分类层级" :label-width="labelWidth" :required="true" v-if="level != 1">
        <el-select v-model="inputModel.gtype" filterable ref="gtypeSelect" placeholder="请输入分类名称" v-if="level==2"
          :disabled="isEdit">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in options"></el-option>
        </el-select>
        <el-cascader v-model="optionsValue" :options="options" filterable :props="props" :disabled="isEdit" v-else>
        </el-cascader>
      </el-form-item>
      <el-form-item label="分类名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" show-word-limit placeholder="请输入分类名称">
        </el-input>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" placeholder="请输入排序" ref="sortInput"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort = val;}"></el-input>
      </el-form-item>
      <el-form-item label="分类图片" :label-width="labelWidth" v-if="level == 1">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
      </el-form-item>
      <el-form-item label="平台商品分类" :label-width="labelWidth" v-if="level==2">
        <el-cascader v-model="inputModel.sysTwoGtype" :options="categoryList" :props="sysProps" clearable filterable @change="handleChange" placeholder="商品类型"></el-cascader>
        <span style="color: #999;">添加平台分类后，可将平台下的商品数据显示到市场分类列表中</span>
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
  import dropdown from "@/assets/js/dropdown";
  import ImgUpload from "@/components/global/ImgUpload";
  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        level: 1,
        options: [],
        optionsValue: "",
        isEdit: false, // 是否是编辑
        inputModel: {
          pkey: "",
          name: "",
          sort: "",
          photo: "",
          gtype: "",
          gtypeTwo: "",
          sysTwoGtype: "",
        },
        props: {
          value: 'pkey',
          label: 'name',
          children: 'goodsList',
        }, //级联选择器配置
        categoryList: [],
        sysProps: {
          value: 'pkey',
          label: 'name',
          children: 'gmList',
          emitPath: false,
        }
      };
    },
    components: {
      ImgUpload,
    },
    mounted() {},
    methods: {
      /**
       * 清空数据
       */
      clearData: function () {
        this.level = 1;
        this.optionsValue = "";
        this.isEdit = false;
        this.inputModel = {
          pkey: "",
          name: "",
          sort: "",
          gtype: "",
          gtypeTwo: "",
          sysTwoGtype: "",
        };
        if(this.$refs.ImgUpload) this.$refs.ImgUpload.updateImg('');
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel,
        level
      }) {
        this.level = level;
        this.inputModel = inputModel;
        if(inputModel.photo) this.$refs.ImgUpload.updateImg(inputModel.photo); 
        if (this.level == 3) {
          this.optionsValue = [this.inputModel.gtype, this.inputModel.gtypeTwo];
        }
        this.isEdit = true;
      },
      show: function () {
        this.visible = true;
        this.clearData();
        dropdown.getType().then(result => {
          this.options = result.content;
        });
        this.getSysGoodsList()
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
       * 上传图片
       */
      changeImg(imgUrl) {
        this.inputModel.photo = imgUrl[0];
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.name) {
          this.$message.error("请输入分类名称");
          // this.$refs.nameInput.focus();
          return;
        }

        if (this.level == 2) {
          if (!this.inputModel.gtype) {
            this.$message.error("请选择分类层级");
            return;
          }
          this.inputModel.gtypeTwo = "";
        }

        if (this.level == 3) {
          if (!this.optionsValue || this.optionsValue.length != 2) {
            this.$message.error("请选择分类层级");
            return;
          }
          this.inputModel.gtype = this.optionsValue[0];
          this.inputModel.gtypeTwo = this.optionsValue[1];
        }

        this.$emit("confirm", {
          inputModel: this.inputModel,
          level: this.level
        });
      },
      getSysGoodsList() {
        axios.post(api.mall.sysGoodsList)
          .then(res => {
            this.categoryList = res
          })
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