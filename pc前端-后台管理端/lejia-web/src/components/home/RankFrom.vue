<!-- 
@name: RankFrom.vue 
@description: 排行榜
@author: crj
@date: 2020/08/19
-->


<template>
  <div class="container">
    <div class="container-title">{{title}}</div>
    <el-table
      :header-cell-style="{background:'#FAFAFA'}"
      :data="tableData"
      style="width: 100%"
      class="table-fixed"
      max-height="600"
     
    >
      <template v-for="(item,index) in  tableContentData">
        <el-table-column
          :key="index"
          :label="item.label"
          :prop="item.propName"
          :sortable="item.sortable"
          v-if="item.propName!='index'"
           align="center"
           :width="item.propName=='name'?250:'auto'"
        ></el-table-column>
        <el-table-column :label="item.label" v-if="item.propName=='index'"  align="center">
          <template slot-scope="scope">
            <span class="default-label" :class="scope.$index<3?'top-label':''">{{scope.$index+1}}</span>
          </template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>

<script>
export default {
  props: {
    tableContentData: Array, //设置表格内容
    tableData: Array, //表格数据
    title: String //表格标题
  },
  data() {
    return {};
  },
  methods: {}
};
</script>

<style lang="less" scoped>

.container {
  width: 48%;
  padding-bottom: 20px;
  .container-title {
    text-align: center;
    padding-bottom: 20px;
  }
}
.default-label{
  width: auto;
}
.default-label,
.top-label {
  border-radius: 50%;
  display: inline-block;
  line-height: 20px;
  text-align: center;
}
.top-label {
  width: 20px;
  height: 20px;
  color: #fff;
  background: #409eff;
}
</style>