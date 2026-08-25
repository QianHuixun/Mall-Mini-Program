// pages/vendor/index.js
import http from '../../utils/http'
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        supplierInfo: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.getData()
    },

    /**
     * 
     */
    getData() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/supplier/get',
            header: {
                'content-type': 'application/x-www-form-urlencoded',
                "openid": app.globalData.openid
            },
            success: (res) => {
                if (res.data.success) {
                    this.setData({
                        supplierInfo: res.data.result
                    })
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 2000
                    })
                }
            },
        })
    },

    /** */
    handleLogout() {
        console.log('logout');
        this.setData({
            show: true
        })
    },
    // 确认退出
    onConfirm() {
        wx.reLaunch({
            url: '/pages/login/index',
        });
    },

    /**
     * 扫码核销
     */
    goVerification: function () {
        wx.scanCode({
            onlyFromCamera: true,
            success(res) {
                console.log(res);
                if (res.errMsg == 'scanCode:ok') {
                    wx.navigateTo({
                        url: '/pages/verification/index?cardNumber=' + res.result,
                    })
                }
            }
        })
    },
})