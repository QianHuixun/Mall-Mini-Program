// pages/shouyeGroup/marketDetail/index.js
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        market: null,
        background: null,
        indicatorDots: true,
        vertical: false,
        autoplay: false,
        interval: 2000,
        duration: 500,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const market = wx.getStorageSync('marketDetail')
        const background = []
        for (let key in market) {
            if (key.includes('photo') && market[key]) {
                background.push(market[key])
            }
        }
        console.log(background);
        this.setData({
            market,
            background,
        })
    },
    callPhone() {
        const mobile = this.data.market.mobile
        wx.makePhoneCall({
            phoneNumber: mobile,
        })
    },
    handleCustomer() {
        const {
            customerServiceId,
            customerServiceLink
        } = this.data.market.config
        console.log(customerServiceId, customerServiceLink);
        wx.openCustomerServiceChat({
            extInfo: {
                url: customerServiceLink
            },
            corpId: customerServiceId,
            success(res) {
                /* 处理成功 */
                console.log("handleCustomer2")
            },
            fail: (err) => {
                console.error('失败', err)
            }
        });
    },
    openLocation() {
        const {latitude, longitude, addr} = this.data.market.config
        wx.openLocation({ //​使用微信内置地图查看位置。
            latitude, //要去的纬度-地址
            longitude, //要去的经度-地址
            name: addr,
            address: addr,
        });
    }
})