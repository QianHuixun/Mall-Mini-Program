package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 京东操作类型
 */
public enum JdOperationType implements IBaseDbEnum
{
    // @formatter:off
    CONSUME(1, "消费"),
    CLEAR(2, "清空"),
    RECHARGE(3, "充值"),
    MANUAL_ADJUST(4, "手动调整"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    JdOperationType(int index, String name)
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
    
    public static JdOperationType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(JdOperationType.class, index);
    }
}
