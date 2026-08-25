// pages/cardScan/index.js
import http from '../../utils/http'
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    cardNumber: "",
    isSuccess: true,
    card: {
      name: '',
      space:'',
    },
    confirmLoading:false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (options.hasOwnProperty('cardNumber')) {
      this.setData({
        cardNumber: options.cardNumber
      })
    }
    this.getcardInfo()
  },
  /**获取卡券信息 */
  getcardInfo() {
    let that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/vendor/gift/loadName',
      data: {
        cardNumber: this.data.cardNumber
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          console.log(res)
          that.setData({
            isSuccess: true,
            card: res.data.result
          })
        } else {
          that.setData({
            isSuccess: false
          })
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2500,
          })
        }

      }
    });
  },
  /**确定核销 */
  handleConfirm() {
    let that =this;
    this.setData({
      confirmLoading:true
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/vendor/gift/writeOff',
      data: {
        cardNumber: this.data.cardNumber
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          wx.showToast({
            title: '核销成功',
            icon: 'none',
          })
          setTimeout(() => {
            wx.navigateBack({
              delta: 1,
            })
            that.setData({
              confirmLoading:false
            })
          }, 1500)

        } else {
          that.setData({
            confirmLoading:false
          })
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2500,
          })
        }
       

      }
    });
  },
  /**取消核销 */
  handleCancel() {
    wx.navigateBack({
      delta: 1,
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