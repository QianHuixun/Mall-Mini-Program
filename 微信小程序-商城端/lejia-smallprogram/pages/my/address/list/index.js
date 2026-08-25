// pages/my/address/list/index.js
let app = getApp();
import http from '../../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isAuto: true,
    iShidden: true,
    datalist: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // this.getData();
  },
  handleAddClick(){
    wx.navigateTo({
      url: '/pages/my/address/edit/index?type=add',
    })
  },
  onClose(event) {
    console.log(event.detail)
    const { position, instance } = event.detail;
    switch (position) {
      case 'left':
      case 'cell':
        instance.close();
        break;
      case 'right':
        Dialog.confirm({
          message: '确定删除吗？',
        }).then(() => {
          instance.close();
        });
        break;
    }
  },
  /**
   * 获取数据
   */
  getData: function () {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + "/v1/app/market/lm/member/addr/query",
      data: {},
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        that.setData({
          datalist: res.data.result.content
        })
      }
    });
  },
  /**
   * 点击编辑按钮
   * @param {地址pkey} pkey 
   */
  handleEdit: function(data) {
    wx.navigateTo({
      url: '/pages/my/address/edit/index?type=edit&pkey=' + data.currentTarget.dataset.value + '& isPickup=' + data.currentTarget.dataset.pickup,
    })
  },
  /**
   * 删除
   */
  handleDelete: function(data) {
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
              pkey: data.currentTarget.dataset.value
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
              "openid": app.globalData.openid,
              "farmer": app.globalData.location.pkey
            },
            success: res => {
              if (res.data.code == "999") {
                that.setData({
                  iShidden: false
                })
                return;
              };
              if(res.data.success) {
                that.getData();
              }
            }
          });
        }
      }
    })

  },
  /**
   * 设置默认地址
   */
  handleDefault: function (data) {
    var that = this;
    wx.showModal({
      title: '操作',
      content: '确定设置为默认地址？',
      success(res) {
        if (res.confirm) {
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/lm/member/addr/default",
            data: {
              pkey: data.currentTarget.dataset.value
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
              "openid": app.globalData.openid,
              "farmer": app.globalData.location.pkey
            },
            success: res => {
              if (res.data.code == "999") {
                that.setData({
                  iShidden: false
                })
                return;
              };
              if(res.data.success) {
                that.getData();
              }
            }
          });
        }
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