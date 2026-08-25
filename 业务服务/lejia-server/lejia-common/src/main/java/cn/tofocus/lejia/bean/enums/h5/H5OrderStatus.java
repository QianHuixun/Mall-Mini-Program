package cn.tofocus.lejia.bean.enums.h5;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum H5OrderStatus implements IBaseDbEnum
{
    UNPAID_ORDER(0, "未付款"), 
    NOTUSED_ORDER(1, "未使用"),
    USED_ORDER(2, "已使用"),
    VOID_ORDER(99, "作废")
    ;
    
    private final int index;
    
    private final String name;
    
    private H5OrderStatus(int index, String name)
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
    
    public static H5OrderStatus fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(H5OrderStatus.class, index);
    }
}
