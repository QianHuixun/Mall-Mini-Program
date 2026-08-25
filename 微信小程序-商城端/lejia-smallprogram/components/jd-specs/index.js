// components/jd-specs/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        show: false,
        specs: null,
        skuList: null,
        selectedSpecs: null,
        flexibleColumns: null,
    },

    /**
     * 
     */
    observers: {
        show: function(newValue) {
            console.log('observers: show', newValue);
            if(newValue) {
                console.log(this.properties);
                const { selectedSpecs } = this.properties
                if(selectedSpecs.lowestBuy && (this.data.count <= selectedSpecs.lowestBuy)) {
                    this.setData({
                        count: selectedSpecs.lowestBuy
                    })
                } else {
                    this.setData({
                        count: selectedSpecs.lowestBuy || 1
                    })
                }
            }
        }
    },

    /**
     * 组件的初始数据
     */
    data: {
        count: 1,
    },

    /**
     * 组件的方法列表
     */
    methods: {
        /**
         * 关闭规格弹窗
         */
        handleClose() {
            this.triggerEvent('close')
        },
        onChange(data) {
            console.log(data);
            let count = data.detail
            if(this.properties.selectedSpecs.lowestBuy && (count < this.properties.selectedSpecs.lowestBuy)) {
                count = this.properties.selectedSpecs.lowestBuy
            }
            console.log(count);
            this.setData({
                count: count
            })
        },
        /**
         * 切换商品规格
         */
        handleSpecsTap(data) {
            const specs = data.currentTarget.dataset.specs
            this.triggerEvent('change', specs)
        },
        handleConfirm() {
            console.log(this.data.count);
            this.triggerEvent('confirm', this.data.count)
        },
    }
})
