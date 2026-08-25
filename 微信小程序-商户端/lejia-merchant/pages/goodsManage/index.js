// pages/goodsManage/index.js
import http from '../../utils/http'
let app = getApp();
var loadMoreView;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        isIphoneX: app.globalData.isIphoneX,
        title: "",
        status: "",
        statusName: "商品状态",
        pagesize: 10,
        page: 0,
        datalist: [],
        statusShow: false,
        columns: ['全部', '在售商品', '已下架', '已售罄'],
        selectItem: {}, //选中的数据
        editShow: false,
        inputData: {
            index: '',
            type: ""
        }
    },

    // 搜索
    searchClick(e) {
        this.setData({
            page: 0,
            title: e.detail.value
        });
        this.loadData();
    },

    // 点击上下架
    statusClick(data) {
        const item = data.currentTarget.dataset.value;
        var that = this,
            url = item.enabled ? "/v4/app/vendor/goods/enable/stop" : "/v4/app/vendor/goods/enable/start",
            params = {
                pkey: item.pkey
            }

        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid
            },
            success: function (res) {
                if (res.data.success) {
                    wx.showToast({
                        title: item.enabled ? '下架成功' : '上架成功',
                        icon: 'none',
                    })
                    that.setData({
                        page: 0
                    });
                    that.loadData();
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none'
                    });
                }
            }
        })
    },

    // 状态搜索点击
    statusSearchClick() {
        this.setData({
            statusShow: true
        });
    },

    // 状态框操作
    cancle() {
        this.setData({
            statusShow: false
        });
    },
    confirm(event) {
        const { value, index } = event.detail;
        this.setData({
            status: index ? index : "",
            statusShow: false,
            statusName: index ? value : "商品状态",
        });
        this.setData({
            page: 0
        });
        this.loadData();
    },

    //库存价格修改
    editClick(data) {
        const item = data.currentTarget.dataset.value;
        this.setData({
            selectItem: item,
            editShow: true
        });
    },
    //价格修改
    priceChange(data) {
        var selectItem = this.data.selectItem;
        selectItem.spaces[data.currentTarget.dataset.id].price = data.detail;
        this.setData({
            selectItem: selectItem
        });
    },
    //原价修改
    priceOldChange(data) {
        var selectItem = this.data.selectItem;
        selectItem.spaces[data.currentTarget.dataset.id].priceOld = data.detail;
        this.setData({
            selectItem: selectItem
        });
    },
    //库存修改
    kcNumChange(data) {
        console.log(data, this.data.selectItem);
        var selectItem = this.data.selectItem;
        selectItem.spaces[data.currentTarget.dataset.id].kcNum = data.detail;
        this.setData({
            selectItem: selectItem
        });
    },
    //库存清零修改
    clearClick(data) {
        console.log(data.currentTarget.dataset.id);
        var selectItem = this.data.selectItem;
        selectItem.spaces[data.currentTarget.dataset.id].kcNum = 0;
        this.setData({
            selectItem: selectItem
        });
    },
    //库存置满修改
    fillClick(data) {
        console.log(data.currentTarget.dataset.id);
        var selectItem = this.data.selectItem;
        selectItem.spaces[data.currentTarget.dataset.id].kcNum = 9999;
        this.setData({
            selectItem: selectItem
        });
        console.log(this.data.selectItem);
    },
    // 库存修改取消
    editCancleClick() {
        this.setData({
            editShow: false
        });
    },
    // 库存修改确定
    editComfirmClick() {
        let isOK = true;
        this.data.selectItem.spaces.forEach(item => {
            if (!item.price || !item.priceOld) {
                isOK = false;
            }
        });

        if (!isOK) {
            wx.showToast({
                title: '请输入商品售价/原价',
                icon: 'none'
            });
            return;
        }


        var that = this,
            url = "/v4/app/vendor/goods/updKcAndPrice"

        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: this.data.selectItem.spaces,
            header: {
                'content-type': 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                if (res.data.success) {
                    that.setData({
                        editShow: false
                    });
                    that.setData({
                        page: 0
                    });
                    that.loadData();
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none'
                    });
                }
            }
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        loadMoreView = this.selectComponent("#loadMoreView");
        this.loadData();
    },

    loadMoreListener: function (e) {
        this.loadData()
    },
    clickLoadMore: function (e) {
        this.loadData()
    },

    loadData: function () {
        var that = this,
            url = "/v4/app/vendor/goods/query",
            params = {
                page: this.data.page,
                pagesize: this.data.pagesize,
                title: this.data.title,
                status: this.data.status
            }

        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid
            },
            success: function (res) {
                if (res.data.success) {

                    if (that.data.page == 0) {
                        that.setData({
                            datalist: res.data.result.content,
                            page: ++that.data.page
                        });
                    } else {
                        that.setData({
                            datalist: that.data.datalist.concat(res.data.result.content),
                            page: ++that.data.page
                        });
                    }
                    res.data.result.curPage = that.data.page;
                    loadMoreView.loadMoreComplete(res.data);
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none'
                    });
                }
            }
        })
    },

    onFocus(event) {
        this.setData({
            inputData: {
                index: event.currentTarget.dataset.id,
                type: event.currentTarget.dataset.value
            }
        });
    },
    onBlur(event) {
        this.setData({
            inputData: {
                index: '',
                type: ''
            }
        });
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {
        loadMoreView.loadMore()
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})