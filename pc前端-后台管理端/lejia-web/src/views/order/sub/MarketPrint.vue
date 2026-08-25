<template>
  <div style="display: none">
    <div id="print">
      <div style="width: 48mm;line-height: 18px;font-size: 10px;padding-left: 0px;box-sizing: border-box;">
        <div class="header">
          <div>顾客联</div>
          <div>
            <div>#911</div>
            <div>自提</div>
          </div>
          <div>市场电话：19900002222</div>
        </div>
        <hr>
        <div class="day-definite">
          <div>期望送达时间</div>
          <div>12-12 12:00-12:30</div>
        </div>
        <hr>
        <div class="remarks">【备注】送点小葱</div>
        <div class="goods-list">
          <div>
            <hr>
            商品
            <hr>
          </div>
          <div>
            <div  v-for="(item, index) in printData.goodsList">
              <div>{{index}}.{{item.goodsName}}</div>
              <div>
                <div>x{{item.goodsNum}}</div>
                <div>x{{item.spaceName}}</div>
                <div>x{{item.totalPricen}}</div>
              </div>
            </div>
          </div>
        </div>
        <hr>
        <div class="amt-list">
          <div>
            <div>商品金额</div>
            <div>25.00</div>
          </div>
          <div>
            <div>配送费</div>
            <div>8.00</div>
          </div>
          <div>
            <div>优惠金额</div>
            <div>-2.00</div>
          </div>
        </div>
        <hr>
        <div class="total-amt">
          <div>总件数：4</div>
          <div>合计：31.00</div>
        </div>
        <hr>
        <div class="user-info">
          <div>浙江省温州市瓯海区xxxxxxxxxxxxx</div>
          <div>16778888999</div>
          <div>黄**</div>
        </div>
        <div class="order-number">
          <div>订单编号：999393993939393</div>
          <div>下单时间：2023-12-12 12:12:12</div>
        </div>
    </div>
  </div>
  <!-- 打印内容 end -->
</template>

<script>
export default {
  data() {
    return {
      printData: {}
    }
  },
  methods: {
    handlePrint(row) {
      let LODOP = getLodop();
      console.log(typeof LODOP == 'undefined');
      if (typeof LODOP == 'undefined') {
        this.$confirm('未安装打印插件，请下载安装?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            location.href = api.mall.downPrint;
          })
          .catch(() => {
            return;
          });
      }
      LODOP.PRINT_INIT('订单打印');
      LODOP.SET_PRINT_STYLE('FontSize', 12);
      LODOP.SET_PRINT_STYLE('Bold', 1);
      LODOP.SET_PRINT_PAGESIZE(3, 800, 10, '');
      this.printData = row;
      this.printData.date = row.createdTime.substring(0, 10);
      // this.makeQrcode(row.qrCode);

      setTimeout(() => {
        LODOP.ADD_PRINT_HTM(
          20,
          0,
          250,
          400,
          document.getElementById('print').innerHTML
        );
        LODOP.PREVIEW();
      }, 1000);
    }
  }
}
</script>

<style>

</style>