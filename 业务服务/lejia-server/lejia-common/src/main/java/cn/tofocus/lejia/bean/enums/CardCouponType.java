package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CardCouponType implements IBaseDbEnum
{
    GOODS_COUPON(0, "满减券"),
    POSTAGE_COUPON(1, "配送券"),
    ;
    
    private final int index;
    
    private final String name;
    
    CardCouponType(int index, String name)
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
    
    public static CardCouponType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CardCouponType.class, index);
    }
}
