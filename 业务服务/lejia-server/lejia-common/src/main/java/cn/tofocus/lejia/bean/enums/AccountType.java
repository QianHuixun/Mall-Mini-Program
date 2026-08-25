package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum AccountType implements IBaseDbEnum
{

	WX(0, "公众号"),
	USER(1, "用户端"),
	COURIER(2, "骑手端"),
	VENDOR(3, "商户端"),
	;

	private final int index;

	private final String name;

	private AccountType(int index, String name) {
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

	public static AccountType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(AccountType.class, index);
	}
}
