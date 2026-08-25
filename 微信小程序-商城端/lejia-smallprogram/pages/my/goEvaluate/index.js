// pages/my/goEvaluate/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      orderPkey: "",
      loading: false,
      disabled: false,
      goodsList: [], 
      storeText:{
        0: "",
        1: "很差",
        2: "差",
        3: "一般",
        4: "满意",
        5: "很满意",
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      if(options.q) {
        const q = decodeURIComponent(options.q);
        const pkey = utils.getQueryString(q, 'pkey');
          this.setData({
            orderPkey:pkey
          });     
      }
      
      if(options.pkey) {
        this.setData({
          orderPkey:options.pkey
        });   
      }
      let goodsList = JSON.parse(wx.getStorageSync('orderInfoGoods'));
      console.log("goodsList",goodsList);
      goodsList = goodsList.filter(item => item.wholeRefund ==false).map(item => {
        return {
          goods: item.goods,
          goodsName: item.goodsName,
          goodsPhoto:  item.photo,
          score: 0,
          content: "",
          photo: [] 
        }
      });
      console.log("goodsList",goodsList)
      this.setData({
        goodsList: goodsList
      })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },
    onScoreChange(event) {
      console.log(event)
      const index = event.currentTarget.dataset.index;
      this.setData({
        ['goodsList['+index+'].score']: event.detail,
      });
    },
    onContentChange(event) {
      const index = event.currentTarget.dataset.index;
      this.setData({
        ['goodsList['+index+'].content']: event.detail,
      });
    },
    // 图片上传
    afterRead(event) {
      console.log(111)
      const goodsListIndex = event.currentTarget.dataset.index;
      const { file } = event.detail;
      const that = this;
      const url = "/v1/app/market/lm/member/uploadImage";
      this.setData({
        loading: true,
        disabled: true,
      });
      console.log(app.globalData.ajax_url + url,file)
      wx.uploadFile({
        filePath: file.url,
        name: 'file',
        url: app.globalData.ajax_url + url,
        header: {
          "Content-Type": 'application/xml',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey,
          "ascription":app.globalData.ascription
        },
        success: function (res) {
          console.log("res",res)
          const { goodsList = [] } = that.data;
          goodsList[goodsListIndex].photo.push({ ...file, url: JSON.parse(res.data).result.url });
          that.setData({ goodsList });
          that.setData({
            loading: false,
            disabled: false,
          });
        },
        fail(err) {
            wx.showToast({
                title: '图片上传失败',
                icon: 'none',
                duration: 2000
            });
            that.setData({
              loading: false,
              disabled: false,
            });
        }
    })
    },
    uploaderDelete(event){
      // console.log(event,"event")
      const goodsListIndex = event.currentTarget.dataset.index;
      const index = event.detail.index;
      console.log(index);
      let photo2 = this.data.goodsList[goodsListIndex].photo;
      photo2.splice(index, 1);
      
      this.setData({
        ['goodsList['+goodsListIndex+'].photo']: photo2
      })
    },
    // 一键好评
    handleAllGood() {
      const goodsList=this.data.goodsList.map(item=> {
        item.score = 5;
        item.content = "好评";
        return item
      }) ;
      this.setData({
        goodsList: goodsList
      })
    },
    // 提交评价
    handleSubmit() {
      let status = true;
      const that = this;
      console.log(this.data.goodsList)
      this.data.goodsList =  this.data.goodsList.map(item=> {
        if(item.score == 0){
          status = false;
        }
        if(item.photo.length) {
          item.photo = item.photo.map(subitem=> {
            return subitem.url;
          })
        }
        
        return item;
      });
      if(!status) {
        wx.showToast({
          title: '请为商品打分',
          icon: "none"
        });
        return;
      }
      this.setData({
        loading: true,
        disabled: true,
      });
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + "/v1/app/market/lm/order/comment/add",
        data: {
          orderPkey: this.data.orderPkey,
          lines: this.data.goodsList,
        },
        header: {
          "Content-Type": "application/json",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {
          that.setData({
            loading: false,
            disabled: false,
          });
          if(!res.data.success) {
            wx.showToast({
              title: res.data.msg,
              icon: "none"
            });
            return;
          }
          
          wx.showToast({
            title: '评价成功',
            icon: "none"
          });
          setTimeout(()=> {
            wx.navigateBack({
              delta: 1, // 返回层数（默认1）
              success: () => {
                const pages = getCurrentPages();
                const prevPage = pages[pages.length - 2]; // 获取上一页实例
                prevPage.getData(); // 调用上一页的刷新方法
              }
            });
          },3000);
          
        }
        });
    },
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})