package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum KryStatus implements IBaseDbEnum 
{
	// 订单状态 已完成/其他
	KRY_COMPLETED(0, "已完成"), 
	KRY_OTHER(1, "其他");

	private final int index;

	private final String name;

	private KryStatus(int index, String name) {
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

	public static KryStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(KryStatus.class, index);
	}
}
