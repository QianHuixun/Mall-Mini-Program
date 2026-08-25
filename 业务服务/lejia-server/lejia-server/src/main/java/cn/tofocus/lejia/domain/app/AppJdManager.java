package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.tofocus.db.aggs.AggregationBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.jd.open.api.sdk.domain.vopdz.ConvertAddressOpenProvider.response.convertFourAreaByLatLng.QueryAreaFourIdOpenResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkusAllSaleState.GetSkuCanSaleResp;

import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsDetails;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsOnPage;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryDrop;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsService;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdGoodsServiceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPGoodsManager;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPSkuNum;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppJdManager
{
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private JdGoodsServiceDao jdGoodsServiceDao;
    
    @Autowired
    private MktAddrDao addrDao;

    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;
    
    public List<JdCategoryDrop> categoryDrop()
    {
        if (MobileSession.memberPkey() == null)
            return new ArrayList<>();
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null)
            return new ArrayList<>();
        List<Long> goodsMsdKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, Lists.newArrayList(memberMsd.getTag()));
        
        Map<Long, JdCategoryDrop> map = new HashMap<>();
        for (int i = 0; i * 10000 < goodsMsdKeys.size(); i++)
        {
            int from = i * 10000;
            int to = Math.min(goodsMsdKeys.size(), (i + 1) * 10000);
            List<JdCategoryDrop> list = categoryDropQuery(goodsMsdKeys.subList(from, to));
            mergeCategoryDrop(map, list);
        }
        
        return new ArrayList<>(map.values());
    }
    
    private List<JdCategoryDrop> categoryDropQuery(List<Long> goodsMsdKeys)
    {
        AggregationBuilder<Long, JdGoods> builder = jdGoodsDao.aggregation()
            .pagesize(10000)
            .eq(JdGoods.F.idDel, false)
            .eq(JdGoods.F.enabled, true)
            .eq(JdGoods.F.skuState, 1);
        if (CollectionUtils.isNotEmpty(goodsMsdKeys))
        {
            //@formatter:off
            builder.or()
                    .eq("visibleRange", MemberVisibleRange.ALL)
                    .and()
                        .eq("visibleRange", MemberVisibleRange.TAG)
                        .in("pkey", goodsMsdKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            builder.eq("visibleRange", MemberVisibleRange.ALL);
        }
        return builder.groupby("category", "pkey")
            .groupby("categoryName", "categoryName")
            .execListDto(JdCategoryDrop.class);
    }
    
    private void mergeCategoryDrop(Map<Long, JdCategoryDrop> target, List<JdCategoryDrop> source)
    {
        for (JdCategoryDrop item : source)
        {
            if (!target.containsKey(item.getPkey()))
            {
                if ("家庭清洁/纸品".equals(item.getCategoryName()))
                    item.setCategoryName("家庭清洁");
                if ("文教文化用品".equals(item.getCategoryName()))
                    item.setCategoryName("文具");
                target.put(item.getPkey(), item);
            }
        }
    }
    
    public PageResult<AppJdGoodsOnPage> queryGoods(int page, int pagesize, Long category)
    {
        if (MobileSession.memberPkey() == null)
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null)
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<Long> goodsMsdKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, Lists.newArrayList(memberMsd.getTag()));
        
        Map<Long, AppJdGoodsOnPage> map = new HashMap<>();
        for (int i = 0; i * 10000 < goodsMsdKeys.size(); i++)
        {
            int from = i * 10000;
            int to = Math.min(goodsMsdKeys.size(), (i + 1) * 10000);
            List<AppJdGoodsOnPage> list = queryGoodsQuery(category, goodsMsdKeys.subList(from, to));
            mergeGoods(map, list);
        }
        
        PageResult<AppJdGoodsOnPage> res =
            PageUtil.page(new ArrayList<>(map.values()), PageParameter.of(page, pagesize));
        for (AppJdGoodsOnPage jg : res.getContent())
        {
            JdGoods g = jdGoodsDao.bySkuId(jg.getPkey());
            jg.setPkey(g.getPkey());
            jg.setTitle(g.getTitle());
            jg.setPhoto1(g.getPhoto1());
            jg.setPrice(g.getPrice());
            jg.setGwcNum(0);
            jg.setLowestBuy(g.getLowestBuy());
            MktGwc bean = gwcDao.getJdGwcMember(g.getPkey(), MobileSession.memberPkey());
            if (bean != null)
            {
                jg.setGwcNum(bean.getNum());
            }
        }
        return res;
    }
    
    private List<AppJdGoodsOnPage> queryGoodsQuery(long category, List<Long> goodsMsdKeys)
    {
        AggregationBuilder<Long, JdGoods> builder =
            jdGoodsDao.aggregation().eq("idDel", false).eq("enabled", true).eq("skuState", 1).eq("category", category);
        if (CollectionUtils.isNotEmpty(goodsMsdKeys))
        {
            //@formatter:off
            builder.or()
                    .eq("visibleRange", MemberVisibleRange.ALL)
                    .and()
                        .eq("visibleRange", MemberVisibleRange.TAG)
                        .in("pkey", goodsMsdKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            builder.eq("visibleRange", MemberVisibleRange.ALL);
        }
        return builder.groupby("spuId", "spuId").min("pkey", "pkey").execListDto(AppJdGoodsOnPage.class);
    }
    
    private void mergeGoods(Map<Long, AppJdGoodsOnPage> target, List<AppJdGoodsOnPage> source)
    {
        for (AppJdGoodsOnPage item : source)
        {
            AppJdGoodsOnPage exist = target.get(item.getSpuId());
            if (exist == null)
            {
                target.put(item.getSpuId(), item);
            }
            else if (item.getPkey() < exist.getPkey())
            {
                exist.setPkey(item.getPkey());
            }
        }
    }
    
    public PageResult<AppJdGoodsOnPage> byTitleGoods(int page, int pagesize, String title)
    {
        if (MobileSession.memberPkey() == null)
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null)
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<Long> goodsMsdKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, Lists.newArrayList(memberMsd.getTag()));
        
        Map<Long, AppJdGoodsOnPage> map = new HashMap<>();
        for (int i = 0; i * 10000 < goodsMsdKeys.size(); i++)
        {
            int from = i * 10000;
            int to = Math.min(goodsMsdKeys.size(), (i + 1) * 10000);
            List<AppJdGoodsOnPage> list = byTitleGoodsQuery(title, goodsMsdKeys.subList(from, to));
            mergeByTitleGoods(map, list);
        }
        
        PageResult<AppJdGoodsOnPage> res =
            PageUtil.page(new ArrayList<>(map.values()), PageParameter.of(page, pagesize));
        for (AppJdGoodsOnPage jg : res.getContent())
        {
            JdGoods g = jdGoodsDao.bySkuId(jg.getPkey());
            jg.setPkey(g.getPkey());
            jg.setTitle(g.getTitle());
            jg.setPhoto1(g.getPhoto1());
            if (g.getPhoto1() != null && !g.getPhoto1().isEmpty())
                jg.setWrapperPhoto(g.getPhoto1().get(0));
            jg.setPrice(g.getPrice());
            jg.setLowestBuy(g.getLowestBuy());
            jg.setGwcNum(0);
            MktGwc bean = gwcDao.getJdGwcMember(g.getPkey(), MobileSession.memberPkey());
            if (bean != null)
            {
                jg.setGwcNum(bean.getNum());
            }
        }
        return res;
    }
    
    private List<AppJdGoodsOnPage> byTitleGoodsQuery(String title, List<Long> goodsMsdKeys)
    {
        AggregationBuilder<Long, JdGoods> builder =
            jdGoodsDao.aggregation().eq("idDel", false).eq("enabled", true).eq("skuState", 1).like("title", title);
        if (CollectionUtils.isNotEmpty(goodsMsdKeys))
        {
            //@formatter:off
            builder.or()
                .eq("visibleRange", MemberVisibleRange.ALL)
                    .and()
                        .eq("visibleRange", MemberVisibleRange.TAG)
                        .in("pkey", goodsMsdKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            builder.eq("visibleRange", MemberVisibleRange.ALL);
        }
        return builder.groupby(JdGoods.F.spuId, JdGoods.F.spuId)
            .min(JdGoods.F.pkey, JdGoods.F.pkey)
            .execListDto(AppJdGoodsOnPage.class);
    }
    
    private void mergeByTitleGoods(Map<Long, AppJdGoodsOnPage> target, List<AppJdGoodsOnPage> source)
    {
        for (AppJdGoodsOnPage item : source)
        {
            if (!target.containsKey(item.getSpuId()))
            {
                target.put(item.getSpuId(), item);
            }
        }
    }
    
    public List<AppJdGoodsDetails> getGoodsDetails(Long pkey)
    {
        List<AppJdGoodsDetails> res = new ArrayList<>();
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null)
            return res;
        List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), MobileSession.appid());
        AppJdGoodsDetails defaultGoods = jdGoodsDao.selectOne().eq("pkey", pkey).execDto(AppJdGoodsDetails.class);
        if (defaultGoods == null)
            return res;
        List<Integer> defaultGoodsTag = tagVisibleDao.listTagKeys(TagVisibleTargetType.JD_GOODS, pkey);
        // 如果 默认商品 和 用户标签 不匹配，直接返回
        if (defaultGoods.getVisibleRange() != MemberVisibleRange.ALL
            && !CollectionUtils.containsAny(defaultGoodsTag, listTag))
            return res;
        res.add(defaultGoods);
        
        List<AppJdGoodsDetails> list = jdGoodsDao.select()
            .eq("spuId", defaultGoods.getSpuId())
            .eq("idDel", false)
            .eq("enabled", true)
            .notEq("pkey", pkey)
            .execDto(AppJdGoodsDetails.class);
        // 查出所有sku标签关联关系
        List<Long> skuIds = list.stream().map(AppJdGoodsDetails::getPkey).collect(Collectors.toList());
        log.info("[京东商品详情] skuIds: {}", skuIds);
        Set<Long> filteredSkuIds = tagVisibleDao.filterVisibleTargets(TagVisibleTargetType.JD_GOODS, skuIds, listTag);
        log.info("[京东商品详情] filteredSkuIds: {}", filteredSkuIds);
        // 遍历sku，过滤标签
        for (AppJdGoodsDetails sku : list)
        {
            if (sku.getVisibleRange() == MemberVisibleRange.ALL || filteredSkuIds.contains(sku.getPkey()))
                res.add(sku);
        }
        
        for (AppJdGoodsDetails jg : res)
        {
            JdGoodsSpace jdGoodsSpace = jdGoodsSpaceDao.get(jg.getPkey());
            if (jdGoodsSpace != null)
                BeanUtils.copyProperties(jdGoodsSpace, jg);
        }
        return res;
    }
    
    public List<String> getGoodsContent()
    {
        List<JdGoodsService> all = jdGoodsServiceDao.findAll();
        List<String> res = new ArrayList<>();
        for(JdGoodsService gs : all)
        {
            res.add(gs.getContent());
        }
        return res;
    }
    
    public Boolean gwcIns(long pkey, int goodsNum, BigDecimal longitude, BigDecimal latitude)
    {
        JdGoods jdGoods = jdGoodsDao.get(pkey);
        if (jdGoods == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        JdVOPSkuNum jdVOPSkuNum = new JdVOPSkuNum();
        jdVOPSkuNum.setSkuId(pkey);
        jdVOPSkuNum.setSkuNum(goodsNum);
        skuNumInfoList.add(jdVOPSkuNum);
//        skuList.add(line.getSpace());
        Integer memberPkey = MobileSession.memberPkey();
        if(memberPkey == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_MANAGER_ERROR);
        MktAddr addrObj = addrDao.getDefaultAddrDelivery(memberPkey);
        JdVOPAreaInfo areaInfo;
        try
        {
            if(addrObj == null)
            {
                if(longitude == null || latitude == null)
                {
                    throw TofocusException.of(LejiaErrCode.JD_GOODS_ADDR_LONGITUDE_LATITUDE_ERROR);
                }
                QueryAreaFourIdOpenResp qaf = jdVOPAddrManager.convertFourAreaByLatLng(longitude.doubleValue(), latitude.doubleValue());
                areaInfo = new JdVOPAreaInfo(qaf.getProvinceId(), qaf.getCityId(), qaf.getCountyId());
                areaInfo.setTownId(qaf.getTownId());
            }
            else
            {
                if(StringUtils.isBlank(addrObj.getTown()))
                {
                    QueryAreaFourIdOpenResp qaf = jdVOPAddrManager.convertFourAreaByLatLng(addrObj.getLongitude().doubleValue(), addrObj.getLatitude().doubleValue());
                    areaInfo = new JdVOPAreaInfo(qaf.getProvinceId(), qaf.getCityId(), qaf.getCountyId());
                    areaInfo.setTownId(qaf.getTownId());
                }
                else
                {
                    areaInfo = jdVOPAddrManager.convert2AreaInfo(addrObj);
                }
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            throw TofocusException.of(LejiaErrCode.JD_GOODS_ADDR_LONGITUDE_LATITUDE_ERROR);
        }
        List<GetSkuCanSaleResp> skusAllSaleState = jdVOPGoodsManager.getSkusAllSaleState(skuNumInfoList, areaInfo);
        // 检查京东商品是否可采(包含是否在商品池、是否主站上架状态、是否预约预售、是否合同支持购买此商品、是否区域限售)
        for (GetSkuCanSaleResp gscsr : skusAllSaleState)
        {
            if (Boolean.FALSE.equals(gscsr.getCanPurchase()))
            {
                jdGoods.setEnabled(false);
                jdGoodsDao.update(jdGoods);
                log.error("检验商品,京东显示不可售,下架商城商品,商品主键: {}", pkey);
                throw TofocusException.of(LejiaErrCode.JD_GOODS_GWC_ERROR);
//                throw TofocusException.of(LejiaErrCode.JD_GOODS_GWC_ERROR, gscsr.getMessage() + " 商品名称: " + jdGoods.getTitle());
            }
        }
        
        MktGwc bean = gwcDao.getJdGwcMember(pkey, MobileSession.memberPkey());
        if(bean == null)
        {
            bean = new MktGwc();
            bean.setIsJd(true);
            bean.setSkuId(pkey);
            bean.setSpuId(jdGoods.getSpuId());
            bean.setAscription(MobileSession.appid());
            bean.setFarmer(Constant.Operation + MobileSession.appid());
            bean.setCompany(Constant.Operation + MobileSession.appid());
            bean.setNum(goodsNum);
            bean.setMember(MobileSession.memberPkey());
        }
        else
            bean.setNum(bean.getNum() + goodsNum);
        gwcDao.put(bean);
        return true;
    }
    
    public Boolean addGwcNum(long pkey, int goodsNum)
    {
        JdGoods jdGoods = jdGoodsDao.get(pkey);
        if (jdGoods == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
        MktGwc bean = gwcDao.getJdGwcMember(pkey, MobileSession.memberPkey());
        bean.setNum(bean.getNum() + goodsNum);
        gwcDao.put(bean);
        return true;
    }
    
    public Boolean lessGwcNum(long pkey, int goodsNum)
    {
        MktGwc bean = gwcDao.getJdGwcMember(pkey, MobileSession.memberPkey());
        if(bean != null)
        {
            int i = bean.getNum() - goodsNum;
            if (i <= 0)
            {
                gwcDao.remove(bean);
            }
            else
            {
                bean.setNum(i);
                gwcDao.update(bean);
            }
        }
        return true;
    }
}
