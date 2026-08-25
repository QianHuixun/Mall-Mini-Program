// pages/my/refundApply/index.js
import http from '../../../utils/http';
let app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
      pkey: "",
      checkResult: [],
      allCheck: false,
      allRefundAmt: 0,
      allRefundPoint: 0,
      orderInfo: {},
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.setData({
        pkey:options.pkey
      });
      this.getData();
    },
    onChange(event) {
      this.setData({
        checkResult: event.detail,
      });
      let orderInfo = this.data.orderInfo,
      allRefundAmt = 0,
      allRefundPoint = 0,
      checkResult = this.data.checkResult,
      allCheck = true;
      console.log("checkResult",checkResult);
      orderInfo.infos.forEach((item,index)=> {
        if(item.lines.length === 1 && (item.num - item.refundNum > 0)) {
          if(!checkResult.includes(`${item.orderLinePkey}`)) {
            allCheck =false;
            orderInfo.infos[index].newRefundNum = 0;
            // 如果选中数组中包含当前item的pkey,则计算退款金额，总金额
          } else {
            // 如果选中了，stepper为0 ，则修改为1
            if( !orderInfo.infos[index].newRefundNum && (item.num - item.refundNum > 0)) {
              orderInfo.infos[index].newRefundNum = 1;
            }

            const lastAmt = item.couponAmt - item.refundAmt;// 剩余金额
            orderInfo.infos[index].newRefundAmt = item.couponPrice * orderInfo.infos[index].newRefundNum;
            console.log("orderInfo.infos[index].newRefundAmt", orderInfo.infos[index].newRefundAmt, "lastAmt", lastAmt)
            if(  orderInfo.infos[index].newRefundNum  === (item.num - item.refundNum) && orderInfo.infos[index].newRefundAmt != lastAmt) {
              orderInfo.infos[index].newRefundAmt = lastAmt;
              console.log(1)
            }
            if(orderInfo.infos[index].sales - orderInfo.infos[index].refundAmt < orderInfo.infos[index].newRefundAmt) {
              orderInfo.infos[index].newRefundAmt = orderInfo.infos[index].sales - orderInfo.infos[index].refundAmt;
            }
            allRefundAmt += orderInfo.infos[index].newRefundAmt;
            if(item.num - item.refundNum - orderInfo.infos[index].newRefundNum == 0) {
              allRefundPoint += orderInfo.infos[index].point;
            }
            
          }       
        } else {
          item.lines.forEach((subItem,subIndex) => {
            //  如果选中数组中不包含当前item的pkey，则全选按钮不选中
            if(!checkResult.includes(`${subItem.pkey}`)) {
              allCheck =false;
              orderInfo.infos[index].lines[subIndex].newRefundNum = 0;
              // 如果选中数组中包含当前item的pkey,则计算退款金额，总金额
            } else {
              if( !orderInfo.infos[index].lines[subIndex].newRefundNum  && (subItem.num - subItem.refundNum > 0)) {
                orderInfo.infos[index].lines[subIndex].newRefundNum = 1;
              }

              const lastAmt = subItem.couponAmt - subItem.refundAmt;// 剩余金额  
              orderInfo.infos[index].lines[subIndex].newRefundAmt = subItem.couponPrice * orderInfo.infos[index].lines[subIndex].newRefundNum;
              if( orderInfo.infos[index].lines[subIndex].newRefundNum  === (subItem.num - subItem.refundNum) && orderInfo.infos[index].lines[subIndex].newRefundAmt != lastAmt) {
                orderInfo.infos[index].lines[subIndex].newRefundAmt = lastAmt;
              }

              if(orderInfo.infos[index].lines[subIndex].sales -orderInfo.infos[index].lines[subIndex].refundAmt <orderInfo.infos[index].lines[subIndex].newRefundAmt) {
                orderInfo.infos[index].lines[subIndex].newRefundAmt = orderInfo.infos[index].lines[subIndex].sales -orderInfo.infos[index].lines[subIndex].refundAmt;
              }
              allRefundAmt += orderInfo.infos[index].lines[subIndex].newRefundAmt;
              if(orderInfo.infos[index].lines[subIndex].num - orderInfo.infos[index].lines[subIndex].refundNum - orderInfo.infos[index].lines[subIndex].newRefundNum == 0) {
                allRefundPoint += orderInfo.infos[index].lines[subIndex].point;
              }
            }       
          });
        }
        
      });
      console.log("allCheck", allCheck)
      this.setData({
        allCheck: allCheck,
        orderInfo: orderInfo,
        allRefundAmt: allRefundAmt,
        allRefundPoint: allRefundPoint
      });
    },
    stepChange(data) {
      let orderInfo = this.data.orderInfo,
      allRefundAmt = 0,
      allRefundPoint = 0,
      checkResult = this.data.checkResult,
      allCheck = true;
      const pkey = data.currentTarget.dataset.pkey, 
      newValue = data.detail; //修改后的值
      console.log("data", data)
       // stepper的pkey不包含在选中项中，则添加到checkResult（多选按钮选中数组）里
      if(!checkResult.includes(`${pkey}`)) {
        checkResult.push(`${pkey}`);
      }
      // 当stepper的value 是0 时，从选中项中删除           
      if(newValue == 0) {
        checkResult = checkResult.filter(item =>{return item!=pkey});       
      }
      console.log("checkResult",checkResult);
      orderInfo.infos.forEach((item,index)=> {
        // 如果规格数量=1，且 剩余数量>0,
        if(item.lines.length === 1 && (item.num - item.refundNum > 0)) {
          //如果当前item的pkey = 调整stepper的pkey
          if(item.orderLinePkey == pkey) {
            // 将当前item的退款数量调整为stepper的最新值
             orderInfo.infos[index].newRefundNum = newValue;         
          }
          //  如果选中数组中不包含当前item的pkey，则全选按钮不选中
          console.log(item.orderLinePkey, checkResult.includes(`${item.orderLinePkey}`))
          if(!checkResult.includes(`${item.orderLinePkey}`)) {
            allCheck =false;
            // 如果选中数组中包含当前item的pkey,则计算退款金额，总金额
          } else {
           

            const lastAmt = item.couponAmt - item.refundAmt;// 剩余金额  
            orderInfo.infos[index].newRefundAmt = item.couponPrice * orderInfo.infos[index].newRefundNum;
            if( orderInfo.infos[index].newRefundNum  === (item.num - item.refundNum) &&  orderInfo.infos[index].newRefundAmt != lastAmt) {
              orderInfo.infos[index].newRefundAmt = lastAmt;
            }

            if(orderInfo.infos[index].sales -orderInfo.infos[index].refundAmt < orderInfo.infos[index].newRefundAmt) {
              orderInfo.infos[index].newRefundAmt = orderInfo.infos[index].sales -orderInfo.infos[index].refundAmt;
            }
            allRefundAmt += orderInfo.infos[index].newRefundAmt;
            if(item.num - item.refundNum - orderInfo.infos[index].newRefundNum == 0) {
              allRefundPoint += orderInfo.infos[index].point;
            }
          }       
        } else {
          item.lines.forEach((subItem,subIndex) => {
            //如果当前item的pkey = 调整stepper的pkey
            if(subItem.pkey == pkey) {
              // 将当前item的退款数量调整为stepper的最新值
              orderInfo.infos[index].lines[subIndex].newRefundNum = newValue;         
            }
            //  如果选中数组中不包含当前item的pkey，则全选按钮不选中
            console.log(subItem.pkey,checkResult.includes(`${subItem.pkey}`))
            if(!checkResult.includes(`${subItem.pkey}`)) {
              allCheck = false;
              // 如果选中数组中包含当前item的pkey,则计算退款金额，总金额
            } else {
              const lastAmt = subItem.couponAmt - subItem.refundAmt;// 剩余金额  
              orderInfo.infos[index].lines[subIndex].newRefundAmt = subItem.couponPrice * orderInfo.infos[index].lines[subIndex].newRefundNum;
            if( orderInfo.infos[index].lines[subIndex].newRefundNum  === (subItem.num - subItem.refundNum) &&  orderInfo.infos[index].lines[subIndex].newRefundAmt != lastAmt) {
              orderInfo.infos[index].lines[subIndex].newRefundAmt = lastAmt;
            }
             
              if(orderInfo.infos[index].lines[subIndex].sales -orderInfo.infos[index].lines[subIndex].refundAmt < orderInfo.infos[index].lines[subIndex].newRefundAmt) {
                orderInfo.infos[index].lines[subIndex].newRefundAmt = orderInfo.infos[index].lines[subIndex].sales -orderInfo.infos[index].lines[subIndex].refundAmt;
              }
              allRefundAmt += orderInfo.infos[index].lines[subIndex].newRefundAmt;
              if(orderInfo.infos[index].lines[subIndex].num - orderInfo.infos[index].lines[subIndex].refundNum - orderInfo.infos[index].lines[subIndex].newRefundNum == 0) {
                allRefundPoint += orderInfo.infos[index].lines[subIndex].point;
              }
            }       
          });
        }
        
      });
      console.log("allCheck", allCheck)
      this.setData({
        checkResult: checkResult,
        orderInfo: orderInfo,
        allRefundAmt: allRefundAmt,
        allRefundPoint: allRefundPoint,
        allCheck: allCheck
      });
    },
    allCheckChange(event) {
      let checkResult =[],
       orderInfo = this.data.orderInfo,
       allRefundAmt = 0,allRefundPoint = 0;
       console.log("orderInfo", event.detail)
      if(event.detail) {
        orderInfo.infos.forEach((item,index)=> {
          if(item.lines.length === 1 && (item.num - item.refundNum > 0)) {
            const orderInfoItem = orderInfo.infos[index];
            orderInfoItem.newRefundNum = item.num - item.refundNum;  
           
            const lastAmt = item.couponAmt - item.refundAmt;// 剩余金额  
            orderInfoItem.newRefundAmt = item.couponPrice * (item.num - item.refundNum);
            console.log("newRefundAmt:", orderInfoItem.newRefundAmt, "lastAmt:", lastAmt)
          if(  orderInfoItem.newRefundNum  === (item.num - item.refundNum) && orderInfoItem.newRefundAmt != lastAmt) {
            orderInfoItem.newRefundAmt = lastAmt;
          }
            if(orderInfoItem.sales -orderInfoItem.refundAmt < orderInfoItem.newRefundAmt) {
              
              orderInfoItem.newRefundAmt = orderInfoItem.sales - orderInfoItem.refundAmt;
            }
            allRefundAmt += orderInfoItem.newRefundAmt;
            if(item.num - item.refundNum - orderInfoItem.newRefundNum == 0) {
              allRefundPoint += orderInfoItem.point;
            }
          }
          item.lines.forEach((subItem,subIndex) => {
            if(item.num - item.refundNum > 0) {
              checkResult.push(`${subItem.pkey}`);
            }
           
            if(item.lines.length >1) {
              const orderInfoSubItem = orderInfo.infos[index].lines[subIndex];
              orderInfoSubItem.newRefundNum=1; 
             
              const lastAmt = subItem.couponAmt - subItem.refundAmt;// 剩余金额  
              orderInfoSubItem.newRefundAmt=subItem.couponPrice;   
              if(  orderInfoItem.newRefundNum  === (subItem.num -subItem.refundNum) && orderInfoItem.newRefundAmt != lastAmt) {
                orderInfoItem.newRefundAmt = lastAmt;
              }

              if(orderInfoSubItem.sales -orderInfoSubItem.refundAmt < orderInfoSubItem.newRefundAmt) {
                orderInfoSubItem.newRefundAmt = orderInfoSubItem.sales -orderInfoSubItem.refundAmt;
              }
              allRefundAmt +=orderInfoSubItem.newRefundAmt;
              if(subItem.num - subItem.refundNum - orderInfoSubItem.newRefundNum == 0) {
                allRefundPoint += orderInfoSubItem.point;
              }
            }
            
          });
        });
      } else {
        orderInfo.infos.forEach((item,index)=> {
          orderInfo.infos[index].newRefundNum = 0;  
          orderInfo.infos[index].newRefundAmt = 0;
          item.lines.forEach((subItem,subIndex) => {
            orderInfo.infos[index].lines[subIndex].newRefundNum= 0; 
            orderInfo.infos[index].lines[subIndex].newRefundAmt= 0;  
          }); 
        });
      }
      console.log("allRefundAmt", allRefundAmt,checkResult)
      this.setData({
        allCheck: event.detail,
        checkResult: checkResult,
        orderInfo: orderInfo,
        allRefundAmt: allRefundAmt,
        allRefundPoint: allRefundPoint
      });
    },
    getData: function () {
      var that = this,
        url = "/v2/app/market/lm/order/loadOrder";
      http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
          pkey: this.data.pkey
        },
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          "openid": app.globalData.openid,
          "farmer": app.globalData.location.pkey
        },
        success: res => {
          if (res.data.success) {
            wx.setStorageSync('refundInfo', JSON.stringify(res.data.result));
            that.setData({
              orderInfo: res.data.result
            });
          }
        }
        })
    },
    // 完成 退款商品选择
    handleRefundGoods(){
     const orderInfo =  this.data.orderInfo,
     checkResult = this.data.checkResult;
     if(!checkResult.length) {
       wx.showToast({
         title: '请选择商品',
         icon: "none"
       })
       return;
     }
     let lines = [];
     orderInfo.infos.forEach((item)=> {
       if(item.lines.length === 1) {
        if(checkResult.includes(`${item.orderLinePkey}`)) {
          lines.push({
            pkey: item.orderLinePkey,
            refundAmt: Number((item.newRefundAmt).toFixed(2)),
            num: item.newRefundNum,
            photo: item.photo
          });
        }
       } else {    
        item.lines.forEach((subItem) => {
          if(checkResult.includes(`${subItem.pkey}`)) {
            lines.push({
              pkey: subItem.pkey,
              refundAmt: Number((subItem.newRefundAmt).toFixed(2)),
              num: subItem.newRefundNum,
              photo: subItem.photo
            });
          }
        });
      }
     });
     const refundData = {
      pkey: this.data.orderInfo.pkey,
      lines: lines,
      allRefundAmt: Number((this.data.allRefundAmt).toFixed(2)),
      allRefundPoint: Number((this.data.allRefundPoint).toFixed(1)),
      status:this.data.orderInfo.status,
      tel: this.data.orderInfo.tel,
      orderOir: this.data.orderInfo.orderOir,
      orderType: this.data.orderInfo.orderType,
     }
     console.log(refundData);
     wx.setStorageSync('refundStepOneData', JSON.stringify(refundData));
     wx.navigateTo({
       url: '/pages/my/refundSubmit/index',
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