// pages/login/index.js
import http from '../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    phone: "",
    sms: "",
    getyzmText: "发送验证码",
    getyzmDisabled: false,
    userInfo: {},
    AppInfo: {},
    checked: false,
    role: null,
  },

  /**输入手机号码 */
  onPhoneChange(event) {
    this.setData({
      phone: event.detail
    });
  },

  /**输入验证码 */
  onSmsChange(event) {
    this.setData({
      sms: event.detail
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
    wx.requestSubscribeMessage({
      tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
      success(res) {},
      complete(res) {}
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/vendorLogin/captcha',
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
              });
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
  //授权
  setUserInfo(e) {
    if (e.detail.userInfo) {
      //用户按了允许授权按钮
      var that = this;
      // 获取到用户的信息了，打印到控制台上看下
      console.log("用户的信息如下：", e.detail);
      //授权成功后,通过改变 isHide 的值，让实现页面显示出来，把授权页面隐藏起来
      that.setData({
        isHide: false,
        isPhone: true
      });
      /**请求订阅号消息 */
      wx.requestSubscribeMessage({
        tmplIds: ['TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g'],
        success(res) {},
        complete(res) {}
      })
      that.getUserInfo();
    } else {
      //用户按了拒绝按钮
      wx.showModal({
        title: '警告',
        content: '您点击了拒绝授权，将无法进入小程序，请授权之后再进入!!!',
        showCancel: false,
        confirmText: '返回授权',
        success: function (res) {
          // 用户没有授权成功，不需要改变 isHide 的值
          if (res.confirm) {
            console.log('用户点击了“返回授权”');
          }
        }
      });
    }
  },
  /**
   * 获取用户信息
   */
  getUserInfo: function () {
    var that = this;
    wx.getUserInfo({
      success: function (res) {
        // 用户已经授权过,不需要显示授权页面,所以不需要改变 isHide 的值
        // 根据自己的需求有其他操作再补充
        // 我这里实现的是在用户授权成功后，调用微信的 wx.login 接口，从而获取code
        var userinfo = res.userInfo;
        userinfo.iv = res.iv;
        userinfo.encryptedData = res.encryptedData;
        wx.setStorageSync('userInfo', userinfo)
        // console.log("userinfo", userinfo)
        wx.login({
          success: res => {
            if (!res.code) return wx.showToast({
              title: '登录失败',
            });
            // 获取到用户的 code 之后：res.code
            console.log("用户的code:" + res.code);
            //获取在setUserInfo方法中获取的用户信息并赋值给params 变量
            var params = {};
            params["wxcode"] = res.code;
            params["sign"] = "VENDOR";
            params["ascription"] = app.globalData.ascription;
            http.request({
              method: "GET",
              url: app.globalData.ajax_url + "/v1/wx/getOpenidByCode",
              data: params,
              header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
              },
              success: res => {
                
                console.log("res.data", res.data)
                wx.setStorageSync('openid', res.data.result.openid);
                app.globalData.openid = wx.getStorageSync('openid');
                that.loginClick();
              }
            })
          }
        });
      }
    });
  },
  /**登录页面 */
  loginClick() {
    var _this = this;
    if (this.data.phone == "") {
      wx.showToast({
        title: '请输入商户手机号码',
        icon: "none"
      });
      return;
    }

    if (this.data.phone == "") {
      wx.showToast({
        title: '请输入短信验证码',
        icon: "none"
      });
      return;
    }

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/vendorLogin/roles',
      data: {
        phone: _this.data.phone,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        console.log(res);
        _this.setData({
          role: res.data.result
        })
        _this.login()
      },
    })
   
  },

  login() {
    var _this = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/vendorLogin/login',
      data: {
        phone: _this.data.phone,
        captcha: _this.data.sms,
        role: (_this.data.role && _this.data.role.length != 0) ? _this.data.role[0] : _this.data.role
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        console.log(res);
        if (res.data.success) {
          console.log("进入首页");
          let role = (_this.data.role && _this.data.role.length != 0) ? _this.data.role[0] : _this.data.role
          if(role.includes('VENDOR')) {
            wx.reLaunch({
              url: '/pages/my/index',
            });
          } else {
            wx.reLaunch({
              url: '/pages/vendor/index',
            });
          }
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
  
  openNote(e) {
    this.setData({
        checked: !this.data.checked
    })
  },
  routerTo(e) {
    console.log(e);
    const url = e.target.dataset.url
    wx.navigateTo({url})
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {},

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
  // onShareAppMessage: function () {
  //   return {
  //     title: '菜篮商户',
  //     path: '/pages/introduce/introduce',
  //   }
  // }
})