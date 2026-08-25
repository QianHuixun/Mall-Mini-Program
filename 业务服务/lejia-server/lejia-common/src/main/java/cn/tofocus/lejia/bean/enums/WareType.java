package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum WareType implements IBaseDbEnum
{

	WAREHOUSING(0, "入库"),
	INVENTORY(1, "盘点"),
	SALES(2, "销售"),
	EXPIRE(3, "过期"),
	REFUND(4, "退货");

	private final int index;

	private final String name;

	private WareType(int index, String name) {
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

	public static WareType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(WareType.class, index);
	}
}