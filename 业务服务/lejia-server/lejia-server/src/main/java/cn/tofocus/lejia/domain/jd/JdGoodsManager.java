package cn.tofocus.lejia.domain.jd;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Sets;
import com.jd.open.api.sdk.domain.vopsp.CategoryInfoGoodsProvider.response.getCategoryInfoList.GetCategoryInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSellPrice.GetSellPriceGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.GetSimilarSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.SaleLabelSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuDetailInfo.GetSkuPoolInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.GetSkuImageGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.SkuImageItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.getSkuPoolInfo.GetSkuPoolInfoItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.querySkuByPage.OpenPagingResult;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.PreClose;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.config.JdGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsDto;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsExcel;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsLowestBuy;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsLowestBuySyncTask;
import cn.tofocus.lejia.bean.dto.market.jd.*;
import cn.tofocus.lejia.bean.entity.jd.*;
import cn.tofocus.lejia.bean.entity.market.MktTagVisible;
import cn.tofocus.lejia.bean.entity.market.MktTagVisible.F;
import cn.tofocus.lejia.bean.enums.jd.JdGoodsUpdType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.cache.JdGoodsLowestBuySyncQueue;
import cn.tofocus.lejia.config.LejiaConfig;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.jd.*;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPGoodsManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdGoodsManager
{
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;
    
    @Autowired
    private JdCategoryDao jdCategoryDao;
    
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private JdGoodsServiceDao jdGoodsServiceDao;
    
    @Autowired
    private JdGoodsUpdNoticeDao jdGoodsUpdNoticeDao;
    
    @Autowired
    private JdGoodsLowestBuySyncQueue jdGoodsLowestBuySyncQueue;
    
    @Autowired
    private LejiaConfig lejiaConfig;
    
    private static final String JD_GOODS_LOWEST_BUY_SYNC_TASK_KEY = "jd:goods:lowestBy:sync:task";

    /** 京东商品导入模板的期望表头（按列顺序取自 {@link JdGoodsExcel} 的 @ExcelProperty，导入时据此校验） */
    private static final List<String> JD_GOODS_IMPORT_HEAD;
    static
    {
        List<String> head = new ArrayList<>();
        for (Field field : JdGoodsExcel.class.getDeclaredFields())
        {
            ExcelProperty ep = field.getAnnotation(ExcelProperty.class);
            if (ep != null && ep.value().length > 0)
            {
                head.add(ep.value()[0]);
            }
        }
        JD_GOODS_IMPORT_HEAD = Collections.unmodifiableList(head);
    }

    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    public PageResult<JdGoodsSpuOnPage> queryGoods(int page, int pagesize, String title, Long category, Long spuId,
        Long skuId, Boolean enabled)
    {
        PageResult<JdGoodsSpuOnPage> res = jdGoodsDao.aggregation()
            .page(page)
            .pagesize(pagesize)
            .like("title", title)
            .eq("idDel", false)
            .eq("enabled", enabled)
            .eq("spuId", spuId)
            .eq("pkey", skuId)
            .or()
            .eq("category", category)
            .eq("twoCategory", category)
            .eq("threeCategory", category)
            .close()
            .done()
            .groupby("categoryName", "categoryName")
            .groupby("spuId", "spuId")
            .count("pkey", "skuNum")
            .execDto(JdGoodsSpuOnPage.class);
        for (JdGoodsSpuOnPage gs : res.getContent())
        {
            JdGoods defaultSku = null;
            boolean spuEnabled = false;
            if (skuId != null)
            {
                defaultSku = jdGoodsDao.get(skuId);
            }
            else
            {
                
                List<JdGoods> skus = jdGoodsDao.listBySpuId(gs.getSpuId());
                for (JdGoods jg : skus)
                {
                    if (Boolean.TRUE.equals(jg.getEnabled()))
                    {
                        defaultSku = jg;
                        break;
                    }
                }
                if (defaultSku == null)
                    defaultSku = skus.get(0);
            }
            spuEnabled = defaultSku.getEnabled();
            
            gs.setPkey(defaultSku.getPkey());
            gs.setTitle(defaultSku.getTitle());
            gs.setPhoto1(defaultSku.getPhoto1());
            gs.setSalePrice(defaultSku.getSalePrice());
            gs.setPrice(defaultSku.getPrice());
            gs.setVisibleRange(defaultSku.getVisibleRange());
            gs.setEnabled(spuEnabled);
        }
        return res;
    }
    
    public List<JdCategoryDrop> listCategory()
    {
        return jdCategoryDao.listDrop();
    }
    
    public List<JdCategoryThreeDrop> listThreeCategory()
    {
        List<JdCategoryThreeDrop> res = jdCategoryDao.listThreeDrop(null, 0);
        for (JdCategoryThreeDrop jc : res)
        {
            List<JdCategoryThreeDrop> listTwoDrop = jdCategoryDao.listThreeDrop(jc.getPkey(), 1);
            if (!listTwoDrop.isEmpty())
            {
                for (JdCategoryThreeDrop tjc : listTwoDrop)
                {
                    List<JdCategoryThreeDrop> listThreeDrop = jdCategoryDao.listThreeDrop(tjc.getPkey(), 2);
                    tjc.setList(listThreeDrop);
                }
            }
            jc.setList(listTwoDrop);
        }
        return res;
    }
    
    public JdGoodsSpuOnInfo listSku(Long spuId)
    {
        List<JdGoods> list = jdGoodsDao.bySpuId(Arrays.asList(spuId));
        JdGoodsSpuOnInfo res = new JdGoodsSpuOnInfo();
        JdGoods goods = list.get(0);
        res.setCategoryName(
            goods.getCategoryName() + "/" + goods.getTwoCategoryName() + "/" + goods.getThreeCategoryName());
        res.setSpuId(spuId);
        res.setSpuName(goods.getSpuName());
        res.setVisibleRange(goods.getVisibleRange());
        if (MemberVisibleRange.TAG.equals(goods.getVisibleRange()))
        {
            List<Integer> tagKeys = new ArrayList<>();
            tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.JD_GOODS, goods.getPkey());
            res.setTagKeys(tagKeys);
        }
        List<JdGoodsSkuOnInfo> skuList = new ArrayList<>();
        for (JdGoods g : list)
        {
            JdGoodsSkuOnInfo info = BeanUtil.beanFrom(JdGoodsSkuOnInfo.class, g);
            JdGoodsSpace jdGoodsSpace = jdGoodsSpaceDao.get(g.getPkey());
            if (jdGoodsSpace != null)
            {
                BeanUtils.copyProperties(jdGoodsSpace, info);
            }
            skuList.add(info);
        }
        res.setSkuList(skuList);
        return res;
    }
    
    public String getJdGoodsZoneConfig()
    {
        JdGoodsZoneConfig farmerConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        if (farmerConfig == null)
        {
            dynamicAttributeDao.setSysAttribute(new JdGoodsZoneConfig(), qfAscription);
            farmerConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        }
        return farmerConfig.getJdGoodsName();
    }
    
    public Boolean setJdGoodsZoneConfig(String jdGoodsName)
    {
        JdGoodsZoneConfig farmerConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        farmerConfig.setJdGoodsName(jdGoodsName);
        dynamicAttributeDao.setSysAttribute(farmerConfig, qfAscription);
        return true;
    }
    
    public List<String> getServiceContent()
    {
        List<JdGoodsService> list = jdGoodsServiceDao.select().sort("pkey").exec();
        List<String> res = new ArrayList<>();
        for (JdGoodsService g : list)
            res.add(g.getContent());
        return res;
    }
    
    public Boolean setServiceContent(List<String> info)
    {
        List<JdGoodsService> all = jdGoodsServiceDao.findAll();
        jdGoodsServiceDao.removeAll(all);
        List<JdGoodsService> list = new ArrayList<>();
        for (String s : info)
        {
            JdGoodsService jgs = new JdGoodsService();
            jgs.setPkey(UUID.randomUUID().toString());
            jgs.setContent(s);
            jgs.setAscription(CurrentSession.ascriptionPkey());
            list.add(jgs);
        }
        jdGoodsServiceDao.addAll(list);
        return true;
    }
    
    public Boolean updGoods(JdGoodsSpuOnInfo info)
    {
        List<JdGoods> list = jdGoodsDao.bySpuId(Arrays.asList(info.getSpuId()));
        Map<Long, JdGoodsSkuOnInfo> map = new HashMap<>();
        info.getSkuList().forEach(e -> map.put(e.getPkey(), e));
        for (JdGoods g : list)
        {
            if (map.containsKey(g.getPkey()))
            {
                JdGoodsSkuOnInfo jds = map.get(g.getPkey());
                g.setPrice(jds.getPrice());
                g.setEnabled(jds.getEnabled());
            }
            g.setVisibleRange(info.getVisibleRange());
            if (MemberVisibleRange.TAG.equals(g.getVisibleRange()) && info.getTagKeys() != null
                && !info.getTagKeys().isEmpty())
            {
                tagManager.putTagVisibles(TagVisibleTargetType.JD_GOODS,
                    g.getPkey(),
                    info.getTagKeys(),
                    CurrentSession.ascriptionPkey());
            }
        }
        jdGoodsDao.updateAll(list);
        return true;
    }
    
    public Boolean enableSpuIdGoods(List<Long> spuIds, boolean enabled)
    {
        List<JdGoods> bySpuId = jdGoodsDao.bySpuId(spuIds);
        for (JdGoods goods : bySpuId)
        {
            goods.setEnabled(enabled);
        }
        jdGoodsDao.updateAll(bySpuId);
        return true;
    }
    
    public Boolean enableGoods(List<Long> pkeys, boolean enabled)
    {
        List<JdGoods> bySpuId = jdGoodsDao.byPkey(pkeys);
        for (JdGoods goods : bySpuId)
        {
            goods.setEnabled(enabled);
        }
        jdGoodsDao.updateAll(bySpuId);
        return true;
    }
    
    // 手动同步单个商品
    public Boolean manualSyncGoods(long pkey)
    {
        JdGoods g = jdGoodsDao.get(pkey);
        if (g == null)
        {
            g = new JdGoods();
            g.setPkey(pkey);
        }
        GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(g.getPkey(), Sets.newHashSet(3));
        log.info("s: {}", JsonUtil.toString(s, true));
        Map<Long, String> cateMap = jdCategoryDao.allMap();
        setCate(s.getCategory(), g, cateMap);
        String title = specialTitle(s.getSkuName());
        g.setTitle(title);
        g.setWeight(s.getWeight());
        g.setSaleUnit(s.getSaleUnit());
        g.setSeoModel(s.getSeoModel());
        g.setIntroduce(s.getIntroduce());
        g.setIntroducePc(s.getIntroducePc());
        g.setIntroduceApp(s.getIntroduceApp());
        g.setLowestBuy(s.getLowestBuy());
        g.setIntroduceWechat(s.getIntroduceWechat());
        g.setSkuState(s.getSkuState());
        g.setSpuId(s.getSpuId());
        g.setSpuName(s.getSpuName());
        g.setVisibleRange(MemberVisibleRange.ALL);
        g.setSort(0);
        g.setFarmer(Constant.Operation + qfAscription);
        g.setEnabled(false);
        g.setIdDel(false);
        g.setAscription(qfAscription);
        // 处理图片
        setPhoto(Arrays.asList(g));
        // 处理价格
        setPrice(Arrays.asList(g));
        // 处理规格
        setGoodsSpace(g);
        jdGoodsDao.put(g);
        return true;
    }
    
    // 手动同步未查询详情的数据
    public Boolean manualSyncGoodsNoTitle()
    {
        List<JdGoods> list = jdGoodsDao.select().isNull("title").exec();
        for (JdGoods g : list)
        {
            try
            {
                GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(g.getPkey(), Sets.newHashSet(3));
                log.info("s: {}", JsonUtil.toString(s, true));
                Map<Long, String> cateMap = jdCategoryDao.allMap();
                setCate(s.getCategory(), g, cateMap);
                String title = specialTitle(s.getSkuName());
                g.setTitle(title);
                g.setWeight(s.getWeight());
                g.setSaleUnit(s.getSaleUnit());
                g.setSeoModel(s.getSeoModel());
                g.setIntroduce(s.getIntroduce());
                g.setIntroducePc(s.getIntroducePc());
                g.setIntroduceApp(s.getIntroduceApp());
                g.setIntroduceWechat(s.getIntroduceWechat());
                g.setLowestBuy(s.getLowestBuy());
                g.setSkuState(s.getSkuState());
                g.setSpuId(s.getSpuId());
                g.setSpuName(s.getSpuName());
                g.setVisibleRange(MemberVisibleRange.ALL);
                g.setSort(0);
                g.setFarmer(Constant.Operation + qfAscription);
                g.setEnabled(false);
                g.setIdDel(false);
                g.setAscription(qfAscription);
                // 处理图片
                setPhoto(Arrays.asList(g));
                // 处理价格
                setPrice(Arrays.asList(g));
                // 处理规格
                setGoodsSpace(g);
                jdGoodsDao.put(g);
            }
            catch (Exception e)
            {
                log.error("京东获取详情的接口报错,报错商品主键: {}", g.getPkey());
                throw e;
            }
        }
        return true;
    }
    
    // 手动同步未查询详情的数据
    public Boolean manualSyncGoodsNoSpace()
    {
        List<JdGoods> list = jdGoodsDao.select().isNull("space1").exec();
        int i = 0;
        for (JdGoods g : list)
        {
            log.info("规格数量: {}", i++);
            try
            {
                // 处理规格
                setGoodsSpace(g);
            }
            catch (Exception e)
            {
                log.info("处理规格报错 {}, 商品主键", e.getMessage(), g.getPkey());
            }
        }
        
        return true;
    }
    
    // 手动同步最低起购量字段
    public Boolean manualSyncGoodsLowestBuy()
    {
        List<JdGoods> list = jdGoodsDao.findAll();
        int i = 1;
        log.info("商品总数: " + list.size());
        List<JdGoods> updateList = new ArrayList<>();
        for (JdGoods g : list)
        {
            log.info("商品数量: {}", i++);
            try
            {
                GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(g.getPkey(), null);
                if (s.getLowestBuy() != null)
                {
                    g.setLowestBuy(s.getLowestBuy());
                    updateList.add(g);
                }
            }
            catch (Exception e)
            {
                log.info("处理最低起购量字段报错 {}, 商品主键", e.getMessage(), g.getPkey());
            }
        }
        log.info("需要更新的商品明细: {}", JsonUtil.toString(updateList, true));
        log.info("需要更新的商品数量: {}", updateList.size());
        jdGoodsDao.updateAll(updateList);
        return true;
    }
    
    public Boolean specialTitle()
    {
        List<JdGoods> list = jdGoodsDao.findAll();
        int i = 1;
        log.info("商品总数: " + list.size());
        List<JdGoods> updateList = new ArrayList<>();
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
        for (JdGoods g : list)
        {
            log.info("商品数量: {}", i++);
            String title = g.getTitle();
            for (String k : t)
            {
                boolean contains = title.contains(k);
                if (contains)
                {
                    g.setTitle(title.replace(k, ""));
                    updateList.add(g);
                }
            }
        }
        //        log.info("需要更新的商品明细: {}", JsonUtil.toString(updateList, true));
        log.info("需要更新的商品数量: {}", updateList.size());
        jdGoodsDao.updateAll(updateList);
        return true;
    }
    
    // 设置商品规格
    private void setGoodsSpace(JdGoods jg)
    {
        Map<Long, JdGoodsSpace> gsMap = new HashMap<>();
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
        List<JdGoodsSpace> gsList = gsMap.values().stream().collect(Collectors.toList());
        if (gsList != null && !gsList.isEmpty())
            jdGoodsSpaceDao.putAll(gsList);
    }
    
    public void setSimilarSkuList(String bpi)
    {
        List<JdGoods> all = jdGoodsDao.findAll();
        List<Long> skuIds = new ArrayList<>();
        Map<Long, JdGoodsSpace> gsMap = new HashMap<>();
        
        int f = 0;
        List<List<JdGoods>> splitList = splitList(all, 100);
        for (List<JdGoods> gl : splitList)
        {
            for (JdGoods jg : gl)
            {
                System.out.println("商品数据: " + f++);
                try
                {
                    List<GetSimilarSkuGoodsResp> list = jdVOPGoodsManager.getSimilarSkuList(jg.getPkey());
                    if (list != null && !list.isEmpty())
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            GetSimilarSkuGoodsResp gssgr = list.get(i);
                            setSpace(i, jg, gsMap, gssgr);
                            Map<String, SaleLabelSkuGoodsResp> saleLabelMap = gssgr.getSaleLabelMap();
                            for (String key : saleLabelMap.keySet())
                            {
                                SaleLabelSkuGoodsResp resp = saleLabelMap.get(key);
                                skuIds.addAll(resp.getSkuIdSet());
                            }
                        }
                    }
                }
                catch (Exception e2)
                {
                }
            }
            jdGoodsDao.updateAll(gl);
        }
        
        Map<Long, JdGoods> map = new HashMap<>();
        all.forEach(e -> map.put(e.getPkey(), e));
        List<JdGoodsSpace> gsList = gsMap.values().stream().collect(Collectors.toList());
        jdGoodsSpaceDao.putAll(gsList);
        System.out.println("skuIds: " + skuIds.size());
        skuIds = skuIds.stream().distinct().collect(Collectors.toList());
        System.out.println("去重之后skuIds: " + skuIds.size());
        if (!skuIds.isEmpty())
        {
            List<JdGoods> addList = new ArrayList<>();
            for (Long k : skuIds)
            {
                if (!map.containsKey(k))
                {
                    JdGoods g = new JdGoods();
                    g.setAscription(qfAscription);
                    g.setPkey(k);
                    g.setBizPoolId(bpi);
                    addList.add(g);
                }
            }
            jdGoodsDao.addAll(addList);
        }
        setGoodsDetailInfo();
    }
    
    public void setSpace(int i, JdGoods jg, Map<Long, JdGoodsSpace> gsMap, GetSimilarSkuGoodsResp gssgr)
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
    
    // 同步商品数据
    public void runJdGoodsInfo(String bizPoolId)
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
        System.out.println("bizPoolIdList: " + JsonUtil.toString(bizPoolIdList, true));
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
                jdGoodsDao.putAll(addList);
            }
            long k2 = System.currentTimeMillis();
            log.info("耗时: {}", (k2 - k1));
            setSimilarSkuList(bpi);
        }
    }
    
    private void setGoodsDetailInfo()
    {
        List<JdGoods> all = jdGoodsDao.findAll();
        Map<Long, String> map = jdCategoryDao.allMap();
        
        // 批量处理图片和价格
        List<List<JdGoods>> splitList = splitList(all, 100);
        for (List<JdGoods> gl : splitList)
        {
            try
            {
                for (JdGoods g : gl)
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
                    //                    if(StringUtils.isNotBlank(s.getIntroduceWechat()))
                    //                    {
                    //                        String introduceWechat = s.getIntroduceWechat();
                    //                        if(introduceWechat.contains("[") && introduceWechat.contains("]")
                    //                            && introduceWechat.contains("\""))
                    //                        {
                    //                            List<String> list = JSON.parseArray(introduceWechat, String.class);
                    //                            StringBuilder sb = new StringBuilder();
                    //                            sb.append("[");
                    //                            for(String k : list)
                    //                            {
                    //                                sb.append("\"");
                    //                                sb.append(completePhoto(k));
                    //                                sb.append("\"");
                    //                                sb.append(",");
                    //                            }
                    //                            if(sb.length() > 1)
                    //                                sb.deleteCharAt(sb.length() - 1);
                    //                            sb.append("]");
                    //                            if(sb.length() == 2)
                    //                                g.setIntroduceWechat(null);
                    //                            else
                    //                                g.setIntroduceWechat(sb.toString());
                    //                        }
                    //                    }
                    g.setSkuState(s.getSkuState());
                    g.setSpuId(s.getSpuId());
                    g.setSpuName(s.getSpuName());
                    g.setVisibleRange(MemberVisibleRange.ALL);
                    g.setLowestBuy(s.getLowestBuy());
                    if (g.getSort() == null)
                        g.setSort(0);
                    if (StringUtils.isBlank(g.getFarmer()))
                        g.setFarmer(Constant.Operation + qfAscription);
                    if (g.getEnabled() == null)
                        g.setEnabled(false);
                    if (g.getIdDel() == null)
                        g.setIdDel(false);
                    if (g.getAscription() == null)
                        g.setAscription(qfAscription);
                }
                
                // 处理图片
                setPhoto(gl);
                // 处理价格
                setPrice(gl);
                jdGoodsDao.updateAll(gl);
            }
            catch (Exception e)
            {
            }
            
        }
        //        jdGoodsDao.updateAll(all);
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
        for (String k : t)
        {
            boolean contains = title.contains(k);
            if (contains)
            {
                title = title.replace(k, "");
            }
        }
        return title;
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
    
    public void setPrice(List<JdGoods> gl)
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
                    if (g.getPrice() == null)
                        g.setPrice(g.getSalePrice());
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
            jdGoodsUpdNoticeDao.putAll(noticeList);
        }
    }
    
    public void setPhoto(List<JdGoods> gl)
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
                if (photo1.size() > 30)
                    photo1 = photo1.subList(0, 30);
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
    
    public PageResult<JdGoodsUpdNoticeOnPage> queryJdGoodsUpdNotice(int page, int pagesize, String title, Long skuId,
        JdGoodsUpdType type, String startDate, String endDate)
    {
        return jdGoodsUpdNoticeDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(JdGoodsUpdNotice.F.jdGoods, skuId)
            .eq(JdGoodsUpdNotice.F.type, type)
            .ge(JdGoodsUpdNotice.F.createdTime, DateUtil.atStartOfDay(startDate))
            .lt(JdGoodsUpdNotice.F.createdTime, DateUtil.atStartOfNextDay(endDate))
            .as(JdGoodsUpdNotice.F.pkey)
            .as(JdGoodsUpdNotice.F.type)
            .as(JdGoodsUpdNotice.F.jdGoods)
            .as(JdGoodsUpdNotice.F.description)
            .as(JdGoodsUpdNotice.F.createdTime)
            .join(JdGoods.class, JdGoodsUpdNotice.F.jdGoods, JdGoods.F.pkey)
            .like(JdGoods.F.title, title)
            .as(JdGoods.F.title)
            .as(JdGoods.F.spuId)
            .endJoin()
            .sort(JdGoodsUpdNotice.F.createdTime)
            .sort(JdGoodsUpdNotice.F.pkey)
            .exec(JdGoodsUpdNoticeOnPage.class);
    }
    
    public JdPostageConfigDTO getJdPostageConfig()
    {
        JdGoodsZoneConfig config = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        if (config == null)
        {
            dynamicAttributeDao.setSysAttribute(new JdGoodsZoneConfig(), qfAscription);
            config = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        }
        JdPostageConfigDTO dto = new JdPostageConfigDTO();
        dto.setIsConsumerPostage(config.getIsConsumerPostage());
        return dto;
    }
    
    public boolean setJdPostageConfig(JdPostageConfigDTO dto)
    {
        JdGoodsZoneConfig farmerConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, qfAscription);
        farmerConfig.setIsConsumerPostage(dto.getIsConsumerPostage());
        dynamicAttributeDao.setSysAttribute(farmerConfig, qfAscription);
        return true;
    }
    
    public List<JdGoodsExcel> exportGoods(String title, Long category, Long spuId, Long skuId, Boolean enabled)
    {
        long k1 = System.currentTimeMillis();
        //        Scroller<JdGoods> scroller = jdGoodsDao.scroll()
        //        .like("title", title)
        //        .eq("idDel", false)
        //        .eq("enabled", enabled)
        //        .eq("spuId", spuId)
        //        .eq("pkey", skuId)
        //        .or()
        //        .eq("category", category)
        //        .eq("twoCategory", category)
        //        .eq("threeCategory", category)
        //        .close()
        //        .done()
        //        .exec();
        List<JdGoodsDto> list = jdGoodsDao.select()
            .like("title", title)
            .eq("idDel", false)
            .eq("enabled", enabled)
            .eq("spuId", spuId)
            .eq("pkey", skuId)
            .or()
            .eq("category", category)
            .eq("twoCategory", category)
            .eq("threeCategory", category)
            .close()
            .done()
            .execDto(JdGoodsDto.class);
        long k2 = System.currentTimeMillis();
        log.info("查询耗时: {}", (k2 - k1));
        Map<Integer, String> map = tagDao.mapName(new ArrayList<>(), CurrentSession.ascriptionPkey());
        long k3 = System.currentTimeMillis();
        log.info("tagDao查询耗时: {}", (k3 - k2));
        List<MktTagVisible> tvList = tagVisibleDao.select()
            .eq(F.type, TagVisibleTargetType.JD_GOODS)
            .eq(F.ascription, CurrentSession.ascriptionPkey())
            .exec();
        Map<Long, List<Integer>> tvMap = new HashMap<>();
        tvList.forEach(e -> {
            if (!tvMap.containsKey(e.getTarget()))
            {
                List<Integer> value = new ArrayList<>();
                tvMap.put(e.getTarget(), value);
            }
            tvMap.get(e.getTarget()).add(e.getTag());
        });
        List<JdGoodsExcel> res = new ArrayList<>();
        for (JdGoodsDto jg : list)
        {
            JdGoodsExcel jge = new JdGoodsExcel();
            jge.setPkey(jg.getPkey());
            jge.setCategoryName(jg.getCategoryName() + "/" + jg.getTwoCategoryName() + "/" + jg.getThreeCategoryName());
            jge.setTitle(jg.getTitle());
            if (jg.getVisibleRange() != null)
                jge.setVisibleRangeName(jg.getVisibleRange().getName());
            // 标签
            if (MemberVisibleRange.TAG.equals(jg.getVisibleRange()))
            {
                StringBuffer sb = new StringBuffer();
                if (tvMap.containsKey(jg.getPkey()))
                {
                    List<Integer> tagKeys = tvMap.get(jg.getPkey());
                    for (Integer k : tagKeys)
                    {
                        if (map.containsKey(k))
                        {
                            sb.append(map.get(k)).append(",");
                        }
                    }
                    if (sb.length() > 0)
                        sb.deleteCharAt(sb.length() - 1);
                    jge.setTag(sb.toString());
                }
                //                List<Integer> tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.JD_GOODS, jg.getPkey());
            }
            
            jge.setSalePrice(jg.getSalePrice());
            jge.setPrice(jg.getPrice());
            jge.setEnabled(jg.getEnabled());
            JdGoodsSpace jgs = jdGoodsSpaceDao.get(jg.getPkey());
            if (jgs != null)
            {
                jge.setSpaceName(jgs.getSpaceName());
            }
            res.add(jge);
        }
        long k4 = System.currentTimeMillis();
        log.info("组合耗时查询耗时: {}", (k4 - k3));
        return res;
    }
    
    public void importGoods(MultipartFile myfile, OutputStream out)
        throws Exception
    {
        log.info("[京东商品导入]开始导入处理");
        long start = System.currentTimeMillis();
        List<JdGoodsExcel> list = new ArrayList<>();
        ExcelReaderBuilder read = EasyExcel.read(myfile.getInputStream());
        read.head(JdGoodsExcel.class);
        read.registerReadListener(new JdGoodsListener(list, out));
        read.headRowNumber(1);
        read.doReadAll();
        //        long readFinish = System.currentTimeMillis();
        if (CollectionUtil.isNotEmpty(list))
            goodsExcelDataProcessV2(list);
        long finish = System.currentTimeMillis();
        log.info("[京东商品导入]导入总耗时：{}", finish - start);
    }
    
    private void goodsExcelDataProcessV2(List<JdGoodsExcel> list)
    {
        int split = 1000;
        int end = 0;
        for (int start = 0, size = list.size(); start < size; start += split)
        {
            end = start + split > size ? size : start + split;
            List<JdGoodsExcel> subList = list.subList(start, end);
            goodsExcelDataProcess(subList);
        }
    }
    
    private void goodsExcelDataProcess(List<JdGoodsExcel> list)
    {
        List<Long> keys =
            list.stream().map(JdGoodsExcel::getPkey).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, JdGoods> map = jdGoodsDao.mapJdGoods(keys);
        Integer ascription = CurrentSession.ascriptionPkey();
        Map<String, Integer> tagMap = tagDao.map(ascription);
        // 指定标签的京东商品id
        List<Long> tagJdGoodsKeys = new ArrayList<>();
        // 指定标签的京东商品、标签关联
        List<MktTagVisible> toAddTagVisibleList = new ArrayList<>();
        for (JdGoodsExcel jge : list)
        {
            if (map.containsKey(jge.getPkey()))
            {
                JdGoods jg = map.get(jge.getPkey());
                jg.setPrice(jge.getPrice());
                jg.setEnabled(jge.getEnabled());
                if ("全部用户".equals(jge.getVisibleRangeName()))
                {
                    jg.setVisibleRange(MemberVisibleRange.ALL);
                }
                if ("指定标签".equals(jge.getVisibleRangeName()))
                {
                    jg.setVisibleRange(MemberVisibleRange.TAG);
                    List<Integer> tagKeys = new ArrayList<>();
                    if (jge.getTag().contains(","))
                    {
                        String[] split = jge.getTag().split(",");
                        for (String s : split)
                        {
                            if (tagMap.containsKey(s))
                            {
                                tagKeys.add(tagMap.get(s));
                            }
                        }
                    }
                    else if (jge.getTag().contains("‚"))
                    {
                        String[] split = jge.getTag().split("‚");
                        for (String s : split)
                        {
                            if (tagMap.containsKey(s))
                            {
                                tagKeys.add(tagMap.get(s));
                            }
                        }
                    }
                    else
                    {
                        if (tagMap.containsKey(jge.getTag()))
                        {
                            tagKeys.add(tagMap.get(jge.getTag()));
                        }
                    }
                    tagJdGoodsKeys.add(jg.getPkey());
                    for (Integer tag : new HashSet<>(tagKeys))
                    {
                        MktTagVisible tv = new MktTagVisible();
                        tv.setPkey(TagVisibleTargetType.JD_GOODS, jg.getPkey(), tag);
                        tv.setAscription(ascription);
                        toAddTagVisibleList.add(tv);
                    }
                }
            }
        }
        List<MktTagVisible> dels = tagVisibleDao.listByTargets(TagVisibleTargetType.JD_GOODS, tagJdGoodsKeys);
        tagVisibleDao.addAll(toAddTagVisibleList);
        tagVisibleDao.removeAndPutAll(dels, toAddTagVisibleList);
        jdGoodsDao.updateAll(new ArrayList<>(map.values()));
    }
    
    // 新写监听器  监听传进来的参数
    class JdGoodsListener extends AnalysisEventListener<JdGoodsExcel>
    {
        OutputStream out;
        
        ExcelWriterBuilder errBuilder;
        
        List<JdGoodsExcel> list = new ArrayList<>();
        
        List<JdGoodsExcel> errList = new ArrayList<>();
        
        JdGoodsListener(List<JdGoodsExcel> list, OutputStream out)
        {
            this.list = list;
            this.out = out;
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context)
        {
            // 导入开始即校验表头：列顺序与名称必须与模板一致，不匹配直接失败，避免数据按错列写入
            for (int i = 0; i < JD_GOODS_IMPORT_HEAD.size(); i++)
            {
                String expected = JD_GOODS_IMPORT_HEAD.get(i);
                String actual = headMap.get(i);
                if (actual == null || !expected.equals(actual.trim()))
                {
                    throw TofocusException.of(LejiaErrCode.IMPORT_ERROR,
                        "表头不匹配，第" + (i + 1) + "列应为「" + expected + "」，实际为「" + actual + "」");
                }
            }
        }

        @Override
        public void invoke(JdGoodsExcel data, AnalysisContext context)
        {
            try
            {
                if (data.getEnabled() == null)
                    data.setEnabled(false);
                if (data.getPrice() == null)
                    throw TofocusException.of(LejiaErrCode.JD_GOODS_EXCEL_ERROE, "商品价格不能为空");
                if (data.getPkey() == null)
                    throw TofocusException.of(LejiaErrCode.JD_GOODS_EXCEL_ERROE, "商品主键不能为空");
                if (StringUtils.isNotBlank(data.getTag()))
                {
                    if ("全部用户".equals(data.getVisibleRangeName()))
                        data.setVisibleRangeName("指定标签");
                }
                else
                {
                    if ("指定标签".equals(data.getVisibleRangeName()))
                        data.setVisibleRangeName("全部用户");
                }
                list.add(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String errmsg;
                if (e instanceof TofocusException)
                {
                    errmsg = e.getMessage();
                }
                else if (e instanceof ExcelDataConvertException)
                {
                    errmsg = "数据格式异常!";
                }
                else
                {
                    errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
                }
                data.setErrMsg(errmsg);
                errList.add(data);
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, JdGoodsExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
        
    }
    
    private boolean taskProcessing;
    
    private boolean stopTask;
    
    public void syncLowestBuy4AllSku()
    {
        taskProcessing = true;
        long time0 = System.currentTimeMillis();
        JdGoodsLowestBuySyncTask task =
            JsonUtil.getBean(lejiaConfig.get(JD_GOODS_LOWEST_BUY_SYNC_TASK_KEY), JdGoodsLowestBuySyncTask.class);
        // 任务未结束
        if (jdGoodsLowestBuySyncQueue.size() == 0)
        {
            List<JdGoodsLowestBuy> jdGoodsList =
                jdGoodsDao.select().eq(JdGoods.F.idDel, false).execDto(JdGoodsLowestBuy.class);
            jdGoodsLowestBuySyncQueue.add(jdGoodsList);
            task = JdGoodsLowestBuySyncTask.init(new Date(), jdGoodsList.size());
            lejiaConfig.put(JD_GOODS_LOWEST_BUY_SYNC_TASK_KEY, JsonUtil.toString(task));
            log.info("[京东VOP][同步最低起购量]开始新的同步任务：{}", JsonUtil.toString(task));
        }
        else
        {
            log.info("[京东VOP][同步最低起购量]继续处理未完成的同步任务：{}", JsonUtil.toString(task));
        }
        AtomicInteger updNum = new AtomicInteger(task.getUpdNum());
        long timeS = System.currentTimeMillis();
        while (jdGoodsLowestBuySyncQueue.size() > 0)
        {
            if (stopTask)
            {
                task.setUpdNum(updNum.get());
                lejiaConfig.put(JD_GOODS_LOWEST_BUY_SYNC_TASK_KEY, JsonUtil.toString(task));
                break;
            }
            JdGoodsLowestBuy jdGoods = jdGoodsLowestBuySyncQueue.poll();
            try
            {
                GetSkuPoolInfoGoodsResp resp = jdVOPGoodsManager.getSkuDetailInfo(jdGoods.getPkey(), null);
                if (!Objects.equals(jdGoods.getLowestBuy(), resp.getLowestBuy()))
                {
                    jdGoodsDao.select()
                        .strict(true)
                        .eq(JdGoods.F.pkey, jdGoods.getPkey())
                        .update(JdGoods.F.lowestBuy, resp.getLowestBuy());
                    log.info("[京东VOP][同步最低起购量]sku（{}）最低起购量更新为：{}", jdGoods.getPkey(), resp.getLowestBuy());
                    updNum.incrementAndGet();
                }
            }
            catch (Exception e)
            {
                log.error("[京东VOP][同步最低起购量]sku（" + jdGoods.getPkey() + "）处理出错，已跳过", e);
            }
            long timeE = System.currentTimeMillis();
            if (timeE - timeS > 5000) {
                long handledNum = task.getTotalNum() - jdGoodsLowestBuySyncQueue.size();
                log.info("[京东VOP][同步最低起购量]处理进度：{}%（{}/{}）",
                        handledNum * 100 / task.getTotalNum(),
                        handledNum,
                        task.getTotalNum());
                timeS = timeE;
            }
        }
        if (jdGoodsLowestBuySyncQueue.size() == 0)
        {
            task.setUpdNum(updNum.get());
            task.setFinished(true);
            task.setEndTime(new Date());
            lejiaConfig.put(JD_GOODS_LOWEST_BUY_SYNC_TASK_KEY, JsonUtil.toString(task));
            JdGoodsUpdNotice notice = JdGoodsUpdNotice.lowestBuyOf(Constant.Operation + qfAscription, qfAscription);
            notice.setDescription("更新最低起购量共" + updNum.get() + "个");
            jdGoodsUpdNoticeDao.add(notice);
        }
        long time1 = System.currentTimeMillis();
        if (task.isFinished())
            log.info("[京东VOP][同步最低起购量]任务已完成，共查询{}个sku，更新了{}个最低起购量，本次耗时：{}", task.getTotalNum(), updNum, time1 - time0);
        else
            log.info("[京东VOP][同步最低起购量]任务已中止，共{}个sku，已查询{}个，已更新了{}个最低起购量，本次耗时：{}",
                task.getTotalNum(),
                task.getTotalNum() - jdGoodsLowestBuySyncQueue.size(),
                updNum,
                time1 - time0);
        taskProcessing = false;
    }
    
    @PreClose(concurrency = true)
    public void close()
    {
        int checkInterval = 1000;
        stopTask = true;
        while (taskProcessing)
        {
            try
            {
                log.warn("京东VOP同步最低起购量任务停止中...");
                Thread.sleep(checkInterval);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
