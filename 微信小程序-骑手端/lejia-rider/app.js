//app.js
App({
  onLaunch: function () {
    this.getAppInfo();
    this.getOpenid();
  },
  globalData: {
    // ajax_url: 'https://www.ywsczxonline.com/zyysc',
    // ajax_url: 'https://ymkt.xinanshizu.com/zyysc',
    // ajax_url: 'http://192.168.128.91:23800',
    // ajax_url: 'https://cloudtest.xinanshizu.com/zyysc', //测试环境
    // ajax_url: 'https://small.xinanshizu.com/zyysc', // 正式环境
    ajax_url: 'https://shop.xbg.bhmssy.com/zyysc', // 天津独立部署
    // ajax_url: 'http://192.168.128.91/zyysc',
    openid: wx.getStorageSync('openid'),
    appid: wx.getStorageSync('appid'),
    isLogin:true, //默认有数据是自动登录的，点击回到主页的时候不要登录
    AppInfo: {},  // 小程序相关信息
    ascription: 13, //正式环境 1: 杭州朱家角; 2: 致一云农贸;wxe548357850084152; 3: 新兴生鲜; 4：567生鲜; 5：又见智慧; 8: 鹿晨市集;wxe548357850084152;9: 念宏农业发展有限责任公司;13;心滨购;wxe8de110c1eb084e2 16: 顺亿捷农贸市场 wxc862790db26d531c; 17：满投烹小鲜新市集：wxcddfe2e63b7b5a0e;
    // ascription: 22, //测试环境  22: 鹿晨市集
  },
  getOpenid: function () {
    // if (this.globalData.openid && this.globalData.session_key) return;
    const that = this;
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
        params["sign"] = "COURIER";
        params["ascription"] = that.globalData.ascription;
        wx.request({
          method: "GET",
          url: that.globalData.ajax_url + "/v1/wx/getOpenidByCode",
          data: params,
          header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
          },
          success: res => {
            if (res.data.result) {
              var result = res;
              wx.setStorageSync('openid', result.data.result.openid);
              wx.setStorageSync('session_key', result.data.result.session_key);
              that.globalData.openid = wx.getStorageSync('openid');
              that.globalData.session_key = wx.getStorageSync('session_key');


            }
          }
        });
      }
    });
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
          title: that.globalData.AppInfo.courierName,
        })
      }
    })
  }
})