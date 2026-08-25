package cn.tofocus.lejia.bean.enums.vendor;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum WithdrawalStatus implements IBaseDbEnum
{
    NO_PAYMENT(1, "未打款"),
    PAYMENT(2, "已打款"),
    ;

    private final int index;

    private final String name;

    private WithdrawalStatus(int index, String name) {
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

    public static WithdrawalStatus fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(WithdrawalStatus.class, index);
    }
}