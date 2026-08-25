// components/dragBall/index.js

var startPoint
import http from '../../utils/http.js';
const app = getApp();
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        type: {
            type: String,
            value: 'MARKET_MALL'//SELF_EMPLOYED运营 MARKET_MALL市场
        },
        notShow: {
            type: Boolean,
            value: false
        }
    },

    /**
     * 组件的初始数据
     */
    data: {
        buttonTop: 0,
        buttonLeft: 0,
        windowHeight: '',
        windowWidth: '',
        //角标显示数字
        corner_data: 0,
        show: false,
        marketDragBallInfo: {},
        operationDragBallInfo: {},
        hasMarketCustomerService: false,
        hasOperationCustomerService: false,
    },
    lifetimes: {
        attached: function () {
            this.setData({
                corner_data: 3
            })
            // 获取购物车控件适配参数
            var that = this;
            wx.getSystemInfo({
                success: function (res) {
                    console.log(res);
                    // 屏幕宽度、高度
                    console.log('height=' + res.windowHeight);
                    console.log('width=' + res.windowWidth);
                    // 高度,宽度 单位为px
                    that.setData({
                        windowHeight: res.windowHeight,
                        windowWidth: res.windowWidth,
                        buttonTop: res.windowHeight * 0.45, //这里定义按钮的初始位置
                        buttonLeft: 0, //这里定义按钮的初始位置
                    })
                }
            })
            // 在组件实例进入页面节点树时执行
        },
        detached: function () {
            // 在组件实例被从页面节点树移除时执行
        },
    },
    /**
     * 组件的方法列表
     */
    methods: {
        //可拖动悬浮按钮点击事件
        handleClick: function () {
           this.getData();
            this.setData({
                show: true
            })
        },
        getData() {
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/customerService/getInfo',
            data: {
              type: 'MARKET_MALL'
            },
           
            success: (res) => {
              if (res.data.success) {
                const result = res.data.result
                this.setData({
                    marketDragBallInfo: result,
                    hasMarketCustomerService: result.customerServiceId && result.customerServiceLink
                })
              }
            }
          });
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v1/app/market/customerService/getInfo',
            data: {
              type: 'SELF_EMPLOYED'
            },
            success: (res) => {
              if (res.data.success) {
                const result = res.data.result
                this.setData({
                    operationDragBallInfo: result,
                    hasOperationCustomerService: result.customerServiceId && result.customerServiceLink
                })
              }
            }
          });
        },
        handleMobile(data) {
            const tel = data.currentTarget.dataset.tel

            wx.makePhoneCall({
                phoneNumber: tel,
            })
          
            this.onClose()
        },
        handleCustomer(data) {
          const type = data.currentTarget.dataset.type
          let DragBallInfo = null
          if(type == 'market') {
            DragBallInfo = this.data.marketDragBallInfo
          } else {
            DragBallInfo = this.data.operationDragBallInfo
          }
          console.log("handleCustomer");
          wx.openCustomerServiceChat({
            extInfo: { url:  DragBallInfo.customerServiceLink},
            corpId: DragBallInfo.customerServiceId,
            success(res) { /* 处理成功 */
            console.log("handleCustomer2") },
            fail: (err) => { console.error('失败', err) }
          });
        },
        onClose() {
            this.setData({
                show: false
            })
        },
        //以下是按钮拖动事件
        buttonStart: function (e) {
            startPoint = e.touches[0] //获取拖动开始点
        },
        buttonMove: function (e) {
            var endPoint = e.touches[e.touches.length - 1] //获取拖动结束点
            //计算在X轴上拖动的距离和在Y轴上拖动的距离
            var translateX = endPoint.clientX - startPoint.clientX
            var translateY = endPoint.clientY - startPoint.clientY
            startPoint = endPoint //重置开始位置
            var buttonTop = this.data.buttonTop + translateY
            var buttonLeft = this.data.buttonLeft + translateX
            //判断是移动否超出屏幕
            // if (buttonLeft + 50 >= this.data.windowWidth) {
            //     buttonLeft = this.data.windowWidth - 50;
            // }
            if (buttonLeft <= 5) {
                buttonLeft = 0;
            }
            // console.log(buttonLeft,this.data.windowWidth)
            if (buttonLeft >= this.data.windowWidth - 55) {
                buttonLeft = this.data.windowWidth - 50;
            }
            if (buttonTop <= 0) {
                buttonTop = 0
            }
            if (buttonTop + 50 >= this.data.windowHeight) {
                buttonTop = this.data.windowHeight - 50;
            }
            this.setData({
                buttonTop: buttonTop,
                buttonLeft: buttonLeft
            })
        },
        buttonEnd: function (e) {}
    }
})