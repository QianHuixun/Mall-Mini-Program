// pages/wallet/details/index.js
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      list: [],
      page: 0,
      pagesize: 10,
      last: false,
    },
    onScroll(){},
    scrollBottom(){
      if(this.data.last) return;
      this.getData();
    },
    getData(){
      var that = this,
        url = "/v3/app/vendor/wallet/line/query";
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + url,
          data: {
            page: this.data.page,
            pagesize: this.data.pagesize
          },
          header: {
            "Content-Type": 'application/x-www-form-urlencoded',
            "openid": app.globalData.openid
          },
          success: function (res) {
            if (res.data.success) {
              that.setData({
                list: that.data.list.concat(res.data.result.content),
                page: ++that.data.page,
                last: res.data.result.last,
              })
              
            } else {
              wx.showToast({
                title: res.data.msg,
                icon: "none"
              })
            }
          }
        })
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
      this.getData();
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