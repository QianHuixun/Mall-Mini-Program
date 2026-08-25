package cn.tofocus.lejia.bean.enums.jd;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 商品变更类型
 * @author czy
 * @version [版本号, 2026/4/7]
 */
public enum JdGoodsUpdType implements IBaseDbEnum
{
    // @formatter:off
    PRICE(1, "商品价格变更"),
    SALE_STATE(2, "商品可售性变更"),
    LOWEST_BUY(3, "最低起购量"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    JdGoodsUpdType(int index, String name)
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
    
    public static JdGoodsUpdType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(JdGoodsUpdType.class, index);
    }
}
