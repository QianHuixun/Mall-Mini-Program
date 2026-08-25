// pages/my/index.js
import http from '../../utils/http'
import Dialog from '../../miniprogram_npm/@vant/weapp/dialog/dialog';
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    merchantInfo: {},
    avatar: null,
    show: false,
    settlementAmt: "",
    walletAmt: "",
  },
  // 获取 我的钱包 余额和待到账
  getMyWalletData(){
    var that = this,
    url = "/v3/app/vendor/wallet/get";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        "Content-Type": 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            walletAmt: res.data.result.walletAmt,
            settlementAmt: res.data.result.settlementAmt, 
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
   * 获取商户信息
   */
  getData: function () {
    var that = this,
      url = "/v1/app/vendor/get";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        "Content-Type": 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            merchantInfo: res.data.result,
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
   * 登出
   */
  handleLogout() {
    console.log('logout');
    this.setData({
      show: true
    })
    // wx.clearStorageSync()
    // app.globalData.isLogin = false
    // wx.reLaunch({
    //   url: '/pages/login/index',
    // });
  },
  // 确认退出
  onConfirm() {
    wx.reLaunch({
      url: '/pages/login/index',
    });
  },

  goEditInfo() {
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
      success(res) {},
      complete(res) {

      }
    })
    wx.navigateTo({
      url: '/pages/editInfo/index',
    })
  },
  goWallet(){
    wx.navigateTo({
      url: '/pages/wallet/myWallet/index',
    });
    
  },
  /**
   * 跳转到 订单查询
   */
  goOrder() {
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
      success(res) {},
      complete(res) {

      }
    })
    wx.navigateTo({
      url: '/pages/order/index',
    })
  },
  /**
   * 跳转到 积分消费查询
   */
  goPoint: function () {
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
      success(res) {},
      complete(res) {

      }
    })
    wx.navigateTo({
      url: '/pages/point/index',
    })
  },
  /**
   * 扫码核销
   */
  goCancel: function () {

    // wx.navigateTo({
    //   url: '/pages/cardCancel/index',
    // })
    wx.scanCode({
      onlyFromCamera:true,
      // scanType: ['barCode'],
      success(res) {
        if (res.errMsg == 'scanCode:ok') {
          wx.navigateTo({
            url: '/pages/cardScan/index?cardNumber='+res.result,
          })
        }
        
        
      }
    })
  },
  /**
   * 跳转到 核销记录查询
   */
  goCancelRecord: function () {
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
      success(res) {},
      complete(res) {}
    })
    wx.navigateTo({
      url: '/pages/cancelRecord/index',
    })
  },

  goWithdraw(){
    wx.navigateTo({
      url: '/pages/wallet/withdraw/index',
    });
  },

  /**
   * 
   * 跳转到商品管理
   */
  goGoodsManage(){
    wx.navigateTo({
        url: '/pages/goodsManage/index',
      });
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
    this.getMyWalletData();
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
  // },
   /**
   * @Desc 跳转消息推送页面
   */
  handleMessage(){
    wx.navigateTo({
      url: '/pages/gzh/gzh',
    });
  },

})