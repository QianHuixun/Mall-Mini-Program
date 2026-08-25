package cn.tofocus.lejia.bean.enums.v2;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxCardStatus implements IBaseDbEnum
{

    NOT_BINDING(1, "未绑卡"),
    BINDING_SUCCESS(2, "绑卡成功"),
    BINDING_FAILURE(3, "绑卡失败"),
	;

	private final int index;

	private final String name;

	private ZxCardStatus(int index, String name) {
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

	public static ZxCardStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ZxCardStatus.class, index);
	}
}
