// pages/my/problem/index.js
import http from '../../../utils/http'
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getData()
  },

  /**
   * 获取问题列表
   */
  getData() {
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v3/app/problem/list',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        console.log(res);
        this.setData({
          list: res.data.result
        })
      }
    })
  },
  goProblemDetail(e) {
    const item = JSON.stringify(e.currentTarget.dataset.item)
    console.log(item);
    wx.navigateTo({
      url: `/pages/my/problem/problemDetail/index?item=${encodeURIComponent(item)}`,
    })
  }
})