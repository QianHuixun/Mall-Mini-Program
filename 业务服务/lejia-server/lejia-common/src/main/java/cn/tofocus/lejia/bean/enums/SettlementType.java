package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum SettlementType implements IBaseDbEnum
{
    // @formatter:off
    AWAIT_CONFIRM(0, "待确认"),
    NOT_START(1, "未结算"),
    DOING(2, "结算中"),
    FAIL(3, "结算异常"),
    SUCCESS(4, "已结算"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    private SettlementType(int index, String name)
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
    
    public static SettlementType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(SettlementType.class, index);
    }
}
