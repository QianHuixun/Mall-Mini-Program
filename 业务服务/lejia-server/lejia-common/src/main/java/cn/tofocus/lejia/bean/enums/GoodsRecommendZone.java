package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 商品推荐区域
 * @author czy
 * @version [版本号, 2025/7/8]
 */
public enum GoodsRecommendZone implements IBaseDbEnum
{
    // @formatter:off
    SPECIAL(1, "限时秒杀"),
    CATEGORY(2, "分类"),
    GOODS_DETAIL(3, "商品详情"),
    GWC(4, "购物车"),
    MINE(5, "我的"),
    HOME_PAGE(6, "首页"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    GoodsRecommendZone(int index, String name)
    {
        this.name = name;
        this.index = index;
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public int getIndex()
    {
        return index;
    }
    
    public static GoodsRecommendZone fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(GoodsRecommendZone.class, index);
    }
}
