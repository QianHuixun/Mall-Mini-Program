package cn.tofocus.lejia.domain.v2;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.lejia.dao.market.*;
import org.apache.commons.lang.StringUtils;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderAddr;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.config.MsdPayConfig;
import cn.tofocus.lejia.bean.dto.goods.GoodsGiftInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressRouteInfo;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gwc.AmtoWeightTotal;
import cn.tofocus.lejia.bean.dto.v2.order.OrderDetailsV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderGwcV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.dto.v3.GwcSupplierPickupLocationInfo;
import cn.tofocus.lejia.bean.entity.applet.XaszAssociationEntity;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.linshi.MktActivityWriteOffLinshi;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCouponLinshi;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundExtend;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.jd.CourierType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.cache.CardLinshiMap;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.applet.XaszAssociationDao;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.linshi.MktActivityWriteOffLinshiDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundExtendDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerPickupLocationDao;
import cn.tofocus.lejia.dao.sys.SysFarmerTimeDao;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.domain.app.AppIndexManager;
import cn.tofocus.lejia.domain.app.AppSupplierManager;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;
import cn.tofocus.lejia.domain.jd.JdOrderRefundManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.GiftManager;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.NsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.LocationUtils;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.wx.PayJs;
import cn.tofocus.lejia.utils.OrderVerifyCodeGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppOrderV2Manager
{
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
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
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Autowired
    private XaszAssociationDao xaszAssociationDao;
    
    @Autowired
    private AppOrderV2Expand appOrderV2Expand;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private MktExpressDao expressDao;

    @Autowired
    private MktOrderExpressRouteDao orderExpressRouteDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private NsPayManager nsPayManager;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private AppIndexManager appIndexManager;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundExtendDao orderRefundExtendDao;
    
    @Autowired
    private MktMemberCouponLinshiDao memberCouponLinshiDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    @Autowired
    private CardLinshiMap cardLinshiMap;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private MktMemberCardDao memberCardDao;

    @Autowired
    private CardV2Manager cardV2Manager;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private SysFarmerPickupLocationDao farmerPickupLocationDao;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private MktOrderGoodsCommentDao orderGoodsCommentDao;
    
    @Autowired
    private AppSupplierManager appSupplierManager;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private MktPayLineDao payLineDao;
    
    @Autowired
    private ThirdPayLineDao thirdPayLineDao;
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Autowired
    private JdOrderRefundManager jdOrderRefundManager;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Value("${zyysc.app.pickup.write.off.url:https://small.xinanshizu.com/writeOffIntegralPresale}")
    private String pickupWriteOffUrl;
    
    @Value("${wei.xin.xiaochengxu.order.ascription:18}")
    private String wxOrder;
    
    /**
     * 是否对接第三方餐饮系统的会员
     */
    @Value("${catering.enabled:false}")
    private boolean cateringEnabled;

    @Value("${catering.ascription:22}")
    private Integer cateringAscription;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    /*
     * 直接购买
     */
    public OrderTotalV2Info bugGoods(Integer spacePkey, Integer num, String tjr, Boolean pickupType,
        Integer addressPkey, Boolean dineIn, Integer association, BigDecimal longitude, BigDecimal latitude)
    {
        Integer qrCode = MobileSession.qrCode();
        OrderTotalV2Info dto = new OrderTotalV2Info();
        dto.setPickupType(pickupType);
        dto.setDineIn(false);
        MsdPayConfig mpConfig = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(mpConfig == null)
        {
            mpConfig = new MsdPayConfig();
            mpConfig.setFarmerGoods(false);
            mpConfig.setSysGoods(false);
        }
        dto.setFarmerGoods(mpConfig.getFarmerGoods());
        dto.setSysGoods(mpConfig.getSysGoods());
        dto.setMsdPay(false);
        if (Boolean.TRUE.equals(pickupType)) dto.setDistributionType(DistributionType.PICKUP);
        else
            dto.setDistributionType(DistributionType.IMMEDIATELY);
        dto.setMember(MobileSession.memberPkey());
        dto.setPayType(PayType.ORDER_WEIXIN);
        if (StringUtils.isNotBlank(tjr))
        {
            MktMember tjrMember = memberDao.selectOne().eq("openid1", tjr).exec();
            if (tjrMember != null && tjrMember.getPkey().intValue() != dto.getMember().intValue())
                dto.setTjr(tjrMember.getPkey());
        }
        MktGoodsSpace space = goodsSpaceDao.get(spacePkey);
        MktGoods goods = goodsDao.get(space.getGoods());
        SysFarmer farmer = sysFarmerDao.get(goods.getFarmer());
        SysFarmerConfig farmerConfig = farmer.getConfig();
        Integer msdPay = 0;
        MType mType = goods.getMType();
        if(
//            MType.GIFT_GOODS.equals(mType) 
//            || MType.COUPON_GOODS.equals(mType) 
            MType.PROCESS_GOODS.equals(mType)
            || MType.BOX_GOODS.equals(mType))
        {
            msdPay +=1;
        }
        if(Boolean.FALSE.equals(dto.getSysGoods()) 
            && 
            (MType.INTEGRAL_GOODS.equals(mType)
                || MType.INTEGRAL_BNYP_GOODS.equals(mType)
                ||MType.INTEGRAL_PRESALE_GOODS.equals(mType)))
        {
            msdPay +=1;
        }
        if(Boolean.FALSE.equals(dto.getFarmerGoods()) 
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
        if(msdPay == 0)
            dto.setMsdPay(true);
        
//        if(dineIn == null && qrCode != null)
//            dto.setDistributionType(DistributionType.DINE_IN);
        if (Boolean.TRUE.equals(dineIn))
        {
            if(qrCode == null && !goods.getMType().equals(MType.BOX_GOODS))
                throw TofocusException.of(LejiaErrCode.NO_QRCODE_DINEIN_ERROR);
            dto.setDistributionType(DistributionType.DINE_IN);
        }
        if (goods.getMType().equals(MType.MARKET_GOODS) || goods.getMType().equals(MType.SPECIAL_GOODS)
            || goods.getMType().equals(MType.BOX_GOODS) || goods.getMType().equals(MType.PROCESS_GOODS))
            dto.setIsCard(true);
        
        if (goods.getMType().equals(MType.BOX_GOODS))
        {
            dto.setDineIn(true);
            dto.setDistributionType(DistributionType.DINE_IN);
        }
        if (goods.getMType().equals(MType.GIFT_GOODS))
        {
            GoodsGiftInfo giftInfo =
                appOrderV2Expand.getGoodsGiftInfo(Integer.valueOf(goods.getExtendCon()), space.getGoods(), farmer);
            dto.setGiftInfo(giftInfo);
        }
        
        // 校验是否超出每日限购
        appOrderV2Expand.getBuyGoodsNum(goods, num);
        // 校验库存及下架 
        appOrderV2Expand.checkGoodsKcNum(goods, space, num);
        dto.setOrderType(appOrderV2Expand.getOrderType(goods.getMType().getIndex()));
        dto.setFarmer(goods.getFarmer());
        dto.setCompany(goods.getCompany());
        appOrderV2Expand.checkMtypes(dto, farmer, goods.getMType());
        // 计算正常价格
        dto.setAmto(space.getPrice().multiply(new BigDecimal(num)));
        if(association != null)
        {
            // 不计算重量 只计算价格 优惠券和运费判断 算上这个优惠券的金额
            MktGoodsSpace goodsSpace = goodsSpaceDao.get(association);
            if(goodsSpace != null)
                dto.setAmto(dto.getAmto().add(goodsSpace.getPrice()));
        }
        dto.setReducePrice(BigDecimal.ZERO);
        // 砍价商品 取原价 等待用户砍价
        if (goods.getMType().equals(MType.CUT_GOODS)) dto.setAmto(space.getPriceOld().multiply(new BigDecimal(num)));
        // 计算会员优惠减免
        if (goods.getMType().equals(MType.MARKET_GOODS) && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
            && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
        {
            BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
            dto.setReducePrice(subtract.multiply(new BigDecimal(num)));
        }
        // 是否包邮 默认false
        dto.setPostFree(false);
        // 运费计算 配送时间
        appOrderV2Expand.assemblePostage(dto, goods, space, num, farmerConfig, addressPkey, qrCode);
        log.info("dto.getPostage: {}", dto.getPostage());
        dto.setPointn(space.getPoint() * num);
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        dto.setCommn(BigDecimal.ZERO);
        // 分享商品计算佣金
        if (goods.getMType().equals(MType.SHARE_GOODS)) dto.setCommn(space.getComm());
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        dto.setCardAmt(BigDecimal.ZERO);
        dto.setCardPostageAmt(BigDecimal.ZERO);
        dto.setOldPostage(dto.getPostage());
        // 添加info和card
        appOrderV2Expand.addInfoAndCard(dto, goods, space, farmer.getPkey(), num, association);
        dto.setType(CardCouponType.GOODS_COUPON);
        
        dto.setAmtn(dto.getAmto().subtract(dto.getCardAmt()));
        if (dto.getAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setAmtn(BigDecimal.ZERO);
        dto.setAmtn(dto.getAmtn().add(dto.getPostage()));
        
        dto.setPickupAmt(dto.getPickupAmt().subtract(dto.getCardAmt()));
        if(dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0) dto.setPickupAmt(BigDecimal.ZERO);
        // 校验
        appOrderV2Expand.newChkOrder(dto, false);
        
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        List<DistributionTypeDTO> rlsit = appOrderV2Expand.buildDistributionType(farmer, deliveryTimeConfig);
        dto.setDistype(rlsit);
        
        assembleWeekTimeAndDayTime(dto, farmer, farmerConfig);
        List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
            .findByFarmer(farmer.getPkey(), farmer.getAscription(), GwcSupplierPickupLocationInfo.class);
        if(findByFarmer.isEmpty())
        {
            GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
            gspl.setAddress(farmerConfig.getAddr());
            gspl.setLongitude(farmerConfig.getLongitude());
            gspl.setLatitude(farmerConfig.getLatitude());
            findByFarmer.add(gspl);
        }
        if(latitude != null && longitude != null)
        {
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
        }
        dto.setSplList(findByFarmer);
        dto.setMyMsd(getMsdBalance(MobileSession.member().getPkey(), MobileSession.appid()));
        return dto;
    }
    
    /*
     * 读取购物车购买模拟订单
     */
    public OrderTotalV2Info bugGwc(List<Integer> gwcList, Boolean pickupType, Integer addressPkey, Boolean dineIn, BigDecimal longitude, BigDecimal latitude)
    {
        Integer qrCode = MobileSession.qrCode();
        if (gwcList.isEmpty()) throw TofocusException.of(LejiaErrCode.ORDER_NULL);
        OrderTotalV2Info dto = new OrderTotalV2Info();
        dto.setPickupType(pickupType);
        dto.setPayType(PayType.ORDER_WEIXIN);
        MsdPayConfig mpConfig = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(mpConfig == null)
        {
            mpConfig = new MsdPayConfig();
            mpConfig.setFarmerGoods(false);
            mpConfig.setSysGoods(false);
        }
        dto.setFarmerGoods(mpConfig.getFarmerGoods());
        dto.setSysGoods(mpConfig.getSysGoods());
        dto.setMsdPay(false);
        if (Boolean.TRUE.equals(pickupType)) dto.setDistributionType(DistributionType.PICKUP);
        else
            dto.setDistributionType(DistributionType.IMMEDIATELY);
       
        dto.setMember(MobileSession.memberPkey());
        List<MktGwc> gwcs = gwcDao.listGwc(gwcList);
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        gwcs.forEach(e -> {
            gkeys.add(e.getGoods());
            skeys.add(e.getSpace());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        AmtoWeightTotal awTotal = new AmtoWeightTotal();
        BigDecimal reducePrice = BigDecimal.ZERO;
        
        Map<Integer, OrderV2Info> map = new HashMap<>();
        MktMember mktMember = MobileSession.member();
        LevelType level = mktMember.getLevel();
        Integer appid = MobileSession.appid();
        String farmerKey = Constant.Operation + appid;
        // 商品限购校验
        appOrderV2Expand.checkBugGoodsNumGwc(gwcs, goodsMap);
        
        // 2024-04-15 临时活动使用
        List<Integer> inVendorKeys = new ArrayList<>();
        Boolean isBox = false;
        Boolean isPostage = false;
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
            MType mType = goods.getMType();
            if(MType.GIFT_GOODS.equals(mType) 
                || MType.COUPON_GOODS.equals(mType) 
                || MType.PROCESS_GOODS.equals(mType)
                || MType.BOX_GOODS.equals(mType))
            {
                msdPay +=1;
            }
            if(Boolean.FALSE.equals(dto.getSysGoods()) 
                && 
                (MType.INTEGRAL_GOODS.equals(mType)
                    || MType.INTEGRAL_BNYP_GOODS.equals(mType)
                    ||MType.INTEGRAL_PRESALE_GOODS.equals(mType)))
            {
                msdPay +=1;
            }
            if(Boolean.FALSE.equals(dto.getFarmerGoods()) 
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
            if (goods.getVendor() != null) inVendorKeys.add(goods.getVendor());
            if (goods.getMType().equals(MType.BOX_GOODS))
            {
                if(Boolean.TRUE.equals(isBox))
                    throw TofocusException.of(LejiaErrCode.GOODS_BOX_ERROR);
                isBox = true;
                dto.setDistributionType(DistributionType.DINE_IN);
            }
            if (goods.getMType().equals(MType.MARKET_GOODS) || goods.getMType().equals(MType.PROCESS_GOODS)
                || goods.getMType().equals(MType.BOX_GOODS) || goods.getMType().equals(MType.SPECIAL_GOODS))
                dto.setIsCard(true);
            if ((Constant.Operation + appid).equals(farmerKey)
                && !(Constant.Operation + appid).equals(goods.getFarmer())) farmerKey = goods.getFarmer();
            MktGoodsSpace space = spaceMap.get(gwc.getSpace());
            if(Boolean.TRUE.equals(goods.getIsPostage()))
                isPostage = true;
            if(goods.getMType().equals(MType.BOX_GOODS))
            {
                MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
                MktAppAddrDTO addr = new MktAppAddrDTO();
                addr.setAddr(goodsBox.getDesktopName());
                addr.setPkey(goodsBox.getDesktop());
                dto.setAddr(addr);
            }
            
            Integer num = gwc.getNum();
            // 校验库存及下架
            appOrderV2Expand.checkGoodsKcNum(goods, space, num);
            if (level.equals(LevelType.PAID_MEMBER) && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
            {
                BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
                reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(num)));
            }
            appOrderV2Expand.addInfoAndLineGwc(dto, awTotal, goods, space, map, num, gwc.getPkey(), gwc.getAssociation(), gwc.getAssociationName(), false);
        }
        if(msdPay == 0)
            dto.setMsdPay(true);
        dto.setDineIn(isBox);
        if (Boolean.TRUE.equals(dineIn))
        {
            if(qrCode == null && !isBox)
                throw TofocusException.of(LejiaErrCode.NO_QRCODE_DINEIN_ERROR);
            dto.setDistributionType(DistributionType.DINE_IN);
        }
        
        SysFarmer farmer = sysFarmerDao.get(farmerKey);
        SysFarmerConfig farmerConfig = farmer.getConfig();
        appOrderV2Expand.checkMtypesGwc(dto, farmer);
        // 组装farmerConfig相关数据
        dto.setFarmer(farmerKey);
        if(gwcs.size() != 1)
            isPostage = false;
        appOrderV2Expand.farmerConfigDtoGwc(dto, farmerConfig, reducePrice, awTotal, addressPkey, qrCode, isPostage);
        
        // 最优优惠券
        dto.setOldPostage(dto.getPostage());
        dto.setCardAmt(BigDecimal.ZERO);
        dto.setCardPostageAmt(BigDecimal.ZERO);
        List<MemberCardV2OnList> cards = cardV2Manager.listCard(dto);
        if (CollectionUtil.isNotEmpty(cards))
        {
            MemberCardV2OnList card = cards.get(0);
            dto.setCardAmt(card.getCost());
            dto.setCard(card.getPkey());
            dto.setCardUsable(true);
        }
        else
        {
            dto.setCard(null);
            dto.setCardAmt(BigDecimal.ZERO);
        }
        dto.setType(CardCouponType.POSTAGE_COUPON);
        cards = cardV2Manager.listCard(dto);
        if (CollectionUtil.isNotEmpty(cards))
        {
            MemberCardV2OnList card = null;
            for(MemberCardV2OnList mc : cards)
            {
                if(Boolean.TRUE.equals(mc.getAvoidPostage()))
                    card = mc;
            }
            if(card == null)
                card = cards.get(0);
            if(Boolean.TRUE.equals(card.getAvoidPostage()))
            {
                dto.setCardPostageAmt(dto.getOldPostage());
                dto.setPostage(BigDecimal.ZERO);
            }
            else
            {
                BigDecimal postage = dto.getOldPostage().subtract(card.getCost());
                dto.setCardPostageAmt(card.getCost());
                if(postage.compareTo(BigDecimal.ZERO) < 0)
                {
                    postage = BigDecimal.ZERO;
                    dto.setCardPostageAmt(dto.getOldPostage());
                }
                dto.setPostage(postage);
            }
            dto.setCardPostage(card.getPkey());
            dto.setCardPostageName(card.getTitle());
            dto.setCardPostageUsable(true);
        }
        dto.setType(CardCouponType.GOODS_COUPON);
        appOrderV2Expand.assembleDtoGwc(dto, farmerKey, awTotal.getPoints(), farmer);
        // 校验
        appOrderV2Expand.newChkOrder(dto, false);
        
        assembleWeekTimeAndDayTime(dto, farmer, farmerConfig);
        
        List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
            .findByFarmer(farmer.getPkey(), appid, GwcSupplierPickupLocationInfo.class);
        if(findByFarmer.isEmpty())
        {
            GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
            gspl.setAddress(farmerConfig.getAddr());
            gspl.setLongitude(farmerConfig.getLongitude());
            gspl.setLatitude(farmerConfig.getLatitude());
            findByFarmer.add(gspl);
        }
        if(latitude != null && longitude != null)
        {
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
        }
        dto.setSplList(findByFarmer);
        dto.setMyMsd(getMsdBalance(mktMember.getPkey(), appid));
        return dto;
    }
    
    private BigDecimal getMsdBalance(Integer memberPkey, Integer ascription)
    {
        BigDecimal balance = BigDecimal.ZERO;
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd != null)
        {
            balance = memberMsd.getBalance();
        }
        return balance;
    }
    
    private String getTimeFormat(Integer time)
    {
        String res = "";
        if (time > 9)
            res = time + "";
        else
            res = "0" + time;
        return res;
    }
    
    /*
     * 下订单
     */
    @Transactional(rollbackFor = Throwable.class)
    public OrderTotalV2Info commitOrder(OrderTotalV2Info dto)
    {
        Long k = System.currentTimeMillis();
        log.info("----------提交订单----------");
        Long ll = orderTokenMap.get("order:" + dto.getMember());
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
        System.out.println("pstime: " + dto.getPstime());
        System.out.println("dto.getPayType(): " + dto.getPayType());
        appOrderV2Expand.newChkOrder(dto, true);
        if (dto.getPkey() != null) return appOrderV2Expand.updOrderOne(dto, dto.getPkey());
        if (dto.getPkey1() != null) appOrderV2Expand.updOrderOne(dto, dto.getPkey1());
        if (dto.getPkey2() != null) appOrderV2Expand.updOrderOne(dto, dto.getPkey2());
        if (dto.getPkey1() != null || dto.getPkey2() != null)
        {
            return dto;
        }
        String payNumber = numberUtils.createOrderNumber();
        MktOrder order1 = null;
        MktOrder order2 = null;
        BigDecimal longitude = null;
        BigDecimal latitude = null;
        String body = "";
        if (dto.getPointInfo() != null && !dto.getPointInfo().isEmpty())
        {
            checkPayType(dto.getPointInfo(), dto.getPayType());
            OrderType orderType = OrderType.INTEGRAL_ORDER;
            if (dto.getOrderType().equals(OrderType.GIFT_ORDER) || dto.getOrderType().equals(OrderType.COUPON_ORDER))
            {
                orderType = dto.getOrderType();
            }
            Integer appid = MobileSession.appid();
            order1 = appOrderV2Expand.insOrderOne(dto,
                Constant.Operation + appid,
                Constant.Operation + appid,
                payNumber + "1",
                dto.getPointInfo(),
                orderType,
                body,
                longitude,
                latitude);
            dto.setPkey1(order1.getPkey());
        }
        if (dto.getFarmerInfo() != null && !dto.getFarmerInfo().isEmpty())
        {
            checkPayType(dto.getFarmerInfo(), dto.getPayType());
            order2 = appOrderV2Expand.insOrderOne(dto,
                dto.getFarmer(),
                dto.getCompany(),
                payNumber + "2",
                dto.getFarmerInfo(),
                dto.getOrderType(),
                body,
                longitude,
                latitude);
            dto.setPkey2(order2.getPkey());
        }
        dto.setCode(payNumber);
        log.info("下单dto.getPostage(): {}", dto.getPostage());
        log.info("下单order2: {}", JsonUtil.toString(order2, true));
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) || dto.getPayType().equals(PayType.ORDER_MSD))
        {
            if (order1 != null) appOrderManager.payAfterOrder(order1);
            if (order2 != null) appOrderManager.payAfterOrder(order2);
        }
        if (dto.getPayType().equals(PayType.NM_MEMBER))
        {
            try
            {
                if (order1 != null) appOrderManager.payAfterOrder(order1);
                if (order2 != null) appOrderManager.payAfterOrder(order2);
            }
            catch (Exception e)
            {
                throw TofocusException.of(LejiaErrCode.XASZ_PAY_ERROR, e.getMessage());
            }
        }
        ionvokePayWx(dto, payNumber, order1, order2);
        
        log.info("----------订单提交成功----------");
        log.info("----------订单提交用时: {}----------", System.currentTimeMillis() - k);
        return dto;
    }

    
    // 调用支付 和微信支付有关
    private void ionvokePayWx(OrderTotalV2Info dto, String payNumber, MktOrder order1, MktOrder order2)
    {
        BigDecimal amt = BigDecimal.ZERO;
        Integer memberPkey = null;
        Integer ascription = null;
        List<MktOrder> orderList = new ArrayList<>();
        if (order1 != null)
        {
            amt = amt.add(order1.getWeixinAmt());
            memberPkey = order1.getMember();
            ascription = order1.getAscription();
            orderList.add(order1);
        }
        if (order2 != null)
        {
            amt = amt.add(order2.getWeixinAmt());
            memberPkey = order2.getMember();
            ascription = order2.getAscription();
            orderList.add(order2);
        }
        if(amt.compareTo(BigDecimal.ZERO) == 0)
        {
            if (order1 != null) appOrderManager.payAfterOrder(order1);
            if (order2 != null) appOrderManager.payAfterOrder(order2);
        }
        
        if(dto.getPayType().equals(PayType.MSD_COMBINATION))
        {
            // 第三方餐饮系统没有退款接口 暂时不支持组合支付
            if (cateringEnabled && cateringAscription.equals(ascription))
            {
                throw TofocusException.of(LejiaErrCode.CATERING_ERROR);
            }
            BigDecimal loadMsd = getMsdBalance(memberPkey);
            updOrderOtherAmt(loadMsd, orderList, amt);
            
            if(order1 != null)
            {
                if(order1.getWeixinAmt().compareTo(BigDecimal.ZERO) <= 0)
                {
                    order1.setPayType(PayType.ORDER_MSD);
                    dto.setPayType(PayType.ORDER_MSD);
                    appOrderManager.payAfterOrder(order1);
                }
                else if(order1.getOtherAmt() != null && order1.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                    memberMsdManager.updLockMsd(order1.getMember(), order1.getOtherAmt(), order1.getAscription());
            }
            if(order2 != null)
            {
                if(order2.getWeixinAmt().compareTo(BigDecimal.ZERO) <= 0)
                {
                    order2.setPayType(PayType.ORDER_MSD);
                    dto.setPayType(PayType.ORDER_MSD);
                    appOrderManager.payAfterOrder(order2);
                }
                else if(order2.getOtherAmt() != null && order2.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                    memberMsdManager.updLockMsd(order2.getMember(), order2.getOtherAmt(), order2.getAscription());
            }
        }
        if(dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
        {
            BigDecimal comm = commManager.loadComm(memberPkey);
            updOrderOtherAmt(comm, orderList, amt);
            if(order1 != null)
            {
                if(order1.getWeixinAmt().compareTo(BigDecimal.ZERO) <= 0)
                {
                    order1.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
                    dto.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
                    appOrderManager.payAfterOrder(order1);
                }
                else if(order1.getOtherAmt() != null && order1.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                    commManager.updLockComm(order1.getMember(), order1.getOtherAmt(), order1.getCode(), order1.getAscription());
            }
            if(order2 != null)
            {
                if(order2.getWeixinAmt().compareTo(BigDecimal.ZERO) <= 0)
                {
                    order2.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
                    dto.setPayType(PayType.ORDER_ELECTRONIC_ACCOUNT);
                    appOrderManager.payAfterOrder(order2);
                }
                else if(order2.getOtherAmt() != null && order2.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                    commManager.updLockComm(order2.getMember(), order2.getOtherAmt(), order2.getCode(), order2.getAscription());
            }
        }
        if (dto.getPayType().equals(PayType.ORDER_WEIXIN) 
            || dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION)
            || dto.getPayType().equals(PayType.MSD_COMBINATION)) try
        {
            Integer appid = MobileSession.appid();
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
    
    private BigDecimal getMsdBalance(Integer memberPkey)
    {
        BigDecimal balance = BigDecimal.ZERO;
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd != null)
        {
            balance = memberMsd.getBalance();
        }
        return balance;
    }
    
    // 判断是否可用组合支付 
    private void checkPayType(List<OrderV2Info> infos, PayType payType)
    {
        if(!PayType.ORDER_MSD.equals(payType) && !PayType.MSD_COMBINATION.equals(payType))
            return;
        MsdPayConfig mpConfig = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(mpConfig == null)
        {
            mpConfig = new MsdPayConfig();
            mpConfig.setFarmerGoods(false);
            mpConfig.setSysGoods(false);
        }
        List<Long> goodsKeys = new ArrayList<>();
        for(OrderV2Info o : infos)
        {
            goodsKeys.add(o.getGoods());
        }
        
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
    
    @Deprecated
    public WxPayData payCoupon(String activity)
    {
        log.info("----------活动购买卡券----------");
        String openid = MobileSession.openid();
        activity = "hhhnmddddd" + activity;
        Long ll = orderTokenMap.get("linshi:" + openid + "---" + activity);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("linshi:" + openid + "---" + activity, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.LINSHI_HUODONG_CLICK_ERROR);
        }
        orderTokenMap.put("linshi:" + openid + "---" + activity, System.currentTimeMillis());
        
        // 时间判断 活动截止到星期五
        Calendar cal = Calendar.getInstance();
        CardLinshiDto cardLinshiDto = cardLinshiMap.get(activity);
        if (cardLinshiDto == null) throw TofocusException.of(LejiaErrCode.LINSHI_HUODONG_END_ERROR);
        Date startDate = cardLinshiDto.getStartDate();
        if (startDate.compareTo(cal.getTime()) > 0) throw TofocusException.of(LejiaErrCode.LINSHI_HUODONG_START_ERROR);
        Date endDate = cardLinshiDto.getEndDate();
        if (endDate.compareTo(cal.getTime()) < 0) throw TofocusException.of(LejiaErrCode.LINSHI_HUODONG_END_ERROR);
        
        Integer appid = MobileSession.appid();
        log.info("appid: {}", appid);
        BigDecimal amt = cardLinshiDto.getAmt();
        WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
        String payNumber = numberUtils.createOrderNumber();
        payNumber = "95" + payNumber;
        PayJs payJs;
        Integer card = cardLinshiDto.getCard();
        Boolean byOpenid = memberCouponLinshiDao.byOpenid(openid, card);
        if(Boolean.TRUE.equals(byOpenid))
            throw TofocusException.of(LejiaErrCode.LINSHI_HUODONG_ERROR);
        MktCard mktCard = cardDao.get(card);
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        // 优惠券总数量判断
        Integer num = cardLinshiDto.getNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum - num) < 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        if ((count - issuedNum) == 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        // 每日优惠券上限判断
        Integer dayNum = cardLinshiDto.getDayNum();
        Integer issuedDayNum = memberCouponLinshiDao.count(card);
        if (issuedDayNum >= dayNum) throw TofocusException.of(LejiaErrCode.LINSHI_CARD_DAYNUM_ISEMPTY);
        if (amt == null || amt.compareTo(BigDecimal.ZERO) == 0)
        {
            // 免费直接领
            cardManager.insMemberCardLinshiFree(openid, card, activity, payNumber);
            return null;
        }
        try
        {
            if (StringUtils.isBlank(openid)) return new WxPayData();
            MktMemberCouponLinshi mcl = new MktMemberCouponLinshi();
            mcl.setCode(payNumber);
            mcl.setOpenid1(openid);
            mcl.setCard(card);
            mcl.setActivity(activity);
            mcl.setAscription(MobileSession.appid());
            mcl.setStatus(OrderStatus.UNPAID_ORDER);
            memberCouponLinshiDao.add(mcl);
            if (MobileSession.appid().equals(13))
            {
                return chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payNumber, amt);
            }
            else
            {
                
                payJs = wxPayManger.topayIvc(MobileSession.billIp(), openid, payNumber, amt, wxc);
                return BeanUtil.beanFrom(WxPayData.class, payJs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new WxPayData();
    }
    
    public Boolean insMapHuodongLinshi(String activity, CardLinshiDto info)
    {
        activity = "hhhnmddddd" + activity;
        cardLinshiMap.put(activity, info);
        return true;
    }
    
    /*
     * 获取未支付订单支付页面
     */
    public OrderTotalV2Info getUnpaidOrder(Integer pkey, Integer addressPkey, BigDecimal longitude, BigDecimal latitude)
    {
        OrderTotalV2Info dto = new OrderTotalV2Info();
        MktOrder order = orderDao.get(pkey);
        List<MktOrderLine> lines = orderLineDao.select().in("orderPkey", pkey).exec();
        BeanUtils.copyProperties(order, dto);
        dto.setUnpayType(dto.getPayType());
        if(dto.getCard() != null)
        {
            MktMemberCard mktMemberCard = memberCardDao.get(dto.getCard());
            if(mktMemberCard != null)
            {
                if(!CardStatus.UNUSED.equals(mktMemberCard.getStatus()) || Boolean.TRUE.equals(mktMemberCard.getInvalid()))
                {
                    dto.setCard(null);
                    dto.setCardAmt(BigDecimal.ZERO);
                }
            }
        }
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        lines.forEach(e -> {
            gkeys.add(e.getGoods().intValue());
            skeys.add(e.getSpace().intValue());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        AmtoWeightTotal awTotal = new AmtoWeightTotal();
        BigDecimal reducePrice = BigDecimal.ZERO;
        
        Map<Integer, OrderV2Info> map = new HashMap<>();
        LevelType level = MobileSession.member().getLevel();
        Integer appid = MobileSession.appid();
        String farmerKey = Constant.Operation + appid;
        // 商品限购校验
        appOrderV2Expand.checkBugGoodsNumOrderLine(lines, goodsMap);
        
        Boolean flagCut = false;
        if (OrderType.CUT_ORDER.equals(dto.getOrderType())) flagCut = true;
        Boolean isPostage = false;
        for (MktOrderLine gwc : lines)
        {
            if (!goodsMap.containsKey(gwc.getGoods().intValue())) continue;
            if (!spaceMap.containsKey(gwc.getSpace().intValue()))
            {
                throw TofocusException.of(LejiaErrCode.GWC_SPACE_NOTEXIST);
            }
            MktGoods goods = goodsMap.get(gwc.getGoods().intValue());
            if ((Constant.Operation + appid).equals(farmerKey)
                && !(Constant.Operation + appid).equals(goods.getFarmer())) farmerKey = goods.getFarmer();
            if(Boolean.TRUE.equals(goods.getIsPostage()))
                isPostage = true;
            if(MType.GIFT_GOODS.equals(goods.getMType()))
                isPostage = true;
            if(goods.getMType().equals(MType.BOX_GOODS))
            {
                MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
                MktAppAddrDTO addr = new MktAppAddrDTO();
                addr.setAddr(goodsBox.getDesktopName());
                addr.setPkey(goodsBox.getDesktop());
                dto.setAddr(addr);
                dto.setDistributionType(DistributionType.DINE_IN);
                dto.setDineIn(true);
            } 
            MktGoodsSpace space = spaceMap.get(gwc.getSpace().intValue());
            Integer num = gwc.getNum();
            
            // 校验库存及下架
            if (!goods.getEnabled()) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED, goods.getTitle() + "已下架");
            if (level.equals(LevelType.PAID_MEMBER) && space.getPriceMember().compareTo(BigDecimal.ZERO) == 1)
            {
                BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
                reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(num)));
            }
            appOrderV2Expand.addInfoAndLineGwc(dto, awTotal, goods, space, map, num, gwc.getPkey(), gwc.getAssociation(), gwc.getAssociationName(), flagCut);
        }
        SysFarmer farmer = sysFarmerDao.get(farmerKey);
        SysFarmerConfig farmerConfig = farmer.getConfig();
        if (order.getOrderType().equals(OrderType.GIFT_ORDER))
        {
            MktGoods goods = goodsMap.get(lines.get(0).getGoods().intValue());
            GoodsGiftInfo giftInfo =
                appOrderV2Expand.getGoodsGiftInfo(Integer.valueOf(goods.getExtendCon()), goods.getPkey(), farmer);
            dto.setGiftInfo(giftInfo);
        }
        Integer qrCode = MobileSession.qrCode();
        // 组装farmerConfig相关数据
        if(lines.size() != 1)
            isPostage = false;
        appOrderV2Expand.farmerConfigDtoGwc(dto, farmerConfig, reducePrice, awTotal, addressPkey, qrCode, isPostage);
        if(dto.getCardPostage() != null)
        {
            MktMemberCard mktMemberCard = memberCardDao.get(dto.getCardPostage());
            if(mktMemberCard != null)
            {
                if(!CardStatus.UNUSED.equals(mktMemberCard.getStatus()) || Boolean.TRUE.equals(mktMemberCard.getInvalid()))
                {
                    dto.setCardPostage(null);
                    dto.setCardPostageAmt(BigDecimal.ZERO);
                }
                else
                {
                    MktCard mktCard = cardDao.get(mktMemberCard.getCard());
                    if(mktCard != null)
                        dto.setCardPostageName(mktCard.getTitle());
                    dto.setOldPostage(dto.getPostage());
                    if(Boolean.TRUE.equals(mktMemberCard.getAvoidPostage()))
                    {
                        dto.setCardPostageAmt(dto.getPostage());
                        dto.setPostage(BigDecimal.ZERO);
                    }
                    else
                    {
                        dto.setPostage(dto.getPostage().subtract(mktMemberCard.getCost()));
                        if(dto.getPostage().compareTo(BigDecimal.ZERO) < 0)
                            dto.setPostage(BigDecimal.ZERO);
                    }
                }
            }
        }
        appOrderV2Expand.assembleDtoGwc(dto, farmerKey, awTotal.getPoints(), farmer);
        if (lines.size() == 1)
        {
            MktGoods goods = goodsMap.get(lines.get(0).getGoods().intValue());
            MType mtype = MType.INTEGRAL_MSD_GOODS;
            if(goods != null)
            {
                mtype = goods.getMType();
            }
            appOrderV2Expand.checkMtypes(dto, farmer, mtype);
        }
        else
        {
            appOrderV2Expand.checkMtypesGwc(dto, farmer);
        }
        // 校验
        appOrderV2Expand.newChkOrder(dto, false);
        
        assembleWeekTimeAndDayTime(dto, farmer, farmerConfig);
        
        List<GwcSupplierPickupLocationInfo> findByFarmer = farmerPickupLocationDao
            .findByFarmer(farmer.getPkey(), farmer.getAscription(), GwcSupplierPickupLocationInfo.class);
        if(findByFarmer.isEmpty())
        {
            GwcSupplierPickupLocationInfo gspl = new GwcSupplierPickupLocationInfo();
            gspl.setAddress(farmerConfig.getAddr());
            gspl.setLongitude(farmerConfig.getLongitude());
            gspl.setLatitude(farmerConfig.getLatitude());
            findByFarmer.add(gspl);
        }
        if(latitude != null && longitude != null)
        {
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
        }
        dto.setSplList(findByFarmer);
        return dto;
    }
    
    /*
     * 读取订单详情
     */
    public OrderDetailsV2Info loadOrder(Integer pkey, RefundJdType jdType)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null) throw TofocusException.of(LejiaErrCode.ORDER_DEL);
        OrderDetailsV2Info res = new OrderDetailsV2Info();
        BeanUtils.copyProperties(order, res);
//        if(res.getRefundAmt() == null)
//            res.setRefundAmt(BigDecimal.ZERO);
//        if(res.getRefundPoint() == null)
//            res.setRefundPoint(0);
        if(order.getCardPostage() != null)
        {
            MktMemberCard mktMemberCard = memberCardDao.get(order.getCardPostage());
            if(mktMemberCard != null)
            {
                MktCard mktCard = cardDao.get(mktMemberCard.getCard());
                if(mktCard != null)
                    res.setCardPostageName(mktCard.getTitle());
            }
        }
        List<MktOrderLine> lines = orderLineDao.select().in("orderPkey", pkey).exec();
//        List<Integer> gkeys = new ArrayList<>();
//        lines.forEach(e -> {
//            gkeys.add(e.getGoods().intValue());
//        });
//        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
        if (farmer != null)
        {
            res.setFarmerName(farmer.getName());
            res.setTel(farmer.getTel());
        }
        if((Constant.Operation + order.getAscription()).equals(order.getFarmer()))
        {
            MktAppConfig appConfig = appConfigManager.getAppConfig(order.getAscription());
            res.setTel(appConfig.getTel());
        }
        MktAppAddrDTO addDto = new MktAppAddrDTO();
        MktOrderDesc orderDesc = orderDescDao.get(pkey);
        res.setLogistics("");
        res.setKdCode("");
        if (orderDesc != null && (!order.getOrderType().equals(OrderType.GIFT_ORDER)
            || !order.getOrderType().equals(OrderType.COUPON_ORDER)))
        {
            MktAddr mktAddr = addrDao.getAddrDelivery(order.getMember(), orderDesc.getAddr());
            if(mktAddr != null)
                addDto.setPkey(mktAddr.getPkey());
            addDto.setAddr(orderDesc.getAddr());
            addDto.setAddrDetail(orderDesc.getAddr());
            if (addDto.getAddrDetail() == null) addDto.setAddrDetail("");
            if (addDto.getAddr() == null) addDto.setAddr("");
            addDto.setMobile(orderDesc.getMobile());
            addDto.setName(orderDesc.getName());
            if (addDto.getMobile() == null) addDto.setMobile("");
            if (addDto.getName() == null) addDto.setName("");
            addDto.setEnabled(true);
            addDto.setDistance(orderDesc.getDistance());
            res.setAddr(addDto);
            res.setRemark(orderDesc.getRemark());
            res.setDrTime(orderDesc.getDrTime());
            Integer appid = MobileSession.appid();
            if ((Constant.Operation + appid).equals(order.getFarmer()) && !DistributionType.PICKUP.equals(order.getDistributionType()))
            {
                if(!OrderType.INTEGRAL_PRESALE_ORDER.equals(order.getOrderType()))
                    res.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
                else if(orderDesc.getFhTime() != null)
                    res.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
                
                String logistics = orderDesc.getLogistics();
                res.setLogistics(logistics == null ? "" : logistics);
                String kdCode = orderDesc.getKdCode();
                res.setKdCode(kdCode == null ? "" : kdCode);
            }
            
            log.warn("[配送距离] loadOrder {}米， {}", addDto.getDistance(), addDto.getAddr());
        }
//        Map<Long, OrderV2Info> map = new HashMap<>();
        if (order.getOrderType().equals(OrderType.GIFT_ORDER))
        {
            List<MktGiftOnList> giftList = giftManager.listByOrder(pkey);
            StringBuilder cc = new StringBuilder();
            for (MktGiftOnList g : giftList)
            {
                cc.append(g.getCardNumber());
                cc.append(",");
            }
            String cardCode = cc.toString();
            if (cardCode.length() > 0) cardCode = cardCode.substring(0, cardCode.length() - 1);
            res.setCardCode(cardCode);
            
            MktGoods goods = goodsDao.get(lines.get(0).getGoods().intValue());
            GoodsGiftInfo giftInfo =
                appOrderV2Expand.getGoodsGiftInfo(Integer.valueOf(goods.getExtendCon()), goods.getPkey(), farmer);
            res.setGiftInfo(giftInfo);
        }
        // 获取退款信息
        List<MktOrderRefund> orderRefundList = orderRefundDao.listOrderPkey(pkey);
        if (orderRefundList != null && !orderRefundList.isEmpty())
        {
            if (orderRefundList.size() > 1)
                res.setIsComplex(true);
            else
                res.setIsComplex(false);
            MktOrderRefund orderRefund = orderRefundList.get(0);
            res.setRefundPkey(orderRefund.getPkey());
            
            res.setRefundAmt(order.getRefundAmt());
            res.setRefundPoint(order.getRefundPoint());
            if(res.getRefundAmt() == null)
                res.setRefundAmt(BigDecimal.ZERO);
            if(res.getRefundPoint() == null)
                res.setRefundPoint(0);
            res.setReason(orderRefund.getReason());
            res.setDescribe(orderRefund.getDescribe());
            if(res.getDescribe() == null)
                res.setDescribe("");
            res.setJdType(orderRefund.getJdType());
            if(res.getJdType() == null)
                res.setJdType(RefundJdType.RETURN_MONEY);
            res.setRefundStatus(orderRefund.getStatus());
            res.setRefundStatusName(orderRefund.getStatus().getName());
            res.setPhoto(orderRefund.getPhoto());
            res.setRefundTime(orderRefund.getCreatedTime());
            if(RefundStatus.JD_APPROVAL_ACCEPTED.equals(orderRefund.getStatus()))
            {
                MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(orderRefund.getPkey());
                StringBuffer sb = new StringBuffer();
                if(ore != null && CourierType.JD_DOOR_TO_DOOR_PICKUP.equals(ore.getCourierType()))
                {
                    sb.append("上门取件: 京东快递 ");
                    String pt = ore.getPickupTimeStart().substring(0, 10);
//                    Date date = DateUtil.formatDateStr(pt, "yyyy-MM-dd");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate targetDate = LocalDate.parse(pt, formatter);
                    boolean isToday = targetDate.isEqual(LocalDate.now());
                    if(isToday)
                        sb.append("今天").append(ore.getPickupTimeStart().substring(11,16))
                        .append("-").append(ore.getPickupTimeEnd().substring(11,16));
                    else
                        sb.append(ore.getPickupTimeStart().substring(0, 16)).append("-")
                        .append("-").append(ore.getPickupTimeEnd().substring(11,16));
                    sb.append(" 上门");
                }
                else
                {
                    sb.append("自行寄出(");
                    if(StringUtils.isNotBlank(ore.getCourierCompany()))
                    {
                        sb.append("已寄出):您已寄出商品,待商家收货");
                    }
                    else
                    {
                        sb.append("未寄出):审核已完成,待寄出商品");
                    }
                }
                res.setJdExpress(sb.toString());
            }
        }
        List<Integer> refundPkeyList = orderRefundDao.listStatusKey(pkey);
        Map<Integer, BigDecimal> refundLineMap = new HashMap<>();
        Map<Integer, Integer> refundLinePointMap = new HashMap<>();
        if (refundPkeyList != null && !refundPkeyList.isEmpty())
        {
            refundLineMap = orderRefundLineDao.aggLinePkeyAmt(refundPkeyList);
            refundLinePointMap = orderRefundLineDao.aggLinePkeyPoint(refundPkeyList);
        }
        res.setHasApplyingRefund(orderRefundDao.existApplying(pkey));
        
        List<Long> skuIdList = new ArrayList<>();
       
        for (MktOrderLine line : lines)
        {
            appOrderV2Expand.addInfoAndLineLoadOrder(res,
                line,
//                goodsDao.get(line.getGoods().intValue()),
//                goodsMap.get(line.getGoods().intValue()),
//                map,
                refundLineMap,
                refundLinePointMap);
            skuIdList.add(line.getSpace());
        }
        Map<Long,OrderV2Info> map = new HashMap<>();
        for (OrderV2Info o : res.getInfos())
        {
            BigDecimal oSumAmt = BigDecimal.ZERO;
            BigDecimal refundAmt = BigDecimal.ZERO;
            Integer point = 0;
            for (OrderGwcV2OnList og : o.getLines())
            {
                if (og.getRefundAmt() == null)
                    og.setRefundAmt(BigDecimal.ZERO);
                if (og.getRefundPoint() == null)
                    og.setRefundPoint(0);
                refundAmt = refundAmt.add(og.getRefundAmt());
                if (og.getCouponAmt() != null)
                {
                    oSumAmt = oSumAmt.add(og.getCouponAmt());
                }
                else if (og.getCouponPrice() != null)
                {
                    BigDecimal multiply = og.getCouponPrice().multiply(new BigDecimal(og.getNum()));
                    oSumAmt = oSumAmt.add(multiply);
                }
                else
                {
                    BigDecimal multiply = og.getPrice().multiply(new BigDecimal(og.getNum()));
                    oSumAmt = oSumAmt.add(multiply);
                }
                if(og.getPoint() != null)
                    point += og.getPoint();
            }
            if(o.getCouponPrice().compareTo(BigDecimal.ZERO) == 0)
            {
                if(o.getRefundNum() == o.getNum())
                    o.setWholeRefund(true);
                else
                    o.setWholeRefund(false);
            }
            else
            {
                if (oSumAmt.compareTo(refundAmt) == 0)
                {
                    o.setRefundNum(o.getNum());
                    o.setWholeRefund(true);
                }
                else
                {
                    o.setWholeRefund(false);
                }
            }
            o.setPoint(point);
            map.put(o.getSpace().longValue(), o);
        }
        if(jdType != null)
        {
            JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(order.getCode());
            Map<Long, OrderV2Info> jdMap = jdOrderRefundManager.mapGoodsAttributes(jdType, skuIdList, joc.getJdCode(), map);
            log.info("校验售后情况: {}", JsonUtil.toString(jdMap, true));
            for (OrderV2Info o : res.getInfos())
            {
                if(jdMap.containsKey(o.getSpace().longValue()))
                {
                    OrderV2Info oi = jdMap.get(o.getSpace().longValue());
                    o.setJdRefundNum(oi.getJdRefundNum());
                    o.setJdAttributes(oi.getJdAttributes());
                    o.setJdDoor(oi.getJdDoor());
                    o.setSelfMailing(oi.getSelfMailing());
                }
                if(o.getJdDoor() == null)
                    o.setJdDoor(true);
                if(o.getSelfMailing() == null)
                    o.setSelfMailing(true);
            }
        }
        
        if (order.getStatus().equals(OrderStatus.UNPAID_ORDER))
        {
            //            BigDecimal postage = order.getPostage();
            //            if (postage != null && postage.compareTo(BigDecimal.ZERO) > 0)
            //            {
            //                res.setPickupAmt(order.getAmtn().subtract(postage));
            //            }
            //            else
            res.setPickupAmt(order.getAmtn());
        }
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(order.getFarmer(), addDto);
        List<DistributionTypeDTO> rlsit = appOrderV2Expand.buildDistributionType(farmer, deliveryTimeConfig);
        res.setDistype(rlsit);
        if (order.getOrderOir().equals(OrderOir.MARKET_MALL))
        {
            MktExpress express = expressDao.selectOne().eq("orderId", pkey).exec();
            if (express != null)
            {
                res.setArrivedPhoto(express.getPhoto());
                //  if("-1".equals(express.getCourier()))
                if (express.getCourier().equals(-1))
                {
                    res.setCourierName(express.getCourierName());
                    res.setCourierMobile(express.getCourierMobile());
                }
                else
                {
                    res.setExpressStatus(express.getStatus());
                    MktCourier courier = courierDao.get(express.getCourier());
                    if (courier != null)
                    {
                        res.setCourierName(courier.getName());
                        res.setCourierMobile(courier.getMobile());
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(res.getPickupCode()) && res.getPickupCode().length() < 4)
        {
            res.setPickupCode(null);
        }

        // 如果顺丰发货，查询物流节点（目前默认就一个包裹）
        if (res.getExpressType() == ExpressType.EXPRESS_SF)
        {
            List<MktOrderExpressRouteInfo> routes =
                    orderExpressRouteDao.listByOrderPkey(pkey, MktOrderExpressRouteInfo.class);
            res.setExpressRoutes(routes);
        }
        
        // 查出供应商名称
        if (order.getSupplier() != null)
        {
            MktSupplier supplier = supplierDao.get(order.getSupplier());
            if (supplier != null) res.setSupplierName(supplier.getName());
        }
//        res.setUrl(pickupWriteOffUrl);
        res.setUrl(pickupWriteOffUrl + "_" + order.getAscription());
        if(PayType.ORDER_WEIXIN.equals(res.getPayType()))
        {
            MktPayLine pl = payLineDao.getOrderNumber(res.getCode().substring(0, 14));
            if(pl != null)
                res.setTransactionId(pl.getCode());
            List<MktOrder> listCode = orderDao.listCode(res.getCode().substring(0, 14));
            if(listCode.size() > 1)
            {
                for(MktOrder o : listCode)
                {
                    if(OrderStatus.CONFIRM_ORDER.equals(o.getStatus()))
                    {
                        res.setOpenBusinessView(false);
                    }
                }
            }
            if(qfAscription.equals(res.getAscription()))
            {
                ThirdPayLineEntity tpl = thirdPayLineDao.byMerOrderId(res.getCode().substring(0, 14));
                if(tpl != null)
                {
                    res.setTargetOrderId(tpl.getTargetOrderId());
                    res.setMerOrderId(tpl.getMerOrderId());
                }
            }
            // 临时处理
            if(checkWxOrder(res.getAscription()))
                res.setOpenBusinessView(false);
        }
        else
            res.setOpenBusinessView(false);
        if (res.getStatus() == OrderStatus.CONFIRM_ORDER)
        {
            if (res.getDrTime() != null)
            {
                LocalDateTime localDrTime = DateUtil.date2LocalDateTime(res.getDrTime());
                if (localDrTime.plusHours(7 * 24).isAfter(LocalDateTime.now()))
                    res.setAllowedComment(true);
            }
            res.setHasComment(orderGoodsCommentDao.existByOrder(res.getPkey()));
        }
        return res;
    }
    
    
    public BigDecimal checkNmMemberPay()
    {
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getFarmer(MobileSession.farmerPkey());
        if (xaszAssociationEntity == null) return null;
        MktMember member = MobileSession.member();
        return saasTokenPublicManager.getAccountBalance(member.getMobile(), member.getOpenid1());
    }
    
    private void assembleWeekTimeAndDayTime(OrderTotalV2Info dto, SysFarmer farmer, SysFarmerConfig farmerConfig)
    {
        String weekTime = appIndexManager.assembleWeekTime(farmerConfig);
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(farmer.getPkey(), farmer.getAscription());
        String dayTime = "";
        
        for (int i = 0; i < listTime.size(); i++)
        {
            SysFarmerTime ft = listTime.get(i);
            String sh = getTimeFormat(ft.getStartHour());
            String sm = getTimeFormat(ft.getStartMinute());
            String eh = getTimeFormat(ft.getEndHour());
            String em = getTimeFormat(ft.getEndMinute());
            if (i == listTime.size() - 1)
            {
                dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em;
            }
            else
                dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em + ", ";
        }
        dto.setWeekTime(weekTime);
        dto.setDayTime(dayTime);
    }
    
    @Autowired
    private MktActivityWriteOffLinshiDao activityWriteOffLinshiDao;
    
    public boolean checkWriteOffActivity(String name)
    {
        MktMember member = MobileSession.member();
        if (member == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        return activityWriteOffLinshiDao.exist(name, member.getPkey(), member.getAscription());
    }
    
    public boolean writeOffActivity(String name)
    {
        MktMember member = MobileSession.member();
        if (member == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        boolean exist = activityWriteOffLinshiDao.exist(name, member.getPkey(), member.getAscription());
        if (exist) throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT, "会员不能重复核销");
        MktActivityWriteOffLinshi bean = new MktActivityWriteOffLinshi();
        bean.setName(name);
        bean.setMember(member.getPkey());
        bean.setAscription(member.getAscription());
        activityWriteOffLinshiDao.put(bean);
        return true;
    }
    
    public void activityQrCode(String name, HttpServletResponse response)
    {
        if (StringUtil.isBlank(name)) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "活动名称不能为空");
        String baseUrl = "pages/activity/whiteOff/index";
        try
        {
            response.setContentType("image/jpeg;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(name + ".jpg", "UTF-8"));
            makeQrCode(baseUrl + "?name=" + name, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("生成活动礼品核销二维码失败，名称：{}", name, e);
        }
    }
    
    private void makeQrCode(String content, OutputStream outputStream)
        throws Exception
    {
        int width = 500;
        int height = 500;
        //二维码的图片格式
        String format = "jpg";
        Hashtable hints = new Hashtable();
        //内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        //生成二维码
        MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
    }
    
    // 核销 自提订单
    public void writeOffPickupOrder(String kcCode, String verifyCode)
    {
        MktOrder order = orderDao.selectOne()
            .eq(MktOrder.F.code, kcCode)
            .eq(MktOrder.F.orderOir, OrderOir.MARKET_MALL)
            .eq(MktOrder.F.distributionType, DistributionType.PICKUP)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .exec();
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (order.getStatus() == OrderStatus.SHIPPED_ORDER || order.getStatus() == OrderStatus.ARRIVED_ORDER
            || order.getStatus() == OrderStatus.CONFIRM_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "已核销，无需重复操作");
        if (order.getStatus() != OrderStatus.DELIVERED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前状态不支持核销");
        MktMember member = MobileSession.member();
        if (member == null || StringUtil.isBlank(member.getMobile()))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "无权核销该订单");
        MktManager manager =
            managerDao.getByMobileAndFarmerOrder(member.getMobile(), order.getFarmer(), order.getAscription());
        if (manager == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "无权核销该订单");
        // 验证核销码
        String realVerifyCode =
            OrderVerifyCodeGenerator.build(order.getCode(), order.getPkey(), order.getCreatedTime());
        if (!realVerifyCode.equals(verifyCode)) throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        order.setPickupFlag(true);
        order.setPickupTime(DateUtil.formatDate(new Date()));
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        desc.setDrTime(new Date());
        desc.setEndTime(new Date());
        orderDescDao.update(desc);
        orderDao.update(order);
        
        appSupplierManager.uploadPickupInfo2Wx(order.getPkey(), order.getCode(), order.getMember(), order.getAscription());
    }
    
    public AppSupplierOrderInfo getOrderByScanVerifyCode(String kcCode, String verifyCode)
    {
        AppSupplierOrderInfo info = orderDao.selectOne()
            .eq(MktOrder.F.code, kcCode)
            .eq(MktOrder.F.orderOir, OrderOir.MARKET_MALL)
            .eq(MktOrder.F.distributionType, DistributionType.PICKUP)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .execDto(AppSupplierOrderInfo.class);
        if (info == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (info.getStatus() != OrderStatus.DELIVERED_ORDER && info.getStatus() != OrderStatus.SHIPPED_ORDER
            && info.getStatus() != OrderStatus.ARRIVED_ORDER && info.getStatus() != OrderStatus.CONFIRM_ORDER
            && info.getStatus() != OrderStatus.REFUNDED_ORDER 
            && info.getStatus() != OrderStatus.WAIT_ARRIVAL_ORDER && info.getStatus() != OrderStatus.WAIT_WRITEOFF_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前状态不支持核销");
        
        MktMember member = MobileSession.member();
        if (member == null || StringUtil.isBlank(member.getMobile()))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "无权核销该订单");
        MktManager manager =
            managerDao.getByMobileAndFarmerOrder(member.getMobile(), info.getFarmer(), MobileSession.appid());
        if (manager == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "无权核销该订单");
        
        // 验证核销码
        String realVerifyCode = OrderVerifyCodeGenerator.build(info.getCode(), info.getPkey(), info.getCreatedTime());
        if (!realVerifyCode.equals(verifyCode)) throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        
        MktOrderDesc orderDesc = orderDescDao.get(info.getPkey());
        if (orderDesc != null)
        {
            AppSupplierOrderAddr addr = new AppSupplierOrderAddr();
            addr.setAddr(orderDesc.getAddr());
            addr.setAddrDetail(orderDesc.getAddr());
            addr.setMobile(orderDesc.getMobile());
            addr.setName(orderDesc.getName());
            addr.setEnabled(true);
            addr.setDistance(orderDesc.getDistance());
            if (addr.getAddrDetail() == null) addr.setAddrDetail("");
            if (addr.getAddr() == null) addr.setAddr("");
            if (addr.getMobile() == null) addr.setMobile("");
            if (addr.getName() == null) addr.setName("");
            info.setRemark(orderDesc.getRemark());
            info.setAddr(addr);
        }
        
        List<MktOrderLine> lines = orderLineDao.listOrder(info.getPkey());
//        List<Integer> goodsPkeys = lines.stream().map(MktOrderLine::getGoods).collect(Collectors.toList());
        List<Integer> goodsPkeys = new ArrayList<>();
        for(MktOrderLine l : lines)
        {
            goodsPkeys.add(l.getGoods().intValue());
        }
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(goodsPkeys);
        
        List<OrderV2Info> list = appOrderV2Expand.supplierOrderInfos(lines, goodsMap, info.getPkey());
        info.setInfos(list);
        if(StringUtils.isNotBlank(info.getFarmer()))
        {
            SysFarmer farmer = sysFarmerDao.get(info.getFarmer());
            if(farmer != null)
                info.setFarmerName(farmer.getName());
        }
        return info;
    }
    
    public Boolean checkWxOrder(Integer ascription)
    {
        String[] split = wxOrder.split(",");
        List<String> ascrList = Arrays.asList(split);
        return ascrList.contains(ascription.toString());
    }
}
