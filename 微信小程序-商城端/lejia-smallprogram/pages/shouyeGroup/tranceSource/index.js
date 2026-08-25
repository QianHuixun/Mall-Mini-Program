// pages/shouyeGroup/tranceSource/index.js
import http from '../../../utils/http'
const app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: "0",
    sylistData: [],//溯源列表
    syPage: 0,
    jclistData: [],//检测公示
    jcPage: 0,
    pagesize: 12,
  },

  /**
  * Tab标签页修改事件
  */
  onChange(event) {
    this.setData({
      active: event.detail.name
    });
   this.goTop();
   this.loadData();
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.loadData();
  },
  loadMoreListener: function(e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * 加载数据
   */
  loadData: function () {
    var _this = this; 
    //true获取检测信息列表  false获取溯源信息列表  
    var url = this.data.active=="1" ? "/v1/app/market/ori/test/query" : "/v1/app/market/ori/ven/query",
      params = {
        page: this.data.active=="1" ? this.data.jcPage : this.data.syPage,
        pagesize: this.data.pagesize,
      };
      console.log()
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
          if (_this.data.active == "0") { //溯源公示          
            if(_this.data.syPage == 0 ) {
              _this.setData({
                sylistData: res.data.result.content,
                syPage: ++_this.data.syPage
              });
            } else {   
              _this.setData({
                sylistData: _this.data.sylistData.concat(res.data.result.content),
                syPage: ++_this.data.syPage
              }); 
            } 
            res.data.result.curPage = _this.data.syPage;
            loadMoreView.loadMoreComplete(res.data);   
          } else {//检测公示
            if(_this.data.jcPage == 0 ) {    
              _this.setData({
                jclistData: res.data.result.content,
                jcPage: ++_this.data.jcPage
              });
            } else {
              _this.setData({
                jclistData: _this.data.jclistData.concat(res.data.result.content),
                jcPage: ++_this.data.jcPage
              });
            } 
            res.data.result.curPage = _this.data.jcPage;
            loadMoreView.loadMoreComplete(res.data);
          }
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    });
  }, 
  //回到顶部
  goTop: function (e) {  // 一键回到顶部
    if (wx.pageScrollTo) {
      wx.pageScrollTo({
        scrollTop: 0,
        duration: 0
      })
    } 
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


});

