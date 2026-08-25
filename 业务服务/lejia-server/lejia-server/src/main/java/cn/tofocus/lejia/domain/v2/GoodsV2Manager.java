package cn.tofocus.lejia.domain.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponExportExcel;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.bean.dto.market.CardUpDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.dto.v2.goods.PresaleTimeOnInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class GoodsV2Manager
{
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    public PresaleTimeOnInfo getPresaleTime()
    {
        PresaleTimeOnInfo res = new PresaleTimeOnInfo();
        SysFarmer farmer = MobileSession.farmer();
        SysFarmerConfig config = farmer.getConfig();
        Date now = new Date();
        String yytb = config.getYytb();
        String yyte = config.getYyte();
        
        // 距离今天结束时间
        if (StringUtils.isBlank(yyte) || yyte.length() != 5)
        {
            Date date = DateUtil.atStartOfNextDay(now);
            res.setEndTime(date.getTime() - now.getTime());
        }
        else
        {
            yyte = DateUtil.formatDate(now, "yyyy-MM-dd") + " " + yyte + ":00";
            Date dateStr = DateUtil.formatDateStr(yyte, "yyyy-MM-dd HH:mm:ss");
            long time = dateStr.getTime() - now.getTime();
            if (time < 0) time = 0l;
            res.setEndTime(time);
        }
        
        // 距离明天开售时间
        Date date = DateUtil.atStartOfNextDay(now);
        if (StringUtils.isBlank(yytb) || yytb.length() != 5)
        {
            res.setStartTime(date.getTime() - now.getTime());
        }
        else
        {
            yytb = DateUtil.formatDate(date, "yyyy-MM-dd") + " " + yytb + ":00";
            Date dateStr = DateUtil.formatDateStr(yytb, "yyyy-MM-dd HH:mm:ss");
            long time = dateStr.getTime() - now.getTime();
            if (time < 0) time = 0l;
            res.setStartTime(time);
        }
        return res;
    }
    
    @Transactional(rollbackOn = Throwable.class)
    public Integer insGoodsCoupon(GoodsCouponInfo entity)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktGoods mktGoods = BeanUtil.beanFrom(MktGoods.class, entity);
        mktGoods.setMType(MType.COUPON_GOODS);
        MktGtype gtype = gtypeDao.getCouponGtype(ascription);
        if (gtype == null) throw TofocusException.of(LejiaErrCode.GTYPE_NOT_COUPON);
        // 校验 分类是否在这个分类下面
        if (goodsMainDao.checkGtype(gtype.getPkey(), entity.getGoodsMain()))
            throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODSMAIN);
        String marketPkey = CurrentSession.marketPkey();
        if (goodsDao.checkTitleRepeat(entity.getTitle(), MType.COUPON_GOODS, marketPkey, null))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        mktGoods.setGtype(gtype.getPkey());
        mktGoods.setFarmer(marketPkey);
        mktGoods.setCompany(CurrentSession.companyPkey());
        mktGoods.setAscription(ascription);
        mktGoods.setRowVension(3);
        mktGoods.setIdDel(false);
        mktGoods.setViewCount(0);
        mktGoods.setXsNum(0);
        mktGoods.setGuessLike(false);
        mktGoods.setGuessSort(0);
        mktGoods.setIsPostage(true);
        if (System.currentTimeMillis() < mktGoods.getStartDate().getTime())
        {
            mktGoods.setEnabled(false);
        }
        else
        {
            mktGoods.setEnabled(true);
        }
        if (entity.getEndDate() == null)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(2030, 1, 1);
            mktGoods.setEndDate(cal.getTime());
        }
        if (entity.getSort() == null)
        {
            mktGoods.setSort(0);
        }
        if (entity.getPurchaseNum() == null)
        {
            mktGoods.setPurchaseNum(0);
        }
        MktGoods add = goodsDao.add(mktGoods);
        Integer goodsPkey = add.getPkey();
        MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, entity.getSpace());
        space.setPriceOld(space.getPrice());
        space.setPriceMember(BigDecimal.ZERO);
        space.setWeight(BigDecimal.ZERO);
        space.setGoods(goodsPkey);
        space.setXsNum(0);
        space.setAscription(ascription);
        MktGoodsSpace spaceAdd = goodsSpaceDao.add(space);
        
        MktSpaceKc kc = BeanUtil.beanFrom(MktSpaceKc.class, spaceAdd);
        spaceKcDao.add(kc);
        spaceKcCache.set(String.valueOf(kc.getPkey()), (long)kc.getKcNum());
        add.setPrice(space.getPrice());
        wareManager.insWare(add, Arrays.asList(spaceAdd));
        
        // ************以下处理卡券
        MktCardInsDTO card = new MktCardInsDTO();
        BeanUtils.copyProperties(entity, card, "content", "startDate", "endDate", "title");
        card.setStartDate(entity.getCardStartDate());
        card.setEndDate(entity.getCardEndDate());
        card.setTitle(space.getSpace());
        card.setCount(space.getKcNum());
        card.setCardType(CardType.INTEGRAL_BUY);
        MktCardOnList insCard = cardManager.insCard(card);
        add.setExtendCon(String.valueOf(insCard.getPkey()));
        goodsDao.put(add);
        return goodsPkey;
    }
    
    @Transactional(rollbackOn = Throwable.class)
    public Integer updGoodsCoupon(GoodsCouponInfo entity)
    {
        Integer goodsPkey = entity.getPkey();
        MktGoods mktGoods = goodsDao.get(goodsPkey);
        if (mktGoods == null) throw TofocusException.of(WsaleErrCode.NOT_GOODS);
        if (mktGoods.getEnabled()) throw TofocusException.of(WsaleErrCode.GOODS_CANNOT_EDIT);
        if (cardDao.checkInvalid(Integer.valueOf(mktGoods.getExtendCon())))
            throw TofocusException.of(WsaleErrCode.CARD_INVALID);
        mktGoods.setMType(MType.COUPON_GOODS);
        MktGtype gtype = gtypeDao.getCouponGtype(CurrentSession.ascriptionPkey());
        if (gtype == null) throw TofocusException.of(LejiaErrCode.GTYPE_NOT_COUPON);
        // 校验 分类是否在这个分类下面
        if (goodsMainDao.checkGtype(gtype.getPkey(), entity.getGoodsMain()))
            throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODSMAIN);
        String marketPkey = CurrentSession.marketPkey();
        if (goodsDao.checkTitleRepeat(entity.getTitle(), MType.COUPON_GOODS, marketPkey, goodsPkey))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        
        if (entity.getSort() == null) throw TofocusException.of(WsaleErrCode.SORT_NOT_EMPTY);
        BeanUtils.copyProperties(entity, mktGoods, "viewCount", "extendCon");
        if (entity.getPurchaseNum() == null) mktGoods.setPurchaseNum(0);
        MktGoodsSpaceOnList space = entity.getSpace();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        MktGoodsSpace updSpace = goodsSpaceDao.get(space.getPkey());
        if (updSpace.getKcNum().intValue() != space.getKcNum().intValue())
        {
            MktWareLine add = new MktWareLine();
            add.setWareType(WareType.INVENTORY);
            add.setGoods(mktGoods.getPkey());
            add.setGoodsName(mktGoods.getTitle());
            add.setSpace(space.getPkey());
            add.setSpaceName(space.getSpace());
            add.setNum(space.getKcNum() - updSpace.getKcNum());
            add.setActualNum(space.getKcNum());
            addWareLineAll.add(add);
        }
        BeanUtils.copyProperties(space, updSpace, "xsNum");
        if (updSpace.getWeight() == null) updSpace.setWeight(BigDecimal.ZERO);
        
        if (mktGoods.getSort() == null) mktGoods.setSort(0);
        mktGoods.setPrice(updSpace.getPrice());
        goodsDao.update(mktGoods);
        // 要先更新规格前,将库存更新
        MktSpaceKc kc = BeanUtil.beanFrom(MktSpaceKc.class, updSpace);
        spaceKcDao.add(kc);
        spaceKcCache.set(String.valueOf(kc.getPkey()), (long)kc.getKcNum());
        if (!addWareLineAll.isEmpty())
        {
            wareLineDao.addAll(addWareLineAll);
        }
        goodsSpaceDao.update(updSpace);
        
        // ***********以下处理 card***********
        CardUpDTO card = new CardUpDTO();
        BeanUtils.copyProperties(entity, card, "content", "startDate", "endDate", "title");
        card.setPkey(Integer.valueOf(mktGoods.getExtendCon()));
        card.setStartDate(entity.getCardStartDate());
        card.setEndDate(entity.getCardEndDate());
        card.setTitle(space.getSpace());
        card.setCount(space.getKcNum());
        cardManager.updCard(card);
        return goodsPkey;
    }
    
    @Transactional
    public Boolean invalidGoodsCoupon(Integer pkey)
    {
        MktGoods goods = goodsDao.get(pkey);
        Integer cardPkey = Integer.valueOf(goods.getExtendCon());
        if (cardDao.checkInvalid(cardPkey)) throw TofocusException.of(WsaleErrCode.CARD_INVALID);
        goods.setEnabled(false);
        goodsDao.update(goods);
        return cardManager.invalidCard(cardPkey);
    }
    
    public GoodsCouponInfo getGoods(Integer pkey)
    {
        MktGoods mktGoods = goodsDao.getGoods(pkey);
        if (mktGoods == null) return null;
        MktGoodsSpaceOnList exec = goodsSpaceDao.selectOne().eq("goods", pkey).execDto(MktGoodsSpaceOnList.class);
        GoodsCouponInfo result = BeanUtil.beanFrom(GoodsCouponInfo.class, mktGoods);
        result.setSpace(exec);
        MktCardOnList cardOnList = cardManager.getCard(Integer.valueOf(mktGoods.getExtendCon()));
        BeanUtils.copyProperties(cardOnList, result, "pkey", "title", "startDate", "endDate", "content");
        result.setCardStartDate(cardOnList.getStartDate());
        result.setCardEndDate(cardOnList.getEndDate());
        return result;
    }
    
    public PageResult<GoodsCouponOnPage> queryGoods(Integer page, Integer pagesize, String title, Integer goodsMain,
        Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<GoodsCouponOnPage> pageResult =
            goodsDao.queryCoupon(page, pagesize, title, goodsMain, enabled, GoodsCouponOnPage.class, ascription);
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
        return pageResult;
    }
    
    public List<GoodsCouponExportExcel> queryGoodsExcel(String title, Integer goodsMain, Boolean enabled)
    {
        
        PageResult<GoodsCouponOnPage> pageResult = queryGoods(0, 100000, title, goodsMain, enabled);
        Map<Integer, MktGoodsMain> map = goodsMainDao.getAllMap(CurrentSession.ascriptionPkey());
        List<GoodsCouponExportExcel> res = new ArrayList<>();
        for (GoodsCouponOnPage c : pageResult.getContent())
        {
            GoodsCouponExportExcel dto = BeanUtil.beanFrom(GoodsCouponExportExcel.class, c);
            if (map.containsKey(c.getGoodsMain())) dto.setGooodsMainName(map.get(c.getGoodsMain()).getName());
            res.add(dto);
        }
        return res;
    }
    
    public Boolean enabledGoods(List<Integer> pkeys, Boolean flag)
    {
        List<MktGoods> list = goodsDao.select().in("pkey", pkeys).exec();
        // 商品校验
        if (Objects.isNull(list) || list.isEmpty())
        {
            throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE);
        }
        if (flag)
        {
            for (MktGoods mktGoods : list)
            {
                Date startDate = mktGoods.getStartDate();
                if (startDate != null && new Date().getTime() < startDate.getTime())
                    throw TofocusException.of(WsaleErrCode.GOODS_NOT_AVAILABLE);
                
                if(mktGoods.getEndDate() != null) 
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
                mktGoods.setEnabled(flag);
            }
        }
        else
        {
            for (MktGoods mktGoods : list)
            {
                // 商品下架，同时关闭“猜我喜欢”
                mktGoods.setGuessLike(false);
                mktGoods.setZoneRecommend(false);
                mktGoods.setEnabled(flag);
            }
            
        }
        goodsDao.updateAll(list);
        goodListQueryer.resetAll(CurrentSession.marketPkey(), null);
        return true;
    }
}
