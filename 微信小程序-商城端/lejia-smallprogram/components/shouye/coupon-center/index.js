// components/shouye/coupon-center/coupon-center.js
import http from '../../../utils/http.js'
const app = getApp();
Component({
    pageLifetimes: {
        show() {
            this.loadCouponData()
            this.judgeCouponOver()
        }
    },
    /**
     * 组件的初始数据
     */
    data: {
        couponData: [],
        couponAllData: [], //卡券所有数据
        isCouponOver: false,
        imgUrl: app.globalData.file_url,
    },

    /**
     * 组件的方法列表
     */
    methods: {
        /**
         * @desc 获取卡券数据
         */
        loadCouponData() {
            let that = this;
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/index/queryCard',
                data: {
                    page: 0,
                    pagesize: 999,
                    cardPkey: ''
                },
                header: {
                    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    "openid": app.globalData.openid,
                    "farmer": app.globalData.location.pkey
                },
                success: function (res) {
                    if (res.data.code == "999") {
                        that.setData({
                            protocolShow: !that.data.protocol_check && that.data.protocol_isFirst,
                        })
                        return;
                    };
                    if (res.data.success) {
                        console.log('----------------------');
                        that.setData({
                            couponData: res.data.result.slice(0, 20),
                            couponAllData: res.data.result
                        })
                    }
                },
            })
        },
        /**
         * @desc 判断优惠券是否领完
         */
        judgeCouponOver() {
            let that = this;
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/index/card/isFinish',
                data: {},
                header: {
                    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    "openid": app.globalData.openid,
                    "farmer": app.globalData.location.pkey
                },
                success: function (res) {
                    if (res.data.code == "999") {
                        that.setData({
                            protocolShow: !that.data.protocol_check && that.data.protocol_isFirst,
                        })
                        return;
                    };
                    if (res.data.success) {
                        that.setData({
                            isCouponOver: res.data.result
                        })
                    }
                },
            })
        },
        /**
         * @desc 一键领取或者单个卡券领取
         * @param event 点击事件实例 单个卡券领取时带pkey
         */
        handleGetCoupon(event) {
            let cardPkeys = [],
                that = this;
            if (event.currentTarget.dataset.pkey) {
                if (!this.data.couponData[event.currentTarget.dataset.index].isReceive && this.data.couponData[event.currentTarget.dataset.index].count)
                    cardPkeys.push(event.currentTarget.dataset.pkey)
                else if (this.data.couponData[event.currentTarget.dataset.index].isReceive) {
                    return
                }
            } else {
                this.data.couponAllData.map(item => {
                    if (!item.isReceive && item.count) {
                        cardPkeys.push(item.pkey)
                    }
                });

            }
            if (!cardPkeys.length) {
                wx.showToast({
                    icon: 'none',
                    title: `优惠券已领完`,
                    duration: 2000
                })
                return
            }

            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/index/card/insertList',
                data: {
                    cardPkeys
                },
                header: {
                    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    "openid": app.globalData.openid,
                    "farmer": app.globalData.location.pkey
                },
                success: function (res) {
                    if (res.data.code == "999") {
                        that.setData({
                            protocolShow: !that.data.protocol_check && that.data.protocol_isFirst,
                        })
                        return;
                    };
                    if (res.data.success) {
                        wx.showToast({
                            icon: 'none',
                            title: `已成功领取${cardPkeys.length}张优惠券`,
                            duration: 2000
                        })
                        that.judgeCouponOver()
                        that.loadCouponData();
                    } else {
                        wx.showToast({
                            title: res.data.msg || '',
                            icon: "none"
                        });
                    }
                },
            })
        },
        /**
         * @desc 去领券中心
         */
        handleCoupon() {
            this.subscribeToMessages();
            wx.navigateTo({
                url: '/pages/my/coupon/coupon',
            })
        },
        // 微信订阅消息
        subscribeToMessages() {
            wx.requestSubscribeMessage({
                tmplIds: ['2cfjjpWq3FTiPK7CoJQmoGxmuHWunx7gF0qqVLCrV_Q', 'HhPUXnm42UW_b_smumzhZ31SLcfuacw84ULJiJXy5yE'],
                success: (res) => {
                    //成功回调  
                    console.log(1112)
                },
                fail: (res) => {
                    console.log("2222 res", res)
                }
            });

        },

    }
})