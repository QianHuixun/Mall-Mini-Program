package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PrizeType  implements IBaseDbEnum 
{
	
	INTEGRAL_PRIZE(0, "积分"), 
	CARD_PRIZE(1, "优惠券"),
	GIFT_PRIZE(2, "实物"),
	THANK_PRIZE(3, "谢谢惠顾");

	private final int index;

	private final String name;

	private PrizeType(int index, String name) {
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

	public static PrizeType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PrizeType.class, index);
	}
}
