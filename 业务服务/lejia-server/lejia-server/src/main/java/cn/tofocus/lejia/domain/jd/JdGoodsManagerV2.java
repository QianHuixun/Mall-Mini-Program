package cn.tofocus.lejia.domain.jd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.google.common.collect.Sets;
import com.jd.open.api.sdk.domain.vopsp.CategoryInfoGoodsProvider.response.getCategoryInfoList.GetCategoryInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkSkuSaleList.CheckSkuSaleGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSellPrice.GetSellPriceGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.GetSimilarSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.SaleLabelSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuDetailInfo.GetSkuPoolInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.GetSkuImageGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.SkuImageItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.getSkuPoolInfo.GetSkuPoolInfoItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.querySkuByPage.OpenPagingResult;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.entity.jd.JdCategory;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsUpdNotice;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.dao.jd.JdCategoryDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdGoodsSpaceDao;
import cn.tofocus.lejia.domain.jdvop.JdVOPGoodsManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdGoodsManagerV2
{
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;
    
    @Autowired
    private JdCategoryDao jdCategoryDao;
    
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    /**
     * 同步商品数据 
     * 分批处理
     * 规格最后处理
     * @param bizPoolId
     */
    public void runJdGoodsInfoV2(String bizPoolId)
    {
        long k1 = System.currentTimeMillis();
        List<String> bizPoolIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(bizPoolId))
            bizPoolIdList.add(bizPoolId);
        else
        {
            List<GetSkuPoolInfoItemGoodsResp> list = jdVOPGoodsManager.getSkuPoolInfo();
            for (GetSkuPoolInfoItemGoodsResp g : list)
                bizPoolIdList.add(g.getBizPoolId());
        }
        System.out.println("bizPoolIdListV2: " + JsonUtil.toString(bizPoolIdList, true));
        int goodsSum = 0;
        for (String bpi : bizPoolIdList)
        {
            Long remainPage = 1L;
            long offset = 0L;
            while (remainPage != 0L)
            {
                OpenPagingResult querySkuByPage = jdVOPGoodsManager.querySkuByPage(bpi, offset, 100);
                remainPage = querySkuByPage.getRemainPage();
                offset = querySkuByPage.getOffset();
                List<Long> skus = querySkuByPage.getSkus();
                log.info("商品查询数量: {}", skus.size());
                List<JdGoods> byNotGoodsIdDel = jdGoodsDao.byNotGoodsIdDel(skus);
                if(byNotGoodsIdDel != null && !byNotGoodsIdDel.isEmpty())
                {
                    for(JdGoods jg : byNotGoodsIdDel)
                    {
                        jg.setIdDel(false);
                    }
                    jdGoodsDao.updateAll(byNotGoodsIdDel);
                }
                List<Long> klist = jdGoodsDao.byNotGoods(skus);
                List<JdGoods> addList = new ArrayList<>();
                for (Long k : klist)
                {
                    JdGoods g = new JdGoods();
                    g.setAscription(qfAscription);
                    g.setPkey(k);
                    g.setBizPoolId(bpi);
                    addList.add(g);
                }
                log.info("新商品数量: " + addList.size());
                goodsSum += skus.size();
                jdGoodsDao.putAll(addList);
            }
            long k2 = System.currentTimeMillis();
            log.info("拉取商品耗时: {}", (k2 - k1));
            setGoodsDetailInfo();
            long k3 = System.currentTimeMillis();
            log.info("设置商品详情耗时: {}", (k3 - k2));
            setGoodsSpace();
            long k4 = System.currentTimeMillis();
            log.info("设置商品规格耗时: {}", (k4 - k3));
            removeNoSaleStateGoods();
            long k5 = System.currentTimeMillis();
            log.info("设置商品规格耗时: {}", (k5 - k4));
        }
        long k6 = System.currentTimeMillis();
        log.info("从京东全部拉取的数据总共: {}条, 合计用时: {}", goodsSum, (k6 - k1));
    }
    
    // 设置商品规格
    private void setGoodsSpace()
    {
        List<JdGoods> all = jdGoodsDao.notIdDel();
        Map<Long, List<JdGoods>> map = new HashMap<>();
        all.forEach(e -> {
            if (!map.containsKey(e.getSpuId()))
            {
                map.put(e.getSpuId(), new ArrayList<>());
            }
            map.get(e.getSpuId()).add(e);
        });
        Map<Long, JdGoodsSpace> gsMap = new HashMap<>();
        int f = 0;
        for (Long key : map.keySet())
        {
            for (JdGoods jg : map.get(key))
            {
                log.info("设置京东商品规格: {}", f++);
                try
                {
                    List<GetSimilarSkuGoodsResp> list = jdVOPGoodsManager.getSimilarSkuList(jg.getPkey());
                    if (list != null && !list.isEmpty())
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            GetSimilarSkuGoodsResp gssgr = list.get(i);
                            setSpace(i, jg, gsMap, gssgr);
                        }
                    }
                }
                catch (Exception e2)
                {
                    log.error("jdVOPGoodsManager.getSimilarSkuList 接口有问题", e2.getMessage());
                }
            }
            jdGoodsDao.updateAll(map.get(key));
        }
        List<JdGoodsSpace> gsList = gsMap.values().stream().collect(Collectors.toList());
        if(gsList != null && !gsList.isEmpty())
            jdGoodsSpaceDao.putAll(gsList);
    }
    
    private void setSpace(int i, JdGoods jg, Map<Long, JdGoodsSpace> gsMap, GetSimilarSkuGoodsResp gssgr)
    {
        int j = i + 1;
        Map<String, SaleLabelSkuGoodsResp> saleLabelMap = gssgr.getSaleLabelMap();
        switch (j)
        {
            case 1:
                jg.setSpace1(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue1(key);
                    }
                }
                break;
            case 2:
                jg.setSpace2(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue2(key);
                    }
                }
                break;
            case 3:
                jg.setSpace3(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue3(key);
                    }
                }
                break;
            case 4:
                jg.setSpace4(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue4(key);
                    }
                }
                break;
            case 5:
                jg.setSpace5(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue5(key);
                    }
                }
                break;
            case 6:
                jg.setSpace6(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue6(key);
                    }
                }
                break;
            case 7:
                jg.setSpace7(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue7(key);
                    }
                }
                break;
            case 8:
                jg.setSpace8(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue8(key);
                    }
                }
                break;
            case 9:
                jg.setSpace9(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue9(key);
                    }
                }
                break;
            case 10:
                jg.setSpace10(gssgr.getSaleName());
                for (String key : saleLabelMap.keySet())
                {
                    SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                    for (Long k : resp.getSkuIdSet())
                    {
                        JdGoodsSpace gs;
                        if (gsMap.containsKey(k))
                        {
                            gs = gsMap.get(k);
                        }
                        else
                        {
                            gs = new JdGoodsSpace();
                            gs.setPkey(k);
                            gs.setBizPoolId(jg.getBizPoolId());
                            gsMap.put(k, gs);
                        }
                        gs.setSpaceValue10(key);
                    }
                }
                break;
        }
    }
    
    // 设置商品详情
    private void setGoodsDetailInfo()
    {
        List<JdGoods> all = jdGoodsDao.notIdDel();
        Map<Long, String> map = jdCategoryDao.allMap();
        
        // 批量处理图片和价格
        List<List<JdGoods>> splitList = splitList(all, 100);
        for (List<JdGoods> gl : splitList)
        {
            for (JdGoods g : gl)
            {
                try
                {
                    GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(g.getPkey(), Sets.newHashSet(3));
//                    log.info("s: {}", JsonUtil.toString(s, true));
                    setCate(s.getCategory(), g, map);
                    String title = specialTitle(s.getSkuName());
                    g.setTitle(title);
                    g.setWeight(s.getWeight());
                    g.setSaleUnit(s.getSaleUnit());
                    g.setSeoModel(s.getSeoModel());
                    g.setIntroduce(s.getIntroduce());
                    g.setIntroducePc(s.getIntroducePc());
                    g.setIntroduceApp(s.getIntroduceApp());
                    g.setIntroduceWechat(s.getIntroduceWechat());
                    g.setSkuState(s.getSkuState());
                    g.setSpuId(s.getSpuId());
                    g.setSpuName(s.getSpuName());
                    g.setVisibleRange(MemberVisibleRange.ALL);
                    g.setLowestBuy(s.getLowestBuy());
                    if (g.getSort() == null) g.setSort(0);
                    if (StringUtils.isBlank(g.getFarmer())) g.setFarmer(Constant.Operation + qfAscription);
                    if (g.getEnabled() == null) g.setEnabled(false);
                    if (g.getIdDel() == null) g.setIdDel(false);
                    if (g.getAscription() == null) g.setAscription(qfAscription);
                }
                catch (Exception e)
                {
                    log.error("京东获取详情的接口报错,报错商品主键: {}", g.getPkey());
                    throw e;
                }
            }
            // 处理图片
            setPhoto(gl);
            // 处理价格
            setPrice(gl);
            jdGoodsDao.updateAll(gl);
        }
    }
    
    private String specialTitle(String title)
    {
        List<String> t = new ArrayList<>();
        t.add("鲜京采");
        t.add("京东京造");
        t.add("京觅");
        t.add("京东");
        t.add("京东自营");
        t.add("京东物流");
        t.add("京东专供");
        t.add("京东金榜");
        t.add("京东超市");
        t.add("京鲜生");
        t.add("1号会员店");
        for(String k : t)
        {
            boolean contains = title.contains(k);
            if(contains)
            {
                title = title.replace(k, "");
            }
        }
        return title;
    }
    
    private void setPrice(List<JdGoods> gl)
    {
        List<Long> skuIdList = new ArrayList<>();
        gl.forEach(e -> skuIdList.add(e.getPkey()));
        List<GetSellPriceGoodsResp> sellPrice = jdVOPGoodsManager.getSellPrice(skuIdList);
        if (!sellPrice.isEmpty())
        {
            List<JdGoodsUpdNotice> noticeList = new ArrayList<>();
            Map<Long, GetSellPriceGoodsResp> map = new HashMap<>();
            sellPrice.forEach(e -> map.put(e.getSkuId(), e));
            for (JdGoods g : gl)
            {
                if (map.containsKey(g.getPkey()))
                {
                    BigDecimal originSalePrice = g.getSalePrice();
                    GetSellPriceGoodsResp spgr = map.get(g.getPkey());
                    g.setTaxPrice(spgr.getTaxPrice());
                    g.setJdPrice(spgr.getJdPrice());
                    g.setSalePrice(spgr.getSalePrice());
                    g.setNakedPrice(spgr.getNakedPrice());
                    g.setTaxRatePercentage(spgr.getTaxRatePercentage());
                    g.setHasPromotion(spgr.getHasPromotion());
                    g.setPromotionType(spgr.getPromotionType());
                    if (spgr.getFixedPricePromotion() != null)
                    {
                        g.setOriginalPrice(spgr.getFixedPricePromotion().getOriginalPrice());
                        g.setLimitedNum(spgr.getFixedPricePromotion().getLimitedNum());
                        g.setRemainNum(spgr.getFixedPricePromotion().getRemainNum());
                    }
                    if (g.getPrice() == null) g.setPrice(g.getSalePrice());
                    if (spgr.getSalePrice() != null && originSalePrice != null)
                    {
                        int compareTo = spgr.getSalePrice().compareTo(originSalePrice);
                        // 京东销售价变动，记录变动通知
                        if (compareTo != 0)
                        {
                            JdGoodsUpdNotice notice = JdGoodsUpdNotice.priceOf(g);
                            StringBuilder desc = new StringBuilder();
                            // 京东销售价上升，商品下架
                            if (spgr.getSalePrice().compareTo(originSalePrice) > 0)
                            {
                                desc.append("京东价上升");
                                if (!Boolean.FALSE.equals(g.getEnabled()))
                                {
                                    log.info("京东商品（sku：{}）的京东销售价从{}上升为{}，已下架处理",
                                        g.getPkey(),
                                        originSalePrice,
                                        spgr.getSalePrice());
                                    g.setEnabled(false);
                                    desc.append("，已自动下架");
                                }
                            }
                            else
                            {
                                desc.append("京东价下降");
                            }
                            notice.setDescription(desc.toString());
                            noticeList.add(notice);
                        }
                    }
                }
            }
        }
    }
    
    private void setPhoto(List<JdGoods> gl)
    {
        List<Long> skuIdList = new ArrayList<>();
        gl.forEach(e -> skuIdList.add(e.getPkey()));
        List<GetSkuImageGoodsResp> skuImageList = jdVOPGoodsManager.getSkuImageList(skuIdList);
        Map<Long, GetSkuImageGoodsResp> map = new HashMap<>();
        skuImageList.forEach(e -> map.put(e.getSkuId(), e));
        for (JdGoods g : gl)
        {
            if (map.containsKey(g.getPkey()))
            {
                GetSkuImageGoodsResp sigr = map.get(g.getPkey());
                List<String> photo1 = new ArrayList<>();
                List<SkuImageItemGoodsResp> imageList = sigr.getSkuImageList();
                for (SkuImageItemGoodsResp e : imageList)
                    photo1.add(completePhoto(e.getShortPath()));
                if (photo1.size() > 30) photo1 = photo1.subList(0, 30);
                g.setPhoto1(photo1);
            }
        }
    }
    
    public String completePhoto(String path)
    {
        return "https://img13.360buyimg.com/n12/" + path;
    }
    
    // 设置分类
    public void setCate(String cate, JdGoods g, Map<Long, String> map)
    {
        if (StringUtils.isNotBlank(cate))
        {
            log.info("cate: {}", cate);
            String[] split = cate.split(";");
            if (split.length == 3)
            {
                g.setCategory(Long.valueOf(split[0]));
                g.setTwoCategory(Long.valueOf(split[1]));
                g.setThreeCategory(Long.valueOf(split[2]));
                Set<Long> categoryIds = new HashSet<>();
                if (map.containsKey(g.getCategory()))
                    g.setCategoryName(map.get(g.getCategory()));
                else
                {
                    categoryIds.add(g.getCategory());
                }
                if (map.containsKey(g.getTwoCategory()))
                    g.setTwoCategoryName(map.get(g.getTwoCategory()));
                else
                {
                    categoryIds.add(g.getTwoCategory());
                }
                if (map.containsKey(g.getThreeCategory()))
                    g.setThreeCategoryName(map.get(g.getThreeCategory()));
                else
                {
                    categoryIds.add(g.getThreeCategory());
                }
                if (!categoryIds.isEmpty())
                {
                    addCate(categoryIds, map);
                    g.setCategoryName(map.get(g.getCategory()));
                    g.setTwoCategoryName(map.get(g.getTwoCategory()));
                    g.setThreeCategoryName(map.get(g.getThreeCategory()));
                }
            }
        }
    }
    
    private void addCate(Set<Long> categoryIds, Map<Long, String> map)
    {
        List<GetCategoryInfoGoodsResp> categoryInfoList = jdVOPGoodsManager.getCategoryInfoList(categoryIds);
        if (!categoryInfoList.isEmpty())
        {
            for (GetCategoryInfoGoodsResp categoryInfoGoodsResp : categoryInfoList)
            {
                JdCategory jc = new JdCategory();
                jc.setAscription(qfAscription);
                jc.setCategoryLevel(categoryInfoGoodsResp.getCategoryLevel());
                jc.setParentId(categoryInfoGoodsResp.getParentId());
                jc.setCategoryName(categoryInfoGoodsResp.getCategoryName());
                jc.setNeedShow(categoryInfoGoodsResp.getNeedShow());
                jc.setOrderSort(categoryInfoGoodsResp.getOrderSort());
                jc.setPkey(categoryInfoGoodsResp.getCategoryId());
                jdCategoryDao.add(jc);
                map.put(jc.getPkey(), jc.getCategoryName());
            }
        }
    }
    
    // 删除不可售的商品
    public void removeNoSaleStateGoods()
    {
        List<JdGoods> list = jdGoodsDao.salePriceIsNull();
        log.info("价格为空的商品数据: {}", JsonUtil.toString(list, true));
        List<Long> keyList = CollectionUtil.keyList(list);
        List<CheckSkuSaleGoodsResp> checkSkuSaleList = jdVOPGoodsManager.checkSkuSaleList(keyList);
        Map<Long,JdGoods> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        for(CheckSkuSaleGoodsResp cssgr : checkSkuSaleList)
        {
            if(map.containsKey(cssgr.getSkuId()))
            {
                int saleState = cssgr.getSaleState();
                if(saleState == 0)
                {
                    JdGoods jdGoods = map.get(cssgr.getSkuId());
                    jdGoods.setIdDel(true);
                    jdGoodsDao.update(jdGoods);
                }
            }
        }
        
        
    }
    
    public static <T> List<List<T>> splitList(List<T> originalList, int batchSize)
    {
        // 存储分割后的所有子 List
        List<List<T>> result = new ArrayList<>();
        
        // 空列表直接返回空结果
        if (originalList == null || originalList.isEmpty())
        {
            return result;
        }
        
        // 计算总共有多少组
        int totalSize = originalList.size();
        int totalBatch = (totalSize + batchSize - 1) / batchSize; // 向上取整
        
        // 循环截取每一组
        for (int i = 0; i < totalBatch; i++)
        {
            // 计算当前组的起始索引
            int start = i * batchSize;
            // 计算当前组的结束索引（取最小值，避免越界）
            int end = Math.min(start + batchSize, totalSize);
            // 截取子列表并转换为新的 ArrayList（避免视图关联）
            List<T> subList = new ArrayList<>(originalList.subList(start, end));
            result.add(subList);
        }
        
        return result;
    }
}
