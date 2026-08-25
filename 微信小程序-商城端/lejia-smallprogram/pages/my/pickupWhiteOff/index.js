// pages/my/pickupWhiteOff/index.js
import utils from '../../../utils/util.js';
import http from '../../../utils/http'
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        isAuto: true,
        iShidden: true,
        cardNumber: null,
        verifyCode: null,
        kcCode: null,
        orderInfo: null,
        isError: false,
        errorMsg: "",
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        console.log(options);
        if(options.q) {
          const q = decodeURIComponent(options.q);
          console.log(q);
          const verifyCode = utils.getQueryString(q, 'verifyCode');
          const code = utils.getQueryString(q, 'code');
          this.setData({
            verifyCode: verifyCode,
            kcCode: code
          });
        }
        this.getData()
    },

    /**
     * 获取订单详情
     */
    getData() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v2/app/market/lm/order/pickup/verifyCode/scan',
            data: {
                kcCode: this.data.kcCode,
                verifyCode: this.data.verifyCode,
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded',
                "openid": app.globalData.openid
            },
            success: (res) => {
                console.log(res);
                if (res.data.success) {
                    this.setData({
                        orderInfo: res.data.result
                    })
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 2000
                    })
                    this.setData({
                        isError: true,
                        errorMsg: res.data.msg
                    })
                }
            }
        })
    },

    /**
     * 确认核销
     */
    handleWriteOff() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v2/app/market/lm/order/pickup/writeOff',
            data: {
                kcCode: this.data.kcCode,
                verifyCode: this.data.verifyCode,
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded',
                "openid": app.globalData.openid
            },
            success: (res) => {
                if (res.data.success) {
                    wx.showToast({
                      title: '核销成功',
                      icon: 'success'
                    })
                    this.getData()
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 2000
                    })
                }
            },
        })
    }
})