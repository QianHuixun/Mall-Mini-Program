package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum LevelType implements IBaseDbEnum 
{
	ORDINARY_MEMBER(0, "普通会员"), 
	PAID_MEMBER(1, "年费会员");

	private final int index;

	private final String name;

	private LevelType(int index, String name) {
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

	public static LevelType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(LevelType.class, index);
	}
}
