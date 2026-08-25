//app.js
App({
    onLaunch: function () {
        this.getAppInfo()
        this.setPromote()
        const deviceInfo = wx.getDeviceInfo()
        let mobileModel = deviceInfo.model;
        // 判断手机型号，作为底部是否抬高的判断
        if (mobileModel.indexOf('iPhone') > -1) {
            if (mobileModel.indexOf('iPhone X') > -1 || mobileModel.indexOf('iPhone 11') > -1 || mobileModel.indexOf('iPhone 12') > -1 ||
                mobileModel.indexOf('iPhone 13') > -1 || mobileModel.indexOf('iPhone 14') > -1 || mobileModel.indexOf('iPhone 15') > -1 ||
                mobileModel.indexOf('unknown') > -1) {
                this.globalData.isIphoneX = true
            }
        }
    },
    globalData: {
        // ajax_url:"https://wxtest.tofocus.cn/lejia",
        // ajax_url: 'http://192.168.128.91:23500',
        // ajax_url: 'https://www.ywsczxonline.com/zyysc',
        // ajax_url:"https://ymkt.xinanshizu.com/zyysc",
        ajax_url:"https://small.xinanshizu.com/zyysc",// 正式
        // ajax_url:"https://shop.xbg.bhmssy.com/zyysc",// 天津独立部署
        // ajax_url: "https://cloudtest.xinanshizu.com/zyysc",// 测试 13110000035 有商品管理 17608459145 无商品管理 840727
        // ajax_url: 'http://192.168.128.91/zyysc',
        openid: wx.getStorageSync('openid'),
        userInfo: wx.getStorageSync('userInfo'),
        isLogin: true, //默认有数据是自动登录的，点击回到主页的时候不要登录
        ascription: 18, /**正式 1: 杭州朱家角; 8: 鹿晨市集;wxa854bf40fa427151; 2: 致一云农贸;wx3094bd4cb64096fc; 3: 新兴生鲜; 4：567生鲜;
                            5：又见智慧; 13;心滨购;wxf190efe393a70f8d;  16: 顺亿捷农贸市场	wx36863ca346a003dd; 17：满投烹小鲜新市集：wx61c1455b4feac54c;
                            18:青枫好市: wx908eea4b047ff874
                        */
        // ascription: 22, // 测试
        isIphoneX: false,
    },
    getAppInfo() {
        const that = this
        wx.request({
            url: that.globalData.ajax_url + "/v1/app/saas/get",
            method: "POST",
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "ascription": that.globalData.ascription,
            },
            success(res) {
                console.log(res)
                that.globalData.AppInfo = res.data.result
                wx.setNavigationBarTitle({
                    title: that.globalData.AppInfo.userName,
                })
                // that.setPromote()
            }
        })
    },
    setPromote() {
        const that = this
        //获取页面配置并进行页面分享配置
        var PageTmp = Page
        Page = function (pageConfig) {
            //1. 获取当前页面路由
            let routerUrl = ""
            wx.onAppRoute(function (res) {
                //app.js中需要在隐式路由中才能用getCurrentPages（）获取到页面路由
                let pages = getCurrentPages(),
                    view = pages[pages.length - 1];
                routerUrl = view.route
            })

            //2. 全局开启分享配置
            pageConfig = Object.assign({
                onShareAppMessage: function () {
                    //根据不同路由设置不同分享内容（微信小程序分享自带参数，如非特例，不需配置分享路径）
                    let shareInfo = {}
                    shareInfo = {
                        title: that.globalData.AppInfo.userName + '商户',
                        path: '/pages/introduce/introduce',
                    }
                    return shareInfo
                }
            }, pageConfig);
            // 配置页面模板
            PageTmp(pageConfig);
        }
    },
})
