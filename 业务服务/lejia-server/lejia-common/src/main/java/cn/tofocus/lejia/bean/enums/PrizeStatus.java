package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PrizeStatus implements IBaseDbEnum 
{
	
	NOT_ISSUED(0, "初始"), 
	ISSUED(1, "已发");

	private final int index;

	private final String name;

	private PrizeStatus(int index, String name) {
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

	public static PrizeStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PrizeStatus.class, index);
	}
}
