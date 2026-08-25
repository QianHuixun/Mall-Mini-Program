package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum DataEnums implements IBaseDbEnum 
{
	
	DAY(0, "日"), 
	MONTH(1, "月"),
	SEASON(2, "季度"),
	YEAR(3, "年")
	;

	private final int index;

	private final String name;

	private DataEnums(int index, String name) {
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

	public static DataEnums fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(DataEnums.class, index);
	}
}
