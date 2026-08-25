// pages/my/invite/index.js
var app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    tjrList: [], //推荐人列表
    invitationPhoto: '' //邀请有礼图片
  },
  /**获取推荐人列表 */
  getData() {
    let that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/tjrList',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          console.log(res)
          that.setData({
            tjrList: res.data.result
          });
        }

      }
    });

  },
  /**获取邀请有礼图片 */
  getinviteImg() {
    let  that =this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/invPhoto',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          that.setData({
            invitationPhoto: res.data.result
          });
        }

      }
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getData();
    this.getinviteImg();
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
  onShareAppMessage: function (e) {
    if (e.from == "button") {
      return {
        title: `邀请有礼`,
        path: `/pages/home/my/index?tjrOpenid=${app.globalData.openid}`,
        imageUrl: this.data.invitationPhoto,
        success: function (res) {
          // 转发成功
          wx.showToast({
            title: "分享成功",
            icon: 'success',
            duration: 2000
          })
        },
        fail: function (res) {
          // 分享失败
        },
      }
    }
  }
})