<template>
  <el-tabs v-model="activeName">
    <el-tab-pane label="订单统计" name="order">
      <order-sale ref="orderSale"></order-sale>
    </el-tab-pane>
    <el-tab-pane label="商品统计" name="goods">
      <goods-sale ref="goodsSale"></goods-sale>
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import OrderSale from './sub/JDSale/Order.vue'
import GoodsSale from './sub/JDSale/Goods.vue'
export default {
  data() {
    return {
      activeName: 'order'
    }
  },
  components: {
    OrderSale,
    GoodsSale
  }
}
</script>

<style>

</style>