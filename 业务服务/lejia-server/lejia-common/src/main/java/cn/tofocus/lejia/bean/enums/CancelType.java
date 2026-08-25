package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CancelType implements IBaseDbEnum
{
    PERSON(1, "个人原因"),
    COURIERRECHNG(2, "骑手配送不及时"),
    COURIERNG(3, "骑手无法配送"),
    COURIERGETNG(4, "骑手取货不及时"),
    OTHER(20, "其他")
    ;
    
    private final int index;

    private final String name;

    private CancelType(int index, String name) {
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

    public static CancelType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(CancelType.class, index);
    }
}
