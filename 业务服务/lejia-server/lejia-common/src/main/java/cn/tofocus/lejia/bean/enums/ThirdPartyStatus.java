package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ThirdPartyStatus implements IBaseDbEnum 
{
    // 1-初始化 20-待接单、30取货中、40-配送中、50-已完成、60- 已取消、70- 配送异常 Integer(3)
    THIRD_PARTY_INIT(1, "初始化"),
    THIRD_PARTY_PENDING(2, "待接单"),
    THIRD_PARTY_PICKING_UP(3, "取货中"),
    THIRD_PARTY_DELIVERY(4, "配送中"),
    THIRD_PARTY_CONFIRM(5, "已完成"),
    THIRD_PARTY_VOID(6, "已取消"),
    THIRD_PARTY_ERROR(7, "配送异常"),
    
    ;
    
    private final int index;

    private final String name;

    private ThirdPartyStatus(int index, String name) {
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

    public static ThirdPartyStatus fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ThirdPartyStatus.class, index);
    }
}
