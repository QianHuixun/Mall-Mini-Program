// pages/activity/whiteOff/index.js
import utils from '../../../utils/util.js';
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      title: '',
      isGet: false,
      show: false,
      isAuto: true,
      iShidden: true,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const title = utils.getQueryString(q, 'title');
          this.setData({
            title:title
          });     
      }
      this.handleCheck();
    },
    handleCheck(){
      var that = this,
      url = "/v2/app/market/lm/order/activity/writeOff/check";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          name: this.data.title
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
          that.setData({
            isGet: res.data.result
          })
        
        }
      });
    },
    handleWriteOff(){
      var that = this,
      url = "/v2/app/market/lm/order/activity/writeOff";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          name: this.data.title
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
          that.setData({
            show: true
          });
        
        }
      });
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

    }
})