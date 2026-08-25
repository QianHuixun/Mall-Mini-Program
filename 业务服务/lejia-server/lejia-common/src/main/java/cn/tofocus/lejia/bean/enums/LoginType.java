package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum LoginType implements IBaseDbEnum 
{
	
	MP(0, "小程序"), 
	WECHAT(1, "公众号"),
	APP_ANDROID(2,"app_Android"),
	APP_IOS(3,"app_ios");

	private final int index;

	private final String name;

	private LoginType(int index, String name) {
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

	public static LoginType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(LoginType.class, index);
	}	
}
