package cn.tofocus.lejia.bean.enums.jd;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ReturnExchange implements IBaseDbEnum 
{
    RETURN_GOODS(1, "退货"),
    EXCHANGE(2, "换货"),
    ;

    private final int index;

    private final String name;

    private ReturnExchange(int index, String name) {
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

    public static ReturnExchange fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ReturnExchange.class, index);
    }
}
