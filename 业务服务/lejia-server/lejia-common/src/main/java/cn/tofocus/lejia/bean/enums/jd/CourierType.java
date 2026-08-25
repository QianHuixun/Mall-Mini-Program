package cn.tofocus.lejia.bean.enums.jd;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CourierType implements IBaseDbEnum 
{
    JD_DOOR_TO_DOOR_PICKUP(1, "京东上门取件"),
    SELF_MAILING(2, "自行寄出"),
    ;

    private final int index;

    private final String name;

    private CourierType(int index, String name) {
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

    public static CourierType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(CourierType.class, index);
    }
}
