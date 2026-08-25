package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum OrderOir implements IBaseDbEnum 
{
	// 订单来源 自营/积分商城/市场商城
	SELF_EMPLOYED(0, "自营"), 
	POINTS_MALL(1, "积分商城"),//市场商品对应普通商品
	MARKET_MALL(2, "市场商城");

	private final int index;

	private final String name;

	private OrderOir(int index, String name) {
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

	public static OrderOir fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(OrderOir.class, index);
	}
}
