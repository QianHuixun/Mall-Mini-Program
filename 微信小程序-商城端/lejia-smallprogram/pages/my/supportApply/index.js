// pages/my/supportApply/index.js
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        pkey: null,
        orderInfo: null,
        supportStepOneData: null,
        supportType: 'RETURN_GOODS', // RETURN_GOODS: 退货; EXCHANGE: 换货
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.setData({
            pkey: options.pkey
        });
        this.getData();
    },

    getData() {
        const url = "/v2/app/market/lm/order/loadOrder";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                pkey: this.data.pkey,
                jdType: this.data.supportType,
            },
            success: res => {
                if (res.data.success) {
                    wx.setStorageSync('refundInfo', JSON.stringify(res.data.result));
                    res.data.result.infos = res.data.result.infos.map(item => {
                        item.refundNum = 0
                        return item
                    })
                    this.setData({
                        orderInfo: res.data.result
                    });
                }
            }
        })
    },

    /**
     * 切换退换货类型
     */
    handleChangeType(data) {
        const supportType = data.currentTarget.dataset.type
        this.setData({
            supportType
        })
        this.getData();
    },

    /**
     * 确认提交退款/换货
     */
    handleSubmit() {
        const { orderInfo, supportType } = this.data
        let refundNum = 0
        orderInfo.infos.map(item => {
            refundNum += item.refundNum
        })
        if (refundNum <= 0) {
            wx.showToast({
                title: '请选择商品数量',
                icon: 'none'
            })
            return
        }
        const lines = []
        let jdDoor = true
        let selfMailing = true
        orderInfo.infos.map(item => {
            if(item.refundNum <= 0) return
            lines.push({
                pkey: item.orderLinePkey,
                num: item.refundNum,
                refundAmt: item.refundNum * item.couponPrice,
                photo: item.photo
            })
            if(item.refundNum && !item.jdDoor) jdDoor = false
            if(item.refundNum && !item.selfMailing) selfMailing = false
        })
        const supportData = {
            pkey: orderInfo.pkey,
            lines,
            returnExchange: supportType,
            status: orderInfo.status,
            orderOir: orderInfo.orderOir,
            addr: orderInfo.addr,
            orderType: orderInfo.orderType,
            jdDoor: jdDoor,
            selfMailing: selfMailing,
        }
        wx.setStorageSync('supportStepOneData', JSON.stringify(supportData));
        wx.navigateTo({
            url: `/pages/my/supportSubmit/index`,
        });
    },

    /**
     * 减少商品数量
     */
    handleMinusGoodsNum(data) {
        const goods = data.currentTarget.dataset.goods
        const {
            refundNum
        } = goods
        if (refundNum <= 0) return
        goods.refundNum--
        this.handleGoodsNumChange(goods)
    },

    /**
     * 增加商品数量
     */
    handlePlusGoodsNum(data) {
        const goods = data.currentTarget.dataset.goods
        const {
            refundNum
        } = goods
        if (refundNum >= goods.jdRefundNum) return
        goods.refundNum++
        this.handleGoodsNumChange(goods)
    },

    /**
     * 商品数量改变
     */
    handleGoodsNumChange(goods) {
        const {
            orderInfo
        } = this.data
        const goodsIndex = orderInfo.infos.findIndex(item => item.goods == goods.goods)
        orderInfo.infos[goodsIndex] = goods
        this.setData({
            orderInfo
        })
    }
})