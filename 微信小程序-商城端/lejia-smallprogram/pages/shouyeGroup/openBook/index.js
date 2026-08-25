// pages/shouyeGroup/openBook/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: 0,
    willpage: 0,
    willgoodsList: [],
    willloadingBom: false, //是否在加载更多
    willnoMoreBom: false, //没有更多
    // height: wx.getSystemInfoSync().windowWidth * 0.49 * 0.9,
    nowpage: 0,
    nowgoodsList: [],
    nowloadingBom: false, //是否在加载更多
    nownoMoreBom: false, //没有更多
    lastGoodsPkey: '',  // 点击进页面的商品pkey，置顶该商品
    imageList: [] //顶部广告图
  },

  /**切换 */
  onChangeTab(event) {
    this.setData({
      active: event.detail.index
    });
  },

  /**获取即将列表 */
  getWillList() {
    var _this = this;
    var parame = {
      page: this.data.willpage,
      pagesize: 10,
      mType: "INTEGRAL_PRESALE_GOODS"
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
          var willnoMoreBom = false;
          var list = _this.data.willgoodsList.concat(res.data.result.content);
          console.log(list.length, "list.length");
          if (list.length == res.data.result.total) {
            willnoMoreBom = true;
          }
          _this.setData({
            willgoodsList: list,
            willloadingBom: false,
            willnoMoreBom: willnoMoreBom
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

  /**获取正在列表 */
  getNowList() {
    var _this = this;
    var parame = {
      page: this.data.nowpage,
      pagesize: 10,
      isOnPresale: true,
      mType: "INTEGRAL_PRESALE_GOODS",
      topGoods: _this.data.lastGoodsPkey || '',
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
          var nownoMoreBom = false;
          var list = _this.data.nowgoodsList.concat(res.data.result.content);
          console.log(list.length, "list.length");
          if (list.length == res.data.result.total) {
            nownoMoreBom = true;
          }
          _this.setData({
            nowgoodsList: list,
            nowloadingBom: false,
            nownoMoreBom: nownoMoreBom
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

  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_SALE"
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
        } else {

        }
      },
    })
  },
  /**轮播广告点击事件 */
  goAds: function (data) {
    onClickEffect(data)
  },

  //倒计时
  onChange(e) {
    var list = this.data.nowgoodsList;
    list[e.currentTarget.dataset.id].remark = e.detail;
    this.setData({
      nowgoodsList: list,
    });
  },


  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
    });
  },
  getDisPlayName() {
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
      success: (res) => {
        const data = res.data.result
        if(data.integralPresaleDisplayName) {
            wx.setNavigationBarTitle({
                title: data.integralPresaleDisplayName,
            })
        }
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getDisPlayName()
    this.setData({
        lastGoodsPkey: options.pkey != 'undefined' ? options.pkey: ''
    })
    this.getImageList();
    this.getWillList();
    this.getNowList();
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
    console.log("qqqqqq", this.data.active);
    if (this.data.active == 0) {
      if (this.data.nownoMoreBom || this.data.nowloadingBom) {
        return;
      }
      this.setData({
        nowpage: this.data.nowpage + 1,
        nowloadingBom: true,
        nownoMoreBom: false
      });
      this.getNowList();
    } else {
      if (this.data.willnoMoreBom || this.data.willloadingBom) {
        return;
      }
      this.setData({
        willpage: this.data.willpage + 1,
        willloadingBom: true,
        willnoMoreBom: false
      });
      this.getWillList();
    }

  },

  
})