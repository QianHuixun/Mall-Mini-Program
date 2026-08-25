package cn.tofocus.lejia.bean.enums.jd;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum OrderCorrelationStatus implements IBaseDbEnum
{
    NORMAL_ORDER(1, "正常订单"),
    REVOKE_ORDER(2, "作废订单"),
    ;

    private final int index;

    private final String name;

    private OrderCorrelationStatus(int index, String name) {
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

    public static OrderCorrelationStatus fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(OrderCorrelationStatus.class, index);
    }
}
