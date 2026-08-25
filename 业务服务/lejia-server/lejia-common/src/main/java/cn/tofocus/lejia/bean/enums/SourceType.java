package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum SourceType implements IBaseDbEnum
{

	POINTS_BUY(0, "购买"),
	POINTS_CONSUMPTION(1, "消费"),
	POINTS_ACTIVITY(2, "活动"),
	POINTS_MANUAL_ADD(3,"手动增加"),
	POINTS_MANUAL_LESS(4,"手动减少"),
	POINTS_GIFT(5, "礼券兑换"),
	POINTS_SIGN_DAY(6, "签到"),
	POINTS_EMPTY(7,"积分清空"),
	POINTS_COUPON(8,"优惠券兑换"),
	POINTS_REFUND(9,"积分退款"),
	POINTS_REFUND_CONSUMPTION(10,"消费退款"),
	;

	private final int index;

	private final String name;

	private SourceType(int index, String name) {
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

	public static SourceType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(SourceType.class, index);
	}
}
