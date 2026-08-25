//app.js
App({
  onLaunch: function () {
    this.getAppInfo()
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    wx.setStorageSync('protocol_isFirst', true)
    this.globalData.protocol_isFirst = wx.getStorageSync('protocol_isFirst');
    wx.getSystemInfo({
      success: res => {
        let mobileModel = res.model;
        wx.setStorageSync('mobileModel', mobileModel);
        this.globalData.mobileModel=mobileModel;
        // 判断手机型号，作为底部是否抬高的判断
        if (mobileModel.indexOf('iPhone') > -1) {
          if (mobileModel.indexOf('iPhone X') > -1 || mobileModel.indexOf('iPhone 11') > -1 || mobileModel.indexOf('iPhone 12') > -1 ||
              mobileModel.indexOf('iPhone 13') > -1 ||  mobileModel.indexOf('iPhone 14') > -1 || mobileModel.indexOf('iPhone 15') > -1 ||
              mobileModel.indexOf('iPhone 16') > -1 ||
              mobileModel.indexOf('unknown') > -1) {
            this.globalData.isIphoneX = true
          }
        }
      }
    })
    if (wx.canIUse('getUpdateManager')) {
      const updateManager = wx.getUpdateManager();
      updateManager.onCheckForUpdate(function (res) {
        console.log('onCheckForUpdate====', res)
        // 请求完新版本信息的回调
        if (res.hasUpdate) {
          console.log('res.hasUpdate====')
          updateManager.onUpdateReady(function () {
            wx.showModal({
              title: '更新提示',
              content: '新版本已经准备好，是否重启应用？',
              success: function (res) {
                console.log('success====', res)
                if (res.confirm) {
                  updateManager.applyUpdate()
                }
              }
            })
          })
          updateManager.onUpdateFailed(function () {
            // 新的版本下载失败
            wx.showModal({
              title: '已经有新版本了哟~',
              content: '新版本已经上线啦~，请您删除当前小程序，重新搜索打开哟~'
            })
          })
        }
      })
    }
    this.getOpenid();
    this.getAppid()
    this.getPromote()
    if (this.globalData.openid && this.globalData.session_key)
      this.getBuycarNum()
      this.getBuycarPrice()
  },
  onShow(options) {
    console.log(this);
    console.log('app options', options);
    const pages = getCurrentPages(); // 获取当前页面栈
    if (pages.length > 0) {
      const currentPage = pages[pages.length - 1]; // 获取当前页面实例
      if (currentPage.onAppShow) {
        currentPage.onAppShow(options); // 调用页面的自定义方法
      }
    }
    this.getPromote()
    this.setPromote()
  },
  onShareAppMessage: function () {
    return {
      title: "官方直营免配送，线上线下买菜同价",
      imageUrl: this.globalData.file_url + 'share_back.png',
    }
  },
  globalData: {
    userinfo: wx.getStorageSync('userInfo'),
    openid: wx.getStorageSync('openid'),
    appid: wx.getStorageSync('appid'),
    session_key: wx.getStorageSync('session_key'),
    protocol_check: wx.getStorageSync('protocol_check') || false, // 用户协议是否同意
    protocol_isFirst:wx.getStorageSync('protocol_isFirst') || false,// 是否第一次进入首页，用于判断当用户协议不同意时，不重新弹出用户协议
    // ajax_url: 'https://wxtest.tofocus.cn/zyysc',
    // ajax_url: 'https://ymkt.xinanshizu.com/zyysc',
    // ajax_url: 'https://www.ywsczxonline.com/zyysc',  // 义乌商城
    // ajax_url: 'https://shop.xbg.bhmssy.com/zyysc',// 心滨购 独立部署
    ajax_url: 'https://cloudtest.xinanshizu.com/zyysc', //测试环境
    // ajax_url: "http://172.30.10.31:23505",// 达伟本地服务
    // ajax_url: 'https://small.xinanshizu.com/zyysc', //正式环境
    file_url: 'https://file.xinanshizu.com/public/images/wxMall/',// 文件服务 用于保存大的图片
    // cardWhiteOff_url: 'https://small.xinanshizu.com/cardWhiteOff_test', //测试环境,÷礼品券核销地址
    cardWhiteOff_url: 'https://small.xinanshizu.com/cardWhiteOff', //正式环境
    // cardWhiteOff_url: 'https://shop.xbg.bhmssy.com/cardWhiteOff', //正式环境 心滨购 独立部署
    isIphoneX: false,
    location: {
      pkey: wx.getStorageSync('location_pkey'),
      name: wx.getStorageSync('location_name'),
      district: wx.getStorageSync('location_district'),
      address: wx.getStorageSync('location_address'),
      tel: wx.getStorageSync('location_tel'),
      marketType:  wx.getStorageSync('location_type'),
    },
    buycarNum: 0,
    buycarPrice: 0,
    shareInfo: {
      title: "泽起邻里，红包福利戳不停~",
      imageUrl: 'https://file.xinanshizu.com/public/images/wxMall/share_back.png',
    },
    AppInfo: {},
    version: 13, //现已不用修改，废弃。 因小程序营业执照没有对应类目，所以上线前需要下架商品，操作麻烦，所以增加版本号，在审核后更改运营端的版本号来控制显示。
    // ascription: 13, 
    /**正式环境 1: 杭州朱家角; 2: 致一云农贸;wxe548357850084152; 3: 新兴生鲜; 4：567生鲜; 5：又见智慧;
                        8: 鹿晨市集;wxf8e8ea188dd9d52c;9: 念宏农业发展有限责任公司; wx4009bfc1dda4beea;10: 良橙吉市-禾泽优选;wxce773703c2a30fe6;
                        14: 楞上农贸市场线上商城;wxe24e08971e6abebf; 13;心滨购;wxe8e4269393cd1295; 15:金渝来优选;wx93f18abc19c71451; 
                        17：满投烹小鲜新市集：wx7554f21b3bc5a5c6; 18:青枫好市: wxcb411f8bd5eebf15；19:青海互助生鲜市集: wxd4465311616191cf
                    */
    ascription: 22, //测试环境  22: 鹿晨市集 
    qrCode: "", //餐桌桌面二维码pkeys
    source: wx.getStorageSync("source") || "",// 用于统计扫码来源
    indexModel: 3, // 首页模板设置  1 || 2 || 3  1:普通商城模板； 2:天津定制模板; 3:天津定制模板
    theme: 'custom',  // 默认主题色
    themeColors: {
        basics: {
            name: '基础',
            value: 'basics',
            primary: '#0DAE4E',
            background: '#25C958',
            buttonDangerBackgroundColor: '#0DAE4E',
            buttonDefaultBackgroundColor: '#0DAE4E',
            stepperBackgroundColor: '#0DAE4E',
            iconColor: '#0DAE4E',
            tagColor: '#4dce78',
            tagSuccessColor: '#0DAE4E',
        },
        custom: {
            name: '定制',
            value: 'custom',
            primary: '#09AF84',
            background: '#08BB8D',
            buttonDangerBackgroundColor: '#09AF84',
            buttonDefaultBackgroundColor: '#09AF84',
            stepperBackgroundColor: '#09AF84',
            iconColor: '#09AF84',
            tagColor: '#09AF84',
            tagSuccessColor: '#09AF84',
        }
    }
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
        params["sign"] = "USER";
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
              this.getBuycarNum()
              this.getBuycarPrice()


            }
          }
        });
      }
    });
  },
  getAppid: function () {
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
        params["sign"] = "USER";
        params["ascription"] = that.globalData.ascription;
        wx.request({
          method: "GET",
          url: that.globalData.ajax_url + "/v1/wx/getAppid",
          data: params,
          header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
          },
          success: res => {
            if (res.data.result) {
              var result = res;
              wx.setStorageSync('appid', result.data.result);
            }
          }
        });
      }
    });
  },
  watch(key, method) {
    var obj = this.globalData;
		//加个前缀生成隐藏变量，防止死循环发生
		let ori = obj[key]; //obj[key]这个不能放在Object.defineProperty里
		if (ori) { //处理已经声明的变量，绑定处理
			method(ori);
		}
		Object.defineProperty(obj, key, {
			configurable: true,
			enumerable: true,
			set: function(value) {
				this['_' + key] = value;
				console.log('是否会被执行2')
				method(value);
			},
			get: function() {
				// 在其他界面调用key值的时候，这里就会执行。
				if (typeof this['_' + key] == 'undefined') {
					if (ori) {
						//这里读取数据的时候隐藏变量和 globalData设置不一样，所以要做同步处理
						this['_' + key] = ori;
						return ori;
					} else {
						return undefined;
					}
				} else {
					return this['_' + key];
				}
			}
		})
  },
  getBuycarNum() {
    let that = this
    wx.request({
      method: "POST",
      url: this.globalData.ajax_url + "/v1/app/market/lm/member/gwc/get/gwc/goods/num",
      data: '',
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "appid": this.globalData.appid,
        "openid": this.globalData.openid,
        "farmer": this.globalData.location.pkey,
        ascription: this.globalData.ascription
      },
      success(res) {
        that.globalData.buycarNum = res.data.result
        // that.bus.emit('paramName', that.globalData.buycarNum)  
        if (res.data.result) {
          wx.setTabBarBadge({
            index: 3,
            text: res.data.result + ""
          });
        } else
          wx.hideTabBarRedDot({
            index: 3,
          });
      }
    })
  },
  getBuycarPrice() {
    let that = this
    wx.request({
      method: "POST",
      url: this.globalData.ajax_url + "/v1/app/market/lm/member/gwc/get/gwc/goods/price",
      data: '',
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "appid": this.globalData.appid,
        "openid": this.globalData.openid,
        "farmer": this.globalData.location.pkey,
        ascription: this.globalData.ascription
      },
      success(res) {
        console.log('buucarPrice', res);
        that.globalData.buycarPrice = res.data.result
      }
    })
  },
  getPromote() {
    const that = this
    wx.request({
      method: "POST",                                                         
      url: that.globalData.ajax_url + '/v3/app/promote/get',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "ascription": that.globalData.ascription,
        "openid": that.globalData.openid,
        "farmer": that.globalData.location.pkey
      },
      success: res => {
        console.log(res)
        if(!res.data.result?.pkey) {
          that.globalData.shareInfo = {
            content: `${that.globalData.AppInfo.userName}，红包福利戳不停~`,
            photo: that.globalData.file_url + 'share_back.png',
          }
        } else {
          that.globalData.shareInfo = res.data.result
        }
      }
    })
  },
  setPromote () {
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
          let shareInfo={}
          shareInfo = {
            title: that.globalData.shareInfo.content,
            imageUrl: that.globalData.shareInfo.photo
          }
          return shareInfo
        }
      }, pageConfig);
      // 配置页面模板
      PageTmp(pageConfig);
    }
  },
  getAppInfo() {
    const that = this
    wx.request({
      url: that.globalData.ajax_url + "/v1/app/saas/get",
      method: "POST",
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "ascription": that.globalData.ascription,
        "openid": that.globalData.openid,
        "farmer": that.globalData.location.pkey
      },
      success(res) {
        console.log(res)
        that.globalData.AppInfo = res.data.result
        wx.setNavigationBarTitle({
          title: res.data.result.userName
        })
      }
    })
  }
})

