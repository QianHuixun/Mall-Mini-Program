package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum MemberPType implements IBaseDbEnum 
{
	
	ANNUAL_FEE(0, "年费"), 
	RECHARGE(1, "充值");

	private final int index;

	private final String name;

	private MemberPType(int index, String name) {
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

	public static MemberPType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(MemberPType.class, index);
	}
}
