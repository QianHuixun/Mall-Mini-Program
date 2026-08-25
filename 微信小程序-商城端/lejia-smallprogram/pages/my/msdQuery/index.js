// pages/my/pointQuery/index.js
let app = getApp();
import http from '../../../utils/http'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgUrl: app.globalData.file_url,
    isAuto: true,
    show: false,
    cardNumber: "",
    cardPassword: "",
    loading: false,
    customStyle: "width: 425rpx; background: #ebebeb; padding: 12rpx 24rpx; border-radius: 12rpx;",
    iShidden: true,
    balance: 0,
    pagesize: 10,
    page: 0,
    datalist: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if(options.balance) {
        this.setData({
            balance: options.balance
        })
    }
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
      url = "/v1/app/market/lm/member/query/centre/msdLine",
      params = {
        page: this.data.page,
        pagesize: this.data.pagesize
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
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    })
  },
  getBalance: function() {
  const url = "/v1/app/market/lm/member/msd/balance",that = this;

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
              balance: res.data.result
            })
        }       
      }
    })
  },
  /**
   * 跳转到积分商城
   */
  goIntegral: function() {
    wx.navigateTo({
      url: '/pages/home/integral/index',
    })
  },
  /**
   * 跳转到签到
   */
  goSignin: function() {
    wx.navigateTo({
      url: '/pages/my/signIn/index',
    })
  },
  /**
   * 跳转到积分抽奖
   */
  goLottery: function() {
    wx.navigateTo({
      url: '/pages/lottery/lottery/index',
    })
  },
  // 显示卡密充值弹窗
  showPopup() {
    this.setData({ show: true });
},

onClose() {
    this.setData({ 
        show: false,
        loading: false,
        cardNumber: '',
        cardPassword: '',
    });
},

onChange(e) {
  const value= e.detail
  const name = e.currentTarget.dataset.name
  this.setData({
      [name]: value
  })
},

    /**
     * 卡密充值
     */
    handleRechargeCard(e) {
      console.log(e);
      let { cardNumber, cardPassword } = this.data;
      console.log("1111",cardNumber, cardPassword)
      if(!cardNumber) {
          wx.showToast({
            title: '请输入卡号',
            icon: 'error'
          })
          return
      }
      if(!cardPassword) {
          wx.showToast({
            title: '请输入卡密',
            icon: 'error'
          })
          return
      }
      this.setData({
          loading: true
      })
      let url = "/v1/app/market/lm/member/msd/recharge/card";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          cardNumber,
          cardPassword
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
        },
        success: res => {
          if(!res.data.success) {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            })
            this.setData({
                loading: false
            })
            return;
          }
          wx.showToast({
            title: '充值成功',
            icon: 'success'
          })
          this.setData({
            page: 0
          });
          this.loadData();
          this.getBalance();
          this.onClose();
        }
      });
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
    loadMoreView.loadMore()
  },

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.getData();
    this.loadData();
  }
})
