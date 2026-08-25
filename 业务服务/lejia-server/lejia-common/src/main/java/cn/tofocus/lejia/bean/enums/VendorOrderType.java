package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 商户订单类型
 */
public enum VendorOrderType implements IBaseDbEnum
{
	MARKET_ORDER(1, "市场订单"),
	COMPANY_ORDER(2, "公司订单"),
	OPERATION_ORDER(3, "运营订单"),
	OTHER(4, "其他");

	private final int index;

	private final String name;

	private VendorOrderType(int index, String name) {
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

	public static VendorOrderType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(VendorOrderType.class, index);
	}
}