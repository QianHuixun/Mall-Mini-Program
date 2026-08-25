package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum RefundType implements IBaseDbEnum 
{
	
	REFUND_MEMBER(0, "用户申请退款"),
	REFUND_FARMER(1, "市场退款"),
	;

	private final int index;

	private final String name;

	private RefundType(int index, String name) {
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

	public static RefundType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(RefundType.class, index);
	}
}
