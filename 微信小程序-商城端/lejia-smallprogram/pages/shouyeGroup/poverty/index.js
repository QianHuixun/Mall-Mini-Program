// pages/shouyeGroup/poverty/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    page: 0,
    pagesize: 6,
    goodslist: [],
    imageList: [],
    height: wx.getSystemInfoSync().windowWidth * 0.49 * 0.9,
  },

  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_POVERTY_ALLEVIATION"
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/img/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          var imageList = res.data.result.map(item => {
            if (item.urlType == "NOT_URL") {
              item.url = "";
            } else if (item.urlType == "LINK") {
              item.url = item.objKey;
            } else if (item.urlType == "POINTS_MALL") {
              item.url = "/pages/home/integral/index";
            } else if (item.urlType == "MEMBERSHIP") {
              item.url = "/pages/my/openVip/index";
            } else if (item.urlType == "GOODS") {
              item.url = "/pages/shouyeGroup/goodsDeatil/index?pkey=" + item.objKey;
            }else if (item.urlType == "ACTIVITY") {
              item.url = "/pages/activity/coupon/index?pkey=" + item.objKey;
            }
            return item;
          });
          _this.setData({
            imageList: imageList
          });
        }
      },
    })
  },
  /**轮播广告点击事件 */
  goAds: function (data) {
    onClickEffect(data)
  },
  /**
   * 获取商品列表
   */
  loadData: function () {
    var that = this,
      url = "/v1/app/market/goods/query";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        mType: "POVERTY_ALLEVIATION_GOODS",
        page: that.data.page,
        pagesize: that.data.pagesize
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          var goodsList = res.data.result.content;
          if (that.data.page == 0) {
            that.setData({
              goodslist: goodsList,
              page: ++that.data.page
            });
          } else {
            that.setData({
              goodslist: that.data.goodslist.concat(goodsList),
              page: ++that.data.page
            });
          }
          res.data.result.curPage = that.data.page;
          loadMoreView.loadMoreComplete(res.data);
        }
      }
    });
  },

  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.loadData();
    this.getImageList();
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
})