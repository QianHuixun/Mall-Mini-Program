// pages/activity/result/index.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
      pkey: "",
      title: "",
      cost: "",
      limitCost: '',
      endDate: ''
    },
    handeBack(){
      wx.navigateTo({
        url: '/pages/shouyeGroup/merchant/index?pkey=' +  this.data.pkey
      });
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      console.log(options)
      if(options.vendor) {
        this.setData({
          pkey: options.vendor
        })
      }
      if(options.title) {
        this.setData({
          title: options.title
        })
        console.log(this.data.title)
      }
      if(options.cost) {
        this.setData({
          cost: options.cost
        })
      }
      if(options.limitCost) {
        this.setData({
          limitCost: options.limitCost
        })
      }
      if(options.endDate) {
        this.setData({
          endDate: options.endDate == 'null' ? '' : options.endDate
        })
      }
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