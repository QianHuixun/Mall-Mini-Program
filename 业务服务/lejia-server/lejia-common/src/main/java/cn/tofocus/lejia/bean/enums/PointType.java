package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PointType implements IBaseDbEnum
{
	OPERATION(1, "运营端"),
	MARKET(2, "市场端"),
	COMPANY(3, "公司端");

	private final int index;

	private final String name;

	private PointType(int index, String name) {
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

	public static PointType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PointType.class, index);
	}
}
