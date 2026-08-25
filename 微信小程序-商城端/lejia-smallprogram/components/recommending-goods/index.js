// components/recommending-goods/index.js
const app = getApp();
var loadMoreView;
import http from '../../utils/http'
const { applyTheme } = require('../../utils/themeMixin')
Component({
    lifetimes: {
        created() {
            applyTheme(this)
            loadMoreView = this.selectComponent("#loadMoreView");
        }
    },
    pageLifetimes: {
        show: function () {
            // 页面被展示
            this.getData()
        },
    },
    /**
     * 组件的属性列表
     */
    properties: {
        zone: {
            type: String,
            value: ''
        },
        title: {
            type: Boolean,
            value: true
        },
    },

    /**
     * 组件的初始数据
     */
    data: {
        loading: false,
        page: 1,
        pagesize: 10,
        list: [],
        isShow: false, //规格弹出显示
        iShidden: true,
    },

    /**
     * 组件的方法列表
     */
    methods: {
        getData() {
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/goods/recommend/query',
                data: {
                    zone: this.properties.zone,
                    page: this.data.page - 1,
                    pagesize: this.data.pagesize,
                },
                success: (res) => {
                    console.log('recommendQuery:', res);
                    if (res.data.result.first) {
                        this.setData({
                            list: res.data.result.content
                        })
                        if(!this.data.list.length) {
                            this.triggerEvent('nodata')
                        }
                    } else {
                        this.setData({
                            list: this.data.list.concat(res.data.result.content)
                        })
                    }
                    if(!loadMoreView) loadMoreView = this.selectComponent("#loadMoreView");
                    loadMoreView.loadMoreComplete(res.data);
                }
            })
        },
        loadMoreListener: function (e) {
            this.getData()
        },
        clickLoadMore: function (e) {
            this.getData()
        },
        bindscrollbottom() {
            console.log('bindscrollbottom');
            this.setData({
                page: this.data.page++
            })
            if(!loadMoreView) loadMoreView = this.selectComponent("#loadMoreView");
            loadMoreView.loadMore()
        },
        goodsClick(data) {
            wx.navigateTo({
                url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${data.currentTarget.dataset.pkey}`
            })
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
                    console.log(res);
                    if (res.data.result == 1) {
                        that.handleAddTOCart(data, pkey, space);
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
        handleAddTOCart: function (data, pkey, space) {
            let params;
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
            var url = "/v1/app/market/lm/member/gwc/ins",
                that = this;
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
                            iShidden: false
                        })
                        return;
                    };
                    if (res.data.success) {
                        app.getBuycarNum();
                        const {
                            list
                        } = that.data
                        const index = list.findIndex(item => item.pkey === pkey)
                        console.log(index);
                        list[index].gwcNum++
                        that.setData({
                            list
                        })
                    } else {
                        wx.showToast({
                            title: res.data.msg || '',
                            icon: "none"
                        });
                    }
                    that.triggerEvent('refresh')
                }
            });
        },
    }
})