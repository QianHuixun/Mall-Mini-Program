// pages/my/map/index.js
import QQMapWX from '../../../utils/qqmap-wx-jssdk.min.js';
import http from '../../../utils/http';
// 实例化API核心类
var qqmapsdk = new QQMapWX({
  key: 'IGCBZ-MWAK3-DA633-YA7GK-BNZG7-ZHFSG' // 必填
});
let app = getApp();
Page({
    /**
     * 页面的初始数据
     */
    data: {
      pkey: '',
      status: '',
      name: "",
      mobile: "",
      longitude: '', // 给个默认的经纬度 定位到北京天安门
      latitude: '', //
      distance: '',
      distanceKM: '',
      polyline: [], // 存放路线的经纬度
      markers: [// 自己设置的三个mark标记，分别是 商家，用户，骑手
        {
          iconPath: "/images/my/map-qishou.png",
          id: 1,
          latitude: '',
          longitude: '',
          width: 35,
          height: 32,
          customCallout: {
            anchorY: -10,
            anchorX: 5,
            display: 'ALWAYS',
          },
        },
        { 
        iconPath: "/images/my/map-shichang.png",
        id: 2,
        latitude: '',
        longitude: '',
        width: 22,
        height: 32
      }, {
        iconPath: "/images/my/map-shouhuo.png",
        id: 3,
        latitude: '',
        longitude: '',
        width: 22,
        height: 32
      }, 
      ],
      timer:null
    },
// 获取 市场、会员、骑手坐标
getCourier(){
  var that = this;
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + "/v2/app/market/lm/order/get/courier",
    data: {
      pkey: this.data.pkey,
    },
    header: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
      "openid": app.globalData.openid,
      "farmer": app.globalData.location.pkey
    },
    success: res => {
      that.setData({
        name: res.data.result.name,
        mobile: res.data.result.mobile,
        longitude: res.data.result.longitude || 116.307520,
        latitude: res.data.result.latitude || 39.984060,
        distance: res.data.result.distance,
        distanceKM:(res.data.result.distance/1000).toFixed(2),
        ['markers[0].latitude']: res.data.result.latitude || 39.984060,
        ['markers[0].longitude']: res.data.result.longitude || 116.307520,
        ['markers[1].latitude']:  res.data.result.marketLatitude,
        ['markers[1].longitude']: res.data.result.marketLongitude,
        ['markers[2].latitude']:  res.data.result.memberLatitude,
        ['markers[2].longitude']: res.data.result.memberLongitude,
      });
      var from = {latitude: res.data.result.marketLatitude, longitude: res.data.result.marketLongitude},
      to = {latitude: res.data.result.memberLatitude, longitude: res.data.result.memberLongitude};
      that.getDirection(from, to);
    }
  });

},
// 地图骑手路径
getDirection(from, to) {
  var _this = this;
  //调用距离计算接口
  qqmapsdk.direction({
    mode: 'bicycling',//可选值：'driving'（驾车）、'walking'（步行）、'bicycling'（骑行），不填默认：'driving',可不填
    //from参数不填默认当前地址
    from: from,
    to: to, 
    success: function (res) {
      console.log(res);
      var ret = res;
      var coors = ret.result.routes[0].polyline, pl = [];
      //坐标解压（返回的点串坐标，通过前向差分进行压缩）
      var kr = 1000000;
      for (var i = 2; i < coors.length; i++) {
        coors[i] = Number(coors[i - 2]) + Number(coors[i]) / kr;
      }
      //将解压后的坐标放入点串数组pl中
      for (var i = 0; i < coors.length; i += 2) {
        pl.push({ latitude: coors[i], longitude: coors[i + 1] })
      }
      console.log(pl)
      //设置polyline属性，将路线显示出来,将解压坐标第一个数据作为起点
      _this.setData({
        polyline: [{
          points: pl,
          color: '#0DAE4E',
          width: 4
        }]
      });
      console.log("polyline",_this.data.polyline)
    },
    fail: function (error) {
      console.error(error);
    },
    complete: function (res) {
      console.log(res);
    }
  });
},
  // 刷新地图
  refreshMap() {
    this.getCourier();
  },
   //拨打骑手电话
  riderPhoneClick () {
    wx.makePhoneCall({
      phoneNumber: this.data.mobile,
    })
  },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      console.log("options",options)
      this.setData({
        pkey: options.pkey,
        status: options.status
      });
      
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {
      this.getCourier();
      this.data.timer = setInterval(()=>{
        this.getCourier();
      },5000);
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
      clearInterval(this.data.timer);
      this.setData({
        timer: null
      });  
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {
      clearInterval(this.data.timer);
      this.setData({
        timer: null
      });  
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
    onShareAppMessage() {

    }
})