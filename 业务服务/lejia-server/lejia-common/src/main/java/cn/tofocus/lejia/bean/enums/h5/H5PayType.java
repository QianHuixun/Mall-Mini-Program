package cn.tofocus.lejia.bean.enums.h5;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum H5PayType implements IBaseDbEnum 
{
    ORDER_WEIXIN(1, "微信"),
    ORDER_ELECTRONIC_ACCOUNT(2, "电子账户"),
    ;
    
    private final int index;

    private final String name;

    private H5PayType(int index, String name) {
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

    public static H5PayType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(H5PayType.class, index);
    }
}
