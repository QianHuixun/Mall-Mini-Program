// pages/my/scanPay/index.js
let app = getApp();
import http from '../../../utils/http'
import utils from '../../../utils/util.js';
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    loading: false,
    disabled: true,
    pkey: "", //商户ecode
    info: {},
    points: "", //支付积分
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(options);
    const q = decodeURIComponent(options.q)
    const pkey = utils.getQueryString(q, 'pkey')
    console.log(q, pkey);
    // this.setData({
    //   pkey: options.pkey
    // });
     this.setData({
      pkey: pkey
    });
    this.getData();
  },
  getData: function () {
    var that = this,
      url = "/v1/app/market/lm/member/point/loadIndex";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ecode: this.data.pkey
      },
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
            info: res.data.result
          });
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
   * 输入框改变事件
   */
  onChange: function (event) {

    if (event.detail != "") {
      this.setData({
        disabled: false,
        points: event.detail
      });
    } else {
      this.setData({
        disabled: true,
        points: event.detail
      });
    }

    if (this.data.info.points < event.detail) {
      this.setData({
        points: this.data.info.points
      })
    }
  },
  /**
   * 提交支付
   */
  handleSubmit: function () {
    var that = this,
      url = "/v1/app/market/lm/member/point/payPoints";

    if (this.data.points == "") {
      wx.showToast({
        title: '请输入积分数量',
        icon: 'none'
      })
      return;
    }
    this.setData({
      loading: true
    });

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ecode: this.data.pkey,
        points: this.data.points
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {



        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
            loading: false
          })
          return;
        };
        if (res.data.success) {
          wx.showToast({
            title: '支付成功',
          });
          setTimeout(() => {
            wx.navigateBack({
              delta: 1,
              success() {
                setTimeout(() => {
                  that.setData({
                    loading: false
                  });
                }, 200)
              }
            })
          }, 1000);
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
          setTimeout(() => {
            that.setData({
              loading: false
            });
          }, 200)
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
  onLoadFun: function () {}
})