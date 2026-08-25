package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ConfigGoodsType implements IBaseDbEnum
{
    VENDOR_RECOMMEND(0, "商户推荐"),
    SINGLE_GOODS_RECOMMEND(1, "单个商品推荐"),
    ;

    private final int index;

    private final String name;

    private ConfigGoodsType(int index, String name) {
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

    public static ConfigGoodsType fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ConfigGoodsType.class, index);
    }
}
