// pages/pay/addr/index.js
let app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    datalist: [],
    hasMarket: true,
    checkedPkey: "", //选中的地址pkey
    isPrize: false, //是否为奖品地址填写
    shareFarm: "", //分享的市场
    isPickup: false, //是否是自提
    isSupport: false, //是否是退换货
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log("options", options)

    if (options.hasOwnProperty('shareFarm')) {
      this.setData({
        shareFarm: JSON.parse(options.shareFarm)
      })
    }
   
    if (options.hasOwnProperty('type')) {
      if (options.type == 'true')
        this.setData({
          hasMarket: true,
        })
      else
        this.setData({
          hasMarket: false,
        })
    }

    if (options.hasOwnProperty('pkey'))
      this.setData({
        checkedPkey: options.pkey
      })
    if (options.hasOwnProperty('isPrize')) {
      this.setData({
        isPrize: true
      })
    }
    if(options.hasOwnProperty('isPickup') && options.isPickup == 'true') {
      this.setData({
        isPickup: true
      })
    }
    if(options.hasOwnProperty('isSupport') && options.isSupport == 'true') {
        this.setData({
            isSupport: true
        })
    }
  },
  handleAddClick(){
    console.log("isPickup",this.data.isPickup)
    wx.navigateTo({
      url: '/pages/my/address/edit/index?type=add&isPickup='+this.data.isPickup + '&shareFarm=' + JSON.stringify(this.data.shareFarm),
    })
  },
  /**
   * @desc 编辑地址
   */
  handleEdit(data){
    wx.navigateTo({
      url: '/pages/my/address/edit/index?type=edit&pkey=' + data.currentTarget.dataset.value + '&isPickup=' + this.data.isPickup,
    })
  },
  /**
   * 获取数据
   */
  getData: function () {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/lm/order/listAddr",
      data: {
        distributionType: this.data.isPickup ? 'PICKUP' : ''
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
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
        var datalist = res.data.result.map(item => {
          item.checked = item.pkey == that.data.checkedPkey ? true : false;
          return item;
        })
        console.log(datalist)
        that.setData({
          datalist: datalist
        })
      }
    });
  },
  /**
   * 地址选择事件
   */
  addrChoose: function (event) {
    console.log(this.data);
    var addr = this.data.datalist[event.currentTarget.dataset.index];
    console.log(addr);
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2]; //上一个页面
    // 售后选择地址
    if(this.data.isSupport) {
        prevPage.chooseAdress(addr)
        //返回上一页
        wx.navigateBack({
            delta: 1
        });
        return
    }
    // console.log(this.data.hasMarket && !addr.enabled, this.data.hasMarket, addr.enabled)
    console.log(this.data.hasMarket,addr.enabled,this.data.isPrize, this.data.isPickup)
    if (this.data.hasMarket && !addr.enabled && !this.data.isPrize && !this.data.isPickup) {
      wx.showToast({
        title: '当前地址超出市场配送范围',
        icon: "none"
      })
      return;
    }
      if (!this.data.isPrize)
      if(this.data.isPickup) {
        if(prevPage.data.orderInfo.hasOwnProperty('addrPkey')) {
          if(addr.pkey !== prevPage.data.orderInfo.addrPkey) {
            prevPage.addrChange(addr.pkey)
          }
          prevPage.setData({
            ["orderInfo.addrPkey"]: addr.pkey,
            ["orderInfo.name"]: addr.name,
            ["orderInfo.mobile"]: addr.mobile,
          });
        } else {
          prevPage.setData({
            pickupAddr: addr,
            pickAddressPkey: addr.pkey
          });
        }
        if(prevPage.handleShopTypeChange) {
          prevPage.handleShopTypeChange(this.data.isPickup ? 'zt' : 'ps');
        }
      } else {
        if(prevPage.data.orderInfo.hasOwnProperty('addrPkey')) {
          if(addr.pkey !== prevPage.data.orderInfo.addrPkey) {
            prevPage.addrChange(addr.pkey)
          }
          prevPage.setData({
            ["orderInfo.addrPkey"]: addr.pkey,
            ["orderInfo.addr"]: addr.addr,
            ["orderInfo.name"]: addr.name,
            ["orderInfo.mobile"]: addr.mobile,
          });
        } else {
          prevPage.setData({
            ["orderInfo.addr"]: addr,
            addressPkey: addr.pkey
          });
        }
        if(prevPage.handleShopTypeChange) {
          prevPage.handleShopTypeChange(this.data.isPickup ? 'zt' : 'ps');
        }
      }
    
    else {

      prevPage.setData({
        addr: addr.addrDetail
      });
      prevPage.chooseAdress()
    }
    //返回上一页
    wx.navigateBack({
      delta: 1
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
    this.getData();
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
    // 如果页面关闭是，地址列表为空，则重新获取订单信息
console.log(this.data.datalist)
var pages = getCurrentPages();
var prevPage = pages[pages.length - 2]; //上一个页面
if(!this.data.datalist.length && prevPage.data.orderInfo.addr) {
  if (!this.data.isPrize)
    if(this.data.isPickup) {
      prevPage.setData({
        pickupAddr: null,
        pickAddressPkey: ''
      });
      prevPage.handleShopTypeChange(this.data.isPickup ? 'zt' : 'ps');
      console.log("addr",addr)
    } else {
      prevPage.setData({
        ["orderInfo.addr"]: null,
        addressPkey: ''
      });
      prevPage.handleShopTypeChange(this.data.isPickup ? 'zt' : 'ps');
    }
    
  else {

    prevPage.setData({
      addr: addr.addrDetail
    });
    prevPage.chooseAdress()
  }
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

  },
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.getData();
  }
})