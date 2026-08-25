// pages/introduce/introduce.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    AppInfo: {},
  },

  /**去登录 */
  logClick() {
    wx.redirectTo({
      url: '../login/index',
    });
  },

  /**获取openid */
  auto(){
    wx.login({
      success (res) {
        if (res.code) {
          //发起网络请求
          var params = {
            sign:"COURIER",
            wxcode: res.code,
            ascription: app.globalData.ascription
          };
          wx.request({
            method: "GET",
            url: app.globalData.ajax_url + "/v1/wx/getOpenidByCode",
            data: params,
            header: {
              'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            success: function (res) {
              console.log(res);
              wx.setStorageSync('openid', res.data.result.openid);
              app.globalData.openid = wx.getStorageSync('openid');
            },
          })
        } else {
          console.log('获取openid失败')
        }
      }
    })
  },

  /**判断是否登录 */
  isLogin(){
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courierlogin/checkLogin',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.success && res.data.result) {
          console.log("进入首页");
          wx.reLaunch({
            url: '../order/index',
          });
        }
      },
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.setNavigationBarTitle({
      title: app.globalData.AppInfo.courierName,
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    this.setData({
      AppInfo: app.globalData.AppInfo
    })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if(app.globalData.openid){
      console.log("去验证登录");
      this.isLogin();
    }else{
      this.auto();
    }
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

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})