package cn.tofocus.lejia.bean.enums.v3;

import cn.tofocus.core.enums.IBaseDbEnum;
import cn.tofocus.lejia.bean.enums.MType;

public enum SettleSortType implements IBaseDbEnum 
{
    ORDERCOUNT_SORT(0, "总采购笔数"), 
    ORDERAMT_SORT(1, "总采购金额"),
    COMMISSION_SORT(2, "佣金费率"),
    ORDERCOMMISSION_SORT(3, "总交易佣金"),
    AMT(4, "结算总金额"),
    ;

    private final int index;

    private final String name;

    private SettleSortType(int index, String name) {
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

    public static MType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(MType.class, index);
    }
}
