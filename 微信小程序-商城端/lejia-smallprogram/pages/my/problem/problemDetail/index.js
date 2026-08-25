// pages/my/problem/problemDetail/index.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    activeName: null,
    list: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    const item = JSON.parse(decodeURIComponent(options.item))
    item.content.forEach(_item => {
        _item.answer = _item.answer.replace(/&nbsp;/g, ' ')
    })
    console.log(item.content);
    wx.setNavigationBarTitle({
      title: item.name,
    })
    this.setData({
        list: item.content
    })
  },
  onChange(event) {
    this.setData({
      activeName: event.detail,
    });
  },
})