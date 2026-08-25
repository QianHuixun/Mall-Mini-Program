// pages/shouyeGroup/recipe/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    // todayList:[],
    // hotList:[],
    page: 0,
    pagesize: 8,
    caipuList: [],
    imageList: [],
    classList: [],
    ctype: "" //当前分类
  },

  // /**获取今日列表 */
  // getTodayList() {
  //   var _this = this;
  //   var parame = {
  //     page: 0,
  //     pagesize: 3,
  //     recom: true
  //   };
  //   http.request({
  //     method: "POST",
  //     url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/query',
  //     data: parame,
  //     header: {
  //       'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
  //       "openid": app.globalData.openid,
  //       "farmer": app.globalData.location.pkey
  //     },
  //     success: function (res) {
  //       if (res.data.success) {
  //         _this.setData({
  //           todayList: res.data.result.content,
  //         });
  //       } else {
  //         wx.showToast({
  //           title: res.data.msg,
  //           icon: 'none'
  //         });
  //       }
  //     },
  //   })
  // },

  // /**获取热门列表 */
  // getHotList() {
  //   var _this = this;
  //   var parame = {
  //     page: 0,
  //     pagesize: 3,
  //     hot: true
  //   };
  //   http.request({
  //     method: "POST",
  //     url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/query',
  //     data: parame,
  //     header: {
  //       'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
  //       "openid": app.globalData.openid,
  //       "farmer": app.globalData.location.pkey
  //     },
  //     success: function (res) {
  //       if (res.data.success) {
  //         _this.setData({
  //           hotList: res.data.result.content,
  //         });
  //       } else {
  //         wx.showToast({
  //           title: res.data.msg,
  //           icon: 'none'
  //         });
  //       }
  //     },
  //   })
  // },
  /**轮播广告点击事件 */
  goAds: function (data) {
    onClickEffect(data)
  },
  /**
   * 分类点击事件
   */
  handleTab: function (data) {
    console.log(this.data.ctype, data.currentTarget.dataset.pkey)
    var pkey = data.currentTarget.dataset.pkey;
    if (this.data.ctype == pkey)
      return;
    this.setData({
      ctype: pkey,
      page: 0,
      caipuList: []
    });
    this.getGoods();
  },
  /**获取菜谱大全列表 */
  getGoods() {
    var that = this;

    var parame = {
      page: this.data.page,
      pagesize: 10,
    };

    if (this.data.ctype != "") {
      parame.ctype = this.data.ctype
    }
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
          var caipuList = res.data.result.content;
          if (that.data.page == 0) {
            that.setData({
              caipuList: caipuList,
              page: ++that.data.page
            });
          } else {
            that.setData({
              caipuList: that.data.caipuList.concat(caipuList),
              page: ++that.data.page
            });
          }
          res.data.result.curPage = that.data.page;
          loadMoreView.loadMoreComplete(res.data);
        }
      },
    })
  },


  /**搜索 */
  searfocus() {
    wx.navigateTo({
      url: './search',
    });
  },

  /**详情 */
  caipuClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: './detail?pkey=' + item.pkey,
    });
  },

  /**收藏 */
  collectClick() {
    wx.navigateTo({
      url: './collect',
    });
  },
  getClassify: function () {
    var that = this,
      url = "/v1/app/market/goods/cookfd/query/ctype";
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
            classList: res.data.result
          });
        }
      }
    });

  },
  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_COOKFD"
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
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    // this.getTodayList();
    // this.getHotList();
    this.getImageList();
    this.getClassify();
    this.getGoods();
  },
  loadMoreListener: function (e) {
    this.getGoods()
  },
  clickLoadMore: function (e) {
    this.getGoods()
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
  onPullDownRefresh: function () {},

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    loadMoreView.loadMore()
  },

})