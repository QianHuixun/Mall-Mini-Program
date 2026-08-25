// pages/shouyeGroup/merchant/index.js
import http from '../../../utils/http.js'
import utils from '../../../utils/util.js';
const app = getApp();
var spaceView;
const { applyTheme } = require('../../../utils/themeMixin')
Page({

    /**
     * 页面的初始数据
     */
    data: {
      isClassify: false,// 是否从分类页跳转来
      imgUrl: app.globalData.file_url,
      merchantPKey: null, //商户PKey
      merchantInfo: null,
      statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
      topContentHeight: 154,
      value: "",
      isShow: false, //是否显示 规格选择dialog
      iShidden: true,
      goodsList: [], //商品列表
      activeKey: 0,
      activeClassify: "", //选中一级分类pkey
      queryClassify: "",
      classifyList: [],
      page: 0,
      pagesize: 6,
      loading: false,
      hasMore: true,
      isBottom: false,
      firstBottom: false,
      startClientY: 0,
      endClientY: 0,
      goodsMain: '', //二级分类
      searchName: '',   //搜索名称
      sortType: 'num', //排序类型
      sortValue: true,
      buycarNum: app.globalData.buycarNum,
      buycarPrice: app.globalData.buycarPrice,
      show: false,
      theme:null,
    },
    handlePreviewImage(e) {
        const files = this.data.merchantInfo.files.map(item => item.url)
        const index = e.currentTarget.dataset.index
        console.log(files);
        wx.previewImage({
            urls: files,
            current: files[index]
        })
    },
    /**
   * 加入收藏夹
   */
  addCollection: function () {
    if (this.data.merchantInfo.isCollection) {
      this.handleDelete();
      return;
    }
    var that = this,
      url = "/v1/app/market/goods/collection/ins";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ctype: 2,
        objKey: this.data.merchantInfo.pkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
            noLogin: true
          })
          return;
        }
        if (res.data.success) {
          that.setData({
            ["merchantInfo.isCollection"]: true,
            ["merchantInfo.collectionPkey"]: res.data.result
          });
          wx.showToast({
            title: "商户收藏成功",
            icon: 'none'
          })
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          })
        }
      }
    });

  },
  /**
   * 删除收藏
   */
  handleDelete: function () {
    var url = "/v1/app/market/goods/collection/del",
      that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: this.data.merchantInfo.collectionPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            ["merchantInfo.isCollection"]: false
          });
          wx.showToast({
            title: '取消收藏',
            icon: "none"
          })
        } else {
          wx.showToast({
            title: '取消失败',
            icon: "none"
          })
        }
      }
    });
  },
    showPopup() {
      this.setData({ show: true });
    },
    onClose(){
      this.setData({ show: false });
    },
     // 防止van-stepper冒泡
  catchTap() {},
    goBack: function() {
      // wx.switchTab({
      //   url: '/pages/home/classification/index',
      // });
      wx.navigateBack({
        delta: 1 // 返回的页面数，如果delta大于现有页面数，则返回到首页       
        });
    },
    // 商品一级分类点击事件
  sidebarTab: function(e) {
    let pkey = e.currentTarget.dataset.pkey
        this.flag = true // 修复点击分类过快时，会因为滚动条的的滑动，调用scroll触发瞄点
        // 联动右边
        this.setData({
          activeClassify: pkey,
          queryClassify: pkey,
          page: 0,
          goodsList: [],
          priceSort: "",
          xsNumSort: '',
          sortType: null
        });
          
        this.loadAllData();
  },
    /**
     * 获取商户详情
     */
    getData() {
      var url = '/v1/app/market/demeanour/get'
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: this.data.merchantPKey
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: (response) => {
          console.log('response', response);
          const result = response.data.result

          // 保存市场信息，主要是用于新用户扫商户二维码进入
          wx.setStorageSync('location_pkey', result.farmer);
          app.globalData.location.pkey = result.farmer;

          result.files.forEach(item => {
            if(item.type === 'HEAD_ICON') {
              result.headIcon = item.url
            }
          })
          this.setData({
            merchantInfo: response.data.result
          })
          console.log('farmer',  app.globalData.location.pkey);
          this.getClassify();
        }
      })
    },
      /**
   * 获取当前商品的规格数量
   */
  getSpaceNumber: function (data) {
    console.log('getSpaceNumber', app.globalData.location.pkey);
    var url = "/v1/app/market/goods/space/totalAmount",
      pkey = data.currentTarget.dataset.pkey,
      space = data.currentTarget.dataset.space,
      index = data.currentTarget.dataset.index,
      subIndex = data.currentTarget.dataset.subindex,
      that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: pkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.result == 1) {
          that.handleAddTOCart(data, pkey, space, index, subIndex);
        } else {
          that.setData({
            isShow: true
          });
          spaceView.getData(pkey);
        }
      }
    })
  },
/**
   *@desc 添加到购物车
   * @param pkey 商品pkey
   * @param space 商品规格pkey
   * @param index 商品列表下标
   */
  handleAddTOCart: function (data, pkey, space, index, subIndex='') {
    var url = "/v1/app/market/lm/member/gwc/ins",
      that = this,
      params;
    if (data.detail.hasOwnProperty('data')) {
      params = {
        goodsPkey: data.detail.goodsPkey,
        goodsNum: 1,
        space: data.detail.space,
      }
      data = data.detail.data
    } else
      params = {
        goodsPkey: pkey,
        goodsNum: 1,
        space: space,
      }
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
          // goCartView.addshopcar(data);
          that.setData({
            [`goodsList[${index}].data[${subIndex}].gwcNum`]: 1
          })
          app.getBuycarNum();
          app.getBuycarPrice();
          console.log(app.globalData.buycarNum);
          wx.showToast({
            title: '已加入购物车',
            icon: "none"
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
      }
    });
  },
   /**
   *@desc 改变商品数量
   */
  stepChange(data) {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/less/goods/num", //减少购物车里单个商品的数量
      url_add = "/v1/app/market/lm/member/gwc/add/goods/num", //增加购物车里单个商品的数量
      index = data.currentTarget.dataset.index,
      subIndex = data.currentTarget.dataset.subindex,
      newValue = data.detail; //修改后的值
    if (this.data.goodsList[index].data[subIndex].gwcNum < newValue) {
      url = url_add;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: this.data.goodsList[index].data[subIndex].pkey,
        space: this.data.goodsList[index].data[subIndex].spaces[0].pkey,
        goodsNum: 1
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.success) {
          that.setData({
            [`goodsList[${index}]data.[${subIndex}].gwcNum`]: newValue
          })
          app.getBuycarNum();
          app.getBuycarPrice();
          console.log(app.globalData.buycarNum, app.globalData.buycarPrice);
          setTimeout(() => {
            console.log(app.globalData.buycarNum, app.globalData.buycarPrice);
            that.setData({
              buycarNum: app.globalData.buycarNum,
              buycarPrice: app.globalData.buycarPrice
            })
          }, 0)
        } else {
          that.setData({
            [`goodsList[${index}]data.[${subIndex}].gwcNum`]: newValue - 1
          })
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
      }
    });
  },
  /**
   * 跳转到商品详情页
   */
  goDetail: function (data) {
    wx.navigateTo({
      url: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + data.currentTarget.dataset.pkey
    })
  },
  scrollGoodsBottom(){
    console.log("hasmore", this.data.hasMore)
    if(!this.data.hasMore) return;
    this.loadAllData();
  },
    onScroll(e) {
        if(this.flag){
          this.flag = false
          return
      }
      // 获取每个goodItem到顶部的距离
      // 减去顶部距离其他东西的距离
      // 如果距离小于或等于0则更新index
      // 设置最后更新index
      var index = this.data.activeClassify;
      var newIndex ="";
      // console.log("开始", index);
      // scroll-view 距离顶部的高度
      var scrollMenuTop  = 0
      let query = wx.createSelectorQuery()
      query.selectAll('#scroller').boundingClientRect()
      query.selectAll('.goodItem').boundingClientRect()
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
            activeClassify: newIndex
          })
      });
    
    },
  /**
   * 获取商品列表
   */
  loadAllData: function () {
    var that = this,
      url = "/v4/app/market/goods/vendor/goodsMain/query";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        from: that.data.page,
        limit: that.data.pagesize,
        goodsMain: this.data.queryClassify,  //二级分类pkey
        name: '', //商品名称
        priceSort: this.data.sortType==='price'? this.data.sortValue : '', //价格排序
        vendor: this.data.merchantPKey, //商户主键
        xsNumSort: this.data.sortType==='num'? this.data.sortValue : '', //销量排序
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          // 价格处理，将价格存在2个字段中，用于大小价格的显示
          var resultGroup = res.data.result.groups.map(item => {
            item.data = item.data.map(subItem => {
                 var price = subItem.price.toFixed(2).toString().split('.');
                 subItem.bigPrice = price[0];
                 subItem.smallPrice = price[1];
                 return subItem;
            });
            return item;
          });
          if(that.data.goodsList.length) {           
            if(that.data.goodsList[that.data.goodsList.length-1].group.key == resultGroup[0].group.key) {
              that.data.goodsList[that.data.goodsList.length-1].data =that.data.goodsList[that.data.goodsList.length-1].data.concat(resultGroup[0].data);
              resultGroup.splice(0,1);
            } 
              that.setData({
                goodsList: that.data.goodsList.concat(resultGroup),
              });
            
            console.log("结束", that.data.goodsList);
          } else {//否则直接插入商品列表
            that.setData({
              goodsList: resultGroup,
            });
          } 
            that.setData({
              page: that.data.page  + that.data.pagesize,
              hasMore: res.data.result.hasNext
            });
        }
       
      }
    });


  },
  /**
   *@desc 获取一级商品分类列表
   */
  getClassify: function () {
    var that = this,
      url = "/v2/app/market/goods/gtype/vendor/list";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        vendor: this.data.merchantPKey,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        console.log(res);
        if (res.data.success) {
          var classifyList = res.data.result

          that.setData({
            classifyList: classifyList,
            activeClassify: classifyList[0].pkey,
            queryClassify: classifyList[0].pkey,
          });
          that.loadAllData();
        }
      }
    });

  },

  /**搜索商品 */
  onSearch(event) {
    wx.navigateTo({
      url: '/pages/shouyeGroup/search/index?vendor=' + this.data.merchantPKey
    });
  },
  handleSortClick(event) {
    console.log(event);
    const type = event.currentTarget.dataset.type
    let sortValue = this.data.sortValue
    if(type !== this.data.sortType) {
      sortValue = false
    } else if(sortValue === '' ){
      sortValue = false
    } else if(sortValue === false) {
      sortValue = true
    } else if(sortValue === true) {
      sortValue = false
    }
    if(type != 'price') {
      sortValue = true
    }
    // const sortType = this.data.sortType === type ? null : type
    console.log(type, sortValue);
    this.setData({ 
      sortType: type,
      page: 0,
      sortValue,
      queryClassify: this.data.activeClassify,
      goodsList: []
    })
    console.log(this.data.sortValue);
    this.loadAllData()
  },

  /**
   * 去购物车结算
   */
  goBuyCar() {
    wx.navigateTo({
      url: '/pages/shouyeGroup/buyCar/index',
    })
  },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        applyTheme(this)
      console.log(options);
      if(options.q) {   // 通过商户二维码进入
        const q = decodeURIComponent(options.q)
        const tjv = utils.getQueryString(q, 'pkey')
        wx.setStorageSync('tjv', tjv)
        this.setData({
          merchantPKey: tjv
        })
      } else {
        this.setData({
          merchantPKey: options.pkey
        })
      }
      if (options.hasOwnProperty('isClassify')) {
        this.setData({
          isClassify: JSON.parse(options.isClassify)
        });
      }
      this.getData()
      app.watch('buycarNum', (v) => {
        this.setData({
          buycarNum: v
        })
      })
      app.watch('buycarPrice', (v) => {
        this.setData({
          buycarPrice: v
        })
      })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {
      wx.nextTick(() => {
        const query = wx.createSelectorQuery()
        query.selectAll('.merchant-top').boundingClientRect();
        setTimeout(()=> {
        query.exec((res) => {
          console.log('query', res);
          let height = res[0][0].height
          console.log('height', height);
          this.setData({
            topContentHeight: height
          })
        })
      },300);
      })
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      spaceView = this.selectComponent("#spaceView");

      this.setData({
        iShidden: true,
        page: 0,
      })
      app.getBuycarNum();
      app.getBuycarPrice();
      // console.log('farmer',  app.globalData.location.pkey);
      // this.getClassify();
      console.log(app.globalData.buycarNum, app.globalData.buycarPrice);
      this.setData({
        buycarNum: app.globalData.buycarNum,
        buycarPrice: app.globalData.buycarPrice
      })
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
      if(this.data.isClassify) {
        var pages = getCurrentPages();
        var prevPage = pages[pages.length - 2]; //上一个页面
        console.log(prevPage)
        prevPage.setData({
          isRefresh: false
        })
      }
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