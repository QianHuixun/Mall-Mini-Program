package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum MType implements IBaseDbEnum 
{
	INTEGRAL_GOODS(0, "积分"), 
	MARKET_GOODS(1, "市场"),//市场商品对应普通商品
	MEMBER_GOODS(2, "会员"),
	SPECIAL_GOODS(3, "特价"),
	SHARE_GOODS(4, "分享"),
	CUT_GOODS(5, "砍价"),
	COLLAGE_GOODS(6, "团购"),
	PRESALE_GOODS(7, "预售"),
	POVERTY_ALLEVIATION_GOODS(8, "扶贫"),
    GIFT_GOODS(9, "礼品券"),
    COUPON_GOODS(10, "优惠券"),
    PROCESS_GOODS(11, "加工商品"),
    BOX_GOODS(12, "包厢商品"),
    INTEGRAL_PRESALE_GOODS(13, "预售"),// 积分商城预算商品
    INTEGRAL_BNYP_GOODS(14, "滨农优品"),
    INTEGRAL_MSD_GOODS(15, "民生商品"),
    ;
    
	private final int index;

	private final String name;

	private MType(int index, String name) {
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

	public static MType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(MType.class, index);
	}
}
