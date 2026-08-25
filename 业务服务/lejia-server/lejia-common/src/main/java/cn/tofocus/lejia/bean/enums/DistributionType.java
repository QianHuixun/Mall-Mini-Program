package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

import java.util.ArrayList;
import java.util.List;

public enum DistributionType implements IBaseDbEnum 
{
    
    PICKUP(0, "自提"), 
    IMMEDIATELY(1, "立刻配送"),
    ORDERED(2,"预约"),
    EXCHANGE(3,"自行兑换"),
    SEND_DIRECTLY(4,"直接送达"),
    DINE_IN(5, "堂食"),
    ;


    private final int index;

    private final String name;

    private DistributionType(int index, String name) {
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

    public static DistributionType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(DistributionType.class, index);
    }

    public static List<DistributionType> delivery()
    {
        List<DistributionType> list = new ArrayList<>();
        list.add(IMMEDIATELY);
        list.add(ORDERED);
        return list;
    }
}
