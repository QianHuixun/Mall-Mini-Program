package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * @author czy
 * @version [版本号, 2024/4/17]
 */
public enum CouponExpireChoose implements IBaseDbEnum
{
    // @formatter:off
    LONG_TERM(0, "长期有效"),
    DATE_RANGE(1, "指定日期"),
    DAYS(1, "指定天数"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    CouponExpireChoose(int index, String name)
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
    
    public static CouponExpireChoose fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(CouponExpireChoose.class, index);
    }
}
