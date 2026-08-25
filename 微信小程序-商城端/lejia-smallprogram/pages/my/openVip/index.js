// pages/my/openVip/index.js
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
    isAuto: true,
    iShidden: true,
    page: 0,
    pagesize: 12,
    listData: [],
    userInfo: {},
    price: {}, //会员信息，包含原价、优惠价、会员内容详情图片
    iShidden: true,
    isShow: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    spaceView = this.selectComponent("#spaceView");
    // goCartView = this.selectComponent("#goCartView");
    // goCartView.getPosition();
    this.loadData();
    this.getData();
    this.getMemberPrice()
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * 加载数据
   */
  loadData: function () {
    var that = this,
      url = "/v1/app/market/goods/query",
      params = {
        page: this.data.page,
        pagesize: this.data.pagesize,
        mType: "MEMBER_GOODS"
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
          if (that.data.page == 0) {
            that.setData({
              listData: res.data.result.content,
              page: ++that.data.page
            });
          } else {
            that.setData({
              listData: that.data.listData.concat(res.data.result.content),
              page: ++that.data.page
            });
          }
          res.data.result.curPage = that.data.page;
          loadMoreView.loadMoreComplete(res.data);

        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    });
  },
  getMemberPrice() {
    let that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/price',
      data: {},
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
          that.setData({
            price: res.data.result,
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    });

  },
  handlePay: function () {
    var that = this,
      url = "/v1/app/market/lm/member/beforePay",
      params = {
        amt: 88,
        memberPType: "ANNUAL_FEE",
        payType: "ORDER_WEIXIN"
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
      success: res => {
        console.log(res)
        wx.requestPayment({
          'timeStamp': res.data.result.timeStamp,
          'nonceStr': res.data.result.nonceStr,
          'package': res.data.result.pack,
          'signType': res.data.result.signType,
          'paySign': res.data.result.paySign,
          'success': function (res) {
            that.getData();
          },
          'fail': function (res) {

          },
          'complete': function (res) {}
        })
      }
    })
  },
  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
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
   * 获取用户信息
   */
  getData: function () {
    var that = this,
      url = "/v1/app/market/lm/member/get";

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        that.setData({
          userInfo: res.data.result
        })
      }
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
    this.loadData();
  }
})