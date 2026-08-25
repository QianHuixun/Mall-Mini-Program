package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ShowType implements IBaseDbEnum 
{
    SHOW_PHOTO(0, "展示图片"), 
    SHOW_GOODS(1, "展示商品"),
	;
    
	private final int index;

	private final String name;

	private ShowType(int index, String name) {
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

	public static ShowType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ShowType.class, index);
	}
}
