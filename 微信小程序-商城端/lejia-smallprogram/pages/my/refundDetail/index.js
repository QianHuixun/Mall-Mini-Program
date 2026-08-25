// pages/my/refundDetail/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      isAuto: true,
      iShidden: true,
      orderInfo: {},
      pkey: "",
      isFold: false,
      foldHeight: 0,
      goodsListHeight: 0,
      showPopup: false,
      photoIndex: 0,
      refundStatus: {
        REFUND_APPLYING: {
            name: '已提交退款',
            express: '已提交退款，待商家处理',
            exchange: '已提交退款，待商家处理',
            image: 'wait_order',
        },
        REFUND_AGREE: {
            name: '',
            express: '',
            exchange: '',
            image: '',
        },
        REFUND_FINAL: {
            name: '退款成功',
            express: '退款成功，退款金额已原路返回',
            exchange: '退款成功，退款金额已原路返回',
            image: 'success_order',
        },
        REFUND_REFUSE: {
            name: '退款失败',
            express: '退款失败',
            exchange: '退款失败',
            image: 'fail_order',
        },
        REFUND_JD_HANDLE: {
            name: '等待审核',
            express: '等待审核',
            exchange: '等待审核',
            image: 'fail_order',
        },
        JD_PENDING_APPROVAL: {
            name: '申请中待审核',
            express: '您已成功申请售后，待商家审核',
            exchange: '您已成功申请售后，待商家审核',
            image: 'JD_PENDING_APPROVAL',
        },
        JD_APPROVAL_ACCEPTED: {
            name: '审核完成待收货',
            express: '',
            exchange: '',
            image: 'JD_APPROVAL_ACCEPTED',
        },
        JD_APPROVAL_REJECTED: {
            name: '审核不通过',
            express: '售后审核未通过',
            exchange: '售后审核未通过',
            image: 'JD_APPROVAL_REJECTED',
        },
        JD_RECEIPTED: {
            name: '收货完成待处理',
            express: '商家已收货，等待商户处理',
            exchange: '商家已收货，等待商户处理',
            image: 'JD_APPROVAL_ACCEPTED',
        },
        JD_PROCESSED_SUCCESS: {
            name: '处理完成',
            express: '商户已处理完成，等待退款',
            exchange: '商户已处理完成，等待换货',
            image: 'JD_PROCESSED_SUCCESS',
        },
        JD_PROCESSED_FAILED: {
            name: '处理失败',
            express: '售后处理失败',
            exchange: '售后处理失败',
            image: 'JD_PROCESSED_FAILED',
        },
        JD_PENDING_CONFIRM: {
            name: '待用户确认',
            express: '退款金额已原路退回，待用户确认',
            exchange: '已为您换新商品，请关注新订单状态',
            image: 'JD_PENDING_APPROVAL',
        },
        JD_CONFIRMED: {
            name: '用户确认完成',
            express: '退款金额已原路退回',
            exchange: '换货订单已完成',
            image: 'success_order',
        },
        JD_CANCELED: {
            name: '售后取消',
            express: '已取消售后，如有需要可在订单详情页重新提交售后',
            exchange: '已取消售后，如有需要可在订单详情页重新提交售后',
            image: 'JD_CANCELED',
        },
      },
      disabled: false,
      payTypeName: {
        ORDER_ZHIFUBAO: '支付宝',
        ORDER_WEIXIN: '微信',
        ORDER_ELECTRONIC_ACCOUNT:  app.globalData.ascription == 22 || app.globalData.ascription == 13 ?  'I DO' : '钱包',
        ZXYW_WEIXIN: '中信银行',
        NM_MEMBER: "会员卡",
        ORDER_MSD: '热力豆',
        MSD_COMBINATION: '热力豆',
        ELECTRONIC_ACCOUNT_COMBINATION: app.globalData.ascription == 22 || app.globalData.ascription == 13 ?  'I DO' : '钱包'
      },
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.setData({
        pkey: options.pkey
      });
    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.getData();
    },
    handleLookMore() {
      let isFold = this.data.isFold;
      isFold = !isFold
      this.setData({
        isFold
      });
    },
     /**
   * 获取订单信息
   */
  getData: function () {
    var that = this,
      url = "/v2/app/market/lm/refund/get";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        refundPkey: this.data.pkey,
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
          wx.setStorageSync('refundInfo', JSON.stringify(res.data.result));
          that.setData({
            orderInfo: res.data.result
          });
          
          let foldHeight = 0;
          // 140:商品高度; 32:商品高度上外边距margin-top; 90:规格高度; 24:规格高度上外边距margin-top;
          if (res.data.result.list[0].lines.length > 1) {
            foldHeight = (90 + 24 ) * 2 + 140
          } else {
            if (res.data.result.list[1] && res.data.result.list[1].lines.length > 1) {
              foldHeight = 140 * 2 + 32 + (90 + 24)
            } else {
              foldHeight = (140 * 3) + (32 * 2 )  
            }
          }
          let goodsListHeight = 0;
          res.data.result.list.map(item => {
            if (item.lines.length == 1) {
              goodsListHeight = goodsListHeight + 140
            } else {
              goodsListHeight = goodsListHeight + 140 + (item.lines.length * 114)
            }
          })
          if (res.data.result.list.length > 1) {
            goodsListHeight = goodsListHeight + (res.data.result.list.length - 1) * 32
          }
          that.setData({
            goodsListHeight,
            foldHeight: foldHeight > goodsListHeight ? goodsListHeight : foldHeight
          });

        } else {
          wx.showToast({
            icon: 'none',
            title: res.data.msg || ''
          })
        }
      }
    });
  },
  goGoodsDetails(e) {
    const { enabled, goods, MType } = e.currentTarget.dataset.item;
    console.log("enabled",enabled, goods)
    if(enabled) {
      if(MType ==="INTEGRAL_MSD_GOODS") {
        wx.navigateTo({
          url: '/pages/shouyeGroup/jdGoodsDetail/index?pkey=' + goods,
        })
      } else {
        wx.navigateTo({
          url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${goods}`
       });
      }
     

     
    }  else {
      wx.showToast({
        title: '当前商品已下架',
      });
    }
    
  },
  showImage(data) {
    
    const index = data.currentTarget.dataset.index;
    this.setData({
      showPopup: true,
      photoIndex: index
    })
    console.log(data)
  },
  onClose(){
    this.setData({
      showPopup: false
    })
  },
  phoneClick() {
      const Mobile = this.data.orderInfo.tel
      wx.makePhoneCall({
        phoneNumber: Mobile,
      })
  
  },
  goOrderDetail(){
    wx.navigateTo({
      url: '/pages/my/orderDetail/index?pkey=' + this.data.orderInfo.orderPkey,
    })
  },
  /**
   * 手动填写寄出运单
   */
  handleGoDispatchSlip() {
    wx.navigateTo({
      url: `/pages/my/dispatchSlip/index?pkey=${this.data.pkey}`,
    })
  },
  /**
   * 复制商家地址
   */
  handleCopyAfterService() {
    const {afterService, afterServiceTel, afterServicePhone, afterServiceAddr} = this.data.orderInfo
    const completeAfterService = afterService + (afterServicePhone ? afterServicePhone : afterServiceTel) + afterServiceAddr + ''
    console.log(completeAfterService,{afterService, afterServiceTel, afterServicePhone, afterServiceAddr});
    wx.setClipboardData({
        data: completeAfterService
    })
  },

    /**
     * 取消售后
     */
    cancelAfsApply() {
        const url = '/v1/app/jd/refund/cancelAfsApply'
        this.setData({
            disabled: true
        })
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                refundPkey: this.data.pkey,
            },
            success: (res) => {
                if(res.data.success) {
                    this.getData()
                    wx.showToast({
                        icon: 'success',
                        title: '取消成功'
                    })
                } else {
                    wx.showToast({
                        icon: 'none',
                        title: res.data.msg || ''
                    })
                }
                setTimeout(() => {
                    this.setData({
                        disabled: false
                    })
                }, 1000)
            }
        })
    },

    /**
     * 确认完成
     */
    handConfirmCompleted() {
        const url = '/v1/app/jd/refund/confirmed'
        this.setData({
            disabled: true
        })
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                refundPkey: this.data.pkey,
            },
            success: (res) => {
                if(res.data.success) {
                    this.getData()
                    wx.showToast({
                        icon: 'success',
                        title: '确认成功'
                    })
                } else {
                    wx.showToast({
                        icon: 'none',
                        title: res.data.msg || ''
                    })
                }
                setTimeout(() => {
                    this.setData({
                        disabled: false
                    })
                }, 1000)
            }
        })
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

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

    },
    //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {}
})