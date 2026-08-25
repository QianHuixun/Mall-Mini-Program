package cn.tofocus.lejia.dao.goods;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.function.Function2;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.page.GroupResult;
import cn.tofocus.db.SelectGroupBuilder;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.entity.goods.ThreeGtypeSortEntity;
import cn.tofocus.lejia.bean.entity.goods.ThreeGtypeSortEntity.F;
import cn.tofocus.lejia.bean.enums.GoodsSortType;

@Component
public class ThreeGtypeSortDao extends JpaSpecificationDelegate<String, ThreeGtypeSortEntity>
{
    @Autowired
    private DtoEnhance dtoDeal;
    
    public long getGtypeStart(String market, Integer gtypeSort, GoodsSortType sortType)
    {
        return this.aggregation()
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .lt(F.gtypeSort, gtypeSort)
            .execCount();
    }
    
    public GroupResult<String, GoodsListItemV2> groupSelectByGtype(GoodsSortType sortType, String market, int from,
        int limit, Function<String, String> groupValueFunction, Boolean sortDesc)
    {
//        boolean sortDesc = GoodsSortType.PRICE == sortType ? false : true;
        if(GoodsSortType.SALED == sortType)
            sortDesc = true;
        GroupResult<String, GoodsListItemV2> r = this.selectGroup(String.class, GoodsListItemV2.class)
            .groupBy(F.gtype)
            .groupValue(groupValueFunction)
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .sort(F.gtypeSort, false)
            .sort(F.goodsMainSort, false)
            .sort(F.sortValue, sortDesc)
            .sort(F.threeGtypeSort, false)
            .from(from)
            .limit(limit)
            .exec();
        return r;
    }
    
    public GroupResult<String, GoodsListItemV2> groupSelectByGtypeWithTopVendor(GoodsSortType sortType, String market,
        int from, int limit, Integer topVendor, Function<String, String> groupValueFunction,
        Function2<Integer, List<ThreeGtypeSortEntity>, List<ThreeGtypeSortEntity>> topVendorFunction)
    {
        boolean sortDesc = GoodsSortType.PRICE == sortType ? false : true;
        //取所有数据
        List<ThreeGtypeSortEntity> list = this.select()
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            
            .sort(F.gtypeSort, false)
            .sort(F.goodsMainSort, false)
            .sort(F.sortValue, sortDesc)
            .sort(F.threeGtypeSort, false)
            .exec();
        
        //替换指定商户数据
        final List<ThreeGtypeSortEntity> list2 = topVendorFunction.apply(topVendor, list);
        list2.sort(new ThreeGtypeSortComparator(sortType));
        //分组
        SelectGroupBuilder<?, HasPkey<Integer>, String, GoodsListItemV2> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItemV2.class);
        return s.groupBy(F.gtype).groupValue(groupValueFunction).from(from).limit(limit).exec((t1, t2) -> {
            List<GoodsListItemV2> l = BeanUtil.beanListFrom(GoodsListItemV2.class, CollectionUtil.subList(list2, t1, t2));
            dtoDeal.deal(GoodsListItemV2.class, l);
            return l;
        });
    }
    
    public long getGoodsMainStart(String market, int gtype, Integer goodsMainSort, GoodsSortType sortType)
    {
        return this.aggregation()
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .eq(F.gtype, gtype)
            .lt(F.goodsMainSort, goodsMainSort)
            .execCount();
    }
    
    public GroupResult<String, GoodsListItemV2> groupSelectByGoodsMain(int gtype, GoodsSortType sortType, String market,
        int from, int limit, Function<String, String> groupValueFunction, Boolean sortDesc, Boolean limitGoodsMain,
        Integer goodsMain)
    {
//        boolean sortDesc = GoodsSortType.PRICE == sortType ? false : true;
        if(GoodsSortType.SALED == sortType)
            sortDesc = true;
        GroupResult<String, GoodsListItemV2> r = this.selectGroup(String.class, GoodsListItemV2.class)
            .groupBy(F.goodsMain)
            .groupValue(groupValueFunction)
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .eq(F.gtype, gtype)
            .iF(Boolean.TRUE.equals(limitGoodsMain))
                .eq(F.goodsMain, goodsMain)
            .endIf()
            .sort(F.gtypeSort, false)
            .sort(F.goodsMainSort, false)
            .sort(F.sortValue, sortDesc)
            .sort(F.threeGtypeSort, false)
            .from(from)
            .limit(limit)
            .exec();
        return r;
    }
    
    public GroupResult<String, GoodsListItemV2> groupSelectByGtypeWithTopVendor(int gtype, GoodsSortType sortType,
        String market, int from, int limit, Integer topVendor, Function<String, String> groupValueFunction,
        Function2<Integer, List<ThreeGtypeSortEntity>, List<ThreeGtypeSortEntity>> topVendorFunction, Boolean sortDesc)
    {
        //取所有数据
        List<ThreeGtypeSortEntity> list = this.select()
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .exec();
        //替换指定商户数据
        final List<ThreeGtypeSortEntity> list2 = topVendorFunction.apply(topVendor, list);
        if(Boolean.FALSE.equals(sortDesc))
            list2.sort(new ThreeGtypeSortComparator(sortType));
        else
            list2.sort(new ThreeGtypeSortComparatorDesc(sortType));
        //分组
        SelectGroupBuilder<?, HasPkey<Integer>, String, GoodsListItemV2> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItemV2.class);
        
        return s.groupBy(F.gtype).groupValue(groupValueFunction).from(from).limit(limit).exec((t1, t2) -> {
            List<GoodsListItemV2> l = BeanUtil.beanListFrom(GoodsListItemV2.class, CollectionUtil.subList(list2, t1, t2));
            dtoDeal.deal(GoodsListItemV2.class, l);
            return l;
        });
    }
    
    public GroupResult<String, GoodsListItemV2> groupSelectByGoodsMainWithTopVendor(int gtype, Integer goodsMain,
        GoodsSortType sortType, String market, int from, int limit, Integer topVendor,
        Function<String, String> groupValueFunction,
        Function2<Integer, List<ThreeGtypeSortEntity>, List<ThreeGtypeSortEntity>> topVendorFunction, Boolean sortDesc,
        Boolean limitGoodsMain)
    {
        //取所有数据
        List<ThreeGtypeSortEntity> list = this.select()
            .eq(F.gtype, gtype)
            .iF(Boolean.TRUE.equals(limitGoodsMain))
                .eq(F.goodsMain, goodsMain)
            .endIf()
            .eq(F.farmer, market)
            .eq(F.gtypeEnable, true)
            .eq(F.goodsMainEnable, true)
            .eq(F.threeGtypeEnable, true)
            .eq(F.sortType, sortType)
            .exec();
        //替换指定商户数据
        final List<ThreeGtypeSortEntity> list2 = topVendorFunction.apply(topVendor, list);
        if(Boolean.FALSE.equals(sortDesc))
            list2.sort(new ThreeGtypeSortComparator(sortType));
        else
            list2.sort(new ThreeGtypeSortComparatorDesc(sortType));
        //分组
        SelectGroupBuilder<?, HasPkey<Integer>, String, GoodsListItemV2> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItemV2.class);
        return s.groupBy(F.goodsMain).groupValue(groupValueFunction).from(from).limit(limit).exec((t1, t2) -> {
            List<GoodsListItemV2> l = BeanUtil.beanListFrom(GoodsListItemV2.class, CollectionUtil.subList(list2, t1, t2));
            dtoDeal.deal(GoodsListItemV2.class, l);
            return l;
        });
    }
    
    private class ThreeGtypeSortComparator implements Comparator<ThreeGtypeSortEntity>
    {
        private GoodsSortType sortType;
        
        private ThreeGtypeSortComparator(GoodsSortType sortType)
        {
            this.sortType = sortType;
        }
        
        @Override
        public int compare(ThreeGtypeSortEntity o1, ThreeGtypeSortEntity o2)
        {
            if (o1.getGtype().equals(o2.getGtype()))
            {
                if (o1.getGoodsMain().equals(o2.getGoodsMain()))
                {
                    if (GoodsSortType.PRICE.equals(sortType))
                    {
                        return o1.getSortValue().compareTo(o2.getSortValue());
                    }
                    else
                    {
                        return o2.getSortValue().compareTo(o1.getSortValue());
                    }
                }
                else
                    return o1.getGoodsMainSort() - o2.getGoodsMainSort();
            }
            else
                return o1.getGtypeSort() - o2.getGtypeSort();
        }
    }
    
    private class ThreeGtypeSortComparatorDesc implements Comparator<ThreeGtypeSortEntity>
    {
        private GoodsSortType sortType;
        
        private ThreeGtypeSortComparatorDesc(GoodsSortType sortType)
        {
            this.sortType = sortType;
        }
        
        @Override
        public int compare(ThreeGtypeSortEntity o1, ThreeGtypeSortEntity o2)
        {
            if (o1.getGtype().equals(o2.getGtype()))
            {
                if (o1.getGoodsMain().equals(o2.getGoodsMain()))
                {
                    if (GoodsSortType.PRICE.equals(sortType))
                    {
//                        return o1.getSortValue().compareTo(o2.getSortValue());
                        return o2.getSortValue().compareTo(o1.getSortValue());
                    }
                    else
                    {
                        return o2.getSortValue().compareTo(o1.getSortValue());
                    }
                }
                else
                    return o1.getGoodsMainSort() - o2.getGoodsMainSort();
            }
            else
                return o1.getGtypeSort() - o2.getGtypeSort();
        }
    }
    
}
