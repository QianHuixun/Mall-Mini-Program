package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxBillType implements IBaseDbEnum 
{
    INCOME(1, "收入"), 
    ALLOCATION(2, "调拨"), 
    WITHDRAWAL(3, "提现"),

    ;

    private final int index;

    private final String name;

    private ZxBillType(int index, String name) {
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

    public static ZxBillType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ZxBillType.class, index);
    }
}