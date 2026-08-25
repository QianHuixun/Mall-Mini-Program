<!-- 
@name: MallDetailUpd.vue 
@description: 详情--编辑模板 
@author: zs
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <div class="el-form">
      <p>基本信息</p>
      <div class="base">
        <div class="base-item">
          <span class="base-item-title">订单编号：</span>
          <span class="base-item-content">{{data.code}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">下单时间：</span>
          <span class="base-item-content">{{data.createdTime}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">发货时间：</span>
          <span class="base-item-content">{{data.pstime}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">购买用户：</span>
          <span class="base-item-content">{{data.memberName}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">配送方式：</span>
          <span class="base-item-content">{{data.distributionType == 'PICKUP' ? '自提' : '配送' }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">重量：</span>
          <span class="base-item-content">{{data.weight}}kg</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">配送费用：</span>
          <span class="base-item-content">{{data.postage}}元</span>
        </div>
        <!-- <div class="base-item">
          <span class="base-item-title">发货单号：</span>
          <span class="base-item-content">{{data.kdCode}}</span>
        </div> -->
        <div class="base-item">
          <span class="base-item-title">订单总金额：</span>
          <span class="base-item-content">{{data.amtall}}元</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">实际支付：</span>
          <span class="base-item-content">{{data.amtn}}元</span>
        </div>
        <div class="base-item refund-color">
          <span class="base-item-title">退款总金额：</span>
          <span class="base-item-content">{{data.refundAmt || 0}}元</span>
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
        
      </div>
      <div class="material" v-if="data.distributionType != 'PICKUP'">
        <div class="material-sub">
        <p>物流信息</p>
        <div class="base">
          <div class="base-item">
            <span class="base-item-title">快递公司：</span>
            <span class="base-item-content">{{data.logistics}}</span>
          </div>
           <div class="base-item">
            <span class="base-item-title">快递单号：</span>
            <span class="base-item-content">{{data.kdCode}}</span>
          </div>
          <div class="base-item">
             <div class="base-item-content" style="text-align: center;">
              <el-popconfirm title="是否取消快递订单？" placement="top" @onConfirm="handleCancel()" v-if="data.expressType == 'EXPRESS_SF' && data.kdCode && data.orderExpressInfo && data.orderExpressInfo.status == 'ORDERED'">
                <el-button slot="reference" size="mini" type="danger"> 取消快递单 </el-button>
              </el-popconfirm>
             </div>
          </div>
        </div>
      </div>
      <div class="material-sub">
        <p v-if="data.expressType == 'EXPRESS_SF' && data.kdCode">物流状态</p>
        <div class="base">
          <template v-for="(item,index) in data.expressRoutes">
            <div class="base-item" v-if="index < 3" :key="item.pkey">
              <span class="base-item-content">{{item.time}}{{item.description}}</span>
            </div>
          </template>
          <div class="base-item" v-if="data.expressRoutes && data.expressRoutes.length>3">
            <div class="base-item-content">
              <el-button type="text" @click="expressRoutesMoreClick()"> 查看更多 </el-button>
            </div>
          </div>
        </div>
      </div>
      </div>
      
      <p style="margin-bottom: 12px;border-top: 1px solid #e9ecf3;padding-top: 12px;">客户留言</p>
      <el-input type="textarea" :disabled="true" :rows="3" placeholder="请输入内容" v-model="data.remark">
      </el-input>
      <p style="margin-top: 12px;border-top: 1px solid #e9ecf3;padding-top: 12px;">{{data.distributionType == 'PICKUP' ? '自提信息' : '收货人信息' }}
      </p>
      <div class="consignee">
        <div class="base-item">
          <span class="base-item-title">姓名：</span>
          <span class="base-item-content">{{data.addr ? data.addr.name : "--"}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">手机：</span>
          <span class="base-item-content">{{data.addr ? data.addr.mobile : "--"}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">{{data.distributionType == 'PICKUP' ? '提货地址' : '地址' }}：</span>
          <span class="base-item-content">{{
            data.addr && data.addr.addr  ? data.addr.addr : ""
          }} {{ (data.addr && data.addr.addrDetail )|| "--" }}</span>
          <el-button
            type="text"
            v-if="data.distributionType == 'PICKUP' && data.status == 'DELIVERED_ORDER'"
            @click="handleEditPickupAddr"
            style="margin-left: 10px;"
          >
            修改
          </el-button>
        </div>
      </div>
      <p style="margin-bottom: 12px;">商品信息</p>
      <el-table :data="data.list1" :loading="loading" border style="width: 100%">
        <el-table-column label="商品名称" prop="goodsName"></el-table-column>
        <el-table-column label="规格" prop="spaceName"></el-table-column>
        <el-table-column label="类型" prop="mtypeName"></el-table-column>
        <el-table-column label="单价" prop="price"></el-table-column>
        <el-table-column label="数量" prop="num"></el-table-column>
      </el-table>
      <p v-show="data.status == 'REFUND_APPLICATION_ORDER' || data.status == 'REFUNDED_ORDER' " style="margin-top: 12px;border-top: 1px solid #e9ecf3;padding-top: 12px;">退款信息
      </p>
      <div class="consignee" v-show="data.status == 'REFUND_APPLICATION_ORDER' || data.status == 'REFUNDED_ORDER' ">
        <div class="base-item">
          <span class="base-item-title">退款状态：</span>
          <span class="base-item-content">{{data.refund ? refundStats[data.refund.status] : '--'}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">退款发起时间：</span>
          <span class="base-item-content">{{data.refund ? data.refund.createdTime : '--'}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">交易金额：</span>
          <span class="base-item-content">{{data.refund ? data.refund.amtall : '--'}}元</span>
        </div>
        <div class="base-item refund-color" >
          <span class="base-item-title">退款金额：</span>
          <span class="base-item-content">{{data.refund ? data.refund.amtre : '--'}}元</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">留言：</span>
          <span class="base-item-content">{{data.refund ? data.refund.reason : '--'}}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">退款完成时间：</span>
          <span class="base-item-content">{{data.refund ? data.refund.delTime : '--'}}</span>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" type="primary" @click="hide" :loading="loading">
        确 定
      </el-button>
      <el-button size="medium" type="primary" v-if="type == 'vrify'" @click="vrify" :loading="loading">
        核 销
      </el-button>
    </div>
    <express-routes-more ref="ExpressRoutesMore"></express-routes-more>
    <pickup-addr-edit ref="PickupAddrEdit" @confirm="initData"></pickup-addr-edit>
  </el-dialog>
</template>
<script>
import qs from "qs";
import ExpressRoutesMore from './ExpressRoutesMore';
import PickupAddrEdit from './PickupAddrEdit';
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      companyList: [],
      inputModel: {
        pkey: "",
      },
      data: {},
      refundStats: {
        REFUND_APPLYING: '未处理',
        REFUND_AGREE: '同意',
        REFUND_FINAL: '退款成功',
        REFUND_REFUSE: '退款失败',
      },
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
    };
  },
  mounted() {},
  components: {
    ExpressRoutesMore,
    PickupAddrEdit
  },
  methods: {
    /**
     * 取消物流单号
     */
    handleCancel() {
      const params = {
        pkey: this.data.orderExpressInfo.pkey,
      };
      axios
        .post(api.order.sendOrderSFcancel, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success("取消成功");
          this.initData({ inputModel: this.inputModel });
          this.$emit("hide");
        });
    },
    // 查看更多
    expressRoutesMoreClick(){
      this.$refs.ExpressRoutesMore.show({
        expressRoutes: this.data.expressRoutes,
      });
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        pkey: "",
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      console.log(inputModel);
      this.inputModel = inputModel;
      const params = {
        pkey: this.inputModel.pkey,
      };
      axios
        .post(api.order.detailOrder, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.data = response;
        });
    },
    show: function (type = "") {
      if (type == "vrify") {
        this.type = "vrify";
        console.log("this.type", this.type);
      }
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
    },
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
              this.$emit("refresh");
            });
        })
        .catch(() => {
          // this.$message({
          //   type: 'info',
          //   message: '已取消删除'
          // });
        });
    },
    /**
     * @desc 修改自提地址
     */
    handleEditPickupAddr() {
      this.$refs.PickupAddrEdit.show(this.inputModel, this.data)
    }
  },
  props: {
    title: {
      type: String,
      default: "详情",
    },
  },
};
</script>


<style lang="less" scoped>
@import url("~@/assets/css/variable.less");
.material {
  display: flex;
  .material-sub {
    flex: 1;
    .base {
      border: 0;
      .base-item {
        width: 70%;
      }
    }
  }
}
.base,
.consignee {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 12px;
  border-bottom: 1px solid @color-bg;
  margin-bottom: 12px;

  .base-item {
    margin-top: 12px;
    width: 35%;

    .base-item-title {
      width: 40%;
      text-align: right;
      display: inline-block;
    }
  }
}

.consignee {
  flex-direction: column;
  .base-item {
    width: 100%;
  }
  .base-item .base-item-title {
    // width: 35%;
    width: auto;
  }
}

.refund-color {
  color: @color-red;
}
</style>