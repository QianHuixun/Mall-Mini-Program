// pages/wallet/account/index.js
import { getBank } from "../../../utils/banks";
import http from '../../../utils/http';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      value: "",
      show: false,

        bankcard: "",
        bankname: "",
        bankBranchName: "",
        bankuser: "",
        mobile: "",
        code: "",
  
      codeText: "获取验证码",
      isDisabled: false,
      list: getBank(),
      dialogShow: false,
    },
    onDialogClose(){
      this.setData({
        dialogShow: false,
      });
    },
    submitCheck() {
      if(!this.data.bankname) {
        wx.showToast({
          title: "请选择提现银行",
          icon: "none"
        });
        return;
      }
      if(!this.data.bankBranchName) {
        if(!this.data.bankcard) {
          wx.showToast({
            title: "请输入银行卡号",
            icon: "none"
          });
          return;
        }
  
        wx.showToast({
          title: "请输入开户支行",
          icon: "none"
        });
        return;
      }

      
      if(!this.data.bankuser) {
        wx.showToast({
          title: "请输入持卡人姓名",
          icon: "none"
        });
        return;
      }

      if(!this.data.code) {
        wx.showToast({
          title: "请输入验证码",
          icon: "none"
        });
        return;
      }

      this.setData({
        dialogShow: true
      })
    },
    onPopShow(){
      this.setData({
        show: true,
        radio: this.data.bankname
      });
    },
    onActionClose(){
      this.setData({
        show: false
      })
    },
    onCellClick(event) {
      const { name } = event.currentTarget.dataset;
      this.setData({
        radio: name,
      });
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
              bankcard: res.data.result.bankcard || "",
              bankname: res.data.result.bankname || "",
              bankBranchName: res.data.result.bankBranchName || "",
              bankuser: res.data.result.bankuser || "",
               mobile: res.data.result.mobile || "",
            });
          }         
        }
      });
    },
    onChange(){
      this.setData({
        "bankname": this.data.radio,
        show: false,
      });
    },
    getCode(){
      this.setData({
        isDisabled: true,
      });
      var that = this,url = "/v3/app/vendor/wallet/captcha";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {phone: this.data.mobile},
        header: {
          "Content-Type": 'application/x-www-form-urlencoded',
          "openid": app.globalData.openid
        },
        success: function (res) {
          if (res.data.success) {
            that.startCountdown(60);
            wx.showToast({
              title: "验证码已发送",
              icon: "none"
            })
          } else{
            that.setData({
              isDisabled: false,
            })
          }       
        }
      });
    },
    startCountdown(seconds) {
      var that= this;
      var timer = setInterval(function() {
          seconds--;
          
          if (seconds < 0) {
              clearInterval(timer);
              that.setData({
                isDisabled: false,
                codeText: "获取验证码"
              })
              return;
          }
          that.setData({
            codeText:  seconds + "s"
          })
      }, 1000);
  },

  onSubmit(){
    var url = "/v3/app/vendor/wallet/upd/bankInfo";
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        bankcard: this.data.bankcard,
        bankname: this.data.bankname,
        bankBranchName: this.data.bankBranchName,
        bankuser: this.data.bankuser,
        mobile: this.data.mobile,
        code: this.data.code
      },
      header: {
        "Content-Type": "application/json;charset=UTF-8",
        "openid": app.globalData.openid
      },
      success: function (res) {
        if (res.data.success) {
          wx.showToast({
            title: '保存成功',
            icon: 'passed' 
          });
          setTimeout(()=>{
            wx.navigateBack({         //返回上一页  
              delta:1
            });
          },300);
        }  else {
          console.log(res)
          wx.showToast({
            title: res.data.msg ,
            icon: 'passed' 
          });
        } 
      }
    });

  },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.getBankInfo();
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