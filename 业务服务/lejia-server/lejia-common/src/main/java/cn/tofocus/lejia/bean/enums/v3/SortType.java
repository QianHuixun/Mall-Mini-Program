package cn.tofocus.lejia.bean.enums.v3;

import cn.tofocus.core.enums.IBaseDbEnum;
import cn.tofocus.lejia.bean.enums.MType;

public enum SortType implements IBaseDbEnum 
{
    PAGEVIEWS_SORT(0, "浏览量"), 
    SALES_SORT(1, "销量"),
    ORIGINAL_PRICE_SORT(2, "原价"),
    CURRENT_PRICE_SORT(3, "现价"),
    STOCK_SORT(4, "库存"),
    MEMBER_SORT(5, "会员价"),
    COMMISSION_SORT(6, "佣金"),
    INTEGRAL_SORT(7, "积分"),
    EXCHANGE_VALIDITY_SORT(8, "兑换有效期"),
    ADDED_TIME_SORT(9, "上架时间"),
    FACE_VALUE_SORT(10, "面值金额"),
    ;

    private final int index;

    private final String name;

    private SortType(int index, String name) {
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

    public static MType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(MType.class, index);
    }
}
