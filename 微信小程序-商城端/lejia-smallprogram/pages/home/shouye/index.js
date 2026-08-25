// pages/shouye/index.js
const app = getApp();
import utils from '../../../utils/util.js';
import http from '../../../utils/http.js'
import mapKey from '../../../utils/map-key.js';
const { applyTheme } = require('../../../utils/themeMixin')
var spaceView;
// var goCartView;
var timer;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        protocolShow: false,
        protocol_check: app.globalData.protocol_check,
        protocol_isFirst: app.globalData.protocol_isFirst,
        imgUrl: app.globalData.file_url,
        isLogin: false,
        isLoginClick: false,
        location: {
            pkey: wx.getStorageSync('location_pkey') || '',
            name: wx.getStorageSync('location_name') || ''
        },
        time: 0,
        timeData: {},
        miaoshaList: [], //限时秒杀
        imageList: [], //轮播图
        gtypeList: [], //分类
        likeGoodsList: [], //猜你喜欢
        page: 0,
        loadingBom: false, //是否在加载更多
        noMoreBom: false, //没有更多
        statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
        titleBarHeight: 44,
        firstLoading: false,
        windowHeight: wx.getSystemInfoSync().windowHeight,
        show: [], //弹窗广告是否显示变量
        popList: [], //弹窗广告列表
        adChecked: false, // 是否勾选不再显示此弹框
        adCheckedList: wx.getStorageSync('adCheckedList'), // 保存勾选了不再显示此弹框的广告pkey
        couponData: [], //卡券前20数据
        couponAllData: [], //卡券所有数据
        hasNewCoupon: false, //是否有新的手动发放卡券
        newCouponList: [], //手动发放卡券列表
        recipeList: [], //菜谱列表
        boutiqueVendor: [],
        rightVendor: [],
        leftVendor: [],
        isCouponOver: false,
        topNum: 0,
        userData: {},
        marketInfo: {
            config: {}
        },
        shareInfo: {},
        AppInfo: {},
        marketType: app.globalData.location.marketType || wx.getStorageSync('location_type'),
        goodsType: app.globalData.location.goodsType || wx.getStorageSync('location_goodsType'),
        rangeShow: false, //超出配送范围 是否显示弹窗
        rangeData: {},
        timeShow: false, //获取首页预约配送时间  在营业时间内 就不显示 
        deliveryTime: "",
        // 活动弹窗
        couponActivityShow: false, // 是否显示活动弹窗
        couponActivityPkey: "", // 活动pkey
        couponActivityData: {},
        // end 活动弹窗
        indexModel: app.globalData.indexModel,  // 首页使用模板
        merchantTitleName: app.globalData.ascription == 22 || app.globalData.ascription == 13 ? '市场商户专区' : '精选品牌',
        displayName: {                      // 专区名称
            integralBNYPDisplayName: '',    // 滨农优品
            integralDisplayName: '',        // 积分商城 | 滨海自营
            integralPresaleDisplayName: '', // 预售专区
            specialDisplayName: '',         // 限时秒杀
        },
        zoneGoodsList: {                // 轮播商品列表
            specialList: [],            // 限时秒杀
            integralList: [],           // 积分商城 | 滨海自营
            integralPresaleList: [],    // 预售专区
            integralBNYPList: [],       // 滨农优品
        },
        walletName: '钱包',
        theme: {
            value: 'basics'
        }
    },
    /** 跳转到我的优惠券 */
    goMyCard() {
        wx.navigateTo({
            url: '/pages/my/card/index',
        })
    },
    /** 跳转到积分兑换 */
    goIntegral() {
        wx.switchTab({
            url: '/pages/home/integral/index',
        });
    },
    /** 跳转到充值有礼 */
    goRecharge() {
        wx.showToast({
            title: "暂未开放",
            icon: 'none'
        });
    },
    /** 跳转到会员优惠 */
    goMember() {
        wx.navigateTo({
            url: '/pages/my/coupon/coupon',
        })
    },
    /**
     * 获取会员信息
     */
    getMemberData() {
        var that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/centre',
            data: {},
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: res => {
                if (res.data.code == "999") {
                    that.setData({
                        isLogin: false,
                        protocolShow: !that.data.protocol_check && that.data.protocol_isFirst,
                    })
                    return;
                };
                that.setData({
                    isLogin: true,
                    protocolShow: false,
                });

                that.setData({
                    userData: res.data.result
                });
            }
        })
    },
    /**
     * 跳转到菜谱
     */
    goRecipe() {
        wx.navigateTo({
            url: '/pages/shouyeGroup/recipe/index',
        })
    },
    /**跳转到菜谱详情 */
    goRecipeDetail(event) {
        var item = event.currentTarget.dataset.id;
        wx.navigateTo({
            url: '/pages/shouyeGroup/recipe/detail?pkey=' + item.pkey,
        })
    },
    /**获取菜谱大全列表 */
    getRecipe() {
        var that = this;
        var parame = {
            page: 0,
            pagesize: 10,
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/query',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    that.setData({
                        recipeList: res.data.result.content
                    })
                }
            },
        })
    },
    /**获取精选商户列表 */
    getBoutiqueVendor() {
        const _this = this;
        if (!this.data.page) {
            this.setData({
                firstLoading: true
            })
        }
        const parame = {
            page: this.data.page,
            pagesize: 6,
        }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/index/query/vendor/boutique',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success(res) {
                console.log('getBoutiqueVendor', res);
                if (res.data.success) {
                    var list = _this.data.boutiqueVendor,
                        noMoreBom = false;

                    if (!_this.data.page) {
                        list = res.data.result.content
                    } else
                        var list = _this.data.boutiqueVendor.concat(res.data.result.content);
                    if (list.length == res.data.result.total) {
                        noMoreBom = true;

                    }
                    _this.setData({
                        boutiqueVendor: list,
                        loadingBom: false,
                        noMoreBom: noMoreBom,
                    });
                    setTimeout(() => {
                        _this.setData({
                            firstLoading: false
                        });
                    }, 1200)
                    _this.getBoutiqueVendorSplit()
                }
            },
        })
    },
    /**将精选商户列表分成左右两列 */
    getBoutiqueVendorSplit() {
        const right = []
        const left = []
        const list = this.data.boutiqueVendor
        list.forEach((item, index) => {
            console.log(index);
            if (index % 2 === 0) {
                left.push(item)
            } else {
                right.push(item)
            }
        })
        this.setData({
            rightVendor: right,
            leftVendor: left
        })
    },
    /**
     * @desc 跳转到会员开通页面
     */
    handleGoVip() {
        wx.navigateTo({
            url: '/pages/my/openVip/index'
        })
    },
    /**
     * @desc 商品购物车数量发生改变 刷新数据
     */
    refreshItemNum(e) {
        let likeGoodsList = this.data.likeGoodsList;
        for (let i in this.data.likeGoodsList) {
            let item = this.data.likeGoodsList[i];
            if (item.pkey == e.detail.goodsPkey) {
                likeGoodsList[i].gwcNum = e.detail.num;
                break;
            }
        }
        this.setData({
            likeGoodsList: likeGoodsList
        })
    },
    /**
     * @desc 去分类页面
     */
    handleGoClassify(e) {
        wx.setStorageSync('classiftyPkey', e.currentTarget.dataset.pkey)
        wx.switchTab({
            url: '/pages/home/classification/index',
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
     * @desc 判断是否有新的手动发放卡券到达账户
     */
    judgeNewCoupon() {
        let that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/index/queryNewCard',
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
                if (res.data.success && res.data.result.length) {
                    that.setData({
                        newCouponList: res.data.result,
                        hasNewCoupon: true
                    })
                }
            },
        })
    },
    /**
     * @desc 关闭卡券弹窗
     */
    handleCloseCoupon() {
        this.setData({
            hasNewCoupon: false
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
  subscribeToMessages(){
    wx.requestSubscribeMessage({  
      tmplIds: ['2cfjjpWq3FTiPK7CoJQmoGxmuHWunx7gF0qqVLCrV_Q','HhPUXnm42UW_b_smumzhZ31SLcfuacw84ULJiJXy5yE'],  
      success:(res)=> {  
      //成功回调  
        console.log(1112)  
      },
      fail:(res)=> {
        console.log("2222 res", res)  
      }  
    });     
    
  },
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
                    that.setData({
                        couponData: res.data.result.slice(0, 20),
                        couponAllData: res.data.result
                    })
                }
            },
        })
    },

    /**关闭弹窗 */
    handleClosePop(e) {
        let show = this.data.show;
        show[e.currentTarget.dataset.index] = !1;
        wx.setStorageSync('adCheckedList', this.data.adCheckedList)
        console.log(this.data.adCheckedList);
        this.setData({
            show,
            adChecked: false,
        })
    },
    /**获取弹窗列表 */
    getPopData() {
        let that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/img/query/index',
            data: {},
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.result.length) {
                    let show = [],
                        adsLists = res.data.result,
                        adCheckedList = that.data.adCheckedList || wx.getStorageSync('adCheckedList')
                    show = adsLists.map(item => {
                        if (adCheckedList.includes(item.pkey)) return false
                        return true
                    })
                    that.setData({
                        show,
                        popList: adsLists
                    })
                }
            }
        })
    },

    adCheckedChange(event) {
        console.log(event);
        let pkey = event.currentTarget.dataset.item.pkey
        let adCheckedList = this.data.adCheckedList || []
        if (adCheckedList.includes(pkey)) {
            let index = adCheckedList.findIndex(item => item == pkey)
            if (index > -1) adCheckedList.splice(index, 1)
        } else {
            adCheckedList.push(pkey)
        }
        console.log(adCheckedList);
        this.setData({
            adChecked: !this.data.adChecked,
            adCheckedList
        })
    },

    /**图片加载监听 */
    imageLoad(e) {
        if (e.detail.width) {
            let goodsList = `goodsList[${e.currentTarget.dataset.index}].hidden`
            this.setData({
                [goodsList]: !0
            })
        }
    },
    //回到顶部
    goTop() {
        this.setData({
            topNum: 0,
        })
    },
    //滚动到顶部
    bindscrolltop(eventhandle) {
        if (eventhandle.detail.direction == "top") {
            // console.log("top");
        }
    },
    goOpenVip() {
        wx.navigateTo({
            url: '/pages/my/openVip/index',
        })
    },
    //滚动到底部
    bindscrollbottom(eventhandle) {
        console.log(this.data.loadingBom)
        if (eventhandle.detail.direction == "bottom") {
            // console.log("bottom");
            if (this.data.noMoreBom || this.data.loadingBom) {
                return;
            }
            this.setData({
                page: this.data.page + 1,
                loadingBom: true,
                noMoreBom: false
            });
            this.getLikeGoodsList();
            this.getBoutiqueVendor()
        }
    },

    //输入框聚焦
    searfocus() {
        // console.log("输入框聚焦");
        wx.navigateTo({
            url: '/pages/shouyeGroup/search/index',
        });

    },

    //进去选择市场
    addressSelect() {
        wx.navigateTo({
            url: '/pages/shouyeGroup/position/index',
        });
    },

    /**
     * 获取当前商品的规格数量
     */
    getSpaceNumber: function (data) {
        var url = "/v1/app/market/goods/space/totalAmount",
            pkey = data.currentTarget.dataset.pkey,
            space = data.currentTarget.dataset.space,
            index = data.currentTarget.dataset.index,
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
                    that.handleAddTOCart(data, pkey, space, index);
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
    handleAddTOCart: function (data, pkey, space, index) {

        var url = "/v1/app/market/lm/member/gwc/ins",
            that = this,
            params;
        if (data.detail.hasOwnProperty('data')) {
            params = {
                goodsPkey: data.detail.goodsPkey,
                goodsNum: 1,
                space: data.detail.space,
            }
            data = data.detail.data
        } else
            params = {
                goodsPkey: pkey,
                goodsNum: 1,
                space: space,
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
            success: function (res) {
                if (res.data.code == "999") {
                    that.setData({
                        protocolShow: !that.data.protocol_check && that.data.protocol_isFirst,
                    })
                    return;
                };
                if (res.data.success) {
                    // goCartView.addshopcar(data);
                    app.getBuycarNum();
                    wx.showToast({
                        title: '已加入购物车',
                        icon: "none"
                    });
                    let likeGoodsList = that.data.likeGoodsList;
                    likeGoodsList[index].gwcNum = likeGoodsList[index].gwcNum + 1;
                    that.setData({
                        likeGoodsList: likeGoodsList
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
    //倒计时
    onChange(e) {
        this.setData({
            timeData: e.detail,
        });
    },

    /**限时秒杀 */
    getMiaoShaList() {
        var _this = this;
        var parame = {
            page: 0,
            pagesize: 10,
            mType: "SPECIAL_GOODS"
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/goods/query',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    // console.log(res.data.result);
                    var time = 0;
                    if (res.data.result.content.length != 0) {
                        time = res.data.result.content[0].remainingTime;
                    }
                    _this.setData({
                        miaoshaList: res.data.result.content,
                        time: time
                    });
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    });
                }
            },
        })
    },


    /**商品详情 */
    goodsClick(event) {
        var item = event.currentTarget.dataset.id || event.currentTarget.dataset.pkey;
        wx.navigateTo({
            url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
        });
    },

    /**抢购 */
    snapClick() {
        wx.navigateTo({
            url: "/pages/shouyeGroup/presell/index",
        });
    },

    /**获取定位 */
    getLocal() {
        console.log(app.globalData.location.pkey)
        // if (app.globalData.location.pkey) return;
        // if (!this.data.protocol_check) {
        //     // wx.navigateTo({
        //     //   url: '/pages/shouyeGroup/position/index',
        //     // });
        //     this.getMarket("", "");
        //     return;
        // }

        console.log("开始定位");
        var _this = this;
        wx.getLocation({
            type: 'wgs84',
            isHighAccuracy: true,
            success(res) {
                const latitude = res.latitude
                const longitude = res.longitude
                const speed = res.speed
                const accuracy = res.accuracy
                console.log(res, "res");
                wx.setStorageSync('latitude', latitude);
                wx.setStorageSync('longitude', longitude);
                var url = mapKey.getReverseGeocoderUrl(res.latitude, res.longitude);
                http.request({
                    url: url,
                    success: function (result) {
                        wx.setStorageSync('location_district', result.data.result.ad_info.district);
                        wx.setStorageSync('location_address', result.data.result.address);

                        app.globalData.location.district = result.data.result.ad_info.district;
                        app.globalData.location.address = result.data.result.address;
                        // console.log(app.globalData.location.district, "app.globalData.location.district")
                    }
                })
                _this.getMarket(latitude, longitude);
            },
            fail(res) {
                if (!app.globalData.location.pkey) {
                    wx.navigateTo({
                        url: '/pages/shouyeGroup/position/index',
                    });
                }
                _this.getMarket("", "");
            }
        });
    },

    /**获取最近的市场 */
    getMarket(lat, lon) {
        var _this = this;
        var parame = {
            latitude: lat,
            longitude: lon,
            version: app.globalData.version,
            accountType: "USER"
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/index/currentFarmer',
            data: parame,
            async: false,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    // console.log(res.data);
                    _this.setData({
                        location: {
                            pkey: res.data.result.pkey,
                            name: res.data.result.name
                        },
                        marketInfo: res.data.result
                    });
                    // 是否超出配送范围
                    if (lat && lon) {
                        _this.checkFarmerInRange(lat, lon, res.data.result.pkey);
                    };
                    console.log("marketInfo", _this.data.marketInfo)
                    wx.setStorageSync('location_pkey', res.data.result.pkey);
                    wx.setStorageSync('location_name', res.data.result.name);
                    wx.setStorageSync('location_tel', res.data.result.tel || res.data.result.mobile);
                    wx.setStorageSync('location_type', res.data.result.type)
                    wx.setStorageSync('location_goodsType', res.data.result.config.goodsType)
              
                    app.globalData.location.pkey = res.data.result.pkey;
                    app.globalData.location.name = res.data.result.name;
                    app.globalData.location.tel = res.data.result.name || res.data.result.mobile;
                    app.globalData.location.marketType = res.data.result.type;
                    app.globalData.location.goodsType = res.data.result.config.goodsType;
                    _this.setData({
                        marketType: res.data.result.type,
                        goodsType: res.data.result.config.goodsType,
                        page: 0
                    })

                    _this.getDeliveryTime();
                    _this.getGtype();
                    _this.getImageList();
                    _this.getLikeGoodsList();
                    _this.getBoutiqueVendor()
                    _this.getMiaoShaList();
                    const spaceView = _this.selectComponent("#classBar");
                    if(spaceView) spaceView.getData()
                    const thirdEdition = _this.selectComponent("#thirdEdition");
                    if(thirdEdition) thirdEdition.getData()
                    const recommendGoods = _this.selectComponent("#recommendGoods");
                    if(recommendGoods) recommendGoods.getData()
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    });
                }
            },
        })
    },

    // 是否超出配送范围
    checkFarmerInRange(lat, lon, pkey) {
        var _this = this;
        var parame = {
            latitude: lat,
            longitude: lon,
            farmer: pkey
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/index/checkFarmerInRange',
            data: parame,
            async: false,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    console.log("rangeData", res.data.result);
                    res.data.result.distanceKM = (res.data.result.distance / 1000).toFixed(2);
                    _this.setData({
                        rangeShow: !res.data.result.inRange,
                        rangeData: res.data.result
                    })
                }
            }
        });
    },
    rangeClose() {
        this.setData({
            rangeShow: false
        });
    },
    rangeOtherMarket() {
        this.setData({
            rangeShow: false
        });
        this.addressSelect();
    },
    // 获取首页预约配送时间  在营业时间内 就不显示
    getDeliveryTime() {
        const _this = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/index/get/reservation/time',
            data: {},
            async: false,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    console.log("rangeData", res.data.result);
                    _this.setData({
                        deliveryTime: res.data.result,
                        timeShow: !!res.data.result
                    })
                }
            },
            fail: function () {

            }
        });
    },
    /**商品类别 */
    getGtype() {
        var _this = this;
        var parame = {
            showMarket: true,
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/goods/gtype/query',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    // console.log(res.data);
                    _this.setData({
                        gtypeList: res.data.result
                    });
                } else {

                }
            },
        })
    },

    /**分类点击 */
    gtypeClick(event) {
        var item = event.currentTarget.dataset.id;
        if (item) {
            wx.switchTab({
                url: '../classification/index?pkey=' + item.pkey,
            });
        } else {
            wx.switchTab({
                url: '../classification/index',
            });
        }

    },

    /**广告列表 */
    getImageList() {
        var _this = this;
        var parame = {
            position: "ADVERT_POSITION_INDEX"
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
                        if (item.urlType == "NOT_URL") {
                            item.url = "";
                        } else if (item.urlType == "LINK") {
                            item.url = item.objKey;
                        } else if (item.urlType == "POINTS_MALL") {
                            item.url = "/pages/home/integral/index";
                        } else if (item.urlType == "ACTIVITY") {
                            item.url = "/pages/activity/coupon/index?pkey=" + item.objKey;
                        } else if (item.urlType == "MEMBERSHIP") {
                            item.url = "/pages/my/openVip/index";
                        } else if (item.urlType == "GOODS") {
                            item.url = "/pages/shouyeGroup/goodsDeatil/index?pkey=" + item.objKey;
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
        utils.onClickEffect(data)
        
    },
    /**猜你喜欢列表 */
    getLikeGoodsList() {
        var _this = this;
        if (!this.data.page) {
            this.setData({
                firstLoading: true
            })
        }
        var parame = {
            page: this.data.page,
            pagesize: 6,
            hotSort: "1",
            guessLike: true
        };
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/goods/query/guessLike',
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    // console.log(res.data.result);
                    var list = _this.data.likeGoodsList,
                        noMoreBom = false;

                    if (!_this.data.page) {
                        list = res.data.result.content
                    } else
                        var list = _this.data.likeGoodsList.concat(res.data.result.content);
                    if (list.length == res.data.result.total) {
                        noMoreBom = true;

                    }
                    _this.setData({
                        likeGoodsList: list,
                        loadingBom: false,
                        noMoreBom: noMoreBom,
                    });
                    setTimeout(() => {
                        _this.setData({
                            firstLoading: false
                        });
                    }, 1200)
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    });
                }
            },
        })
    },
    /**
     * 跳转到分类-商户
     */
    goMerchant() {
        wx.setStorageSync('isMerchant', true)
        wx.switchTab({
            url: '/pages/home/classification/index',
        })
    },
    /**
     * 跳转到商户详情
     */
    goMerchantDetail: function (data) {
        wx.navigateTo({
            url: '/pages/shouyeGroup/merchant/index?pkey=' + data.currentTarget.dataset.pkey
        })
    },

    /**
     * 扫码
     */
    scanCode: function () {
        wx.scanCode({
            // onlyFromCamera: true,
            success(res) {
                console.log(res)
                if (res.errMsg == 'scanCode:ok') {
                    console.log(res)
                    wx.navigateTo({
                        url: res.result
                    })
                }
            }
        })
    },

    topGridClick() {
        wx.showToast({
            title: "暂未开放",
            icon: 'none'
        });
    },
    /**
     * 获取头部导航高度
     */
    getSystemInfo() {

        let _that = this
        wx.getSystemInfo({
            success: function (res) {
                let header = wx.getMenuButtonBoundingClientRect()
                _that.setData({
                    titleBarHeight: (header.bottom + header.top) - (res.statusBarHeight * 2)
                })
            },
            fail() {
                _that.data.statusBarHeight = 88
                _that.data.titleBarHeight = 44
            }
        })
    },
    /**
     * 点击图片查看大图
     */
    handleImageClock(event) {
        const src = event.currentTarget.dataset.src
        console.log(event);
        wx.previewImage({
            urls: [src]
        })
    },
    /**
     * 跳转 钱包充值
     */
    goCommsQuery: function () {
        wx.navigateTo({
            url: '/pages/my/commsQuery/index?mobile=' + this.data.userData.mobile
        })
    },
    /**
     * 跳转到 积分查询
     */
    goPointQuery: function () {
        wx.navigateTo({
            url: '/pages/my/pointQuery/index'
        })
    },
    /**
     * 跳转到 我的优惠券
     */
    goCard: function () {
        wx.navigateTo({
            url: '/pages/my/card/index'
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log(options);
        applyTheme(this)
        if (options.q) {
            const q = decodeURIComponent(options.q);
            const tjv = utils.getQueryString(q, 'pkey');
            wx.setStorageSync('tjv', tjv);
            // 扫码来源
            const source = utils.getQueryString(q, 'qd');
            wx.setStorageSync("source", source);
            app.globalData.source = source;

            const couponActivityPkey = utils.getQueryString(q, 'activity');
            if (couponActivityPkey) {
                this.setData({
                    couponActivityPkey
                });
                this.getCoupon();
            }

        }

        if (app.globalData.openid) {
            this.getPopData();
        }

        spaceView = this.selectComponent("#spaceView");
        // goCartView = this.selectComponent("#goCartView");
        // goCartView.getPosition();
        setTimeout(() => {
            this.getLocal();
        }, 500);

        if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
            this.setData({ walletName: 'I DO' })
        }
    },
    getCoupon: function () {
        var that = this,
            url = "/v1/app/market/activity/get";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                pkey: this.data.couponActivityPkey
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey,
                "ascription": app.globalData.ascription
            },
            success: res => {
                if (!res.data.success) {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    })
                    return;
                }
                that.setData({
                    couponActivityData: res.data.result,
                    couponActivityShow: true,
                });
                console.log("couponData", that.data.couponActivityData.photo)
            }
        });
    },
    handleCloseCoupon() {
        this.setData({
            couponActivityShow: false
        })
    },
    goCouponActivity() {
        wx.navigateTo({
            url: '/pages/activity/coupon/index?pkey=' + this.data.couponActivityData.pkey
        })
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
        if(this.getTabBar()) {this.getTabBar().init()}
        spaceView = this.selectComponent("#spaceView");
        console.log('that.data.protocol_isFirst', this.data.protocol_isFirst)
        // goCartView = this.selectComponent("#goCartView");
        // goCartView.getPosition();
        console.log(app.globalData.location);
        if (app.globalData.location.pkey) {
            this.setData({
                location: app.globalData.location
            });
        }
        this.getMemberData();
        // this.judgeCouponOver();
        this.getGtype();
        this.getImageList();
        this.getLikeGoodsList();
        this.getMiaoShaList();
        this.getSystemInfo();
        this.loadCouponData();
        this.judgeNewCoupon();
        this.getRecipe()
        this.getBoutiqueVendor();
        this.getDeliveryTime();
        this.getDisplayName()
        this.getZoneGoodsList()
        app.getBuycarNum();
        console.log("app.globalData.location.pkey", app.globalData.location.pkey)
        this.setData({
            AppInfo: app.globalData.AppInfo
        })
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {
        let show = this.data.show;
        for (let i = 0; i < show.length; i++) {
            show[i] = !1;
        }
        this.setData({
            show,
            isShow: false,
            page: 0,

        });

    },
    handleProtocalAgree() {
        console.log("this.data.isLoginClick", this.data.isLoginClick)
        wx.setStorageSync('protocol_check', true);
        wx.setStorageSync('protocol_isFirst', false);
        app.globalData.protocol_check = wx.getStorageSync('protocol_check');
        this.setData({
            protocolShow: false,
            protocol_check: app.globalData.protocol_check
        });
        // 不是点击登录按钮
        if (!this.data.isLoginClick) {
            this.getLocal();
        }
    },
    //获取手机号码
    getPhoneNumber(e) {
        var that = this;
        wx.login({
            success: res => {
                if (!res.code) return wx.showToast({
                    title: '登录失败',
                });
                // 获取到用户的 code 之后：res.code
                // console.log("用户的code:" + res.code);
                //获取在setUserInfo方法中获取的用户信息并赋值给params 变量
                var params = {};
                params["wxcode"] = res.code;
                params["sign"] = "USER";
                params["ascription"] = app.globalData.ascription;
                wx.request({
                    method: "GET",
                    url: app.globalData.ajax_url + "/v1/wx/getOpenidByCode",
                    data: params,
                    header: {
                        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
                    },
                    success: res => {
                        var result = res;
                        wx.getUserInfo({
                            success: function (res) {
                                // 用户已经授权过,不需要显示授权页面,所以不需要改变 isHide 的值
                                // 根据自己的需求有其他操作再补充
                                // 我这里实现的是在用户授权成功后，调用微信的 wx.login 接口，从而获取code
                                var userinfo = res.userInfo;
                                userinfo.iv = res.iv;
                                userinfo.encryptedData = res.encryptedData;
                                // console.log("userinfo", userinfo)
                                // console.log("userinfo", userinfo)
                                userinfo.openid = result.data.result.openid;
                                userinfo.session_key = result.data.result.session_key;
                                wx.setStorageSync('openid', result.data.result.openid);
                                wx.setStorageSync('session_key', result.data.result.session_key);
                                //获取手机
                                var params = userinfo,
                                    tjrOpenid = wx.getStorageSync('tjrOpenid');
                                console.log("params", params)

                                if (e) {
                                    if (e.detail.errMsg === "getPhoneNumber:ok") {
                                        params.iv = e.detail.iv;
                                        params.encryptedData = e.detail.encryptedData;
                                    } else {
                                        return
                                    }
                                }
                                if (tjrOpenid) {
                                    params.tjrOpenid = tjrOpenid
                                }
                                console.log("e", e)
                                console.log(e.detail.iv, e.detail.encryptedData)
                                console.log("tel", params)
                                const tjv = wx.getStorageSync('tjv') // 用户是否通商户码扫码进入
                                if (tjv) params.tjv = tjv
                                wx.showLoading({
                                    title: '正在登录中'
                                });
                                http.request({
                                    method: "POST",
                                    url: app.globalData.ajax_url + "/v1/wx/auth/phone",
                                    data: params,
                                    header: {
                                        'content-type': 'application/json;charset=UTF-8',
                                        "farmer": app.globalData.location.pkey
                                    },
                                    success: res => {
                                        var result = res;
                                        console.log("res_phone", res.data)
                                        wx.setStorageSync('openid', result.data.data.openid1);
                                        wx.setStorageSync('userinfo', result.data.data);
                                        app.globalData.userinfo = wx.getStorageSync('userinfo');
                                        app.globalData.openid = wx.getStorageSync('openid');
                                        that.getMemberData();
                                        wx.hideLoading();
                                    }
                                });
                                // end 获取手机
                            }
                        })
                    }
                });
            }
        });

    },
    handleProtocolDisagree() {
        wx.setStorageSync('protocol_check', false);
        wx.setStorageSync('protocol_isFirst', false);
        app.globalData.protocol_check = wx.getStorageSync('protocol_check');
        app.globalData.protocol_isFirst = wx.getStorageSync('protocol_isFirst');
        console.log("this.data.isLoginClick", this.data.isLoginClick, app.globalData.protocol_check)
        this.setData({
            protocolShow: false,
            protocol_check: wx.getStorageSync('protocol_check'),
            protocol_isFirst: wx.getStorageSync('protocol_isFirst'),
        });
        // 不是登录按钮点击进入
        if (!this.data.isLoginClick) {
            this.getLocal();
        }
    },
    handleProtocolShow() {
        this.setData({
            protocolShow: true,
            isLoginClick: true
        })
    },
    getDisplayName() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
            success: (res) => {
                this.setData({
                    displayName: res.data.result
                })
            }
        })
    },
    getZoneGoodsList() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/index/zone/goods/list",
            success: (res) => {
                console.log(res);
                this.setData({
                    zoneGoodsList: res.data.result
                })
            }
        })
    },
    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {},

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
