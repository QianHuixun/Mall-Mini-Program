// pages/merMien/merchantList/index.js
const app = getApp();
import http from '../../../utils/http'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    keywords: '',
    tabKey:'',
    tabList:[],
    searchData:{
      scopes:''
    },
    pageSize:10,
    page:0,
    merchantList:[],
    windowHeight: wx.getSystemInfoSync().windowHeight,
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.getGtypeList();
    this.loadData();
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
  
  /**
   *@desc 搜索值变化时
   */
  onChange(e) {
    this.setData({
      keywords: e.detail,
      page: 0,
    });
 
    this.loadData();
  },
  onClickLeft() {
    wx.navigateBack({
      delta: 0,
      fail() {
        wx.switchTab({
          url: '/pages/home/shouye/index'
        })
      }
    })
  },
  getGtypeList() {
    let that =this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/demeanour/gtypePkeyNameList',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          // console.log('分类列表为',res);
          that.setData({
            tabList:res.data.result,
            tabKey:'-1',
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
  tabChange(e){
    if(e.detail.index){
      this.setData({
        'searchData.scopes':this.data.tabList[e.detail.index-1].pkey,
      });
    }else{
      this.setData({
        'searchData.scopes': '',
      });
    }
    this.setData({
      page: 0,
    });
    this.loadData();
  },
   /**
   * 获取商品列表
   */
  loadData: function () {
    var that = this,
      url = "/v1/app/market/demeanour/pageList";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        scopes: that.data.searchData.scopes,
        marketPkeys:app.globalData.location.pkey,
        page:that.data.page,
        name:that.data.keywords,
        pagesize:that.data.pageSize,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          
          if (that.data.page == 0) {
            that.setData({
              merchantList: res.data.result.content,
              page: ++that.data.page
            })
          } else {
            that.setData({
              merchantList: that.data.merchantList.concat(res.data.result.content),
              page: ++that.data.page
            })
          }
          console.log(that.data.merchantList)
          res.data.result.curPage = that.data.page;
          loadMoreView.loadMoreComplete(res.data);
        }
      }
    });


  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * @desc 查看详情
   */
  handleDetail(e){
     wx.navigateTo({
        url: '/pages/merMien/merchantDetail/index?pkey='+e.currentTarget.dataset.pkey,
      });
  }
})