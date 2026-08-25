// pages/classification/index.js
const app = getApp();
import http from '../../../utils/http'
var loadMoreView;
var spaceView;
// var goCartView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isShow: false, //是否显示 规格选择dialog
    iShidden: true,
    goodsList: [], //商品列表
    page: 0,
    pagesize: 6,
    buycarNum: 0,
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      buycarNum: app.globalData.buycarNum
    })
    loadMoreView = this.selectComponent("#loadMoreView");
    spaceView = this.selectComponent("#spaceView");
    // goCartView = this.selectComponent("#goCartView");
    // goCartView.getPosition();
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * 获取商品列表
   */
  loadData: function () {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/free";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
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

          // 价格处理，将价格存在2个字段中，用于大小价格的显示
          var goodslist = res.data.result.content.map(item => {
            var price = item.price.toFixed(2).toString().split('.');
            item.bigPrice = price[0];
            item.smallPrice = price[1];
            return item;
          });

          if (that.data.page == 0) {
            that.setData({
              goodsList: goodslist,
              page: ++that.data.page
            })
          } else {
            that.setData({
              goodsList: that.data.goodsList.concat(goodslist),
              page: ++that.data.page
            })
          }

          res.data.result.curPage = that.data.syPage;
          loadMoreView.loadMoreComplete(res.data);
        }
      }
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
      data: {
        pkey: pkey
      },
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
          app.globalData.buycarNum++
          that.setData({
            buycarNum: app.globalData.buycarNum
          })
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
   * 跳转到商品详情页
   */
  goDetail: function (data) {
    wx.navigateTo({
      url: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + data.currentTarget.dataset.pkey
    })
  },
  /**
   * 跳转到购物车
   */
  goBuycar() {
    wx.switchTab({
      url: '/pages/home/buyCar/index',
    })
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
    this.setData({
      iShidden: true
    })
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
  onPullDownRefresh: function () {},

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    loadMoreView.loadMore()
  },

  
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {}
})