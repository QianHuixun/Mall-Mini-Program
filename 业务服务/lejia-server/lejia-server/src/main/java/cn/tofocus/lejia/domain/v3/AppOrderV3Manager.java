package cn.tofocus.lejia.domain.v3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.lejia.domain.MsdCateringManager;
import cn.tofocus.lejia.domain.market.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.api.v3.ProblemApiImpl;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.config.MsdPayConfig;
import cn.tofocus.lejia.bean.dto.express.SfGetFreightAddedServicesResult;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.GoodsCardInfo;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderGoodsSpaceV3OnList;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderGoodsV3OnList;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderTotalV3Info;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderTotalV3Info2;
import cn.tofocus.lejia.bean.dto.v3.GwcOrderV3Info;
import cn.tofocus.lejia.bean.dto.v3.GwcSupplierPickupLocationInfo;
import cn.tofocus.lejia.bean.dto.v3.OrderGoodsV3OnList;
import cn.tofocus.lejia.bean.dto.v3.OrderTotalV3Info;
import cn.tofocus.lejia.bean.dto.v3.OrderV3Info;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsPresale;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktOrderTag;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.market.MktSupplierPickupLocation;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerPickupLocation;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerStation;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.enums.AddrType;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.cache.MemberTjrMap;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsPresaleDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktDeliveryTimeConfigDao;
import cn.tofocus.lejia.dao.market.MktDesktopDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderGroupDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktOrderTagDao;
import cn.tofocus.lejia.dao.market.MktPostageConfigDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.market.MktSupplierPickupLocationDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerMtypeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerPickupLocationDao;
import cn.tofocus.lejia.dao.sys.SysFarmerStationDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.wx.MktGzhAssociateDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;
import cn.tofocus.lejia.domain.express.ExpressSfManager;
import cn.tofocus.lejia.domain.h5.H5OrderManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.NsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.LocationUtils;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.TongTongSuoUtil;
import cn.tofocus.lejia.util.wx.PayJs;
import cn.tofocus.lejia.utils.LejiaUtils;
import lombok.extern.slf4j.Slf4j;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Slf4j
@Component
public class AppOrderV3Manager
{

    private final ProblemApiImpl problemApiImpl;

    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private SysFarmerMtypeDao sysFarmerMtypeDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktOrderTagDao orderTagDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MemberPointManager pointManager;
    
    @Autowired
    private MemberCommManager commManager;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private OrderTokenMap orderTokenMap;
    
    @Autowired
    private WxPayManager wxPayManger;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktPostageConfigDao postageConfigDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private NsPayManager nsPayManager;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private RedisCounter redisCounter;
    
    @Resource
    private SmsConfig smsConfig;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;
    
    @Autowired
    private MktDesktopDao desktopDao;
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Autowired
    private H5OrderManager h5OrderManager;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private RedisLockTemplate lock;
    
    @Resource
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private VendorOrderManager vendorOrderManager;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MemberTjrMap tjrMap;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private MktGzhDao gzhDao;
    
    @Autowired
    private MktGzhAssociateDao gzhAssociateDao;
    
    @Autowired
    private MktSupplierPickupLocationDao supplierPickupLocationDao;
    
    @Autowired
    private SysFarmerPickupLocationDao farmerPickupLocationDao;
    
    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private SysFarmerStationDao sysFarmerStationDao;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private ExpressSfManager expressSfManager;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;

    @Autowired
    private DtoEnhance dtoEnhance;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MsdCateringManager cateringManager;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;

    /**
     * 是否对接第三方餐饮系统的会员
     */
    @Value("${catering.enabled:false}")
    private boolean cateringEnabled;

    @Value("${catering.ascription:22}")
    private Integer cateringAscription;

    AppOrderV3Manager(ProblemApiImpl problemApiImpl) {
        this.problemApiImpl = problemApiImpl;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public OrderTotalV3Info commitOrder(OrderTotalV3Info dto)
    {
        Long k = System.currentTimeMillis();
        log.info("----------提交订单----------");
        Integer memberPkey = MobileSession.memberPkey();
        Long ll = orderTokenMap.get("order:" + memberPkey);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("order:" + memberPkey, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        orderTokenMap.put("order:" + memberPkey, System.currentTimeMillis());
        System.out.println("v3dto.getPayType(): " + dto.getPayType());
        checkOrder(dto, true);
        String payNumber = numberUtils.createOrderNumber();
        checkPayType(dto.getInfos(), dto.getPayType());
        // 写入订单
        List<MktOrder> listOrder = insAllOrder(dto, memberPkey, payNumber);
        
        dto.setOrderPkey(listOrder.get(0).getPkey());
        // 检验订单金额不可为零  由原来的 订单不可为零 修改为   订单金额是0 或者小于0 的 默认为0.01元
        if (dto.getGoodsSumAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setGoodsSumAmtn(BigDecimal.ZERO);
        int loadPoints = pointManager.loadPoints(memberPkey);
        // 积分余额不足
        if (loadPoints < dto.getSumPointn()) throw TofocusException.of(LejiaErrCode.NO_P0INTS);
        BigDecimal loadComm = commManager.loadComm(memberPkey);
        // 电子帐户余额不足
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) && loadComm.compareTo(dto.getGoodsSumAmtn()) < 0)
            throw TofocusException.of(LejiaErrCode.NO_COMMS);
        
        // 判断民生豆是否足够支付
        if (dto.getPayType().equals(PayType.ORDER_MSD))
        {
            BigDecimal loadMsd = getMsdBalance(memberPkey, MobileSession.appid());
            if (loadMsd.compareTo(dto.getGoodsSumAmtn()) < 0)
                throw TofocusException.of(LejiaErrCode.NO_MSD);
            payAfterOrder(dto, listOrder);
            return dto;
        }
        if(dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION) && loadComm.compareTo(dto.getGoodsSumAmtn()) >= 0)
        {
            dto.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
            for(MktOrder o : listOrder)
                o.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
            orderDao.updateAll(listOrder);
            payAfterOrder(dto, listOrder);
            return dto;
        }
        if (dto.getPayType().equals(PayType.MSD_COMBINATION))
        {
            BigDecimal loadMsd = getMsdBalance(memberPkey, MobileSession.appid());
            if (loadMsd.compareTo(dto.getGoodsSumAmtn()) >= 0)
            {
                dto.setPayType(PayType.ORDER_MSD);
                for(MktOrder o : listOrder)
                    o.setPayType(PayType.ORDER_MSD);
                orderDao.updateAll(listOrder);
                payAfterOrder(dto, listOrder);
                return dto;
            }
        }
        
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) || dto.getPayType().equals(PayType.NM_MEMBER))
        {
            payAfterOrder(dto, listOrder);
            return dto;
        }
        if(dto.getGoodsSumAmtn().compareTo(BigDecimal.ZERO) == 0)
        {
            payAfterOrder(dto, listOrder);
            return dto;
        }
        
        if (dto.getPayType().equals(PayType.ORDER_WEIXIN)
            || dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION)
            || dto.getPayType().equals(PayType.MSD_COMBINATION)) try
        {
            Integer appid = MobileSession.appid();
            BigDecimal amt = dto.getGoodsSumWeixinAmtn();
            if(dto.getPayType().equals(PayType.MSD_COMBINATION))
            {
                // 第三方餐饮系统没有退款接口 暂时不支持组合支付
                if (cateringEnabled && cateringAscription.equals(appid))
                {
                    throw TofocusException.of(LejiaErrCode.CATERING_ERROR);
                }
                for(MktOrder o : listOrder)
                {
                    memberMsdManager.updLockMsd(o.getMember(), o.getOtherAmt(), o.getAscription());
                }
            }
            if(dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
            {
                for(MktOrder o : listOrder)
                {
                    if(o.getOtherAmt() != null && o.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                    {
                        commManager.updLockComm(o.getMember(), 
                            o.getOtherAmt(), o.getCode(), o.getAscription());
                    }
                }
            }
            if (appid.equals(1))
            {
                WxPayData payData = nsPayManager.topayIvc(MobileSession.openid(), payNumber, amt);
                dto.setWxPayData(payData);
            }
            else if (appid.equals(13))
            {
                WxPayData payData = chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payNumber, amt);
                dto.setWxPayData(payData);
            }
            else
            {
                WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
                PayJs payJs = wxPayManger.topayIvc(MobileSession.billIp(), MobileSession.openid(), payNumber, amt, wxc);
                dto.setWxPayData(BeanUtil.beanFrom(WxPayData.class, payJs));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
        }
        
        log.info("----------订单提交成功----------");
        log.info("----------订单提交用时: {}----------", System.currentTimeMillis() - k);
        return dto;
    }
    
    // 判断是否可用组合支付 
    private void checkPayType(List<OrderV3Info> infos, PayType payType)
    {
        if(!PayType.MSD_COMBINATION.equals(payType) && !PayType.ORDER_MSD.equals(payType))
            return;
        MsdPayConfig mpConfig = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(mpConfig == null)
        {
            mpConfig = new MsdPayConfig();
            mpConfig.setFarmerGoods(false);
            mpConfig.setSysGoods(false);
        }
        List<Integer> spaceKeys = new ArrayList<>();
        for(OrderV3Info o : infos)
        {
            for(OrderGoodsV3OnList g : o.getGoodsList())
            {
                spaceKeys.add(g.getSpace());
            }
        }
        List<MktGoodsSpace> list = goodsSpaceDao.select().in("pkey", spaceKeys).exec();
        List<Integer> goodsKeys = new ArrayList<>();
        for(MktGoodsSpace gs : list)
            goodsKeys.add(gs.getGoods());
        List<MktGoods> goodsList = goodsDao.select().in("pkey", goodsKeys).exec();
        List<MType> mList = new ArrayList<>();
        mList.add(MType.INTEGRAL_GOODS);
        mList.add(MType.INTEGRAL_BNYP_GOODS);
        mList.add(MType.INTEGRAL_PRESALE_GOODS);
        for(MktGoods g : goodsList)
        {
            MType mType = g.getMType();
            if((MType.MARKET_GOODS.equals(mType) || MType.SPECIAL_GOODS.equals(mType))
                && Boolean.FALSE.equals(mpConfig.getFarmerGoods()))
            {
                throw TofocusException.of(LejiaErrCode.MSD_PAYTYPE_ERROR, g.getTitle());
            }
            if(mList.contains(mType) && Boolean.FALSE.equals(mpConfig.getSysGoods()))
                throw TofocusException.of(LejiaErrCode.MSD_PAYTYPE_ERROR, g.getTitle());
        }
    }
    
    private void checkOrder(OrderTotalV3Info dto, boolean isCommit)
    {
        // 校验时间
        checkbBusinessTime(dto.getInfos(), dto.getAddrPkey(), isCommit);
        
        //        if (dto.getGoodsSumAmt().compareTo(BigDecimal.ZERO) <= 0) dto.setGoodsSumAmt(new BigDecimal("0.01"));
        //        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        
        // 心安食足 会员积分不足
        if (dto.getPayType().equals(PayType.NM_MEMBER))
        {
            MktMember member = MobileSession.member();
            BigDecimal xaszComms = saasTokenPublicManager.getAccountBalance(member.getMobile(), member.getOpenid1());
            System.out.println("xaszComms: " + xaszComms);
            System.out.println("dto.getGoodsSumAmt(): " + dto.getGoodsSumAmtn());
            if (xaszComms.compareTo(dto.getGoodsSumAmtn()) < 0) throw TofocusException.of(LejiaErrCode.NO_COMMS);
        }
        // TODO 民生豆 如果走接口 这里需要提前查询
        if (dto.getPayType().equals(PayType.ORDER_MSD))
        {
//            if (xaszComms.compareTo(dto.getGoodsSumAmtn()) < 0) throw TofocusException.of(LejiaErrCode.NO_MSD);
        }
        
        //        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
        //            dto.setPickupAmt(new BigDecimal("0.01"));
        // 市场商品起送费用校验
        for (OrderV3Info oi : dto.getInfos())
        {
            if (StringUtils.isNotBlank(oi.getFarmer()))
            {
                SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(oi.getFarmer());
                if (isCommit && oi.getDistributionType() != DistributionType.PICKUP
                    && oi.getDistributionType() != DistributionType.DINE_IN && farmerConfig.getStartingPrice() != null
                    && farmerConfig.getStartingPrice().compareTo(BigDecimal.ZERO) > 0)
                {
                    BigDecimal price = BigDecimal.ZERO;
                    for (OrderGoodsV3OnList g : oi.getGoodsList())
                    {
                        MktGoodsSpace gs = goodsSpaceDao.get(g.getSpace());
                        BigDecimal multiply = gs.getPrice().multiply(new BigDecimal(g.getNum()));
                        price = price.add(multiply);
                    }
                    if (price.compareTo(farmerConfig.getStartingPrice()) < 0)
                        throw TofocusException.of(LejiaErrCode.STARTINGPRICE_ERROR);
                }
            }
        }
    }
    
    // 校验营业时间
    private void checkbBusinessTime(List<OrderV3Info> infos, Integer addPkey, boolean isCommit)
    {
        for (OrderV3Info oi : infos)
        {
            // 备注字数限制
            if (StringUtils.isNotBlank(oi.getRemark()) && oi.getRemark().length() > 50)
                throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT);
            // 判断 市场还是供应商
            if (StringUtils.isNotBlank(oi.getFarmer()) && !oi.getFarmer().startsWith(Constant.Operation))
            {
                // 营业时间校验
                SysFarmer farmer = sysFarmerDao.get(oi.getFarmer());
                if (farmer.getIdDel() || !farmer.getEnabled()) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
                SysFarmerConfig config = farmer.getConfig();
                if (Boolean.FALSE.equals(config.getYStatus())) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
                
                // 配送范围检验
                if (addPkey != null)
                {
                    MktAddr addrObj = addrDao.get(addPkey);
                    if (DistributionType.PICKUP.equals(oi.getDistributionType()))
                    {
                        if (isCommit && StringUtils.isBlank(oi.getPstime()))
                            throw TofocusException.of(LejiaErrCode.PICKUP_TIME_ERROR);
//                        if (AddrType.DELIVERY.equals(addrObj.getType()))
//                            throw TofocusException.of(LejiaErrCode.DELIVERY_ADDR_ERROR);
                    }
                    else if (!DistributionType.DINE_IN.equals(oi.getDistributionType()))
                    {
                        if (!AddrType.DELIVERY.equals(addrObj.getType()))
                            throw TofocusException.of(LejiaErrCode.DELIVERY_ADDR_ERROR);
                        Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                            config.getLongitude().doubleValue(),
                            addrObj.getLatitude().doubleValue(),
                            addrObj.getLongitude().doubleValue());
                        BigDecimal distance = new BigDecimal(a.toString());
                        // 转为米
                        BigDecimal configDistance = config.getDeliveryRange().multiply(new BigDecimal("1000"));
                        if (distance.compareTo(configDistance) > 0)
                        {
                            throw TofocusException.of(LejiaErrCode.FARMER_OVERRANE);
                        }
                    }
                }
                else if (isCommit)
                {
                    if (DistributionType.DINE_IN.equals(oi.getDistributionType()))
                    {
                        Integer qrCode = MobileSession.qrCode();
                        if (qrCode == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择桌位");
                    }
                    else
                    {
//                        String addrColumName =
//                            DistributionType.PICKUP.equals(oi.getDistributionType()) ? "自提人" : "收货地址";
//                        throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择" + addrColumName);
                    }
                }
            }
            else
            {
                // 判断用户的地址
                if (addPkey != null)
                {
//                    MktAddr addrObj = addrDao.get(addPkey);
                    if (DistributionType.PICKUP.equals(oi.getDistributionType()))
                    {
                        if (StringUtils.isBlank(oi.getPstime()))
                            throw TofocusException.of(LejiaErrCode.PICKUP_TIME_ERROR);
//                        if (AddrType.DELIVERY.equals(addrObj.getType()))
//                            throw TofocusException.of(LejiaErrCode.DELIVERY_ADDR_ERROR);
                    }
                }
            }
        }
    }
    
    //    public OrderTotalV3Info updOrderOne(OrderTotalV3Info dto, int pkey)
    //    {
    //        BigDecimal longitude = null;
    //        BigDecimal latitude = null;
    //        MktOrder order = orderDao.get(pkey);
    //        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
    //        if (!order.getStatus().equals(OrderStatus.UNPAID_ORDER)) throw TofocusException.of(LejiaErrCode.WRONG_STATUS);
    //        List<MktOrderLine> lines = orderLineDao.select().in("orderPkey", pkey).exec();
    //        String body = "";
    //        String payNumber = numberUtils.createOrderNumber();
    //        MktOrderCode oc = new MktOrderCode();
    //        oc.setOrderPkey(order.getPkey());
    //        oc.setCode(order.getCode());
    //        orderCodeDao.add(oc);
    //        if (dto.getFarmerInfo() != null && !dto.getFarmerInfo().isEmpty())
    //            order.setCode(payNumber + "2");
    //        else
    //            order.setCode(payNumber + "1");
    //        updOrderOne(order, dto, lines, body, longitude, latitude);
    //        
    //        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT))
    //        {
    //            appOrderManager.payAfterOrder(order);
    //        }
    //        try
    //        {
    //            if (dto.getPayType().equals(PayType.ORDER_WEIXIN))
    //            {
    //                Integer appid = MobileSession.appid();
    //                if (appid.equals(1))
    //                {
    //                    WxPayData payData = nsPayManager.topayIvc(MobileSession.openid(), payNumber, order.getAmtn());
    //                    dto.setWxPayData(payData);
    //                }
    //                else
    //                {
    //                    WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
    //                    PayJs js = wxPayManger.topayIvc(MobileSession.billIp(),
    //                        MobileSession.openid(),
    //                        order.getCode().substring(0, order.getCode().length() - 1),
    //                        order.getAmtn(),
    //                        wxc);
    //                    dto.setWxPayData(BeanUtil.beanFrom(WxPayData.class, js));
    //                }
    //            }
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
    //        }
    //        return dto;
    //    }
    
    //    private MktOrder updOrderOne(MktOrder order, OrderTotalV3Info dto, List<MktOrderLine> lines, String body,
    //        BigDecimal longitude, BigDecimal latitude)
    //    {
    //        order.setPayType(dto.getPayType());
    //        order.setPstime(dto.getPstime());
    //        if (!order.getDistributionType().equals(dto.getDistributionType()))
    //            throw TofocusException.of(LejiaErrCode.ORDER_DISTRIBUTIONTYPE_ERROR);
    //        //        order.setDistributionType(dto.getDistributionType());
    //        
    //        BigDecimal amto = BigDecimal.ZERO;
    //        BigDecimal weight = BigDecimal.ZERO;
    //        BigDecimal postageWeight = BigDecimal.ZERO;
    //        Boolean isPostage = true;
    //        Boolean isCard = false;
    //        Boolean flagCut = false;
    //        if (OrderType.CUT_ORDER.equals(order.getOrderType())) flagCut = true;
    //        
    //        List<Integer> gkeys = new ArrayList<>();
    //        List<Integer> skeys = new ArrayList<>();
    //        lines.forEach(e -> {
    //            gkeys.add(e.getGoods());
    //            skeys.add(e.getSpace());
    //        });
    //        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
    //        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
    //        
    //        Map<MType, SysFarmerMtype> mapMType = null;
    //        Integer appid = MobileSession.appid();
    //        if ((Constant.Operation + appid).equals(order.getFarmer()))
    //        {
    //            order.setOrderOir(OrderOir.POINTS_MALL);
    //        }
    //        else
    //        {
    //            order.setOrderOir(OrderOir.MARKET_MALL);
    //            mapMType = sysFarmerMtypeDao.mapMType(order.getFarmer());
    //        }
    //        // 商品限购校验
    //        checkBugGoodsNumOrderLine(lines, goodsMap);
    //        BigDecimal reducePrice = BigDecimal.ZERO;
    //        for (MktOrderLine line : lines)
    //        {
    //            if (!goodsMap.containsKey(line.getGoods()))
    //            {
    //                throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
    //            }
    //            MktGoods goods = goodsMap.get(line.getGoods());
    //            body = body + line.getGoodsName() + " ";
    //            MktGoodsSpace space = spaceMap.get(line.getSpace());
    //            line.setPrice(space.getPriceOld());
    //            if (Boolean.TRUE.equals(flagCut)) line.setPrice(order.getAmto());
    //            
    //            // 判断商品的配送方式和市场设置的 是否有冲突
    //            checkMtypeGoods(goods, mapMType, dto.getDistributionType());
    //            
    //            BigDecimal num = new BigDecimal(line.getNum());
    //            if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
    //                && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
    //            {
    //                line.setPricen(space.getPriceMember());
    //                if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
    //                {
    //                    BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
    //                    reducePrice = reducePrice.add(subtract.multiply(num));
    //                }
    //            }
    //            else
    //            {
    //                line.setPricen(space.getPrice());
    //            }
    //            line.setCouponPrice(line.getPricen());
    //            line.setCouponAmt(line.getPricen().multiply(num));
    //            if (goods.getMType().equals(MType.MARKET_GOODS)) isCard = true;
    //            if (goods.getMType().equals(MType.CUT_GOODS))
    //                amto = amto.add(line.getPrice().multiply(num));
    //            else
    //            {
    //                amto = amto.add(line.getPricen().multiply(num)).setScale(2);
    //            }
    //            weight = weight.add(space.getWeight().multiply(num));
    //            if ((DistributionType.IMMEDIATELY.equals(order.getDistributionType())
    //                || DistributionType.ORDERED.equals(order.getDistributionType())) && goods.getIsPostage() != null
    //                && !goods.getIsPostage())
    //            {
    //                postageWeight = postageWeight.add(space.getWeight().multiply(num));
    //                isPostage = false;
    //            }
    //        }
    //        order.setWeight(weight);
    //        order.setAmto(amto);
    //        order.setReducePrice(reducePrice);
    //        
    //        SysFarmerConfig config = sysFarmerConfigDao.get(order.getFarmer());
    //        BigDecimal postage = BigDecimal.ZERO;
    //        if (!isPostage)
    //        {
    //            if (config == null || config.getPkey().equals(Constant.Operation + appid)
    //                || config.getDistributionConfig() == null || Boolean.TRUE.equals(config.getDistributionConfig()))
    //            {
    //                postage = loadPostage(config, postageWeight, amto.subtract(reducePrice));
    //            }
    //            else
    //            {
    //                postage = loadPostageFee(config, amto.subtract(reducePrice));
    //            }
    //        }
    //        order.setOldPostage(postage);
    //        order.setPostage(postage);
    //        log.info("ins_order_postage: {}", order.getPostage());
    //        if (order.getDistributionType() != null && DistributionType.PICKUP.equals(order.getDistributionType()))
    //            order.setAmtall(order.getAmto());
    //        else
    //        {
    //            order.setAmtall(order.getAmto().add(order.getPostage()));
    //        }
    //        if (order.getOrderType().equals(OrderType.MARKET_ORDER) || order.getOrderType().equals(OrderType.GIFT_ORDER))
    //            order.setPointn(oi.getPointn());
    //        else
    //            order.setPointn(0);
    //        order.setCommn(dto.getCommn());
    //        if (isCard) order.setCard(dto.getCard());
    //        // TODO 2022-07-21 注释掉  
    //        //        order.setCutAmt(BigDecimal.ZERO);
    //        
    //        if (dto.getCardPostage() != null && postage.compareTo(BigDecimal.ZERO) > 0)
    //        {
    //            //            MktMemberCard card = checkCardPostage(dto.getMember(), order.getFarmer(), dto.getCardPostage(), postage);
    //            MktMemberCard card = checkCard(goodsMap,
    //                spaceMap,
    //                dto.getMember(),
    //                order.getCardPostage(),
    //                lines,
    //                order.getFarmer(),
    //                order.getDistributionType(),
    //                CardCouponType.POSTAGE_COUPON);
    //            if (card != null)
    //            {
    //                order.setCardPostage(card.getPkey());
    //                if (Boolean.TRUE.equals(card.getAvoidPostage()))
    //                {
    //                    order.setPostage(BigDecimal.ZERO);
    //                    order.setCardPostageAmt(postage);
    //                }
    //                else
    //                {
    //                    postage = postage.subtract(card.getCost());
    //                    if (postage.compareTo(BigDecimal.ZERO) < 0)
    //                    {
    //                        postage = BigDecimal.ZERO;
    //                        order.setCardPostageAmt(postage);
    //                    }
    //                    else
    //                        order.setCardPostageAmt(card.getCost());
    //                    order.setPostage(postage);
    //                }
    //            }
    //        }
    //        if (DistributionType.DINE_IN.equals(dto.getDistributionType())) order.setPostage(BigDecimal.ZERO);
    //        
    //        if (order.getCard() != null)
    //        {
    //            // 校验该卡券是否可用
    //            MktMemberCard checkCard = checkCard(goodsMap,
    //                spaceMap,
    //                dto.getMember(),
    //                order.getCard(),
    //                lines,
    //                order.getFarmer(),
    //                order.getDistributionType(),
    //                CardCouponType.GOODS_COUPON);
    //            order.setCardAmt(checkCard.getCost());
    //        }
    //        else
    //        {
    //            order.setCardAmt(BigDecimal.ZERO);
    //        }
    //        BigDecimal subtract = order.getAmtall().subtract(order.getCardAmt()).subtract(order.getCardPostageAmt());
    //        if (subtract.compareTo(BigDecimal.ZERO) <= 0) subtract = new BigDecimal(0.01);
    //        order.setAmtn(subtract);
    //        order = orderDao.update(order);
    //        if (order.getCardAmt().compareTo(BigDecimal.ZERO) > 0)
    //        {
    //            calculateCouponAmt(lines, order.getCardAmt());
    //        }
    //        if (lines.size() == 1)
    //        {
    //            MktOrderLine orderLine = lines.get(0);
    //            if (orderLine.getCouponAmt().compareTo(BigDecimal.ZERO) == 0)
    //                orderLine.setCouponAmt(new BigDecimal("0.01"));
    //        }
    //        orderLineDao.updateAll(lines);
    //        MktOrderDesc desc = new MktOrderDesc();
    //        desc.setPkey(order.getPkey());
    //        desc.setLatitude(BigDecimal.ZERO);
    //        desc.setLongitude(BigDecimal.ZERO);
    //        desc.setAscription(appid);
    //        Integer qrCode = MobileSession.qrCode();
    //        if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
    //        {
    //            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
    //            {
    //                MktDesktop mktDesktop = desktopDao.get(dto.getAddr().getPkey());
    //                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
    //            }
    //            else if (qrCode != null)
    //            {
    //                MktDesktop mktDesktop = desktopDao.get(qrCode);
    //                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
    //            }
    //            MktMember member = memberDao.get(MobileSession.memberPkey());
    //            if (member != null)
    //            {
    //                desc.setName(member.getName());
    //                desc.setMobile(member.getMobile());
    //            }
    //            desc.setDistance(BigDecimal.ZERO);
    //        }
    //        else if (dto.getAddr() != null)
    //        {
    //            MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
    //            desc.setAddr(addrObj.getAddr());
    //            if (StringUtils.isNotBlank(addrObj.getAddrDetail())) desc.setAddr(desc.getAddr() + addrObj.getAddrDetail());
    //            desc.setName(addrObj.getName());
    //            desc.setMobile(addrObj.getMobile());
    //            desc.setLatitude(addrObj.getLatitude());
    //            desc.setLongitude(addrObj.getLongitude());
    //            desc.setRemark(dto.getRemark());
    //            if (config != null && config.getLatitude() != null && config.getLongitude() != null)
    //            {
    //                Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
    //                    config.getLongitude().doubleValue(),
    //                    addrObj.getLatitude().doubleValue(),
    //                    addrObj.getLongitude().doubleValue());
    //                BigDecimal distance = new BigDecimal(a.toString());
    //                desc.setDistance(distance);
    //            }
    //            else
    //                desc.setDistance(BigDecimal.ZERO);
    //        }
    //        orderDescDao.put(desc);
    //        return order;
    //    }
    
    // 检验卡券是否可以用 下订单时候使用
    private MktMemberCard checkCard(Map<Integer, MktGoods> goodsMap, Map<Integer, MktGoodsSpace> spaceMap,
        Integer member, Integer pkey, List<MktOrderLine> orderlines, String farmer, DistributionType distributionType,
        CardCouponType cardCouponType)
    {
        Map<Integer, GoodsCardInfo> cardMap = new HashMap<>();
        for (MktOrderLine ol : orderlines)
        {
            Integer goodsKey = ol.getGoods().intValue();
            MktGoodsSpace space = spaceMap.get(ol.getSpace().intValue());
            if (cardMap.containsKey(goodsKey))
            {
                GoodsCardInfo goodsCardInfo = cardMap.get(goodsKey);
                BigDecimal cost = space.getPrice().multiply(BigDecimal.valueOf(ol.getNum()));
                goodsCardInfo.setCost(goodsCardInfo.getCost().add(cost));
            }
            else
            {
                MktGoods goods = goodsMap.get(goodsKey);
                GoodsCardInfo goodsCardInfo = new GoodsCardInfo();
                goodsCardInfo.setFarmer(goods.getFarmer());
                goodsCardInfo.setUserType(goods.getGtype());
                goodsCardInfo.setUserGoods(goodsKey);
                goodsCardInfo.setCost(space.getPrice().multiply(BigDecimal.valueOf(ol.getNum())));
                cardMap.put(goodsKey, goodsCardInfo);
            }
        }
        return checkCard(pkey, goodsMap, spaceMap, farmer, member, cardMap, distributionType, cardCouponType);
    }
    
    private MktMemberCard checkCard(Integer pkey, Map<Integer, MktGoods> goodsMap, Map<Integer, MktGoodsSpace> spaceMap,
        String farmer, Integer member, Map<Integer, GoodsCardInfo> cardMap, DistributionType distributionType,
        CardCouponType cardCouponType)
    {
        String time = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        MktMemberCard card = memberCardDao.selectOne()
            .eq("status", CardStatus.UNUSED)
            .eq("member", member)
            .eq("pkey", pkey)
            .eq("type", cardCouponType)
            .eq("invalid", false)
            .ge("endDate", time)
            .exec();
        System.out.println("checkCard-card: " + card);
        if (card == null) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        if (StringUtils.isNotBlank(card.getUserFarmer()) && !card.getUserFarmer().equals(farmer))
            throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        
        // 如果是活动卡券，检查活动限制
        if (card.getActivity() != null)
        {
            MktActivity activity = activityDao.get(card.getActivity());
            if (activity != null && activity.getLimitDailyCardNum() != -1)
            {
                long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                    member,
                    CardStatus.USED,
                    cn.tofocus.lejia.utils.DateUtil.atStartOfToday(),
                    cn.tofocus.lejia.utils.DateUtil.atStartOfTomorrow());
                if (usedNum >= activity.getLimitDailyCardNum())
                    throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该活动优惠券已达到今日使用上限");
            }
        }
        if (card.getUserOrderType() != null)
        {
            if (card.getUserOrderType() == CardUserOrderType.PICKUP && distributionType != DistributionType.PICKUP)
                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该优惠券仅支持自提使用");
            if (card.getUserOrderType() == CardUserOrderType.DELIVERY
                && distributionType != DistributionType.IMMEDIATELY && distributionType != DistributionType.ORDERED)
                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该优惠券仅支持配送使用");
        }
        if (card.getUserGoods() != null)
        {
            if (!cardMap.containsKey(card.getUserGoods()))
            {
                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
            }
            GoodsCardInfo cardInfo = cardMap.get(card.getUserGoods());
            if (cardInfo.getCost().compareTo(card.getLimitCost()) == -1)
                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        }
        else if (card.getUserType() != null)
        {
            Boolean userType = true;
            Boolean userTypeCost = true;
            for (Integer key : cardMap.keySet())
            {
                GoodsCardInfo info = cardMap.get(key);
                if (info.getUserType() != null && card.getUserType().equals(info.getUserType()))
                {
                    userType = false;
                    if (info.getCost().compareTo(card.getLimitCost()) != -1)
                    {
                        userTypeCost = false;
                    }
                }
            }
            if (userType) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
            if (userTypeCost) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        }
        else
        {
            BigDecimal cost = BigDecimal.ZERO;
            for (Integer key : cardMap.keySet())
            {
                GoodsCardInfo info = cardMap.get(key);
                cost = cost.add(info.getCost());
            }
            if (cost.compareTo(card.getLimitCost()) == -1) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        }
        return card;
    }
    
    private void calculateCouponAmt(List<MktOrderLine> addOrderlines, BigDecimal zCardAmt)
    {
        BigDecimal sumAmt = BigDecimal.ZERO;
        for (MktOrderLine ol : addOrderlines)
        {
            sumAmt = sumAmt.add(ol.getPricen().multiply(new BigDecimal(ol.getNum())));
        }
        BigDecimal remainCardAmt = zCardAmt;
        
        // 遍历按比例计算分配优惠金额（精确到分，多余部分舍去）
        for (MktOrderLine ol : addOrderlines)
        {
            BigDecimal pricen = ol.getPricen();
            BigDecimal olNum = new BigDecimal(ol.getNum());
            // 明细优惠 = 原单价 * 数量 * 优惠券总金额 / 商品总金额
            BigDecimal discount = pricen.multiply(olNum).multiply(zCardAmt).divide(sumAmt, 2, RoundingMode.DOWN);
            ol.setCouponAmt(pricen.multiply(olNum).subtract(discount));
            if (ol.getCouponAmt().compareTo(BigDecimal.ZERO) < 0) ol.setCouponAmt(BigDecimal.ZERO);
            ol.setCouponPrice(ol.getCouponAmt().divide(olNum, 2, RoundingMode.HALF_UP));
            remainCardAmt = remainCardAmt.subtract(discount);
        }
        
        // 最终如果有剩余优惠金额（被舍去的部分），遍历每条明细给最多（数量*0.01），如果商品优惠价格不够减了跳过，知道没有剩余优惠金额
        while (remainCardAmt.compareTo(BigDecimal.ZERO) > 0)
        {
            for (int i = 0; i < addOrderlines.size() && remainCardAmt.compareTo(BigDecimal.ZERO) > 0; i++)
            {
                MktOrderLine ol = addOrderlines.get(i);
                BigDecimal couponAmt = ol.getCouponAmt();
                BigDecimal delta = new BigDecimal("0.01");
                if(couponAmt.compareTo(BigDecimal.ZERO) == 0)
                    remainCardAmt = remainCardAmt.subtract(delta);
                if (couponAmt.compareTo(delta) < 0) continue;
                ol.setCouponAmt(couponAmt.subtract(delta));
                ol.setCouponPrice(ol.getCouponAmt().divide(new BigDecimal(ol.getNum()), 2, RoundingMode.HALF_UP));
                remainCardAmt = remainCardAmt.subtract(delta);
            }
        }
    }
    
    private BigDecimal loadPostageFee(SysFarmerConfig farmerConfig, BigDecimal amto)
    {
        
        if (farmerConfig.getIsFree() != null && farmerConfig.getIsFree() && farmerConfig.getFreeDelivery() != null
            && amto.compareTo(farmerConfig.getFreeDelivery()) >= 0)
        {
            return BigDecimal.ZERO;
        }
        BigDecimal res = farmerConfig.getFee() == null ? BigDecimal.ZERO : farmerConfig.getFee();
        
        if (farmerConfig.getIsReductionTwo() != null && farmerConfig.getIsReductionTwo()
            && amto.compareTo(farmerConfig.getReachTwo()) >= 0 && farmerConfig.getReductionDeliveryTwo() != null)
        {
            System.out.println("loadPostageFee满减减免运费 2 ");
            res = res.subtract(farmerConfig.getReductionDeliveryTwo());
        }
        else if (farmerConfig.getIsReductionOne() != null && farmerConfig.getIsReductionOne()
            && amto.compareTo(farmerConfig.getReachOne()) >= 0 && farmerConfig.getReductionDeliveryOne() != null)
        {
            System.out.println("loadPostageFee满减减免运费 1 ");
            res = res.subtract(farmerConfig.getReductionDeliveryOne());
        }
        if (res.compareTo(BigDecimal.ZERO) < 0)
        {
            res = BigDecimal.ZERO;
        }
        return res;
    }
    
    /*
     * 计算邮费
     */
    private BigDecimal loadPostage(SysFarmerConfig farmerConfig, BigDecimal weight, BigDecimal amto)
    {
        List<MktPostageConfig> list =
            postageConfigDao.select().eq("farmer", farmerConfig.getPkey()).sort("weight", true).exec();
        log.info("计算邮费传进来的数据:  weight: {}, amto: {}", weight, amto);
        if (farmerConfig.getIsFree() != null && farmerConfig.getIsFree() && farmerConfig.getFreeDelivery() != null
            && amto.compareTo(farmerConfig.getFreeDelivery()) >= 0)
        {
            return BigDecimal.ZERO;
        }
        int j = -1;
        for (int i = 0; i < list.size(); i++)
        {
            MktPostageConfig mktPostageConfig = list.get(i);
            if (weight.compareTo(mktPostageConfig.getWeight()) >= 0)
            {
                j = i;
                break;
            }
        }
        if (j < 0)
        {
            return list.get(list.size() - 1).getPostage();
        }
        
        if (j > 0)
        {
            j = j - 1;
        }
        BigDecimal postage = list.get(j).getPostage();
        // 满减减免运费
        if (farmerConfig.getIsReductionTwo() != null && farmerConfig.getIsReductionTwo()
            && amto.compareTo(farmerConfig.getReachTwo()) >= 0 && farmerConfig.getReductionDeliveryTwo() != null)
        {
            System.out.println("满减减免运费 2 ");
            postage = postage.subtract(farmerConfig.getReductionDeliveryTwo());
        }
        else if (farmerConfig.getIsReductionOne() != null && farmerConfig.getIsReductionOne()
            && amto.compareTo(farmerConfig.getReachOne()) >= 0 && farmerConfig.getReductionDeliveryOne() != null)
        {
            System.out.println("满减减免运费 1 ");
            postage = postage.subtract(farmerConfig.getReductionDeliveryOne());
        }
        if (postage.compareTo(BigDecimal.ZERO) < 0)
        {
            postage = BigDecimal.ZERO;
        }
        return postage;
    }
    
    private void checkMtypeGoods(MktGoods goods, Map<MType, SysFarmerMtype> mapMType, DistributionType dt)
    {
        if (mapMType != null)
        {
            MType mtype = MType.MARKET_GOODS;
            if (goods.getMType().equals(MType.PRESALE_GOODS))
            {
                mtype = MType.PRESALE_GOODS;
            }
            else if (goods.getMType().equals(MType.COLLAGE_GOODS))
            {
                mtype = MType.COLLAGE_GOODS;
            }
            else if (goods.getMType().equals(MType.CUT_GOODS))
            {
                mtype = MType.CUT_GOODS;
            }
            if (mapMType.containsKey(mtype))
            {
                SysFarmerMtype farmerMtype = mapMType.get(mtype);
                if (dt != null && DistributionType.PICKUP.equals(dt) && !farmerMtype.getPickup())
                {
                    throw TofocusException.of(LejiaErrCode.GWC_DELIVERY_PICKUP_ERROR,
                        "商品名: " + goods.getTitle() + "  不能自提");
                }
                if (dt != null && DistributionType.IMMEDIATELY.equals(dt) && !farmerMtype.getDelivery())
                {
                    throw TofocusException.of(LejiaErrCode.GWC_DELIVERY_PICKUP_ERROR,
                        "商品名: " + goods.getTitle() + "  不能配送");
                }
            }
        }
    }
    
    //    private void checkBugGoodsNumOrderLine(List<MktOrderLine> lines, Map<Integer, MktGoods> goodsMap)
    //    {
    //        Map<Integer, Integer> checkNumMap = new HashMap<>();
    //        for (MktOrderLine gwc : lines)
    //        {
    //            if (checkNumMap.containsKey(gwc.getGoods()))
    //            {
    //                checkNumMap.put(gwc.getGoods(), checkNumMap.get(gwc.getGoods()) + gwc.getNum());
    //            }
    //            else
    //            {
    //                checkNumMap.put(gwc.getGoods(), gwc.getNum());
    //            }
    //        }
    //        for (Integer key : checkNumMap.keySet())
    //        {
    //            if (!goodsMap.containsKey(key)) continue;
    //            MktGoods goods = goodsMap.get(key);
    //            // 校验是否超出每日限购
    //            getBuyGoodsNum(goods, checkNumMap.get(key));
    //        }
    //    }
    
    // 校验是否超出每日限购
    private Boolean getBuyGoodsNum(MktGoods goods, int num)
    {
        long l = System.currentTimeMillis();
        Integer purchaseNum = goods.getPurchaseNum();
        if (purchaseNum == null || purchaseNum == 0) return true;
        Integer memberPkey = MobileSession.memberPkey();
        List<MktOrder> exec = orderDao.select()
            .eq("member", memberPkey)
            .eq(substring(f("createdTime"), 1, 10), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .exec();
        List<Integer> pkeys = new ArrayList<>();
        for (MktOrder o : exec)
        {
            pkeys.add(o.getPkey());
        }
        Number sum = 0;
        if (pkeys.isEmpty())
            sum = 0;
        else
        {
            sum =
                orderLineDao.aggregation().eq("goods", goods.getPkey()).in("orderPkey", pkeys.toArray()).execSum("num");
            if (sum == null) sum = 0;
        }
        System.out.println("sum" + sum.intValue());
        System.out.println("结束: " + (System.currentTimeMillis() - l));
        // 前端有用到判断该异常的code，请勿修改报错
        if (sum.intValue() >= purchaseNum || (sum.intValue() + num) > purchaseNum)
            throw TofocusException.of(LejiaErrCode.GOODS_NUM_PURCHASENUM,
                "商品: " + goods.getTitle() + "  每人每天限购: " + purchaseNum + "件,请明日再来购买");
        return true;
    }
    
    private List<MktOrder> insAllOrder(OrderTotalV3Info dto, Integer memberPkey, String orderNumber)
    {
        List<MktOrder> orderList = new ArrayList<>();
        List<Integer> selectCards = new ArrayList<>();
        Map<Integer,MktSupplier> sMap = new HashMap<>();
        // 拆分的子订单序号（用于计数）
        int i = 0;
        for (OrderV3Info oi : dto.getInfos())
        {
            i++;
            MktOrder order = new MktOrder();
            order.setMember(memberPkey);
            order.setStatus(OrderStatus.UNPAID_ORDER);
            order.setSettlementType(SettlementType.NOT_START);
            Map<MType, SysFarmerMtype> mapMType = null;
            if (StringUtils.isNotBlank(oi.getFarmer())
                && !(Constant.Operation + MobileSession.appid()).equals(oi.getFarmer()))
            {
                order.setCode(orderNumber + "2");
                order.setOrderOir(OrderOir.MARKET_MALL);
                order.setOrderType(OrderType.MARKET_ORDER);
            }
            else
            {
                order.setOrderOir(OrderOir.POINTS_MALL);
                order.setOrderType(OrderType.INTEGRAL_ORDER);
                if (oi.getSupplier() != null)
                    order.setCode(orderNumber + oi.getSupplier() + i + "1");
                else
                    order.setCode(orderNumber + "1");
            }
            if (StringUtils.isNotBlank(oi.getFarmer()))
                mapMType = sysFarmerMtypeDao.mapMType(oi.getFarmer());
            orderDao.generateID(order);
            order.setCgCheck(0);
            order.setPayType(dto.getPayType());
            order.setPstime(oi.getPstime());
            
            order.setTjr(oi.getTjr());
            if (StringUtils.isNotBlank(oi.getFarmer()))
            {
                order.setFarmer(oi.getFarmer());
                SysFarmer sysFarmer = sysFarmerDao.get(oi.getFarmer());
                order.setCompany(sysFarmer.getOrg());
            }
            else
            {
                order.setFarmer(Constant.Operation + MobileSession.appid());
                order.setCompany(Constant.Operation + MobileSession.appid());
            }
            order.setAscription(MobileSession.appid());
            order.setDistributionType(oi.getDistributionType());
            order.setIsBox(false);
            
            BigDecimal amto = BigDecimal.ZERO;
            int pointn = 0;
            BigDecimal weight = BigDecimal.ZERO;
            BigDecimal postageWeight = BigDecimal.ZERO;
            Boolean isPostage = true;
            List<Integer> gwcIds = new ArrayList<>();
//            Boolean isCard = false;
            
            List<Integer> gkeys = new ArrayList<>();
            List<Integer> skeys = new ArrayList<>();
            oi.getGoodsList().forEach(e -> skeys.add(e.getSpace()));
            Map<Integer, MktGoodsSpace> spaceMap = new HashMap<>();
            List<MktGoodsSpace> listGoodsSpace = goodsSpaceDao.listGoodsSpace(skeys);
            listGoodsSpace.forEach(e -> {
                spaceMap.put(e.getPkey(), e);
                gkeys.add(e.getGoods());
            });
            
            Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
            
            // 商品限购校验
            checkBugGoodsNum(oi.getGoodsList(), goodsMap, spaceMap);
            
            List<MktOrderLine> addOrderlines = new ArrayList<>();
            BigDecimal reducePrice = BigDecimal.ZERO;
            for (OrderGoodsV3OnList line : oi.getGoodsList())
            {
                if (!spaceMap.containsKey(line.getSpace()))
                {
                    throw TofocusException.of(LejiaErrCode.GWC_SPACE_NOTEXIST);
                }
                MktGoodsSpace mktGoodsSpace = spaceMap.get(line.getSpace());
                if (!goodsMap.containsKey(mktGoodsSpace.getGoods()))
                {
                    throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
                }
                MktGoods goods = goodsMap.get(mktGoodsSpace.getGoods());
                if (oi.getSf() == null && goods.getSupplier() != null)
                {
                    order.setSupplier(goods.getSupplier());
                    MktSupplier supplier = supplierDao.get(goods.getSupplier());
                    oi.setSf(false);
                    if (supplier != null)
                    {
                        if (supplier.getAllowedPickup() != null) oi.setPickup(supplier.getAllowedPickup());
                        if (supplier.getAllowedDelivery() != null) oi.setDelivery(supplier.getAllowedDelivery());
                        if (StringUtils.isNotBlank(supplier.getSfMonthlyCard())
                            && StringUtils.isNotBlank(supplier.getSfAppId())
                            && StringUtils.isNotBlank(supplier.getSfSk()))
                        {
                            oi.setSf(true);
                            sMap.put(goods.getSupplier(), supplier);
                        }
                    }
                }
                switch (goods.getMType())
                {
                    case INTEGRAL_GOODS:
                        order.setOrderType(OrderType.INTEGRAL_ORDER);
                        break;
                    case MARKET_GOODS:
                        order.setOrderType(OrderType.MARKET_ORDER);
                        break;
                    case SHARE_GOODS:
                        order.setOrderType(OrderType.SHARE_ORDER);
                        break;
                    case CUT_GOODS:
                        order.setOrderType(OrderType.CUT_ORDER);
                        break;
                    case COLLAGE_GOODS:
                        order.setOrderType(OrderType.COLLAGE_ORDER);
                        break;
                    case PRESALE_GOODS:
                        order.setOrderType(OrderType.PRESALE_ORDER);
                        break;
                    case GIFT_GOODS:
                        order.setOrderType(OrderType.GIFT_ORDER);
                        break;
                    case COUPON_GOODS:
                        order.setOrderType(OrderType.COUPON_ORDER);
                        break;
                    case INTEGRAL_PRESALE_GOODS:
                        order.setOrderType(OrderType.INTEGRAL_PRESALE_ORDER);
                        break;
                    case INTEGRAL_BNYP_GOODS:
                        order.setOrderType(OrderType.INTEGRAL_BNYP_ORDER);
                        break;
                    case INTEGRAL_MSD_GOODS:
                        order.setOrderType(OrderType.INTEGRAL_MSD_ORDER);
                        break;
                }
                // 判断商品的配送方式和市场设置的 是否有冲突
                checkMtypeGoods(goods, mapMType, oi.getDistributionType());
                
                MktOrderLine orderLine = new MktOrderLine();
                orderLine.setStatus(order.getStatus());
                orderLine.setOrderPkey(order.getPkey());
                orderLine.setGoods((long)mktGoodsSpace.getGoods());
                orderLine.setSpace((long)line.getSpace());
                orderLine.setSpaceName(mktGoodsSpace.getSpace());
                orderLine.setGoodsName(goods.getTitle());
                orderLine.setAscription(mktGoodsSpace.getAscription());
                orderLine.setPoint(mktGoodsSpace.getPoint());
                if(mktGoodsSpace.getWeight() != null)
                    orderLine.setWeight(mktGoodsSpace.getWeight().multiply(new BigDecimal(line.getNum())));
                
                if (goods.getMType().equals(MType.COUPON_GOODS))
                    orderLine.setCard(Integer.valueOf(goods.getExtendCon()));
                if (goods.getMType().equals(MType.BOX_GOODS))
                {
                    MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
                    if (goodsBox != null)
                    {
                        order.setLockId(goodsBox.getLockId());
                    }
                    order.setBoxSd(mktGoodsSpace.getBoxSd());
                    order.setBoxEd(mktGoodsSpace.getBoxEd());
                    order.setIsBox(true);
                    order.setBoxTime(orderLine.getSpaceName());
                    order.setBoxName(orderLine.getGoodsName());
                }
                //                body = body + goods.getTitle() + " ";
                
                // 校验库存及下架 
                checkGoodsKcNum(goods, mktGoodsSpace, line.getNum());
                orderLine.setPrice(mktGoodsSpace.getPriceOld());
                if (mktGoodsSpace.getPriceMember().compareTo(BigDecimal.ZERO) > 0
                    && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
                {
                    orderLine.setPricen(mktGoodsSpace.getPriceMember());
                    if (mktGoodsSpace.getPriceMember().compareTo(BigDecimal.ZERO) == 1)
                    {
                        BigDecimal subtract = mktGoodsSpace.getPrice().subtract(mktGoodsSpace.getPriceMember());
                        reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(line.getNum())));
                    }
                }
                else
                {
                    orderLine.setPricen(mktGoodsSpace.getPrice());
                }
                orderLine.setNum(line.getNum());
                BigDecimal num = new BigDecimal(orderLine.getNum());
                orderLine.setFarmer(goods.getFarmer());
                orderLine.setCompany(goods.getCompany());
                orderLine.setCouponPrice(orderLine.getPricen());
                orderLine.setCouponAmt(orderLine.getPricen().multiply(num));
                orderLine.setGtype(goods.getGtype());
                if (goods.getMType().equals(MType.PROCESS_GOODS))
                {
                    orderLine.setAssociation(line.getAssociation());
                    orderLine.setAssociationName(line.getAssociationName());
                }
//                if (goods.getMType().equals(MType.MARKET_GOODS) || goods.getMType().equals(MType.SPECIAL_GOODS)
//                    || goods.getMType().equals(MType.BOX_GOODS) || goods.getMType().equals(MType.PROCESS_GOODS))
//                    isCard = true;
                if (goods.getMType().equals(MType.CUT_GOODS))
                    amto = amto.add(orderLine.getPrice().multiply(num));
                else
                {
                    amto = amto.add(orderLine.getPricen().multiply(num)).setScale(2);
                }
                pointn = pointn + (mktGoodsSpace.getPoint() * orderLine.getNum());
                if (mktGoodsSpace.getWeight() != null) weight = weight.add(mktGoodsSpace.getWeight().multiply(num));
                if ((DistributionType.IMMEDIATELY.equals(order.getDistributionType())
                    || DistributionType.ORDERED.equals(order.getDistributionType())) && goods.getIsPostage() != null
                    && !goods.getMType().equals(MType.GIFT_GOODS) && !goods.getIsPostage())
                {
                    postageWeight = postageWeight.add(mktGoodsSpace.getWeight().multiply(num));
                    isPostage = false;
                }
                if (line.getGwcPkey() != null) gwcIds.add(line.getGwcPkey());
                addOrderlines.add(orderLine);
            }
            order.setReducePrice(reducePrice);
            order.setWeight(weight);
            order.setAmto(amto);
            order.setPointn(pointn);
            
            SysFarmerConfig config = sysFarmerConfigDao.get(order.getFarmer());
            if(config != null)
                order.setCommissionType(config.getCommissionType());
            MktOrderDesc desc = new MktOrderDesc();
            desc.setPkey(order.getPkey());
            desc.setLatitude(BigDecimal.ZERO);
            desc.setLongitude(BigDecimal.ZERO);
            desc.setAscription(order.getAscription());
            Integer qrCode = MobileSession.qrCode();
            if (DistributionType.DINE_IN.equals(order.getDistributionType()))
            {
                if (dto.getAddrPkey() != null)
                {
                    MktDesktop mktDesktop = desktopDao.get(dto.getAddrPkey());
                    if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
                }
                else if (qrCode != null)
                {
                    MktDesktop mktDesktop = desktopDao.get(qrCode);
                    if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
                }
                MktMember member = memberDao.get(MobileSession.memberPkey());
                if (member != null)
                {
                    desc.setName(member.getName());
                    desc.setMobile(member.getMobile());
                }
                desc.setDistance(BigDecimal.ZERO);
            }
            else if (dto.getAddrPkey() != null)
            {
                MktAddr addrObj = addrDao.get(dto.getAddrPkey());
                desc.setAddr(addrObj.getAddr());
                if (StringUtils.isNotBlank(addrObj.getAddrDetail()))
                    desc.setAddr(desc.getAddr() + addrObj.getAddrDetail());
                if (DistributionType.PICKUP.equals(order.getDistributionType()))
                {
                    if (oi.getPickupPkey() != null)
                    {
                        if (OrderOir.MARKET_MALL.equals(order.getOrderOir()))
                        {
                            SysFarmerPickupLocation sysFarmerPickupLocation =
                                farmerPickupLocationDao.get(oi.getPickupPkey());
                            if (sysFarmerPickupLocation != null)
                            {
                                desc.setAddr(sysFarmerPickupLocation.getAddress());
                            }
                        }
                        else
                        {
                            MktSupplierPickupLocation mktSupplierPickupLocation =
                                supplierPickupLocationDao.get(oi.getPickupPkey());
                            if (mktSupplierPickupLocation != null) desc.setAddr(mktSupplierPickupLocation.getAddress());
                        }
                    }
                    else
                    {
//                        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(order.getFarmer());
                        if (config != null) desc.setAddr(config.getAddr());
                    }
                }
                desc.setName(addrObj.getName());
                desc.setMobile(addrObj.getMobile());
                desc.setLatitude(addrObj.getLatitude());
                desc.setLongitude(addrObj.getLongitude());
                desc.setPro(addrObj.getPro());
                desc.setCity(addrObj.getCity());
                desc.setArea(addrObj.getArea());
                desc.setRemark(oi.getRemark());
                if (config != null && config.getLatitude() != null && config.getLongitude() != null)
                {
                    Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                        config.getLongitude().doubleValue(),
                        addrObj.getLatitude().doubleValue(),
                        addrObj.getLongitude().doubleValue());
                    BigDecimal distance = new BigDecimal(a.toString());
                    desc.setDistance(distance);
                }
                else
                    desc.setDistance(BigDecimal.ZERO);
            }
            else if(dto.getAddrPkey() == null && DistributionType.PICKUP.equals(order.getDistributionType()))
            {
                MktAddr defaultAddrPickup = addrDao.getDefaultAddrPickup(memberPkey);
                if(defaultAddrPickup == null)
                {
                    MktMember member = memberDao.get(memberPkey);
                    if (member != null)
                    {
                        desc.setName(member.getName());
                        desc.setMobile(member.getMobile());
                    }
                }
                else
                {
                    desc.setName(defaultAddrPickup.getName());
                    desc.setMobile(defaultAddrPickup.getMobile());
                }
                if (oi.getPickupPkey() != null)
                {
                    if (OrderOir.MARKET_MALL.equals(order.getOrderOir()))
                    {
                        SysFarmerPickupLocation sysFarmerPickupLocation =
                            farmerPickupLocationDao.get(oi.getPickupPkey());
                        if (sysFarmerPickupLocation != null)
                        {
                            desc.setAddr(sysFarmerPickupLocation.getAddress());
                        }
                    }
                    else
                    {
                        MktSupplierPickupLocation mktSupplierPickupLocation =
                            supplierPickupLocationDao.get(oi.getPickupPkey());
                        if (mktSupplierPickupLocation != null) desc.setAddr(mktSupplierPickupLocation.getAddress());
                    }
                }
                else
                {
                    if (config != null) desc.setAddr(config.getAddr());
                }
                
                
            }
            orderDescDao.add(desc);
            
           
            BigDecimal postage = BigDecimal.ZERO;
            if (!isPostage)
            {
                if (config.getPkey().equals(Constant.Operation + order.getAscription()))
                {
                    if (config.getIsFree() != null && config.getIsFree() && config.getFreeDelivery() != null
                        && amto.compareTo(config.getFreeDelivery()) >= 0)
                    {
                        postage = BigDecimal.ZERO;
                    }
                    else if (Boolean.TRUE.equals(oi.getSf()))
                    {
                        // 调用顺丰的接口 计算配送费
                        MktSupplier supplier = sMap.get(oi.getSupplier());
                        try
                        {
                            SfGetFreightAddedServicesResult servicesResult = expressSfManager.getFreightAddedServices(supplier.getSfAppId(), supplier.getSfSk(), 
                                supplier.getSfMonthlyCard(), 
                                supplier.getExpressPro(), supplier.getExpressCity(), 
                                supplier.getExpressAddress(), 
                                desc.getPro(), desc.getCity(), desc.getAddr(), addOrderlines.get(0).getGoodsName(),
                                postageWeight);
                            postage = servicesResult.getTotalBD();
                        }
                        catch (Exception e)
                        {
                            log.error("请求顺丰配送费报错:  ", e);
                            postage = loadPostage(config, postageWeight, amto.subtract(reducePrice));
                        }
                    }
                    else
                        postage = loadPostage(config, postageWeight, amto.subtract(reducePrice));
                }
                else
                {
                    if (config == null || config.getDistributionConfig() == null
                        || Boolean.TRUE.equals(config.getDistributionConfig()))
                    {
                        postage = loadPostage(config, postageWeight, amto.subtract(reducePrice));
                    }
                    else
                    {
                        postage = loadPostageFee(config, amto.subtract(reducePrice));
                    }
                }
            }
            order.setOldPostage(postage);
            order.setPostage(postage);
            order.setCardPostageAmt(BigDecimal.ZERO);
            if (oi.getCardPostage() != null && postage.compareTo(BigDecimal.ZERO) > 0)
            {
                order.setCardPostage(oi.getCardPostage());
                MktMemberCard card = checkCard(goodsMap,
                    spaceMap,
                    order.getMember(),
                    order.getCardPostage(),
                    addOrderlines,
                    order.getFarmer(),
                    order.getDistributionType(),
                    CardCouponType.POSTAGE_COUPON);
                if (card != null)
                {
                    order.setCardPostage(card.getPkey());
                    if (Boolean.TRUE.equals(card.getAvoidPostage()))
                    {
                        order.setPostage(BigDecimal.ZERO);
                        order.setCardPostageAmt(postage);
                    }
                    else
                    {
                        postage = postage.subtract(card.getCost());
                        if (postage.compareTo(BigDecimal.ZERO) < 0)
                        {
                            postage = BigDecimal.ZERO;
                            order.setCardPostageAmt(order.getOldPostage());
                        }
                        else
                            order.setCardPostageAmt(card.getCost());
                        order.setPostage(postage);
                    }
                }
            }
            if (DistributionType.DINE_IN.equals(order.getDistributionType())) order.setPostage(BigDecimal.ZERO);
            
            log.info("ins_order_postage: {}", order.getPostage());
            if (order.getDistributionType() != null && DistributionType.PICKUP.equals(order.getDistributionType()))
                order.setAmtall(order.getAmto());
            else
            {
                order.setAmtall(order.getAmto().add(order.getOldPostage()));
            }
            order.setCommn(BigDecimal.ZERO);
           
            if(!selectCards.contains(oi.getCard()))
            {
                order.setCard(oi.getCard());
                selectCards.add(oi.getCard());
            }
            if(MType.INTEGRAL_MSD_GOODS.equals(oi.getMType()))
            {
                order.setCard(null);
            }
            order.setCutAmt(BigDecimal.ZERO);
            if (order.getCard() != null)
            {
                // 校验该卡券是否可用
                MktMemberCard checkCard = checkCard(goodsMap,
                    spaceMap,
                    order.getMember(),
                    order.getCard(),
                    addOrderlines,
                    order.getFarmer(),
                    order.getDistributionType(),
                    CardCouponType.GOODS_COUPON);
                order.setCardAmt(checkCard.getCost());
            }
            else
            {
                order.setCardAmt(BigDecimal.ZERO);
            }
            order.setAmtn(order.getAmto().subtract(order.getCardAmt()));
            if (order.getAmtn().compareTo(BigDecimal.ZERO) <= 0) order.setAmtn(BigDecimal.ZERO);
            order.setAmtn(order.getAmtn().add(order.getPostage()));
            
            for (OrderGoodsV3OnList line : oi.getGoodsList())
            {
                spaceKcCache.decrement(String.valueOf(line.getSpace()), line.getNum(), null);
            }
            order.setWeixinAmt(order.getAmtn());
            order.setOtherAmt(BigDecimal.ZERO);
            order = orderDao.add(order);
            List<Integer> listTag = memberTagDao.listTag(order.getMember(), order.getAscription());
            if(listTag != null && !listTag.isEmpty())
            {
                List<MktOrderTag> otl = new ArrayList<>();
                for(Integer a : listTag)
                {
                    MktOrderTag ot = new MktOrderTag();
                    ot.setAscription(order.getAscription());
                    ot.setOrderPkey(order.getPkey());
                    ot.setTag(a);
                    MktTag mktTag = tagDao.get(a);
                    if(mktTag != null)
                        ot.setTagName(mktTag.getName());
                    otl.add(ot);
                }
                orderTagDao.addAll(otl);
            }
            // 有优惠金额.将每个商品优惠后的价格记录一下
            if (order.getCardAmt().compareTo(BigDecimal.ZERO) > 0)
            {
                calculateCouponAmt(addOrderlines, order.getCardAmt());
            }
            orderLineDao.addAll(addOrderlines);
           
            if (!gwcIds.isEmpty())
            {
                List<MktGwc> gwcList = gwcDao.select().in("pkey", gwcIds).exec();
                for (MktGwc mktGwc : gwcList)
                {
                    if (mktGwc.getAssociation() != null)
                    {
                        MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(mktGwc.getAssociation());
                        if (mktGoodsSpace != null)
                        {
                            MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                            if (mktGoods != null && !MType.PROCESS_GOODS.equals(mktGoods.getMType()))
                            {
                                List<MktGwc> removeList = gwcDao.select()
                                    .eq("member", mktGwc.getMember())
                                    .eq("association", mktGwc.getSpace())
                                    .exec();
                                gwcDao.removeAll(removeList);
                            }
                        }
                    }
                }
                gwcDao.removeAllById(gwcIds);
            }
            // 库存处理
            updateKcForList(addOrderlines, order.getPkey());
            orderList.add(order);
        }
        dto.setGoodsSumAmtn(BigDecimal.ZERO);
        dto.setSumPointn(0);
        dto.setSumPostage(BigDecimal.ZERO);
        dto.setGoodsSumWeixinAmtn(BigDecimal.ZERO);
        dto.setGoodsSumOtherAmtn(BigDecimal.ZERO);
        
        for (MktOrder order : orderList)
        {
            dto.setGoodsSumAmtn(dto.getGoodsSumAmtn().add(order.getAmtn()));
            dto.setSumPointn(dto.getSumPointn() + order.getPointn());
            dto.setSumPostage(dto.getSumPostage().add(order.getPostage()));
        }
        if(dto.getPayType().equals(PayType.MSD_COMBINATION))
        {
            BigDecimal loadMsd = getMsdBalance(memberPkey, MobileSession.appid());
            updOrderOtherAmt(loadMsd, orderList, dto.getGoodsSumAmtn());
        }
        if(dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
        {
            BigDecimal comm = commManager.loadComm(memberPkey);
            updOrderOtherAmt(comm, orderList, dto.getGoodsSumAmtn());
        }
        for (MktOrder order : orderList)
        {
            if(order.getWeixinAmt().compareTo(BigDecimal.ZERO) <= 0)
            {
                if(order.getPayType().equals(PayType.MSD_COMBINATION))
                    order.setPayType(PayType.ORDER_MSD);
                if(order.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
                    order.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
            }
            dto.setGoodsSumWeixinAmtn(dto.getGoodsSumWeixinAmtn().add(order.getWeixinAmt()));
            if(order.getOtherAmt() != null)
                dto.setGoodsSumOtherAmtn(dto.getGoodsSumOtherAmtn().add(order.getOtherAmt()));
        }
        orderDao.updateAll(orderList);
        return orderList;
    }
    
    private void updOrderOtherAmt(BigDecimal loadMsd, List<MktOrder> orderList, BigDecimal goodsSumAmtn)
    {
        BigDecimal surplus = loadMsd;
        for(int f = 0; f < orderList.size(); f++)
        {
            MktOrder order = orderList.get(f);
            if(f == (orderList.size() - 1))
            {
                if(surplus.compareTo(order.getAmtn()) > 0)
                {
                    order.setOtherAmt(order.getAmtn());
                    order.setWeixinAmt(BigDecimal.ZERO);
                }
                else
                {
                    order.setOtherAmt(surplus);
                    order.setWeixinAmt(order.getAmtn().subtract(surplus));
                }
            }
            else
            {
                BigDecimal msd = order.getAmtn()
                    .divide(goodsSumAmtn, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(loadMsd).setScale(0, BigDecimal.ROUND_HALF_UP);
                surplus = surplus.subtract(msd);
                if(msd.compareTo(order.getAmtn()) > 0)
                {
                    order.setOtherAmt(order.getAmtn());
                    order.setWeixinAmt(BigDecimal.ZERO);
                }
                else
                {
                    order.setOtherAmt(msd);
                    order.setWeixinAmt(order.getAmtn().subtract(msd));
                }
            }
        }
        orderDao.updateAll(orderList);
    }
    
    /*
     * 支付成功后库存修改
     */
    private void updateKcForList(List<MktOrderLine> list, int orderPkey)
    {
        List<Integer> key = new ArrayList<>();
        for (MktOrderLine line : list)
        {
            updateKu(line.getSpace().intValue(), line.getNum(), orderPkey);
            // 处理H5包厢商品
            h5OrderManager.upBoxSpace(line.getSpace().intValue());
            key.add(line.getGoods().intValue());
        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(5000);
                    
                    if (!key.isEmpty())
                    {
                        List<MktGoods> exec = goodsDao.select().in("pkey", key).exec();
                        for (MktGoods g : exec)
                        {
                            goodListQueryer.resetThreeGtype(g);
                        }
                    }
                    
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            
        }).start();
    }
    
    /*
     * 更新单商品库存
     */
    private void updateKu(int gdPkey, int num, int orderPkey)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "goodsSpace" + gdPkey);// 业务锁
            log.info("库存处理开始：{} : {}", gdPkey, num);
            MktGoodsSpace gd = goodsSpaceDao.get(gdPkey);
            gd.setKcNum(gd.getKcNum() - num);
            gd.setXsNum(gd.getXsNum() + num);
            goodsSpaceDao.update(gd);
            MktGoods good = goodsDao.get(gd.getGoods());
            good.setXsNum(good.getXsNum() + num);
            goodsDao.update(good);
            wareManager.insWare(gdPkey, num, orderPkey);
            log.info("库存处理结束");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "goodsSpace" + gdPkey);
        }
    }
    
    private void checkBugGoodsNum(List<OrderGoodsV3OnList> list, Map<Integer, MktGoods> goodsMap,
        Map<Integer, MktGoodsSpace> spaceMap)
    {
        Map<Integer, Integer> checkNumMap = new HashMap<>();
        for (OrderGoodsV3OnList line : list)
        {
            if (spaceMap.containsKey(line.getSpace()))
            {
                MktGoodsSpace gs = spaceMap.get(line.getSpace());
                if (checkNumMap.containsKey(gs.getGoods()))
                {
                    checkNumMap.put(gs.getGoods(), checkNumMap.get(gs.getGoods()) + line.getNum());
                }
                else
                {
                    checkNumMap.put(gs.getGoods(), line.getNum());
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : checkNumMap.entrySet())
        {
            Integer key = entry.getKey();
            if (!goodsMap.containsKey(key)) continue;
            MktGoods goods = goodsMap.get(key);
            // 校验是否超出每日限购
            getBuyGoodsNum(goods, checkNumMap.get(key));
        }
    }
    
    /**
     * 库存和下架校验
     * <功能详细描述>
     * @return
     */
    private void checkGoodsKcNum(MktGoods goods, MktGoodsSpace space, int num)
    {
        Long kcNum = spaceKcCache.getLong(String.valueOf(space.getPkey()));
        System.out.println("kcNum: " + kcNum);
        if (kcNum == null) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
        if (kcNum.intValue() < num) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
        if (!goods.getEnabled()) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED, goods.getTitle() + "已下架");
    }
    
    private void payAfterOrder(OrderTotalV3Info dto, List<MktOrder> listOrder)
    {
        Integer ecardAccountConsume = null;
        if (dto.getPayType().equals(PayType.NM_MEMBER))
        {
            // 去心安食足进行交互
            ecardAccountConsume = saasTokenPublicManager.ecardAccountConsume(dto.getGoodsSumAmtn());
        }
        for (MktOrder order : listOrder)
        {
            if (order.getOrderType().getIndex() != OrderType.GIFT_ORDER.getIndex())
            {
                MktOrderDesc desc = orderDescDao.get(order.getPkey());
                if (desc != null)
                {
                    desc.setFkTime(new Date());
                    orderDescDao.update(desc);
                }
            }
            if (order.getDistributionType().equals(DistributionType.PICKUP))
            {
                order.setPostage(BigDecimal.ZERO);
                order.setPickupFlag(false);
                //方便市场统计自提订单数修改随机核销码为T001格式
                String sequence = order.getFarmer() + DateUtil.formatDate(new Date(), "yyyyMMdd");
                Long index = redisCounter.increment(Constant.DomainId, Constant.App.SERVER, sequence);
                if (index == 1)
                {
                    long timeout = (DateUtil.atEndOfToday().getTime() - System.currentTimeMillis()) / 1000;
                    redisCounter.expire(Constant.DomainId, Constant.App.SERVER, sequence, timeout);
                }
//                String pickupcode = "T" + StringUtil.right("000" + index, 3);
                String pickupcode = LejiaUtils.getNewRandomString(4);
                order.setPickupCode(pickupcode);
                order.setStatus(OrderStatus.DELIVERED_ORDER);
            }
            else
                order.setStatus(OrderStatus.DELIVERED_ORDER);
            order.setPurchaseStatus(PurchaseStatus.AWAIT_PURCHASE);
            if (Boolean.TRUE.equals(order.getIsBox()) && StringUtils.isNotBlank(order.getLockId())
                && order.getBoxSd() != null && order.getBoxEd() != null)
            {
                // 发送短信给用户,告知会员包厢密码 
                MktMember member = memberDao.get(order.getMember());
                if (member != null && StringUtils.isNotBlank(member.getMobile()))
                {
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String boxPassword = TongTongSuoUtil.timeLimitPwd(Integer.valueOf(order.getLockId()),
                                order.getCode(),
                                order.getBoxSd(),
                                order.getBoxEd());
                            log.info("包厢密码: {}", boxPassword);
                            order.setBoxPassword(boxPassword);
                            
                            orderDao.update(order);
                            // 短信内容 已经 短信模板ID  临时门锁ID  16304453
                            List<String> params = new ArrayList<>();
                            SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
                            params.add("家和菜-" + farmer.getName());
                            params.add(order.getBoxTime());
                            params.add(order.getBoxName());
                            params.add(boxPassword);
                            params.add(farmer.getTel());
                            params.add("鹿城区东屿路66号 东屿农贸市场3楼   \nhttps://j.map.baidu.com/5c/-Qzi");
                            params.add("店门口与地下均有停车场");
                            new SMSNotify(smsConfig).sendNotify(member.getMobile(), params, "TDVGPrkepo2d");
                        }
                    }).start();
                }
            }
            order.setXaszConsumption(ecardAccountConsume);
            orderDao.update(order);
            // 明细表跟着修改
            List<MktOrderLine> line = orderLineDao.select().in("orderPkey", order.getPkey()).exec();
            for (MktOrderLine ol : line)
            {
                ol.setStatus(order.getStatus());
            }
            orderLineDao.updateAll(line);
            
            if (order.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT))
            {
                // 电子帐户更新
                commManager.updComm(order.getMember(),
                    order.getAmtn(),
                    false,
                    CommSourceType.COMM_BUY,
                    order.getCode(),
                    order.getAscription());
            }
            // 民生豆账户更新
            if (order.getPayType().equals(PayType.ORDER_MSD))
            {
                // 第三方餐饮系统请求获取余额
                if (cateringEnabled && cateringAscription.equals(MobileSession.appid()))
                {
                    cateringManager.consume(order.getMember(), order.getAmtn(), order.getCode());
                }
                else
                {
                    Boolean jdOrder = OrderType.INTEGRAL_JD_ORDER.equals(order.getOrderType());
                    memberMsdManager.updMsdBalance(order.getMember(),
                        null,
                        false,
                        order.getAmtn(),
                        MsdOperationType.CONSUME,
                        order.getCode(),
                        order.getCode(),
                        order.getAscription(),
                        jdOrder);
                }
            }
            
            if (order.getPayType().equals(PayType.MSD_COMBINATION))
            {
                // TODO 第三方餐饮 未处理 后续处理
                memberMsdManager.updMsd(order.getMember(), 
                    order.getOtherAmt(), order.getCode(), order.getAscription());
            }
            if (order.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
            {
                // 组合支付成功电子帐户更新
                commManager.updComm(order.getMember(), order.getOtherAmt(), order.getCode(), order.getAscription());
            }
            
            SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
            if (order.getOrderOir().equals(OrderOir.POINTS_MALL))
            {
                SourceType st = SourceType.POINTS_BUY;
                // 积分帐户更新
                if (order.getOrderType().equals(OrderType.GIFT_ORDER)) st = SourceType.POINTS_GIFT;
                if (order.getOrderType().equals(OrderType.COUPON_ORDER)) st = SourceType.POINTS_COUPON;
                pointManager.updPoint(order.getMember(),
                    order.getPointn(),
                    false,
                    st,
                    order.getCode(),
                    farmer.getName(),
                    farmer.getAscription());
            }
            if(!OrderType.INTEGRAL_MSD_ORDER.equals(order.getOrderType()))
            {
                // 只要购物 就增加积分
                pointManager.updPointForAmt(order.getMember(),
                    order.getAmtn(),
                    true,
                    SourceType.POINTS_CONSUMPTION,
                    order.getCode(),
                    farmer.getName(),
                    farmer.getAscription(),
                    null);
            }
            if (order.getCard() != null)
            {// 卡券核销
                userCard(order.getCard(), order.getFarmer(), order.getPkey());
            }
            if (order.getCardPostage() != null)
            {
                userCard(order.getCardPostage(), order.getFarmer(), order.getPkey());
            }
            // 库存处理
            //        updateKcForList(order.getPkey()); 2022-03-25 下单时就处理 zdw v1下单接口 没有调整，下单后 库存会有问题
            // 团购订单处理
            updateOrderGroup(order);
            // 礼品券处理
            if (order.getOrderType().equals(OrderType.GIFT_ORDER)) drOrderGift(order);
            // 优惠券处理
            if (order.getOrderType().equals(OrderType.COUPON_ORDER))
            {
                drOrderCoupon(order);
            }
            // 发送消息给对应市场  2022-08-03
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    assembleAndSendWx(order);
                }
                
            }).start();
            
            // 自动采购
            // 是否统一配置
            Boolean flag = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, order.getAscription());
            SysFarmerConfig config = sysFarmerConfigDao.get(farmer.getPkey());
            DistributionType type = order.getDistributionType();
            
            // 市场配置
            if (Boolean.FALSE.equals(flag) && Boolean.TRUE.equals(config.getAutomaticPurchase()))
            {
                // 是否自动采购
                new Thread(new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        vendorOrderManager.autoPurchase(order);
                        if (Boolean.TRUE.equals(config.getAutomaticCourier()) && type != null
                            && !type.equals(DistributionType.PICKUP))
                            vendorOrderManager.automaticCourier(order.getPkey());
                    }
                }).start();
            }
            else
            {
                Boolean purchaseFlag =
                    sysConfigDao.getValue(Constant.SysConfig.GOODS_PURCHASE_DEPLOY, order.getAscription());
                if (Boolean.TRUE.equals(purchaseFlag))
                {
                    // 是否自动采购
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(5000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            vendorOrderManager.autoPurchase(order);
                            if (Boolean.TRUE.equals(config.getAutomaticCourier()) && type != null
                                && !type.equals(DistributionType.PICKUP))
                                vendorOrderManager.automaticCourier(order.getPkey());
                        }
                    }).start();
                }
            }
            
            // 更新会员的消费记录
            if (order.getMember() != null)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        memberDao.updLastConsume(order.getMember(), order.getCreatedTime(), order.getFarmer());
                    }
                }).start();
            }
            
        }
    }
    
    /*
     * 支付成功后 更新团购订单
     */
    private void updateOrderGroup(MktOrder order)
    {
        if (order.getOrderType().getIndex() == 4)
        {
            MktOrderLine exec = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
            if (exec != null)
            {
                MktOrderGroup group = orderGroupDao.selectOne()
                    .eq("goods", exec.getGoods())
                    .eq("status", OrderGroupStatus.NOT_GROUPS)
                    .exec();
                if (group != null)
                {
                    group.getOrderList().add(order.getPkey() + "");
                    Integer buyNum = group.getBuyNum();
                    Integer num = group.getGroupNum() - buyNum;
                    if (num == 1) group.setStatus(OrderGroupStatus.INTO_GROUPS);
                    group.setBuyNum(buyNum + 1);
                }
                else
                {
                    group = new MktOrderGroup();
                    group.setOrderList(Arrays.asList(order.getPkey() + ""));
                    group.setGoods(exec.getGoods().intValue());
                    group.setGroupId(Integer.parseInt(NumberUtils.createCheckCode()));
                    group.setStatus(OrderGroupStatus.NOT_GROUPS);
                    group.setAscription(order.getAscription());
                    MktGoods goods = goodsDao.get(exec.getGoods().intValue());
                    if (goods != null)
                    {
                        group.setBuyNum(1);
                        group.setGroupNum(Integer.valueOf(goods.getExtendCon()));
                        group.setEndDate(goods.getEndDate());
                        if (group.getBuyNum().intValue() == group.getGroupNum().intValue())
                            group.setStatus(OrderGroupStatus.INTO_GROUPS);
                        orderGroupDao.add(group);
                    }
                }
            }
        }
    }
    
    /*
     * 核销卡券
     */
    private void userCard(int cardPkey, String farmer, Integer orderId)
    {
        MktMemberCard card = memberCardDao.get(cardPkey);
        card.setOrderId(orderId);
        card.setStatus(CardStatus.USED);
        card.setUserTime(new Date());
        card.setUserFarmer(farmer);
        memberCardDao.update(card);
        
        MktCard mktCard = cardDao.get(card.getCard());
        Integer usedNum = mktCard.getUsedNum();
        if (usedNum == null) usedNum = 0;
        mktCard.setUsedNum(usedNum + 1);
        cardDao.update(mktCard);
        
        if (card.getActivity() != null)
        {
            MktActivity mktActivity = activityDao.get(card.getActivity());
            if (mktActivity != null)
            {
                Integer useNum = mktActivity.getUseNum();
                if (useNum == null) useNum = 0;
                useNum += 1;
                activityDao.updUseNum(mktActivity.getPkey(), useNum);
            }
        }
    }
    
    // 礼品券支付成功
    private void drOrderGift(MktOrder order)
    {
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        desc.setDrTime(new Date());
        desc.setEndTime(new Date());
        orderDescDao.update(desc);
        orderDao.update(order);
        
        giftManager.insMemberGift(order);
        
        Long tjr = tjrMap.get(order.getMember() + "");
        if (tjr != null)
        {
            commManager.updComm(tjr.intValue(),
                order.getAmtall(),
                true,
                CommSourceType.SHARE_NEW,
                order.getMember() + "",
                order.getAscription());
            tjrMap.remove(order.getMember() + "");
        }
    }
    
    // 优惠券支付成功
    private void drOrderCoupon(MktOrder order)
    {
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        desc.setDrTime(new Date());
        desc.setEndTime(new Date());
        orderDescDao.update(desc);
        orderDao.update(order);
        MktOrderLine line = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
        cardManager.insMemberCard(order.getMember(), line.getCard(), line.getNum(), order.getCode());
        
        Long tjr = tjrMap.get(order.getMember() + "");
        if (tjr != null)
        {
            commManager.updComm(tjr.intValue(),
                order.getAmtall(),
                true,
                CommSourceType.SHARE_NEW,
                order.getMember() + "",
                order.getAscription());
            tjrMap.remove(order.getMember() + "");
        }
    }
    
    private void assembleAndSendWx(MktOrder order)
    {
        Integer ascription = order.getAscription();
        SysConfigEntity sysConfig;
        MktOrderLine ol = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        List<Integer> list = gzhAssociateDao.listTrueAssKeys(order.getFarmer());
        if (list.isEmpty()) return;
        JSONObject data = new JSONObject();
        if (DistributionType.IMMEDIATELY.equals(order.getDistributionType()))
        {
            sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_MARKET, ascription);
            if (sysConfig == null) return;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "订单号: " + order.getCode());
            data.put("first", jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("value", ol.getGoodsName());
            data.put("keyword1", jsonObject2);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("value", desc.getName());
            data.put("keyword2", jsonObject3);
            
            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("value", desc.getMobile());
            data.put("keyword3", jsonObject5);
            
            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("value", desc.getAddr());
            data.put("keyword4", jsonObject6);
            
            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("value", order.getPstime());
            data.put("keyword5", jsonObject7);
            
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("value", "请及时处理");
            data.put("remark", jsonObject4);
        }
        else
        {
            sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_MARKET_PICK, ascription);
            if (sysConfig == null) return;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "您有新的自提订单");
            data.put("first", jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("value", order.getCode());
            data.put("keyword1", jsonObject2);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("value", ol.getGoodsName());
            data.put("keyword2", jsonObject3);
            
            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("value", desc.getName());
            data.put("keyword3", jsonObject5);
            
            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("value", desc.getMobile());
            data.put("keyword4", jsonObject6);
            
            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("value", DateUtil.formatDate(order.getCreatedTime()));
            data.put("keyword5", jsonObject7);
            
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("value", "请及时处理");
            data.put("remark", jsonObject4);
            
        }
        List<MktGzh> gzh = gzhDao.listGzh(list);
        gzh = gzh.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOpenid()))),
                ArrayList::new));
        for (MktGzh g : gzh)
        {
            Boolean msg = wxManager.wechatSendMsgYs(sysConfig.getValue(), g.getOpenid(), null, data, ascription);
            log.info("市场订单发送给市场管理人员微信公众号: {}", msg);
        }
    }
    
    public GwcOrderTotalV3Info2 buyGoods(Integer space, Integer num, Boolean pickupType, Integer addressPkey,
        Boolean dineIn, Integer association, BigDecimal longitude, BigDecimal latitude)
    {
        List<MktGwc> gwcs = new ArrayList<>();
        MktGwc g = new MktGwc();
        g.setMember(MobileSession.memberPkey());
        MktGoodsSpace goodsSpace = goodsSpaceDao.get(space);
        MktGoods gd = goodsDao.get(goodsSpace.getGoods());
        g.setGoods(goodsSpace.getGoods());
        g.setSpace(space);
        g.setNum(num);
        g.setAscription(association);
        
        if (association != null)
        {
            MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
            if (mktGoodsSpace != null)
            {
                MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                if (mktGoods != null)
                {
                    g.setAssociation(association);
                    g.setAssociationName(mktGoods.getTitle());
                    MktGwc gwcAss = new MktGwc();
                    gwcAss.setMember(MobileSession.memberPkey());
                    gwcAss.setGoods(mktGoodsSpace.getGoods());
                    gwcAss.setCompany(mktGoods.getCompany());
                    gwcAss.setFarmer(mktGoods.getFarmer());
                    gwcAss.setSpace(association);
                    gwcAss.setNum(1);
                    gwcAss.setAssociation(space);
                    gwcAss.setAssociationName(gd.getTitle());
                    gwcs.add(gwcAss);
                }
            }
        }
        gwcs.add(g);
        return buyGwc(gwcs, pickupType, addressPkey, dineIn, longitude, latitude);
    }
    
    public GwcOrderTotalV3Info2 buyGwcV3(List<Integer> gwcList, Boolean pickupType, Integer addressPkey, Boolean dineIn, BigDecimal longitude, BigDecimal latitude)
    {
        if (gwcList.isEmpty()) throw TofocusException.of(LejiaErrCode.ORDER_NULL);
        List<MktGwc> gwcs = gwcDao.listGwc(gwcList);
        return buyGwc(gwcs, pickupType, addressPkey, dineIn, longitude, latitude);
    }
    
    // 购物车下单   
    public GwcOrderTotalV3Info2 buyGwc(List<MktGwc> gwcs, Boolean pickupType, Integer addressPkey, Boolean dineIn, BigDecimal longitude, BigDecimal latitude)
    {
        Integer qrCode = MobileSession.qrCode();
        //        if (gwcList.isEmpty()) throw TofocusException.of(LejiaErrCode.ORDER_NULL);
        GwcOrderTotalV3Info dto = new GwcOrderTotalV3Info();
        
        DistributionType dt = DistributionType.IMMEDIATELY;
        if (Boolean.TRUE.equals(pickupType)) dt = DistributionType.PICKUP;
        //        dto.setPickupType(pickupType);
        //                if (Boolean.TRUE.equals(pickupType)) dto.setDistributionType(DistributionType.PICKUP);
        //                else
        //                    dto.setDistributionType(DistributionType.IMMEDIATELY);
        //        dto.setMember(MobileSession.memberPkey());
        
        //        List<MktGwc> gwcs = gwcDao.listGwc(gwcList);
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        gwcs.forEach(e -> {
            if(e.getGoods() != null)
                gkeys.add(e.getGoods());
            if(e.getSpace() != null)
                skeys.add(e.getSpace());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        // 检查民生专区不能和其他商品合并下单
        Iterator<MktGoods> it = goodsMap.values().iterator();
        boolean isMsd = false;
        if(it.hasNext())
            isMsd = it.next().getMType() == MType.INTEGRAL_MSD_GOODS;
        while (it.hasNext())
        {
            MktGoods goods = it.next();
            if ((goods.getMType() == MType.INTEGRAL_MSD_GOODS) != isMsd)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "不允许合并下单");
            }
        }
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        BigDecimal reducePrice = BigDecimal.ZERO;
        MktMember mktMember = MobileSession.member();
        LevelType level = mktMember.getLevel();
        Integer appid = MobileSession.appid();
        String farmerKey = Constant.Operation + appid;
        // 商品限购校验
        List<OrderGoodsV3OnList> list = new ArrayList<>();
        for (MktGwc g : gwcs)
        {
            OrderGoodsV3OnList bean = BeanUtil.beanFrom(OrderGoodsV3OnList.class, g);
            bean.setGwcPkey(g.getPkey());
            list.add(bean);
        }
        checkBugGoodsNum(list, goodsMap, spaceMap);
        List<OrderV3Info> infos = new ArrayList<>();
        
        Boolean isBox = false;
        Map<Integer,MktSupplier> sMap = new HashMap<>();
        
        OrderV3Info fo = new OrderV3Info();
        List<OrderGoodsV3OnList> goodsListFo = new ArrayList<>();
        fo.setGoodsList(goodsListFo);
        fo.setDelivery(true);
        fo.setPickup(true);
        infos.add(fo);
        boolean integralPresaleGoods = false;
        Map<String, OrderV3Info> omap = new HashMap<>();
        
        MsdPayConfig mpConfig = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(mpConfig == null)
        {
            mpConfig = new MsdPayConfig();
            mpConfig.setFarmerGoods(false);
            mpConfig.setSysGoods(false);
        }
        GwcOrderTotalV3Info2 res = new GwcOrderTotalV3Info2();
        res.setFarmerGoods(mpConfig.getFarmerGoods());
        res.setSysGoods(mpConfig.getSysGoods());
        res.setMsdPay(false);
        Integer msdPay = 0;
        for (MktGwc gwc : gwcs)
        {
            if (!goodsMap.containsKey(gwc.getGoods())) continue;
            if (!spaceMap.containsKey(gwc.getSpace()))
            {
                gwcDao.remove(gwc);
                throw TofocusException.of(LejiaErrCode.GWC_SPACE_NOTEXIST);
            }
            MktGoods goods = goodsMap.get(gwc.getGoods());
            if(MType.INTEGRAL_PRESALE_GOODS.equals(goods.getMType()))
                integralPresaleGoods = true;
            MktGoodsSpace goodsSpace = spaceMap.get(gwc.getSpace());
            // 组合商品数据
            OrderGoodsV3OnList og = new OrderGoodsV3OnList();
            og.setSpace(goodsSpace.getPkey());
            og.setNum(gwc.getNum());
            og.setAssociation(gwc.getAssociation());
            og.setAssociationName(gwc.getAssociationName());
            og.setGwcPkey(gwc.getPkey());
            og.setGoods(goods.getPkey());
            og.setGoodsName(goods.getTitle());
            og.setMType(goods.getMType());
            og.setWeight(goodsSpace.getWeight());
            String photo3 = goods.getPhoto3();
            if (StringUtils.isBlank(photo3))
            {
                List<String> photo1 = goods.getPhoto1();
                if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
            }
            og.setPhoto(photo3);
            og.setSpaceName(goodsSpace.getSpace());
            og.setPrice(goodsSpace.getPrice());
            og.setPriceMember(goodsSpace.getPriceMember());
            og.setPoint(goodsSpace.getPoint());
            og.setVendor(goods.getVendor());
            og.setGtype(goods.getGtype());
            if (Boolean.TRUE.equals(goods.getIsPostage()))
                og.setIsPostage(true);
            else
                og.setIsPostage(false);
            
            checkGoodsKcNum(goods, goodsSpace, gwc.getNum());
            
            MType mType = goods.getMType();
            if(MType.GIFT_GOODS.equals(mType) 
                || MType.COUPON_GOODS.equals(mType) 
                || MType.PROCESS_GOODS.equals(mType)
                || MType.BOX_GOODS.equals(mType))
            {
                msdPay +=1;
            }
            if(Boolean.FALSE.equals(res.getSysGoods()) 
                && 
                (MType.INTEGRAL_GOODS.equals(mType)
                    || MType.INTEGRAL_BNYP_GOODS.equals(mType)
                    ||MType.INTEGRAL_PRESALE_GOODS.equals(mType)))
            {
                msdPay +=1;
            }
            if(Boolean.FALSE.equals(res.getFarmerGoods()) 
                && (
                    MType.MARKET_GOODS.equals(mType) ||
                    MType.MEMBER_GOODS.equals(mType) ||
                    MType.SPECIAL_GOODS.equals(mType) ||
                    MType.SHARE_GOODS.equals(mType) ||
                    MType.COLLAGE_GOODS.equals(mType) ||
                    MType.PRESALE_GOODS.equals(mType) ||
                    MType.POVERTY_ALLEVIATION_GOODS.equals(mType) ||
                    MType.CUT_GOODS.equals(mType)
                    ))
            {
                 msdPay +=1;
            }
            
            if (farmerKey.equals(goods.getFarmer()))
            {
                String omapKey = goods.getSupplier().toString();
                String pstime = null;
                boolean allowedPickPstime = true;
                if (isMsd)
                {
                    pstime = getPsTime4Presale(goods.getPkey());
                    omapKey += pstime;
                    allowedPickPstime = "".equals(pstime);
                }
                if (!omap.containsKey(omapKey))
                {
                    OrderV3Info oi = new OrderV3Info();
                    oi.setSupplier(goods.getSupplier());
                    oi.setDistributionType(dt);
                    List<OrderGoodsV3OnList> goodsList = new ArrayList<>();
                    oi.setGoodsList(goodsList);
                    List<String> goodsPhotos = new ArrayList<>();
                    oi.setGoodsPhotos(goodsPhotos);
                    oi.setPostage(BigDecimal.ZERO);
                    oi.setDelivery(true);
                    oi.setPickup(true);
                    oi.setMType(goods.getMType());
                    oi.setPstime(pstime);
                    oi.setAllowedPickPstime(allowedPickPstime);
                    if (goods.getSupplier() != null)
                    {
                        MktSupplier supplier = supplierDao.get(goods.getSupplier());
                        if (supplier != null)
                        {
                            if (supplier.getAllowedPickup() != null) oi.setPickup(supplier.getAllowedPickup());
                            if (supplier.getAllowedDelivery() != null) oi.setDelivery(supplier.getAllowedDelivery());
                            if (StringUtils.isNotBlank(supplier.getSfMonthlyCard())
                                && StringUtils.isNotBlank(supplier.getSfAppId())
                                && StringUtils.isNotBlank(supplier.getSfSk()))
                            {
                                oi.setSf(true);
                                // 这里存供应商的数据
                                sMap.put(goods.getSupplier(), supplier);
                            }
                            oi.setSupplierName(supplier.getName());
                        }
//                        // 自提地点
//                        List<GwcSupplierPickupLocationInfo> findByFarmer = supplierPickupLocationDao
//                            .findBySupplier(goods.getSupplier(), appid, GwcSupplierPickupLocationInfo.class);
//                        oi.setSplList(findByFarmer);
                    }
                    omap.put(omapKey, oi);
                }
                OrderV3Info oi = omap.get(omapKey);
                if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                    oi.getGoodsPhotos().add(goods.getPhoto1().get(0));
                oi.getGoodsList().add(og);
            }
            else
            {
                fo.setFarmer(goods.getFarmer());
                if (StringUtils.isBlank(fo.getFarmerName()))
                {
                    fo.setMType(goods.getMType());
                    SysFarmer sysFarmer = sysFarmerDao.get(goods.getFarmer());
                    if (sysFarmer != null) fo.setFarmerName(sysFarmer.getName());
//                    List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
//                        .findByFarmer(fo.getFarmer(), appid, GwcSupplierPickupLocationInfo.class);
//                    fo.setSplList(findByFarmer);
                }
                fo.setDistributionType(dt);
                if (goods.getMType().equals(MType.BOX_GOODS))
                {
                    if (Boolean.TRUE.equals(isBox)) throw TofocusException.of(LejiaErrCode.GOODS_BOX_ERROR);
                    isBox = true;
                    fo.setDistributionType(DistributionType.DINE_IN);
                    MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
                    dto.setAddrPkey(goodsBox.getDesktop());
                    dto.setAddr(goodsBox.getDesktopName());
                }
                fo.getGoodsList().add(og);
                
                if (goodsSpace.getPriceMember().compareTo(BigDecimal.ZERO) > 0 && level.equals(LevelType.PAID_MEMBER)
                    && goodsSpace.getPriceMember().compareTo(BigDecimal.ZERO) == 1)
                {
                    BigDecimal subtract = goodsSpace.getPrice().subtract(goodsSpace.getPriceMember());
                    reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(gwc.getNum())));
                }
            }
        }
        
        if(msdPay == 0)
            res.setMsdPay(true);
        
        // 判断堂食
        if (Boolean.TRUE.equals(dineIn) && qrCode == null && !isBox)
        {
            throw TofocusException.of(LejiaErrCode.NO_QRCODE_DINEIN_ERROR);
        }
        if (isBox && qrCode != null)
        {
            MktDesktop mktDesktop = desktopDao.get(qrCode);
            if (mktDesktop != null)
            {
                dto.setAddr(mktDesktop.getName());
                dto.setAddrPkey(mktDesktop.getPkey());
            }
        }
        
        if (fo.getGoodsList().isEmpty())
        {
            fo = null;
            infos.remove(0);
            MktAppAddrDTO loadAddr = loadAddr(mktMember.getPkey(),
                null,
                AddrType.DELIVERY,
//                isMsd ? AddrType.PICKUP : AddrType.DELIVERY,
                addressPkey);
            if (loadAddr != null)
            {
                dto.setAddrPkey(loadAddr.getPkey());
                dto.setAddr(loadAddr.getAddr());
                dto.setAddrDetail(loadAddr.getAddrDetail());
                dto.setPro(loadAddr.getPro());
                dto.setCity(loadAddr.getCity());
                dto.setName(loadAddr.getName());
                dto.setMobile(loadAddr.getMobile());
            }
        }
        else
        {
            SysFarmerMtype farmerMtype = sysFarmerMtypeDao.byFarmerMtype(fo.getFarmer(), MType.MARKET_GOODS);
            if (farmerMtype != null)
            {
                fo.setDelivery(farmerMtype.getDelivery());
                fo.setPickup(farmerMtype.getPickup());
                if (Boolean.FALSE.equals(fo.getDelivery())) fo.setDistributionType(DistributionType.PICKUP);
            }
            
            if (StringUtils.isNotBlank(fo.getFarmer()))
            {
                SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(fo.getFarmer());
                //        DistributionType dt = DistributionType.IMMEDIATELY;
                //        if (Boolean.TRUE.equals(pickupType)) dt = DistributionType.PICKUP;
                // 获取默认地址
                if (dto.getAddr() == null)
                {
                    MktAppAddrDTO loadAddr = loadAddr(mktMember.getPkey(),
                        farmerConfig,
                        isMsd || DistributionType.PICKUP.equals(fo.getDistributionType()) ? AddrType.PICKUP : AddrType.DELIVERY,
                        addressPkey);
                    
                    if (loadAddr != null)
                    {
                        dto.setAddrPkey(loadAddr.getPkey());
                        dto.setAddr(loadAddr.getAddr());
                        dto.setAddrDetail(loadAddr.getAddrDetail());
                        dto.setPro(loadAddr.getPro());
                        dto.setCity(loadAddr.getCity());
                        dto.setName(loadAddr.getName());
                        dto.setMobile(loadAddr.getMobile());
                    }
                    // 配送时间
                    MktDeliveryTimeConfig deliveryTimeConfig =
                        deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(fo.getFarmer(), loadAddr);
                    if (farmerConfig != null)
                        fo.setPstime(appOrderManager.getOrderPsTime(farmerConfig, deliveryTimeConfig));
                }
                fo.setStartingPrice(farmerConfig.getStartingPrice());
                
                if (Boolean.TRUE.equals(fo.getPickup()))
                {
                    if(latitude != null && longitude != null)
                    {
                        List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
                            .findByFarmer(fo.getFarmer(), appid, GwcSupplierPickupLocationInfo.class);
                        if(findByFarmer.isEmpty())
                        {
                            GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
                            gspl.setAddress(farmerConfig.getAddr());
                            gspl.setLongitude(farmerConfig.getLongitude());
                            gspl.setLatitude(farmerConfig.getLatitude());
                            findByFarmer.add(gspl);
                        }
                        for (GwcSupplierPickupLocationInfo gspl : findByFarmer)
                        {
                            // 如果是市场商城，判断有效距离
                            Double a = LocationUtils.getDistance(gspl.getLatitude().doubleValue(),
                                gspl.getLongitude().doubleValue(),
                                latitude.doubleValue(),
                                longitude.doubleValue());
                            // 距离
                            BigDecimal distance = new BigDecimal(a.toString());
                            log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, gspl.getAddress(), dto.getAddr());
                            gspl.setDistance(distance);
                        }
                        Collections.sort(findByFarmer, new Comparator<GwcSupplierPickupLocationInfo>()
                        {
                            @Override
                            public int compare(GwcSupplierPickupLocationInfo o1, GwcSupplierPickupLocationInfo o2)
                            {
                                return o1.getDistance().compareTo(o2.getDistance());
                            }
                        });
                        fo.setSplList(findByFarmer);
                    }
                    else
                    {
                        MktAppAddrDTO loadAddr = loadAddr(mktMember.getPkey(), farmerConfig, AddrType.PICKUP, null);
                        if (loadAddr != null)
                        {
                            MktAddr addr = addrDao.get(loadAddr.getPkey());
                            if (addr != null && addr.getLatitude() != null && addr.getLongitude() != null)
                            {
                                List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
                                    .findByFarmer(fo.getFarmer(), appid, GwcSupplierPickupLocationInfo.class);
                                if(findByFarmer.isEmpty())
                                {
                                    GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
                                    gspl.setAddress(farmerConfig.getAddr());
                                    gspl.setLongitude(farmerConfig.getLongitude());
                                    gspl.setLatitude(farmerConfig.getLatitude());
                                    findByFarmer.add(gspl);
                                }
                                for (GwcSupplierPickupLocationInfo gspl : findByFarmer)
                                {
                                    // 如果是市场商城，判断有效距离
                                    Double a = LocationUtils.getDistance(gspl.getLatitude().doubleValue(),
                                        gspl.getLongitude().doubleValue(),
                                        addr.getLatitude().doubleValue(),
                                        addr.getLongitude().doubleValue());
                                    // 距离
                                    BigDecimal distance = new BigDecimal(a.toString());
                                    log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, gspl.getAddress(), dto.getAddr());
                                    gspl.setDistance(distance);
                                }
                                Collections.sort(findByFarmer, new Comparator<GwcSupplierPickupLocationInfo>()
                                {
                                    @Override
                                    public int compare(GwcSupplierPickupLocationInfo o1, GwcSupplierPickupLocationInfo o2)
                                    {
                                        return o1.getDistance().compareTo(o2.getDistance());
                                    }
                                });
                                fo.setSplList(findByFarmer);
                            }
                        }
                        else
                        {
                            List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
                                .findByFarmer(fo.getFarmer(), appid, GwcSupplierPickupLocationInfo.class);
                            if(findByFarmer.isEmpty())
                            {
                                GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
                                gspl.setAddress(farmerConfig.getAddr());
                                gspl.setLongitude(farmerConfig.getLongitude());
                                gspl.setLatitude(farmerConfig.getLatitude());
                                findByFarmer.add(gspl);
                            }
                            fo.setSplList(findByFarmer);
                        }
                    }
                }
                if(Boolean.TRUE.equals(fo.getDelivery()))
                {
                    if (fo.getGoodsList().size() == 1 && Boolean.TRUE.equals(fo.getGoodsList().get(0).getIsPostage()))
                        fo.setPostage(BigDecimal.ZERO);
                    else
                    {
                        BigDecimal weight = BigDecimal.ZERO;
                        BigDecimal amto = BigDecimal.ZERO;
                        BigDecimal postage = BigDecimal.ZERO;
                        for (OrderGoodsV3OnList og : fo.getGoodsList())
                        {
                            if (Boolean.FALSE.equals(og.getIsPostage())) weight = weight.add(og.getWeight().multiply(new BigDecimal(og.getNum())));
                            amto = amto.add((og.getPrice().multiply(new BigDecimal(og.getNum()))));
                        }
                        if (farmerConfig == null || farmerConfig.getPkey().equals(farmerKey)
                            || farmerConfig.getDistributionConfig() == null
                            || Boolean.TRUE.equals(farmerConfig.getDistributionConfig()))
                        {
                            postage = loadPostage(farmerConfig, weight, amto.subtract(reducePrice));
                        }
                        else
                        {
                            postage = loadPostageFee(farmerConfig, amto.subtract(reducePrice));
                        }
                        fo.setPostage(postage);
                    }
                }
                BigDecimal sales = BigDecimal.ZERO;
                for (OrderGoodsV3OnList og : fo.getGoodsList())
                {
                    sales = sales.add((og.getPrice().multiply(new BigDecimal(og.getNum()))));
                }
                fo.setSales(sales);
            }
        }
        
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", Constant.Operation + appid).exec();
        Calendar cal = Calendar.getInstance();
        for (String s : omap.keySet())
        {
            OrderV3Info info = omap.get(s);

            info.setStartingPrice(BigDecimal.ZERO);
            
            BigDecimal weight = BigDecimal.ZERO;
            BigDecimal amto = BigDecimal.ZERO;
            BigDecimal postage = BigDecimal.ZERO;
            int pointn = 0;
            for (OrderGoodsV3OnList og : info.getGoodsList())
            {
                if (Boolean.FALSE.equals(og.getIsPostage())) weight = weight.add(og.getWeight().multiply(new BigDecimal(og.getNum())));
                amto = amto.add((og.getPrice().multiply(new BigDecimal(og.getNum()))));
                pointn = pointn  + (og.getPoint() * og.getNum());
            }
            SysFarmerConfig config = sysFarmerConfigDao.get(farmerKey);
            if (config.getIsFree() != null && config.getIsFree() && config.getFreeDelivery() != null
                && amto.compareTo(config.getFreeDelivery()) >= 0)
            {
                postage = BigDecimal.ZERO;
            }
            else if (info.getGoodsList().size() == 1 && Boolean.TRUE.equals(info.getGoodsList().get(0).getIsPostage()))
                info.setPostage(BigDecimal.ZERO);
//            else if (MType.INTEGRAL_MSD_GOODS.equals(info.getMType()))
//                info.setPostage(BigDecimal.ZERO);
            else if (Boolean.TRUE.equals(info.getSf()))
            {
                // 这里取供应商数据给顺丰 获取配送费
                // 调用顺丰的接口 计算配送费
                MktSupplier supplier = sMap.get(info.getSupplier());
                try
                {
                    SfGetFreightAddedServicesResult servicesResult = expressSfManager.getFreightAddedServices(supplier.getSfAppId(), supplier.getSfSk(), 
                        supplier.getSfMonthlyCard(), 
                        supplier.getExpressPro(), supplier.getExpressCity(), 
                        supplier.getExpressAddress(), 
                        dto.getPro(), dto.getCity(), dto.getAddr(), info.getGoodsList().get(0).getGoodsName(),
                        weight);
                    postage = servicesResult.getTotalBD();
                }
                catch (Exception e)
                {
                    log.error("请求顺丰配送费报错:  ", e);
                    postage = loadPostage(config, weight, amto);
                }
            }
            else
            {
                postage = loadPostage(config, weight, amto);
            }
            info.setSales(amto);
            info.setPointn(pointn);
            info.setPostage(postage);

            if(integralPresaleGoods)
            {
                info.setPstime(getPsTime4Presale(info.getGoodsList().get(0).getGoods()));
                info.setAllowedPickPstime("".equals(info.getPstime()));
            }
            // 自提地点 自提时间
            if (Boolean.TRUE.equals(info.getPickup()))
            {
                if(latitude != null && longitude != null)
                {
                    List<GwcSupplierPickupLocationInfo> findByFarmer = supplierPickupLocationDao
                        .findBySupplier(info.getSupplier(), appid, GwcSupplierPickupLocationInfo.class);
                    for (GwcSupplierPickupLocationInfo gspl : findByFarmer)
                    {
                        // 如果是市场商城，判断有效距离
                        Double a = LocationUtils.getDistance(gspl.getLatitude().doubleValue(),
                            gspl.getLongitude().doubleValue(),
                            latitude.doubleValue(),
                            longitude.doubleValue());
                        // 距离
                        BigDecimal distance = new BigDecimal(a.toString());
                        log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, gspl.getAddress(), dto.getAddr());
                        gspl.setDistance(distance);
                    }
                    Collections.sort(findByFarmer, new Comparator<GwcSupplierPickupLocationInfo>()
                    {
                        @Override
                        public int compare(GwcSupplierPickupLocationInfo o1, GwcSupplierPickupLocationInfo o2)
                        {
                            return o1.getDistance().compareTo(o2.getDistance());
                        }
                    });
                    info.setSplList(findByFarmer);
                }
                else
                {
                    MktAddr addr = addrDao.getDefaultAddrPickup(mktMember.getPkey());
                    if (addr != null && addr.getLatitude() != null && addr.getLongitude() != null)
                    {
                        List<GwcSupplierPickupLocationInfo> findByFarmer = supplierPickupLocationDao
                            .findBySupplier(info.getSupplier(), appid, GwcSupplierPickupLocationInfo.class);
                        for (GwcSupplierPickupLocationInfo gspl : findByFarmer)
                        {
                            // 如果是市场商城，判断有效距离
                            Double a = LocationUtils.getDistance(gspl.getLatitude().doubleValue(),
                                gspl.getLongitude().doubleValue(),
                                addr.getLatitude().doubleValue(),
                                addr.getLongitude().doubleValue());
                            // 距离
                            BigDecimal distance = new BigDecimal(a.toString());
                            log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, gspl.getAddress(), dto.getAddr());
                            gspl.setDistance(distance);
                        }
                        Collections.sort(findByFarmer, new Comparator<GwcSupplierPickupLocationInfo>()
                        {
                            @Override
                            public int compare(GwcSupplierPickupLocationInfo o1, GwcSupplierPickupLocationInfo o2)
                            {
                                return o1.getDistance().compareTo(o2.getDistance());
                            }
                        });
                        info.setSplList(findByFarmer);
                    }
                    else
                    { 
                        List<GwcSupplierPickupLocationInfo> findByFarmer = supplierPickupLocationDao
                            .findBySupplier(info.getSupplier(), appid, GwcSupplierPickupLocationInfo.class);
                        info.setSplList(findByFarmer);
                    }
                }
                
                if(!integralPresaleGoods && !MType.INTEGRAL_MSD_GOODS.equals(info.getMType()))
                {
                    // 自提时间
                    if (station != null && station.getPhour() != null && station.getPminute() != null)
                    {
                        cal.add(Calendar.HOUR, station.getPhour());
                        cal.add(Calendar.MINUTE, station.getPminute());
                        info.setPstime(DateUtil.formatDate(cal.getTime()));
                    }
                    else
                    {
                        info.setPstime("");
                    }
                }
            }
        }
        if (dto.getAddrPkey() == null && fo == null)
        {
            MktAddr addr = addrDao.getDefaultAddrDelivery(mktMember.getPkey());
            if(addr != null)
            {
                dto.setAddrPkey(addr.getPkey());
                dto.setAddr(addr.getAddr());
                dto.setAddrDetail(addr.getAddrDetail());
                dto.setName(addr.getName());
                dto.setMobile(addr.getMobile());
            }
        }
        infos.addAll(omap.values());
        
        dto.setMyCommn(commManager.loadComm(mktMember.getPkey()));
        // 获取民生豆的余额
        dto.setMyMsd(getMsdBalance(mktMember.getPkey(), appid));
        // 获取最优优惠券
        dto.setCardAmt(BigDecimal.ZERO);
        dto.setCardPostageAmt(BigDecimal.ZERO);
        List<Integer> selectCards = new ArrayList<>();
        List<Integer> selectPostageCards = new ArrayList<>();
        if (!isMsd)
        {
            for (OrderV3Info info : infos)
            {
                info.setCardAmt(BigDecimal.ZERO);
                info.setCardPostageAmt(BigDecimal.ZERO);
                if (StringUtils.isBlank(info.getFarmer()))
                    info.setFarmer(Constant.Operation + MobileSession.appid());
                info.setOriginPostage(info.getPostage());
                getBestOffer(info, mktMember.getPkey(), info.getFarmer(), selectCards, selectPostageCards);
                if (info.getCardAmt().compareTo(info.getSales()) > 0)
                    dto.setCardAmt(dto.getCardAmt().add(info.getSales()));
                else
                    dto.setCardAmt(dto.getCardAmt().add(info.getCardAmt()));
                if (info.getCardPostageAmt().compareTo(info.getOriginPostage()) > 0)
                    dto.setCardPostageAmt(dto.getCardPostageAmt().add(info.getOriginPostage()));
                else
                    dto.setCardPostageAmt(dto.getCardPostageAmt().add(info.getCardPostageAmt()));
            }
        }
        
        //        MktDeliveryTimeConfig deliveryTimeConfig =
        //            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(fo.getFarmer(), dto.getAddr());
        //        List<DistributionTypeDTO> rlsit = buildDistributionType(farmer, deliveryTimeConfig);
        //        fo.setDistype(rlsit);
        
        // 补上dto数据
        dto.setInfos(infos);
        dto.setPayType(PayType.ORDER_WEIXIN);
        // 校验
        checkOrder(BeanUtil.beanFrom(OrderTotalV3Info.class, dto), false);
        
        dto.setGoodsSumAmto(BigDecimal.ZERO);
        dto.setSumPointn(0);
        dto.setSumPostage(BigDecimal.ZERO);
        
        for (OrderV3Info o : dto.getInfos())
        {
            if (o.getSales() != null) dto.setGoodsSumAmto(dto.getGoodsSumAmto().add(o.getSales()));
            if (o.getPointn() != null) dto.setSumPointn(dto.getSumPointn() + o.getPointn());
            if (o.getPostage() != null) dto.setSumPostage(dto.getSumPostage().add(o.getPostage()));
        }
        dto.setGoodsSumAmtn(dto.getGoodsSumAmto().add(dto.getSumPostage()));
        if (dto.getCardAmt() != null) dto.setGoodsSumAmtn(dto.getGoodsSumAmtn().subtract(dto.getCardAmt()));
//        if (dto.getCardPostageAmt() != null)
//            dto.setGoodsSumAmtn(dto.getGoodsSumAmtn().subtract(dto.getCardPostageAmt()));
        
        // 检验订单金额不可为零  由原来的 订单不可为零 修改为   订单金额是0 或者小于0 的 默认为0.01元
        if (dto.getGoodsSumAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setGoodsSumAmtn(BigDecimal.ZERO);
//        if (dto.getGoodsSumAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setGoodsSumAmtn(new BigDecimal("0.01"));
        int loadPoints = pointManager.loadPoints(mktMember.getPkey());
        // 积分余额不足
        if (loadPoints < dto.getSumPointn()) throw TofocusException.of(LejiaErrCode.NO_P0INTS);
        BigDecimal loadComm = commManager.loadComm(mktMember.getPkey());
        // 电子帐户余额不足
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) && loadComm.compareTo(dto.getGoodsSumAmtn()) < 0)
            throw TofocusException.of(LejiaErrCode.NO_COMMS);
        
        BeanUtils.copyProperties(dto, res, "msdPay", "farmerGoods", "sysGoods");
        List<GwcOrderV3Info> gwcInfos = new ArrayList<>();
        for (OrderV3Info o : dto.getInfos())
        {
            GwcOrderV3Info go = BeanUtil.beanFrom(GwcOrderV3Info.class, o);
            Map<Integer, GwcOrderGoodsV3OnList> goMap = new HashMap<>();
            for (OrderGoodsV3OnList og : o.getGoodsList())
            {
                if (!goMap.containsKey(og.getGoods()))
                {
                    GwcOrderGoodsV3OnList gog = BeanUtil.beanFrom(GwcOrderGoodsV3OnList.class, og);
                    gog.setPrice(BigDecimal.ZERO);
                    gog.setSpaceList(new ArrayList<>());
                    goMap.put(og.getGoods(), gog);
                }
                GwcOrderGoodsSpaceV3OnList gogs = new GwcOrderGoodsSpaceV3OnList();
                gogs.setSpace(og.getSpace());
                gogs.setNum(og.getNum());
                gogs.setAssociation(og.getAssociation());
                gogs.setAssociationName(og.getAssociationName());
                gogs.setGwcPkey(og.getGwcPkey());
                gogs.setWeight(og.getWeight());
                gogs.setSpaceName(og.getSpaceName());
                gogs.setPrice(og.getPrice());
                gogs.setPriceMember(og.getPriceMember());
                gogs.setPoint(og.getPoint());
                
                MktGoodsSpace goodsSpace = spaceMap.get(og.getSpace());
                if (StringUtils.isNotBlank(goodsSpace.getPhoto1()))
                    gogs.setPhoto(goodsSpace.getPhoto1());
                else
                    gogs.setPhoto(og.getPhoto());
                GwcOrderGoodsV3OnList gog = goMap.get(og.getGoods());
                gog.setPrice(gog.getPrice().add(gogs.getPrice()));
                gog.getSpaceList().add(gogs);
            }
            List<GwcOrderGoodsV3OnList> goodsList = new ArrayList<>();
            goodsList.addAll(goMap.values());
            go.setGoodsList(goodsList);
            gwcInfos.add(go);
        }
        res.setInfos(gwcInfos);
       
        return res;
    }
    
    private String getPsTime4Presale(Integer goodsPkey)
    {
        MktGoodsPresale goodsPresale = goodsPresaleDao.get(goodsPkey);
        if (goodsPresale != null)
        {
            if (goodsPresale.getEndDate() != null)
                return DateUtil.formatDate(goodsPresale.getStartDate(), "yyyy-MM-dd") + "~"
                    + DateUtil.formatDate(goodsPresale.getEndDate(), "yyyy-MM-dd");
            else
                return DateUtil.formatDate(goodsPresale.getStartDate(), "yyyy-MM-dd");
        }
        else
            return "";
    }
    
    private void getBestOffer(OrderV3Info fo, Integer memberPkey, String farmer, List<Integer> selectCards, List<Integer> selectPostageCards)
    {
        if(fo == null)
            return;
        // 优惠券
        MemberCardOrderInfo mco = listCardV2(fo, memberPkey, fo.getFarmer(), selectCards);
        List<MemberCardV2OnList> cards = null;
        List<MemberCardV2OnList> available = mco.getAvailable();
        if(available != null && !available.isEmpty())
        {
            cards = available;
        }
        if (CollectionUtil.isNotEmpty(cards))
        {
            for (MemberCardV2OnList card : cards)
            {
                if (!selectCards.contains(card.getPkey()))
                {
                    fo.setCardAmt(card.getCost());
                    fo.setCard(card.getPkey());
                    fo.setCardUsable(true);
                    selectCards.add(card.getPkey());
                    break;
                }
            }
        }
        else
        {
            fo.setCardUsable(false);
            fo.setCard(null);
            fo.setCardAmt(BigDecimal.ZERO);
        }
        // 配送券
        cards = listPostageCard(fo, memberPkey, selectPostageCards);
        if (CollectionUtil.isNotEmpty(cards))
        {
            MemberCardV2OnList card = null;
            for (MemberCardV2OnList mc : cards)
            {
                if (Boolean.TRUE.equals(mc.getAvoidPostage())) card = mc;
            }
            if (card == null) card = cards.get(0);
            if (Boolean.TRUE.equals(card.getAvoidPostage()))
            {
                BigDecimal postage = fo.getPostage();
                fo.setCardPostageAmt(postage);
                fo.setPostage(BigDecimal.ZERO);
            }
            else
            {
                BigDecimal postage = fo.getPostage().subtract(card.getCost());
                fo.setCardPostageAmt(card.getCost());
                if (postage.compareTo(BigDecimal.ZERO) < 0)
                {
                    postage = BigDecimal.ZERO;
                    fo.setCardPostageAmt(fo.getPostage());
                }
                fo.setPostage(postage);
            }
            fo.setCardPostage(card.getPkey());
            fo.setCardPostageName(card.getTitle());
            fo.setCardPostageUsable(true);
        }
    }
    
    private MktAppAddrDTO loadAddr(Integer member, SysFarmerConfig config, AddrType addrType, Integer addressPkey)
    {
        MktAddr addr = null;
        if (addressPkey != null)
        {
            //指定地址
            addr = addrDao.selectOne().eq("member", member).eq("type", addrType).eq("pkey", addressPkey).exec();
        }
        else
        {
            //默认地址
            addr = addrDao.selectOne().eq("member", member).eq("type", addrType).eq("defaultAddr", true).exec();
        }
        if (addr == null)
        {
            log.warn("[配送距离] 配送地址不存在");
            if(AddrType.PICKUP.equals(addrType))
            {
                log.warn("手动新增自提地址");
                MktAddr a = new MktAddr();
                a.setType(AddrType.PICKUP);
                MktMember mktMember = MobileSession.member();
                a.setMember(mktMember.getPkey());
                a.setName(mktMember.getName());
                a.setMobile(mktMember.getMobile());
                a.setAddrCode("1");
                a.setDefaultAddr(true);
                a.setLatitude(BigDecimal.ZERO);
                a.setLongitude(BigDecimal.ZERO);
                a.setAscription(MobileSession.appid());
                addr = addrDao.add(a);
            }
            else
                return null;
        }
        MktAppAddrDTO dto = new MktAppAddrDTO();
        BeanUtils.copyProperties(addr, dto);
        dto.setAddrDetail(addr.getAddr());
        if (StringUtils.isNotBlank(addr.getAddrDetail())) dto.setAddrDetail(dto.getAddrDetail() + addr.getAddrDetail());
        if (dto.getAddr() == null) dto.setAddr("");
        if (dto.getAddrDetail() == null) dto.setAddrDetail("");
        dto.setEnabled(true);
        Integer appid = MobileSession.appid();
        if (config == null || config.getPkey().equals(Constant.Operation + appid))// 如果是积分商城，直接返回默认地址
        {
            log.warn("[配送距离] 积分商城，直接返回默认地址");
            return dto;
        }
        if (AddrType.PICKUP.equals(addrType))
        {
            log.warn("[配送距离] 自提，不计算距离");
            return dto;
        }
        // 配置的距离，转为米
        BigDecimal configDistance = config.getDeliveryRange().multiply(new BigDecimal("1000"));
        
        if (addr.getLatitude() != null || addr.getLongitude() != null)
        {
            // 如果是市场商城，判断有效距离
            Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                config.getLongitude().doubleValue(),
                addr.getLatitude().doubleValue(),
                addr.getLongitude().doubleValue());
            // 距离
            BigDecimal distance = new BigDecimal(a.toString());
            log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, config.getAddr(), dto.getAddr());
            if (distance.compareTo(configDistance) <= 0)
            {
                dto.setDistance(distance);
                return dto;
            }
        }
        return getNoDefaultAddr(member, config, configDistance, addr.getAddrDetail(), addrType);
    }
    
    // 获取不是默认地址,在范围内的地址
    private MktAppAddrDTO getNoDefaultAddr(Integer member, SysFarmerConfig config, BigDecimal configDistance,
        String addrDetail, AddrType addrType)
    {
        MktAddr addr = null;
        BigDecimal distance = null;
        if (AddrType.PICKUP.equals(addrType))
        {
            addr = addrDao.selectOne()
                .eq("member", member)
                .eq("type", AddrType.PICKUP)
                .eq("defaultAddr", false)
                .sort("pkey")
                .exec();
            
            if (addr.getLatitude() != null && addr.getLongitude() != null)
            {
                Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                    config.getLongitude().doubleValue(),
                    addr.getLatitude().doubleValue(),
                    addr.getLongitude().doubleValue());
                distance = new BigDecimal(a.toString());
            }
        }
        else
        {
            // 不在默认配送访问内选一个 其他可以配送的地址
            List<MktAddr> list = addrDao.select()
                .eq("member", member)
                .eq("type", AddrType.DELIVERY)
                .eq("defaultAddr", false)
                .sort("pkey")
                .exec();
            for (MktAddr ad : list)
            {
                if (ad.getLatitude() == null || ad.getLongitude() == null) continue;
                Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                    config.getLongitude().doubleValue(),
                    ad.getLatitude().doubleValue(),
                    ad.getLongitude().doubleValue());
                distance = new BigDecimal(a.toString());
                if (distance.compareTo(configDistance) <= 0)
                {
                    addr = ad;
                    break;
                }
            }
        }
        if (addr != null)
        {
            MktAppAddrDTO dto2 = new MktAppAddrDTO();
            BeanUtils.copyProperties(addr, dto2);
            dto2.setAddrDetail(addr.getAddr());
            if (StringUtils.isNotBlank(addrDetail)) dto2.setAddrDetail(addr.getAddr() + addr.getAddrDetail());
            dto2.setEnabled(true);
            dto2.setDistance(distance);
            log.warn("[配送距离] 获取不是默认地址,在范围内的地址 {}米，从 {} 到 {}", distance, config.getAddr(), dto2.getAddr());
            return dto2;
        }
        return null;
    }
    
    public List<MemberCardV2OnList> listCard(OrderV3Info fo, Integer memberPkey, BigDecimal reducePrice, Boolean flag)
    {
        List<MemberCardV2OnList> cards = new ArrayList<>();
        String farmer = fo.getFarmer();
        List<MktMemberCard> list = memberCardDao.listMemberCardV2(memberPkey, CardCouponType.GOODS_COUPON, farmer);
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> {
            keys.add(e.getCard());
        });
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        Map<Integer, BigDecimal> vendorMap = new HashMap<>();
        Map<Integer, BigDecimal> gtypeMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMap = new HashMap<>();
        //        BigDecimal amt = BigDecimal.ZERO;
        for (OrderGoodsV3OnList og : fo.getGoodsList())
        {
            BigDecimal addAmt = og.getPrice().multiply(new BigDecimal(og.getNum()));
            if(Boolean.TRUE.equals(flag) && og.getGtype() == null)
            {
                MktGoods goods = goodsDao.get(og.getGoods());
                og.setGtype(goods.getGtype());
                og.setVendor(goods.getVendor());
            }
            //            amt = amt.add(addAmt);
            if (vendorMap.containsKey(og.getVendor()))
            {
                vendorMap.put(og.getVendor(), vendorMap.get(og.getVendor()).add(addAmt));
            }
            else
                vendorMap.put(og.getVendor(), addAmt);
            
            if (gtypeMap.containsKey(og.getGtype()))
            {
                gtypeMap.put(og.getGtype(), gtypeMap.get(og.getGtype()).add(addAmt));
            }
            else
                gtypeMap.put(og.getGtype(), addAmt);
            
            if (gdMap.containsKey(og.getGoods()))
            {
                gdMap.put(og.getGoods(), gdMap.get(og.getGoods()).add(addAmt));
            }
            else
                gdMap.put(og.getGoods(), addAmt);
        }
        //        amt = amt.subtract(reducePrice);
        for (MktMemberCard mcard : list)
        {
            if (fo.getSales().compareTo(mcard.getLimitCost()) < 0)
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
            Integer userGoods = mcard.getUserGoods();
            if (userGoods != null)
            {
                if (gdMap.containsKey(userGoods))
                {
                    if (gdMap.get(userGoods).compareTo(mcard.getLimitCost()) == -1) continue;
                }
                else
                    continue;
            }
            CardUserOrderType userOrderType = mcard.getUserOrderType();
            if (userOrderType != null)
            {
                if (DistributionType.PICKUP.equals(fo.getDistributionType())
                    || DistributionType.DINE_IN.equals(fo.getDistributionType()))
                {
                    if (userOrderType == CardUserOrderType.DELIVERY) continue;
                }
                else
                {
                    if (userOrderType == CardUserOrderType.PICKUP) continue;
                }
            }
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        memberPkey,
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
        //        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        //        for (MemberCardV2OnList mc : cards)
        //        {
        //            Integer userVendor = mc.getUserVendor();
        //            if(userVendor != null)
        //            {
        //                MktVendor mktVendor = vendorDao.get(userVendor);
        //                if(mktVendor != null)
        //                    mc.setUserVendorName(mktVendor.getDisplayName());
        //            }
        //            Integer userGoods = mc.getUserGoods();
        //            if (goodsMap.containsKey(userGoods)) mc.setUserGoodsName(goodsMap.get(userGoods).getTitle());
        //        }
        return cards;
    }
    
    public MemberCardOrderInfo listCardV2(OrderV3Info fo, Integer memberPkey, String farmer, List<Integer> selectCards)
    {
        if (StringUtil.isBlank(fo.getFarmer()))
        {
            MktGoods goods = goodsDao.get(fo.getGoodsList().get(0).getGoods());
            farmer = goods.getFarmer();
        }
        MemberCardOrderInfo res = new MemberCardOrderInfo();
        List<MemberCardV2OnList> cards = new ArrayList<>();
        List<MemberCardV2OnList> notCards = new ArrayList<>();
        List<MktMemberCard> list = memberCardDao.listMemberCardV2(memberPkey, CardCouponType.GOODS_COUPON);
        List<Integer> keys = list.stream().map(MktMemberCard::getCard).collect(Collectors.toList());
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        Map<Integer, BigDecimal> vendorMap = new HashMap<>();
        Map<Integer, BigDecimal> gtypeMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMtypeMap = new HashMap<>();
        for (OrderGoodsV3OnList og : fo.getGoodsList())
        {
            BigDecimal addAmt = og.getPrice().multiply(new BigDecimal(og.getNum()));
            MType mType = og.getMType();
            if (mType == null)
            {
                MktGoods goods = goodsDao.get(og.getGoods());
                mType = goods.getMType();
                og.setGtype(goods.getGtype());
                og.setVendor(goods.getVendor());
            }
            if (og.getGtype() == null)
            {
                MktGoods goods = goodsDao.get(og.getGoods());
                og.setGtype(goods.getGtype());
                og.setVendor(goods.getVendor());
            }
            if (vendorMap.containsKey(og.getVendor()))
            {
                vendorMap.put(og.getVendor(), vendorMap.get(og.getVendor()).add(addAmt));
            }
            else
            {
                vendorMap.put(og.getVendor(), addAmt);
            }
            
            if (gtypeMap.containsKey(og.getGtype()))
            {
                gtypeMap.put(og.getGtype(), gtypeMap.get(og.getGtype()).add(addAmt));
            }
            else
            {
                gtypeMap.put(og.getGtype(), addAmt);
            }
            
            if (gdMap.containsKey(og.getGoods()))
            {
                gdMap.put(og.getGoods(), gdMap.get(og.getGoods()).add(addAmt));
            }
            else
            {
                gdMap.put(og.getGoods(), addAmt);
            }
            
            if (gdMtypeMap.containsKey(mType.getIndex()))
            {
                gdMtypeMap.put(mType.getIndex(), gdMtypeMap.get(mType.getIndex()).add(addAmt));
            }
            else
            {
                gdMtypeMap.put(mType.getIndex(), addAmt);
            }
        }
        for (MktMemberCard mcard : list)
        {
            MemberCardV2OnList cardDto = new MemberCardV2OnList();
            BeanUtils.copyProperties(mcard, cardDto);
            dtoEnhance.deal(MemberCardV2OnList.class, cardDto);
            if (mapCard.containsKey(mcard.getCard()))
                cardDto.setTitle(mapCard.get(mcard.getCard()).getTitle());
            if (fo.getSales().compareTo(mcard.getLimitCost()) < 0)
            {
                if(StringUtils.isBlank(cardDto.getNotAvailable()))
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
                Integer appid = MobileSession.appid();
                if (!userFarmer.equals(farmer) && !(Constant.Operation + appid).equals(userFarmer))
                {
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("市场不匹配 过滤");
                        cardDto.setNotAvailable("仅限【" + cardDto.getUserFarmerName() + "】线上支付使用");
                        notCards.add(cardDto);
                    }
//                    continue;
                }
            }
            List<Integer> userGoodsList = mcard.getUserGoodsList();
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
                    else
                    {
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
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                cardDto.setMtypeName(sb.toString());
                if (Boolean.FALSE.equals(mflag))
                {
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("专区不匹配 过滤");
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
                            if (gdMap.get(g).compareTo(mcard.getLimitCost()) > -1)
                                mflag = true;
                        }
                    }
                    if (Boolean.FALSE.equals(mflag))
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            System.out.println("商品金额不匹配 过滤");
                            cardDto.setNotAvailable("指定商品金额满" + mcard.getLimitCost() + "元可用");
                            notCards.add(cardDto);
                        }
//                        continue;
                    }
                }
                
                if (selectCards != null && !selectCards.isEmpty() && selectCards.contains(cardDto.getPkey()))
                {
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("该卡券已被其他选择 过滤");
                        cardDto.setNotAvailable("该卡券已被其他选择");
                        notCards.add(cardDto);
                    }
//                    continue;
                }
            }
            Integer userVendor = mcard.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null)
                    cardDto.setUserVendorName(mktVendor.getDisplayName());
                if (vendorMap.containsKey(userVendor))
                {
                    if (vendorMap.get(userVendor).compareTo(mcard.getLimitCost()) < 0)
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
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
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
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
                if (gtypeMap.containsKey(userType))
                {
                    if (gtypeMap.get(userType).compareTo(mcard.getLimitCost()) < 0)
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
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
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        cardDto.setNotAvailable("适用于【" + cardDto.getUserTypeName() + "】分类下的商品");
                        notCards.add(cardDto);
                    }
//                    continue;
                }
            }
          
            if (userGoodsList != null && !userGoodsList.isEmpty())
            {
                boolean mflag = false;
                StringBuffer sb = new StringBuffer();
                for (Integer g : userGoodsList)
                {
                    if (gdMap.containsKey(g))
                    {
                        mflag = true;
                    }
                    else
                    {
                        MktGoods mktGoods = goodsDao.get(g);
                        sb.append(mktGoods.getTitle());
                        sb.append("、");
                    }
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                cardDto.setUserGoodsName(sb.toString());
                if (Boolean.FALSE.equals(mflag))
                {
                    if(StringUtils.isBlank(cardDto.getNotAvailable()))
                    {
                        System.out.println("商品不匹配 过滤");
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
                            if (gdMap.get(g).compareTo(mcard.getLimitCost()) > -1)
                                mflag = true;
                        }
                    }
                    if (Boolean.FALSE.equals(mflag))
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            System.out.println("商品金额不匹配 过滤");
                            cardDto.setNotAvailable("指定商品金额满" + mcard.getLimitCost() + "元可用");
                            notCards.add(cardDto);
                        }
//                        continue;
                    }
                }
            }
            CardUserOrderType userOrderType = mcard.getUserOrderType();
            if (userOrderType != null)
            {
                if (DistributionType.PICKUP.equals(fo.getDistributionType())
                    || DistributionType.DINE_IN.equals(fo.getDistributionType()))
                {
                    if (userOrderType == CardUserOrderType.DELIVERY)
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            cardDto.setNotAvailable("仅限" + userOrderType.getName() + "使用");
                            notCards.add(cardDto);
                        }
//                        continue;
                    }
                }
                else
                {
                    if (userOrderType == CardUserOrderType.PICKUP)
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            cardDto.setNotAvailable("仅限" + userOrderType.getName() + "使用");
                            notCards.add(cardDto);
                        }
//                        continue;
                    }
                }
            }
            // 如果是活动卡券，检查活动限制
            if (mcard.getActivity() != null)
            {
                MktActivity activity = activityDao.get(mcard.getActivity());
                if (activity != null && activity.getLimitDailyCardNum() != -1)
                {
                    long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                        memberPkey,
                        CardStatus.USED,
                        DateUtil.atStartOfToday(),
                        DateUtil.atStartOfTomorrow());
                    if (usedNum >= activity.getLimitDailyCardNum())
                    {
                        if(StringUtils.isBlank(cardDto.getNotAvailable()))
                        {
                            System.out.println("该活动优惠券已达到今日使用上限");
                            cardDto.setNotAvailable("每日限用" + activity.getLimitDailyCardNum() + "张");
                            notCards.add(cardDto);
                        }
//                        continue;
                    }
                }
            }
            if(StringUtils.isBlank(cardDto.getNotAvailable()))
            {
                System.out.println("该卡券可用：" + mcard.getPkey());
                cards.add(cardDto);
            }
        }
        res.setAvailable(cards);
        res.setNotAvailable(notCards);
        return res;
    }
    
    private List<MemberCardV2OnList> listPostageCard(OrderV3Info fo, Integer memberPkey, List<Integer> selectPostageCards)
    {
        List<MemberCardV2OnList> cards = new ArrayList<>();
        BigDecimal postage = fo.getPostage();
        if (postage == null) postage = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(postage) == 0) return cards;
        if (fo.getGoodsList() == null || fo.getGoodsList().isEmpty()) return cards;
        
        List<MktMemberCard> list =
            memberCardDao.listMemberCardV2(memberPkey, CardCouponType.POSTAGE_COUPON, fo.getFarmer());
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getCard()));
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        
        //        BigDecimal amt = BigDecimal.ZERO;
        //        for(OrderGoodsV3OnList og : fo.getGoodsList())
        //        {
        //            BigDecimal addAmt = og.getPrice().multiply(new BigDecimal(og.getNum()));
        //            amt = amt.add(addAmt);
        //        }
        //        amt = amt.subtract(reducePrice);
        
        for (MktMemberCard mcard : list)
        {
            if(selectPostageCards != null && !selectPostageCards.isEmpty() && selectPostageCards.contains(mcard.getPkey()))
            {
                System.out.println("已被其他订单选中 过滤" + mcard.getPkey());
                continue;
            }
            if (fo.getSales().compareTo(mcard.getLimitCost()) < 0)
            {
                System.out.println("少于最低消费 过滤" + mcard.getPkey());
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
//                Integer appid = MobileSession.appid();
//                if (!userFarmer.equals(fo.getFarmer()) && !(Constant.Operation + appid).equals(userFarmer))
                if (!userFarmer.equals(fo.getFarmer()))
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
                        memberPkey,
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
        //        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        return cards;
    }
    
    private List<MemberCardV2OnList> listPostageCardV2(OrderV3Info fo, Integer memberPkey)
    {
        List<MemberCardV2OnList> cards = new ArrayList<>();
        BigDecimal postage = fo.getPostage();
        if (postage == null) postage = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(postage) == 0) return cards;
        if (fo.getGoodsList() == null || fo.getGoodsList().isEmpty()) return cards;
        
        List<MktMemberCard> list =
            memberCardDao.listMemberCardV2(memberPkey, CardCouponType.POSTAGE_COUPON, fo.getFarmer());
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getCard()));
        Map<Integer, MktCard> mapCard = cardDao.mapCard(keys);
        
        //        BigDecimal amt = BigDecimal.ZERO;
        //        for(OrderGoodsV3OnList og : fo.getGoodsList())
        //        {
        //            BigDecimal addAmt = og.getPrice().multiply(new BigDecimal(og.getNum()));
        //            amt = amt.add(addAmt);
        //        }
        //        amt = amt.subtract(reducePrice);
        
        for (MktMemberCard mcard : list)
        {
            if (fo.getSales().compareTo(mcard.getLimitCost()) < 0)
            {
                System.out.println("少于最低消费 过滤" + mcard.getPkey());
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                Integer appid = MobileSession.appid();
                if (!fo.getFarmer().equals(userFarmer) && !(Constant.Operation + appid).equals(userFarmer))
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
                        memberPkey,
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
        //        dtoEnhance.deal(MemberCardV2OnList.class, cards);
        return cards;
    }
    
    /*
     * 建立配置地址选项目
     */
    public List<DistributionTypeDTO> buildDistributionType(SysFarmer farmer, MktDeliveryTimeConfig deliveryTimeConfig)
    {
        List<DistributionTypeDTO> list = new ArrayList<>();
        SysFarmerConfig config = farmer.getConfig();
        
        DistributionTypeDTO t =
            buildDistributionType1(DistributionType.IMMEDIATELY, farmer, config, deliveryTimeConfig);
        list.add(t);
        t = buildDistributionType1(DistributionType.ORDERED, farmer, config, deliveryTimeConfig);
        list.add(t);
        t = buildDistributionType1(DistributionType.EXCHANGE, farmer, config, deliveryTimeConfig);
        list.add(t);
        
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", farmer.getPkey()).exec();
        if (station != null)
        {
            t = new DistributionTypeDTO();
            
            t.setType(DistributionType.PICKUP);
            t.setAddress(station.getAddress());
            t.setYytb(station.getYytb());
            t.setYyte(station.getYyte());
            t.setMobile(farmer.getMobile());
            t.setLatitude(station.getLatitude());
            t.setLongitude(station.getLongitude());
            if (station.getPhour() != null && station.getPminute() != null)
            {
                Integer b = station.getPhour() * 60 + station.getPminute();
                t.setMinute(b);
            }
            list.add(t);
        }
        
        return list;
    }
    
    public DistributionTypeDTO buildDistributionType1(DistributionType type, SysFarmer farmer, SysFarmerConfig config,
        MktDeliveryTimeConfig deliveryTimeConfig)
    {
        
        DistributionTypeDTO t = new DistributionTypeDTO();
        t.setType(type);
        t.setAddress(config.getAddr());
        t.setYytb(config.getYytb());
        t.setYyte(config.getYyte());
        t.setMobile(farmer.getMobile());
        t.setAddress(config.getAddr());
        if (deliveryTimeConfig.getHour() != null && deliveryTimeConfig.getMinute() != null)
        {
            Integer b = deliveryTimeConfig.getHour() * 60 + deliveryTimeConfig.getMinute();
            t.setMinute(b);
        }
        t.setLatitude(config.getLatitude());
        t.setLongitude(config.getLongitude());
        
        return t;
    }

    private BigDecimal getMsdBalance(Integer memberPkey, Integer ascription)
    {
        BigDecimal balance = BigDecimal.ZERO;
        // 第三方餐饮系统请求获取余额
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd != null)
        {
            if (cateringEnabled && cateringAscription.equals(ascription))
            {
                MktMember member = memberDao.get(memberPkey);
                if (member == null)
                    throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR);
                BigDecimal cateringBalance = cateringManager.getMemberBalance(member.getMobile());
                if (cateringBalance != null)
                    balance = cateringBalance;
            }
            else
            {
                balance = memberMsd.getBalance();
            }
        }
        return balance;
    }
}
