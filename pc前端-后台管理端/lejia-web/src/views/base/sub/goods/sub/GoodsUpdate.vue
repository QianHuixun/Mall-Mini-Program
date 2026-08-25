<!-- 
@name: GoodsUpdate.vue 
@description: 商品库中心--编辑模板 
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="商品名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入商品名称"></el-input>
      </el-form-item>
      <el-form-item label="所属分类" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.gtype" ref="gtypeSelect" placeholder="商品分类" v-if="type=='two'">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typeList"></el-option>
        </el-select>
        <el-cascader v-model="goodsType" ref="twoGtypeSelect" :options="typeList" filterable :props="props" v-else @change="handleChange">
        </el-cascader>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth" >
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序" v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort =val;}"></el-input>
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

export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        name: "",
        gtype: "",
        sort: "0",
        enabled: true,
        twoGtype: '',
      },
      goodsType: [],
      typeList: [],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
      }, //级联选择器配置
    };
  },
  mounted() {
    dropdown.getType().then(result => {
      this.typeList = result.content;
    });
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        name: "",
        gtype: "",
        sort: "0",
        enabled: true,
        twoGtype: '',
      };
      this.goodsType = []
    },
    /**
     * 初始化数据
     */
    initData: function({ inputModel, row }) {
      this.inputModel = inputModel;
      this.goodsType = [row.gtype, row.twoGtype]
    },
    show: function() {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
      this.$emit("hide");
    },
    handleChange(value) {
      this.inputModel.twoGtype = value[1];
    },
    /**
     * 处理提交
     */
    handleSubmit: function() {
      if (!this.inputModel.name) {
        this.$message.error("请输入商品名称");
        this.$refs.nameInput.focus();
        return;
      }

      if (this.type === 'two' && !this.inputModel.gtype) {
        this.$message.error("请选择商品分类");
        this.$refs.gtypeSelect.focus();
        return;
      }

      if (this.type === 'three' && !this.inputModel.twoGtype) {
        this.$message.error("请选择商品分类");
        this.$refs.twoGtypeSelect.focus();
        return;
      }

      if (!this.inputModel.sort) {
        this.inputModel.sort = 0;
      }
      
      this.$emit("confirm", { inputModel: this.inputModel });
    }
  },
  props: {
    title: {
      type: String,
      default: "新增"
    },
    type: {
      type: String,
      default: 'two'
    }
  }
};
</script>
<style lang="less" scoped>
/deep/ .el-form{
 overflow: visible !important;
}
</style>