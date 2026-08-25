// pages/wallet/withdraw/index.js
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      popShow: false,
      bankInfo: {},
      walletAmt: "",
      amount: "",
      shortBankCard: "",
      loading: false,
    },
    goAccount(){
      if(this.data.bankInfo.bankcard && !this.data.bankInfo.allowedUpd) return
      wx.navigateTo({
        url: '/pages/wallet/account/index',
      });  
    },
    //只能输入金额数字
 checkMoney(e){
    let price = e.detail;
  console.log(e,price)
      price = price.replace(/[^\d.]/g, "");  //清除“数字”和“.”以外的字符
      price = price.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的
      price = price.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
      price = price.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数
      if (price.indexOf(".") < 0 && price != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
          price = parseFloat(price);
      }
if(price > this.data.walletAmt) {
  price = this.data.walletAmt;
}
    this.setData({
      amount:price
    });
},
    // 点击 全部提现
    getAllAmount(){
      this.setData({
        amount: this.data.walletAmt
      })
    },
    // 获取 我的钱包 余额和待到账
  getMyWalletData(){
    var that = this,
    url = "/v3/app/vendor/wallet/get";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {},
      header: {
        "Content-Type": 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          that.setData({
            walletAmt: res.data.result.walletAmt,
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
    // 获取银行信息
    getBankInfo() {
      var that = this, url = "/v3/app/vendor/wallet/get/bankInfo";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {},
        header: {
          "Content-Type": 'application/x-www-form-urlencoded',
          "openid": app.globalData.openid
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({
              bankInfo: res.data.result,
            });
            if(that.data.bankInfo.bankcard) {
              that.setData({
                shortBankCard: that.data.bankInfo.bankcard.substr(-4)
              })
            }

          }
        }
      });
    },
    // 关闭弹出层
    onPopClose(){
      this.setData({
        popShow: false,
        amount: "",
      });
      this.getMyWalletData();
    },
    // 确认提现
    onSubmit(){
      var that = this, url = "/v3/app/vendor/wallet/apply/withdrawal";
      this.setData({
        loading: true
      })
      if(!this.data.amount) {
        wx.showToast({
          title: '请输入提现金额',
          icon: "none"
        })
      }
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {amount: this.data.amount},
        header: {
          "Content-Type": 'application/x-www-form-urlencoded',
          "openid": app.globalData.openid
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({
              popShow: true,
              amount: '',
            });

          } else {
            wx.showToast({
              title: res.data.msg,
              icon: "none"
            })
          }
          that.setData({
            loading: false
          });

        }
      });
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.getMyWalletData();
      
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
      this.getBankInfo();
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