// components/goCart/index.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    isIcon: {
      type: Boolean,
      value: false //true为显示购物车图标，false不无购物车图标
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    //购物车x坐标
    animationx: 0,
    //购物车y坐标
    animationy: 0,
    //是否显示飞行物，默认不显示
    showdot: false,
    //动画对象
    ani: {}
  },
  /**
   * 组件的方法列表
   */
  methods: {
   /**
     * 动画获取坐标
     */
    getPosition: function () {
      // console.log("isIcon", this.properties.isIcon)
    var that = this;
    //页面渲染完后获取购物车在页面中的坐标
    const query = wx.createSelectorQuery().in(this)

    query.select('#shopcar').boundingClientRect(function (res) {
      // console.log("res", res)
    })
    query.selectViewport().scrollOffset()
    query.exec(function (res) {
      let point = res[0]
      //坐标修正，让飞行物可以正好落在购物车正中心，20是飞行物宽度一半然后转化成px
      var xtemp = (point.left + point.right) / 2 - 20 / 750 * wx.getSystemInfoSync().windowWidth
      var ytemp = (point.top + point.bottom) / 2 - 20 / 750 * wx.getSystemInfoSync().windowWidth
      // console.log('xtemp : ' + xtemp + ' ytemp : ' + ytemp)
      that.setData({
        //获取修正后坐标
        animationx: xtemp,
        animationy: ytemp
      })
    });
    },
    /**
     * 加入购物车动画
     */
    addshopcar(e) {
      let that = this;
      if (that.data.showdot == true) {
        return
      }
      //获取点击点坐标
      var touches = e.touches[0]
      //坐标修正，同上，这么做是为了让飞行点落到点击的中心
      let toptemp = touches.clientY - 20 / 750 * wx.getSystemInfoSync().windowWidth
      let lefttemp = touches.clientX - 20 / 750 * wx.getSystemInfoSync().windowWidth
      // console.log('toptemp : ' + toptemp + ' lefttemp : ' + lefttemp)
      var animation1 = wx.createAnimation({
        duration: 0,
        timingFunction: 'ease'
      })
      //通过极短的时间让飞行点移动到手指点击位置，同时让飞行点显示出来
      animation1.left(lefttemp).top(toptemp).step()
      that.setData({
        showdot: true,
        ani: animation1.export()
      })
      // return;
      //然后让飞行点飞行到购物车坐标处，形成添加效果
      setTimeout(function () {
        const animation = wx.createAnimation({
          duration: 500,
          timingFunction: 'ease'
        })
        //通过Animation的left和top这两个API，将飞行点移动到购物车坐标处
        animation.left(that.data.animationx).top(that.data.animationy).step()
        that.setData({
          ani: animation.export()
        })
        setTimeout(function () {
          that.setData({
            showdot: false,
            ani: null
   
          })
        }.bind(this), 1560)//这里也是要稍微延后，后隐藏飞行点，然后清除动画，增加购物计数器
      }, 5)//注意这里要稍微延后，保证前面移动到手指点击处的动画完成
    },
  }
})
