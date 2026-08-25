package cn.tofocus.lejia.domain.market.mall;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.market.*;
import cn.tofocus.lejia.bean.dto.market.*;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.sys.*;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.cache.MemberTjrMap;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.sys.*;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.wx.MktGzhAssociateDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;
import cn.tofocus.lejia.domain.MsdCateringManager;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.app.AppGoodsManager;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import cn.tofocus.lejia.domain.market.*;
import cn.tofocus.lejia.domain.pub.PubMemberManager;
import cn.tofocus.lejia.domain.wanli.WanliManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.LocationUtils;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.TongTongSuoUtil;
import cn.tofocus.lejia.utils.LejiaUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppOrderManager
{
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktPostageConfigDao postageConfigDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Autowired
    private MktOrderCodeDao orderCodeDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private MktRefundDao refundDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktExpressDao expressDao;

    @Autowired
    private MktOrderExpressDao orderExpressDao;

    @Autowired
    private MktOrderExpressRouteDao orderExpressRouteDao;
    
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private MemberPointManager pointManager;
    
    @Autowired
    private MemberCommManager commManager;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private OrderTokenMap orderTokenMap;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;
    
    @Autowired
    private AppGoodsManager appGoodsManager;
    
    @Autowired
    private MktOrderCutDao orderCutDao;
    
    @Autowired
    private MemberTjrMap tjrMap;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private VendorOrderManager vendorOrderManager;
    
    @Autowired
    private SysFarmerStationDao sysFarmerStationDao;
    
    @Resource
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktGzhDao gzhDao;
    
    @Autowired
    private MktGzhAssociateDao gzhAssociateDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private WanliManager wanliManager;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private PubMemberManager pubMemberManager;

    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    @Value("${tofocus.file.baseUrl}")
    private String fileStart;
    
    @Autowired
    private RedisCounter redisCounter;
    
    @Resource
    private SmsConfig smsConfig;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktPayLineDao payLineDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;

    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Value("${zyysc.app.pickup.write.off.url:https://small.xinanshizu.com/writeOffIntegralPresale}")
    private String pickupWriteOffUrl;
    
    @Value("${wei.xin.xiaochengxu.order.ascription:18}")
    private String wxOrder;
    
    @Value("${catering.enabled:false}")
    private boolean cateringEnabled;

    @Value("${catering.ascription:22}")
    private Integer cateringAscription;
    
    @Autowired
    private MsdCateringManager cateringManager;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    
    /*
     * 读取订单列表
     */
    public PageResult<MktAppOrderDTO> listOrder(int page, int pagesize, OrderStatus status)
    {
        SelectPageBuilder<Integer, MktOrder> builder = orderDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("member", MobileSession.memberPkey())
            .notEq("status", OrderStatus.VOID_ORDER)
            .sort("pkey", true);
        if (status != null)
        {
            if (status.equals(OrderStatus.REFUND_APPLICATION_ORDER))
                builder.in("status", OrderStatus.REFUND_APPLICATION_ORDER, OrderStatus.REFUNDED_ORDER);
            else
            {
                if (status.getIndex() == 2)
                    builder.in("status", OrderStatus.SHIPPED_ORDER, 
                        OrderStatus.WAIT_ARRIVAL_ORDER,
                        OrderStatus.WAIT_WRITEOFF_ORDER,
                        OrderStatus.ARRIVED_ORDER);
                else
                    builder.eq("status", status);
            }
        }
        
        List<MktAppOrderDTO> dtoList = new ArrayList<>();
        PageResult<MktOrder> list = builder.exec();
        PageResult<MktAppOrderDTO> result = BeanUtil.beanPageFrom(MktAppOrderDTO.class, list);
        for (MktOrder line : list)
        {
            MktAppOrderDTO dto = loadOrderForList(line.getPkey());
            if (StringUtils.isNotBlank(dto.getPickupCode()) && dto.getPickupCode().length() < 4)
                dto.setPickupCode(null);
            dtoList.add(dto);
            if(PayType.ORDER_WEIXIN.equals(line.getPayType()))
            {
                MktPayLine pl = payLineDao.getOrderNumber(line.getCode().substring(0, 14));
                if(pl != null)
                    dto.setTransactionId(pl.getCode());
                List<MktOrder> listCode = orderDao.listCode(line.getCode().substring(0, 14));
                if(listCode.size() > 1)
                {
                    for(MktOrder o : listCode)
                    {
                        if(OrderStatus.CONFIRM_ORDER.equals(o.getStatus()))
                        {
                            dto.setOpenBusinessView(false);
                        }
                    }
                }
                // 临时处理
                if(checkWxOrder(line.getAscription()))
                    dto.setOpenBusinessView(false);
            }
            else
                dto.setOpenBusinessView(false);
        }
        result.setContent(dtoList);
        return result;
    }
    
    public Boolean checkWxOrder(Integer ascription)
    {
        String[] split = wxOrder.split(",");
        List<String> ascrList = Arrays.asList(split);
        return ascrList.contains(ascription.toString());
    }
    

    
    /*
     * 读取直接购买模拟订单
     */
    public MktAppOrderDTO loadInitOrder(Integer goodsPkey, int num, String tjr, Integer addressPkey)
    {
        MktAppOrderDTO dto = new MktAppOrderDTO();
        dto.setMember(MobileSession.memberPkey());
        dto.setPayType(PayType.ORDER_WEIXIN);
        if (StringUtils.isNotBlank(tjr))
        {
            MktMember tjrMember = memberDao.selectOne().eq("openid1", tjr).exec();
            if (tjrMember != null && tjrMember.getPkey().intValue() != dto.getMember().intValue())
                dto.setTjr(tjrMember.getPkey());
        }
        
        MktGoodsSpace space = goodsSpaceDao.get(goodsPkey);
        MktGoods goods = goodsDao.get(space.getGoods());
        getBuyGoodsNum(goods.getPkey(), num);
        SysFarmer farmer = sysFarmerDao.get(goods.getFarmer());
        SysFarmerConfig farmerconfig = sysFarmerConfigDao.get(farmer.getPkey());
        
        dto.setFarmer(goods.getFarmer());
        dto.setCompany(farmer.getOrg());
        dto.setAddr(loadAddr(dto.getMember(), farmer.getPkey(), addressPkey));
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        DistributionTypeTimeDTO pstime =
            getDistributionTypePsTime(goods.getFarmer(), DistributionType.IMMEDIATELY, addressPkey);
        dto.setPstime(pstime.getImPsTime());
        switch (goods.getMType().getIndex())
        {
            case 0:// 积分
                dto.setOrderType(OrderType.INTEGRAL_ORDER);
                break;
            case 1:// 市场
                dto.setOrderType(OrderType.MARKET_ORDER);
                break;
            case 2:// 会员
                dto.setOrderType(OrderType.MARKET_ORDER);
                break;
            case 3:// 特价
                dto.setOrderType(OrderType.MARKET_ORDER);
                break;
            case 4:// 分享
                dto.setOrderType(OrderType.SHARE_ORDER);
                break;
            case 5:// 砍价
                dto.setOrderType(OrderType.CUT_ORDER);
                break;
            case 6:// 团购
                dto.setOrderType(OrderType.COLLAGE_ORDER);
                break;
            case 7:// 预售
                dto.setOrderType(OrderType.PRESALE_ORDER);
                break;
            case 9:// 礼品券
                dto.setOrderType(OrderType.GIFT_ORDER);
                break;
            default:
                dto.setOrderType(OrderType.MARKET_ORDER);
                break;
        }
        dto.setAmto(space.getPrice().multiply(new BigDecimal(num)));
        dto.setReducePrice(BigDecimal.ZERO);
        // 砍价商品 取原价 等待用户砍价
        if (goods.getMType().getIndex() == 5)
            dto.setAmto(space.getPriceOld().multiply(new BigDecimal(num)));
        else if (goods.getMType().getIndex() == 1 && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
            && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
        {
            BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
            dto.setReducePrice(subtract.multiply(new BigDecimal(num)));
        }
        
        if (goods.getMType().getIndex() == 9)
        {
            dto.setWeight(BigDecimal.ZERO);
            dto.setPostage(BigDecimal.ZERO);
        }
        else
        {
            //            if (goods.getMType().getIndex() == 6) dto.setPickupType(goods.getPickupType());
            
            if (goods.getMType().getIndex() != 0 && farmerconfig.getDistributionConfig() != null
                && Boolean.FALSE.equals(farmerconfig.getDistributionConfig()) && !goods.getIsPostage())
            {
                BigDecimal fee = loadPostageFee(farmerconfig, dto.getAmto().subtract(dto.getReducePrice()));
                dto.setFee(fee);
            }
            dto.setWeight(space.getWeight().multiply(new BigDecimal(num)));
            
            if (goods.getIsPostage())
                dto.setPostage(BigDecimal.ZERO);
            else
            {
                dto.setPostage(loadPostage(dto.getPstime(),
                    dto.getWeight(),
                    farmer.getPkey(),
                    dto.getAmto().subtract(dto.getReducePrice())));
                
            }
            // 新增
            dto.setStartingPrice(farmerconfig.getStartingPrice());
        }
        
        log.info("dto.getPostage: {}", dto.getPostage());
        dto.setPointn(space.getPoint() * num);
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        dto.setCommn(BigDecimal.ZERO);
        if (goods.getMType().equals(MType.SHARE_GOODS)) dto.setCommn(space.getComm());
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        dto.setCardAmt(BigDecimal.ZERO);
        dto.setCutAmt(BigDecimal.ZERO);
        
        List<MktAppGwcDTO> list = new ArrayList<MktAppGwcDTO>();
        List<MktGoods> gdList = new ArrayList<MktGoods>();
        MktAppGwcDTO line = new MktAppGwcDTO();
        line.setMember(dto.getMember());
        line.setGoods(space.getGoods());
        line.setSpace(goodsPkey);
        String photo3 = goods.getPhoto3();
        if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
        {
            List<String> photo1 = goods.getPhoto1();
            if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
        }
        line.setPhoto(photo3);
        line.setSpaceName(space.getSpace());
        line.setGoodsName(goods.getTitle());
        line.setPrice(space.getPrice());
        if (goods.getMType().getIndex() == 5) line.setPrice(space.getPriceOld());
        
        line.setNum(num);
        line.setFarmer(goods.getFarmer());
        line.setCompany(goods.getCompany());
        line.setMType(goods.getMType());
        line.setMTypeName(line.getMType().getName());
        list.add(line);
        gdList.add(goods);
        if (goods.getFarmer().equals(Constant.Operation + MobileSession.appid()))
        {
            dto.setList1(list);
        }
        else
        {
            dto.setList2(list);
            if (goods.getMType().getIndex() == 1)
            {
                dto.setCard(loadCard(dto.getMember(),
                    dto.getFarmer(),
                    gdList,
                    dto.getAmto().add(dto.getPostage()).subtract(dto.getReducePrice())));
                
                if (dto.getCard() != null) dto.setCardAmt(memberCardDao.get(dto.getCard()).getCost());
            }
        }
        
        if (farmerconfig.getDistributionConfig() != null && Boolean.FALSE.equals(farmerconfig.getDistributionConfig())
            && !goods.getIsPostage())
        {
            BigDecimal fee = dto.getFee();
            dto.setAmtall(dto.getAmto().add(fee));
            dto.setAmtn(dto.getAmtall().subtract(dto.getReducePrice()).subtract(dto.getCardAmt()));
            dto.setPickupAmt(dto.getAmtn().subtract(fee));
        }
        else
        {
            dto.setAmtall(dto.getPostage().add(dto.getAmto()));
            dto.setAmtn(dto.getAmtall().subtract(dto.getReducePrice()).subtract(dto.getCardAmt()));
            dto.setPickupAmt(dto.getAmtn().subtract(dto.getPostage()));
        }
        
        Boolean rb = judgePostFree(farmerconfig, dto.getAmto().subtract(dto.getReducePrice()));
        dto.setPostFree(rb);
        log.info("dto.getPostage: {}", dto.toString());
        newChkOrder(dto);
        List<DistributionTypeDTO> rlsit = buildDistributionType(farmer, deliveryTimeConfig);
        dto.setDistype(rlsit);
        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
            dto.setPickupAmt(new BigDecimal("0.01"));
        return dto;
    }
    
    /**
     * 建立配置地址选项目
     * <功能详细描述>
     * @param farmer
     * @return
     */
    private List<DistributionTypeDTO> buildDistributionType(SysFarmer farmer, MktDeliveryTimeConfig deliveryTimeConfig)
    {
        List<DistributionTypeDTO> list = new ArrayList<>();
        SysFarmerConfig config = sysFarmerConfigDao.get(farmer.getPkey());
        
        DistributionTypeDTO t =
            buildDistributionType1(DistributionType.IMMEDIATELY, farmer, config, deliveryTimeConfig);
        list.add(t);
        t = buildDistributionType1(DistributionType.ORDERED, farmer, config, deliveryTimeConfig);
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
    
    private DistributionTypeDTO buildDistributionType1(DistributionType type, SysFarmer farmer, SysFarmerConfig config,
        MktDeliveryTimeConfig deliveryTimeConfig)
    {
        
        DistributionTypeDTO t = new DistributionTypeDTO();
        t.setType(type);
        t.setAddress(config.getAddr());
        t.setYytb(config.getYytb());
        t.setYyte(config.getYyte());
        t.setMobile(farmer.getMobile());
        if (deliveryTimeConfig.getHour() != null && deliveryTimeConfig.getMinute() != null)
        {
            Integer b = deliveryTimeConfig.getHour() * 60 + deliveryTimeConfig.getMinute();
            t.setMinute(b);
        }
        t.setLatitude(config.getLatitude());
        t.setLongitude(config.getLongitude());
        
        return t;
    }
    
    private void newChkOrder(MktAppOrderDTO dto)
    {
        
        if (!(Constant.Operation + MobileSession.appid()).equals(dto.getFarmer()))
        {
            // 营业时间校验
            SysFarmerConfig config = sysFarmerConfigDao.get(dto.getFarmer());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String dd = sdf.format(new Date());
            // 配送范围检验
            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
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
            MktDeliveryTimeConfig deliveryTimeConfig =
                deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
            if (deliveryTimeConfig.getHour() != null && deliveryTimeConfig.getMinute() != null)
            {
                //配送时间增加 延迟时间 21.12.2 z
                Integer m = deliveryTimeConfig.getHour() * 60 + deliveryTimeConfig.getMinute();
                dd = LejiaUtils.getNewTime(dd, String.valueOf(m));
            }
            if (dd.compareTo(config.getYytb()) < 0 || dd.compareTo(config.getYyte()) > 0 || !config.getYStatus())
                throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
            
            SysFarmer farmer = sysFarmerDao.get(dto.getFarmer());
            if (farmer.getIdDel() || !farmer.getEnabled()) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
        }
        // 检验订单金额不可为零  由原来的 订单不可为零 修改为   订单金额是0 或者小于0 的 默认为0.01元
        if (dto.getAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setAmtn(new BigDecimal(0.01));
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        // 积分余额不足
        if (dto.getMyPoints().compareTo(dto.getPointn()) < 0) throw TofocusException.of(LejiaErrCode.NO_P0INTS);
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        // 电子帐户余额不足
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) && dto.getMyCommn().compareTo(dto.getAmtn()) < 0)
            throw TofocusException.of(LejiaErrCode.NO_COMMS);
        // 卡券已失效
        if (dto.getCard() != null && !memberCardDao.get(dto.getCard()).getStatus().equals(CardStatus.UNUSED))
        {
            System.out.println(memberCardDao.get(dto.getCard()).getStatus());
            throw TofocusException.of(LejiaErrCode.CARD_DEL);
        }
        // 库存校验
        if (dto.getList1() != null) for (MktAppGwcDTO line : dto.getList1())
        {
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace());
            MktGoods gd = goodsDao.get(line.getGoods());
            if (space.getKcNum().intValue() < line.getNum().intValue())
                throw TofocusException.of(LejiaErrCode.GOODS_NONUM, gd.getTitle() + "库存不足");
            if (!gd.getEnabled()) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED, gd.getTitle() + "已下架");
        }
        if (dto.getList2() != null) for (MktAppGwcDTO line : dto.getList2())
        {
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace());
            MktGoods gd = goodsDao.get(line.getGoods());
            if (!gd.getFarmer().equals(dto.getFarmer())) throw TofocusException.of(LejiaErrCode.WRONG_FARMER);
            if (space.getKcNum().intValue() < line.getNum().intValue())
                throw TofocusException.of(LejiaErrCode.GOODS_NONUM, gd.getTitle() + "库存不足");
            if (!gd.getEnabled()) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED, gd.getTitle() + "已下架");
        }
        if (dto.getOrderType().getIndex() == 4)
        {
            MktOrderGroup group = orderGroupDao.selectOne()
                .eq("goods", dto.getList2().get(0).getGoods())
                .eq("status", OrderGroupStatus.NOT_GROUPS)
                .exec();
            if (group != null)
            {
                List<String> list = group.getOrderList();
                if (list.size() > 0)
                {
                    List<MktOrder> exec =
                        orderDao.select().in("pkey", list.toArray()).eq("member", dto.getMember()).exec();
                    if (exec != null && exec.size() > 0)
                        throw TofocusException.of(LejiaErrCode.ALREADYHERE_COLLAGEORDER);
                }
            }
            
        }
    }
    
    /*
     * 读取购物车购买模拟订单
     */
    public MktAppOrderDTO loadInitOrder(List<String> gwcList, Integer addressPkey)
    {
        if (gwcList.size() == 0) throw TofocusException.of(LejiaErrCode.ORDER_NULL);
        List<MktAppGwcDTO> list1 = new ArrayList<MktAppGwcDTO>();
        List<MktAppGwcDTO> list2 = new ArrayList<MktAppGwcDTO>();
        List<MktGoods> gdList = new ArrayList<MktGoods>();
        BigDecimal amto1 = BigDecimal.ZERO;
        BigDecimal amto2 = BigDecimal.ZERO;
        BigDecimal weight1 = BigDecimal.ZERO;
        BigDecimal weight2 = BigDecimal.ZERO;
        BigDecimal weight11 = BigDecimal.ZERO;
        BigDecimal weight22 = BigDecimal.ZERO;
        Boolean isPostage1 = true;
        Boolean isPostage2 = true;
        int points = 0;
        BigDecimal reducePrice = BigDecimal.ZERO;
        
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (String pkey : gwcList)
        {
            System.out.println("购物车PKEY：" + pkey);
            MktGwc gwc = gwcDao.get(Integer.parseInt(pkey));
            if (gwc == null) continue;
            getBuyGoodsNum(gwc.getGoods(), gwc.getNum());
            MktAppGwcDTO line = new MktAppGwcDTO();
            MktGoodsSpace space = goodsSpaceDao.get(gwc.getSpace());
            if (space == null)
            {
                gwcDao.remove(gwc);
                throw TofocusException.of(LejiaErrCode.GWC_SPACE_NOTEXIST);
            }
            BeanUtils.copyProperties(gwc, line);
            MktGoods gd = goodsDao.get(gwc.getGoods());
            line.setMType(gd.getMType());
            line.setMTypeName(line.getMType().getName());
            line.setSpaceName(space.getSpace());
            line.setGoodsName(gd.getTitle());
            String photo3 = gd.getPhoto3();
            if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
            {
                List<String> photo1 = gd.getPhoto1();
                if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
            }
            line.setPhoto(photo3);
            line.setPrice(space.getPrice());
            if (gd.getMType().getIndex() == 1 && space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
                && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
            {
                // 普通商品会员价处理
                BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
                reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(line.getNum())));
            }
            if (line.getFarmer().equals(Constant.Operation + MobileSession.appid()))
            {
                list1.add(line);
                amto1 = amto1.add(line.getPrice().multiply(new BigDecimal(line.getNum())));
                weight1 = weight1.add(space.getWeight().multiply(new BigDecimal(line.getNum())));
                if (!gd.getIsPostage())
                {
                    weight11 = weight11.add(space.getWeight().multiply(new BigDecimal(line.getNum())));
                    isPostage1 = false;
                }
                points = points + space.getPoint() * line.getNum();
            }
            else
            {
                if (!line.getFarmer().equals(MobileSession.farmerPkey()))
                    throw TofocusException.of(LejiaErrCode.WRONG_FARMER);
                list2.add(line);
                amto2 = amto2.add(line.getPrice().multiply(new BigDecimal(line.getNum())));
                
                weight2 = weight2.add(space.getWeight().multiply(new BigDecimal(line.getNum())));
                if (!gd.getIsPostage())
                {
                    weight22 = weight22.add(space.getWeight().multiply(new BigDecimal(line.getNum())));
                    isPostage2 = false;
                }
                gdList.add(goodsDao.get(space.getGoods()));
                map.put(gwc.getGoods(), line.getPrice().multiply(new BigDecimal(line.getNum())));
            }
        }
        MktAppOrderDTO dto = new MktAppOrderDTO();
        dto.setReducePrice(reducePrice);
        dto.setMember(MobileSession.memberPkey());
        
        dto.setPayType(PayType.ORDER_WEIXIN);
        String farmerPkey = Constant.Operation + MobileSession.appid();
        if (list2 != null && list2.size() > 0)
        {
            dto.setOrderType(OrderType.MARKET_ORDER);
            farmerPkey = MobileSession.farmerPkey();
        }
        else
            dto.setOrderType(OrderType.INTEGRAL_ORDER);
        SysFarmer farmer = sysFarmerDao.get(farmerPkey);
        
        dto.setFarmer(farmer.getPkey());
        dto.setCompany(farmer.getOrg());
        dto.setPointn(points);
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        dto.setCommn(BigDecimal.ZERO);
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        dto.setAddr(loadAddr(dto.getMember(), farmer.getPkey(), addressPkey));
        dto.setCardAmt(BigDecimal.ZERO);
        dto.setCutAmt(BigDecimal.ZERO);
        dto.setList1(list1);
        dto.setList2(list2);
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        DistributionTypeTimeDTO d = getDistributionTypePsTime(farmerPkey, DistributionType.IMMEDIATELY, addressPkey);
        dto.setPstime(d.getImPsTime());
        
        dto.setWeight(weight1.add(weight2));
        BigDecimal postage1 = BigDecimal.ZERO;
        BigDecimal postage2 = BigDecimal.ZERO;
        BigDecimal postage4 = BigDecimal.ZERO;
        SysFarmerConfig farmerconfig = sysFarmerConfigDao.get(dto.getFarmer());
        
        if (!isPostage1 && weight11.compareTo(BigDecimal.ZERO) >= 0)
        {
            postage1 = loadPostage(dto.getPstime(),
                weight11,
                Constant.Operation + MobileSession.appid(),
                amto1.subtract(dto.getReducePrice()));
        }
        
        if (!isPostage2 && weight22.compareTo(BigDecimal.ZERO) >= 0)
        {
            if (farmerconfig.getDistributionConfig() != null
                && Boolean.FALSE.equals(farmerconfig.getDistributionConfig()))
            {
                postage2 = loadPostageFee(farmerconfig, amto2.subtract(dto.getReducePrice()));
                postage4 =
                    loadPostage(dto.getPstime(), weight22, dto.getFarmer(), amto2.subtract(dto.getReducePrice()));
            }
            else
            {
                postage2 =
                    loadPostage(dto.getPstime(), weight22, dto.getFarmer(), amto2.subtract(dto.getReducePrice()));
            }
        }
        log.info("购物车 postage1: {}, postage2: {}", postage1, postage2);
        BigDecimal amtn = amto2.add(postage2).subtract(dto.getReducePrice());
        if (list2 != null && list2.size() > 0 && dto.getOrderType().getIndex() == 1)
        {
            Integer loadCard = loadCard2(dto.getMember(),
                dto.getFarmer(),
                gdList,
                map,
                amto2.add(postage2).subtract(dto.getReducePrice()),
                postage2);
            log.info("loadCard: {}", loadCard);
            dto.setCard(loadCard);
            if (dto.getCard() != null)
            {
                dto.setCardAmt(memberCardDao.get(dto.getCard()).getCost());
                // 市场订单 加 运费 使用优惠券后价格 小于等于0的 为0.01元  
                amtn = amtn.subtract(dto.getCardAmt());
                log.info("未处理amtn: {}", amtn);
                if (amtn.compareTo(BigDecimal.ZERO) <= 0) amtn = new BigDecimal(0.01);
                log.info("处理amtn: {}", amtn);
            }
            
        }
        
        dto.setAmto(amto1.add(amto2));
        
        if (farmerconfig.getDistributionConfig() != null && Boolean.FALSE.equals(farmerconfig.getDistributionConfig())
            && !isPostage2)
        {
            dto.setPostage(postage1.add(postage4));
            dto.setFee(farmerconfig.getFee());
            BigDecimal b = loadPostageFee(farmerconfig, dto.getAmto().subtract(dto.getReducePrice()));
            if (dto.getCard() != null)
            {
                dto.setAmtn(amtn);
            }
            else
            {
                dto.setAmtn(dto.getAmto().add(b));
            }
            dto.setAmtall(dto.getAmto().subtract(dto.getReducePrice()).add(b));
            dto.setPickupAmt(dto.getAmtn().subtract(b));
        }
        else
        {
            dto.setPostage(postage1.add(postage2));
            dto.setAmtall(dto.getPostage().add(dto.getAmto()).subtract(dto.getReducePrice()));
            dto.setAmtn(amto1.add(postage1).add(amtn));
            dto.setPickupAmt(dto.getAmtn().subtract(dto.getPostage()));
        }
        
        Boolean rb = judgePostFree(farmerconfig, dto.getAmto().subtract(dto.getReducePrice()));
        dto.setPostFree(rb);
        newChkOrder(dto);
        log.info("dto.getPostage: {}", dto.toString());
        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
            dto.setPickupAmt(new BigDecimal("0.01"));
        List<DistributionTypeDTO> rlsit = buildDistributionType(farmer, deliveryTimeConfig);
        dto.setDistype(rlsit);
        return dto;
    }
    
    /*
     * 读取支付订单信息
     */
    
    public Boolean getThirdPartyStatus(int pkey)
    {
        MktOrder order = orderDao.selectOne().eq("pkey", pkey).notEq("status", OrderStatus.VOID_ORDER).exec();
        if (order.getExpressType() == ExpressType.WANLI) wanliManager.orderQueryDetail(order);
        return true;
    }
    
    public MktAppOrderDTO loadInitOrder(int pkey, boolean isMobile)
    {
        log.info("fileStart: {}", fileStart);
        Integer ascription = MobileSession.appid();
        if (ascription == null) ascription = CurrentSession.ascriptionPkey();
        // MktOrder order = orderDao.get(pkey);
        MktOrder order = orderDao.selectOne().eq("pkey", pkey).notEq("status", OrderStatus.VOID_ORDER).exec();
        Boolean flag = false;
        if (order.getOrderType().getIndex() == 6) flag = true;
        MktAppOrderDTO dto = new MktAppOrderDTO();
        BeanUtils.copyProperties(order, dto);
        if (dto.getOrderType().getIndex() != 0) dto.setPointn(0);
        if (dto.getPointn() == null) dto.setPointn(0);
        if ((dto.getDistributionType() == DistributionType.IMMEDIATELY
            || dto.getDistributionType() == DistributionType.ORDERED) && order.getExpressType() == ExpressType.COURIER)
        {
            MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
            if (e != null)
            {
                dto.setExpressStatus(e.getStatus());
            }
        }
        
        MktAppAddrDTO addDto = new MktAppAddrDTO();
//        if ((Constant.Operation + ascription).equals(CurrentSession.marketPkey())) dto.setPstime("");
        MktOrderDesc orderDesc = orderDescDao.get(pkey);
        dto.setLogistics("");
        dto.setKdCode("");
        if (orderDesc != null)
        {
            addDto.setAddrDetail(orderDesc.getAddr());
            addDto.setMobile(orderDesc.getMobile());
            addDto.setName(orderDesc.getName());
            addDto.setEnabled(true);
            addDto.setDistance(orderDesc.getDistance());
            dto.setRemark(orderDesc.getRemark());
            dto.setAddr(addDto);
            String logistics = orderDesc.getLogistics();
            dto.setLogistics(logistics == null ? "" : logistics);
            String kdCode = orderDesc.getKdCode();
            dto.setKdCode(kdCode == null ? "" : kdCode);
            if (StringUtils.isBlank(dto.getPstime()) && (Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
                dto.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
            
            log.warn("[配送距离] loadInitOrder {}米， {}", addDto.getDistance(), addDto.getAddr());
        }
        
        if (order.getCard() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(order.getCard());
            if (memberCard != null)
            {
                dto.setCardCode(memberCard.getCardNumber());
                MktCard mktCard = cardDao.get(memberCard.getCard());
                if (mktCard != null) dto.setCardName(mktCard.getTitle());
            }
            
        }
        SysFarmer sysFarmer = sysFarmerDao.get(order.getFarmer());
        if (order.getDistributionType() != null && order.getDistributionType().equals(DistributionType.PICKUP) 
            && !OrderType.INTEGRAL_PRESALE_ORDER.equals(order.getOrderType()))
        {
            dto.setPickupType(true);
            String pstime = dto.getPstime();
            System.out.println("pstime:" + pstime);
            try
            {
                Date date = DateUtil.formatDateStr(pstime, "yyyy-MM-dd HH:mm");
                long m = 30 * 60 * 1000;
                long time = date.getTime();
                time = m + time;
                String string = DateUtil.formatDate(new Date(time), "~HH:mm");
                dto.setPstime(DateUtil.formatDate(date, "yyyy-MM-dd HH:mm") + string);
            }
            catch (Exception e)
            {
                log.error("pstime转换错误, e: {}",  e);
            }
        }
        
        List<MktAppGwcDTO> list = new ArrayList<>();
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        Boolean flagCut = false;
        if (dto.getOrderType().getIndex() == OrderType.CUT_ORDER.getIndex()) flagCut = true;
        for (MktOrderLine line : lineList)
        {
            MktAppGwcDTO gwcDto = new MktAppGwcDTO();
            BeanUtils.copyProperties(line, gwcDto);
            gwcDto.setSpace(line.getSpace().intValue());
            gwcDto.setGoods(line.getGoods().intValue());
            MktGoods gd = goodsDao.get(line.getGoods().intValue());
            gwcDto.setPrice(line.getPricen());
            gwcDto.setMType(gd.getMType());
            gwcDto.setMTypeName(gwcDto.getMType().getName());
            String photo3 = gd.getPhoto3();
            if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
            {
                List<String> photo1 = gd.getPhoto1();
                if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
            }
            gwcDto.setPhoto(photo3);
            MktGoodsSpace space = goodsSpaceDao.get(gwcDto.getSpace());
            if (space != null)
            {
                gwcDto.setSpaceName(space.getSpace());
            }
            if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
            {
                gwcDto.setVendor(gd.getVendor());
                MktVendor vendor = vendorDao.get(gd.getVendor());
                if (vendor != null)
                {
                    gwcDto.setVerdorName(vendor.getName());
                    if (StringUtils.isNotBlank(vendor.getDisplayName())) gwcDto.setVerdorName(vendor.getDisplayName());
                    gwcDto.setVerdorMobile(vendor.getMobile());
                    gwcDto.setVerdorAddr(vendor.getAddr());
                    gwcDto.setBooth(vendor.getBooth());
                }
            }
            if (flag)
            {
                MktVendor vendor = vendorDao.get(Integer.valueOf(gd.getExtendCon()));
                if (vendor != null)
                {
                    gwcDto.setVendor(vendor.getPkey());
                    gwcDto.setVerdorName(vendor.getName());
                    if (StringUtils.isNotBlank(vendor.getDisplayName())) gwcDto.setVerdorName(vendor.getDisplayName());
                    gwcDto.setVerdorMobile(vendor.getMobile());
                    gwcDto.setVerdorAddr(vendor.getAddr());
                    gwcDto.setBooth(vendor.getBooth());
                }
            }
            if (flagCut) gwcDto.setPrice(order.getAmto());
            list.add(gwcDto);
        }
        if (order.getOrderOir().equals(OrderOir.POINTS_MALL))
            dto.setList1(list);
        else
            dto.setList2(list);
        SysFarmerConfig farmerconfig = sysFarmerConfigDao.get(order.getFarmer());
        if (isMobile && order.getStatus().equals(OrderStatus.UNPAID_ORDER))
        {
            // 如果是移动端
            // 且待支付订单，重新生成订单号
            String payNumber = numberUtils.createOrderNumber();
            log.info("payNumber: {}", payNumber);
            if (order.getOrderOir().equals(OrderOir.POINTS_MALL))
                order.setCode(payNumber + "1");
            else
                order.setCode(payNumber + "2");
            orderDao.update(order);
            dto.setCode(payNumber);
            
        }
        if (order.getStatus().equals(OrderStatus.REFUND_APPLICATION_ORDER)
            || order.getStatus().equals(OrderStatus.REFUNDED_ORDER))
        {
//            MktRefund refund = refundDao.selectOne().eq("orderNum", pkey).exec();
//            dto.setRefund(refund);
            MktRefund refund = orderRefundDao.selectOne().eq("orderPkey", pkey).execDto(MktRefund.class);
            dto.setRefund(refund);
        }
        if (order.getOrderOir().equals(OrderOir.MARKET_MALL))
        {
            MktExpress express = expressDao.selectOne().eq("orderId", pkey).exec();
            if (express != null)
            {
                dto.setArrivedPhoto(express.getPhoto());
                if (-1 == express.getCourier())
                {
                    MktCourier mc = new MktCourier();
                    mc.setName(express.getCourierName());
                    mc.setMobile(express.getCourierMobile());
                    dto.setCourier(mc);
                }
                else
                {
                    dto.setCourier(courierDao.get(express.getCourier()));
                }
            }
        }
        if (dto.getOrderType().getIndex() == 6)
        {
            List<MktGiftOnList> giftList = giftManager.listByOrder(pkey);
            log.info("giftList: {}", JsonUtil.toString(giftList, true));
            String cardCode = "";
            for (MktGiftOnList g : giftList)
            {
                cardCode = cardCode + g.getCardNumber() + ",";
            }
            if (cardCode.length() > 0) cardCode = cardCode.substring(0, cardCode.length() - 1);
            dto.setCardCode(cardCode);
        }
        if (dto.getMember() != null)
        {
            MktMember mktMember = memberDao.get(dto.getMember());
            if (mktMember != null) dto.setMemberName(mktMember.getName());
        }
        
        Boolean rb = judgePostFree(farmerconfig, dto.getAmto().subtract(dto.getReducePrice()));
        dto.setPostFree(rb);
        SysFarmer farmer = sysFarmerDao.get(dto.getFarmer());
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        List<DistributionTypeDTO> rlsit = buildDistributionType(farmer, deliveryTimeConfig);
        dto.setDistype(rlsit);
        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
            dto.setPickupAmt(new BigDecimal("0.01"));
        if (StringUtils.isNotBlank(dto.getPickupCode()) && !DistributionType.PICKUP.equals(order.getDistributionType()))
        {
            dto.setPickupCode(null);
        }
        
        // 如果顺丰发货，查询物流节点（目前默认就一个包裹）
        if (dto.getExpressType() == ExpressType.EXPRESS_SF)
        {
            MktOrderExpressInfo orderExpressInfo =
                orderExpressDao.getNotCanceledByOrderPkey(pkey, MktOrderExpressInfo.class);
            dto.setOrderExpressInfo(orderExpressInfo);
            List<MktOrderExpressRouteInfo> routes =
                orderExpressRouteDao.listByOrderPkey(pkey, MktOrderExpressRouteInfo.class);
            dto.setExpressRoutes(routes);
        }
        dto.setUrl(pickupWriteOffUrl);
        return dto;
    }
    
    public MktAppOrderVendorDTO loadOrderVenodr(Integer pkey)
    {
        MktAppOrderDTO dto = loadInitOrder(pkey, false);
        MktAppOrderVendorDTO res = BeanUtil.beanFrom(MktAppOrderVendorDTO.class, dto);
        if(dto.getOldPostage() != null)
            res.setPostage(dto.getOldPostage());
        List<MktAppGwcDTO> list2 = dto.getList2();
        if (list2.isEmpty()) return res;
        Map<Integer, List<MktAppGwcDTO>> map = new HashMap<>();
        for (MktAppGwcDTO m : list2)
        {
            if (m.getVendor() == null) continue;
            if (!map.containsKey(m.getVendor()))
            {
                List<MktAppGwcDTO> value = new ArrayList<>();
                map.put(m.getVendor(), value);
            }
            map.get(m.getVendor()).add(m);
        }
        List<MktAppGwcVendorDTO> resList2 = new ArrayList<>();
        for (Entry<Integer, List<MktAppGwcDTO>> entry : map.entrySet())
        {
            List<MktAppGwcDTO> value = entry.getValue();
            MktAppGwcDTO mktAppGwcDTO = value.get(0);
            MktAppGwcVendorDTO agvDto = new MktAppGwcVendorDTO();
            agvDto.setVerdorName(mktAppGwcDTO.getVerdorName());
            agvDto.setVerdorMobile(mktAppGwcDTO.getVerdorMobile());
            agvDto.setVerdorAddr(mktAppGwcDTO.getVerdorAddr());
            agvDto.setBooth(mktAppGwcDTO.getBooth());
            agvDto.setList2(value);
            resList2.add(agvDto);
        }
        res.setList2(resList2);
        return res;
    }
    
    /*
     * 读取订单信息
     */
    public MktAppOrderDTO loadOrderForList(int pkey)
    {
        MktOrder order = orderDao.get(pkey);
        MktAppOrderDTO dto = new MktAppOrderDTO();
        BeanUtils.copyProperties(order, dto);
        MktOrderDesc orderDesc = orderDescDao.get(pkey);
        if (orderDesc != null)
        {
            MktAppAddrDTO addDto = new MktAppAddrDTO();
            addDto.setAddrDetail(orderDesc.getAddr());
            addDto.setMobile(orderDesc.getMobile());
            addDto.setName(orderDesc.getName());
            addDto.setEnabled(true);
            dto.setAddr(addDto);
        }
        List<MktAppGwcDTO> list = new ArrayList<MktAppGwcDTO>();
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        Integer goodsPkey = null;
        Boolean flag = false;
        if (dto.getOrderType().getIndex() == OrderType.CUT_ORDER.getIndex())
        {
            flag = true;
        }
        Integer sum = 0;
        for (MktOrderLine line : lineList)
        {
            MktAppGwcDTO gwcDto = new MktAppGwcDTO();
            BeanUtils.copyProperties(line, gwcDto);
            MktGoods gd = goodsDao.get(line.getGoods().intValue());
            if (gd == null) gd = new MktGoods();
            gwcDto.setPrice(line.getPricen());
            gwcDto.setMType(gd.getMType());
            gwcDto.setMTypeName(gwcDto.getMType() == null ? "" : gwcDto.getMType().getName());
            String photo3 = gd.getPhoto3();
            if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
            {
                List<String> photo1 = gd.getPhoto1();
                if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
            }
            gwcDto.setPhoto(photo3);
            if(OrderType.INTEGRAL_JD_ORDER.equals(order.getOrderType()))
            {
                JdGoods goods = jdGoodsDao.get(line.getSpace());
                if (goods != null)
                {
                    List<String> photo1 = goods.getPhoto1();
                    if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
                    gwcDto.setPhoto(photo3);
                    gwcDto.setMType(MType.INTEGRAL_MSD_GOODS);
                }
            }
            if (flag) gwcDto.setPrice(order.getAmto());
            list.add(gwcDto);
            sum += line.getNum();
        }
        if (order.getOrderOir().equals(OrderOir.POINTS_MALL))
            dto.setList1(list);
        else
            dto.setList2(list);
        dto.setOrderNum(sum);
        for (MktOrderLine line : lineList)
        {
            if (dto.getOrderType().getIndex() == 3)
            {
                MktGoodsSpace space = goodsSpaceDao.get(line.getSpace().intValue());
                BigDecimal subtract = space.getPriceOld().subtract(space.getPrice());
                BigDecimal cutAmt = dto.getCutAmt();
                if (cutAmt == null) cutAmt = new BigDecimal(0);
                dto.setRCutAmt(subtract.subtract(cutAmt));
                MktGoods goods = goodsDao.get(line.getGoods().intValue());
                goodsPkey = goods.getPkey();
                dto.setEndTime(goods.getEndDate().getTime());
                if (dto.getList2() != null)
                {
                    for (MktAppGwcDTO g : dto.getList2())
                    {
                        String photo3 = goods.getPhoto3();
                        if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
                        {
                            List<String> photo1 = goods.getPhoto1();
                            if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
                        }
                        g.setPhoto(photo3);
                    }
                }
            }
        }
        if (goodsPkey != null)
        {
            Integer orderCount = orderDao.getOrderCount(goodsPkey);
            dto.setCutSuccessNum(orderCount);
        }
        if (dto.getOrderType().getIndex() == 6)
        {
            List<MktGiftOnList> giftList = giftManager.listByOrder(pkey);
            String cardCode = "";
            StringBuilder cc = new StringBuilder();
            for (MktGiftOnList g : giftList)
            {
                cc.append(g.getCardNumber());
                cc.append(",");
                dto.setGiftStatus(g.getStatus());
            }
            cardCode = cc.toString();
            if (cardCode.length() > 0) cardCode = cardCode.substring(0, cardCode.length() - 1);
            dto.setCardCode(cardCode);
        }
        return dto;
    }
    
    /*
     * 下订单
     */
    //    @Transactional(rollbackFor = Throwable.class)
    //    public MktAppOrderDTO insOrder(MktAppOrderDTO dto)
    //    {
    //        Long k = System.currentTimeMillis();
    //        log.info("----------提交订单----------");
    //        Long ll = orderTokenMap.get("order:" + dto.getMember());
    //        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
    //        {
    //            orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
    //            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
    //        }
    //        
    //        orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
    //        newChkOrder(dto);
    //        if (dto.getPkey() != null) return updOrderOne(dto, dto.getPkey());
    //        if (dto.getPkey1() != null) updOrderOne(dto, dto.getPkey1());
    //        if (dto.getPkey2() != null) updOrderOne(dto, dto.getPkey2());
    //        if (dto.getPkey1() != null || dto.getPkey2() != null) return dto;
    //        String payNumber = numberUtils.createOrderNumber();
    //        MktOrder order1 = null;
    //        MktOrder order2 = null;
    //        String body = "";
    //        
    //        if (dto.getList1() != null && dto.getList1().size() > 0)
    //        {
    //            OrderType type = OrderType.INTEGRAL_ORDER;
    //            if (dto.getOrderType().getIndex() == 6) type = dto.getOrderType();
    //            order1 = insOrderOne(dto,
    //                Constant.Operation + MobileSession.appid(),
    //                Constant.Operation + MobileSession.appid(),
    //                payNumber + "1",
    //                dto.getList1(),
    //                type,
    //                body);
    //            dto.setPkey1(order1.getPkey());
    //        }
    //        if (dto.getList2() != null && dto.getList2().size() > 0)
    //        {
    //            order2 = insOrderOne(dto,
    //                dto.getFarmer(),
    //                dto.getCompany(),
    //                payNumber + "2",
    //                dto.getList2(),
    //                dto.getOrderType(),
    //                body);
    //            dto.setPkey2(order2.getPkey());
    //        }
    //        dto.setCode(payNumber);
    //        log.info("下单dto.getPostage(): {}", dto.getPostage());
    //        log.info("下单order2: {}", JsonUtil.toString(order2, true));
    //        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT))
    //        {
    //            if (order1 != null) payAfterOrder(order1);
    //            if (order2 != null) payAfterOrder(order2);
    //        }
    //        if (dto.getPayType().equals(PayType.ORDER_WEIXIN)) try
    //        {
    //            PayJs payJs =
    //                wxPayManger.topayIvc(MobileSession.billIp(), MobileSession.openid(), payNumber, dto.getAmtn());
    //            dto.setWxPayData(BeanUtil.beanFrom(WxPayData.class, payJs));
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
    //        }
    //        
    //        if (dto.getPayType().equals(PayType.ZXYW_WEIXIN)) try
    //        {
    //            //            WxPayData wxPayData = appZxPayManager.tradeNative(MobileSession.openid(), payNumber, dto.getAmtn(), body);
    //            //            dto.setWxPayData(wxPayData);
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
    //        }
    //        
    //        log.info("----------订单提交成功----------");
    //        log.info("----------订单提交用时: {}----------", System.currentTimeMillis() - k);
    //        return dto;
    //    }
    
    // 只生成订单 不支付
    private MktAppOrderDTO insOrderNotPay(MktAppOrderDTO dto)
    {
        Long ll = orderTokenMap.get("order:" + dto.getMember());
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        if (dto.getList2() != null && dto.getList2().size() > 0)
        {
            Integer judgOrderCut = orderDao.judgOrderCut(dto.getList2().get(0).getGoods(), dto.getMember());
            log.info("judgOrderCut: {}", judgOrderCut);
            if (judgOrderCut != null)
            {
                dto.setPkey2(judgOrderCut);
                return dto;
            }
        }
        orderTokenMap.put("order:" + dto.getMember(), System.currentTimeMillis());
        newChkOrder(dto);
        log.info("insOrderNotPay:dto: {}", dto);
        String payNumber = numberUtils.createOrderNumber();
        MktOrder order1 = null;
        MktOrder order2 = null;
        if (dto.getList1() != null && dto.getList1().size() > 0)
        {
            order1 = insOrderOne(dto,
                Constant.Operation + MobileSession.appid(),
                Constant.Operation + MobileSession.appid(),
                payNumber + "1",
                dto.getList1(),
                OrderType.INTEGRAL_ORDER,
                "");
            dto.setPkey1(order1.getPkey());
        }
        if (dto.getList2() != null && dto.getList2().size() > 0)
        {
            order2 = insOrderOne(dto,
                dto.getFarmer(),
                dto.getCompany(),
                payNumber + "2",
                dto.getList2(),
                dto.getOrderType(),
                "");
            dto.setPkey2(order2.getPkey());
        }
        dto.setCode(payNumber);
        return dto;
    }
    
    private MktOrder insOrderOne(MktAppOrderDTO dto, String farmer, String company, String orderNumber,
        List<MktAppGwcDTO> list, OrderType orderType, String body)
    {
        MktOrder order = new MktOrder();
        Integer ascription = MobileSession.appid();
        order.setCode(orderNumber);
        order.setMember(dto.getMember());
        order.setStatus(OrderStatus.UNPAID_ORDER);
        order.setAscription(ascription);
        if ((Constant.Operation + ascription).equals(farmer))
        {
            order.setOrderOir(OrderOir.POINTS_MALL);
            order.setReducePrice(BigDecimal.ZERO);
        }
        else
        {
            order.setOrderOir(OrderOir.MARKET_MALL);
            order.setReducePrice(dto.getReducePrice() == null ? BigDecimal.ZERO : dto.getReducePrice());
        }
        orderDao.generateID(order);
        order.setOrderType(orderType);
        order.setCgCheck(0);
        order.setPayType(dto.getPayType());
        if (dto.getPstime() != null && dto.getPstime().length() >= 16)
            order.setPstime(dto.getPstime().substring(0, 16));
        else
            order.setPstime(dto.getPstime());
        
        order.setTjr(dto.getTjr());
        order.setFarmer(farmer);
        order.setCompany(company);
        order.setDistributionType(dto.getDistributionType());
        
        BigDecimal amto = BigDecimal.ZERO;
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal postageWeight = BigDecimal.ZERO;
        Boolean isPostage = true;
        List<Integer> gwcIds = new ArrayList<Integer>();
        Boolean isCard = false;
        for (MktAppGwcDTO line : list)
        {
            getBuyGoodsNum(line.getGoods(), line.getNum());
            MktOrderLine orderLine = new MktOrderLine();
            orderLine.setStatus(order.getStatus());
            orderLine.setOrderPkey(order.getPkey());
            orderLine.setGoods((long)line.getGoods());
            orderLine.setSpace((long)line.getSpace());
            orderLine.setGoodsName(line.getGoodsName());
            body = body + line.getGoodsName() + " ";
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace());
            orderLine.setPrice(space.getPriceOld());
            if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
                && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
            {
                orderLine.setPricen(space.getPriceMember());
            }
            else
            {
                orderLine.setPricen(space.getPrice());
            }
            orderLine.setNum(line.getNum());
            orderLine.setFarmer(farmer);
            orderLine.setCompany(company);
            MktGoods goods = goodsDao.get(line.getGoods().intValue());
            orderLine.setGtype(goods.getGtype());
            if (goods.getMType().getIndex() == 1) isCard = true;
            if (goods.getMType().getIndex() == 5)
                amto = amto.add(orderLine.getPrice().multiply(new BigDecimal(orderLine.getNum())));
            else
            {
                amto = amto.add(orderLine.getPricen().multiply(new BigDecimal(orderLine.getNum()))).setScale(2);
            }
            
            weight = weight.add(space.getWeight().multiply(new BigDecimal(orderLine.getNum())));
            if (goods.getIsPostage() != null && !goods.getIsPostage())
            {
                postageWeight = postageWeight.add(space.getWeight().multiply(new BigDecimal(orderLine.getNum())));
                isPostage = false;
            }
            if (line.getPkey() != null) gwcIds.add(line.getPkey());
            orderLine.setAscription(ascription);
            orderLineDao.add(orderLine);
        }
        order.setWeight(weight);
        order.setAmto(amto);
        
        SysFarmerConfig config = sysFarmerConfigDao.get(order.getFarmer());
        BigDecimal postage = BigDecimal.ZERO;
        if (!isPostage)
        {
            if (config == null || config.getPkey().equals((Constant.Operation + ascription))
                || config.getDistributionConfig() == null || Boolean.TRUE.equals(config.getDistributionConfig()))
            {
                postage = loadPostage(order.getPstime(), postageWeight, farmer, amto);
            }
            else
            {
                postage = loadPostageFee(config, amto.subtract(dto.getReducePrice()));
            }
        }
        order.setPostage(postage);
        
        log.info("ins_order_postage: {}", order.getPostage());
        if (dto.getDistributionType() != null && DistributionType.PICKUP.equals(dto.getDistributionType()))
            order.setAmtall(order.getAmto());
        else
        {
            order.setAmtall(order.getAmto().add(order.getPostage()));
        }
        
        if (order.getOrderType().getIndex() == 0 || order.getOrderType().getIndex() == 6)
            order.setPointn(dto.getPointn());
        else
            order.setPointn(0);
        order.setCommn(dto.getCommn());
        if (isCard) order.setCard(dto.getCard());
        order.setCutAmt(BigDecimal.ZERO);
        if (order.getCard() != null)
        {
            MktMemberCard mc = memberCardDao.selectOne().eq("pkey", order.getCard()).eq("invalid", false).exec();
            if (mc == null) throw TofocusException.of(LejiaErrCode.CARD_INVALID);
            order.setCardAmt(mc.getCost());
        }
        else
        {
            order.setCardAmt(BigDecimal.ZERO);
        }
        BigDecimal subtract = order.getAmtall().subtract(order.getCardAmt());
        if (subtract.compareTo(BigDecimal.ZERO) <= 0) subtract = new BigDecimal(0.01);
        order.setAmtn(subtract);
        order = orderDao.add(order);
        MktOrderDesc desc = new MktOrderDesc();
        desc.setPkey(order.getPkey());
        desc.setLatitude(BigDecimal.ZERO);
        desc.setLongitude(BigDecimal.ZERO);
        if (dto.getAddr() != null)
        {
            desc.setAddr(dto.getAddr().getAddrDetail());
            desc.setName(dto.getAddr().getName());
            desc.setMobile(dto.getAddr().getMobile());
            MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
            desc.setLatitude(addrObj.getLatitude());
            desc.setLongitude(addrObj.getLongitude());
            desc.setRemark(dto.getRemark());
            desc.setAscription(ascription);
        }
        orderDescDao.add(desc);
        if (orderType.getIndex() == OrderType.GIFT_ORDER.getIndex() && dto.getAddr() == null)
        {
            desc.setLatitude(BigDecimal.ZERO);
            desc.setLongitude(BigDecimal.ZERO);
            orderDescDao.add(desc);
        }
        if (gwcIds.size() > 0)
        {
            gwcDao.removeAllById(gwcIds);
        }
        return order;
    }
    
    /*
     * 在线支付成功订单处理
     */
    @Transactional(rollbackFor = Throwable.class)
    public void payOrder(String payNumber)
    {
        List<MktOrder> list = orderDao.select().start("code", payNumber).exec();
        List<Integer> keys = new ArrayList<>();
        keys.addAll(CollectionUtil.keyList(list));
        List<MktOrderCode> exec = orderCodeDao.select().start("code", payNumber).notIn("orderPkey", keys).exec();
        for(MktOrder o : list)
        {
            if(o.getOrderType().equals(OrderType.INTEGRAL_JD_ORDER))
            {
                jdAppOrderManager.msdCombination(o);
            }
            else
                payAfterOrder(o);
        }
        for(MktOrderCode oc : exec)
        {
            MktOrder o = orderDao.get(oc.getOrderPkey());
            if(o.getOrderType().equals(OrderType.INTEGRAL_JD_ORDER))
            {
                jdAppOrderManager.msdCombination(o);
            }
            else
                payAfterOrder(o);
        }
        
        
//        MktOrder order1 = orderDao.selectOne().start("code", payNumber + "1").exec();
//        if (order1 == null)
//        {
//            MktOrderCode oc = orderCodeDao.selectOne().start("code", payNumber + "1").exec();
//            if (oc != null) order1 = orderDao.get(oc.getOrderPkey());
//        }
//        if (order1 != null) payAfterOrder(order1);
//        MktOrder order2 = orderDao.selectOne().start("code", payNumber + "2").exec();
//        if (order2 == null)
//        {
//            MktOrderCode oc = orderCodeDao.selectOne().start("code", payNumber + "2").exec();
//            if (oc != null) order2 = orderDao.get(oc.getOrderPkey());
//        }
//        if (order2 != null) payAfterOrder(order2);
    }
    
    public void payAfterOrder(MktOrder order)
    {
        log.info("payAfterOrder-order: {}", JsonUtil.toString(order, true));
        Integer ecardAccountConsume = null;
        if (order.getPayType().equals(PayType.NM_MEMBER))
        {
            // 去心安食足进行交互
            ecardAccountConsume = saasTokenPublicManager.ecardAccountConsume(order.getAmtn());
        }
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
            String pickupcode = LejiaUtils.getNewRandomString(4);
            //方便市场统计自提订单数修改随机核销码为T001格式
            String sequence = order.getFarmer() + DateUtil.formatDate(new Date(), "yyyyMMdd");
            Long index = redisCounter.increment(Constant.DomainId, Constant.App.SERVER, sequence);
            if (index == 1)
            {
                long timeout = (DateUtil.atEndOfToday().getTime() - System.currentTimeMillis()) / 1000;
                redisCounter.expire(Constant.DomainId, Constant.App.SERVER, sequence, timeout);
            }
//            String pickupcode = "T" + StringUtil.right("000" + index, 3);
            order.setPickupCode(pickupcode);
            order.setStatus(OrderStatus.DELIVERED_ORDER);
        }
//        else if (order.getDistributionType().equals(DistributionType.DINE_IN))
//        {
//            order.setStatus(OrderStatus.CONFIRM_ORDER);
//        }
        else
            order.setStatus(OrderStatus.DELIVERED_ORDER);
        order.setPurchaseStatus(PurchaseStatus.AWAIT_PURCHASE);
        if(Boolean.TRUE.equals(order.getIsBox()) && StringUtils.isNotBlank(order.getLockId()) 
            && order.getBoxSd() != null && order.getBoxEd() != null)
        {
            // 发送短信给用户,告知会员包厢密码 
            MktMember member = memberDao.get(order.getMember());
            if(member != null && StringUtils.isNotBlank(member.getMobile()))
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String boxPassword = TongTongSuoUtil.timeLimitPwd(Integer.valueOf(order.getLockId()), order.getCode(), order.getBoxSd(), order.getBoxEd());
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
//                        params.add(farmer.getConfig().getAddr() + "鹿城区东屿路66号 东屿农贸市场3楼 /b https://j.map.baidu.com/5c/-Qzi");
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
            commManager.updComm(order
                .getMember(), order.getAmtn(), false, CommSourceType.COMM_BUY, order.getCode(), order.getAscription());
        }
        if (order.getPayType().equals(PayType.ORDER_MSD))
        {
            // 热力豆更新
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
        if (order.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
        {
            // 组合支付成功电子帐户更新
            commManager.updComm(order.getMember(), order.getOtherAmt(), order.getCode(), order.getAscription());
        }
        if (order.getPayType().equals(PayType.MSD_COMBINATION))
        {
            // 组合支付成功热力豆更新
            if(order != null && order.getOtherAmt() != null && order.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                memberMsdManager.updMsd(order.getMember(), order.getOtherAmt(), order.getCode(), order.getAscription());
        }
        
        SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
        if (order.getOrderOir().equals(OrderOir.POINTS_MALL))
        {
            SourceType st = SourceType.POINTS_BUY;
            // 积分帐户更新
            if (order.getOrderType().equals(OrderType.GIFT_ORDER)) st = SourceType.POINTS_GIFT;
            if (order.getOrderType().equals(OrderType.COUPON_ORDER)) st = SourceType.POINTS_COUPON;
            pointManager.updPoint(order
                .getMember(), order.getPointn(), false, st, order.getCode(), farmer.getName(), farmer.getAscription());
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
        if(order.getCardPostage() != null)
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
                        && !type.equals(DistributionType.PICKUP)) vendorOrderManager.automaticCourier(order.getPkey());
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
                    // TODO 临时活动 东屿 满一百送优惠券
                    if(pubMemberManager.getSaasMarket().equals(order.getFarmer()))
                    {
                        MktMember member = memberDao.get(order.getMember());
                        // 查询金额
                        BigDecimal dayConsumptionAmt = pubMemberManager.getDayConsumptionAmt(member.getMobile());
                        System.out.println("dayConsumptionAmt: " + dayConsumptionAmt);
                        if(dayConsumptionAmt.compareTo(new BigDecimal("100")) >= 0)
                            pubMemberManager.fullGift(member.getMobile());
                        else
                        {
                            BigDecimal xaszAmt = saasTokenPublicManager.getDayConsumptionAmt(member.getMobile(), member.getOpenid1());
                            System.out.println("xaszAmt: " + xaszAmt);
                            if((xaszAmt.add(dayConsumptionAmt)).compareTo(new BigDecimal("100")) >= 0)
                                pubMemberManager.fullGift(member.getMobile());
                        }
                    }
                }
            }).start();
        }
    }
    
    
    
    public void assembleAndSendWx(MktOrder order)
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
    
    /*
     * 订单确认跑批：市场订单当天自动确认，积分商城订单发货后10天自动确认；
     * 自提订单仅确认当天自提的
     */
    public void runDrOrder(int ascription)
    {
//        String date = LocalDate.now().plusDays(-1).toString();
        // 市场订单确认到货
        List<OrderType> otList = new ArrayList<>();
        otList.add(OrderType.MARKET_ORDER);
        otList.add(OrderType.SHARE_ORDER);
        otList.add(OrderType.CUT_ORDER);
        otList.add(OrderType.COLLAGE_ORDER);
        otList.add(OrderType.PRESALE_ORDER);
        List<MktOrder> list1 = orderDao.select()
            .in("orderType", otList)
            .in("status", OrderStatus.SHIPPED_ORDER, 
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER)
            .start("farmer", "zy_mkt_")
            .notEq("farmer", Constant.Operation + ascription)
            .exec();
        for (MktOrder line : list1)
        {
            try
            {
//                if (line.getDistributionType() == DistributionType.PICKUP)
//                {
//                    String pstime = line.getPstime();
//                    if (pstime != null && pstime.length() >= 10 && date.equals(pstime.substring(0, 10)))
//                    {
//                        drOrder(line);
//                    }
//                }
//                else
//                {
//                    drOrder(line);
//                }
                drOrder(line);
            }
            catch (Exception e)
            {
                log.error("[自动确认到货任务] 市场订单（{}）自动确认到货失败，配送类型：{}，配送时间：{}",
                    line.getCode(),
                    line.getDistributionType(),
                    line.getPstime(),
                    e);
            }
        }
        
        Calendar calendar = Calendar.getInstance();
        if(ascription == 13)
            calendar.add(Calendar.DATE, -3);
        else
            calendar.add(Calendar.DATE, -10);
        
        // 商城订单确认到货  优惠券和礼品券
        List<MktOrder> list2 = orderDao.select()
            .in("orderType", OrderType.GIFT_ORDER, OrderType.COUPON_ORDER)
            .in("status", OrderStatus.SHIPPED_ORDER, OrderStatus.ARRIVED_ORDER)
            .eq("farmer", Constant.Operation + ascription)
            .le("createdTime", calendar.getTime())
            .exec();
        for (MktOrder line : list2)
        {
            drOrder(line);
        }
        // 商城订单确认到货 积分商品和预售商品
        List<MktOrder> list3 = orderDao.joinSelect()
            .as("pkey")
        .in("orderType", OrderType.INTEGRAL_ORDER, OrderType.INTEGRAL_PRESALE_ORDER)
        .in("status", OrderStatus.SHIPPED_ORDER, OrderStatus.ARRIVED_ORDER)
        .eq("farmer", Constant.Operation + ascription)
        .join(MktOrderDesc.class, "pkey", "pkey")
            .isNotNull("fhTime")
            .le("fhTime", calendar.getTime())
        .endJoin()
        .exec(MktOrder.class);
        if(!list3.isEmpty())
        {
            List<Integer> keyList = CollectionUtil.keyList(list3);
            List<MktOrder> list = orderDao.select().in("pkey", keyList).exec();
            for (MktOrder line : list)
            {
                drOrder(line);
            }
        }
    }
    
    //礼品券支付成功
    public void drOrderGift(MktOrder order)
    {
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        order.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
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
    
    //优惠券支付成功
    public void drOrderCoupon(MktOrder order)
    {
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        order.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
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
    
    /*
     * 确认到货
     */
    @Transactional(rollbackFor = Throwable.class)
    public void drOrder(MktOrder order)
    {
        //        MktOrder order = orderDao.get(orderPkey);
        Integer orderPkey = order.getPkey();
        if (order.getStatus().getIndex() > 4 || order.getStatus().getIndex() < 1)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS);
        order.setStatus(OrderStatus.CONFIRM_ORDER);
        order.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
        MktOrderDesc desc = orderDescDao.get(orderPkey);
        if (desc != null)
        {
            desc.setDrTime(new Date());
            desc.setEndTime(new Date());
            orderDescDao.update(desc);
        }
        orderDao.update(order);
        // 明细表跟着修改
        List<MktOrderLine> line = orderLineDao.select().in("orderPkey", order.getPkey()).exec();
        for (MktOrderLine ol : line)
        {
            ol.setStatus(order.getStatus());
        }
        orderLineDao.updateAll(line);
        // 商户采购订单确认
        List<MktVendorOrder> listVendorOrder = vendorOrderDao.listOrder(orderPkey);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        for (MktVendorOrder vo : listVendorOrder)
        {
            vo.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
            if (vo.getVendorTime() == null) vo.setVendorTime(cal.getTime());
            if (vo.getFarmerTime() == null) vo.setFarmerTime(cal.getTime());
        }
        vendorOrderDao.updateAll(listVendorOrder);
        
        if (order.getOrderOir().equals(OrderOir.MARKET_MALL))
        {
            MktExpress express = expressDao.selectOne().eq("orderId", orderPkey).exec();
            if (express != null)
            {
                if (express.getQrTime() == null) express.setQrTime(new Date());
                express.setStatus(ExpressStatus.EXPRESS_ARRIVED);
                expressDao.update(express);
            }
        }
        if (order.getOrderType().equals(OrderType.SHARE_ORDER) && order.getTjr() != null) // 分享佣金
            commManager.updComm(order
                .getTjr(), order.getCommn(), true, CommSourceType.COMM_SHARE, order.getCode(), order.getAscription());
        Long tjr = tjrMap.get(order.getMember() + "");
        if (tjr != null)
        {
            // TODO 先注释 后期定义后 再处理
            //            commManager
            //                .updComm(tjr.intValue(), order.getAmtall(), true, CommSourceType.SHARE_NEW, order.getMember() + "");
            tjrMap.remove(order.getMember() + "");
        }
        if(OrderType.INTEGRAL_JD_ORDER.equals(order.getOrderType()))
        {
            try
            {
                JdOrderCorrelation joc = jdOrderCorrelationDao.get(order.getPkey());
                jdVOPOrderManager.confirmReceiveByOrder(joc.getJdCode(), joc.getOrderCode());
            }
            catch (Exception e)
            {
                log.error("京东确认收货报错: {}", e.getMessage());
            }
        }
    }
    
    /*
     * 待支付过时处理（跑批） 每10分钟处理1小时前订单
     */
    @Transactional(rollbackFor = Throwable.class)
    public void runCheckOrder()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
//        calendar.add(Calendar.MINUTE, -10);
        List<MktOrder> list = orderDao.select()
            .eq("status", OrderStatus.UNPAID_ORDER)
            .notEq("orderType", OrderType.CUT_ORDER)
//            .notEq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .le("createdTime", calendar.getTime())
            .exec();
        List<Integer> keys = new ArrayList<>();
        for (MktOrder line : list)
        {
            if(!line.getOrderType().equals(OrderType.INTEGRAL_JD_ORDER))
            {
                keys.add(line.getPkey());
            }
            line.setStatus(OrderStatus.VOID_ORDER);
            orderDao.update(line);
            if(line.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
            {
                commManager.updCommPayFail(line.getMember(), line.getOtherAmt(), line.getAscription());
            }
            if(line.getPayType().equals(PayType.MSD_COMBINATION))
            {
                memberMsdManager.updMsdPayFail(line.getMember(), null, line.getOtherAmt(), line.getAscription());
            }
        }
        if (!keys.isEmpty())
        {
            List<MktOrderLine> exec = orderLineDao.select().in("orderPkey", keys.toArray()).exec();
            List<Integer> spaceKeys = new ArrayList<>();
            exec.forEach(e -> spaceKeys.add(e.getSpace().intValue()));
            for (MktOrderLine ol : exec)
            {
                spaceKcCache.increment(String.valueOf(ol.getSpace()), ol.getNum(), null);
                ol.setStatus(OrderStatus.VOID_ORDER);
                MktGoods goods = goodsDao.get(ol.getGoods().intValue());
                if(goods != null)
                {
                    goods.setXsNum(goods.getXsNum() - ol.getNum());
                }
                goodsDao.update(goods);
            }
            orderLineDao.updateAll(exec);
        }
    }
    
    /*
     * 退款申请
     */
    @Transactional(rollbackFor = Throwable.class)
    public void reFund(MktAppRefundDTO dto)
    {
        MktRefund fund = new MktRefund();
        BeanUtils.copyProperties(dto, fund);
        MktOrder order = orderDao.get(fund.getOrderNum());
        if (!(Constant.Operation + MobileSession.appid()).equals(order.getFarmer()))
            throw TofocusException.of(LejiaErrCode.REFUND_NOPOINT);
        if (order.getStatus().getIndex() >= 4) throw TofocusException.of(LejiaErrCode.REFUND_NOSTATUS);
        MktRefund rundOld = refundDao.selectOne().eq("orderNum", fund.getOrderNum()).exec();
        if (rundOld != null)
        {
            if (!rundOld.getStatus().equals(RefundStatus.REFUND_REFUSE))
                throw TofocusException.of(LejiaErrCode.WRONG_RE);
            rundOld.setStatus(RefundStatus.REFUND_APPLYING);
            rundOld.setReason(dto.getReason());
            rundOld.setPhoto(dto.getPhoto());
            order.setStatus(OrderStatus.REFUND_APPLICATION_ORDER);
            orderDao.update(order);
            refundDao.update(rundOld);
            return;
        }
        fund.setStatus(RefundStatus.REFUND_APPLYING);
        fund.setCode(order.getCode());
        fund.setMember(order.getMember());
        fund.setAmtall(order.getAmtall());
        fund.setAmtre(order.getAmtn());
        fund.setFarmer(order.getFarmer());
        fund.setCompany(order.getCompany());
        fund.setAscription(order.getAscription());
        order.setStatus(OrderStatus.REFUND_APPLICATION_ORDER);
        orderDao.update(order);
        refundDao.add(fund);
    }
    
    /*
     * 读取可用卡券
     */
    public List<MktAppCardDTO> listCard(MktAppOrderDTO dto)
    {
        List<MktAppCardDTO> cards = new ArrayList<>();
        Integer member = dto.getMember();
        String farmer = dto.getFarmer();
        MktMember mktMember = memberDao.get(member);
        Boolean flag = false;
        if (mktMember.getLevel().getIndex() == 1) flag = true;
        
        if (dto.getList2() == null || dto.getList2().isEmpty()) return cards;
        List<MktGoods> goodsList = new ArrayList<>();
        BigDecimal amt = BigDecimal.ZERO;
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (MktAppGwcDTO line : dto.getList2())
        {
            
            goodsList.add(goodsDao.get(line.getGoods()));
            BigDecimal add = BigDecimal.ZERO;
            if (flag)
            {
                add = goodsSpaceDao.get(line.getSpace()).getPriceMember().multiply(new BigDecimal(line.getNum()));
            }
            else
            {
                add = goodsSpaceDao.get(line.getSpace()).getPrice().multiply(new BigDecimal(line.getNum()));
            }
            amt = amt.add(add);
            map.put(line.getGoods(), add);
        }
        BigDecimal postage = dto.getPostage();
        if (postage == null) postage = BigDecimal.ZERO;
        if (dto.getFee() != null) postage = dto.getFee();
        List<MktMemberCard> list = memberCardDao.listMemberCard(member, null, null, null);
        for (MktMemberCard mcard : list)
        {
            System.out.println("校验该卡券可用：" + mcard.getPkey());
            MktCard card = cardDao.selectOne().eq("pkey", mcard.getCard()).eq("invalid", false).exec();
            if (card == null) continue;
            if (amt.add(postage).compareTo(card.getLimitCost()) < 0)
            {// 少于最低消费 过滤
                System.out.println("少于最低消费 过滤");
                continue;
            }
            if (card.getUserFarmer() != null)
            {
                if (!farmer.equals(card.getUserFarmer())
                    && !(Constant.Operation + MobileSession.appid()).equals(card.getUserFarmer()))
                {
                    System.out.println("市场不匹配");
                    continue;
                }
            }
            if (card.getUserType() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getGtype().intValue() == card.getUserType().intValue())
                    {
                        if (map.containsKey(gd.getPkey()))
                        {
                            BigDecimal add = map.get(gd.getPkey()).add(postage);
                            log.info("gtype-add: {}", add);
                            if (add.compareTo(card.getLimitCost()) < 0) continue;
                        }
                        sign = false;
                        break;
                    }
                }
                if (sign)
                {// 分类全对不上 过滤
                    System.out.println("分类不匹配");
                    continue;
                }
            }
            if (card.getUserGoods() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getPkey().intValue() == card.getUserGoods().intValue())
                    {
                        if (map.containsKey(gd.getPkey()))
                        {
                            BigDecimal add = map.get(gd.getPkey()).add(postage);
                            log.info("gtype-add: {}", add);
                            if (add.compareTo(card.getLimitCost()) < 0) continue;
                        }
                        sign = false;
                        break;
                    }
                }
                if (sign)
                {// 商品全对不上 过滤
                    System.out.println("商品不匹配");
                    continue;
                }
            }
            System.out.println("该卡券可用：" + mcard.getPkey());
            MktAppCardDTO cardDto = new MktAppCardDTO();
            cardDto.setPkey(mcard.getPkey());
            cardDto.setEndDate(mcard.getEndDate());
            cardDto.setTitle(card.getTitle());
            cardDto.setCost(card.getCost());
            cardDto.setLimitCost(card.getLimitCost());
            cards.add(cardDto);
        }
        return cards;
    }
    
    /*
     * 读取最优卡券
     */
    public Integer loadCard(Integer member, String farmer, List<MktGoods> goodsList, BigDecimal amt)
    {
        List<MktMemberCard> list = memberCardDao.listMemberCard(member, null, null, null);
        for (MktMemberCard mcard : list)
        {
            MktCard card = cardDao.selectOne()
                .eq("pkey", mcard.getCard())
                .eq("invalid", false)
                //                .eq("enabled", true).eq("idDel", false)
                .exec();
            //			MktCard card = cardDao.get(mcard.getCard());
            if (card == null) continue;
            if (amt.compareTo(card.getLimitCost()) < 0)// 少于最低消费 过滤
                continue;
            if (card.getUserFarmer() != null)
            {
                if (!farmer.equals(card.getUserFarmer())
                    && !(Constant.Operation + MobileSession.appid()).equals(card.getUserFarmer())) continue;
            }
            if (card.getUserType() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getGtype().intValue() == card.getUserType().intValue())
                    {
                        sign = false;
                        break;
                    }
                }
                if (sign)// 分类全对不上 过滤
                    continue;
            }
            if (card.getUserGoods() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getPkey().intValue() == card.getUserGoods().intValue())
                    {
                        sign = false;
                        break;
                    }
                }
                if (sign)// 商品全对不上 过滤
                    continue;
            }
            return mcard.getPkey();
        }
        return null;
    }
    
    public Integer loadCard2(Integer member, String farmer, List<MktGoods> goodsList, Map<Integer, BigDecimal> map,
        BigDecimal amt, BigDecimal postage2)
    {
        List<MktMemberCard> list = memberCardDao.listMemberCard(member, null, null, null);
        for (MktMemberCard mcard : list)
        {
            MktCard card = cardDao.selectOne()
                .eq("pkey", mcard.getCard())
                .eq("invalid", false)
                //                .eq("enabled", true).eq("idDel", false)
                .exec();
            if (card == null) continue;
            if (amt.compareTo(card.getLimitCost()) < 0)// 少于最低消费 过滤
                continue;
            if (card.getUserFarmer() != null)
            {
                if (!farmer.equals(card.getUserFarmer())
                    && !(Constant.Operation + MobileSession.appid()).equals(card.getUserFarmer())) continue;
            }
            if (card.getUserType() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getGtype().intValue() == card.getUserType().intValue())
                    {
                        if (map.containsKey(gd.getPkey()))
                        {
                            BigDecimal add = map.get(gd.getPkey()).add(postage2);
                            log.info("gtype-add: {}", add);
                            if (add.compareTo(card.getLimitCost()) < 0) continue;
                        }
                        sign = false;
                        break;
                    }
                }
                if (sign)// 分类全对不上 过滤
                    continue;
            }
            if (card.getUserGoods() != null)
            {
                boolean sign = true;
                for (MktGoods gd : goodsList)
                {
                    if (gd.getPkey().intValue() == card.getUserGoods().intValue())
                    {
                        if (map.containsKey(gd.getPkey()))
                        {
                            BigDecimal add = map.get(gd.getPkey()).add(postage2);
                            log.info("gtype-add: {}", add);
                            if (add.compareTo(card.getLimitCost()) < 0) continue;
                        }
                        sign = false;
                        break;
                    }
                }
                if (sign)// 商品全对不上 过滤
                    continue;
            }
            return mcard.getPkey();
        }
        return null;
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
        
//        MktActivityCoupon activityCoupon = activityCouponDao.byActivityCoupon(CouponType.CARD, card.getCard());
        if(card.getActivity() != null)
        {
            MktActivity mktActivity = activityDao.get(card.getActivity());
            if(mktActivity != null)
            {
                Integer useNum = mktActivity.getUseNum();
                if(useNum == null)
                    useNum = 0;
                useNum += 1;
                activityDao.updUseNum(mktActivity.getPkey(), useNum);
            }
        }
    }
    
    /*
     * 读取可用地址
     */
    public List<MktAppAddrDTO> listAddr(DistributionType distributionType)
    {
        Integer member = MobileSession.memberPkey();
        String farmer = MobileSession.farmerPkey();
        if (farmer == null) farmer = Constant.Operation + MobileSession.appid();
        SysFarmerConfig config = sysFarmerConfigDao.get(farmer);
        BigDecimal configDistance = config.getDeliveryRange().multiply(new BigDecimal("1000"));
        List<MktAddr> list = addrDao.select()
            .eq("member", member)
            .eq("type", DistributionType.PICKUP.equals(distributionType) ? AddrType.PICKUP : AddrType.DELIVERY)
            .exec();
        List<MktAppAddrDTO> dtoList = new ArrayList<>();
        for (MktAddr addr : list)
        {
            MktAppAddrDTO dto = new MktAppAddrDTO();
            BeanUtils.copyProperties(addr, dto);
            dto.setAddrDetail(addr.getAddr());
            if (StringUtils.isNotBlank(addr.getAddrDetail()))
                dto.setAddrDetail(dto.getAddrDetail() + addr.getAddrDetail());
            if (dto.getAddr() == null) dto.setAddr("");
            if (DistributionType.PICKUP.equals(distributionType))
            {
                dto.setEnabled(true);
            }
            else
            {
                if (addr.getLatitude() == null || addr.getLongitude() == null)
                {
                    dto.setEnabled(false);
                }
                else
                {
                    Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                        config.getLongitude().doubleValue(),
                        addr.getLatitude().doubleValue(),
                        addr.getLongitude().doubleValue());
                    // 距离
                    BigDecimal distance = new BigDecimal(a.toString());
                    // 配置的距离，转为米
                    int i = distance.compareTo(configDistance);
                    dto.setEnabled(i <= 0);
                }
            }
            dtoList.add(dto);
        }
        return dtoList;
    }
    
    /*
     * 读取默认地址
     */
    public MktAppAddrDTO loadAddr(Integer member, String farmer, Integer addressPkey)
    {
        SysFarmerConfig config = sysFarmerConfigDao.get(farmer);
        MktAddr addr = null;
        if (addressPkey != null)
        {
            //指定地址
            addr =
                addrDao.selectOne().eq("member", member).eq("type", AddrType.DELIVERY).eq("pkey", addressPkey).exec();
        }
        else
        {
            addr =
                addrDao.selectOne().eq("member", member).eq("type", AddrType.DELIVERY).eq("defaultAddr", true).exec();
        }
        if (addr == null)
        {
            log.warn("[配送距离] 配送地址不存在");
            return null;
        }
        MktAppAddrDTO dto = new MktAppAddrDTO();
        BeanUtils.copyProperties(addr, dto);
        dto.setAddrDetail(addr.getAddr() + addr.getAddrDetail());
        dto.setEnabled(true);
        if (farmer.equals(Constant.Operation + MobileSession.appid()))// 如果是积分商城，直接返回默认地址
        {
            log.warn("[配送距离] 积分商城，直接返回默认地址");
            return dto;
        }
        
        // 如果是市场商城，判断有效距离
        Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
            config.getLongitude().doubleValue(),
            addr.getLatitude().doubleValue(),
            addr.getLongitude().doubleValue());
        
        // 距离
        BigDecimal distance = new BigDecimal(a.toString());
        // 配置的距离，转为米
        BigDecimal configDistance = config.getDeliveryRange().multiply(new BigDecimal("1000"));
        
        log.warn("[配送距离] 为{}米，从 {} 到 {}", distance, config.getAddr(), dto.getAddr());
        
        if (distance.compareTo(configDistance) <= 0)
        {
            dto.setDistance(distance);
            return dto;
        }
        return null;
    }
    
    /*
     * 计算邮费
     */
    private BigDecimal loadPostage(String psTime, BigDecimal weight, String farmer, BigDecimal amto)
    {
        if (BigDecimal.ZERO.compareTo(weight) == 0) return BigDecimal.ZERO;
        List<MktPostageConfig> list = postageConfigDao.select().eq("farmer", farmer).sort("weight", true).exec();
        log.info("计算邮费传进来的数据:  weight: {}, farmer: {}, amto: {}", weight, farmer, amto);
        SysFarmerConfig farmerconfig = sysFarmerConfigDao.get(farmer);
        if (farmerconfig.getIsFree() != null && farmerconfig.getIsFree() && farmerconfig.getFreeDelivery() != null)
        {
            if (amto.compareTo(farmerconfig.getFreeDelivery()) >= 0) return BigDecimal.ZERO;
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
        if (j < 0) return list.get(list.size() - 1).getPostage();
        if (j > 0) j = j - 1;
        return list.get(j).getPostage();
    }
    
    private BigDecimal loadPostageFee(SysFarmerConfig farmerconfig, BigDecimal amto)
    {
        
        if (farmerconfig.getIsFree() != null && farmerconfig.getIsFree() && farmerconfig.getFreeDelivery() != null)
        {
            if (amto.compareTo(farmerconfig.getFreeDelivery()) >= 0) return BigDecimal.ZERO;
        }
        return farmerconfig.getFee() == null ? BigDecimal.ZERO : farmerconfig.getFee();
    }
    
    private Boolean judgePostFree(SysFarmerConfig farmerconfig, BigDecimal amto)
    {
        
        if (farmerconfig.getIsFree() != null && Boolean.TRUE.equals(farmerconfig.getIsFree())
            && amto.compareTo(farmerconfig.getFreeDelivery()) >= 0) return true;
        
        return false;
    }
    
    private List<String> getListPsTimeV2(List<SysFarmerTime> listTime, Integer phour, Integer pminute,
        DeliveryDate deliveryDate, SysFarmerConfig farmerconfig)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        List<String> listDay = new ArrayList<>();
        if (listTime == null || listTime.isEmpty() || phour == null || pminute == null) return listDay;
        try
        {
            Integer minute = phour * 60 + pminute;
            Date date = new Date();
            String strnow = formatter.format(date);
            String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
            String yytb = "";
            String yyte = "";
            LocalDateTime now = LocalDateTime.now();
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            int nh = now.getHour();
            int nm = now.getMinute();
            Boolean timeBoolean = false;
            if (Boolean.TRUE.equals(getWeek(farmerconfig, dayOfWeek)))
            {
                for (SysFarmerTime ft : listTime)
                {
                    Integer sh = ft.getStartHour();
                    Integer sm = ft.getStartMinute();
                    Integer eh = ft.getEndHour();
                    Integer em = ft.getEndMinute();
                    if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                    {
                        timeBoolean = true;
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0) yytb = newTime;
                        if (newTime.compareTo(yyte) <= 0)
                        {
                            listDay.addAll(LejiaUtils.getListTime(date, yytb, yyte));
                        }
                    }
                    else if ((nh * 60 + nm) <= (eh * 60 + em))
                    {
                        timeBoolean = true;
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        listDay.addAll(LejiaUtils.getListTime(date, yytb, yyte));
                    }
                }
            }
            //            if(Boolean.FALSE.equals(timeBoolean))
            //                throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
            if (deliveryDate == null || DeliveryDate.TOMORROW.equals(deliveryDate))
            {
                DayOfWeek plus = dayOfWeek.plus(1l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        Date tomorrow = DateUtil.atStartOfTomorrow();
                        List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                        listDay.addAll(listYesterDay);
                    }
                }
            }
            else if (DeliveryDate.AFTER_TOMORROW.equals(deliveryDate))
            {
                DayOfWeek plus = dayOfWeek.plus(1l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        Date tomorrow = DateUtil.atStartOfTomorrow();
                        List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                        listDay.addAll(listYesterDay);
                    }
                }
                plus = dayOfWeek.plus(2l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        LocalDate localDate = LocalDate.now().plusDays(2);
                        ZoneId zone = ZoneId.systemDefault();
                        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                        Date afterTomorrow = Date.from(instant);
                        List<String> listAfterDay = LejiaUtils.getNextListTime(afterTomorrow, yytb, yyte);
                        listDay.addAll(listAfterDay);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.TIME_IS_ERROR);
        }
        return listDay;
    }
    
    private List<DistributionTypeTimeOption> getListPsTimeV3(List<SysFarmerTime> listTime, Integer phour,
        Integer pminute, SysFarmerConfig farmerconfig)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        //        List<String> listDay = new ArrayList<>();
        List<DistributionTypeTimeOption> lines = new ArrayList<>();
        DeliveryDate deliveryDate = farmerconfig.getDeliveryDate();
        if (listTime == null || listTime.isEmpty() || phour == null || pminute == null) return lines;
        try
        {
            Integer minute = phour * 60 + pminute;
            Date date = new Date();
            String strnow = formatter.format(date);
            String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
            String yytb = "";
            String yyte = "";
            LocalDateTime now = LocalDateTime.now();
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            int nh = now.getHour();
            int nm = now.getMinute();
            Boolean timeBoolean = false;
            for (SysFarmerTime ft : listTime)
            {
                Integer sh = ft.getStartHour();
                Integer sm = ft.getStartMinute();
                Integer eh = ft.getEndHour();
                Integer em = ft.getEndMinute();
                if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                {
                    timeBoolean = true;
                    if (sh < 10)
                        yytb = "0" + sh + ":";
                    else
                        yytb = sh + ":";
                    if (sm < 10)
                        yytb = yytb + "0" + sm;
                    else
                        yytb = yytb + sm;
                    if (eh < 10)
                        yyte = "0" + eh + ":";
                    else
                        yyte = eh + ":";
                    if (em < 10)
                        yyte = yyte + "0" + em;
                    else
                        yyte = yyte + em;
                    if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0) yytb = newTime;
                    if (newTime.compareTo(yyte) <= 0)
                    {
                        DistributionTypeTimeOption dt = new DistributionTypeTimeOption();
                        dt.setPsOption(LejiaUtils.getListTime(date, yytb, yyte));
                        String day = now.getYear() + "-";
                        if (now.getMonthValue() < 10) day = day + "0";
                        day = day + now.getMonthValue() + "-" + now.getDayOfMonth();
                        dt.setDay(day);
                        lines.add(dt);
                    }
                }
                else if ((nh * 60 + nm) <= (eh * 60 + em))
                {
                    timeBoolean = true;
                    if (sh < 10)
                        yytb = "0" + sh + ":";
                    else
                        yytb = sh + ":";
                    if (sm < 10)
                        yytb = yytb + "0" + sm;
                    else
                        yytb = yytb + sm;
                    if (eh < 10)
                        yyte = "0" + eh + ":";
                    else
                        yyte = eh + ":";
                    if (em < 10)
                        yyte = yyte + "0" + em;
                    else
                        yyte = yyte + em;
                    
                    DistributionTypeTimeOption dt = new DistributionTypeTimeOption();
                    dt.setPsOption(LejiaUtils.getListTime(date, yytb, yyte));
                    String day = now.getYear() + "-";
                    if (now.getMonthValue() < 10) day = day + "0";
                    day = day + now.getMonthValue() + "-" + now.getDayOfMonth();
                    dt.setDay(day);
                    lines.add(dt);
                }
            }
            if (Boolean.FALSE.equals(timeBoolean))
            {
                //                throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
            }
            if (deliveryDate == null || DeliveryDate.TOMORROW.equals(deliveryDate))
            {
                DayOfWeek plus = dayOfWeek.plus(1l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        Date tomorrow = DateUtil.atStartOfTomorrow();
                        List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                        DistributionTypeTimeOption dt = new DistributionTypeTimeOption();
                        dt.setPsOption(listYesterDay);
                        LocalDateTime plusDays = now.plusDays(1);
                        String day = plusDays.getYear() + "-";
                        if (plusDays.getMonthValue() < 10) day = day + "0";
                        day = day + plusDays.getMonthValue() + "-" + plusDays.getDayOfMonth();
                        dt.setDay(day);
                        lines.add(dt);
                    }
                }
            }
            else if (DeliveryDate.AFTER_TOMORROW.equals(deliveryDate))
            {
                DayOfWeek plus = dayOfWeek.plus(1l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        Date tomorrow = DateUtil.atStartOfTomorrow();
                        List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                        DistributionTypeTimeOption dt = new DistributionTypeTimeOption();
                        dt.setPsOption(listYesterDay);
                        LocalDateTime plusDays = now.plusDays(1);
                        String day = plusDays.getYear() + "-";
                        if (plusDays.getMonthValue() < 10) day = day + "0";
                        day = day + plusDays.getMonthValue() + "-" + plusDays.getDayOfMonth();
                        dt.setDay(day);
                        lines.add(dt);
                    }
                }
                plus = dayOfWeek.plus(2l);
                if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                {
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                        LocalDate localDate = LocalDate.now().plusDays(2);
                        ZoneId zone = ZoneId.systemDefault();
                        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                        Date afterTomorrow = Date.from(instant);
                        List<String> listAfterDay = LejiaUtils.getNextListTime(afterTomorrow, yytb, yyte);
                        //                        listDay.addAll(listAfterDay);
                        DistributionTypeTimeOption dt = new DistributionTypeTimeOption();
                        dt.setPsOption(listAfterDay);
                        LocalDateTime plusDays = now.plusDays(2);
                        String day = plusDays.getYear() + "-";
                        if (plusDays.getMonthValue() < 10) day = day + "0";
                        day = day + plusDays.getMonthValue() + "-" + plusDays.getDayOfMonth();
                        dt.setDay(day);
                        lines.add(dt);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.TIME_IS_ERROR);
        }
        return lines;
    }
    
    private List<String> getListPsTime(SysFarmerConfig farmerconfig, MktDeliveryTimeConfig deliveryTimeConfig)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        List<String> listDay = new ArrayList<>();
        try
        {
            List<SysFarmerTime> listTime =
                sysFarmerTimeDao.listTime(farmerconfig.getPkey(), farmerconfig.getAscription());
            if (listTime != null && !listTime.isEmpty() && deliveryTimeConfig.getHour() != null
                && deliveryTimeConfig.getMinute() != null)
            {
                Integer minute = deliveryTimeConfig.getHour() * 60 + deliveryTimeConfig.getMinute();
                Date date = new Date();
                String strnow = formatter.format(date);
                String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                String yytb = "";
                String yyte = "";
                LocalDateTime now = LocalDateTime.now();
                DayOfWeek dayOfWeek = now.getDayOfWeek();
                int nh = now.getHour();
                int nm = now.getMinute();
                Boolean timeBoolean = false;
                for (SysFarmerTime ft : listTime)
                {
                    Integer sh = ft.getStartHour();
                    Integer sm = ft.getStartMinute();
                    Integer eh = ft.getEndHour();
                    Integer em = ft.getEndMinute();
                    if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                    {
                        timeBoolean = true;
                        if (sh < 10)
                            yytb = "0" + sh + ":";
                        else
                            yytb = sh + ":";
                        if (sm < 10)
                            yytb = yytb + "0" + sm;
                        else
                            yytb = yytb + sm;
                        if (eh < 10)
                            yyte = "0" + eh + ":";
                        else
                            yyte = eh + ":";
                        if (em < 10)
                            yyte = yyte + "0" + em;
                        else
                            yyte = yyte + em;
                    }
                    if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
                        yytb = newTime;
                    else if (newTime.compareTo(yyte) >= 0)
                    {
                        listDay.addAll(LejiaUtils.getListTime(date, yytb, yyte));
                    }
                }
                if (Boolean.FALSE.equals(timeBoolean)) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
                DeliveryDate deliveryDate = farmerconfig.getDeliveryDate();
                if (deliveryDate == null || DeliveryDate.TOMORROW.equals(deliveryDate))
                {
                    DayOfWeek plus = dayOfWeek.plus(1l);
                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                    {
                        for (SysFarmerTime ft : listTime)
                        {
                            Integer sh = ft.getStartHour();
                            Integer sm = ft.getStartMinute();
                            Integer eh = ft.getEndHour();
                            Integer em = ft.getEndMinute();
                            if (sh < 10)
                                yytb = "0" + sh + ":";
                            else
                                yytb = sh + ":";
                            if (sm < 10)
                                yytb = yytb + "0" + sm;
                            else
                                yytb = yytb + sm;
                            if (eh < 10)
                                yyte = "0" + eh + ":";
                            else
                                yyte = eh + ":";
                            if (em < 10)
                                yyte = yyte + "0" + em;
                            else
                                yyte = yyte + em;
                            Date tomorrow = DateUtil.atStartOfTomorrow();
                            List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                            listDay.addAll(listYesterDay);
                        }
                    }
                }
                else if (DeliveryDate.AFTER_TOMORROW.equals(deliveryDate))
                {
                    DayOfWeek plus = dayOfWeek.plus(1l);
                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                    {
                        for (SysFarmerTime ft : listTime)
                        {
                            Integer sh = ft.getStartHour();
                            Integer sm = ft.getStartMinute();
                            Integer eh = ft.getEndHour();
                            Integer em = ft.getEndMinute();
                            if (sh < 10)
                                yytb = "0" + sh + ":";
                            else
                                yytb = sh + ":";
                            if (sm < 10)
                                yytb = yytb + "0" + sm;
                            else
                                yytb = yytb + sm;
                            if (eh < 10)
                                yyte = "0" + eh + ":";
                            else
                                yyte = eh + ":";
                            if (em < 10)
                                yyte = yyte + "0" + em;
                            else
                                yyte = yyte + em;
                            Date tomorrow = DateUtil.atStartOfTomorrow();
                            List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
                            listDay.addAll(listYesterDay);
                        }
                    }
                    plus = dayOfWeek.plus(2l);
                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
                    {
                        for (SysFarmerTime ft : listTime)
                        {
                            Integer sh = ft.getStartHour();
                            Integer sm = ft.getStartMinute();
                            Integer eh = ft.getEndHour();
                            Integer em = ft.getEndMinute();
                            if (sh < 10)
                                yytb = "0" + sh + ":";
                            else
                                yytb = sh + ":";
                            if (sm < 10)
                                yytb = yytb + "0" + sm;
                            else
                                yytb = yytb + sm;
                            if (eh < 10)
                                yyte = "0" + eh + ":";
                            else
                                yyte = eh + ":";
                            if (em < 10)
                                yyte = yyte + "0" + em;
                            else
                                yyte = yyte + em;
                            LocalDate localDate = LocalDate.now().plusDays(2);
                            ZoneId zone = ZoneId.systemDefault();
                            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                            Date afterTomorrow = Date.from(instant);
                            List<String> listAfterDay = LejiaUtils.getNextListTime(afterTomorrow, yytb, yyte);
                            listDay.addAll(listAfterDay);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.TIME_IS_ERROR);
        }
        return listDay;
    }
    
    private Boolean getWeek(SysFarmerConfig config, DayOfWeek dayOfWeek)
    {
        Boolean res = false;
        switch (dayOfWeek)
        {
            case MONDAY:
                Boolean monday = config.getMonday();
                if (monday != null) res = monday;
                break;
            case TUESDAY:
                Boolean tuesday = config.getTuesday();
                if (tuesday != null) res = tuesday;
                break;
            case WEDNESDAY:
                Boolean wednesday = config.getWednesday();
                if (wednesday != null) res = wednesday;
                break;
            case THURSDAY:
                Boolean thursday = config.getThursday();
                if (thursday != null) res = thursday;
                break;
            case FRIDAY:
                Boolean friday = config.getFriday();
                if (friday != null) res = friday;
                break;
            case SATURDAY:
                Boolean saturday = config.getSaturday();
                if (saturday != null) res = saturday;
                break;
            case SUNDAY:
                Boolean sunday = config.getSunday();
                if (sunday != null) res = sunday;
                break;
            
            default:
                break;
        }
        return res;
    }
    
//    private List<String> getListPickUpPsTime(SysFarmerStation sysFarmerStation, SysFarmerConfig farmerconfig)
//    {
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        Date time = new Date();
//        List<String> listDay = new ArrayList<>();
//        try
//        {
//            List<SysFarmerTime> listTime =
//                sysFarmerTimeDao.listTime(sysFarmerStation.getMarket(), sysFarmerStation.getAscription());
//            if (listTime != null && !listTime.isEmpty() && sysFarmerStation.getPhour() != null
//                && sysFarmerStation.getPminute() != null)
//            {
//                String yytb = "";
//                String yyte = "";
//                String strnow = formatter.format(time);
//                Integer minute = sysFarmerStation.getPhour() * 60 + sysFarmerStation.getPminute();
//                String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
//                LocalDateTime now = LocalDateTime.now();
//                DayOfWeek dayOfWeek = now.getDayOfWeek();
//                int nh = now.getHour();
//                int nm = now.getMinute();
//                for (SysFarmerTime ft : listTime)
//                {
//                    Integer sh = ft.getStartHour();
//                    Integer sm = ft.getStartMinute();
//                    Integer eh = ft.getEndHour();
//                    Integer em = ft.getEndMinute();
//                    if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
//                    {
//                        if (sh < 10)
//                            yytb = "0" + sh + ":";
//                        else
//                            yytb = sh + ":";
//                        if (sm < 10)
//                            yytb = yytb + "0" + sm;
//                        else
//                            yytb = yytb + sm;
//                        if (eh < 10)
//                            yyte = "0" + eh + ":";
//                        else
//                            yyte = eh + ":";
//                        if (em < 10)
//                            yyte = yyte + "0" + em;
//                        else
//                            yyte = yyte + em;
//                    }
//                    if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
//                        yytb = newTime;
//                    else if (newTime.compareTo(yyte) >= 0)
//                    {
//                        listDay.addAll(LejiaUtils.getListTime(time, yytb, yyte));
//                    }
//                }
//                
//                DeliveryDate deliveryDate = sysFarmerStation.getDeliveryDate();
//                if (DeliveryDate.AFTER_TOMORROW.equals(deliveryDate))
//                {
//                    DayOfWeek plus = dayOfWeek.plus(1l);
//                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
//                    {
//                        for (SysFarmerTime ft : listTime)
//                        {
//                            Integer sh = ft.getStartHour();
//                            Integer sm = ft.getStartMinute();
//                            Integer eh = ft.getEndHour();
//                            Integer em = ft.getEndMinute();
//                            if (sh < 10)
//                                yytb = "0" + sh + ":";
//                            else
//                                yytb = sh + ":";
//                            if (sm < 10)
//                                yytb = yytb + "0" + sm;
//                            else
//                                yytb = yytb + sm;
//                            if (eh < 10)
//                                yyte = "0" + eh + ":";
//                            else
//                                yyte = eh + ":";
//                            if (em < 10)
//                                yyte = yyte + "0" + em;
//                            else
//                                yyte = yyte + em;
//                            Date tomorrow = DateUtil.atStartOfTomorrow();
//                            List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
//                            listDay.addAll(listYesterDay);
//                        }
//                    }
//                    plus = dayOfWeek.plus(2l);
//                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
//                    {
//                        for (SysFarmerTime ft : listTime)
//                        {
//                            Integer sh = ft.getStartHour();
//                            Integer sm = ft.getStartMinute();
//                            Integer eh = ft.getEndHour();
//                            Integer em = ft.getEndMinute();
//                            if (sh < 10)
//                                yytb = "0" + sh + ":";
//                            else
//                                yytb = sh + ":";
//                            if (sm < 10)
//                                yytb = yytb + "0" + sm;
//                            else
//                                yytb = yytb + sm;
//                            if (eh < 10)
//                                yyte = "0" + eh + ":";
//                            else
//                                yyte = eh + ":";
//                            if (em < 10)
//                                yyte = yyte + "0" + em;
//                            else
//                                yyte = yyte + em;
//                            LocalDate localDate = LocalDate.now().plusDays(2);
//                            ZoneId zone = ZoneId.systemDefault();
//                            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
//                            Date afterTomorrow = Date.from(instant);
//                            List<String> listAfterDay = LejiaUtils.getNextListTime(afterTomorrow, yytb, yyte);
//                            listDay.addAll(listAfterDay);
//                        }
//                    }
//                }
//                else
//                {
//                    DayOfWeek plus = dayOfWeek.plus(1l);
//                    if (Boolean.TRUE.equals(getWeek(farmerconfig, plus)))
//                    {
//                        for (SysFarmerTime ft : listTime)
//                        {
//                            Integer sh = ft.getStartHour();
//                            Integer sm = ft.getStartMinute();
//                            Integer eh = ft.getEndHour();
//                            Integer em = ft.getEndMinute();
//                            if (sh < 10)
//                                yytb = "0" + sh + ":";
//                            else
//                                yytb = sh + ":";
//                            if (sm < 10)
//                                yytb = yytb + "0" + sm;
//                            else
//                                yytb = yytb + sm;
//                            if (eh < 10)
//                                yyte = "0" + eh + ":";
//                            else
//                                yyte = eh + ":";
//                            if (em < 10)
//                                yyte = yyte + "0" + em;
//                            else
//                                yyte = yyte + em;
//                            Date tomorrow = DateUtil.atStartOfTomorrow();
//                            List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, yytb, yyte);
//                            listDay.addAll(listYesterDay);
//                        }
//                    }
//                }
//                
//            }
//            
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            throw TofocusException.of(LejiaErrCode.TIME_IS_ERROR);
//        }
//        
//        return listDay;
//    }
    
    private List<String> getListPickUpPsTime(SysFarmerStation sysFarmerStation)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        List<String> listDay = new ArrayList<>();
        try
        {
            
            if (sysFarmerStation.getYytb() != null && sysFarmerStation.getYyte() != null && sysFarmerStation.getPhour() != null
                && sysFarmerStation.getPminute() != null)
            {
                Boolean tomorrowFlag = false;
                
                String yytb = sysFarmerStation.getYytb();
                String yyte = sysFarmerStation.getYyte();
                String tyytb = sysFarmerStation.getYytb();
                String tyyte = sysFarmerStation.getYyte();
                String strnow = formatter.format(now);
                Integer minute = sysFarmerStation.getPhour() * 60 + sysFarmerStation.getPminute();
                String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
                    yytb = newTime;
                else if (newTime.compareTo(yyte) >= 0) tomorrowFlag = true;
                
                DeliveryDate deliveryDate = sysFarmerStation.getDeliveryDate();
                if (deliveryDate == null || DeliveryDate.TODAY.equals(deliveryDate))
                {
                    listDay = LejiaUtils.getListTime(now, yytb, yyte);
                }
                else if (DeliveryDate.AFTER_TOMORROW.equals(deliveryDate))
                {
                    if (Boolean.FALSE.equals(tomorrowFlag)) listDay = LejiaUtils.getListTime(now, yytb, yyte);
                    Date tomorrow = DateUtil.atStartOfTomorrow();
                    List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, tyytb, tyyte);
                    listDay.addAll(listYesterDay);
                    
                    LocalDate localDate = LocalDate.now().plusDays(2);
                    ZoneId zone = ZoneId.systemDefault();
                    Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                    Date afterTomorrow = Date.from(instant);
                    List<String> listAfterDay = LejiaUtils.getNextListTime(afterTomorrow, tyytb, tyyte);
                    listDay.addAll(listAfterDay);
                }
                else
                {
                    if (Boolean.FALSE.equals(tomorrowFlag)) listDay = LejiaUtils.getListTime(now, yytb, yyte);
                    Date tomorrow = DateUtil.atStartOfTomorrow();
                    List<String> listYesterDay = LejiaUtils.getNextListTime(tomorrow, tyytb, tyyte);
                    listDay.addAll(listYesterDay);
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new TofocusException(LejiaErrCode.TIME_IS_ERROR);
        }
        
        return listDay;
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
    
    public AppGoodsCollageDTO getOrderCollage(Integer orderPkey)
    {
        log.info("orderPkey: {}", orderPkey);
        List<MktOrderGroup> exec = orderGroupDao.select().eq("ascription", MobileSession.appid()).exec();
        return assemblyAppGoodsCollageDTO(exec, orderPkey);
    }
    
    public PageResult<AppGoodsCollageDTO> listOrderCollage(int page, int pagesize, OrderGroupStatus status)
    {
        Integer memberPkey = MobileSession.memberPkey();
        Integer ascription = MobileSession.appid();
        log.info("listOrderCollage-memberPkey: {}", memberPkey);
        List<MktOrderGroup> groupExec = orderGroupDao.select().eq("ascription", ascription).eq("status", status).exec();
        List<String> orderList = new ArrayList<>();
        for (MktOrderGroup g : groupExec)
            orderList.addAll(g.getOrderList());
        SelectPageBuilder<Integer, MktOrder> builder = orderDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("member", MobileSession.memberPkey())
            .sort("pkey", true)
            .notIn("status", OrderStatus.UNPAID_ORDER, OrderStatus.VOID_ORDER)
            .eq("orderType", OrderType.COLLAGE_ORDER);
        if (!orderList.isEmpty())
            builder.in("pkey", orderList.toArray());
        else
            builder.isNull("pkey");
        PageResult<MktOrder> exec = builder.exec();
        
        PageResult<AppGoodsCollageDTO> result = BeanUtil.beanPageFrom(AppGoodsCollageDTO.class, exec);
        
        List<AppGoodsCollageDTO> list = new ArrayList<>();
        for (MktOrder order : exec.getContent())
        {
            AppGoodsCollageDTO dto = assemblyAppGoodsCollageDTO(groupExec, order.getPkey());
            if (dto != null) list.add(dto);
        }
        result.setContent(list);
        return result;
    }
    
    private AppGoodsCollageDTO assemblyAppGoodsCollageDTO(List<MktOrderGroup> groupExec, Integer orderPkey)
    {
        MktOrderGroup og = null;
        log.info("groupExec: {}, orderPkey: {}", groupExec, orderPkey);
        for (MktOrderGroup g : groupExec)
        {
            for (String o : g.getOrderList())
            {
                if (orderPkey.intValue() == Integer.valueOf(o).intValue()) og = g;
            }
        }
        if (og == null) return null;
        AppGoodsDetailsDTO appGoods = appGoodsManager.getAppGoods(og.getGoods());
        if (appGoods == null) return null;
        AppGoodsCollageDTO dto = BeanUtil.beanFrom(AppGoodsCollageDTO.class, appGoods);
        String photo3 = dto.getPhoto3();
        if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
        {
            List<String> photo1 = dto.getPhoto1();
            if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
        }
        MktOrder order = orderDao.get(orderPkey);
        SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
        if (farmer != null) dto.setFarmerName(farmer.getName());
        dto.setAmtn(order.getAmtn());
        dto.setWrapperPhoto(photo3);
        dto.setRemainingGroupNum(og.getGroupNum() - og.getBuyNum());
        dto.setStatus(og.getStatus());
        dto.setStatusName(og.getStatus().getName());
        dto.setOrderList(og.getOrderList());
        dto.setBuyNum(og.getBuyNum());
        dto.setGroupNum(og.getGroupNum());
        return dto;
    }
    
    public PageResult<MktAppOrderDTO> getOrderCutList(int page, int pagesize)
    {
        SelectPageBuilder<Integer, MktOrder> builder = orderDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("member", MobileSession.memberPkey())
            .sort("pkey", true)
            .eq("status", OrderStatus.UNPAID_ORDER)
            .eq("orderType", OrderType.CUT_ORDER);
        List<MktAppOrderDTO> dtoList = new ArrayList<>();
        PageResult<MktOrder> list = builder.exec();
        PageResult<MktAppOrderDTO> result = BeanUtil.beanPageFrom(MktAppOrderDTO.class, list);
        for (MktOrder line : list)
        {
            dtoList.add(loadOrderForList(line.getPkey()));
        }
        result.setContent(dtoList);
        return result;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public BigDecimal cutOrder(int orderPkey)
    {
        BigDecimal result = BigDecimal.ZERO;
        Integer memberPkey = MobileSession.memberPkey();
        MktOrder order = orderDao.get(orderPkey);
        List<MktOrderLine> list = orderLineDao.select().eq("orderPkey", orderPkey).exec();
        List<MktOrderCut> exec = orderCutDao.select().eq("memberPkey", memberPkey).eq("orderPkey", orderPkey).exec();
        if (exec.size() > 0) throw TofocusException.of(WsaleErrCode.MEMBER_ALREADY_CUT);
        List<MktOrderCut> orderCutList = orderCutDao.select().eq("orderPkey", orderPkey).exec();
        int size = orderCutList.size();
        BigDecimal lowestPrice = BigDecimal.ZERO;
        for (MktOrderLine line : list)
        {
            lowestPrice = line.getPricen().multiply(new BigDecimal(line.getNum()));
            MktGoods goods = goodsDao.get(line.getGoods().intValue());
            if (!checkCutGoodsTime(goods.getPkey())) throw TofocusException.of(WsaleErrCode.GOOODS_CUT_TIME_OUT);
            @SuppressWarnings("unchecked")
            List<String> conList = JsonUtil.getBean(goods.getExtendCon(), List.class);
            if (conList == null) conList = new ArrayList<>();
            int conSize = conList.size();
            
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace().intValue());
            BigDecimal subtract = space.getPriceOld().subtract(space.getPrice());
            BigDecimal cutAmt = order.getCutAmt();
            if (cutAmt == null) cutAmt = new BigDecimal(0);
            MktOrderCut orderCut = new MktOrderCut();
            orderCut.setMemberPkey(memberPkey);
            orderCut.setOrderPkey(orderPkey);
            orderCut.setEndDate(goods.getEndDate());
            // 已经超过砍价人数 如果还没砍完 这次就把剩下的全部砍完
            log.info("size: {}, conSize: {}", size, conSize);
            if (size >= conSize)
            {
                result = subtract.subtract(cutAmt);
                orderCut.setCutAmt(result);
                order.setCutAmt(subtract);
            }
            else
            {
                String string = conList.get(size);
                String[] split = string.split(",");
                int x = Integer.valueOf(split[0]);
                int y = Integer.valueOf(split[1]);
                int max = Math.max(x, y);
                int min = Math.min(x, y);
                int mid = max - min;// 求差
                // 产生随机数
                int i = (int)(Math.random() * (mid + 1)) + min;
                log.info("i: {}", i);
                BigDecimal randomCut = new BigDecimal(i);
                result = subtract.multiply(randomCut).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                if (result.compareTo(BigDecimal.ZERO) == 0) result = BigDecimal.valueOf(0.01);
                log.info("subtract-result: {}", result);
                if (order.getAmto().subtract(result).compareTo(lowestPrice) < 0)
                    result = order.getAmto().subtract(lowestPrice);
                orderCut.setCutAmt(result);
                order.setCutAmt(cutAmt.add(result));
            }
            orderCutDao.add(orderCut);
        }
        BigDecimal subtract = order.getAmto().subtract(result);
        order.setAmto(subtract);
        BigDecimal subtractAmtall = order.getAmtall().subtract(result);
        if (subtractAmtall.compareTo(BigDecimal.ZERO) <= 0) subtractAmtall = BigDecimal.valueOf(0.01);
        order.setAmtall(subtractAmtall);
        BigDecimal subtractAmtn = order.getAmtn().subtract(result);
        if (subtractAmtn.compareTo(BigDecimal.ZERO) <= 0) subtractAmtn = BigDecimal.valueOf(0.01);
        order.setAmtn(subtractAmtn);
        orderDao.update(order);
        if (result.compareTo(BigDecimal.ZERO) == 0) throw TofocusException.of(WsaleErrCode.GOODS_LOWEST_CUT);
        return result;
    }
    
    public MktAppOrderCutDTO loadCutOrder(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        MktAppOrderCutDTO dto = new MktAppOrderCutDTO();
        BeanUtils.copyProperties(order, dto);
        log.info("MktAppOrderCutDTO: {}", dto);
        dto.setCutSuccessNum(0);
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        Integer goodsPkey = null;
        for (MktOrderLine line : lineList)
        {
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace().intValue());
            BigDecimal subtract = space.getPriceOld().subtract(space.getPrice());
            BigDecimal cutAmt = dto.getCutAmt();
            if (cutAmt == null) cutAmt = new BigDecimal(0);
            dto.setCutAmt(cutAmt);
            dto.setRCutAmt(subtract.subtract(cutAmt));
            MktGoods goods = goodsDao.get(line.getGoods().intValue());
            goodsPkey = goods.getPkey();
            dto.setEndTime(goods.getEndDate().getTime());
            dto.setSpace(space.getPkey());
            dto.setGoodsSpaceName(space.getSpace());
            dto.setGoods(goodsPkey);
            dto.setGoodsName(goods.getTitle());
            String photo3 = goods.getPhoto3();
            if (StringUtils.isBlank(photo3) || StringUtils.isBlank(photo3.replace(fileStart, "")))
            {
                List<String> photo1 = goods.getPhoto1();
                if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
            }
            dto.setPhoto(photo3);
            dto.setPrice(space.getPriceOld());
        }
        if (goodsPkey != null)
        {
            Integer orderCount = orderDao.getOrderCount(goodsPkey);
            dto.setCutSuccessNum(orderCount);
        }
        dto.setIsCut(false);
        dto.setIsMember(false);
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey.intValue() == dto.getMember().intValue()) dto.setIsMember(true);
        List<MktOrderCut> memExec = orderCutDao.select().eq("memberPkey", memberPkey).eq("orderPkey", pkey).exec();
        if (!memExec.isEmpty()) dto.setIsCut(true);
        MktMember member = memberDao.get(order.getMember());
        if (member != null)
        {
            dto.setMemberPhoto(member.getPhoto());
            dto.setMemberName(member.getName());
        }
        dto.setCutMemberList(new ArrayList<>());
        List<MktOrderCut> exec = orderCutDao.select().eq("orderPkey", pkey).exec();
        List<MktAppCutMemberDTO> list = BeanUtil.beanListFrom(MktAppCutMemberDTO.class, exec);
        if (list != null && !list.isEmpty())
        {
            for (MktAppCutMemberDTO d : list)
            {
                MktMember m = memberDao.get(d.getMemberPkey());
                if (m != null)
                {
                    d.setMemberName(m.getName());
                    d.setPhoto(m.getPhoto());
                }
            }
        }
        dto.setCutMemberList(list);
        
        return dto;
    }
    
    public MktAppOrderCutDTO initiateCut(Integer goodsPkey, Integer num, String tjr, Integer addressPkey)
    {
        MktGoodsSpace space = goodsSpaceDao.get(goodsPkey);
        if (!checkCutGoodsTime(space.getGoods())) throw TofocusException.of(WsaleErrCode.GOOODS_CUT_TIME_OUT);
        MktAppOrderDTO dto = loadInitOrder(goodsPkey, num, tjr, addressPkey);
        log.info("dto: {}", dto);
        MktAppOrderCutDTO result = new MktAppOrderCutDTO();
        dto = insOrderNotPay(dto);
        log.info("dto2: {}", dto);
        if (dto.getPkey() != null) result = loadCutOrder(dto.getPkey());
        if (dto.getPkey1() != null) result = loadCutOrder(dto.getPkey1());
        if (dto.getPkey2() != null) result = loadCutOrder(dto.getPkey2());
        return result;
    }
    
    private Boolean checkCutGoodsTime(Integer goodsPkey)
    {
        log.info("goodsPkey: {}", goodsPkey);
        MktGoods exec = goodsDao.selectOne()
            .eq("pkey", goodsPkey)
            .eq("enabled", true)
            .eq("idDel", false)
            .ge("endDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .exec();
        return exec != null;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean isshow(int orderPkey)
    {
        MktOrder order = orderDao.get(orderPkey);
        if (order.getStatus().getIndex() != 0) throw TofocusException.of(WsaleErrCode.ORDER_NOT_DEL);
        order.setStatus(OrderStatus.VOID_ORDER);
        List<MktOrderLine> exec = orderLineDao.select().eq("orderPkey", order.getPkey()).exec();
        for (MktOrderLine ol : exec)
        {
            spaceKcCache.increment(String.valueOf(ol.getSpace()), ol.getNum(), null);
            ol.setStatus(OrderStatus.VOID_ORDER);
            MktGoods goods = goodsDao.get(ol.getGoods().intValue());
            if(goods != null)
            {
                goods.setXsNum(goods.getXsNum() - ol.getNum());
                goodsDao.update(goods);
            }
        }
        orderLineDao.updateAll(exec);
        orderDao.update(order);
        
        if(order.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
        {
            commManager.updCommPayFail(order.getMember(), order.getOtherAmt(), order.getAscription());
        }
        if(order.getPayType().equals(PayType.MSD_COMBINATION))
        {
            memberMsdManager.updMsdPayFail(order.getMember(), null, order.getOtherAmt(), order.getAscription());
        }
        
        return true;
    }
    
    /**
     * 判断今日限购 true 没有超过限制
     * <功能详细描述>
     * @return
     */
    public Boolean getBuyGoodsNum(Integer goodsPkey, int num)
    {
        long l = System.currentTimeMillis();
        MktGoods goods = goodsDao.get(goodsPkey);
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
            sum = orderLineDao.aggregation().eq("goods", goodsPkey).in("orderPkey", pkeys.toArray()).execSum("num");
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
    
    public DistributionTypeDTO getDistributionType(String marketPkey, DistributionType type, Integer addressPkey)
    {
        MktAppAddrDTO addr = loadAddr(MobileSession.memberPkey(), marketPkey, addressPkey);
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(marketPkey, addr);
        
        DistributionTypeDTO dto = new DistributionTypeDTO();
        dto.setType(type);
        
        switch (type)
        {
            case PICKUP:
                SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
                if (station == null) return dto;
                dto.setAddress(station.getAddress());
                dto.setYytb(station.getYytb());
                dto.setYyte(station.getYyte());
                if (station.getPhour() != null && station.getPminute() != null)
                    dto.setMinute(station.getPhour() + station.getPminute());
                else
                    dto.setMinute(0);
                
                break;
            case IMMEDIATELY:
            case ORDERED:
                SysFarmer farmer = sysFarmerDao.get(marketPkey);
                if (farmer == null) return dto;
                SysFarmerConfig config = sysFarmerConfigDao.get(marketPkey);
                dto.setAddress(config.getAddr());
                if (deliveryTimeConfig.getHour() != null && deliveryTimeConfig.getMinute() != null)
                    dto.setMinute(deliveryTimeConfig.getHour() + deliveryTimeConfig.getMinute());
                else
                    dto.setMinute(0);
                
                dto.setYytb(config.getYytb());
                break;
        }
        
        return dto;
    }
    
    public DistributionTypeTimeDTO getDistributionTypePsTime(String marketPkey, DistributionType type,
        Integer addressPkey)
    {
        MktAppAddrDTO addr = loadAddr(MobileSession.memberPkey(), marketPkey, addressPkey);
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(marketPkey, addr);
        
        DistributionTypeTimeDTO dto = new DistributionTypeTimeDTO();
        dto.setType(type);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
        Boolean week = getWeek(sysFarmerConfig, dayOfWeek);
        String strnow = formatter.format(new Date());
        switch (type)
        {
            case PICKUP:
            {
                SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
                if (station != null)
                {
                    List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(marketPkey, station.getAscription());
                    if (listTime != null && !listTime.isEmpty())
                    {
                        List<String> list = getListPsTimeV2(listTime,
                            station.getPhour(),
                            station.getPminute(),
                            station.getDeliveryDate(),
                            sysFarmerConfig);
                        Integer minute = 0;
                        if (station.getPhour() != null) minute = station.getPhour() * 60;
                        if (station.getPminute() != null) minute = minute + station.getPminute();
                        String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                        // 如果开始自提时间在营业时间内，但结束自提时间在营业时间之后，则在最前面插入一条“开始自提时间~结束营业时间”
                        if (week)
                        {
                            String yytb = "";
                            String yyte = "";
                            for (SysFarmerTime ft : listTime)
                            {
                                KeyValue<String, String> kv = formatFarmerTime(ft, now);
                                if (kv != null)
                                {
                                    yytb = kv.getKey();
                                    yyte = kv.getValue();
                                }
                            }
                            String nextHourTime = LejiaUtils.getNextHourTime(newTime);
                            if (nextHourTime != null)
                            {
                                String endOfNextHourTime = LejiaUtils.getNewTime(nextHourTime, String.valueOf(30));
                                if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0
                                    && endOfNextHourTime.compareTo(yyte) > 0)
                                {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    String day = sdf.format(new Date());
                                    list.add(0, day + " " + newTime + "~" + yyte);
                                }
                            }
                        }
                        dto.setPsOption(list);
                    }
                }
                break;
            }
            case IMMEDIATELY:
            case ORDERED:
            {
                List<SysFarmerTime> listTime =
                    sysFarmerTimeDao.listTime(sysFarmerConfig.getPkey(), sysFarmerConfig.getAscription());
                if (listTime != null && !listTime.isEmpty())
                {
                    Integer minute = 0;
                    if (deliveryTimeConfig.getHour() != null) minute = deliveryTimeConfig.getHour() * 60;
                    if (deliveryTimeConfig.getMinute() != null) minute = minute + deliveryTimeConfig.getMinute();
                    String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                    if (week)
                    {
                        String yytb = "";
                        String yyte = "";
                        for (SysFarmerTime ft : listTime)
                        {
                            KeyValue<String, String> kv = formatFarmerTime(ft, now);
                            if (kv != null)
                            {
                                yytb = kv.getKey();
                                yyte = kv.getValue();
                            }
                        }
                        // 20240402 云商城，只要在营业时间内，都可以选择立即送出，改用当前时间去比较
                        //if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
                        if (strnow.compareTo(yytb) >= 0 && strnow.compareTo(yyte) <= 0)
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String day = sdf.format(new Date());
                            dto.setImPsTime(day + " " + newTime);
                        }
                    }
                    List<String> list = getListPsTimeV2(listTime,
                        deliveryTimeConfig.getHour(),
                        deliveryTimeConfig.getMinute(),
                        sysFarmerConfig.getDeliveryDate(),
                        sysFarmerConfig);
                    //                    list = getListPsTime(sysFarmerConfig);\\
                    if (dto.getImPsTime() == null && !list.isEmpty())
                    {
                        String str = list.get(0);
                        LocalDateTime plusDays = now.plusDays(1);
                        String y = str.substring(8, 10);
                        String sub = str.substring(11, 16);
                        if (Integer.valueOf(y).intValue() == now.getDayOfMonth())
                        {
                            sub = "" + sub;
                        }
                        else if (Integer.valueOf(y).intValue() == plusDays.getDayOfMonth())
                        {
                            sub = "明天" + sub;
                        }
                        else
                        {
                            sub = "后天" + sub;
                        }
                        String imPsTime = "接受预定中，最快" + sub + "送达";
                        dto.setPsTime(imPsTime);
                    }
                    else
                    {
                        String psTime = getPsTime(true, null);
                        dto.setPsTime(psTime);
                    }
                    dto.setPsOption(list);
                }
                break;
            }
            default:
                break;
        }
        return dto;
    }
    
    // 获取供应商自提时间
    public DistributionTypeTimeDTO getSupplierPsTime(Integer supplier)
    {
        String marketPkey = Constant.Operation + MobileSession.appid();
//        MktAppAddrDTO addr = loadAddr(MobileSession.memberPkey(),marketPkey, addressPkey);
        
        DistributionTypeTimeDTO dto = new DistributionTypeTimeDTO();
        dto.setType(DistributionType.PICKUP);
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
//        String strnow = formatter.format(new Date());
        
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
        MktSupplier mktSupplier = supplierDao.get(supplier);
        if(mktSupplier == null)
            return dto;
        station.setYytb(mktSupplier.getStartBusinessTime());
        station.setYyte(mktSupplier.getEndBusinessTime());
        List<String> list = getListPickUpPsTime(station);
        dto.setPsOption(list);
        
        return dto;
    }
    
    private KeyValue<String, String> formatFarmerTime(SysFarmerTime ft, LocalDateTime now)
    {
        int nh = now.getHour();
        int nm = now.getMinute();
        Integer sh = ft.getStartHour();
        Integer sm = ft.getStartMinute();
        Integer eh = ft.getEndHour();
        Integer em = ft.getEndMinute();
        if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
        {
            String yytb = "";
            String yyte = "";
            if (sh < 10)
                yytb = "0" + sh + ":";
            else
                yytb = sh + ":";
            if (sm < 10)
                yytb = yytb + "0" + sm;
            else
                yytb = yytb + sm;
            if (eh < 10)
                yyte = "0" + eh + ":";
            else
                yyte = eh + ":";
            if (em < 10)
                yyte = yyte + "0" + em;
            else
                yyte = yyte + em;
            return new KeyValue<>(yytb, yyte);
        }
        return null;
    }
    
    public String getOrderPsTime(SysFarmerConfig config, MktDeliveryTimeConfig deliveryTimeConfig)
    {
        String res = "";
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        if (Boolean.FALSE.equals(config.getYStatus())) return res;
        Boolean week = getWeek(config, dayOfWeek);
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(config.getPkey(), config.getAscription());
        
        if (week)
        {
            int nh = now.getHour();
            int nm = now.getMinute();
            String yytb = "";
            String yyte = "";
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String strnow = formatter.format(new Date());
            Integer minute = 0;
            if (deliveryTimeConfig.getHour() != null) minute = deliveryTimeConfig.getHour() * 60;
            if (deliveryTimeConfig.getMinute() != null) minute = minute + deliveryTimeConfig.getMinute();
            String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
            for (SysFarmerTime ft : listTime)
            {
                Integer sh = ft.getStartHour();
                Integer sm = ft.getStartMinute();
                Integer eh = ft.getEndHour();
                Integer em = ft.getEndMinute();
                if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                {
                    if (sh < 10)
                        yytb = "0" + sh + ":";
                    else
                        yytb = sh + ":";
                    if (sm < 10)
                        yytb = yytb + "0" + sm;
                    else
                        yytb = yytb + sm;
                    if (eh < 10)
                        yyte = "0" + eh + ":";
                    else
                        yyte = eh + ":";
                    if (em < 10)
                        yyte = yyte + "0" + em;
                    else
                        yyte = yyte + em;
                }
            }
            // 20240402 云商城，只要在营业时间内，都可以选择立即送出，改用当前时间去比较
            //if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
            if (strnow.compareTo(yytb) >= 0 && strnow.compareTo(yyte) <= 0)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = sdf.format(new Date());
                res = day + " " + newTime;
                minute = minute + 30;
                String endTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                res = res + "~" + endTime;
            }
        }
        log.warn("[配送距离] 预计配送时间 {}", res);
        return res;
    }
    
    public String getPsTime(Boolean flag, String mk)
    {
        String res = "";
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        String marketPkey = MobileSession.farmerPkey();
        if(StringUtils.isNotBlank(mk))
            marketPkey = mk;
        if(StringUtils.isBlank(marketPkey))
            return "市场已休息，暂不营业";
        SysFarmerConfig config = sysFarmerConfigDao.get(marketPkey);
        if (Boolean.FALSE.equals(config.getYStatus())) return "市场已休息，暂不营业";
        Boolean week = getWeek(config, dayOfWeek);
        
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(config.getPkey(), config.getAscription());
        if (Boolean.FALSE.equals(week))
        {
            if (listTime.isEmpty()) return res;
            int i = 0;
            DayOfWeek plus = dayOfWeek.plus(1);
            while (Boolean.FALSE.equals(week))
            {
                now = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
                week = getWeek(config, plus);
                i++;
                plus = plus.plus(1);
                if (i > 8) week = true;
            }
            SysFarmerTime time = listTime.get(0);
            Integer sh = time.getStartHour();
            Integer sm = time.getStartMinute();
            if (sh < 10)
                res = "0" + sh + ":";
            else
                res = sh + ":";
            if (sm < 10)
                res = res + "0" + sm;
            else
                res = res + sm;
            res = res + "开始配送";
            if (i == 1)
            {
                res = ",明天" + res;
            }
            else if (i == 2)
            {
                res = ",后天" + res;
            }
            else
            {
                String format = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd "));
                res = "," + format + res;
            }
            if (Boolean.TRUE.equals(flag))
            {
                res = "暂未营业" + res;
            }
            else
                res = "接受预定" + res;
        }
        else
        {
            if(listTime.isEmpty())
            {
                System.out.println("config: " + config.getPkey() + "   a: " + config.getAscription());
            }
            res = getTodayImPsTime(listTime, config, flag);
        }
        return res;
    }
    
    public DistributionTypeTimeDTO getDistributionTypePsTimeV2(String marketPkey, DistributionType type,
        Integer addressPkey)
    {
        MktAppAddrDTO addr = loadAddr(MobileSession.memberPkey(), marketPkey, addressPkey);
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(marketPkey, addr);
        
        List<String> list = null;
        DistributionTypeTimeDTO dto = new DistributionTypeTimeDTO();
        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
        List<SysFarmerTime> listTime =
            sysFarmerTimeDao.listTime(sysFarmerConfig.getPkey(), sysFarmerConfig.getAscription());
        dto.setType(type);
        switch (type)
        {
            case PICKUP:
                SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
                if (station == null) return dto;
                list = getListPsTimeV2(listTime,
                    station.getPhour(),
                    station.getPminute(),
                    station.getDeliveryDate(),
                    sysFarmerConfig);
                dto.setPsOption(list);
                break;
            case IMMEDIATELY:
            case ORDERED:
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                if (listTime != null && !listTime.isEmpty())
                {
                    LocalDateTime now = LocalDateTime.now();
                    int nh = now.getHour();
                    int nm = now.getMinute();
                    String yytb = "";
                    String yyte = "";
                    String strnow = formatter.format(new Date());
                    Integer minute = 0;
                    if (deliveryTimeConfig.getHour() != null) minute = deliveryTimeConfig.getHour() * 60;
                    if (deliveryTimeConfig.getMinute() != null) minute = minute + deliveryTimeConfig.getMinute();
                    String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                        {
                            if (sh < 10)
                                yytb = "0" + sh + ":";
                            else
                                yytb = sh + ":";
                            if (sm < 10)
                                yytb = yytb + "0" + sm;
                            else
                                yytb = yytb + sm;
                            if (eh < 10)
                                yyte = "0" + eh + ":";
                            else
                                yyte = eh + ":";
                            if (em < 10)
                                yyte = yyte + "0" + em;
                            else
                                yyte = yyte + em;
                        }
                    }
                    
                    // 20240402 云商城，只要在营业时间内，都可以选择立即送出，改用当前时间去比较
                    //if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
                    if (strnow.compareTo(yytb) >= 0 && strnow.compareTo(yyte) <= 0)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String day = sdf.format(new Date());
                        dto.setImPsTime(day + " " + newTime);
                    }
                    list = getListPsTimeV2(listTime,
                        deliveryTimeConfig.getHour(),
                        deliveryTimeConfig.getMinute(),
                        sysFarmerConfig.getDeliveryDate(),
                        sysFarmerConfig);
                    if (dto.getImPsTime() == null && !list.isEmpty())
                    {
                        String str = list.get(0);
                        LocalDateTime plusDays = now.plusDays(1);
                        String y = str.substring(8, 10);
                        String sub = str.substring(11, 16);
                        if (y.equals(plusDays.getDayOfMonth()))
                            sub = "明天" + sub;
                        else
                        {
                            sub = "后天" + sub;
                        }
                        
                        String imPsTime = "接收预定中，最快" + sub + "送达";
                        dto.setPsTime(imPsTime);
                    }
                    dto.setPsOption(list);
                }
                break;
            default:
                break;
        }
        return dto;
    }
    
    // 今天预约下单时间
    private String getTodayImPsTime(List<SysFarmerTime> listTime, SysFarmerConfig sysFarmerConfig, Boolean flag)
    {
        LocalDateTime now = LocalDateTime.now();
        int nh = now.getHour();
        int nm = now.getMinute();
        int timeInt = nh * 60 + nm;
        String yytb = "";
        //Integer minute = 0;
        //if (sysFarmerConfig.getPhour() != null) minute = sysFarmerConfig.getPhour() * 60;
        //if (sysFarmerConfig.getPminute() != null) minute = minute + sysFarmerConfig.getPminute();
        
        Boolean timeBoolean = false;
        for (SysFarmerTime ft : listTime)
        {
            Integer sh = ft.getStartHour();
            Integer sm = ft.getStartMinute();
            Integer eh = ft.getEndHour();
            Integer em = ft.getEndMinute();
            if (timeInt >= (sh * 60 + sm) && timeInt <= (eh * 60 + em))
            {
                timeBoolean = true;
            }
        }
        String imPsTime = "";
        if (timeBoolean) return imPsTime;
        for (int i = 0; i < listTime.size(); i++)
        {
            SysFarmerTime ft = listTime.get(i);
            Integer sh = ft.getStartHour();
            Integer sm = ft.getStartMinute();
            if (timeInt < (sh * 60 + sm))
            {
                if (sh < 10)
                    yytb = "0" + sh + ":";
                else
                    yytb = sh + ":";
                if (sm < 10)
                    yytb = yytb + "0" + sm;
                else
                    yytb = yytb + sm;
                break;
            }
        }
        if (StringUtils.isBlank(yytb))
        {
            String res = "";
            int i = 0;
            DayOfWeek plus = now.getDayOfWeek().plus(1);
            Boolean week = getWeek(sysFarmerConfig, plus);
            while (Boolean.FALSE.equals(week))
            {
                now = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
                week = getWeek(sysFarmerConfig, plus);
                i++;
                plus = plus.plus(1);
                if (i > 8) week = true;
            }
            SysFarmerTime time = listTime.get(0);
            Integer sh = time.getStartHour();
            Integer sm = time.getStartMinute();
            if (sh < 10)
                res = "0" + sh + ":";
            else
                res = sh + ":";
            if (sm < 10)
                res = res + "0" + sm;
            else
                res = res + sm;
            res = res + "开始配送";
            if (i == 0)
            {
                res = ",明天" + res;
            }
            else if (i == 1)
            {
                res = ",后天" + res;
            }
            else
            {
                String format = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd "));
                res = "," + format + res;
            }
            if (Boolean.TRUE.equals(flag))
            {
                res = "暂未营业" + res;
            }
            else
                res = "接受预定" + res;
            return res;
        }
        return "接受预定，" + yytb + "开始配送";
    }
    
    public DistributionTypeTimeV2DTO getDistributionTypePsTimeV23(String marketPkey, DistributionType type,
        MktDeliveryTimeConfig deliveryTimeConfig)
    {
        DistributionTypeTimeV2DTO dto = new DistributionTypeTimeV2DTO();
        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
        List<SysFarmerTime> listTime =
            sysFarmerTimeDao.listTime(sysFarmerConfig.getPkey(), sysFarmerConfig.getAscription());
        dto.setType(type);
        // 是否在营业时间范围内
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dw = now.getDayOfWeek();
        Boolean week = getWeek(sysFarmerConfig, dw);
        //        if(Boolean.FALSE.equals(week))
        //        {
        //            int i = 0;
        //            while(Boolean.FALSE.equals(week))
        //            {
        //                DayOfWeek plus = dw.plus(1);
        //                now = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        //                week = getWeek(sysFarmerConfig, plus);
        //                i++;
        //                if(i > 8)
        //                    week = true;
        //            }
        //        }
        switch (type)
        {
            case PICKUP:
                SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
                if (station == null) return dto;
                List<DistributionTypeTimeOption> lines =
                    getListPsTimeV3(listTime, station.getPhour(), station.getPminute(), sysFarmerConfig);
                dto.setLines(lines);
                break;
            case IMMEDIATELY:
            case ORDERED:
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                if (listTime != null && !listTime.isEmpty())
                {
                    int nh = now.getHour();
                    int nm = now.getMinute();
                    String yytb = "";
                    String yyte = "";
                    String strnow = formatter.format(new Date());
                    Integer minute = 0;
                    if (deliveryTimeConfig.getHour() != null) minute = deliveryTimeConfig.getHour() * 60;
                    if (deliveryTimeConfig.getMinute() != null) minute = minute + deliveryTimeConfig.getMinute();
                    String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
                    for (SysFarmerTime ft : listTime)
                    {
                        Integer sh = ft.getStartHour();
                        Integer sm = ft.getStartMinute();
                        Integer eh = ft.getEndHour();
                        Integer em = ft.getEndMinute();
                        if ((nh * 60 + nm) >= (sh * 60 + sm) && (nh * 60 + nm) <= (eh * 60 + em))
                        {
                            if (sh < 10)
                                yytb = "0" + sh + ":";
                            else
                                yytb = sh + ":";
                            if (sm < 10)
                                yytb = yytb + "0" + sm;
                            else
                                yytb = yytb + sm;
                            if (eh < 10)
                                yyte = "0" + eh + ":";
                            else
                                yyte = eh + ":";
                            if (em < 10)
                                yyte = yyte + "0" + em;
                            else
                                yyte = yyte + em;
                        }
                    }
                    
                    if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0 && week)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String day = sdf.format(new Date());
                        dto.setImPsTime(day + " " + newTime);
                    }
                    lines = getListPsTimeV3(listTime,
                        deliveryTimeConfig.getHour(),
                        deliveryTimeConfig.getMinute(),
                        sysFarmerConfig);
                    dto.setLines(lines);
                }
                break;
            default:
                break;
            
        }
        return dto;
    }
}
