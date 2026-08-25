package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 用户可见范围
 * @author czy
 * @version [版本号, 2024/8/12]
 */
public enum MemberVisibleRange implements IBaseDbEnum
{
    // @formatter:off
    ALL(1, "全部用户"),
    TAG(2, "指定标签"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    MemberVisibleRange(int index, String name)
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
    
    public static MemberVisibleRange fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(MemberVisibleRange.class, index);
    }
}
