// pages/wallet/billDetails/index.js
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      currentDate:"",
      chooseDate: "",
      show: false,
      minDate: new Date(2022, 0, 1).getTime(), 
      orderList: [],
      settlementTime: '',
      orderAmt: '',
      amt:"",// 订单金额	
      packingCharge:"",// 打包费用	
      isPackingCharge:false,//是否显示打包费用 true:显示	
      status: ""
    },
    onClose(){
      this.setData({
        show: false,
      });
    },
    getData(){
      const that=this,url="/v3/app/vendor/wallet/list/order";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {time: this.data.currentDate,status: this.data.status},
        header: {
          "Content-Type": 'application/x-www-form-urlencoded',
          "openid": app.globalData.openid
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({
              orderList: res.data.result.appWalletOrderOnList,
              settlementTime: res.data.result.settlementTime,
              orderAmt: res.data.result.orderAmt,
              amt:res.data.result.amt,// 订单金额	
              packingCharge:res.data.result.packingCharge,// 打包费用	
              isPackingCharge:res.data.result.isPackingCharge,//是否显示打包费用 true:显示	
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
    onConfirm(event){
      var chooseDate = new Date(event.detail);
      this.setData({
       chooseDate: event.detail
      });
      var seperator = "-";
      var month = chooseDate.getMonth() + 1;
      var strDate = chooseDate.getDate();
      if (month >= 1 && month <= 9) {
        month = "0" + month;
      }
      if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
      }
      var date =  chooseDate.getFullYear() +  seperator + month +  seperator + strDate;  

      this.setData({
        show: false,
        currentDate: date,
        orderList: [],
        settlementTime: '',
        orderAmt: ''
      });
      this.getData();
    },
    showPopup(){
      this.setData({
        show: true
      })
    },
    goOrder(options){
      console.log("11", options);
      const dataset = options.currentTarget.dataset
      wx.navigateTo({
        url: `/pages/wallet/orderDetail/index?pkey=${dataset.pkey}`,
      })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.setData({
        currentDate: options.date,
        status: options.status,
        chooseDate: new Date(options.date).getTime()
      })
      this.getData();
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

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