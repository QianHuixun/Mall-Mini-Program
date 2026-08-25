// pages/my/index.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    score:5,
    inputModel:{}
  },

   /**获取首页数据 */
   getData(){
     var _this = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/get',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.code == "999") {
          wx.reLaunch({
            url: '../login/index',
          });
          return;
        };
        if (res.data.success) {
          _this.setData({
            inputModel: res.data.result
          });
        }
      },
    })
  },

  /**点击完成订单 */
  itemFinishClick(){
    wx.navigateTo({
      url: '../order/finishOrder',
    });
  },
  /**
   * @Desc 跳转消息推送页面
   */
  handleMessage(){
    wx.navigateTo({
      url: '/pages/gzh/gzh',
    });
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

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.getData();
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