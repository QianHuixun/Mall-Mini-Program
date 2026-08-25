package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum OrderGroupStatus implements IBaseDbEnum 
{
	
	NOT_GROUPS(0, "未成团"), 
	INTO_GROUPS(1, "成团");

	private final int index;

	private final String name;

	private OrderGroupStatus(int index, String name) {
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

	public static OrderGroupStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(OrderGroupStatus.class, index);
	}
}
