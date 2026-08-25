// pages/pay/pay/index.js
let app = getApp();
import utils from '../../../utils/util.js';
import Dialog from '@vant/weapp/dialog/dialog';
import http from '../../../utils/http'
Page({
  /**
   * 页面的初始数据
   */
  data: {
    imgUrl: app.globalData.file_url,
    show: false, // 是否显示跑腿配送时间弹出窗
    loading: false,
    disabled: false,
    buyGoods: "",
    isAuto: true,
    iShidden: true,
    orderInfo: {},
    type: "cart",
    marketName: "",
    num: 0, //购买数量
    remark: '', //留言
    shareFarm: '', //分享的市场对象
    footerMargin: 0,
    activeKey: 0,
    activeId: 0,
    timeItems: [],
    mainActiveIndex: 0,
    shoptype: app.globalData.qrCode  ? 'ts': 'ps',
    deliveryType: 'IMMEDIATELY', // 配送类型, 默认立即配送：IMMEDIATELY； 预约配送：ORDERED； 自提： PICKUP
    PickUpTime: '',
    orderedTime: '',
    pstimeCopy: '',
    distributionTypeCopy: null,
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    spaceShow: false,
    ismarket: false,
    marketSpacePhoto: [],
    pointSpacePhoto: [],
    pickupAddr: null,
    memberBalance: null,
    isReservation: '',  // 预计配送时间
    type: '',// gwc 从购物车跳过来， goods 从商品详情页跳过来
    gwcs:"", //购物车选中的商品pkey
    goodsSpace: '', // 商品详情页传来的商品规格
    association: '',//加工服务
    goodsNum: '',//  商品详情页传来的商品数量
    goodsTJR: '',//  商品详情页传来的推荐人
    addressPkey: '',// 用于切换切换配送、自提时，传递地址pkey
    pickAddressPkey: '',// 自提地址pkey
    dineIn: "",
    walletName: '钱包',
    payType: 'ORDER_WEIXIN', //组合支付2 支付类型
    payAmt: 0,// 支付金额
    payTypeAmt: 0,// 支付类型余额
  },
  remarkConfirm() {
    this.setData({
      'orderInfo.remark': this.data.remark,
      remarkShow: false,
      remark: ''
    })
    console.log(this.data.remark)
  },
  getTips(){
    Dialog.alert({
      message: '请兑换前，提前联系商户',
      closeOnClickOverlay:true
    }).then(() => {
      // on close
    });
  },
  remarkClose() {
    this.setData({
      remarkShow: false,
      remark: ''
    })
  },
  spaceClose() {
    this.setData({
      spaceShow: false,

    })
    setTimeout(() => {
      this.setData({
        ismarket: false
      })
    }, 200)
  },
  handleRemark() {
    this.setData({
      remarkShow: true,
      remark: this.data.orderInfo.remark
    })
  },
  /**
   * @desc 查看所有规格
   */
  handleLookspace(event) {
    this.setData({
      spaceShow: true,
    })
    if (event.currentTarget.dataset.type == 'market') {
      this.setData({
        ismarket: true,
      })
    } else {
      this.setData({
        ismarket: false,
      })

    }
  },
  /**
   * @desc 返回
   */
  onClickLeft() {
    wx.navigateBack({
      delta: 1
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      dineIn: app.globalData.qrCode,
      shoptype: app.globalData.qrCode ? 'ts': 'ps',
    })
    console.log("options", options)
    if (options.hasOwnProperty('shareFarm') && JSON.parse(options.shareFarm) != "") {
      this.setData({
        marketName: JSON.parse(options.shareFarm).name,
        shareFarm: JSON.parse(options.shareFarm),
      });

    } else {
      this.setData({
        marketName: app.globalData.location.name
      });
    }
    if(options.type) {
      this.setData({
        type: options.type
      })
    }
    if(options.gwcs) {
      this.setData({
        gwcs: options.gwcs
      })
    }
    if(options.space) {
      this.setData({
        goodsSpace: options.space,
        association: options.association,
        goodsNum: options.num,
        goodsTJR: options.tjr
      })
    }
    const query = wx.createSelectorQuery().in(this),
      that = this;
    query.select('#submit-bar>>>.van-submit-bar').boundingClientRect();
    query.exec(function (res) {
      that.setData({
        footerMargin: res[0].height
      })
    });

    this.getData();
    this.getMemberPay();
    this.isStartingPrice();
    if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
        this.setData({ walletName: 'I DO' })
    }
  },
   isStartingPrice() {
    if(this.data.orderInfo.startingPrice > this.data.orderInfo.amto && this.data.shoptype=='ps') {
      Dialog.confirm({
        title: '不满足配送起送价',
        message: `配送订单商品金额满${this.data.orderInfo.startingPrice}元起送`,
        theme: 'round-button',
        confirmButtonText: '切换自提',
        className: 'starting_dialog'
      })
        .then(() => {
          this.handleShopTypeChange('zt');
        })
        .catch(() => {
          wx.navigateBack({
            delta: 1 // 返回的页面层数，1 表示返回上一层页面
          })
        });
    }
   
   },
  /**
   * 获取订单信息
   */
  getData: function () {
    this.setData({
      orderInfo: JSON.parse(wx.getStorageSync('orderInfo'))
    });

    let orderInfo = this.data.orderInfo;
    // 包厢商品显示堂食
    if(orderInfo.dineIn) {
      this.setData({
        shoptype: 'ts'
      })
    }
   
    let marketSpacePhoto = orderInfo.farmerPhoto;
  
    let pointSpacePhoto = [];
    if (orderInfo.pointInfo && orderInfo.pointInfo.length) {
      orderInfo.pointInfo.map(item => {
        if (item.lines) {
          item.lines.map(subItem => {
            pointSpacePhoto.push(subItem.photo)
          })
        }
      })
    }
    // console.log(orderInfo.pointSpacePhoto)
    const payAmt = this.data.shoptype == 'zt' ? this.data.orderInfo.pickupAmt : this.data.orderInfo.amtn;
    this.setData({
      orderInfo,
      pointSpacePhoto,
      marketSpacePhoto,
      orderedTime: orderInfo.pstime ? orderInfo.pstime.slice(11) : '',
      payAmt,
      payTypeAmt: 0
      // addressPkey: orderInfo.addr ? orderInfo.addr.pkey  : ''
    });
    this.initPayType();
    if(this.data.shoptype == 'zt') {
      this.setData({
        pickAddressPkey: orderInfo.addr ? orderInfo.addr.pkey  : '',
      });
    } else if(this.data.shoptype == 'ps') {
      this.setData({
        addressPkey: orderInfo.addr ? orderInfo.addr.pkey  : ''
      });
    }
    // console.log('orderinfo1',this.data.orderInfo)
  },
  /**
   * 获取会员支付余额
   */
  getMemberPay() {
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v2/app/market/lm/order/check/nm/member/pay',
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
        },
        success: (res) => {
          console.log(res);
          const result = res.data.result
          this.setData({
            memberBalance: result
          })
        }
      })
  },
  /**
   * 切换送货方式，自提or配送or堂食
   */
  handleTabClick(e) {
    const shoptype = e.target.dataset.shoptype;
     if(this.data.orderInfo.dineIn && shoptype!="ts") {
      wx.showToast({
        title: '预定包厢仅支持堂食',
        icon: 'none'
      })
      return;
    }
    this.handleShopTypeChange(shoptype);
  },
  handleShopTypeChange(shoptype) {
    if (this.data.orderInfo.pointInfo && this.data.orderInfo.pointInfo.length && shoptype == 'zt') {
      wx.showToast({
        title: `该订单包含积分商城商品，不能自提！`,
        icon: 'none'
      });
      return
    }
    if (!this.data.orderInfo.pickup && shoptype == 'zt') {
      wx.showToast({
        title: `该订单有个别商品不能自提！`,
        icon: 'none'
      });
      return
    }
    if (!this.data.orderInfo.delivery && shoptype == 'ps') {
      wx.showToast({
        title: `该订单有个别商品不能配送！`,
        icon: 'none'
      });
      return
    }

    // if (shoptype == this.data.shoptype) return
    console.log(' this.data.pickAddressPkey', this.data.pickAddressPkey,"this.data.addressPkey",this.data.addressPkey)
    var url = "",params= {},that = this;
    if(this.data.type == 'gwc') {
      url = "/v2/app/market/lm/order/buyGwc";
      params = {
        pickupType: shoptype == 'zt' ? true : false,
        gwcs: this.data.gwcs,
        dineIn: app.globalData.qrCode && shoptype=='ts' ? true : false,
        addressPkey: shoptype == 'zt' ? this.data.pickAddressPkey : this.data.addressPkey,
        latitude: wx.getStorageSync('latitude'),
        longitude: wx.getStorageSync('longitude'),
      }
    } else if( this.data.type == 'goods') {
      url = "/v2/app/market/lm/order/buyGoods";
      params = {
        pickupType: shoptype == 'zt' ? true : false,
        space: this.data.goodsSpace,
        num: this.data.goodsNum,
        tjr: this.data.goodsTJR,
        dineIn: app.globalData.qrCode && shoptype=='ts' ? true : false,
        association: this.data.association,
        addressPkey: shoptype == 'zt' ? this.data.pickAddressPkey :  this.data.addressPkey ,
        latitude: wx.getStorageSync('latitude'),
        longitude: wx.getStorageSync('longitude'),
      }
    }
    if(this.data.type) {
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: params,
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
        },
        success: res => {
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          }
          if (res.data.success) {
            let orderInfo = res.data.result
            if(shoptype == 'zt') {
              orderInfo.pickupPkey = orderInfo.splList[0].pkey
              orderInfo.pickupAddr = orderInfo.splList[0].address
            } else {
              orderInfo.pickupPkey = null
              orderInfo.pickupAddr = null
            }
            wx.setStorageSync('orderInfo', JSON.stringify(orderInfo));   
            that.setData({
              orderInfo: orderInfo,
            });
            if(shoptype == 'zt') {
              that.setData({
                pickAddressPkey: orderInfo.addr ? orderInfo.addr.pkey  : ''
              });
            } else if(shoptype == 'ps') {
              that.setData({
                addressPkey: orderInfo.addr ? orderInfo.addr.pkey  : ''
              });
            }
            console.log("orderInfo-tab", that.data.orderInfo)
            that.refreshOrderInfo(shoptype);
            that.getData();
          } else {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            })
          }
        }
      })
    } else {
      that.refreshOrderInfo(shoptype);
    }
  },
  refreshOrderInfo(shoptype) {
    let orderInfo = this.data.orderInfo;
    let orderInfoPstime = this.data.orderInfo.pstime
    let orderInfoDistributionType = this.data.orderInfo.distributionType
    let {
      pstimeCopy,
      distributionTypeCopy
    } = this.data

    console.log('orderInfo', orderInfo);

    if(orderInfo.orderType == 'PRESALE_ORDER'){ //预售商品配送和自提时间固定不能变动
      pstimeCopy = orderInfo.pstime
    }
    
    // 切换配送方式是保存另一种配送方式的数据
    orderInfo.distributionType = distributionTypeCopy
    orderInfo.pstime = pstimeCopy
    pstimeCopy = orderInfoPstime
    distributionTypeCopy = orderInfoDistributionType

    let activeId = -1
    this.setData({
      shoptype,
      activeId,
      orderInfo,
      pstimeCopy,
      distributionTypeCopy
    })
    this.isStartingPrice();

    console.log(orderInfo)
  },
  onClose() {
    this.setData({
      show: false
    })
    setTimeout(() => {
      if (this.data.spaceShow) {
        this.setData({
          spaceShow: false
        })
      }
    }, 200)

  },
  onClickNav({
    detail = {}
  }) {
    this.setData({
      mainActiveIndex: detail.index || 0,
    });
  },
  /**
   * 选择时间选项
   */
  onClickItem({
    detail = {}
  }) {
    console.log("detail", detail);
    // const activeId = this.data.activeId === detail.id ? null : detail.id;
    const activeId = detail.id;
    const showtype = this.data.shoptype
    if (detail.type === 'IMMEDIATELY') {
      this.setData({
        deliveryType: 'IMMEDIATELY',
        orderedTime: detail.original.slice(11),
        ["orderInfo.distributionType"]: 'IMMEDIATELY'
      })
    } else if (showtype === 'zt') {
      this.setData({
        deliveryType: 'PICKUP',
        PickUpTime: `${detail.isTomorrow? detail.tomorrow:''}${detail.text}`,
        ["orderInfo.distributionType"]: 'PICKUP'
      })
    } else {
      this.setData({
        deliveryType: 'ORDERED',
        orderedTime: `${detail.isTomorrow? detail.tomorrow:''}${detail.text}`,
        ["orderInfo.distributionType"]: 'ORDERED'
      })
    }
    this.setData({
      activeId,
      ["orderInfo.pstime"]: detail.original,
      show: false,
    });
    console.log("orderinfo", this.data.orderInfo)
  },
  /**
   * 跑腿配送时间 点击事件
   */
  getTime: function () {
    this.setData({
      show: true,
    })
    const shoptype = this.data.shoptype,
      url = '/v1/app/market/lm/order/get/distributionType/psTime'
    console.log("shoptype", shoptype)
    const data = {
      marketPkey: this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey,
      type: shoptype == 'ps' ? 'ORDERED' : 'PICKUP',
      addressPkey: shoptype == 'zt'? this.data.pickAddressPkey : this.data.addressPkey
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: (res) => {
        console.log("res2", res);
        console.log(res.data.result);
        if(res.data.result === null) {
          wx.showModal({
            title: '提示',
            content: '无法获取到市场，请重新选择市场',
            showCancel: false, // 不显示取消按钮
            success: function (res) {
              wx.switchTab({
                url: '/pages/home/shouye/index'
              });
            }
          })
        } 
        this.getTimeList(res.data.result)
      },
      fail() {
        console.log("res1")
      }
    })
  },
  getTimeList({
    imPsTime,
    psTime,
    psOption,
    type
  }) {
    // console.log(res);
    let timeItems = [],
      todayId = 0,
      tomorrowId = 0,
      acquiredId = 0
    const today = utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D')
    const week = utils.getWeekDate(today, '周', )
    const tomorrow = utils.formatTimeInArr((new Date().getTime() + 86400000) / 1000, 'Y-M-D')
    const tomorrowWeek = utils.getWeekDate(tomorrow, '周', )
    const acquired = utils.formatTimeInArr((new Date().getTime() + 86400000 * 2) / 1000, 'Y-M-D')
    const acquiredWeek = utils.getWeekDate(acquired, '周', )
    console.log('today', today, week, tomorrowWeek);
    let todayList = {
        text: `今天(${week})`,
        children: []
      },
      tomorrowList = {
        text: `明天(${tomorrowWeek})`,
        children: []
      },
      acquiredList = {
        text: `后天(${acquiredWeek})`,
        children: []
      }
    if (type === 'ORDERED' && imPsTime) {
      todayList.children.push({
        text: `尽快送达，预计${imPsTime.slice(11)}送达`,
        original: imPsTime,
        type: 'IMMEDIATELY',
        id: todayId++
      })
    }
    if (psOption.length > 0) {
      psOption.forEach(item => {
        if (item.indexOf(today) > -1) {
          todayList.children.push({
            text: item.split(' ')[1],
            original: item,
            id: todayId++
          })
        } else if(item.indexOf(tomorrow) > -1) {
          tomorrowList.children.push({
            text: item.split(' ')[1],
            original: item,
            id: tomorrowId++,
            isTomorrow: true,
            tomorrow: tomorrowWeek,
          })
        } else if(item.indexOf(acquired) > -1) {
          acquiredList.children.push({
            text: item.split(' ')[1],
            original: item,
            id: acquiredId++,
            isTomorrow: true,
            tomorrow: acquiredWeek,
          })
        }
      })
    }
    // timeItems.push(todayList)
    if (todayList.children && todayList.children.length) {
      timeItems.push(todayList)
    }
    if (tomorrowList.children && tomorrowList.children.length) {
      timeItems.push(tomorrowList)
    }
    if (acquiredList.children && acquiredList.children.length) {
      timeItems.push(acquiredList)
    }
    console.log({timeItems});
    this.setData({
      timeItems,
      mainActiveIndex: 0,
      activeId: -1,
      isReservation: psTime,
    })
  },
  /**
   * 获取当前时间,关闭弹出层
   */
  confirmTime(event) {
    console.log(event.detail.value)
    let date = event.detail.value,
      psDate;
    if (event.detail.value.indexOf('明天') > -1) {
      psDate = utils.formatTimeInArr((new Date().getTime() + 86400000) / 1000, 'Y-M-D') + date.replace('明天 ', '');
    } else {
      psDate = utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D') + ' ' + date;
    }
    this.setData({
      ["orderInfo.pstime"]: psDate,
      show: false
    });
  },
  /**
   * 退出时间选择
   */
  cancleTime: function () {
    this.setData({
      currentDate: this.data.time,
      show: false
    });
  },
  /**
   * 选择自提地址
   */
  goPickupAddr() {
    wx.setStorageSync('orderInfo', JSON.stringify(this.data.orderInfo));
    wx.navigateTo({
      url: '/pages/pay/pickupAddr/index',
    })
  },
   // 初始化支付类型，默认选中热力豆/I DO 支付，优先选热力豆，计算支付金额
   initPayType() {
    const orderInfo = this.data.orderInfo;
    if(orderInfo.unpayType) return;
    // const mtype = this.data.mtype;
    let payType = "ORDER_WEIXIN";
    let payTypeAmt = 0;
    let payAmt = 0;
    if(orderInfo.msdPay && orderInfo.myMsd > 0) {
      payType = "MSD_COMBINATION";
      payTypeAmt = orderInfo.myMsd;
    } else if( orderInfo.myCommn > 0) {
      payType = "ELECTRONIC_ACCOUNT_COMBINATION";
      payTypeAmt = orderInfo.myCommn;
    } 
    const orderAmt = this.data.shoptype == 'zt' ? orderInfo.pickupAmt : orderInfo.amtn;
    payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
    const payType2 = payType !== 'NM_MEMBER' || !payType  ?  'ORDER_WEIXIN'  : null;
    this.setData({
      ["orderInfo.payType"]: payType, 
      payType: payType2,
      payTypeAmt,
      payAmt,
    });

  },
  /**
   * 支付方式选择改变
   */
  paytypeChange: function (event) {
    console.log(event)
    // 从自定义属性 data-name 获取当前点击的 radio 的 name
    const { name, amt = 0 }= event.currentTarget.dataset;
  
      // 如果点击的选项已经是选中状态pay，则取消选中（置为 null）
    const  payType1 = this.data.orderInfo.payType === name ? null : name;
    const payType2 = payType1 !== 'NM_MEMBER' || !payType1  ?  'ORDER_WEIXIN'  : null;
    const orderAmt = this.data.shoptype == 'zt' ? this.data.orderInfo.pickupAmt : this.data.orderInfo.amtn;
    let payAmt = 0;
      if(payType1 === null) {
        payAmt =  orderAmt;
      } else {
        payAmt = orderAmt > amt ? orderAmt - amt : 0;
      }
    this.setData({
      ["orderInfo.payType"]: payType1,
      payType: payType2,
      payAmt,
      payTypeAmt: amt
    })
    
  },
  /**
   * 获取可选地址
   */
  getAddr: function () {
    console.log('deliveryType', this.data.deliveryType, this.data.shoptype);
    var hasMarket = false,
      pkey = "",
      isPickup = "",
      orderInfo = this.data.orderInfo;
    if (orderInfo.farmerInfo != null && orderInfo.farmerInfo.length > 0) {
      hasMarket = true;
    }
    if (orderInfo.addr != null) {
      pkey = orderInfo.addr.pkey;
    }
    if(this.data.shoptype === 'zt') {
      isPickup = true
    } else {
      isPickup = false
    }
    wx.navigateTo({
      url: '/pages/pay/addr/index?pkey=' + pkey + '&shareFarm=' + JSON.stringify(this.data.shareFarm) + '&type=' + hasMarket + '&isPickup=' + isPickup,
    });
  },
  /** 
   * 获取可选优惠券
   */
  goCard: function () {
    wx.setStorageSync('orderInfo', JSON.stringify(this.data.orderInfo));
    wx.navigateTo({
      url: '/pages/pay/card/index?shareFarm=' + JSON.stringify(this.data.shareFarm)
    })
  },
  /**
   * 提交支付
   */
  onSubmit: function () {
    let orderInfo = this.data.orderInfo;
    let pickupAddr = this.data.pickupAddr
    // console.log(this.data)
    // console.log("orderInfo",orderInfo)
    // console.log(orderInfo.addr)
    // console.log('pickupAddr', this.data.pickupAddr)
    if (orderInfo.addr == null  && orderInfo.farmerInfo != null && this.data.shoptype !== 'ts') {
      wx.showToast({
        title: this.data.shoptype =='ps' ? '请选择收货地址' : '请选择自提人员',
        icon: 'none'
      });
      return;
    }
  console.log(orderInfo.orderType, orderInfo.pstime,orderInfo.distributionType,this.data.shoptype)
    if ((orderInfo.orderType == 'MARKET_ORDER' || orderInfo.orderType == 'SHARE_ORDER') && orderInfo.pstime == "" && orderInfo.distributionType !== 'PICKUP'  && this.data.shoptype !== 'ts') {
      wx.showToast({
        title: '请选择配送时间',
        icon: 'none'

      });
      return;
    }
    console.log(orderInfo)
    if (orderInfo.orderType != "INTEGRAL_ORDER" && orderInfo.orderType != "GIFT_ORDER" && orderInfo.orderType != "COUPON_ORDER" && (orderInfo.distributionType != 'PICKUP' && !orderInfo.pstime) && this.data.shoptype !== 'ts') {
      wx.showToast({
        title: '请选择正确的配送时间',
        icon: 'none'
      })
      return
    }
    if(orderInfo.orderType != "INTEGRAL_ORDER" && orderInfo.orderType != "GIFT_ORDER"&& orderInfo.orderType != "COUPON_ORDER" && this.data.shoptype == 'zt' && !this.data.PickUpTime) {
      wx.showToast({
        title: '请选择自提时间',
        icon: 'none'
      })
      return
    }
    this.setData({
      loading: true,
      disabled: true
    });
    // return; {{}}
    // 判断自提是否选择了自提地址
    if(this.data.shoptype == 'zt' && this.data.pickupAddr && this.data.pickupAddr.addrDetail) {
      orderInfo.addr = pickupAddr
    }

    // 如果orderInfo.payType 订单支付类型 为null，则传"ORDER_WEIXIN"
    if(!orderInfo.payType) {
      orderInfo.payType = "ORDER_WEIXIN";
    }
    var that = this,
      url = "/v2/app/market/lm/order/commitOrder";

    if(this.data)
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: orderInfo,
      header: {
        'content-type': 'application/json;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {
          wx.setStorageSync('allCheckList', []);
          wx.setStorageSync('marketResult', []);
          wx.setStorageSync('mallResult', []);
          var result = res.data.result;
          that.setData({
            ["orderInfo.pkey"]: result.pkey,
            ["orderInfo.pkey1"]: result.pkey1,
            ["orderInfo.pkey2"]: result.pkey2
          })
          if ((result.payType == "ORDER_WEIXIN" || result.payType == "MSD_COMBINATION" ||  result.payType == "ELECTRONIC_ACCOUNT_COMBINATION") && (result.goodsSumAmtn != 0)) {
            wx.requestPayment({
              'timeStamp': result.wxPayData.timeStamp,
              'nonceStr': result.wxPayData.nonceStr,
              'package': result.wxPayData.pack,
              'signType': result.wxPayData.signType,
              'paySign': result.wxPayData.paySign,
              'success': function (res) {
                var pkey = "";
                if (result.pkey != null) {
                  pkey = result.pkey;
                } else if (result.pkey1 != null) {
                  pkey = result.pkey1;
                } else {
                  pkey = result.pkey2;
                }
                that.subscribeToMessages();
                wx.redirectTo({
                  url: '/pages/pay/result/index?pkey=' + pkey + '&result=success&type=' + that.data.orderInfo.orderType,
                })
              },
              'fail': function (res) {
                var pkey = "";
                if (result.pkey != null) {
                  pkey = result.pkey;
                } else if (result.pkey1 != null) {
                  pkey = result.pkey1;
                } else {
                  pkey = result.pkey2;
                }
                that.subscribeToMessages();
                wx.redirectTo({
                  url: '/pages/pay/result/index?pkey=' + pkey + '&result=fail&type=' + that.data.orderInfo.orderType,
                })
              },
              'complete': function (res) {
                app.getBuycarNum()
              }
            })
          } else {
            var pkey = "";
            if (result.pkey != null) {
              pkey = result.pkey;
            } else if (result.pkey1 != null) {
              pkey = result.pkey1;
            } else {
              pkey = result.pkey2;
            }

            app.getBuycarNum()
            that.subscribeToMessages();
            wx.redirectTo({
              url: '/pages/pay/result/index?pkey=' + pkey + '&result=success&type=' + that.data.orderInfo.orderType,
            });
          }
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });

        }
        that.setData({
          loading: false,
          disabled: false
        })
      }
    })
  },
  subscribeToMessages(){
    wx.requestSubscribeMessage({  
      tmplIds: ['pVIkpgKki0HOGcKWyvmvk8aSOceWpv4Jj0BsTsy-1Eo'],  
      success:(res)=> {  
      //成功回调  
        console.log(1112)  
      },
      fail:(res)=> {
        console.log("2222 res", res)  
      }  
    });     
    
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
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

})