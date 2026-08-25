// pages/shouyeGroup/memberZone/index.js
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
const app = getApp();
var spaceView;
// var goCartView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    page: 0,
    goodsList: [],
    loadingBom: false,//是否在加载更多
    noMoreBom: false, //没有更多
    imageList: [],
    gtypeList: [],//分类
    iShidden: true,
    userData:''
  },

  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey+'&isMember=true' ,
    });
  },
  /**
   * 获取会员信息
   */
  getMemberData: function () {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/centre',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        that.setData({
          userData: res.data.result
        });
      }
    })
  },
  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_MEMBER"
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
   * 跳转到vip申请
   */
  goVip: function () {
    wx.navigateTo({
      url: '/pages/my/openVip/index',
    })
  },
  /**商品类别 */
  getGtype() {
    var _this = this;
    var parame = {
      showMarket: true,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/gtype/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          console.log(res.data);
          _this.setData({
            gtypeList: res.data.result
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
  /**获取列表 */
  getList() {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize: 10,
      mType: "MEMBER_GOODS"
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/goods/query/member',
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
   /**
   * 获取当前商品的规格数量
   */
  getSpaceNumber: function (data) {
    var url = "/v1/app/market/goods/space/totalAmount",
      pkey = data.currentTarget.dataset.pkey,
      space = data.currentTarget.dataset.space,
      that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: { pkey: pkey },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.result == 1) {
          that.handleAddTOCart(data, pkey, space);
        } else {
          that.setData({
            isShow: true
          });
          spaceView.getData(pkey);
        }
      }
    })
  },
  /**
     * 添加到购物车
     */
  handleAddTOCart: function (data, pkey, space) {
    var url = "/v1/app/market/lm/member/gwc/ins",
      that = this,
      params;
    if (data.detail.hasOwnProperty('data')) {
      params = {
        goodsPkey: data.detail.goodsPkey,
        goodsNum: 1,
        space: data.detail.space,
      }
      data = data.detail.data
    } else
      params = {
        goodsPkey: pkey,
        goodsNum: 1,
        space: space,
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
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          // goCartView.addshopcar(data);
          app.getBuycarNum();
          wx.showToast({
            title: '已加入购物车',
            icon: "none"
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    spaceView = this.selectComponent("#spaceView");
    // goCartView = this.selectComponent("#goCartView");
    // goCartView.getPosition();
    this.getList();
    this.getImageList();
    this.getGtype();
    this.getMemberData();
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
    console.log("qqqqqq");
    if (this.data.noMoreBom || this.data.loadingBom) {
      return;
    }
    this.setData({
      page: this.data.page + 1,
      loadingBom: true,
      noMoreBom: false
    });
    this.getList();
  },

})