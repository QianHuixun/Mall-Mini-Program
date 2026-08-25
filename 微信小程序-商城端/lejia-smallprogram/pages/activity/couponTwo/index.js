// pages/activity/couponTwo/index.js
import utils from '../../../utils/util.js';
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
      vendor: '',
      isAuto: true,
      iShidden: true,
      imgUrl: app.globalData.file_url,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const vendor = utils.getQueryString(q, 'vendor');
        this.setData({
          vendor:vendor
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
      url = "/v2/app/market/lm/order/receive/coupon/one";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          vendor: this.data.vendor
          // vendor: 248
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
          
          if(res.data.success){
            var result = res.data.result;
            wx.redirectTo({
              url: '/pages/activity/result/index?vendor='+ that.data.vendor+'&endDate='+ result.endDate + '&limitCost='+result.limitCost+'&cost='+result.cost+'&title='+ result.title,
              // url: '/pages/activity/result/index'
            })
          } else {
            wx.showToast({
              title: res.data.msg || '领取失败',
              icon: 'none'
            })
          }
        
        }
      });
    },
       //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
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