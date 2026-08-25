// pages/my/order/index.js
let app = getApp();
var loadMoreView;
import Dialog from '@vant/weapp/dialog/dialog';
import http from '../../../utils/http'
const { applyTheme } = require('../../../utils/themeMixin')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: "0",
    isAuto: true,
    iShidden: true,
    datalist1: [],
    page1: 0,
    datalist2: [],
    page2: 0,
    datalist3: [],
    page3: 0,
    datalist4: [],
    page4: 0,
    datalist5: [],
    page5: 0,
    datalist6: [],
    page6: 0,
    pagesize: 10,
    isOnAppShow: false, // 是否是确认收货回调来的
    theme: null
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    applyTheme(this)
    loadMoreView = this.selectComponent("#loadMoreView");

    if (options.type) {
      this.setData({
        active: options.type
      });
    }
    console.log(this.data.active)

  },
  loadMoreListener: function (e) {
    this.loadData()
  },
  clickLoadMore: function (e) {
    this.loadData()
  },
  /**
   * Tab标签页修改事件
   */
  onChange(event) {
    this.setData({
      active: event.detail.name,
      page1:0,
      page2:0,
      page3:0,
      page4:0,
      page5:0,
      page6:0,
    });
    this.goTop();
    this.loadData(event.detail.name);
  },
  /**
   * 加载数据
   */
  loadData: function ( active ="0") {
    var _this = this;
    var url = "/v1/app/market/lm/order/listOrder",
      page = this.data.page1;
      active = this.data.active;
    if (this.data.active == "UNPAID_ORDER") {
      page = this.data.page2;
    } else if (this.data.active == "SHIPPED_ORDER") {
      page = this.data.page3;
    } else if (this.data.active == "CONFIRM_ORDER") {
      page = this.data.page4;
    } else if (this.data.active == "REFUND_APPLICATION_ORDER") {
      page = this.data.page5;
    } else if (this.data.active == "DELIVERED_ORDER") {
      page = this.data.page6;
    }

    if (active == "0") {
      active = ""
    }
    var params = {
      page: page,
      pagesize: this.data.pagesize,
      status: active
    };

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: function (res) {
        if (res.data.success) {
            console.log("获取列表");
          if (active == "") { //全部          
            if (_this.data.page1 == 0) {
              _this.setData({
                datalist1: res.data.result.content,
                page1: ++_this.data.page1
              });
            } else {
              _this.setData({
                datalist1: _this.data.datalist1.concat(res.data.result.content),
                page1: ++_this.data.page1
              });
            }
            res.data.result.curPage = _this.data.page1;
            loadMoreView.loadMoreComplete(res.data);
          } else if (active == "UNPAID_ORDER") { //待付款
            if (_this.data.page2 == 0) {
              _this.setData({
                datalist2: res.data.result.content,
                page2: ++_this.data.page2
              });
            } else {
              _this.setData({
                datalist2: _this.data.datalist2.concat(res.data.result.content),
                page2: ++_this.data.page2
              });
            }
            res.data.result.curPage = _this.data.page2;
            loadMoreView.loadMoreComplete(res.data);
          } else if (active == "SHIPPED_ORDER") { //待收货
            console.log(res.data.result.content)
            if (_this.data.page3 == 0) {
              _this.setData({
                datalist3: res.data.result.content,
                page3: ++_this.data.page3
              });
            } else {
              _this.setData({
                datalist3: _this.data.datalist3.concat(res.data.result.content),
                page3: ++_this.data.page3
              });
            }
            res.data.result.curPage = _this.data.page3;
            loadMoreView.loadMoreComplete(res.data);
          } else if (active == "CONFIRM_ORDER") { //已完成
            if (_this.data.page4 == 0) {
              _this.setData({
                datalist4: res.data.result.content,
                page4: ++_this.data.page4
              });
            } else {
              _this.setData({
                datalist4: _this.data.datalist4.concat(res.data.result.content),
                page3: ++_this.data.page4
              });
            }
            res.data.result.curPage = _this.data.page4;
            loadMoreView.loadMoreComplete(res.data);
          } else if (active == "REFUND_APPLICATION_ORDER") { //退款
            if (_this.data.page5 == 0) {
              _this.setData({
                datalist5: res.data.result.content,
                page5: ++_this.data.page5
              });
            } else {
              _this.setData({
                datalist5: _this.data.datalist5.concat(res.data.result.content),
                page5: ++_this.data.page5
              });
            }
            res.data.result.curPage = _this.data.page5;
            loadMoreView.loadMoreComplete(res.data);
          } else if (active == "DELIVERED_ORDER") { //待发货
            if (_this.data.page6 == 0) {
              _this.setData({
                datalist6: res.data.result.content,
                page6: ++_this.data.page6
              });
            } else {
              _this.setData({
                datalist6: _this.data.datalist6.concat(res.data.result.content),
                page6: ++_this.data.page6
              });
            }
            res.data.result.curPage = _this.data.page6;
            loadMoreView.loadMoreComplete(res.data);
          }
        } else {
          wx.showToast({
            title: res.data.msg || '',
            icon: 'none'
          });
        }
      }
    });
  },
  //回到顶部
  goTop: function (e) { // 一键回到顶部
    if (wx.pageScrollTo) {
      wx.pageScrollTo({
        scrollTop: 0,
        duration: 0
      })
    }
  },

  /**
   * 
   * 微信确认收货的回调
   */
  onAppShow(options) {
      if(options && options.referrerInfo.extraData.status == 'success'){
        console.log('收到 app.onShow 的数据:', options);
        this.setData({
            isOnAppShow: true
        });
        this.handleArrivedHttp(options.referrerInfo.extraData.req_extradata.pkey);
      }
  },

  /**
   * 确认收货
   */
  
  handleArrived: function (data) {
     var pkey = data.currentTarget.dataset.pkey,
      index = data.currentTarget.dataset.index,
      openBusinessView = data.currentTarget.dataset.openbusinessview,
      transactionid = data.currentTarget.dataset.transactionid;
      if(openBusinessView){
        if (wx.openBusinessView) {
            wx.openBusinessView({
              businessType: 'weappOrderConfirm',
              extraData: {
                transaction_id: transactionid,
                pkey: pkey,
                // index:index
              },
              success() {
                //dosomething
              },
              fail(err) {
                //dosomething
                console.log(err,'err')
              },
              complete() {
                //dosomething
              }
            });
          } else {
            //引导用户升级微信版本
            wx.showToast({
                title: '微信版本太低，请升级'
            })
          }
      }else{
        Dialog.confirm({
            message: '确定收货吗？',
          }).then(() => {
              this.handleArrivedHttp(pkey);
          });
      }
  },

  /**
   * 
   * 确认收货请求
   */
  handleArrivedHttp(pkey){
    var that = this;
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + "/v1/app/market/lm/order/drOrder",
        data: {
          pkey: pkey
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
          if (res.data.success) {
            wx.showToast({
              title: '确认收货成功'
            });
            if (that.data.active == "0") {
            //   that.setData({
            //     [datalist1[index].status]: 'CONFIRM_ORDER'
            //   });
            } else if (that.data.active == "SHIPPED_ORDER") {
              var datalist3 = that.data.datalist3.map(item => {
                if (item.pkey == pkey) item.status = 'CONFIRM_ORDER';
                return item;
              });
              that.setData({
                datalist3: datalist3
              });
            }
            that.setData({
              page1:0,
              page3:0
            });
            that.loadData()
          } else {
            wx.showToast({
              title: res.data.msg || ''
            })
          }
        }
      });
  },


  /**
   * 前往支付
   */
  handleUnpaid: function (data) {
    var that = this,
      pkey = data.currentTarget.dataset.pkey,
      index = data.currentTarget.dataset.index,
      url = "/v2/app/market/lm/order/getUnpaidOrder";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        pkey: pkey
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
        if (res.data.success) {
          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/pay/index'
          })
        } else {
          wx.showToast({
            icon:'none',
            title: res.data.msg || ''
          })
        }
      }
    });
  },
  /**
   * 取消订单
   */
  handleCancel(data) {
    let pkey = data.currentTarget.dataset.pkey,
    that = this;
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: function (res) {
        if (res.confirm) {
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/lm/order/isshow',
            data: {
              orderPkey: pkey
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
              if (res.data.success) {
                that.setData({
                  page1:0,
                  page2:0
                })
                wx.showToast({
                  title:'取消成功',
                  icon: 'none',
                  duration: 2000,
                  success(){
                    that.loadData();
                  }
                })
              } else {
                wx.showToast({
                  title: res.data.msg || ''
                })
              }
            }
          });
        }
      }
    });

  },
  /**
   * 跳转到详情页
   */
  goDetail: function (data) {
    wx.navigateTo({
      url: '/pages/my/orderDetail/index?pkey=' + data.currentTarget.dataset.pkey
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
  onShow: function (options) {
    console.log(options,'options');
    if(!this.data.isOnAppShow){
        this.setData({
            page1: 0,
            pagelist1: [],
            page2: 0,
            pagelist2: [],
            page3: 0,
            pagelist3: [],
            page4: 0,
            pagelist4: [],
            page5: 0,
            pagelist5: []
          });
      
          if (this.data.active == "0") {
            this.loadData();
          }
    }
    this.setData({
        isOnAppShow: false
    });
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
    loadMoreView.loadMore()
  },
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {}
})