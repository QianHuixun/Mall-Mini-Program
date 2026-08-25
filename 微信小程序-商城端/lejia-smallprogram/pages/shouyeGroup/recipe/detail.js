// pages/shouyeGroup/recipe/detail.js
import http from '../../../utils/http'
const app = getApp()
const { applyTheme } = require('../../../utils/themeMixin')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isIphoneX: app.globalData.isIphoneX,
    caipuPkey: "",
    photo1: [],
    collection: false,
    collCount: "",
    name: "",
    descp: "",
    lines: [],
    content: [],
    isAuto: true,
    iShidden: true,
    collectionPkey: ""
  },

  /**详情信息请求 */
  getDetail(pkey) {
    var _this = this;
    var parame = {
      pkey: pkey
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/cookfd/get',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          _this.setData({
            photo1: res.data.result.photo1,
            collection: res.data.result.collection,
            collCount: res.data.result.collCount,
            name: res.data.result.name,
            descp: res.data.result.descp,
            lines: res.data.result.lines,
            content: res.data.result.content,
            collectionPkey: res.data.result.collectionPkey
          });
          wx.setNavigationBarTitle({
            title: res.data.result.name,
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      },
    })
  },

  /**
   * 加入收藏夹
   */
  addCollection: function () {
    var that = this,
      url = "/v1/app/market/goods/collection/ins";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ctype: 0,
        objKey: this.data.caipuPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
            noLogin: true
          })
          return;
        }
        if (res.data.success) {
          that.setData({
            collection: true,
            collCount: parseInt(that.data.collCount) + 1,
            collectionPkey: res.data.result
          });
          wx.showToast({
            title: "菜谱收藏成功"
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
        }
      }
    });

  },

  /**取消收藏 */
  deleteCollection() {
    var that = this,
      url = "/v1/app/market/goods/collection/del";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ctype: 0,
        pkey: this.data.collectionPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
            noLogin: true
          })
          return;
        }
        if (res.data.success) {
          that.setData({
            collection: false,
            collCount: parseInt(that.data.collCount) - 1
          });
          wx.showToast({
            title: "菜谱取消收藏"
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
        }
      }
    });
  },

  /**购物车 */
  carClick() {
    wx.switchTab({
      url: '../../home/buyCar/index',
    });
  },

  /**商品详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.goods,
    });
  },

  //**未开放选择 */
  noClick() {
    wx.showToast({
      title: "暂未开放",
      icon: 'none'
    });
  },
  goCart: function (data) {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/insCp",
      params = {
        pkey: this.data.caipuPkey
      };

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
        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
            noLogin: true
          })
          return;
        }
        if (res.data.success) {
          app.getBuycarNum();
          wx.showToast({
            title: "加入购物车成功"
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
        }
      }
    });
  },
  /**点击加 */
  addClick(event) {
    var id = event.currentTarget.dataset.id;
    console.log(id, "id");
    var list = this.data.lines;
    var item = list[id];
    item.num++;
    this.setData({
      lines: list
    });
  },

  /**点击减 */
  subClick(event) {
    var id = event.currentTarget.dataset.id;
    console.log(id, "id");
    var list = this.data.lines;
    var item = list[id];
    if (item.num == 0) {
      wx.showToast({
        title: "已减至0",
        icon: 'none'
      });
      return;
    }
    item.num--;
    this.setData({
      lines: list
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    applyTheme(this)
    var pkey = options.pkey;
    console.log(pkey, "pkey");
    this.setData({
      caipuPkey: pkey
    });
    this.getDetail(pkey);
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