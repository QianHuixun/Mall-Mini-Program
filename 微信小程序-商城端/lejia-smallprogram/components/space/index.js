// components/space/index.js
let app = getApp();
import http from '../../utils/http'
// var goCartView;
Component({
  options: {
    addGlobalClass: true,
    styleIsolation: 'shared'
  },
  /**
   * 组件的属性列表
   */
  properties: {
    //是否显示 弹出层
    isShow: {
      type: Boolean, //定义类型
      value: false, //定义默认值
    },
    isIcon: {
      type: Boolean,
      value: false
    },
    iShidden: {
      type: Boolean,
      value: true
    },
    isbindAddCart: {
      type: Boolean,
      value: true
    },
    isVip: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    title: "商品名称", // 商品名称
    price: 0, //商品价格
    spacePrice: 0, //规格价格
    processPrice: 0,//加工价格
    gwcNum: 0, //在购物车里的数量
    kcNum: 0, //库存数量
    goodsPkey: "", //商品pkey
    spaceList: [], //规格列表
    processLines: [],// 加工服务列表
    spacePkey: '',
    // iShidden: true,
    isAuto: true,
    disabled: false,
    loading: false,
    //购物车x坐标
    animationx: 0,
    //购物车y坐标
    animationy: 0,
    //是否显示飞行物，默认不显示
    showdot: false,
    //动画对象
    ani: {},
    type: 'market', //添加商品的类型 默认为市场商品
    ProcessRadio: "",// 加工服务选中项
  },
  attached() {
    // goCartView = this.selectComponent("#goCartView");
  },
  /**
   * 组件的方法列表
   */
  methods: {
    // 加工服务 选择事件
    handleProcessChecked(data){
      
      if(data.currentTarget.dataset.process  == this.data.ProcessRadio) {
        this.setData({
          ProcessRadio: ""
        })
        this.setData({
          price: this.data.spacePrice
        })
        return;
      } 
      this.setData({
        ProcessRadio:  data.currentTarget.dataset.process,
        processPrice: data.currentTarget.dataset.price
      });
      this.setData({
        price: this.data.processPrice + this.data.spacePrice
      })
    },
    onClickHide() {
      this.setData({
        isShow: false
      });
    },
    /**
     * 规格选中事件
     */
    handleChecked: function (data) {
      if( data.currentTarget.dataset.kcnum <1 ) return;
      this.setData({
        spacePkey: data.currentTarget.dataset.pkey,
        spacePrice: data.currentTarget.dataset.price,
        gwcNum: data.currentTarget.dataset.num,
        kcNum: data.currentTarget.dataset.kcnum
      });
      this.setData({
        price: this.data.spacePrice + this.data.processPrice
      });
    },
    /**
     * 获取数据
     */
    getData: function (pkey) {
      this.setData({
        ProcessRadio: "",// 加工服务选中项
      })
      var url =this.data.isVip?"/v1/app/market/goods/space/get/member": "/v1/app/market/goods/space/get",
        that = this;
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: pkey
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: function (res) {
          let spacePkey = '',kcNum = 0;
          res.data.result.spaceList.forEach(item => {
            if(item.kcNum>0 && spacePkey=='') {       
                spacePkey = item.pkey; 
                kcNum = item.kcNum;
            }
          })
          that.setData({
            title: res.data.result.goodsTitle,
            goodsPkey: res.data.result.goods,
            price: res.data.result.spaceList[0].price,
            spacePrice: res.data.result.spaceList[0].price,
            spacePkey: spacePkey,
            spaceList: res.data.result.spaceList,
            gwcNum: res.data.result.spaceList[0].gwcNum,
            kcNum: kcNum,
            type: 'market',
            processLines: res.data.result.processLines
          });
          // goCartView.getPosition();
        }
      })
    },
    /**
     *@desc 改变商品数量
     */
    stepChange(data) {
      var that = this,
        url = "/v1/app/market/lm/member/gwc/less/goods/num", //减少购物车里单个商品的数量
        url_add = "/v1/app/market/lm/member/gwc/add/goods/num", //增加购物车里单个商品的数量
        newValue = data.detail; //修改后的值

      if (!this.data.gwcNum) {
        this.handleAddTOCart();
        return
      } else if (this.data.gwcNum < newValue) {
        url = url_add;
      }

      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          goodsPkey: that.data.goodsPkey,
          space: that.data.spacePkey,
          goodsNum: 1,
          association: that.data.ProcessRadio
        },
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {
          if (res.data.success) {
            that.setData({
              gwcNum: newValue
            });
            let num = newValue;
            let spaceList = that.data.spaceList;
            spaceList.map((item) => {
              if (item.pkey != that.data.spacePkey) {
                num = num + item.gwcNum;
              } else {
                item.gwcNum = newValue
              }
              return item
            })
            that.setData({
              spaceList: spaceList
            });
            that.triggerEvent('refresh', {
              goodsPkey: that.data.goodsPkey,
              num: num
            })
            app.getBuycarNum();
          } else {
            that.setData({
              gwcNum: newValue - 1
            })
            wx.showToast({
              title: res.data.msg || '',
              icon: "none"
            });
          }
        }
      });
    },
    /**
     * 添加到购物车
     */
    handleAddTOCart() {
      if(this.data.spacePkey == '') {
        wx.showToast({
          title: '库存不足',
          icon: 'none'
        });
        return;
      }
      var url = "/v1/app/market/lm/member/gwc/ins",
        that = this;
      that.setData({
        loading: true,
        disabled: true
      });
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          goodsPkey: that.data.goodsPkey,
          goodsNum: 1,
          space: that.data.spacePkey,
          association: that.data.ProcessRadio
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: function (res) {

          if (res.data.code == "999") {
            that.setData({
              iShidden: false
            })
            return;
          };
          if (res.data.success) {
            //飞入购物车动画效果
            // goCartView.addshopcar(data);

            let spaceList = that.data.spaceList,
              num = 0;
            spaceList.map((item) => {
            
              if (item.pkey == that.data.spacePkey) {
                item.gwcNum = item.gwcNum + 1;
                that.setData({
                  gwcNum: item.gwcNum
                });
              }
              num = num + item.gwcNum;
              return item
            })
            that.setData({
              spaceList: spaceList
            });

            that.triggerEvent('refresh', {
              goodsPkey: that.data.goodsPkey,
              num: num
            })
            that.setData({
              loading: false,
              disabled: false,
            })
            app.getBuycarNum();
          } else {
            wx.showToast({
              title: res.data.msg || '',
              icon: "none"
            });
            that.setData({
              loading: false,
              disabled: false,
            })
          }
        }
      });
    },

    //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
    //函数里面执行
    onLoadFun: function () {

    }
  }
})