// pages/shouyeGroup/goodsDeatil/index.js

const app = getApp()
import http from '../../../utils/http';
const { applyTheme } = require('../../../utils/themeMixin')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isClassify: false,// 是否从分类页跳转来
    totalNum: false,// 当前商品在购物车中的数量，如果是多规格或者没加入购物车则返回false
    imgUrl: app.globalData.file_url,
    goodPkey: "", //商品主键
    swiperCurre: 1, //当前轮播页
    swipeTotal: 3, //总共轮播页
    goodsInfo: {},
    content: [], //商品正文 
    isAuto: true,
    iShidden: true,
    selectSpaces: {
      pkey: "",
      price: "",
      priceOld: "",
      priceBig: "",
      priceMember: "",
      priceSmall: "",
      priceOldBig: "",
      priceOldSmall: "",
      kcNum: "",
      comm: ""
    },
    isIphoneX: false,
    recipeList: [], //相关菜谱
    kcNum: 10000, //可购买数量
    tjr: '',
    time: 0,
    timeData: {
      days: 0,
      hours: 0,
      seconds: 0
    },
    num: 1,
    orderPkey: 0,
    share_imageUrl: "",
    show: !1,
    hasShare: true,
    userInfo: app.globalData.userinfo,
    shareFarm: '', //分享的市场
    gwcNum: app.globalData.buycarNum,
    isMember: false,
    marketType: app.globalData.location.marketType,
    ProcessRadio: "",// 加工服务选中项
    recommendGoods: [], // 推荐商品列表
    isSpaceShow: false,
    isTabChanging: false,// 初始状态为未切换
    activeTab: 0,
    // 评论
    value: 3,
    lineEllipsis1: true, // 默认收起
    showBtn: false, // 初始不显示按钮
    textContent: "您的文本内容...",
    commentList: [],
    tabActive: 0,
    isLoading: true,
    hasMore: true,
    page: 0,
    pagesize: 6,
    enableComment: false,
    // end 评论
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
  onTabChange(e) {
    this.setData({
      isTabChanging: true, // 加锁
       activeTab: e.detail.index });
       console.log(e.detail.index)
    if (e.detail.index === 1) { // 第二个标签页
      this.loadComments(0);
    }
  },
  getCommentStatus() {
    const url= "/v1/app/market/index/config/ascription/get",that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        that.setData({
          enableComment: res.data.result.enableComment
        })
      }
    });

  },
  // 检测是否需要显示按钮
  calcExpandBtn() {
    const query = wx.createSelectorQuery().in(this);
   
    
    query.selectAll('.text-content').boundingClientRect(rects => {
      rects.forEach((rect, index) => {
        const lineHeight = 20; // 根据实际行高调整（单位：rpx）
        const maxHeight = lineHeight * 2; // 2行高度
        
        const showExpandBtn = rect.height > maxHeight;
        console.log(maxHeight, rect.height,showExpandBtn)
        this.setData({
          [`commentList[${index}].showExpandBtn`]: showExpandBtn,
          [`commentList[${index}].isExpand`]: showExpandBtn  
        });
        console.log(this.data.commentList)
      });
    }).exec();
  },
  loadComments(offset = 1) {
    if(offset == 0) {
      this.setData({
        commentList: [],
        page: 0,
        hasMore: true
      });
    }
    if(!this.data.hasMore) return;
    console.log("page",offset,this.data.page)
    const pkey = this.data.goodPkey,
    that = this,
    url = "/v1/app/market/goods/comment/query";
    this.setData({
      isLoading: true,
      hasMore: true,
      page: this.data.page,
    })
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey,
        page: this.data.page,
        pagesize: this.data.pagesize
      },
      success: function (res) {
        if (res.data.success) {
          const newList = res.data.result.content.map(item=> {
            item.isExpand = false;
            item.showExpandBtn = false;
            item.memberNameSecret= that.desensitizeNameRegex(item.memberName);
            return item;
          });

          that.setData({ commentList: [...that.data.commentList, ...newList] });
          wx.nextTick(()=> {
            setTimeout(()=> {
              that.calcExpandBtn();
            },0);
          })
          
         
          that.setData({
            hasMore: !res.data.result.last,
            isLoading: false,
            page: that.data.page+1,
            isTabChanging: false
          });
        }
      }
    });

    
  },
  desensitizeNameRegex(name) {
    if (!name) return '';
    return name.replace(/^(.).+$/, (match, p1) => `${p1}${'*'.repeat(match.length - 1)}`);
  },
  // 切换展开/收起
  toggleExpand(e) {
    const index = e.currentTarget.dataset.index;
    const newState = !this.data.commentList[index].isExpand;
    this.setData({
      [`commentList[${index}].isExpand`]: newState
    });
  },
   // 加工服务 选择事件
   handleProcessChecked(data){
    if(data.currentTarget.dataset.process  == this.data.ProcessRadio) {
      this.setData({
        ProcessRadio: ""
      })
      return;
    } 
    this.setData({
      ProcessRadio:  data.currentTarget.dataset.process
    })
  },
  /**关闭分享窗口 */
  handleColseShare() {
    this.setData({
      hasShare: false
    })
  },
  /**
   * @desc 放大观看图片
   */
  handlePreviewimg(event) {
    wx.previewImage({
      urls: [event.currentTarget.dataset.url],
    })
  },
  // 砍价分享弹窗
  handlePopup(e) {
    if (!app.globalData.userinfo) {
      wx.setStorage({
        data: e.detail.userInfo,
        key: 'userInfo',
      })
      app.globalData.userinfo = e.detail.userInfo
      this.setData({
        userInfo: e.detail.userInfo
      })
    }
    let that = this,
      params = {
        goods: e.currentTarget.dataset.pkey,
        num: 1
      }
    if (e.detail.userInfo) {
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v1/app/market/lm/order/initiate/cut',
        data: params,
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: function (res) {
          that.getDetail(that.data.goodPkey)
          if (res.data.success)
            that.setData({
              orderPkey: res.data.result.pkey,
              show: !0,
              share_imageUrl: e.currentTarget.dataset.img,
            })
          else
            wx.showToast({
              title: res.data.codeMsg || '',
              icon: 'none',
              duration: 2000
            })

        },
      })
    }


  },
  /**关闭砍价弹窗 */
  onClose() {
    this.setData({
      show: false
    })
  },
  /**
   * 获取用户信息
   */
  handleGetuserinfo(e) {
    let that = this;
    if (!app.globalData.userinfo) {
      wx.getUserProfile({
        desc: '用于分享显示用户昵称',
        success: function (res) {
          var userinfo = res.userInfo;
          wx.setStorage({
            data: userinfo,
            key: 'userInfo',
          })
          that.setData({
            userInfo: userinfo
          })
          app.globalData.userinfo = userinfo
        },

      })

    }
  },
  /**阻止冒泡 */
  stopBubble() {
    // console.log('阻止冒泡')
  },
  /**图片滚动 */
  swiperChange(event) {
    this.setData({
      swiperCurre: event.detail.current + 1
    });
  },
  /**点击轮播图放大 */
  handlePreviewImage(e) {
    const current = e.currentTarget.dataset.url
    const listName = e.currentTarget.dataset.listname
    let urls = this.data.goodsInfo[listName]
    if (typeof urls == 'string') {
      urls = [urls]
    }
    console.log(current, listName, urls);
    wx.previewImage({
      current, // 当前显示图片的http链接
      urls // 需要预览的图片http链接列表
    })
  },
  /**跳转到购物车 */
  goCart() {
    // wx.switchTab({
    //   url: '../../home/buyCar/index',
    // });
    wx.navigateTo({
      url: '/pages/shouyeGroup/buyCar/index',
    })
  },
  goRecipe: function (event) {
    var pkey = event.currentTarget.dataset.pkey;
    wx.navigateTo({
      url: '/pages/shouyeGroup/recipe/detail?pkey=' + pkey
    });
  },
  /**
   * 获取相关菜谱
   * @param pkey 商品pkey值
   */
  getRecipe: function (pkey) {
    const that = this,
      url = "/v1/app/market/goods/cookfd/query/related",
      params = {
        goods: pkey
      };
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
        if (res.data.success) {
          that.setData({
            recipeList: res.data.result
          })
        }
      }
    });
  },
  /**详情信息请求 */
  getDetail(pkey, spacePkey = '') {
    var _this = this;
    var parame = {
        pkey: pkey
      },
      url = this.data.isMember ? '/v1/app/market/goods/get/member' : '/v1/app/market/goods/get';
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
          var selectSpaces = {
            pkey: "",
            price: "",
            priceOld: "",
            priceMember: "",
            priceBig: "",
            priceSmall: "",
            point: "",
            priceOldBig: "",
            priceOldSmall: "",
            comm: ""
          };

          if (res.data.result.spaces && res.data.result.spaces.length != 0) {
            let spaceIndex = 0;

              res.data.result.spaces.map((item, index) => {
                if ((item.pkey == spacePkey && item.kcNum > 0)  || (item.kcNum > 0 && spaceIndex == 0)) {
                  spaceIndex = index;
                } 
              });

            let price = res.data.result.spaces[spaceIndex].price + "",
              arr = price.split("."),
              priceOld = res.data.result.spaces[spaceIndex].priceOld + "",
              arrOld = priceOld.split(".");

            selectSpaces = {
              pkey: res.data.result.spaces[spaceIndex].pkey,
              price: res.data.result.spaces[spaceIndex].price,
              priceOld: res.data.result.spaces[spaceIndex].priceOld,
              priceMember: res.data.result.spaces[spaceIndex].priceMember,
              priceBig: arr[0],
              point: res.data.result.spaces[spaceIndex].point,
              priceOldBig: arrOld[0],
              kcNum: res.data.result.spaces[spaceIndex].kcNum,
              comm: res.data.result.spaces[spaceIndex].comm
            };
            if (arr.length == 2) {
              selectSpaces.priceSmall = "." + arr[1];
            }
            if (arrOld.length == 2) {
              selectSpaces.priceOldSmall = "." + arrOld[1];
            }


          }
          if (res.data.result.content2)
            res.data.result.content2 = res.data.result.content2.replace(/\<img/gi, '<img class="rich-img" ');
          _this.setData({
            goodsInfo: res.data.result,
            time: res.data.result.remainingTime ? res.data.result.remainingTime : 0,
            swiperCurre: res.data.result.photo1 != null ? 1 : 0, //当前轮播页
            swipeTotal: res.data.result.photo1 != null ? res.data.result.photo1.length : 0, //总共轮播页
            selectSpaces: selectSpaces
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

  /**规格选择 */
  tagClick(event) {
    console.log('--tagClick--');
    var item = event.currentTarget.dataset.id;
    if(item.kcNum < 1) return;
    var selectSpaces = {
      pkey: "",
      price: "",
      priceOld: "",
      priceBig: "",
      priceMember: "",
      priceSmall: "",
      point: "",
      priceOldBig: "",
      priceOldSmall: "",
      comm: "",
    };

    var price = item.price + "";
    var arr = price.split(".");
    var priceOld = item.priceOld + "";
    var arrOld = priceOld.split(".");
    selectSpaces = {
      pkey: item.pkey,
      price: item.price,
      priceOld: item.priceOld,
      priceMember: item.priceMember,
      priceBig: arr[0],
      point: item.point,
      priceOldBig: arrOld[0],
      kcNum: item.kcNum,
      comm: item.comm
    };

    if (arr.length == 2) {
      selectSpaces.priceSmall = "." + arr[1];
    }
    if (arrOld.length == 2) {
      selectSpaces.priceOldSmall = "." + arrOld[1];
    }
    this.setData({
      selectSpaces: selectSpaces,
      kcNum: item.kcNum
    });
  },
  /**
   * 数量选择 
   */
  stepChange: function (value) {
    this.setData({
      num: value.detail
    });
  },
  //倒计时
  onChange(e) {
    this.setData({
      timeData: e.detail,
    });
    if (e.detail.days == 0 && e.detail.hours == 0 &&
      e.detail.minutes == 0 && e.detail.seconds == 0) {
      this.getDetail(this.data.goodPkey);
      this.getRecipe(this.data.goodPkey);
    }
  },
  /**
   * 添加到购物车
   */
  handleAddTOCart: function () {
    var url = "/v1/app/market/lm/member/gwc/ins",
      that = this;
    if (this.data.selectSpaces.kcNum < this.data.num) {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
      return
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: this.data.goodPkey,
        goodsNum: this.data.num,
        space: this.data.selectSpaces.pkey,
        association: this.data.ProcessRadio
      },
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
          wx.showToast({
            title: '已加入购物车',
            icon: 'none'
          });
          if(that.data.goodsInfo.spaces.length == 1) {
            that.setData({
              totalNum: res.data.result
            })
          }
          app.getBuycarNum();
          that.setData({
            gwcNum: that.data.gwcNum + 1
          })
        } else {
          if(res.data.code == 'lejia-0132') {
            that.setData({   
              num: 1,
            });
          }
          
          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
      }
    });
  },


  /**
   * 立即购买
   */
  goSubmit: function () {
    console.log(this.data.goodsInfo);
    if(this.data.selectSpaces.pkey == '') {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
      return;
    }
    if(this.data.goodsInfo.mtype == "INTEGRAL_GOODS" || this.data.goodsInfo.mtype == "INTEGRAL_BNYP_GOODS" || this.data.goodsInfo.mtype == "INTEGRAL_PRESALE_GOODS" || this.data.goodsInfo.mtype == "INTEGRAL_MSD_GOODS") {
      this.goComboPay()
      return
    }
    var that = this,
      url = "/v2/app/market/lm/order/buyGoods",
      params = {
        association: this.data.ProcessRadio,//加工服务
        space: this.data.selectSpaces.pkey, //规格pkey
        num: this.data.num, //商品数量
        tjr: this.data.tjr, //推荐人
        dineIn: app.globalData.qrCode ? true : false,
        latitude: wx.getStorageSync('latitude'),
        longitude: wx.getStorageSync('longitude'),
      };
    if (this.data.goodsInfo.mtype == "CUT_GOODS" && this.data.goodsInfo.isCut) {
      let time = new Date(this.data.goodsInfo.endDate.replace('-', '/')).getTime() - new Date().getTime()
      if (time < 1)
        return wx.showToast({
          title: '砍价活动已结束!',
          icon: 'none',
          duration: 2000
        })
      url = "/v2/app/market/lm/order/getUnpaidOrder";
      params = {
        pkey: this.data.orderPkey
      };
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/pay/index?type=goods&space='+this.data.selectSpaces.pkey+ '&num='+this.data.num+'&tjr='+this.data.tjr+'&shareFarm=' + JSON.stringify(that.data.shareFarm)+ '&association='+this.data.ProcessRadio
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          })
        }


      }
    })
  },
  /**新组合支付页 */
  goComboPay() {
    let that = this
    let url = "/v3/app/market/lm/order/buyGoods"
    let params = {
      association: this.data.ProcessRadio,//加工服务
      space: this.data.selectSpaces.pkey, //规格pkey
      num: this.data.num, //商品数量
      tjr: this.data.tjr, //推荐人
      dineIn: app.globalData.qrCode ? true : false,
      latitude: wx.getStorageSync('latitude'),
      longitude: wx.getStorageSync('longitude'),
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/comboPay/index?type=goods&space='+this.data.selectSpaces.pkey+ '&num='+this.data.num+'&tjr='+this.data.tjr+'&shareFarm=' + JSON.stringify(that.data.shareFarm)+ '&association='+this.data.ProcessRadio + '&mtype=' + this.data.goodsInfo.mtype
          });
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          })
        }


      }
    })
  },

  /**
   * 加入收藏夹
   */
  addCollection: function () {
    if (this.data.goodsInfo.collection) {
      this.handleDelete();
      return;
    }
    var that = this,
      url = "/v1/app/market/goods/collection/ins";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ctype: 1,
        objKey: this.data.goodPkey
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
            ["goodsInfo.collection"]: true,
            ["goodsInfo.collectionPkey"]: res.data.result
          });
          wx.showToast({
            title: "商品收藏成功",
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
        pkey: this.data.goodsInfo.collectionPkey
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            ["goodsInfo.collection"]: false
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
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    applyTheme(this)
    this.setData({
      gwcNum: app.globalData.buycarNum,
      ascription: app.globalData.ascription
    })
    let spacePkey = '';
    if (app.globalData.mobileModel.indexOf('iPhone') > -1) {
      this.setData({
        isIphoneX: app.globalData.isIphoneX
      })
    }
    if (options.hasOwnProperty('shareFarm')) {
      this.setData({
        shareFarm: JSON.parse(options.shareFarm)
      });
    }
    if (options.hasOwnProperty('isClassify')) {
      this.setData({
        isClassify: JSON.parse(options.isClassify)
      });
    }
    console.log("isClassify",typeof this.data.isClassify, this.data.isClassify)
    this.setData({
      userInfo: app.globalData.userinfo
    });
    if (options.hasOwnProperty('orderPkey')) {
      this.setData({
        orderPkey: parseInt(options.orderPkey)
      });
    }
    if (options.hasOwnProperty('isMember')) {
      this.setData({
        isMember: true
      });
    }
    if (options.hasOwnProperty('space')) {
      spacePkey = options.space
    }
    if(options.q) {
      const q = decodeURIComponent(options.q);
      const pkey = utils.getQueryString(q, 'pkey');
        this.setData({
          goodPkey:pkey
        });     
    }
    console.log("options",options)
    if(options.pkey) {
      this.setData({
        goodPkey:options.pkey
      });   
    }
    this.setData({
      tjr: options.tjr ? options.tjr : ''
    });
    this.getCommentStatus();
    this.getDetail(this.data.goodPkey, spacePkey);
    this.getRecipe(this.data.goodPkey);
    this.load();

    this.getRecommendGoods(this.data.goodPkey)
  },

  /**
   * 跳转到商户详情
   */
  goMerchantDetail: function (data) {
    wx.navigateTo({
      url: `/pages/shouyeGroup/merchant/index?pkey=${data.currentTarget.dataset.pkey}`
    })
  },

  /**
   * 获取推荐商品列表Start
   */
  getRecommendGoods(pkey) {
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v1/app/market/goods/recommend/query',
        data: {
            page: 0,
            pagesize: 999,
            zone: 'GOODS_DETAIL',
            sourceGoods: pkey,
        },
        success: (res) => {
            this.setData({
                recommendGoods: res.data.result.content
            })
        }
    })
  },

  goRecommendGoods(data) {
    wx.navigateTo({
        url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${data.currentTarget.dataset.pkey}`
    })
  },

  addRecommendGoodsByBuyCar(data) {
    console.log(data);
    const { goods, index} = data.currentTarget.dataset
    console.log(goods, index);
    if(goods.spaces.length > 1) {
        const spaceView  = this.selectComponent("#spaceView");
        spaceView.getData(goods.pkey);
        this.setData({
            isSpaceShow: true
        })
        return
    }
    const params = {
        goodsPkey: goods.pkey,
        space: goods.spaces[0].pkey,
        goodsNum: 1,
    }
    http.request({
        method: 'POST',
        url: app.globalData.ajax_url + '/v1/app/market/lm/member/gwc/ins',
        data: params,
        success: (res) => {
            console.log(res);
            if(res.data.success) {
                app.getBuycarNum();
                this.setData({
                    gwcNum: this.data.gwcNum + 1
                })
                const recommendGoods = this.data.recommendGoods
                recommendGoods[index].gwcNum++
                this.setData({
                    recommendGoods
                })
            } else {
                wx.showToast({
                    title: res.data.msg || '',
                    icon: "none"
                })
            }
        }
    })
  },

  handleAddRecommendTOCart(data) {
    const {goodsPkey, num} = data.detail
    let {recommendGoods, gwcNum} = this.data
    const index = recommendGoods.findIndex(item => item.pkey === goodsPkey)
    gwcNum = gwcNum +  num - recommendGoods[index].gwcNum
    recommendGoods[index].gwcNum = num
    this.setData({
        recommendGoods,
        gwcNum
    })
  },
  /**
   * 获取推荐商品列表End
   */

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.setData({
      marketType: app.globalData.location.marketType
    });
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    console.log(212121212121212)
    
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  load: function () {
    // console.log(44444444444444, this.data.isClassify)
    if(this.data.isClassify) {
      var pages = getCurrentPages();
      var prevPage = pages[pages.length - 2]; //上一个页面
      console.log(prevPage)
      prevPage.setData({
        isRefresh: false,
        refreshData: {
          pkey: this.data.goodPkey, 
          gwcNum: this.data.totalNum
        }
      })
    }
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
    if (this.data.isTabChanging) return; // 切换中不执行加载
    console.log("activeTab", this.data.activeTab)
    if(this.data.hasMore && this.data.activeTab == '1') {
      this.loadComments();
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    let shareFarm = {
      name: app.globalData.location.name,
      pkey: app.globalData.location.pkey,
    };
    if (this.data.goodsInfo.mtype == "CUT_GOODS")
      return {
        title: `${app.globalData.userinfo.nickName}正在砍价，快来祝他一臂之力！`,
        path: `/pages/my/bargainDetail/index?pKey=${this.data.orderPkey}&shareFarm=${JSON.stringify(shareFarm)}`,
        imageUrl: this.data.goodsInfo.photo1[0], //用户分享出去的自定义图片大小为5:4,
        success: function (res) {
          // 转发成功
          wx.showToast({
            title: "分享成功",
            icon: 'success',
            duration: 2000
          })
        },
        fail: function (res) {
          // 分享失败
        },
      }
    else if (this.data.goodsInfo.mtype == "COLLAGE_GOODS") {
      return {
        title: `${app.globalData.userinfo.nickName}正在拼团，一起拼团超值好货！`,
        path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + this.data.goodPkey + '&tjr=' + app.globalData.openid + '&shareFarm=' + JSON.stringify(shareFarm),
        imageUrl: this.data.goodsInfo.wrapperPhoto, //用户分享出去的自定义图片大小为5:4,
        success: function (res) {
          // 转发成功
          wx.showToast({
            title: "分享成功",
            icon: 'success',
            duration: 2000
          })
        },
        fail: function (res) {
          // 分享失败
        },
      }
    } else {
      return {
        title: this.data.goodsInfo.title,
        path: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + this.data.goodPkey + '&tjr=' + app.globalData.openid + '&shareFarm=' + JSON.stringify(shareFarm),
        imageUrl: this.data.goodsInfo.wrapperPhoto, //用户分享出去的自定义图片大小为5:4,
        success: function (res) {
          // 转发成功
          wx.showToast({
            title: "分享成功",
            icon: 'success',
            duration: 2000
          })
        },
        fail: function (res) {
          // 分享失败
        },
      }
    }

  }
})