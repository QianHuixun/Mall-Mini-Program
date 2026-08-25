// pages/my/orderDetail/index.js
import Dialog from '@vant/weapp/dialog/dialog';
import http from '../../../utils/http';
import { weBtoa } from '../../../utils/weapp-jwt';
import QQMapWX from '../../../utils/qqmap-wx-jssdk.min.js';
import utils from '../../../utils/util.js';
import drawQrcode from '../../../utils/weapp.qrcode.esm.js';
// 实例化API核心类
var qqmapsdk = new QQMapWX({
  key: 'IGCBZ-MWAK3-DA633-YA7GK-BNZG7-ZHFSG' // 必填
});
let app = getApp();
const { applyTheme } = require('../../../utils/themeMixin')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    orderInfo: {},
    pkey: "",
    qrCode: "",
    pickAdress: "",
    shopTime: "",
    goodsListHeight: 0,
    isFold: false,
    foldHeight: 0,
    longitude: '', // 给个默认的经纬度 定位到北京天安门
    latitude: '', //
    distance: '',
    distanceKM: '',
    polyline: [], // 存放路线的经纬度
    markers: [// 自己设置的三个mark标记，分别是 商家，用户，骑手
      {
        iconPath: "/images/my/map-qishou.png",
        id: 1,
        latitude: '',
        longitude: '',
        width: 35,
        height: 32,
        customCallout: {
          anchorY: -10,
          anchorX: 5,
          display: 'ALWAYS',
        },
      },
      { 
       iconPath: "/images/my/map-shichang.png",
       id: 2,
       latitude: '',
       longitude: '',
       width: 22,
       height: 32
     }, {
       iconPath: "/images/my/map-shouhuo.png",
       id: 3,
       latitude: '',
       longitude: '',
       width: 22,
       height: 32
     }, 
    ],
    refundStatus: {
        REFUND_APPLYING: "已提交退款，待商家处理",
        REFUND_AGREE: "",
        REFUND_FINAL: "退款成功",
        REFUND_REFUSE: "退款失败",
    },
    jdType: {
        RETURN_MONEY: '退款',
        RETURN_GOODS: '退货',
        EXCHANGE: '换货',
    },
    jdRefundStatus: {
      REFUND_APPLYING: "申请中待审核",
      REFUND_AGREE: "",
      REFUND_FINAL: "退款成功",
      REFUND_REFUSE: "退款失败",
      JD_PENDING_APPROVAL: "申请中待审核",
      JD_APPROVAL_ACCEPTED: "审核完成待收货",
      JD_APPROVAL_REJECTED: "审核不通过",
      JD_RECEIPTED: "收货完成待处理",
      JD_PROCESSED_SUCCESS: "处理完成",
      JD_PROCESSED_FAILED: "处理失败",
      JD_PENDING_CONFIRM: "待用户确认",
      JD_CONFIRMED: "用户确认完成",
      JD_CANCELED: "售后取消",
    },
    jdRefundStatusExpress: {
        REFUND_APPLYING: "已提交退款，待商家处理",
        REFUND_AGREE: "",
        REFUND_FINAL: "退款成功，退款金额已原路返回",
        REFUND_REFUSE: "退款失败",
        JD_PENDING_APPROVAL: "您已成功申请售后，待商家审核",
        JD_APPROVAL_ACCEPTED: "",
        JD_APPROVAL_REJECTED: "售后审核未通过",
        JD_RECEIPTED: "商家已收货，等待商户处理",
        JD_PROCESSED_SUCCESS: "商户已处理完成，等待退款",
        JD_PROCESSED_FAILED: "售后处理失败",
        JD_PENDING_CONFIRM: "退款金额已原路退回，待用户确认",
        JD_CONFIRMED: "退款金额已原路退回",
        JD_CANCELED: "已取消售后，如有需要可在订单详情页重新提交售后",
    },
    jdExchangeStatusExpress: {
        REFUND_APPLYING: "已提交换货，待商家处理",
        REFUND_AGREE: "",
        REFUND_FINAL: "换货成功",
        REFUND_REFUSE: "换货失败",
        JD_PENDING_APPROVAL: "您已成功申请售后，待商家审核",
        JD_APPROVAL_ACCEPTED: "",
        JD_APPROVAL_REJECTED: "售后审核未通过",
        JD_RECEIPTED: "商家已收货，等待商户处理",
        JD_PROCESSED_SUCCESS: "商户已处理完成，等待换货",
        JD_PROCESSED_FAILED: "售后处理失败",
        JD_PENDING_CONFIRM: "已为您换新商品，请关注新订单状态",
        JD_CONFIRMED: "换货订单已完成",
        JD_CANCELED: "已取消售后，如有需要可在订单详情页重新提交售后",
    },
    showPopup: false,
    photoIndex: 0,
    isOnAppShow: false, // 是否是确认收货回调来的
    enableComment: false,//是否开启评价功能
    theme: null,
    jdDeliveryInfo: null,   // jd物流信息
    showRefundConfirm: false,
    isReceived: false,
    jdGoodsDisplayName: '京东专区',
    payTypeName: {
      ORDER_ZHIFUBAO: '支付宝',
      ORDER_WEIXIN: '微信',
      ORDER_ELECTRONIC_ACCOUNT:  app.globalData.ascription == 22 || app.globalData.ascription == 13 ?  'I DO' : '钱包',
      ZXYW_WEIXIN: '中信银行',
      NM_MEMBER: "会员卡",
      MSD_COMBINATION: '热力豆',
      ELECTRONIC_ACCOUNT_COMBINATION: app.globalData.ascription == 22 || app.globalData.ascription == 13 ?  'I DO' : '钱包'
    },
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log("options",options)
    applyTheme(this)
    if(options.q) {
      const q = decodeURIComponent(options.q);
      const pkey = utils.getQueryString(q, 'pkey');
        this.setData({
          pkey:pkey
        });     
    }
    
    if(options.pkey) {
      this.setData({
        pkey:options.pkey
      });   
    }
   
    
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function (options) {
    if(!this.data.isOnAppShow){
        this.getData();
        this.getCommentStatus();
    }
    this.setData({
        isOnAppShow: false,
    });
  },
  getCommentStatus: function() {
    const url= "/v1/app/market/index/config/ascription/get",that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        that.setData({
          enableComment: res.data.result.enableComment
        })
      }
    });

  },
  goMap(){
    wx.navigateTo({
      url: '/pages/my/map/index?pkey=' + this.data.orderInfo.pkey + '&status=' + this.data.orderInfo.thirdPartyStatus,
    })
  },
  goGoodsDetails(e) {
    const { enabled, goods, MType } = e.currentTarget.dataset.item;
    console.log("enabled",enabled, goods)
    if(enabled) {
      if(MType ==="INTEGRAL_MSD_GOODS") {
        wx.navigateTo({
          url: '/pages/shouyeGroup/jdGoodsDetail/index?pkey=' + goods,
        })
      } else {
        wx.navigateTo({
          url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${goods}`
       });
      }
     

     
    }  else {
      wx.showToast({
        title: '当前商品已下架',
      });
    }
    
  },
  // 评价
  handleEvaluate() {
    wx.setStorageSync('orderInfoGoods', JSON.stringify(this.data.orderInfo.infos));
    wx.navigateTo({
      url: '/pages/my/goEvaluate/index?pkey=' + this.data.orderInfo.pkey
    })
  },
    // 评价
    LookEvaluate() {
      wx.navigateTo({
        url: '/pages/my/lookEvaluate/index?pkey=' + this.data.orderInfo.pkey
      })
    },
  /*
  *@desp 查看退款记录,1次退款：跳转到退款订单详情;多次退款：跳转到退款订单列表，并筛选当前订单关联的退款订单数据；
  *@param {isComplex} [boolean] true 多条 false 一条
  */
  goRefundList() {
    if(this.data.orderInfo.isComplex) {
      wx.navigateTo({
        url: '/pages/my/refundOrder/index?pkey=' + this.data.orderInfo.pkey,
      })
    } else {
      wx.navigateTo({
        url: '/pages/my/refundDetail/index?pkey='+ this.data.orderInfo.refundPkey,
      })
    }
    

  },
  /**
   * @desc 
   */
  goExpressRouter() {
    if(this.data.orderInfo.orderType == 'INTEGRAL_JD_ORDER') {
        wx.setStorageSync('jdDeliveryInfo', JSON.stringify(this.data.jdDeliveryInfo)); 
        wx.navigateTo({
            url: `/pages/my/expressRouter/index?orderType=${this.data.orderInfo.orderType}`,
        })
        return
    }
    wx.setStorageSync('orderInfo', JSON.stringify(this.data.orderInfo)); 
    wx.navigateTo({
      url: '/pages/my/expressRouter/index',
    })
  },
  // 获取 市场、会员、骑手坐标
  getCourier(){
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v2/app/market/lm/order/get/courier",
      data: {
        pkey: this.data.orderInfo.pkey,
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        that.setData({
          longitude: res.data.result.longitude,
          latitude: res.data.result.latitude,
          distance: res.data.result.distance,
          distanceKM: (res.data.result.distance/1000).toFixed(2),
          ['markers[0].latitude']: res.data.result.latitude || 39.984060,
          ['markers[0].longitude']: res.data.result.longitude || 116.307520,
          ['markers[1].latitude']:  res.data.result.marketLatitude,
          ['markers[1].longitude']: res.data.result.marketLongitude,
          ['markers[2].latitude']:  res.data.result.memberLatitude,
          ['markers[2].longitude']: res.data.result.memberLongitude,
        });
        var from = {latitude: res.data.result.marketLatitude, longitude: res.data.result.marketLongitude},
        to = {latitude: res.data.result.memberLatitude, longitude: res.data.result.memberLongitude};
        that.getDirection(from, to);
      }
    });

  },
  // 地图骑手路径
  getDirection(from, to) {
    var _this = this;
    //调用距离计算接口
    qqmapsdk.direction({
      mode: 'bicycling',//可选值：'driving'（驾车）、'walking'（步行）、'bicycling'（骑行），不填默认：'driving',可不填
      //from参数不填默认当前地址
      from: from,
      to: to, 
      success: function (res) {
        console.log(res);
        var ret = res;
        var coors = ret.result.routes[0].polyline, pl = [];
        //坐标解压（返回的点串坐标，通过前向差分进行压缩）
        var kr = 1000000;
        for (var i = 2; i < coors.length; i++) {
          coors[i] = Number(coors[i - 2]) + Number(coors[i]) / kr;
        }
        //将解压后的坐标放入点串数组pl中
        for (var i = 0; i < coors.length; i += 2) {
          pl.push({ latitude: coors[i], longitude: coors[i + 1] })
        }
        console.log(pl)
        //设置polyline属性，将路线显示出来,将解压坐标第一个数据作为起点
        _this.setData({
          polyline: [{
            points: pl,
            color: '#0DAE4E',
            width: 4
          }]
        });
        console.log("polyline",_this.data.polyline)
      },
      fail: function (error) {
        console.error(error);
      },
      complete: function (res) {
        console.log(res);
      }
    });
  },
  // 刷新地图
  refreshMap() {
    this.getCourier();
  },
  handleLookMore() {
    console.log(11)
    let isFold = this.data.isFold;
    isFold = !isFold
    this.setData({
      isFold
    });
  },
  
  /**获取优惠券核销状态 */
  getGiftStatus() {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/lm/order/queryByOrder",
      data: {
        orderPkey: this.data.orderInfo.pkey,
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        console.log('111', res)
        that.setData({
          ['orderInfo.giftStatus']: res.data.result[0].status
        })
      }
    });

  },
  /**
   * @desc 绘制提货二维码，核销码
   */
  showQrCode() {
    let code = this.data.orderInfo.verifyCode + '-' + this.data.orderInfo.code
    if(this.data.orderInfo.orderOir === 'MARKET_MALL') {
      code = `${this.data.orderInfo.url}?code=${this.data.orderInfo.code}&verifyCode=${this.data.orderInfo.verifyCode}`
    }
    console.log(code);
    drawQrcode({
      width: 154,
      height: 154,
      canvasId: 'qrcode',
      text: code,      
    });
  },
  /**获取优惠券二维码 */
  getQrCode() {
    var that = this;
    http.request({
      method: "GET",
      url: app.globalData.ajax_url + "/v1/app/market/lm/member/down/code?cardNumber=" + this.data.orderInfo.cardCode,
      data: {},
      responseType: 'arraybuffer',
      header: {
        "Content-Type": "application/json;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        console.log(res)
        // let base64 = wx.arrayBufferToBase64(res.data),
         let qrCodeUrl = 'data:image/jpeg;base64,' + res.data;
        that.setData({
          qrCode: qrCodeUrl
        });
      }
    });

  },
  /**
   * 获取订单信息
   */
  getData: function () {
    var that = this,
      url = "/v2/app/market/lm/order/loadOrder";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: that.data.pkey
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          wx.setStorageSync('refundInfo', JSON.stringify(res.data.result));
          that.setData({
            orderInfo: res.data.result
          });
          if(that.data.orderInfo.thirdPartyStatus == 'THIRD_PARTY_DELIVERY' || that.data.orderInfo.thirdPartyStatus == 'THIRD_PARTY_PICKING_UP') {
            that.getCourier();
          }
          let foldHeight = 0;
          // 140:商品高度; 32:商品高度上外边距margin-top; 90:规格高度; 24:规格高度上外边距margin-top;
          if (res.data.result.infos[0].lines.length > 1) {
            foldHeight = (90 + 24 ) * 2 + 140
          } else {
            if (res.data.result.infos[1] && res.data.result.infos[1].lines.length > 1) {
              foldHeight = 140 * 2 + 32 + (90 + 24)
            } else {
              foldHeight = (140 * 3) + (32 * 2 )  
            }
          }
          let goodsListHeight = 0;
          res.data.result.infos.map(item => {
            if (item.lines.length == 1) {
              goodsListHeight = goodsListHeight + 140
            } else {
              goodsListHeight = goodsListHeight + 140 + (item.lines.length * 114)
            }
          })
          if (res.data.result.infos.length > 1) {
            goodsListHeight = goodsListHeight + (res.data.result.infos.length - 1) * 32
          }
          that.setData({
            goodsListHeight,
            foldHeight: foldHeight > goodsListHeight ? goodsListHeight : foldHeight
          });
          let pickAdress = '',
            shopTime = '';
          console.log('---', res.data.result);
          pickAdress = res.data.result.addr.addr
          // for (let i in res.data.result.distype) {
          //   let item = res.data.result.distype[i];
          //   console.log(item);
          //   if (item.type == 'PICKUP') {
          //     pickAdress = item.address;
          //     shopTime = item.yytb + '-' + item.yyte;
          //   }
          // }
          that.setData({
            pickAdress: pickAdress,
            // shopTime: shopTime
          })
          if (res.data.result.orderType == 'GIFT_ORDER' && res.data.result.status != 'UNPAID_ORDER') {
            that.getGiftStatus();
          }
          if(res.data.result.distributionType === 'PICKUP') {
            that.showQrCode()
          }
          if(res.data.result.orderType === 'INTEGRAL_JD_ORDER') {
            that.getJDDeliveryInfo()
            that.getDisPlayName()
          }
          that.getQrCode();
        } else {
          wx.showToast({
            icon: 'none',
            title: res.data.msg || ''
          })
        }
      }
    });
  },
   /**
   * 
   * 微信确认收货的回调
   */
  onAppShow(options) {
    if(options && options.referrerInfo.extraData.status == 'success'){
      console.log('收到 app.onShow 的数据:', options);
      this.setData({
          isOnAppShow: true
      });
      this.handleArrivedHttp(options.referrerInfo.extraData.req_extradata.pkey);
    }
},
  /**
   * 确认收货
   */
  handleArrived: function () {
    const { pkey, openBusinessView, transactionid } = this.data.orderInfo
    if (openBusinessView) {
      if (wx.openBusinessView) {
        wx.openBusinessView({
          businessType: 'weappOrderConfirm',
          extraData: {
            transaction_id: transactionid,
            pkey: pkey,
          },
          success() {
            //dosomething
          },
          fail(err) {
            //dosomething
            console.log(err, 'err')
          },
          complete() {
            //dosomething
          }
        });
      } else {
        //引导用户升级微信版本
        wx.showToast({
          title: '微信版本太低，请升级'
        })
      }
    } else {
      Dialog.confirm({
        message: '确定收货吗？',
      }).then(() => {
        this.handleArrivedHttp(pkey);
      });
    }
  },

   /**
   * 
   * 确认收货请求
   */
  handleArrivedHttp(pkey){
    var that = this;
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + "/v1/app/market/lm/order/drOrder",
        data: {
          pkey: pkey
        },
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };
          if (res.data.success) {
            wx.showToast({
              title: '确认收货成功'
            });

            that.getData();
          } else {
            wx.showToast({
              icon: 'none',
              title: res.data.msg || ''
            })
          }
        }
      });
  },
  /**
   * @desc 查看核销二维码
   */
  checkCode() {
    wx.previewImage({
      urls: [this.data.qrCode],
    })
  },
  // 退款
  handleRefund() {
    if(this.data.orderInfo.hasApplyingRefund) {
      wx.showToast({
        title: '当前有一笔退款订单还未处理',
        icon: 'none'
      })
    } else if (this.data.orderInfo.orderType == 'INTEGRAL_JD_ORDER') {
        this.handleJDRefund()
    } else {
      wx.navigateTo({
        url: `/pages/my/refundApply/index?pkey=${this.data.pkey}`,
      });
    }
  },
  /**
   * 申请退款确认弹窗,选择是否收货
   */
  handleReceivde(data) {
    const isReceived = data.currentTarget.dataset.receive
    console.log(isReceived);
    this.setData({
        isReceived: Boolean(isReceived)
    })
  },
  /**
   * 关闭申请退款确认弹窗
   */
  handleCloseRefundConfirm() {
    this.setData({
        showRefundConfirm: false,
    })
  },
  /**
   * 申请退款确认弹窗
   */
  handleRefundConfirm() {
    const { isReceived, orderInfo } = this.data
    if(isReceived) {
        this.handleCloseRefundConfirm()
        this.handleArrived()
    } else {
        const lines = orderInfo.infos.map(item => {
            const newRefundAmt = item.couponPrice * (item.num - item.refundNum);
            const newRefundNum = item.num - item.refundNum;
            return {
                pkey: item.orderLinePkey,
                refundAmt: Number((newRefundAmt).toFixed(2)),
                num: newRefundNum,
                photo: item.photo
            }
        })
        const refundData = {
            pkey: this.data.orderInfo.pkey,
            lines,
            status: this.data.orderInfo.status,
            tel: this.data.orderInfo.tel,
            orderOir: this.data.orderInfo.orderOir,
            orderType: this.data.orderInfo.orderType,
        }
      wx.setStorageSync('refundStepOneData', JSON.stringify(refundData));
      wx.navigateTo({
        url: `/pages/my/refundSubmit/index?pkey=${this.data.pkey}`,
      });
    }
  },
  /**
   * 京东专区申请售后
   */
  handleJDSupport() {
    if (this.data.orderInfo.hasApplyingRefund) {
        wx.showToast({
            title: '当前有一笔售后订单还未处理',
            icon: 'none'
        })
    } else {
        wx.navigateTo({
            url: `/pages/my/supportApply/index?pkey=${this.data.pkey}`,
        });
    }
  },
  /**
   * 京东专区申请退款
   */
  handleJDRefund() {
    const { orderInfo } = this.data
    if (orderInfo.status == 'SHIPPED_ORDER') {
        this.setData({
          showRefundConfirm: true
        })
        return
    }
    const lines = orderInfo.infos.map(item => {
        const newRefundAmt = item.couponPrice * (item.num - item.refundNum);
        const newRefundNum = item.num - item.refundNum;
        return {
            pkey: item.orderLinePkey,
            refundAmt: Number((newRefundAmt).toFixed(2)),
            num: newRefundNum,
            photo: item.photo
        }
    })
    const refundData = {
      pkey: this.data.orderInfo.pkey,
      lines,
      status: this.data.orderInfo.status,
      tel: this.data.orderInfo.tel,
      orderOir: this.data.orderInfo.orderOir,
      orderType: this.data.orderInfo.orderType,
    }
    wx.setStorageSync('refundStepOneData', JSON.stringify(refundData));
    wx.navigateTo({
      url: `/pages/my/refundSubmit/index?pkey=${this.data.pkey}`,
    });
  },
  /**
   * 立即支付
   */
  handleUnpaid: function (data) {
    var that = this,
      pkey = this.data.orderInfo.pkey,
      url = "/v2/app/market/lm/order/getUnpaidOrder";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: pkey
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/pay/index'
          })
        } else {
          wx.showToast({
            icon: 'none',
            title: res.data.msg || ''
          })
        }
      }
    });
  },
  /**
   *拨打骑手电话
   */
  riderPhoneClick () {
    console.log('----', this.data.orderInfo.courierMobile)
    const courierMobile = this.data.orderInfo.courierMobile
    wx.makePhoneCall({
      phoneNumber: courierMobile,
    })
  },

  handlePhone() {
    let that = this;
    wx.showModal({
      title: '是否拨打商户号码',
      content: that.data.orderInfo.list1[0].verdorMobile,
      success(res) {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: that.data.orderInfo.list1[0].verdorMobile,
          })
        }
      }
    })

  },
  showImage(data) {
    
    const index = data.currentTarget.dataset.index;
    this.setData({
      showPopup: true,
      photoIndex: index
    })
    console.log(data)
  },
  onClose(){
    this.setData({
      showPopup: false
    })
  },
  /**
   * 获取京东物流信息
   */
  getJDDeliveryInfo() {
    const pkey = this.data.orderInfo.pkey;
    const url = "/v1/app/jd/order/deliveryInfo";
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: { pkey },
        success: (res) => {
            console.log('deliveryInfo:', res);
            if(!res.data.success) return
            let result = res.data.result
            result.trackInfoList = result.trackInfoList.reverse()
            this.setData({
                jdDeliveryInfo: result
            })
        }
    })
  },
  
  /**
   * 获取专区名称
   */
  getDisPlayName() {
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
        success: (res) => {
            const data = res.data.result
            if (data.jdGoodsDisplayName) {
                this.setData({
                    jdGoodsDisplayName: data.jdGoodsDisplayName
                })
            }
        }
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {}
})