// pages/activity/couponThree/index.js
import utils from '../../../utils/util.js';
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
      ff: '1',
      isAuto: true,
      iShidden: true,
      imgUrl: app.globalData.file_url,
      show: false
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const ff = utils.getQueryString(q, 'ff');
          this.setData({
            ff:ff
          });     
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

    },
    goReceive() {
      var that = this,
      url = "/v2/app/market/lm/order/pay/coupon/one";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          card: this.data.ff
        },
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

          if(!res.data.success) {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            })
            return;
          }

          if(that.data.ff == 4) {
            that.setData({
              show: true
            });
            return;
          }

          var result = res.data.result;
          wx.requestPayment({
            'timeStamp': result.timeStamp,
            'nonceStr': result.nonceStr,
            'package': result.pack,
            'signType': result.signType,
            'paySign': result.paySign,
            'success': function (res) {
              that.setData({
                show: true
              })
            },
            'fail': function (res) {
              wx.showToast({
                title: '支付失败',
                icon: 'none'
              })
            },
            'complete': function (res) {
             
            }
          })
          
        
        }
      });
    },
       //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
  },
  handeBack(){
    wx.switchTab({
      url: '/pages/home/shouye/index'
    });
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