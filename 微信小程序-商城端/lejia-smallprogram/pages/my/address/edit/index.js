// pages/my/address/edit/index.js
// 引入SDK核心类
import Dialog from '@vant/weapp/dialog/dialog';
import QQMapWX from '../../../../utils/qqmap-wx-jssdk.min.js';
import utils from '../../../../utils/util.js';
import AreaList from '../../../../utils/area.js';
import http from '../../../../utils/http';
import mapKey from '../../../../utils/map-key.js';

// 实例化API核心类
let qqMap = new QQMapWX({
  key: 'IGCBZ-MWAK3-DA633-YA7GK-BNZG7-ZHFSG' // 必填
});

let app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shareFarm: "",
    isAuto: true,
    iShidden: true,
    inRange: false,
    inputModel: {
      "pkey": "",
      "addr": "",
      "addrDetail": "",
      "addrCode": "1",
      "defaultAddr": false,
      "latitude": 0,
      "longitude": 0,
      "mobile": "",
      "name": "",
      "pro": "",
      "city": "",
      "area": "",
      "town": "",
      "region": "",
    },
    loading: false,
    disabled: false,
    areaList: AreaList,
    show: false,
    type: "add", // add新增 edit 编辑
    isPickup: false,
    ascription: null,
    columns: [],
    popupType: null,
  },
   /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log("options", options)
    this.setData({
      type: options.type,
      ascription: app.globalData.ascription
    });
    if (options.pkey) {
      this.setData({
        ["inputModel.pkey"]: options.pkey
      });

    }
    if (options.isPickup == 'true') {
      this.setData({
        isPickup: options.isPickup
      });

    }
    
    if (options.hasOwnProperty('shareFarm') || options.shareFarm) {
      this.setData({
        shareFarm: JSON.parse(options.shareFarm)
      }); 
    }
    if (options.type == "edit") {
      this.getData();
    }
    this.getLocal();
  },
  /**
   * 显示底部弹出层
   */
  showPopup(data) {
    console.log(data);
    const { inputModel } = this.data
    const type = data.currentTarget.dataset.type
    if(type == 'area' && (!inputModel.pro || !inputModel.city || !inputModel.area)) {
        wx.showToast({
          title: '请先选择完整的所在地区',
          icon:'none'
        })
        return
    }
    this.setData({
      show: true,
      popupType: type
    });
    if(type == 'area') this.getListTown()
  },
  /**
   * 关闭底部弹出层
   */
  onClose() {
    this.setData({
      show: false
    });
  },
  /**
   * 根据省市区获取街道列表
   */
  getListTown() {
      const parame = {
        pro: this.data.inputModel.pro,
        city: this.data.inputModel.city,
        area: this.data.inputModel.area,
      }
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + '/v1/app/market/lm/member/addr/listTown',
        data: parame,
        success: (res) => {
          if (res.data.success) {
            this.setData({
                columns: res.data.result
            })
          }
        }
    });
  },
  handleAreachange(data) {
    const areaName = data.detail.value.areaName
    this.setData({
        "inputModel.town": areaName
    })
    this.onClose()
  },
  /**
   * 地址选择 点击“完成”
   */
  areaConfirm: function (data) {
    console.log(data, AreaList);
    var locationData = data.detail.values;
    if (locationData[2].name == "") {
      wx.showToast({
        title: '请选择省市区',
        icon: 'none'
      })
      return;
    }
    this.setData({
      [`inputModel.pro`]: locationData[0].name,
      [`inputModel.city`]: locationData[1].name,
      [`inputModel.area`]: locationData[2].name,
      ["inputModel.region"]: locationData[0].name + locationData[1].name + locationData[2].name,
      ["inputModel.addrCode"]: locationData[2].code,
      ["inputModel.town"]: "",
      show: false
    })
  },
  /**
   * 手动修改收货人 
   */
  filedChange: function (e, name) {
    this.setData({
      ["inputModel." + e.currentTarget.dataset.value]: e.detail
    })
  },
  /**
   * 手动更新checked 状态
   */
  switchChange: function ({
    detail
  }) {
    this.setData({
      ["inputModel.defaultAddr"]: detail
    });
  },
  addrTap() {
    if(this.data.inputModel.addr) return
    this.chooseAddr()
  },
  /**
   * @desc 选择地址
   */
  chooseAddr() {
    wx.getSetting({
      success: function (res) {
        var statu = res.authSetting;
        if (!statu['scope.userLocation']) {
          wx.showModal({
            title: '需要授权定位功能',
            content: '请确认授权，否则地图功能将无法使用',
            success: function (tip) {
              if (tip.confirm) {
                console.log(tip.confirm)
                wx.openSetting({
                  success: function (data) {
                    if (data.authSetting["scope.userLocation"] === true) {
                      app.ShowToast("授权成功")
                    } else {
                      app.ShowToast("授权失败，请重新点击")
                    }
                  },
                  fail: function (data) {
                    console.log(data);
                  }
                })
              } else {
                app.ShowToast("授权失败，请重新点击")
              }
            },
          })
        }
      }
    });
    var that = this;
    wx.chooseLocation({
      success: async function (res) {
        console.log(res)
        let addressard = (that.data.ascription == 13 || that.data.ascription == 22) ? await that.jdAddressard(res) : that.addressard(res.address)
        console.log(addressard);
        if(!addressard.province && addressard.city) {
          addressard.province = addressard.city
        }
        that.setData({
          [`inputModel.addr`]: res.address + res.name,
          [`inputModel.latitude`]: res.latitude,
          [`inputModel.longitude`]: res.longitude,
          [`inputModel.region`]: addressard.province + addressard.city + addressard.area,
          [`inputModel.pro`]: addressard.province,
          [`inputModel.city`]: addressard.city,
          [`inputModel.area`]: addressard.area,
          [`inputModel.town`]: addressard.town,
        })
      }
    })
  },
  // 截取省、市、区
  addressard(address) {
    let matches = ''
    let province = '';
    let city = '';
    let area = '';

    matches = address.match(/(.*?(省|自治区))/)
    if (matches && matches.length > 1) {
      province = matches[matches.length - 2];
      address = address.replace(province, '');
    }

    matches = address.match(/(.*?(市|自治州|地区|区划|县))/)
    if (matches && matches.length > 1) {
      city = matches[matches.length - 2];
      address = address.replace(city, '');
    }

    matches = address.match(/(.*?(区|县|市|镇|乡|街道))/)
    if (matches && matches.length > 1) {
      area = matches[matches.length - 2];
      address = address.replace(area, '');
    }

    let obj = {
      province,
      city,
      area,
    }

    return obj;
  },
  // 通过京东获取省、市、区和街道
  jdAddressard(address) {
    return new Promise((resolve, reject) => {
      console.log(address);
      const url = "/v1/app/market/lm/member/addr/convertFourAreaByLatLng"
      const params = {
        latitude: address.latitude,
        longitude: address.longitude,
      }
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: params,
        success: res => {
          const result = res.data.result
          resolve({
            province: result.pro,
            city: result.city,
            area: result.area,
            town: result.town,
          }) 
        }
      })
    })
  },
  /**
   * @desc 手动修改地址
   */
  addrChange(event) {
    console.log(event);
    this.setData({
      ['inputModel.addr']: event.detail
    })
  },
  /**
   * 点击保存按钮
   */
  handleSubmit: function () {
    if (!this.data.inputModel.name) {
      wx.showToast({
        title: '请输入收货人',
        icon: 'none'
      });
      return;
    }

    if (!this.data.inputModel.mobile) {
      wx.showToast({
        title: '请输入手机号码',
        icon: "none"
      });
      return;
    }

    if (!utils.formatTel(this.data.inputModel.mobile)) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: "none"
      });
      return;
    }
    if ((!this.data.inputModel.pro || !this.data.inputModel.city || !this.data.inputModel.area) && !this.data.isPickup) {
      wx.showToast({
        title: '请选择所在地区',
        icon: "none"
      });
      return;
    }
    if((app.globalData.ascription == 13 || app.globalData.ascription == 22) && !this.data.inputModel.town) {
        wx.showToast({
            title: '所属街道',
            icon: "none"
          });
          return;
    }
    if (!this.data.inputModel.addr && !this.data.isPickup) {
      wx.showToast({
        title: '请选择详细地址',
        icon: "none"
      });
      return;
    }
    if (!this.data.isPickup) {
      this.checkFarmerInRange();
    } else {
      this.onSubmit();
    }


  },
  checkFarmerInRange() {
    var _this = this;

    var parame = {
      latitude: _this.data.inputModel.latitude,
      longitude: _this.data.inputModel.longitude,
      farmer: this.data.shareFarm != "" ? this.data.shareFarm.pkey :  app.globalData.location.pkey,
      addrBoolean: false
    };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/index/checkFarmerInRange',
      data: parame,
      async: false,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
          if (!res.data.result.inRange && app.globalData.ascription != 22 && app.globalData.ascription != 13) {
            Dialog.confirm({
                title: '提示',
                message: '您输入的地址超出配送范围,是否确认添加该地址？',
              })
              .then(() => {
                _this.onSubmit();
              })
              .catch(() => {
                // on cancel
              });
          } else {
            _this.onSubmit();
          }
        }
      }
    });

  },
  onSubmit() {
    var url = "",
      that = this;
    if (this.data.type == "add") {
      url = "/v1/app/market/lm/member/addr/ins";
    } else {
      url = "/v1/app/market/lm/member/addr/upd";
    }
    that.setData({
      loading: true,
      disabled: true
    });
    //提交数据
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        ...that.data.inputModel,
        type: this.data.isPickup ? 'PICKUP' : 'DELIVERY'
      },
      header: {
        "Content-Type": "application/json",
        "openid": app.globalData.openid,
        "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        wx.showToast({
          title: '保存成功',
        });
        //返回上一页并刷新
        wx.navigateBack({
          delta: 1,
        });
        that.setData({
          loading: false,
          disabled: false
        });
      }
    })


  },
  /**
   * 删除
   */
  handleDelete: function (data) {
    console.log("delete")
    var that = this;
    wx.showModal({
      title: '操作',
      content: '确定删除吗？',
      success(res) {
        if (res.confirm) {
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/lm/member/addr/del",
            data: {
              pkey: that.data.inputModel.pkey
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
              "openid": app.globalData.openid,
              "farmer": that.data.shareFarm != "" ? that.data.shareFarm.pkey : app.globalData.location.pkey
            },
            success: res => {
              if (res.data.code == "999") {
                that.setData({
                  iShidden: false
                })
                return;
              };
              if (res.data.success) {
                wx.navigateBack({
                  delta: 1
                })
              }
            }
          });
        }
      }
    })

  },
 
  getLocal() {
    wx.getLocation({
      type: 'wgs84',
      isHighAccuracy: true,
      success(res) {

        var url = mapKey.getReverseGeocoderUrl(res.latitude, res.longitude);
        http.request({
          url: url,
          success: function (result) {
            wx.setStorageSync('location_district', result.data.result.ad_info.district);
            wx.setStorageSync('location_address', result.data.result.address);

            app.globalData.location.district = result.data.result.ad_info.district;
            app.globalData.location.address = result.data.result.address;
          }
        })
      },
      fail(res) {}
    });
  },
  /**
   * 如果type=edit 则获取地址信息
   */
  getData: function () {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/lm/member/addr/get",
      data: {
        pkey: that.data.inputModel.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        that.setData({
          inputModel: res.data.result
        });
        if(res.data.result.pro && res.data.result.city && res.data.result.area) {
          this.setData({
            ['inputModel.region']: res.data.result.pro + res.data.result.city + res.data.result.area
          })
        }
        console.log("inputModel_get", that.data.inputModel)
      }
    });
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

  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    if (this.data.type == "edit") {
      this.getData();
    }
  }
})
