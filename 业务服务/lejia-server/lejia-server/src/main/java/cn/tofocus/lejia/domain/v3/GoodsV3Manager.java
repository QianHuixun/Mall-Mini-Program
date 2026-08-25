package cn.tofocus.lejia.domain.v3;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.lejia.dao.goods.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponExportExcel;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.bean.dto.goods.GoodsMarketExportExcel;
import cn.tofocus.lejia.bean.dto.goods.GoodsMarketExportV2LinshiExcel;
import cn.tofocus.lejia.bean.dto.goods.GoodsMarketVendorExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsCollageExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsCutExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsGiftExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsIntegralBnypExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsIntegralExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsIntegralMsdExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsIntegralPresaleExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsOtherExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsOtherVendorExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsPresaleExportExcel;
import cn.tofocus.lejia.bean.dto.goods.MktGoodsShareExportExcel;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsPresale;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.bean.enums.v3.SortType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class GoodsV3Manager
{
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;

    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private MktVendorDao vendorDao;

    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private MktGoodsRecommendDao goodsRecommendDao;

    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private TagManager tagManager;
    
    public PageResult<MktGoodsDetailsDTO> queryGoods(int page, int pagesize, MType mType, SortType sortType,
        Boolean sort, String title, Integer gtype, Integer goodsMain, Integer threeGtype, Boolean enabled, 
        Integer status, Integer vendor, String booth, Integer supplier)
    {
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<MktGoodsDetailsDTO> res = null;
        List<Integer> vkeys = new ArrayList<>();
        if(StringUtils.isNotBlank(booth))
        {
            List<Integer> byNameAndBooth = vendorDao.byNameAndBooth(null, booth, marketPkey, ascription);
            if(byNameAndBooth == null || byNameAndBooth.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            vkeys.addAll(byNameAndBooth);
        }
        if(vendor != null)
            vkeys.add(vendor);
        if (sortType == null || sortType.getIndex() < 2)
        {
            res = goodsDao.queryGoodsV3(page,
                pagesize,
                mType,
                enabled,
                status,
                gtype,
                goodsMain,
                threeGtype,
                sortType,
                sort,
                title,
                marketPkey,
                companyPkey,
                vkeys,
                supplier);
            // 组装商品名称
            assembleName(res.getContent(), ascription);
        }
        else
        {
            res = queryGoodsSpace(page, pagesize, mType, sortType, sort, title, gtype, goodsMain, threeGtype, enabled, status, vkeys);
        }
        for (MktGoodsDetailsDTO line : res)
        {
            long recommendNum = goodsRecommendDao.countBySourceGoods(ascription, line.getPkey());
            line.setRecommendNum(recommendNum);
        }
        return res;
    }
    
    private void assembleName(List<MktGoodsDetailsDTO> list, Integer ascription)
    {
        List<Integer> keys = new ArrayList<>();
        List<Integer> userVendorKeys = new ArrayList<>();
        for (MktGoodsDetailsDTO bean : list)
        {
            if (bean.getMType().getIndex() == 5)
            {
                @SuppressWarnings("unchecked")
                List<String> extendConList = JsonUtil.getBean(bean.getExtendCon(), List.class);
                if (extendConList == null) extendConList = new ArrayList<>();
                bean.setExtendConList(extendConList);
            }
            if (bean.getMType().equals(MType.GIFT_GOODS))
            {
                keys.add(bean.getPkey());
                if (bean.getUserVendor() != null) userVendorKeys.add(bean.getUserVendor());
            }
            if (bean.getMType().equals(MType.PRESALE_GOODS) || bean.getMType().equals(MType.INTEGRAL_PRESALE_GOODS)
                || bean.getMType().equals(MType.INTEGRAL_MSD_GOODS))
            {
                MktGoodsPresale presale = goodsPresaleDao.get(bean.getPkey());
                if (presale != null)
                {
                    bean.setPresaleStartDate(presale.getStartDate());
                    bean.setPresaleEndDate(presale.getEndDate());
                }
            }
            MktGtype mktGtype = gtypeDao.get(bean.getGtype());
            if(mktGtype != null)
            {
                bean.setGtypeName(mktGtype.getName());
            }
            // 通过mkt_goods_main设置商品名称
            if(bean.getGoodsMain() != null)
            {
                bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
            }
            else
            {
                bean.setName("");
            }
            if(bean.getThreeGtype() != null)
                bean.setThreeGtypeName(goodsMainThreeDao.get(bean.getThreeGtype()).getName());
            else
                bean.setThreeGtypeName("");
            List<MktGoodsSpaceOnList> spaceList =
                goodsSpaceDao.select().eq("goods", bean.getPkey()).execDto(MktGoodsSpaceOnList.class);
            for (MktGoodsSpaceOnList space : spaceList)
            {
                space.setStatus(0);
            }
            bean.setSpaces(spaceList);
            if(bean.getVendor() != null)
            {
                MktVendor mktVendor = vendorDao.get(bean.getVendor());
                if(mktVendor != null)
                {
                    bean.setVendorName(mktVendor.getDisplayName());
                    bean.setBooth(mktVendor.getBooth());
                }
            }
            if (bean.getSupplier() != null)
            {
                MktSupplier mktSupplier = supplierDao.get(bean.getSupplier());
                if (mktSupplier != null)
                {
                    bean.setSupplierName(mktSupplier.getName());
                }
            }
            if (bean.getMType().equals(MType.SPECIAL_GOODS))
            {
                List<Integer> tagKeys = new ArrayList<>();
                tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.SPECIAL_GOODS, bean.getPkey().longValue());
                bean.setTagKeys(tagKeys);
            }
            if (bean.getMType().equals(MType.INTEGRAL_MSD_GOODS))
            {
                List<Integer> tagKeys = new ArrayList<>();
                tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.INTEGRAL_MSD_GOODS, bean.getPkey().longValue());
                bean.setMsdTags(tagKeys);
            }
            
        }
        if (!keys.isEmpty())
        {
            Map<Integer, MktGoodsGift> map = goodsGiftDao.getGoodsMap(keys);
            Map<String, String> nameMap = farmerDao.findNameMap(ascription);
            for (MktGoodsDetailsDTO bean : list)
            {
                if (map.containsKey(bean.getPkey()))
                {
                    MktGoodsGift gift = map.get(bean.getPkey());
                    String userFarmer = gift.getUserFarmer();
                    bean.setExpireChoose(gift.getExpireChoose() != CouponExpireChoose.LONG_TERM);
                    bean.setUserFarmer(userFarmer);
                    bean.setUserVendor(gift.getUserVendor());
                    bean.setGiftStartDate(gift.getStartDate());
                    bean.setGiftEndDate(gift.getEndDate());
                    if (nameMap.containsKey(userFarmer)) bean.setUserFarmerName(nameMap.get(userFarmer));
                    userVendorKeys.add(gift.getUserVendor());
                }
            }
            if (!userVendorKeys.isEmpty())
            {
                Map<Integer, MktVendor> mapVendor = vendorDao.getMapVendor(userVendorKeys);
                for (MktGoodsDetailsDTO bean : list)
                {
                    Integer userVendor = bean.getUserVendor();
                    if (mapVendor.containsKey(userVendor))
                    {
                        bean.setUserVendorName(mapVendor.get(userVendor).getName());
                    }
                }
            }
        }
    }
    
    private PageResult<MktGoodsDetailsDTO> queryGoodsSpace(int page, int pagesize, MType mType, SortType sortType,
        Boolean sort, String title, Integer gtype, Integer goodsMain, Integer threeGtype, Boolean enabled, Integer status, List<Integer> vendor)
    {
        List<MktGoodsDetailsDTO> content = new ArrayList<>();
        PageResult<MktGoodsDetailsDTO> res = PageUtil.page(content, PageParameter.of(page, pagesize));
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGoodsDetailsDTO> list =
            goodsDao.listGoods(mType, enabled, status, gtype, goodsMain, threeGtype, title, marketPkey, companyPkey, ascription, vendor);
        List<Integer> keys = list.stream().map(MktGoodsDetailsDTO::getPkey).collect(Collectors.toList());
        if (keys.isEmpty()) return res;
        Map<Integer, MktGoodsDetailsDTO> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        if (MType.GIFT_GOODS.equals(mType) && sortType != null 
            && SortType.EXCHANGE_VALIDITY_SORT.equals(sortType))
        {
            List<MktGoodsGift> giftList = null;
            if (Boolean.TRUE.equals(sort))
                giftList = goodsGiftDao.listGoodsGiftV3True(keys, page, pagesize);
            else
                giftList = goodsGiftDao.listGoodsGiftV3False(keys, page, pagesize);
            for (MktGoodsGift g : giftList)
            {
                MktGoodsDetailsDTO dto = new MktGoodsDetailsDTO();
                if (map.containsKey(g.getPkey()))
                {
                    BeanUtils.copyProperties(map.get(g.getPkey()), dto);
                    if (g.getUserVendor() != null)
                    {
                        dto.setUserVendor(g.getUserVendor());
                        MktVendor mktVendor = vendorDao.get(g.getUserVendor());
                        dto.setUserVendorName(mktVendor.getName());
                        dto.setBooth(mktVendor.getBooth());
                    }
                    dto.setGtypeName(gtypeDao.get(dto.getGtype()).getName());
                    // 通过mkt_goods_main设置商品名称
                    dto.setName(goodsMainDao.get(dto.getGoodsMain()).getName());
                    MktGoodsSpaceOnList s =
                        goodsSpaceDao.selectOne().eq("goods", dto.getPkey()).execDto(MktGoodsSpaceOnList.class);
                    s.setStatus(0);
                    dto.setSpaces(Arrays.asList(s));
                    
                    dto.setExpireChoose(g.getExpireChoose() != CouponExpireChoose.LONG_TERM);
                    dto.setUserFarmer(g.getUserFarmer());
                    if (StringUtils.isNotBlank(g.getUserFarmer()))
                    {
                        dto.setUserFarmerName(farmerDao.get(g.getUserFarmer()).getName());
                    }
                    dto.setGiftStartDate(g.getStartDate());
                    dto.setGiftEndDate(g.getEndDate());
                    content.add(dto);
                }
            }
            res = PageUtil.page(content, PageParameter.of(page, pagesize));
        }
        else
        {
            PageResult<MktGoodsSpaceOnList> pageResult =
                goodsSpaceDao.listSpaceV3(page, pagesize, sortType, sort, keys);
            res = BeanUtil.beanPageFrom(MktGoodsDetailsDTO.class, pageResult);
            for (MktGoodsSpaceOnList s : pageResult.getContent())
            {
                MktGoodsDetailsDTO dto = new MktGoodsDetailsDTO();
                if (map.containsKey(s.getGoods()))
                {
                    BeanUtils.copyProperties(map.get(s.getGoods()), dto, "spaces");
                    if (MType.CUT_GOODS.equals(dto.getMType()))
                    {
                        @SuppressWarnings("unchecked")
                        List<String> extendConList = JsonUtil.getBean(dto.getExtendCon(), List.class);
                        if (extendConList == null) extendConList = new ArrayList<>();
                        dto.setExtendConList(extendConList);
                    }
                    if(MType.GIFT_GOODS.equals(mType))
                    {
                        MktGoodsGift gift = goodsGiftDao.getByGoods(s.getGoods());
                        if(gift != null)
                        {
                            if (gift.getUserVendor() != null)
                            {
                                dto.setUserVendor(gift.getUserVendor());
                                dto.setUserVendorName(vendorDao.get(gift.getUserVendor()).getName());
                            }
                            if (StringUtils.isNotBlank(gift.getUserFarmer()))
                            {
                                dto.setUserFarmerName(farmerDao.get(gift.getUserFarmer()).getName());
                            }
                            dto.setGiftStartDate(gift.getStartDate());
                            dto.setGiftEndDate(gift.getEndDate());
                        }
                    }
                    if (dto.getMType().equals(MType.PRESALE_GOODS) || dto.getMType().equals(MType.INTEGRAL_PRESALE_GOODS))
                    {
                        MktGoodsPresale presale = goodsPresaleDao.get(dto.getPkey());
                        if(presale != null)
                        {
                            dto.setPresaleStartDate(presale.getStartDate());
                            dto.setPresaleEndDate(presale.getEndDate());
                        }
                    }
                    dto.setGtypeName(gtypeDao.get(dto.getGtype()).getName());
                    // 通过mkt_goods_main设置商品名称
                    dto.setName(goodsMainDao.get(dto.getGoodsMain()).getName());
                    s.setStatus(0);
                    dto.setSpaces(Arrays.asList(s));
                    content.add(dto);
                }
            }
            res.setContent(content);
        }
        return res;
    }
    
    public PageResult<GoodsCouponOnPage> queryCouponGoods(Integer page, Integer pagesize, SortType sortType,
        Boolean sort, String title, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        // 上架时间、金额、积分、库存、面值金额
        PageResult<GoodsCouponOnPage> pageResult = PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        if (sortType != null)
        {
            switch (sortType)
            {
                case ADDED_TIME_SORT:
                    pageResult = goodsDao.queryCouponStartDateV3(page, pagesize, sort, title, enabled, ascription);
                    assembleOldCoupon(pageResult);
                    break;
                case CURRENT_PRICE_SORT:
                case INTEGRAL_SORT:
                case STOCK_SORT:
                    pageResult = querySpaceCoupon(pageResult, page, pagesize, sortType, sort, title, enabled);
                    break;
                case FACE_VALUE_SORT:
                    pageResult = queryCardCounpon(pageResult, page, pagesize, sortType, sort, title, enabled);
                    break;
                default:
                    break;
            }
        }
        else
        {
            pageResult = goodsDao.queryCoupon(page, pagesize, title, null, enabled, GoodsCouponOnPage.class, ascription);
            assembleOldCoupon(pageResult);
        }
        
        return pageResult;
    }
    
    private PageResult<GoodsCouponOnPage> queryCardCounpon(PageResult<GoodsCouponOnPage> pageResult, Integer page,
        Integer pagesize, SortType sortType, Boolean sort, String title, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGoodsDetailsDTO> list = goodsDao.listGoods(MType.COUPON_GOODS, enabled, 0, null, null, null, title, null, null, ascription, null);
        List<String> keys = list.stream().map(MktGoodsDetailsDTO::getExtendCon).collect(Collectors.toList());
        if (keys.isEmpty()) return pageResult;
        PageResult<MktCard> cardPage = cardDao.queryGoods(keys, page, pagesize, sort);
        List<GoodsCouponOnPage> content = new ArrayList<>();
        Map<String, String> farmerMap = sysFarmerDao.findNameMap(CurrentSession.ascriptionPkey());
        for (MktCard c : cardPage.getContent())
        {
            MktGoods goods = goodsDao.selectOne().eq("ascription", ascription).eq("mType", MType.COUPON_GOODS).eq("extendCon", c.getPkey()).exec();
            if (goods == null) continue;
            GoodsCouponOnPage dto = BeanUtil.beanFrom(GoodsCouponOnPage.class, goods);
            BeanUtils.copyProperties(c, dto, "startDate", "endDate", "title", "pkey", "enabled");
            dto.setCardStartDate(c.getStartDate());
            dto.setCardEndDate(c.getEndDate());
            if (dto.getUserGoods() != null)
            {
                MktGoods userGoods = goodsDao.get(dto.getUserGoods());
                if (userGoods != null) dto.setUserGoodsName(userGoods.getTitle());
            }
            if (dto.getUserType() != null)
            {
                MktGtype gtype = gtypeDao.get(dto.getUserType());
                if (gtype != null) dto.setUserTypeName(gtype.getName());
            }
            if (StringUtils.isNotBlank(dto.getUserFarmer()) && farmerMap.containsKey(dto.getUserFarmer()))
                dto.setUserFarmerName(farmerMap.get(dto.getUserFarmer()));
            MktGoodsSpace s = goodsSpaceDao.selectOne().eq("ascription", ascription).eq("goods", goods.getPkey()).exec();
            if (s != null)
            {
                dto.setPrice(s.getPrice());
                dto.setPoint(s.getPoint());
                dto.setKcNum(s.getKcNum());
                dto.setSpace(s.getSpace());
            }
            content.add(dto);
        }
        pageResult = BeanUtil.beanPageFrom(GoodsCouponOnPage.class, cardPage);
        pageResult.setContent(content);
        return pageResult;
    }
    
    private PageResult<GoodsCouponOnPage> querySpaceCoupon(PageResult<GoodsCouponOnPage> pageResult, Integer page,
        Integer pagesize, SortType sortType, Boolean sort, String title, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGoodsDetailsDTO> list = goodsDao.listGoods(MType.COUPON_GOODS, enabled, 0, null, null, null, title, null, null, ascription, null);
        List<Integer> keys = list.stream().map(MktGoodsDetailsDTO::getPkey).collect(Collectors.toList());
        if (keys.isEmpty()) return pageResult;
        PageResult<MktGoodsSpaceOnList> spacePageResult =
            goodsSpaceDao.listSpaceV3(page, pagesize, sortType, sort, keys);
        Map<Integer, MktGoodsDetailsDTO> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        List<GoodsCouponOnPage> content = new ArrayList<>();
        Map<String, String> farmerMap = sysFarmerDao.findNameMap(CurrentSession.ascriptionPkey());
        for (MktGoodsSpaceOnList s : spacePageResult.getContent())
        {
            if (map.containsKey(s.getGoods()))
            {
                GoodsCouponOnPage dto = BeanUtil.beanFrom(GoodsCouponOnPage.class, map.get(s.getGoods()));
                String extendCon = dto.getExtendCon();
                if (StringUtils.isNotBlank(extendCon))
                {
                    MktCard mktCard = cardDao.get(Integer.valueOf(extendCon));
                    if (mktCard != null)
                    {
                        BeanUtils.copyProperties(mktCard, dto, "startDate", "endDate", "title", "pkey", "enabled");
                        dto.setCardStartDate(mktCard.getStartDate());
                        dto.setCardEndDate(mktCard.getEndDate());
                    }
                }
                if (dto.getUserGoods() != null)
                {
                    MktGoods userGoods = goodsDao.get(dto.getUserGoods());
                    if (userGoods != null) dto.setUserGoodsName(userGoods.getTitle());
                }
                if (dto.getUserType() != null)
                {
                    MktGtype gtype = gtypeDao.get(dto.getUserType());
                    if (gtype != null) dto.setUserTypeName(gtype.getName());
                }
                if (StringUtils.isNotBlank(dto.getUserFarmer()) && farmerMap.containsKey(dto.getUserFarmer()))
                    dto.setUserFarmerName(farmerMap.get(dto.getUserFarmer()));
                dto.setPrice(s.getPrice());
                dto.setPoint(s.getPoint());
                dto.setKcNum(s.getKcNum());
                dto.setSpace(s.getSpace());
                content.add(dto);
            }
        }
        pageResult = BeanUtil.beanPageFrom(GoodsCouponOnPage.class, spacePageResult);
        pageResult.setContent(content);
        return pageResult;
    }
    
    private void assembleOldCoupon(PageResult<GoodsCouponOnPage> pageResult)
    {
        List<Integer> keys = new ArrayList<>();
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> gtypeKeys = new ArrayList<>();
        List<Integer> cardKeys = new ArrayList<>();
        
        pageResult.getContent().forEach(e -> {
            keys.add(e.getPkey());
            if (StringUtils.isNotBlank(e.getExtendCon())) cardKeys.add(Integer.valueOf(e.getExtendCon()));
        });
        Map<Integer, MktGoodsSpace> goodsSpaceMap = goodsSpaceDao.getGoodsSpaceMap(keys);
        Map<Integer, MktCard> cardMap = cardDao.mapCard(cardKeys);
        for (Entry<Integer, MktCard> entry : cardMap.entrySet())
        {
            MktCard card = entry.getValue();
            if (card.getUserGoods() != null) gkeys.add(card.getUserGoods());
            if (card.getUserType() != null) gtypeKeys.add(card.getUserType());
        }
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGtype> gtypeMap = gtypeDao.mapGtype(gtypeKeys);
        Map<String, String> farmerMap = sysFarmerDao.findNameMap(CurrentSession.ascriptionPkey());
        for (GoodsCouponOnPage dto : pageResult.getContent())
        {
            String extendCon = dto.getExtendCon();
            if (StringUtils.isNotBlank(extendCon) && cardMap.containsKey(Integer.valueOf(extendCon)))
            {
                MktCard mktCard = cardMap.get(Integer.valueOf(extendCon));
                BeanUtils.copyProperties(mktCard, dto, "startDate", "endDate", "title", "pkey", "enabled");
                dto.setCardStartDate(mktCard.getStartDate());
                dto.setCardEndDate(mktCard.getEndDate());
            }
            if (dto.getUserGoods() != null && goodsMap.containsKey(dto.getUserGoods()))
                dto.setUserGoodsName(goodsMap.get(dto.getUserGoods()).getTitle());
            if (dto.getUserType() != null && gtypeMap.containsKey(dto.getUserType()))
                dto.setUserTypeName(gtypeMap.get(dto.getUserType()).getName());
            if (StringUtils.isNotBlank(dto.getUserFarmer()) && farmerMap.containsKey(dto.getUserFarmer()))
                dto.setUserFarmerName(farmerMap.get(dto.getUserFarmer()));
            if (goodsSpaceMap.containsKey(dto.getPkey()))
            {
                MktGoodsSpace space = goodsSpaceMap.get(dto.getPkey());
                dto.setPrice(space.getPrice());
                dto.setPoint(space.getPoint());
                dto.setKcNum(space.getKcNum());
                dto.setSpace(space.getSpace());
            }
        }
    }
    
    public void exportGoods(MType mType, SortType sortType, Boolean sort, String title, Integer gtype,
        Integer goodsMain, Integer threeGtype, Boolean enabled, Integer status, Integer vendor, String booth,
        Integer supplier, String marketPkey, OutputStream out)
    {
        try
        {
            Class<?> model = GoodsMarketExportExcel.class;
            List<?> list = new ArrayList<>();
            SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
            Boolean typeFlag = false;
            if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType())) typeFlag = true;
            if (mType.equals(MType.COUPON_GOODS))
            {
                model = GoodsCouponExportExcel.class;
                PageResult<GoodsCouponOnPage> result = queryCouponGoods(0, 100000, sortType, sort, title, enabled);
                list = assemblyCouponGoods4Export(result.getContent());
            }
            else
            {
                PageResult<MktGoodsDetailsDTO> result = queryGoods(0,
                    100000,
                    mType,
                    sortType,
                    sort,
                    title,
                    gtype,
                    goodsMain,
                    threeGtype,
                    enabled,
                    status,
                    vendor,
                    booth,
                    supplier);
                list = assemblyGoods4Export(result.getContent(), mType);
            }
            if (typeFlag) model = GoodsMarketVendorExportExcel.class;
            if (mType.equals(MType.SPECIAL_GOODS) || mType.equals(MType.POVERTY_ALLEVIATION_GOODS))
            {
                model = MktGoodsOtherExportExcel.class;
                if (typeFlag) model = MktGoodsOtherVendorExportExcel.class;
            }
            if (mType.equals(MType.PRESALE_GOODS))
            {
                model = MktGoodsPresaleExportExcel.class;
            }
            if (mType.equals(MType.SHARE_GOODS))
            {
                model = MktGoodsShareExportExcel.class;
            }
            if (mType.equals(MType.CUT_GOODS))
            {
                model = MktGoodsCutExportExcel.class;
            }
            if (mType.equals(MType.COLLAGE_GOODS))
            {
                model = MktGoodsCollageExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_GOODS))
            {
                model = MktGoodsIntegralExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_PRESALE_GOODS))
            {
                model = MktGoodsIntegralPresaleExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_BNYP_GOODS))
            {
                model = MktGoodsIntegralBnypExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_MSD_GOODS))
            {
                model = MktGoodsIntegralMsdExportExcel.class;
            }
            if (mType.equals(MType.GIFT_GOODS))
            {
                model = MktGoodsGiftExportExcel.class;
            }
//            List<?> beanListFrom = BeanUtil.beanListFrom(model, list);
            excelHelper.exportExcel(BeanUtil.beanListFrom(model, list), "Sheet1", out, model, null);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void exportGoodsV2(MType mType, SortType sortType, Boolean sort, String title, Integer gtype,
        Integer goodsMain, Integer threeGtype, Boolean enabled, Integer status, Integer vendor, String booth,
        Integer supplier, String marketPkey, OutputStream out)
    {
        try
        {
            Class<?> model = GoodsMarketExportV2LinshiExcel.class;
            List<?> list = new ArrayList<>();
            SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
            Boolean typeFlag = false;
            if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType())) typeFlag = true;
            if (mType.equals(MType.COUPON_GOODS))
            {
                model = GoodsCouponExportExcel.class;
                PageResult<GoodsCouponOnPage> result = queryCouponGoods(0, 100000, sortType, sort, title, enabled);
                list = assemblyCouponGoods4Export(result.getContent());
            }
            else
            {
                PageResult<MktGoodsDetailsDTO> result = queryGoods(0,
                    100000,
                    mType,
                    sortType,
                    sort,
                    title,
                    gtype,
                    goodsMain,
                    threeGtype,
                    enabled,
                    status,
                    vendor,
                    booth,
                    supplier);
                list = assemblyGoods4Export(result.getContent(), mType);
            }
//            if (typeFlag) model = GoodsMarketVendorExportExcel.class;
            if (mType.equals(MType.SPECIAL_GOODS) || mType.equals(MType.POVERTY_ALLEVIATION_GOODS))
            {
                model = MktGoodsOtherExportExcel.class;
                if (typeFlag) model = MktGoodsOtherVendorExportExcel.class;
            }
            if (mType.equals(MType.PRESALE_GOODS))
            {
                model = MktGoodsPresaleExportExcel.class;
            }
            if (mType.equals(MType.SHARE_GOODS))
            {
                model = MktGoodsShareExportExcel.class;
            }
            if (mType.equals(MType.CUT_GOODS))
            {
                model = MktGoodsCutExportExcel.class;
            }
            if (mType.equals(MType.COLLAGE_GOODS))
            {
                model = MktGoodsCollageExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_GOODS))
            {
                model = MktGoodsIntegralExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_PRESALE_GOODS))
            {
                model = MktGoodsIntegralPresaleExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_BNYP_GOODS))
            {
                model = MktGoodsIntegralBnypExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_MSD_GOODS))
            {
                model = MktGoodsIntegralMsdExportExcel.class;
            }
            if (mType.equals(MType.GIFT_GOODS))
            {
                model = MktGoodsGiftExportExcel.class;
            }
            excelHelper.exportExcel(BeanUtil.beanListFrom(model, list), "Sheet1", out, model, null);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private List<GoodsCouponExportExcel> assemblyCouponGoods4Export(List<GoodsCouponOnPage> list)
    {
        List<GoodsCouponExportExcel> res = new ArrayList<>();
        Map<Integer, MktGoodsMain> map = goodsMainDao.getAllMap(CurrentSession.ascriptionPkey());
        for (GoodsCouponOnPage c : list)
        {
            GoodsCouponExportExcel dto = BeanUtil.beanFrom(GoodsCouponExportExcel.class, c);
            if (map.containsKey(c.getGoodsMain())) dto.setGooodsMainName(map.get(c.getGoodsMain()).getName());
            res.add(dto);
        }
        return res;
    }
    
    // 组合导出的list
    private List<?> assemblyGoods4Export(List<MktGoodsDetailsDTO> content, MType mType)
    {
        if (mType.equals(MType.COLLAGE_GOODS))
        {
            List<MktGoodsCollageExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsCollageExportExcel e = BeanUtil.beanFrom(MktGoodsCollageExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    String extendCon = dto.getExtendCon();
                    e.setCollageNum(StringUtils.isBlank(extendCon) ? 0 : Integer.valueOf(extendCon));
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.CUT_GOODS))
        {
            List<MktGoodsCutExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsCutExportExcel e = BeanUtil.beanFrom(MktGoodsCutExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    if (dto.getExtendConList() != null && !dto.getExtendConList().isEmpty())
                    {
                        for (int i = 0; i < dto.getExtendConList().size(); i++)
                        {
                            String str = dto.getExtendConList().get(i);
                            int j = i + 1;
                            String[] split = str.split(",");
                            switch (j)
                            {
                                case 1:
                                    e.setCutLow1(split[0]);
                                    e.setCutUpon1(split[1]);
                                    break;
                                case 2:
                                    e.setCutLow2(split[0]);
                                    e.setCutUpon2(split[1]);
                                    break;
                                case 3:
                                    e.setCutLow3(split[0]);
                                    e.setCutUpon3(split[1]);
                                    break;
                                case 4:
                                    e.setCutLow4(split[0]);
                                    e.setCutUpon4(split[1]);
                                    break;
                                case 5:
                                    e.setCutLow5(split[0]);
                                    e.setCutUpon5(split[1]);
                                    break;
                                case 6:
                                    e.setCutLow6(split[0]);
                                    e.setCutUpon6(split[1]);
                                    break;
                                case 7:
                                    e.setCutLow7(split[0]);
                                    e.setCutUpon7(split[1]);
                                    break;
                                case 8:
                                    e.setCutLow8(split[0]);
                                    e.setCutUpon8(split[1]);
                                    break;
                                case 9:
                                    e.setCutLow9(split[0]);
                                    e.setCutUpon9(split[1]);
                                    break;
                                case 10:
                                    e.setCutLow10(split[0]);
                                    e.setCutUpon10(split[1]);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.SHARE_GOODS))
        {
            List<MktGoodsShareExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsShareExportExcel e = BeanUtil.beanFrom(MktGoodsShareExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setComm(s.getComm());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.GIFT_GOODS))
        {
            List<MktGoodsGiftExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsGiftExportExcel e = BeanUtil.beanFrom(MktGoodsGiftExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    e.setUserFarmerName(dto.getUserFarmerName());
                    e.setUserVendorName(dto.getUserVendorName());
                    e.setPoint(s.getPoint());
                    e.setGiftStartDate(dto.getGiftStartDate());
                    e.setGiftEndDate(dto.getGiftEndDate());
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setKcNum(s.getKcNum());
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType == MType.INTEGRAL_GOODS || mType == MType.INTEGRAL_BNYP_GOODS)
        {
            List<MktGoodsIntegralExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces()) 
                {
                    MktGoodsIntegralExportExcel e = BeanUtil.beanFrom(MktGoodsIntegralExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    e.setPoint(s.getPoint());
                    if (CollectionUtil.isNotEmpty(dto.getPhoto1()))
                    {
                        e.setPhoto1(JsonUtil.toString(dto.getPhoto1()));
                    }
                    e.setContent2(dto.getContent2());
                    e.setSellingPoints(dto.getSellingPoints());
                    list.add(e);
                }
            }
            return list;
        }
        else if(mType == MType.INTEGRAL_MSD_GOODS)
        {
            List<MktGoodsIntegralMsdExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                String msdTags = "全部用户";
                if(MemberVisibleRange.TAG.equals(dto.getVisibleRange()))
                {
                    msdTags = tagManager.getMsdGoodsTagsName(dto.getPkey().longValue());
                }
                for (MktGoodsSpaceOnList s : dto.getSpaces()) 
                {
                    MktGoodsIntegralMsdExportExcel e = BeanUtil.beanFrom(MktGoodsIntegralMsdExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    e.setPoint(s.getPoint());
                    if (CollectionUtil.isNotEmpty(dto.getPhoto1()))
                    {
                        e.setPhoto1(JsonUtil.toString(dto.getPhoto1()));
                    }
                    e.setContent2(dto.getContent2());
                    e.setSellingPoints(dto.getSellingPoints());
                    e.setMsdTags(msdTags);
                    
                    MktGoodsPresale goodsPresale = goodsPresaleDao.get(e.getPkey());
                    if(goodsPresale != null)
                    {
                        e.setPresaleStartDate(DateUtil.formatDate(goodsPresale.getStartDate(), "yyyy-MM-dd"));
                        e.setPresaleEndDate(DateUtil.formatDate(goodsPresale.getEndDate(), "yyyy-MM-dd"));
                    }
                    
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType == MType.INTEGRAL_PRESALE_GOODS)
        {
            List<MktGoodsIntegralPresaleExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces()) {
                    MktGoodsIntegralPresaleExportExcel e = BeanUtil.beanFrom(MktGoodsIntegralPresaleExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());

                    if (CollectionUtil.isNotEmpty(dto.getPhoto1()))
                    {
                        e.setPhoto1(JsonUtil.toString(dto.getPhoto1()));
                    }
                    e.setContent2(dto.getContent2());
                    
                    MktGoodsPresale goodsPresale = goodsPresaleDao.get(e.getPkey());
                    if(goodsPresale != null)
                    {
                        e.setPresaleStartDate(DateUtil.formatDate(goodsPresale.getStartDate(), "yyyy-MM-dd"));
                        e.setPresaleEndDate(DateUtil.formatDate(goodsPresale.getEndDate(), "yyyy-MM-dd"));
                    }
                    e.setSellingPoints(dto.getSellingPoints());
                    list.add(e);
                }
            }
            return list;
        }
        else
        {
            List<GoodsMarketExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    GoodsMarketExportExcel e = BeanUtil.beanFrom(GoodsMarketExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    e.setPoint(s.getPoint());
                    //by yx
                    if (CollectionUtil.isNotEmpty(dto.getPhoto1()))
                    {
                        e.setPhoto1(JsonUtil.toString(dto.getPhoto1()));
                    }
                    e.setContent2(dto.getContent2());
                    e.setSellingPoints(dto.getSellingPoints());
                    list.add(e);
                }
            }
            return list;
        }
    }
    
}
