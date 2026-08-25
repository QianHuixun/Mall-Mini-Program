package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 标签可见对象
 * @author czy
 * @version [版本号, 2024/8/12]
 */
public enum TagVisibleTargetType implements IBaseDbEnum
{
    // @formatter:off
    CARD(1, "优惠券"),
    ACTIVITY(2, "卡券活动"),
    SPECIAL_GOODS(3, "特价商品"),
    INTEGRAL_MSD_GOODS(15, "民生商品"),
    JD_GOODS(16, "京东商品"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    TagVisibleTargetType(int index, String name)
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
    
    public static TagVisibleTargetType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(TagVisibleTargetType.class, index);
    }
}
