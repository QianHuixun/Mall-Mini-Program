package cn.tofocus.lejia.bean.enums.jd;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum RefundJdType implements IBaseDbEnum 
{
    RETURN_MONEY(0, "退款"),
	RETURN_GOODS(1, "退货"),
	EXCHANGE(2, "换货"),
	;

	private final int index;

	private final String name;

	private RefundJdType(int index, String name) {
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

	public static RefundJdType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(RefundJdType.class, index);
	}
}
