// pages/my/supportSubmit/index.js
let app = getApp();
import http from '../../../utils/http'
import utils from '../../../utils/util.js'
Page({

    /**
     * 页面的初始数据
     */
    data: {
        supportStepOneData: null,   // 退换货数据
        reason: null,               // 退货原因
        selectReason: null,         // 退货原因弹窗选中项
        showApplyReason: false,     // 退货原因弹窗
        reasonList: null,           // 退货原因列表
        describe: null,             // 补充描述
        photo: [],                  // 凭证图片
        courierType: null,          // 返回方式
        courierTypeEnum: {          // 返回方式枚举
            JD_DOOR_TO_DOOR_PICKUP: '京东上门取件',
            SELF_MAILING: '自行寄出',
        },
        selectCourierType: null,    // 返回方式弹窗选中项
        showReturnMethod: false,    // 返回方式弹窗
        addr: null,                 // 取件地址
        pickupTime: null,           // 取件时间
        pickupTimeStr: null,        // 取件时间文本
        showPickupTime: false,      // 取件时间弹窗
        timeItems: null,            // 取件时间列表
        mainActiveIndex: 0,         // 取件时间日期Index
        activeId: null,             // 取件时间时间段Id
        receiptAddr: null,          // 收件地址
        showTips: false,            // 成功提交页显示
        supportData: null,          // 退换货预退款数据
        disabled: false,            // 禁止提交,
        addrName: null,             // 点击的地址名称
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const supportStepOneData = JSON.parse( wx.getStorageSync('supportStepOneData') )
        console.log(supportStepOneData);
        if(supportStepOneData.returnExchange == 'EXCHANGE') {
            wx.setNavigationBarTitle({
              title: '申请换货',
            })
        }
        if(supportStepOneData.addr) {
            this.setData({
                addr: supportStepOneData.addr,
                receiptAddr: supportStepOneData.addr
            })
        }
        this.setData({supportStepOneData})
        this.getData()
    },

    getData() {
        const url = "/v2/app/market/lm/refund/preRefundOrder";
        const { supportStepOneData } = this.data
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: supportStepOneData,
            header: {
                'content-type': 'application/json;charset=UTF-8',
            },
            success: res => {
                if (res.data.success) {
                    this.setData({
                        supportData: res.data.result,
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

    /**
     * 确认提交退换货申请
     */
    handleSubmit() {
        const { reason, describe, photo, courierType, addr, pickupTime, receiptAddr, supportStepOneData } = this.data
        if(!reason) {
            wx.showToast({
              title: '请选择申请原因',
              icon: 'none'
            })
            return
        }
        if(!describe && (!photo || !photo.length)) {
            wx.showToast({
                title: '补充描述和凭证',
                icon: 'none'
            })
            return
        }
        if(!courierType) {
            wx.showToast({
              title: '请选择返回方式',
              icon: 'none'
            })
            return
        }
        if(courierType == 'JD_DOOR_TO_DOOR_PICKUP' && !addr) {
            wx.showToast({
              title: '请选择取件地址',
              icon: 'none'
            })
            return
        }
        if(courierType == 'JD_DOOR_TO_DOOR_PICKUP' && !pickupTime) {
            wx.showToast({
              title: '请选择取件时间',
              icon: 'none'
            })
            return
        }
        if(supportStepOneData.returnExchange == 'EXCHANGE' && !receiptAddr) {
            wx.showToast({
                title: '请选择收获地址',
                icon: 'none'
              })
              return
        }
        const url = "/v2/app/market/lm/refund/applyForOrderRefund";
        const params = {
            pkey: supportStepOneData.pkey,
            lines: supportStepOneData.lines,
            reason,
            describe,
            photo: photo.map(item => item.url),
            jdType: supportStepOneData.returnExchange,
            courierType,
            pickupTimeStart: pickupTime ? pickupTime.date + ' ' + pickupTime.time.split('-')[0] : '',
            pickupTimeEnd: pickupTime ? pickupTime.date + ' ' + pickupTime.time.split('-')[1] : '',
            addrPkey: addr.pkey,
            receiptAddrPkey: receiptAddr.pkey || ''
        }
        this.setData({
            disabled: true
        })
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": "application/json",
            },
            success: res => {
                if (res.data.success) {
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
                    disabled: false,
                });

            }
        })
    },

    /**
     * 申请成功，返回订单详情页
     */
    handeBack() {
        wx.navigateBack({
            delta: 2,
        });
    },

    /** 
     * 退货原因 START
     */
    handleShowApplyReason() {
        this.setData({
            showApplyReason: true,
            selectReason: this.data.reason
        })
        this.getReasonList()
    },
    handleCloseApplyReason() {
        this.setData({
            showApplyReason: false,
            selectReason: null,
        })
    },
    getReasonList() {
        const url = "/v2/app/market/lm/refund/list/reason/drop";
        const params = this.data.supportStepOneData
        http.request({
          method: "POST",
          url: app.globalData.ajax_url + url,
          data: {
            status: params.status,
            flag: params.orderOir === 'POINTS_MALL',
            type: params.orderType,
            jdType: params.returnExchange,
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
    handleReasonChange(data) {
        const reason = data.currentTarget.dataset.reason
        this.setData({ selectReason: reason })
    },
    handleReasonConfirm() {
        this.setData({
            reason: this.data.selectReason
        })
        this.handleCloseApplyReason()
    },
    /** 
     * 退货原因 END
     */
    
    /**
     * 补充描述和凭证 START
     */
    onTextareaChange(event) {
        this.setData({
            describe: event.detail
        })
    },
    afterRead(event) {
        const {
            file
        } = event.detail;
        const that = this;
        const url = "/v1/app/market/lm/member/uploadImage";
        const txt = this.data.describe;
        wx.uploadFile({
            filePath: file.url,
            name: 'file',
            url: app.globalData.ajax_url + url,
            header: {
                "Content-Type": 'application/xml',
                "openid": app.globalData.openid,
                "farmer": app.globalData.location.pkey,
                "ascription": app.globalData.ascription
            },
            success: function (res) {
                const {
                    photo = []
                } = that.data;
                photo.push({
                    ...file,
                    url: JSON.parse(res.data).result.url
                });
                that.setData({
                    photo
                });
                console.log("photo", txt, that.data.describe);
                if (txt !== "" && that.data.describe == "") {
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
    uploaderDelete(event) {
        const index = event.detail.index;
        console.log(index);
        console.log(this.data.photo)
        let photo = this.data.photo;
        photo.splice(index, 1);
        this.setData({
            photo: photo
        })
    },
     /**
     * 补充描述和凭证 END
     */

    /** 
     * 返回方式 START
     */
    handleShowReturnMethod() {
        this.setData({
            showReturnMethod: true,
            selectCourierType: this.data.courierType
        })
    },
    handleCloseReturnMethod() {
        this.setData({
            showReturnMethod: false,
            selectCourierType: null,
        })
    },
    handleReturnMethodChange(data) {
        const type = data.currentTarget.dataset.type
        this.setData({ selectCourierType: type })
    },
    handleReturnMethodConfirm() {
        this.setData({
            courierType: this.data.selectCourierType
        })
        this.handleCloseReturnMethod()
    },
    /** 
     * 返回方式 END
     */

     /**
      * 切换取件/收获地址 START
      */
    handleGoAddr(data) {
        console.log(data);
        const name = data.currentTarget.dataset.name
        const addr = this.data[name]
        this.setData({
            addrName: name
        })
        console.log(addr);
        wx.navigateTo({
            url: `/pages/pay/addr/index?isSupport=true&pkey=${addr ? addr.pkey : ''}`,
        });
    },
    chooseAdress(addr) {
        const { addrName } = this.data
        console.log(addrName);
        this.setData({
            [addrName]: addr
        })
    },
    /**
     * 切换取件/收获地址 END
     */

     /** 
     * 取件时间 START
     */
    handleShowPickupTime() {
        this.setData({
            showPickupTime: true
        })
        this.getTimeList()
    },
    handleClosePickupTime() {
        this.setData({
            showPickupTime: false
        })
    },
    handleClickNav(data) {
        this.setData({
            mainActiveIndex: data.detail.index
        })
    },
    handleClickItem(data) {
        this.setData({
            activeId: data.detail.id
        })
    },
    handlePickupTimeConfirm() {
        const { timeItems, mainActiveIndex, activeId } = this.data
        console.log({ timeItems, mainActiveIndex, activeId });
        if(activeId === null) {
            wx.showToast({
              title: '请选择取货时间',
              icon: 'none'
            })
            return
        }
        const date = timeItems[mainActiveIndex]
        const time = date.children.find(item => item.id == activeId)
        console.log({date, time});
        this.setData({
            pickupTime: {
                date: date.date,
                time: time.text
            },
            pickupTimeStr: this.formatDate(date.date) + ' ' + time.text
        })
        this.handleClosePickupTime()
    },
    getTimeList() {
        const url = "/v1/app/jd/refund/generateTimeList";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            success: res => {
                if (res.data.success) {
                    this.handleTimeList(res.data.result)
                } else {
                    wx.showToast({
                        title: res.data.msg,
                        icon: "none"
                    });
                }
            }
        })
    },
    handleTimeList(list) {
        const items = []
        list.map((item, index) => {
            const [ date, time ] = item.split(" ")
            const findIndex = items.findIndex(_item => _item.date == date)
            if(findIndex > -1) {
                items[findIndex].children.push({
                    text: time,
                    id: index,
                })
            } else {
                items.push({
                    date,
                    text: this.formatDateWeek(date),
                    children: [
                        {
                            text: time,
                            id: index
                        }
                    ]
                })
            }
        })
        console.log(items);
        this.setData({
            timeItems: items
        })
    },
    formatDate(dateString) {
        const today = new Date(); // 获取当前日期
        const targetDate = new Date(dateString); // 解析目标日期
    
        // 比较年份和月份是否相同
        if (targetDate.getFullYear() === today.getFullYear() && targetDate.getMonth() === today.getMonth()) {
            // 计算日期差
            const dayDiff = targetDate.getDate() - today.getDate();
            if (dayDiff === 0) {
                return `${targetDate.getMonth() + 1}-${targetDate.getDate()} [今天]`;
            } else if (dayDiff === 1) {
                return `${targetDate.getMonth() + 1}-${targetDate.getDate()} [明天]`;
            } else if (dayDiff === -1) {
                return `${targetDate.getMonth() + 1}-${targetDate.getDate()} [昨天]`;
            } else {
                // 对于其他日期，可以返回原始格式或自定义格式
                return `${targetDate.getMonth() + 1}-${targetDate.getDate()}`;
            }
        } else {
            // 如果年份或月份不同，直接返回完整的日期格式
            return `${targetDate.getFullYear()}-${targetDate.getMonth() + 1}-${targetDate.getDate()}`;
        }
    },
    formatDateWeek(date) {
        const [year, month, day] = date.split('-')
        const week = utils.getWeekDate(date)
        return `${month}-${day}[${week}]`
    },
    /** 
     * 取件时间 END
     */
})