package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PType implements IBaseDbEnum
{

	DRAW(0, "抽奖"),
	CONSUME(1, "消费");

	private final int index;

	private final String name;

	private PType(int index, String name) {
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

	public static PType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PType.class, index);
	}
}
