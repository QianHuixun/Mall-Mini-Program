// pages/my/info/index.js
const app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    userInfo: app.globalData.userInfo,
    custCard: '', //输入框绑定的银行卡号
    accountBank: '', //输入框绑定的开户行
    custName: '', //输入框绑定的姓名
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getData();
  },
  /**输入框聚焦 */
  inputFocus(e) {
    console.log(e.detail.height)
  },
  /**
   * @desc 银行卡号约束
   */
  cardChange(e) {
    let val = e.detail;
    val = val.replace(/[^\d]/g, '');
    this.setData({
      custCard: val
    })
  },
  /**银行卡信息编辑 弹窗弹出*/
  handleEdit() {
    this.setData({
      show: !0
    })
  },
  /** 弹窗关闭*/
  onClose() {
    this.setData({
      show: !1,
    });
    this.getData();
  },
  /**
   * 银行卡信息信息提交
   */
  onSubmit(e) {
    let custCard = this.data.custCard,
      accountBank = this.data.accountBank,
      custName = this.data.custName;

    let params = {
        accountBank,
        custCard,
        custName
      },
      that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/ins/cust/card',
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
            title: '保存成功',
            icon: 'none'
          });
          that.setData({
            show: !1
          });
          that.getData();
        } else {
          wx.showToast({
            title: res.codeMsg || '',
            icon: 'none'
          });
        }

      }
    });
  },
  /**
   * 获取个人信息数据
   */
  getData: function () {
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

          that.setData({
            userInfo: res.data.result,
            custCard: res.data.result.custCard != null ? JSON.parse(JSON.stringify(res.data.result.custCard)) : '',
            accountBank: res.data.result.accountBank ? JSON.parse(JSON.stringify(res.data.result.accountBank)) : '',
            custName: res.data.result.custName ? JSON.parse(JSON.stringify(res.data.result.custName)) : ''
          });
        }

      }
    });

  },

  /**
   * 修改微信头像
   */
  onChooseAvatar(e) {
    console.log(e);
    const { avatarUrl } = e.detail 
    wx.uploadFile({
      filePath: avatarUrl,
      name: 'file',
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/uploadImage',
      header: {
        "Content-Type": 'application/xml',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey,
        "ascription":app.globalData.ascription
      },
      success: (res) => {
        console.log("res",res)
        console.log(JSON.parse(res.data).result.url);
        this.setData({
          ['userInfo.photo']: JSON.parse(res.data).result.url,
        })
        this.updateInfo()
      },
      fail: (err) => {
        wx.showToast({
          title: '图片上传失败',
          icon: 'none',
          duration: 2000
        });
        this.getData()
      }
    })
  },

  /**
   * 修改会员名称
   */
  handleNamChange(event) {
    const name = event.detail.value
    console.log(name);
    this.setData({
      ['userInfo.name']: name
    })
    this.updateInfo()
  },

  /**
   * 更新个人信息
   */
  updateInfo() {
    console.log(this.data.userInfo);
    let params = {
      name: this.data.userInfo.name,
      photo: this.data.userInfo.photo
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/upd',
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: (res) => {
        if (res.data.code == "999") {
          this.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          this.getData()
        }
      }
    });
  },

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.getData();
  }
})