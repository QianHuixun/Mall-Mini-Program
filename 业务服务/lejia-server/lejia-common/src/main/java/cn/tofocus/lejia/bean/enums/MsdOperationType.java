package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 民生豆操作类型
 * @author czy
 * @version [版本号, 2025/8/20]
 */
public enum MsdOperationType implements IBaseDbEnum
{
    // @formatter:off
    CONSUME(1, "消费"),
    CLEAR(2, "清空"),
    RECHARGE(3, "充值"),
    MANUAL_ADJUST(4, "手动调整"),
    REFUND(5, "退款"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    MsdOperationType(int index, String name)
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
    
    public static MsdOperationType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(MsdOperationType.class, index);
    }
}
