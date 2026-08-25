// pages/my/refundOrder/index.js
let app = getApp();
var loadMoreView;
import http from '../../../utils/http'
Page({

    /**
     * 页面的初始数据
     */
    data: {
      isAuto: true,
      iShidden: true,
      datalist: [],
      page: 0,
      pagesize: 10,
      pkey: "",
    }, 

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      loadMoreView = this.selectComponent("#loadMoreView");
      if(options.pkey) {
        this.setData({
          pkey: options.pkey
        })
      }
      this.setData({
        page: 0,
        datalist: [],
      });    
      this.loadData();
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
    var _this = this;
    var url = "/v2/app/market/lm/refund/query",
      page = this.data.page;
   
    var params = {
      orderPkey: this.data.pkey,
      page: page,
      pagesize: this.data.pagesize,
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
        if (res.data.success) {
          if (_this.data.page == 0) {
            _this.setData({
              datalist: res.data.result.content,
              page: ++_this.data.page
            });
          } else {
            _this.setData({
              datalist: _this.data.datalist.concat(res.data.result.content),
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
      }
    });
  },
   /**
   * 跳转到详情页
   */
  goDetail: function (data) {
    console.log(data)
    wx.navigateTo({
      url: '/pages/my/refundDetail/index?pkey='+data.currentTarget.dataset.pkey 
    });
  },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      // this.setData({
      //   page: 0,
      //   datalist: [],
      // });    
      //   this.loadData();
  
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

   
  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    loadMoreView.loadMore()
  },
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {},

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})