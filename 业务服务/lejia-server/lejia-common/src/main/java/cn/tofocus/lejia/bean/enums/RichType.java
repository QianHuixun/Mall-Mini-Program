package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum RichType implements IBaseDbEnum
{

	GOODS_TEMPLATE(1, "商品富文本模板"),
	;

	private final int index;

	private final String name;

	private RichType(int index, String name) {
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

	public static RichType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(RichType.class, index);
	}
}
