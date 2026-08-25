// pages/my/pointDetail/index.js
const app = getApp();
var loadMoreView;
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    active: "0",
    listData1: [],//全部
    page1: 0,
    listData2: [],//支出
    page2: 0,
    listData3: [],//收入
    page3: 0,
    pagesize: 12,
    direct: ""
  },
  /**
    * Tab标签页修改事件
    */
  onChange(event) {
    this.setData({
      active: event.detail.name
    });
    this.goTop();
    this.loadData();
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.loadData();
  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
    if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
        wx.setNavigationBarTitle({
          title: '我的I DO',
        })
    }
  },
  /**
   * 加载数据
   */
  loadData: function () {
    var that = this, url = "/v1/app/market/lm/member/comm/line",
      params = {
        page: this.data.active == "0" ? this.data.page1 : (this.data.active == "1" ? this.data.page2 : this.data.page3),
        pagesize: this.data.pagesize,
        direct: this.data.active == "0" ? '' : (this.data.active == "1" ? '0' : '1')
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
          if (that.data.active == "0") { //全部          
            if (that.data.page1 == 0) {
              that.setData({
                listData1: res.data.result.content,
                page1: ++that.data.page1
              });
            } else {
              that.setData({
                listData1: that.data.listData1.concat(res.data.result.content),
                page1: ++that.data.page1
              });
            }
            res.data.result.curPage = that.data.page1;
            loadMoreView.loadMoreComplete(res.data);
          } else if (that.data.active == "1") {//支出
            if (that.data.page2 == 0) {
              that.setData({
                listData2: res.data.result.content,
                page2: ++that.data.page2
              });
            } else {
              that.setData({
                listData2: that.data.listData2.concat(res.data.result.content),
                page2: ++that.data.page2
              });
            }
            res.data.result.curPage = that.data.page2;
            loadMoreView.loadMoreComplete(res.data);
          } else {//收入
            if (that.data.page3 == 0) {
              that.setData({
                listData3: res.data.result.content,
                page3: ++that.data.page3
              });
            } else {
              that.setData({
                listData3: that.data.listData3.concat(res.data.result.content),
                page3: ++that.data.page3
              });
            }
            res.data.result.curPage = that.data.page3;
            loadMoreView.loadMoreComplete(res.data);
          }
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    });
  },
  //回到顶部
  goTop: function (e) {  // 一键回到顶部
    if (wx.pageScrollTo) {
      wx.pageScrollTo({
        scrollTop: 0,
        duration: 0
      })
    }
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
    loadMoreView.loadMore()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.loadData();
  }
})