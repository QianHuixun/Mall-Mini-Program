// components/login/index.js
import http from '../../utils/http'
const app = getApp();
let count = 1
Component({
  //组件的对外属性 说的确实很官方，用过vue组件的就很容易理解这点
  //父级向子级传值这里就是接收值得地方
  properties: {
    //名称要和父级绑定的名称相同
    //这里主要是控制自动授权弹框是否显示 true=隐藏 false=显示
    iShidden: {
      type: Boolean, //定义类型
      value: true, //定义默认值
      observer(newVal, oldVal) {
        if (!newVal && count == 1) {
          count++
          // this.setAuthStatus();
        }
      }
    },
    //是否自动登录 这里主要用于没有授权是否自动弹出授权提示框 
    //**用在不自动登录页面但是某些操作需要授权登录**
    isAuto: {
      type: Boolean,
      value: true,
    },
  },
  //组件的内部数据，和 properties 一同用于组件的模板渲染
  data: {
    cloneIner: null,
    userInfo: {
      openid: "",
      avatarUrl: "",
      city: "",
      country: "",
      gender: "",
      language: "",
      nickName: "",
      province: "",
    },
    AppInfo: {},
    checked: false,
    show: false,
  },
  //组件所在页面的生命周期声明对象
  pageLifetimes: {
    //页面隐藏
    hide: function () {
      //关闭页面时销毁定时器
      if (this.data.cloneIner) clearInterval(this.data.clearInterval);
    },
    //页面打开
    show: function () {
      //打开页面销毁定时器
      if (this.data.cloneIner) clearInterval(this.data.clearInterval);
      setTimeout(() => {
        this.setData({
          AppInfo: app.globalData.AppInfo
        })
        console.log('AppInfo', this.data.AppInfo);
      }, 100)
    },
  },
  //组件生命周期函数，在组件实例进入页面节点树时执行
  attached() {

  },
  //组件的方法 
  methods: {
    //打开注意事项
    openNote(e){
      // const that = this
      this.setData({checked: true})
      // return
    },
    // 同意用户协议
    onConfirm() {
      this.setData({checked:true})
    },
    // 不同意用户协议
    onCancel() {
      this.setData({checked:false})
    },
    routerTo(e) {
      console.log(e);
      const url = e.target.dataset.url
      wx.navigateTo({url})
    },
    showTips(){
      if(!this.data.checked) {
        wx.showToast({
          title: `请阅读协议后并勾选`,
          icon:'none',
          duration:3000
        })
        return
      }
    },
    //获取手机号码
    getPhoneNumber(e) {
      // var params = {},
      const  that = this;
      // var params = this.data.userinfo,
      
      wx.login({
        success: res => {
          console.log("code",res.code)
          if (!res.code) return wx.showToast({
            title: '登录失败',
          });
          // 获取到用户的 code 之后：res.code
          // console.log("用户的code:" + res.code);
          //获取在setUserInfo方法中获取的用户信息并赋值给params 变量
          var params = {};
          params["wxcode"] = res.code;
          params["sign"] = "USER";
          params["ascription"] = app.globalData.ascription;
          wx.request({
            method: "GET",
            url: app.globalData.ajax_url + "/v1/wx/getOpenidByCode",
            data: params,
            header: {
              'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            success: res => {
              var result = res;
              wx.getUserInfo({
                success: function (res) {
                  // 用户已经授权过,不需要显示授权页面,所以不需要改变 isHide 的值
                  // 根据自己的需求有其他操作再补充
                  // 我这里实现的是在用户授权成功后，调用微信的 wx.login 接口，从而获取code
                  var userinfo = res.userInfo;
                  userinfo.iv = res.iv;
                  userinfo.encryptedData = res.encryptedData;
                  // console.log("userinfo", userinfo)
                  // console.log("userinfo", userinfo)
                  userinfo.openid = result.data.result.openid;
                  userinfo.session_key = result.data.result.session_key;
                  wx.setStorageSync('openid', result.data.result.openid);
                  wx.setStorageSync('session_key', result.data.result.session_key);
                  that.setData({
                    userinfo: userinfo
                  });
                  console.log(userinfo)
                  
                 

                  //获取手机
                  var params = that.data.userinfo,
                        tjrOpenid =wx.getStorageSync('tjrOpenid');
                      console.log("params", params)
                      
                      if (e) {
                        if (e.detail.errMsg === "getPhoneNumber:ok") {
                          params.iv = e.detail.iv;
                          params.encryptedData = e.detail.encryptedData;
                        } else {
                        
                          return
                        }

                      }
                      if(tjrOpenid){
                        params.tjrOpenid =tjrOpenid
                      }
                      console.log("e", e)
                      console.log(e.detail.iv, e.detail.encryptedData)
                      console.log("tel", params)
                      const tjv = wx.getStorageSync('tjv')    // 用户是否通商户码扫码进入
                      if(tjv) params.tjv = tjv
                      wx.showLoading({
                        title: '正在登录中'
                      });
                      // console.log(that.data.userinfo)
                      console.log("params", params)
                      // this.checkSession()sx
                      // .then()
                      // .catch()
                      http.request({
                        method: "POST",
                        url: app.globalData.ajax_url + "/v1/wx/auth/phone",
                        data: params,
                        header: {
                          'content-type': 'application/json;charset=UTF-8',
                          "farmer": app.globalData.location.pkey

                        },
                        success: res => {
                          var result = res;
                          console.log("res_phone", res.data)
                          // wx.showToast({
                          //   title: result.data.data.openid1,
                          // });
                          wx.setStorageSync('openid', result.data.data.openid1);
                          wx.setStorageSync('userinfo', result.data.data);
                          app.globalData.userinfo = wx.getStorageSync('userinfo');
                          app.globalData.openid = wx.getStorageSync('openid');
                          that.setData({
                            iShidden: true
                          });
                          that.triggerEvent('onLoadFun');
                          wx.hideLoading();
                        }
                      });
                  // end 获取手机
                }
              })
            }
          });
        }
      });    
    },
    handleClose() {
        this.triggerEvent('onClose');
    }
  }
});