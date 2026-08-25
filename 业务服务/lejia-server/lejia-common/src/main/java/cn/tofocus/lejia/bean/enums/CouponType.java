package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 卡券类型
 * @author czy
 * @version [版本号, 2024/4/26]
 */
public enum CouponType implements IBaseDbEnum
{
    // @formatter:off
    CARD(1, "优惠券"),
    GIFT(2, "礼品券"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CouponType(int index, String name)
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
    
    public static CouponType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CouponType.class, index);
    }
}
