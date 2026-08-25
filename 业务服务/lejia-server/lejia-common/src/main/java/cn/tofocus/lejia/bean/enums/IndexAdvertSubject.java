package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum IndexAdvertSubject implements IBaseDbEnum 
{
	ALL_MEMBER(0, "全部"),
	ANNUAL_MEMBER(1, "年费会员"),
	ORDINARY_MEMBER(2, "普通会员"),
	ACTIVE_MEMBER(3, "活跃会员"),
	NOT_ACTIVE(4, "非活跃会员"),
	NEW_MEMBER(5, "新注册会员"),
	OLD_MEMBER(6, "老会员"),
	NOT_CONSUMED_MEMBER(7, "从未消费会员"),
	;

	private final int index;

	private final String name;

	private IndexAdvertSubject(int index, String name) {
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

	public static IndexAdvertSubject fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(IndexAdvertSubject.class, index);
	}
}
