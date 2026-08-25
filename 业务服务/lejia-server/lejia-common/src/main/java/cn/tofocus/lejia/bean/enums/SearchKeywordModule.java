package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 搜索关键词所属模块
 * @author czy
 * @version [版本号, 2025/7/1]
 */
public enum SearchKeywordModule implements IBaseDbEnum
{
    // @formatter:off
    HOME(1, "首页"),
    CATEGORY(2, "分类"),
    SELF_MALL(3, "滨海自营"),
    BNYP(4, "滨农优品"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    SearchKeywordModule(int index, String name)
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
    
    public static SearchKeywordModule fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(SearchKeywordModule.class, index);
    }
}
