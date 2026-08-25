package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum GoodsCutType implements IBaseDbEnum 
{
	GOODSCUT_FIXED(0, "固定"), 
	GOODSCUT_PROPORTION(1, "比例");

	private final int index;

	private final String name;

	private GoodsCutType(int index, String name) {
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

	public static GoodsCutType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(GoodsCutType.class, index);
	}
}
