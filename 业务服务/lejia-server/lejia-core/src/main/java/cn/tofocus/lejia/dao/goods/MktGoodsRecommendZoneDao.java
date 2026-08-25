package cn.tofocus.lejia.dao.goods;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommendZone;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommendZone.F;

@Component
public class MktGoodsRecommendZoneDao extends JpaSpecificationDelegate<String, MktGoodsRecommendZone>
{
    public void removeByGoodsRecommend(Integer goodsRecommend)
    {
        this.select().strict(true).eq(F.goodsRecommend, goodsRecommend).del();
    }
}
