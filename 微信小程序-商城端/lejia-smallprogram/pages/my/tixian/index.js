// pages/my/tixian/index.js
let app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {}, //用户信息
    comms: '', //提现金额
    custCard: '' //银行卡后四位
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },
  /**限制input只能输入数字和小数点后2位 */
  limitInput(e) {
    let comms = e.detail;
    comms = comms.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
    comms = comms.replace(/^0{2}/g, ""); //验证第一第二个字符不为0
    comms = comms.replace(/^\./g, ""); //验证第一个字符是数字
    comms = comms.replace(/\.{2,}/g, ""); //只保留第一个, 清除多余的
    comms = comms.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
    comms = comms.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
    this.setData({
      comms: comms
    })
  },
  onSubmit() {
    const that = this,
      userInfo = this.data.userInfo,
      comms = this.data.comms
    if (!userInfo.custCard) {
      wx.showToast({
        title: '请填写银行卡信息',
        icon: 'none'
      })
      return
    }
    if (!comms) {
      wx.showToast({
        title: '请输入金额',
        icon: 'none'
      })
      return
    }

    let params = {
      accountBank: userInfo.accountBank,
      comms,
      custCard: userInfo.custCard,
      custName: userInfo.custName,

    }

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/comm/draw/ins',
      data: params,
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
            title: '提现成功',
            icon: 'none'
          })
          wx.navigateTo({
            url: '/pages/my/wallet/index',
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          })
        }

      }
    });
  },
  /**
   * 获取个人信息数据中的银行卡信息
   */
  getData() {
    const that = this,
      url = "/v1/app/market/lm/member/get";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
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
          let custCard = res.data.result.custCard
          that.setData({
            userInfo: res.data.result,
          });
          if (custCard != null)
            that.setData({
              custCard: custCard.substring(custCard.length - 4)
            });

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
    this.getData()
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

})