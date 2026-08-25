<template>
  <div class="table-container">
    <el-tabs  v-model="activeName">
      <el-tab-pane label="热力豆管理" name="manage">
        <manage @refresh="handleRefresh"></manage>
      </el-tab-pane>
      <el-tab-pane label="热力豆明细" name="detail">
        <detail ref="detail"></detail>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import manage from './MSD/manage.vue';
import detail from './MSD/detail.vue';
export default {
  data() {
    return {
      activeName: 'manage',
    }
  },
  components: {
    manage,
    detail,
  },
  methods: {
    handleRefresh() {
      this.$refs.detail.getData()
    }
  }
}
</script>

<style>

</style>