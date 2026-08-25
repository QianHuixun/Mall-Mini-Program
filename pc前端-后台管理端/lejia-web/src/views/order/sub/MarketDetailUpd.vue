<!-- 
@name: MallDetailUpd.vue 
@description: 详情--编辑模板 
@author: zs
-->
<template>
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" @close="handleClose">
    <div class="el-form">
      <p>基本信息</p>
      <div class="base">
        <div class="base-item">
          <span class="base-item-title">订单编号：</span>
          <span class="base-item-content">{{ data.code }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">下单时间：</span>
          <span class="base-item-content">{{ data.createdTime }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">发货时间：</span>
          <span class="base-item-content">{{ data.pstime }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">购买用户：</span>
          <span class="base-item-content">{{ data.memberName }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">商品类型：</span>
          <span class="base-item-content">{{ data.orderTypeName }}</span>
        </div>
        <!-- <div class="base-item">
          <span class="base-item-title">要求发货时间：</span>
          <span class="base-item-content">2020131212121</span>
        </div> -->
        <div class="base-item">
          <span class="base-item-title">配送方式：</span>
          <span class="base-item-content">{{
            data.distributionType == "PICKUP" ? "自提" : (data.distributionType == "DINE_IN" ? "堂食" : "配送")
          }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">重量：</span>
          <span class="base-item-content">{{ data.weight }}kg</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">配送费：</span>
          <span class="base-item-content">{{ data.postage }}元</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">骑手类型：</span>
          <span class="base-item-content">{{ data.logistics }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">派单时间：</span>
          <span class="base-item-content">{{ data.pstime }}</span>
        </div>
        <div class="base-item" style="width:100%">
          <span class="base-item-title">配送状态：</span>
          <span class="base-item-content">
            {{ data.thirdPartyStatusName || "--" }}
            <!-- 
              THIRD_PARTY_INIT:初始化;
              THIRD_PARTY_PENDING:待接单;
              THIRD_PARTY_PICKING_UP:取货中;
              THIRD_PARTY_DELIVERY:配送中;
              THIRD_PARTY_CONFIRM:已完成;
              THIRD_PARTY_VOID:已取消;
              THIRD_PARTY_ERROR:配送异常
             -->
            <el-button type="text" @click="handleRefresh">刷新</el-button>
            <el-button type="text" @click="handleThirdDeliveryChange" v-if=" data.expressStatus == null && 
               ( data.thirdPartyStatus === 'THIRD_PARTY_ERROR' ||
                  data.thirdPartyStatus === 'THIRD_PARTY_PENDING' ||
                  data.thirdPartyStatus === 'THIRD_PARTY_VOID')
              ">
              更换配送公司
            </el-button>
            <el-button type="text" @click="handleThirdDeliveryCancel"
              v-if="data.thirdPartyStatus === 'THIRD_PARTY_PENDING'">
              <!-- <el-button type="text" @click="handleThirdDeliveryCancel"> -->
              取消配送订单
            </el-button>
            <el-button type="text" @click="handleThirdDeliveryFinish"
              v-if="data.thirdPartyStatus === 'THIRD_PARTY_VOID' &&  data.expressStatus == null ">
              已送达
            </el-button>
            <el-button type="text" @click="handlePaidan"
              v-if="(data.thirdPartyStatus === 'THIRD_PARTY_VOID' || data.thirdPartyStatus === 'THIRD_PARTY_ERROR') && data.expressStatus == null ">
              自有骑手派单
            </el-button>
          </span>
        </div>
        <div class="base-item">
          <span class="base-item-title">配送员：</span>
          <span class="base-item-content">{{ data.courier ? data.courier.name : "--" }}
            {{ data.courier ? data.courier.mobile : "--" }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">订单总金额：</span>
          <span class="base-item-content">{{ data.amtall }}元</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">实际支付：</span>
          <span class="base-item-content">{{
            data.amtn ? data.amtn + "元" : "--"
          }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title" style="color: #ED4528;">退款总金额：</span>
          <span class="base-item-content" style="color: #ED4528;">{{ data.refundAmt || '0' }}元</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">支付类型：</span>
          <span class="base-item-content">{{ data.payTypeName }}</span>
        </div>
        <div class="base-item" v-show="data.orderType == 'MARKET_ORDER'">
          <span class="base-item-title">商品优惠：</span>
          <span class="base-item-content">{{ data.cardAmt }}元</span>
        </div>
        <div class="base-item" v-show="data.orderType == 'MARKET_ORDER'">
          <span class="base-item-title">配送优惠：</span>
          <span class="base-item-content">{{ data.cardPostageAmt }}元</span>
        </div>
        <div class="base-item" v-if="data.weixinAmt">
          <span class="base-item-title">微信支付：</span>
          <span class="base-item-content">{{data.weixinAmt}}元</span>
        </div>
        <div class="base-item refund-color" v-if="data.refundWeixinAmt">
          <span class="base-item-title">微信退款：</span>
          <span class="base-item-content">{{data.refundWeixinAmt}}元</span>
        </div>
        <div class="base-item" v-if="data.pointn">
          <span class="base-item-title">消耗积分：</span>
          <span class="base-item-content">{{data.pointn}}积分</span>
        </div>
        <div class="base-item refund-color" v-if="data.refundPoint">
          <span class="base-item-title">退款积分：</span>
          <span class="base-item-content">{{data.refundPoint}}积分</span>
        </div>
        <div class="base-item" v-if="data.otherAmt">
          <span class="base-item-title">{{payTypeName[data.payType]}}支付：</span>
          <span class="base-item-content">{{data.otherAmt}}元</span>
        </div>
        <div class="base-item refund-color" v-if="data.refundOtherAmt">
          <span class="base-item-title">{{payTypeName[data.payType]}}退款：</span>
          <span class="base-item-content">{{data.refundOtherAmt}}元</span>
        </div>
        
        <div class="base-item" v-show="inputModel.orderType == 'MARKET_ORDER'">
          <span class="base-item-title">卡券编号：</span>
          <span class="base-item-content">{{ data.cardCode }}</span>
        </div>
        <div class="base-item" v-show="inputModel.orderType == 'SHARE_ORDER'">
          <span class="base-item-title">佣金：</span>
          <span class="base-item-content">{{ data.commn }}元</span>
        </div>
        <div class="base-item" v-show="inputModel.orderType == 'SHARE_ORDER'">
          <span class="base-item-title">分享人：</span>
          <span class="base-item-content">{{ data.tjr }}</span>
        </div>

      </div>
      <p style="margin-bottom: 12px;" v-if="data.arrivedPhoto">菜品送达照片</p>
      <el-image v-for="(item,index) in data.arrivedPhoto" :key="index"
        style="width: 100px; height: 100px;margin-bottom: 12px;margin-right: 12px;" :src="item"
        :preview-src-list="data.arrivedPhoto">
      </el-image>
      <p style="margin-bottom: 12px;border-top: 1px solid #e9ecf3;padding-top: 12px;" v-if="data.arrivedPhoto"></p>
      <p style="margin-bottom: 12px;">客户留言</p>
      <el-input type="textarea" :disabled="true" :rows="3" placeholder="请输入内容" v-model="data.remark">
      </el-input>
      <p style="margin-top: 12px;border-top: 1px solid #e9ecf3;padding-top: 12px;">
        {{ data.distributionType == "PICKUP" ? "自提人信息" : (data.distributionType == "DINE_IN" ? "就餐信息" : "收货人信息" )}}
      </p>
      <div class="consignee" v-if="data.distributionType != 'DINE_IN'">
        <div class="base-item" style="width:50%">
          <span class="base-item-title">姓名：</span>
          <span class="base-item-content">{{
            data.addr ? data.addr.name : "--"
          }}</span>
        </div>
        <div class="base-item" style="width:50%">
          <span class="base-item-title"> {{ data.distributionType == "PICKUP" ? "自提时间：" : "配送时间：" }}</span>
          <span
            :class="{ 'base-item-content': true, 'red-font': data.pstime }">{{data.distributionType == "PICKUP" ? "" : (data.distributionType == "ORDERED" ? "预约送达 " : "立即送达 ")}}{{ data.pstime ? data.pstime : "--" }}</span>
        </div>
        <div class="base-item" style="width:50%">
          <span class="base-item-title">手机：</span>
          <span class="base-item-content">{{
            data.addr ? data.addr.mobile : "--"
          }}</span>
        </div>
        <div class="base-item" style="width:50%">
          <span class="base-item-title">{{
              data.distributionType === "PICKUP" ? "自提码" : "取货码"
            }}：</span>
          <span
            :class="{ 'base-item-content': true, 'red-font': data.pickupCode }">{{ data.pickupCode ? data.pickupCode : "--" }}</span>
        </div>
        <div class="base-item" style="width:100%">
          <span class="base-item-title">地址：</span>
          <span class="base-item-content">{{
            data.addr && data.addr.addr  ? data.addr.addr : ""
          }} {{ (data.addr && data.addr.addrDetail) || "" }}</span>
        </div>
      </div> 
      <div class="consignee" v-else>
        <div class="base-item" style="width:50%">
          <span class="base-item-title">桌号：</span>
          <span class="base-item-content">{{
            data.addr ? data.addr.addrDetail : "--"
          }}</span>
        </div>
      </div>
      <p style="margin-bottom: 12px;">商品信息</p>
      <el-table :data="data.list2" :loading="loading" border style="width: 100%"
        v-if="marketType === 'MARKET_SHOPPING_MALL'">
        <el-table-column label="商品名称" prop="goodsName"></el-table-column>
        <el-table-column label="规格" prop="spaceName"></el-table-column>
        <el-table-column label="类型" prop="mtypeName"></el-table-column>
        <el-table-column label="数量" prop="num"></el-table-column>
      </el-table>
      <el-table :loading="loading" border style="width: 100%" class="table-box" v-else>
        <el-table-column label="商品名称" prop="goodsName"> </el-table-column>
        <el-table-column label="规格" prop="spaceName"> </el-table-column>
        <el-table-column label="类型" prop="mtypeName"> </el-table-column>
        <el-table-column label="数量" prop="num"> </el-table-column>
        <div slot="append">
          <div v-for="(item, index) in data.list2" :key="index">
            <div class="table-herder">
              商户：{{ item.verdorName }} 摊位号：{{ item.booth }}
            </div>
            <el-table :data="item.list2" :loading="loading" :show-header="false" style="width: 100%"
              class="table-content">
              <el-table-column label="商品名称" prop="goodsName">
              </el-table-column>
              <el-table-column label="规格" prop="spaceName"> </el-table-column>
              <el-table-column label="类型" prop="mtypeName"> </el-table-column>
              <el-table-column label="数量" prop="num"> </el-table-column>
            </el-table>
          </div>
        </div>
      </el-table>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" type="primary" @click="hide" :loading="loading">
        确 定
      </el-button>
      <el-button size="medium" type="primary" v-if="type == 'vrify'" @click="vrify" :loading="loading">
        核 销
      </el-button>
    </div>
    <third-delivery ref="ThirdDelivery" @refresh="handleRefresh"></third-delivery>
    <third-delivery-cancel ref="ThirdDeliveryCancel" @refresh="handleRefresh"></third-delivery-cancel>
    <paidan-add ref="PaidanAdd" @refresh="handleRefresh"></paidan-add>
  </el-dialog>
</template>
<script>
  // import utils from "@/assets/js/utils";
  import qs from "qs";
  import ThirdDelivery from "./MarketThirdDelivery.vue";
  import ThirdDeliveryCancel from "./MarketThirdDeliveryCancel.vue";
  import PaidanAdd from "./MarketPaidanAdd";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        companyList: [],
        inputModel: {
          pkey: "",
          status: "1" //1其他 2分享
        },
        data: {},
        goodsTypeList: [{
            pkey: "CUT_ORDER",
            name: "砍价"
          },
          {
            pkey: "PRESALE_ORDER",
            name: "预售"
          },
          {
            pkey: "COLLAGE_ORDER",
            name: "团购"
          },
          {
            pkey: "SHARE_ORDER",
            name: "分享"
          },
          {
            pkey: "MARKET_ORDER",
            name: "市场"
          },
          {
            pkey: "INTEGRAL_ORDER",
            name: "积分"
          }
        ],
        payTypeList: [{
            pkey: "ORDER_ZHIFUBAO",
            name: "支付宝"
          },
          {
            pkey: "ORDER_WEIXIN",
            name: "微信"
          },
          {
            pkey: "ORDER_ELECTRONIC_ACCOUNT",
            name: "电子账户"
          }
        ],
        type: "", // 判断是不是核销
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
        marketType: this.$store.state.marketType
      };
    },
    mounted() {},
    methods: {
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          pkey: ""
        };
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        console.log(inputModel);
        this.inputModel = inputModel;
        const params = {
          pkey: this.inputModel.pkey
        };
        const url =
          this.marketType === "MARKET_SHOPPING_MALL" ?
          api.order.detailOrder :
          api.order.vendorDetailOrder;
        axios
          .post(url, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            for (let i in this.goodsTypeList) {
              if (this.goodsTypeList[i].pkey == response.orderType) {
                response.orderTypeName = this.goodsTypeList[i].name;
                break;
              }
            }
            for (let i in this.payTypeList) {
              if (this.payTypeList[i].pkey == response.payType) {
                response.payTypeName = this.payTypeList[i].name;
                break;
              }
            }
            // this.goodsTypeList.forEach(item => {
            //   if (item.pkey == response.orderType) {
            //     response.orderTypeName = item.name;
            //   }
            // });
            // this.payTypeList.forEach(item => {
            //   if (item.pkey == response.payType) {
            //     response.payTypeName = item.name;
            //   }
            // });
            this.data = response;
            console.log(this.data);
          });
      },
      show: function (type = "") {
        console.log("type", type);
        if (type == "vrify") {
          this.type = "vrify";
          console.log("this.type", this.type);
        }
        this.visible = true;
        this.clearData();
        this.$emit("hide");
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
      },
      /**
       *
       */
      vrify() {
        this.$confirm("核销后不可撤销, 是否继续?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
          .then(() => {
            axios
              .post(
                api.order.pickcodeUpd,
                qs.stringify({
                  pkey: this.inputModel.pkey
                })
              )
              .then(() => {
                this.hide();
                this.$emit("hide");
              });
          })
          .catch(() => {
            // this.$message({
            //   type: 'info',
            //   message: '已取消删除'
            // });
          });
      },
      handleRefresh() {
        const params = {
          pkey: this.inputModel.pkey
        };
        axios
          .post(api.order.thirdDeliveryStatus, this.$qs.stringify(params))
          .then(res => {
            if (res) {
              this.initData({
                inputModel: this.inputModel
              });
            }
          });
      },
      /**第三方派单 */
      handleThirdDeliveryChange: function () {
        this.$refs.ThirdDelivery.show(this.inputModel);
      },
      handleThirdDeliveryCancel() {
        this.$refs.ThirdDeliveryCancel.show(this.inputModel);
      },
      handleThirdDeliveryFinish() {
        axios
          .post(
            api.order.thirdDeliveryReach,
            this.$qs.stringify({
              pkey: this.inputModel.pkey
            })
          )
          .then(() => {
            this.initData({
              inputModel: this.inputModel
            });
          });
      },
      /**派单 */
      handlePaidan: function () {
        this.$refs.PaidanAdd.show({
          row: this.inputModel
        });
      },
      handleClose() {
        console.log("-----");
        this.$emit("hide");
      }
    },
    components: {
      ThirdDelivery,
      ThirdDeliveryCancel,
      PaidanAdd
    },
    props: {
      title: {
        type: String,
        default: "详情"
      }
    }
  };
</script>

<style lang="less" scoped>
  @import url("~@/assets/css/variable.less");

  .base,
  .consignee {
    padding-left: 2rem;
    display: flex;
    flex-wrap: wrap;
    padding-bottom: 12px;
    border-bottom: 1px solid @color-bg;
    margin-bottom: 12px;

    .base-item {
      margin-top: 12px;
      text-align: left;

      .base-item-title {
        width: auto;
        text-align: right;
        display: inline-block;
      }
    }
  }

  .base {
    .base-item {
      width: 50%;
    }
  }

  .consignee {
    // flex-direction: column;

    .base-item .base-item-title {
      width: auto;
    }
  }

  .red-font {
    color: #f56c6c;
  }

  .table-box {
    /deep/ .el-table__empty-block {
      display: none;
    }
  }

  .table-content {
    /deep/ .el-table__empty-block {
      display: flex;
    }
  }

  .table-herder {
    height: 40px;
    line-height: 40px;
    padding-left: 12px;
    font-size: 16px;
    font-weight: bold;
    text-align: left;
    background: #f2f2f2;
  }
  .refund-color {
  color: @color-red;
}
</style>