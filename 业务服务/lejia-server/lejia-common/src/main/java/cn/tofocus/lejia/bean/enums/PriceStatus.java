package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PriceStatus implements IBaseDbEnum 
{
    ABNORMAL(1, "异常"),
    ABNORMAL_FINISH(2, "异常(已确认)"),
    
    ;

	private final int index;

	private final String name;

	private PriceStatus(int index, String name) {
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

	public static PriceStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PriceStatus.class, index);
	}
}
