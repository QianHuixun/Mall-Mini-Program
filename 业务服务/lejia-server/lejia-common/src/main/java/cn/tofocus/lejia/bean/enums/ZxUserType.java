package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxUserType implements IBaseDbEnum 
{
    SYSTEM(1, "集团"),
    MARKET(2, "民营市场"),
    VENDOR(3, "商户"),
    TRADE_UNION(4, "工会"),
    SELF_MARKET(5, "自营市场"),
    ;

    private final int index;

    private final String name;

    private ZxUserType(int index, String name) {
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

    public static ZxUserType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ZxUserType.class, index);
    }
}
