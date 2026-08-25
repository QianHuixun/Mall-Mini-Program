// pages/shouyeGroup/presell/index.js
import http from '../../../utils/http'
const app = getApp();
var loadMoreView;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    dateList: [], //今日明日的日期
    page: 0,
    goodsList: [],
    loadingBom: false, //是否在加载更多
    noMoreBom: false, //没有更多
    imageList: [],
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    time: 0,
    startTime:0,
    timeData: {},
    startTimeData:{},
    dayType: '0',
    scrollHeight: 0,
    contetnHight: '600rpx',
    showLookingMore: true,
    lastGoodsPkey: '',  // 点击进页面的商品pkey，置顶该商品
    title: "限时抢购"
  },
  getHeight() {
    var query = wx.createSelectorQuery();
    var that = this;
    query.select('.scroll-view-box').boundingClientRect(function (rect) {
      console.log("rect", rect)
      that.setData({
        scrollHeight: rect.height
      })
    }).exec();
  },
  //滚动到底部
  bindscrollbottom(eventhandle) {
    if (eventhandle.detail.direction == "bottom") {
      const recommendGoods = this.selectComponent('#recommendGoods')
      recommendGoods.bindscrollbottom()
    }
  },
  loadMoreListener: function (e) {
    this.getData()
  },
  clickLoadMore: function (e) {
    this.getData()
  },
  /**
   * @desc 日期类型改变
   */
  handleDayChange(e) {
    console.log(e.currentTarget.dataset.type)
    this.setData({
      dayType: e.currentTarget.dataset.type,
      page: 0,
    })
    this.getData();

  },
  getTime() {
    let that = this;
    var tomorrow = new Date(),
      toDay = tomorrow.getFullYear() + "-" + ((tomorrow.getMonth() + 1) < 10 ? 0 : '') +
      (tomorrow.getMonth() + 1) + "-" + ((tomorrow.getDate()) < 10 ? 0 : '') + tomorrow.getDate();
    tomorrow.setTime(tomorrow.getTime() + 24 * 60 * 60 * 1000);
    tomorrow = tomorrow.getFullYear() + "-" + ((tomorrow.getMonth() + 1) < 10 ? 0 : '') +
      (tomorrow.getMonth() + 1) + "-" + ((tomorrow.getDate()) < 10 ? 0 : '') + tomorrow.getDate();
    let timeStamp = new Date(tomorrow + ' 00:00:00').getTime() - new Date().getTime();
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/market/goods/get/presaleTime',
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
            time: res.data.result.endTime,
            startTime:res.data.result.startTime,
          })
          that.getData()
        }
      },
    })
    that.setData({
      dateList: [toDay, tomorrow]
    })

  },
  /**
   * @desc 返回上一页
   */
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
  /**
   * @desc 结束时间倒计时
   */
  timeChange(e) {
    this.setData({
      timeData: {
        hours: e.detail.hours.toString(),
        minutes: e.detail.minutes.toString(),
        seconds: e.detail.seconds.toString(),
      },
    });
   
  },
  /**
   * @desc 开始时间倒计时
   */
  startTimeChange(e) {
    this.setData({
      startTimeData: {
        hours: e.detail.hours.toString(),
        minutes: e.detail.minutes.toString(),
        seconds: e.detail.seconds.toString(),
      },
    });
  },


  /**获取特价商品 */
  getData() {
    var that = this;
    var date = this.data.dateList[this.data.dayType];
    var parame = {
      page: this.data.page,
      pagesize: 10,
      mType: "SPECIAL_GOODS",
      date: date,
      topGoods: that.data.lastGoodsPkey || '',
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
          console.log(res.data.result);
          var noMoreBom = false;
          res.data.result.content.map(item => {
            item.kcNum = 0;
            item.spaces.map(subItem => {
              item.kcNum += subItem.kcNum
            })
            return item
          })
          let goodslist = res.data.result.content;
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



  /**商品点击进入详情 */
  goodsClick(event) {

    var item = event.currentTarget.dataset.id;
    if(this.data.dayType=='1'){
      wx.showToast({
        title: '请等待开抢时间',
        icon:'none'
      })
      return
    }
    if (!item.kcNum) {
      return
    }
    wx.navigateTo({
      url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
    });
  },
goGoodsDetails(event){
  var item = event.currentTarget.dataset.id;
  wx.navigateTo({
    url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
  });
},
    lookingMore() {
        this.setData({
            contetnHight: 'auto',
            showLookingMore: false
        })
    },
    getDisPlayName() {
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
          success: (res) => {
            const data = res.data.result
            if(data.specialDisplayName) {
                // wx.setNavigationBarTitle({
                //     title: data.specialDisplayName,
                // })
                this.setData({
                    title: data.specialDisplayName
                })
            }
          }
        })
      },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getDisPlayName()
    this.setData({
        llastGoodsPkey: options.pkey != 'undefined' ? options.pkey: ''
    })
    this.getTime();
    this.getHeight()
    loadMoreView = this.selectComponent("#loadMoreView");
    wx.createIntersectionObserver().relativeToViewport({bottom: 100}).observe('#loadMoreView', (res) => {
        loadMoreView.loadMore()
    });
    
  },
  noRecommendGoods() {
    console.log('noRecommendGoods');
  },
  // 微信订阅消息
subscribeToMessages(){
    wx.requestSubscribeMessage({  
      tmplIds: ['sQhVptWg15AotV9SR_MUcrcPexfwsRm1iXC_JyMKQ-8'],  
      success:(res)=> {  
      //成功回调  
        console.log(1112)  
      },
      fail:(res)=> {
        console.log("2222 res", res)  
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
// 进入秒杀专区时，弹出订单消息弹框
this.subscribeToMessages();
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
})