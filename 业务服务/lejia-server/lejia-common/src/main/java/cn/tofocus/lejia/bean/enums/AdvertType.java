package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum AdvertType implements IBaseDbEnum 
{
    OWN(0, "自有广告"),
	SPECIAL_AREA(1, "专区广告"), 
	;
    
	private final int index;

	private final String name;

	private AdvertType(int index, String name) {
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

	public static AdvertType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(AdvertType.class, index);
	}
}
