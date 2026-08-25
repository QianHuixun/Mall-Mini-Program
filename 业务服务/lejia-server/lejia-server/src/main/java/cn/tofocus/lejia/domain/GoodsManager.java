package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.util.*;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.join.db.SelectPageOps;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.config.AscriptionGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.config.FarmerGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.goods.GoodsAdvertOnInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsMarketExcel;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendOnPage;
import cn.tofocus.lejia.bean.dto.market.*;
import cn.tofocus.lejia.bean.entity.goods.*;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.*;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.market.SupplyManager;
import cn.tofocus.lejia.domain.market.goods.SpaceManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GoodsManager
{
    @Value("${lejia.goods.recommend.max:20}")
    private Integer maxRecommend;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private SpaceManager spaceManager;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private MktCookfdLineDao cookfdLineDao;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    @Autowired
    private MktCookfdDao cookfdDao;
    
    @Autowired
    private MktRichTemplateDao richTemplateDao;
    
    @Autowired
    private MktSupplyDao supplyDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private MktGoodsRecommendDao goodsRecommendDao;
    
    @Autowired
    private MktGoodsRecommendZoneDao goodsRecommendZoneDao;
    
    @Autowired
    private MktGoodsSellingPointDao goodsSellingPointDao;
    
    @Autowired
    private SupplyManager supplyManager;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private TagManager tagManager;
    
    @Transactional(rollbackOn = Throwable.class)
    public MktGoods insGoods(MktGoodsDetailsDTO entity)
    {
        if ((entity.getMType() == MType.INTEGRAL_GOODS || entity.getMType() == MType.INTEGRAL_PRESALE_GOODS
            || entity.getMType() == MType.INTEGRAL_BNYP_GOODS) && entity.getSupplier() == null)
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "供应商不能为空");
        }
        if (entity.getTag() != null && entity.getTag().length() > 6)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "标签长度不允许超过6个字");
        List<MktGoodsSellingPoint> sellingPoints = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(entity.getSellingPoints()))
        {
            if (entity.getSellingPoints().size() > 4)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点不允许超过4个");
            for (int i = 0; i < entity.getSellingPoints().size(); i++)
            {
                GoodsSellingPointDTO sellingPoint = entity.getSellingPoints().get(i);
                if (StringUtil.isBlank(sellingPoint.getName()) && StringUtil.isBlank(sellingPoint.getContent()))
                    continue;
                if (StringUtil.isBlank(sellingPoint.getName()) || StringUtil.isBlank(sellingPoint.getContent()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点" + (i + 1) + "名称和内容仅允许同时为空或同时有值");
                if (sellingPoint.getName().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点" + (i + 1) + "名称长度不允许超过6个字");
                if (sellingPoint.getContent().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点" + (i + 1) + "内容长度不允许超过6个字");
                
                MktGoodsSellingPoint sellingPointBean = new MktGoodsSellingPoint();
                sellingPointBean.setName(sellingPoint.getName());
                sellingPointBean.setContent(sellingPoint.getContent());
                sellingPointBean.setAscription(CurrentSession.ascriptionPkey());
                sellingPoints.add(sellingPointBean);
            }
        }
        MktGoods mktGoods = BeanUtil.beanFrom(MktGoods.class, entity);
        if (entity.getMType().getIndex() == 5)
        {
            mktGoods.setExtendCon(JsonUtil.toString(entity.getExtendConList()));
        }
        String marketPkey = CurrentSession.marketPkey();
        SysFarmer sysFarmer = farmerDao.get(marketPkey);
        // 商户商城, 处理商户
        if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            if (entity.getVendor() == null)
                throw TofocusException.of(LejiaErrCode.VENDOR_SHOPPING_MALL_VENDOR_ERROR);
            Boolean checkTitleVendorRepeat = goodsDao.checkTitleVendorRepeat(entity
                .getTitle(), entity.getMType(), marketPkey, entity.getPkey(), entity.getVendor());
            if (checkTitleVendorRepeat)
                throw TofocusException.of(LejiaErrCode.GOODS_VENDOR_NAMEREPEAT);
        }
        else
        {
            if (goodsDao.checkTitleRepeat(entity.getTitle(), entity.getMType(), marketPkey, entity.getPkey()))
                throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        
        mktGoods.setFarmer(marketPkey);
        mktGoods.setCompany(CurrentSession.companyPkey());
        mktGoods.setAscription(CurrentSession.ascriptionPkey());
        mktGoods.setRowVension(1);
        mktGoods.setIdDel(false);
        mktGoods.setViewCount(0);
        if (mktGoods.getStartDate() == null || mktGoods.getStartDate().getTime() <= System.currentTimeMillis())
            mktGoods.setEnabled(true);
        else
            mktGoods.setEnabled(false);
        //        if (System.currentTimeMillis() < mktGoods.getStartDate().getTime())
        //        {
        //            mktGoods.setEnabled(false);
        //        }
        //        else
        //        {
        //            mktGoods.setEnabled(true);
        //        }
        
        if (entity.getXsNum() == null)
        {
            mktGoods.setXsNum(0);
        }
        //        if (entity.getEndDate() == null)
        //        {
        //            Calendar cal = Calendar.getInstance();
        //            cal.set(2030, 1, 1);
        //            mktGoods.setEndDate(cal.getTime());
        //        }
        
        if (entity.getSort() == null)
        {
            mktGoods.setSort(0);
        }
        
        if (entity.getPurchaseNum() == null)
        {
            mktGoods.setPurchaseNum(0);
        }
        
        if (entity.getGuessLike() == null)
        {
            mktGoods.setGuessLike(false);
        }
        if (entity.getGuessSort() == null)
        {
            mktGoods.setGuessSort(0);
        }
        if (goodsMainDao.checkGtype(mktGoods.getGtype(), entity.getGoodsMain()))
            throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODSMAIN);
        MktGoods add = goodsDao.add(mktGoods);
        if (entity.getTagKeys() != null && !entity.getTagKeys().isEmpty()
            && MType.SPECIAL_GOODS.equals(entity.getMType()))
        {
            tagManager.putTagVisibles(TagVisibleTargetType.SPECIAL_GOODS,
                add.getPkey().longValue(),
                entity.getTagKeys(),
                CurrentSession.ascriptionPkey());
        }
        if (MType.INTEGRAL_MSD_GOODS.equals(entity.getMType()))
        {
            if (entity.getMsdTags() != null && !entity.getMsdTags().isEmpty())
            {
                tagManager.putTagVisibles(TagVisibleTargetType.INTEGRAL_MSD_GOODS,
                    add.getPkey().longValue(),
                    entity.getMsdTags(),
                    CurrentSession.ascriptionPkey());
            }
            if (entity.getPresaleStartDate() != null || entity.getPresaleEndDate() != null)
            {
                if (entity.getPresaleStartDate() == null || entity.getPresaleEndDate() == null)
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "发货起止时间需要同时填写");
                MktGoodsPresale gp = new MktGoodsPresale();
                gp.setPkey(add.getPkey());
                gp.setAscription(add.getAscription());
                gp.setStartDate(entity.getPresaleStartDate());
                gp.setEndDate(entity.getPresaleEndDate());
                goodsPresaleDao.add(gp);
            }
        }
        if (MType.PRESALE_GOODS.equals(entity.getMType()) || MType.INTEGRAL_PRESALE_GOODS.equals(entity.getMType())
            )
        {
            if (entity.getPresaleStartDate() == null || entity.getPresaleEndDate() == null)
                throw TofocusException.of(LejiaErrCode.PRESALEGOODS_DELIVERY_TIME_ERROR);
            if (entity.getStartDate() !=null && entity.getPresaleStartDate().compareTo(entity.getStartDate()) < 0)
                throw TofocusException.of(LejiaErrCode.PRESALEGOODS_TIME_ERROR);
            MktGoodsPresale gp = new MktGoodsPresale();
            gp.setPkey(add.getPkey());
            gp.setAscription(add.getAscription());
            gp.setStartDate(entity.getPresaleStartDate());
            gp.setEndDate(entity.getPresaleEndDate());
            goodsPresaleDao.add(gp);
        }
        Integer goodsPkey = add.getPkey();
        if (add.getMType().equals(MType.GIFT_GOODS))
        {
            if (StringUtils.isBlank(entity.getUserFarmer()))
                throw TofocusException.of(LejiaErrCode.USERFARMER_NOT_EMPTY);
            if (entity.getUserVendor() == null)
                throw TofocusException.of(LejiaErrCode.USERVENDOR_NOT_EMPTY);
            add.setExtendCon(String.valueOf(entity.getUserVendor()));
            MktVendor vendor = vendorDao.getVendor(entity.getUserVendor());
            if (vendor == null)
                throw TofocusException.of(LejiaErrCode.VENDOR_ERROR);
            String giftTitle = entity.getTitle() + entity.getSpaces().get(0).getSpace();
            String giftContent = "积分商城礼券，请于商户【" + vendor.getName() + "】核销。";
            MktGoodsGift gift = new MktGoodsGift(goodsPkey, giftTitle, giftContent,
                entity.getExpireChoose() ? CouponExpireChoose.DATE_RANGE : CouponExpireChoose.LONG_TERM,
                entity.getUserFarmer(), entity.getUserVendor(), entity.getGiftStartDate(), entity.getGiftEndDate(),
                GiftType.INTEGRAL_BUY);
            gift.setAscription(CurrentSession.ascriptionPkey());
            goodsGiftDao.add(gift);
            goodsDao.update(add);
        }
        boolean sign = false;
        List<MktGoodsSpace> spaces = BeanUtil.beanListFrom(MktGoodsSpace.class, entity.getSpaces());
        Map<String, MktGoodsSpace> sapcesRepeat = new HashMap<>();
        Iterator<MktGoodsSpace> iterator = spaces.iterator();
        while (iterator.hasNext())
        {
            MktGoodsSpace next = iterator.next();
            if (sapcesRepeat.containsKey(next.getSpace()))
                throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
            sapcesRepeat.put(next.getSpace(), next);
            if (next.getWeight() == null)
                next.setWeight(BigDecimal.ZERO);
            if (next.getPriceOld() == null)
            {
                next.setPriceOld(next.getPrice());
            }
            if (next.getPriceMember() == null)
            {
                next.setPriceMember(BigDecimal.ZERO);
            }
            if (next.getPriceMember().compareTo(BigDecimal.ZERO) > 0)//设置会员商品标签
            {
                sign = true;
            }
            if (next.getPriceMember().compareTo(next.getPrice()) > 0)
            {
                throw TofocusException.of(LejiaErrCode.GOODS_MEMBERPRICE_ERR);
            }
            if (next.getWeight() == null)
            {
                next.setWeight(BigDecimal.ZERO);
            }
            if (next.getPoint() == null)
                next.setPoint(0);
            next.setGoods(goodsPkey);
            next.setXsNum(0);
            next.setAscription(CurrentSession.ascriptionPkey());
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(spaces);
        List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, addAll);
        spaceKcDao.addAll(kcList);
        kcList.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        setGoodsMixPrice(add, addAll);
        
        if (entity.getMType().getIndex() == 1 && sign)
        {
            add.setExtendCon("member");
            goodsDao.update(add);
        }
        wareManager.insWare(add, addAll);
        if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            insertSupply(marketPkey, goodsPkey, entity.getVendor(), add.getMType(), addAll);
        }
        // 处理卖点
        if (CollectionUtil.isNotEmpty(sellingPoints))
        {
            for (MktGoodsSellingPoint sellingPoint : sellingPoints)
            {
                sellingPoint.setGoods(goodsPkey);
            }
            goodsSellingPointDao.addAll(sellingPoints);
        }
        goodListQueryer.resetThreeGtype(add);
        return add;
    }
    
    private void setGoodsMixPrice(MktGoods goods, List<MktGoodsSpace> addAll)
    {
        if (addAll.size() > 1)
        {
            Collections.sort(addAll, new Comparator<MktGoodsSpace>()
            {
                
                @Override
                public int compare(MktGoodsSpace o1, MktGoodsSpace o2)
                {
                    BigDecimal i = o1.getPrice().subtract(o2.getPrice());
                    return i.compareTo(BigDecimal.ZERO);
                }
            });
        }
        if (!addAll.isEmpty())
        {
            goods.setPrice(addAll.get(0).getPrice());
        }
        goodsDao.update(goods);
    }
    
    @Transactional
    public MktGoods updGoods(MktGoodsUpdDTO entity)
    {
        if ((entity.getMType() == MType.INTEGRAL_GOODS || entity.getMType() == MType.INTEGRAL_PRESALE_GOODS
            || entity.getMType() == MType.INTEGRAL_BNYP_GOODS || entity.getMType() == MType.INTEGRAL_MSD_GOODS) 
            && entity.getSupplier() == null)
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "供应商不能为空");
        }
        if (entity.getTag() != null && entity.getTag().length() > 6)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "标签长度不允许超过6个字");
        Integer goodsPkey = entity.getPkey();
        if (goodsPkey == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "主键不能为空");
        List<MktGoodsSellingPoint> sellingPoints = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(entity.getSellingPoints()))
        {
            if (entity.getSellingPoints().size() > 4)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点不允许超过4个");
            for (int i = 0; i < entity.getSellingPoints().size(); i++)
            {
                GoodsSellingPointDTO sellingPoint = entity.getSellingPoints().get(i);
                if (StringUtil.isBlank(sellingPoint.getName()) && StringUtil.isBlank(sellingPoint.getContent()))
                    continue;
                if (StringUtil.isBlank(sellingPoint.getName()) || StringUtil.isBlank(sellingPoint.getContent()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点" + (i + 1) + "名称和内容仅允许同时为空或同时有值");
                if (sellingPoint.getName().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点" + (i + 1) + "名称长度不允许超过6个字");
                if (sellingPoint.getContent().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点" + (i + 1) + "内容长度不允许超过6个字");
                
                MktGoodsSellingPoint sellingPointBean = BeanUtil.beanFrom(MktGoodsSellingPoint.class, sellingPoint);
                sellingPointBean.setGoods(goodsPkey);
                sellingPointBean.setAscription(CurrentSession.ascriptionPkey());
                sellingPoints.add(sellingPointBean);
            }
        }
        MktGoods mktGoods = goodsDao.get(goodsPkey);
        if (mktGoods == null)
            throw TofocusException.of(WsaleErrCode.NOT_GOODS);
        //if (mktGoods.getEnabled()) throw TofocusException.of(WsaleErrCode.GOODS_CANNOT_EDIT);
        String marketPkey = CurrentSession.marketPkey();
        Integer yThreeGtype = mktGoods.getThreeGtype();
        // 商户商城, 处理商户
        SysFarmer sysFarmer = farmerDao.get(marketPkey);
        //        Boolean vendorFlag = false;
        if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            if (entity.getVendor() == null)
                throw TofocusException.of(LejiaErrCode.VENDOR_SHOPPING_MALL_VENDOR_ERROR);
            //            if(mktGoods.getVendor() != null && 
            //                entity.getVendor().intValue() != mktGoods.getVendor().intValue())
            //                vendorFlag = true;
            Boolean checkTitleVendorRepeat = goodsDao.checkTitleVendorRepeat(entity
                .getTitle(), entity.getMType(), marketPkey, entity.getPkey(), entity.getVendor());
            if (checkTitleVendorRepeat)
                throw TofocusException.of(LejiaErrCode.GOODS_VENDOR_NAMEREPEAT);
        }
        else if (!mktGoods.getTitle().equals(entity.getTitle()))
        {
            if (goodsDao.checkTitleRepeat(entity.getTitle(), entity.getMType(), marketPkey, entity.getPkey()))
                throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        
        if (entity.getSort() == null)
            throw TofocusException.of(WsaleErrCode.SORT_NOT_EMPTY);
        BeanUtils.copyProperties(entity, mktGoods, "viewCount");
        if (entity.getMType().getIndex() == 5)
            mktGoods.setExtendCon(JsonUtil.toString(entity.getExtendConList()));
        if (entity.getPurchaseNum() == null)
            mktGoods.setPurchaseNum(0);
        if(MType.INTEGRAL_MSD_GOODS.equals(entity.getMType()) && entity.getMsdTags() != null && !entity.getMsdTags().isEmpty())
        {
            tagManager.putTagVisibles(TagVisibleTargetType.INTEGRAL_MSD_GOODS,
                mktGoods.getPkey().longValue(),
                entity.getMsdTags(),
                CurrentSession.ascriptionPkey());
        }
        
        log.info("updGoods-mktGoods: {}", mktGoods);
        int sign = 0;
        List<MktGoodsSpaceOnList> spaces = entity.getSpaces();
        List<MktGoodsSpace> insSpace = new ArrayList<>();
        List<MktGoodsSpace> updSpace = new ArrayList<>();
        List<MktGoodsSpace> oldSpace = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        List<MktGoodsSpace> delSpace = new ArrayList<>();
        for (MktGoodsSpaceOnList gs : spaces)
        {
            if (gs.getStatus().intValue() == 0) // vendorFlag && 
            {
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(gs.getPkey());
                //                MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, gs);
                oldSpace.add(updaSpaceModel);
            }
            if (gs.getStatus().intValue() == 1)
            {
                MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, gs);
                space.setGoods(goodsPkey);
                if (space.getPriceOld() == null)
                    space.setPriceOld(space.getPrice());
                if (space.getPriceMember() == null)
                    space.setPriceMember(BigDecimal.ZERO);
                if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)//设置会员商品标签
                    sign = 1;
                if (space.getPriceMember().compareTo(space.getPrice()) > 0)
                {
                    throw TofocusException.of(LejiaErrCode.GOODS_MEMBERPRICE_ERR);
                }
                space.setXsNum(0);
                if (space.getWeight() == null)
                    space.setWeight(BigDecimal.ZERO);
                space.setAscription(CurrentSession.ascriptionPkey());
                insSpace.add(space);
            }
            if (gs.getStatus().intValue() == 2)
            {
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(gs.getPkey());
                if (updaSpaceModel.getKcNum().intValue() != gs.getKcNum().intValue())
                {
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.INVENTORY);
                    add.setGoods(mktGoods.getPkey());
                    add.setGoodsName(mktGoods.getTitle());
                    add.setSpace(gs.getPkey());
                    add.setSpaceName(gs.getSpace());
                    add.setNum(gs.getKcNum() - updaSpaceModel.getKcNum());
                    add.setActualNum(gs.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
                BeanUtils.copyProperties(gs, updaSpaceModel, "xsNum", "ascription");
                if (updaSpaceModel.getPriceMember() == null)
                    updaSpaceModel.setPriceMember(BigDecimal.ZERO);
                if (updaSpaceModel.getPriceMember().compareTo(BigDecimal.ZERO) > 0)//设置会员商品标签
                    sign = 1;
                if (updaSpaceModel.getPriceMember().compareTo(updaSpaceModel.getPrice()) > 0)
                {
                    throw TofocusException.of(LejiaErrCode.GOODS_MEMBERPRICE_ERR);
                }
                if (updaSpaceModel.getWeight() == null)
                    updaSpaceModel.setWeight(BigDecimal.ZERO);
                updaSpaceModel.setAscription(CurrentSession.ascriptionPkey());
                updSpace.add(updaSpaceModel);
            }
            if (gs.getStatus().intValue() == 3)
            {
                MktGoodsSpace delSpaceModel = goodsSpaceDao.get(gs.getPkey());
                delSpace.add(delSpaceModel);
            }
        }
        if (entity.getMType().getIndex() == 1)
            if (sign == 1)
                mktGoods.setExtendCon("member");
            else if (sign == 2)
                mktGoods.setExtendCon(null);
        if (mktGoods.getSort() == null)
            mktGoods.setSort(0);
        if (!delSpace.isEmpty())
        {
            List<Integer> delSpaceKeys = new ArrayList<>();
            for (MktGoodsSpace s : delSpace)
            {
                delSpaceKeys.add(s.getPkey());
                List<MktCookfdLine> exec = cookfdLineDao.select().eq("space", s.getPkey()).exec();
                if (exec != null && !exec.isEmpty())
                {
                    MktCookfd cookfd = cookfdDao.get(exec.get(0).getCookfd());
                    if (cookfd != null && Boolean.FALSE.equals(cookfd.getIdDel()))
                    {
                        throw TofocusException.of(WsaleErrCode.COOKFD_SPACE_USE,
                            "请先在菜谱中移除该规格,菜谱名称: 菜谱名称: " + cookfd.getName());
                    }
                }
            }
            List<MktGwc> gwcDelList = gwcDao.select().in("space", delSpaceKeys.toArray()).exec();
            gwcDao.removeAll(gwcDelList);
        }
        if (mktGoods.getMType().equals(MType.GIFT_GOODS))
        {
            mktGoods.setExtendCon(String.valueOf(entity.getUserVendor()));
            if (StringUtils.isBlank(entity.getUserFarmer()))
                throw TofocusException.of(LejiaErrCode.USERFARMER_NOT_EMPTY);
            if (entity.getUserVendor() == null)
                throw TofocusException.of(LejiaErrCode.USERVENDOR_NOT_EMPTY);
            MktVendor vendor = vendorDao.getVendor(entity.getUserVendor());
            if (vendor == null)
                throw TofocusException.of(LejiaErrCode.VENDOR_ERROR);
            String giftTitle = entity.getTitle() + entity.getSpaces().get(0).getSpace();
            String giftContent = "积分商城礼券，请于商户【" + vendor.getName() + "】核销。";
            MktGoodsGift byGoods = goodsGiftDao.getByGoods(goodsPkey);
            MktGoodsGift gift = new MktGoodsGift(goodsPkey, giftTitle, giftContent,
                entity.getExpireChoose() ? CouponExpireChoose.DATE_RANGE : CouponExpireChoose.LONG_TERM,
                entity.getUserFarmer(), entity.getUserVendor(), entity.getGiftStartDate(), entity.getGiftEndDate(),
                GiftType.INTEGRAL_BUY);
            if (byGoods != null)
            {
                gift.setPkey(byGoods.getPkey());
                gift.setAscription(byGoods.getAscription());
                gift.setEnabled(byGoods.getEnabled());
                gift.setInvalid(byGoods.getInvalid());
                gift.setIssuedNum(byGoods.getIssuedNum());
                gift.setUsedNum(byGoods.getUsedNum());
            }
            goodsGiftDao.put(gift);
        }
        if(MType.INTEGRAL_MSD_GOODS.equals(entity.getMType()))
        {
            if (entity.getPresaleStartDate() != null || entity.getPresaleEndDate() != null)
            {
                if (entity.getPresaleStartDate() == null || entity.getPresaleEndDate() == null)
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "发货起止时间需要同时填写");
                MktGoodsPresale gp = goodsPresaleDao.get(goodsPkey);
                if (gp == null)
                {
                    gp = new MktGoodsPresale();
                    gp.setPkey(goodsPkey);
                    gp.setAscription(mktGoods.getAscription());
                }
                gp.setStartDate(entity.getPresaleStartDate());
                gp.setEndDate(entity.getPresaleEndDate());
                goodsPresaleDao.put(gp);
            }
        }
        if (MType.PRESALE_GOODS.equals(entity.getMType())  || MType.INTEGRAL_PRESALE_GOODS.equals(entity.getMType()))
        {
            if (entity.getPresaleStartDate() == null || entity.getPresaleEndDate() == null)
                throw TofocusException.of(LejiaErrCode.PRESALEGOODS_DELIVERY_TIME_ERROR);
            if (entity.getPresaleStartDate().compareTo(entity.getStartDate()) < 0)
                throw TofocusException.of(LejiaErrCode.PRESALEGOODS_TIME_ERROR);
            MktGoodsPresale gp = goodsPresaleDao.get(goodsPkey);
            if (gp == null)
            {
                gp = new MktGoodsPresale();
                gp.setPkey(goodsPkey);
                gp.setAscription(mktGoods.getAscription());
            }
            gp.setStartDate(entity.getPresaleStartDate());
            gp.setEndDate(entity.getPresaleEndDate());
            goodsPresaleDao.put(gp);
        }
        goodsDao.update(mktGoods);
        if (entity.getTagKeys() != null && !entity.getTagKeys().isEmpty()
            && MType.SPECIAL_GOODS.equals(entity.getMType()))
        {
            tagManager.putTagVisibles(TagVisibleTargetType.SPECIAL_GOODS,
                mktGoods.getPkey().longValue(),
                entity.getTagKeys(),
                CurrentSession.ascriptionPkey());
        }
        // 要先更新规格前,将库存更新
        wareLineDao.addAll(addWareLineAll);
        goodsSpaceDao.updateAll(updSpace);
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(insSpace);
        wareManager.insWare(mktGoods, addAll);
        wareManager.delWare(mktGoods, delSpace);
        goodsSpaceDao.removeAll(delSpace);
        if (!delSpace.isEmpty())
        {
            // 删除供应库 对应数据
            delSupply(delSpace);
        }
        List<MktGoodsSpace> s = new ArrayList<>();
        s.addAll(addAll);
        s.addAll(updSpace);
        
        List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, s);
        spaceKcDao.putAll(kcList);
        kcList.forEach(sk -> spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum())));
        
        Map<String, MktGoodsSpace> sapcesRepeat = new HashMap<>();
        for (MktGoodsSpace next : s)
        {
            if (sapcesRepeat.containsKey(next.getSpace()))
                throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
            sapcesRepeat.put(next.getSpace(), next);
        }
        spaces.forEach(e -> {
            if (e.getStatus().intValue() == 0)
            {
                MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, e);
                s.add(space);
            }
        });
        setGoodsMixPrice(mktGoods, s);
        if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            addAll.addAll(updSpace);
            addAll.addAll(oldSpace);
            insertSupply(marketPkey, goodsPkey, entity.getVendor(), mktGoods.getMType(), addAll);
        }
        // 处理卖点
        goodsSellingPointDao.removeByGoods(mktGoods.getPkey(), mktGoods.getAscription());
        if (CollectionUtil.isNotEmpty(sellingPoints))
        {
            goodsSellingPointDao.addAll(sellingPoints);
        }
        // 修改三级分类
        goodListQueryer.resetThreeGtype(yThreeGtype, marketPkey);
        goodListQueryer.resetThreeGtype(mktGoods);
        return mktGoods;
    }
    
    public void insertSupply(String marketPkey, Integer goodsPkey, Integer vendor, MType mtype,
        List<MktGoodsSpace> spaceList)
    {
        MktSupplyInfo supply = new MktSupplyInfo();
        supply.setMarketPkey(marketPkey);
        supply.setGoodsPkey(goodsPkey);
        supply.setMType(mtype);
        MktVendor mktVendor = vendorDao.get(vendor);
        BigDecimal commissionRate = mktVendor.getCommissionRate();
        if (commissionRate == null)
            commissionRate = BigDecimal.ZERO;
        supply.setCommissionRate2(commissionRate);
        List<MktSupplyDetailInfo> list = new ArrayList<>();
        for (MktGoodsSpace gs : spaceList)
        {
            MktSupplyDetailInfo sd = new MktSupplyDetailInfo();
            sd.setSpace(gs.getPkey());
            sd.setVendor(vendor);
            sd.setCommissionRate2(commissionRate);
            sd.setPurchasingPrice(gs.getPrice());
            sd.setSort(1);
            sd.setEnabled(true);
            list.add(sd);
        }
        supply.setList(list);
        supplyManager.insert(supply, false);
    }
    
    public String checkPricePurchase(Integer goodsPkey)
    {
        String res = "";
        MktGoods goods = goodsDao.get(goodsPkey);
        if (goods == null)
            return res;
        List<MktGoodsSpace> gsList = goodsSpaceDao.select().eq("goods", goodsPkey).exec();
        List<MktSupply> sList = supplyDao.select().eq("good", goodsPkey).exec();
        Map<Integer, MktGoodsSpace> map = new HashMap<>();
        gsList.forEach(e -> {
            map.put(e.getPkey(), e);
        });
        for (MktSupply s : sList)
        {
            Integer skey = Integer.valueOf(s.getSpace());
            if (map.containsKey(skey))
            {
                MktGoodsSpace space = map.get(skey);
                BigDecimal price = space.getPriceMember();
                if (price.compareTo(BigDecimal.ZERO) == 0)
                    price = space.getPrice();
                if (price.compareTo(s.getPurchasingPrice()) == -1)
                {
                    if (StringUtils.isBlank(res))
                        res = "商品:" + goods.getTitle() + "  规格:" + space.getSpace();
                    else
                        res = res + "," + space.getSpace();
                }
            }
        }
        if (StringUtils.isNotBlank(res))
        {
            res = res + "  价格低于采购价";
        }
        return res;
    }
    
    // 查询未处理的数据
    public PageResult<MktGoodsDetailsDTO> queryGoodsList(Integer page, Integer pagesize, MType mType, Boolean enabled,
        Integer status, Integer gtype, String title, String farmer)
    {
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(marketPkey) && StringUtils.isNotBlank(farmer))
        {
            marketPkey = farmer;
            companyPkey = null;
        }
        //        if((Constant.Operation + ascription).equals(marketPkey))
        //        {
        //            marketPkey = null;
        //            companyPkey = null;
        //        }
        PageResult<MktGoods> pageResult = goodsDao
            .queryGoodsList(page, pagesize, mType, enabled, status, gtype, title, marketPkey, companyPkey, ascription);
        PageResult<MktGoodsDetailsDTO> result = BeanUtil.beanPageFrom(MktGoodsDetailsDTO.class, pageResult);
        // 组装商品名称及其他信息
        assembleName(result.getContent(), ascription);
        return result;
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
                if (extendConList == null)
                    extendConList = new ArrayList<>();
                bean.setExtendConList(extendConList);
            }
            if (bean.getMType().equals(MType.GIFT_GOODS))
            {
                keys.add(bean.getPkey());
                if (bean.getUserVendor() != null)
                    userVendorKeys.add(bean.getUserVendor());
            }
            bean.setGtypeName(gtypeDao.get(bean.getGtype()).getName());
            // 通过mkt_goods_main设置商品名称
            bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
            if (bean.getThreeGtype() != null)
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
            if (bean.getVendor() != null)
            {
                MktVendor mktVendor = vendorDao.get(bean.getVendor());
                if (mktVendor != null)
                    bean.setVendorName(mktVendor.getDisplayName());
            }
            if (bean.getSupplier() != null)
            {
                MktSupplier mktSupplier = supplierDao.get(bean.getSupplier());
                if (mktSupplier != null)
                    bean.setSupplierName(mktSupplier.getName());
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
                    if (nameMap.containsKey(userFarmer))
                        bean.setUserFarmerName(nameMap.get(userFarmer));
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
    
    public MktGoodsDetailsDTO getGoods(Integer pkey)
    {
        MktGoods mktGoods = goodsDao.getGoods(pkey);
        if (mktGoods == null)
            return null;
        List<MktGoodsSpaceOnList> exec = goodsSpaceDao.select().eq("goods", pkey).execDto(MktGoodsSpaceOnList.class);
        MktGoodsDetailsDTO result = BeanUtil.beanFrom(MktGoodsDetailsDTO.class, mktGoods);
        result.setSpaces(exec);
        if (mktGoods.getVendor() != null)
        {
            MktVendor mktVendor = vendorDao.get(mktGoods.getVendor());
            if (mktVendor != null)
            {
                result.setVendorName(mktVendor.getDisplayName());
            }
        }
        if (mktGoods.getSupplier() != null)
        {
            MktSupplier mktSupplier = supplierDao.get(mktGoods.getSupplier());
            if (mktSupplier != null)
            {
                result.setSupplierName(mktSupplier.getName());
            }
        }
        List<GoodsSellingPointDTO> sellingPoints =
            goodsSellingPointDao.listByGoods(pkey, mktGoods.getAscription(), GoodsSellingPointDTO.class);
        result.setSellingPoints(sellingPoints);
        return result;
    }
    
    public MktGoodsDetailsDTO getMemberGoods(Integer pkey)
    {
        MktGoods mktGoods = goodsDao.getGoods(pkey);
        if (mktGoods == null)
            return null;
        List<MktGoodsSpaceOnList> exec =
            goodsSpaceDao.select().eq("goods", pkey).notEq("priceMember", 0).execDto(MktGoodsSpaceOnList.class);
        MktGoodsDetailsDTO result = BeanUtil.beanFrom(MktGoodsDetailsDTO.class, mktGoods);
        result.setSpaces(exec);
        if (result.getMType().equals(MType.GIFT_GOODS))
        {
            MktGoodsGift gift = goodsGiftDao.getByGoods(result.getPkey());
            if (gift != null)
            {
                result.setExpireChoose(gift.getExpireChoose() != CouponExpireChoose.LONG_TERM);
                result.setUserFarmer(gift.getUserFarmer());
                result.setUserVendor(gift.getUserVendor());
                result.setGiftStartDate(gift.getStartDate());
                result.setGiftEndDate(gift.getEndDate());
            }
        }
        
        return result;
    }
    
    public Boolean delGoods(Integer pkey)
    {
        MktGoods mktGoods = goodsDao.selectOne().eq("pkey", pkey).eq("idDel", false).exec();
        if (mktGoods == null)
            throw TofocusException.of(WsaleErrCode.NOT_GOODS);
        if (mktGoods.getEnabled())
            throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        if (mktGoods.getMType().equals(MType.COUPON_GOODS))
        {
            if (cardDao.checkInvalid(Integer.valueOf(mktGoods.getExtendCon())))
                throw TofocusException.of(WsaleErrCode.CARD_INVALID);
            if (orderLineDao.checkSalesRecord(pkey))
                throw TofocusException.of(WsaleErrCode.EXIST_ORDER_DEL);
        }
        mktGoods.setIdDel(true);
        List<MktCookfdLine> exec = cookfdLineDao.select().eq("goods", pkey).exec();
        if (exec != null && !exec.isEmpty())
        {
            MktCookfd cookfd = cookfdDao.selectOne().eq("pkey", exec.get(0).getCookfd()).eq("idDel", false).exec();
            if (cookfd != null)
            {
                throw TofocusException.of(WsaleErrCode.COOKFD_SPACE_USE, "请先在菜谱中移除该规格,菜谱名称: 菜谱名称: " + cookfd.getName());
            }
        }
        delSupply(pkey);
        MktGoods update = goodsDao.update(mktGoods);
        spaceManager.updGoodsPrice(update.getPkey());
        if (mktGoods.getMType().equals(MType.BOX_GOODS))
        {
            MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", mktGoods.getPkey()).exec();
            goodsBoxDao.remove(goodsBox);
        }
        return true;
    }
    
    public Boolean delListGoods(List<Integer> pkeys)
    {
        List<MktGoods> goodsList = goodsDao.select().in("pkey", pkeys).eq("idDel", false).exec();
        for (MktGoods mktGoods : goodsList)
        {
            if (mktGoods.getEnabled())
                throw TofocusException.of(WsaleErrCode.NOT_DELETED);
            if (mktGoods.getMType().equals(MType.COUPON_GOODS))
            {
                if (cardDao.checkInvalid(Integer.valueOf(mktGoods.getExtendCon())))
                    throw TofocusException.of(WsaleErrCode.CARD_INVALID);
                if (orderLineDao.checkSalesRecord(mktGoods.getPkey()))
                    throw TofocusException.of(WsaleErrCode.EXIST_ORDER_DEL);
            }
            mktGoods.setIdDel(true);
            List<MktCookfdLine> exec = cookfdLineDao.select().eq("goods", mktGoods.getPkey()).exec();
            if (exec != null && !exec.isEmpty())
            {
                MktCookfd cookfd = cookfdDao.selectOne().eq("pkey", exec.get(0).getCookfd()).eq("idDel", false).exec();
                if (cookfd != null)
                    throw TofocusException.of(WsaleErrCode.COOKFD_GOODS_USE, "请先在菜谱中移除该商品,菜谱名称: " + cookfd.getName());
            }
            delSupply(mktGoods.getPkey());
            //            spaceManager.updGoodsPrice(mktGoods.getPkey());
        }
        goodsDao.updateAll(goodsList);
        return true;
    }
    
    public Boolean enabledGoods(Integer pkey, Boolean flag)
    {
        MktGoods mktGoods = goodsDao.getGoods(pkey);
        // 商品校验
        if (Objects.isNull(mktGoods))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE);
        }
        // 上架
        if (flag)
        {
            Date startDate = mktGoods.getStartDate();
            //            if(startDate == null || mktGoods.getEndDate() == null)
            //                throw TofocusException.of(WsaleErrCode.GOODS_DATE_ERROR);
            if (mktGoods.getMType() != MType.INTEGRAL_PRESALE_GOODS && startDate != null
                && new Date().getTime() < startDate.getTime())
                throw TofocusException.of(WsaleErrCode.GOODS_NOT_AVAILABLE);
            if (mktGoods.getEndDate() != null && mktGoods.getMType() != MType.INTEGRAL_PRESALE_GOODS)
            {
                Date date = mktGoods.getEndDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                if (new Date().getTime() > cal.getTime().getTime())
                    throw TofocusException.of(WsaleErrCode.GOODS_NOT_ENDAVAILABLE);
            }
            if (mktGoods.getMType().equals(MType.COUPON_GOODS)
                && cardDao.checkInvalid(Integer.valueOf(mktGoods.getExtendCon())))
                throw TofocusException.of(WsaleErrCode.CARD_INVALID);
            // 积分商品如果没有所属供应商，不允许上架
            if ((mktGoods.getMType() == MType.INTEGRAL_GOODS || mktGoods.getMType() == MType.INTEGRAL_PRESALE_GOODS
                || mktGoods.getMType() == MType.INTEGRAL_BNYP_GOODS || mktGoods.getMType() == MType.INTEGRAL_MSD_GOODS) 
                && mktGoods.getSupplier() == null)
                throw TofocusException.of(WsaleErrCode.GOODS_NO_SUPPLIER);
        }
        // 下架
        else
        {
            // 商品下架，同时关闭“猜我喜欢”
            mktGoods.setGuessLike(false);
            mktGoods.setZoneRecommend(false);
        }
        mktGoods.setEnabled(flag);
        MktGoods update = goodsDao.update(mktGoods);
        if (update == null)
            return false;
        goodListQueryer.resetThreeGtype(update);
        return true;
    }
    
    public List<MktGoodsOnList> listGoodsTitle()
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGoods> list = goodsDao.select()
            .eq("mType", MType.MARKET_GOODS)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .exec();
        return BeanUtil.beanListFrom(MktGoodsOnList.class, list);
    }
    
    public List<DropDTO> listGoodsTitle(String title)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGoods> list = goodsDao.select()
            .like("title", title)
            .eq("mType", MType.MARKET_GOODS)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .exec();
        return BeanUtil.beanListFrom(DropDTO.class, list);
    }
    
    // 导入商品表使用
    public MktGoods putGoods(GoodsMarketExcel data, Map<String, MktGoodsMain> goodsMainMap,
        Map<String, MktGoods> goodsMap)
    {
        MktGoods g = new MktGoods();
        boolean flag = true;
        if (goodsMap.containsKey(data.getTitle()))
        {
            BeanUtils.copyProperties(goodsMap.get(data.getTitle()), g);
            flag = false;
        }
        String gooodsMainName = data.getGooodsMainName();
        log.info("gooodsMainName: {}", gooodsMainName);
        if (StringUtils.isBlank(gooodsMainName) || !gooodsMainName.contains("/"))
        {
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        }
        if (!goodsMainMap.containsKey(gooodsMainName))
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
        g.setGtype(goodsMain.getGtype());
        g.setGoodsMain(goodsMain.getPkey());
        g.setGuessLike(data.getGuessLike() == null ? false : data.getGuessLike());
        g.setMType(MType.MARKET_GOODS);
        g.setTitle(data.getTitle());
        g.setSerialNumber(data.getSerialNumber());
        g.setDescription(data.getDescription());
        g.setStartDate(data.getStartDate());
        g.setEndDate(data.getEndDate());
        int viewCount = 0;
        if (g != null && g.getViewCount() != null)
            viewCount = g.getViewCount();
        if (System.currentTimeMillis() < g.getStartDate().getTime())
        {
            g.setEnabled(false);
        }
        else
        {
            g.setEnabled(true);
        }
        g.setViewCount(viewCount);
        g.setXsNum(data.getXsNum());
        g.setPurchaseNum(data.getPurchaseNum());
        g.setPrice(data.getPrice());
        if (data.getIsPostage().intValue() == 0)
            g.setIsPostage(false);
        else
            g.setIsPostage(true);
        if (data.getSort() != null)
            g.setSort(data.getSort());
        else
            g.setSort(0);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setEnabled(true);
        g.setIdDel(false);
        g.setRowVension(1);
        MktGoods put = goodsDao.put(g);
        if (flag)
            goodsMap.put(data.getTitle(), put);
        data.setGoods(put.getPkey());
        data.setPoint(0);
        data.setComm(BigDecimal.ZERO);
        return put;
    }
    
    // 导入商品表使用(根据商品属性进行区分)
    public MktGoods putGoodsMType(GoodsMarketExcel data, Map<String, MktGoodsMain> goodsMainMap,
        Map<String, MktGoods> goodsMap, MType mType)
    {
        MktGoods g = new MktGoods();
        boolean flag = true;
        if (goodsMap.containsKey(data.getTitle()))
        {
            BeanUtils.copyProperties(goodsMap.get(data.getTitle()), g);
            flag = false;
        }
        String gooodsMainName = data.getGooodsMainName();
        log.info("putGoodsMType-gooodsMainName: {}", gooodsMainName);
        if (StringUtils.isBlank(gooodsMainName) || !gooodsMainName.contains("/"))
        {
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        }
        if (!goodsMainMap.containsKey(gooodsMainName))
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
        g.setGtype(goodsMain.getGtype());
        g.setGoodsMain(goodsMain.getPkey());
        g.setGuessLike(data.getGuessLike() == null ? false : data.getGuessLike());
        g.setMType(mType);
        g.setTitle(data.getTitle());
        g.setSerialNumber(data.getSerialNumber());
        g.setDescription(data.getDescription());
        g.setStartDate(data.getStartDate());
        g.setEndDate(data.getEndDate());
        int viewCount = 0;
        if (g != null && g.getViewCount() != null)
            viewCount = g.getViewCount();
        if (System.currentTimeMillis() < g.getStartDate().getTime())
        {
            g.setEnabled(false);
        }
        else
        {
            g.setEnabled(true);
        }
        g.setViewCount(viewCount);
        g.setXsNum(data.getXsNum());
        g.setPurchaseNum(data.getPurchaseNum());
        g.setPrice(data.getPrice());
        if (data.getIsPostage().intValue() == 0)
            g.setIsPostage(false);
        else
            g.setIsPostage(true);
        if (data.getSort() != null)
            g.setSort(data.getSort());
        else
            g.setSort(0);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setEnabled(true);
        g.setIdDel(false);
        g.setRowVension(1);
        MktGoods put = goodsDao.put(g);
        if (flag)
            goodsMap.put(data.getTitle(), put);
        data.setGoods(put.getPkey());
        data.setPoint(0);
        data.setComm(BigDecimal.ZERO);
        return put;
    }
    
    @Transactional
    public void processExcelData(List<GoodsMarketExcel> data, Map<String, MktGoodsSpace> spaceMap)
    {
        List<MktGoodsSpace> insSpace = new ArrayList<>();
        List<MktGoodsSpace> updSpace = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        Map<Integer, String> goodsMap = new HashMap<>();
        for (GoodsMarketExcel ge : data)
        {
            MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, ge);
            space.setAscription(CurrentSession.ascriptionPkey());
            if (ge.getPkey() == null)
            {
                goodsMap.put(ge.getGoods(), ge.getTitle());
                insSpace.add(space);
            }
            else
            {
                updSpace.add(space);
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(ge.getPkey());
                if (updaSpaceModel.getKcNum().intValue() != ge.getKcNum().intValue())
                {
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.INVENTORY);
                    add.setGoods(ge.getGoods());
                    add.setGoodsName(ge.getTitle());
                    add.setSpace(ge.getPkey());
                    add.setSpaceName(ge.getSpace());
                    add.setNum(ge.getKcNum() - updaSpaceModel.getKcNum());
                    add.setActualNum(ge.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
            }
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(insSpace);
        for (MktGoodsSpace gs : addAll)
        {
            if (goodsMap.containsKey(gs.getGoods()))
            {
                String title = goodsMap.get(gs.getGoods());
                MktWareLine add = new MktWareLine();
                add.setWareType(WareType.WAREHOUSING);
                add.setGoods(gs.getGoods());
                add.setGoodsName(title);
                add.setSpace(gs.getPkey());
                add.setSpaceName(gs.getSpace());
                add.setNum(gs.getKcNum());
                add.setActualNum(gs.getKcNum());
                add.setAscription(CurrentSession.ascriptionPkey());
                addWareLineAll.add(add);
            }
        }
        List<MktGoodsSpace> updateAll = goodsSpaceDao.updateAll(updSpace);
        wareLineDao.addAll(addWareLineAll);
        addAll.addAll(updateAll);
        List<Integer> keys = new ArrayList<>();
        List<Integer> updKeys = new ArrayList<>();
        Map<Integer, Boolean> map = new HashMap<>();
        Map<Integer, Boolean> unMap = new HashMap<>();
        for (MktGoodsSpace s : addAll)
        {
            if (s.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
            {
                map.put(s.getGoods(), true);
            }
            else
                unMap.put(s.getGoods(), false);
        }
        for (Integer key : map.keySet())
        {
            if (!unMap.containsKey(key))
                keys.add(key);
        }
        unMap.keySet().forEach(e -> updKeys.add(e));
        if (!keys.isEmpty())
        {
            List<MktGoods> exec = goodsDao.select().in("pkey", keys.toArray()).exec();
            for (MktGoods g : exec)
            {
                g.setExtendCon("member");
            }
            goodsDao.updateAll(exec);
        }
        if (!updKeys.isEmpty())
        {
            List<MktGoods> exec = goodsDao.select().in("pkey", updKeys.toArray()).exec();
            for (MktGoods g : exec)
            {
                g.setExtendCon(null);
            }
            goodsDao.updateAll(exec);
        }
    }
    
    public Boolean updRichTemp(String content)
    {
        String marketPkey = CurrentSession.marketPkey();
        MktRichTemplate exec =
            richTemplateDao.selectOne().eq("farmer", marketPkey).eq("type", RichType.GOODS_TEMPLATE).exec();
        if (exec == null)
        {
            exec = new MktRichTemplate();
            exec.setFarmer(marketPkey);
            exec.setType(RichType.GOODS_TEMPLATE);
            exec.setAscription(CurrentSession.ascriptionPkey());
        }
        exec.setContent(content);
        richTemplateDao.put(exec);
        return true;
    }
    
    public String getRichTemp()
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        MktRichTemplate exec = richTemplateDao.selectOne()
            .eq("farmer", marketPkey)
            .eq("ascription", ascriptionPkey)
            .eq("type", RichType.GOODS_TEMPLATE)
            .exec();
        if (exec != null)
            return exec.getContent();
        return "";
    }
    
    @Transactional(rollbackOn = Throwable.class)
    public Boolean enableGuessLike(Integer pkey)
    {
        MktGoods mktGoods = goodsDao.get(pkey);
        // 一堆校验
        if (Objects.isNull(mktGoods))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE);
        }
        if (mktGoods.getIdDel())
        {
            throw TofocusException.of(LejiaErrCode.GOODS_IS_DELETED);
        }
        // 原先的状态
        Boolean guessLike = mktGoods.getGuessLike();
        // 原先“猜我喜欢”状态是关闭时，商品未启用，不允许打开“猜我喜欢”
        // 原先“猜我喜欢”状态是开启时, 商品未启用，可以关闭“猜我喜欢” <--不限制就能关闭
        if (guessLike.equals(false) && mktGoods.getEnabled().equals(false))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
        }
        // 修改状态
        mktGoods.setGuessLike(!guessLike);
        goodsDao.update(mktGoods);
        return true;
    }
    
    private void delSupply(List<MktGoodsSpace> delSpace)
    {
        List<Integer> keys = new ArrayList<>();
        delSpace.forEach(e -> keys.add(e.getPkey()));
        if (keys.isEmpty())
            return;
        List<MktSupply> del = supplyDao.select().in("space", keys).exec();
        supplyDao.removeAll(del);
        log.info("更新商品，删除的规格对应的供应库删除,删除数量： {}", del.size());
    }
    
    private void delSupply(Integer goods)
    {
        List<MktSupply> del = supplyDao.select().eq("good", goods).exec();
        supplyDao.removeAll(del);
        log.info("删除商品，删除对应的供应库删除,删除数量： {}", del.size());
    }
    
    public boolean enableZoneRecommend(Integer pkey, Boolean enabled)
    {
        String farmerPkey = CurrentSession.marketPkey();
        if (StringUtil.isBlank(farmerPkey))
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        MktGoods bean = goodsDao.get(pkey);
        if (bean == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到商品");
        if (CurrentSession.marketPkey() == null || !farmerPkey.equals(bean.getFarmer()))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        if (Boolean.TRUE.equals(enabled))
        {
            if (!Boolean.TRUE.equals(bean.getEnabled()))
                throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
            int num = goodsDao.countGoodsZoneRecommend(bean.getMType(), bean.getFarmer(), bean.getAscription());
            if (num >= maxRecommend)
                throw TofocusException.of(LejiaErrCode.GOODS_RECOMMEND_MAX);
        }
        goodsDao.updGoodsZoneRecommend(pkey, enabled);
        return true;
    }
    
    public String getZoneDisplayName(MType mType)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmerPkey = CurrentSession.marketPkey();
        if (ascription == null || farmerPkey == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        switch (mType)
        {
            case INTEGRAL_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                return config.getIntegralDisplayName();
            }
            case INTEGRAL_MSD_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                return config.getIntegralMsdDisplayName();
            }
            case INTEGRAL_PRESALE_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                return config.getIntegralPresaleDisplayName();
            }
            case INTEGRAL_BNYP_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                return config.getIntegralBNYPDisplayName();
            }
            case SPECIAL_GOODS:
            {
                FarmerGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(FarmerGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new FarmerGoodsZoneConfig();
                return config.getSpecialDisplayName();
            }
            default:
                throw TofocusException.of(SysErrCode.UNIMPLENT_FUNCTION);
        }
    }
    
    public boolean setZoneDisplayName(MType mType, String displayName)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String companyPkey = CurrentSession.companyPkey();
        String farmerPkey = CurrentSession.marketPkey();
        if (ascription == null || farmerPkey == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        switch (mType)
        {
            case INTEGRAL_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                config.setIntegralDisplayName(displayName);
                dynamicAttributeDao.setFarmerAttribute(config, ascription, companyPkey, farmerPkey);
                break;
            }
            case INTEGRAL_MSD_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                config.setIntegralMsdDisplayName(displayName);
                dynamicAttributeDao.setFarmerAttribute(config, ascription, companyPkey, farmerPkey);
                break;
            }
            case INTEGRAL_PRESALE_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                config.setIntegralPresaleDisplayName(displayName);
                dynamicAttributeDao.setFarmerAttribute(config, ascription, companyPkey, farmerPkey);
                break;
            }
            case INTEGRAL_BNYP_GOODS:
            {
                AscriptionGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new AscriptionGoodsZoneConfig();
                config.setIntegralBNYPDisplayName(displayName);
                dynamicAttributeDao.setFarmerAttribute(config, ascription, companyPkey, farmerPkey);
                break;
            }
            case SPECIAL_GOODS:
            {
                FarmerGoodsZoneConfig config =
                    dynamicAttributeDao.getFarmerAttribute(FarmerGoodsZoneConfig.class, ascription, farmerPkey);
                if (config == null)
                    config = new FarmerGoodsZoneConfig();
                config.setSpecialDisplayName(displayName);
                dynamicAttributeDao.setFarmerAttribute(config, ascription, companyPkey, farmerPkey);
                break;
            }
            default:
                throw TofocusException.of(SysErrCode.UNIMPLENT_FUNCTION);
        }
        return true;
    }
    
    public PageResult<GoodsRecommendOnPage> queryRecommendGoods(Integer page, Integer pagesize, Integer sourceGoods,
        String goodsFarmer, MType mType, String vendorName, String title, GoodsRecommendZone zone)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        if (currentFarmer == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        List<Integer> vendors = new ArrayList<>();
        List<Integer> suppliers = new ArrayList<>();
        if (StringUtil.isNotBlank(vendorName))
        {
            vendors = vendorDao.byNameAndBooth(vendorName, null, null, ascription);
            suppliers = supplierDao.findPkeys(ascription, vendorName, null);
            if (CollectionUtil.isEmpty(vendors) && CollectionUtil.isEmpty(suppliers))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        SelectPageOps builder = goodsRecommendDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktGoodsRecommend.F.pkey)
            .as(MktGoodsRecommend.F.goods)
            .as(MktGoodsRecommend.F.sort)
            .as(MktGoodsRecommend.F.goodsFarmer)
            .as(MktGoodsRecommend.F.ascription)
            .eq(MktGoodsRecommend.F.ascription, ascription)
            .eq(MktGoodsRecommend.F.farmer, currentFarmer)
            .eq(MktGoodsRecommend.F.goodsFarmer, goodsFarmer);
        // 传值则查该商品关联推荐商品，为空则查运营端配置推荐商品
        if (sourceGoods == null)
            builder.isNull(MktGoodsRecommend.F.sourceGoods);
        else
            builder.eq(MktGoodsRecommend.F.sourceGoods, sourceGoods);
        if (zone != null)
            builder.join(MktGoodsRecommendZone.class, MktGoodsRecommend.F.pkey, MktGoodsRecommendZone.F.goodsRecommend)
                .eq(MktGoodsRecommendZone.F.zone, zone);
        return builder.join(MktGoods.class, MktGoodsRecommend.F.goods, MktGoods.F.pkey)
            .as(MktGoods.F.mType)
            .as(MktGoods.F.vendor)
            .as(MktGoods.F.supplier)
            .as(MktGoods.F.title)
            .as(MktGoods.F.photo1)
            .as(MktGoods.F.enabled)
            .eq(MktGoods.F.mType, mType)
            .like(MktGoods.F.title, title)
            .or()
                .in(MktGoods.F.vendor, vendors)
                .in(MktGoods.F.supplier, suppliers)
            .close()
            .done()
            .endJoin()
            .exec(GoodsRecommendOnPage.class);
    }
    
    public GoodsRecommendInfo getRecommendGoods(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        if (currentFarmer == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        // 运营端
        if (currentFarmer.startsWith(Constant.Operation))
            return goodsRecommendDao.get4Ascription(pkey, ascription, GoodsRecommendInfo.class);
        // 市场端
        else
            return goodsRecommendDao.get4Farmer(pkey, currentFarmer, ascription, GoodsRecommendInfo.class);
    }
    
    @Transactional(rollbackOn = Exception.class)
    public boolean saveRecommendGoods(GoodsRecommendInfo info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        if (currentFarmer == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        SysFarmer goodsFarmer = farmerDao.get(info.getGoodsFarmer());
        if (goodsFarmer == null || !Objects.equals(goodsFarmer.getAscription(), ascription))
            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
        
        MktGoods goods = goodsDao.getGoods(info.getGoods());
        if (goods == null)
            throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE);
        
        // 运营端
        MktGoodsRecommend bean;
        if (currentFarmer.startsWith(Constant.Operation))
        {
            if (StringUtil.isBlank(info.getGoodsFarmer()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "市场不能为空");
            if (!info.getGoodsFarmer().equals(goods.getFarmer()))
                throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
            // 新增
            if (info.getPkey() == null)
            {
                if (info.getSourceGoods() != null)
                {
                    MktGoods sourceGoods = goodsDao.getGoods(info.getSourceGoods());
                    if (sourceGoods == null)
                        throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE, "来源商品不存在");
                    if (!currentFarmer.equals(sourceGoods.getFarmer()))
                        throw TofocusException.of(LejiaErrCode.GOODS_ERROR, "来源商品找不到");
                }
                bean = new MktGoodsRecommend();
                bean.setGoods(info.getGoods());
                bean.setSourceGoods(info.getSourceGoods());
                bean.setGoodsFarmer(info.getGoodsFarmer());
                bean.setFarmer(currentFarmer);
                bean.setCompany(goods.getCompany());
                bean.setAscription(ascription);
            }
            // 编辑
            else
            {
                bean = goodsRecommendDao.get4Ascription(info.getPkey(), ascription, MktGoodsRecommend.class);
                if (bean == null)
                    throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到推荐商品记录");
            }
        }
        // 市场端
        else
        {
            if (!currentFarmer.equals(goods.getFarmer())
                && !(Constant.Operation + ascription).equals(goods.getFarmer()))
                throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
            // 新增
            if (info.getPkey() == null)
            {
                if (info.getSourceGoods() == null)
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "来源商品不能为空");
                MktGoods sourceGoods = goodsDao.getGoods(info.getSourceGoods());
                if (sourceGoods == null)
                    throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE, "来源商品不存在");
                if (!currentFarmer.equals(sourceGoods.getFarmer()))
                    throw TofocusException.of(LejiaErrCode.GOODS_ERROR, "来源商品找不到");
                bean = new MktGoodsRecommend();
                bean.setGoods(info.getGoods());
                bean.setGoodsFarmer(info.getGoodsFarmer());
                bean.setSourceGoods(info.getSourceGoods());
                bean.setFarmer(currentFarmer);
                bean.setCompany(goods.getCompany());
                bean.setAscription(ascription);
            }
            // 编辑
            else
            {
                bean = goodsRecommendDao.get4Farmer(info.getPkey(), currentFarmer, ascription, MktGoodsRecommend.class);
                if (bean == null)
                    throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到推荐商品记录");
            }
        }
        if (goodsRecommendDao.isGoodsRepeat(bean
            .getAscription(), bean.getFarmer(), bean.getPkey(), bean.getGoods(), bean.getSourceGoods()))
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品不允许重复添加");
        bean.setSort(info.getSort());
        if (bean.getSort() == null)
        {
            int maxSort = goodsRecommendDao.maxSort(ascription, currentFarmer, bean.getPkey(), bean.getSourceGoods());
            bean.setSort(maxSort + 1);
        }
        bean = goodsRecommendDao.put(bean);
        // 处理推荐区域
        goodsRecommendZoneDao.removeByGoodsRecommend(bean.getPkey());
        List<MktGoodsRecommendZone> zoneList = new ArrayList<>();
        if (bean.getSourceGoods() == null)
        {
            for (GoodsRecommendZone zone : info.getZones())
            {
                MktGoodsRecommendZone zoneBean = new MktGoodsRecommendZone();
                zoneBean.setPkey(bean.getPkey(), zone);
                zoneBean.setFarmer(bean.getFarmer());
                zoneBean.setCompany(bean.getCompany());
                zoneBean.setAscription(bean.getAscription());
                zoneList.add(zoneBean);
            }
        }
        else
        {
            MktGoodsRecommendZone zoneBean = new MktGoodsRecommendZone();
            zoneBean.setPkey(bean.getPkey(), GoodsRecommendZone.GOODS_DETAIL);
            zoneBean.setFarmer(bean.getFarmer());
            zoneBean.setCompany(bean.getCompany());
            zoneBean.setAscription(bean.getAscription());
            zoneList.add(zoneBean);
        }
        goodsRecommendZoneDao.putAll(zoneList);
        return true;
    }
    
    @Transactional(rollbackOn = Exception.class)
    public boolean delRecommendGoods(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        if (currentFarmer == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        MktGoodsRecommend bean;
        // 运营端
        if (currentFarmer.startsWith(Constant.Operation))
            bean = goodsRecommendDao.get4Ascription(pkey, ascription, MktGoodsRecommend.class);
        // 市场端
        else
            bean = goodsRecommendDao.get4Farmer(pkey, currentFarmer, ascription, MktGoodsRecommend.class);
        if (bean == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到推荐商品记录");
        goodsRecommendZoneDao.removeByGoodsRecommend(bean.getPkey());
        goodsRecommendDao.remove(bean);
        return true;
    }
    
    public PageResult<GoodsAdvertOnInfo> queryAdvertGoods(int page, int pagesize, String farmer, String title)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        PageResult<GoodsAdvertOnInfo> result = goodsDao.selectPage().page(page).pagesize(pagesize)
        .in("farmer", farmer, currentFarmer, Constant.Operation + ascription)
        .like("title", title)
//        .notEq("mType", MType.SPECIAL_GOODS)
        .notEq("mType", MType.SHARE_GOODS)
        .notEq("mType", MType.CUT_GOODS)
        .notEq("mType", MType.COLLAGE_GOODS)
        .notEq("mType", MType.POVERTY_ALLEVIATION_GOODS)
        .notEq("mType", MType.PROCESS_GOODS)
        .notEq("mType", MType.BOX_GOODS)
        .eq("idDel", false).sort("sort", true).sort("pkey", true)
        .execDto(GoodsAdvertOnInfo.class);
        for(GoodsAdvertOnInfo d : result.getContent())
        {
            d.setMTypeName(d.getMType().getName());
            if(d.getMType().equals(MType.INTEGRAL_GOODS) && ascription.equals(13))
                d.setMTypeName("滨海自营");
        }
        return result;
    }
}
