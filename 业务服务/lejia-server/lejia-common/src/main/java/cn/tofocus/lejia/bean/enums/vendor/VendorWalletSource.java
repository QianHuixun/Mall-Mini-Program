package cn.tofocus.lejia.bean.enums.vendor;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum VendorWalletSource implements IBaseDbEnum
{

    CONSUME(1, "消费"),
    WITHDRAWAL(2, "提现"),
    REVOKE(3, "撤销")
    ;

    private final int index;

    private final String name;

    private VendorWalletSource(int index, String name) {
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

    public static VendorWalletSource fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(VendorWalletSource.class, index);
    }
}