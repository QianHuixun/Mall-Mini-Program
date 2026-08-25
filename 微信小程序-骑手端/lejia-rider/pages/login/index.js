// pages/login/index.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    phone:"",
    sms:"",
    getyzmText:"发送验证码",
    getyzmDisabled: false,
    AppInfo: {},
  },


  /**输入用户名 */
  onPhoneChange(event){
    console.log(event);
    this.setData({
      phone:event.detail
    });
  },

  /**输入验证码 */
  onSmsChange(event){
    this.setData({
      sms:event.detail
    });
  },


  /**发送验证码 */
  sendCode: function (e) {
    this.setData({
      sms: ''
    })
    var _this = this,
      step = 60; //发送验证码倒计时步长
    if (_this.data.phone == '') {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'none',
        duration: 2000
      })
      return;
    } else if (!(/^1\d{10}$/.test(_this.data.phone))) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: 'none',
        duration: 2000
      })
      return;
    }
    _this.setData({
      getyzmText: '发送中...'
    });
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courierlogin/captcha',
      data: {
        phone: _this.data.phone,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      success: function (res) {
        if (res.data.success) {
          var t = setInterval(function () {
            if (step == 0) {
              clearInterval(t);
              _this.setData({
                getyzmText: '发送验证码',
                getyzmDisabled: false
              });

              return;
            } else {
              step--
              _this.setData({
                getyzmText: step + "s",
                getyzmDisabled: true
              });;
            }
          }, 1000);
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          })
          _this.setData({
            getyzmText: '发送验证码',
            getyzmDisabled: false
          })
          return;
        }
      },
    })
  },

  /**登录页面 */
  loginClick(){
    var _this = this;
    if (_this.data.phone == '') {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'none',
        duration: 2000
      })
      return;
    } else if (!(/^1\d{10}$/.test(_this.data.phone))) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: 'none',
        duration: 2000
      })
      return;
    }
    if(_this.data.sms == ''){
      wx.showToast({
        title: '请输入验证码',
        icon: 'none',
        duration: 2000
      })
      return;
    }
     /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['WUQXLiAOg-pKLv2_R2y5np3Ql4HEEEdZGmzf4nOev0c'],
      success(res) {
      },
      complete(res) {
        
      },
      fail(res){
        console.log(res)
      }
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courierlogin/login',
      data: {
        phone: _this.data.phone,
        captcha: _this.data.sms
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.success) {
          console.log("进入首页");
          // _this.judgeHasgz();
          wx.reLaunch({
            url: '../order/index',
          });
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          });
        }
      },
    })
  },
  judgeHasgz() {
    let params = {
        phone: this.data.phone
      },
      _this = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/courierlogin/checkwx",
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if(res.data.success){
          if (res.data.result)
          wx.reLaunch({
            url: '../order/index',
          });
        else
          wx.navigateTo({
            url: `/pages/gzh/gzh?phone=${_this.data.phone}&type=USER`,
          });
        }
      },
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.setNavigationBarTitle({
      title: app.globalData.AppInfo.courierName,
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    this.setData({
      AppInfo: app.globalData.AppInfo
    })
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

  }
})