package cn.tofocus.lejia.bean.enums.v2;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum TimeType implements IBaseDbEnum
{

    THE_DAY(1, "当天"),
	THREE_DAY(2, "三日内"),
	WEEK(3, "七日内"),
	MONTH(4, "一月内"),
	;

	private final int index;

	private final String name;

	private TimeType(int index, String name) {
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

	public static TimeType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(TimeType.class, index);
	}
}
