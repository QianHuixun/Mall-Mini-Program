package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.GroupResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.ThreeGtypeSortEntity;
import cn.tofocus.lejia.bean.entity.goods.ThreeGtypeSortEntity.F;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.ThreeGtypeSortDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager2;

@Component
public class GoodListQueryer
{
    @Autowired
    private AppGoodsV4Manager appGoodsV4Manager;
    
    @Autowired
    private AppGoodsV4Manager2 appGoodsV4Manager2;
    
    @Autowired
    private ThreeGtypeSortDao threeGtypeSortDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao mktGoodsSpaceDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Value("${goods.three.gtype.sort:false}")
    private Boolean gtgs;
    
    
    public void resetAll(String market, GoodsSortType sortType)
    {
        //market等于null, 更新所有市场
        //sortType 等于null，同时计算价格和销量
        //每个市场的每个三级分类，产生两条记录，一条按销量，一条按价格
        if(Boolean.TRUE.equals(gtgs))
            return;
        appGoodsV4Manager.putThreeGtypeSort(market, sortType);
    }
    
    /**
     * 一级分类删除，删除一级分类下所有数据
     */
    public void delGtype(int gtype)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.gtype, gtype).strict(true).del();
    }
    
    /**
     * 一级分类启停或勾选市场商城，更新一级分类下所有数据
     */
    public void switchGtype(int gtype, boolean enable)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.gtype, gtype).strict(true).update(F.gtypeEnable, enable);
    }
    
    /**
     * 一级分类排序变更，更新一级分类下所有数据
     */
    public void updateGtypeSort(int gtype, int sort)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.gtype, gtype).strict(true).update(F.gtypeSort, sort);
    }
    
    /**
     * 二级分类删除，删除二级分类下所有数据
     */
    public void delGoodsMain(int goodsMain)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.goodsMain, goodsMain).strict(true).del();
    }
    
    /**
     * 二级分类启停，重新计算二级分类下所有数据
     */
    public void switchGoodsMain(int goodsMain, boolean enable)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.goodsMain, goodsMain).strict(true).update(F.goodsMainEnable, enable);
    }
    
    /**
     * 二级分类排序变更，更新二级分类下所有数据
     */
    public void updateGoodsMainSort(int goodsMain, int sort)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.goodsMain, goodsMain).strict(true).update(F.goodsMainSort, sort);
    }
    
    /**
     * 三级分类删除或停止，删除三级分类下所有数据
     */
    public void delThreeGtype(int threeGtype)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.threeGtype, threeGtype).strict(true).del();
    }
    
    /**
     * 三级分类启停，重新计算三级分类下所有数据
     */
    public void switchThreeGtype(int threeGtype, boolean enable)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.threeGtype, threeGtype).strict(true).update(F.threeGtypeEnable, enable);
    }
    
    /**
     * 三级分类排序变更，更新三级分类下所有数据
     */
    public void updateThreeGtypeSort(int threeGtype, int sort)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        threeGtypeSortDao.select().eq(F.threeGtype, threeGtype).strict(true).update(F.threeGtypeSort, sort);
    }
    
    /**
     * 修改商品属性，修改价格，销量变更，重新计算所属的市场的三级分类的数据
     */
    public void resetThreeGtype(MktGoods goods)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        long k1 = System.currentTimeMillis();
        Integer threeGtype = goods.getThreeGtype();
        if(threeGtype != null)
            threeGtypeSortDao.select().eq(F.threeGtype, threeGtype).strict(true).del();
        String farmer = goods.getFarmer();
        List<MType> mtypes = new ArrayList<>();
        if(farmer.startsWith(Constant.Operation))
        {
            mtypes.add(MType.INTEGRAL_GOODS);
            mtypes.add(MType.INTEGRAL_BNYP_GOODS);
        }
        else
        {
            mtypes.add(MType.MARKET_GOODS);
            mtypes.add(MType.BOX_GOODS);
        }
        List<MktGoods> goodsList = goodsDao.select()
            .eq("threeGtype", threeGtype)
            .in("mType", mtypes)
            .eq("farmer", farmer)
            .eq("enabled", true)
            .eq("idDel", false)
            .exec();
        if(goodsList.isEmpty())
            return;
        ThreeGtypeSortEntity ntgsp = new ThreeGtypeSortEntity();
        Collections.sort(goodsList, new Comparator<MktGoods>()
        {
            @Override
            public int compare(MktGoods o1, MktGoods o2)
            {
                return o1.getPrice().compareTo(o2.getPrice());
            }
        });
        MktGoods g = goodsList.get(0);
        ntgsp.setThreeGtype(threeGtype);
        MktGoodsMainThree mktGoodsMainThree = goodsMainThreeDao.get(threeGtype);
        if (mktGoodsMainThree == null) return;
        ntgsp.setThreeGtypeEnable(mktGoodsMainThree.getEnabled());
        ntgsp.setThreeGtypeSort(mktGoodsMainThree.getSort());
        ntgsp.setFarmer(farmer);
        ntgsp.setSortType(GoodsSortType.PRICE);
        ntgsp.setSortValue(g.getPrice());
        ntgsp.setGtype(g.getGtype());
        MktGtype gtype = gtypeDao.get(g.getGtype());
        if (gtype == null) return;
        ntgsp.setGtypeEnable(gtype.getEnabled());
        ntgsp.setGtypeSort(gtype.getSort());
        MktGoodsMain mktGoodsMain = goodsMainDao.get(g.getGoodsMain());
        if (mktGoodsMain == null) return;
        ntgsp.setGoodsMain(g.getGoodsMain());
        ntgsp.setGoodsMainEnable(mktGoodsMain.getEnabled());
        ntgsp.setGoodsMainSort(mktGoodsMain.getSort());
        ntgsp.setGoods(g.getPkey());
        ntgsp.setVendor(g.getVendor());
        List<MktGoodsSpace> gsList = mktGoodsSpaceDao.select().eq("goods", g.getPkey()).sort("price", false).exec();
        if (!gsList.isEmpty())
        {
            ntgsp.setSpace(gsList.get(0).getPkey());
        }
        threeGtypeSortDao.add(ntgsp);
        
        ThreeGtypeSortEntity ntgsx = new ThreeGtypeSortEntity();
        BeanUtils.copyProperties(ntgsp, ntgsx, "pkey");
        Collections.sort(goodsList, new Comparator<MktGoods>()
        {
            @Override
            public int compare(MktGoods o1, MktGoods o2)
            {
                return o2.getXsNum() - o1.getXsNum();
            }
        });
        g = goodsList.get(0);
        ntgsx.setSortValue(new BigDecimal(g.getXsNum()));
        ntgsx.setGtype(g.getGtype());
        ntgsx.setGoods(g.getPkey());
        ntgsx.setSortType(GoodsSortType.SALED);
        ntgsx.setVendor(g.getVendor());
        gsList = mktGoodsSpaceDao.select().eq("goods", g.getPkey()).sort("price", false).exec();
        if (!gsList.isEmpty())
        {
            ntgsx.setSpace(gsList.get(0).getPkey());
        }
        
        threeGtypeSortDao.add(ntgsx);
        long k2 = System.currentTimeMillis();
        System.out.println("更新分类缓存表耗时: " + (k2 - k1) + "毫秒");
    }
    
    /**
     * 修改商品分类，重新计算所属的市场的被修改掉的三级分类的数据
     */
    public void resetThreeGtype(Integer threeGtype, String farmer)
    {
        if(Boolean.TRUE.equals(gtgs))
            return;
        long k1 = System.currentTimeMillis();
//        Integer threeGtype = goods.getThreeGtype();
        threeGtypeSortDao.select().eq(F.threeGtype, threeGtype).strict(true).del();
//        String farmer = goods.getFarmer();
        List<MktGoods> goodsList = goodsDao.select()
            .eq("threeGtype", threeGtype)
            .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS)
            .eq("farmer", farmer)
            .eq("enabled", true)
            .eq("idDel", false)
            .exec();
        if(goodsList.isEmpty())
            return;
        ThreeGtypeSortEntity ntgsp = new ThreeGtypeSortEntity();
        Collections.sort(goodsList, new Comparator<MktGoods>()
        {
            @Override
            public int compare(MktGoods o1, MktGoods o2)
            {
                return o1.getPrice().compareTo(o2.getPrice());
            }
        });
        MktGoods g = goodsList.get(0);
        ntgsp.setThreeGtype(threeGtype);
        MktGoodsMainThree mktGoodsMainThree = goodsMainThreeDao.get(threeGtype);
        if (mktGoodsMainThree == null) return;
        ntgsp.setThreeGtypeEnable(mktGoodsMainThree.getEnabled());
        ntgsp.setThreeGtypeSort(mktGoodsMainThree.getSort());
        ntgsp.setFarmer(farmer);
        ntgsp.setSortType(GoodsSortType.PRICE);
        ntgsp.setSortValue(g.getPrice());
        ntgsp.setGtype(g.getGtype());
        MktGtype gtype = gtypeDao.get(g.getGtype());
        if (gtype == null) return;
        ntgsp.setGtypeEnable(gtype.getEnabled());
        ntgsp.setGtypeSort(gtype.getSort());
        MktGoodsMain mktGoodsMain = goodsMainDao.get(g.getGoodsMain());
        if (mktGoodsMain == null) return;
        ntgsp.setGoodsMain(g.getGoodsMain());
        ntgsp.setGoodsMainEnable(mktGoodsMain.getEnabled());
        ntgsp.setGoodsMainSort(mktGoodsMain.getSort());
        ntgsp.setGoods(g.getPkey());
        ntgsp.setVendor(g.getVendor());
        List<MktGoodsSpace> gsList = mktGoodsSpaceDao.select().eq("goods", g.getPkey()).sort("price", false).exec();
        if (!gsList.isEmpty())
        {
            ntgsp.setSpace(gsList.get(0).getPkey());
        }
        threeGtypeSortDao.add(ntgsp);
        
        ThreeGtypeSortEntity ntgsx = new ThreeGtypeSortEntity();
        BeanUtils.copyProperties(ntgsp, ntgsx, "pkey");
        Collections.sort(goodsList, new Comparator<MktGoods>()
        {
            @Override
            public int compare(MktGoods o1, MktGoods o2)
            {
                return o2.getXsNum() - o1.getXsNum();
            }
        });
        g = goodsList.get(0);
        ntgsx.setSortValue(new BigDecimal(g.getXsNum()));
        ntgsx.setGtype(g.getGtype());
        ntgsx.setGoods(g.getPkey());
        ntgsx.setSortType(GoodsSortType.SALED);
        ntgsx.setVendor(g.getVendor());
        gsList = mktGoodsSpaceDao.select().eq("goods", g.getPkey()).sort("price", false).exec();
        if (!gsList.isEmpty())
        {
            ntgsx.setSpace(gsList.get(0).getPkey());
        }
        
        threeGtypeSortDao.add(ntgsx);
        long k2 = System.currentTimeMillis();
        System.out.println("更新分类缓存表耗时: " + (k2 - k1) + "毫秒");
    }
    
    private String getGtypeName(String k)
    {
        MktGtype t = gtypeDao.get(Integer.valueOf(k));
        return k == null ? "" : t.getName();
    }
    
    private String getGoodsMainName(String k)
    {
        MktGoodsMain t = goodsMainDao.get(Integer.valueOf(k));
        return k == null ? "" : t.getName();
    }
    
    private List<ThreeGtypeSortEntity> replaceTopVendor(Integer topVendor, List<ThreeGtypeSortEntity> list)
    {
        List<MktGoods> exec = goodsDao.select()
            .eq("idDel", false)
            .isNotNull("vendor")
            .eq("vendor", topVendor)
            .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS)
            .eq("enabled", true)
            .exec();
        Map<Integer, MktGoods> map = new HashMap<>();
        exec.forEach(e -> map.put(e.getThreeGtype(), e));
        for (int i = 0; i < list.size(); i++)
        {
            ThreeGtypeSortEntity tg = list.get(i);
            if (map.containsKey(tg.getThreeGtype()))
            {
                ThreeGtypeSortEntity tgs = new ThreeGtypeSortEntity();
                BeanUtils.copyProperties(tg, tgs);
                MktGoods goods = map.get(tg.getThreeGtype());
                tgs.setGoods(goods.getPkey());
                List<MktGoodsSpace> gsList =
                    mktGoodsSpaceDao.select().eq("goods", goods.getPkey()).sort("price", false).exec();
                if (!gsList.isEmpty())
                {
                    tgs.setSpace(gsList.get(0).getPkey());
                    tgs.setSortValue(gsList.get(0).getPrice());
                }
                if (GoodsSortType.PRICE.equals(tg.getSortType()))
                {
                    tgs.setSortValue(new BigDecimal(-99999).add(tgs.getSortValue()));
                }
                else
                {
                    tgs.setSortValue(new BigDecimal("9999").add(new BigDecimal(goods.getXsNum())));
                }
                list.set(i, tgs);
            }
        }
        return list;
    }
    
    public GroupResult<String, GoodsListItemV2> rangeCat1(GoodsSortType sortType, String market, Integer gtype,
        Integer gtypeSort, int from, int limit, Integer topVendor, Boolean sortDesc)
    {
        //指定类目，设置起始位置
        int gtypeStart = 0;
        if (gtype != null)
        {
            gtypeStart = (int)threeGtypeSortDao.getGtypeStart(market, gtypeSort, sortType);
            from = from + gtypeStart;
        }
        GroupResult<String, GoodsListItemV2> r = null;
        if (topVendor == null)
        {
            //从数据库查询
            r = threeGtypeSortDao.groupSelectByGtype(sortType, market, from, limit, k -> getGtypeName(k), sortDesc);
        }
        else
        {
            //从数据库查询全部数据，合并商户数据，重新内存计算
            r = threeGtypeSortDao.groupSelectByGtypeWithTopVendor(gtype,
                sortType,
                market,
                from,
                limit,
                topVendor,
                k -> getGtypeName(k),
                (v, l) -> replaceTopVendor(v, l),
                sortDesc);
        }
        if (gtype != null)
        {
            r.setStart(r.getStart() - gtypeStart);
            r.setNextStart(r.getNextStart() - gtypeStart);
        }
        appGoodsV4Manager2.assembleGoodsListItem(r);
        return r;
    }
    
//    public GroupResult<String, GoodsListItemV2> rangeCat1MYSQL(GoodsSortType sortType, String market, Integer gtype,
//        Integer gtypeSort, int from, int limit, Integer topVendor, Boolean sortDesc)
//    {
//        //指定类目，设置起始位置
//        int gtypeStart = 0;
//        if (gtype != null)
//        {
//            gtypeStart = (int)threeGtypeSortDao.getGtypeStart(market, gtypeSort, sortType);
//            from = from + gtypeStart;
//        }
//        GroupResult<String, GoodsListItemV2> r = null;
//        if (topVendor == null)
//        {
//            //从数据库查询
//            r = threeGtypeSortDao.groupSelectByGtype(sortType, market, from, limit, k -> getGtypeName(k), sortDesc);
//        }
//        else
//        {
//            //从数据库查询全部数据，合并商户数据，重新内存计算
//            r = threeGtypeSortDao.groupSelectByGtypeWithTopVendor(gtype,
//                sortType,
//                market,
//                from,
//                limit,
//                topVendor,
//                k -> getGtypeName(k),
//                (v, l) -> replaceTopVendor(v, l),
//                sortDesc);
//        }
//        if (gtype != null)
//        {
//            r.setStart(r.getStart() - gtypeStart);
//            r.setNextStart(r.getNextStart() - gtypeStart);
//        }
//        appGoodsV4Manager2.assembleGoodsListItem(r);
//        return r;
//    }
    
    public GroupResult<String, GoodsListItemV2> rangeCat2(GoodsSortType sortType, String market, int gtype,
        Integer goodsMain, Integer goodsMainSort, int from, int limit, Integer topVendor, Boolean sortDesc,
        Boolean limitGoodsMain)
    {
        int goodsMainStart = 0;
        if (goodsMain != null && !Boolean.TRUE.equals(limitGoodsMain))
        {
            goodsMainStart = (int)threeGtypeSortDao.getGoodsMainStart(market, gtype, goodsMainSort, sortType);
            from = from + goodsMainStart;
        }
        
        GroupResult<String, GoodsListItemV2> r = null;
        if (topVendor == null)
        {
            //从数据库查询
            r = threeGtypeSortDao.groupSelectByGoodsMain(gtype,
                sortType,
                market,
                from,
                limit,
                k -> getGoodsMainName(k),
                sortDesc,
                limitGoodsMain,
                goodsMain);
        }
        else
        {
            //从数据库查询全部数据，合并商户数据，重新内存计算
            r = threeGtypeSortDao.groupSelectByGoodsMainWithTopVendor(gtype,
                goodsMain,
                sortType,
                market,
                from,
                limit,
                topVendor,
                k -> getGoodsMainName(k),
                (v, l) -> replaceTopVendor(v, l),
                sortDesc,
                limitGoodsMain);
        }
        
        if (goodsMain != null)
        {
            r.setStart(r.getStart() - goodsMainStart);
            r.setNextStart(r.getNextStart() - goodsMainStart);
        }
        appGoodsV4Manager2.assembleGoodsListItem(r);
        return r;
    }
    
}
