// pages/activity/coupon/index.js
import utils from '../../../utils/util.js';
let app = getApp();
import http from '../../../utils/http';
Page({

    /**
     * 页面的初始数据
     */
    data: {
      isIphoneX: app.globalData.isIphoneX,
      pkey: "6",
      isAuto: true,
      iShidden: true,
      imgUrl: app.globalData.file_url,
      show: false,
      couponData: {}
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const pkey = utils.getQueryString(q, 'pkey');
          this.setData({
            pkey:pkey
          });     
      }
      console.log("options",options)
      if(options.pkey) {
        this.setData({
          pkey:options.pkey
        });   
      }
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
      this.getData();
      wx.updateShareMenu({
        templateInfo: {},
        isPrivateMessage: true,//只是做禁止分享可以不用activityId
        withShareTicket: true,
         })
       //隐藏三个点的分享到朋友圈和转发好友或群的按钮
       wx.hideShareMenu({
         menus: ['shareAppMessage', 'shareTimeline']
      });
    },
    getData(){
      var that = this,
      url = "/v1/app/market/activity/get";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: this.data.pkey
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey,
          "ascription": app.globalData.ascription
        },
        success: res => {
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };

          if(!res.data.success) {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            })
            return;
          }
          that.setData({
            couponData: res.data.result
          });
          wx.setNavigationBarTitle({
            title: that.data.couponData.name || '活动详情',
          })
          if(res.data.result.allowedShare){
            wx.showShareMenu({
               menus: ['shareAppMessage', 'shareTimeline']
            });
          }
        }
        });
    },
    goReceive() {
      var that = this,
      url = "/v1/app/market/lm/activity/join";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: this.data.pkey
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey,
          "ascription": app.globalData.ascription
        },
        success: res => {
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };

          if(!res.data.success) {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            })
            return;
          }
          if(res.data.result) {
            var result = res.data.result;
            wx.requestPayment({
              'timeStamp': result.timeStamp,
              'nonceStr': result.nonceStr,
              'package': result.pack,
              'signType': result.signType,
              'paySign': result.paySign,
              'success': function (res) {
                that.setData({
                  show: true
                })
              },
              'fail': function (res) {
                wx.showToast({
                  title: '支付失败',
                  icon: 'none'
                })
              },
              'complete': function (res) {
               
              }
            })
          } else {
            that.setData({
              show: true
            })
          }
          
        
        }
      });
    },
       //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
  },
  handeBack(){
    wx.switchTab({
      url: '/pages/home/shouye/index'
    });
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

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {
      const that=this;
      // console.log("this.data.couponData", this.data.couponData)
      return {
        imageUrl: that.data.couponData.photo, // 图片 URL
        title: that.data.couponData.name, // 分享的标题
        success: function(res){
          wx.showShareMenu({
            templateInfo: {},
            withShareTicket: true
          })
        }
      };
    }
})