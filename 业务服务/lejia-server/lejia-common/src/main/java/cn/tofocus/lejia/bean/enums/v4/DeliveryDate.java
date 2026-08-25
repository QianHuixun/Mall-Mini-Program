package cn.tofocus.lejia.bean.enums.v4;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum DeliveryDate implements IBaseDbEnum
{

    TODAY(0, "今天"),
    TOMORROW(1, "明天"),
    AFTER_TOMORROW(2, "后天"),
    ;

    private final int index;

    private final String name;

    private DeliveryDate(int index, String name) {
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

    public static DeliveryDate fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(DeliveryDate.class, index);
    }
}


