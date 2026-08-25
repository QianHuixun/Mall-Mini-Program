<template>
  <el-dialog title="修改退款金额" center width="60%" :visible.sync="visible" :closeOnClickModal="false" @close="handleClose"
    append-to-body>
    <div class="el-form">
      <div class="refund-box">
        <div class="refund-box_title">退款明细</div>
        <div class="refund-box_table">
          <div class="refund-table_thead">
            <div class="refund-table_thead_tr">
              <div class="refund-table_thead_th flex-1">
                商品
              </div>
              <div class="refund-table_thead_th">
                数量
              </div>
              <div class="refund-table_thead_th">
                重量
              </div>
              <div class="refund-table_thead_th">
                实付金额
              </div>
              <div class="refund-table_thead_th">
                退款金额
              </div>
              <div class="refund-table_thead_th">
                退款积分
              </div>
            </div>
          </div>
          <div class="refund-table_grouping" v-for="(item,index) in data.refundOrder" :key="index">
            <div class="refund-table_grouping__title">
              商户：{{item.name || "--"}} 摊位号：{{ item.booth || "--"}}
            </div>
            <div class="refund-table_tbody">
              <div class="refund-table_tbody_tr" v-for="(subItem,subIndex) in item.list" :key="subIndex">
                <div class="refund-table_tbody_td flex-1">
                  <div class="table-goods_box">
                    <el-image :src="subItem.photo"></el-image>
                    <div class="table-goods_box__right">
                      <div class="table-goods_box__title">{{subItem.goodsName}}</div>
                      <div class="table-goods_box__space">{{subItem.spaceName}}</div>
                    </div>
                  </div>
                </div>
                <div class="refund-table_tbody_td">
                  {{subItem.num}}
                </div>
                <div class="refund-table_tbody_td">
                  {{subItem.weight}}kg
                </div>
                <div class="refund-table_tbody_td">
                  ￥ {{subItem.sumPrice}}
                </div>
                <div class="refund-table_tbody_td">
                  <el-input type="number" min="0" max v-model="subItem.refundAmt"
                    @input="handleInput($event,index + ',' + subIndex)" />
                </div>
                <div class="refund-table_tbody_td">
                  {{subItem.refundPoint}}积分
                </div>
              </div>
            </div>
            <div class="refund-table_summary">共{{item.num}}件商品 合计退款 ￥{{item.sumAmt}}{{item.refundPoint ? "+"+item.refundPoint+ "积分" : ''}}</div>
          </div>
        </div>
      </div>
      <div class="refund-box">
        <div class="refund-box_title">退款信息</div>
        <div class="refund-box_container">
          <div class="refund-cell">
            <div class="refund-cell-text">退款商品总价</div>
            <div class="refund-cell-value">
              {{preRefund ? (preRefund.refundGoodsAmt || '0') : (data.refundGoodsAmt || '0')}}元</div>
          </div>
          <div class="refund-cell">
            <div class="refund-cell-text">配送费</div>
            <div class="refund-cell-value">{{preRefund ? (preRefund.refundPostage || '0') : data.refundPostage || '0'}}元
            </div>
          </div>
          <div class="refund-cell" v-if="preRefund ? (preRefund.refundCardTitle || false) : data.refundCardTitle || false">
            <div class="refund-cell-text">商品优惠券</div>
            <div class="refund-cell-value">
              {{preRefund ? (preRefund.refundCardTitle || '无') : data.refundCardTitle || '无'}}</div>
          </div>
          <div class="refund-cell" v-if="preRefund ? (preRefund.refundCardPostageTitle || false) : data.refundCardPostageTitle || false">
            <div class="refund-cell-text">配送优惠券</div>
            <div class="refund-cell-value">
              {{preRefund ? (preRefund.refundCardPostageTitle || '无') : data.refundCardPostageTitle || '无'}}</div>
          </div>
          <div class="refund-cell">
            <div class="refund-cell-text">退款合计</div>
            <div class="refund-cell-value" style="color:red;">
              {{preRefund ? (preRefund.refundAmt || '0') : data.currentRefundAmt || '0'}}</div>
          </div>
          <div class="refund-cell">
            <div class="refund-cell-text">退款积分</div>
            <div class="refund-cell-value">
              {{preRefund ? (preRefund.refundPoint || '0') : data.refundPoint || '0'}}积分</div>
          </div>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" type="success" @click="handleAgreeRefund" :loading="loading">
        同意退款
      </el-button>
      <el-button @click="handleClose">取消</el-button>

    </div>

  </el-dialog>
</template>
<script>
  import utils from '@/assets/js/utils';
  export default {
    data() {
      return {
        visible: false,
        loading: false,
        data: {},
        pkey: '',
        lines: [],
        timer: "",
        preRefund: null,
      }
    },
    methods: {
      handleInput(event, str) {
        console.log(event, str);
        clearTimeout(this.timer);
        this.timer = setTimeout(() => {
          // 处理输入值，只允许数字和两位小数
          let arr = str.split(","); // 以空字符串作为分隔符
          const list = this.data.refundOrder[arr[0]].list[arr[1]];
          list.refundAmt = this.formatPrice(event);
          if(list.refundAmt == ''){
            return;
          }
          if(list.initRefundAmt > list.refundAmt && list.refundPoint > 0) {
            list.refundPoint = 0;
          }
          let sumAmtList= 0,
          refundPointList = 0;
          this.data.refundOrder[arr[0]].list.forEach((subItem) => {
            sumAmtList = sumAmtList + Number(subItem.refundAmt);
            refundPointList = refundPointList + subItem.refundPoint;
          });
          this.data.refundOrder[arr[0]].sumAmt = sumAmtList;
          this.data.refundOrder[arr[0]].refundPoint = refundPointList;

          this.getRefund();
        }, 500); // 设置防抖时间，这里是300毫秒
      },
      getRefund() {
        this.lines = [];
        this.data.refundOrder.forEach((item) => {
          item.list.forEach((subItem) => {
            this.lines.push({
              pkey: subItem.pkey,
              refundAmt: subItem.refundAmt,
              num: subItem.num,
            });
          });
        });
        const params = {
          refundPkey: this.pkey,
          lines: this.lines
        }
        axios
          .post(api.sale.updateRefund_Pre, params, {
            headers: {
              Authorization: this.$store.state.token,
              'Content-Type': 'application/json',
            },
          })
          .then(res => {
            if (res) {
              this.preRefund = res;
            }
          });
      },
      show: function (pkey) {
        this.visible = true;
        this.pkey = pkey;
        this.preRefund = null;
        this.getData();
      },
      /**
       * 关闭弹出框
       */
      handleClose: function () {
        this.visible = false;
      },
      //格式化价格
      formatPrice: function (price) {
        console.log(price, utils.formatPrice(price))
        return utils.formatPrice(price);
      },
      // 同意退款
      handleAgreeRefund: function () {
        this.lines = [];
        let status = true;
        this.data.refundOrder.forEach((item) => {
          item.list.forEach((subItem) => {
            if (subItem.refundAmt === "") {
              this.$message.warning("退款金额不可为空！");
              status = false;
            }

            this.lines.push({
              pkey: subItem.pkey,
              refundAmt: subItem.refundAmt,
              num: subItem.num,
            });
          });
        });
        if (!status) return;
        const params = {
          refundPkey: this.pkey,
          lines: this.lines
        }
        axios
          .post(api.sale.updateRefund, params, {
            headers: {
              Authorization: this.$store.state.token,
              'Content-Type': 'application/json',
            },
          })
          .then(res => {
            if (res) {
              this.$message.success("退款成功");
              this.visible = false;
              this.$emit("refresh");
            }
          });
      },
      getData() {
        const params = {
          pkey: this.pkey
        };
        axios
          .post(api.sale.refundGet, this.$qs.stringify(params))
          .then(res => {
            if (res) {
            res.refundOrder.forEach((item,index)=> {
              item.list.forEach((subItem,subIndex)=> {
                res.refundOrder[index].list[subIndex].initRefundAmt = subItem.refundAmt;
              });
            });
            this.data = res;
            }
          });
      },
    }
  }
</script>

<style lang="less" scoped>
  .refund-box_table {

    .refund-table_thead,
    .refund-table_tbody {
      border-top: 1px solid #F2F4F7;
      border-right: 1px solid #F2F4F7;
    }

    .refund-table_thead_tr,
    .refund-table_tbody_tr {
      display: flex;
    }

    .refund-table_thead_th {
      font-weight: bold;
    }

    .refund-table_thead_th,
    .refund-table_tbody_td {
      display: flex;
      align-items: center;
      padding: 8px;
      width: 150px;
      border-bottom: 1px solid #F2F4F7;
      border-left: 1px solid #F2F4F7;
    }

    .refund-table_grouping {
      margin-bottom: 10px;

      .refund-table_grouping__title {
        background: #F2F4F7;
        height: 35px;
        line-height: 35px;
        padding: 0 10px;
        font-weight: bold;
        font-size: 14px;
      }
    }


    .table-goods_box {
      display: flex;

      .el-image {
        width: 45px;
        height: 45px;
        border-radius: 8px;
        margin-right: 5px;
      }

      .table-goods_box__space {
        margin-top: 5px;
        color: #999;
      }
    }

    .flex-1 {
      flex: 1;
    }

    .refund-table_summary {
      background: #F2F4F7;
      height: 30px;
      line-height: 30px;
      padding: 0 8px;
      font-size: 12px;
      text-align: right;
    }

  }

  .refund-box_title {
    font-weight: bold;
    font-size: 18px;
    height: 35px;
    line-height: 35px;
  }

  .refund-cell {
    display: flex;
    padding: 3px 0;
    line-height: 30px;

    .refund-cell-text {
      display: flex;
      width: 8em;
      font-weight: bold;
      text-align: justify;
      text-align-last: justify;
      align-items: center;
      margin-right: 10px;
      color: #333;
      background: #F2F4F7;
      padding: 0 10px;
    }

    .refund-cell-value {
      flex: 1;
      color: #666;

      .refund-cell-value_images {
        .el-image {
          width: 70px;
          height: 70px;
          border-radius: 8px;

          &+.el-image {
            margin-left: 8px;
          }
        }
      }
    }
  }
</style>