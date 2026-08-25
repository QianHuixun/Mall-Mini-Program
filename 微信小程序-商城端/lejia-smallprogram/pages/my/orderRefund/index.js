// pages/my/orderRefund/index.js
let app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    orderInfo: {},
    inputModel: {
      orderNum: "",
      reason: ""
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      orderInfo: JSON.parse(wx.getStorageSync('refundInfo'))
    });
    this.setData({
      ["inputModel.orderNum"]: this.data.orderInfo.pkey
    });
  },
  /**
   * 退款原因 修改
   */
  onChange(event) {
    // event.detail 为当前输入的值
    // console.log(event.detail);
    this.setData({
      ["inputModel.reason"]: event.detail
    })
  },
  /**
   * 申请退款
   */
  handleSubmit: function () {
    if (this.data.inputModel.reason == "") {
      wx.showToast({
        title: '请输入退款原因',
        icon: "none"
      });
      return;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/rufund',
      data: this.data.inputModel,
      header: {
        "Content-Type": "application/json;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          wx.showToast({
            title: '退款申请成功'
          })
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateBack({
            delta: 1,
            success: function (e) {
              var page = getCurrentPages().pop();
              if (page == undefined || page == null) return;
              page.onLoad();
            }
          });//返回上一个页面更新数据
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
        }
      }
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

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
  }
})