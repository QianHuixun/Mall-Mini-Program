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
    iShidden: true,
    pointData: {},
    pagesize: 10,
    page: 0,
    datalist: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.getData();
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
      url = "/v1/app/market/lm/member/point/line",
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
  /**
   * 获取积分信息
   */
  getData: function() {
    const that = this,
      url = "/v1/app/market/lm/member/point/get";
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
          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };
          if(res.data.success) {
            that.setData({
              pointData: res.data.result
            })
          }else {
            wx.showToast({
              title: res.data.msg || '',
              icon: "none"
            });
          }
        }
      });
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
