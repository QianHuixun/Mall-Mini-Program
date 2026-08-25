package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxFileType implements IBaseDbEnum 
{
	QING_FEN(16, "平台商户业务订单明细文件"), 
	QUDAO_RUJIN(11, "渠道入金"),
	PINGTAI_RUJIN(3, "平台入金"),
	QIYE_RUJIN(2, "企业用户入金"),
	GREN_RUJIN(1, "个人用户入金"),
	ALLOCATION(5, "划拨"),

	
	;

	private final int index;

	private final String name;

	private ZxFileType(int index, String name) {
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

	public static ZxFileType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ZxFileType.class, index);
	}
}
