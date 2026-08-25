package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxWithdrawStatus implements IBaseDbEnum 
{
    NOT_MAKE_PAYMENT(1, "未提现"), 
    MAKE_PAYMENT(2, "提现"), 
    MAKE_PAYMENT_FAIL(3, "提现失败"),
    MANUAL_MAKE_PAYMENT(4, "手工提现"),
    OFFLINE_RECHARGE(5, "线下充值"),
    PADDLE_GUARANTEE(6, "划至担保账户"),
    INCOME(7, "收入"),
    ALLOCATION(8, "调拨"), 
    ;

    private final int index;

    private final String name;

    private ZxWithdrawStatus(int index, String name) {
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

    public static ZxWithdrawStatus fromIndex(Integer index) {
        return IBaseDbEnum.fromIndex(ZxWithdrawStatus.class, index);
    }
}
