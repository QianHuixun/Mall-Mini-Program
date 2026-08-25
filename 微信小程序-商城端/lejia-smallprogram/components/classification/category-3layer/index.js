// components/classification/category-3layer/index.js
import http from '../../../utils/http.js';
const app = getApp();
var spaceView;
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
    },
    pageLifetimes: {
      show() {
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
          this.setData({
            activeClassifyTwo: wx.getStorageSync('classiftyPkeyTwo'),
          })
          wx.setStorageSync('classiftyPkeyTwo', '')
        }
    
        this.setData({
          iShidden: true,
          page: -6,
          goodsList: [],
          goodsSortType: 'SALED',// SALED 销量 PRICE 价格
          sortDesc: false
        })
        app.getBuycarNum();
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
      scrollTop: 0,
      offsetTop: 0,
      moreGoodsPopup: false, // 更多同类商品是否显示popup
      loading: false,
      typeLoading: false,
      hasMore: true,
      isBottom: false,
      firstBottom: false,
      startClientY: 0,
      endClientY: 0,
      
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
    },
    /**
     * 组件的方法列表
     */
    methods: {
  /**
   * 点击商品搜索栏
   */
  searfocus: function () {
    wx.navigateTo({
      url: '/pages/shouyeGroup/search/index',
    });
  },
/**
   *@desc 获取一级商品分类列表
   */
  getClassify: function () {
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
        if(that.data.topClassify) {   // 设置置顶分类
          const index = res.data.result.findIndex(item => item.pkey == that.data.topClassify)
          const list = res.data.result.splice(index, 1)
          res.data.result.unshift(list[0])
          that.setData({
            activeClassify: res.data.result.length > 0 ? res.data.result[0].pkey : "",
            queryClassify: res.data.result.length>0 ?res.data.result[0].pkey : "",
          });
          wx.clearStorageSync('topClassify')
          setTimeout(() => {
            that.setData({
              scrollActiveClassify: 'scroll-' + res.data.result[0].pkey
            });
          }, 0)
        }

        if (res.data.success) {
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
            that.loadAllData();
          }
        }
      }
    });

  },

  // 商品一级分类点击事件
  sidebarTab: function(e) {
    console.log("sidebarTab")
    let pkey = e.currentTarget.dataset.pkey;
    const id = e.currentTarget.id; 
        this.flag = true // 修复点击分类过快时，会因为滚动条的的滑动，调用scroll触发瞄点
        // 联动右边
        this.setData({
          activeClassify: pkey,
          queryClassify: pkey,
          scrollActiveClassify: id,
          activeClassifyTwo: "ALL",
          page: -6,
          goodsList: [],

        });          
        this.getClassifyTwo();
        this.loadAllData();
  },
  onScroll(e) {
        if(this.flag){
          this.flag = false
          return
      }

      const scrollTop = e.detail.scrollTop;
      if (scrollTop % 200 < 10) {
        this.calculateDimensions();
      }
     
    },
    calculateDimensions() {
       // 获取每个goodItem到顶部的距离
      // 减去顶部距离其他东西的距离
      // 如果距离小于或等于0则更新index
      // 设置最后更新index
      var index = this.data.activeClassify;
      var newIndex ="";
      console.log("开始", index);
      // scroll-view 距离顶部的高度
      var scrollMenuTop  = 0
      let query = this.createSelectorQuery()
      query.selectAll('.scroller').boundingClientRect()
      query.selectAll('.goodItem').boundingClientRect()
      query.exec(res=>{
          console.log("query.exec", res);
          scrollMenuTop = res[0][0].top;
          res[1].forEach((item) => {
              // 每个项目距离顶部的高度-scroll-view 距离顶部的高度=每个项目距离scroll-view顶部的高度
              if(item.top-scrollMenuTop<= 0){
               newIndex = item.dataset.pkey;
              }
          });
          if(this.data.activeClassifyTwo == "ALL") {
           if(newIndex) {
            this.setData({
              activeClassify: newIndex
            })
           }       
          if(this.data.activeClassify != index) {
            this.getClassifyTwo();
           }
          }
          else {
            this.setData({
              activeClassifyTwo: newIndex
            })
          }
          
      });
    
    },
    scrollGoodsBottom(){
      console.log("hasmore", this.data.hasMore)
      if(!this.data.hasMore) return;
      if(this.data.activeClassifyTwo ==='ALL') {
        this.loadAllData();
      } else {
        this.loadSecondData();
      }    
    },
  /**
   * @desc 获取二级商品分类列表
   */
  getClassifyTwo() {
    console.log(this.data.activeClassify, this.data.queryClassify)
    let that = this;
    this.setData({
      typeLoading: true
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/market/goods/gtype/query',
      data: {
        gtype: that.data.activeClassify
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          res.data.result.splice(0, 0, {
            pkey: 'ALL',
            name: "全部"
          });

          that.setData({
            classifyTwoList: res.data.result,
            activeClassifyTwo: res.data.result.length > 0 ? res.data.result[0].pkey : "",
          });
          if (!that.data.classifyTwoList.length) {
            that.setData({
              goodsList: [],
              page: -6,               
            });
          }
        }
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
      goodsList: [],
      queryClassify: this.data.activeClassify,
      activeClassifyTwo: "ALL"
    })
    this.loadAllData();
  },
  /**
   * 获取商品列表
   */
  loadAllData: function () {
    var that = this,
      url = "/v4/app/market/goods/gtype/query";
      this.setData({
        page:  that.data.page + that.data.pagesize
      });
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        gtype: that.data.queryClassify,
        from : that.data.page,
        limit: that.data.pagesize,
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
          // 价格处理，将价格存在2个字段中，用于大小价格的显示
          console.log("开始goodsList", that.data.goodsList,res.data.result);
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
          // console.log(that.data.goodsList.length);
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
              hasMore: res.data.result.hasNext
            });
        }
      }
    });
  },
  // 二级分类点击事件
  secondClassifyTab(e) {
    let pkey = e.currentTarget.dataset.pkey;
    const id= e.currentTarget.id; 
    this.setData({
      page: -6,
      goodsList: [],
      activeClassifyTwo: pkey,
      scrollActiveClassifyTwo: id,
      queryClassify: this.data.activeClassify
    })
    if(this.data.activeClassifyTwo =='ALL') {
      this.loadAllData();
    }else {
      this.loadSecondData();
    }
   
  },
  // 获取二级分类商品列表
  loadSecondData(){
    var that = this,
    url = "/v4/app/market/goods/goodsMain/query";
  this.setData({
    loading: true,
      page: that.data.page  + that.data.pagesize,
  })
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + url,
    data: {
      goodsMain: that.data.activeClassifyTwo,
      from : that.data.page,
      limit: that.data.pagesize,
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
        // console.log(that.data.goodsList.length);
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
            hasMore: res.data.result.hasNext
          });


      }
      
    }
  });
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