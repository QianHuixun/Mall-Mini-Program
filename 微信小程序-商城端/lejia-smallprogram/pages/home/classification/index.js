// pages/classification/index.js
import http from '../../../utils/http.js';
import utils from '../../../utils/util.js';
const app = getApp();
var spaceView;
const { applyTheme } = require('../../../utils/themeMixin')
// var goCartView;
Page({
  data: {
    pagaeOptions: {},
    currentComponent: [22,13,17].includes(app.globalData.ascription) ? 'category-2layer' : 'category-3layer',
    imgUrl: app.globalData.file_url,
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    isRefresh: true,// 是否刷新页面数据，默认刷新，当从商品详情页或商户详情页后退时不刷新
    refreshData: { // 用来存储从商品详情页后退时，获取商品pkey和数量，刷新列表数据
      pkey: '',
      gwcNum: '',
    },
    tabActive: "GOODS",// tab
    
    scrollTop: 0,
    offsetTop: 0,
    firstBottom: false,
    startClientY: 0,
    endClientY: 0,
  
    // --------- 商户 -----------
    merchantList: [],
    merchantLoading: false,
    hasMerchantMore: true,
    isMerchantBottom: false,
    merchantPage: -6,
    merchantClassifyList: [],
    merchantPagesize: 6,
    isAllMerchantClassify: false,
    activeMerchantClassify: "",
    queryMerchantClassify: "",
    marketType: app.globalData.location.marketType,
    marketPkey: null,
    // end 商户
    theme: null
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 测试代码， 获取扫码点餐桌号，以及 跳转到对应分类
    // app.globalData.qrCode = 10;
    // this.sidebarTab({currentTarget:{dataset: {
    //   pkey: 15930
    // }}});
    applyTheme(this)
    if(options.pkey) {
      app.globalData.qrCode = options.pkey;
      console.log("pkey",options.pkey)
    }
    if(options.gtype) {
      this.selectComponent('#categoryLayer').sidebarTab({currentTarget:{dataset: {
        pkey: gtype
      }}});
    }
    if(options.q) {
      const q = decodeURIComponent(options.q);
      const qrCode = utils.getQueryString(q, 'pkey');
      app.globalData.qrCode = qrCode;
      //  跳转到对应的分类
      const gtype = utils.getQueryString(q, 'gtype');
      this.selectComponent('#categoryLayer').sidebarTab({currentTarget:{dataset: {
        pkey: gtype
      }}});
     console.log("gtype",gtype,"okey",qrCode)
    }
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if(this.getTabBar()) {this.getTabBar().init()}
      console.log("app.globalData.qrCode-1", app.globalData.qrCode)
    // console.log('onload',this.data.isRefresh, app.globalData.location);
   if(this.data.isRefresh){ 
     this.setData({
      marketType: app.globalData.location.marketType,
      marketPkey: app.globalData.location.pkey,
    });
    // 商品 商户 tab栏 重置
    this.selectComponent('#tabs').resize();
    if(this.data.marketType === "VENDOR_SHOPPING_MALL") {
      this.setData({
        merchantPage: -6,
        merchantList: [],
      })
      this.getMerchantClassify();
    }
  
    const isMerchant = wx.getStorageSync('isMerchant')
    console.log('isMerchant', isMerchant);
    if(isMerchant) {
      this.setData({
        tabActive: 'MERCHANT'
      })
      wx.removeStorageSync('isMerchant')
    }
    } else {
      console.log("this.data.refreshData",this.data.refreshData)
      this.setData({
        isRefresh: true
      })
      if(this.data.refreshData.gwcNum) {
        this.selectComponent('#categoryLayer').unRefresh();
      }
    }
  },
    /**
   *@desc 获取一级商品分类列表
   */
  getMerchantClassify: function () {
    var that = this,
      url = "/v2/app/market/goods/gtype/list";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        showMarket: true,
        showPoint: false,
        flag: true,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            merchantClassifyList: res.data.result,
          });
          if (that.data.activeMerchantClassify) {
            let hasMerchantClassify = false;
            for (let i in res.data.result) {
              let item = res.data.result[i];
              if (item.pkey == that.data.activeMerchantClassify) {
                hasMerchantClassify = true;
              }
            }
            if (!hasMerchantClassify) {
              that.setData({
                activeMerchantClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
                queryMerchantClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
              });
            }
          } else {
            that.setData({
              activeMerchantClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
              queryMerchantClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
            });
          }
          if(that.data.activeMerchantClassify && that.data.marketType === "VENDOR_SHOPPING_MALL") {
            that.loadMerchantData();
          }
        }
      }
    });

  },
  // 商户 商品 tab栏切换
  onTabsClick(name, title){
    console.log(name,title)
    this.setData({
      tabActive: name.detail.name
    });
  },
  
   /**
   * 跳转到商户详情
   */
  goMerchantDetail: function (data) {
    wx.navigateTo({
      url: `/pages/shouyeGroup/merchant/index?pkey=${data.currentTarget.dataset.pkey}&isClassify=true`
    })
  },
  
  searchMerchantfocus: function() {
    wx.navigateTo({
      url: '/pages/shouyeGroup/searchMerchant/index',
    });
  },
    // 商户一级分类点击事件
    sidebarMerchantTab: function(e) {
      let pkey = e.currentTarget.dataset.pkey
          this.merchantFlag = true // 修复点击分类过快时，会因为滚动条的的滑动，调用scroll触发瞄点
          // 联动右边
          this.setData({
            activeMerchantClassify: pkey,
            queryMerchantClassify: pkey,
            merchantPage: -6,
            merchantList: [],
  
          });
          this.loadMerchantData();
    },
    onMerchantScroll(e) {
          if(this.merchantFlag){
            this.merchantFlag = false
            return
        }
        // 获取每个goodItem到顶部的距离
        // 减去顶部距离其他东西的距离
        // 如果距离小于或等于0则更新index
        var newIndex ="";
        // console.log("开始", index);
        // scroll-view 距离顶部的高度
        var scrollMenuTop  = 0
        let query = wx.createSelectorQuery()
        query.selectAll('#merchant-scroller').boundingClientRect()
        query.selectAll('.merchantItem').boundingClientRect()
        query.exec(res=>{
            // console.log(res);
            scrollMenuTop = res[0][0].top;
            res[1].forEach((item) => {
                // 每个项目距离顶部的高度-scroll-view 距离顶部的高度=每个项目距离scroll-view顶部的高度
                if(item.top-scrollMenuTop<= 0){
                 newIndex = item.dataset.pkey;
                }
            });
            this.setData({
              activeMerchantClassify: newIndex
            })
        });
      
      },
    scrollMerchantBottom(){
        if(!this.data.hasMerchantMore) return;
       this.loadMerchantData();
      },
    /**
   * 获取商户列表
   */
  loadMerchantData: function () {
    var that = this,
      url = "/v4/app/market/goods/gtype/vendornew/query";
      that.setData({
        merchantPage: that.data.merchantPage  + that.data.merchantPagesize,
      });
      console.log("that.data.merchantPage", that.data.merchantPage)
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        gtype: that.data.queryMerchantClassify,
        from : that.data.merchantPage,
        limit: that.data.merchantPagesize,
        name: ''
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          //当 商户列表长度不为0，则循环商户列表并插入新列表
          // console.log(that.data.goodsList.length);
          if(that.data.merchantList.length) {           
            if(that.data.merchantList[that.data.merchantList.length-1].group.key == res.data.result.groups[0].group.key) {
              that.data.merchantList[that.data.merchantList.length-1].data =that.data.merchantList[that.data.merchantList.length-1].data.concat(res.data.result.groups[0].data);
              res.data.result.groups.splice(0,1);
            } 
              that.setData({
                merchantList: that.data.merchantList.concat(res.data.result.groups),
              });
          } else {//否则直接插入商户列表
            that.setData({
              merchantList: res.data.result.groups,
            });
          } 
            that.setData({
              hasMerchantMore: res.data.result.hasNext
            });
        }
      }
    });
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
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
  onPullDownRefresh: function () {},
  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {},
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {}
})