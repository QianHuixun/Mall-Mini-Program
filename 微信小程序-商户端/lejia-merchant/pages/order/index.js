// pages/order/index.js
var app = getApp();
var loadMoreView;
import utils from '../../utils/util.js';
import Dialog from '@vant/weapp/dialog/dialog';
import http from '../../utils/http'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    date: [], //时间区间
    selectDate: [],
    orderList: [], //列表数据
    alreadySettlement: 0, //已结营收
    awaitSettlement: 0, //未结营收
    pageSize: 6,
    page: 0,
    total: 0,
    status: '', // 订单状态
    show: false, //日历选择是否出现
    minDate: new Date(2020,1,1).getTime(),//下限日期
    maxDate:new Date().getTime(),//上限日期，
    active: 0,
    showPopup: false,
    showPicker: false,
    columns: ['全部','待确认','待结算','已结算'],
    value: '',
    option: [
      { text: '全部', value: '' },
      { text: '待确认', value: 'AWAIT_CONFIRM' },
      { text: '待结算', value: 'NOT_START' },
      { text: '已结算', value: 'SUCCESS' },
    ],
  },
  getData() {
    var that = this,
      url = "/v1/app/vendor/orderstatus/query",
      date = this.data.date,
      params = null
      
      if(this.data.active === 0) {
        params = {
          flag: true,
          pagesize: this.data.pageSize,
          page: this.data.page
        }
      } else {
        params = {
          flag: false,
          endDate: date[1],
          startDate: date[0],
          pagesize: this.data.pageSize,
          page: this.data.page,
          status: this.data.status,
        };
      }
      // params = {
      //   endDate: date[1],
      //   startDate: date[0],
      //   pagesize: this.data.pageSize,
      //   page: this.data.page
      // };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        "Content-Type": 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        console.log(res.data.result);
        if (res.data.success) {
          that.setData({
            alreadySettlement: res.data.result.alreadySettlement,
            awaitSettlement: res.data.result.awaitSettlement,
          })
          if (that.data.page == 0) {
            that.setData({
              orderList: res.data.result.lines.content,
              page: ++that.data.page
            });
          } else {
            that.setData({
              orderList: that.data.orderList.concat(res.data.result.lines.content),
              page: ++that.data.page
            });
          }
          res.data.result.lines.curPage = that.data.page;
          let result = res.data;
          result.result = res.data.result.lines;
          loadMoreView.loadMoreComplete(result);
          console.log(that.data.orderList)
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: "none"
          })
        }

      }

    })
  },
  dateInit() {
    let date = new Date(),
      // currentMonth = date.getMonth(),
      // nextMonth = ++currentMonth,
      // nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1),
      // oneDay = 1000 * 60 * 60 * 24,
      // lastTime = new Date(nextMonthFirstDay - oneDay),
      // month = parseInt(lastTime.getMonth() + 1),
      // day = lastTime.getDate();
      month = parseInt(date.getMonth() + 1),
      day = date.getDate();
    if (month < 10) {
      month = '0' + month
    }
    if (day < 10) {
      day = '0' + day
    }

    let firstDate = this.getPreDay(11),
      lastDate = date.getFullYear() + '-' + month + '-'+ day;
    this.setData({
      maxDate:lastDate,
      date: [firstDate, lastDate]
    })
  },
  getPreDay(count) {
    var time = new Date();
    time.setTime(time.getTime() - (24 * 60 * 60 * 1000 * count));

    var year = time.getFullYear();
    var month = ((time.getMonth() + 1) > 9 ? (time.getMonth() + 1) : '0' + (time.getMonth() + 1));
    var day = (time.getDate() > 9 ? time.getDate() : '0' + time.getDate());
    return year + '-' + month + '-' + day;
  },
  showPopup() {
    this.setData({ 
      showPopup: true,
      selectDate: this.data.date,
      value: this.data.status
     });
  },
  closePopup() {
    this.setData({ showPopup: false });
  },
  /**切换tabs*/ 
  onChange(val) {
    console.log(val)
    this.data.active = val.detail.name
    this.data.page = 0
    console.log(this.data.active);
    this.getData()
  },
  /** 确认采购完成 */
  onFinish(event) {
    console.log(event);
    const pkey = event.target.dataset.pkey
    Dialog.confirm({
      message: '是否确认采购完成？',
    })
      .then(() => {
        console.log('确认成功');
        this.confirmFinish(pkey)
      })
      .catch(() => {
        // console.log('取消完成');
      })
  },
  confirmFinish(pkey) {
    console.log(pkey);
    const that = this,
          url = "/v1/app/vendor/purchase/finish",
          params = {pkey}
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        "Content-Type": 'application/x-www-form-urlencoded',
        "openid": app.globalData.openid
      },
      success: function (res) {
        console.log(res);
        if(res.success){
          that.setData({
            page: 0
          })
        }else{
          wx.showToast({
            title: res.data.msg,
            icon:'none'
          })
          that.setData({
            page:0,
          })
        }
        that.getData()
      }
    })
  },
  /** 重置查询条件 */
  onReset() {
    let date = new Date(),
      month = parseInt(date.getMonth() + 1),
      day = date.getDate();
    if (month < 10) {
      month = '0' + month
    }
    if (day < 10) {
      day = '0' + day
    }

    let firstDate = this.getPreDay(11),
      lastDate = date.getFullYear() + '-' + month + '-'+ day;
    this.setData({
      maxDate:lastDate,
      selectDate: [firstDate, lastDate],
      value: ''
    })
  },
  /**下拉菜单切换 */
  dropdownChange(event) {
    // console.log(val);
    this.setData({
      value: event.detail
    })
    console.log(this.data.value);
  },
  /**确认查询条件 */
  onSubmit () {
    this.setData({
      date: this.data.selectDate,
      status: this.data.value,
      showPopup: false,
      page: 0
    })
    this.getData()
  },
  loadMoreListener: function (e) {
    this.getData()
  },
  clickLoadMore: function (e) {
    this.getData()
  },
  /**打开日历 */
  opencalendar(){
    this.setData({
      show: true
    });
  },
  /**关闭日历 */
  onClose() {
    this.setData({
      show: false
    });
  },
  
  /**日期选择关闭 */
  onConfirm(event) {
    const [start, end] = event.detail;
    this.setData({
      show: false,
      page:0,
      selectDate: [utils.formatTimeInArr(start/1000,'Y-M-D') , utils.formatTimeInArr(end/1000,'Y-M-D')],
    });
    this.getData();
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    loadMoreView = this.selectComponent("#loadMoreView");
    this.dateInit()
    this.getData()
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
    console.log('onPullDownRefresh');
    loadMoreView.loadMore()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    console.log('onPullDownRefresh');
    loadMoreView.loadMore()
  },

  /**
   * 用户点击右上角分享
   */
  // onShareAppMessage: function () {
  //   return {
  //     title: '菜篮商户',
  //     path: '/pages/introduce/introduce',
  //   }
  // }
})