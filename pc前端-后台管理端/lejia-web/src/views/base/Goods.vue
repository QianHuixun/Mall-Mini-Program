<template>
  <el-tabs v-model="activeName">
    <el-tab-pane label="二级分类" name="SubCategory">
      <sub-category v-if="activeName === 'SubCategory'"></sub-category>
    </el-tab-pane>
    <el-tab-pane label="三级分类" name="SubSubCategory">
      <sub-sub-category v-if="activeName === 'SubSubCategory'"></sub-sub-category>
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import SubCategory from './sub/goods/SubCategory.vue'
import SubSubCategory from './sub/goods/SubSubCategory.vue'
export default {
  data() {
    return {
      activeName: 'SubCategory'
    }
  },
  components: {
    SubCategory,
    SubSubCategory,
  }
}
</script>

<style>

</style>