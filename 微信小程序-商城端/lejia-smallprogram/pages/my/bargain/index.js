// pages/shouyeGroup/groupon/index.js
const app = getApp();
import http from '../../../utils/http'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgUrl: app.globalData.file_url,
    time: 0,
    timeList: [],
    goodsList: [],
    page: 0,
    pagesize: 10,
    imageList: [], //顶部广告
    show: !1,
    isAuto: true,
    iShidden: true,
    share_imageUrl: '',

  },
  /**商品点击进入详情 */
  goodsClick(event) {
    var pkey = event.currentTarget.dataset.id.list2[0].goods,
      orderPkey = event.currentTarget.dataset.id.pkey;
    wx.navigateTo({
      url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${pkey}&orderPkey=${orderPkey}`,
    });
  },


  /**获取列表 */
  loadData: function () {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize: 10,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/query/cut',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        console.log(res)
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          let goodsList = _this.dataformat(res.data.result.content)
          // console.log(goodsList)

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
  /**
   * 立即购买
   */
  goSubmit: function (e) {
    let time = e.currentTarget.dataset.time
    console.log(e,time)
    if (!time)
      return wx.showToast({
        title: '砍价活动已结束!',
        icon: 'none',
        duration: 2000
      })
    var that = this,
      url = "/v2/app/market/lm/order/getUnpaidOrder",
      params = {
        pkey: e.currentTarget.dataset.pkey
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
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/pay/index'
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          })
        }


      }
    })
  },
  /**关闭砍价弹窗 */
  onClose() {
    this.setData({
      show: false
    })
  },
  //倒计时
  onChange(e) {
    let index = e.currentTarget.dataset.index,
      timeList = this.data.timeList;
    timeList[index] = e.detail
    this.setData({
      timeList
    });

  },


  /**数据格式化 */
  dataformat(data) {
    let timeList = []
    let goodsList = data.map(item => {

      let nowDate = new Date().getTime();
      timeList.push({})
      item.time = (item.endTime - nowDate > 0 ? item.endTime - nowDate : 0)
      if (!item.time)
        item.label = '砍价已结束'
      else {
        if (!item.rcutAmt)
          item.label = '砍价成功，请及时支付'
      }

      return item;
    });
    this.setData({
      timeList
    })
    return goodsList
  },
  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_COLLAGE"
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
    if (data.currentTarget.dataset.url == "") return;
    wx.navigateTo({
      url: data.currentTarget.dataset.url,
    });
  },
  // 砍价分享弹窗
  handlePopup(e) {
    console.log(e)
    if (!app.globalData.userInfo) {
      wx.setStorage({
        data: e.detail.userInfo,
        key: 'userinfo',
      })
      app.globalData.userInfo = e.detail.userInfo
    }
    let that = this,
      params = {
        goods: e.currentTarget.dataset.pkey,
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
        console.log(res)
  
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

  /**登录回调 */
  onLoadFun: function () {
    this.loadData();
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
      title: `${app.globalData.userInfo.nickName}正在砍价，快来祝他一臂之力！`,
      path: `/pages/my/bargainDetail/index?pKey=${this.data.orderPkey}`,
      imageUrl: this.data.share_imageUrl,
      success: function (res) {
        if (res.errMsg == 'shareAppMessage:ok') {

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