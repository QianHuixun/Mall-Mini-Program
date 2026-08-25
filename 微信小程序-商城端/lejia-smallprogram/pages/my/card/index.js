// pages/my/card/index.js
let app = getApp();
import http from '../../../utils/http';
import drawQrcode from '../../../utils/weapp.qrcode.esm.js';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        isAuto: true,
        iShidden: true,
        datalist: [],
        qrCodeUrl: "", //卡券二维码
        show: false,
        active: "UNUSED",
        topActive: 'CARD',
        scrollHeight: 0,
        giftShow: false,
        popupData: {},
        index: 0
    },
    handleHide(){
      this.setData({
        giftShow: false
      })
    },
    handleShow(event) {
      const item = event.currentTarget.dataset.item;
      this.setData({
        index: this.data.index+1
      })
      drawQrcode({
        width: 219,
        height: 219,
        canvasId: 'myQrcode'+item.pkey+this.data.index,
        text: app.globalData.cardWhiteOff_url +'_'+ app.globalData.ascription + '?pkey=' + item.cardNumber + '&type=' + this.data.topActive,      
      });
      console.log(app.globalData.cardWhiteOff_url +'_'+ app.globalData.ascription + '?pkey=' + item.cardNumber + '&type=' + this.data.topActive)
      this.setData({
        giftShow: true,
        popupData: item
      });

     
    },
    /**
     * @desc 使用优惠券 跳转分类页
     */
    handleUse(event){
        if(event.currentTarget.dataset.pkey){
        wx.setStorageSync('classiftyPkey',event.currentTarget.dataset.pkey)
        }else{
          wx.setStorageSync('classiftyPkey','')
        }
        wx.switchTab({
          url: '/pages/home/classification/index',
        })
    },
    getHeight() {
        var query = wx.createSelectorQuery();
        var that = this;
        query.select('.scroll-view-box').boundingClientRect(function (rect) {
          that.setData({
            scrollHeight: rect.height
          })
        }).exec();
      },
    tabChange(event) {
        this.setData({
            datalist:[],
            active:event.detail.name
        })
        this.getData();
    },
    tabTopChange(event) {
      this.setData({
          datalist:[],
          active: 'UNUSED',
          topActive: event.detail.name,
      })
      this.getData();
  },
    /**
     * 获取数据
     */
    getData: function () {
        var that = this;
        var url = this.data.topActive == 'GIFT' ? '/v2/app/market/lm/member/list/memberGift' : "/v2/app/market/lm/member/list/memberCard";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: {
                status: this.data.active
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
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

                console.log( res.data.result)
               const datalist =  res.data.result.map((item)=> {
                item.rules = [];
                item.rules.push(`仅限${item.userFarmerName ? '【'+item.userFarmerName +'】' : ''}线上支付使用`);
                if(item.userVendorName) {
                  item.rules.push(`适用于【${item.userVendorName}】商户下的商品`);
                }
                if(item.mtypeName) {
                    item.rules.push(`仅限【${item.mtypeName}】的商品使用`);
                  }
                if(item.userTypeName) {
                  item.rules.push(`适用于【${item.userTypeName}】分类下的商品`);
                }
                if(item.userGoodsName) {
                  item.rules.push(`仅限【${item.userGoodsName}】商品使用`);
                }
                if(item.userOrderType == 'DELIVERY') {
                  item.rules.push(`仅限配送订单使用`);
                }
                if(item.userOrderType == 'PICKUP') {
                  item.rules.push(`仅限自提订单使用`);
                }
                if(item.type=='POSTAGE_COUPON') {
                  item.rules.push(`仅减免配送费金额`);
                }
                return item;
               })
                that.setData({
                    datalist: datalist
                })
            }
        });
    },
    ruleChange(event) {
        let index = event.currentTarget.dataset.index,
            datalist = this.data.datalist;
        datalist[index].rule = !datalist[index].rule;
        console.log(datalist)
        this.setData({
            datalist
        });
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.getHeight()
    },


    /**关闭弹窗 */
    onClose() {
        this.setData({
            show: false,
            qrCodeUrl: '',
        })
    },
    /**获取优惠券二维码 */
    handleGetQrcode(e) {
        http.request({
            method: "GET",
            url: app.globalData.ajax_url + "/v1/app/market/lm/member/down/code?cardNumber=" + e.currentTarget.dataset.card,
            data: {},
            responseType: 'arraybuffer',
            header: {
                "Content-Type": "application/json;charset=UTF-8",
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey
            },
            success: res => {
                console.log(res)
                // let base64 = wx.arrayBufferToBase64(res.data),
                let qrCodeUrl = 'data:image/jpeg;base64,' + res.data
                console.log(qrCodeUrl)
                this.setData({
                    qrCodeUrl: qrCodeUrl,
                    show: true
                })
            }
        });
    },
    /**
     * @desc 去领券中心
     */
    handleGoCenter() {
        wx.navigateTo({
            url: '/pages/my/coupon/coupon',
        })
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