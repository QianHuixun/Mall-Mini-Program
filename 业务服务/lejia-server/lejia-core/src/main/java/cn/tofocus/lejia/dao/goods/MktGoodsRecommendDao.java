package cn.tofocus.lejia.dao.goods;

import org.springframework.stereotype.Component;

import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommend;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommend.F;

@Component
public class MktGoodsRecommendDao extends JpaSpecificationDelegate<Integer, MktGoodsRecommend>
{
    public <T> T get4Ascription(Integer pkey, Integer ascription, Class<T> clazz)
    {
        return this.selectOne()
            .eq(F.pkey, pkey)
            .eq(F.farmer, Constant.Operation + ascription)
            .eq(F.ascription, ascription)
            .execDto(clazz);
    }
    
    public <T> T get4Farmer(Integer pkey, String farmer, Integer ascription, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.farmer, farmer).eq(F.ascription, ascription).execDto(clazz);
    }
    
    public int maxSort(Integer ascription, String farmer, Integer notEqPkey, Integer sourceGoods)
    {
        AggregationBuilder<Integer, MktGoodsRecommend> builder =
            this.aggregation().eq(F.ascription, ascription).eq(F.farmer, farmer).notEq(F.pkey, notEqPkey);
        if (sourceGoods == null)
            builder.isNull(F.sourceGoods);
        else
            builder.eq(F.sourceGoods, sourceGoods);
        Number maxSort = builder.execMax(F.sort);
        return maxSort.intValue();
    }
    
    public boolean isGoodsRepeat(Integer ascription, String farmer, Integer notEqPkey, Integer goods,
        Integer sourceGoods)
    {
        MktGoodsRecommend bean = this.selectOne()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .notEq(F.pkey, notEqPkey)
            .eq(F.goods, goods)
            .iF(sourceGoods == null)
                .isNull(F.sourceGoods)
            .eLse()
                .eq(F.sourceGoods, sourceGoods)
            .endIf()
            .exec();
        return bean != null;
    }
    
    public long countBySourceGoods(Integer ascription, Integer sourceGoods)
    {
        return this.aggregation().eq(F.ascription, ascription).eq(F.sourceGoods, sourceGoods).execCount();
    }
}
