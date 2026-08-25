package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CommSourceType implements IBaseDbEnum
{

	COMM_BUY(0, "消费"),
	COMM_SHARE(1, "分享"),
	COMM_RETURN(2, "退货"),
	POINTS_MANUAL_ADD(3,"手动"),
	POINTS_MANUAL_LESS(4,"手动"),
	RECHARGE(5, "充值"),
	WITHDRAW(6, "提现"),
	WITHDRAW_REFUSE(7, "提现拒绝"),
    SHARE_NEW(8, "邀新"),
    RECHARGE_CARD(9, "卡密充值"),
    
    ;

	private final int index;

	private final String name;

	private CommSourceType(int index, String name) {
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

	public static CommSourceType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(CommSourceType.class, index);
	}
}
