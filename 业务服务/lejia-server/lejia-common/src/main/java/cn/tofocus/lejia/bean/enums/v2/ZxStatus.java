package cn.tofocus.lejia.bean.enums.v2;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxStatus implements IBaseDbEnum
{

    NOT_AUDIT(1, "未审核"),
    AUDIT_SUCCESS(2, "审核通过"),
	AUDIT_FAILURE(3, "审核未通过"),
	;

	private final int index;

	private final String name;

	private ZxStatus(int index, String name) {
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

	public static ZxStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ZxStatus.class, index);
	}
}
