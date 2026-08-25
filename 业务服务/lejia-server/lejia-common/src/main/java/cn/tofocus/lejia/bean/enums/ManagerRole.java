package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 管理员角色表
 * @author czy
 * @version [版本号, 2024/8/12]
 */
public enum ManagerRole implements IBaseDbEnum
{
    // @formatter:off
    COUPON_MANAGER(1, "卡券管理"),
    ORDER_WRITE_OFF(2, "订单核销"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    ManagerRole(int index, String name)
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
    
    public static ManagerRole fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(ManagerRole.class, index);
    }
}
