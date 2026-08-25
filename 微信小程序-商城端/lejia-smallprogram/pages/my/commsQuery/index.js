// pages/my/CommsQuery/index.js
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        isIphoneX: app.globalData.isIphoneX,
        amtList: [50, 100, 200, 500],
        selectAmt: 50,
        purse:"",
        mobile:"",
        show: false,
        loading: false,
        cardNumber: "",
        cardPassword: "",
        customStyle: "width: 425rpx; background: #ebebeb; padding: 12rpx 24rpx; border-radius: 12rpx;",
    },

    // 点击金额
    amtClick(data) {
        this.setData({
            selectAmt: data.currentTarget.dataset.value
        });
    },
    
    //立即充值
    goPay(){
        var that = this,
      url = "/v1/app/market/lm/member/beforePay",
      params = {
        amt: this.data.selectAmt,
        memberPType: "RECHARGE",
        payType: "ORDER_WEIXIN"
      };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        console.log(res)
        wx.requestPayment({
          'timeStamp': res.data.result.timeStamp,
          'nonceStr': res.data.result.nonceStr,
          'package': res.data.result.pack,
          'signType': res.data.result.signType,
          'paySign': res.data.result.paySign,
          'success': function (res) {
            that.goComm();
          },
          'fail': function (res) {
            wx.showToast({
                title: '支付失败',
                icon: 'none'
              })
          },
          'complete': function (res) {}
        })
      }
    })
    },

    // 获取余额
    goComm() {
        var that = this,
        url = "/v1/app/market/lm/member/comm/get";
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + url,
          data: {},
          header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
            "openid": app.globalData.openid,
          },
          success: res => {
            if(!res.data.success) {
              wx.showToast({
                title: res.data.msg || '',
                icon: 'none'
              })
              return;
            }
            this.setData({
                purse: res.data.result
            });
          }
        });
      },

    // 显示卡密充值弹窗
    showPopup() {
        this.setData({ show: true });
    },

    onClose() {
        this.setData({ 
            show: false,
            loading: false,
            cardNumber: '',
            cardPassword: '',
        });
    },

    onChange(e) {
        const value= e.detail
        const name = e.currentTarget.dataset.name
        this.setData({
            [name]: value
        })
    },

    /**
     * 卡密充值
     */
    handleRechargeCard(e) {
        console.log(e);
        let { cardNumber, cardPassword } = this.data
        if(!cardNumber) {
            wx.showToast({
              title: '请输入卡号',
              icon: 'error'
            })
            return
        }
        if(!cardPassword) {
            wx.showToast({
              title: '请输入卡密',
              icon: 'error'
            })
            return
        }
        this.setData({
            loading: true
        })
        let url = "/v1/app/market/lm/member/comm/recharge/card";
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + url,
          data: {
            cardNumber,
            cardPassword
          },
          header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
            "openid": app.globalData.openid,
          },
          success: res => {
            if(!res.data.success) {
              wx.showToast({
                title: res.data.msg || '',
                icon: 'none'
              })
              this.setData({
                  loading: false
              })
              return;
            }
            wx.showToast({
              title: '充值成功',
              icon: 'success'
            })
            this.onClose()
            this.goComm()
          }
        });
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        console.log(options)
        if(options.mobile){
            this.setData({
                mobile:options.mobile
            });
        }
        if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
            wx.setNavigationBarTitle({
              title: 'I DO充值',
            })
        }
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
        this.goComm();
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

    }
})