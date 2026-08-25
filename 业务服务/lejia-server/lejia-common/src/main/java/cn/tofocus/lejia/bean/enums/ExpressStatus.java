package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ExpressStatus implements IBaseDbEnum 
{
	EXPRESS_INITIAL(0, "初始"), 
	EXPRESS_ORDER(1, "骑手取货中"),
	EXPRESS_GOODS(2, "配送中"),
	EXPRESS_ARRIVED(3, "已送达"),
	EXPRESS_REJECT(4, "拒收");

	private final int index;

	private final String name;

	private ExpressStatus(int index, String name) {
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

	public static ExpressStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ExpressStatus.class, index);
	}
}
