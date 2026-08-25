const app = getApp()
function request(request) {
  const defaultheader = {
    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
    // "appid": app.globalData.appid,
    "ascription": app.globalData.ascription,
    "openid": app.globalData.openid,
    "farmer": app.globalData.location.pkey,
    "qrCode": app.globalData.qrCode,
    "source": app.globalData.source
}
    const header = {
        ...defaultheader,
        ...request.header
    }

    wx.request({
        method: request.method,
        url: request.url,
        data: request.data,
        header: header,
        success: request.success
    })
}
module.exports = {
    request
}