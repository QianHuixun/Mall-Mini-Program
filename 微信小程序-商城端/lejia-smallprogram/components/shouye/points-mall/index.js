// components/points-mall/index.js
import http from '../../../utils/http'
var spaceView;
const app = getApp();
Component({
  /**
   * 组件的属性列表
   */
  properties: {

  },

  /**
   * 组件的初始数据
   */
  data: {
    goodsList: [],
    isShow: false, //规格弹出显示
    iShidden: true,
    name: '积分商城',
  },

  pageLifetimes: {
    // 组件所在页面的生命周期函数
    show: function () {
      this.loadData()
      spaceView = this.selectComponent("#spaceView");
      this.setData({
        name: app.globalData.ascription === 13 ? '滨海民生自营区' : '积分商城'
      })
    },
  },

  /**
   * 组件的方法列表
   */
  methods: {
    /**
     * 获取商品列表
     */
    loadData: function () {
      console.log('----loadData----');
      var that = this,
        url = "/v1/app/market/goods/query";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          mType: "INTEGRAL_GOODS",
          page: 0,
          pagesize: 20
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({
              goodsList: res.data.result.content
            })
          }
        }
      });
    },

    /**
     * 跳转到积分商城
     */
    goIntegral() {
      wx.navigateTo({
        url: '/pages/home/integral/index',
      })
    },
    /**商品点击进入详情 */
    goodsClick(event) {
      var item = event.currentTarget.dataset.id;
      wx.navigateTo({
        url: '../../shouyeGroup/goodsDeatil/index?pkey=' + item.pkey,
      });
    },
    /**
     * 获取当前商品的规格数量
     */
    getSpaceNumber: function (data) {
      var url = "/v1/app/market/goods/space/totalAmount",
        item = data.currentTarget.dataset.id,
        pkey = data.currentTarget.dataset.pkey,
        space = data.currentTarget.dataset.space,
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
            if (item.mtype === "COUPON_GOODS" || item.mtype === "GIFT_GOODS") {
              that.handlePay(data, pkey, space)
            } else {
              that.handleAddTOCart(data, pkey, space);
            }
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
     * 添加到购物车
     */
    handleAddTOCart: function (data, pkey, space) {
      let params;
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
      var url = "/v1/app/market/lm/member/gwc/ins",
        that = this;
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
     * 去支付页结算
     */
    handlePay(data, pkey, space) {
      console.log(data, pkey, space);
      let url = "/v2/app/market/lm/order/buyGoods"
      let params = {
        space, //规格pkey
        num: 1, //商品数量
        dineIn: app.globalData.qrCode ? true : false
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
        success: (res) => {
          console.log(res);
          if (res.data.success) {
            wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
            wx.navigateTo({
              url: '/pages/pay/pay/index?type=goods&space=' + space + '&num=1'
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
  }
})