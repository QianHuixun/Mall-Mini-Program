// pages/point/index.js
import http from '../../utils/http'
let app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pagesize: 10,
    page: 0,
    datalist: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.loadData();
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  loadData: function () {
    var that = this,
      url = "/v1/app/vendor/queryUsePointsRecord",
      params = {
        page: this.data.page,
        pagesize: this.data.pagesize
      }

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
      
            if (that.data.page == 0) {
              that.setData({
                datalist: res.data.result.content,
                page: ++that.data.page
              });
            } else {
              that.setData({
                datalist: that.data.datalist.concat(res.data.result.content),
                page: ++that.data.page
              });
            }
            res.data.result.curPage = that.data.page;
            loadMoreView.loadMoreComplete(res.data);
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none'
          });
        }
      }
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
    loadMoreView.loadMore()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  // onShareAppMessage: function () {
  //   return {
  //     title: '菜篮商户',
  //     path: '/pages/introduce/introduce',
  //   }
  // }
})