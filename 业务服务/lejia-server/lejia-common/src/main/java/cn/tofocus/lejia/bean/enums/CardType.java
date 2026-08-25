package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CardType implements IBaseDbEnum
{
    // @formatter:off
    MANUALLY_ISSUE(0, "手动发放"),
    //	SELF_LEADING_QR_CODE(1, "二维码自领"),
    //	ALL(2, "所有"),
    CARD_CENTER(1, "领券中心领取"), 
    INTEGRAL_BUY(2, "积分商城购买"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CardType(int index, String name)
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
    
    public static CardType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CardType.class, index);
    }
}
