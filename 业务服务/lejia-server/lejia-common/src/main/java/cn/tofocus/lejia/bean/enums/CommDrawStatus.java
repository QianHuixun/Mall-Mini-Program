package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum CommDrawStatus  implements IBaseDbEnum
{

	COMMDRAW_INITIAL(0, "申请中"),
	COMMDRAW_SENT(1, "同意"),
	COMMDRAW_REFUSE(2, "拒绝"),
	COMMDRAW_PAID(3, "已打款");

	private final int index;

	private final String name;

	private CommDrawStatus(int index, String name) {
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

	public static CommDrawStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(CommDrawStatus.class, index);
	}
}
