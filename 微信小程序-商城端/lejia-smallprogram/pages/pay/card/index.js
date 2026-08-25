// pages/pay/card/index.js
let app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: true,
    isAuto: true,
    iShidden: true,
    orderInfo: {},
    datalist: [],
    shareFarm: "", //分享的市场
    selectCard: '',
    scrollHeight: wx.getSystemInfoSync().screenHeight,
    paytype: null,  // 订单支付类型，combopay: 包含积分商城的组合支付; null: 普通市场支付
    cardNum: 0,
    cardNotNum: 0,
    available: [],
    notAvailable: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (options.hasOwnProperty('shareFarm')) {
      this.setData({
        shareFarm: JSON.parse(options.shareFarm)
      })
    }
    console.log(options);
    if(options.hasOwnProperty('paytype')) {
      this.setData({
        paytype: options.paytype,
        index: options.index
      })
    }
    
   
    this.setData({
      orderInfo: JSON.parse(wx.getStorageSync('orderInfo'))
    })
    this.getData()
  },

  onChange(event) {
    console.log(event, this.data.active);
    this.setData({
        datalist: event.detail.name ?  this.data.available : this.data.notAvailable,
        active: event.detail.name
    })
  },

   /**
   * @desc 卡券选用
   */
  handleRadio(event) {
    if(!this.data.active) return
    console.log(event)
    const pkey = event.currentTarget.dataset.pkey
    const selectCard = this.data.selectCard == pkey ? null : pkey
    this.setData({
      selectCard
    })
  },

  /**
   * @desc 规则折叠
   */
  ruleChange(event) {
    let index = event.currentTarget.dataset.index,
      datalist = this.data.datalist;
    datalist[index].rule = !datalist[index].rule

    this.setData({
      datalist
    });
  },
  /**
   * @desc 去领券中心
   */
  handleGoCenter() {
    wx.navigateTo({
      url: '/pages/my/coupon/coupon',
    })
  },
  /**
   * 获取数据
   */
  getData: function () {
    var that = this;
    let url = app.globalData.ajax_url + "/v2/app/market/lm/member/listCard"
    let data = JSON.parse(wx.getStorageSync('orderInfo'))
    console.log(data);
    if(this.data.paytype) {
      let goodsList = []
      data.goodsList.forEach(item => {
        item.spaceList.forEach(space => {
          goodsList.push({
            ...item,
            ...space
          })
        })
      })
      console.log(goodsList);
      data.goodsList = goodsList
      url = app.globalData.ajax_url + "/v3/app/market/lm/order/listCard"
    }
    http.request({
      method: "POST",
      url,
      data,
      header: {
        "Content-Type": "application/json;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        let { cardNum, cardNotNum, available, notAvailable } = res.data.result
        available = this.setRules(available)
        notAvailable = this.setRules(notAvailable)
        this.setData({
            cardNum, cardNotNum, available, notAvailable
        })
        that.setData({
          datalist: available
        })
      }
    });
  },

  setRules(list) {
    return list.map(item => {
        if (item.pkey == this.data.orderInfo.card) {
            this.setData({
              selectCard: item.pkey
            })
          }
          item.endDate = item.endDate.substr(0, 10);
          item.rules = [];
          item.rules.push(`仅限${item.userFarmerName ? '【'+item.userFarmerName +'】' : ''}线上支付使用`);
          if(item.userVendorName) {
            item.rules.push(`适用于【${item.userVendorName}】商户下的商品`);
          }
          if(item.mtypeName) {
            item.rules.push(`仅限【${item.mtypeName}】的商品使用`);
          }
          if(item.userTypeName) {
            item.rules.push(`适用于【${item.userTypeName}】分类下的商品`);
          }
          if(item.userGoodsName) {
            item.rules.push(`仅限【${item.userGoodsName}】商品使用`);
          }
          if(item.userOrderType == 'DELIVERY') {
            item.rules.push(`仅限配送订单使用`);
          }
          if(item.userOrderType == 'PICKUP') {
            item.rules.push(`仅限自提订单使用`);
          }
          if(item.type=='POSTAGE_COUPON') {
            item.rules.push(`仅减免配送费金额`);
          }
          console.log(item);
          return item;
    })
  },

  handleSubmit() {
    const { selectCard, orderInfo } = this.data
    const card = this.data.available.find(item => item.pkey == selectCard)
    console.log( orderInfo, card, selectCard);
    if(orderInfo.card == selectCard) {
      // 用户没有改变优惠券，什么都不操作
    } else if(!selectCard) {
      // 用户取消了优惠券选择
      this.deselectCard()
    } else {
      // 用户切换优惠券
      this.changeCard(card)
    }
    wx.navigateBack()
  },

    /**
   * 取消优惠券选择
   */
  deselectCard() {
    let {paytype, index} = this.data
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2]; //上一个页面
    var orderInfo = this.data.orderInfo;
    if(paytype) {
      let order = prevPage.data.orderInfo
      order.cardAmt = order.cardAmt - orderInfo.cardAmt
      order.goodsSumAmtn = order.goodsSumAmtn + (orderInfo.sales > orderInfo.cardAmt ? orderInfo.cardAmt : orderInfo.sales)
      orderInfo.card = null
      orderInfo.cardAmt = 0
      order.infos[index] = orderInfo
      console.log(order);
      const orderAmt = order.goodsSumAmtn;
      const payTypeAmt = prevPage.data.payTypeAmt;
      const payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
      console.log(11,order.payType, payTypeAmt)
      prevPage.setData({
        ["orderInfo"]: order,
        payAmt,
      });
      prevPage.initPayType();
    } else {
      prevPage.setData({
        ["orderInfo.card"]: '',
        ["orderInfo.cardAmt"]: 0,
      });
     
      const amtn = orderInfo.amtall- orderInfo.cardPostageAmt - orderInfo.reducePrice;
      const pickupAmt= orderInfo.amto - orderInfo.reducePrice;
      const orderAmt = prevPage.data.shoptype == 'zt' ? pickupAmt : amtn;
      const payTypeAmt = prevPage.data.payTypeAmt;
      const payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
      console.log(22,amtn,pickupAmt)
      prevPage.setData({
        ["orderInfo.amtn"]: amtn,
        ["orderInfo.pickupAmt"]: pickupAmt,
        payAmt,
      });
      prevPage.initPayType();
    }
  },

  /**
   * 切换优惠券
   */
  changeCard(card) {
    let {paytype, orderInfo, selectCard, index} = this.data
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2]; //上一个页面
    if(paytype) {
      let order = prevPage.data.orderInfo
      let goodsSumAmtn = 0
      order.infos = order.infos.map((item, i) => {
        if(item.card == selectCard) {
          item.card = null
          item.cardAmt = 0
        }
        if(index == i) {
          item.card = card.pkey
          item.cardAmt = card.cost
          item.cardUsable = true
        }
        goodsSumAmtn = goodsSumAmtn + item.postage + (item.sales >= item.cardAmt ? (item.sales - item.cardAmt) : 0)
        return item
      })
      order.goodsSumAmtn = goodsSumAmtn;
      const orderAmt = goodsSumAmtn;
      const payTypeAmt = prevPage.data.payTypeAmt;
      const payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
      prevPage.setData({
        orderInfo: order,
        payAmt,
      })
    } else {
      prevPage.setData({
        ["orderInfo.card"]: card.pkey,
        ["orderInfo.cardAmt"]: card.cost,
      });
      const amtn = orderInfo.amtall - orderInfo.cardPostageAmt - orderInfo.reducePrice - card.cost <= 0 ? 0.01 : orderInfo.amtall - orderInfo.cardPostageAmt - orderInfo.reducePrice - card.cost;
      const pickupAmt = orderInfo.amto  - orderInfo.reducePrice - card.cost <= 0 ? 0.01 : orderInfo.amto  - orderInfo.reducePrice - card.cost
      const orderAmt = prevPage.data.shoptype == 'zt' ? pickupAmt : amtn;
      const payTypeAmt = prevPage.data.payTypeAmt;
      const payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
      prevPage.setData({
        ["orderInfo.amtn"]: amtn,
        ["orderInfo.pickupAmt"]: pickupAmt,
        payAmt,
      });
    }
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
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.getData();
  }
})