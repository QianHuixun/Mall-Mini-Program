<template>
  <el-tabs v-model="activeName">
    <el-tab-pane label="供应商统计" name="supplierSaleSub">
      <supplier-sale-sub v-if="activeName === 'supplierSaleSub'"></supplier-sale-sub>
    </el-tab-pane>
    <el-tab-pane label="明细统计" name="supplierSaleSubDetail">
      <supplier-sale-sub-detail v-if="activeName === 'supplierSaleSubDetail'"></supplier-sale-sub-detail>
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import supplierSaleSub from './sub/supplierSale/supplierSaleSub.vue'
import supplierSaleSubDetail from './sub/supplierSale/supplierSaleSubDetail.vue'
export default {
  data() {
    return {
      activeName: 'supplierSaleSub'
    }
  },
  components: {
    supplierSaleSub,
    supplierSaleSubDetail,
  }
}
</script>

<style>

</style>