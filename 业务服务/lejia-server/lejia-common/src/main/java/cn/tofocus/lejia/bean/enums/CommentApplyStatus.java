package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 评论审核状态
 * @author czy
 * @version [版本号, 2025/7/29]
 */
public enum CommentApplyStatus implements IBaseDbEnum
{
    // @formatter:off
    NOT_APPLY(1, "未审核"),
    APPLY(2, "已审核"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CommentApplyStatus(int index, String name)
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
    
    public static CommentApplyStatus fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CommentApplyStatus.class, index);
    }
}
