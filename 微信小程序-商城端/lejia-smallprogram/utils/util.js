const app = getApp()

const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

const formatTel = tel => {
  if (!(/^1[3456789]\d{9}$/.test(tel))) {
    return false;
  } else {
    return true;
  }
}
/**
 * 时间戳转化为年 月 日 时 分 秒
 * number: 传入时间戳
 * format：返回格式，支持自定义，但参数必须与formateArr里保持一致
 */
const formatTimeInArr = (number, format) => {
  var formateArr = ['Y', 'M', 'D', 'h', 'm', 's'];
  var returnArr = [];
  let date = new Date(number * 1000);
  const formatNumber = n => {
    n = n.toString();
    return n[1] ? n : '0' + n;
  }
  returnArr.push(date.getFullYear());
  returnArr.push(formatNumber(date.getMonth() + 1));
  returnArr.push(formatNumber(date.getDate()));
  returnArr.push(formatNumber(date.getHours()));
  returnArr.push(formatNumber(date.getMinutes()));
  returnArr.push(formatNumber(date.getSeconds()));
  for (var i in returnArr) {
    format = format.replace(formateArr[i], returnArr[i]);
  }
  return format
}
/**
 * 获取今天星期
 * date: 传入需要获取星期的时间，默认是星期
 * type: 传入需要返回的格式，默认是星期
 */
const getWeekDate = (date, type = '星期') => {
  var now = date ? new Date(date) : new Date();
  var day = now.getDay();
  var weeks = new Array("日", "一", "二", "三", "四", "五", "六");
  var week = `${type}${weeks[day]}`;
  return week;
}

const  getQueryString = (string, name) => {
  var search = string.split('?')[1]
  console.log(search);
  var  reg = new  RegExp( "(^|&)" + name + "=([^&]*)(&|$)" );
  console.log(search)
  var  r = search.match(reg);
  if (r!= null ) return   unescape(r[2]); return  null ;
}

const onClickEffect = (event) => {
    let page = event.currentTarget.dataset.url,
        urlType = event.currentTarget.dataset.urltype,
        jump = event.currentTarget.dataset.jump;
    switch (urlType) {
        case 'POINTS_MALL': // 积分商城
            wx.navigateTo({
                url: `/pages/home/integral/index`,
            })
            break;
        case 'GOODS': // 商品
            wx.navigateTo({
                url: `/pages/shouyeGroup/goodsDeatil/index?pkey=${page}`,
            })
            break;
        case 'PERSONAL_CENTER': // 个人中心
            wx.switchTab({
                url: `/pages/home/my/index`,
            })
            break;
        case 'SPECIAL_GOODS': // 特价专区
            wx.navigateTo({
                url: `/pages/shouyeGroup/presell/index`,
            })
            break;
        case 'PRESALE_GOODS': // 预售专区
            wx.navigateTo({
                url: `/pages/shouyeGroup/openBook/index`,
            })
            break;
        case 'COOKFD_GOODS': // 菜谱专区
            wx.navigateTo({
                url: `/pages/shouyeGroup/recipe/index`,
            })
            break;
        case 'CARD_CENTER': // 领券中心
            wx.navigateTo({
                url: `/pages/my/coupon/coupon`,
            })
            break;
        case 'GTYPE': // 分类
            page = page.split(',');
            if (page[0])
                wx.setStorageSync('classiftyPkey', page[0])
            if (page[1]) {
                wx.setStorageSync('classiftyPkeyTwo', page[1])
            }
            wx.switchTab({
                url: '/pages/home/classification/index',
            })
            break;
        case 'ACTIVITY': // 卡券活动
            wx.navigateTo({
                url: `/pages/activity/coupon/index?pkey=${page}`,
            })
            break;
        case 'VENDOR': // 商户
            wx.navigateTo({
                url: `/pages/shouyeGroup/merchant/index?pkey=${page}`,
            })
            break;
        case 'WEIXIN_MINI_PROGRAM': // 小程序页面
            wx.navigateTo({
                url: page,
            })
            break;
        case 'BNYP_GOODS': // 滨农优品
            wx.navigateTo({
                url: `/pages/home/bnyp/index`,
            })
            break;
        case 'MS_GOODS': // 民生专区
            const url = app.globalData.ascription == 22 || app.globalData.ascription == 13 ? '/pages/home/newMsd/index?mtype=INTEGRAL_MSD_GOODS' : '/pages/home/msd/index'
            wx.navigateTo({
                url,
            })
            break;
        case 'JD_GOODS': // 京东专区
            wx.navigateTo({
                url: `/pages/home/jdGoods/index`,
            })
            break;
        case 'OFFLINE_STORE': // 线下门店
            wx.navigateTo({
                url: `/pages/shouyeGroup/position/index`,
            })
            break;
        default:
            wx.navigateTo({
                url: page,
                fail(res) {
                    wx.switchTab({
                        url: page,
                    })
                }
            })
            break;
    }
}

module.exports = {
  formatTime: formatTime,
  formatTel: formatTel,
  formatTimeInArr: formatTimeInArr,
  getWeekDate,
  getQueryString,
  onClickEffect
}