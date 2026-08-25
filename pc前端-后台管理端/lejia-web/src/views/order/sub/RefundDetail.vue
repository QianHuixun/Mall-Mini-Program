<!--
 * @Author: 沙晓
 * @Date: 2024-03-18 22:49:57
 * @LastEditors: 沙晓
 * @LastEditTime: 2026-07-02 17:08:50
 * @Description: 退款订单详情
 * @FilePath: /lejia-web/src/views/order/sub/RefundDetail.vue
-->
<template>
  <el-dialog
    :title="title"
    center
    width="60%"
    :visible.sync="visible"
    :closeOnClickModal="false"
    @close="handleClose"
  >
    <div class="el-form">
      <div class="refund-tips">{{data.pstime}} 送达</div>
      <div class="refund-box-group flex">
        <div class="refund-box">
          <div class="refund-box_title">收件人信息</div>
          <div class="refund-box_container">
            <div class="refund-cell">
              <div class="refund-cell-text">姓名</div>
              <div class="refund-cell-value">{{data.addr ? data.addr.name : "--"}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">手机</div>
              <div class="refund-cell-value">{{data.addr ? data.addr.mobile  : "--"}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">地址</div>
              <div class="refund-cell-value">{{ data.addr ? (data.addr.distance/1000).toFixed(2) : '--' }}km  |  {{
            data.addr && data.addr.addr  ? data.addr.addr : ""
          }} {{ data.addr && data.addr.addrDetail ? data.addr.addrDetail : "" }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送方式</div>
              <div class="refund-cell-value">{{distributionType[data.distributionType]}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">自提/取货码</div>
              <div class="refund-cell-value">{{ data.pickupCode || '--' }}</div>
            </div>
          </div>
        </div>
        <div class="refund-box" v-if="data.orderOir !== 'POINTS_MALL'">
          <div class="refund-box_title">骑手信息</div>
          <div class="refund-box_container">
            <div class="refund-cell">
              <div class="refund-cell-text">骑手类型</div>
              <div class="refund-cell-value">{{ data.logistics || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">派单时间</div>
              <div class="refund-cell-value">{{ data.pstime || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送状态</div>
              <div class="refund-cell-value">{{ data.thirdPartyStatusName || "--" }}  <el-button type="text" @click="getData">刷新</el-button>
              </div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">姓名</div>
              <div class="refund-cell-value">{{ data.courier ? data.courier.name : "--" }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">手机</div>
              <div class="refund-cell-value">{{ data.courier ? data.courier.mobile : "--" }}</div>
            </div>
          </div>
        </div>
        <div class="refund-box" v-else>
          <div class="refund-box_title">物流信息</div>
          <div class="refund-box_container">
            <div class="refund-cell">
              <div class="refund-cell-text">快递公司</div>
              <div class="refund-cell-value">{{ data.logistics || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">快递单号</div>
              <div class="refund-cell-value">{{ data.kdCode || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">发货时间</div>
              <div class="refund-cell-value">{{ data.orderExpressInfo ? data.orderExpressInfo.pickupTime : '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">物流状态</div>
              <div class="refund-cell-value">{{ data.orderExpressInfo ? data.orderExpressInfo.statusName : '--' }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="refund-box">
          <div class="refund-box_title">订单信息</div>
          <div class="refund-box_container flex-wrap">
            <div class="refund-cell">
              <div class="refund-cell-text">订单状态</div>
              <div class="refund-cell-value">{{status[data.status]}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">购买用户</div>
              <div class="refund-cell-value">{{ data.memberName || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">手机号</div>
              <div class="refund-cell-value">{{data.addr ? data.addr.mobile : '--'}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">商品类型</div>
              <div class="refund-cell-value">{{ data.orderTypeName || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">重量</div>
              <div class="refund-cell-value">{{ data.weight || '--' }}kg</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">订单编号</div>
              <div class="refund-cell-value">{{ data.code || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">下单时间</div>
              <div class="refund-cell-value">{{ data.createdTime || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">支付时间</div>
              <div class="refund-cell-value">{{ data.createdTime || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">发货时间</div>
              <div class="refund-cell-value">{{ data.pstime || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">商品金额</div>
              <div class="refund-cell-value">{{data.amto || '0'}}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送费</div>
              <div class="refund-cell-value">{{ data.postage  || '0'}}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">商品优惠</div>
              <div class="refund-cell-value">{{ data.cardAmt || '0' }}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送优惠</div>
              <div class="refund-cell-value">{{ data.cardPostageAmt || '0' }}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">合计金额</div>
              <div class="refund-cell-value">{{ data.amtall || '0' }}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">支付金额</div>
              <div class="refund-cell-value">{{data.amtn || '0'}}元  <span v-if="data.refundAmt" style="color:#ED4528">已退款{{ data.refundAmt}}元</span></div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">支付积分</div>
              <div class="refund-cell-value">{{data.pointn || '0'}}积分 </div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">支付方式</div>
              <div class="refund-cell-value">{{ data.payTypeName || '--' }}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">订单备注</div>
              <div class="refund-cell-value">{{data.remark || '--'}}</div>
            </div>
          </div>
        </div>
        <div class="refund-box">
          <div class="refund-box_title">退款信息</div>
          <div class="refund-box_container">
            <div class="refund-cell">
              <div class="refund-cell-text">退款商品总价</div>
              <div class="refund-cell-value">{{data.refundGoodsAmt || '0'}}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送费</div>
              <div class="refund-cell-value">{{data.refundPostage || '0'}}元</div>
            </div>
            <div class="refund-cell" v-if="data.refundPoint">
              <div class="refund-cell-text">退款积分</div>
              <div class="refund-cell-value">{{data.refundPoint || '0'}}积分</div>
            </div>
            <div class="refund-cell" v-if="data.refundWeixinAmt">
              <div class="refund-cell-text">微信退款</div>
              <div class="refund-cell-value">{{data.refundWeixinAmt || '0'}}元</div>
            </div>
            <div class="refund-cell" v-if="data.refundOtherAmt">
              <div class="refund-cell-text">{{payTypeName[data.payType]}}退款</div>
              <div class="refund-cell-value">{{data.refundOtherAmt || '0'}}元</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">商品优惠券</div>
              <div class="refund-cell-value">{{data.refundCardTitle || '无'}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">配送优惠券</div>
              <div class="refund-cell-value">{{data.refundCardPostageTitle || '无'}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">退款状态</div>
              <div class="refund-cell-value">{{ data.refundStatusName }}</div>
            </div>
            <div class="refund-cell" v-if="data.refundStatus === 'REFUND_REFUSE'">
              <div class="refund-cell-text">失败原因</div>
              <div class="refund-cell-value">{{ data.delDesc}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">退款原因</div>
              <div class="refund-cell-value">{{data.reason || '--'}}</div>
            </div>
            <div class="refund-cell">
              <div class="refund-cell-text">描述与凭证</div>
              <div class="refund-cell-value">
                {{ data.describe }}
                <div class="refund-cell-value_images">
                  <el-image  :src="item"  v-for="(item,index) in data.refundPhoto" :key="index" :preview-src-list="data.refundPhoto"></el-image>
                </div>
                </div>
            </div>
          </div>
        </div>
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
              商户：{{item.name || "--"}}      摊位号：{{ item.booth || "--"}}
              </div>
              <div class="refund-table_tbody">
                <div class="refund-table_tbody_tr" v-for="(subItem,subIndex) in item.list" :key="subIndex">
                  <div class="refund-table_tbody_td flex-1">
                  <div class="table-goods_box">
                    <el-image :src="subItem.photo" ></el-image>
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
                  {{subItem.weight}} kg
                </div>
                <div class="refund-table_tbody_td">
                  ￥ {{subItem.sumPrice}}
                </div> 
                <div class="refund-table_tbody_td">
                  ￥ {{subItem.refundAmt}}
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
    </div>
    <div slot="footer" class="dialog-footer" v-if="data.refundStatus === 'REFUND_APPLYING'">
      <el-button size="medium" type="primary" @click="showUpdateDialog" :loading="loading">
        修改退款金额
      </el-button>
      <!-- <el-popconfirm title="确定要同意退款吗？" > -->
        <el-button size="medium" type="success"  @click="handleAgreeRefund" :loading="loading" >
          同意退款
        </el-button>
      <!-- </el-popconfirm> -->
      <el-button size="medium" type="danger" @click="showRefuseDialog" :loading="loading">
        拒绝退款
      </el-button>
    </div>
    <!-- 拒绝退款 -->
    <el-dialog title="拒绝退款" center width="30%" append-to-body :visible.sync="visibleRefuse"
    :closeOnClickModal="false"  @close="handleRefuseClose">
    <el-input type="textarea" :rows="3" placeholder="请输入拒绝退款理由" v-model="delDesc"></el-input>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleRefuseRefund">确 定</el-button>
      <el-button @click="handleRefuseClose">取 消</el-button>
    </span>
  </el-dialog>
  <refund-update ref="refundUpdate" @refresh="refreshTableData"></refund-update>
  </el-dialog>
</template>
<script>
import refundUpdate from "./RefundUpdate.vue";
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      visibleRefuse: false,
      loading: false,
      data: {},
      pkey: '',
      distributionType: {
        PICKUP: "自提",
        IMMEDIATELY: "立刻配送",
        ORDERED: "预约",
        EXCHANGE: "自行兑换",
        SEND_DIRECTLY: "直接送达",
      },
      status: {
        UNPAID_ORDER: "未付款",
        DELIVERED_ORDER: "待发货",
        SHIPPED_ORDER: "已发货",
        ARRIVED_ORDER: "已到货",
        CONFIRM_ORDER: "确认",
        REFUND_APPLICATION_ORDER: "退款申请",
        REFUNDED_ORDER: "已退款",
        VOID_ORDER: "作废"
      },
      refundStatus: {
        REFUND_APPLYING: "申请中",
        REFUND_AGREE: "同意",
        REFUND_FINAL: "已退款",
        REFUND_REFUSE: "拒绝"
      },
      // 拒绝退款
      delDesc: "",
      payTypeName: {
        ORDER_ZHIFUBAO: '支付宝',
        ORDER_WEIXIN: '微信',
        ORDER_ELECTRONIC_ACCOUNT:  localStorage.getItem("ascription") == 22 || localStorage.getItem("ascription")== 13 ?  'I DO' : '钱包',
        ZXYW_WEIXIN: '中信银行',
        NM_MEMBER: "会员卡",
        ORDER_MSD: '热力豆',
        MSD_COMBINATION: '热力豆',
        ELECTRONIC_ACCOUNT_COMBINATION:localStorage.getItem("ascription") == 22 || localStorage.getItem("ascription") == 13 ?  'I DO' : '钱包'
      },
    };
  },
  mounted() {},
  components: {
    refundUpdate
  },
  methods: {
    show: function(row) {
      this.visible = true;
      this.pkey = row.pkey;
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    handleClose: function() {
      this.visible = false;
    },
    // 显示修改退款dialog
    showUpdateDialog: function() {
      this.$refs.refundUpdate.show(this.pkey);
    },
    // 拒绝退款  dialog关闭
    handleRefuseClose: function() {
      this.visibleRefuse = false;
    },
    // 拒绝退款  dialog显示
    showRefuseDialog: function() {
      this.visibleRefuse = true;
    },
    handleAgreeRefund: function() {
      console.log(11)
      const params = {
        pkey: this.pkey,
      }
      axios
        .post(api.sale.agreeRefund, this.$qs.stringify(params))
        .then(res => {
          console.log(res)
          if (res) {
              this.$message.success("同意退款成功");
              this.getData();
              this.$emit('refresh');
          }
        });
    },
    refreshTableData(){
      this.getData();
      this.$emit('refresh');
    },
    handleRefuseRefund: function() {
      if(this.delDesc === "") {
        this.$message.warning("请输入拒绝退款理由");
        return;
      }
      const params = {
        pkey: this.pkey,
        delDesc: this.delDesc
      }
      axios
        .post(api.sale.refuseRefund, this.$qs.stringify(params))
        .then(res => {console.log(res)
          if (res) {
            console.log(res)
              this.$message.success("拒绝退款成功");
              this.visibleRefuse = false;
              this.getData();
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
            this.data = res;
          }
        });
    },
  },
  props: {
    title: {
      type: String,
      default: "退款订单详情"
    }
  }
};
</script>

<style lang="less" scoped>
@import url("~@/assets/css/variable.less");

.refund-tips {
  background: #F2F4F7;
  padding: 0 12px;
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  font-size: 16px;
  font-weight: bold;
}

.flex {
  display: flex;
}

.refund-box {
  margin-top: 12px;
  flex:1;
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
    padding:0 10px;
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

.flex-wrap {
  display: flex;
  flex-wrap: wrap;
}

.flex-wrap>.refund-cell {
  width: 50%;
}

.dialog-footer  {
  .el-popover__reference {
    margin: 0 10px;
  }
}


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
    width: 100px;
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
    .table-goods_box__right {
      flex: 1;
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

</style>
