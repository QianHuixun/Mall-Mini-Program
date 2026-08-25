// pages/my/offlineOrder/index.js
let app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
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
  loadData: function() {},
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
  onLoadFun: function () {
  }
})