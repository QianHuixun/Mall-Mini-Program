// pages/shouyeGroup/position/index.js
import QQMapWX from '../../../utils/qqmap-wx-jssdk.min.js';
import http from '../../../utils/http'
import mapKey from '../../../utils/map-key.js';
const app = getApp();
// 实例化API核心类
let qqMap = new QQMapWX({
  key: 'IGCBZ-MWAK3-DA633-YA7GK-BNZG7-ZHFSG' // 必填
});

Page({

  /**
   * 页面的初始数据
   */
  data: {
    value: "", //搜索
    addressValue: "", //区域
    areaValue: "", //选择的省市区
    addressDetailValue: "", //当前定位
    lon: "",
    lat: "",
    areaList: [], //弹出区域列表
    show: false, //是否弹出区域
    page: 0,
    marketList: [], //市场列表
    loadingBom: false,
    noMoreBom: false,
    hasLocation: true, //是否有定位授权
  },

  /**搜索值变化时 */
  onChange(e) {
    this.setData({
      value: e.detail,
      show: false,
      page: 0,
      marketList: [],
      loadingBom: false,
      noMoreBom: false
    });
    this.getMarket();
  },

  //弹出定位
  searAddress() {
    const AreaData = require("../../../utils/address.js");
    this.setData({
      show: true,
      areaList: AreaData.default
    });
    // console.log(AreaData);
  },

  //关闭定位选择
  onClose() {
    this.setData({
      show: false
    });
  },

  //定位选择
  areaCon(val) {
    // console.log(val);
    let that = this;
    this.setData({
      show: false,
      areaValue: val.detail.values[0].name + val.detail.values[1].name + val.detail.values[2].name,
      addressValue: val.detail.values[2].name,
      page: 0,
      marketList: [],
      loadingBom: false,
      noMoreBom: false
    });
    qqMap.geocoder({
      address: val.detail.values[0].name + val.detail.values[1].name + val.detail.values[2].name, //用户输入的地址 
      complete: res => {
        console.log(res.result);
        if (res.result) {
          //经纬度对象
          that.setData({
            lat: res.result.location.lat,
            lon: res.result.location.lng,
          });
          this.getMarket();

          //提交数据
        } else {
          wx.showToast({
            title: '无法定位到该地址，请确认地址信息！',
            icon: "none"
          });
        }
      }
    });
  },
  getSetting() {
    wx.openSetting({
      withSubscriptions: true,
    })
  },
  //刷新定位
  addressRefresh() {
    var _this = this;
    wx.getLocation({
      type: 'wgs84',
      isHighAccuracy: true,
      success(res) {
        // console.log(res, "res");
        _this.setData({
          lat: res.latitude,
          lon: res.longitude,
          show: false,
          page: 0,
          marketList: [],
          loadingBom: false,
          noMoreBom: false
        });
        var url = mapKey.getReverseGeocoderUrl(res.latitude, res.longitude);
        http.request({
          url: url,
          success: function (result) {
            wx.setStorageSync('location_district', result.data.result.ad_info.district);
            wx.setStorageSync('location_district', result.data.result.address);

            app.globalData.location.district = result.data.result.ad_info.district;
            app.globalData.location.address = result.data.result.address;
            _this.setData({
              addressValue: app.globalData.location.district,
              addressDetailValue: app.globalData.location.address,
              areaValue: "",
              hasLocation: true
            });
            _this.getMarket();
          }
        })

      },
      fail(e) {
        wx.showToast({
          title: '点击定位重新授权地理位置',
          icon: 'none'
        })
        _this.setData({
          lat: "",
          lon: "",
          show: false,
          page: 0,
          marketList: [],
          loadingBom: false,
          noMoreBom: false,
          hasLocation: false
        });

        _this.getMarket();
      }
    });
  },

  /**获取最近的市场 */
  getMarket() {
    var _this = this;
    _this.setData({
            marketList: [],
    });
    var parame = {
      latitude: this.data.lat,
      longitude: this.data.lon,
      page: this.data.page,
      pagesize: 20,
      area: this.data.areaValue,
      name: this.data.value,
      version: app.globalData.version,
      accountType: "USER"
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/index/getNearbyMarket',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          // console.log(res.data);
          var noMoreBom = false;
          var list = _this.data.marketList.concat(res.data.result.content);
          if (list.length == res.data.result.total) {
            noMoreBom = true;
          }
          _this.setData({
            marketList: list,
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

  //加载市场
  bindscrollbottom(event) {
    if (event.detail.direction == "bottom") {
      // console.log("bottom");
      if (this.data.noMoreBom || this.data.loadingBom) {
        return;
      }
      this.setData({
        page: this.data.page + 1,
        loadingBom: true,
        noMoreBom: false
      });
      this.getMarket();
    }
  },

  /**市场选择点击 */
  marketClick(event) {

    var item = event.currentTarget.dataset.id;
    var _this = this;
    var parame = {
      farmer: item.pkey
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/changeFarmer',
      data: parame,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          
          // console.log(res.data.result);
          let mallResult = wx.getStorageSync('mallResult') ? wx.getStorageSync('mallResult') : [];
          wx.setStorageSync('allCheckList', mallResult);
          wx.setStorageSync('marketResult', []);
          wx.setStorageSync('location_pkey', item.pkey);
          wx.setStorageSync('location_name', item.name);
          app.globalData.location.pkey = item.pkey;
          app.globalData.location.name = item.name;
          var pages = getCurrentPages();
          var beforePage = pages[pages.length - 2];
          beforePage.setData({
            page: 0
          });
          beforePage.getMarket("","");
          beforePage.onShow();
         
          app.getBuycarNum();
          app.getPromote();
          app.setPromote();
          wx.navigateBack({
            delta: 1,
            success() {
              beforePage.goTop();
            }
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

  goDetail(event) {
    var item = event.currentTarget.dataset.item;
    console.log(item);
    wx.setStorageSync('marketDetail', item)
    wx.navigateTo({
      url: '/pages/shouyeGroup/marketDetail/index',
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (app.globalData.location) {
      this.setData({
        addressValue: app.globalData.location.district,
        addressDetailValue: app.globalData.location.address,
      });
    }

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
    this.addressRefresh();
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
