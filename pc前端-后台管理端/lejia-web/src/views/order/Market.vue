<!-- 
@name: Market.vue 
@description: 市场订单
@author: zs
@url: /order/market
@date: 2020/07/28
-->

<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select
          v-model="status"
          @change="handleChange"
          placeholder="订单状态"
          clearable
        >
          <el-option
            :value="item.pkey"
            :label="item.name"
            v-for="(item, index) in statusList"
            :key="index"
          ></el-option>
        </el-select>
        <el-select
          v-if="!!!collageKey.hasOwnProperty('pkey')"
          v-model="goodsType"
          @change="handleChange"
          placeholder="商品类型"
          clearable
        >
          <el-option
            :value="item.pkey"
            :key="index"
            :label="item.name"
            v-for="(item, index) in goodsTypeList"
          >
          </el-option>
        </el-select>
        <el-select
          v-model="purchaseStatus"
          @change="handleChange"
          placeholder="采购状态"
          clearable
        >
          <el-option value="AWAIT_PURCHASE" label="待采购"> </el-option>
          <el-option value="PURCHASEING" label="采购中"> </el-option>
          <el-option value="PURCHASE_FINISH" label="采购完成"> </el-option>
          <el-option value="PURCHASE_CONFIRM" label="确认完成"> </el-option>
        </el-select>
        <el-date-picker
          v-model="date"
          type="daterange"
          range-separator="至"
          start-placeholder="付款开始日期"
          end-placeholder="付款结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
        >
        </el-date-picker>
        <!-- <div class="search-item">
          <div class="title">价格波动：</div>
          <el-checkbox v-model="priceAbnormal" @change="handleChange"
            >异常</el-checkbox
          >
          <el-checkbox v-model="priceAbnormalFinsh" @change="handleChange"
            >异常(已确认)</el-checkbox
          >
        </div> -->
        <search-bar
          ref="searchBar"
          @search="startSearch"
          placeholder="请输入关键字"
          :select-options="selectOptions"
        >
        </search-bar>
        <el-select
          v-model="expressType"
          @change="handleChange"
          placeholder="骑手类型"
          clearable
        >
          <el-option value="COURIER" label="跑腿"> </el-option>
          <el-option value="WANLI" label="第三方配送"> </el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">订单笔数</span>
            <span class="count-num-rk">{{ orderCount }}</span>
          </div>
          <div class="count-box">
            <span class="count-tit">总金额</span>
            <span class="count-num-xs">{{ atmCount }}</span>
          </div>
        </div>
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="medium"
          @click="handleImportExcel"
        >
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table
        :data="tableData"
        :loading="loading"
        border
        style="width: 100%"
        class="table-fixed"
      >
        <el-table-column
          label="订单号"
          prop="code"
          min-width="160"
        ></el-table-column>
        <el-table-column
          label="小票码"
          prop="orderTrace"
          min-width="100"
        ></el-table-column>
        <el-table-column label="订单状态" prop="refundInfo" width="160">
          <template slot-scope="scoped">
            <span
              >{{ scoped.row.statusName || "--"
              }}<span style="color: red">{{
                scoped.row.refundInfo
              }}</span></span
            >
          </template>
        </el-table-column>
        <el-table-column
          label="骑手类型"
          prop="expressTypeName"
          min-width="100"
        ></el-table-column>
        <el-table-column
          label="配送状态"
          prop="thirdPartyStatusName"
          width="140"
        >
          <template slot-scope="scoped">
            <span>{{ scoped.row.thirdPartyStatusName || "--" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="配送方式"
          prop="distributionTypeName"
          width="140"
        >
        </el-table-column>
        <el-table-column
          label="自提/取货码"
          prop="pickupCode"
          width="120"
        ></el-table-column>
        <el-table-column
          label="商品类型"
          prop="orderTypeName"
          width="120"
        ></el-table-column>
        <el-table-column
          label="配送时间"
          prop="pstime"
          width="170"
        ></el-table-column>
        <el-table-column
          label="商品价格"
          prop="amto"
          width="100"
        ></el-table-column>
        <el-table-column
          label="配送费"
          prop="postage"
          width="100"
        ></el-table-column>
        <el-table-column
          label="商品优惠"
          prop="cardAmt"
          width="100"
        ></el-table-column>
        <el-table-column
          label="配送优惠"
          prop="cardPostageAmt"
          width="100"
        ></el-table-column>
        <el-table-column
          label="总价"
          prop="amtall"
          width="100"
        ></el-table-column>
        <el-table-column label="支付价格" prop="amtn" width="80">
          <template slot-scope="scope">
            {{ scope.row.amtn || "--" }}
          </template>
        </el-table-column>
        <el-table-column label="退款金额" prop="refundAmt" width="80">
        </el-table-column>
        <el-table-column label="支付方式" prop="payTypeName" width="80">
        </el-table-column>
        <el-table-column label="购买用户" prop="memberMobile" width="120">
        </el-table-column>
        <el-table-column
          label="付款时间"
          prop="createdTime"
          width="150"
        ></el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="
                scope.row.status == 'DELIVERED_ORDER' &&
                scope.row.distributionType != 'PICKUP' && scope.row.distributionType != 'DINE_IN'
              "
              type="text"
              size="small"
              @click="handlePaidan(scope.row)"
            >
              派单
            </el-button>
            <el-button type="text" size="small" @click="handlePrint(scope.row)">
              打印
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="handlePurchase(scope.row)"
              v-if="scope.row.purchaseStatus == 'AWAIT_PURCHASE'"
            >
              采购
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="purchaseDetail(scope.row)"
              v-else
            >
              采购详情
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="vrifyDetail(scope.row)"
              v-if="
                !scope.row.pickupFlag &&
                scope.row.pickupCode &&
                scope.row.status != 'REFUNDED_ORDER'
              "
            >
              核销
            </el-button>
            <el-button
              v-if="
                scope.row.status == 'DELIVERED_ORDER' &&
                scope.row.distributionType != 'PICKUP' && scope.row.distributionType != 'DINE_IN'
              "
              type="text"
              size="small"
              @click="handleThirdDelivery(scope.row)"
            >
              第三方派单
            </el-button>
            <el-popconfirm
              title="确定送达吗？"
              placement="top"
              @onConfirm="handleArrived(scope.row)"
              v-if="
                scope.row.expressType == 'COURIER' &&
                (scope.row.expressStatus == 'EXPRESS_INITIAL' ||
                  scope.row.expressStatus == 'EXPRESS_ORDER' ||
                  scope.row.expressStatus == 'EXPRESS_GOODS')
              "
            >
              <el-button slot="reference" size="mini" type="text"
                >确认送达</el-button
              >
            </el-popconfirm>
            <el-popconfirm
              title="确定送达吗？"
              placement="top"
              @onConfirm="vrify(scope.row)"
              v-if="
                scope.row.distributionType == 'DINE_IN' &&
                (scope.row.status == 'SHIPPED_ORDER' || scope.row.status == 'DELIVERED_ORDER')
              "
            >
              <el-button slot="reference" size="mini" type="text"
                >确认送达</el-button
              >
            </el-popconfirm>
            <el-button v-if="(scope.row.status == 'DELIVERED_ORDER') || (scope.row.status == 'SHIPPED_ORDER') || (scope.row.status == 'ARRIVED_ORDER')"           
              type="text" size="small" @click="handleActiveRefund(scope.row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination
        hide-on-single-page
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="page"
        :page-size="pageSize"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>
    <!-- 组件 -->
    <paidan-add ref="PaidanAdd" @refresh="getData"></paidan-add>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <purchase-add ref="PurchaseAdd" @refresh="getData"></purchase-add>
    <purchase-detail ref="PurchaseDetail" @refresh="getData"></purchase-detail>
    <vrify-detail ref="VrifyDetail" @refresh="getData"></vrify-detail>
    <third-delivery ref="ThirdDelivery" @refresh="getData"></third-delivery>
    <active-refund ref="ActiveRefund" @refresh="getData"></active-refund>
    <div style="display: none">
      <div id="print">
        <div
          style="
            width: 48mm;
            line-height: 18px;
            font-size: 10px;
            padding-left: 0px;
            box-sizing: border-box;
          "
        >
          <div style="font-size: 12px">{{ printData.date }}</div>
          <div
            style="
              font-size: 18px;
              font-weight: bold;
              margin: 5px 0;
              text-align: center;
            "
          >
            {{
              $store.state.marketName  
            }}订单
          </div>
          <div>订单编号：{{ printData.code }}</div>
          <div>下单时间：{{ printData.createdTime }}</div>
          <div
            style="
              padding: 5px 0;
              font-size: 12px;
              border-bottom: 1px solid #000;
            "
          >
            期望送达时间：{{ printData.pstime }}
          </div>
          <table
            style="width: 100%; line-height: 16px; border-top: 1px solid #000"
          >
            <thead>
              <tr style="padding: 10px 0">
                <th align="left">商品</th>
                <th align="left" style="min-width: 2.5em">规格</th>
                <th align="right" style="min-width: 2.5em">数量</th>
                <th align="right" style="min-width: 2.5em">金额</th>
              </tr>
            </thead>
            <tbody>
              <tr
                style="padding: 10px 0"
                v-for="(item, index) in printData.goodsList"
                :key="index"
              >
                <td s>{{ item.goodsName }}</td>
                <td>{{ item.spaceName }}</td>
                <td align="right">{{ item.goodsNum }}</td>
                <td align="right">{{ item.totalPricen }}</td>
              </tr>
            </tbody>
          </table>
          <div
            style="border-top: 1px solid #000; display: flex; padding-top: 5px"
          >
            <div style="flex: 1">配送费</div>
            <div>{{ printData.postage }}</div>
          </div>
          <div
            style="
              border-bottom: 1px solid #000;
              display: flex;
              padding-top: 5px;
              padding-bottom: 5px;
            "
          >
            <div style="flex: 1">优惠券</div>
            <div>{{ printData.cardAmt ? "-" + printData.cardAmt : 0 }}</div>
          </div>
          <div style="text-align: right; margin-top: 5px">
            原价： {{ printData.amtall }}
          </div>
          <div
            style="
              font-size: 14px;
              border-bottom: 1px solid #000;
              text-align: right;
              margin-top: 5px;
              padding-bottom: 5px;
            "
          >
            总计： {{ printData.amtn }}
          </div>
          <div style="font-size: 16px; font-weight: bold; margin-top: 5px">
            {{ printData.addr }}
          </div>
          <div style="font-size: 16px; font-weight: bold">
            {{ printData.mobile }}
          </div>
          <div style="font-size: 16px; font-weight: bold">
            {{ printData.name }}
          </div>
          <div
            v-if="printData.remark"
            style="width: 60mm; font-size: 14px; font-weight: bold"
          >
            备注：{{
              printData.remark.length > 50
                ? printData.remark.substring(0, 50) + "..."
                : printData.remark
            }}
          </div>
          <!-- <div id="qrcode" ref="qrcode" style="margin-top: 10px;margin-left: 50px;">
          </div> -->
          <!-- <div style="font-size: 18px; font-weight: bold;margin-top: 10px; text-align: center;">
              <img style="width:100px;height:100px" src="../../assets/images/mini_code2.jpg" alt="" >
          </div> -->
          <!-- <div >
              <img style="width: 60mm;height:60mm" src="../../assets/images/mini_code2.jpg" alt="" >
          </div> -->
          <div style="height: 50px"></div>
        </div>
      </div>
    </div>
    <!-- 打印内容 end -->
  </div>
</template>
<script>
import qs from "qs";
import PaidanAdd from "./sub/MarketPaidanAdd";
import PurchaseAdd from "./sub/MarketCaigouAdd";
import PurchaseDetail from "./sub/PurchaseDetail";
import EditComp from "./sub/MarketDetailEdit";
import VrifyDetail from "./sub/VrifyDetail.vue";
import ThirdDelivery from "./sub/MarketThirdDelivery.vue";
import ActiveRefund from './sub/ActiveRefund.vue';
// import { getLodop } from "@/assets/js/LodopFuncs"; //打印控件
import QRCode from "qrcodejs2";
// var LODOP;
export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: "code",
      selectOptions: [
        {
          name: "订单编号",
          key: "code",
        },
        {
          name: "收货人手机号",
          key: "mobile",
        },
        {
          name: "自提/取货码",
          key: "vrifyCode",
        },
        {
          name: "购买用户手机号",
          key: "memberMobile",
        },
      ],
      statusList: [
        {
          pkey: "UNPAID_ORDER",
          name: "未付款",
        },
        {
          pkey: "DELIVERED_ORDER",
          name: "待发货",
        },
        {
          pkey: "SHIPPED_ORDER",
          name: "已发货",
        },
        {
          pkey: "ARRIVED_ORDER",
          name: "已到货",
        },
        {
          pkey: "CONFIRM_ORDER",
          name: "已完成",
        },
        {
          pkey: "REFUNDED_ORDER",
          name: "已退款",
        },
        // {
        //   pkey: "VOID_ORDER",
        //   name: "作废"
        // }
      ],
      goodsTypeList: [
        {
          pkey: "CUT_ORDER",
          name: "砍价",
        },
        {
          pkey: "PRESALE_ORDER",
          name: "预售",
        },
        {
          pkey: "COLLAGE_ORDER",
          name: "团购",
        },
        {
          pkey: "SHARE_ORDER",
          name: "分享",
        },
        {
          pkey: "MARKET_ORDER",
          name: "其他",
        },
      ],
      priceAbnormal: false,
      priceAbnormalFinsh: false,
      date: "",
      status: "",
      goodsType: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数
      collageKey: this.$route.query,
      qrcode: null, //二维码
      printData: {}, //打印的内容
      orderCount: 0, //订单笔数
      atmCount: 0, //订单总金额
      purchaseStatus: "",
      times: null,
      expressType: "",
    };
  },
  mounted() {
    // console.log(this.collageKey);
    console.log(this.userIdentity);
    if (this.$route.query.hasOwnProperty("status")) {
      this.status = this.$route.query.status;
    }
    this.getCountData();
    this.getData();
    if (this.times) clearInterval(this.times);
    this.times = setInterval(() => {
      this.getData();
      this.getCountData();
    }, 1000 * 30);
  },
  beforeDestroy() {
    if (this.times) clearInterval(this.times);
  },
  components: {
    PaidanAdd,
    EditComp,
    PurchaseAdd,
    PurchaseDetail,
    VrifyDetail,
    ThirdDelivery,
    ActiveRefund
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    userIdentity() {
      console.log(this.$store.state.userIdentity);
      return this.$store.state.userIdentity
        ? this.$store.state.userIdentity
        : localStorage.getItem("userIdentity");
    },
  },
  methods: {
     /**主动退款 */
    handleActiveRefund(row) {
      this.$refs.ActiveRefund.show(row.pkey, row.status);
    },
    /**
     *
     */
    vrify(row) {
      axios
        .post(
          api.order.pickcodeUpd,
          qs.stringify({
            pkey: row.pkey,
          })
        )
        .then((res) => {
          this.$message.success("确认送达");
          this.getData();
        });
    },
    /**采购 */
    handlePurchase(row) {
      let params = {
        Pkey: row.pkey,
      };
      axios
        .post(api.order.queryOrderPurchase, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          console.log(res);
          this.$refs.PurchaseAdd.show({
            row: res,
            orderPkey: row.pkey,
          });
        });
    },
    /**
     * 跑腿确认送达
     */
    handleArrived(e) {
      let params = {
          pkey: e.pkey,
        },
        url = api.order.arrived,
        txt = "确认送达";
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(txt);
          this.getData();
        });
    },
    /**
     * 列表导出excel
     */
    /**
     * 列表导出excel
     */

    handleImportExcel() {
      const params = {
        page: this.page - 1,
        pagesize: 99999,
        orderOir: "MARKET_MALL",
        farmer:
          this.$store.state.userIdentity == 1
            ? ""
            : this.$store.state.marketPkey,
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
        expressType: this.expressType,
      };
      if (this.date) {
        params.startDate = this.date[0];
        params.endDate = this.date[1];
      }
      if (this.status) params.status = this.status;
      if (this.keywords) params[this.searchKey] = this.keywords;
      let url = api.order.exportOrderExcel + "?" + this.$qs.stringify(params);
      axios
        .get(url, {
          responseType: "blob",
        })
        .then((response) => {
          var _this = this;
          if (response.data.type == "application/json") {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8",
          });
          if (!!window.ActiveXObject || "ActiveXObject" in window) {
            window.navigator.msSaveOrOpenBlob(blob, "市场订单列表.xlsx");
          } else {
            const link = document.createElement("a");
            link.style.display = "none";
            link.href = URL.createObjectURL(blob);
            link.setAttribute("download", "市场订单列表.xlsx");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        });
    },
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
      this.getCountData();
    },
    handleChange: function () {
      this.page = 1;
      this.getData();
      this.getCountData();
    },

    /**详情 */
    handleDetail: function (row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },
    /**
     * @desc 采购详情
     */
    purchaseDetail(row) {
      this.$refs.PurchaseDetail.show({
        row: row,
      });
    },
    /**派单 */
    handlePaidan: function (row) {
      this.$refs.PaidanAdd.show({
        row: row,
      });
    },
    /**第三方派单 */
    handleThirdDelivery: function (row) {
      this.$refs.ThirdDelivery.show(row);
    },
    /**核销 */
    vrifyDetail(row) {
      this.$refs.VrifyDetail.show({
        row: row,
      });
    },

    /**打印 */
    handlePrint: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.order.printOrder, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          if (res) {
            this.$message.success("小票打印成功!");
          }
        });
    },
    // handlePrint: function (row) {
    //   let LODOP = getLodop();
    //   console.log(typeof LODOP == 'undefined');
    //   if (typeof LODOP == 'undefined') {
    //     this.$confirm('未安装打印插件，请下载安装?', '提示', {
    //       confirmButtonText: '确定',
    //       cancelButtonText: '取消',
    //       type: 'warning',
    //     })
    //       .then(() => {
    //         location.href = api.mall.downPrint;
    //       })
    //       .catch(() => {
    //         return;
    //       });
    //   }
    //   LODOP.PRINT_INIT('订单打印');
    //   LODOP.SET_PRINT_STYLE('FontSize', 12);
    //   LODOP.SET_PRINT_STYLE('Bold', 1);
    //   LODOP.SET_PRINT_PAGESIZE(3, 800, 10, '');
    //   this.printData = row;
    //   this.printData.date = row.createdTime.substring(0, 10);
    //   // this.makeQrcode(row.qrCode);

    //   setTimeout(() => {
    //     LODOP.ADD_PRINT_HTM(
    //       20,
    //       0,
    //       250,
    //       400,
    //       document.getElementById('print').innerHTML
    //     );
    //     LODOP.PREVIEW();
    //   }, 1000);
    // },
    /**
     * 生成二维码
     */
    makeQrcode(text) {
      if (this.qrcode) {
        this.qrcode.clear();
        this.qrcode.makeCode(text);
      } else {
        this.qrcode = new QRCode("qrcode", {
          width: 80, // 设置宽度，单位像素
          height: 80, // 设置高度，单位像素
          text, // 设置二维码内容或跳转地址
        });
      }
    },
    // 获取订单信息统计金额和笔数
    getCountData() {
      let hasPkey = this.collageKey.hasOwnProperty("pkey");
      const params = {
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        orderOir: "MARKET_MALL",
        status: this.status,
        orderType: hasPkey ? "COLLAGE_ORDER" : this.goodsType,
        groupPkey: hasPkey ? this.collageKey.pkey : "",
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.queryOrderCount, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.orderCount = res.count == null ? 0 : res.count;
          this.atmCount = res.sum == null ? 0 : res.sum;
        });
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      let hasPkey = this.collageKey.hasOwnProperty("pkey");
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startDate: this.date ? this.date[0] : "",
        endDate: this.date ? this.date[1] : "",
        orderOir: "MARKET_MALL",
        status: this.status,
        orderType: hasPkey ? "COLLAGE_ORDER" : this.goodsType,
        groupPkey: hasPkey ? this.collageKey.pkey : "",
        purchaseStatus: this.purchaseStatus,
        expressType: this.expressType,
        // priceAbnormal: this.priceAbnormal,
        // priceAbnormalFinsh: this.priceAbnormalFinsh
      };

      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.queryOrder, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          // console.log(response);
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
  },
};
</script>
<style lang="less" scope>
.count-container {
  display: inline-block;
  padding-right: 10px;

  .count-box {
    display: inline-block;
    padding: 0 10px;

    .count-tit {
      padding-right: 5px;
    }

    .count-num-rk {
      color: #67c23a;
    }

    .count-num-xs {
      color: #409eff;
    }
  }
}

/deep/ .el-table__fixed-right {
  height: 100% !important; //设置高优先，以覆盖内联样式
}
</style>
<style lang="less" scoped>
/deep/.el-table .el-table__fixed-right {
  height: 100% !important; //设置高优先，以覆盖内联样式
}

.search-item {
  border: 1px solid #e5e4e9;
  display: inline-flex;
  height: 36px;
  align-items: center;
  border-radius: 5px;
  padding: 0 8px;
  margin: 5px;
}
</style>