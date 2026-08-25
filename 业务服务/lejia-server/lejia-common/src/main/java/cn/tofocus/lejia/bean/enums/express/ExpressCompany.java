package cn.tofocus.lejia.bean.enums.express;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 快递公司
 * @author czy
 * @version [版本号, 2024/12/3]
 */
public enum ExpressCompany implements IBaseDbEnum
{
    // @formatter:off
    OTHER(1, "其他"),
    SF(2, "顺丰快递"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    ExpressCompany(int index, String name)
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
    
    public static ExpressCompany fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(ExpressCompany.class, index);
    }
}
