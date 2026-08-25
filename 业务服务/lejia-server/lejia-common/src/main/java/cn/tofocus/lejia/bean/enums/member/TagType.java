package cn.tofocus.lejia.bean.enums.member;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 标签类型
 * @author czy
 * @version [版本号, 2026/5/27]
 */
public enum TagType implements IBaseDbEnum
{
    // @formatter:off
    NORMAL(1, "普通标签"),
    MSD(2, "热力豆标签"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    TagType(int index, String name)
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
    
    public static TagType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(TagType.class, index);
    }
}
