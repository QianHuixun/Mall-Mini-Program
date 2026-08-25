package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CommissionType implements IBaseDbEnum
{
    BLOC(0, "集团"),
    MARKET(1, "市场"),
    MERCHANT(2, "商户"),
    ;

    private final int index;

    private final String name;

    private CommissionType(int index, String name) {
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

    public static CommissionType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(CommissionType.class, index);
    }
}
