// components/search-bar/index.js
const app = getApp();
import http from '../../utils/http'
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        type: {
            Type: String,
            value: '',
        },
        border: {
            type: Boolean,
            value: false,
        },
        mType: {
            Type: String,
            value: ''
        }
    },

    pageLifetimes: {
        // 组件所在页面的生命周期函数
        show() {
            console.log('type', this.properties.type);
            if(!this.properties.type) return
            this.getData()
        },
    },

    /**
     * 组件的初始数据
     */
    data: {
        currentIndex: 0,
        list: []
    },

    /**
     * 组件的方法列表
     */
    methods: {
        getData() {
            http.request({
                method: "POST",
                url: app.globalData.ajax_url + '/v1/app/market/search/keyword/list',
                data: {module: this.properties.type},
                success: (res) => {
                    if(res.data.success) {
                        this.setData({
                            list: res.data.result
                        })
                    }
                }
            })
        },
        handleChange(e) {
            this.setData({
                currentIndex: e.detail.current
            })
        },
        searfocus() {
            console.log(this.data.currentIndex);
            const {
                currentIndex,
                list
            } = this.data
            const name = list[currentIndex]
            wx.navigateTo({
                url: `/pages/shouyeGroup/search/index?name=${name}&mType=${this.properties.mType}`,
            });
        }
    }
})