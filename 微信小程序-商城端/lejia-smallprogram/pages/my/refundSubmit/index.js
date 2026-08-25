// pages/my/refundSubmit/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      reason:'',
      describe: "",
      refundData: {},
      reasonList: [],
      photo: [],
      loading: false,
      disabled: true,
      showTips: false,
      radioResult: ""
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.getReasonList();
      this.getData();
    },
    getData(){
      const url="/v2/app/market/lm/refund/preRefundOrder";
      const params = JSON.parse(wx.getStorageSync('refundStepOneData'));
      console.log("params",params)
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey:params.pkey,
          lines: params.lines
        },
        header: {
          'content-type': 'application/json;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey, 
        },
        success: res => {
          if (res.data.success) {
            this.setData({
              refundData: res.data.result,
              ['refundData.lines']: params.lines,
              ['refundData.pkey']: params.pkey,
              ['refundData.tel']: params.tel,
              ['refundData.orderType']: params.orderType,
            })
          } else {
            wx.showToast({
              title: res.data.msg,
              icon: "none"
            });
          }
        }
      })
    },
    backToRefundApply(){
      // 京东专区没有选择商品页，不能返回
      const { refundData } = this.data
      if(refundData.orderType && refundData.orderType == 'INTEGRAL_JD_ORDER') return
      wx.navigateBack({
        delta: 1 // 返回的页面数，如果 delta 大于现有页面数，则返回到首页
      });
    },
    onChange(event) {
      this.setData({
        reason: event.detail,
      });
      if(this.data.describe && this.data.reason) {
        this.setData({
          disabled: false
        })
      } else {
        this.setData({
          disabled: true
        })
      }
      
    },
    onCellClick(event){
      const { name } = event.currentTarget.dataset;
      this.setData({
        radioResult: name,
        reason: name,
      });
      if(this.data.describe && this.data.reason) {
        this.setData({
          disabled: false
        })
      } else {
        this.setData({
          disabled: true
        })
      }
    },
    onTextareaChange(event){
      this.setData({
        describe: event.detail
      })
      if(this.data.describe && this.data.reason) {
        this.setData({
          disabled: false
        })
      } else {
        this.setData({
          disabled: true
        })
      }
      
    },
    getReasonList() {
      const url="/v2/app/market/lm/refund/list/reason/drop";
      const params = JSON.parse(wx.getStorageSync('refundStepOneData'));
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          status: params.status,
          flag: params.orderOir === 'POINTS_MALL',
          type: params.orderType
        },
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {
          if (res.data.success) {
            this.setData({
              reasonList: res.data.result
            })
          }
        }
      })
    },
    handeBack() {
      wx.navigateBack({
        delta: 2,
      });
    },
    afterRead(event) {
      const { file } = event.detail;
      const that = this;
      const url = "/v1/app/market/lm/member/uploadImage";
      const txt = this.data.describe;
      this.setData({
        loading: true,
        disabled: true,
      });

      wx.uploadFile({
        filePath: file.url,
        name: 'file',
        url: app.globalData.ajax_url + url,
        header: {
          "Content-Type": 'application/xml',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey,
          "ascription":app.globalData.ascription
        },
        success: function (res) {
          console.log("res",res)
          const { photo = [] } = that.data;
          photo.push({ ...file, url: JSON.parse(res.data).result.url });
          that.setData({ photo });
          console.log("photo",txt,that.data.describe);
          if(txt !=="" && that.data.describe=="") {
            that.setData({
              describe: txt
            })
          }
          that.setData({
            loading: false,
            disabled: false,
          });
        },
        fail(err) {
            wx.showToast({
                title: '图片上传失败',
                icon: 'none',
                duration: 2000
            });
            that.setData({
              loading: false,
              disabled: false,
            });
        }
    })
    },
    uploaderDelete(event){
      const index = event.detail.index;
      console.log(index);
      console.log(this.data.photo)
      let photo = this.data.photo;
      photo.splice(index, 1);
      
      this.setData({
        photo: photo
      })
    },
    // 申请退款
    handleSubmit(){
      if (!this.data.reason) {
        wx.showToast({
          title: '请选择退款原因',
          icon: 'none'
        });
        return;
      }

      if (!this.data.describe) {
        wx.showToast({
          title: '请输入描述',
          icon: 'none'
        });
        return;
      }
      this.setData({
        loading: true,
        disabled: true,
      });
      const photo = this.data.photo.map(item=> {
        return item.url;
      })
      console.log("photo",photo)
      const url = "/v2/app/market/lm/refund/applyForOrderRefund";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: this.data.refundData.pkey,
          lines: this.data.refundData.lines,
          reason: this.data.reason,
          photo: photo,
          describe: this.data.describe
        },
        header: {
          "Content-Type": "application/json",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {  
          if(res.data.success) {
            this.setData({
              showTips: true
            });
          } else {
            wx.showToast({
              title: res.data.msg || '',
              icon: 'none'
            });
          }
          this.setData({
            loading: false,
            disabled: false,
          });

        }
      })


    },
    phoneClick() {
      const Mobile = this.data.refundData.tel
      wx.makePhoneCall({
        phoneNumber: Mobile,
      })
  
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