package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 商户小程序登录角色
 * @author czy
 * @version [版本号, 2024/12/13]
 */
public enum AppVendorLoginRole implements IBaseDbEnum
{
    // @formatter:off
    VENDOR(1, "商户"),
    VENDOR_STAFF(2, "商户店员"),
    SUPPLIER(3, "供应商"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    AppVendorLoginRole(int index, String name)
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
    
    public static AppVendorLoginRole fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(AppVendorLoginRole.class, index);
    }
}
