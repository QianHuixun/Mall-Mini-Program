// pages/my/groupon/index.js
const app = getApp();
import http from '../../../utils/http'
var loadMoreView_not,
  loadMoreView_info;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: 0,
    page: {
      not: 0,
      info: 0
    },
    pagesize: 10,
    isAuto: true,
    iShidden: true,
    collageData: {
      notGroups: [],
      infoGroups: [],
    },
    timeList: [{}],
    userInfo:app.globalData.userInfo
  },
  /** 选项卡切换 */
  tabChange(e) {
    // e.detail.index ? this.setData({
    //   'page.info': 0
    // }) : this.setData({
    //   'page.not': 0
    // })
    this.setData({
      active: e.detail.index
    })
    if (e.detail.index) {
      if (!this.data.page.info)
        this.loadData()
    } else {
      if (!this.data.page.not)
        this.loadData()
    }

  },
  /**
   * 获取用户信息
   */
  handleGetuserinfo(e) {
    if (!app.globalData.userInfo) {
      wx.setStorage({
        data: e.detail.userInfo,
        key: 'userinfo',
      })
      app.globalData.userInfo = e.detail.userInfo
      this.setData({
        userInfo:e.detail.userInfo
      })
    }
  },
  /**阻止冒泡 */
  stopBubble() {
    console.log('阻止冒泡')
  },
  //倒计时
  onChange(e) {
    // console.log(e.currentTarget.dataset)
    var list = this.data.collageData[e.currentTarget.dataset.type],
    data_str =`collageData.${e.currentTarget.dataset.type}`
    list[e.currentTarget.dataset.index].coutDown = e.detail;
    // list[''].coutDown = e.detail;
    this.setData({
      [data_str]: list,
    });
  },

  /**商品点击进入详情 */
  goodsClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
    });
  },
  /**获取列表 */
  loadData: function () {
    var _this = this;
    var parame = {
      page: this.data.active ? this.data.page.info : this.data.page.not,
      pagesize: 10,
      status: this.data.active ? 'INTO_GROUPS' : 'NOT_GROUPS'
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/order/list/collage',
      data: parame,
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
          if (_this.data.active) {
            if (_this.data.page.info == 0) {
              _this.setData({
                'collageData.infoGroups': res.data.result.content,
              })
            } else {
              _this.setData({
                'collageData.infoGroups': _this.data.collageData.infoGroups.concat(res.data.result.content),
              })
            }
            res.data.result.curPage = _this.data.page.info
            loadMoreView_info.loadMoreComplete(res.data)
            _this.setData({
              'page.info': ++_this.data.page.info
            })
          } else {
            if (_this.data.page.not == 0) {
              _this.setData({
                'collageData.notGroups': res.data.result.content,
              })
            } else {
              _this.setData({
                'collageData.notGroups': _this.data.collageData.notGroups.concat(res.data.result.content),
              })
            }
            res.data.result.curPage = _this.data.page.not
            loadMoreView_not.loadMoreComplete(res.data)
            _this.setData({
              'page.not': ++_this.data.page.not
            });
          }


        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
        // loadMoreView.loadMoreComplete(res.data);

      },
    })
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**登录回调 */
  onLoadFun: function () {
    this.loadData();
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView_not = this.selectComponent("#loadMoreView_not");
    loadMoreView_info = this.selectComponent("#loadMoreView_info");
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
    this.data.active ? loadMoreView_info.loadMore() : loadMoreView_not.loadMore()
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (e) {
    if (e.from == "button")
      return {
        title: `${app.globalData.userInfo.nickName}正在拼团，一起拼团超值好货！`,
        path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + e.target.dataset.pkey + '&tjr=' + app.globalData.openid,
        imageUrl: e.target.dataset.img, //用户分享出去的自定义图片大小为5:4,
        success: function (res) {
          // 转发成功
          wx.showToast({
            title: "分享成功",
            icon: 'success',
            duration: 2000
          })
        },
        fail: function (res) {
          // 分享失败
        },
      }
    else
      return false
  }
})