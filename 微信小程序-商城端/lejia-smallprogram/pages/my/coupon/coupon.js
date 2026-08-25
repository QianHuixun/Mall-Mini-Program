// pages/my/coupon/coupon.js
let app = getApp();
import http from '../../../utils/http'
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    farmer: "",//市场pkey
    share: !1,
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    titleBarHeight: 44,
    windowHeight: wx.getSystemInfoSync().windowHeight,
    page: 0,
    pageSize: 5,
    isAuto: !0,
    iShidden: !0,
    cardData: [],
    cardPkey:'',
  },


  //返回上一页
  handleBack() {
    wx.navigateBack({
      delta: 1,
    })
  },
  //返回首页
  handleBackhome() {
    wx.switchTab({
      url: '/pages/home/shouye/index',
    })
  },
  //领取优惠券
  handleGetcoupon(e) {
    console.log(e)
    let pkey = e.currentTarget.dataset.pkey,
      index = e.currentTarget.dataset.index,
      that = this;
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['HhPUXnm42UW_b_smumzhZ31SLcfuacw84ULJiJXy5yE'],
      success(res) {
        console.log("111")
      },
      complete(res) {}
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/card/ins',
      data: {
        card: pkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.farmer ? this.data.farmer : app.globalData.location.pkey
      },
      success: function (res) {
        // that.data.cardData.splice(index, 1)
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if(res.data.success){
          let cardData_str = `cardData[${index}].isReceive`
          that.setData({
            [cardData_str]: true
          })
          wx.showToast({
            title: '领取成功',
            icon: 'none'
          })
        }else{
          wx.showToast({
            title: res.data.msg || '',
            icon:'none'
          })
        }
       
      },
    })

  },
  //获取数据
  loadData() {
    let that = this,
      pageSize = this.data.pageSize,
      page = this.data.page
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/query/centercard',
      data: {
        page,
        pagesize: pageSize,
        cardPkey:this.data.cardPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.farmer ? this.data.farmer : app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return; 
        };
        if (res.data.success) {
          let cardData = res.data.result.content
          if (that.data.page == 0) {
            that.setData({
              cardData,
              page: ++that.data.page
            });
          } else {
            that.setData({
              cardData: that.data.cardData.concat(cardData),
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

      },
    })
  },

  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if(options.hasOwnProperty('cardPkey')){
      this.setData({
        cardPkey:parseInt(options.cardPkey)
      });
    }
    if(options.q) {
      const q = decodeURIComponent(options.q);
      const pkey = utils.getQueryString(q, 'pkey');
        this.setData({
          farmer:pkey
        });     
    }
    console.log("options",options)
    if(options.pkey) {
      this.setData({
        farmer:options.pkey
      });   
    }
    // this.setData({
    //   cardPkey:65
    // });
    this.loadData();
    loadMoreView = this.selectComponent("#loadMoreView");
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