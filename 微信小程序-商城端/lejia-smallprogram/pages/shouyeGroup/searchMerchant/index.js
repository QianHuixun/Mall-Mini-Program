// pages/shouyeGroup/searchMerchant/index.js
import http from '../../../utils/http';
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
      value:"",
      isShowMerchant:false,
      merchantList: [],
      hotList:[],
      hisList:[],
      page:0,
      loadingBom:false,//是否在加载更多
      noMoreBom:false, //没有更多,
      placeholder:'请输入商户名称',
      mType: "",
    },

  /**点击取消 */
  onCancel(){
    // console.log("点击取消");
    if (this.data.isShowMerchant){
      this.setData({
        value: "",
        isShowMerchant: false
      });
    }else{
      wx.navigateBack();
    }
  },

  /**搜索值变化时 */
  onChange(e){
    this.setData({
      value: e.detail,  
    });
    
  },
  onSearch(){
    let isShowMerchant = false
    if(this.data.value){
      isShowMerchant = true;
    }
    this.setData({
      isShowMerchant: isShowMerchant,
      page:0,
      merchantList:[],
      loadingBom:false,
      noMoreBom:false
    });
    this.getMerchant(this.data.value);
    // console.log(e);
  },
  /**历史记录点击 */
  hisClick(data){
    
    this.setData({
      value: data.currentTarget.dataset.text,
      isShowMerchant: true,
      page:0,
      merchantList:[],
      loadingBom:false,
      noMoreBom:false
    });
    this.getMerchant(data.currentTarget.dataset.text);
    // console.log(e);
  },
  /**商户搜索加载 */
  bindscrollbottom(eventhandle) {
    if (eventhandle.detail.direction == "bottom") {
      // console.log("bottom");
      if (this.data.noMoreBom || this.data.loadingBom){
        return;
      }
      this.setData({
        page: this.data.page + 1,
        loadingBom: true,
        noMoreBom: false
      });
      this.getMerchant(this.data.value);
    }
  },

  /**热门搜索点击 */
  hotClick(event) {
    var item = event.currentTarget.dataset.id;
    this.setData({
      value: item.descp,
      isShowMerchant: true,
      page: 0,
      merchantList: [],
      loadingBom: false,
      noMoreBom: false
    });
    this.getMerchant(item.descp);
  },

  /**删除 */
  onDelete(){
    // console.log("删除");
    let  that =this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/search/del',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        that.setData({
          hisList :[]
        })
      },
    })
  },


  /**获取用户搜索历史和热门搜索 */
  getSearch() {
    var _this = this;
    var parame = {
      stype: 0,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/search/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          console.log(res.data);
          _this.setData({
            hotList: res.data.result.hotLines,
            hisList: res.data.result.lines
          });
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
   * 跳转到商户详情
   */
  goMerchantDetail: function (data) {
    wx.navigateTo({
      url: '/pages/shouyeGroup/merchant/index?pkey=' + data.currentTarget.dataset.pkey
    })
  },
  /**获取商户列表 */
  getMerchant(val) {
    var _this = this;
    var parame = {
      page: this.data.page,
      pagesize:8,
      name: val,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v4/app/market/goods/gtype/vendor/query',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          var noMoreBom = false;
          var list = _this.data.merchantList.concat(res.data.result.content);
          console.log(list.length,"list.length");
          if (list.length == res.data.result.total){
            noMoreBom = true;
          }
          _this.setData({
            merchantList: list,
            loadingBom: false,
            noMoreBom: noMoreBom
          });
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
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

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
    // onShareAppMessage() {

    // }
})