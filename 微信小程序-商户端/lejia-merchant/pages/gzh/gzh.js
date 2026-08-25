// pages/gzh/gzh.js
let app =getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
     url:'https://small.xinanshizu.com/gzh/vendor'
    //  url:'http://192.168.128.91/zy/gzh/vendor'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      url:`https://small.xinanshizu.com/gzh/vendor?miniopenid=${app.globalData.openid}&ascription=${app.globalData.ascription}`
      // url:`http://192.168.128.91/zy/gzh/vendor?miniopenid=${app.globalData.openid}&ascription=${app.globalData.ascription}`
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