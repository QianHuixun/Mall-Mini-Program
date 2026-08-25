// components/load-more/index.js
Component({
  // 组件的属性列表
  properties: {
    // 判断是否还有更多数据
    hasMore: {
      type: Boolean,
      value: false
    },
    // 加载中的显示文本
    loadingText: {
      type: String,
      value: '加载中...'
    },
    // 加载失败的显示文本
    failText: {
      type: String,
      value: '加载失败, 请点击重试!'
    },
    // 没有更多后的显示文本, 默认没有则隐藏加载更多控件
    finishText: {
      type: String,
      value: '-- 暂无其他数据 --'
    },
    // 列表渲染延时, 默认为 500 ms, 我在开发工具中测试列表渲染速度时快时慢, 可根据实际使用中界面复杂度自行调整
    // ps 如果能监听setData() 渲染结束的话则可以不需要延时 
    listRenderingDelay: {
      type: Number,
      value: 500
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showThis: false,
    text: '加载中...',
    isLoading: false,
    timer:''
  },

  /**
   * 组件的方法列表
   */
  methods: {

    //加载更多的入口方法, 直接在page中使用时请在onReachBottom() 方法中调用这个方法, 并实现loadMoreListener方法去获取数据
    loadMore: function () {
      // console.log("加载更多", this.properties.hasMore)
      // console.log('properties', this.properties)
      if (!this.properties.hasMore) {
        // console.log('load more finish')
        return
      }
      if (this.data.isLoading) {
        // console.log('loading ...')
        this.setData({
          timer: setInterval(() => {
            this.triggerEvent('loadMoreListener')
            clearInterval(this.data.timer)
          }, 1000)
        })
        return
      }
      this.setData({
        isLoading: true
      })
      this.triggerEvent('loadMoreListener')
    },
    //加载完成, 传入hasMore 
    loadMoreComplete: function (data) {
      // console.log("load-more", data);
      var hasMore = data.result.curPage < data.result.totalPages && data.result.totalPages != 1;
      var text = '',
        showThis = false;
      // console.log("hasMore", hasMore)
      // console.log(hasMore )
      if (hasMore) {
        showThis = true
        text = this.properties.loadingText
      } else if (this.properties.finishText.length > 0) {
        text = this.properties.finishText
        showThis = true
      }
      this.setData({
        hasMore: hasMore,
        text: text,
        showThis: showThis
      })
      // console.log("showThis", showThis)
      //界面渲染延迟, 避免列表还未渲染完成就再次触发 loadMore 方法
      setTimeout(function () {
        this.setData({
          isLoading: false,
        })

      }.bind(this), this.properties.listRenderingDelay)
    },
    // 加载失败
    loadMoreFail: function () {
      this.setData({
        text: this.properties.failText
      })

      //界面渲染延迟, 避免列表还未渲染完成就再次触发 loadMore 方法
      setTimeout(function () {
        this.setData({
          isLoading: false
        })
      }.bind(this), this.properties.listRenderingDelay)
    },
    //点击 loadmore 控件时触发, 只有加载失败时才会进入页面回调方法
    clickLoadMore: function () {
      if (this.data.text != this.properties.failText) return
      this.setData({
        text: this.properties.loadingText,
        isLoading: true
      })
      this.triggerEvent('clickLoadMore')
    }
  }
})