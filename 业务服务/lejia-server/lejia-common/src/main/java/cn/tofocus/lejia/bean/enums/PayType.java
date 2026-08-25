package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PayType implements IBaseDbEnum 
{
	
	ORDER_ZHIFUBAO(0, "支付宝"), 
	ORDER_WEIXIN(1, "微信"),
	ORDER_ELECTRONIC_ACCOUNT(2, "电子账户"),
	ZXYW_WEIXIN(3, "中信银行统一下单"),
	NM_MEMBER(4, "农贸会员卡"),
	ORDER_MSD(5, "热力豆"),

	MSD_COMBINATION(6, "热力豆+微信支付"),
	ELECTRONIC_ACCOUNT_COMBINATION(7, "电子账户+微信支付"),
	
	
	;

	private final int index;

	private final String name;

	private PayType(int index, String name) {
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

	public static PayType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PayType.class, index);
	}
}