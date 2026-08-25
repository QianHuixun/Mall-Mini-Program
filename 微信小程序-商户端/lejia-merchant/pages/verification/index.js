// pages/verification/index.js
import http from '../../utils/http'
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        cardNumber: null,
        verifyCode: null,
        kcCode: null,
        orderInfo: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        console.log(options.cardNumber);
        let [verifyCode, kcCode] = options.cardNumber.split('-')
        console.log(verifyCode, kcCode);
        this.setData({
            cardNumber: options.cardNumber,
            verifyCode,
            kcCode
        })
        this.getData()
    },

    /**
     * 获取订单详情
     */
    getData() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/supplier/order/verifyCode/scan',
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
                    setTimeout(() => {
                      wx.navigateBack()
                    }, 2000)
                }
            },
        })
    },

    /**
     * 确认核销
     */
    handleWriteOff() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/supplier/order/writeOff',
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