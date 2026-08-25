package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 评论回复状态
 * @author czy
 * @version [版本号, 2025/7/29]
 */
public enum CommentReplyStatus implements IBaseDbEnum
{
    // @formatter:off
    NOT_REPLIED(1, "未回复"),
    REPLIED(2, "已回复"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CommentReplyStatus(int index, String name)
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
    
    public static CommentReplyStatus fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CommentReplyStatus.class, index);
    }
}
