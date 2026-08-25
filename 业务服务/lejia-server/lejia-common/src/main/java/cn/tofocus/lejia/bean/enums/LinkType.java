package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

/**
* 链接类型 无/链接/积分商城/会员办理
*/
public enum LinkType implements IBaseDbEnum 
{
	NOT_URL(0, "无",""), 
	LINK(1, "链接", "/pages/my/link/index?url="),
	POINTS_MALL(2, "积分商城", ""),
	MEMBERSHIP(3, "会员办理", "/pages/my/openVip/index"),
	GOODS(4, "商品", ""),
	
	PERSONAL_CENTER(5, "个人中心", ""),
	SPECIAL_GOODS(6, "特价秒杀", ""),
	MEMBER_GOODS(7, "会员专区", ""),
	PRESALE_GOODS(8, "预售专区", ""),
	COOKFD_GOODS(9, "菜谱专区", ""),
	CUT_GOODS(10, "砍价专区", ""),
	COLLAGE_GOODS(11, "拼团专区", ""),
	SHARE_GOODS(12, "分享专区", ""),
	POVERTY_ALLEVIATION_GOODS(13, "扶贫专区", ""),
	CARD_CENTER(14, "领券中心", ""),
	POINT_RULES(15,"积分规则", ""),
	
	GTYPE(16,"分类", ""),
	ACTIVITY(17, "卡券活动", ""),
	
	VENDOR(18,"商户", ""),
	WEIXIN_MINI_PROGRAM(19,"小程序页面", ""),
	BNYP_GOODS(20,"滨农优品", ""),
	MS_GOODS(21,"民生专区", ""),
	OFFLINE_STORE(22,"线下门店", ""),
	JD_GOODS(23,"京东专区", ""),
	;
	

	private final int index;

	private final String name;
	private final String value;

	private LinkType(int index, String name, String value) {
		this.name = name;
		this.index = index;
		this.value = value;
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
	
	public String getValue() 
	{
		return value;
	}

	public static LinkType fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(LinkType.class, index);
	}
}
