// pages/merMien/merchantDetail/index..js
const app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pkey: '',
    inputModel: {

    },
    headIcon: '',
    swiperCurre: 0,
    swipeTotal: 0,
    swpierList: [],
    muted: true
  },
  handleVideoError(e){
    console.log('视频播放失败',e)
  },
  handleVideoComp(e){
    console.log('视频加载完成',e)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      pkey: options.pkey
    })
    this.getData();
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
   * 用户点击右上角分享
   */
  // onShareAppMessage: function () {

  // },
  /**
   * @desc 获取数据
   */
  getData() {
    let that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/demeanour/get',
      data: {
        pkey: this.data.pkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          if (res.data.result.files.length) {
            var swiperList = [];
            for (let i in res.data.result.files) {
              let item = res.data.result.files[i];
              if (item.type == 'HEAD_ICON') {
                that.setData({
                  headIcon: item.url
                })
              } else {
                item.muted =true;
                swiperList.push(item);
              }
            }
            that.setData({
              swiperList: swiperList,
              swipeTotal: swiperList.length,
            })
          }
          res.data.result.mktVendorBigData.content=  res.data.result.mktVendorBigData.content.replace(/\<img/gi,   '<img class="rich-img" ' );
          
          that.setData({
            inputModel: res.data.result,
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
  /**
   * @desc 图片滚动
   */
  swiperChange(event) {
    if (this.data.swiperList[event.detail.current].type == 'VIDEO') {
      let video = wx.createVideoContext(`myVideo${event.detail.current}`, this);
      video.play();
    }
    if(event.detail.current==0&&this.data.swiperList[this.data.swiperList.length-1].type == 'VIDEO') {
      let video = wx.createVideoContext(`myVideo${this.data.swiperList.length-1}`, this);
      video.pause();
    }
    if(this.data.swiperList[event.detail.current+1]&&this.data.swiperList[event.detail.current+1].type == 'VIDEO') {
      let video = wx.createVideoContext(`myVideo${event.detail.current+1}`, this);
      video.pause();
    }
    this.setData({
      swiperCurre: event.detail.current 
    });
  },
  /**
   * @Desc 静音改变
   */
  mutedChange(){
    var temp_str='swiperList['+this.data.swiperCurre+'].muted';
    this.setData({
      [temp_str]:!this.data.swiperList[this.data.swiperCurre].muted
    })
    console.log(this.data.swiperCurre,this.data.swiperList[this.data.swiperCurre].muted)
  }
})