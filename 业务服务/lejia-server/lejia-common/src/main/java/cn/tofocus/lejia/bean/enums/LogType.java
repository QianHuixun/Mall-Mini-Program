package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum LogType implements IBaseDbEnum 
{
	// 登陆/操作/查询
	LOG_IN(0, "登陆"), 
	OPERATING(1, "操作"),
	INQUIRE(2,"查询");

	private final int index;

	private final String name;

	private LogType(int index, String name) {
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

	public static LogType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(LogType.class, index);
	}
}
