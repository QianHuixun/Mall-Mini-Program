package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PayStatus implements IBaseDbEnum 
{
	
	PAY_INITIAL(0, "初始"), 
	PAYMENT_SUCCESSFUL(1, "支付成功"),
	PAYMENT_FAILED(2, "支付失败"),
	;

	private final int index;

	private final String name;

	private PayStatus(int index, String name) {
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

	public static PayStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PayStatus.class, index);
	}
}