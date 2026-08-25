// pages/shouyeGroup/snap/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    dateList: [],
    selectIndex: 0,
    page: 0,
    goodsList: [],
    loadingBom: false, //是否在加载更多
    noMoreBom: false, //没有更多
    imageList: []
  },
  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_SPECIAL"
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
            } else if (item.urlType == "ACTIVITY") {
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
  /**日期选择 */
  tapIndex(event) {
    console.log(event);
    let index = event.currentTarget.dataset.index;
    this.setData({
      page: 0,
      goodsList: [],
      loadingBom: true,
      noMoreBom: false,
      selectIndex: index
    });
    this.getData();
  },

  /**获取特价商品日期范围 */
  getTimeList() {
    var _this = this;
    var parame = {};
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/getSpecialGoodsSellDate',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          console.log(res.data.result);
          _this.setData({
            dateList: res.data.result,
            selectIndex: 2
          });
          _this.getData();
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      },
    })
  },


  /**获取特价商品 */
  getData() {
    var _this = this;
    var date = this.data.dateList[this.data.selectIndex].timeDate;
    var parame = {
      page: this.data.page,
      pagesize: 10,
      mType: "SPECIAL_GOODS",
      date: date
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          console.log(res.data.result);
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

  /**商品搜索加载 */
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
      this.getData();
    }
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
    this.getTimeList();
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

  },

})