// pages/wallet/orderDetail/index.js
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      pkey: "",
      list:[],
      code: '',
      orderAmt: '',// 订单金额
      amt:"",// 结算金额	
      packingCharge:"",// 打包费用	
      isPackingCharge:false,//是否显示打包费用 true:显示	
      smallTicket: "",
      pstime: '',
      orderTime: ''
    },
    getData(){
      const that=this,url="/v3/app/vendor/wallet/get/order";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {pkey: this.data.pkey},
        header: {
          "Content-Type": 'application/x-www-form-urlencoded',
          "openid": app.globalData.openid
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({
              list: res.data.result.listOrder,
              code: res.data.result.code,
              orderAmt: res.data.result.orderAmt,
              amt:res.data.result.amt,	
              packingCharge:res.data.result.packingCharge,// 打包费用	
              isPackingCharge:res.data.result.isPackingCharge,//是否显示打包费用 true:显示
              smallTicket: res.data.result.smallTicket,
              pstime: res.data.result.pstime,
              orderTime: res.data.result.orderTime,
              commissions: res.data.result.commissions, // 佣金
              commissionType: res.data.result.commissionType, // 结算类型
              payComm: res.data.result.payComm, // 手续费
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
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // console.log(options)
      this.setData({
        pkey: options.pkey
      });
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