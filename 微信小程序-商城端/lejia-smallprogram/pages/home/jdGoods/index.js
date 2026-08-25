// pages/integral/index.js
const app = getApp();
import http from '../../../utils/http'
import {
    onClickEffect
} from '../../../utils/util'
const {
    applyTheme
} = require('../../../utils/themeMixin')
var spaceView;
var loadMoreView;
// var goCartView;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        imgUrl: app.globalData.file_url,
        page: 0,
        pagesize: 6,
        imageList: [],
        goodsList: [],
        gtypeList: [],
        gtype: null,
        height: wx.getSystemInfoSync().windowWidth * 0.49 * 0.9,
        isLogin: false, //该页面是否需要登录
        isShow: false, //规格弹出显示
        iShidden: true,
        typeShow: false, // 分类更多弹框
        // isAuto:true
        lastGtype: '', // 点击进页面的商品分类，置顶该分类
        lastGoodsPkey: '', // 点击进页面的商品pkey，置顶该商品
        sellingPoints: ['新鲜现杀', '肉质细腻', '口感丰富', '价格颇高'],
        theme: null
    },
    /**礼品券入口 */
    handleGoClass() {
        wx.navigateTo({
            url: `/pages/shouyeGroup/integralClass/index`,
        });
    },
    //输入框聚焦
    searfocus() {
        // console.log("输入框聚焦");
        wx.navigateTo({
            url: '/pages/shouyeGroup/search/index?mType=INTEGRAL_GOODS',
        });

    },
    /**广告列表 */
    getImageList() {
        var _this = this;
        var parame = {
            position: "ADVERT_POSITION_JD"
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/img/query',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    var imageList = res.data.result.map(item => {
                        item.isTabbar = !1
                        if (item.urlType == "NOT_URL") {
                            item.url = "";
                        } else if (item.urlType == "LINK") {
                            item.url = item.objKey;
                        } else if (item.urlType == "POINTS_MALL") {
                            item.isTabbar = !0
                            item.url = "/pages/home/integral/index";
                        } else if (item.urlType == "MEMBERSHIP") {
                            item.url = "/pages/my/openVip/index";
                        } else if (item.urlType == "GOODS") {
                            item.url = "/pages/shouyeGroup/goodsDeatil/index?pkey=" + item.objKey;
                        } else if (item.urlType == "ACTIVITY") {
                            item.url = "/pages/activity/coupon/index?pkey=" + item.objKey;
                        } else if (item.urlType == "PERSONAL_CENTER") {
                            item.url = item.objKey;
                            item.isTabbar = !0
                        } else {
                            item.url = item.objKey;
                        }
                        return item;
                    });
                    _this.setData({
                        imageList: imageList
                    });
                }
            },
        })
    },
    /**轮播广告点击事件 */
    goAds: function (data) {
        onClickEffect(data)
    },
    /**商品类别 */
    getGtype() {
        var _this = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/jd/category/drop',
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    console.log(res.data);
                    const result = res.data.result
                    if (_this.data.lastGtype) {
                        const index = result.findIndex(item => item.pkey == _this.data.lastGtype)
                        const list = result.splice(index, 1)
                        result.unshift(list[0])
                    }
                    _this.setData({
                        gtypeList: result,
                        gtype: result[0].pkey
                    });
                    console.log('gtypeList:', result);
                    _this.loadData();
                }
            },
        })
    },

    handleGTypeChange(event) {
        console.log(event, '22');
        this.setData({
            gtype: event.currentTarget.dataset.id,
            page: 0,
            typeShow: false
        })
        this.loadData()
    },

    /**分类点击 */
    gtypeClick(event) {
        var item = event.currentTarget.dataset.id;
        wx.switchTab({
            url: '../classification/index?pkey=' + item.pkey,
        });
    },


    /**
     * 获取商品列表
     */
    loadData: function () {
        var that = this,
            url = "/v1/app/jd/goods/query";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                category: that.data.gtype,
                page: that.data.page,
                pagesize: that.data.pagesize,
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    if (that.data.page == 0) {
                        that.setData({
                            goodsList: res.data.result.content,
                            page: ++that.data.page
                        })
                    } else {
                        that.setData({
                            goodsList: that.data.goodsList.concat(res.data.result.content),
                            page: ++that.data.page
                        })
                    }
                    res.data.result.curPage = that.data.page;
                    loadMoreView.loadMoreComplete(res.data);
                }
            }
        });
    },

    /**商品点击进入详情 */
    goodsClick(event) {
        var item = event.currentTarget.dataset.id;
        wx.navigateTo({
            url: '/pages/shouyeGroup/jdGoodsDetail/index?pkey=' + item.pkey,
        });
    },

    /**
     * 获取当前商品的规格数量
     */
    getSpaceNumber: function (data) {
        var url = "/v1/app/market/goods/space/totalAmount",
            item = data.currentTarget.dataset.id,
            pkey = data.currentTarget.dataset.pkey,
            space = data.currentTarget.dataset.space,
            that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                pkey: pkey
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.result == 1) {
                    if (item.mtype === "COUPON_GOODS" || item.mtype === "GIFT_GOODS") {
                        that.handlePay(data, pkey, space)
                    } else {
                        that.handleAddTOCart(data, pkey, space);
                    }
                } else {
                    that.setData({
                        isShow: true
                    });
                    spaceView.getData(pkey);
                }
            }
        })
    },

    /**
     * 添加到购物车
     */
    handleAddTOCart(data) {
        const {pkey, id} = data.currentTarget.dataset
        let goodsNum = 1
        if(id.lowestBuy && (id.lowestBuy > id.gwcNum)) {
            goodsNum = id.lowestBuy
        }
        const params = {
            pkey,
            goodsNum,
            latitude: wx.getStorageSync('latitude'), 
            longitude: wx.getStorageSync('longitude'), 
        };
        const url = "/v1/app/jd/goods/gwc/ins";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            success: (res) => {
                if (res.data.code == "999") {
                    this.setData({
                        iShidden: false
                    })
                    return;
                };
                if (res.data.success) {
                    app.getBuycarNum();
                    const {
                        goodsList
                    } = this.data
                    const index = goodsList.findIndex(item => item.pkey === pkey)
                    goodsList[index].gwcNum += goodsNum
                    this.setData({
                        goodsList
                    })
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: "none"
                    });
                }
            }
        });
    },

    /**
     * 商品规格添加购物车
     */
    handleAddSpaceToBuyCar(data) {
        const {
            goodsPkey,
            num
        } = data.detail
        let {
            goodsList
        } = this.data
        const index = goodsList.findIndex(item => item.pkey === goodsPkey)
        goodsList[index].gwcNum = num
        this.setData({
            goodsList
        })
    },

    /**
     * 去支付页结算
     */
    handlePay(data, pkey, space) {
        console.log(data, pkey, space);
        let url = "/v2/app/market/lm/order/buyGoods"
        let params = {
            space, //规格pkey
            num: 1, //商品数量
            dineIn: app.globalData.qrCode ? true : false
        }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: (res) => {
                console.log(res);
                if (res.data.success) {
                    wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
                    wx.navigateTo({
                        url: '/pages/pay/pay/index?type=goods&space=' + space + '&num=1'
                    });
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    })
                }
            }
        })
    },
    /*
    分类更多弹框
    */
    showTypePopup() {
        wx.pageScrollTo({
            scrollTop: 210,
            duration: 300 // 滚动动画的时长（单位：ms）
        });
        this.setData({
            typeShow: true
        });
    },

    onTypeClose() {
        this.setData({
            typeShow: false
        });
    },
    getDisPlayName() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
            success: (res) => {
                const data = res.data.result
                if (data.jdGoodsDisplayName) {
                    wx.setNavigationBarTitle({
                        title: data.jdGoodsDisplayName,
                    })
                }
            }
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log('--pageOnload--');
        applyTheme(this)
        this.getDisPlayName()
        loadMoreView = this.selectComponent("#loadMoreView");
        spaceView = this.selectComponent("#spaceView");
        this.setData({
            iShidden: true,
            lastGtype: options.gtype || '',
            lastGoodsPkey: options.pkey != 'undefined' ? options.pkey : ''
        });
        app.getBuycarNum();
        this.getImageList();
        this.getGtype();
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        // const tabs = this.selectComponent('.tabs');
        // setTimeout(() => {
        //   tabs.resize()
        // },100)
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
        this.setData({
            isShow: false,
            // page: 0
        })
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
        loadMoreView.loadMore()
    },


})