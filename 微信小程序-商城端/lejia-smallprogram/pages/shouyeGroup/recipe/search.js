// pages/shouyeGroup/recipe/search.js
import http from '../../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    value: "",
    isShowGood: false,
    hotList:[],
    page: 0,
    goodsList: [],
    loadingBom: false,//是否在加载更多
    noMoreBom: false //没有更多
  },

  /**获取热门列表 */
  getHotList() {
    var _this = this;
    var parame = {
      page: 0,
      pagesize: 3,
      hot: true
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          _this.setData({
            hotList: res.data.result.content,
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      },
    })
  },

  /**热门搜索点击 */
  hotClick(event) {
    var item = event.currentTarget.dataset.id;
    this.setData({
      value: item.name,
      isShowGood: true,
      page: 0,
      goodsList: [],
      loadingBom: false,
      noMoreBom: false
    });
    this.getGoods(item.name);
  },

  /**获取菜谱列表 */
  getGoods(val) {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize: 10,
      name: val
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          var noMoreBom = false;
          var list = _this.data.goodsList.concat(res.data.result.content);
          console.log(list.length, "list.length");
          if (list.length == res.data.result.total) {
            noMoreBom = true;
          }
          _this.setData({
            goodsList: list,
            loadingBom: false,
            noMoreBom: noMoreBom
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      },
    })
  },

  /**菜谱搜索加载 */
  bindscrollbottom(eventhandle) {
    if (eventhandle.detail.direction == "bottom") {
      console.log("bottom");
      if (this.data.noMoreBom || this.data.loadingBom) {
        return;
      }
      this.setData({
        page: this.data.page + 1,
        loadingBom: true,
        noMoreBom: false
      });
      this.getGoods(this.data.value);
    }
  },

  /**点击取消 */
  onCancel() {
    console.log("点击取消");
    if (this.data.isShowGood) {
      this.setData({
        value: "",
        isShowGood: false
      });
    } else {
      wx.navigateBack();
    }
  },

  /**搜索值变化时 */
  onChange(e) {
    let isShowGood = false
    if (e.detail) {
      isShowGood = true;
    }
    this.setData({
      value: e.detail,
      isShowGood: isShowGood,
      page: 0,
      goodsList: [],
      loadingBom: false,
      noMoreBom: false
    });
    console.log(e);
    this.getGoods(e.detail);
  },

  /**菜谱点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: './detail?pkey=' + item.pkey,
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getHotList();
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

  },

})