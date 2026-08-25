package cn.tofocus.lejia.domain.app;

import java.util.*;

import cn.tofocus.db.aggs.AggregationBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeGoodsDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeGoodsSpaceDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppMallGtypeTwoLevelsDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.entity.goods.*;
import cn.tofocus.lejia.bean.entity.jd.JdCategory;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsRecommendDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdCategoryDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class AppGoodsGtypeManager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktGoodsRecommendDao mktGoodsRecommendDao;

    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private JdCategoryDao jdCategoryDao;

    @Autowired
    private MktMemberTagDao memberTagDao;
    


    public List<AppGtypeDTO> queryGtypeGoods(int page, int pagesize)
    {
        List<MktGtype> exec = gtypeDao.select().eq("idDel", false).eq("enabled", true).sort("sort", true).exec();
        List<AppGtypeDTO> result = BeanUtil.beanListFrom(AppGtypeDTO.class, exec);
        for (AppGtypeDTO agd : result)
        {
            assembleAppGtypeDTO(page, pagesize, agd);
        }
        return result;
    }
    
    // 组装appGtypeDTO
    private void assembleAppGtypeDTO(int page, int pagesize, AppGtypeDTO agd)
    {
        PageResult<MktGoods> goodsExec = goodsDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("gtype", agd.getPkey())
            .eq("idDel", false)
            .eq("enabled", true)
            .sort("xsNum", true)
            .exec();
        List<AppGtypeGoodsDTO> goodsList = new ArrayList<>();
        for (MktGoods bean : goodsExec.getContent())
        {
            AppGtypeGoodsDTO dto = new AppGtypeGoodsDTO();
            dto.setGoods(bean.getPkey());
            dto.setGoodsTitle(bean.getTitle());
            dto.setXsNum(bean.getXsNum());
            List<MktGoodsSpace> spaceExec = goodsSpaceDao.select().eq("goods", bean.getPkey()).exec();
            for (MktGoodsSpace mgs : spaceExec)
            {
                AppGtypeGoodsSpaceDTO aggsdto = new AppGtypeGoodsSpaceDTO();
                aggsdto.setSpace(mgs.getPkey());
                aggsdto.setPrice(mgs.getPrice());
                aggsdto.setPriceOld(mgs.getPriceOld());
                dto.getGoodsSpaceList().add(aggsdto);
            }
            goodsList.add(dto);
        }
        agd.setGoodsList(goodsList);
    }
    
    public List<PkeyNameDTO> queryGtype(Integer gtype, boolean hasRecommend)
    {
        // 实体列表
        List<MktGoodsMain> entityList =
            goodsMainDao.select().eq("gtype", gtype).eq("enabled", true).eq("idDel", false).sort("sort", false).exec();
        if (Objects.isNull(entityList))
        {
            return Collections.emptyList();
        }
        Map<String, Long> gtypeMap = goodsDao.aggregation()
        .eq("farmer", MobileSession.farmerPkey())
        .eq("enabled", true)
        .eq("idDel", false)
        .eq("mType", MType.MARKET_GOODS)
        .execGroupByCount("goodsMain", "pkey");
        List<PkeyNameDTO> result = new ArrayList<>();
        for (MktGoodsMain m : entityList)
        {
            if (!gtypeMap.containsKey(String.valueOf(m.getPkey()))) continue;
            PkeyNameDTO dto = BeanUtil.beanFrom(PkeyNameDTO.class, m);
            result.add(dto);
        }
        if (result.isEmpty())
            throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODS);
        if (hasRecommend)
        {
            List<GoodsListItemV2> list = mktGoodsRecommendDao.joinSelect()
                .limit(0, 1)
                .isNull(MktGoodsRecommend.F.sourceGoods)
                .eq(MktGoodsRecommend.F.ascription, MobileSession.appid())
                .join(MktGoodsRecommendZone.class, MktGoodsRecommend.F.pkey, MktGoodsRecommendZone.F.goodsRecommend)
                    .eq(MktGoodsRecommendZone.F.zone, GoodsRecommendZone.CATEGORY)
                .join(MktGoods.class, MktGoodsRecommend.F.goods, MktGoods.F.pkey)
                    .as(MktGoods.F.pkey)
                    .as(MktGoods.F.pkey, "goods")
                    .as(MktGoods.F.gtype)
                    .as(MktGoods.F.goodsMain)
                    .as(MktGoods.F.vendor)
                    .eq(MktGoods.F.enabled, true)
                    .eq(MktGoods.F.idDel, false)
                .endJoin()
                .exec(GoodsListItemV2.class);
            if (CollectionUtil.isNotEmpty(list))
                result.add(new PkeyNameDTO(Constant.GoodsMainRecommend.pkey, Constant.GoodsMainRecommend.name));
        }
        return result;
    }
    
    public List<AppGtypeDTO> queryGtypeV2(Boolean showPoint, Boolean showMarket, Boolean flag, MType mtype)
    {
        Integer ascription = MobileSession.appid();
        List<MktGtype> list = null;
        Map<Integer, Integer> map = new HashMap<>();
        Map<String, Long> gtypeCount = null;
        if (showPoint && !showMarket)
        {
            list = gtypeDao.quaryAppPointGtype(Constant.Operation + ascription, ascription);
            if(MType.INTEGRAL_BNYP_GOODS.equals(mtype))
            {
                gtypeCount = goodsDao.aggregation()
                    .eq("farmer", Constant.Operation + ascription)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .in("mType", MType.INTEGRAL_BNYP_GOODS)
                    .execGroupByCount("gtype", "pkey");
            }
            else if(MType.INTEGRAL_MSD_GOODS.equals(mtype))
            {
                
                MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
                if(memberMsd == null)
                    return new ArrayList<>();
                List<Long> goodsMsdKeys = tagVisibleDao.listTarget(TagVisibleTargetType.INTEGRAL_MSD_GOODS,
                    Arrays.asList(memberMsd.getTag()));
                AggregationBuilder<Integer, MktGoods> builder = goodsDao.aggregation()
                    .eq("farmer", Constant.Operation + ascription)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .in("mType", MType.INTEGRAL_MSD_GOODS);
                if (CollectionUtil.isNotEmpty(goodsMsdKeys))
                {
                    // @formatter:off
                    builder.or()
                            .eq("visibleRange", MemberVisibleRange.ALL)
                            .and()
                                .eq("visibleRange", MemberVisibleRange.TAG)
                                .in("pkey", goodsMsdKeys)
                            .close()
                        .close()
                        .done();
                    // @formatter:on
                }
                else
                {
                    builder.eq("visibleRange", MemberVisibleRange.ALL);
                }
                gtypeCount = builder.execGroupByCount("gtype", "pkey");
            }
            else
            {
                gtypeCount = goodsDao.aggregation()
                    .eq("farmer", Constant.Operation + ascription)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .in("mType", MType.INTEGRAL_GOODS, MType.GIFT_GOODS, MType.COUPON_GOODS)
                    .execGroupByCount("gtype", "pkey");
            }
        }
        else
        {
            list = gtypeDao.quaryAppGtype(MobileSession.farmerPkey(), ascription);
            if(Boolean.TRUE.equals(flag))
            {
                gtypeCount = new HashMap<>();
                List<MktVendor> vendorList = vendorDao.listVendor(MobileSession.farmerPkey());
                for(MktVendor e : vendorList)
                {
                    if(StringUtils.isNotBlank(e.getBusinessScope()))
                    {
                        String[] split = e.getBusinessScope().split(",");
                        if (split.length >= 1) 
                        {
                            for(String s : split)
                            {
                                gtypeCount.put(s, 1l);
                            }
                        }
                    }
                }
            }
            else
            {
                gtypeCount = goodsDao.aggregation()
                    .eq("farmer", MobileSession.farmerPkey())
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS)
                    .execGroupByCount("gtype", "pkey");
            }
        }
        Set<String> set = gtypeCount.keySet();
        set.forEach(e -> map.put(Integer.valueOf(e), 1));
        List<AppGtypeDTO> result = new ArrayList<>();
        
        for (MktGtype g : list)
        {
            if (!map.containsKey(g.getPkey())) continue;
            AppGtypeDTO dto = BeanUtil.beanFrom(AppGtypeDTO.class, g);
            result.add(dto);
        }
        return result;
    }
    

    
    /**
     * 商城一二级分类嵌套列表：按 MType 筛选「有商品的分类」。
     * 非 MSD：mkt_goods 按 mType 聚合 goodsMain（INTEGRAL_GOODS 合并礼券/优惠券）。
     * MSD：双源合并 —— mkt_goods(mType=INTEGRAL_MSD_GOODS) + JdGoods(经 twoCategory→JdCategory.mallCategory→MktGoodsMain 反查)，
     * 两源均按会员标签做可见性过滤。
     */
    public List<AppMallGtypeTwoLevelsDTO> listMallTwoLevelsGtype(MType mtype)
    {
        if (!EnumSet.of(MType.INTEGRAL_GOODS, MType.INTEGRAL_PRESALE_GOODS, MType.INTEGRAL_BNYP_GOODS,
            MType.INTEGRAL_MSD_GOODS).contains(mtype))
        {
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "不支持的MType");
        }
        Integer ascription = MobileSession.appid();
        String farmer = Constant.Operation + ascription;
        // gtype(一级) -> goodsMain(二级) pkey 集合
        Map<Integer, Set<Integer>> gtypeGoodsMainMap = new HashMap<>();

        if (MType.INTEGRAL_MSD_GOODS.equals(mtype))
        {
            // MSD 双源合并
            MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
            if (memberMsd == null) return new ArrayList<>();

            // Source A：mkt_goods(mType=INTEGRAL_MSD_GOODS)，按会员标签可见性
            List<Long> msdGoodsKeys = tagVisibleDao.listTarget(TagVisibleTargetType.INTEGRAL_MSD_GOODS,
                Arrays.asList(memberMsd.getTag()));
            AggregationBuilder<Integer, MktGoods> builderA = goodsDao.aggregation()
                .eq(MktGoods.F.farmer, farmer)
                .eq(MktGoods.F.enabled, true)
                .eq(MktGoods.F.idDel, false)
                .in(MktGoods.F.mType, MType.INTEGRAL_MSD_GOODS);
            if (CollectionUtil.isNotEmpty(msdGoodsKeys))
            {
                //@formatter:off
                builderA.or()
                        .eq(MktGoods.F.visibleRange, MemberVisibleRange.ALL)
                        .and()
                            .eq(MktGoods.F.visibleRange, MemberVisibleRange.TAG)
                            .in(MktGoods.F.pkey, msdGoodsKeys)
                        .close()
                    .close()
                    .done();
                //@formatter:on
            }
            else
            {
                builderA.eq(MktGoods.F.visibleRange, MemberVisibleRange.ALL);
            }
            mergeGoodsMainGtype(builderA.execGroupByCount(MktGoods.F.goodsMain, MktGoods.F.pkey).keySet(), gtypeGoodsMainMap);

            // Source B：JdGoods，按会员标签可见性（jdGoodsKeys 可能超 1w，in(pkey) 按 1w 分批聚合后并入 Set 去重）
            List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), ascription);
            List<Long> jdGoodsKeys = tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, listTag);
            // twoCategory 去重
            Set<Long> twoCategoryKeys = new HashSet<>();
            if (jdGoodsKeys.isEmpty())
            {
                collectTwoCategory(twoCategoryKeys, null);
            }
            else
            {
                for (int i = 0; i * 10000 < jdGoodsKeys.size(); i++)
                {
                    int from = i * 10000;
                    int to = Math.min(jdGoodsKeys.size(), (i + 1) * 10000);
                    collectTwoCategory(twoCategoryKeys, jdGoodsKeys.subList(from, to));
                }
            }
            if (!twoCategoryKeys.isEmpty())
            {
                // twoCategory -> mallCategory(MktGoodsMain.pkey)，单列投影后去重
                Set<Integer> mallMainKeys = new HashSet<>(jdCategoryDao.select()
                    .in(JdCategory.F.pkey, twoCategoryKeys)
                    .eq(JdCategory.F.categoryLevel, 1)
                    .eq(JdCategory.F.needShow, 1)
                    .isNotNull(JdCategory.F.mallCategory)
                    .execDto(JdCategory.F.mallCategory, Integer.class));
                if (!mallMainKeys.isEmpty())
                {
                    List<MktGoodsMain> mains = goodsMainDao.select()
                        .in(MktGoodsMain.F.pkey, mallMainKeys)
                        .eq(MktGoodsMain.F.idDel, false)
                        .eq(MktGoodsMain.F.enabled, true)
                        .exec();
                    for (MktGoodsMain m : mains)
                    {
                        if (m.getGtype() != null && m.getPkey() != null)
                        {
                            gtypeGoodsMainMap.computeIfAbsent(m.getGtype(), k -> new HashSet<>())
                                .add(m.getPkey());
                        }
                    }
                }
            }
        }
        else
        {
            // 非 MSD：mkt_goods 按 mType 聚合 goodsMain（INTEGRAL_GOODS 合并礼券/优惠券）
            List<MType> types = MType.INTEGRAL_GOODS.equals(mtype)
                ? Arrays.asList(MType.INTEGRAL_GOODS, MType.GIFT_GOODS, MType.COUPON_GOODS)
                : Collections.singletonList(mtype);
            Map<String, Long> count = goodsDao.aggregation()
                .eq(MktGoods.F.farmer, farmer)
                .eq(MktGoods.F.enabled, true)
                .eq(MktGoods.F.idDel, false)
                .in(MktGoods.F.mType, types)
                .execGroupByCount(MktGoods.F.goodsMain, MktGoods.F.pkey);
            mergeGoodsMainGtype(count.keySet(), gtypeGoodsMainMap);
        }

        // 组装嵌套结果
        List<AppMallGtypeTwoLevelsDTO> result = new ArrayList<>();
        if (gtypeGoodsMainMap.isEmpty()) return result;
        List<MktGtype> gtypeList = gtypeDao.quaryAppPointGtype(farmer, ascription);
        for (MktGtype g : gtypeList)
        {
            Set<Integer> goodsMainSet = gtypeGoodsMainMap.get(g.getPkey());
            if (goodsMainSet == null || goodsMainSet.isEmpty()) continue;
            AppMallGtypeTwoLevelsDTO dto = BeanUtil.beanFrom(AppMallGtypeTwoLevelsDTO.class, g);
            List<MktGoodsMain> goodsMainList = goodsMainDao.listSortFalse(g.getPkey(), true, farmer, ascription);
            for (MktGoodsMain gm : goodsMainList)
            {
                if (goodsMainSet.contains(gm.getPkey()))
                {
                    dto.getSecond().add(new PkeyNameDTO(gm.getPkey(), gm.getName()));
                }
            }
            if (!dto.getSecond().isEmpty()) result.add(dto);
        }
        return result;
    }

    /**
     * 将「有商品的 goodsMain pkey 集合」解析为其所属一级 gtype，并入 gtypeGoodsMainMap。
     */
    private void mergeGoodsMainGtype(Set<String> goodsMainKeys, Map<Integer, Set<Integer>> gtypeGoodsMainMap)
    {
        Set<Integer> keys = new HashSet<>();
        for (String key : goodsMainKeys)
        {
            if (StringUtils.isNotBlank(key)) keys.add(Integer.valueOf(key));
        }
        if (keys.isEmpty()) return;
        List<MktGoodsMain> list = goodsMainDao.select().in("pkey", keys).eq("idDel", false).exec();
        for (MktGoodsMain gm : list)
        {
            if (gm.getGtype() == null) continue;
            gtypeGoodsMainMap.computeIfAbsent(gm.getGtype(), k -> new HashSet<>()).add(gm.getPkey());
        }
    }

    /**
     * JD 商品按会员标签可见性聚合 twoCategory：jdGoodsKeys 为 null/空时仅查 ALL 范围（无 in，不受 1w 限制）；
     * 非空时附加 TAG 范围 + in(pkey)（调用方按 1w 分批）。ALL 范围商品会在多批重复，靠 dst(Set) 自动去重。
     */
    private void collectTwoCategory(Set<Long> dst, List<Long> jdGoodsKeys)
    {
        AggregationBuilder<Long, JdGoods> builderB =
            jdGoodsDao.aggregation().eq(JdGoods.F.idDel, false).eq(JdGoods.F.enabled, true).eq(JdGoods.F.skuState, 1);
        if (CollectionUtil.isNotEmpty(jdGoodsKeys))
        {
            //@formatter:off
            builderB.or()
                    .eq(JdGoods.F.visibleRange, MemberVisibleRange.ALL)
                    .and()
                        .eq(JdGoods.F.visibleRange, MemberVisibleRange.TAG)
                        .in(JdGoods.F.pkey, jdGoodsKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            builderB.eq(JdGoods.F.visibleRange, MemberVisibleRange.ALL);
        }
        for (String key : builderB.execGroupByCount(JdGoods.F.twoCategory, JdGoods.F.pkey).keySet())
        {
            if (StringUtils.isNotBlank(key)) dst.add(Long.valueOf(key));
        }
    }

    public List<PkeyNameDTO> queryVendorGtype(Integer vendor)
    {
        Map<String, Long> gtypeMap = goodsDao.aggregation()
        .eq("farmer", MobileSession.farmerPkey())
        .eq("enabled", true)
        .eq("idDel", false)
        .eq("vendor", vendor)
        .execGroupByCount("goodsMain", "pkey");
        List<PkeyNameDTO> result = new ArrayList<>();
        List<MktGoodsMain> list = goodsMainDao.listSortFalse(null, true, MobileSession.farmerPkey(), MobileSession.appid());
        for(MktGoodsMain gm : list)
        {
            PkeyNameDTO dto = new PkeyNameDTO();
            if(gtypeMap.containsKey(gm.getPkey().toString()))
            {
                dto.setPkey(gm.getPkey());
                dto.setName(gm.getName());
                result.add(dto);
            }
        }
        if (result.isEmpty()) throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODS);
        
        return result;
    }
    
    public List<PkeyNameDTO> queryVendorOneGtype()
    {
        List<MktVendor> list = vendorDao.select()  
        .eq("farmer", MobileSession.farmerPkey())
        .eq("ascription", MobileSession.appid())
        .eq("enabled", true)
        .eq("idDel", false)
        .isNotNull("businessScope")
        .exec();
        if (list.isEmpty()) throw TofocusException.of(LejiaErrCode.GTYPE_NOT_VENDOR);
        Map<Integer,Integer> map = new HashMap<>();
        for(MktVendor v : list)
        {
            if(StringUtils.isNotBlank(v.getBusinessScope()))
            {
                String[] split = v.getBusinessScope().split(",");
                for(String s : split)
                {
                    if(!map.containsKey(Integer.valueOf(s)))
                        map.put(Integer.valueOf(s), 1);
                }
            }
        }
        List<PkeyNameDTO> res = new ArrayList<>();
        List<MktGtype> exec = gtypeDao.select().in("pkey", map.keySet()).exec();
        for(MktGtype g : exec)
        {
            PkeyNameDTO dto = new PkeyNameDTO();
            dto.setPkey(g.getPkey());
            dto.setName(g.getName());
            res.add(dto);
        }
        if (res.isEmpty()) throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODS);
        return res;
    }
    
}
