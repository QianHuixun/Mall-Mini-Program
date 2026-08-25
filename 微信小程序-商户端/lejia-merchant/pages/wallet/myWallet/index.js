// pages/wallet/myWallet/index.js
import {getElementHeight} from "../../../utils/util";
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      top: 0,
      walletAmt: "",
      noSettlementList: [],
      settlementList: [],
      currentDate: "",
      show: false,
      minDate: new Date(2023, 1, 1).getTime(),
      chooseDate: new Date().getTime(),
      ascription: app.globalData.ascription,
    },
 // 获取 我的钱包 余额和待到账
 getMyWalletData(){
  var that = this,
  url = "/v3/app/vendor/wallet/get";
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + url,
    data: {},
    header: {
      "Content-Type": 'application/x-www-form-urlencoded',
      "openid": app.globalData.openid
    },
    success: function (res) {
      if (res.data.success) {
        that.setData({
          walletAmt: res.data.result.walletAmt,
        })
      } else {
        wx.showToast({
          title: res.data.msg,
          icon: "none"
        })
      }
    }
  })
},
getData(){
  var that = this,
  url = "/v3/app/vendor/wallet/list/bill";
  http.request({
    method: "POST",
    url: app.globalData.ajax_url + url,
    data: {startDate: this.data.currentDate},
    header: {
      "Content-Type": 'application/x-www-form-urlencoded',
      "openid": app.globalData.openid
    },
    success: function (res) {
      if (res.data.success) {
        that.setData({
          noSettlementList: res.data.result.noSettlement,
          settlementList: res.data.result.settlement
        })
        
      } else {
        wx.showToast({
          title: res.data.msg,
          icon: "none"
        })
      }
    }
  })
},
showPopup(){
  this.setData({
    show: true
  })
},
onClose(){
  this.setData({
    show: false,
  });
},

onConfirm(event){
  var chooseDate = new Date(event.detail);
  this.setData({
    chooseDate: event.detail
  });
  var seperator = "-";
  var month = chooseDate.getMonth() + 1;
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }  
  
  var date =  chooseDate.getFullYear() +  seperator + month;  

  this.setData({
    show: false,
    currentDate: date, 
    noSettlementList: [],
    settlementList: [],
  });
  this.getData();
},
onScroll(){},
scrollBottom(){},
goAccount(){
  wx.navigateTo({
    url: '/pages/wallet/account/index',
  });
},
goDetail(){
  wx.navigateTo({
    url: '/pages/wallet/details/index',
  });
},
goWithdraw(){
  wx.navigateTo({
    url: '/pages/wallet/withdraw/index',
  });
},
goBill(data){
  console.log(data)
  wx.navigateTo({
    url: '/pages/wallet/billDetails/index?date=' + data.currentTarget.dataset.date + '&status=' + data.currentTarget.dataset.status,
  });
},
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
   async onReady() {
      const top = await getElementHeight(".my-wallet_top");
      this.setData({
        top: top
      })
      console.log("top",this.data.top)
     
      this.getData();
    },
    
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      this.getMyWalletData();
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

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