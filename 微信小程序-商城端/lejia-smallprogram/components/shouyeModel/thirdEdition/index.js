// components/shouyeModel/thirdEdition/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp();
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        statusBarHeight: 0,
        location: null,
        imageList: null,
        timeShow: false,
        deliveryTime: null,
    },

    /**
     * 组件的初始数据
     */
    data: {
        navOpacity: 0,
        comboImageList: null,
        leftImgList: null,
        upRightImgList: null,
        centerImgList: null,
        downRightImgList: null,
        menuList: null,
    },

    /**
     * 组件的方法列表
     */
    methods: {
        // 滚动改变顶部导航栏透明度
        bindscroll(event) {
            const {
                scrollTop
            } = event.detail
            let navOpacity = (scrollTop / 120).toFixed(2)
            if (navOpacity >= 1) navOpacity = 1
            this.setData({
                navOpacity
            })
        },
        //进去选择市场
        addressSelect() {
            wx.navigateTo({
                url: '/pages/shouyeGroup/position/index',
            });
        },
        // 去搜索
        goSearch() {
            wx.navigateTo({
                url: `/pages/shouyeGroup/search/index`,
            });
        },
        // 跳转广告
        goAds(data) {
            onClickEffect(data)
        },
        /**广告列表 */
        getImageList() {
            var _this = this;
            var parame = {
                position: "ADVERT_POSITION_COM"
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
                        console.log('getImageList', res);
                        var leftImgList = [],
                            upRightImgList = [],
                            centerImgList = [],
                            downRightImgList = [];
                        var imageList = res.data.result.map(item => {
                            if (item.urlType == "NOT_URL") {
                                item.url = "";
                            } else if (item.urlType == "LINK") {
                                item.url = item.objKey;
                            } else if (item.urlType == "POINTS_MALL") {
                                item.url = "/pages/home/integral/index";
                            } else if (item.urlType == "MEMBERSHIP") {
                                item.url = "/pages/my/openVip/index";
                            } else if (item.urlType == "GOODS") {
                                item.url = "/pages/shouyeGroup/goodsDeatil/index?pkey=" + item.objKey;
                            } else if (item.urlType == "ACTIVITY") {
                                item.url = "/pages/activity/coupon/index?pkey=" + item.objKey;
                            }
                            switch (item.locationType) {
                                case "LEFT":
                                    leftImgList.push(item)
                                    break;
                                case "UPPERRIGHT":
                                    upRightImgList.push(item)
                                    break;
                                case "CEZONTER":
                                    centerImgList.push(item)
                                    break;
                                case "LOWERRIGHT":
                                    downRightImgList.push(item)
                                    break;
                            }
                            return item;
                        });
                        console.log('getImageList', res, leftImgList);
                        _this.setData({
                            comboImageList: imageList,
                            leftImgList,
                            upRightImgList,
                            centerImgList,
                            downRightImgList,
                        });
                    }
                },
            })
        },
        /**获取功能性菜单 */
        getMenu() {
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/img/funMenuConfig/list',
                data: {},
                header: {
                    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    "openid": app.globalData.openid,
                    "farmer": app.globalData.location.pkey
                },
                success: (res) => {
                    if (res.data.success) {
                        const list = res.data.result
                        this.setData({
                            menuList: list
                        })
                    }
                },
            })
        },
        handleClick(e) {
            console.log(e);
        },
        getData() {
            const spaceView = this.selectComponent("#classBar");
            if (spaceView) spaceView.getData()
            this.getImageList()
            this.getMenu()
        }
    },
    pageLifetimes: {
        show() {
            const spaceView = this.selectComponent("#classBar");
            if (spaceView) spaceView.getData()
            this.getImageList()
            this.getMenu()
        }
    },
})