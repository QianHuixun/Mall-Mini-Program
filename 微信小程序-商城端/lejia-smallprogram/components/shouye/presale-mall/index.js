// components/shouye/presale-mall/index.js
import http from '../../../utils/http'
const app = getApp();
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        displayName: {
            type: String,
            value: '预售专区',
        },
        time: {
            type: Number,
            value: 0,
        },
        list: {
            type: Array,
            value: [],
        },
        full: {
            type: Boolean,
            value: false,
        }
    },

    /**
     * 组件的初始数据
     */
    data: {
        chunks: [],
        displayChunks: [],
        index: 0,
        interval: 3000,
        times: null,
    },

    /**
     * 属性值变化时的回调函数
     */
    observers: {
        list(newValue) {
            clearInterval(this.data.times)
            if (!newValue || !newValue.length) return
            let chunkSize = this.properties.full ? 4 : 2
            const chunks = Array.from({
                    length: Math.ceil(newValue.length / chunkSize)
                }, (v, i) =>
                newValue.slice(i * chunkSize, i * chunkSize + chunkSize)
            );
            console.log('chunks', chunks);
            this.setData({
                chunks,
            })
            this.startCircularQueue()
        }
    },

    /**
     * 组件的方法列表
     */
    methods: {
        //倒计时
        onChange(e) {
            this.setData({
                timeData: e.detail,
            });
        },
        startCircularQueue() {
            let {
                chunks,
                index,
                times,
                interval
            } = this.data
            this.setData({
                displayChunks: chunks[index]
            })
            clearInterval(times)
            times = setInterval(() => {
                index++
                if (index >= chunks.length) index = 0
                this.setData({
                    displayChunks: chunks[index]
                })
            }, interval);
            this.setData({
                times
            })
        },
        goPresell(event) {
            const pkey = event.currentTarget.dataset.pkey
            const displayName = this.properties.displayName
            wx.navigateTo({
                url: `/pages/shouyeGroup/openBook/index?pkey=${pkey}&displayName=${displayName}`,
            });
        }
    }
})