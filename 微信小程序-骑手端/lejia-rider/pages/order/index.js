// pages/order/index.js
import http from '../../utils/http'
import Dialog from '../../miniprogram_npm/@vant/weapp/dialog/dialog';
const app = getApp()
Page({

  /**
   * 页面的初始数据
   * EXPRESS_ORDER pickup 待取货
   * EXPRESS_GOODS distribution 配送中
   */
  data: {
    pagesize: 4,
    active: "pickup",
    pickupPage: 0,
    pickupList: [],
    pickupCount: '',
    distributionPage: 0,
    distributionList: [],
    distributionCount: '',
    topNum:0,
    // 确认送达 弹窗
    deliveryShow: false,
    deliveryData: {},
    deliveryIndex: "",
    deliveryPhoto: [],//送达图片
    // end 确认送达
  },

  /**获取待取货数据 */
  getPickupData() {
    var _this = this;
    var parameter = {
      page: this.data.pickupPage,
      pagesize: this.data.pagesize,
      status: 'EXPRESS_ORDER'
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/express/query',
      data: parameter,
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.code == "999") {
          wx.reLaunch({
            url: '../login/index',
          });
          return;
        };
        if (res.data.success) {
          if (_this.data.pickupPage == 0) {
            _this.setData({
              pickupList: res.data.result.content,
            });
          } else {
            _this.setData({
              pickupList: _this.data.pickupList.concat(res.data.result.content),
            });
          }
          _this.setData({
            pickupCount: res.data.result.total
          })

        }

      },
    })
  },

  /**获取配送中数据 */
  getDistributionData() {
    var _this = this;
    var parameter = {
      page: this.data.distributionPage,
      pagesize: this.data.pagesize,
      status: 'EXPRESS_GOODS'
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/express/query',
      data: parameter,
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.code == "999") {
          wx.reLaunch({
            url: '../login/index',
          });
          return;
        };
        if (res.data.success) {
          if (_this.data.distributionPage == 0) {
            _this.setData({
              distributionList: res.data.result.content,
            });
          } else {
            _this.setData({
              distributionList: _this.data.distributionList.concat(res.data.result.content),
            });
          }
          _this.setData({
            distributionCount: res.data.result.total
          })

        }

      },
    })
  },

  //切换标签
  onChange(event) {
    if (event.detail.name == 'pickup') {
      this.setData({
        pickupPage: 0
      })
      this.getPickupData()
    } else if (event.detail.name == 'distribution') {
      this.setData({
        distributionPage: 0
      })
      this.getDistributionData();
    }
    this.setData({
      active: event.detail.name
    });

  },

  //点击地图
  addrClick(event) {
    var item = event.currentTarget.dataset.id;
    if (item.latitude) {
      wx.openLocation({ //​使用微信内置地图查看位置。
        latitude: item.latitude, //要去的纬度-地址
        longitude: item.longitude, //要去的经度-地址
        name: item.addr,
        address: item.addr
      });
    }
  },

  //点击拨打电话
  phoneClick(event) {
    var item = event.currentTarget.dataset.id;
    if (item.mobile) {
      wx.makePhoneCall({
        phoneNumber: item.mobile,
        success: function () {
          console.log("拨打电话成功！")
        },
        fail: function () {
          console.log("拨打电话失败！")
        }
      })
    }
  },

  //item点击进入详情
  itemClick(event) {
    var item = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: './detail?pkey=' + item.pkey,
    });
  },
  //确认揽货 点击
  subComClick(event) {
    var item = event.currentTarget.dataset.id;
    var index = event.currentTarget.dataset.index;
    var _that = this;
    console.log("点击了");
    /**请求订阅号消息 */
    wx.requestSubscribeMessage({
      tmplIds: ['WUQXLiAOg-pKLv2_R2y5np3Ql4HEEEdZGmzf4nOev0c'],
      success(res) {},
      complete(res) {
        Dialog.confirm({
            message: "是否确认揽货",
          })
          .then(() => {         
              _that.freightData(item, index);       
              // _that.delivery(item, index);         
          })
          .catch(() => {
            // on cancel
          });
      }
    })


  },
  // 确认送达 按钮点击
  DeliveryClick(event) {
    const _that = this;
    var item = event.currentTarget.dataset.id;
    var index = event.currentTarget.dataset.index;
     /**请求订阅号消息 */
     wx.requestSubscribeMessage({
      tmplIds: ['WUQXLiAOg-pKLv2_R2y5np3Ql4HEEEdZGmzf4nOev0c'],
      success(res) {},
      complete(res) {
        _that.setData({
          deliveryShow: true,
          deliveryData: item,
          deliveryIndex: index
        });     
      }
    });
  },
  onDeliveryClose() {
    this.setData({
      deliveryShow: false
    })
  },
  afterRead(event) {
    const { file } = event.detail;
    const that = this;
    const url = "/v1/app/courier/uploadImage";
    this.setData({
      loading: true,
      disabled: true,
    });
    console.log("file",file)
    wx.uploadFile({
      filePath: file.url,
      name: 'file',
      url: app.globalData.ajax_url + url,
      header: {
        "Content-Type": 'application/xml',
        "openid": app.globalData.openid,
        "ascription":app.globalData.ascription
      },
      success: function (res) {
        console.log("res",res)
        const { deliveryPhoto = [] } = that.data;
        deliveryPhoto.push({ ...file, url: JSON.parse(res.data).result.url });
        that.setData({ deliveryPhoto });
       
        that.setData({
          loading: false,
          disabled: false,
        });
      },
      fail(err) {
        console.log(err)
          wx.showToast({
              title: '图片上传失败',
              icon: 'none',
              duration: 2000
          });
          that.setData({
            loading: false,
            disabled: false,
          });
      }
  })
  },
  uploaderDelete(event){
    const index = event.detail.index;
    let deliveryPhoto = this.data.deliveryPhoto;
    deliveryPhoto.splice(index, 1);
    
    this.setData({
      deliveryPhoto: deliveryPhoto
    })
  },
  /**揽货 */
  freightData(item, index) {
    var _this = this;
    var parameter = {
      pkey: item.pkey
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/express/upd/goods',
      data: parameter,
      header: {
        'content-type': 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.code == "999") {
          wx.reLaunch({
            url: '../login/index',
          });
          return;
        };
        if (res.data.success) {
          var arr = _this.data.pickupList;
          arr.splice(index, 1);
          _this.setData({
            pickupList: arr,
            pickupCount: --_this.data.pickupCount,

            distributionPage: 0,
            distributionList: [],
            distributionCount: '',
          });
          _this.getDistributionData();
          wx.showToast({
            title: '揽件成功',
            icon: 'none'
          });
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none'
          });
        }
      },
    })
  },

  /**送达 */
  delivery() {
    var _this = this;
    
    const  index = _this.data.deliveryIndex;
    const photo = this.data.deliveryPhoto.map(item=> {
      return item.url;
    })
    var parameter = {
      pkey: _this.data.deliveryData.pkey,
      photo: photo
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/courier/express/upd/arrived',
      data: parameter,
      header: {
        'content-type': 'application/json',
        "openid": app.globalData.openid,
      },
      success: function (res) {
        if (res.data.code == "999") {
          wx.reLaunch({
            url: '../login/index',
          });
          return;
        };
        if (res.data.success) {
          var arr = _this.data.distributionList;
          arr.splice(index, 1);
          _this.setData({
            distributionList: arr,
            distributionCount: --_this.data.distributionCount,
            deliveryPhoto: [],
            deliveryShow: false
          });
          wx.showToast({
            title: '送达成功',
            icon: 'none'
          });
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: 'none'
          });
        }
      },
    })
  },

  //滚动到顶部
  // bindscrolltop(eventhandle) {
  //   console.log(eventhandle);
  //   if (eventhandle.detail.direction == "top") {
  //     console.log("top");
  //     if (this.data.active == 'pickup') { //待取货
  //       this.setData({
  //         pickupPage: 0,
  //         pickupList: [],
  //         pickupCount: '',
  //       });
  //       this.getPickupData();
  //     } else {
  //       this.setData({
  //         distributionPage: 0,
  //         distributionList: [],
  //         distributionCount: '',
  //       });
  //       this.getDistributionData();
  //     }
  //   }
  // },

  //滚动到底部
  bindscrollbottom(eventhandle) {
    if (eventhandle.detail.direction == "bottom") {
      console.log("bottom");
      if (this.data.active == 'pickup') { //待取货
        if (this.data.pickupList.length >= this.data.pickupCount) {
          return;
        } else {
          this.setData({
            pickupPage: ++this.data.pickupPage,
          });
          this.getPickupData();
        }
      } else {
        if (this.data.distributionList.length >= this.data.distributionCount) {
          return;
        } else {
          this.setData({
            distributionPage: ++this.data.distributionPage,
          });
          this.getDistributionData();
        }
      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getPickupData();
    this.getDistributionData();
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

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  /**
   * @desc 刷新数据
   */
  refreshData(){
    this.setData({
      pickupPage:0,
      distributionPage:0,
      topNum:  0
    });
    this.getPickupData();
    this.getDistributionData();
  },
})