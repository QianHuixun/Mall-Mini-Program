// pages/my/cardWhiteOff/index.js
import utils from '../../../utils/util.js';
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
      isAuto: true,
      iShidden: true,
      pkey: '070824762114', //核销码 290424212281  290424196883 290424793999  
      isError: false, // 获取卡券信息是否报错
      type: "CARD",//类型 GIFT 礼品券 CARD 优惠券
      errorMsg: "",
      isSuccess: false,// 是否核销成功
      cardData: {}
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const pkey = utils.getQueryString(q, 'pkey');
        const type = utils.getQueryString(q, 'type');
        this.setData({
          pkey: pkey,
          type: type
        });
      }
      this.getData();
    },
    getData() {
      const that = this;
      let url= "/v2/app/market/lm/gift/writeOff/load";
      if(this.data.type == 'CARD') {
        url = "/v2/app/market/lm/coupon/writeOff/load";
      }
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          cardNumber: this.data.pkey
        },
        header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
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

            console.log( res.data.result)
            if(!res.data.success) {
              this.setData({
                isError: true,
                errorMsg: res.data.msg
              })
              return;
            }
            const cardData =  res.data.result;
            if(this.data.type == 'CARD') {
              cardData.rules = [];
              cardData.rules.push(`仅限${cardData.userFarmerName ? '【'+cardData.userFarmerName +'】' : ''}线上支付使用`);
              if(cardData.userVendorName) {
                cardData.rules.push(`适用于【${cardData.userVendorName}】商户下的商品`);
              }
              if(cardData.userTypeName) {
                cardData.rules.push(`适用于【${cardData.userTypeName}】分类下的商品`);
              }
              if(cardData.userGoodsName) {
                cardData.rules.push(`仅限【${cardData.userGoodsName}】商品使用`);
              }
              if(cardData.userOrderType == 'DELIVERY') {
                cardData.rules.push(`仅限配送订单使用`);
              }
              if(cardData.userOrderType == 'PICKUP') {
                cardData.rules.push(`仅限自提订单使用`);
              }
              if(cardData.type=='POSTAGE_COUPON') {
                cardData.rules.push(`仅减免配送费金额`);
              }
            }
            that.setData({
              cardData: cardData
            })
        }
    });
    },
    handleWhiteOff(){
      let url = "/v2/app/market/lm/gift/writeOff",that = this;
      
      if(this.data.type == 'CARD') {
        url = '/v2/app/market/lm/coupon/writeOff';
      }
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          cardNumber: this.data.pkey
        },
        header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
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

            console.log( res.data.result)
            if(!res.data.success) {
             wx.showToast({
               title: res.data.msg,
               icon: "none",
               isSuccess: false
             })
              return;
            } else {
              that.setData({
                isSuccess: true
              });
            }
          
        }
    });
    },
    ruleChange(event) {
      let  cardData = this.data.cardData;
      cardData.rule = !cardData.rule;
      this.setData({
        cardData
      });
  },
  handleBack(){
    wx.switchTab({
      url: '/pages/home/shouye/index',
    })
  },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    },
    //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
    //函数里面执行
    onLoadFun: function () {
     this.getData();
  }
})