package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 优惠券使用订单类型
 * @author czy
 * @version [版本号, 2024/5/10]
 */
public enum CardUserOrderType implements IBaseDbEnum
{
    // @formatter:off
    DELIVERY(1, "配送订单"),
    PICKUP(2, "自提订单"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CardUserOrderType(int index, String name)
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
    
    public static CardUserOrderType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CardUserOrderType.class, index);
    }
}
