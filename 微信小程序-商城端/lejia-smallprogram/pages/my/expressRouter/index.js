// pages/my/expressRouter/index.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        steps: [],
        orderInfo: {},
        logisticInfoList: null,
        orderType: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        if (options.orderType && options.orderType == 'INTEGRAL_JD_ORDER') {
            this.setData({
                orderType: options.orderType
            })
            const jdDeliveryInfo = JSON.parse(wx.getStorageSync('jdDeliveryInfo'))
            this.setData({
                logisticInfoList: jdDeliveryInfo.logisticInfoList,
                steps: jdDeliveryInfo.trackInfoList
            })
            return
        }
        const orderInfo = JSON.parse(wx.getStorageSync('orderInfo'))
        this.setData({
            orderInfo,
            steps: orderInfo.expressRoutes
        })
    },

    handleCopy() {
        wx.setClipboardData({
            data: this.data.orderInfo.kdCode
        })
    },

    handleJDCopy(data) {
        const deliveryOrderId = data.currentTarget.dataset.deliveryorderid
        wx.setClipboardData({
            data: deliveryOrderId
        })
    }
})