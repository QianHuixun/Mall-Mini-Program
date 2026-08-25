// pages/shouyeGroup/groupon/index.js
const app = getApp();
import http from '../../../utils/http'
import { onClickEffect } from '../../../utils/util'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgUrl: app.globalData.file_url,
    goodsList: [],
    page: 0,
    pagesize: 10,
    imageList: [], //顶部广告
    show: false,
    orderPkey: '',
    share_imageUrl: "",
    share_title: "",
    isAuto: true,
    iShidden: true,
  },
  /**
   * 获取砍价订单
   */
  getCutOrder(item) {
    let that = this,
      params = {
        goods: item.spaces[0].pkey,
        num: 1
      }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/initiate/cut',
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        that.setData({
          orderPkey: res.data.result.pkey
        })
        wx.navigateTo({
          url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${item.pkey}&orderPkey=${that.data.orderPkey}`,
        });
      },
    })
  },
  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    if (item.isCut) {
      this.getCutOrder(item)
    } else {
      wx.navigateTo({
        url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${item.pkey}`,
      });
    }

  },
  // 砍价分享弹窗
  handlePopup(e) {
    console.log(e)
    let that = this,
    params = {
      goods: e.currentTarget.dataset.pkey,
      num: 1
    }
    if (!app.globalData.userinfo) {
      wx.getUserProfile({
        desc: '用于分享显示用户昵称',
        success: function (res) {
          var userinfo = res.userInfo;
          wx.setStorage({
            data: userinfo,
            key: 'userInfo',
          })
          that.setData({
            userInfo: userinfo
          })
          app.globalData.userinfo = userinfo
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/lm/order/initiate/cut',
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
              if (res.data.success)
                that.setData({
                  orderPkey: res.data.result.pkey,
                  show: !0,
                  share_imageUrl: e.currentTarget.dataset.img,
                })
              else
                wx.showToast({
                  title: res.data.codeMsg || '',
                  icon: 'none',
                  duration: 2000
                })
            },
          })
        },
      })
    }else{
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v1/app/market/lm/order/initiate/cut',
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
          if (res.data.success)
            that.setData({
              orderPkey: res.data.result.pkey,
              show: !0,
              share_imageUrl: e.currentTarget.dataset.img,
            })
          else
            wx.showToast({
              title: res.data.codeMsg || '',
              icon: 'none',
              duration: 2000
            })
        },
      })
    }
  },
  /**阻止冒泡 */
  stopBubble() {
    console.log('阻止冒泡')
  },
  /**获取列表 */
  loadData: function () {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize: 10,
      mType: "CUT_GOODS"
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
          let goodsList = _this.dataformat(res.data.result.content)

          if (_this.data.page == 0) {
            _this.setData({
              goodsList,
              page: ++_this.data.page
            });
          } else {
            _this.setData({
              goodsList: _this.data.goodsList.concat(goodsList),
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
  /**数据格式化 */
  dataformat(data) {
    let goodsList = data.map(item => {
      let nowDate = new Date().getTime(),
        endDate = new Date(item.endDate.replace('-', '/')).getTime();
      if (item.isCut)
        item.btnTxt = '继续砍价'
      else
        item.btnTxt = '发起砍价'
      let price
      item.priceOld != null ?
        price = item.priceOld.toFixed(2).toString().split('.') :
        price = ['0', '00'];
      item.bigPrice = price[0];
      item.smallPrice = price[1];
      return item;
    });
    return goodsList
  },
  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_CUT"
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
  /**关闭砍价弹窗 */
  onClose() {
    this.setData({
      show: false
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.getImageList();
    this.loadData();
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
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

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    return {
      title: `${app.globalData.userinfo.nickName}正在砍价，快来祝他一臂之力！`,
      path: `/pages/my/bargainDetail/index?pKey=${this.data.orderPkey}`,
      imageUrl: this.data.share_imageUrl,
      success: function (res) {
        if (res.errMsg == 'shareAppMessage:ok') {

          wx.navigateTo({
            url: '/pages/my/bargain/index',
          })
        }
      },
      fail: function () {
        if (res.errMsg == 'shareAppMessage:fail cancel') {
          // 用户取消转发
        } else if (res.errMsg == 'shareAppMessage:fail') {
          // 转发失败，其中 detail message 为详细失败信息
        }
      }
    }







  }
})