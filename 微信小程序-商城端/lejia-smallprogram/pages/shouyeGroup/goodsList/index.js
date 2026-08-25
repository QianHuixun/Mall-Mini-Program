// pages/shouyeGroup/goodsList/index.js
import http from '../../../utils/http'
const app = getApp();
var loadMoreView;
var spaceView;
// var goCartView; 
Page({

  /**
   * 页面的初始数据
   */
  data: {
    option1: [
      { text: '销量', value: "0" },
      { text: '销量从高到低', value: "1" },
      { text: '销量从低到高', value: "2" },
    ],
    option2: [
      { text: '价格', value: '0' },
      { text: '价格从高到低', value: "1" },
      { text: '价格从低到高', value: "2" },
    ],
    hotSort: "0",//
    priceSort: "0",//按价格排序
    mType: '',//商品属性
    gtype: "",//分类pkey
    page: 0,
    pagesize: 10,
    goodsList: [],
    iShidden: true
  },
  onHotChange: function (value) {
    this.setData({
      hotSort: value.detail,
      priceSort: "0",
      page: 0,
      goodsList: []
    });
    this.loadData();
  },
  onPriceChange: function (value) {
    this.setData({
      hotSort: "0",
      priceSort: value.detail,
      page: 0,
      goodsList: []
    });
    this.loadData();
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    spaceView = this.selectComponent("#spaceView");
    // goCartView = this.selectComponent("#goCartView");
    // goCartView.getPosition();
    // console.log(options)
    if (options.pkey) {
      this.setData({
        gtype: options.pkey
      });
    }
    if (options.mType) {
      this.setData({
        mType: options.mType
      });
    }
    this.loadData();

  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * 获取商品信息
   */
  loadData() {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize: this.data.pagesize,
      gtype: this.data.gtype,
      hotSort: this.data.hotSort,
      priceSort: this.data.priceSort,
      mType: this.data.mType
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
          if (_this.data.page == 0) {
            _this.setData({
              goodsList: res.data.result.content,
              page: ++_this.data.page
            });
          } else {
            _this.setData({
              goodsList: _this.data.goodsList.concat(res.data.result.content),
              page: ++_this.data.page
            });
          }
          res.data.result.curPage = _this.data.page;
          loadMoreView.loadMoreComplete(res.data);
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      },
    })
  },
  /**商品详情 */
  goodsClick(event) {
    var pkey = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + pkey,
    });
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
          wx.showToast({
            title: '已加入购物车',
            icon: "none"
          });
        }else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
      }
    });
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