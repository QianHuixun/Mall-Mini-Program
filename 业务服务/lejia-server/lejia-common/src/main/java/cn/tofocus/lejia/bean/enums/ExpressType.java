package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ExpressType implements IBaseDbEnum 
{
    
    COURIER(0, "跑腿"), 
    WANLI(1, "第三方派送"),
    EXPRESS_SF(2, "顺丰快递"),
    ;


    private final int index;

    private final String name;

    private ExpressType(int index, String name) {
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

    public static ExpressType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ExpressType.class, index);
    }
}

