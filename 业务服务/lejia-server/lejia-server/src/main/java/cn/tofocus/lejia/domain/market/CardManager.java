package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.page.PageSort;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.app.AppCardDTO;
import cn.tofocus.lejia.bean.dto.app.AppWxErrMsgDTO;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;
import cn.tofocus.lejia.bean.dto.excel.market.ExportMktMemberCardUse;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.market.CardUpDTO;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCouponLinshi;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.cache.CardLinshiMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktAccessLogDao;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCouponLinshiDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Slf4j
@Component
public class CardManager
{
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktAccessLogDao accessLogDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private MktMemberCouponLinshiDao memberCouponLinshiDao;
    
    @Value("${tofocus.zyysc.huodong.linshi:87}")
    private Integer linshiCard;
    
    @Autowired
    private CardLinshiMap cardLinshiMap;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;

    public MktCardOnList insCard(MktCardInsDTO entity)
    {
        String marketPkey = CurrentSession.marketPkey();
        MktCard card = BeanUtil.beanFrom(MktCard.class, entity);
        if (StringUtils.isBlank(entity.getUserFarmer())) card.setUserFarmer(null);
        card.setRowVension(1);
        card.setCardCode(NumberUtils.createCardCode());
        card.setFarmer(marketPkey);
        card.setCompany(CurrentSession.companyPkey());
        card.setAscription(CurrentSession.ascriptionPkey());
        card.setIdDel(false);
        card.setInvalid(false);
        card.setEnabled(true);
        card.setIssuedNum(0);
        card.setUsedNum(0);
        if(card.getCost() == null)
            card.setCost(BigDecimal.ZERO);
        if(card.getType() == null)
            card.setType(CardCouponType.GOODS_COUPON);
        if(card.getAvoidPostage() == null)
            card.setAvoidPostage(false);
        if (card.getUserGoods() != null && card.getUserType() != null)
        {
            MktGoods goods = goodsDao.get(card.getUserGoods());
            if (goods == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
            if (!goods.getGtype().equals(card.getUserType()))
                throw TofocusException.of(LejiaErrCode.GOODS_GTYPE_ERROR);
        }
        if (cardDao.selectOne().eq("cardCode", card.getCardCode()).exec() != null)
            throw TofocusException.of(WsaleErrCode.TRY_AGAIN);
        MktCard add = cardDao.add(card);
        if (entity.getExpireChoose())
        {
            card.setEndDate(null);
            card.setStartDate(null);
        }
        else
            card.setEffective(null);
        MktCardOnList result = BeanUtil.beanFrom(MktCardOnList.class, add);
        if(entity.getTagKeys() != null && !entity.getTagKeys().isEmpty())
        {
            tagManager.putTagVisibles(TagVisibleTargetType.CARD, add.getPkey().longValue(), entity.getTagKeys(), CurrentSession.ascriptionPkey());
        }
        return result;
    }
    
    public MktCardOnList getCard(Integer pkey)
    {
        MktCard card = cardDao.getCard(pkey);
        if (card == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        MktCardOnList result = BeanUtil.beanFrom(MktCardOnList.class, card);
        assembleCard(Arrays.asList(result));
        return result;
    }
    
    public MktCard getMktCard(Integer pkey)
    {
        MktCard card = cardDao.getCard(pkey);
        if (card == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        return card;
    }
    
    public PageResult<MktCardOnList> queryCard(int page, int pagesize, String title, CardType cardType, Boolean enabled,
        Boolean invalid)
    {
        PageResult<MktCardOnList> result = cardDao.queryCard(page, pagesize, title, cardType, enabled, invalid);
        assembleCard(result.getContent());
        return result;
    }
    
    @Transactional
    public Boolean invalidCard(@RequestParam(value = "pkey") Integer pkey)
    {
        MktCard card = cardDao.get(pkey);
        if (card == null) return false;
        card.setInvalid(true);
        List<MktMemberCard> exec = memberCardDao.select().eq("card", pkey).eq("status", CardStatus.UNUSED).exec();
        for (MktMemberCard mc : exec)
        {
            mc.setInvalid(true);
        }
        memberCardDao.updateAll(exec);
        return true;
    }
    
    public void assembleCard(List<MktCardOnList> list)
    {
        for (MktCardOnList co : list)
        {
            if (StringUtils.isNotBlank(co.getUserFarmer()))
            {
                SysFarmer sf = sysFarmerDao.selectOne().eq("pkey", co.getUserFarmer()).exec();
                co.setUserFarmerName(sf.getName());
                co.setRangUse(sf.getName());
            }
            if (co.getUserType() != null)
            {
                MktGtype mg = gtypeDao.selectOne().in("pkey", co.getUserType()).exec();
                if (mg != null)
                {
                    if (StringUtils.isNotBlank(co.getRangUse()))
                        co.setRangUse(co.getRangUse() + "," + mg.getName());
                    else
                        co.setRangUse(mg.getName());
                    co.setUserTypeName(mg.getName());
                }
            }
            if (co.getUserGoods() != null)
            {
                log.info("assembleCard:goodsDao:pkey: {}", co.getUserGoods());
                MktGoods goods = goodsDao.selectOne().eq("pkey", co.getUserGoods()).exec();
                if (goods != null)
                {
                    if (StringUtils.isNotBlank(co.getRangUse()))
                        co.setRangUse(co.getRangUse() + "," + goods.getTitle());
                    else
                        co.setRangUse(goods.getTitle());
                    co.setUserGoodsName(goods.getTitle());
                }
            }
            if (co.getUserGoodsList() != null && !co.getUserGoodsList().isEmpty())
            {
                List<MktGoods> exec = goodsDao.select().in("pkey", co.getUserGoodsList()).exec();
                if(exec != null && !exec.isEmpty())
                {
                    String ru = co.getRangUse();
                    String ugn = null;
                    for(MktGoods g : exec)
                    {
                        if (StringUtils.isNotBlank(ru))
                            ru = ru + "," + g.getTitle();
                        else
                            ru = g.getTitle();
                        if (StringUtils.isNotBlank(ugn))
                            ugn = ugn + "," + g.getTitle();
                        else
                            ugn = g.getTitle();
                    }
                    co.setRangUse(ru);
                    co.setUserGoodsName(ugn);
                }
            }
            if (co.getUserMtype() != null && !co.getUserMtype().isEmpty())
            {
                List<MktGtype> exec = gtypeDao.select().in("pkey", co.getUserMtype()).exec();
                if(exec != null && !exec.isEmpty())
                {
                    String ru = co.getRangUse();
                    String ugn = null;
                    for(MktGtype g : exec)
                    {
                        if (StringUtils.isNotBlank(ru))
                            ru = ru + "," + g.getName();
                        else
                            ru = g.getName();
                        if (StringUtils.isNotBlank(ugn))
                            ugn = ugn + "," + g.getName();
                        else
                            ugn = g.getName();
                    }
                    co.setRangUse(ru);
                    co.setUserMtypeName(ugn);
                }
            }
            
            if (co.getEffective() != null)
            {
                co.setEffectiveDate(co.getEffectiveDate());
                co.setExpireChoose(true);
            }
            if (co.getEndDate() != null)
            {
                co.setEffectiveDate(DateUtil.formatDate(co.getEndDate(), "yyyy-MM-dd"));
                co.setExpireChoose(false);
            }
            if(MemberVisibleRange.TAG.equals(co.getVisibleRange()))
            {
                List<Integer> tagKeys = new ArrayList<>();
                tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.CARD, co.getPkey().longValue());
                co.setTagKeys(tagKeys);
            }
        }
        
    }
    
    public MktCardOnList updCard(CardUpDTO entity)
    {
        MktCard card = cardDao.get(entity.getPkey());
        BeanUtils.copyProperties(entity, card, "enabled");
        if (StringUtils.isBlank(entity.getUserFarmer())) card.setUserFarmer(null);
        if(card.getType() == null)
            card.setType(CardCouponType.GOODS_COUPON);
        if(card.getAvoidPostage() == null)
            card.setAvoidPostage(false);
        if (entity.getExpireChoose())
        {
            card.setEndDate(null);
            card.setStartDate(null);
        }
        else
            card.setEffective(null);
        MktCard update = cardDao.update(card);
        if(entity.getTagKeys() != null && !entity.getTagKeys().isEmpty())
        {
            tagManager.putTagVisibles(TagVisibleTargetType.CARD, update.getPkey().longValue(), entity.getTagKeys(), CurrentSession.ascriptionPkey());
        }
        return BeanUtil.beanFrom(MktCardOnList.class, update);
    }
    
    public Boolean delCard(Integer pkey)
    {
        MktCard card = cardDao.getCard(pkey);
        if (card == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (card.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        card.setIdDel(true);
        cardDao.update(card);
        return true;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean enabledCard(Integer pkey, Boolean flag)
    {
        MktCard card = cardDao.getCard(pkey);
        card.setEnabled(flag);
        cardDao.update(card);
        if(!flag)
        {
            MktAppConfig appConfig = appConfigManager.getAppConfig();
            if(appConfig.getNewcomerCard() != null)
            {
                List<Map<String,Integer>> list = appConfig.getNewcomerCard();
                List<Map<String,Integer>> newcomerCard = new ArrayList<>();
                for(Map<String,Integer> e : list)
                {
                    Integer cardKey = e.get("newcomerCard");
                    if(cardKey != null && !cardKey.equals(pkey))
                        newcomerCard.add(e);
                }
                appConfig.setNewcomerCard(newcomerCard);
                appConfigDao.update(appConfig);
            }
        }
        return false;
    }
    
    
    /**
     * 设置MktMemberCard
     * @param member  微信用户pkey
     * @param card    mkt_card的pkey
     * @param mktCard mkt_card对象
     */
    public MktMemberCard setMktMemberCard(Integer member, Integer card, MktCard mktCard)
    {
        MktMemberCard entity = new MktMemberCard();
        BeanUtils.copyProperties(mktCard, entity, "pkey", "createdTime");
        entity.setStatus(CardStatus.UNUSED);
        entity.setMember(member);
        entity.setCard(card);
        entity.setCost(mktCard.getCost());
        entity.setCardNumber(numberUtils.createCardNumber());
        entity.setEndDate(getEndDate(mktCard));
        entity.setInvalid(mktCard.getInvalid());
        entity.setIsRead(false);
        return entity;
    }
    
    private List<MktMemberCard> setMktMemberCard(Integer member, Integer card, MktCard mktCard, Integer num)
    {
        List<MktMemberCard> res = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            MktMemberCard entity = new MktMemberCard();
            BeanUtils.copyProperties(mktCard, entity, "pkey", "createdTime");
            entity.setStatus(CardStatus.UNUSED);
            entity.setMember(member);
            entity.setCard(card);
            entity.setCost(mktCard.getCost());
            entity.setCardNumber(numberUtils.createCardNumber());
            entity.setEndDate(getEndDate(mktCard));
            entity.setInvalid(mktCard.getInvalid());
            entity.setIsRead(false);
            res.add(entity);
        }
        return res;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean insMemberCard(Integer member, Integer card)
    {
        if(card == null)
            return true;
        MktCard mktCard = cardDao.get(card);
        if (Objects.isNull(mktCard))
        {
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        }
        // 数量校验
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum() == null ? 0 : mktCard.getIssuedNum();
        if (count == 0 || (count - issuedNum) == 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
//        // 设置数量
//        else
//        {
//            mktCard.setCount(count - 1);
//        }
        mktCard.setIssuedNum(issuedNum + 1); 
        cardDao.update(mktCard);
        MktMemberCard entity = setMktMemberCard(member, card, mktCard);
        MktMemberCard add = memberCardDao.add(entity);
        return add != null;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean insMemberCard(Integer member, Integer card, Integer num, String orderNumber)
    {
        if(card == null)
            return true;
        MktCard mktCard = cardDao.get(card);
        if (Objects.isNull(mktCard))
        {
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        }
        // 数量校验
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum() == null ? 0 : mktCard.getIssuedNum();
        if (count == 0 || (count - issuedNum) == 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        if((count - issuedNum - num) <= 0)
            num = count - issuedNum;
//        // 设置数量
//        else
//        {
//            mktCard.setCount(count - num);
//        }
        mktCard.setIssuedNum(issuedNum + num); 
        cardDao.update(mktCard);
        List<MktMemberCard> list = setMktMemberCard(member, card, mktCard, num);
        List<MktMemberCard> addAll = memberCardDao.addAll(list);
        
        
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if(StringUtils.isNotBlank(orderNumber))
                    {
                        // 积分商城购买优惠券后 微信发货
                        StringBuilder sb = new StringBuilder();
                        sb.append("优惠券已到账：");
                        sb.append(mktCard.getTitle());
                        String itemDesc = sb.toString();
                        if(itemDesc.length() > 120)
                            itemDesc = itemDesc.substring(0, 120);
                        String openid = null;
                        String mchid = null;
                        SysAscription sysAscription = ascriptionDao.get(mktCard.getAscription());
                        if(sysAscription != null)
                        {
                            mchid = sysAscription.getConfigMchid();
                        }
                        MktMember mktMember = memberDao.get(member);
                        if(mktMember != null)
                            openid = mktMember.getOpenid1();
                        if(openid != null && mchid != null)
                        {
                            wxManager.uploadShippingInfo(null,
                                orderNumber,
                                mchid,
                                itemDesc,
                                3,
                                null,
                                null,
                                null,
                                null,
                                openid,
                                mktCard.getAscription());
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error(e.getMessage());
                    log.error("微信确认收货报错");
                }
            }
            
        }).start();
        
        return addAll != null;
    }

    @Deprecated
    public Boolean insMemberCardLinshi(String code)
    {
        MktMemberCouponLinshi mcl = memberCouponLinshiDao.byCode(code);
        if(mcl == null)
        {
            System.out.println("购买卡券活动有问题");
            return false;
        }
        mcl.setStatus(OrderStatus.CONFIRM_ORDER);
        CardLinshiDto cardLinshiDto = cardLinshiMap.get(mcl.getActivity());
        Integer num = cardLinshiDto.getNum();
        MktMember mktMember = memberDao.byOpenid1(mcl.getOpenid1());
        Integer member = mktMember.getPkey();
        Integer card = mcl.getCard();
        MktCard mktCard = cardDao.get(card);
        if (Objects.isNull(mktCard))
        {
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        }
        // 数量校验
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum() == null ? 0 : mktCard.getIssuedNum();
        if (count == 0 || (count - issuedNum - num) <= 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        
        // 设置数量
        mktCard.setIssuedNum(issuedNum + num);
        cardDao.update(mktCard);
        List<MktMemberCard> list = setMktMemberCard(member, card, mktCard, num);
        List<MktMemberCard> addAll = memberCardDao.addAll(list);
        memberCouponLinshiDao.update(mcl);
        return addAll != null;
    }

    @Deprecated
    public Boolean insMemberCardLinshiFree(String openid, Integer card, String activity, String payNumber)
    {
        MktMemberCouponLinshi mcl = new MktMemberCouponLinshi();
        mcl.setCode(payNumber);
        mcl.setOpenid1(openid);
        mcl.setCard(card);
        mcl.setActivity(activity);
        mcl.setAscription(MobileSession.appid());
        mcl.setStatus(OrderStatus.CONFIRM_ORDER);
        
        CardLinshiDto cardLinshiDto = cardLinshiMap.get(mcl.getActivity());
        Integer num = cardLinshiDto.getNum();
        MktMember mktMember = memberDao.byOpenid1(mcl.getOpenid1());
        Integer member = mktMember.getPkey();
        MktCard mktCard = cardDao.get(card);
        if (Objects.isNull(mktCard))
        {
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        }
        memberCouponLinshiDao.add(mcl);
        
        // 数量校验
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum() == null ? 0 : mktCard.getIssuedNum();
        if (count == 0 || (count - issuedNum - num) <= 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        // 设置数量
        mktCard.setIssuedNum(issuedNum + num);
        cardDao.update(mktCard);
        List<MktMemberCard> list = setMktMemberCard(member, card, mktCard, num);
        List<MktMemberCard> addAll = memberCardDao.addAll(list);
        return addAll != null;
    }

    
    public String gethdNum()
    {
        Map<String, Long> map = memberCouponLinshiDao.aggregation().notEq("status", OrderStatus.UNPAID_ORDER)
        .isNotNull("card")
        .execGroupByCount("activity", "pkey");
        StringBuffer sb = new StringBuffer();
        for(String key : map.keySet())
        {
            String s = key.substring(key.length() - 1);
            sb.append("活动");
            sb.append(s);
            sb.append("当前购买人数:  ");
            sb.append(map.get(key));
            sb.append("位\r");
        }
        return sb.toString();
    }
    
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean insCardList(List<Integer> cardPkeys)
    {
        Integer memberPkey = MobileSession.memberPkey();
        log.info("商城app-一键领取卡券-memberPkey: {}", memberPkey);
        log.info("商城app-一键领取卡券-cardPkeys: {}", cardPkeys);
        if(memberPkey == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        // 获取列表
        List<MktCard> mktCards = cardDao.select().in("pkey", cardPkeys).eq("idDel", false).exec();
        if (CollectionUtils.isEmpty(mktCards))
        {
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        }
        
        // 领取记录
        List<MktMemberCard> mktMemberCards = memberCardDao.select().eq("member", memberPkey).exec();
        // 已领取的pkey
        List<Integer> cardPkeyList = mktMemberCards.stream().map(MktMemberCard::getCard).collect(Collectors.toList());
        
        mktCards.forEach(mktCard -> {
            // 数量校验
            Integer count = mktCard.getCount();
            Integer issuedNum = mktCard.getIssuedNum();
            if (issuedNum == null) issuedNum = 0;
            if ((count - issuedNum) == 0)
            {
                throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY, "选中的卡券有部分已经领取完毕");
            }
            // 是否领取校验
            if (cardPkeyList.contains(mktCard.getPkey()))
            {
                throw TofocusException.of(LejiaErrCode.CARD_IS_RECEIVED, "选中的卡券有部分已经领取过");
            }
            // 设置数量
            else
            {
                mktCard.setIssuedNum(issuedNum + 1);
            }
        });
        cardDao.updateAll(mktCards);
        // 设置领取记录
        List<MktMemberCard> memberCards = new ArrayList<>();
        mktCards.forEach(card -> {
            MktMemberCard memberCard = setMktMemberCard(memberPkey, card.getPkey(), card);
            memberCards.add(memberCard);
        });
        
        memberCardDao.addAll(memberCards);
        Integer size = cardPkeys.size();
        return size.equals(memberCards.size());
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public List<MktMemberCard> insAllCard(Integer status, Integer card, Integer member, Integer ascription)
    {
        if (status == null) throw TofocusException.of(WsaleErrCode.REQUIRED_PARAMETERS_NOT_EMPTY, "必要参数status不能为空");
        if (card == null) throw TofocusException.of(WsaleErrCode.REQUIRED_PARAMETERS_NOT_EMPTY, "必要参数card不能为空");
        List<MktMemberCard> mcList = new ArrayList<>();
        List<MktMember> exec = new ArrayList<>();
        // TODO 根据status的值 获取不同的用户 发放卡券    0:全部     1:年费会员  2:普通会员  3:活跃会员  4:非活跃会员  5:新注册会员  6:老会员  7:从未消费会员  10:发指定用户
        exec = getMemberList(status, exec, member, ascription);
        if (exec.size() <= 0) return null;
        MktCard mktCard = cardDao.getCard(card);
        Date endDate = getEndDate(mktCard);
        // 设置优惠券类型
        //        mktCard.setCardType(CardType.MANUALLY_ISSUE);
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY, "选中的卡券已经领取完毕");
        }
     
        if (endDate == null)
        {
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE, "卡券:" + card + " 查询不到");
        }
        
        // 获取当前卡券的member
        List<Integer> memberPkeys = memberCardDao.select()
            .eq("card", card)
            .exec()
            .stream()
            .map(MktMemberCard::getMember)
            .collect(Collectors.toList());
        // 当前已经领取过该卡券的用户，不再发放
        exec.removeIf(member2 -> memberPkeys.contains(member2.getPkey()));
        int num = count;
        if ((count - issuedNum - exec.size()) <= 0)
        {
            num = count - issuedNum;
        }
        // 用户列表
        for (MktMember bean : exec)
        {
            if(mcList.size() < num)
            {
                log.info("MktMember: {}", bean);
                MktMemberCard entity = new MktMemberCard();
                BeanUtils.copyProperties(mktCard, entity, "pkey");
                entity.setStatus(CardStatus.UNUSED);
                entity.setMember(bean.getPkey());
                entity.setCard(card);
                entity.setCardNumber(numberUtils.createCardNumber());
                entity.setEndDate(endDate);
                entity.setIsRead(false);
                entity.setInvalid(false);
                mcList.add(entity);
            }
        }
        
        mktCard.setIssuedNum(issuedNum + mcList.size());
        // 修改数据
        cardDao.update(mktCard);
        
        List<MktMemberCard> addAll = memberCardDao.addAll(mcList);
//        if (addAll == null || addAll.size() <= 0) throw TofocusException.of(LejiaErrCode.CARD_REPEAT_ERROR);
        
        return addAll;
    }

    public Boolean insAllCardTest(Integer card, String mobile, Integer num, Integer ascription)
    {
        MktMember member = memberDao.selectOne().eq("mobile", mobile).eq("ascription", ascription).exec();
        if(member == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "这个手机号码没有会员");
        MktCard mktCard = cardDao.getCard(card);
        Date endDate = getEndDate(mktCard);
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY, "选中的卡券已经领取完毕");
        }
        
        if (endDate == null)
        {
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE, "卡券:" + card + " 查询不到");
        }
        
        if ((count - issuedNum - num) <= 0)
        {
            num = count - issuedNum;
        }
        List<MktMemberCard> mcList = new ArrayList<>();
        for(int i = 0; i < num; i++)
        {
            // 用户列表
            MktMemberCard entity = new MktMemberCard();
            BeanUtils.copyProperties(mktCard, entity, "pkey");
            entity.setStatus(CardStatus.UNUSED);
            entity.setMember(member.getPkey());
            entity.setCard(card);
            entity.setCardNumber(numberUtils.createCardNumber());
            entity.setEndDate(endDate);
            entity.setIsRead(false);
            entity.setInvalid(false);
            mcList.add(entity);
        }
        mktCard.setIssuedNum(issuedNum + num);
        // 修改数据
        cardDao.update(mktCard);
        memberCardDao.addAll(mcList);
        return true;
    }
    
    // 取过期日期
    private Date getEndDate(MktCard mktCard)
    {
        if (mktCard == null) return null;
        Integer effective = mktCard.getEffective();
        
        if (effective == null)
        {
            Date date = mktCard.getEndDate();
            if (date == null) return null;
            return date;
        }
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, effective);
        date = calendar.getTime();
        return date;
    }
    
    private List<MktMember> getMemberList(Integer status, List<MktMember> exec, Integer member, Integer ascription)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date date = calendar.getTime();
        
        // 根据status的值 获取不同的用户 发放卡券    0:全部     1:年费会员  2:普通会员  3:活跃会员  4:非活跃会员  5:新注册会员  6:老会员  7:从未消费会员  10:发指定用户
        /**
         *  年会会员：开通了年会会员的用户；
                            普通会员：点开了小程序授权的用户；
                            活跃会员：1个月内打开小程序次数大于等于4；
                            非活跃会员：1个月内打开小程序次数小于4；
                            新注册会员： 注册时间小于等于7天的用户
                            老会员：注册时间大于7天的用户；
                            从未消费会员；未有消费记录的用户
         */
        switch (status)
        {
            case 0:
                exec = memberDao.select()
                    .eq("ascription", ascription)
                    .eq("enabled", true)
                    .sort("createdTime", true)
                    .exec();
                break;
            case 1:
                exec = memberDao.select()
                    .eq("ascription", ascription)
                    .eq("enabled", true)
                    .eq("level", LevelType.PAID_MEMBER)
                    .sort("createdTime", true)
                    .exec();
                break;
            case 2:
                exec = memberDao.select()
                    .eq("ascription", ascription)
                    .eq("enabled", true)
                    .eq("level", LevelType.ORDINARY_MEMBER)
                    .sort("createdTime", true)
                    .exec();
                break;
            case 3:
                List<Integer> list = accessLogDao.getMonthMember(true);
                if (list.isEmpty()) break;
                exec = memberDao.select().eq("ascription", ascription).in("pkey", list).exec();
                break;
            case 4:
                List<Integer> keys = accessLogDao.getMonthMember(false);
                if (keys.isEmpty()) break;
                exec = memberDao.select().eq("ascription", ascription).in("pkey", keys).exec();
                break;
            case 5:
                exec = memberDao.select()
                    .eq("ascription", ascription)
                    .eq("enabled", true)
                    .ge("createdTime", date)
                    .sort("createdTime", true)
                    .exec();
                break;
            case 6:
                exec = memberDao.select()
                    .eq("ascription", ascription)
                    .eq("enabled", true)
                    .le("createdTime", date)
                    .sort("createdTime", true)
                    .exec();
                break;
            case 7:
                exec = memberDao.getNotOrder(null, ascription);
                break;
            case 10:
                if (member == null) throw TofocusException.of(WsaleErrCode.CAN_NOT_BE_EMPTY, "微信用户pkey没传递");
                MktMember mktMember =
                    memberDao.selectOne().eq("ascription", ascription).eq("pkey", member).eq("enabled", true).exec();
                if (mktMember == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE, "当前微信用户不存在");
                if (MemberStatus.LOGGED_OUT.equals(mktMember.getStatus()))
                    throw TofocusException.of(WsaleErrCode.MEMBER_LOGGED_OUT);
                exec.add(mktMember);
                break;
        }
        return exec;
    }
    
    public PageResult<MktMemberCardOnList> queryUseCard(int page, int pagesize, String userFarmer, String startTime,
        String endTime, String st, String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        String farmerPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Integer> members = null;
        if (StringUtil.isNotBlank(mobile))
        {
            members = memberDao.listPkeys(ascription, mobile);
            if (CollectionUtil.isEmpty(members)) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        List<Integer> cards = null;
        if (StringUtil.isNotBlank(title))
        {
            cards = cardDao.listPkeys(ascription, farmerPkey, title);
            if (CollectionUtil.isEmpty(cards)) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktMemberCardOnList> result = memberCardDao
            .queryUseCard(page, pagesize, userFarmer, startTime, endTime, st, et, members, cards, farmerPkey, status, invalid, ascription);
        for (MktMemberCardOnList dto : result.getContent())
        {
            MktMember member = memberDao.get(dto.getMember());
            if (member != null)
            {
                dto.setMemberName(member.getName());
                dto.setMobile(member.getMobile());
            }
            if (StringUtil.isNotBlank(dto.getUserFarmer()))
            {
                SysFarmer farmer = sysFarmerDao.get(dto.getUserFarmer());
                if (farmer != null) dto.setUserFarmerName(farmer.getName());
//                else
//                {
//                    dto.setUserFarmerName("东屿农贸市场");
//                }
            }
//            else
//            {
//                dto.setUserFarmerName("东屿农贸市场");
//            }
            MktCard card = cardDao.get(dto.getCard());
            if (card != null) dto.setCardName(card.getTitle());
        }
        return result;
    }

    public CardStatisticsInfo queryUseSumCard(String userFarmer, String startTime, String endTime, String st,
        String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        CardStatisticsInfo info = new CardStatisticsInfo();
        info.setSum(0l);
        info.setUnusedNum(0l);
        info.setUsedNum(0l);
        info.setExpiredNum(0l);
        info.setInvalidNum(0l);
        String farmerPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Integer> members = null;
        if (StringUtil.isNotBlank(mobile))
        {
            members = memberDao.listPkeys(ascription, mobile);
            if (CollectionUtil.isEmpty(members)) return info;
        }
        List<Integer> cards = null;
        if (StringUtil.isNotBlank(title))
        {
            cards = cardDao.listPkeys(ascription, farmerPkey, title);
            if (CollectionUtil.isEmpty(cards)) return info;
        }
        List<CardStatisticsInfo> list = memberCardDao
            .queryUseSumCard(userFarmer, startTime, endTime, st, et, members, cards, farmerPkey, status, invalid, ascription);
        if(list.isEmpty())
            return info;
        for(CardStatisticsInfo cs : list)
        {
            info.setSum(info.getSum() + cs.getSum());
            if(Boolean.FALSE.equals(cs.getInvalid()))
            {
                switch (cs.getStatus())
                {
                    case UNUSED:
                        info.setUnusedNum(info.getUnusedNum() + cs.getSum());
                        break;
                    case USED:
                        info.setUsedNum(info.getUsedNum() + cs.getSum());
                        break;
                    case EXPIRED:
                        info.setExpiredNum(info.getExpiredNum() + cs.getSum());
                        break;
                }
            }
            else
            {
                info.setInvalidNum(info.getInvalidNum() + cs.getSum());
            }
        }
        return info;
    }

    
    public void exportUseCard(String userFarmer, String startTime, String endTime, String st, String et, String mobile, String title,
        CardStatus status, Boolean invalid,
        HttpServletResponse response)
    {
        try
        {
            PageResult<MktMemberCardOnList> pageResult =
                queryUseCard(0, 10000, userFarmer, startTime, endTime, st, et, mobile, title, status, invalid);
            CardStatisticsInfo sumInfo = queryUseSumCard(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid);
            List<ExportMktMemberCardUse> list =
                BeanUtil.beanListFrom(ExportMktMemberCardUse.class, pageResult.getContent());
            StringBuilder sb = new StringBuilder();
            sb.append("合计    总数：");
            sb.append(sumInfo.getSum());
            sb.append("张  未使用：");
            sb.append(sumInfo.getUnusedNum());
            sb.append("张  已使用：");
            sb.append(sumInfo.getUsedNum());
            sb.append("张  已过期：");
            sb.append(sumInfo.getExpiredNum());
            sb.append("张");
            ExcelUtil.exportExcel(list,
                "优惠券使用记录",
                response.getOutputStream(),
                ExportMktMemberCardUse.class,
                new String[] {"优惠券使用记录", sb.toString()});
        }
        catch (Exception e)
        {
            log.error("导出优惠券使用记录失败", e);
        }
    }
    
    public PageResult<AppCardDTO> getCenterCard(int page, int pagesize, Integer cardPkey)
    {
        String farmerPkey = MobileSession.farmerPkey();
        Integer ascription = MobileSession.appid();
        SelectPageBuilder<Integer, MktCard> builder = cardDao.selectPage()
            .eq("ascription", ascription)
            .page(page)
            .pagesize(pagesize)
            .eq("enabled", true)
            .eq("invalid", false)
            .eq("idDel", false)
            
            ;
        
        if (cardPkey != null)
            builder.eq("pkey", cardPkey);
        else
            builder.eq("cardType", CardType.CARD_CENTER).gt("count", f("issuedNum"));
        
        String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        
        // @formatter:off
        builder.and()
            .or()
                .and()
                    .ge("endDate", date)
                    .le("startDate", date)
                .close()
                .isNull("endDate")
            .close()
            .or()
                .and()
                    .eq("farmer", (Constant.Operation + ascription))
                    .eq("userFarmer", farmerPkey)
                .close()
                .and()
                    .eq("farmer", (Constant.Operation + ascription))
                    .isNull("userFarmer")
                .close()
                .and()
                    .eq("farmer", farmerPkey)
                .close()
            .close()
            .close()
            .done();
        // @formatter:on
        
        List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), MobileSession.appid());
        List<Long> cardPkeys = new ArrayList<>();
        if(!listTag.isEmpty())
        {
            cardPkeys = tagVisibleDao.listTarget(TagVisibleTargetType.CARD, listTag);
        }
        ConditionBuilder<SelectPageBuilder<Integer, MktCard>> or = builder.or()
        .eq("visibleRange", MemberVisibleRange.ALL)
        .isNull("visibleRange");
        if(!cardPkeys.isEmpty())
        {
            or = or
            .and()
                .eq("visibleRange", MemberVisibleRange.TAG)
                .in("pkey", cardPkeys)
            .close();
        }
        builder = or.close().done();
        
        PageResult<MktCard> exec = builder.sort("createdTime").sort("pkey").exec();
        PageResult<AppCardDTO> result = BeanUtil.beanPageFrom(AppCardDTO.class, exec);
        List<AppCardDTO> list = result.getContent();
        for (AppCardDTO co : list)
        {
            setAppDto(co);
        }
        
        // 设置是否领取
        setIsReceive(list);
        // 排序（原来的list不是ArrayList）
        List<AppCardDTO> sortList = new ArrayList<>(list);
        List<PageSort.Order> sorts = new ArrayList<>();
        sorts.add(new PageSort.Order(Sort.Direction.ASC, "isReceive"));
        sorts.add(new PageSort.Order(Sort.Direction.DESC, "pkey"));
        PageUtil.sortList(sortList, sorts);
        result.setContent(sortList);
        return result;
    }
    
    public Boolean setCenterCard(Integer pkey)
    {
        MktCard card = cardDao.get(pkey);
        if (card == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        card.setCardType(CardType.CARD_CENTER);
        cardDao.update(card);
        return true;
    }
    
    // 3天到期的卡券,给用户发送提醒 
    public void maturityNews()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        String time = DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd");
        List<MktMemberCard> exec =
            memberCardDao.select().eq("status", CardStatus.UNUSED).eq(substring(f("endDate"), 1, 10), time).exec();
        log.info("exec: {}", JsonUtil.toString(exec, true));
        AccountEntity account = wxManager.getAccountEntity(AccountType.USER, CurrentSession.ascriptionPkey());
        
        for (MktMemberCard mc : exec)
        {
            MktMember member = memberDao.get(mc.getMember());
            if (member == null) continue;
            log.info("member: {}", JsonUtil.toString(member, true));
            String openid = member.getOpenid1();
            if (openid == null) openid = member.getOpenid2();
            if (openid == null) continue;
            MktCard card = cardDao.get(mc.getCard());
            if (card == null) continue;
            sendWeapp(card.getTitle(), time, openid, account);
        }
    }
    
    private void sendWeapp(String cardName, String time, String openid, AccountEntity account)
    {
        log.info("time: {}, openid: {}, cardName: {}", DateUtil.formatDate(new Date()), openid, cardName);
        String templateId = "UQybbZ6j-0S6_erIxg3rTZRfPNuE0FwWLFdbxp5618Q";
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", cardName);
        data.put("thing1", jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", time);
        data.put("date2", jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("value", "您的卡券即将过期,请及时使用!");
        data.put("thing3", jsonObject3);
        AppWxErrMsgDTO dto =
            wxManager.sendWeappSubscribeMessage(account, openid, templateId, "/pages/my/coupon/coupon", data);
        System.out.println(JsonUtil.toString(dto, true));
    }
    
    // 跑批处理 过期的卡券 
    public void removeExpiredMemberCard()
    {
        Calendar cl = Calendar.getInstance();
        //    	cl.add(Calendar.DATE, -1);
        log.info("date: {}", DateUtil.formatDate(cl.getTime()));
        List<MktMemberCard> exec = memberCardDao.select().lt("endDate", cl.getTime()).exec();
        log.info("size: {}", exec.size());
        memberCardDao.removeAll(exec);
    }
    
    public List<DropDTO> queryCard()
    {
        List<MktCard> list = cardDao.listCards(CardType.MANUALLY_ISSUE, true);
        return BeanUtil.beanListFrom(DropDTO.class, list);
    }
    
    /**
     * 设置当前卡片是否被领取
     * @param dtoList  dto列表
     */
    private void setIsReceive(List<AppCardDTO> dtoList)
    {
        if (CollectionUtils.isNotEmpty(dtoList))
        {
            Integer memberPkey = MobileSession.memberPkey();
            if(memberPkey == null)
            {
                for (AppCardDTO dto : dtoList)
                {
                    dto.setIsReceive(false);
                }
            }
            else
            {
                // 当前微信用户、card的主键列表的数据
                List<Integer> pkeyList = dtoList.stream().map(AppCardDTO::getPkey).collect(Collectors.toList());
                List<MktMemberCard> mCardList = memberCardDao.select().eq("member", memberPkey).in("card", pkeyList).exec();
                
                List<Integer> cardPkeyList = mCardList.stream().map(MktMemberCard::getCard).collect(Collectors.toList());
                for (AppCardDTO dto : dtoList)
                {
                    dto.setIsReceive(false);
                    if (cardPkeyList.contains(dto.getPkey()))
                    {
                        // 已被收取
                        dto.setIsReceive(true);
                    }
                }
            }
        }
    }
    
    /**
     * 首页-卡券列表
     * @return  结果
     */
    public List<AppCardDTO> queryAppCard()
    {
        List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), MobileSession.appid());
        List<Long> cardPkeys = new ArrayList<>();
        if(!listTag.isEmpty())
        {
            cardPkeys = tagVisibleDao.listTarget(TagVisibleTargetType.CARD, listTag);
        }
       
        List<MktCard> list = cardDao.queryCard(MobileSession.farmerPkey(), null, CardType.CARD_CENTER, true, null, 
            MobileSession.appid(), cardPkeys);
        // 转换数据
        List<AppCardDTO> result = BeanUtil.beanListFrom(AppCardDTO.class, list);
        setIsReceive(result);
        // 排序
        List<PageSort.Order> sorts = new ArrayList<>();
        sorts.add(new PageSort.Order(Sort.Direction.ASC, "isReceive"));
        sorts.add(new PageSort.Order(Sort.Direction.DESC, "pkey"));
        PageUtil.sortList(result, sorts);
        return result;
    }
    
    /**
     * 弹框卡券列表
     * @return  结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public List<AppCardDTO> queryNewCard()
    {
//        Integer memberPkey = MobileSession.memberPkey();
//        String farmerPkey = MobileSession.farmerPkey();
//        // 领取记录
//        List<MktMemberCard> mktMemberCards = memberCardDao.select().eq("member", memberPkey).eq("isRead", false).exec();
//        
//        if (CollectionUtils.isNotEmpty(mktMemberCards))
//        {
//            // 已领取的pkey
//            List<Integer> cardPkeyList =
//                mktMemberCards.stream().map(MktMemberCard::getCard).collect(Collectors.toList());
//            
//            // 手动发放的优惠券
//            List<MktCard> list = cardDao.queryCard(farmerPkey, null, CardType.MANUALLY_ISSUE, true, cardPkeyList, MobileSession.appid());
//            if (CollectionUtils.isNotEmpty(list))
//            {
//                // 修改已读状态
//                mktMemberCards.forEach(i -> {
//                    i.setIsRead(true);
//                });
//                memberCardDao.updateAll(mktMemberCards);
//                
//                // 转换数据
//                List<AppCardDTO> appCardDTOS = BeanUtil.beanListFrom(AppCardDTO.class, list);
//                
//                // 填充app的mkt_card数据
//                appCardDTOS.forEach(this::setAppDto);
//                return appCardDTOS;
//            }
//        }
        return Collections.emptyList();
    }
    
    // 填充app的mkt_card数据
    private void setAppDto(AppCardDTO co)
    {
        if (co.getUserType() != null)
        {
            MktGtype mktGtype = gtypeDao.selectOne().eq("pkey", co.getUserType()).exec();
            if (mktGtype != null)
            {
                co.setUserTypeName(mktGtype.getName());
            }
        }
        if (co.getUserGoods() != null)
        {
            log.info("assembleCard:goodsDao:pkey: {}", co.getUserGoods());
            MktGoods goods = goodsDao.selectOne().eq("pkey", co.getUserGoods()).exec();
            if (goods != null)
            {
                co.setUserGoodsName(goods.getTitle());
            }
        }
        if (co.getEffective() != null)
        {
            co.setEffectiveDate(co.getEffectiveDate());
            co.setExpireChoose(true);
        }
        if (co.getEndDate() != null)
        {
            co.setEffectiveDate(DateUtil.formatDate(co.getEndDate(), "yyyy-MM-dd"));
            co.setExpireChoose(false);
        }
    }
    
    public Boolean isFinish()
    {
        List<AppCardDTO> appCardDTOS = queryAppCard();
        if (CollectionUtils.isNotEmpty(appCardDTOS))
        {
            List<Boolean> isReceivedList =
                appCardDTOS.stream().map(AppCardDTO::getIsReceive).collect(Collectors.toList());
            // 所有都为true，表明领取完毕
            return !isReceivedList.contains(false);
        }
        return false;
    }
    
}
