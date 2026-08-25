package cn.tofocus.lejia.bean.enums;

import java.util.Set;

import org.apache.commons.compress.utils.Sets;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum AdvertPosition implements IBaseDbEnum
{
    // @formatter:off
	ADVERT_POSITION_TYPE(0, "分类"), 
	ADVERT_POSITION_POINTS_MALL(1, "积分商城"),
	ADVERT_POSITION_INDEX(2, "首页"),
	ADVERT_POSITION_POVERTY_ALLEVIATION(3, "扶贫专区"),
	ADVERT_POSITION_SHARE(4, "分享专区"),
	ADVERT_POSITION_SALE(5,"预售专区"),
	ADVERT_POSITION_MEMBER(6, "会员专区"),
	ADVERT_POSITION_SPECIAL(7, "抢购专区"),
	ADVERT_POSITION_COLLAGE(8, "团购专区"),
	ADVERT_POSITION_COOKFD(9, "菜谱广告"),
	ADVERT_POSITION_CUT(10, "砍价专区"),
	ADVERT_POSITION_BNYP(11, "滨农优品"),
	ADVERT_POSITION_GOODS_MAIN(12, "市场分类页二级类目"),
	ADVERT_POSITION_MSD(13, "热力豆专区"),
	ADVERT_POSITION_COM(14, "组合广告"),
	ADVERT_POSITION_JD(15, "京东专区"),
    ADVERT_POSITION_MSD_GOODS_MAIN(16, "热力豆专区二级类目"),
	;
	// @formatter:on
    
    private final int index;
    
    private final String name;
    
    private AdvertPosition(int index, String name)
    {
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
    
    public static AdvertPosition fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(AdvertPosition.class, index);
    }
    
    public static Set<AdvertPosition> ascriptionPositions()
    {
        return Sets.newHashSet(ADVERT_POSITION_POINTS_MALL, ADVERT_POSITION_BNYP, ADVERT_POSITION_MSD, ADVERT_POSITION_JD);
    }
}
