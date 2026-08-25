package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
 * 商户文件类型
 */
public enum VendorFileType implements IBaseDbEnum
{
	HEAD_ICON(0, "头像"),
	VIDEO(1, "视频"),
	PROPAGANDA(2, "个性宣传");

	private final int index;

	private final String name;

	private VendorFileType(int index, String name) {
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

	public static VendorFileType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(VendorFileType.class, index);
	}
}
