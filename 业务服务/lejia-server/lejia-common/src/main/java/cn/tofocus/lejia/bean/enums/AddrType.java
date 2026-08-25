package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * @author czy
 * @version [版本号, 2024/4/12]
 */
public enum AddrType implements IBaseDbEnum
{
    // @formatter:off
    DELIVERY(1, "配送"),
    PICKUP(2, "自提"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    AddrType(int index, String name)
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
    
    public static AddrType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(AddrType.class, index);
    }
    
    public static AddrType getDefault()
    {
        return DELIVERY;
    }
}
