// pages/shouyeGroup/search/index.js
import http from '../../../utils/http'
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    value:"",
    isShowGood:false,
    hotList:[],
    hisList:[],
    page:0,
    goodsList:[],
    vendor: "",
    loadingBom:false,//是否在加载更多
    noMoreBom:false, //没有更多,
    mType:'',//商品类型
    placeholder: null,
    offset: 0,  // 滚动查询数量
  },

  /**点击取消 */
  onCancel(){
    // console.log("点击取消");
    if (this.data.isShowGood){
      this.setData({
        value: "",
        isShowGood: false
      });
    }else{
      wx.navigateBack();
    }
  },

  /**搜索值变化时 */
  onChange(e){
    console.log("e.detail",e.detail);
    this.setData({
      value: e.detail,  
    });
  },

  onSearch(){
    let isShowGood = false
    if(!this.data.value && this.data.placeholder) {
        this.setData({
            value: this.data.placeholder
        })
    }
    if(this.data.value){
      isShowGood = true;
    }
    this.setData({
      isShowGood: isShowGood,
      page:0,
      goodsList:[],
      loadingBom:false,
      noMoreBom:false
    });
    this.getGoods(this.data.value);
  },

  /**历史记录点击 */
  hisClick(data){
    
    this.setData({
      value: data.currentTarget.dataset.text,
      isShowGood: true,
      page:0,
      goodsList:[],
      loadingBom:false,
      noMoreBom:false
    });
    this.getGoods(data.currentTarget.dataset.text);
    // console.log(e);
  },

  /**商品搜索加载 */
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
      this.getGoods(this.data.value);
    }
  },

  /**热门搜索点击 */
  hotClick(event) {
    var item = event.currentTarget.dataset.id;
    this.setData({
      value: item.descp,
      isShowGood: true,
      page: 0,
      goodsList: [],
      loadingBom: false,
      noMoreBom: false
    });
    this.getGoods(item.descp);
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

  /**商品点击进入详情 */
  goodsClick(event){
    var item = event.currentTarget.dataset.id;
    let url =  '/pages/shouyeGroup/goodsDeatil/index?pkey=' + item.pkey
    if(this.data.mType === 'JD_GOODS' || item.source == 'JD') {
        url = '/pages/shouyeGroup/jdGoodsDetail/index?pkey=' + item.pkey
    }
    wx.navigateTo({
      url: url,
    });
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

  /**获取商品列表 */
  getGoods(val) {
    if(this.data.mType == 'INTEGRAL_MSD_GOODS' && (app.globalData.ascription == 22 || app.globalData.ascription == 13)) {
      this.getMsdGoods(val)
      return
    }
    var _this = this;
    var url = this.data.mType === 'JD_GOODS' ? '/v1/app/jd/goods/by/title' : '/v1/app/market/goods/query';
    var parame = {
      page: this.data.page,
      pagesize:10,
      title: val || this.data.placeholder,
      mType:this.data.mType,
      vendor: this.data.vendor
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          var noMoreBom = false;
          var list = _this.data.goodsList.concat(res.data.result.content);
          console.log(list.length,"list.length");
          if (list.length == res.data.result.total){
            noMoreBom = true;
          }
          _this.setData({
            goodsList: list,
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

  /**获取民生专区和京东专区商品列表 */
  getMsdGoods(val) {
    if(!val) return
    var url = '/v1/app/market/goods/msd/search'
    const { offset } = this.data 
    var params = {
      offset: offset,
      limit: 10,
      title: val || this.data.placeholder,
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      success: (res) => {
        if (res.data.success) {
          var noMoreBom = false;
          var list = this.data.goodsList.concat(res.data.result.list);
          if (!res.data.result.nextOffset){
            noMoreBom = true;
          }
          this.setData({
            offset: res.data.result.nextOffset || 0,
            goodsList: list,
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
  onLoad: function (options) {
    console.log(options)
    if(options.hasOwnProperty('mType')){
      this.setData({
        mType:options.mType,
      })
    }
    if(options.hasOwnProperty('vendor')) {
      this.setData({
        vendor: options.vendor
      })
    }
    if(options.hasOwnProperty('name') && options.name != 'undefined') {
      this.setData({
        placeholder: options.name
      })
    }
    
    this.getSearch();
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
})