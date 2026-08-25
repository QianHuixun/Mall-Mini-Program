// pages/my/index.js
var app = getApp();
import http from '../../../utils/http'
const { applyTheme } = require('../../../utils/themeMixin')
Page({

    /**
     * 页面的初始数据
     */
    data: {
        imgUrl: app.globalData.file_url,
        userinfo: app.globalData.userInfo,
        statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
        isAuto: true,
        iShidden: true,
        userData: {},
        DragBallInfo: {
            weekTime: wx.getStorageSync('weekTime') || '',
            dayTime: wx.getStorageSync('dayTime') || '',
            tel: wx.getStorageSync('drag_tel') || ''
        },
        show: false,
        walletName: '钱包',
        paddingBottom: 0,
        orderNumberData: {},//存储订单数量信息
    },
    /**
     * 获取订单数量信息
     */
    getOrderNumber:function() {
      const that = this,
      url = `${app.globalData.ajax_url }/v1/app/market/lm/member/get/orderStatusNum`;
      http.request({
        method: "POST",
        url: url,
        data: {},
        header: {
            'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
            "openid": app.globalData.openid,
            "farmer": app.globalData.location.pkey
        },
        success: res => {
            if (res.data.code == "999") {
                that.setData({
                    iShidden: false
                })
                return;
            };

            that.setData({
                orderNumberData: res.data.result
            });
        }
    })
    },
    /**
     * 获取会员信息
     */
    getData: function () {
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
                        iShidden: false
                    })
                    return;
                };
                // wx.showToast({
                //   title: app.globalData.openid,
                // })
                console.log("res", res.data.result)
                wx.setStorageSync('userStatus', res.data.result.status)
                app.globalData.userStatus = res.data.result.status
                that.setData({
                    userData: res.data.result
                });
            }
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
     * 跳转到签到页面
     */
    goSignIn: function () {
        wx.navigateTo({
            url: '/pages/my/signIn/index'
        })
    },
    /**
     * 跳转到订单页
     */
    goOrder: function () {
        wx.navigateTo({
            url: '/pages/my/order/index'
        })
    },
    /**
     * 跳转到 我的钱包
     */
    goWallet: function () {
        wx.navigateTo({
            url: '/pages/my/wallet/index'
        })
    },
    /**
     * 跳转到 个人信息
     */
    goInfo: function () {
        wx.navigateTo({
            url: '/pages/my/info/index'
        })
    },
    /**
     * 跳转 热力豆查询
     */
    goMsdQuery(data) {
        const balance = data.currentTarget.dataset.balance
        wx.navigateTo({
            url: '/pages/my/msdQuery/index?balance=' + balance
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

    notOpen() {
        wx.showToast({
            title: "暂未开放",
            icon: 'none'
        });
    },
    /**点击联系客服 */
    getServiceData() {
      const that=this;
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v1/app/market/customerService/getInfo',
        data: {
          type: 'MARKET_MALL'
        },
       
        success: function (res) {
          if (res.data.success) {
            that.setData({
              DragBallInfo: res.data.result
            })
          }
        }
      });
    },
    contactService() {
        const DragBall = this.selectComponent('#DragBall');
        console.log(DragBall);
        DragBall.handleClick()
    //   this.getServiceData();
    //     this.setData({
    //         show: true
    //     });
    },
    onClose() {
        this.setData({
            show: false
        })
    },
    handleMobile() {
        wx.makePhoneCall({
            phoneNumber: this.data.DragBallInfo.tel,
        })
  
        this.onClose()
    },
    handleCustomer() {
        console.log("handleCustomer");
        wx.openCustomerServiceChat({
          extInfo: { url:  this.data.DragBallInfo.customerServiceLink},
          corpId: this.data.DragBallInfo.customerServiceId,
          success(res) { /* 处理成功 */
          console.log("handleCustomer2") },
          fail: (err) => { console.error('失败', err) }
        });
    },
    /**
     * 跳转到 申请会员
     */
    goVip: function () {
        wx.navigateTo({
            url: '/pages/my/openVip/index'
        })
    },
    /**
     * 积分扫码消费
     */
    scanCode: function () {
        //   wx.navigateTo({
        //     url: '/pages/my/scanPay/index?pkey=9AA54E226999DE7425C0C8FF388782D6'
        // });
        //   return;
        wx.scanCode({
            onlyFromCamera: true,
            success(res) {
                if (res.errMsg == 'scanCode:ok') {
                    wx.navigateTo({
                        url: '/pages/my/scanPay/index?pkey=' + res.result
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
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        applyTheme(this)
        this.setData({
            ascription: app.globalData.ascription
          })
        console.log('onLoadOptions', options);
        if (options.hasOwnProperty('tjrOpenid')) {
            wx.setStorage({
                data: options.tjrOpenid,
                key: 'tjrOpenid',
            })
        }
        if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
            this.setData({ walletName: 'I DO' })
        }
        if(this.getTabBar()) {this.setData({paddingBottom: 186})}
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
        this.setData({
            iShidden: true
        })
        app.getBuycarNum();
        this.getData();
        this.getOrderNumber();
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

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
        const recommendGoods = this.selectComponent('#recommendGoods')
        recommendGoods.bindscrollbottom()
    },

    //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
    //函数里面执行
    onLoadFun: function () {
        this.getData();
    },
    onAuthClose() {
        this.setData({
            iShidden: true
        })
    },
})