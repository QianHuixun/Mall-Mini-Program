// components/shouye/msd-mall/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        displayName: {
            type: String,
            value: '',
        },
        time: {
            type: Number,
            value: 0,
        },
        list: {
            type: Array,
            value: [],
        }
    },

    /**
     * 组件的初始数据
     */
    data: {
        goodsList: [],
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
            let chunkSize = 4
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
            const {gtype, pkey} = event.currentTarget.dataset
            const displayName = this.properties.displayName
            wx.navigateTo({
                url: `/pages/home/msd/index?gtype=${gtype}&pkey=${pkey}&displayName=${displayName}`,
            });
        }
    }
})
