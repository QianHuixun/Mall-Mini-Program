// pages/my/dispatchSlip/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        refundPkey: null,
        courierNumber: null,
        courierCompany: null,
        postage: null,
        show: false,
        columns: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.setData({
            refundPkey: options.pkey
        });
        this.getCourierComany()
    },
    handleCourierNumberInput(data) {
        console.log(data);
        this.setData({
            courierNumber: data.detail.value
        })
        const findCourierCompany = this.identifyCourier(data.detail.value)
        console.log(findCourierCompany);
        if(findCourierCompany) {
            this.data.columns.map(item => {
                if(item.includes(findCourierCompany)) {
                    this.setData({
                        courierCompany: item
                    })
                }
            })
        }
    },
    handleComanyClick() {
        this.setData({
            show: true
        })
    },
    getCourierComany() {
        const url = "/v1/app/jd/refund/courier/drop"
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            success: res => {
                if (res.data.success) {
                    this.setData({
                        columns: res.data.result
                    })
                } else {
                    wx.showToast({
                        icon: 'none',
                        title: res.data.msg || ''
                    })
                }
            }
        });
    },
    handlePopupconfirm(data) {
        this.setData({
            courierCompany: data.detail.value
        })
        this.handleClose()
    },
    handleClose() {
        this.setData({
            show: false
        })
    },
    identifyCourier(trackingNumber) {
        // 预处理：去除空格，统一转大写
        trackingNumber = trackingNumber.trim().toUpperCase();
        const patterns = {
            顺丰: /^SF\d{13}$/, // SF + 13位数字
            中通: /^\d{14}$/, // 14位纯数字（当前主流）
            圆通: /^YT\d{13}$/, // YT + 13位数字
            韵达: /^\d{15}$/, // 15位纯数字
            京东: /^JD[A-Z\d]{13}$/, // JD + 13位字母或数字
            极兔: /^JT\d{13}$/, // JT + 13位数字
            申通: /^\d{12}$/, // 12位纯数字
        };
        for (const [company, regex] of Object.entries(patterns)) {
            if (regex.test(trackingNumber)) {
                return company;
            }
        }
        return '';
    },
    handlePostageInput(data) {
        this.setData({
            postage: data.detail
        })
    },
    handleSubmit() {
        const { refundPkey, courierNumber, courierCompany, postage } = this.data
        if(!courierNumber) {
            wx.showToast({
                title: '请输入快递单号',
                icon: 'none'
            })
            return
        }
        if(!courierCompany) {
            wx.showToast({
                title: '请选择快递公司',
                icon: 'none'
            })
            return
        }
        if(!postage) {
            wx.showToast({
                title: '请输入运费',
                icon: 'none'
            })
            return
        }
        const url = '/v1/app/jd/refund/updateSendInfo'
        const params = { refundPkey, courierNumber, courierCompany, postage }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            success: res => {
                if (res.data.success) {
                    wx.navigateBack({
                        delta: 1,
                    });
                } else {
                    wx.showToast({
                        icon: 'none',
                        title: res.data.msg || ''
                    })
                }
            }
        });
    }
})