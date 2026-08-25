package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 活动分发方式
 * @author czy
 * @version [版本号, 2024/5/28]
 */
public enum ActivityDistributeType implements IBaseDbEnum
{
    // @formatter:off
    QRCode(1, "二维码分发"),
    WeChatGroup(2, "微信群分发"),
    memberWelfare(3, "会员福利"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    ActivityDistributeType(int index, String name)
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
    
    public static ActivityDistributeType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(ActivityDistributeType.class, index);
    }
}
