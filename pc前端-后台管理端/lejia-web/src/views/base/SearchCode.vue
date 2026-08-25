<template>
  <div class="table-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="首页" name="HOME" v-if="userIdentity != 1">
        <page-comp type="HOME"></page-comp>
      </el-tab-pane>
      <el-tab-pane label="分类" name="CATEGORY" v-if="userIdentity != 1">
        <page-comp type="CATEGORY"></page-comp>
      </el-tab-pane>
      <el-tab-pane label="滨海自营" name="SELF_MALL" v-if="userIdentity == 1">
        <page-comp type="SELF_MALL"></page-comp>
      </el-tab-pane>
      <el-tab-pane label="滨农优品" name="BNYP" v-if="userIdentity == 1">
        <page-comp type="BNYP"></page-comp>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import PageComp from './sub/SearchCode/Pages.vue'
export default {
  data() {
    return {
      activeName: 'HOME',
    }
  },
  computed: {
    userIdentity: function() {
      return this.$store.state.userIdentity; //1 运营商 2 市场 3公司
    },
  },
  mounted() {
    console.log('userIdentity', this.userIdentity);
    if(this.userIdentity == 1) {
      this.activeName = 'SELF_MALL'
    } else {
      this.activeName = 'HOME'
    }
  },
  components: {
    PageComp,
  },
  methods: {
    handleClick() {

    }
  }
}
</script>
