// pages/my/feedback/index.js
const app = getApp()
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    message: "",
    disabled: true,
    loading: false,
    isAuto: true,
    iShidden: true,
    wxchatAccount: {}
  },
  getData() {
    let that = this
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/index/get',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        that.setData({
          wxchatAccount: res.data.result
        })
      },
    })
  },
  //拨打电话
  handlePhone() {
    let that = this
    wx.showModal({
      title: '是否拨打以下号码',
      content: that.data.wxchatAccount.tel,
      success(res) {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: that.data.wxchatAccount.tel
          })
        } else if (res.cancel) {}
      }
    })
  },
  //保存图片
  handleSaveImage() {
    let that = this
    wx.getImageInfo({
      src: that.data.wxchatAccount.wechatCode,
      success: function (res) {
        var path = res.path;
        wx.getSetting({
          success(res) {
            console.log(res.authSetting)
            if (!res.authSetting.hasOwnProperty('scope.writePhotosAlbum'))
              wx.authorize({
                scope: 'scope.writePhotosAlbum',
                success() {
                  wx.saveImageToPhotosAlbum({
                    filePath: path,
                    success(result) {
                      wx.showToast({
                        title: '保存成功',
                        icon: 'success',
                        duration: 2000
                      })
                    },
                  })
                }
              })
            else if (!res.authSetting['scope.writePhotosAlbum']) {
              wx.showModal({
                title: '提示',
                content: '请授权',
                showCancel: !1,
                confirmText: '去设置',
                success(res) {
                  if (res.confirm) {
                    wx.openSetting({
                      success(res) {
                        if (res.authSetting['scope.writePhotosAlbum'])
                          wx.saveImageToPhotosAlbum({
                            filePath: path,
                            success(result) {
                              wx.showToast({
                                title: '保存成功',
                                icon: 'success',
                                duration: 2000
                              })
                            },
                          })
                      }
                    })
                  }
                }
              })
              wx.openSetting({
                withSubscriptions: true,
              })
            } else
              wx.saveImageToPhotosAlbum({
                filePath: path,
                success(result) {
                  wx.showToast({
                    title: '保存成功',
                    icon: 'success',
                    duration: 2000
                  })
                },
              })
          }
        })



      }
    })
  },
  /**输入发生变化 */
  changeMessage(event) {
    this.setData({
      message: event.detail
    });
    if (event.detail.value != "") {
      this.setData({
        disabled: false
      })
    }
  },

  subClick() {
    console.log(this.data.message)
    if (!this.data.message) {
      wx.showToast({
        title: "请输入建议",
        icon: 'none'
      });
      return;
    }
    this.setData({
      loading: true,
      disabled: true
    })
    var that = this;
    var parame = {
      content: this.data.message
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/advise/ins',
      data: parame,
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
          wx.showToast({
            title: "提交成功",
            icon: 'none'
          });
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/home/my/index',
            });
          }, 300);
          that.setData({
            loading: false,
            disabled: true,
            message: ""
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
          that.setData({
            loading: false,
            disabled: false
          });
        }
      },
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getData()
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
    this.setData({
      loading: false,
      disabled: false
    })
  }
})