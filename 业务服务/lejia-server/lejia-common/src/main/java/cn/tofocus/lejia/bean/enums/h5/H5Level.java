package cn.tofocus.lejia.bean.enums.h5;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum H5Level implements IBaseDbEnum 
{
    PREDETERMINE(1, "可预定"), 
    NOT_PREDETERMINE(2, "不可预定"),
    INVISIBLE(3, "不可见"),
    ;
    
    private final int index;

    private final String name;

    private H5Level(int index, String name) {
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

    public static H5Level fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(H5Level.class, index);
    }
}
