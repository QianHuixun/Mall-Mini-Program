<!-- 
@name: GoodsLike.vue 
@description: 商品市场推荐管理
@author: 池仁杰
@date: 2022/01/05
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="850px">
    <div class="dialog-content">
      <div  class="left-content">
        <div class="title">未选商品</div>
        <el-input prefix-icon="el-icon-search" v-model="searchData.left" placeholder="请入商品名称或规格"></el-input>
        <el-checkbox-group v-model="inputModel.left">
          <el-checkbox label="复选框 A"></el-checkbox>
          <el-checkbox label="复选框 B"></el-checkbox>
          <el-checkbox label="复选框 C"></el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="left-btn">
        <el-button type="primary" icon="el-icon-arrow-left"></el-button>
      </div>
      <div class="right-btn">
        <el-button type="primary" icon="el-icon-arrow-right"></el-button>
      </div>
      <div class="right-content">
      </div>
    </div>
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
import qs from 'qs';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        left: [],
        right: [],
      },
      searchData: {
        left: '',
        right: '',
      },
    };
  },
  mounted() {},
  methods: {
    /**
     *@desc 清空数据
     */
    clearData: function () {
      this.inputModel = {};
    },
    /**
     * 初始化数据
     */
    initData() {},
    show: function () {
      this.visible = true;
      this.initData();
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
    handleSubmit: function () {},
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
.dialog-content {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
