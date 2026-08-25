<template>
  <div class="table-container">
    <el-tabs  v-model="activeName">
      <el-tab-pane label="滨海自营" name="first">
        <point-ads position="ADVERT_POSITION_POINTS_MALL"></point-ads>
      </el-tab-pane>
      <el-tab-pane label="滨农优品" name="second">
        <point-ads position="ADVERT_POSITION_BNYP"></point-ads>
      </el-tab-pane>
      <el-tab-pane label="民生专区" name="third">
        <point-ads position="ADVERT_POSITION_MSD"></point-ads>
      </el-tab-pane>
      <el-tab-pane label="京东专区" name="fourth">
        <point-ads position="ADVERT_POSITION_JD"></point-ads>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import PointAds from './PointAds.vue'
export default {
  data() {
    return {
      activeName: 'first',
    }
  },
  components: {
    PointAds,
  }
}
</script>

<style>

</style>