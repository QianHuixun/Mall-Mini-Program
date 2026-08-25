package cn.tofocus.lejia.bean.enums.v5;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum FarmerType implements IBaseDbEnum
{

    MARKET_SHOPPING_MALL(1, "市场商城"),
    VENDOR_SHOPPING_MALL(2, "商户商城"),
    ;

    private final int index;

    private final String name;

    private FarmerType(int index, String name) {
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

    public static FarmerType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(FarmerType.class, index);
    }
}