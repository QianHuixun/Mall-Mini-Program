package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CardStatus implements IBaseDbEnum 
{
	
	UNUSED(0, "未使用"), 
	USED(1, "已使用"),
	EXPIRED(2, "已过期");

	private final int index;

	private final String name;

	private CardStatus(int index, String name) {
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

	public static CardStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(CardStatus.class, index);
	}
}
