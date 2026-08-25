package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum MemberStatus implements IBaseDbEnum 
{
	
    NORMAL(0, "正常"), 
	LOG_OUTING(1, "注销中"),
	LOGGED_OUT(2, "已注销");

	private final int index;

	private final String name;

	private MemberStatus(int index, String name) {
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

	public static MemberStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(MemberStatus.class, index);
	}
}
