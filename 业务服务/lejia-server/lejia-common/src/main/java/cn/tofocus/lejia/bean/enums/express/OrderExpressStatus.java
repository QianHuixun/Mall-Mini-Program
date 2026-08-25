package cn.tofocus.lejia.bean.enums.express;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * @author czy
 * @version [版本号, 2024/12/6]
 */
public enum OrderExpressStatus implements IBaseDbEnum
{
    // @formatter:off
    CANCELED(-1, "已取消"),
    ERROR(0, "订单异常"),
    ORDERED(1, "已下单"),
    IN_TRANSIT(2, "运输中"),
    OUT_FOR_DELIVERY(3, "正在派送中"),
    RECEIVED(4, "已签收"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    OrderExpressStatus(int index, String name)
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
    
    public static OrderExpressStatus fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(OrderExpressStatus.class, index);
    }
}
