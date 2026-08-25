package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum SettlementMethodType implements IBaseDbEnum 
{
    PURCHASE_SETTLEMENT(0, "采购价结算"), 
    COMMISSION_SETTLEMENT(1, "佣金结算"),
	;
    
	private final int index;

	private final String name;

	private SettlementMethodType(int index, String name) {
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

	public static SettlementMethodType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(SettlementMethodType.class, index);
	}
}
