package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum GoodsSortType implements IBaseDbEnum
{
    SALED(0, "销量"),
    PRICE(1, "价格");

    private final int index;
    
    private final String name;
    
    private GoodsSortType(int index, String name)
    {
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

}
