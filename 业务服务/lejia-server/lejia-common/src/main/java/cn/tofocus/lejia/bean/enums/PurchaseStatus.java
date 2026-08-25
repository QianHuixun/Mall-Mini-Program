package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum PurchaseStatus implements IBaseDbEnum 
{
	
    AWAIT_PURCHASE(0, "待采购"),
    PURCHASEING(1, "采购中"),
    PURCHASE_FINISH(2, "采购完成"),
    PURCHASE_CONFIRM(3, "确认完成"),
    PURCHASE_REVOKE(4, "已撤销"),
;

	private final int index;

	private final String name;

	private PurchaseStatus(int index, String name) {
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

	public static PurchaseStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(PurchaseStatus.class, index);
	}
}
