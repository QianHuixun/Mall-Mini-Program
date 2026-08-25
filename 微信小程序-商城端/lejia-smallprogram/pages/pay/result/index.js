// pages/pay/result/index.js
const app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imageUrl: app.globalData.file_url,
    result: "success",
    pkey: "",
    type: '',
    orderNum: null,
    display: false,
    number: 0,
    goodsInfo: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      pkey: options.pkey,
      result: options.result,
      type: options.type,
      orderNum: options.orderNum,
    });
    if (options.type == "COLLAGE_ORDER" && options.result == "success") {
      this.setData({
        display: true
      });
      this.getData();
    }
  },
  /**
   * 如果是团购订单，获取信息
   */
  getData: function () {
    var that = this,
      url = "/v1/app/market/lm/order/collage";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        orderPkey: this.data.pkey
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {
          that.setData({
            goodsInfo: res.data.result
          });
          console.log(that.data.goodsInfo)

        }

      }
    });
  },
  /**
   * 跳转到 订单详情页
   */
  goDetail: function () {
    if (this.data.orderNum && this.data.orderNum > 1 || this.data.type == 'INTEGRAL_JD_ORDER') {
      wx.navigateTo({
        url: '/pages/my/order/index',
      });
    } else {
      wx.navigateTo({
        url: '/pages/my/orderDetail/index?pkey=' + this.data.pkey,
      });
    }

  },

  goIndex: function () {
    wx.switchTab({
      url: '/pages/home/shouye/index',
    })

  },
  /**
   * 关闭弹出窗
   */
  closePopon: function () {
    this.setData({
      display: false
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
  onShareAppMessage: function () {
    return {
      title: app.globalData.userinfo ? `${app.globalData.userinfo.nickName}正在拼团，一起拼团超值好货！` : this.data.goodsInfo.title,
      path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + this.data.goodsInfo.pkey,
      imageUrl: this.data.goodsInfo.wrapperPhoto, //用户分享出去的自定义图片大小为5:4,
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
})