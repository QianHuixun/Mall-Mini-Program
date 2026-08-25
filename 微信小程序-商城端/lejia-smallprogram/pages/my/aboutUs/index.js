// pages/my/aboutUs/index.js
const app = getApp()
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userStatus: app.globalData.userStatus || wx.getStorageSync('userStatus'),
  },

  /**
   * 用户注销
   */
  handleLoginOut() {
    const userStatus = this.data.userStatus
    const url = userStatus == 'NORMAL' ? '/v1/app/market/lm/member/logOut' : '/v1/app/market/lm/member/cancel/logOut';
    wx.showModal({
      title: `确认${userStatus == 'NORMAL' ? '注销用户' : '取消注销'}？`,
      content: '申请注销成功后账号将于7日后注销，期间用户可取消注销！',
      cancelText:'取消',
      confirmText:'确认',
      success: res => {
        console.log('scuess');
        if(!res.confirm) {
          wx.showToast({
            icon: 'error',
            title: '已取消申请'
          })
          return
        }
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + url,
          data: {},
          header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
            "openid": app.globalData.openid,
            "farmer": app.globalData.location.pkey
          },
          success: res => {
            console.log(res);
            const title = userStatus == 'NORMAL' ? '申请成功' : '已取消注销'
            wx.showToast({
              title,
            })
            this.getCentre()
          }
        })
      }
    })
    return
    
  },
  getCentre() {
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/centre',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        wx.setStorageSync('userStatus', res.data.result.status)
        app.globalData.userStatus = res.data.result.status
        this.setData({
          userStatus: res.data.result.status
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      AppInfo: app.globalData.AppInfo,

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

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

})