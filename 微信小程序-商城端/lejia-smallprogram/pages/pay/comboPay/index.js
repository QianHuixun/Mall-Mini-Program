// pages/pay/comboPay/index.js
let app = getApp();
import utils from '../../../utils/util.js';
import Dialog from '@vant/weapp/dialog/dialog';
import http from '../../../utils/http'
import Toast from '@vant/weapp/toast/toast';
let mythis = null;
Page({

    /**
     * 页面的初始数据
     */
    data: {
        imgUrl: app.globalData.file_url,
        statusBarHeight: wx.getSystemInfoSync().statusBarHeight,
        addressPkey: '',
        orderInfo: null,
        shareFarm: '', //分享的市场对象
        memberBalance: null, // 会员余额
        spaceShow: false,
        spaceGoods: null,
        selectInfo: null, // 选中的市场/商户
        selectInfoIndex: null, // 选中的市场/商户的index
        timeShow: false,
        timeItems: '',
        mainActiveIndex: '',
        activeId: '',
        isReservation: '', // 预计配送时间
        walletName: '钱包',
        payType: 'ORDER_WEIXIN', //组合支付2 支付类型
        payAmt: 0,// 支付金额
        payTypeAmt: 0,// 支付类型余额

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        console.log('options', options);
        mythis = this;
        this.setData({
            dineIn: app.globalData.qrCode,
            type: options?.type,
            gwcs: options?.gwcs,
            space: options?.space,
            num: options?.num,
            mtype: options?.mtype,
        })
        if (options.hasOwnProperty('shareFarm') && JSON.parse(options.shareFarm) != "") {
            this.setData({
                marketName: JSON.parse(options.shareFarm).name,
                shareFarm: JSON.parse(options.shareFarm),
            });
        } else {
            this.setData({
                marketName: app.globalData.location.name
            });
        }
        this.getData();
        this.getMemberPay();
        this.isStartingPrice();
        if(app.globalData.ascription == 22 || app.globalData.ascription == 13) {
            this.setData({ walletName: 'I DO' })
        }
    },
    // 初始化支付类型，默认选中热力豆/I DO 支付，优先选热力豆，计算支付金额
    initPayType() {
      const orderInfo = this.data.orderInfo;
      if(orderInfo.unpayType) return;
      const mtype = this.data.mtype;
      let payType = "ORDER_WEIXIN";
      let payTypeAmt = 0;
      let payAmt = 0;
      if(orderInfo.msdPay && orderInfo.myMsd > 0) {
        payType = "MSD_COMBINATION";
        payTypeAmt = orderInfo.myMsd;
      } else if(mtype != 'INTEGRAL_MSD_GOODS' && mtype != 'JD_GOODS' && orderInfo.myCommn > 0) {
        payType = "ELECTRONIC_ACCOUNT_COMBINATION";
        payTypeAmt = orderInfo.myCommn;
      } 
      const orderAmt = orderInfo.goodsSumAmtn;
      payAmt = orderAmt > payTypeAmt ? orderAmt - payTypeAmt : 0;
      const payType2 = payType !== 'NM_MEMBER' || !payType  ?  'ORDER_WEIXIN'  : null;
      this.setData({
        ["orderInfo.payType"]: payType,
        payType: payType2,
        payTypeAmt,
        payAmt,
      });

    },
    /**
     * 获取订单信息
     */
    getData: function () {
        let orderInfo = JSON.parse(wx.getStorageSync('orderInfo'))
        console.log(this.data.orderInfo, orderInfo);
        orderInfo.infos = orderInfo.infos.map((item, index) => {
            // 去除民生专区只能自提的限制
            // if(this.data.mtype === 'INTEGRAL_MSD_GOODS') {
            //     item.delivery = false
            //     item.pickup = true
            // }
            if(item.delivery === false) {
                item.distributionType = 'PICKUP'
                if(item.allowedPickPstime) {
                  item.pstime = ''
                }
                item.pickupAddr = item.splList?.length ? item.splList[0].address : null
                item.pickupPkey = item.splList?.length ? item.splList[0].pkey : null
                if (item.postage) {
                    orderInfo.sumPostage = orderInfo.sumPostage - item.postage
                    orderInfo.goodsSumAmtn = orderInfo.goodsSumAmtn - item.postage
                }
            }
            if(this.data.orderInfo) {
                let oriItem = this.data.orderInfo.infos[index]
                item = oriItem
            }
            return item
        })
        this.setData({
            orderInfo,
            payAmt: orderInfo.goodsSumAmtn
        });
        this.initPayType();
        if(this.data.mtype === 'INTEGRAL_MSD_GOODS') {
            let orderInfo = this.data.orderInfo
            // orderInfo.payType = 'ORDER_MSD'
            this.setData({
                orderInfo,
            });
        }
    },
    /**
     * 获取会员支付余额
     */
    getMemberPay() {
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v2/app/market/lm/order/check/nm/member/pay',
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
            },
            success: (res) => {
                const result = res.data.result
                this.setData({
                    memberBalance: result
                })
            }
        })
    },
    /**
     * 是否满足配送起送价
     */
    isStartingPrice() {
        console.log(this.data.orderInfo);
        let orderInfo = this.data.orderInfo
        orderInfo.infos.forEach((item, index) => {
            if (item.startingPrice > item.sales && item.distributionType !== 'PICKUP') {
                console.log('--isStartingPrice--');
                Dialog.confirm({
                        title: '不满足配送起送价',
                        message: `配送订单商品金额满${item.startingPrice}元起送`,
                        theme: 'round-button',
                        confirmButtonText: '切换自提',
                        className: 'starting_dialog'
                    })
                    .then(() => {
                        this.handlePickupChange(index)
                    })
                    .catch(() => {
                        wx.navigateBack({
                            delta: 1 // 返回的页面层数，1 表示返回上一层页面
                        })
                    });
            }
        })
    },

    /**
     * @desc 未满起送费，切换自提模式
     */
    handlePickupChange(index) {
        console.log('--------');
        let orderInfo = this.data.orderInfo
        let item = orderInfo.infos[index]
        item.distributionType = 'PICKUP'
        item.pstime = ''
        item.pickupAddr = item.splList?.length ? item.splList[0].address : null
        item.pickupPkey = item.splList?.length ? item.splList[0].pkey : null
        orderInfo.infos[index] = item
        if (item.postage) {
            orderInfo.sumPostage -= item.postage
            orderInfo.goodsSumAmtn -= item.postage
        }
        this.setData({
            orderInfo
        })
    },
    /**
     * @desc 返回
     */
    onClickLeft() {
        wx.navigateBack({
            delta: 1
        })
    },
    /**
     * @desc 切换配送地址
     */
    handleChangeAddr(e) {
        console.log(e);
        let isPickup = e.currentTarget.dataset.ispickup || false
        // let isPickup = false
        console.log('isPickup', isPickup);
        let hasMarket = false,
            orderInfo = this.data.orderInfo;
        let pkey = orderInfo.addrPkey
        if (orderInfo.infos[0].farmer) {
            hasMarket = true;
        }
        wx.navigateTo({
            url: `/pages/pay/addr/index?pkey=${pkey}&shareFarm=${JSON.stringify(this.data.shareFarm)}&type=${hasMarket}&isPickup=${isPickup}`,
        });
    },
    addrChange(addressPkey) {
        console.log('------', addressPkey);
        let url
        if(this.data.mtype == 'JD_GOODS') {
            url = this.data.gwcs ? "/v1/app/jd/order/buyGwc" : "/v1/app/jd/order/buyGoods"
        } else {
            url = this.data.gwcs ? "/v3/app/market/lm/order/buyGwc" : "/v3/app/market/lm/order/buyGoods"
        }
        let params = {
                gwcs: this.data.gwcs,
                space: this.data.space,
                num: this.data.num,
                dineIn: this.data.dineIn,
                latitude: wx.getStorageSync('latitude'),
                longitude: wx.getStorageSync('longitude'),
                addressPkey,
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
            success: (res) => {
                console.log(res);
                if (res.data.success) {
                    wx.setStorageSync('orderInfo', JSON.stringify(res.data.result))
                    this.getData()
                } else {
                    Toast(res.data.msg);
                }
            }
        })
    },
    handleMethodsChang(event) {
        console.log(event);
        const {
            item,
            index
        } = event.currentTarget.dataset
        const type = event.target.dataset.type
        const orderInfo = this.data.orderInfo
        console.log(type, index, item);
        if (type === 'PICKUP' && item.distributionType !== type) {
            item.distributionType = type
            if(item.allowedPickPstime) {
              item.pstime = ''
            }
            item.pickupAddr = item.splList?.length ? item.splList[0].address : null
            item.pickupPkey = item.splList?.length ? item.splList[0].pkey : null
            orderInfo.infos[index] = item
            if (item.postage) {
                orderInfo.sumPostage = orderInfo.sumPostage - item.postage
                orderInfo.goodsSumAmtn = orderInfo.goodsSumAmtn - item.postage
            }

            const  payType1 = this.data.orderInfo.payType;
            const amt = this.data.payTypeAmt;
            let payAmt = 0;
            if(payType1 === null) {
             payAmt =  orderInfo.goodsSumAmtn;
           } else {
             payAmt = orderInfo.goodsSumAmtn > amt ? orderInfo.goodsSumAmtn - amt : 0;
           }

            this.setData({
                orderInfo,
                payAmt,
            })
            this.isStartingPrice()
        } else if (type === 'IMMEDIATELY' && item.distributionType === 'PICKUP') {
            item.distributionType = type
            if(item.allowedPickPstime) {
              item.pstime = ''
            }
            item.pickupAddr = null
            item.pickupPkey = null
            orderInfo.infos[index] = item
            if (item.postage) {
                orderInfo.sumPostage = orderInfo.sumPostage + item.postage
                orderInfo.goodsSumAmtn = orderInfo.goodsSumAmtn + item.postage
            }

           const  payType1 = this.data.orderInfo.payType;
           const amt = this.data.payTypeAmt;
           let payAmt = 0;
           if(payType1 === null) {
            payAmt =  orderInfo.goodsSumAmtn;
          } else {
            payAmt = orderInfo.goodsSumAmtn > amt ? orderInfo.goodsSumAmtn - amt : 0;
          }
            this.setData({
                orderInfo,
                payAmt,
            })
            this.isStartingPrice();
        }
    },
    /**
     * @desc 查看所有商品
     */
    handleLookspace(event) {
        console.log('handleLookspace', event.currentTarget.dataset.goods);
        this.setData({
            spaceShow: true,
            spaceGoods: event.currentTarget.dataset.goods,
        })
    },
    /**
     * @desc 关闭商品规格弹窗
     */
    spaceClose() {
        this.setData({
            spaceShow: false,

        })
    },
    /**
     * @desc 切换配送时间
     */
    handleTimeChange(event) {
        const {
            item,
            index
        } = event.currentTarget.dataset
        console.log(item, index);
        this.setData({
            selectInfo: item,
            selectInfoIndex: index
        })
        this.getTime(item)
    },
    onClose() {
        this.setData({
            timeShow: false
        })
    },
    /**
     * 跑腿配送时间 点击事件
     */
    getTime: function (item) {
        this.setData({
            timeShow: true
        })
        let url = '/v1/app/market/lm/order/get/distributionType/psTime'
        if (item.supplier) {
            url = '/v1/app/market/lm/order/get/distributionType/supplier/psTime'
        }
        const data = {
            marketPkey: this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey,
            type: item.distributionType == 'IMMEDIATELY' || item.distributionType == 'ORDERED' ? 'ORDERED' : 'PICKUP',
            addressPkey: item.distributionType == 'IMMEDIATELY' ? this.data.orderInfo.addrPkey : this.data.addressPkey,
            supplier: item.supplier
        }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data,
            header: {
                'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
            },
            success: (res) => {
                console.log(res);
                console.log(res.data.result);
                this.getTimeList(res.data.result)
            },
            fail() {

            }
        })
    },
    getTimeList({
        imPsTime,
        psTime,
        psOption,
        type
    }) {
        // console.log(res);
        let timeItems = [],
            todayId = 0,
            tomorrowId = 0,
            acquiredId = 0
        const today = utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D')
        const week = utils.getWeekDate(today, '周', )
        const tomorrow = utils.formatTimeInArr((new Date().getTime() + 86400000) / 1000, 'Y-M-D')
        const tomorrowWeek = utils.getWeekDate(tomorrow, '周', )
        const acquired = utils.formatTimeInArr((new Date().getTime() + 86400000 * 2) / 1000, 'Y-M-D')
        const acquiredWeek = utils.getWeekDate(acquired, '周', )
        console.log('today', today, week, tomorrowWeek);
        let todayList = {
                text: `今天(${week})`,
                children: []
            },
            tomorrowList = {
                text: `明天(${tomorrowWeek})`,
                children: []
            },
            acquiredList = {
                text: `后天(${acquiredWeek})`,
                children: []
            }
        if (type === 'ORDERED' && imPsTime) {
            todayList.children.push({
                text: `尽快送达，预计${imPsTime.slice(11)}送达`,
                original: imPsTime,
                type: 'IMMEDIATELY',
                id: todayId++
            })
        }
        if (psOption.length > 0) {
            psOption.forEach(item => {
                if (item.indexOf(today) > -1) {
                    todayList.children.push({
                        text: item.split(' ')[1],
                        original: item,
                        id: todayId++
                    })
                } else if (item.indexOf(tomorrow) > -1) {
                    tomorrowList.children.push({
                        text: item.split(' ')[1],
                        original: item,
                        id: tomorrowId++,
                        isTomorrow: true,
                        tomorrow: tomorrowWeek,
                    })
                } else if (item.indexOf(acquired) > -1) {
                    acquiredList.children.push({
                        text: item.split(' ')[1],
                        original: item,
                        id: acquiredId++,
                        isTomorrow: true,
                        tomorrow: acquiredWeek,
                    })
                }
            })
        }
        // timeItems.push(todayList)
        if (todayList.children && todayList.children.length) {
            timeItems.push(todayList)
        }
        if (tomorrowList.children && tomorrowList.children.length) {
            timeItems.push(tomorrowList)
        }
        if (acquiredList.children && acquiredList.children.length) {
            timeItems.push(acquiredList)
        }
        console.log({
            timeItems
        });
        this.setData({
            timeItems,
            mainActiveIndex: 0,
            activeId: -1,
            isReservation: psTime,
        })
    },
    onClickNav({
        detail = {}
    }) {
        this.setData({
            mainActiveIndex: detail.index || 0,
        });
    },
    /**
     * 选择时间选项
     */
    onClickItem({
        detail = {}
    }) {
        console.log("detail", detail);
        const activeId = detail.id;
        const {
            selectInfo: item,
            selectInfoIndex: index,
            orderInfo
        } = this.data
        console.log(item, index);
        if (detail.type === 'IMMEDIATELY') {
            item.distributionType = 'IMMEDIATELY'
            item.pstime = detail.original
            orderInfo.infos[index] = item
            this.setData({
                orderInfo,
                selectInfo: null,
                selectInfoIndex: null
            })
        } else if (item.distributionType === 'PICKUP') {
            item.pstime = detail.original
            orderInfo.infos[index] = item
            this.setData({
                orderInfo,
                selectInfo: null,
                selectInfoIndex: null
            })
        } else {
            item.distributionType = 'ORDERED'
            item.pstime = detail.original
            orderInfo.infos[index] = item
            this.setData({
                orderInfo,
                selectInfo: null,
                selectInfoIndex: null
            })
        }
        this.setData({
            activeId,
            timeShow: false,
        });
    },
    /**
     * @desc 切换自提地点
     */
    goPickupAddr(event) {
        console.log('goPickupAddr', event);
        let index = event.currentTarget.dataset.index
        wx.setStorageSync('orderInfo', JSON.stringify(this.data.orderInfo));
        wx.navigateTo({
            url: '/pages/pay/pickupAddr/index?index=' + index,
        })
    },
    /**
     * 输入备注
     */
    filedChange(event) {
        console.log(event);
        const orderInfo = this.data.orderInfo
        const index = event.currentTarget.dataset.index
        const value = event.detail
        orderInfo.infos[index].remark = value
        this.setData({
            orderInfo
        })
    },

    /**
     * @desc 获取可选优惠券
     */
    goCard(event) {
        const index = event.currentTarget.dataset.index
        wx.setStorageSync('orderInfo', JSON.stringify(this.data. orderInfo.infos[index]));
        wx.navigateTo({
            url: '/pages/pay/card/index?paytype=comboPay&index=' + index + '&shareFarm=' + JSON.stringify(this.data.shareFarm)
        })
    },

    /**
     * 支付方式选择改变
     */
    paytypeChange: function (event) {
        console.log(event)
        let { name, amt = 0 } = event.currentTarget.dataset;
  
        // 如果点击的选项已经是选中状态pay，则取消选中（置为 null）
      const  payType1 = this.data.orderInfo.payType === name ? null : name;
      const payType2 = payType1 !== 'NM_MEMBER' || !payType1  ?  'ORDER_WEIXIN'  : null;
      const orderAmt = this.data.orderInfo.goodsSumAmtn;
      let payAmt = 0;
      if(payType1 === null) {
        payAmt =  orderAmt;
        amt = 0;
      } else {
        payAmt = orderAmt > amt ? orderAmt - amt : 0;
      }
      this.setData({
        ["orderInfo.payType"]: payType1,
        payType: payType2,
        payAmt,
        payTypeAmt:  amt
      })
    },

    /**
     * @desc 提交订单
     */
    onSubmit() {
        const that = this
        let orderInfo = this.data.orderInfo;
        console.log(orderInfo);
        /**数据验证 */
        if (!orderInfo.addr) {
            let allPICKUP = true
            orderInfo.infos.map(item => {
                if(item.distributionType == 'IMMEDIATELY') {
                    allPICKUP = false
                }
            })
            if(!allPICKUP) {
                wx.showToast({
                    title: '请选择收货地址',
                    icon: 'none'
                });
                return;
            }
        }
        for (let i = 0; i < orderInfo.infos.length; i++) {
            let info = orderInfo.infos[i]
            if ((info.farmer && info.farmerName) && (info.distributionType === 'IMMEDIATELY' || info.distributionType === 'ORDERED') && !info.pstime) {
                wx.showToast({
                    title: '请选择配送时间',
                    icon: 'none'

                });
                return
            }
            if (info.distributionType === 'PICKUP' && !info.pstime) {
                wx.showToast({
                    title: '请选择自提时间',
                    icon: 'none'

                });
                return
            }
        }
        /**结构拆分，把商品拆分到规格 */
        orderInfo.infos.forEach(info => {
            let goodsList = []
            info.goodsList.forEach(item => {
                item.spaceList.forEach(space => {
                    goodsList.push({
                        ...item,
                        ...space
                    })
                })
            })
            info.goodsList = goodsList
        })

        console.log(orderInfo);
        this.setData({
            loading: true,
            disabled: true
        });
        // 如果orderInfo.payType 订单支付类型 为null，则传"ORDER_WEIXIN"
        if(!orderInfo.payType) {
          orderInfo.payType = "ORDER_WEIXIN";
        }
        /**提交请求 */
        let url = this.data.mtype == 'JD_GOODS' ? "/v1/app/jd/order/commitOrder" : "/v3/app/market/lm/order/commitOrder";
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: orderInfo,
            header: {
                'content-type': 'application/json;charset=UTF-8',
                "openid": app.globalData.openid,
                "farmer": this.data.shareFarm != "" ? this.data.shareFarm.pkey : app.globalData.location.pkey
            },
            success: res => {
                if (res.data.code == "999") {
                    that.setData({
                        iShidden: false
                    })
                    return;
                }
                if (res.data.success) {
                    wx.setStorageSync('allCheckList', []);
                    wx.setStorageSync('marketResult', []);
                    wx.setStorageSync('mallResult', []);
                    var result = res.data.result;
                    that.setData({
                        ["orderInfo.pkey"]: result.orderPkey,
                    })
                    console.log(result);
                    // 组合支付如果其他支付余额足额支付，后端返回时将payType类型改成非组合支付
                    if (result.payType == "ORDER_WEIXIN" || result.payType == "MSD_COMBINATION" ||  result.payType == "ELECTRONIC_ACCOUNT_COMBINATION") {
                        wx.requestPayment({
                            'timeStamp': result.wxPayData.timeStamp,
                            'nonceStr': result.wxPayData.nonceStr,
                            'package': result.wxPayData.pack,
                            'signType': result.wxPayData.signType,
                            'paySign': result.wxPayData.paySign,
                            'success': function (res) {
                                var pkey = result.orderPkey;
                                that.subscribeToMessages();
                                wx.redirectTo({
                                    url: `/pages/pay/result/index?pkey=${pkey}&result=success&type=${that.data.orderInfo.orderType}&orderNum=${that.data.orderInfo.infos.length}`,
                                })
                            },
                            'fail': function (res) {
                                var pkey = result.orderPkey;
                                that.subscribeToMessages();
                                wx.redirectTo({
                                    url: `/pages/pay/result/index?pkey=${pkey}&result=fail&type=${that.data.orderInfo.orderType}&orderNum=${that.data.orderInfo.infos.length}`,
                                })
                            },
                            'complete': function (res) {
                                app.getBuycarNum()
                            }
                        })
                    } else {
                        var pkey = result.orderPkey;

                        app.getBuycarNum()
                        that.subscribeToMessages();
                        wx.redirectTo({
                            url: `/pages/pay/result/index?pkey=${pkey}&result=success&type=${that.data.orderInfo.orderType}&orderNum=${that.data.orderInfo.infos.length}`,
                        });
                    }
                } else {
                    wx.showToast({
                        title: res.data.msg || '',
                        icon: 'none'
                    });

                }
                that.setData({
                    loading: false,
                    disabled: false
                })
            }
        })
    },

    subscribeToMessages() {
        wx.requestSubscribeMessage({
            tmplIds: ['pVIkpgKki0HOGcKWyvmvk8aSOceWpv4Jj0BsTsy-1Eo'],
            success: (res) => {
                //成功回调  
                console.log(1112)
            },
            fail: (res) => {
                console.log("2222 res", res)
            }
        });

    },
})