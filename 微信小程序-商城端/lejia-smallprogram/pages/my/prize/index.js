// pages/my/prize/index.js
let app = getApp();
import http from '../../../utils/http'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    page: 0,
    pagesize: 10,
    datalist: [],
    addr: '', //收货地址
    addrPkey: '', //发货的奖品pkey
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");

  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  loadData: function () {
    var that = this,
      url = "/v1/app/market/lm/member/get/draw",
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
  //选择地址
  goAdress(e) {
    this.setData({
      addrPkey: e.currentTarget.dataset.pkey
    })
    wx.navigateTo({
      url: `/pages/pay/addr/index?isPrize=${!0}`,
    })
  },
  chooseAdress() {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/lm/draw/insAddr",
      data: {
        addr: this.data.addr,
        pkey: this.data.addrPkey
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
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
        if(res.data.success){
          wx.showToast({
            title: '填写成功',
            icon:'none'
          })
          that.setData({
            page:0
          })
          that.loadData()
        }else{
          
        }
    
      }
    });
    this.setData({
      addrPkey:''
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
    this.loadData();
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
  onLoadFun: function () {}
})