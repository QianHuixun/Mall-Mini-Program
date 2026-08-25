// pages/home/newMsd/index.js
const app = getApp();
import http from "../../../utils/http";
const { applyTheme } = require("../../../utils/themeMixin");
import { onClickEffect } from "../../../utils/util";
Page({
  /**
   * 页面的初始数据
   */
  data: {
    theme: null,
    statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
    mtype: null,
    imageList: [],
    classifyList: [], //一级分类列表
    classifyTwoList: [], //二级分类列表
    isShowAllClassify: false,
    activeClassify: "", //高亮一级分类pkey
    queryClassify: "", //点击一级分类用于接口获取数据的pkey
    scrollActiveClassify: "", //scroll-view 滚动到该id
    activeClassifyTwo: "", //高亮二级分类pkey
    scrollActiveClassifyTwo: "", //scroll-view 滚动到该id
    classifyImageList: [], // 二级分类广告图
    page: 0,
    pagesize: 6,
    goodsSortType: null, // SALED 销量 PRICE 价格
    sortDesc: false,
    hotSort: 0, // 销量排序 0-不排序/1-降序/2-升序
    priceSort: 0, // 价格排序 0-不排序/1-降序/2-升序
    goodsList: [], //商品列表
    isLoading: false, // 是否加载中
    hasMore: true,
    showHint: false, //是否显示文字提示
    isEnd: false, // 是否是最后一个分类
    isBottom: false,
    startY: 0, // 商品列表初始Y轴高度
    isLastFirstClassify: false,
    isLastSecondClassify: false,
    lastScrollTime: null, //滚动节流
    isShow: false, //是否显示规格选择dialog
    iShidden: true,
    loadRequest: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    applyTheme(this);
    if (options.mtype) {
      this.setData({ mtype: options.mtype });
    }
    this.getDisPlayName();
    this.getImageList();
    this.getClassify();
  },


  /**
   * 获取专区名称
   */
  getDisPlayName() {
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/index/zone/config/get",
      success: (res) => {
        const data = res.data.result;
        if (data.integralMsdDisplayName) {
          wx.setNavigationBarTitle({
            title: data.integralMsdDisplayName,
          });
        }
      },
    });
  },

  /**广告列表 */
  getImageList() {
    var _this = this;
    var parame = {
      position: "ADVERT_POSITION_MSD",
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/img/query",
      data: parame,
      header: {
        "content-type": "application/x-www-form-urlencoded;charset=UTF-8",
        openid: app.globalData.openid,
        farmer: app.globalData.location.pkey,
      },
      success: function (res) {
        if (res.data.success) {
          var imageList = res.data.result.map((item) => {
            item.isTabbar = !1;

            if (item.urlType == "NOT_URL") {
              item.url = "";
            } else if (item.urlType == "LINK") {
              item.url = item.objKey;
            } else if (item.urlType == "POINTS_MALL") {
              item.isTabbar = !0;
              item.url = "/pages/home/integral/index";
            } else if (item.urlType == "MEMBERSHIP") {
              item.url = "/pages/my/openVip/index";
            } else if (item.urlType == "GOODS") {
              item.url =
                "/pages/shouyeGroup/goodsDeatil/index?pkey=" + item.objKey;
            } else if (item.urlType == "ACTIVITY") {
              item.url = "/pages/activity/coupon/index?pkey=" + item.objKey;
            } else if (item.urlType == "PERSONAL_CENTER") {
              item.url = item.objKey;
              item.isTabbar = !0;
            } else {
              item.url = item.objKey;
            }
            return item;
          });
          _this.setData({
            imageList: imageList,
          });
        }
      },
    });
  },

  /**轮播广告点击事件 */
  goAds: function (data) {
    onClickEffect(data);
  },

  /**
   *@desc 获取一级商品分类列表
   */
  getClassify: function () {
    var that = this,
      url = "/v1/app/market/goods/gtype/mall/twoLevels/list";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        mtype: this.data.mtype,
      },
      success: function (res) {
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
                activeClassify:
                  res.data.result.length > 0 ? res.data.result[0].pkey : "",
                queryClassify:
                  res.data.result.length > 0 ? res.data.result[0].pkey : "",
              });
            }
          } else {
            that.setData({
              activeClassify:
                res.data.result.length > 0 ? res.data.result[0].pkey : "",
              queryClassify:
                res.data.result.length > 0 ? res.data.result[0].pkey : "",
            });
          }

          if (that.data.activeClassify) {
            that.getClassifyTwo();
          }
        } else {
          wx.showToast({
            title: res.data.msg || "",
            icon: 'none'
          });
        }
      },
    });
  },
  // 显示全部一级分类
  showAllClassify() {
    this.setData({
      isShowAllClassify: true,
    });
  },
  // 隐藏全部一级分类
  hideAllClassify() {
    this.setData({
      isShowAllClassify: false,
    });
  },
  // 商品一级分类点击事件
  firstClassifyTab(e) {
    let pkey = e.currentTarget.dataset.pkey;
    const id = e.currentTarget.id;
    this.flag = true; // 修复点击分类过快时，会因为滚动条的的滑动，调用scroll触发瞄点
    // 联动右边
    this.setData({
      activeClassify: pkey,
      queryClassify: pkey,
      activeClassifyTwo: "",
      scrollActiveClassify: id,
      page: 0,
      goodsList: [],
      showHint: false,
      isLoading: false,
      isBottom: false,
      isShowAllClassify: false,
      hasMore: true,
    });
    this.getClassifyTwo();
  },
  /**
   * @desc 获取二级分类列表
   */
  getClassifyTwo() {
    const { classifyList, activeClassify, activeClassifyTwo } = this.data;
    const classifyTwoList = classifyList.find((item) => {
      return item.pkey == activeClassify;
    })?.second;
    this.setData({
      classifyTwoList,
    });
    if (!classifyTwoList.find((item) => item.pkey == activeClassifyTwo)) {
      this.setData({
        activeClassifyTwo:
          classifyTwoList.length > 0 ? classifyTwoList[0].pkey : "",
      });
    }
    if (!classifyTwoList.length) {
      this.setData({
        goodsList: [],
        page: 0,
        hasMore: true,
      });
    }
    this.loadSecondData();
    this.getPicture();
  },

  // 二级分类点击事件
  secondClassifyTab(e) {
    let pkey = e.currentTarget.dataset.pkey;
    const id = e.currentTarget.id;
    this.setData({
      page: 0,
      hasMore: true,
      goodsList: [],
      activeClassifyTwo: pkey,
      scrollActiveClassifyTwo: id,
      queryClassify: this.data.activeClassify,
      isBottom: false,
    });
    this.getPicture();
    this.loadSecondData();
  },

  // 获取分类图片
  getPicture: function () {
    var that = this,
      url = "/v1/app/market/img/query";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        position: "ADVERT_POSITION_MSD_GOODS_MAIN",
        positionObj: this.data.activeClassifyTwo,
      },
      header: {
        "content-type": "application/x-www-form-urlencoded;charset=UTF-8",
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            classifyImageList: res.data.result,
          });
        }
      },
    });
  },

  // 获取二级分类商品列表
  loadSecondData() {
    const {
      hasMore,
      page,
      pagesize,
      mtype,
      activeClassify,
      activeClassifyTwo,
      hotSort,
      priceSort,
      loadRequest,
    } = this.data;
    if (!hasMore || loadRequest) return;
    const url = "/v1/app/market/goods/mall/query";
    this.setData({
      isLoading: true,
      hasMore: true,
      loadRequest: true,
    });
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        page,
        pagesize,
        mtype,
        gtype: activeClassify,
        goodsMain: activeClassifyTwo,
        hotSort,
        priceSort,
      },
      success: (res) => {
        this.setData({
          loadRequest: false
        })
        if (res.data.success) {
          if (res.data.result.last) {
            this.setData({
              hasMore: false,
              isLoading: false,
              showHint: true,
            });
          }
          this.setData({
            page: page + 1,
          });
          // 价格处理，将价格存在2个字段中，用于大小价格的显示
          if (!res.data.result.content.length) {
            this.setData({
              hasMore: false,
              isLoading: false,
              showHint: true,
            });
            return;
          }
          var resultGroup = res.data.result.content.map((item) => {
            var price = item.price.toFixed(2).toString().split(".");
            item.bigPrice = price[0];
            item.smallPrice = price[1];
            return item;
          });
          //当 商品列表长度不为0，则循环商品列表并插入新列表
          const goodsList = this.data.goodsList;
          if (goodsList.length) {
            this.setData({
              goodsList: goodsList.concat(resultGroup),
            });
          } else {
            this.setData({
              goodsList: resultGroup,
            });
          }
          this.setData({
            hasMore: !res.data.result.last,
            isLoading: false,
          });
          if (!this.data.hasMore && this.data.goodsList.length <= 6) {
            this.setData({ showHint: true });
          }
          this.handleIsEnd();
          wx.nextTick(() => {
            const query = this.createSelectorQuery();
            query.select(".scroller").boundingClientRect();
            query.select(".scroller-container").boundingClientRect();
            query.exec((rect) => {
              if (rect[0].height > rect[1].height) {
                // 内容高度 < 容器高度
                this.setData({ isBottom: true }); // 主动标记触底
              }
            });
          });
        } else {
          wx.showToast({
            title: res.data.msg || "",
            icon: 'none'
          });
        }
      },
    });
  },

  // 判断是否为最后一个分类
  handleIsEnd() {
    const { classifyList, classifyTwoList, activeClassify, activeClassifyTwo } =
      this.data;

    // 使用findIndex代替forEach
    const firstIndex = classifyList.findIndex(
      (item) => item.pkey === activeClassify,
    );
    const secondIndex = classifyTwoList.findIndex(
      (item) => item.pkey === activeClassifyTwo,
    );

    this.setData({
      isLastFirstClassify: firstIndex === classifyList.length - 1,
      isLastSecondClassify: secondIndex === classifyTwoList.length - 1,
      isEnd:
        firstIndex === classifyList.length - 1 &&
        secondIndex === classifyTwoList.length - 1,
    });
  },

  /**
   * @desc 商品页滚地事件
   * @param {*} e
   * @returns
   */
  onScroll(e) {
    if (this.flag) {
      this.flag = false;
      return;
    }
    if (this.data.isLoading) return;
    let query = this.createSelectorQuery();
    query.selectAll(".scroller").boundingClientRect();
    query.exec((rect) => {
      const clientHeight = rect[0][0].height; // 可视区高度
      const { scrollTop, scrollHeight } = e.detail;
      const threshold = 100; // 距底部阈值
      // 判断是否触发加载
      if (scrollTop + clientHeight >= scrollHeight - threshold) {
        if (this.data.hasMore) {
          this.setData({ isLoading: true });
          this.loadSecondData();
        }
      }
    });
  },

  // 记录起始位置
  handleTouchStart(e) {
    if (!this.data.isBottom || this.data.hasMore) return;
    this.setData({ startY: e.touches[0].clientY });
  },

  // 实时计算滑动距离
  handleTouchEnd(e) {
    if (!this.data.isBottom || this.data.hasMore) return;
    const currentY = e.changedTouches[0].clientY;
    const deltaY = this.data.startY - currentY; // 上滑时 deltaY > 0
    if (deltaY > 100) {
      // 上滑超过阈值
      this.setData({ startY: 0 });
      this.switchToNextCategory(); // 切换分类
    }
  },

  // 滚动到底部触发
  scrollGoodsBottom() {
    if (!this.data.hasMore) {
      this.setData({ isBottom: true });
    }
  },

  // 滑动切换到下一个分类
  switchToNextCategory() {
    if (this.data.isEnd) return;
    this.setData({
      scrollTop: 0, // 重置滚动距离
    });
    let currentCategoryIndex = 0,
      SecondPkey = "",
      firstPkey = "";
    if (!this.data.isLastSecondClassify) {
      this.data.classifyTwoList.forEach((item, index) => {
        if (item.pkey == this.data.activeClassifyTwo) {
          currentCategoryIndex = index;
        }
      });
      SecondPkey = this.data.classifyTwoList[currentCategoryIndex + 1].pkey;
      this.setData({
        page: 0,
        goodsList: [],
        activeClassifyTwo: SecondPkey,
        queryClassify: this.data.activeClassify,
        showHint: false,
        isLoading: false,
        isBottom: false,
        hasMore: true,
      });
      this.scrollToSecondClassify();
      this.getPicture();
      // this.loadSecondData();
    } else {
      this.data.classifyList.forEach((item, index) => {
        if (item.pkey == this.data.activeClassify) {
          currentCategoryIndex = index;
        }
      });
      firstPkey = this.data.classifyList[currentCategoryIndex + 1].pkey;
      this.setData({
        activeClassify: firstPkey,
        queryClassify: firstPkey,
        activeClassifyTwo: "",
        page: 0,
        goodsList: [],
        showHint: false,
        isLoading: false,
        hasMore: true,
        isBottom: false,
      });
      this.scrollToFirstClassify();
      this.getClassifyTwo();
    }
  },

  // 滑动到指定为止
  scrollToFirstClassify() {
    // 在自定义组件内部
    const query = this.createSelectorQuery();
    query
      .select(".scroll-" + this.data.activeClassify)
      .boundingClientRect((rect) => {
        this.createSelectorQuery()
          .select(".first-scroll")
          .node()
          .exec((res) => {
            const scrollView = res[0].node;
            scrollView.scrollTo({ left: rect.left }); // 滚动到目标位置
          });
      })
      .exec();
  },

  // 滑动到指定位置
  scrollToSecondClassify() {
    // 在自定义组件内部
    const query = this.createSelectorQuery();
    query
      .select(
        ".scroll-" +
          this.data.activeClassify +
          "-" +
          this.data.activeClassifyTwo,
      )
      .boundingClientRect((rect) => {
        this.createSelectorQuery()
          .select(".second-scroller")
          .node()
          .exec((res) => {
            const scrollView = res[0].node;
            scrollView.scrollTo({ top: rect.top }); // 滚动到目标位置
          });
      })
      .exec();
  },

  /**
   * 获取当前商品的规格数量
   */
  getSpaceNumber(data) {
    var url = "/v1/app/market/goods/space/totalAmount",
      pkey = data.currentTarget.dataset.pkey,
      space = data.currentTarget.dataset.space,
      source = data.currentTarget.dataset.source || "",
      that = this;
    if (source == "JD") {
      this.handleJDAddTOCart(data);
      return;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: pkey,
      },
      success: function (res) {
        if (res.data.result == 1) {
          that.handleAddTOCart(data, pkey, space);
        } else {
          that.setData({
            isShow: true,
          });
          const spaceView = that.selectComponent("#spaceView");
          if (spaceView) spaceView.getData(pkey);
        }
      },
    });
  },
  /**
   *@desc 添加到购物车
   * @param pkey 商品pkey
   * @param space 商品规格pkey
   * @param index 商品列表下标
   */
  handleAddTOCart(data, pkey, space) {
    console.log('handleAddTOCart');
    
    var url = "/v1/app/market/lm/member/gwc/ins",
      that = this,
      params;
    if (data.detail.hasOwnProperty("data")) {
      params = {
        goodsPkey: data.detail.goodsPkey,
        goodsNum: 1,
        space: data.detail.space,
      };
      data = data.detail.data;
    } else
      params = {
        goodsPkey: pkey,
        goodsNum: 1,
        space: space,
      };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      success: function (res) {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false,
          });
          return;
        }
        if (res.data.success) {
          app.getBuycarNum();
          const { goodsList } = that.data
          const index = goodsList.findIndex(item => item.pkey === pkey)
          goodsList[index].gwcNum++
          that.setData({goodsList})
          wx.showToast({
            title: "已加入购物车",
            icon: "none",
          });
        } else {
          wx.showToast({
            title: res.data.msg || "",
            icon: "none",
          });
        }
      },
    });
  },

  /**
   * 添加京东商品到购物车
   */
  handleJDAddTOCart(data) {
    const { pkey, id } = data.currentTarget.dataset;
    let goodsNum = 1;
    if (id.lowestBuy && id.lowestBuy > id.gwcNum) {
      goodsNum = id.lowestBuy;
    }
    const params = {
      pkey,
      goodsNum,
      latitude: wx.getStorageSync("latitude"),
      longitude: wx.getStorageSync("longitude"),
    };
    const url = "/v1/app/jd/goods/gwc/ins";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      success: (res) => {
        if (res.data.code == "999") {
          this.setData({
            iShidden: false,
          });
          return;
        }
        if (res.data.success) {
          app.getBuycarNum();
          const { goodsList } = this.data;
          const index = goodsList.findIndex((item) => item.pkey === pkey);
          goodsList[index].gwcNum += goodsNum;
          this.setData({
            goodsList,
          });
        } else {
          wx.showToast({
            title: res.data.msg || "",
            icon: "none",
          });
        }
      },
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
      subIndex = data.currentTarget.dataset.subindex,
      newValue = data.detail; //修改后的值
    if (this.data.goodsList[subIndex].gwcNum < newValue) {
      url = url_add;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: this.data.goodsList[subIndex].pkey,
        space: this.data.goodsList[subIndex].spaces[0].pkey,
        goodsNum: 1,
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        openid: app.globalData.openid,
        farmer: app.globalData.location.pkey,
      },
      success: (res) => {
        if (res.data.success) {
          that.setData({
            [`goodsList[${subIndex}].gwcNum`]: newValue,
          });
          app.getBuycarNum();
        } else {
          that.setData({
            [`goodsList[${subIndex}].gwcNum`]: newValue - 1,
          });
          wx.showToast({
            title: res.data.msg || "",
            icon: "none",
          });
        }
      },
    });
  },

  // 排序
  handleSort(e) {
    const type = e.currentTarget.dataset.type;
    const { goodsSortType, hotSort, priceSort } = this.data;
    if (type != goodsSortType) {
      this.setData({
        hotSort: 0,
        priceSort: 0,
        sortDesc: true,
        goodsSortType: type,
      });
      if (type == "SALED") {
        this.setData({
          hotSort: 1,
        });
      } else {
        this.setData({
          priceSort: 1,
        });
      }
    } else {
      if (type == "SALED") {
        const sort = hotSort + 1 > 2 ? 0 : hotSort + 1;
        this.setData({
          hotSort: sort,
        });
      } else {
        const sort = priceSort + 1 > 2 ? 0 : priceSort + 1;
        this.setData({
          priceSort: sort,
        });
      }
    }
    this.setData({
      page: 0,
      hasMore: true,
      goodsList: [],
      queryClassify: this.data.activeClassify,
      activeClassifyTwo: this.data.activeClassifyTwo,
    });
    this.loadSecondData();
  },

  /**
   * 跳转到商品详情页
   */
  goDetail: function (data) {
    if (data.currentTarget.dataset.source == "JD") {
      wx.navigateTo({
        url: `/pages/shouyeGroup/jdGoodsDetail/index?pkey=${data.currentTarget.dataset.pkey}`,
      });
      return;
    }
    wx.navigateTo({
      url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${data.currentTarget.dataset.pkey}`,
    });
  },
});
