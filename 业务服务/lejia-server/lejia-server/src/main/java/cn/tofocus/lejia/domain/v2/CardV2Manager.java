package cn.tofocus.lejia.domain.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderGwcV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.utils.DateUtil;
import io.micrometer.core.instrument.util.StringUtils;

@Component
public class CardV2Manager
{
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private DtoEnhance dtoEnhance;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    public List<MemberCardV2OnList> listMemberCard(CardStatus status)
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<MemberCardV2OnList> res = memberCardDao.listMemberCardV2(memberPkey, status);
        List<Integer> goodsKeys = new ArrayList<>();
        List<Integer> keys = new ArrayList<>();
        res.forEach(e -> {
            keys.add(e.getCard());
            if (e.getUserGoodsList() != null && !e.getUserGoodsList().isEmpty()) goodsKeys.addAll(e.getUserGoodsList());
            
        });
        
        if (!goodsKeys.isEmpty())
        {
            Map<Integer, MktGoods> map = goodsDao.getGoodsMap(goodsKeys);
            for (MemberCardV2OnList mc : res)
            {
                if (mc.getUserGoodsList() != null && !mc.getUserGoodsList().isEmpty())
                {
                    StringBuffer sb = new StringBuffer();
                    for (Integer ug : mc.getUserGoodsList())
                    {
                        if (map.containsKey(ug))
                        {
                            sb.append(map.get(ug).getTitle());
                            sb.append("、");
                        }
                    }
                    if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                    mc.setUserGoodsName(sb.toString());
                }
            }
        }
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        for (MemberCardV2OnList cardDto : res)
        {
            if (mapCard.containsKey(cardDto.getCard()))
            {
                MktCard mktCard = mapCard.get(cardDto.getCard());
                cardDto.setTitle(mktCard.getTitle());
                cardDto.setUserFarmer(mktCard.getUserFarmer());
                if (StringUtils.isBlank(cardDto.getUserFarmer())) cardDto.setUserFarmerName(null);
            }
            Integer userVendor = cardDto.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null) cardDto.setUserVendorName(mktVendor.getDisplayName());
            }
            List<Integer> userMtype = cardDto.getUserMtype();
            if (userMtype != null && !userMtype.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                for (Integer m : userMtype)
                {
                    String n = "";
                    switch (m)
                    {
                        case 0:
                            if (qfAscription.equals(cardDto.getAscription()))
                            {
                                n = "滨海民生自营";
                            }
                            else
                            {
                                n = "积分商城";
                            }
                            break;
                        case 11:
                        case 12:
                            break;
                        case 13:
                            n = "预售商品";
                            break;
                        default:
                            n = MType.fromIndex(m).getName() + "商品";
                            break;
                    }
                    sb.append(n);
                    sb.append("专区、");
                    
                }
                if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                cardDto.setMtypeName(sb.toString());
            }
        }
        
        return res;
    }
    
    public List<MemberCardV2OnList> listCard(OrderTotalV2Info dto)
    {
        long k1 = System.currentTimeMillis();
        if (CardCouponType.POSTAGE_COUPON.equals(dto.getType()))
        {
            return listPostageCard(dto);
        }
        MktMember mktMember = MobileSession.member();
        
        List<MemberCardV2OnList> cards = new ArrayList<>();
        String farmer = MobileSession.farmerPkey();
        Boolean flag = false;
        
        if (mktMember.getLevel().getIndex() == 1) flag = true;
        if (dto.getFarmerInfo() == null || dto.getFarmerInfo().isEmpty()) return cards;
        BigDecimal amt = BigDecimal.ZERO;
        Map<Integer, BigDecimal> vendorMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMap = new HashMap<>();
        Map<Integer, BigDecimal> gtypeMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMtypeMap = new HashMap<>();
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        for (OrderV2Info line : dto.getFarmerInfo())
        {
            gkeys.add(line.getGoods().intValue());
            for (OrderGwcV2OnList og : line.getLines())
            {
                if(og.getSpace() != null)
                    skeys.add(og.getSpace());
            }
        }
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        for (OrderV2Info line : dto.getFarmerInfo())
        {
            MktGoods goods = goodsMap.get(line.getGoods().intValue());
            //            if(!goods.getMType().equals(MType.MARKET_GOODS) && !goods.getMType().equals(MType.SPECIAL_GOODS) 
            //                && !goods.getMType().equals(MType.PROCESS_GOODS) && !goods.getMType().equals(MType.BOX_GOODS))
            //                continue;
            Integer gtype = goods.getGtype();
            //if (goods.getVendor() != null) inVendorKeys.add(goods.getVendor());
            BigDecimal add = BigDecimal.ZERO;
            for (OrderGwcV2OnList og : line.getLines())
            {
                if (!spaceMap.containsKey(og.getSpace())) continue;
                MktGoodsSpace space = spaceMap.get(og.getSpace());
                BigDecimal addAmt = null;
                if (Boolean.TRUE.equals(flag) && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    addAmt = space.getPriceMember().multiply(new BigDecimal(og.getNum()));
                }
                else
                {
                    addAmt = space.getPrice().multiply(new BigDecimal(og.getNum()));
                }
                if (vendorMap.containsKey(goods.getVendor()))
                {
                    vendorMap.put(goods.getVendor(), vendorMap.get(goods.getVendor()).add(addAmt));
                }
                else
                    vendorMap.put(goods.getVendor(), addAmt);
                add = add.add(addAmt);
                amt = amt.add(addAmt);
            }
            gdMap.put(line.getGoods().intValue(), add);
            if (gtypeMap.containsKey(gtype))
            {
                gtypeMap.put(gtype, gtypeMap.get(gtype).add(add));
            }
            else
                gtypeMap.put(gtype, add);
            if (gdMtypeMap.containsKey(goods.getMType().getIndex()))
            {
                gdMtypeMap.put(goods.getMType().getIndex(), gdMtypeMap.get(goods.getMType().getIndex()).add(add));
            }
            else
                gdMtypeMap.put(goods.getMType().getIndex(), add);
        }
        List<MktMemberCard> list =
            memberCardDao.listMemberCardV2(mktMember.getPkey(), CardCouponType.GOODS_COUPON, farmer);
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> {
            keys.add(e.getCard());
        });
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        for (MktMemberCard mcard : list)
        {
            if (amt.compareTo(mcard.getLimitCost()) < 0)
            {
                System.out.println("少于最低消费 过滤");
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                System.out.println("farmer: " + farmer);
                Integer appid = MobileSession.appid();
                if (!userFarmer.equals(farmer) && !(Constant.Operation + appid).equals(userFarmer))
                {
                    System.out.println("市场不匹配 过滤");
                    continue;
                }
            }
            Integer userVendor = mcard.getUserVendor();
            if (userVendor != null)
            {
                if (vendorMap.containsKey(userVendor))
                {
                    if (vendorMap.get(userVendor).compareTo(mcard.getLimitCost()) < 0) continue;
                }
                else
                    continue;
            }
            Integer userType = mcard.getUserType();
            if (userType != null)
            {
                if (gtypeMap.containsKey(userType))
                {
                    if (gtypeMap.get(userType).compareTo(mcard.getLimitCost()) == -1) continue;
                }
                else
                    continue;
            }
//            Integer userGoods = mcard.getUserGoods();
//            if (userGoods != null)
//            {
//                if (gdMap.containsKey(userGoods))
//                {
//                    if (gdMap.get(userGoods).compareTo(mcard.getLimitCost()) == -1) continue;
//                }
//                else
//                    continue;
//            }
            List<Integer> userMtype = mcard.getUserMtype();
            if (userMtype != null && !userMtype.isEmpty())
            {
                boolean mflag = false;
                for (Integer m : userMtype)
                {
                    if (gdMtypeMap.containsKey(m))
                    {
                        mflag = true;
                    }
                }
                if (Boolean.TRUE.equals(mflag))
                {
                    BigDecimal ma = BigDecimal.ZERO;
                    for (Integer m : userMtype)
                    {
                        if (gdMtypeMap.containsKey(m))
                        {
                            ma = ma.add(gdMtypeMap.get(m));
                        }
                    }
                   
                    if (ma.compareTo(mcard.getLimitCost()) < 0)
                    {
                        mflag = false;
                    }
                }
                if (Boolean.FALSE.equals(mflag))
                {
                    continue;
                }
            }
            
            List<Integer> userGoodsList = mcard.getUserGoodsList();
            if (userGoodsList != null && !userGoodsList.isEmpty())
            {
                boolean mflag = false;
                for (Integer g : userGoodsList)
                {
                    if (gdMap.containsKey(g))
                    {
                        mflag = true;
                    }
                }
                if (Boolean.FALSE.equals(mflag))
                {
                    continue;
                }
                else
                {
                    mflag = false;
                    for (Integer g : userGoodsList)
                    {
                        if (gdMap.containsKey(g))
                        {
                            if (gdMap.get(g).compareTo(mcard.getLimitCost()) > -1) mflag = true;
                        }
                    }
                    if (Boolean.FALSE.equals(mflag))
                    {
                        continue;
                    }
                }
            }
            
            CardUserOrderType userOrderType = mcard.getUserOrderType();
            if (userOrderType != null)
            {
                if ((userOrderType == CardUserOrderType.DELIVERY && Boolean.TRUE.equals(dto.getPickupType()))
                    || (userOrderType == CardUserOrderType.PICKUP && Boolean.FALSE.equals(dto.getPickupType())))
                    continue;
            }
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        mktMember.getPkey(),
                        CardStatus.USED,
                        DateUtil.atStartOfToday(),
                        DateUtil.atStartOfTomorrow());
                    if (usedNum >= activity.getLimitDailyCardNum())
                    {
                        System.out.println("该活动优惠券已达到今日使用上限");
                        continue;
                    }
                }
            }
            
            System.out.println("该卡券可用：" + mcard.getPkey());
            MemberCardV2OnList cardDto = new MemberCardV2OnList();
            BeanUtils.copyProperties(mcard, cardDto);
            if (mapCard.containsKey(mcard.getCard())) cardDto.setTitle(mapCard.get(mcard.getCard()).getTitle());
            cards.add(cardDto);
        }
        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        for (MemberCardV2OnList mc : cards)
        {
            Integer userVendor = mc.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null) mc.setUserVendorName(mktVendor.getDisplayName());
            }
            Integer userGoods = mc.getUserGoods();
            if (goodsMap.containsKey(userGoods)) mc.setUserGoodsName(goodsMap.get(userGoods).getTitle());
        }
        System.out.println("耗时: " + (System.currentTimeMillis() - k1));
        return cards;
    }
    
    private BigDecimal extractDataFromOrder4Card(List<OrderV2Info> orders, boolean isPaidMember,
        Map<Integer, BigDecimal> vendorMap, Map<Integer, BigDecimal> gdMap, Map<Integer, BigDecimal> gtypeMap,
        Map<Integer, BigDecimal> gdMtypeMap, Map<Integer, MktGoods> goodsMap)
    {
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        for (OrderV2Info line : orders)
        {
            gkeys.add(line.getGoods().intValue());
            for (OrderGwcV2OnList og : line.getLines())
            {
                skeys.add(og.getSpace());
            }
        }
        goodsMap.putAll(goodsDao.getGoodsMap(gkeys));
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        BigDecimal amt = BigDecimal.ZERO;
        for (OrderV2Info line : orders)
        {
            MktGoods goods = goodsMap.get(line.getGoods().intValue());
            Integer gtype = goods.getGtype();
            MType mType = goods.getMType();
            BigDecimal add = BigDecimal.ZERO;
            for (OrderGwcV2OnList og : line.getLines())
            {
                if (!spaceMap.containsKey(og.getSpace())) continue;
                MktGoodsSpace space = spaceMap.get(og.getSpace());
                BigDecimal addAmt = null;
                if (isPaidMember && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    addAmt = space.getPriceMember().multiply(new BigDecimal(og.getNum()));
                }
                else
                {
                    addAmt = space.getPrice().multiply(new BigDecimal(og.getNum()));
                }
                if (goods.getVendor() != null)
                {
                    if (vendorMap.containsKey(goods.getVendor()))
                    {
                        vendorMap.put(goods.getVendor(), vendorMap.get(goods.getVendor()).add(addAmt));
                    }
                    else
                    {
                        vendorMap.put(goods.getVendor(), addAmt);
                    }
                }
                add = add.add(addAmt);
                amt = amt.add(addAmt);
            }
            gdMap.put(line.getGoods().intValue(), add);
            if (gtypeMap.containsKey(gtype))
            {
                gtypeMap.put(gtype, gtypeMap.get(gtype).add(add));
            }
            else
            {
                gtypeMap.put(gtype, add);
            }
            if (gdMtypeMap.containsKey(mType.getIndex()))
            {
                gdMtypeMap.put(mType.getIndex(), gdMtypeMap.get(mType.getIndex()).add(add));
            }
            else
            {
                gdMtypeMap.put(mType.getIndex(), add);
            }
        }
        return amt;
    }
    
    private MemberCardOrderInfo listMemberCard4OrderV2(OrderTotalV2Info dto)
    {
        long k1 = System.currentTimeMillis();
        
        MemberCardOrderInfo res = new MemberCardOrderInfo();
        List<MemberCardV2OnList> cards = new ArrayList<>();
        List<MemberCardV2OnList> notCards = new ArrayList<>();
        
        MktMember mktMember = MobileSession.member();
        boolean isPaidMember = false;
        if (mktMember.getLevel().getIndex() == 1) isPaidMember = true;
        if (CollectionUtil.isEmpty(dto.getFarmerInfo()) && CollectionUtil.isEmpty(dto.getPointInfo()))
        {
            res.setAvailable(cards);
            res.setNotAvailable(notCards);
            return res;
        }
        BigDecimal amt;
        Map<Integer, BigDecimal> vendorMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMap = new HashMap<>();
        Map<Integer, BigDecimal> gtypeMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMtypeMap = new HashMap<>();
        Map<Integer, MktGoods> goodsMap = new HashMap<>();
        String farmer;
        
        // 市场商品
        if (CollectionUtil.isNotEmpty(dto.getFarmerInfo()))
        {
            farmer = MobileSession.farmerPkey();
            amt = extractDataFromOrder4Card(dto
                .getFarmerInfo(), isPaidMember, vendorMap, gdMap, gtypeMap, gdMtypeMap, goodsMap);
        }
        // 积分商城
        else
        {
            farmer = Constant.Operation + mktMember.getAscription();
            amt = extractDataFromOrder4Card(dto
                .getPointInfo(), isPaidMember, vendorMap, gdMap, gtypeMap, gdMtypeMap, goodsMap);
        }
        List<MktMemberCard> list = memberCardDao.listMemberCardV2(mktMember.getPkey(), CardCouponType.GOODS_COUPON);
        List<Integer> keys = list.stream().map(MktMemberCard::getCard).collect(Collectors.toList());
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        for (MktMemberCard mcard : list)
        {
            MemberCardV2OnList cardDto = new MemberCardV2OnList();
            BeanUtils.copyProperties(mcard, cardDto);
            dtoEnhance.deal(MemberCardV2OnList.class, cardDto);
            if (mapCard.containsKey(mcard.getCard())) cardDto.setTitle(mapCard.get(mcard.getCard()).getTitle());
            
            // 判断可用
            if (amt.compareTo(mcard.getLimitCost()) < 0)
            {
                if (StringUtils.isBlank(cardDto.getNotAvailable()))
                {
                    System.out.println("少于最低消费 过滤");
                    cardDto.setNotAvailable("订单金额满" + mcard.getLimitCost() + "元可用");
                    notCards.add(cardDto);
                }
                
                //                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                System.out.println("farmer: " + farmer);
                //                Integer appid = MobileSession.appid();
                //                !(Constant.Operation + appid).equals(userFarmer)
                if (!userFarmer.equals(farmer))
                {
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("市场不匹配 过滤");
                        cardDto.setNotAvailable("仅限【" + cardDto.getUserFarmerName() + "】线上支付使用");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
            }
            
            List<Integer> userMtype = mcard.getUserMtype();
            if (userMtype != null && !userMtype.isEmpty())
            {
                boolean mflag = false;
                StringBuilder sb = new StringBuilder();
                for (Integer m : userMtype)
                {
                    if (gdMtypeMap.containsKey(m))
                    {
                        mflag = true;
                    }
                    String n = "";
                    switch (m)
                    {
                        case 0:
                            if (qfAscription.equals(mcard.getAscription()))
                            {
                                n = "滨海民生自营";
                            }
                            else
                            {
                                n = "积分商城";
                            }
                            break;
                        case 11:
                        case 12:
                            break;
                        case 13:
                            n = "预售商品";
                            break;
                        default:
                            n = MType.fromIndex(m).getName() + "商品";
                            break;
                    }
                    sb.append(n);
                    sb.append("专区、");
                    
                }
                if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                cardDto.setMtypeName(sb.toString());
                if (Boolean.FALSE.equals(mflag))
                {
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("专区不匹配 过滤");
                        cardDto.setNotAvailable("仅限【" + sb.toString() + "】线上支付使用");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
                if (Boolean.TRUE.equals(mflag))
                {
                    BigDecimal ma = BigDecimal.ZERO;
                    for (Integer m : userMtype)
                    {
                        if (gdMtypeMap.containsKey(m))
                        {
                            ma = ma.add(gdMtypeMap.get(m));
                        }
                    }
                    if (ma.compareTo(mcard.getLimitCost()) < 0)
                    {
                        if (StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            System.out.println("少于最低消费 过滤");
                            cardDto.setNotAvailable("订单金额满" + mcard.getLimitCost() + "元可用");
                            notCards.add(cardDto);
                        }
                        //                        continue;
                    }
                }
            }
            
            Integer userVendor = mcard.getUserVendor();
            if (userVendor != null)
            {
                if (vendorMap.containsKey(userVendor))
                {
                    if (vendorMap.get(userVendor).compareTo(mcard.getLimitCost()) < 0)
                    {
                        if (StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            cardDto.setNotAvailable(
                                "指定商户【" + cardDto.getUserVendorName() + "】商品金额满" + mcard.getLimitCost() + "元可用");
                            notCards.add(cardDto);
                        }
                        //                        continue;
                    }
                }
                else
                {
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        cardDto.setNotAvailable("指定商户【" + cardDto.getUserVendorName() + "】线上支付使用");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
            }
            Integer userType = mcard.getUserType();
            if (userType != null)
            {
                cardDto.setUserTypeName(cardDto.getUserTypeName());
                if (gtypeMap.containsKey(userType))
                {
                    if (gtypeMap.get(userType).compareTo(mcard.getLimitCost()) < 0)
                    {
                        if (StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            cardDto.setNotAvailable(
                                "适用于【" + cardDto.getUserTypeName() + "】分类下的商品金额满" + mcard.getLimitCost() + "元可用");
                            notCards.add(cardDto);
                        }
                        //                        continue;
                    }
                }
                else
                {
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        cardDto.setNotAvailable("适用于【" + cardDto.getUserTypeName() + "】分类下的商品");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
            }
            List<Integer> userGoodsList = mcard.getUserGoodsList();
            if (userGoodsList != null && !userGoodsList.isEmpty())
            {
                boolean mflag = false;
                StringBuilder sb = new StringBuilder();
                for (Integer g : userGoodsList)
                {
                    if (gdMap.containsKey(g))
                    {
                        mflag = true;
                    }
                    MktGoods mktGoods = goodsDao.get(g);
                    sb.append(mktGoods.getTitle());
                    sb.append("、");
                }
                if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                cardDto.setUserGoodsName(sb.toString());
                if (Boolean.FALSE.equals(mflag))
                {
                    System.out.println("商品不匹配 过滤");
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        cardDto.setNotAvailable("仅限【" + sb.toString() + "】线上支付使用");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
                else
                {
                    mflag = false;
                    for (Integer g : userGoodsList)
                    {
                        if (gdMap.containsKey(g))
                        {
                            if (gdMap.get(g).compareTo(mcard.getLimitCost()) > -1) mflag = true;
                        }
                    }
                    if (Boolean.FALSE.equals(mflag))
                    {
                        System.out.println("商品金额不匹配 过滤");
                        cardDto.setNotAvailable("指定商品金额满" + mcard.getLimitCost() + "元可用");
                        notCards.add(cardDto);
                        //                        continue;
                    }
                }
            }
            
            CardUserOrderType userOrderType = mcard.getUserOrderType();
            if (userOrderType != null)
            {
                cardDto.setUserOrderTypeName(mcard.getUserOrderType().getName());
                if ((userOrderType == CardUserOrderType.DELIVERY && Boolean.TRUE.equals(dto.getPickupType()))
                    || (userOrderType == CardUserOrderType.PICKUP && Boolean.FALSE.equals(dto.getPickupType())))
                {
                    if (StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        cardDto.setNotAvailable("仅限" + mcard.getUserOrderType().getName() + "使用");
                        notCards.add(cardDto);
                    }
                    //                    continue;
                }
            }
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        mktMember.getPkey(),
                        CardStatus.USED,
                        DateUtil.atStartOfToday(),
                        DateUtil.atStartOfTomorrow());
                    if (usedNum >= activity.getLimitDailyCardNum())
                    {
                        if (StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            System.out.println("该活动优惠券已达到今日使用上限");
                            cardDto.setNotAvailable("每日限用" + activity.getLimitDailyCardNum() + "张");
                            notCards.add(cardDto);
                        }
                        //                        continue;
                    }
                }
            }
            if (StringUtils.isBlank(cardDto.getNotAvailable()))
            {
                System.out.println("该卡券可用：" + mcard.getPkey());
                cards.add(cardDto);
            }
        }
        for (MemberCardV2OnList mc : cards)
        {
            Integer userVendor = mc.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null) mc.setUserVendorName(mktVendor.getDisplayName());
            }
        }
        for (MemberCardV2OnList mc : notCards)
        {
            Integer userVendor = mc.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null) mc.setUserVendorName(mktVendor.getDisplayName());
            }
        }
        res.setAvailable(cards);
        res.setNotAvailable(notCards);
        System.out.println("耗时: " + (System.currentTimeMillis() - k1));
        return res;
    }
    
    public MemberCardOrderInfo listCardV2(OrderTotalV2Info dto)
    {
        MemberCardOrderInfo res = listMemberCard4OrderV2(dto);
        return res;
    }
    
    private List<MemberCardV2OnList> listPostageCard(OrderTotalV2Info dto)
    {
        List<MemberCardV2OnList> cards = new ArrayList<>();
        MktMember mktMember = MobileSession.member();
        String farmer = MobileSession.farmerPkey();
        BigDecimal postage = dto.getOldPostage();
        if (postage == null) postage = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(dto.getPostFree())) postage = BigDecimal.ZERO;
        if (DistributionType.PICKUP.equals(dto.getDistributionType())
            || DistributionType.DINE_IN.equals(dto.getDistributionType())) postage = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(postage) == 0) return cards;
        Boolean flag = false;
        if (mktMember.getLevel().getIndex() == 1) flag = true;
        if (dto.getFarmerInfo() == null || dto.getFarmerInfo().isEmpty()) return cards;
        
        List<MktMemberCard> list =
            memberCardDao.listMemberCardV2(mktMember.getPkey(), CardCouponType.POSTAGE_COUPON, farmer);
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getCard()));
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        BigDecimal amt = BigDecimal.ZERO;
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        for (OrderV2Info line : dto.getFarmerInfo())
        {
            gkeys.add(line.getGoods().intValue());
            for (OrderGwcV2OnList og : line.getLines())
            {
                skeys.add(og.getSpace());
            }
        }
        //        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        for (OrderV2Info line : dto.getFarmerInfo())
        {
            //            MktGoods goods = goodsMap.get(line.getGoods());
            //            if(!goods.getMType().equals(MType.MARKET_GOODS) && !goods.getMType().equals(MType.SPECIAL_GOODS)
            //                && !goods.getMType().equals(MType.PROCESS_GOODS))
            //                continue;
            for (OrderGwcV2OnList og : line.getLines())
            {
                if (!spaceMap.containsKey(og.getSpace())) continue;
                MktGoodsSpace space = spaceMap.get(og.getSpace());
                BigDecimal addAmt = null;
                if (Boolean.TRUE.equals(flag) && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    addAmt = space.getPriceMember().multiply(new BigDecimal(og.getNum()));
                }
                else
                {
                    addAmt = space.getPrice().multiply(new BigDecimal(og.getNum()));
                }
                amt = amt.add(addAmt);
            }
        }
        
        for (MktMemberCard mcard : list)
        {
            if (amt.compareTo(mcard.getLimitCost()) < 0)
            {
                System.out.println("少于最低消费 过滤" + mcard.getPkey());
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                Integer appid = MobileSession.appid();
                if (!farmer.equals(userFarmer) && !(Constant.Operation + appid).equals(userFarmer))
                {
                    System.out.println("市场不匹配 过滤");
                    continue;
                }
            }
            
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        mktMember.getPkey(),
                        CardStatus.USED,
                        DateUtil.atStartOfToday(),
                        DateUtil.atStartOfTomorrow());
                    if (usedNum >= activity.getLimitDailyCardNum())
                    {
                        System.out.println("该活动优惠券已达到今日使用上限");
                        continue;
                    }
                }
            }
            
            System.out.println("该卡券可用：" + mcard.getPkey());
            MemberCardV2OnList cardDto = new MemberCardV2OnList();
            BeanUtils.copyProperties(mcard, cardDto);
            if (mapCard.containsKey(mcard.getCard())) cardDto.setTitle(mapCard.get(mcard.getCard()).getTitle());
            cards.add(cardDto);
        }
        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        return cards;
    }
    
    private List<MemberCardV2OnList> listPostageCardSys(OrderTotalV2Info dto)
    {
        List<MemberCardV2OnList> cards = new ArrayList<>();
        MktMember mktMember = MobileSession.member();
        //        String farmer = MobileSession.farmerPkey();
        String farmer = Constant.Operation + mktMember.getAscription();
        BigDecimal postage = dto.getOldPostage();
        if (postage == null) postage = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(dto.getPostFree())) postage = BigDecimal.ZERO;
        if (DistributionType.PICKUP.equals(dto.getDistributionType())
            || DistributionType.DINE_IN.equals(dto.getDistributionType())) postage = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(postage) == 0) return cards;
        Boolean flag = false;
        if (mktMember.getLevel().getIndex() == 1) flag = true;
        if (dto.getPointInfo() == null || dto.getPointInfo().isEmpty()) return cards;
        
        List<MktMemberCard> list =
            memberCardDao.listMemberCardV2(mktMember.getPkey(), CardCouponType.POSTAGE_COUPON, farmer);
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getCard()));
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        BigDecimal amt = BigDecimal.ZERO;
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        for (OrderV2Info line : dto.getPointInfo())
        {
            gkeys.add(line.getGoods().intValue());
            for (OrderGwcV2OnList og : line.getLines())
            {
                skeys.add(og.getSpace());
            }
        }
        //        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        for (OrderV2Info line : dto.getPointInfo())
        {
            //            MktGoods goods = goodsMap.get(line.getGoods());
            //            if(!goods.getMType().equals(MType.MARKET_GOODS) && !goods.getMType().equals(MType.SPECIAL_GOODS)
            //                && !goods.getMType().equals(MType.PROCESS_GOODS))
            //                continue;
            for (OrderGwcV2OnList og : line.getLines())
            {
                if (!spaceMap.containsKey(og.getSpace())) continue;
                MktGoodsSpace space = spaceMap.get(og.getSpace());
                BigDecimal addAmt = null;
                if (Boolean.TRUE.equals(flag) && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    addAmt = space.getPriceMember().multiply(new BigDecimal(og.getNum()));
                }
                else
                {
                    addAmt = space.getPrice().multiply(new BigDecimal(og.getNum()));
                }
                amt = amt.add(addAmt);
            }
        }
        
        for (MktMemberCard mcard : list)
        {
            if (amt.compareTo(mcard.getLimitCost()) < 0)
            {
                System.out.println("少于最低消费 过滤" + mcard.getPkey());
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                Integer appid = MobileSession.appid();
                if (!farmer.equals(userFarmer) && !(Constant.Operation + appid).equals(userFarmer))
                {
                    System.out.println("市场不匹配 过滤");
                    continue;
                }
            }
            
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        mktMember.getPkey(),
                        CardStatus.USED,
                        DateUtil.atStartOfToday(),
                        DateUtil.atStartOfTomorrow());
                    if (usedNum >= activity.getLimitDailyCardNum())
                    {
                        System.out.println("该活动优惠券已达到今日使用上限");
                        continue;
                    }
                }
            }
            
            System.out.println("该卡券可用：" + mcard.getPkey());
            MemberCardV2OnList cardDto = new MemberCardV2OnList();
            BeanUtils.copyProperties(mcard, cardDto);
            if (mapCard.containsKey(mcard.getCard())) cardDto.setTitle(mapCard.get(mcard.getCard()).getTitle());
            cards.add(cardDto);
        }
        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        return cards;
    }
}
