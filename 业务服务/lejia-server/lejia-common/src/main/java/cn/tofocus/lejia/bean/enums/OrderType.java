package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum OrderType implements IBaseDbEnum 
{
	//砍价/团购/预售/佣金/普通
	INTEGRAL_ORDER(0, "商城"), 
	MARKET_ORDER(1, "市场"),//市场商品对应普通商品
	SHARE_ORDER(2, "佣金"),
	CUT_ORDER(3, "砍价"),
	COLLAGE_ORDER(4, "团购"),
	PRESALE_ORDER(5, "预售"),
    GIFT_ORDER(6, "礼券"),
    COUPON_ORDER(7, "优惠券"),
    INTEGRAL_PRESALE_ORDER(13, "预售"),
    INTEGRAL_BNYP_ORDER(14, "滨农优品"),
    INTEGRAL_MSD_ORDER(15, "热力豆"),
    INTEGRAL_JD_ORDER(16, "京东")
    ;

	private final int index;

	private final String name;

	private OrderType(int index, String name) {
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

	public static OrderType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(OrderType.class, index);
	}
}