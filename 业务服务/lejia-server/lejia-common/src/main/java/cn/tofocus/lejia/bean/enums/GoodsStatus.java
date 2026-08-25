package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum GoodsStatus implements IBaseDbEnum 
{
	
	ALL(1, "登陆"), 
	ON_SALE(2, "在售中"),
	SOLD_OUT(3,"已售罄"),
	REMOVED(4,"已下架");

	private final int index;

	private final String name;

	private GoodsStatus(int index, String name) {
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

	public static GoodsStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(GoodsStatus.class, index);
	}
}
