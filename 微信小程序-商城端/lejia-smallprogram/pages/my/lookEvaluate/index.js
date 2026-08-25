// pages/my/lookEvaluate/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      pkey: "",
      commentList: [],
      showBtn: false, // 初始不显示按钮
      commentList: [],
      storeText:{
        0: "",
        1: "很差",
        2: "差",
        3: "一般",
        4: "满意",
        5: "很满意",
      }
      },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const pkey = utils.getQueryString(q, 'pkey');
          this.setData({
            pkey:pkey
          });     
      }
      
      if(options.pkey) {
        this.setData({
          pkey:options.pkey
        });   
      }
     this.getData();
    },
// 图片放大预览
handlePreview(e) {
  console.log(e)
  const images = e.currentTarget.dataset.images;
  const image = e.currentTarget.dataset.image;
  wx.previewImage({
    current: image, // 当前图片
    urls: images// 所有图片
  });
},
/**
   * 获取评价信息
   */
  getData: function () {
    var that = this,
      url = "/v1/app/market/lm/order/comment/list";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: that.data.pkey
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if(!res.data.success) return;
        const commentList = res.data.result.map((item)=>{
          item.showEllipsis = false;
          item.showButton = false;
          return  item;
        })
        that.setData({
          commentList: commentList
        });
        this.checkTextHeight();
      }
    });
  },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {
      
    },
// 检测是否需要显示按钮
checkTextHeight() {
  const query = wx.createSelectorQuery().in(this);
  query.selectAll('.text-content').boundingClientRect(rects => {
    rects.forEach((rect, index) => {
      const lineHeight = 20; // 根据实际行高调整（单位：rpx）
      const maxHeight = lineHeight * 2; // 2行高度
      console.log(maxHeight, rect.height)
      const showButton = rect.height > maxHeight;
      this.setData({
        [`commentList[${index}].showButton`]: showButton,
        [`commentList[${index}].showEllipsis`]: showButton   
      });
      // if(this.data.showButton) { 
      //   this.setData({
            
      // });
        
      // }

    });
  }).exec();
},

// 切换展开/收起
toggleEllipsis(e) {
  const index = e.currentTarget.dataset.index;
  const newState = !this.data.commentList[index].showEllipsis;
  this.setData({
    [`commentList[${index}].showEllipsis`]: newState
  });
},
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      
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
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})