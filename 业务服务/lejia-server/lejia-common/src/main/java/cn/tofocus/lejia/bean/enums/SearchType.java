package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum SearchType implements IBaseDbEnum
{

	GOODS(0, "商品"),
	COOKFD(1, "菜谱"),
	POINTS_MALL(2, "积分商城");

	private final int index;

	private final String name;

	private SearchType(int index, String name) {
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

	public static SearchType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(SearchType.class, index);
	}
}
