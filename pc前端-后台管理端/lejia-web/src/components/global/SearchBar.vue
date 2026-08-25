<!--
 * @Author: 沙晓
 * @Date: 2022-05-09 11:13:38
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-23 13:37:51
 * @Description: 全局组件--搜索组件
 * @FilePath: /lejia-web/src/components/global/SearchBar.vue
-->
<template lang="html">
  <div class="search-bar">
    <el-input
      :placeholder="placeholder"
      v-model="keywords"
      class="input-with-select"
    >
      <el-select v-model="searchKey" slot="prepend" placeholder="请选择">
        <el-option
          :label="item.name"
          :value="item.key"
          :key="item.name"
          v-for="item in selectOptions"
        ></el-option>
      </el-select>
      <el-button
        type="primary"
        size="medium"
        slot="append"
        icon="el-icon-search"
        @click.active="startSearch"
        >搜索</el-button
      >
    </el-input>
  </div>
</template>
<script>
export default {
  data() {
    return {
      keywords: "",
      searchKey: "" //选中项
    };
  },
  created() {},
  mounted() {
    //默认选中第一个
    if (this.selectOptions && this.selectOptions.length)
      this.searchKey = this.selectOptions[0].key;
  },
  methods: {
    clearData: function() {
      this.keywords = "";
      if (this.selectOptions && this.selectOptions.length)
        this.searchKey = this.selectOptions[0].key;
    },
    /**
     * 开始搜索
     */
    startSearch: function() {
      this.$emit("search", { key: this.searchKey, keywords: this.keywords });
    }
  },
  props: {
    selectOptions: {
      type: Array,
      default: () => {
        return [{ name: "名称", key: "name" }];
      }
    },
    placeholder: {
      type: String,
      default: "请输入..."
    }
  }
};
</script>
<style lang="less">
.search-bar {
  display: inline-block;
  width: 350px;
  height: 36px;
  margin: 5px;

  .el-input-group__append,
  .el-input-group__prepend,
  .el-input__inner {
    height: 36px;
  }

  .el-input-group__prepend {
    width: 130px;
  }

  .el-input__icon {
    line-height: 36px;
  }
}
</style>
