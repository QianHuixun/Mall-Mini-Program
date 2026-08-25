// pages/introduce/introduce.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    AppInfo: {}
  },

  /**去登录 */
  logClick() {
     /**请求订阅号消息 */
    this.checkLogin();
  },
  /**
   * 验证登录
   */
  checkLogin: function () {
    console.log('接口执行了')
    var that = this,
    url = "/v2/app/vendorLogin/checkLogin";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
       console.log('接口返回了',res)
       if(res.data.result) {
        console.log('首页',res.data.result)
        if(res.data.result.includes('VENDOR')) {
          wx.navigateTo({
            url: '/pages/my/index',
            success(res){
             console.log('首页跳转成功',res)
            },
          });
        } else {
          wx.navigateTo({
            url: '/pages/vendor/index',
            success(res){
             console.log('首页跳转成功',res)
            },
          });
        }
         
       }else {
        console.log('登录页',res.data.result)
        wx.redirectTo({
          url: '/pages/login/index',
          success(res){
           console.log('登录跳转成功',res)
          },
        });
       }
      }
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
    console.log(app.globalData.AppInfo);
    this.setData({
      AppInfo: app.globalData.AppInfo
    })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if (app.globalData.isLogin) {
      console.log("是可以登录的状态");
    }
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