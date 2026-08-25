<!-- 
@name: Mall.vue 
@description: 商城订单
@author: zs
@url: /order/mall
@date: 2020/07/28
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="status" @change="handleChange" placeholder="订单状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <el-select v-model="orderType" @change="handleChange" placeholder="订单类型" clearable>
          <el-option value="INTEGRAL_ORDER" label="商城订单"></el-option>
          <el-option value="GIFT_ORDER" label="礼券订单"></el-option>
          <el-option value="COUPON_ORDER" label="优惠券订单"></el-option>
          <el-option value="INTEGRAL_PRESALE_ORDER" label="预售订单"></el-option>
          <el-option value="INTEGRAL_BNYP_ORDER" label="滨农优品订单"></el-option>
          <el-option value="INTEGRAL_MSD_ORDER" label="热力豆订单"></el-option>
          <el-option value="INTEGRAL_JD_ORDER" label="京东订单"></el-option>
        </el-select>
        <el-select v-model="distributionType" @change="handleChange" placeholder="配送方式" clearable>
          <el-option value="PICKUP" label="自提"></el-option>
          <el-option value="IMMEDIATELY" label="配送"></el-option>
        </el-select>
        <el-date-picker v-model="date" type="daterange" :clearable="false" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <div class="search-item" v-if="userIdentity==2">
          <div class="title">价格波动：</div>
          <el-checkbox v-model="priceAbnormal" @change="handleChange">异常</el-checkbox>
          <el-checkbox v-model="priceAbnormalFinsh" @change="handleChange">异常(已确认)</el-checkbox>
        </div>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
        <el-select class="tags-select" v-model="payTypes" multiple collapse-tags clearable placeholder="支付方式" @change="handleChange">
          <el-option value="ORDER_WEIXIN" label="微信"></el-option>
          <el-option value="ORDER_ELECTRONIC_ACCOUNT" label="电子账户"></el-option>
          <el-option value="NM_MEMBER" label="农贸会员卡"></el-option>
          <el-option value="ORDER_MSD" label="热力豆"></el-option>
        </el-select>
        <el-select class="tags-select" v-model="tags" @change="handleChange" filterable multiple collapse-tags placeholder="选择标签" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in tagList"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box"><span class="count-tit">订单笔数</span>
            <span class="count-num-rk">{{orderCount}}</span>
          </div>
          <div class="count-box"><span class="count-tit">总金额</span>
            <span class="count-num-xs">{{atmCount}}</span>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel">
          列表导出
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportLineExcel">
          明细导出
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleBatchDelivery">
          批量自提出货
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleBatchArrival">
          批量自提到货
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table  ref="multipleTable" :data="tableData" :loading="loading" border style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column label="选择" type="selection" width="50"></el-table-column>
        <el-table-column label="订单号" prop="code" min-width="160"></el-table-column>
        <el-table-column label="状态" prop="statusName" min-width="140">
          <template slot-scope="scope">
            <span>{{ scope.row.statusName }}</span>
            <span style="color:#D9001B;">{{ scope.row.refundInfo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="购买用户" prop="memberMobile" min-width="150"></el-table-column>
        <el-table-column label="用户标签" prop="tagName" min-width="100" show-overflow-tooltip></el-table-column>
        <el-table-column label="配送方式" prop="distributionTypeName" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.distributionType == 'PICKUP' ? '自提' : '配送' }}
          </template>
        </el-table-column>
        <el-table-column label="付款时间" prop="createdTime" min-width="150"></el-table-column>
        <!-- <el-table-column label="付款类型" prop="payTypeName" min-width="100"></el-table-column> -->
        <el-table-column label="商品类型" prop="orderTypeName" min-width="100"></el-table-column>
        <el-table-column label="商品价格" prop="amto" min-width="100"></el-table-column>
        <el-table-column label="支付积分" prop="pointn" min-width="100"></el-table-column>
        <el-table-column label="邮费" prop="postage" min-width="100"></el-table-column>
        <el-table-column label="商品优惠" prop="cardAmt" min-width="100"></el-table-column>
        <el-table-column label="配送优惠" prop="cardPostageAmt" min-width="100"></el-table-column>
        <el-table-column label="总价" prop="amtall" min-width="100"></el-table-column>
        <el-table-column label="支付金额" prop="amtn" min-width="100"></el-table-column>
        <el-table-column label="退款金额" prop="refundAmt" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.refundAmt || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="退款积分" prop="refundPoint" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.refundPoint || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" prop="payTypeName" min-width="100"></el-table-column>
        <el-table-column label="发货时间" prop="pstime" min-width="150"></el-table-column>
        <el-table-column label="快递公司" prop="logistics" min-width="120"></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row)">
              详情
            </el-button>
            <el-button v-if="(scope.row.orderType == 'INTEGRAL_MSD_ORDER') && (scope.row.status == 'DELIVERED_ORDER') && (scope.row.distributionType == 'PICKUP')" type="text" size="small"
              @click="handlePickupDelivery(scope.row)">
              自提出货
            </el-button>
            <el-button v-if="(scope.row.orderType == 'INTEGRAL_MSD_ORDER') && (scope.row.status == 'WAIT_ARRIVAL_ORDER') && (scope.row.distributionType == 'PICKUP')" type="text" size="small"
              @click="handlePickupArrival(scope.row)">
              自提到货
            </el-button>
            <el-button v-if="(scope.row.status == 'DELIVERED_ORDER') && (scope.row.distributionType != 'PICKUP')" type="text" size="small"
              @click="handleDelivery(scope.row)">
              发货
            </el-button>
            <el-button v-if="(scope.row.status == 'DELIVERED_ORDER') && (scope.row.distributionType == 'IMMEDIATELY')" type="text" size="small"
              @click="handleSFDelivery(scope.row)">
              顺丰发货
            </el-button>
            <el-button type="text" size="small" @click="handlePrint(scope.row)">
              打印
            </el-button>
            <el-button
              v-if="!scope.row.pickupFlag && scope.row.pickupCode && scope.row.status != 'REFUNDED_ORDER'"
              type="text" size="small" @click="vrifyDetail(scope.row)" 
            >
              核销
            </el-button>
            <el-button v-if="((scope.row.status == 'DELIVERED_ORDER') || (scope.row.status == 'SHIPPED_ORDER') || (scope.row.status == 'ARRIVED_ORDER')) && (scope.row.orderType != 'INTEGRAL_MSD_ORDER')"
              type="text" size="small" @click="handleActiveRefund(scope.row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <delivery-add ref="DeliveryAdd" @refresh="getData"></delivery-add>
    <delivery-sf-add ref="DeliverySfAdd" @refresh="getData"></delivery-sf-add>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <mall-detail-upd ref="MallDetailUpd" @refresh="getData"></mall-detail-upd>
    <active-refund ref="ActiveRefund"  @refresh="getData"></active-refund>
    <div style="display: none">
      <div id="print">
        <div style="width: 60mm;line-height: 18px;font-size: 10px;padding-left: 10px;box-sizing: border-box;">
          <div style="font-size: 12px">{{printData.date}}</div>
          <div style="font-size: 18px; font-weight: bold;margin: 5px 0; text-align: center;">{{saasName}}订单</div>
          <div>订单编号：{{printData.code}}</div>
          <div>下单时间：{{printData.createdTime}}</div>
          <!--  <div style="padding: 5px 0;font-size: 12px;border-bottom: 1px solid #000;">期望送达时间：{{printData.pstime}}</div> -->
          <table style="width: 100%;line-height: 18px;border-top: 1px solid #000">
            <thead>
              <tr style="padding: 10px 0 ">
                <th align="left">商品</th>
                <th align="left" style="min-width: 2.5em">规格</th>
                <th align="right" style="min-width: 2.5em">数量</th>
                <th align="right" style="min-width: 2.5em">金额</th>
              </tr>
            </thead>
            <tbody>
              <tr style="padding: 10px 0;" v-for="(item,index) in printData.goodsList" v-bind:key="index">
                <td>{{item.goodsName}}</td>
                <td>{{item.spaceName}}</td>
                <td align="right">{{item.goodsNum}}</td>
                <td align="right">{{item.goodsNum*item.goodsPricen}}</td>
              </tr>
            </tbody>
          </table>
          <div style="border-top: 1px solid #000;display: flex;padding-top: 5px;">
            <div style="flex: 1">配送费</div>
            <div>{{printData.postage}}</div>
          </div>
          <div  style="border-bottom: 1px solid #000;display: flex;padding-top: 5px;padding-bottom:5px;">
            <div style="flex: 1">优惠券</div>
            <div>{{printData.cardAmt?'-'+printData.cardAmt:0}}</div>
          </div>
          <div style="text-align: right; margin-top: 5px;">
            原价： {{printData.amtall}}
          </div>
          <div style="font-size: 14px;border-bottom: 1px solid #000;text-align: right; margin-top: 5px;padding-bottom:5px">
            总计： {{printData.amtn}}
          </div>
          <div style="font-size: 16px; font-weight: bold;margin-top: 5px;">
            {{printData.addr}}
          </div>
          <div style="font-size: 16px;font-weight: bold;">
            {{printData.mobile}}
          </div>
          <div style="font-size: 16px;font-weight: bold">
            {{printData.name}}
          </div>
          <div v-if="printData.remark" style="width: 60mm;font-size: 14px;font-weight: bold;">
            备注：{{printData.remark.length>50?(printData.remark.substring(0,50)+'...'):printData.remark}} 
          </div>
          <!-- <div id="qrcode" ref="qrcode" style="margin-top: 10px;margin-left: 50px;"> 
          </div> -->
          <!-- <div >
              <img style="width: 60mm;height:60mm" src="../../assets/images/mini_code2.jpg" alt="" >
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
import DeliveryAdd from './sub/MallDeliveryAdd';
import DeliverySfAdd from './sub/DeliverySFAdd';
import EditComp from './sub/MallDetailEdit';
import MallDetailUpd from './sub/MallDetailUpd.vue';
import ActiveRefund from './sub/ActiveRefund.vue';
import { getLodop } from '@/assets/js/LodopFuncs'; //打印控件
import QRCode from 'qrcodejs2';
export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: 'code',
      selectOptions: [
        {
          name: '订单编号',
          key: 'code',
        },
        {
          name: '收货人手机号',
          key: 'mobile',
        },{
          name: '购买人手机号',
          key: 'memberMobile',
        },
      ],
      statusList: [
        {
          pkey: '',
          name: '全部',
        },
        {
          pkey: 'UNPAID_ORDER',
          name: '未付款',
        },
        {
          pkey: 'DELIVERED_ORDER',
          name: '待发货',
        },
        {
          pkey: 'SHIPPED_ORDER',
          name: '已发货',
        },
        {
          pkey: 'ARRIVED_ORDER',
          name: '已到货',
        },
        {
          pkey: 'CONFIRM_ORDER',
          name: '已完成',
        },
        // {
        //   pkey: 'REFUND_APPLICATION_ORDER',
        //   name: '退款申请',
        // },
        {
          pkey: 'REFUNDED_ORDER',
          name: '已退款',
        },
        // {
        //   pkey: "VOID_ORDER",
        //   name: "作废"
        // }
      ],
      tagList:[],
      priceAbnormal: false,
      priceAbnormalFinsh: false,
      date: [utils.getCustDate(30), utils.getNowDate()],
      status: '',
      orderType: '',
      distributionType: '',
      tags: [],
      payTypes: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      qrcode: null, //二维码
      printData: {}, //打印的内容
      orderCount: 0, //订单笔数
      atmCount: 0, //订单总金额
      multipleSelection: [],
    };
  },
  mounted() {
    if (this.$route.query.hasOwnProperty('status')) {
      this.status = this.$route.query.status;
    }
    this.getData();
    this.getCountData();
    this.getTagData();
  },
  components: {
    DeliveryAdd,
    DeliverySfAdd,
    EditComp,
    MallDetailUpd,
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
    saasName() {
      return this.$store.state.saasName ? this.$store.state.saasName : localStorage.getItem('saasName')
    },
    userIdentity() {
      console.log(this.$store.state.userIdentity);
      return this.$store.state.userIdentity
        ? this.$store.state.userIdentity
        : localStorage.getItem("userIdentity");
    }
  },
  methods: {
    /**
     * 列表选中
     */
    handleSelectionChange(val){
      // 返回pkey
      this.multipleSelection = val.map(item => item.pkey);
      console.log("handleSelectionChange", this.multipleSelection)
    },
    /**
     * 批量自提出货
     */
    handleBatchDelivery() {
      // 如果没有选中项，给出提示信息
      if (!this.isSelected()) {
        this.$message.error('请选择自提订单');
        return;
      }
      console.log("multipleSelection", this.multipleSelection)
      const url = api.order.pickupWaitArrival,
        params = {
        pkeys: this.multipleSelection.join(',') || '',
      };
      axios.post(url, qs.stringify(params)).then(() => {
        this.$message.success('批量自提出货成功！')
        this.getData();
      });
    },
    /**
     * 批量自提到货
     */
    handleBatchArrival() {
      // 如果没有选中项，给出提示信息
      if (!this.isSelected()) {
        this.$message.error('请选择自提订单');
        return;
      }

      const url = api.order.pickupWaitWriteoff,
      params = {
        pkeys: this.multipleSelection.join(',') || '',
      };
      axios.post(url, qs.stringify(params)).then(() => {
        this.$message.success('批量自提到货成功！')
        this.getData();
      });

    },
    /**
     * 热力豆自提订单-自提到货
     */
    handlePickupArrival(row){
      const url = api.order.pickupWaitWriteoff,
      params = {
        pkeys: row.pkey,
      };
      axios.post(url, qs.stringify(params)).then(() => {
        this.$message.success('自提到货成功！')
        this.getData();
      });
    },
    /**
     * 热力豆自提订单-自提出货
     */
    handlePickupDelivery(row){
      const url = api.order.pickupWaitArrival,
        params = {
        pkeys: row.pkey,
      };
      axios.post(url, qs.stringify(params)).then(() => {
        this.$message.success('自提出货成功！')
        this.getData();
      });
    },
    /**
     * 判断表格选中条数是否大于0
     */
    isSelected() {
      return this.multipleSelection.length > 0;
    },
    /**
     * 列表导出excel
     */

    handleImportExcel() {
      const params = {
        page: this.page - 1,
        pagesize: 99999,
        orderOir: 'POINTS_MALL',
        farmer:
          this.$store.state.userIdentity == 1
            ? ''
            : this.$store.state.marketPkey,
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      if (this.date) {
        params.startDate = this.date[0];
        params.endDate = this.date[1];
      }
      if (this.status) params.status = this.status;
      if(this.orderType) params.orderType = this.orderType;
      if(this.distributionType) params.distributionType = this.distributionType;
      if (this.keywords) params[this.searchKey] = this.keywords;
      let url = api.order.exportOrderExcel + '?' + this.$qs.stringify(params);
      axios
        .get(url, {
          responseType: 'blob',
        })
        .then((response) => {
          var _this = this;
          if (response.data.type == 'application/json') {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(blob, '商城订单列表.xlsx');
          } else {
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.setAttribute('download', '商城订单列表.xlsx');
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        });
    },
    /**
     * 明细导出excel
     */

    handleImportLineExcel() {
      const params = {
        page: this.page - 1,
        pagesize: 99999,
        orderOir: 'POINTS_MALL',
        farmer:
          this.$store.state.userIdentity == 1
            ? ''
            : this.$store.state.marketPkey,
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      if (this.date) {
        params.startDate = this.date[0];
        params.endDate = this.date[1];
      }
      if (this.status) params.status = this.status;
      if(this.orderType) params.orderType = this.orderType;
      if(this.distributionType) params.distributionType = this.distributionType;
      if (this.keywords) params[this.searchKey] = this.keywords;
      let url = api.order.exportOrderLine + '?' + this.$qs.stringify(params);
      const loading = this.$loading({
        lock: true,
        text: '文件导出中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      axios
        .get(url, {
          responseType: 'blob',
        })
        .then((response) => {
          console.log('response', response);
          var _this = this;
          if (response.data.type == 'application/json') {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          var disposition = response.headers['content-disposition'];
          var headersFileName = disposition ? disposition.split('=') : '';
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(
              blob,
              headersFileName && headersFileName.length != 0 ?
              decodeURI(headersFileName[1]) :
              `${'商城订单明细列表'
              }.xlsx`
            );
          } else {
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.setAttribute(
              'download',
              headersFileName && headersFileName.length != 0 ?
              decodeURI(headersFileName[1]) :
              `${'商城订单明细列表'
              }.xlsx`
            );
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        })
        .finally(() => {
          loading.close();
        })
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

    /**发货 */
    handleDelivery: function (row) {
      this.$refs.DeliveryAdd.show({
        row: row,
      });
    },

     /**顺丰发货 */
    handleSFDelivery: function (row) {
      this.$refs.DeliverySfAdd.show({
        row: row,
      });
    },

    /**核销 */
    vrifyDetail(row) {
      this.$refs.MallDetailUpd.show('vrify');
      this.$refs.MallDetailUpd.initData({inputModel: row});
    },

    /**主动退款 */
    handleActiveRefund(row) {
      this.$refs.ActiveRefund.show(row.pkey, row.status)
    },

    /**打印 */
    handlePrint: function (row) {
      // console.log("打印");
      // this.CreateOneFormPage(row);
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
          20,
          250,
          400,
          document.getElementById('print').innerHTML
        );
        LODOP.PREVIEW();
      }, 1000);
    },
    // /**
    //  * 创建打印内容
    //  */
    // CreateOneFormPage(row) {

    // },

    makeQrcode(text) {
      if (this.qrcode) {
        this.qrcode.clear();
        this.qrcode.makeCode(text);
      } else {
        this.qrcode = new QRCode('qrcode', {
          width: 80, // 设置宽度，单位像素
          height: 80, // 设置高度，单位像素
          text, // 设置二维码内容或跳转地址
        });
      }
    },
    // 获取订单信息统计金额和笔数
    getCountData() {
      const params = {
        startDate: this.date ? this.date[0] : '',
        endDate: this.date ? this.date[1] : '',
        orderOir: 'POINTS_MALL',
        status: this.status,
        orderType: this.orderType,
        distributionType: this.distributionType,
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.queryOrderCount, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.orderCount = res.count;
          this.atmCount = res.sum == null ? 0 : res.sum;
        });
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        startDate: this.date ? this.date[0] : '',
        endDate: this.date ? this.date[1] : '',
        orderOir: 'POINTS_MALL',
        status: this.status,
        orderType: this.orderType,
        distributionType: this.distributionType,
        priceAbnormal: this.priceAbnormal,
        priceAbnormalFinsh: this.priceAbnormalFinsh,
        payTypes: this.payTypes.join(','),

        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.queryOrder, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     * @desc 获取标签列表
     */
    getTagData() {
      axios.post(api.marketing.tagsDrop).then((response) => {
        this.tagList = response;
      });
    },
  },
};
</script>
<style lang="less" scoped>
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
.search-item {
  border: 1px solid #e5e4e9;
  display: inline-flex;
  height: 36px;
  align-items: center;
  border-radius: 5px;
  padding: 0 8px;
  margin: 5px;
}
.search-box-form {
  /deep/ .tags-select {
    width: 200px !important;
  }
}
</style>