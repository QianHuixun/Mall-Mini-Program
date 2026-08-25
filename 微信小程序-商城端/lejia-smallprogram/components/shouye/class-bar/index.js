// components/shouye/class-bar/index.js
const app = getApp();
import http from '../../../utils/http'
Component({
    pageLifetimes: {
        // 组件所在页面的生命周期函数
        show() {
            this.getData()
        },
    },

    /**
     * 组件的属性列表
     */
    properties: {
        hasThirdLine: true
    },

    /**
     * 组件的初始数据
     */
    data: {
        topList: [],
        bottomList: [],
    },

    /**
     * 组件的方法列表
     */
    methods: {
        getData() {
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v2/app/market/goods/gtype/list',
                data: {
                    showMarket: true,
                    showPoint: false
                },
                success: (res) => {
                    const result = res.data.result
                    console.log('gtype', result);
                    const arr = result.splice(0, 10)
                    console.log('gtype', result, arr );
                    this.setData({
                        topList: arr,
                        bottomList: result,
                    })
                }
            })
        },
        goClassification(data) {
            console.log(data);
            wx.setStorageSync('topClassify', data.currentTarget.dataset.pkey)
            wx.switchTab({
                url: '/pages/home/classification/index',
            })
        }
    }
})
