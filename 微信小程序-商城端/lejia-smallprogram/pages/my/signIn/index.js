// pages/my/signIn/index.js
var date = require("../../../utils/date.js");
var app = getApp();
import http from '../../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgUrl: app.globalData.file_url,
    todayDate: "1",
    todayMonth: "",
    todayYear: "",
    nextMonth: "",
    nextYear: "",
    prevYear: "",
    prevMonth: "",
    isAuto: true,//登录用
    iShidden: true,//登录用
    signData: {
      points: "",
      signNum: "",
      nowDays: false
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setDate();
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

  /**
   * 日历初始化
   */
  setDate: function () {
    var getToday = new Date();
    var todayDate = getToday.getDate();
    var todayMonths = getToday.getMonth();
    var todayMonth = (todayMonths + 1);
    var todayYear = getToday.getFullYear();
    var todayss = getToday.getDate();
    if (todayMonth < 10) {
      var todayMonthss = "0" + todayMonth;
    } else {
      var todayMonthss = todayMonth;
    }
    /**
     * 后台请求获取选中日期并显示日历
     */
    this.getDate(todayYear, todayMonth, todayYear, todayMonthss);
    this.setData({
      todayDate: todayDate,
      todayMonth: todayMonth,
      todayYear: todayYear,
      prevYear: todayYear,
      nextYear: todayYear,
      prevMonth: todayMonth,
      nextMonth: todayMonth,
      showYear: todayYear,
      showMonth: todayMonth
    });//存入一份原始月份日期，一份用来跳转的月份
  },
  sign_prev: function () {
    console.log("上一月");
    var showMonth = this.data.showMonth;
    var todayMonth = this.data.todayMonth;
    if (showMonth == "1") {
      var showMonth = "12";
      var showYear = parseInt(this.data.showYear) - 1;
    } else {
      var showMonth = parseInt(this.data.showMonth) - 1;
      var showYear = this.data.showYear;
    }
    var that = this;
    if (showMonth < 10) {
      var showMonths = "0" + showMonth;
    } else {
      var showMonths = showMonth;
    }
    /**
     * 后台请求获取选中日期并显示日历
     */
    this.getDate(showYear, showMonth, showYear, showMonths);
    this.setData({
      showYear: showYear,
      showMonth: showMonth,
    });
  },
  sign_next: function () {
    console.log("下一月");
    var showMonth = this.data.showMonth;
    var todayMonth = this.data.todayMonth;

    if (showMonth == "12") {
      var showMonth = "1";
      var showYear = parseInt(this.data.showYear) + 1;
    } else {
      var showMonth = parseInt(this.data.showMonth) + 1;
      var showYear = this.data.showYear;
    }
    var that = this;
    if (showMonth < 10) {
      var showMonths = "0" + showMonth;
    } else {
      var showMonths = showMonth;
    }
    /**
     * 后台请求获取选中日期并显示日历
     */
    this.getDate(showYear, showMonth, showYear, showMonths);

    this.setData({
      showYear: showYear,
      showMonth: showMonth,
    });

  },
  getDate: function(showYear, showMonth, todayYear, todayMonths) {
    var godates = todayYear + "-" + todayMonths;//向服务器发送自己需要的签到月份数据
    console.log(godates)
    var that = this;

    http.request({
      url: app.globalData.ajax_url + "/v1/app/market/lm/member/query",///v1/app/market/lm/member/query
      method: "POST",
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      data: {
        signMonth: godates,
      },
      success: function (res) {
        console.log(res.data)
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
        }else {
          var $datas = res.data.result.signDates;
          date.date.bulidCal(showYear, showMonth, that, $datas);
          that.setData({
            signData: {
              points: res.data.result.points,
              nowDays: res.data.result.nowDays,
              // nowDays: false,
              signNum: res.data.result.signNum
            }
          });
        }
      }, error: function () {
        wx.showToast({
          title: '服务器错误',
          icon: 'loading',
          duration: 1500
        });
      }
    });
  },
  //会员签到
  signIn: function() {
    var that = this;
    if(this.data.signData.nowDays) {
      wx.showToast({
        title: "今日您已签到",
        duration: 1500
      });
      return;
    }
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/ins',///v1/app/market/lm/member/ins
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
        }else {
        wx.showToast({
          title: "签到成功",
          duration: 1500
        });
        // that.setData({
        //   ['signData.nowDays']: true
        // }); 
        that.setDate();
      }
      }
    })
  },
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function(){
    this.setDate();
  }
})