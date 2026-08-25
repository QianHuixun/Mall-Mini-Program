// pages/activity/couponOne/index.js
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
      show: false,
      isAuto: true,
      iShidden: true,
      imgUrl: app.globalData.file_url,
    },
    showNotice() {
      this.setData({
        show: true
      });
    },
    handleClose() {
      this.setData({
        show: false
      })
    },
    goPay() {
      var that = this,
      url = "/v2/app/market/lm/order/pay/coupon/one";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {},
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey,
          "ascription": app.globalData.ascription
        },
        success: res => {
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };
          var result = res.data.result;
          wx.requestPayment({
            'timeStamp': result.timeStamp,
            'nonceStr': result.nonceStr,
            'package': result.pack,
            'signType': result.signType,
            'paySign': result.paySign,
            'success': function (res) {
            //  console.log("111",res)

              wx.redirectTo({
                url: '/pages/activity/result/index',
              })
            },
            'fail': function (res) {
              wx.showToast({
                title: '支付失败',
                icon: 'none'
              })
              setTimeout(()=>{
                wx.redirectTo({
                  url: '/pages/activity/couponOne/index',
                })
              }, 3000);
            },
            'complete': function (res) {
             
            }
          })
        }
      });
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

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
  }
})