package cn.tofocus.lejia.dao.goods;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.db.*;
import cn.tofocus.db.aggs.AggregationBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.v3.MktVendorGoodsOnInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoods.F;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.v3.SortType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.repository.market.MktGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@DataSourceWithFileUrl
public class MktGoodsDao extends JpaSpecificationDelegate<Integer, MktGoods>
{
    
    @Autowired
    private MktGoodsRepository repository;
    
    public long countVendorGoodsNum(Integer vendor, Integer ascription)
    {
        return this.aggregation()
            .eq("vendor", vendor)
            .eq("enabled", true)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .execCount();
    }
    
    public PageResult<MktGoods> queryAppGoods(Integer page, Integer pagesize, Integer gtype, Integer goodsMain,
        MType mType, String title, Integer hotSort, Integer priceSort, String date, boolean isOnPresale,
        List<Integer> goodsPkeys, List<Integer> supplierPkeys, List<Long> goodsMsdKeys, Boolean guessLike, Integer topGoods,
        Boolean zoneRecommend, Integer ascription, Integer vendor)
    {
        SelectPageBuilder<Integer, MktGoods> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .notEq("pkey", topGoods)
            .eq("zoneRecommend", zoneRecommend);

        SelectOneBuilder<Integer, MktGoods> topBuilder = selectOne()
                .eq(F.ascription, ascription)
                .eq(F.idDel, false)
                .eq(F.pkey, topGoods);
        
        if (vendor != null)
        {
            builder.isNotNull("vendor").eq("vendor", vendor);
            topBuilder.isNotNull(F.vendor).eq(F.vendor, vendor);
        }
        // 日期
        String formatDate = DateUtil.formatDate(new Date());
        Date now = DateUtil.formatDateStr(formatDate, "yyyy-MM-dd");
        if (StringUtils.isNotBlank(date))
        {
            now = DateUtil.formatDateStr(date, "yyyy-MM-dd");
        }
        // mType判断
        if (mType == null)
        {
            builder(builder);
            builder.in("mType", MType.MARKET_GOODS, MType.SPECIAL_GOODS, MType.BOX_GOODS).eq("enabled", true);
            builder(topBuilder);
            topBuilder.in(F.mType, MType.MARKET_GOODS, MType.SPECIAL_GOODS, MType.BOX_GOODS).eq(F.enabled, true);
        }
        else if (MType.MEMBER_GOODS.equals(mType))
        {
            builder(builder);
            builder.eq("mType", MType.MARKET_GOODS).eq("extendCon", "member").eq("enabled", true);
            builder(topBuilder);
            topBuilder.eq(F.mType, MType.MARKET_GOODS).eq(F.extendCon, "member").eq(F.enabled, true);
        }
        else if (MType.INTEGRAL_GOODS.equals(mType))
        {
            builder
                .eq("enabled", true)
                .eq("idDel", false)
                .eq("farmer", Constant.Operation + ascription)
                .eq("company", Constant.Operation + ascription)
                .or()
                    .and()
                        .eq("mType", MType.INTEGRAL_GOODS)
                        .isNotNull(F.supplier)
                        .in(F.supplier, supplierPkeys)
                    .close()
                    .eq("mType", MType.GIFT_GOODS)
                    .eq("mType", MType.COUPON_GOODS)
                .close()
                .done();
            topBuilder.eq(F.enabled, true)
                .eq(F.idDel, false)
                .eq(F.farmer, Constant.Operation + ascription)
                .eq(F.company, Constant.Operation + ascription)
                .or()
                    .and()
                        .eq(F.mType, MType.INTEGRAL_GOODS)
                        .isNotNull(F.supplier)
                        .in(F.supplier, supplierPkeys)
                    .close()
                    .eq(F.mType, MType.GIFT_GOODS)
                    .eq(F.mType, MType.COUPON_GOODS)
                .close()
                .done();
        }
        else if (MType.INTEGRAL_BNYP_GOODS.equals(mType))
        {
            builder.eq("enabled", true)
                .eq("idDel", false)
                .eq("farmer", Constant.Operation + ascription)
                .eq("company", Constant.Operation + ascription)
                .or()
                    .and()
                        .eq("mType", MType.INTEGRAL_BNYP_GOODS)
                        .isNotNull(F.supplier)
                        .in(F.supplier, supplierPkeys)
                    .close()
                .close()
                .done();
            topBuilder.eq(F.enabled, true)
                .eq(F.idDel, false)
                .eq(F.farmer, Constant.Operation + ascription)
                .eq(F.company, Constant.Operation + ascription)
                .or()
                    .and()
                        .eq(F.mType, MType.INTEGRAL_BNYP_GOODS)
                        .isNotNull(F.supplier)
                        .in(F.supplier, supplierPkeys)
                    .close()
                .close()
                .done();
        }
        else if (MType.INTEGRAL_MSD_GOODS.equals(mType))
        {
            builder.eq("enabled", true)
                .eq("idDel", false)
                .eq("farmer", Constant.Operation + ascription)
                .eq("company", Constant.Operation + ascription)
                .eq("mType", MType.INTEGRAL_MSD_GOODS)
                .isNotNull(F.supplier)
                .in(F.supplier, supplierPkeys);
            topBuilder.eq(F.enabled, true)
                .eq(F.idDel, false)
                .eq(F.farmer, Constant.Operation + ascription)
                .eq(F.company, Constant.Operation + ascription)
                .eq(F.mType, MType.INTEGRAL_MSD_GOODS)
                .isNotNull(F.supplier)
                .in(F.supplier, supplierPkeys);
            if (CollectionUtil.isNotEmpty(goodsMsdKeys))
            {
                builder.or()
                        .eq("visibleRange", MemberVisibleRange.ALL)
                        .and()
                            .eq("visibleRange", MemberVisibleRange.TAG)
                            .in("pkey", goodsMsdKeys)
                        .close()
                    .close()
                    .done();
                topBuilder.or()
                        .eq("visibleRange", MemberVisibleRange.ALL)
                        .and()
                            .eq("visibleRange", MemberVisibleRange.TAG)
                            .in("pkey", goodsMsdKeys)
                        .close()
                    .close()
                    .done();
            }
            else
            {
                builder.eq("visibleRange", MemberVisibleRange.ALL);
                topBuilder.eq("visibleRange", MemberVisibleRange.ALL);
            }
        }
        else if (MType.INTEGRAL_PRESALE_GOODS.equals(mType))
        {
            builder.eq("enabled", true)
                .eq("mType", mType)
                .eq("farmer", Constant.Operation + ascription)
                .eq("company", Constant.Operation + ascription)
                .isNotNull(F.supplier)
                .in(F.supplier, supplierPkeys);
            if (!isOnPresale)
            {
                builder.gt("startDate", now).sort("startDate", true);
            }
            else
            {
                builder.le("startDate", now)
                    .or()
                        .ge("endDate", now)
                        .isNull("endDate")
                    .close()
                    .done()
                    .sort("endDate", false);
            }
            
            topBuilder.eq(F.enabled, true)
                .eq(F.mType, mType)
                .eq(F.farmer, Constant.Operation + ascription)
                .eq(F.company, Constant.Operation + ascription)
                .isNotNull(F.supplier)
                .in(F.supplier, supplierPkeys);
            if (!isOnPresale)
            {
                topBuilder.gt(F.startDate, now).sort(F.startDate, true);
            }
            else
            {
                topBuilder.le(F.startDate, now)
                    .or()
                        .ge(F.endDate, now)
                        .isNull(F.endDate)
                    .close()
                    .done()
                    .sort(F.endDate, false);
            }
        }
        else if (MType.PRESALE_GOODS.equals(mType))
        {
            builder(builder);
            builder.eq("mType", mType);
            if (!isOnPresale)
            {
                builder.eq("enabled", false).gt("startDate", now).sort("startDate", true);
            }
            else
            {
                builder.eq("enabled", true).le("startDate", now).ge("endDate", now);
            }
            builder(topBuilder);
            topBuilder.eq(F.mType, mType);
            if (!isOnPresale)
            {
                topBuilder.eq(F.enabled, false).gt(F.startDate, now).sort(F.startDate, true);
            }
            else
            {
                topBuilder.eq(F.enabled, true).le(F.startDate, now).ge(F.endDate, now);
            }
        }
        else if (MType.SPECIAL_GOODS.equals(mType))
        {
            builder(builder);
            builder(topBuilder);
            LocalDate of = null;
            if (StringUtils.isBlank(date))
                of = LocalDate.now();
            else
            {
                of = LocalDate.of(Integer.valueOf(date.substring(0, 4)),
                    Integer.valueOf(date.substring(5, 7)),
                    Integer.valueOf(date.substring(8, 10)));
            }
            if (LocalDate.now().isBefore(of))
            {
                builder.ge(F.endDate, of.toString()).sort(F.startDate, true);
                topBuilder.ge(F.endDate, of.toString()).sort(F.startDate, true);
            }
            else
            {
                builder.le(F.startDate, of.toString())
                    .ge(F.endDate, of.toString())
                    .eq(F.enabled, true)
                    .sort(F.startDate, true);
                topBuilder.le(F.startDate, of.toString())
                    .ge(F.endDate, of.toString())
                    .eq(F.enabled, true)
                    .sort(F.startDate, true);
            }
            builder.eq(F.mType, mType);
            topBuilder.eq(F.mType, mType);
        }
        else
        {
            builder(builder);
            builder.eq("enabled", true).eq("mType", mType);
            builder(topBuilder);
            topBuilder.eq(F.enabled, true).eq(F.mType, mType);
        }
        
        if (gtype != null)
        {
            builder.eq("gtype", gtype);
            topBuilder.eq(F.gtype, gtype);
        }
        // 2021-11-30 geshaojian
        if (goodsMain != null)
        {
            builder.eq("goodsMain", goodsMain);
            topBuilder.eq(F.goodsMain, goodsMain);
        }
        // 2021-12-02 geshaojian
        if (guessLike != null)
        {
            builder.eq("guessLike", guessLike);
        }
        
        if (goodsPkeys != null && goodsPkeys.size() > 0) builder.in("pkey", goodsPkeys.toArray());
        
        if (StringUtils.isNotBlank(title)) builder.like("title", title);
        
        if (priceSort == 1)
            builder.sort("price", true);
        else if (priceSort == 2) builder.sort("price", false);
        
        if (hotSort != 0)
        {
            if (hotSort == 1)
                builder.sort("xsNum", true);
            else if (hotSort == 2) builder.sort("xsNum", false);
        }

        PageResult<MktGoods> res = builder.sort(F.sort, false).sort(F.createdTime, true).exec();
        if (page == 0 && topGoods != null)
        {
            MktGoods top = topBuilder.exec();
            if (top != null)
            {
                List<MktGoods> list = new ArrayList<>();
                list.add(top);
                list.addAll(res.getContent());
                res.setContent(list);
            }
        }
        return res;
    }

    /**
     * MSD 民生商品符合条件的总数（execCount）。供滚动查询判断 offset 是否越过 MKT 源——
     * 用真实总数，不依赖分页查询在越界页退化为 0 的 total。条件同 {@link #listMsdGoods}。
     */
    public long countMsdGoods(String title, List<Integer> supplierPkeys, List<Long> goodsMsdKeys, Integer ascription)
    {
        AggregationBuilder<Integer, MktGoods> b = aggregation()
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .eq(F.farmer, Constant.Operation + ascription)
            .eq(F.company, Constant.Operation + ascription)
            .eq(F.mType, MType.INTEGRAL_MSD_GOODS)
            .isNotNull(F.supplier)
            .in(F.supplier, supplierPkeys)
            .like(F.title, title);
        if (CollectionUtil.isNotEmpty(goodsMsdKeys))
        {
            //@formatter:off
            b.or()
                    .eq(F.visibleRange, MemberVisibleRange.ALL)
                    .and()
                        .eq(F.visibleRange, MemberVisibleRange.TAG)
                        .in(F.pkey, goodsMsdKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            b.eq(F.visibleRange, MemberVisibleRange.ALL);
        }
        return b.execCount();
    }

    /**
     * MSD 民生商品滚动查询：仅 MSD 条件 + title 模糊，用 offset/limit 取窗口。
     * 条件取自 {@link #queryAppGoods} 的 INTEGRAL_MSD_GOODS 分支（去掉 gtype/goodsMain/topGoods），
     * 排序追加 sort(pkey) 作唯一兜底保证 offset 滚动稳定。总数请用 {@link #countMsdGoods}（不依赖本方法分页 total）。
     */
    public List<MktGoods> listMsdGoods(Integer offset, Integer limit, String title,
        List<Integer> supplierPkeys, List<Long> goodsMsdKeys, Integer ascription)
    {
        SelectBuilder<Integer, MktGoods> builder = select()
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .eq(F.farmer, Constant.Operation + ascription)
            .eq(F.company, Constant.Operation + ascription)
            .eq(F.mType, MType.INTEGRAL_MSD_GOODS)
            .isNotNull(F.supplier)
            .in(F.supplier, supplierPkeys)
            .like(F.title, title);
        if (CollectionUtil.isNotEmpty(goodsMsdKeys))
        {
            //@formatter:off
            builder.or()
                    .eq(F.visibleRange, MemberVisibleRange.ALL)
                    .and()
                        .eq(F.visibleRange, MemberVisibleRange.TAG)
                        .in(F.pkey, goodsMsdKeys)
                    .close()
                .close()
                .done();
            //@formatter:on
        }
        else
        {
            builder.eq(F.visibleRange, MemberVisibleRange.ALL);
        }
        return builder.sort(F.sort, false).sort(F.createdTime, true).sort(F.pkey, true).limit(offset, limit).exec();
    }

    public PageResult<MktGoods> queryAppSpecialGoods(Integer page, Integer pagesize, String date,
        List<Long> goodsPkeys, Integer topGoods, Boolean zoneRecommend, Integer ascription)
    {
        SelectPageBuilder<Integer, MktGoods> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .notEq("pkey", topGoods)
            .eq("zoneRecommend", zoneRecommend)
            .sort("sort", false)
            .sort("createdTime", true)
            .eq("mType", MType.SPECIAL_GOODS);
        SelectOneBuilder<Integer, MktGoods> topBuilder = selectOne().eq(F.ascription, ascription)
            .eq(F.idDel, false)
            .eq(F.pkey, topGoods)
            .eq(F.mType, MType.SPECIAL_GOODS);
        builder(builder);
        builder(topBuilder);
        LocalDate of = null;
        if (StringUtils.isBlank(date))
            of = LocalDate.now();
        else
        {
            of = LocalDate.of(Integer.valueOf(date.substring(0, 4)),
                Integer.valueOf(date.substring(5, 7)),
                Integer.valueOf(date.substring(8, 10)));
        }
        if (LocalDate.now().isBefore(of))
        {
            builder.ge("endDate", of.toString()).sort("startDate", true);
            topBuilder.ge(F.endDate, of.toString()).sort(F.startDate, true);
        }
        else
        {
            builder.le("startDate", of.toString())
                .ge("endDate", of.toString())
                .eq("enabled", true)
                .sort("startDate", true);
            topBuilder.le(F.startDate, of.toString())
                .ge(F.endDate, of.toString())
                .eq(F.enabled, true)
                .sort(F.startDate, true);
        }
        ConditionBuilder<SelectPageBuilder<Integer, MktGoods>> or = builder.or()
        .eq("visibleRange", MemberVisibleRange.ALL)
        .isNull("visibleRange");
        if(!goodsPkeys.isEmpty())
        {
            or = or
            .and()
                .eq("visibleRange", MemberVisibleRange.TAG)
                .in("pkey", goodsPkeys)
            .close();
        }
        builder = or.close().done();
        PageResult<MktGoods> res = builder.exec();
        if (page == 0 && topGoods != null)
        {
            MktGoods top = topBuilder.exec();
            if (top != null)
            {
                List<MktGoods> list = new ArrayList<>();
                list.add(top);
                list.addAll(res.getContent());
                res.setContent(list);
            }
        }
        return res;
    }
    
    public PageResult<MktGoods> queryAppGuessLikeGoods(Integer page, Integer pagesize, String marketPkey)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("enabled", true)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("guessLike", true)
            .sort("guessSort")
            .sort("xsNum")
            .sort("pkey")
            .exec();
    }
    
    public PageResult<MktGoods> queryAppMemberGoods(Integer page, Integer pagesize, String marketPkey)
    {
        PageResult<MktGoods> result = this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("enabled", true)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("mType", MType.MARKET_GOODS)
            .eq("extendCon", "member")
            .sort("sort")
            .sort("xsNum")
            .sort("createdTime")
            .sort("pkey")
            .exec();
        return result;
    }
    
    /**
     * 市场条件
     * @param builder 构建对象
     */
    private void builder(BaseSelectBuilder builder)
    {
        String farmerPkey = MobileSession.farmerPkey();
        if (StringUtils.isBlank(farmerPkey))
        {
            farmerPkey = Constant.Operation + MobileSession.appid();
        }
        builder.eq("farmer", farmerPkey);
    }
    
    @Autowired
    private MktGoodsSpaceDao spaceDao;
    
    public Map<String, Object> getGoodsAllinfo(Integer pkey)
    {
        MktGoods goods = selectOne().eq("pkey", pkey).eq("idDel", false).exec();
        Map<String, Object> map = new HashMap<>();
        if (goods != null)
        {
            List<MktGoodsSpace> spaces = spaceDao.select().eq("goods", pkey).exec();
            if (goods.getPhoto1().size() > 0)
                map.put("photo", goods.getPhoto1().get(0));
            else
                map.put("photo", "");
            map.put("price", spaces.get(0).getPrice());
            map.put("space", spaces.get(0).getPkey());
            map.put("name", goods.getTitle());
        }
        return map;
    }
    
    public PageResult<MktGoods> queryGoodsList(Integer page, Integer pagesize, MType mType, Boolean enabled,
        Integer status, Integer gtype, String title, String marketPkey, String companyPkey, Integer ascriptionPkey)
    {
        SelectPageBuilder<Integer, MktGoods> builder =
            selectPage().page(page).pagesize(pagesize).eq("idDel", false).sort("sort", true).sort("pkey", true);
        if(marketPkey != null)
            builder.eq("farmer", marketPkey);
        builder.eq("company", companyPkey);
        if (gtype != null) builder.eq("gtype", gtype);
        if (mType != null) builder.eq("mType", mType);
        if (StringUtils.isNotBlank(title))
        {
            log.info("title: {}", title);
            builder.like("title", title);
        }
        if (enabled != null) builder.eq("enabled", enabled);
        // 1:全部 2: 未开始 3: 进行中 4: 已结束
        Calendar calendar = Calendar.getInstance();//TODO
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        if (status.equals(2))
        {
            builder.ge("startDate", new Date());
        }
        if (status.equals(3))
        {
            builder.lt("startDate", new Date());
            builder.gt("endDate", date);
        }
        if (status.equals(4))
        {
            builder.lt("endDate", date);
        }
        return builder.exec();
    }
    
    public List<MktGoodsDetailsDTO> listGoods(MType mType, Boolean enabled,
        Integer status, Integer gtype, Integer goodsMain, Integer threeGtype, String title, String marketPkey, String companyPkey, 
        Integer ascription, List<Integer> vendor)
    {
        SelectBuilder<Integer, MktGoods> builder =
            select()
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .in("vendor", vendor)
            .eq("company", companyPkey)
            .eq("ascription", ascription)
            .eq("gtype", gtype)
            .eq("goodsMain", goodsMain)
            .eq("threeGtype", threeGtype)
            .eq("mType", mType)
            .like("title", title)
            .eq("enabled", enabled);
        // 1:全部 2: 未开始 3: 进行中 4: 已结束
        Calendar calendar = Calendar.getInstance(); 
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        if (status.equals(2))
        {
            builder.ge("startDate", new Date());
        }
        if (status.equals(3))
        {
            builder.lt("startDate", new Date());
            builder.gt("endDate", date);
        }
        if (status.equals(4))
        {
            builder.lt("endDate", date);
        }
        return builder.execDto(MktGoodsDetailsDTO.class);
    }
    
    
    public PageResult<MktGoodsDetailsDTO> queryGoodsV3(Integer page, Integer pagesize, MType mType, Boolean enabled,
        Integer status, Integer gtype, Integer goodsMain, Integer threeGtype, SortType sortType,
        Boolean sort, String title, String marketPkey, String companyPkey, List<Integer> vendor, Integer supplier)
    {
        SelectPageBuilder<Integer, MktGoods> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .in("vendor", vendor)
            .eq("company", companyPkey)
            .eq(F.supplier, supplier)
            .eq("gtype", gtype)
            .eq("goodsMain", goodsMain)
            .eq("threeGtype", threeGtype)
            .eq("mType", mType)
            .like("title", title)
            .eq("enabled", enabled);
        
        if(sortType != null)
        {
            if(SortType.PAGEVIEWS_SORT.equals(sortType))
                builder.sort("viewCount", sort);
            if(SortType.SALES_SORT.equals(sortType))
                builder.sort("xsNum", sort);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        if (status.equals(2))
        {
            builder.ge("startDate", new Date());
        }
        if (status.equals(3))
        {
            builder.lt("startDate", new Date());
            builder.gt("endDate", date);
        }
        if (status.equals(4))
        {
            builder.lt("endDate", date);
        }
        return builder.sort("sort", true).sort("pkey", true).execDto(MktGoodsDetailsDTO.class);
    }
    
    
    public PageResult<MktVendorGoodsOnInfo> queryAppVendorGoods(Integer page, Integer pagesize,  Boolean enabled,
        Integer status, Integer gtype, String title, String marketPkey, String companyPkey, Integer ascription)
    {
        SelectPageBuilder<Integer, MktGoods> builder =
            selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("company", companyPkey)
            .eq("gtype", gtype)
            .eq("mType", MType.MARKET_GOODS)
            .like("title", title)
            .eq("enabled", enabled)
            .eq("ascription", ascription);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        if (status.equals(2))
        {
            builder.ge("startDate", new Date());
        }
        if (status.equals(3))
        {
            builder.lt("startDate", new Date());
            builder.gt("endDate", date);
        }
        if (status.equals(4))
        {
            builder.lt("endDate", date);
        }
        return builder.sort("sort", true).sort("pkey", true).execDto(MktVendorGoodsOnInfo.class);
    }
    
    public MktGoods getGoods(Integer pkey)
    {
        return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
    }
    
    public List<List<Object>> getGoodsKc(String marketPkey, String companyPkey, Integer mType, Integer ascription)
    {
        return repository.getGoodsKc(marketPkey, companyPkey, mType, ascription);
    }
    
    public List<List<Object>> getGoodsPool(String marketPkey, BigDecimal pool, int page, int pagesize)
    {
        return repository.getGoodsPool(marketPkey, pool, page * pagesize, pagesize);
    }
    
    public Map<Integer, MktGoods> getGoodsMap(List<Integer> gkeys)
    {
        Map<Integer, MktGoods> res = new HashMap<>();
        if (gkeys.isEmpty()) return res;
        List<MktGoods> exec = this.select().in("pkey", gkeys.toArray()).exec();
        exec.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public Map<String, MktGoods> getMarketGoodsAllMap()
    {
        Map<String, MktGoods> res = new HashMap<>();
        List<MktGoods> exec = this.select().eq("mType", MType.MARKET_GOODS).exec();
        exec.forEach(e -> res.put(e.getTitle(), e));
        return res;
    }
    
    public Boolean checkTitleRepeat(String title, MType mType, String marketPkey, Integer pkey)
    {
        long count = this.aggregation()
            .notEq("pkey", pkey)
            .eq("mType", mType)
            .eq("farmer", marketPkey)
            .eq("title", title)
            .eq("idDel", false)
            .execCount();
        return count > 0;
    }
    
    public <T> PageResult<T> queryCoupon(Integer page, Integer pagesize, String title, Integer goodsMain,
        Boolean enabled, Class<T> calzz, Integer ascription)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .like("title", title)
            .eq("ascription", ascription)
            .eq("mType", MType.COUPON_GOODS)
            .eq("goodsMain", goodsMain)
            .eq("enabled", enabled)
            .eq("idDel", false)
            .execDto(calzz);
    }
    
    public PageResult<GoodsCouponOnPage> queryCouponStartDateV3(Integer page, Integer pagesize, 
        Boolean sort, String title,Boolean enabled, Integer ascription)
    {
        // 上架时间
        return this.selectPage()
        .page(page)
        .pagesize(pagesize)
        .eq("ascription", ascription)
        .like("title", title)
        .eq("mType", MType.COUPON_GOODS)
        .eq("enabled", enabled)
        .eq("idDel", false)
        .sort("startDate", sort).execDto(GoodsCouponOnPage.class);
    }
    
    public List<PkeyNameDTO> dropMarketGoodsV2(String farmer, Integer gtype, Integer ascription, List<Integer> mtype)
    {
        List<PkeyNameDTO> res = new ArrayList<>();
        List<MType> mt = new ArrayList<>();
        if(mtype != null && !mtype.isEmpty())
        {
            for(int i = 0; i< mtype.size(); i++)
            {
                MType t = MType.fromIndex(mtype.get(i));
                mt.add(t);
            }
        }
        List<MktGoods> list = this.select()
            .eq("ascription", ascription)
            .eq("farmer", farmer)
            .eq("gtype", gtype)
            .in("mType", mt)
            .eq("idDel", false)
            .sort("sort", false)
            .exec();
        list.forEach(e -> {
            PkeyNameDTO dto = new PkeyNameDTO();
            dto.setPkey(e.getPkey());
            dto.setName(e.getTitle());
            res.add(dto);
        });
        return res;
    }
    
    public void updCoupon(Map<Integer,String> updGoods)
    {
        List<MktGoods> list = this.select().in("pkey", updGoods.keySet()).exec();
        for(MktGoods g : list)
        {
            if(updGoods.containsKey(g.getPkey()))
            {
                g.setExtendCon(updGoods.get(g.getPkey()));
            }
        }
        this.updateAll(list);
    }
    
    public Boolean checkTitleVendorRepeat(String title, MType mType, String marketPkey, Integer pkey, Integer vendor)
    {
        long count = this.aggregation()
            .notEq("pkey", pkey)
            .eq("vendor", vendor)
            .eq("mType", mType)
            .eq("farmer", marketPkey)
            .eq("title", title)
            .eq("idDel", false)
            .execCount();
        return count > 0;
    }
    
    public Integer getGoodsVendorXsNum(Integer vendor)
    {
        Number execSum = this.aggregation().eq("vendor", vendor)
        .execSum("xsNum");
        if (execSum == null) return 0;
        return execSum.intValue();
    }
    
    public MktGoods byH5Goods(String title, String farmer)
    {
        return this.selectOne()
        .eq("idDel", false)
        .eq("title", title)
        .eq("farmer", farmer)
        .eq("mType", MType.BOX_GOODS)
        .exec();
        
    }
    
    public void clearGoodsSupplier(Integer ascription, Integer supplier)
    {
        Map<String, Object> values = new HashMap<>();
        values.put(F.supplier, null);
        values.put(F.enabled, false);
        this.select().strict(true).eq(F.ascription, ascription).eq(F.supplier, supplier).update(values);
    }
    
    public int countGoodsZoneRecommend(MType mType, String farmer, Integer ascription)
    {
        Number count = this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.mType, mType)
            .eq(F.zoneRecommend, true)
            .execCount();
        return count.intValue();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updGoodsZoneRecommend(Integer pkey, Boolean recommend)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.zoneRecommend, recommend);
    }
    
    public Map<String, Long> countEnabledGroupByGoodsMain(Integer ascription, String farmer, List<MType> mTypes)
    {
        return this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .in(F.mType, mTypes)
            .execGroupByCount(F.goodsMain, F.pkey);
    }
    
    public List<MktGoods> listVendor(Integer vendor, Integer ascription)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .eq(F.vendor, vendor)
            .isNotNull(F.vendor)
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .exec();
    }
}
