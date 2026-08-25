// pages/pay/pickupAddr/index.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
      orderInfo: null,
      splList: [],
      index: null,
      pickupPkey: null
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      const orderInfo = JSON.parse(wx.getStorageSync('orderInfo'))
      let index = options.index
      let pickupPkey
      let splList
      console.log(orderInfo, options.index);
      if(index === undefined) {
        pickupPkey = orderInfo.pickupPkey
        splList = orderInfo.splList
      } else {
        pickupPkey = orderInfo.infos[index].pickupPkey
        splList = orderInfo.infos[index].splList
      }
      this.setData({
        orderInfo,
        splList,
        index,
        pickupPkey
      })
    },

    handleItemClick(event) {
      let item = event.currentTarget.dataset.item
      let {index, orderInfo} = this.data
      this.setData({
        pickupPkey: item.pkey
      })
      if(index === undefined) {
        orderInfo.pickupPkey = item.pkey
        orderInfo.pickupAddr = item.address
        var pages = getCurrentPages();
        var prevPage = pages[pages.length - 2]; //上一个页面
        prevPage.setData({
          orderInfo
        })
      } else {
        orderInfo.infos[index].pickupPkey = item.pkey
        orderInfo.infos[index].pickupAddr = item.address
        var pages = getCurrentPages();
        var prevPage = pages[pages.length - 2]; //上一个页面
        prevPage.setData({
          orderInfo
        })
      }
      wx.navigateBack()
    },

})