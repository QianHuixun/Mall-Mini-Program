package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * @author czy
 * @version [版本号, 2024/4/16]
 */
public enum GiftType implements IBaseDbEnum
{
    // @formatter:off
    NORMAL(0, "普通优惠券"),
    INTEGRAL_BUY(1, "积分商城购买"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    GiftType(int index, String name)
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
    
    public static GiftType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(GiftType.class, index);
    }
}
