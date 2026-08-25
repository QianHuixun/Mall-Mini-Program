// pages/shouyeGroup/goodsDeatil/index.js

const app = getApp()
import http from '../../../utils/http';
const {
    applyTheme
} = require('../../../utils/themeMixin')
Page({

    /**
     * 页面的初始数据
     */
    data: {
        totalNum: false, // 当前商品在购物车中的数量，如果是多规格或者没加入购物车则返回false
        goodPkey: "", //商品主键
        swiperCurre: 1, //当前轮播页
        swipeTotal: 3, //总共轮播页
        goodsInfo: {},
        content: [], //商品正文 
        isAuto: true,
        iShidden: true,
        // selectSpaces: {
        //     pkey: "",
        //     price: "",
        //     priceOld: "",
        //     priceBig: "",
        //     priceMember: "",
        //     priceSmall: "",
        //     priceOldBig: "",
        //     priceOldSmall: "",
        //     kcNum: "",
        //     comm: ""
        // },
        isIphoneX: false,
        tjr: '',
        time: 0,
        timeData: {
            days: 0,
            hours: 0,
            seconds: 0
        },
        num: 1,
        orderPkey: 0,
        hasShare: true,
        userInfo: app.globalData.userinfo,
        shareFarm: '', //分享的市场
        gwcNum: app.globalData.buycarNum,
        isMember: false,
        marketType: app.globalData.location.marketType,
        isTabChanging: false, // 初始状态为未切换
        activeTab: 0,
        serviceContent: null,   // 服务内容
        /**商品规格选择Start */
        specs: {},
        selectedSpecs: {},
        skuList: [],  //所有sku列表
        flexibleColumns: [],
        detailsImgs: null, //详情图片列表
        showSpecsDialog: false,
        specsDialogType: null,
        /**商品规格选择End */
    },
    /**
     * @desc 放大观看图片
     */
    handlePreviewimg(event) {
        wx.previewImage({
            urls: [event.currentTarget.dataset.url],
        })
    },
    /**图片滚动 */
    swiperChange(event) {
        this.setData({
            swiperCurre: event.detail.current + 1
        });
    },
    /**点击轮播图放大 */
    handlePreviewImage(e) {
        const current = e.currentTarget.dataset.url
        const listName = e.currentTarget.dataset.listname
        let urls = this.data.selectedSpecs[listName]
        if (typeof urls == 'string') {
            urls = [urls]
        }
        wx.previewImage({
            current, // 当前显示图片的http链接
            urls // 需要预览的图片http链接列表
        })
    },
    /**跳转到购物车 */
    goCart() {
        wx.navigateTo({
            url: '/pages/shouyeGroup/buyCar/index',
        })
    },
    /**详情信息请求 */
    getDetail(pkey, spacePkey = '') {
        var _this = this;
        var parame = {
                pkey: pkey
            },
            url = '/v1/app/jd/goods/get'
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: parame,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    let result = res.data.result
                    _this.setData({
                        skuList: result,
                        selectedSpecs: result[0]
                    })
                    _this.convertDetailsImgs()
                    _this.getFlexibleColumns()
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
     * 获取获取服务内容
     */
    getServiceContent() {
        const url = '/v1/app/jd/goods/content'
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            success: (res) => {
                if (res.data.success) {
                    this.setData({
                        serviceContent: res.data.result
                    })
                }
            },
        })
    },

    /**
     * 转化
     */
    convertDetailsImgs() {
        const { selectedSpecs } = this.data
        const imgsText = JSON.parse(selectedSpecs.introduceWechat)
        const detailsImgs = imgsText.map(item => {
            return 'https:' + item
        })
        this.setData({
            detailsImgs,
        })
    },

    /**商品规格以及弹窗Start */
    getFlexibleColumns() {
        const flexibleColumns = []
        const item = this.data.skuList[0]
        for (let key in item) {
            if (key.includes('space') && !key.includes('spaceValue')) {
                if (item[key]) {
                    flexibleColumns.push({
                        key,
                        label: item[key]
                    })
                }
            }
        }
        this.setData({
            flexibleColumns
        })
        this.getFlexibleColumnsList()
    },

    getFlexibleColumnsList() {
        const { skuList, flexibleColumns, specs } = this.data
        flexibleColumns.map((columns, index) => {
            let columnsValue = []
            specs[columns.key] = columnsValue
            skuList.map(sku => {
                columnsValue.push({
                    name: sku['spaceValue' + (index + 1)],
                    price: sku.price,
                    pkey: sku.pkey,
                    img: sku.photo1[0]
                })
            })
        })
        this.setData({
            specs,
        })
    },

    handleSpecsDialogShow(data) {
        console.log(data);
        const type = data.currentTarget.dataset.type
        this.setData({
            showSpecsDialog: true,
            specsDialogType: type
        })
    },
    handleSpecsDialogClose() {
        this.setData({
            showSpecsDialog: false
        })
    },
    handleSpecsDialogConfirm(data) {
        const type = this.data.specsDialogType
        const count = data.detail
        switch (type) {
            case 'gwc':
                this.handleAddTOCart(count)
                break;
            case 'buy':
                this.goComboPay(count)
                break;
            default:
                break;
        }
        this.handleSpecsDialogClose()
    },
    handleSpecsChange(data) {
        const specs = data.type == 'change' ? data.detail : data.currentTarget.dataset.item
        const { skuList, selectedSpecs } = this.data
        if(specs.pkey == selectedSpecs.pkey) return
        const found = skuList.find(item => item.pkey == specs.pkey)
        this.setData({
            selectedSpecs: found
        })
        this.convertDetailsImgs()
    },
    /**商品规格弹窗End */

    /**
     * 数量选择 
     */
    stepChange: function (value) {
        this.setData({
            num: value.detail
        });
    },
    /**
     * 添加到购物车
     */
    handleAddTOCart: function (count) {
        var url = "/v1/app/jd/goods/gwc/ins"
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                pkey: this.data.selectedSpecs.pkey,
                goodsNum: count,
                latitude: wx.getStorageSync('latitude'),
                longitude: wx.getStorageSync('longitude'),
            },
            success: (res) => {
                if (res.data.code == "999") {
                    this.setData({
                        iShidden: false
                    })
                    return;
                };
                if (res.data.success) {
                    wx.showToast({
                        title: '已加入购物车',
                        icon: 'none'
                    });
                    app.getBuycarNum();
                    this.setData({
                        gwcNum: this.data.gwcNum + count
                    })
                } else {
                    if (res.data.code == 'lejia-0132') {
                        this.setData({
                            num: 1,
                        });
                    }
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: "none"
                    });
                }
            }
        });
    },

    /**新组合支付页 */
    goComboPay(count) {
        console.log(this.data.selectedSpecs);
        let that = this
        let url = "/v1/app/jd/order/buyGoods"
        let params = {
            space: this.data.selectedSpecs.pkey, //规格pkey
            num: count, //商品数量
            latitude: wx.getStorageSync('latitude'),
            longitude: wx.getStorageSync('longitude'),
        }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
            },
            success: res => {
                if (res.data.code == "999") {
                    that.setData({
                        iShidden: false
                    })
                    return;
                }
                if (res.data.success) {
                    wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
                    wx.navigateTo({
                        url: '/pages/pay/comboPay/index?type=goods&space=' + this.data.selectedSpecs.pkey + '&num=' + count + '&mtype=JD_GOODS'
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
    
    /**
     * 删除收藏
     */
    handleDelete: function () {
        var url = "/v1/app/market/goods/collection/del",
            that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                pkey: this.data.goodsInfo.collectionPkey
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: function (res) {
                if (res.data.success) {
                    that.setData({
                        ["goodsInfo.collection"]: false
                    });
                    wx.showToast({
                        title: '取消收藏',
                        icon: "none"
                    })
                } else {
                    wx.showToast({
                        title: '取消失败',
                        icon: "none"
                    })
                }
            }
        });
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        applyTheme(this)
        this.setData({
            gwcNum: app.globalData.buycarNum,
            ascription: app.globalData.ascription
        })
        let spacePkey = '';
        if (app.globalData.mobileModel.indexOf('iPhone') > -1) {
            this.setData({
                isIphoneX: app.globalData.isIphoneX
            })
        }
        if (options.hasOwnProperty('shareFarm')) {
            this.setData({
                shareFarm: JSON.parse(options.shareFarm)
            });
        }
        this.setData({
            userInfo: app.globalData.userinfo
        });
        if (options.hasOwnProperty('orderPkey')) {
            this.setData({
                orderPkey: parseInt(options.orderPkey)
            });
        }
        if (options.hasOwnProperty('isMember')) {
            this.setData({
                isMember: true
            });
        }
        if (options.hasOwnProperty('space')) {
            spacePkey = options.space
        }
        if (options.q) {
            const q = decodeURIComponent(options.q);
            const pkey = utils.getQueryString(q, 'pkey');
            this.setData({
                goodPkey: pkey
            });
        }
        if (options.pkey) {
            this.setData({
                goodPkey: options.pkey
            });
        }
        this.setData({
            tjr: options.tjr ? options.tjr : ''
        });
        this.getDetail(this.data.goodPkey, spacePkey);
        this.getServiceContent()
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        this.setData({
            marketType: app.globalData.location.marketType
        });
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        if (this.data.isTabChanging) return; // 切换中不执行加载
        if (this.data.hasMore && this.data.activeTab == '1') {
            this.loadComments();
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {
        let shareFarm = {
            name: app.globalData.location.name,
            pkey: app.globalData.location.pkey,
        };
        if (this.data.goodsInfo.mtype == "CUT_GOODS")
            return {
                title: `${app.globalData.userinfo.nickName}正在砍价，快来祝他一臂之力！`,
                path: `/pages/my/bargainDetail/index?pKey=${this.data.orderPkey}&shareFarm=${JSON.stringify(shareFarm)}`,
                imageUrl: this.data.goodsInfo.photo1[0], //用户分享出去的自定义图片大小为5:4,
                success: function (res) {
                    // 转发成功
                    wx.showToast({
                        title: "分享成功",
                        icon: 'success',
                        duration: 2000
                    })
                },
                fail: function (res) {
                    // 分享失败
                },
            }
        else if (this.data.goodsInfo.mtype == "COLLAGE_GOODS") {
            return {
                title: `${app.globalData.userinfo.nickName}正在拼团，一起拼团超值好货！`,
                path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + this.data.goodPkey + '&tjr=' + app.globalData.openid + '&shareFarm=' + JSON.stringify(shareFarm),
                imageUrl: this.data.goodsInfo.wrapperPhoto, //用户分享出去的自定义图片大小为5:4,
                success: function (res) {
                    // 转发成功
                    wx.showToast({
                        title: "分享成功",
                        icon: 'success',
                        duration: 2000
                    })
                },
                fail: function (res) {
                    // 分享失败
                },
            }
        } else {
            return {
                title: this.data.goodsInfo.title,
                path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + this.data.goodPkey + '&tjr=' + app.globalData.openid + '&shareFarm=' + JSON.stringify(shareFarm),
                imageUrl: this.data.goodsInfo.wrapperPhoto, //用户分享出去的自定义图片大小为5:4,
                success: function (res) {
                    // 转发成功
                    wx.showToast({
                        title: "分享成功",
                        icon: 'success',
                        duration: 2000
                    })
                },
                fail: function (res) {
                    // 分享失败
                },
            }
        }

    }
})