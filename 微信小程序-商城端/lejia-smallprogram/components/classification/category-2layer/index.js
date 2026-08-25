// components/classification/category-3layer/index.js
import http from '../../../utils/http.js';
import { onClickEffect } from '../../../utils/util'
const app = getApp();
var spaceView;
const { applyTheme } = require('../../../utils/themeMixin')
Component({
// 启用页面生命周期监听
options: {
  addGlobalClass: true,
  styleIsolation: 'shared' // 关闭隔离，id 不再被添加前缀
},
    /**
     * 组件的属性列表
     */
    properties: {
        theme: null
    },
    pageLifetimes: {
      show() {
        applyTheme(this)
        wx.getSystemInfo({ success: res => {
          this.setData({ scrollHeight: res.windowHeight });
        }});
        const that = this;
        this.setData({
          topClassify: wx.getStorageSync('topClassify') || ''
        })
       if(this.data.isRefresh){ 
         this.setData({
          marketType: app.globalData.location.marketType,
          marketPkey: app.globalData.location.pkey,
        });
        // 商品 商户 tab栏 重置
        spaceView = this.selectComponent("#spaceView");
        if (wx.getStorageSync('classiftyPkey')) {
          this.setData({
            activeClassify: wx.getStorageSync('classiftyPkey'),
            queryClassify: wx.getStorageSync('classiftyPkey'),
            activeClassifyTwo: '',
          })
          wx.setStorageSync('classiftyPkey', '')
        }
        if (wx.getStorageSync('classiftyPkeyTwo')) {
            console.log('classiftyPkeyTwo', wx.getStorageSync('classiftyPkeyTwo'));
          this.setData({
            activeClassifyTwo: wx.getStorageSync('classiftyPkeyTwo'),
          })
          wx.setStorageSync('classiftyPkeyTwo', '')
        }
    
        this.setData({
          iShidden: true,
          page: -6,
          hasMore: true,
          goodsList: [],
          goodsSortType: 'SALED',// SALED 销量 PRICE 价格
          sortDesc: false
        })
        app.getBuycarNum();
        console.log("show-getClassify")
        this.getClassify();
       
      
        } else {
          console.log("this.data.refreshData",this.data.refreshData)
          this.setData({
            isRefresh: true
          })
          if(this.data.refreshData.gwcNum) {
            const goodsList = this.data.goodsList;
            goodsList.forEach((item,index)=> {
              item.data.forEach((subItem,subIndex)=> {
                if(subItem.pkey == that.data.refreshData.pkey) {
                  goodsList[index].data[subIndex].gwcNum = that.data.refreshData.gwcNum;
                }
              })
            });
            // console.log("goodsList", goodsList)
            this.setData({
              goodsList,            
            })
          }
        }
      },
      hide() {
        console.log('组件所在页面隐藏');
        clearTimeout(this.data.pullUpTimer);
      }
    },
    /**
     * 组件的初始数据
     */
    data: {
      imgUrl: app.globalData.file_url,
      statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
      isRefresh: true,// 是否刷新页面数据，默认刷新，当从商品详情页或商户详情页后退时不刷新
      refreshData: { // 用来存储从商品详情页后退时，获取商品pkey和数量，刷新列表数据
        pkey: '',
        gwcNum: '',
      },
      tabActive: "GOODS",// tab
      imageList: [],
  //-------- 商品-------
      activeClassify: "", //高亮一级分类pkey
      scrollActiveClassify: '',//scroll-view 滚动到该id
      queryClassify: "",//点击一级分类用于接口获取数据的pkey
      classifyList: [], //一级分类列表
      classifyTwoList: [], //二级分类列表
      activeKey: 0,
      goodsList: [], //商品列表
      activeClassifyTwo: "", //高亮二级分类pkey
      scrollActiveClassifyTwo: "",//scroll-view 滚动到该id
      isShowAllClassify: false,   
      page: -6,
      pagesize: 6,

      scrollTop: 0,// 右侧滚动位置
      isLoading: false, // 是否加载中
      typeLoading: false,
      hasMore: true,
      showHint: false,//是否显示文字提示
      isEnd: false, // 是否是最后一个分类
      startY: 0,
      isLastFirstClassify: false,
      isLastSecondClassify: false,
      isBottom: false,
      scrollHeight: "", 
      scrollTop: 0, 

      moreGoodsPopup: false, // 更多同类商品是否显示popup 
      // 更多同类商品 查询
      threeGtype: "",// 更多同类商品 pkey
      sameGoodsList: [],
      threeTotal: 0,
      samePage: 0,
      samePagesize: 6,
      thirdNoList: false,
      // end 更多同类商品
      isShow: false, //是否显示 规格选择dialog
      iShidden: true,
      goodsSortType: 'SALED',// SALED 销量 PRICE 价格
      sortDesc: false,
      topClassify: '',  // 置顶分类
      deliveryType: 0, // 配送方式(全部0,快递配送1,骑手配送2),不传默认是0
      showDeliveryType: false,
    },
    /**
     * 组件的方法列表
     */
    methods: {
       /**轮播广告点击事件 */
    goAds: function (data) {
        onClickEffect(data)
  },
  /**
   * 点击商品搜索栏
   */
  searfocus: function () {
    wx.navigateTo({
      url: '/pages/shouyeGroup/search/index',
    });
  },
  // 获取分类图片
  getPicture:function() {
    var that = this,
    url = "/v1/app/market/img/query";
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + url,
    data: {
      position: "ADVERT_POSITION_GOODS_MAIN",
      positionObj:  this.data.activeClassifyTwo
    },
    header: {
      'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
    success: function (res) {
      if (res.data.success) {
        that.setData({
          imageList: res.data.result
        })
      }
    }
  });
  },
/**
   *@desc 获取一级商品分类列表
   */
  getClassify: function () {
    console.log("getClassify")
    var that = this,
      url = "/v2/app/market/goods/gtype/list";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        showMarket: true,
        showPoint: false,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          if(that.data.topClassify) {   // 设置置顶分类
            const index = res.data.result.findIndex(item => item.pkey == that.data.topClassify)
            const list = res.data.result.splice(index, 1)
            res.data.result.unshift(list[0])
            that.setData({
              activeClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
              queryClassify: res.data.result.length>0 ?res.data.result[0].pkey : ""
            });
            wx.clearStorageSync('topClassify')
          }
          
          that.setData({
            classifyList: res.data.result,
          });

          if (that.data.activeClassify) {
            let hasClassify = false;
            for (let i in res.data.result) {
              let item = res.data.result[i];
              if (item.pkey == that.data.activeClassify) {
                hasClassify = true;
              }
            }
            if (!hasClassify) {
              that.setData({
                activeClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
                queryClassify: res.data.result.length>0 ?res.data.result[0].pkey : ""
              });
            }
          } else {
            that.setData({
              activeClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
              queryClassify: res.data.result.length>0 ?res.data.result[0].pkey : ""
            });
          }
         
          if (that.data.activeClassify) {
            that.getClassifyTwo();
          }
        }
      }
    });

  },

  // 商品一级分类点击事件
  firstClassifyTab: function(e) {
    console.log("sidebarTab")
    let pkey = e.currentTarget.dataset.pkey;
    const id = e.currentTarget.id;
        this.flag = true // 修复点击分类过快时，会因为滚动条的的滑动，调用scroll触发瞄点
        // 联动右边
        this.setData({
          activeClassify: pkey,
          queryClassify: pkey,
          activeClassifyTwo: "",
          scrollActiveClassify: id,
          page: -6,
          goodsList: [],
          showHint: false,
          isLoading: false,
          isBottom: false,
          isShowAllClassify: false,
          hasMore: true,
          deliveryType: 0,
        });          
        this.getClassifyTwo();
  },
  onScroll: function(e) {
        if(this.flag){
          this.flag = false
          return
      }
      if(this.data.isLoading) return;
      let query = this.createSelectorQuery()
      query.selectAll('.scroller').boundingClientRect()
      query.exec(rect => {
        console.log(rect)
       const clientHeight = rect[0][0].height; // 可视区高度
     
      const { scrollTop, scrollHeight } = e.detail;
      
      const threshold = 100; // 距底部阈值
      // 判断是否触发加载
      console.log(scrollTop,clientHeight,scrollHeight,threshold)
    if (scrollTop + clientHeight >= scrollHeight - threshold) {
        if(this.data.hasMore) {
          this.setData({ isLoading: true });
          this.loadSecondData();     
        }
      }
    });
    }, 
 
    scrollGoodsBottom(){ 
      if(!this.data.hasMore) {
        this.setData({isBottom: true});
      }
       
    },

  // 记录起始位置
  handleTouchStart(e) {
    console.log("handleTouchStart",this.data.isBottom)
    if (!this.data.isBottom || this.data.hasMore) return;
    console.log("e", e)
    this.setData({ startY: e.touches[0].clientY });
  },
  // 实时计算滑动距离
  handleTouchEnd(e) {
    if (!this.data.isBottom || this.data.hasMore) return;
    console.log("e", e)
    const currentY = e.changedTouches[0].clientY;
    const deltaY = this.data.startY - currentY; // 上滑时 deltaY > 0
    if (deltaY > 100) { // 上滑超过阈值
      this.setData({ startY: 0});
      this.switchToNextCategory(); // 切换分类
    }
  },
  // 滑动到指定为止
  scrollToFirstClassify: function() {
    // 在自定义组件内部
    const query = this.createSelectorQuery();
    query.select('.scroll-' + this.data.activeClassify).boundingClientRect(rect => {
      this.createSelectorQuery()
        .select('.first-scroll')
        .node()
        .exec(res => {
          console.log(this.data.activeClassify, rect, res)
          const scrollView = res[0].node;
          scrollView.scrollTo({ left: rect.left }); // 滚动到目标位置
        });
    }).exec();
  },
  // 滑动到指定位置
  scrollToSecondClassify: function() {
    // 在自定义组件内部
    const query = this.createSelectorQuery();
    query.select('.scroll-' + this.data.activeClassify+"-"+this.data.activeClassifyTwo).boundingClientRect(rect => {
      this.createSelectorQuery()
        .select('.second-scroller')
        .node()
        .exec(res => {
          console.log(this.data.activeClassify, rect, res)
          const scrollView = res[0].node;
          scrollView.scrollTo({ top: rect.top }); // 滚动到目标位置
        });
    }).exec();
  },
  // 滑动切换到下一个分类
  switchToNextCategory() {
    if(this.data.isEnd) return;
    this.setData({ 
      scrollTop: 0,       // 重置滚动距离
    });
    let currentCategoryIndex = 0,SecondPkey="",firstPkey="";
    console.log("isLastSecondClassify",this.data.isLastSecondClassify)
    if(!this.data.isLastSecondClassify) {
      this.data.classifyTwoList.forEach((item, index)=> {
        if(item.pkey == this.data.activeClassifyTwo) {
         currentCategoryIndex = index;
        }
      })  
       SecondPkey = this.data.classifyTwoList[currentCategoryIndex+1].pkey; 
       this.setData({
        page: -6,
        goodsList: [],
        activeClassifyTwo: SecondPkey,
        queryClassify: this.data.activeClassify,
        showHint: false,
        isLoading: false,
        isBottom: false,
        hasMore: true,
        deliveryType: 0,
      })
      this.scrollToSecondClassify();
      this.getPicture();
      this.loadSecondData();
    } else {
      
      this.data.classifyList.forEach((item, index)=> {
        if(item.pkey== this.data.activeClassify) {
         currentCategoryIndex = index;
        }
      })  
       firstPkey = this.data.classifyList[currentCategoryIndex+1].pkey;
       this.setData({
          activeClassify: firstPkey,
          queryClassify: firstPkey,
          activeClassifyTwo: "",
          page: -6,
          goodsList: [],
          showHint: false,
          isLoading: false,
          hasMore: true,
          isBottom: false
       });
       this.scrollToFirstClassify();
       this.getClassifyTwo();

    }
     

   
  },
  /**
   * @desc 获取二级商品分类列表
   */
  getClassifyTwo() {
    console.log("getClassifyTwo")

    console.log(this.data.activeClassify, this.data.queryClassify)
    clearTimeout(this.data.pullUpTimer);
    let that = this;
    this.setData({
      typeLoading: true
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/market/goods/gtype/query',
      data: {
        gtype: that.data.activeClassify,
        hasRecommend: true
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            isPulling: false,
            classifyTwoList: res.data.result,
            // activeClassifyTwo: res.data.result.length > 0 ? res.data.result[0].pkey : "",
          });
          console.log("activeClassifyTwo",that.data.activeClassifyTwo)
          if(!that.data.classifyTwoList.find(item => item.pkey == that.data.activeClassifyTwo)) {
            that.setData({
                activeClassifyTwo: res.data.result.length > 0 ? res.data.result[0].pkey : "",
            })
          }
          if (!that.data.classifyTwoList.length) {
            that.setData({
              goodsList: [],
              page: -6,               
              hasMore: true,
            });
          }
          that.loadSecondData();
        } 
        
        that.getPicture();
        that.setData({
          typeLoading: false
        })

      },
      fail(err) {
        that.setData({
          typeLoading: false
        })
      }
    });
  },
// 显示全部二级分类
  showAllClassify() {
    this.setData({
      isShowAllClassify: true,
    })
  },
// 隐藏全部二级分类
  hideAllClassify(){
    this.setData({
      isShowAllClassify: false,
    })
  },
  handleDeliveryClick(event) {
    console.log(event);
    let type = event.currentTarget.dataset.type
    console.log(type);
    if(type == this.data.deliveryType) return
    this.setData({
        deliveryType: type,
        page: -6,
        hasMore: true,
        goodsList: [],
        isBottom: false
    })
    this.loadSecondData()
  },
  // 排序
  handleSort(e) {
    const type= e.currentTarget.dataset.type;
    if(type == 'SALED') {
      this.setData({
        goodsSortType: 'SALED',
        sortDesc: true
      })
    } else if(type == 'PRICE') {
      if(this.data.goodsSortType == 'PRICE') {
        this.setData({
          sortDesc: !this.data.sortDesc
        })
      } else {
        this.setData({
          goodsSortType: 'PRICE',
          sortDesc: false
        })
      }
    }
    console.log(this.data.activeClassify, this.data.queryClassify, this.data.activeClassifyTwo)
    this.setData({
      page: -6,
      hasMore: true,
      goodsList: [],
      queryClassify: this.data.activeClassify,
      activeClassifyTwo: this.data.activeClassifyTwo
    })
    this.loadSecondData();
  },
  // 二级分类点击事件
  secondClassifyTab(e) {
    let pkey = e.currentTarget.dataset.pkey;
    const id = e.currentTarget.id; 
    this.setData({
      page: -6,
      hasMore: true,
      goodsList: [],
      activeClassifyTwo: pkey,
      scrollActiveClassifyTwo: id,
      queryClassify: this.data.activeClassify,
      isBottom: false,
      deliveryType: 0
    })
    console.log("secondClassifyTab", this.data.isBottom)
    this.getPicture();
    this.loadSecondData();
  },
  // 判断是否为最后一个分类
  handleIsEnd() {
    const {classifyList,classifyTwoList,activeClassify,activeClassifyTwo} = this.data;
    classifyList.forEach((item,index)=> {
      if(item.pkey == activeClassify) {
        if(index == classifyList.length-1) {
          this.setData({isLastFirstClassify: true})
        }else {
          this.setData({isLastFirstClassify: false})
        }
      }
    })
    classifyTwoList.forEach((item,index)=> {
      if(item.pkey == activeClassifyTwo) {
        if(index == classifyTwoList.length-1) {
          this.setData({isLastSecondClassify: true})
        }else {
          this.setData({isLastSecondClassify: false})
        }
      }
    })
    if(this.data.isLastFirstClassify && this.data.isLastSecondClassify) {
      this.setData({isEnd: true})
    }else {
      this.setData({isEnd: false})
    }
  },
  // 获取二级分类商品列表
  loadSecondData(){
    if(!this.data.hasMore) return;
    console.log("loadSecondData",this.data.isBottom)
    var that = this,
    url = "/v4/app/market/goods/goodsMain/query";
  this.setData({
    isLoading: true,
    hasMore: true,
    page: that.data.page  + that.data.pagesize,
  })
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + url,
    data: {
      goodsMain: that.data.activeClassifyTwo,
      from : that.data.page,
      limit: that.data.pagesize,
      limitGoodsMain: true,
      goodsSortType: that.data.goodsSortType,
      sortDesc: that.data.sortDesc,
      deliveryType: that.data.deliveryType,
    },
    header: {
      'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
      "openid": app.globalData.openid,
      "farmer": app.globalData.location.pkey
    },
    success: function (res) {
      if (res.data.success) {
        // 价格处理，将价格存在2个字段中，用于大小价格的显示
        console.log("开始goodsList", that.data.goodsList,res.data.result);
        if(!res.data.result.groups.length) {
          that.setData({
            hasMore: false,
            isLoading: false,
            showHint: true
          });
          return;
        }
        var resultGroup = res.data.result.groups.map(item => { 
          item.data = item.data.map(subItem => {
               var price = subItem.price.toFixed(2).toString().split('.');
               subItem.bigPrice = price[0];
               subItem.smallPrice = price[1];
               return subItem;
          });
          return item;
        });
        //当 商品列表长度不为0，则循环商品列表并插入新列表
        // console.log("that.data.goodsList.length" ,that.data.goodsList.length);
        const goodsList = that.data.goodsList;
        if(goodsList.length && goodsList[goodsList.length-1].group) {           
          if(goodsList[goodsList.length-1].group?.key == resultGroup[0].group?.key) {
            goodsList[goodsList.length-1].data =goodsList[goodsList.length-1].data.concat(resultGroup[0].data);
            resultGroup.splice(0,1);
          } 
            that.setData({
              goodsList: goodsList.concat(resultGroup),
            });
          
          console.log("结束", goodsList);
        } else {//否则直接插入商品列表
          that.setData({
            goodsList: resultGroup,
          });
        }       
          that.setData({
            hasMore: res.data.result.hasNext,
            isLoading: false
          });
          if(!that.data.hasMore && that.data.goodsList.length <=6) {
            that.setData({showHint: true})
          }
          that.handleIsEnd();
          that.getCorrelation()
          wx.nextTick(() => {
            const query = that.createSelectorQuery();
            query.select('.scroller').boundingClientRect()
            query.select('.scroller-container').boundingClientRect()
            query.exec(rect=>{
              console.log("rect.height",rect[0].height,"scrollHeight",rect[1].height)
              if (rect[0].height > rect[1].height) { // 内容高度 < 容器高度
                that.setData({ isBottom: true }); // 主动标记触底
                console.log("loadData",that.data.isBottom)
              }
            });
          });
      }
      
    }
  });
  },
  /**
   * @desc 判断二级分类有没有运营端商品
   */
  getCorrelation() {
    if(this.data.activeClassifyTwo < 0) {
      this.setData({
        showDeliveryType: false
      })
      return
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v4/app/market/goods/goodsMain/correlation',
      data: {
        goodsMain: this.data.activeClassifyTwo,
      },
      success: (res) => {
        console.log('getCorrelation', res);
        if (res.data.success) {
          const result = res.data.result
          this.setData({
            showDeliveryType: result
          })
        }
      }
    })
  },

// 更多同类商品 弹窗
   /**
   * @desc 关闭弹窗
   */
  handelGoodsClosePop() {
    this.setData({
      moreGoodsPopup: false
    })
  },
  handelGoodsOpenPop(event) {
    const {  threegtype } = event.currentTarget.dataset;
    
    this.setData({
      moreGoodsPopup: true,
      threeGtype: threegtype,
      sameGoodsList: [],
      samePage: 0
    });
   this.loadSameGoodsData();
  },
  // 更多同类商品 - 触底加载
  scrolltolower () {
    if (this.data.sameGoodsList.length === this.data.threeTotal){
      this.setData({
        thirdNoList: true,
      }) 
      return;
    } // 判断是否加载全部
    this.loadSameGoodsData();
},
  //获取 更多同类商品
  loadSameGoodsData:function() {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v4/app/market/goods/threeGtype/query',
      data: {
        threeGtype: that.data.threeGtype,
        page: that.data.samePage,
        pagesize: that.data.samePagesize,
        goodsSortType: that.data.goodsSortType,
        sortDesc: that.data.sortDesc,
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
        var goodslist = res.data.result.content.map(item => {
          var price = item.price.toFixed(2).toString().split('.');
          item.bigPrice = price[0];
          item.smallPrice = price[1];
          return item;
        });
        that.setData({
          sameGoodsList: that.data.sameGoodsList.concat(goodslist),
          samePage: ++that.data.samePage,
          threeTotal: res.data.result.total
        });
          
      }
    
      },
      fail(err) {
     
      }
    });
  },
/**
   * 获取当前商品的规格数量
   */
  getSpaceNumber: function (data) {
    console.log(data.currentTarget.dataset)
    var url = "/v1/app/market/goods/space/totalAmount",
      pkey = data.currentTarget.dataset.pkey,
      space = data.currentTarget.dataset.space,
      index = data.currentTarget.dataset.index,
      subIndex = data.currentTarget.dataset.subindex,
      process = data.currentTarget.dataset.process,
      type = data.currentTarget.dataset.type || "",
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
        if (res.data.result == 1 && process == 0) {
          that.handleAddTOCart(data, pkey, space, index, subIndex, type);
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
  handleAddTOCart: function (data, pkey, space, index, subIndex="", type= '') {

    console.log("subIndex", subIndex)
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
          if(type === "third") {
            that.setData({
              [`sameGoodsList[${index}].gwcNum`]: 1
            })
          } else {
            that.setData({
              [`goodsList[${index}].data[${subIndex}].gwcNum`]: 1
            })
          }
         
          app.getBuycarNum();
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
  // 防止van-stepper冒泡
  catchTap() {},
  stepChange(data) {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/less/goods/num", //减少购物车里单个商品的数量
      url_add = "/v1/app/market/lm/member/gwc/add/goods/num", //增加购物车里单个商品的数量
      index = data.currentTarget.dataset.index,
      subIndex = data.currentTarget.dataset.subindex,
      newValue = data.detail; //修改后的值
      console.log(data.currentTarget.dataset,this.data.goodsList[index].data[subIndex])
    if (this.data.goodsList[index].data[subIndex].gwcNum < newValue) {
      url = url_add;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: this.data.goodsList[index].data[subIndex].goods,
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
          });
          app.getBuycarNum();
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
  // 更多同类商品
  stepSameChange(data) {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/less/goods/num", //减少购物车里单个商品的数量
      url_add = "/v1/app/market/lm/member/gwc/add/goods/num", //增加购物车里单个商品的数量
      index = data.currentTarget.dataset.index,
      newValue = data.detail; //修改后的值
    if (this.data.sameGoodsList[index].gwcNum < newValue) {
      url = url_add;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: this.data.sameGoodsList[index].pkey,
        space: this.data.sameGoodsList[index].spaces[0].pkey,
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
            [`sameGoodsList[${index}].gwcNum`]: newValue
          })
         that.data.goodsList.forEach((item,i) => {
            item.data.forEach((subItem,subIndex) => {
              if(subItem.pkey === that.data.sameGoodsList[i].pkey ) {
                that.setData({
                  [`goodsList[${i}].data[${subIndex}].gwcNum`]: newValue
                })
              }
            })
          });
          app.getBuycarNum();
        } else {
          that.setData({
            [`sameGoodsList[${index}].gwcNum`]: newValue - 1
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
      url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${data.currentTarget.dataset.pkey}&isClassify=true`
    })
  },
   
  
    }
})