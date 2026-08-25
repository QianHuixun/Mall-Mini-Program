function applyTheme (pageInstance) {
    const app = getApp()
    const theme = app.globalData.theme
    wx.setNavigationBarColor({
      backgroundColor: app.globalData.themeColors[theme].primary,
    })
    pageInstance.setData({
        theme: app.globalData.themeColors[theme]
    })
    /**
     * 设置tabbar激活标签
     */
    wx.setTabBarItem({
        index: 0,
        selectedIconPath: `/images/tabbar/shouyeSelect_${theme}.png`
    })
    wx.setTabBarItem({
        index: 1,
        selectedIconPath: `/images/tabbar/fenleiSelect_${theme}.png`
    })
    wx.setTabBarItem({
        index: 2,
        selectedIconPath: `/images/tabbar/memberBenefitsSelect_${theme}.png`
    })
    wx.setTabBarItem({
        index: 3,
        selectedIconPath: `/images/tabbar/carSelect_${theme}.png`
    })
    wx.setTabBarItem({
        index: 4,
        selectedIconPath: `/images/tabbar/mySelect_${theme}.png`
    })
}

module.exports = {
    applyTheme
}