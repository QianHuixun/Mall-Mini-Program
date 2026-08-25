package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.join.db.SelectPageOps;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsOnPage;
import cn.tofocus.lejia.bean.dto.app.market.*;
import cn.tofocus.lejia.bean.dto.jd.AppJdGoodsBackfill;
import cn.tofocus.lejia.bean.dto.jd.AppJdGoodsSortKey;
import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.*;
import cn.tofocus.lejia.bean.entity.jd.JdCategory;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.*;
import cn.tofocus.lejia.dao.jd.JdCategoryDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.domain.GoodsManager;
import cn.tofocus.lejia.domain.market.SearchManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.repository.market.MktCookfdRepository;
import cn.tofocus.lejia.repository.market.MktGoodsRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
public class AppGoodsManager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private SearchManager searchManager;
    
    @Autowired
    private MktGoodsRepository goodsRepository;
    
    @Autowired
    private GoodsManager goodsManager;
    
    @Autowired
    private AppCollectionManager collectionManager;
    
    @Autowired
    private MktCookfdRepository cookfdRepository;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private MktGoodsProcessDao goodsProcessDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;

    @Autowired
    private MktSupplierDao supplierDao;

    @Autowired
    private MktVendorDao vendorDao;

    @Autowired
    private MktVendorFileDao vendorFileDao;

    @Autowired
    private MktGoodsSellingPointDao goodsSellingPointDao;

    @Autowired
    private MktOrderGoodsCommentDao orderGoodsCommentDao;
    
    @Autowired
    private MktGoodsRecommendDao goodsRecommendDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;

    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private JdCategoryDao jdCategoryDao;

    @Value("${tofocus.file.baseUrl}")
    private String fileStart;
    
    public PageResult<AppGoodsAppOnList> queryAppGoods(Integer page, Integer pagesize, MType mType, Boolean isOnPresale,
        Boolean zoneRecommend)
    {
        return queryAppGoods(page,
            pagesize,
            null,
            null,
            mType,
            null,
            0,
            0,
            null,
            isOnPresale,
            null,
            null,
            null,
            zoneRecommend);
    }
    
    public PageResult<AppGoodsAppOnList> queryAppGoods(Integer page, Integer pagesize, Integer gtype, Integer goodsMain,
        MType mType, String title, Integer hotSort, Integer priceSort, String date, Boolean isOnPresale,
        Boolean guessLike, Integer vendor, Integer topGoods, Boolean zoneRecommend)
    {
        if (StringUtils.isNotBlank(title))
        {
            searchManager.insSearch(SearchType.GOODS, title);
        }
        PageResult<MktGoods> list;
        if (mType != null && MType.SPECIAL_GOODS.equals(mType))
        {
            List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), MobileSession.appid());
            List<Long> goodsPkeys = new ArrayList<>();
            if (!listTag.isEmpty())
            {
                goodsPkeys = tagVisibleDao.listTarget(TagVisibleTargetType.SPECIAL_GOODS, listTag);
            }
            list =
                goodsDao.queryAppSpecialGoods(page, pagesize, date, goodsPkeys, topGoods, zoneRecommend, MobileSession.appid());
        }
        else
        {
            List<Integer> supplierPkeys = null;
            List<Long> goodsMsdKeys = null;
            // 积分商品，过滤掉停用的供应商
            if (mType == MType.INTEGRAL_GOODS || mType == MType.INTEGRAL_PRESALE_GOODS
                || mType == MType.INTEGRAL_BNYP_GOODS || mType == MType.INTEGRAL_MSD_GOODS)
            {
                supplierPkeys = supplierDao.findPkeys(MobileSession.appid(), true);
                if(mType == MType.INTEGRAL_MSD_GOODS)
                {
                    if(MobileSession.memberPkey() == null)
                        return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
                    MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
                    if(memberMsd == null)
                        return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
                    goodsMsdKeys = tagVisibleDao.listTarget(TagVisibleTargetType.INTEGRAL_MSD_GOODS, Arrays.asList(memberMsd.getTag()));
                }
            }
            list = goodsDao.queryAppGoods(page,
                pagesize,
                gtype,
                goodsMain,
                mType,
                title,
                hotSort,
                priceSort,
                date,
                isOnPresale,
                null,
                supplierPkeys,
                goodsMsdKeys,
                guessLike,
                topGoods,
                zoneRecommend,
                MobileSession.appid(),
                vendor);
        }
        PageResult<AppGoodsAppOnList> result = assembleGoodsDTO(list, mType, isOnPresale, false, page, pagesize);
        assembleGwcNum(result);
        return result;
    }
    
    /**
     * 商城商品分页查询：按商城一级/二级分类 + MType + 销量/价格排序。
     * 非 MSD：单源（mkt_goods），DB 排序后取大批量（上限 10000）再 PageUtil 内存分页。
     * MSD：双源内存合并 —— mkt_goods(mType=INTEGRAL_MSD_GOODS) + JdGoods（经 goodsMain 反查 threeCategory，按 spuId 去重，
     * 会员标签可见性），合并后全局排序再 PageUtil 内存分页。
     * 分类参数 gtype/goodsMain 由接口层 @RequestParam(required=true) 保证非空。
     */
    public PageResult<AppMallGoodsOnPage> queryMallGoods(Integer page, Integer pagesize, MType mtype,
        Integer gtype, Integer goodsMain, Integer hotSort, Integer priceSort, String title)
    {
        if (!EnumSet.of(MType.INTEGRAL_GOODS, MType.INTEGRAL_PRESALE_GOODS, MType.INTEGRAL_BNYP_GOODS,
            MType.INTEGRAL_MSD_GOODS).contains(mtype))
        {
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "不支持的MType");
        }

        if (MType.INTEGRAL_MSD_GOODS.equals(mtype))
        {
            // MSD：双源合并，JD 两阶段装载（精简投影取全量排序键 → 内存排序分页 → 仅窗口批量回填）
            return queryMsdMallGoodsPage(page, pagesize, gtype, goodsMain, hotSort, priceSort, title);
        }
        // 非 MSD：单源，DB 排序后大批量取数
        PageResult<AppGoodsAppOnList> pr = queryAppGoods(0, 10000, gtype, goodsMain, mtype, title,
            hotSort, priceSort, null, false, null, null, null, null);
        List<AppMallGoodsOnPage> merged = new ArrayList<>();
        for (AppGoodsAppOnList app : pr.getContent())
        {
            merged.add(toMallGoodsOnPage(app));
        }
        return PageUtil.page(merged, PageParameter.of(page, pagesize));
    }

    /** AppGoodsAppOnList -> AppMallGoodsOnPage（来源 MALL，字段全量拷贝；pkey 转 Long）。 */
    private AppMallGoodsOnPage toMallGoodsOnPage(AppGoodsAppOnList app)
    {
        AppMallGoodsOnPage dto = new AppMallGoodsOnPage();
        dto.setPkey(app.getPkey() == null ? null : app.getPkey().longValue());
        dto.setSource("MALL");
        dto.setTitle(app.getTitle());
        dto.setTag(app.getTag());
        dto.setPrice(app.getPrice());
        dto.setPriceOld(app.getPriceOld());
        dto.setPoint(app.getPoint());
        dto.setPhoto1(app.getPhoto1());
        dto.setWrapperPhoto(app.getWrapperPhoto());
        dto.setSpaces(app.getSpaces());
        dto.setSellingPoints(app.getSellingPoints());
        dto.setXsNum(app.getXsNum());
        dto.setGwcNum(app.getGwcNum());
        dto.setGtype(app.getGtype());
        dto.setGoodsMain(app.getGoodsMain());
        return dto;
    }

    /**
     * MSD 商城分页（双源合并，JD 聚合去重 + 批量精简回填）：
     * MKT 全量装配（数据量小）；JD 阶段一聚合 groupby(spuId).min(pkey) 在 DB 端去重取代表 pkey（不加载全量 SKU、不碰 text 列），
     * 仅当需要排序时阶段二批量精简取代表行 price/xsNum；阶段三对窗口内 JD 批量回填完整字段（listBackfill + gwc，含 price/xsNum）。
     */
    private PageResult<AppMallGoodsOnPage> queryMsdMallGoodsPage(int page, int pagesize, Integer gtype,
        Integer goodsMain, Integer hotSort, Integer priceSort, String title)
    {
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));

        // Source A：MKT 全量装配（数据量小，保持现状）
        PageResult<AppGoodsAppOnList> mallPage = queryAppGoods(0, 10000, gtype, goodsMain, MType.INTEGRAL_MSD_GOODS,
            title, 0, 0, null, false, null, null, null, null);
        List<AppMallGoodsOnPage> merged = new ArrayList<>();
        for (AppGoodsAppOnList app : mallPage.getContent())
        {
            merged.add(toMallGoodsOnPage(app));
        }

        // Source B 阶段一：JD 聚合去重（DB 端）→ 代表 pkey；条目仅写 pkey/source，price/xsNum 仅排序时需要、否则留待窗口回填
        List<Long> jdRepPkeys = queryMsdJdRepPkeys(goodsMain, title);
        boolean needSort = (hotSort != null && hotSort != 0) || (priceSort != null && priceSort != 0);
        Map<Long, AppJdGoodsSortKey> jdSortKeys = needSort ? fetchJdSortKeys(jdRepPkeys) : Collections.emptyMap();
        for (Long pk : jdRepPkeys)
        {
            if (needSort && !jdSortKeys.containsKey(pk)) continue; // 排序键缺失则跳过（极少见）
            AppMallGoodsOnPage dto = new AppMallGoodsOnPage();
            dto.setPkey(pk);
            dto.setSource("JD");
            if (needSort)
            {
                AppJdGoodsSortKey k = jdSortKeys.get(pk);
                dto.setPrice(k.getPrice());
                dto.setXsNum(k.getXsNum() == null ? 0 : k.getXsNum());
            }
            merged.add(dto);
        }
        long total = merged.size();

        // 全局排序（hotSort/priceSort）后取窗口 [page*ps, (page+1)*ps)
        sortMallGoods(merged, hotSort, priceSort);
        int from = Math.min(page * pagesize, merged.size());
        int to = Math.min((page + 1) * pagesize, merged.size());
        List<AppMallGoodsOnPage> window = from >= to ? new ArrayList<>() : new ArrayList<>(merged.subList(from, to));

        // 阶段三：窗口内 JD 条目批量回填完整字段（含 price/xsNum）
        fillMallJdWindow(window, gtype, goodsMain);
        return new PageResult<>(window, PageParameter.of(page, pagesize), total);
    }

    /**
     * JD 阶段一（聚合去重，DB 端）：goodsMain 反查 twoCategory，aggregation groupby(spuId).min(pkey) + 会员标签可见性
     * （jdGoodsKeys 按 1w 分批），返回每个 spuId 的代表 pkey（去重后）。ALL 范围商品多批重复，靠 spuId 合并自动去重。
     */
    private List<Long> queryMsdJdRepPkeys(Integer goodsMain, String title)
    {
        List<Long> twoCategoryKeys = jdCategoryDao.select()
            .eq(JdCategory.F.mallCategory, goodsMain)
            .eq(JdCategory.F.categoryLevel, 1)
            .eq(JdCategory.F.needShow, 1)
            .execDto(JdCategory.F.pkey, Long.class);
        if (twoCategoryKeys.isEmpty())
            return new ArrayList<>();
        
        MktMemberMsd memberMsd = memberMsdDao.get(MobileSession.memberPkey());
        if (memberMsd == null)
            return new ArrayList<>();
        List<Long> jdGoodsKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, Lists.newArrayList(memberMsd.getTag()));
        
        Map<Long, Long> spuRep = new HashMap<>();
        if (jdGoodsKeys.isEmpty())
        {
            mergeRepPkeys(spuRep, queryMsdJdRepPkeysAgg(title, twoCategoryKeys, null));
        }
        else
        {
            for (int i = 0; i * 10000 < jdGoodsKeys.size(); i++)
            {
                int from = i * 10000;
                int to = Math.min(jdGoodsKeys.size(), (i + 1) * 10000);
                mergeRepPkeys(spuRep, queryMsdJdRepPkeysAgg(title, twoCategoryKeys, jdGoodsKeys.subList(from, to)));
            }
        }
        return new ArrayList<>(spuRep.values());
    }

    /** JD 聚合去重单批：twoCategory 过滤 + title 模糊 + 会员标签可见性，groupby(spuId).min(pkey)。jdGoodsKeys 为 null/空时仅 ALL 范围。 */
    private List<AppJdGoodsOnPage> queryMsdJdRepPkeysAgg(String title, List<Long> twoCategoryKeys, List<Long> jdGoodsKeys)
    {
        AggregationBuilder<Long, JdGoods> builder = jdGoodsDao.aggregation()
            .eq(JdGoods.F.idDel, false)
            .eq(JdGoods.F.enabled, true)
            .eq(JdGoods.F.skuState, 1)
            .in(JdGoods.F.twoCategory, twoCategoryKeys)
            .like(JdGoods.F.title, title);
        if (CollectionUtil.isNotEmpty(jdGoodsKeys))
        {
            //@formatter:off
            builder.or()
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
            builder.eq(JdGoods.F.visibleRange, MemberVisibleRange.ALL);
        }
        return builder.groupby(JdGoods.F.spuId, JdGoods.F.spuId)
            .min(JdGoods.F.pkey, JdGoods.F.pkey).execListDto(AppJdGoodsOnPage.class);
    }

    /** 将一批聚合结果(spuId,pkey) 合并入 dst：同 spuId 保留最小 pkey。 */
    private void mergeRepPkeys(Map<Long, Long> dst, List<AppJdGoodsOnPage> batch)
    {
        for (AppJdGoodsOnPage jg : batch)
        {
            dst.merge(jg.getSpuId(), jg.getPkey(), Long::min);
        }
    }

    /** 阶段二：为代表 pkey 批量精简取排序键(price/xsNum)，repPkeys >1w 时分批避开 in 超限。仅在需要排序时调用。 */
    private Map<Long, AppJdGoodsSortKey> fetchJdSortKeys(List<Long> repPkeys)
    {
        Map<Long, AppJdGoodsSortKey> map = new HashMap<>();
        for (int i = 0; i * 10000 < repPkeys.size(); i++)
        {
            int from = i * 10000;
            int to = Math.min(repPkeys.size(), (i + 1) * 10000);
            for (AppJdGoodsSortKey k : jdGoodsDao.listBackfill(repPkeys.subList(from, to), AppJdGoodsSortKey.class))
            {
                map.put(k.getPkey(), k);
            }
        }
        return map;
    }

    /** 阶段二：窗口内来源为 JD 的条目，用 byPkey 批量回填完整字段（窗口 ≤ pagesize，text 列影响可忽略）+ 购物车数量。 */
    private void fillMallJdWindow(List<AppMallGoodsOnPage> window, Integer gtype, Integer goodsMain)
    {
        List<Long> jdPkeys = new ArrayList<>();
        for (AppMallGoodsOnPage d : window)
        {
            if ("JD".equals(d.getSource())) jdPkeys.add(d.getPkey());
        }
        if (jdPkeys.isEmpty()) return;
        Map<Long, AppJdGoodsBackfill> map =
            jdGoodsDao.listBackfill(jdPkeys, AppJdGoodsBackfill.class).stream()
                .collect(Collectors.toMap(AppJdGoodsBackfill::getPkey, g -> g, (a, b) -> a));
        for (AppMallGoodsOnPage d : window)
        {
            if (!"JD".equals(d.getSource())) continue;
            AppJdGoodsBackfill g = map.get(d.getPkey());
            if (g == null) continue;
            d.setTitle(g.getTitle());
            d.setTag(g.getTag());
            d.setPrice(g.getPrice());
            d.setPriceOld(g.getPrice()); // 京东商品原价统一用 price
            d.setXsNum(g.getXsNum() == null ? 0 : g.getXsNum());
            d.setPhoto1(g.getPhoto1());
            if (g.getPhoto1() != null && !g.getPhoto1().isEmpty()) d.setWrapperPhoto(g.getPhoto1().get(0));
            d.setLowestBuy(g.getLowestBuy());
            d.setGwcNum(0);
            MktGwc bean = gwcDao.getJdGwcMember(g.getPkey(), MobileSession.memberPkey());
            if (bean != null) d.setGwcNum(bean.getNum());
            d.setGtype(gtype);
            d.setGoodsMain(goodsMain);
        }
    }

    /** 合并后的商城商品全局排序：hotSort(销量) 优先，否则 priceSort(价格)；null 值末尾。 */
    private void sortMallGoods(List<AppMallGoodsOnPage> list, Integer hotSort, Integer priceSort)
    {
        if (list.isEmpty()) return;
        Comparator<AppMallGoodsOnPage> cmp = null;
        if (hotSort != null && hotSort == 1)
            cmp = Comparator.comparing(AppMallGoodsOnPage::getXsNum, Comparator.nullsLast(Integer::compareTo)).reversed();
        else if (hotSort != null && hotSort == 2)
            cmp = Comparator.comparing(AppMallGoodsOnPage::getXsNum, Comparator.nullsLast(Integer::compareTo));
        else if (priceSort != null && priceSort == 1)
            cmp = Comparator.comparing(AppMallGoodsOnPage::getPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed();
        else if (priceSort != null && priceSort == 2)
            cmp = Comparator.comparing(AppMallGoodsOnPage::getPrice, Comparator.nullsLast(BigDecimal::compareTo));
        if (cmp != null) list.sort(cmp);
    }

    /**
     * 民生商品搜索（滚动查询）：MktGoods(INTEGRAL_MSD_GOODS) 优先，用尽后接 JdGoods(spuId 去重)。
     * 条件与会员可见性参照 {@link #queryMallGoods} 的 MSD 分支；不按销量/价格排序。
     * 入参 title 模糊搜索、offset 起始值、limit 条数；返回商品列表与下次滚动起始值（null=已无更多）。
     * offset 由客户端按 limit 递增（窗口对齐），故 page = offset / limit 精确。
     */
    public AppMsdGoodsOnScroll searchMsdGoods(String title, Integer offset, Integer limit)
    {
        if (offset == null || offset < 0)
        {
            offset = 0;
        }
        if (limit == null || limit < 1)
        {
            limit = 10;
        }
        if (limit > 100)
        {
            limit = 100;
        }

        AppMsdGoodsOnScroll result = new AppMsdGoodsOnScroll();
        List<AppMsdGoodsOnList> merged = new ArrayList<>();
        result.setList(merged);

        // 前置条件：MSD 会员身份有效（MKT 与 JD 两个数据源共同依赖）
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null)
        {
            result.setNextOffset(null);
            return result;
        }
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd == null)
        {
            result.setNextOffset(null);
            return result;
        }

        // MKT 源：execCount 取真实总数（不依赖分页 total）；offset 未越过总数才用 limit 取窗口
        MktScrollResult mkt = queryMsdGoodsScroll(offset, limit, title, memberMsd);
        long mktCount = mkt.total;
        merged.addAll(mkt.window);

        if (offset < mktCount && merged.size() < limit)
        {
            // 窗口内 MktGoods 见底，从 JdGoods 起始位置 0 补齐
            merged.addAll(queryMsdJdGoodsScroll(memberMsd, title, 0, limit - merged.size()));
        }
        else if (offset >= mktCount)
        {
            // 整段落在 JdGoods
            merged.addAll(queryMsdJdGoodsScroll(memberMsd, title, (int)(offset - mktCount), limit));
        }

        result.setNextOffset(merged.size() < limit ? null : offset + limit);
        return result;
    }

    /**
     * MSD 滚动查询的 MktGoods 源（精简、无搜索埋点）：基于传入的 memberMsd（已由 {@link #searchMsdGoods} 校验非空）
     * 解析启用供应商 + MSD 会员标签可见性（仅 MKT 源用），先 execCount 取 MKT 总数；
     * offset 未越过总数时取窗口并按 MSD 精简装配（{@link #toMsdMallGoodsOnPage}，跳过 spaces/收藏/卖点/购物车等关联查询），否则窗口为空。
     */
    private MktScrollResult queryMsdGoodsScroll(int offset, int limit, String title, MktMemberMsd memberMsd)
    {
        List<Integer> supplierPkeys = supplierDao.findPkeys(MobileSession.appid(), true);
        List<Long> goodsMsdKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.INTEGRAL_MSD_GOODS, Arrays.asList(memberMsd.getTag()));
        long total = goodsDao.countMsdGoods(title, supplierPkeys, goodsMsdKeys, MobileSession.appid());
        if (offset >= total)
        {
            return new MktScrollResult(total, Collections.emptyList());
        }
        List<MktGoods> list =
            goodsDao.listMsdGoods(offset, limit, title, supplierPkeys, goodsMsdKeys, MobileSession.appid());
        List<AppMsdGoodsOnList> window = new ArrayList<>(list.size());
        for (MktGoods g : list)
        {
            window.add(toMsdMallGoodsOnPage(g));
        }
        return new MktScrollResult(total, window);
    }

    /** MKT 商品列表小图：photo3 非空（去掉 fileStart 前缀后仍非空）取 photo3，否则取 photo1 首张。 */
    private String mktWrapperPhoto(List<String> photo1, String photo3)
    {
        if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
        {
            if (photo1 != null && !photo1.isEmpty())
            {
                return photo1.get(0);
            }
            return null;
        }
        return photo3;
    }

    /**
     * MktGoods → AppMallGoodsOnPage（MSD 专用精简装配）：仅设置客户端用到的字段与廉价拷贝，
     * 跳过 spaces/收藏/卖点/购物车等关联查询，避免为未使用字段产生 N+1 查询。
     */
    private AppMsdGoodsOnList toMsdMallGoodsOnPage(MktGoods g)
    {
        AppMsdGoodsOnList dto = new AppMsdGoodsOnList();
        dto.setPkey(g.getPkey() == null ? null : g.getPkey().longValue());
        dto.setSource("MALL");
        dto.setTitle(g.getTitle());
        dto.setTag(g.getTag());
        dto.setPrice(g.getPrice());
        dto.setPhoto1(g.getPhoto1());
        dto.setWrapperPhoto(mktWrapperPhoto(g.getPhoto1(), g.getPhoto3()));
        dto.setXsNum(g.getXsNum());
        dto.setGtype(g.getGtype());
        dto.setGoodsMain(g.getGoodsMain());
        return dto;
    }

    /** MKT 源滚动查询中间结果：MKT 总数（execCount）+ 当前窗口已装配列表。 */
    private static class MktScrollResult
    {
        final long total;

        final List<AppMsdGoodsOnList> window;

        MktScrollResult(long total, List<AppMsdGoodsOnList> window)
        {
            this.total = total;
            this.window = window;
        }
    }

    /**
     * MSD 滚动查询的 JdGoods 源：会员标签可见性 + spuId 去重，按任意 jdOffset/jdLimit 取窗口。
     * jdGoodsKeys ≤ 1w：走 DB 分页（queryMsdJdAggPage，仅 page/pagesize），跨边界 jdOffset 非页对齐时取至多 2 页；
     * jdGoodsKeys > 1w：in(pkey) 按 1w 分批全量聚合去重，再内存按 spuId 降序取窗口。
     */
    private List<AppMsdGoodsOnList> queryMsdJdGoodsScroll(MktMemberMsd memberMsd, String title, int jdOffset, int jdLimit)
    {
        List<AppMsdGoodsOnList> result = new ArrayList<>();
        if (jdLimit <= 0)
        {
            return result;
        }
        List<Long> jdGoodsKeys =
            tagVisibleDao.listTarget(TagVisibleTargetType.JD_GOODS, Lists.newArrayList(memberMsd.getTag()));

        List<Long> repPkeys = jdGoodsKeys.size() > 10000
            ? queryMsdJdRepPkeysBatched(title, jdGoodsKeys, jdOffset, jdLimit)
            : queryMsdJdRepPkeysPaged(title, jdGoodsKeys, jdOffset, jdLimit);
        if (repPkeys.isEmpty())
        {
            return result;
        }
        Map<Long, AppJdGoodsBackfill> goodsMap = jdGoodsDao.listBackfill(repPkeys, AppJdGoodsBackfill.class).stream()
            .collect(Collectors.toMap(AppJdGoodsBackfill::getPkey, g -> g, (a, b) -> a));
        for (Long pk : repPkeys)
        {
            AppJdGoodsBackfill g = goodsMap.get(pk);
            if (g == null)
            {
                continue;
            }
            AppMsdGoodsOnList dto = new AppMsdGoodsOnList();
            dto.setPkey(g.getPkey());
            dto.setSource("JD");
            dto.setTitle(g.getTitle());
            dto.setTag(g.getTag());
            dto.setPrice(g.getPrice());
            dto.setPriceOld(g.getPrice()); // 京东商品原价统一用 price
            dto.setPhoto1(g.getPhoto1());
            if (g.getPhoto1() != null && !g.getPhoto1().isEmpty())
            {
                dto.setWrapperPhoto(g.getPhoto1().get(0));
            }
            dto.setXsNum(g.getXsNum() == null ? 0 : g.getXsNum());
            dto.setLowestBuy(g.getLowestBuy());
            result.add(dto);
        }
        return result;
    }

    /** ≤1w：沿用 DB 分页聚合取窗口（spuId 去重 + page/pagesize），跨边界 jdOffset 非页对齐时取至多 2 页。 */
    private List<Long> queryMsdJdRepPkeysPaged(String title, List<Long> jdGoodsKeys, int jdOffset, int jdLimit)
    {
        int p = jdOffset / jdLimit;
        int rem = jdOffset % jdLimit;
        List<Long> repPkeys = new ArrayList<>();
        PageResult<AppJdGoodsOnPage> pg0 = queryMsdJdAggPage(title, jdGoodsKeys, p, jdLimit);
        collectPkeys(pg0, repPkeys, rem, jdLimit);
        if (repPkeys.size() < jdLimit && pg0.getContent().size() == jdLimit)
        {
            // 当前页满，可能还有下一页，补取一页凑齐 jdLimit
            PageResult<AppJdGoodsOnPage> pg1 = queryMsdJdAggPage(title, jdGoodsKeys, p + 1, jdLimit);
            collectPkeys(pg1, repPkeys, 0, jdLimit);
        }
        return repPkeys;
    }

    /**
     * >1w：jdGoodsKeys 按 1w 分批全量聚合 spuId 去重(min pkey)，合并后内存按 spuId 降序取窗口 [jdOffset, jdOffset+jdLimit)。
     * 排序等价原 sort(spuId, true)（spuId 降序）；ALL 范围商品多批重复，靠 map key 自动去重。
     */
    private List<Long> queryMsdJdRepPkeysBatched(String title, List<Long> jdGoodsKeys, int jdOffset, int jdLimit)
    {
        Map<Long, Long> spuRep = new HashMap<>();
        for (int i = 0; i * 10000 < jdGoodsKeys.size(); i++)
        {
            int from = i * 10000;
            int to = Math.min(jdGoodsKeys.size(), (i + 1) * 10000);
            for (AppJdGoodsOnPage jg : queryMsdJdAggList(title, jdGoodsKeys.subList(from, to)))
            {
                spuRep.merge(jg.getSpuId(), jg.getPkey(), Long::min);
            }
        }
        List<Long> spuIds = new ArrayList<>(spuRep.keySet());
        spuIds.sort(Collections.reverseOrder()); // spuId 降序，等价 sort(spuId, true)
        List<Long> repPkeys = new ArrayList<>();
        for (int i = jdOffset; i < spuIds.size() && repPkeys.size() < jdLimit; i++)
        {
            repPkeys.add(spuRep.get(spuIds.get(i)));
        }
        return repPkeys;
    }

    /** JD 商品按会员标签可见性聚合 spuId 去重(min pkey)，无分页/排序（供 >1w 分批全量合并）。 */
    private List<AppJdGoodsOnPage> queryMsdJdAggList(String title, List<Long> jdGoodsKeys)
    {
        AggregationBuilder<Long, JdGoods> b = jdGoodsDao.aggregation()
            .eq(JdGoods.F.idDel, false)
            .eq(JdGoods.F.enabled, true)
            .eq(JdGoods.F.skuState, 1)
            .like(JdGoods.F.title, title);
        if (CollectionUtil.isNotEmpty(jdGoodsKeys))
        {
            //@formatter:off
            b.or()
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
            b.eq(JdGoods.F.visibleRange, MemberVisibleRange.ALL);
        }
        return b.groupby(JdGoods.F.spuId, JdGoods.F.spuId).min(JdGoods.F.pkey, JdGoods.F.pkey)
            .execListDto(AppJdGoodsOnPage.class);
    }

    /** MSD 京东商品去重聚合：spuId 分组取最小 pkey 作代表 SKU，按 spuId 排序保证分页稳定。 */
    private PageResult<AppJdGoodsOnPage> queryMsdJdAggPage(String title, List<Long> jdGoodsKeys, int page, int pagesize)
    {
        AggregationBuilder<Long, JdGoods> b = jdGoodsDao.aggregation()
            .eq(JdGoods.F.idDel, false)
            .eq(JdGoods.F.enabled, true)
            .eq(JdGoods.F.skuState, 1)
            .like(JdGoods.F.title, title);
        if (CollectionUtil.isNotEmpty(jdGoodsKeys))
        {
            //@formatter:off
            b.or()
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
            b.eq(JdGoods.F.visibleRange, MemberVisibleRange.ALL);
        }
        return b.groupby(JdGoods.F.spuId, JdGoods.F.spuId)
            .min(JdGoods.F.pkey, JdGoods.F.pkey)
            .sort(JdGoods.F.spuId, true)
            .page(page)
            .pagesize(pagesize)
            .execDto(AppJdGoodsOnPage.class);
    }

    /** 从聚合结果页中收集代表 pkey：跳过前 skip 条，最多收 max 条。 */
    private void collectPkeys(PageResult<AppJdGoodsOnPage> pg, List<Long> dst, int skip, int max)
    {
        List<AppJdGoodsOnPage> content = pg.getContent();
        for (int i = skip; i < content.size() && dst.size() < max; i++)
        {
            dst.add(content.get(i).getPkey());
        }
    }

    public PageResult<AppGoodsAppOnList> queryAppGoodsV3(Integer page, Integer pagesize, Integer gtype, Integer goodsMain,
        MType mType, String title, Integer hotSort, Integer priceSort, String date, Boolean isOnPresale,
        Boolean guessLike)
    {
        List<Integer> supplierPkeys = null;
        // 积分商品，过滤掉停用的供应商
        if (mType == MType.INTEGRAL_GOODS)
        {
            supplierPkeys = supplierDao.findPkeys(MobileSession.appid(), true);
        }
        PageResult<MktGoods> list = goodsDao.queryAppGoods(0,
            3000,
            gtype,
            goodsMain,
            mType,
            title,
            hotSort,
            priceSort,
            date,
            isOnPresale,
            null,
            supplierPkeys,
            null,
            guessLike,
            null,
            null,
            MobileSession.appid(),
            null);
        PageResult<AppGoodsAppOnList> result = assembleGoodsDTO(list, mType, isOnPresale, false, page, pagesize);
        assembleGwcNum(result);
        return result;
    }
    
    public PageResult<AppGoodsAppOnList> queryAppGuessLikeGoods(Integer page, Integer pagesize)
    {
        String marketPkey = MobileSession.farmerPkey();
        if (StringUtils.isBlank(marketPkey))
        {
            marketPkey = Constant.Operation + MobileSession.appid();
        }
        PageResult<MktGoods> list = goodsDao.queryAppGuessLikeGoods(0, 10000, marketPkey);
        PageResult<AppGoodsAppOnList> result = assembleGoodsDTO(list, MType.MARKET_GOODS, false, false, page, pagesize);
        assembleGwcNum(result);
        return result;
    }
    
    public PageResult<AppGoodsAppOnList> queryAppMemberGoods(Integer page, Integer pagesize)
    {
        String marketPkey = MobileSession.farmerPkey();
        if (StringUtils.isBlank(marketPkey))
        {
            marketPkey = Constant.Operation + MobileSession.appid();
        }
        PageResult<MktGoods> list = goodsDao.queryAppMemberGoods(0, 10000, marketPkey);
        PageResult<AppGoodsAppOnList> result = assembleGoodsDTO(list, MType.MARKET_GOODS, false, true, page, pagesize);
        assembleGwcNum(result);
        return result;
    }
    
    private PageResult<AppGoodsAppOnList> assembleGoodsDTO(PageResult<MktGoods> pr, MType mType, Boolean isOnPresale,
        Boolean flag, int page, int pagesize)
    {
//        List<AppGoodsAppOnList> beanListFrom = BeanUtil.beanListFrom(AppGoodsAppOnList.class, list);
//        PageResult<AppGoodsAppOnList> result = BeanUtil.beanPageFrom(AppGoodsAppOnList.class, list);
        PageResult<AppGoodsAppOnList> beanPageFrom  = BeanUtil.beanPageFrom(AppGoodsAppOnList.class, pr);
        Integer memberPkey = MobileSession.memberPkey();
        PageResult<AppGoodsAppOnList> res = assembleName(beanPageFrom, memberPkey, flag, page, pagesize, mType);
        for (AppGoodsAppOnList appGoodsAppOnList : res)
        {
            if (!appGoodsAppOnList.getSpaces().isEmpty())
            {
                Integer totalKcNum = 0;
                Integer totalXsNum = 0;
                for (MktGoodsSpaceOnList space : appGoodsAppOnList.getSpaces())
                {
                    totalKcNum += space.getKcNum();
                    totalXsNum += space.getXsNum();
                }
                Integer kcNumPer = 0;
                if (MType.SPECIAL_GOODS.equals(mType))
                {
                    if((totalKcNum + totalXsNum) == 0)
                        kcNumPer = 0;
                    else
                    {
                        kcNumPer = new BigDecimal(totalXsNum)
                            .divide(new BigDecimal(totalKcNum + totalXsNum), 2, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100))
                            .intValue();
                    }
                    if (kcNumPer > 100) kcNumPer = 100;
                    appGoodsAppOnList.setKcNumPer(kcNumPer);
                    
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    Long remainingTime = cal.getTimeInMillis() - new Date().getTime();
                    appGoodsAppOnList.setRemainingTime(remainingTime);
                }
                
                if (MType.INTEGRAL_PRESALE_GOODS.equals(mType))
                {
                    Date endDate = appGoodsAppOnList.getEndDate();
                    long remainingTime = 0l;
                    if(endDate == null)
                    {
                        if (appGoodsAppOnList.getStartDate().getTime() > new Date().getTime())
                        {
                            remainingTime = appGoodsAppOnList.getStartDate().getTime() - new Date().getTime();
                        }
                    }
                    else
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.HOUR, 8);// 24小时制   
                        long zeroT = cal.getTime().getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24)
                            - TimeZone.getDefault().getRawOffset();
                        long endT = zeroT + 24 * 60 * 60 * 1000 - 1;
                       
                        if (endT > new Date().getTime()
                            && appGoodsAppOnList.getStartDate().getTime() < new Date().getTime())
                        {
                            remainingTime = endT - new Date().getTime();
                        }
                        else if (appGoodsAppOnList.getStartDate().getTime() > new Date().getTime())
                        {
                            remainingTime = appGoodsAppOnList.getStartDate().getTime() - new Date().getTime();
                        }
                    }
                    appGoodsAppOnList.setRemainingTime(remainingTime);
                }
                if (MType.PRESALE_GOODS.equals(mType))
                {
                    Date endDate = appGoodsAppOnList.getEndDate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate);
                    cal.add(Calendar.HOUR, 8);// 24小时制   
                    long zeroT = cal.getTime().getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24)
                        - TimeZone.getDefault().getRawOffset();
                    long endT = zeroT + 24 * 60 * 60 * 1000 - 1;
                    long remainingTime = 0l;
                    if (endT > new Date().getTime()
                        && appGoodsAppOnList.getStartDate().getTime() < new Date().getTime())
                    {
                        remainingTime = endT - new Date().getTime();
                    }
                    else if (appGoodsAppOnList.getStartDate().getTime() > new Date().getTime())
                    {
                        remainingTime = appGoodsAppOnList.getStartDate().getTime() - new Date().getTime();
                    }
                    appGoodsAppOnList.setRemainingTime(remainingTime);
                }
                
                if (MType.COLLAGE_GOODS.equals(mType))
                {
                    Long remainingTime = 0l;
                    remainingTime = appGoodsAppOnList.getEndDate().getTime() - new Date().getTime();
                    appGoodsAppOnList.setRemainingTime(remainingTime);
                    
                    // 计算下 还有多少人拼团成功
                    List<MktOrderGroup> exec = orderGroupDao.select()
                        .eq("status", OrderGroupStatus.NOT_GROUPS)
                        .eq("goods", appGoodsAppOnList.getPkey())
                        .exec();
                    if (exec != null && exec.size() > 0)
                    {
                        MktOrderGroup group = exec.get(0);
                        int s = group.getGroupNum() - group.getBuyNum();
                        appGoodsAppOnList.setExtendCon(s + "");
                    }
                }
            }
            
            if (memberPkey != null)
            {
                Integer collectionPkey = collectionManager.chkCollection(1, appGoodsAppOnList.getPkey());
                if (collectionPkey != 0)
                {
                    appGoodsAppOnList.setCollection(true);
                    appGoodsAppOnList.setCollectionPkey(collectionPkey);
                }
            }
            List<String> sellingPoints =
                goodsSellingPointDao.listContentByGoods(appGoodsAppOnList.getPkey(), MobileSession.appid());
            appGoodsAppOnList.setSellingPoints(sellingPoints);
        }
        return res;
    }
    
    private void assembleGwcNum(PageResult<AppGoodsAppOnList> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) return;
        Map<String, Number> map = gwcDao.aggregation()
            .eq("member", memberPkey).isNotNull("goods")
            .execGroupBySum("goods", "num");
        Map<String, Number> mapJd = gwcDao.aggregation()
            .eq("member", memberPkey).isNotNull("skuId")
            .execGroupBySum("skuId", "num");
        for (AppGoodsAppOnList agal : result.getContent())
        {
            String pkey = agal.getPkey().toString();
            if (map.containsKey(pkey))
            {
                Number number = map.get(pkey);
                if (number != null) agal.setGwcNum(number.intValue());
            }
            if (mapJd.containsKey(pkey))
            {
                Number number = map.get(pkey);
                if (number != null) agal.setGwcNum(number.intValue());
            }
        }
        
    }
    
    // 查询购物车各个商品数量
    private PageResult<AppGoodsAppOnList> assembleName(PageResult<AppGoodsAppOnList> pr, Integer memberPkey, Boolean flag, int page, int pagesize, MType mType)
    {
        List<Integer> keys = new ArrayList<>();
        pr.getContent().forEach(e -> keys.add(e.getPkey()));
        if(keys.isEmpty())
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        SelectBuilder<Integer, MktGoodsSpace> builder =
            goodsSpaceDao.select().in("goods", keys).sort("price", false);
        if (flag) builder.notEq("priceMember", 0);
        List<MktGoodsSpaceOnList> sList = builder.execDto(MktGoodsSpaceOnList.class);
        Map<Integer,List<MktGoodsSpaceOnList>> map = new HashMap<>();
        sList.forEach(e -> {
            if(!map.containsKey(e.getGoods()))
            {
                List<MktGoodsSpaceOnList> v = new ArrayList<>();
                map.put(e.getGoods(), v);
            }
            map.get(e.getGoods()).add(e);
        });
        for (AppGoodsAppOnList bean : pr.getContent())
        {
            List<MktGoodsSpaceOnList> spaceList = map.get(bean.getPkey());
            int kcNum = 0;
            bean.setKcNum(kcNum);
            for (MktGoodsSpaceOnList space : spaceList)
            {
                kcNum += space.getKcNum();
                space.setStatus(0);
            }
            bean.setSpaces(spaceList);
            bean.setKcNum(kcNum);
        }
        List<AppGoodsAppOnList> content = new ArrayList<>();
        pr.getContent().forEach(e -> content.add(e));
//        if(!MType.INTEGRAL_PRESALE_GOODS.equals(mType) && !MType.INTEGRAL_GOODS.equals(mType))
//        {
//            Collections.sort(content, new Comparator<AppGoodsAppOnList>()
//            {
//                @Override
//                public int compare(AppGoodsAppOnList o1, AppGoodsAppOnList o2)
//                {
//                    return o2.getKcNum() - o1.getKcNum();
//                }
//            });
//        }
        PageResult<AppGoodsAppOnList> res = pr;
        res.setContent(content);
//            PageUtil.page(content, PageParameter.of(page, pagesize));
        for (AppGoodsAppOnList bean : res.getContent())
        {
            if (StringUtils.isBlank(bean.getPhoto3()) || StringUtils.isBlank(bean.getPhoto3().replace(fileStart, "")))
            {
                if (bean.getPhoto1() != null && bean.getPhoto1().size() > 0)
                    bean.setWrapperPhoto(bean.getPhoto1().get(0));
            }
            else
                bean.setWrapperPhoto(bean.getPhoto3());
            bean.setGtypeName(gtypeDao.get(bean.getGtype()).getName());
            bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
            
            if (bean.getMType().getIndex() == 5)
            {
                // 如果是砍价商品 增加已经砍价成功的人数
                Integer orderCount = orderDao.getOrderCount(bean.getPkey());
                bean.setCutMemberNum(orderCount);
                Integer judgOrderCut = orderDao.judgOrderCut(bean.getPkey(), memberPkey);
                if (judgOrderCut == null)
                    bean.setIsCut(false);
                else
                    bean.setIsCut(true);
            }
        }
        return res;
    }
    
    public AppGoodsDetailsDTO getAppGoods(Integer pkey)
    {
        goodsRepository.autoViewCount(pkey);
        MktGoodsDetailsDTO mktGoodsDetailsDTO = goodsManager.getGoods(pkey);
        if (mktGoodsDetailsDTO == null) return null;
        if (Boolean.FALSE.equals(mktGoodsDetailsDTO.getEnabled()))
            throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
        AppGoodsDetailsDTO appGoodsDetailsDTO = BeanUtil.beanFrom(AppGoodsDetailsDTO.class, mktGoodsDetailsDTO);
        MktVendor vendor = vendorDao.getVendor(appGoodsDetailsDTO.getVendor());
        if (vendor != null)
        {
            appGoodsDetailsDTO.setVendorName(vendor.getDisplayName());
            appGoodsDetailsDTO.setVendorBooth(vendor.getBooth());
            String headIcon = vendorFileDao.getHeadIcon(vendor.getPkey());
            appGoodsDetailsDTO.setVendorHeadIcon(headIcon);
            long goodsNum = goodsDao.countVendorGoodsNum(vendor.getPkey(), MobileSession.appid());
            appGoodsDetailsDTO.setVendorGoodsNum(goodsNum);
        }
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null)
        {
            Integer collectionPkey = collectionManager.chkCollection(1, appGoodsDetailsDTO.getPkey());
            if (collectionPkey != 0)
            {
                appGoodsDetailsDTO.setCollection(true);
                appGoodsDetailsDTO.setCollectionPkey(collectionPkey);
            }
        }
        long remainingTime = 0l;
        Date endDate = mktGoodsDetailsDTO.getEndDate();
        
        Calendar cal = Calendar.getInstance();
        if(endDate != null)
            cal.setTime(endDate);
        cal.add(Calendar.HOUR, 8);// 24小时制   
        
        long zeroT =
            cal.getTime().getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long endT = zeroT + 24 * 60 * 60 * 1000 - 1;
        if (MType.PRESALE_GOODS.equals(mktGoodsDetailsDTO.getMType()) || MType.INTEGRAL_PRESALE_GOODS.equals(mktGoodsDetailsDTO.getMType()))
        {
            if(endDate == null && mktGoodsDetailsDTO.getStartDate().getTime() < new Date().getTime())
            {
                appGoodsDetailsDTO.setIsPresale(true);
            }
            else if (endT > new Date().getTime() && mktGoodsDetailsDTO.getStartDate().getTime() < new Date().getTime())
            {
                appGoodsDetailsDTO.setIsPresale(true);
                remainingTime = endT - new Date().getTime();
            }
            else if (mktGoodsDetailsDTO.getStartDate().getTime() > new Date().getTime())
            {
                appGoodsDetailsDTO.setIsPresale(false);
                remainingTime = mktGoodsDetailsDTO.getStartDate().getTime() - new Date().getTime();
            }
            appGoodsDetailsDTO.setRemainingTime(remainingTime);
            MktGoodsPresale presale = goodsPresaleDao.get(pkey);
            if(presale != null)
            {
                appGoodsDetailsDTO.setPresaleStartDate(presale.getStartDate());
                appGoodsDetailsDTO.setPresaleEndDate(presale.getEndDate());
            }
        }
        else if (MType.SPECIAL_GOODS.equals(mktGoodsDetailsDTO.getMType())
            || MType.COLLAGE_GOODS.equals(mktGoodsDetailsDTO.getMType()))
        {
            if (endT > new Date().getTime() && mktGoodsDetailsDTO.getStartDate().getTime() < new Date().getTime())
            {
                System.out.println("pkey: " + pkey);
                remainingTime = endT - new Date().getTime();
            }
            appGoodsDetailsDTO.setRemainingTime(remainingTime);
        }
        else if (MType.CUT_GOODS.equals(mktGoodsDetailsDTO.getMType()))
        {
            List<MktGoodsSpaceOnList> spaces = mktGoodsDetailsDTO.getSpaces();
            
            for (MktGoodsSpaceOnList space : spaces)
            {
                BigDecimal subtract = space.getPriceOld().subtract(space.getPrice());
                if (memberPkey != null)
                {
                    Integer judgOrderCut = orderDao.judgOrderCut(mktGoodsDetailsDTO.getPkey(), memberPkey);
                    log.info("judgOrderCut: {}", judgOrderCut);
                    if (judgOrderCut != null)
                    {
                        MktOrder order = orderDao.get(judgOrderCut);
                        BigDecimal cutAmt = order.getCutAmt();
                        if (cutAmt == null) cutAmt = BigDecimal.ZERO;
                        appGoodsDetailsDTO.setCutAmt(cutAmt);
                        appGoodsDetailsDTO.setRCutAmt(subtract.subtract(cutAmt));
                        appGoodsDetailsDTO.setIsCut(true);
                    }
                }
            }
        }
        else if (MType.INTEGRAL_MSD_GOODS.equals(mktGoodsDetailsDTO.getMType()))
        {
            MktGoodsPresale presale = goodsPresaleDao.get(pkey);
            if(presale != null)
            {
                appGoodsDetailsDTO.setPresaleStartDate(presale.getStartDate());
                appGoodsDetailsDTO.setPresaleEndDate(presale.getEndDate());
            }
        }
        
        List<MktCookfd> list = cookfdRepository.queryByGoods(pkey);
        List<MktCookfdAppOnList> result = BeanUtil.beanListFrom(MktCookfdAppOnList.class, list);
        if (memberPkey != null)
        {
            for (MktCookfdAppOnList mktCookfdAppOnList : result)
            {
                Integer collectionPkey = collectionManager.chkCollection(0, mktCookfdAppOnList.getPkey());
                if (collectionPkey != 0)
                {
                    mktCookfdAppOnList.setCollection(true);
                }
            }
            Number sum = gwcDao.aggregation().eq("member", memberPkey).execSum("num");
            if (sum != null) appGoodsDetailsDTO.setGwcNum(sum.intValue());
        }
        appGoodsDetailsDTO.setCookfdList(result);
        SysFarmer farmer = MobileSession.farmer();
        if (farmer != null && farmer.getConfig() != null)
        {
            appGoodsDetailsDTO.setStartingPrice(farmer.getConfig().getStartingPrice());
        }
        List<GoodsProcessOnInfo> processLines = new ArrayList<>();
        if(Boolean.TRUE.equals(appGoodsDetailsDTO.getIsProcess()))
        {
            List<Integer> listProcess = goodsProcessDao.listProcess(pkey);
            if(!listProcess.isEmpty())
            {
                List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                    .in("pkey", listProcess)
                    .exec();
                
                gsList.forEach(e -> {
                    MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                        .eq("mType", MType.PROCESS_GOODS)
                        .eq("idDel", false)
                        .eq("enabled", true)
                        .exec();
                    if(mktGoods != null)
                    {
                        GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                        p.setProcess(e.getPkey());
                        p.setProcessName(mktGoods.getTitle());
                        if(StringUtils.isNotBlank(e.getPhoto1()))
                            e.setPhoto1(e.getPhoto1());
                        else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                            p.setPhoto(mktGoods.getPhoto1().get(0));
                        p.setPrice(e.getPrice());
                        processLines.add(p);
                    }
                });
            }
        }
        appGoodsDetailsDTO.setProcessLines(processLines);
        return appGoodsDetailsDTO;
    }
    
    public AppGoodsDetailsDTO getMemberGoods(Integer pkey)
    {
        goodsRepository.autoViewCount(pkey);
        MktGoodsDetailsDTO mktGoodsDetailsDTO = goodsManager.getMemberGoods(pkey);
        if (mktGoodsDetailsDTO == null) return null;
        AppGoodsDetailsDTO appGoodsDetailsDTO = BeanUtil.beanFrom(AppGoodsDetailsDTO.class, mktGoodsDetailsDTO);
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null)
        {
            Integer collectionPkey = collectionManager.chkCollection(1, appGoodsDetailsDTO.getPkey());
            if (collectionPkey != 0)
            {
                appGoodsDetailsDTO.setCollection(true);
                appGoodsDetailsDTO.setCollectionPkey(collectionPkey);
            }
        }
        
        List<MktCookfd> list = cookfdRepository.queryByGoods(pkey);
        List<MktCookfdAppOnList> result = BeanUtil.beanListFrom(MktCookfdAppOnList.class, list);
        if (memberPkey != null)
        {
            for (MktCookfdAppOnList mktCookfdAppOnList : result)
            {
                Integer collectionPkey = collectionManager.chkCollection(0, mktCookfdAppOnList.getPkey());
                if (collectionPkey != 0)
                {
                    mktCookfdAppOnList.setCollection(true);
                }
            }
            Number sum = gwcDao.aggregation().eq("member", memberPkey).execSum("num");
            if (sum != null) appGoodsDetailsDTO.setGwcNum(sum.intValue());
        }
        appGoodsDetailsDTO.setCookfdList(result);
        SysFarmer farmer = MobileSession.farmer();
        if (farmer != null && farmer.getConfig() != null)
        {
            appGoodsDetailsDTO.setStartingPrice(farmer.getConfig().getStartingPrice());
        }
        
        return appGoodsDetailsDTO;
    }
    
    public PageResult<AppGoodsCommentOnList> queryGoodsComments(Integer page, Integer pagesize, Integer pkey)
    {
        return orderGoodsCommentDao.queryByGoods(page, pagesize, pkey, AppGoodsCommentOnList.class);
    }
    
    public List<GoodsProcessOnInfo> listGoodsProcessOnInfo(Integer pkey)
    {
        MktGoods goods = goodsDao.get(pkey);
        List<GoodsProcessOnInfo> processLines = new ArrayList<>();
        if(Boolean.TRUE.equals(goods.getIsProcess()))
        {
            List<Integer> listProcess = goodsProcessDao.listProcess(pkey);
            if(!listProcess.isEmpty())
            {
                List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                    .in("pkey", listProcess)
                    .exec();
               
                gsList.forEach(e -> {
                    MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                        .eq("mType", MType.PROCESS_GOODS)
                        .eq("idDel", false)
                        .eq("enabled", true)
                        .exec();
                    if(mktGoods != null)
                    {
                        GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                        p.setProcess(e.getPkey());
                        p.setProcessName(mktGoods.getTitle());
                        processLines.add(p);
                    }
                });
            }
        }
        return processLines;
    }
    
    public PageResult<AppRecommendGoodsOnPage> queryAppGoodsRecommend(Integer page, Integer pagesize,
        GoodsRecommendZone zone, Integer sourceGoods)
    {
        Integer ascription = MobileSession.appid();
        String currentFarmer = MobileSession.farmerPkey();
        PageResult<AppRecommendGoodsOnPage> res = null;
        // 商品详情，特殊处理，把商品推荐的
        if (zone == GoodsRecommendZone.GOODS_DETAIL)
        {
            if (sourceGoods == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品主键不能为空");
            res = doQueryAppGoodsRecommend(page, pagesize, ascription, currentFarmer, zone, sourceGoods);
            if (!res.hasContent())
                res = doQueryAppGoodsRecommend(page, pagesize, ascription, currentFarmer, zone);
        }
        else
        {
            res = doQueryAppGoodsRecommend(page, pagesize, ascription, currentFarmer, zone);
        }
        assembleSpace4Recommend(res);
        assembleGwcNum4Recommend(res);
        return res;
    }
    
    private PageResult<AppRecommendGoodsOnPage> doQueryAppGoodsRecommend(Integer page, Integer pagesize,
        Integer ascription, String currentFarmer, GoodsRecommendZone zone)
    {
        return doQueryAppGoodsRecommend(page, pagesize, ascription, currentFarmer, zone, null);
    }
    
    private PageResult<AppRecommendGoodsOnPage> doQueryAppGoodsRecommend(Integer page, Integer pagesize,
        Integer ascription, String currentFarmer, GoodsRecommendZone zone, Integer sourceGoods)
    {
        SelectPageOps builder = goodsRecommendDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktGoodsRecommend.F.pkey, "recommendPkey")
            .as(MktGoodsRecommend.F.goods, "pkey")
            .as(MktGoodsRecommend.F.sort);
        if (zone == GoodsRecommendZone.GOODS_DETAIL && sourceGoods != null)
        {
            builder.eq(MktGoodsRecommend.F.sourceGoods, sourceGoods);
        }
        else
        {
            builder.isNull(MktGoodsRecommend.F.sourceGoods);
        }
        return builder.eq(MktGoodsRecommend.F.ascription, ascription)
            .or()
            .eq(MktGoodsRecommend.F.goodsFarmer, currentFarmer)
            .eq(MktGoodsRecommend.F.goodsFarmer, Constant.Operation + ascription)
            .close()
            .done()
            .join(MktGoodsRecommendZone.class, MktGoodsRecommend.F.pkey, MktGoodsRecommendZone.F.goodsRecommend)
            .eq(MktGoodsRecommendZone.F.zone, zone)
            .join(MktGoods.class, MktGoodsRecommend.F.goods, MktGoods.F.pkey)
            .as(MktGoods.F.mType)
            .as(MktGoods.F.title)
            .as(MktGoods.F.tag)
            .as(MktGoods.F.price)
            .as(MktGoods.F.vendor)
            .eq(MktGoods.F.enabled, true)
            .eq(MktGoods.F.idDel, false)
            .endJoin()
            .sort(MktGoodsRecommend.F.sort, false)
            .sort(MktGoodsRecommend.F.pkey)
            .exec(AppRecommendGoodsOnPage.class);
    }
    
    // 查询商品规格
    private void assembleSpace4Recommend(PageResult<AppRecommendGoodsOnPage> pr)
    {
        List<Integer> keys = pr.stream().map(AppRecommendGoodsOnPage::getPkey).collect(Collectors.toList());
        if (keys.isEmpty())
            return;
        List<MktGoodsSpaceOnList> sList = goodsSpaceDao.listByGoodsSortByPrice(keys, false, MktGoodsSpaceOnList.class);
        Map<Integer, List<MktGoodsSpaceOnList>> map = new HashMap<>();
        sList.forEach(e -> {
            if (!map.containsKey(e.getGoods()))
            {
                List<MktGoodsSpaceOnList> v = new ArrayList<>();
                map.put(e.getGoods(), v);
            }
            map.get(e.getGoods()).add(e);
        });
        for (AppRecommendGoodsOnPage bean : pr)
        {
            List<MktGoodsSpaceOnList> spaceList = map.get(bean.getPkey());
            bean.setSpaces(spaceList);
        }
    }
    
    // 查询购物车各个商品数量
    private void assembleGwcNum4Recommend(PageResult<AppRecommendGoodsOnPage> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null)
            return;
        Map<String, Number> map = gwcDao.aggregation()
            .eq("member", memberPkey)
            .isNotNull("goods")
            .execGroupBySum("goods", "num");
        Map<String, Number> mapJd = gwcDao.aggregation()
            .eq("member", memberPkey)
            .isNotNull("skuId")
            .execGroupBySum("skuId", "num");
        for (AppRecommendGoodsOnPage line : result.getContent())
        {
            String pkey = line.getPkey().toString();
            if (map.containsKey(pkey))
            {
                Number number = map.get(pkey);
                if (number != null)
                    line.setGwcNum(number.intValue());
            }
            if (mapJd.containsKey(pkey))
            {
                Number number = mapJd.get(pkey);
                if (number != null)
                    line.setGwcNum(number.intValue());
            }
        }
    }
}
