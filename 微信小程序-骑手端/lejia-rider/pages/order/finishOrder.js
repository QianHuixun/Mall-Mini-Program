// pages/order/finishOrder.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    page: 0,
    pagesize: 6,
    dataList: [],
    total: 0,
    requesting: false,//数据是否在加载
    end:false,//列表数据加载完成
  },

  /**获取数据 */
  getData() {
    var _this = this;
    var parameter = {
      page: this.data.page,
      pagesize: this.data.pagesize,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/express/query/fulfill',
      data: parameter,
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
            dataList: res.data.result.first ? res.data.result.content : _this.data.dataList.concat(res.data.result.content),
            total: res.data.result.total,
            requesting: false,
            end: res.data.result.last ? true : false
          });
        }

      },
    })
  },

  /**刷新 */
  refresh() {
    this.setData({
      page: 0,
      requesting: true
    });
    this.getData();
  },

  /**加载 */
  more() {
    this.setData({
      page: ++this.data.page,
      requesting: true
    });
    this.getData();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

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
    console.log("下拉");

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    console.log("上拉");
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})