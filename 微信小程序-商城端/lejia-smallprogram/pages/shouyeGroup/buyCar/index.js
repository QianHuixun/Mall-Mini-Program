// pages/buyCar/index.js
let app = getApp();
import http from '../../../utils/http';
import Toast from '@vant/weapp/toast/toast';
const { applyTheme } = require('../../../utils/themeMixin')
let recommendGoods
Page({

  /**
   * 页面的初始数据
   */
  data: {
    name: app.globalData.location.name,
    newTabbar: false,   // 新导航栏
    marketList: [], //市场商品列表
    mallList: [], //积分商场商品列表
    msdList: [], //民生豆商品列表
    jdList: [], //京东商品列表
    marketResult: [], //市场产品选中项
    mallResult: [], //积分商城产品选中项
    msdResult: [], //民生豆产品选中项
    jdResult: [], //民生豆产品选中项
    unActiveList: [], //已下架商品
    allMarket: false, //是否全选市场商品
    allMall: false, //是否全选积分商城商品
    allMsd: false, //是否全选民生豆商品
    allJd: false, //是否全选京东商品
    allCheckList: [], //存全局 选中商品pkey
    allCheck: false, //全部选中/不选
    loading: false, //按钮载入效果
    disabled: true, //按钮禁用效果
    marketPrice: 0, //市场选中商品 价格和
    mallPrice: 0, //积分商城选中商品 价格和
    msdPrice: 0, //民生豆选中商品 价格和
    jdPrice: 0, //民生豆选中商品 价格和
    allPrice: 0.00, //总价
    iShidden: true,
    isAuto: true,
    total: 0,
    marketName: app.globalData.location.name,
    freeDelivery: 99999,
    isFree: false,
    reachOne: 99999,
    isReductionOne: false,
    reductionDeliveryOne: 0,
    reachTwo: 99999,
    isReductionTwo: false,
    reductionDeliveryTwo: 0,
    allMoney: 0,
    startingPrice: 0,
    userData:{},
    title: '积分商城',
    msdTitle: '民生豆',
    jdTitle: '京东优选',
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if(this.getTabBar()) {
        this.getTabBar().init()
        this.setData({
            newTabbar: true
        })
    }
    applyTheme(this)
    // wx.hideTabBar();
    recommendGoods = this.selectComponent('#recommendGoods')
    this.setData({
      title: (app.globalData.ascription === 22 || app.globalData.ascription === 13) ? '滨海民生' : '积分商城'
    })
  },
  /**
   * 获取会员信息
   */
  getMemeberData: function () {
    var that = this;
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v1/app/market/lm/member/get/centre',
      data: {},
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        that.setData({
          userData: res.data.result
        });
      }
    })
  },
  /**
   * @desc 删除所有商品
   * @param event 点击事件实例 包含了参数type判断是删除失效商品还是正常商品
   */
  handleAllDelete(event) {
    let allCheckList = [],
      that = this,
      marketList = JSON.parse(JSON.stringify(this.data.marketList)) ,
      mallList = JSON.parse(JSON.stringify(this.data.mallList)),
      msdList = JSON.parse(JSON.stringify(this.data.msdList)),
      jdList = JSON.parse(JSON.stringify(this.data.jdList));

    marketList.map((item, index) => {
      item.marketResult.map(subItem => {
        allCheckList.push(subItem + '')
      });
      item.marketResult = [];
      return item
    });

    mallList.map((item, index) => {
      item.mallResult.map(subItem => {
        allCheckList.push(subItem + '')
      });
      item.mallResult = [];
      return item
    });

    msdList.map((item, index) => {
        item.msdResult.map(subItem => {
          allCheckList.push(subItem + '')
        });
        item.msdResult = [];
        return item
      });
    jdList.map((item, index) => {
        if(item.jdResult) {
            allCheckList.push(item.pkey + '')
        }
      });
    let message = allCheckList.length == 1 ? '确定将这1种商品删除？' : `确认将这${allCheckList.length}种商品删除？`;
    if (event.currentTarget.dataset.type) {
      let unActiveList = [];
      this.data.unActiveList.map(item => {
        if (!item.lines || item.lines.length == 1) {
          unActiveList.push(item.pkey + "");
        } else {
          item.lines.map(subItem => {
            unActiveList.push(subItem.pkey + "");
          })
        }

      });
      allCheckList = unActiveList;
      message = '确认清空失效商品吗？';
    }
  
    console.log(allCheckList);
    if (!allCheckList.length) {
      wx.showToast({
        title: '请选择商品',
        icon: 'none',
        duration: 2000
      });
      return
    }

    wx.showModal({
      title: '操作',
      content: message,
      confirmText: '删除',
      cancelText: '我再想想',
      confirmColor: '#63BF57',
      success(res) {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中',
          })
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/lm/member/gwc/delByPkeys",
            data: {
              pkeys: allCheckList
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
              "openid": app.globalData.openid,
              "farmer": app.globalData.location.pkey
            },
            success: res => {
              wx.hideLoading()
              if (res.data.code == "999") {
                that.setData({
                  iShidden: false
                })
                return;
              };
              if (res.data.success) {
                that.setData({
                  marketResult: [],
                  mallResult: [],
                  msdResult: [],
                  allCheckList: [],
                  marketList,
                  mallList,
                  msdList
                });
                app.getBuycarNum();
                that.getData();
              }
              recommendGoods.getData()
            }
          });
        }
      }
    })
  },
  /**
   * 获取购物车数据
   */
  getData: function () {
    var that = this,
      allMoney = 0
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + '/v2/app/market/lm/gwc/query',
      data: {},
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
          res.data.result.currentFarmer.forEach(item => {
            if (item.goodsEnabled && item.kcNum)
              allMoney += (item.price * item.num)
          });
          let marketList = [],
            mallList = [],
            msdList = [],
            jdList = [],
            unActiveList = [];
          res.data.result.currentFarmer.map(item => {
            if (!item.goodsEnabled) {
              unActiveList.push(item)
            } else {
              if (item.lines.length > 1) {
                let marketSpace = [],
                  marketSpacePkey = [],
                  unActiveSpace = [];
                item.lines.map(subItem => {
                  if (subItem.kcNum) {
                    marketSpace.push(subItem)
                    marketSpacePkey.push(subItem.pkey + '')
                  } else {
                    unActiveSpace.push(subItem);
                  }
                })

                if (marketSpace.length) {
                  let marketItem = JSON.parse(JSON.stringify(item))
                  marketItem.marketResult = marketSpacePkey;
                  marketItem.lines = marketSpace;
                  marketList.push(marketItem)

                }
                if (unActiveSpace.length) {
                  let unActiveItem = JSON.parse(JSON.stringify(item))
                  unActiveItem.lines = unActiveSpace;
                  unActiveList.push(unActiveItem)
                }

              } else {
                if (item.kcNum) {
                  item.marketResult = [item.lines[0].pkey + ''];
                  marketList.push(item)
                } else
                  unActiveList.push(item)
              }
            }
          });

          // 积分商城商品
          res.data.result.pointsMall.map(item => {
            if (!item.goodsEnabled) {
              unActiveList.push(item)
            } else {
              if (item.lines.length > 1) {
                let mallSpace = [],
                  mallSpacePkey = [],
                  unActiveSpace = [];
                item.lines.map(subItem => {
                  if (subItem.kcNum) {
                    mallSpace.push(subItem)
                    mallSpacePkey.push(subItem.pkey + '')
                  } else {
                    unActiveSpace.push(subItem);
                  }
                })
                if (mallSpace.length) {
                  item.lines = mallSpace;
                  item.mallResult = res.data.result.startingPrice ? [] : mallSpacePkey;
                  mallList.push(item)
                }
                if (unActiveSpace.length) {
                  item.lines = unActiveSpace;
                  unActiveList.push(item)
                }

              } else {
                if (item.kcNum) {
                  item.mallResult = res.data.result.startingPrice ? [] : [item.lines[0].pkey + ''];
                  mallList.push(item)
                } else
                  unActiveList.push(item)
              }
            }
          });

          // 民生豆专区商品
          res.data.result.pointsMsd.map(item => {
              console.log('msdGoods',item);
            if (!item.goodsEnabled) {
              unActiveList.push(item)
            } else {
              if (item.lines.length > 1) {
                let msdSpace = [],
                    msdSpacePkey = [],
                    unActiveSpace = [];
                item.lines.map(subItem => {
                  if (subItem.kcNum) {
                    msdSpace.push(subItem)
                    msdSpacePkey.push(subItem.pkey + '')
                  } else {
                    unActiveSpace.push(subItem);
                  }
                })
                if (msdSpace.length) {
                  item.lines = msdSpace;
                  item.msdResult = [];
                  msdList.push(item)
                }
                if (unActiveSpace.length) {
                  item.lines = unActiveSpace;
                  unActiveList.push(item)
                }

              } else {
                if (item.kcNum) {
                //   item.msdResult = [item.lines[0].pkey + ''];
                item.msdResult = [];
                  msdList.push(item)
                } else
                  unActiveList.push(item)
              }
            }
          });

          // 京东专区商品
          res.data.result.jdGoodsList.map(item => {
            if (!item.enabled) {
            //   unActiveList.push(item)
            } else {
              item.jdResult = false
              jdList.push(item)
            }
          });
          that.setData({
            msdTitle: res.data.result.integralMsdDisplayName,
            jdTitle: res.data.result.jdGoodsDisplayName,
            marketList: marketList,
            mallList: mallList,
            msdList: msdList,
            jdList: jdList,
            total: res.data.result.total,
            unActiveList: unActiveList,
            freeDelivery: res.data.result.freeDelivery,
            isFree: res.data.result.isFree,
            reachOne: res.data.result.reachOne ? res.data.result.reachOne.toFixed(2) : '0',
            isReductionOne: res.data.result.isReductionOne,
            reductionDeliveryOne: res.data.result.reductionDeliveryOne,
            reachTwo: res.data.result.reachTwo ? res.data.result.reachTwo.toFixed(2) : '0',
            isReductionTwo: res.data.result.isReductionTwo,
            reductionDeliveryTwo: res.data.result.reductionDeliveryTwo,
            allMoney,
            startingPrice: res.data.result.startingPrice
          });
          let marketResult = marketList.map(item => {
              return item.pkey + ''
            }),
            mallResult = mallList.map((item, index) => {
              mallList[index].mallResult = [];
              item.lines.map(subItem => {
                mallList[index].mallResult.push(subItem.pkey + '');
              });
              return item.pkey + "";
            });
          that.setData({
            marketResult, //市场产品选中项
            mallResult, //积分商城产品选中项
          })
          this.calculateGoods()
          /**
           * 设置底部导航栏的 购物车图标显示商品数量
           */

        }
      },
      complete: res => {
        wx.hideLoading()
      }
    })
  },
  /**
   * @desc 相加函数 避免计算出现多位小数问题
   */
  accAdd(arg1, arg2) {
    var r1, r2, m;
    try {
      r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
      r1 = 0
    }

    try {
      r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
      r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    return (arg1 * m + arg2 * m) / m;
  },
  /**
   * @desc 相乘函数 避免计算出现多位小数问题
   */
  accMul(arg1, arg2) {
    var m = 0,
      s1 = arg1.toString(),
      s2 = arg2.toString();
    try {
      m += s1.split(".")[1].length
    } catch (e) {}
    try {
      m += s2.split(".")[1].length
    } catch (e) {}
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
  },
  /**
   * 改变数量
   */
  stepChange(data) {
    var that = this,
      url = "/v1/app/market/lm/member/gwc/less/goods/num", //减少购物车里单个商品的数量
      url_add = "/v1/app/market/lm/member/gwc/add/goods/num", //增加购物车里单个商品的数量
      pkey = data.currentTarget.dataset.pkey, //当前产品pkey
      spacePkey = (data.currentTarget.dataset.spacepkey?data.currentTarget.dataset.spacepkey+'':'') , //当前规格购物车pkey
      space = data.currentTarget.dataset.space, //当前规格pkey
      type = data.currentTarget.dataset.type, //类型
      newValue = data.detail, //修改后的值
      marketList = [],
      mallList = [],
      msdList = [];
    if (!newValue) {
      this.handleDelete({
        currentTarget: {
          dataset: {
            value: spacePkey ?spacePkey: pkey,
            type
          }
        }
      })
      return
    }

    msdList = that.data.msdList.map(item => {
        if (item.pkey == pkey) {
          item.lines.map(subItem => {
            if (subItem.space == space && subItem.num < newValue) {
              url = url_add;
            }
          })
        }
      });

    mallList = that.data.mallList.map(item => {
      if (item.pkey == pkey) {
        item.lines.map(subItem => {
          if (subItem.space == space && subItem.num < newValue) {
            url = url_add;
          }
        })
      }
    });

    marketList = that.data.marketList.map(item => {
      if (item.pkey == pkey) {
        item.lines.map(subItem => {
          if (subItem.space == space && subItem.num < newValue) {
            url = url_add;
          }
        })

      }
    });


    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: {
        goodsPkey: pkey,
        space: space,
        goodsNum: 1
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        };
        if (res.data.success) {
            msdList = that.data.msdList.map(item => {
                if (item.pkey == pkey) {
                  item.sumPrice = 0;
                  item.lines = item.lines.map(subItem => {
                    if (subItem.space == space) {
                      subItem.num = newValue;
                    }
                    item.sumPrice = that.accAdd(item.sumPrice, that.accMul(subItem.num, subItem.price))
                    return subItem
                  })
    
                }
                return item;
              });

          mallList = that.data.mallList.map(item => {
            if (item.pkey == pkey) {
              item.sumPrice = 0;
              item.lines = item.lines.map(subItem => {
                if (subItem.space == space) {
                  subItem.num = newValue;
                }
                item.sumPrice = that.accAdd(item.sumPrice, that.accMul(subItem.num, subItem.price))
                return subItem
              })

            }
            return item;
          });

          marketList = that.data.marketList.map(item => {
            if (item.pkey == pkey) {
              item.sumPrice = 0;
              item.lines = item.lines.map(subItem => {
                if (subItem.space == space) {
                  subItem.num = newValue;
                }
                item.sumPrice = that.accAdd(item.sumPrice, that.accMul(subItem.num, subItem.price))
                return subItem
              })
            }
            return item;
          });

          app.getBuycarNum();
          that.setData({
            marketList: marketList,
            mallList: mallList,
            msdList: msdList
          });
         

          that.calculateGoods();
        } else {

          wx.showToast({
            title: res.data.msg || '',
            icon: "none"
          });
        }
        that.calmarketList();
        recommendGoods.getData()
      }
    });
  },
  jdStepChange(data) {
    let url;
    const { index, skuid, pkey, lowestbuy } = data.currentTarget.dataset;
    const newValue = data.detail; //修改后的值
    if (!newValue || newValue < lowestbuy) {
        this.handleDelete({
            currentTarget: {
                dataset: {
                    value: pkey,
                    type: 'jd'
                }
            }
        });
        return;
    };
    const goods = this.data.jdList[index]
    url = goods.num < newValue ? '/v1/app/jd/goods/gwc/add/num' : '/v1/app/jd/goods/gwc/less/num';
    http.request({
        method: "POST",
        url: app.globalData.ajax_url + url,
        data: {
            pkey: skuid,
            goodsNum: 1
        },
        success: (res) => {
            if (res.data.code == "999") {
                this.setData({
                    iShidden: false
                })
                return;
            };
            if(res.data.success) {
               goods.num = newValue;
               const jdList = this.data.jdList;
               jdList[index] = goods;
               app.getBuycarNum();
               this.setData({jdList});
               this.calculateGoods();
            } else {
                wx.showToast({
                    title: res.data.msg || '',
                    icon: "none"
                });
            };
            this.calmarketList();
            recommendGoods.getData()
        }
    })
  },
  /**
   * 删除
   */
  handleDelete: function (data) {
    var that = this,
      pkey = data.currentTarget.dataset.value + '',
      type = data.currentTarget.dataset.type,
      marketList = this.data.marketList,
      mallList = this.data.mallList,
      msdList = this.data.msdList,
      jdList = this.data.jdList,
      marketResult = this.data.marketResult,
      mallResult = this.data.mallResult,
      msdResult = this.data.msdResult,
      jdResult = this.data.jdResult,
      unActiveList = this.data.unActiveList;
    wx.showModal({
      title: '操作',
      content: '确认将这个商品删除？',
      confirmText: '删除',
      cancelText: '我再想想',
      confirmColor: '#63BF57',
      success(res) {
        if (res.confirm) {
          http.request({
            method: "POST",
            url: app.globalData.ajax_url + "/v1/app/market/lm/member/gwc/del",
            data: {
              pkey: pkey
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
              "openid": app.globalData.openid,
              "farmer": app.globalData.location.pkey
            },
            success: res => {
              if (res.data.code == "999") {
                that.setData({
                  iShidden: false
                })
                return;
              };
              if (res.data.success) {
                if (type != "unactive") {
                  let total = that.data.total;
                  if (type == "market") {
                    for (let i = marketList.length - 1; i >= 0; i--) {
                      let item = marketList[i]
                      if (item.pkey == pkey && item.lines.length == 1) {
                        marketList.splice(i, 1);
                        marketResult = marketResult.filter(subItem => {
                          if (subItem != pkey) {
                            return subItem
                          }
                        })
                      } else {
                        item.marketResult = item.marketResult.filter(subItem => {
                          if (subItem != pkey) {
                            return subItem
                          }
                        })
                        item.lines = item.lines.filter(subItem => {
                          if (subItem.pkey != pkey) {
                            return subItem
                          }
                        });
                        marketList[i] = item;
                      }

                    }
                    that.setData({
                      marketList: marketList,
                      marketResult: marketResult
                    });
                    that.calmarketList()
                  }else if(type == "msd"){
                    for (let i = msdList.length - 1; i >= 0; i--) {
                        let item = msdList[i]
                        if (item.pkey == pkey && item.lines.length == 1) {
                          msdList.splice(i, 1);
                          msdResult = msdResult.filter(subItem => {
                            if (subItem != pkey) {
                              return subItem
                            }
                          })
                        } else {
                          item.msdResult = item.msdResult.filter(subItem => {
                            if (subItem != pkey) {
                              return subItem
                            }
                          })
                          item.lines = item.lines.filter(subItem => {
                            if (subItem.pkey != pkey) {
                              return subItem
                            }
                          });
                          msdList[i] = item;
                        }
  
                      }
                      that.setData({
                        total: total - 1,
                        msdList: msdList,
                        msdResult: msdResult
                      });
                  }else if(type == 'jd') {
                    for (let i = jdList.length - 1; i >= 0; i--) {
                        let item = jdList[i]
                        if (item.pkey == pkey) {
                            jdList.splice(i, 1);
                            jdResult = jdResult.filter(subItem => {
                                if (subItem != pkey) {
                                  return subItem
                                }
                            })
                        }
                    }
                    that.setData({
                        total: total - 1,
                        jdList: jdList,
                        jdResult: jdResult
                    });
                  }else {
                    for (let i = mallList.length - 1; i >= 0; i--) {
                      let item = mallList[i]
                      if (item.pkey == pkey && item.lines.length == 1) {
                        mallList.splice(i, 1);
                        mallResult = mallResult.filter(subItem => {
                          if (subItem != pkey) {
                            return subItem
                          }
                        })
                      } else {
                        item.mallResult = item.mallResult.filter(subItem => {
                          if (subItem != pkey) {
                            return subItem
                          }
                        })
                        item.lines = item.lines.filter(subItem => {
                          if (subItem.pkey != pkey) {
                            return subItem
                          }
                        });
                        mallList[i] = item;
                      }

                    }
                    that.setData({
                      total: total - 1,
                      mallList: mallList,
                      mallResult: mallResult
                    });

                  }
                  that.calmarketList();
                  app.getBuycarNum();
                  that.calculateGoods();
                } else {

                  for (let i = unActiveList.length - 1; i >= 0; i--) {
                    let item = unActiveList[i]
                    if (item.pkey == pkey && item.lines.length == 1) {
                      unActiveList.splice(i, 1);
                    } else {
                      item.lines = item.lines.filter(subItem => {
                        if (subItem.pkey != pkey) {
                          return subItem
                        }
                      });
                      unActiveList[i] = item;
                    }

                  }
                  that.setData({
                    unActiveList: unActiveList
                  })
                }

                that.getData();

              }
              recommendGoods.getData()
            }
          });
        }
      }
    })

  },
  marketToggle(event) {
    const {
      index,
      pkey
    } = event.currentTarget.dataset;
    let marketList = this.data.marketList,
      marketResult = this.data.marketResult;

    if (marketResult.includes(pkey + '')) {
      marketList[index].marketResult = marketList[index].lines.map(item => {
        return item.pkey + ''
      });
    } else {
      marketList[index].marketResult = []
    }
    let allMarket = true;
    marketList.map(item => {
      if (item.lines.length != item.marketResult.length) {
        allMarket = false;
      }
    })
    this.setData({
      allMarket,
      marketList: marketList
    })
    this.calculateGoods();

  },
  mallToggle(event) {
    const {
      index,
      pkey
    } = event.currentTarget.dataset;
    let mallList = this.data.mallList,
      mallResult = this.data.mallResult;
    if (mallResult.includes(pkey + '')) {
      mallList[index].mallResult = mallList[index].lines.map(item => {
        return item.pkey + ''
      });
    } else {
      mallList[index].mallResult = []
    }
    let allMall = true;
    mallList.map(item => {
      if (item.lines.length != item.mallResult.length) {
        allMall = false;
      }
    })

    this.setData({
      allMall,
      mallList: mallList
    })
    this.calculateGoods();
  },
  msdToggle(event) {
    const {
      index,
      pkey
    } = event.currentTarget.dataset;
    let msdList = this.data.msdList,
      msdResult = this.data.msdResult;
    if (msdResult.includes(pkey + '')) {
        msdList[index].msdResult = msdList[index].lines.map(item => {
        return item.pkey + ''
      });
    } else {
        msdList[index].msdResult = []
    }
    let allMsd = true;
    msdList.map(item => {
      if (item.lines.length != item.msdResult.length) {
        allMsd = false;
      }
    })

    this.setData({
      allMsd,
      msdList: msdList
    })
    this.calculateGoods();
  },
  jdToggle(event) {
    const {
        index,
        pkey
    } = event.currentTarget.dataset;
    let jdList = this.data.jdList,
        jdResult = this.data.jdResult;
    const allJd = jdList.length == jdResult.length ? true : false
    if (jdResult.includes(pkey + '')) {
        jdList[index].jdResult = true
    } else {
        jdList[index].jdResult = false
    }
    this.setData({
      allJd,
      jdList,
    })
    this.calculateGoods();
  },

  /**
   * 多选按钮 改变事件 --市场
   */
  groupMarketCheckChange: function (event) {

    this.setData({
      marketResult: event.detail,
    })

  },
  /**
   * @desc 多选按钮 规格选中改变 --市场
   */
  marketSpaceChange(event) {
    let index = event.currentTarget.dataset.index,
      marketList = this.data.marketList,
      marketResult = this.data.marketResult;
    marketList[index].marketResult = event.detail;


    if (!event.detail.length && marketResult.includes(marketList[index].pkey + '')) {
      let marketIndex = -1;

      marketResult.map((item, subIndex) => {
        if (item == marketList[index].pkey + '') {
          marketIndex = subIndex;
        }
      })
      marketResult.splice(marketIndex, 1);
    } else if (event.detail.length && !marketResult.includes(marketList[index].pkey + '')) {
      marketResult.push(marketList[index].pkey + '')
    }
    let allMarket = true;
    marketList.map(item => {
      if (item.lines.length != item.marketResult.length) {
        allMarket = false;
      }
    })
    this.setData({
      marketList,
      marketResult,
      allMarket
    })
    this.calculateGoods();
  },
  /**
   * 多选按钮 改变事件 -- 积分商城
   */
  groupMallCheckChange: function (event) {
    this.setData({
      mallResult: event.detail,
    });
  },
  /**
   * @desc 多选按钮 规格选中改变 --积分商城
   */
  mallSpaceChange(event) {
    let index = event.currentTarget.dataset.index,
      mallList = this.data.mallList,
      mallResult = this.data.mallResult;
    mallList[index].mallResult = event.detail;


    if (!event.detail.length && mallResult.includes(mallList[index].pkey + '')) {
      let marketIndex = -1;
      mallResult.map((item, subIndex) => {
        if (item == mallList[index].pkey + '') {
          marketIndex = subIndex;
        }
      })
      mallResult.splice(marketIndex, 1);
    } else if (event.detail.length && !mallResult.includes(mallList[index].pkey + '')) {
      mallResult.push(mallList[index].pkey + '')
    }
    let allMall = true;
    mallList.map(item => {
      if (item.lines.length != item.mallResult.length) {
        allMall = false;
      }
    })
    this.setData({
      allMall,
      mallList,
      mallResult
    })
    this.calculateGoods();
  },


  /**
   * 多选按钮 改变事件 -- 民生豆
   */
  groupMsdCheckChange: function (event) {
    this.setData({
      msdResult: event.detail,
    });
  },
  /**
   * 多选按钮 改变事件 -- 京东
   */
  groupJdCheckChange: function (event) {
    this.setData({
      jdResult: event.detail,
    });
  },
  /**
   * @desc 多选按钮 规格选中改变 --民生豆
   */
  msdSpaceChange(event) {
    let index = event.currentTarget.dataset.index,
      msdList = this.data.msdList,
      msdResult = this.data.msdResult;
    msdList[index].msdResult = event.detail;


    if (!event.detail.length && msdResult.includes(msdList[index].pkey + '')) {
      let marketIndex = -1;
      msdResult.map((item, subIndex) => {
        if (item == msdList[index].pkey + '') {
          marketIndex = subIndex;
        }
      })
      msdResult.splice(marketIndex, 1);
    } else if (event.detail.length && !msdResult.includes(msdList[index].pkey + '')) {
        msdResult.push(msdList[index].pkey + '')
    }
    let allMsd = true;
    msdList.map(item => {
      if (item.lines.length != item.msdResult.length) {
        allMsd = false;
      }
    })
    this.setData({
      allMsd,
      msdList,
      msdResult
    })
    this.calculateGoods();
  },

  /**
   * 全选按钮 -- 市场
   */
  allMarketCheckChange: function (event) {
    var marketResult = [],
      marketList = this.data.marketList
    if (event.detail) {
      marketResult = marketList.map((item, index) => {
        marketList[index].marketResult = [];
        item.lines.map(subItem => {
          marketList[index].marketResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
    } else {
      marketList.map((item, index) => {
        marketList[index].marketResult = [];
      });
    }
    this.setData({
      allMarket: event.detail,
      marketList: marketList,
      marketResult: marketResult,
    });

    this.calculateGoods();
  },
  /**
   * 全选按钮 -- 积分商城
   */
  allMallCheckChange: function (event) {
    console.log(event);
    var mallResult = [],
      mallList = this.data.mallList
    if (event.detail) {
      mallResult = mallList.map((item, index) => {
        mallList[index].mallResult = [];
        item.lines.map(subItem => {
          mallList[index].mallResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
    } else {
      mallList.map((item, index) => {
        mallList[index].mallResult = [];
      });
    }
    this.setData({
      allMarket: event.detail,
      mallList: mallList,
      mallResult: mallResult,
    });

    this.calculateGoods();
  },
  /**
   * 全选按钮 -- 民生豆
   */
  allMsdCheckChange: function (event) {
    console.log(event);
    var msdResult = [],
      msdList = this.data.msdList
    if (event.detail) {
      msdResult = msdList.map((item, index) => {
        msdList[index].msdResult = [];
        item.lines.map(subItem => {
          msdList[index].msdResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
    } else {
      msdList.map((item, index) => {
        msdList[index].msdResult = [];
      });
    }
    this.setData({
      allMsd: event.detail,
      msdList: msdList,
      msdResult: msdResult,
    });

    this.calculateGoods();
  },
  /**
   * 全选按钮 -- 京东
   */
  allJdCheckChange: function (event) {
    console.log(event);
    var jdResult = [],
      jdList = this.data.jdList
    if (event.detail) {
      jdResult = jdList.map((item) => {
        item.jdResult = true
        return item.pkey + "";
      })
    } else {
        jdList.map((item) => {
            item.jdResult = false
        })
    }
    this.setData({
      allJd: event.detail,
      jdResult: jdResult,
      jdList
    });

    this.calculateGoods();
  },
  /**
   * 底部全选按钮 
   */
  allCheckChange: function (event) {
    var
      marketResult = [],
      mallResult = [],
      msdResult = [],
      jdResult = [],
      marketList = this.data.marketList,
      mallList = this.data.mallList,
      msdList = this.data.msdList,
      jdList = this.data.jdList;
    if (event.detail) {
      marketResult = marketList.map((item, index) => {
        marketList[index].marketResult = [];
        item.lines.map(subItem => {
          marketList[index].marketResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
      mallResult = mallList.map((item, index) => {
        mallList[index].mallResult = [];
        item.lines.map(subItem => {
          mallList[index].mallResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
      msdResult = msdList.map((item, index) => {
        msdList[index].msdResult = [];
        item.lines.map(subItem => {
            msdList[index].msdResult.push(subItem.pkey + '');
        });
        return item.pkey + "";
      });
      jdResult = jdList.map((item, index) => {
        jdList[index].jdResult = true;
        return item.pkey + "";
      });
    } else {
      marketList.map((item, index) => {
        marketList[index].marketResult = [];
      });
      mallList.map((item, index) => {
        mallList[index].mallResult = [];
      });
      msdList.map((item, index) => {
        msdList[index].msdResult = [];
      });
      jdList.map((item, index) => {
        jdList[index].jdResult = false;
      });
    }
    this.setData({
      allMarket: event.detail,
      allMall: event.detail,
      allMsd: event.detail,
      marketResult,
      mallResult,
      msdResult,
      jdResult,
      marketList,
      mallList,
      msdList,
      jdList
    });
    this.calculateGoods();
  },
  /**
   * 计算市场商品价格
   */
  calmarketList() {
    var marketList = this.data.marketList, //市场产品
      price = 0;
    if (marketList.length > 0) {
      marketList.map(item => {
        item.lines.map(subItem => {
          price += Number(subItem.price) * Number(subItem.num) * 100;
        })
      });
    }
    this.setData({
      allMoney: price / 100
    });
  },
  /**
   * 计算价格 及 市场全选按钮  积分商城全选按钮  底部全选按钮的显示
   */
  calculateGoods: function () {
    var marketList = this.data.marketList,
      mallList = this.data.mallList,
      msdList = this.data.msdList,
      jdList = this.data.jdList,
      allCheck = true,
      allMsd = true,
      allJd = true,
      allMall = true,
      allMarket = true,
      allCheckList = [];
    marketList.map(item => {
      if (item.marketResult.length) {
        item.marketResult.map(subItem => {
          allCheckList.push(subItem)
        })
      }
      if (item.lines.length != item.marketResult.length) {
        allCheck = false;
        allMarket = false;
      }

    })
    mallList.map(item => {
      if (item.mallResult.length) {
        item.mallResult.map(subItem => {
          allCheckList.push(subItem)
        })
      }
      if (item.lines.length != item.mallResult.length) {
        allMall = false;
        allCheck = false;
      }
    })
    msdList.map(item => {
        if (item.msdResult.length) {
          item.msdResult.map(subItem => {
            allCheckList.push(subItem)
          })
        }
        if (item.lines.length != item.msdResult.length) {
          allMsd = false;
          allCheck = false;
        }
      })
    jdList.map(item => {
        if (item.jdResult) {
            allCheckList.push(item.pkey)
        } else {
            allJd = false
            allCheck = false
        }
    })
    this.setData({
      allCheck,
      allMarket,
      allMall,
      allMsd,
      allJd,
      allCheckList
    });

    var price = 0;
    if (marketList.length > 0) {
      marketList.map(item => {
        item.lines.map(subItem => {
          if (item.marketResult && item.marketResult.includes(subItem.pkey + "")) {
            price += Number(this.data.userData.level=='PAID_MEMBER'?(subItem.priceMember||subItem.price):subItem.price) * Number(subItem.num) * 100;
          }
        })
      });
      this.setData({
        marketPrice: price
      })
    }

    if (mallList.length > 0) {
      mallList.map(item => {

        item.lines.map(subItem => {
          if (item.mallResult && item.mallResult.includes(subItem.pkey + "")) {
            price += Number(subItem.price) * Number(subItem.num) * 100;
          }
        })
      });
    }
    if (msdList.length > 0) {
        msdList.map(item => {
  
          item.lines.map(subItem => {
            if (item.msdResult && item.msdResult.includes(subItem.pkey + "")) {
              price += Number(subItem.price) * Number(subItem.num) * 100;
            }
          })
        });
      }
    if (jdList.length > 0) {
        jdList.map(item => {
            if (item.jdResult) {
                price += Number(item.salePrice) * Number(item.num) * 100;
            }
        });
    }
    console.log(price)
    this.setData({
      allPrice: price
    });
    console.log('选中的商品', this.data.allCheckList)
  },
  onSubmit: function () {
    console.log(this.data.allCheckList, this.data.jdResult, this.data.msdResult)
    if (this.data.allCheckList.length == 0) {
      wx.showToast({
        title: '请选择商品',
        icon: "none"
      });
      return;
    }
    if ((this.data.jdResult.length && this.data.msdResult.length) || ((this.data.jdResult.length || this.data.msdResult.length) && (this.data.mallResult.length || this.data.marketResult.length))) {
        wx.showToast({
          title: '不允许合并下单！',
          icon: "none"
        });
        return;
    }
    if (this.data.msdResult.length) {
        this.comboPaySettle('INTEGRAL_MSD_GOODS');//民生豆组合支付
        return;
    }
    if (this.data.jdResult.length) {
        this.comboPaySettle('JD_GOODS');//京东商品支付
        return;
    }
    // if (this.data.startingPrice && this.data.mallResult.length && this.data.marketResult.length) {
    if (this.data.mallResult.length) {
      this.comboPaySettle()
      // wx.showToast({
      //   title: '当前市场商品不能与积分商品同时结算！',
      //   icon: "none"
      // });
      return;
    }

    var that = this,
      url = "/v2/app/market/lm/order/buyGwc",
      params = {
        gwcs: this.data.allCheckList,
        dineIn: app.globalData.qrCode ? true : false,
        latitude: wx.getStorageSync('latitude'),
        longitude: wx.getStorageSync('longitude'),
      };

    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: res => {
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {

          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          wx.navigateTo({
            url: '/pages/pay/pay/index?type=gwc&gwcs='+ this.data.allCheckList
          })

        } else {
          Toast(res.data.msg);
        }
      }
    })
  },
  /**
   * 市场商品和积分商品组合购物车结算
   */
  comboPaySettle(type) {
    let url = type == 'JD_GOODS' ? '/v1/app/jd/order/buyGwc' : "/v3/app/market/lm/order/buyGwc"
    let params = {
          gwcs: this.data.allCheckList,
          dineIn: app.globalData.qrCode ? true : false,
          latitude: wx.getStorageSync('latitude'),
          longitude: wx.getStorageSync('longitude'),
        };
    http.request({
      method: "POST",
      url: app.globalData.ajax_url + url,
      data: params,
      header: {
        'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
        "openid": app.globalData.openid,
        "farmer": app.globalData.location.pkey
      },
      success: (res) => {
        console.log(res);
        if (res.data.code == "999") {
          that.setData({
            iShidden: false
          })
          return;
        }
        if (res.data.success) {

          wx.setStorageSync('orderInfo', JSON.stringify(res.data.result));
          if(type){
            wx.navigateTo({
                url: '/pages/pay/comboPay/index?type=gwc&gwcs='+ this.data.allCheckList + '&mtype=' + type
              })
          }else{
            wx.navigateTo({
                url: '/pages/pay/comboPay/index?type=gwc&gwcs='+ this.data.allCheckList
              })
          }
        } else {
          Toast(res.data.msg);
        }
      }
    })
  },

  /**
   * 跳转到商品详情页
   */
  goGoods: function (data) {
    var pkey = data.currentTarget.dataset.pkey;
    var spacePkey = data.currentTarget.dataset.space || '';
    wx.navigateTo({
      url: '/pages/shouyeGroup/goodsDeatil/index?pkey=' + pkey + '&space=' + spacePkey,
    })
  },
  /**
   * 跳转到京东商品详情页
   */
  goJdGoods: function (data) {
    var skuid = data.currentTarget.dataset.skuid;
    wx.navigateTo({
      url: '/pages/shouyeGroup/jdGoodsDetail/index?pkey=' + skuid,
    })
  },
  /**
   * 跳转到凑单页
   */
  gocollectBill() {
    // wx.navigateTo({
    //   url: '/pages/my/collectBill/collectBill'
    // })
    wx.switchTab({
        url: '/pages/home/classification/index',
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.setData({
      iShidden: true,
      marketList: [], //市场商品列表
      mallList: [], //积分商场商品列表
      msdList: [], //民生豆商品列表
      jdList: [], //京东商品列表
      marketResult: [], //市场产品选中项
      mallResult: [], //积分商城产品选中项
      msdResult: [], //民生豆产品选中项
      jdResult: [], //京东产品选中项
      allMarket: false, //是否全选市场商品
      allMall: false, //是否全选积分商城商品
      allMsd: false, //是否全选民生豆商品
      allJd: false, //是否全选京东商品
      allCheckList: [], //存全局 选中商品pkey
      allCheck: false, //全部选中/不选
      loading: false, //按钮载入效果
      disabled: true, //按钮禁用效果
      marketPrice: 0, //市场选中商品 价格和
      mallPrice: 0, //积分商城选中商品 价格和
      msdPrice: 0, //民生豆选中商品 价格和
      jdPrice: 0, //京东选中商品 价格和
      allPrice: 0.00, //总价
      total: 0,
      marketName: app.globalData.location.name,
      name: app.globalData.location.name,
    })

    app.getBuycarNum();
    this.getData();
    this.getMemeberData();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    // wx.showTabBar();
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    recommendGoods.bindscrollbottom()
  },
  //这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
  //函数里面执行
  onLoadFun: function () {
    this.getData();
  }
})
