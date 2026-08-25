// pages/my/bargainDeatil/index.js
let app = getApp()
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    titleBarHeight: 44,
    time: 360000,
    timeData: {},
    show: false,
    cutPkey: 577,
    cutData: {},
    cutLabel: '',
    cutAmt: 0,
    isAuto: true,
    iShidden: true,
    userInfo: app.globalData.userInfo,
    orderPkey: ''
  },
  /**返回首页 */
  backhome() {
    wx.switchTab({
      url: '/pages/home/shouye/index',
    })
  },
  /**
   * 获取头部导航高度
   */
  getSystemInfo() {
    let _that = this
    wx.getSystemInfo({
      success: function (res) {
        let header = wx.getMenuButtonBoundingClientRect()
        _that.setData({
          titleBarHeight: (header.bottom + header.top) - (res.statusBarHeight * 2)
        })
      },
      fail() {
        _that.data.statusBarHeight = 20
        _that.data.titleBarHeight = 44
      }
    })
  },
  /**
   * 我也想砍价
   */
  handelGetUserInfo(e) {
    if (!app.globalData.userInfo) {
      wx.setStorage({
        data: e.detail.userInfo,
        key: 'userinfo',
      });
      app.globalData.userInfo = e.detail.userInfo;
      this.setData({
        userInfo: e.detail.userInfo
      })
    }
    let that = this,
      params = {
        goods: this.data.cutData.space,
        num: 1
      };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/initiate/cut',
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            orderPkey: res.data.result.pkey,
            show: !0,
            cutLabel: '我也要发起砍价'
          });
        } else
          wx.showToast({
            title: res.data.codeMsg || '',
            icon: 'none',
            duration: 2000
          })
      },
    })

  },
  //倒计时
  onChange(e) {
    this.setData({
      timeData: e.detail,
    });
  },
  /**砍价 */
  handleCut() {
    let that = this
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/cut',
      data: {
        orderPkey: this.data.cutPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        // that.setData({
        //   cutData: that.dataformat(res.data.result)
        // })
        // console.log(res)
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success)
          that.setData({
            cutAmt: res.data.result
          })
        else if (res.data.code == "zy-0044" || res.data.code == "zy-0045")
          that.setData({
            cutLabel: res.data.codeMsg
          })
        that.getData()
        that.setData({
          show: !0
        })
      },
    })


  },
  /**关闭砍价 */
  closePopup() {
    this.setData({
      show: !1
    })
  },
  /**获取砍价信息 */
  getData() {
    let that = this
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/loadCutOrder',
      data: {
        pkey: this.data.cutPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        console.log(res)
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        console.log(res.data.result)
        that.setData({
          cutData: that.dataformat(res.data.result)
        })
      },
    })
  },
  /**数据格式化 */
  dataformat(data) {

    let nowDate = new Date().getTime();
    data.time = (data.endTime - nowDate > 0 ? data.endTime - nowDate : 0)

    let price = data.price.toFixed(2).toString().split('.');
    data.bigPrice = price[0];
    data.smallPrice = price[1];

    return data
  },
  onLoadFun: function (e) {
    this.getData();
    this.setData({
      userInfo: app.globalData.userInfo
    })
  },
  handlePopClose() {
    this.setData({
      show: !1
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(options.pKey)
    if (options.hasOwnProperty('pKey')) {
      this.setData({
        cutPkey: parseInt(options.pKey)
      })
    }

    this.getData()
    this.getSystemInfo()
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
    console.log(e)
    let that = this;

    if (e.from == "button") {
      return {
        title: `${app.globalData.userInfo.nickName}正在砍价，快来祝他一臂之力！`,
        path: `/pages/my/bargainDetail/index?pKey=${that.data.orderPkey}`,
        imageUrl: that.data.cutData.photo, //用户分享出去的自定义图片大小为5:4,
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