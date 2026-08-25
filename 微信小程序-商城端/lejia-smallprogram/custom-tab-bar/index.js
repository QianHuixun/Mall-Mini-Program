// custom-tab-bar/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {

    },

    /**
     * 组件的初始数据
     */
    data: {
        active: -1,
        list: [{
                value: 0,
                show: true,
                label: '首页',
                icon: 'shouye',
                url: '/pages/home/shouye/index'
            },
            {
                value: 1,
                show: true,
                label: '分类',
                icon: 'fenlei',
                url: '/pages/home/classification/index'
            },
            {
                value: 2,
                show: true,
                label: '会员福利',
                icon: 'memberBenefits',
                url: '/pages/home/memberBenefits/index'
            },
            {
                value: 3,
                show: true,
                label: '购物车',
                icon: 'car',
                url: '/pages/home/buyCar/index'
            },
            {
                value: 4,
                show: true,
                label: '我的',
                icon: 'my',
                url: '/pages/home/my/index'
            },
        ],

    },

    /**
     * 组件的方法列表
     */
    methods: {
        init() {
            console.log('init');
            const page = getCurrentPages().pop();
            console.log(page);
            let urls = this.data.list.map(v => v.url);
            let active = urls.findIndex(v => v === `/${page.route}`);
            console.log('active', active)
            this.setData({
                active
            });
        },
        onChange(e) {
            const value = e.currentTarget.dataset.value
            if (value == this.data.active) return
            const item = this.data.list.find(_item => {
                return _item.value == value
            })
            wx.switchTab({
                url: item.url
            })
        }
    }
})