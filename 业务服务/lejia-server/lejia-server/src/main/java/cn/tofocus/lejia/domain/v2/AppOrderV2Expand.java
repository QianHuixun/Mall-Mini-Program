package cn.tofocus.lejia.domain.v2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.goods.GoodsGiftInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gwc.AmtoWeightTotal;
import cn.tofocus.lejia.bean.dto.v2.order.GoodsCardInfo;
import cn.tofocus.lejia.bean.dto.v2.order.OrderDetailsV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderGwcV2OnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsPresale;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.linshi.MktActivityLinshi;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderCode;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerPickupLocation;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerStation;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.AddrType;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.cache.CardLinshiMap;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.goods.MktGoodsPresaleDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.linshi.MktActivityLinshiDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.dao.market.MktDeliveryTimeConfigDao;
import cn.tofocus.lejia.dao.market.MktDesktopDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktOrderCodeDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderGroupDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktPostageConfigDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerMtypeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerPickupLocationDao;
import cn.tofocus.lejia.dao.sys.SysFarmerStationDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.domain.MsdCateringManager;
import cn.tofocus.lejia.domain.app.AppZxPayManager;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;
import cn.tofocus.lejia.domain.h5.H5OrderManager;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.NsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.LocationUtils;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.wx.PayJs;
import cn.tofocus.lejia.utils.LejiaUtils;
import lombok.extern.slf4j.Slf4j;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Slf4j
@Component
public class AppOrderV2Expand
{
    @Autowired
    public SysFarmerDao sysFarmerDao;
    
    @Autowired
    public SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    public SysFarmerMtypeDao sysFarmerMtypeDao;
    
    @Autowired
    public MktPostageConfigDao postageConfigDao;
    
    @Autowired
    public MktMemberCardDao memberCardDao;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @Autowired
    public MktMemberDao memberDao;
    
    @Autowired
    public MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    public MktGoodsDao goodsDao;
    
    @Autowired
    public MktAddrDao addrDao;
    
    @Autowired
    public MktOrderDao orderDao;
    
    @Autowired
    private MktOrderCodeDao orderCodeDao;
    
    @Autowired
    public MktOrderLineDao orderLineDao;
    
    @Autowired
    public MktGwcDao gwcDao;
    
    @Autowired
    public MktOrderDescDao orderDescDao;
    
    @Autowired
    public MemberPointManager pointManager;
    
    @Autowired
    public MemberCommManager commManager;
    
    @Autowired
    public NumberUtils numberUtils;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    public OrderTokenMap orderTokenMap;
    
    @Autowired
    public WxPayManager wxPayManger;
    
    @Autowired
    public MktOrderGroupDao orderGroupDao;
    
    @Autowired
    public AppZxPayManager appZxPayManager;
    
    @Autowired
    public SysFarmerStationDao sysFarmerStationDao;
    
    @Autowired
    public AppOrderManager appOrderManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private RedisLockTemplate lock;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private NsPayManager nsPayManager;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private MktActivityLinshiDao activityLinshiDao;
    
    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    @Autowired
    private CardLinshiMap cardLinshiMap;
    
    @Autowired
    private CardV2Manager cardV2Manager;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private MktDesktopDao desktopDao;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private H5OrderManager h5OrderManager;
    
    @Autowired
    private SysFarmerPickupLocationDao farmerPickupLocationDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MsdCateringManager cateringManager;
    
    @Value("${catering.enabled:false}")
    private boolean cateringEnabled;

    @Value("${catering.ascription:22}")
    private Integer cateringAscription;
    
    // 添加line和info 
    public void addInfoAndLineGwc(OrderTotalV2Info dto, AmtoWeightTotal awTotal, MktGoods goods, MktGoodsSpace space,
        Map<Integer, OrderV2Info> map, Integer num, Integer gwcPkey, Integer association, String associationName,
        Boolean flagCut)
    {
        OrderV2Info info = null;
        Boolean flag = false;
        if (map.containsKey(goods.getPkey()))
        {
            info = map.get(goods.getPkey());
            OrderGwcV2OnList line = new OrderGwcV2OnList();
            line.setPkey(gwcPkey);
            line.setSpace(space.getPkey());
            line.setSpaceName(space.getSpace());
            if (Boolean.TRUE.equals(flagCut))
                line.setPrice(dto.getAmto());
            else
                line.setPrice(space.getPrice());
            line.setPriceMember(space.getPriceMember());
            line.setNum(num);
            if (StringUtils.isNotBlank(space.getPhoto1()))
                line.setPhoto(space.getPhoto1());
            else
                line.setPhoto(info.getPhoto());
            info.getLines().add(line);
        }
        else
        {
            flag = true;
            info = new OrderV2Info();
            info.setGoods(goods.getPkey().longValue());
            info.setGoodsName(goods.getTitle());
            info.setAssociation(association);
            info.setAssociationName(associationName);
            String photo3 = goods.getPhoto3();
            if (StringUtils.isBlank(photo3))
            {
                List<String> photo1 = goods.getPhoto1();
                if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
            }
            info.setPhoto(photo3);
            info.setMType(goods.getMType());
            info.setSpace(space.getPkey().longValue());
            info.setSpaceName(space.getSpace());
            // 价格取现价 有会员价 在会员优惠里体现
            if (Boolean.TRUE.equals(flagCut))
                info.setPrice(dto.getAmto());
            else
                info.setPrice(space.getPrice());
            info.setPriceMember(space.getPriceMember());
            info.setNum(num);
            OrderGwcV2OnList line = new OrderGwcV2OnList();
            BeanUtils.copyProperties(info, line, "photo");
            line.setPkey(gwcPkey);
            line.setSpace(space.getPkey());
            if (StringUtils.isNotBlank(space.getPhoto1()))
                line.setPhoto(space.getPhoto1());
            else
                line.setPhoto(info.getPhoto());
            info.getLines().add(line);
            map.put(goods.getPkey(), info);
        }
        Integer appid = MobileSession.appid();
        if (goods.getFarmer().equals(Constant.Operation + appid))
        {
            if (dto.getPointInfo() == null)
            {
                List<OrderV2Info> pointInfo = new ArrayList<>();
                pointInfo.add(info);
                dto.setPointInfo(pointInfo);
                dto.setPointNum(1);
            }
            else
            {
                if (flag)
                {
                    dto.getPointInfo().add(info);
                    dto.setPointNum(dto.getPointNum() + 1);
                }
            }
            String pointPhoto = null;
            if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty()) pointPhoto = goods.getPhoto1().get(0);
            if (dto.getPointPhoto() == null)
            {
                List<String> pointPhotos = new ArrayList<>();
                pointPhotos.add(pointPhoto);
                dto.setPointPhoto(pointPhotos);
            }
            else
            {
                if (flag) dto.getPointPhoto().add(pointPhoto);
            }
            awTotal.setPointAmto(awTotal.getPointAmto().add(space.getPrice().multiply(new BigDecimal(num))));
            if (awTotal.getPointWeight() != null)
                awTotal.setPointWeight(awTotal.getPointWeight().add(space.getWeight().multiply(new BigDecimal(num))));
            if (!goods.getIsPostage())
            {
                awTotal.setPointCalculateWeight(
                    awTotal.getPointCalculateWeight().add(space.getWeight().multiply(new BigDecimal(num))));
            }
            awTotal.setPoints(awTotal.getPoints() + (space.getPoint() * num));
        }
        else
        {
            if (dto.getFarmerInfo() == null)
            {
                List<OrderV2Info> farmerInfo = new ArrayList<>();
                farmerInfo.add(info);
                dto.setFarmerInfo(farmerInfo);
                dto.setFarmerNum(1);
            }
            else
            {
                if (flag)
                {
                    dto.getFarmerInfo().add(info);
                    dto.setFarmerNum(dto.getFarmerNum() + 1);
                }
            }
            String farmerPhoto = null;
            if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty()) farmerPhoto = goods.getPhoto1().get(0);
            if (dto.getFarmerPhoto() == null)
            {
                List<String> farmerPhotos = new ArrayList<>();
                farmerPhotos.add(farmerPhoto);
                dto.setFarmerPhoto(farmerPhotos);
            }
            else
            {
                if (flag) dto.getFarmerPhoto().add(farmerPhoto);
            }
            if (Boolean.TRUE.equals(flagCut))
                awTotal.setFarmerAmto(dto.getAmto());
            else
                awTotal.setFarmerAmto(awTotal.getFarmerAmto().add(space.getPrice().multiply(new BigDecimal(num))));
            
            awTotal.setFarmerWeight(awTotal.getFarmerWeight().add(space.getWeight().multiply(new BigDecimal(num))));
            if (!goods.getIsPostage())
            {
                awTotal.setFarmerCalculateWeight(
                    awTotal.getFarmerCalculateWeight().add(space.getWeight().multiply(new BigDecimal(num))));
            }
            awTotal.setPoints(awTotal.getPoints() + (space.getPoint() * num));
        }
        
    }
    
    // 读取订单详情  添加line和info 
    public void addInfoAndLineLoadOrder(OrderDetailsV2Info dto, MktOrderLine orderLine, 
         Map<Integer, BigDecimal> refundLineMap, Map<Integer, Integer> refundLinePointMap)
    {
        OrderV2Info info = null;
        Map<Long, OrderV2Info> map = new HashMap<>();
        if (map.containsKey(orderLine.getGoods()))
        {
            info = map.get(orderLine.getGoods());
            OrderGwcV2OnList line = BeanUtil.beanFrom(OrderGwcV2OnList.class, orderLine);
            line.setPkey(orderLine.getPkey());
            line.setPrice(orderLine.getPricen());
            line.setCouponPrice(orderLine.getCouponPrice());
            line.setCouponAmt(orderLine.getCouponAmt());
            if (line.getCouponPrice() == null)
            {
                BigDecimal num = new BigDecimal(line.getNum());
                if (line.getCouponAmt() == null)
                {
                    line.setCouponPrice(line.getPrice());
                    line.setCouponAmt(line.getCouponPrice().multiply(num));
                }
                else
                {
                    line.setCouponPrice(line.getCouponAmt().divide(num, 2, RoundingMode.HALF_UP));
                }
            }
            //            line.setSpaceName(space.getSpace());
            //            if (StringUtils.isNotBlank(space.getPhoto1()))
            //                line.setPhoto(space.getPhoto1());
            //            else
            //                line.setPhoto(info.getPhoto());
            if (refundLineMap.containsKey(orderLine.getPkey()))
            {
                line.setRefundAmt(refundLineMap.get(orderLine.getPkey()));
            }
            if (refundLinePointMap.containsKey(orderLine.getPkey()))
            {
                line.setRefundPoint(refundLinePointMap.get(orderLine.getPkey()));
            }
            line.setRefundNum(orderLine.getRefundNum());
            if (line.getRefundAmt() != null && line.getCouponAmt().compareTo(line.getRefundAmt()) == 0)
                line.setRefundNum(orderLine.getNum());
            info.getLines().add(line);
        }
        else
        {
            info = new OrderV2Info();
            info.setGoods(orderLine.getGoods());
            info.setGoodsName(orderLine.getGoodsName());
            Long mapKey = null;
            if(OrderType.INTEGRAL_JD_ORDER.equals(dto.getOrderType()))
            {
                JdGoods goods = jdGoodsDao.get(orderLine.getSpace());
                if (goods != null)
                {
                    mapKey = goods.getPkey();
                    String photo3 = "";
                    List<String> photo1 = goods.getPhoto1();
                    if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
                    info.setPhoto(photo3);
                    info.setMType(MType.INTEGRAL_MSD_GOODS);
                    info.setEnabled(goods.getEnabled());
                }
            }
            else
            {
                MktGoods goods = goodsDao.get(orderLine.getGoods().intValue());
                if (goods != null)
                {
                    mapKey = goods.getPkey().longValue();
                    String photo3 = goods.getPhoto3();
                    if (StringUtils.isBlank(photo3))
                    {
                        List<String> photo1 = goods.getPhoto1();
                        if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
                    }
                    info.setPhoto(photo3);
                    info.setMType(goods.getMType());
                    info.setEnabled(goods.getEnabled());
                }
            }
            info.setSpace(orderLine.getSpace());
            info.setSpaceName(orderLine.getSpaceName());
            info.setPrice(orderLine.getPricen());
            info.setCouponPrice(orderLine.getCouponPrice());
            info.setCouponAmt(orderLine.getCouponAmt());
            info.setNum(orderLine.getNum());
            if(orderLine.getPoint() != null)
                info.setPoint(orderLine.getPoint() * orderLine.getNum());
            info.setAssociation(orderLine.getAssociation());
            info.setAssociationName(orderLine.getAssociationName());
            if (info.getCouponPrice() == null)
            {
                BigDecimal num = new BigDecimal(info.getNum());
                if (info.getCouponAmt() == null)
                {
                    info.setCouponPrice(info.getPrice());
                    info.setCouponAmt(info.getCouponPrice().multiply(num));
                }
                else
                {
                    info.setCouponPrice(info.getCouponAmt().divide(num, 2, RoundingMode.HALF_UP));
                }
            }
            OrderGwcV2OnList line = new OrderGwcV2OnList();
            BeanUtils.copyProperties(info, line, "photo");
            //            if (space != null)
            //            {
            //                info.setSpaceName(space.getSpace());
            //                line.setSpaceName(space.getSpace());
            //                if (StringUtils.isNotBlank(space.getPhoto1()))
            //                    line.setPhoto(space.getPhoto1());
            //                else
            //                    line.setPhoto(info.getPhoto());
            //            }
            
            line.setPkey(orderLine.getPkey());
            if (refundLineMap.containsKey(orderLine.getPkey()))
                line.setRefundAmt(refundLineMap.get(orderLine.getPkey()));
            if (refundLinePointMap.containsKey(orderLine.getPkey()))
                line.setRefundPoint(refundLinePointMap.get(orderLine.getPkey()));
            line.setRefundNum(orderLine.getRefundNum());
            if (line.getRefundAmt() != null && line.getCouponAmt().compareTo(line.getRefundAmt()) == 0)
                line.setRefundNum(orderLine.getNum());
            info.getLines().add(line);
            map.put(mapKey, info);
            if (dto.getInfos() == null)
            {
                List<OrderV2Info> infos = new ArrayList<>();
                infos.add(info);
                dto.setInfos(infos);
            }
            else
            {
                dto.getInfos().add(info);
            }
        }
        info.setOrderLinePkey(orderLine.getPkey());
        if (info.getRefundAmt() == null) info.setRefundAmt(BigDecimal.ZERO);
        if (refundLineMap.containsKey(orderLine.getPkey()))
            info.setRefundAmt(info.getRefundAmt().add(refundLineMap.get(orderLine.getPkey())));
        if (info.getRefundNum() == null) info.setRefundNum(0);
        if (refundLinePointMap.containsKey(orderLine.getPkey()))
            info.setRefundPoint(refundLinePointMap.get(orderLine.getPkey()));
        if (orderLine.getRefundNum() != null && orderLine.getRefundNum() > 0)
            info.setRefundNum(info.getRefundNum() + orderLine.getRefundNum());
    }

    // 积分商城供应商核销订单详情  添加line和info，暂不考虑退款
    public List<OrderV2Info> supplierOrderInfos(List<MktOrderLine> orderLines, Map<Integer, MktGoods> goodsMap, Integer orderPkey)
    {
        List<OrderV2Info> list = new ArrayList<>();
        Map<Integer, OrderV2Info> map = new HashMap<>();
        
        List<Integer> refundPkeyList = orderRefundDao.listStatusKey(orderPkey);
        Map<Long, BigDecimal> orlMap = new HashMap<>();
        if(refundPkeyList != null && !refundPkeyList.isEmpty())
        {
            orlMap = orderRefundLineDao.aggLineGoodsAmt(refundPkeyList);
        }
        
        for (MktOrderLine orderLine : orderLines)
        {
            OrderV2Info info = null;
            if (map.containsKey(orderLine.getGoods().intValue()))
            {
                info = map.get(orderLine.getGoods().intValue());
                OrderGwcV2OnList line = BeanUtil.beanFrom(OrderGwcV2OnList.class, orderLine);
                line.setPkey(orderLine.getPkey());
                info.getLines().add(line);
//                if(orlMap.containsKey(orderLine.getPkey()))
//                {
//                    line.setRefundAmt(orlMap.get(orderLine.getPkey()));
//                    info.setRefundAmt(info.getRefundAmt().add(line.getRefundAmt()));
//                }
                if(line.getRefundNum() != null)
                {
                    if(info.getRefundNum() == null)
                    {
                        info.setRefundNum(line.getRefundNum());
//                        info.setRefundAmt(line.getRefundAmt());
                    }
                    else
                    {
                        info.setRefundNum(info.getRefundNum() + line.getRefundNum());
//                        info.setRefundAmt(info.getRefundAmt().add(line.getRefundAmt()));
                    }
                }
            }
            else
            {
                info = new OrderV2Info();
                info.setGoods(orderLine.getGoods());
                info.setGoodsName(orderLine.getGoodsName());
                Integer mapKey = null;
                if (goodsMap.containsKey(orderLine.getGoods().intValue()))
                {
                    MktGoods goods = goodsMap.get(orderLine.getGoods().intValue());
                    mapKey = goods.getPkey();
                    String photo3 = goods.getPhoto3();
                    if (StringUtils.isBlank(photo3))
                    {
                        List<String> photo1 = goods.getPhoto1();
                        if (photo1 != null && !photo1.isEmpty()) photo3 = photo1.get(0);
                    }
                    info.setPhoto(photo3);
                    info.setMType(goods.getMType());
                }
                info.setSpace(orderLine.getSpace());
                info.setSpaceName(orderLine.getSpaceName());
                info.setNum(orderLine.getNum());
                info.setAssociation(orderLine.getAssociation());
                info.setAssociationName(orderLine.getAssociationName());
                
                if(orlMap.containsKey(orderLine.getGoods()))
                {
                    info.setRefundAmt(orlMap.get(orderLine.getGoods()));
                    info.setRefundNum(orderLine.getRefundNum());
                }
                
                OrderGwcV2OnList line = new OrderGwcV2OnList();
                BeanUtils.copyProperties(info, line, "photo");
                line.setPkey(orderLine.getPkey());
                info.getLines().add(line);
                map.put(mapKey, info);
            }
            info.setOrderLinePkey(orderLine.getPkey());
            list.add(info);
        }
        return list;
    }
    
    // dto填充farmerConfig
    public void farmerConfigDtoGwc(OrderTotalV2Info dto, SysFarmerConfig farmerConfig, BigDecimal reducePrice,
        AmtoWeightTotal awTotal, Integer addressPkey, Integer qrCode, Boolean isPostage)
    {
        if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
        {
            MktAppAddrDTO addr = new MktAppAddrDTO();
            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktDesktop mktDesktop = desktopDao.get(dto.getAddr().getPkey());
                if (mktDesktop != null)
                {
                    addr.setAddr(mktDesktop.getName());
                    addr.setPkey(mktDesktop.getPkey());
                }
            }
            else if (qrCode != null)
            {
                MktDesktop mktDesktop = desktopDao.get(qrCode);
                if (mktDesktop != null)
                {
                    addr.setAddr(mktDesktop.getName());
                    addr.setPkey(mktDesktop.getPkey());
                }
            }
            dto.setAddr(addr);
        }
        // 获取默认地址
        if (dto.getAddr() == null)
        {
            dto.setAddr(loadAddr(dto.getMember(),
                farmerConfig,
                DistributionType.PICKUP.equals(dto.getDistributionType()) ? AddrType.PICKUP : AddrType.DELIVERY,
                addressPkey));
        }
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        dto.setPstime(getImPsTime(farmerConfig, deliveryTimeConfig));
        // 计算会员优惠减免
        dto.setReducePrice(reducePrice);
        // 计算正常价格
        dto.setAmto(awTotal.getPointAmto().add(awTotal.getFarmerAmto()));
        dto.setWeight(awTotal.getPointWeight().add(awTotal.getFarmerWeight()));
        dto.setPostage(BigDecimal.ZERO);
        dto.setFee(BigDecimal.ZERO);
        if (Boolean.FALSE.equals(isPostage))
        {
            if (awTotal.getPointAmto().compareTo(BigDecimal.ZERO) > 0)
            //            && awTotal.getPointCalculateWeight().compareTo(BigDecimal.ZERO) == 1)
            {
                // 计算邮费
                loadPostage(dto,
                    farmerConfig,
                    awTotal.getPointCalculateWeight(),
                    awTotal.getPointAmto().subtract(reducePrice));
            }
            
            if (awTotal.getFarmerAmto().compareTo(BigDecimal.ZERO) > 0)
            //            && awTotal.getFarmerCalculateWeight().compareTo(BigDecimal.ZERO) == 1)
            {
                // 不是积分商品, 统一配送,不免邮的商品 计算统一配送费用
                if (farmerConfig.getDistributionConfig() != null && !farmerConfig.getDistributionConfig())
                {
                    BigDecimal fee =
                        loadPostageFee(farmerConfig, awTotal.getFarmerAmto().subtract(dto.getReducePrice()));
                    dto.setFee(fee);
                    dto.setPostage(dto.getPostage().add(fee));
                }
                else
                {
                    // 计算邮费
                    loadPostage(dto,
                        farmerConfig,
                        awTotal.getFarmerCalculateWeight(),
                        awTotal.getFarmerAmto().subtract(reducePrice));
                }
            }
            if (DistributionType.DINE_IN.equals(dto.getDistributionType())
                || DistributionType.PICKUP.equals(dto.getDistributionType()))
            {
                dto.setPostage(BigDecimal.ZERO);
                dto.setFee(BigDecimal.ZERO);
            }
        }
        
        dto.setAmtall(dto.getPostage().add(dto.getAmto()));
        dto.setAmtn(dto.getAmtall().subtract(dto.getReducePrice()));
        dto.setPickupAmt(dto.getAmtn().subtract(dto.getPostage()));
        
        log.info("dto.getPostage: {}", dto.getPostage());
        // 设置起步价
        dto.setStartingPrice(farmerConfig.getStartingPrice());
    }
    
    // dto组装
    public void assembleDtoGwc(OrderTotalV2Info dto, String farmerKey, int points, SysFarmer farmer)
    {
        Integer appid = MobileSession.appid();
        if (!(Constant.Operation + appid).equals(farmerKey))
            dto.setOrderType(OrderType.MARKET_ORDER);
        else
            dto.setOrderType(OrderType.INTEGRAL_ORDER);
//        dto.setPayType(PayType.ORDER_WEIXIN);
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        dto.setCommn(BigDecimal.ZERO);
        dto.setFarmer(farmerKey);
        dto.setCompany(farmer.getOrg());
        dto.setPointn(points);
        dto.setPostFree(false);
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        
        if (dto.getCardAmt().compareTo(dto.getAmto()) > 0)
            dto.setAmtn(dto.getAmto().subtract(dto.getAmto()));
        else
            dto.setAmtn(dto.getAmto().subtract(dto.getCardAmt()));
//        if (dto.getAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setAmtn(new BigDecimal(0.01));
        dto.setAmtn(dto.getAmtn().add(dto.getPostage()));
        //        dto.setAmtn(dto.getAmtn().subtract(dto.getCardAmt()).subtract(dto.getCardPostageAmt()));
        dto.setPickupAmt(dto.getPickupAmt().subtract(dto.getCardAmt()));
        if(dto.getPickupAmt().compareTo(BigDecimal.ZERO) < 0)
            dto.setPickupAmt(BigDecimal.ZERO);
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        List<DistributionTypeDTO> rlsit = buildDistributionType(farmer, deliveryTimeConfig);
        dto.setDistype(rlsit);
    }
    
    @Deprecated
    // gwc 获取最优优惠券
    public MktMemberCard getCard(List<MktGwc> gwcs, Map<Integer, MktGoods> goodsMap,
        Map<Integer, MktGoodsSpace> spaceMap, Integer member, MktMember mktMember, BigDecimal postage, String farmer,
        List<Integer> inVendorKeys)
    {
        Integer appid = MobileSession.appid();
        List<Integer> inVendorCardKeys = new ArrayList<>();
        Boolean checkMemberCard = checkCardDayOne(member, appid, 87);
        if (Boolean.FALSE.equals(checkMemberCard)) inVendorCardKeys.add(87);
        Map<String, CardLinshiDto> map = cardLinshiMap.findAll();
        for (CardLinshiDto cl : map.values())
        {
            checkMemberCard = checkCardDayOne(member, appid, cl.getCard());
            if (Boolean.FALSE.equals(checkMemberCard)) inVendorCardKeys.add(cl.getCard());
        }
        if (inVendorKeys != null && !inVendorKeys.isEmpty())
        {
            List<MktActivityLinshi> listMktActivityLinshi = activityLinshiDao.listMktActivityLinshi(inVendorKeys);
            listMktActivityLinshi.forEach(e -> inVendorCardKeys.add(e.getCard()));
        }
        // 活动
        if (inVendorCardKeys == null || inVendorCardKeys.isEmpty())
        {
            return null;
        }
        
        List<Integer> checkMemberCardKeys = checkCardDayOneV2(member, appid);
        Boolean flag = false;
        if (mktMember.getLevel().getIndex() == 1) flag = true;
        BigDecimal amt = BigDecimal.ZERO;
        Map<Integer, BigDecimal> vendorMap = new HashMap<>();
        Map<Integer, BigDecimal> gdMap = new HashMap<>();
        Map<Integer, BigDecimal> gtypeMap = new HashMap<>();
        for (MktGwc gwc : gwcs)
        {
            if (!spaceMap.containsKey(gwc.getSpace())) continue;
            MktGoodsSpace space = spaceMap.get(gwc.getSpace());
            MktGoods goods = goodsMap.get(gwc.getGoods());
            if (!goods.getMType().equals(MType.MARKET_GOODS) && !goods.getMType().equals(MType.SPECIAL_GOODS)) continue;
            Integer gtype = goods.getGtype();
            BigDecimal add = BigDecimal.ZERO;
            if (flag && space.getPriceMember().compareTo(BigDecimal.ZERO) == 1)
            {
                add = space.getPriceMember().multiply(new BigDecimal(gwc.getNum()));
            }
            else
            {
                add = space.getPrice().multiply(new BigDecimal(gwc.getNum()));
            }
            amt = amt.add(add);
            if (vendorMap.containsKey(goods.getVendor()))
            {
                vendorMap.put(goods.getVendor(), vendorMap.get(goods.getVendor()).add(add));
            }
            else
                vendorMap.put(goods.getVendor(), add);
            if (gdMap.containsKey(gwc.getGoods()))
            {
                gdMap.put(gwc.getGoods(), gdMap.get(gwc.getGoods()).add(add));
            }
            else
                gdMap.put(gwc.getGoods(), add);
            if (gtypeMap.containsKey(gtype))
            {
                gtypeMap.put(gtype, gtypeMap.get(gtype).add(add));
            }
            else
                gtypeMap.put(gtype, add);
        }
        
        List<MktMemberCard> list =
            memberCardDao.listMemberCard(mktMember.getPkey(), null, checkMemberCardKeys, inVendorCardKeys);
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getCard()));
        List<MktMemberCard> cards = new ArrayList<>();
        for (MktMemberCard mcard : list)
        {
            if (amt.add(postage).compareTo(mcard.getLimitCost()) < 0)
            {
                continue;
            }
            String userFarmer = mcard.getUserFarmer();
            if (StringUtils.isNotBlank(userFarmer))
            {
                if (!farmer.equals(userFarmer) && !(Constant.Operation + appid).equals(userFarmer))
                {
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
            cards.add(mcard);
        }
        
        MktMemberCard memberCard = null;
        if (!cards.isEmpty())
        {
            Collections.sort(cards, new Comparator<MktMemberCard>()
            {
                @Override
                public int compare(MktMemberCard o1, MktMemberCard o2)
                {
                    return o2.getCost().compareTo(o1.getCost());
                }
            });
            memberCard = cards.get(0);
        }
        return memberCard;
    }
    
    // 订单明显  获取最优优惠券
    //    public MktMemberCard getCardOrderLine(List<MktOrderLine> gwcs, Map<Integer, MktGoods> goodsMap,
    //        Map<Integer, MktGoodsSpace> spaceMap, Integer member)
    //    {
    //        Map<Integer, GoodsCardInfo> cardMap = new HashMap<>();
    //        for (MktOrderLine gwc : gwcs)
    //        {
    //            MktGoodsSpace space = spaceMap.get(gwc.getSpace());
    //            Integer goodsKey = space.getGoods();
    //            MktGoods goods = goodsMap.get(goodsKey);
    //            if (cardMap.containsKey(goodsKey))
    //            {
    //                GoodsCardInfo goodsCardInfo = cardMap.get(goodsKey);
    //                BigDecimal cost = space.getPrice().multiply(BigDecimal.valueOf(gwc.getNum()));
    //                goodsCardInfo.setCost(goodsCardInfo.getCost().add(cost));
    //            }
    //            else
    //            {
    //                GoodsCardInfo goodsCardInfo = new GoodsCardInfo();
    //                goodsCardInfo.setFarmer(goods.getFarmer());
    //                goodsCardInfo.setUserType(goods.getGtype());
    //                goodsCardInfo.setUserGoods(goodsKey);
    //                goodsCardInfo.setCost(space.getPrice().multiply(BigDecimal.valueOf(gwc.getNum())));
    //                cardMap.put(goodsKey, goodsCardInfo);
    //            }
    //        }
    //        MktMemberCard memberCard = getOptimalCard(cardMap, member);
    //        return memberCard;
    //    }
    
    //    public MktMemberCard getOptimalCard(Map<Integer, GoodsCardInfo> cardMap, Integer member)
    //    {
    //        MktMemberCard card = null;
    //        List<MktMemberCard> cards = new ArrayList<>();
    //        for (Integer key : cardMap.keySet())
    //        {
    //            GoodsCardInfo info = cardMap.get(key);
    //            MktMemberCard optimalCard = memberCardDao
    //                .optimalCard(member, info.getFarmer(), info.getUserType(), info.getUserGoods(), info.getCost());
    //            if (optimalCard != null)
    //            {
    //                cards.add(optimalCard);
    //            }
    //        }
    //        if (!cards.isEmpty())
    //        {
    //            Collections.sort(cards, new Comparator<MktMemberCard>()
    //            {
    //                @Override
    //                public int compare(MktMemberCard o1, MktMemberCard o2)
    //                {
    //                    return o2.getCost().compareTo(o1.getCost());
    //                }
    //            });
    //            card = cards.get(0);
    //        }
    //        return card;
    //    }
    
    // 临时活动 进行校验  一个会员一天只能用一张优惠券
    public Boolean checkCardDayOne(Integer member, Integer ascription, Integer card)
    {
        Date date = new Date();
        return orderDao
            .checkMemberCard(member, DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date), ascription, card);
    }
    
    // 临时活动 进行校验  一个会员一天只能用一张优惠券以及商户只能用一张
    public List<Integer> checkCardDayOneV2(Integer member, Integer ascription)
    {
        Date date = new Date();
        Map<String, Long> map =
            orderDao.checkMemberCardV2(member, DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date), ascription);
        List<Integer> keys = new ArrayList<>();
        for (String key : map.keySet())
        {
            keys.add(Integer.valueOf(key));
        }
        if (keys.isEmpty()) return keys;
        List<MktMemberCard> list = memberCardDao.select().in("pkey", keys).exec();
        keys.clear();
        list.forEach(e -> keys.add(e.getCard()));
        return keys;
    }
    
    //    public MktMemberCard checkCardPostage(Integer member, String farmer, Integer pkey, BigDecimal postage)
    //    {
    //        String time = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
    //        MktMemberCard card = memberCardDao.selectOne()
    //            .eq("status", CardStatus.UNUSED)
    //            .eq("member", member)
    //            .eq("pkey", pkey)
    //            .eq("type", CardCouponType.POSTAGE_COUPON)
    //            .eq("invalid", false)
    //            .ge("endDate", time)
    //            .exec();
    //        System.out.println("checkCardPostage-card: " + card);
    //        if (card == null) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
    //        if (StringUtils.isNotBlank(card.getUserFarmer()) && !card.getUserFarmer().equals(farmer))
    //            throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
    //        if (postage.compareTo(card.getLimitCost()) < 0) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
    //        // 如果是活动卡券，检查活动限制
    //        if (card.getActivity() != null)
    //        {
    //            MktActivity activity = activityDao.get(card.getActivity());
    //            if (activity != null && activity.getLimitDailyCardNum() != -1)
    //            {
    //                long usedNum = memberCardDao.countByActivity(activity.getPkey(),
    //                        member,
    //                        CardStatus.USED,
    //                        cn.tofocus.lejia.utils.DateUtil.atStartOfToday(),
    //                        cn.tofocus.lejia.utils.DateUtil.atStartOfTomorrow());
    //                if (usedNum >= activity.getLimitDailyCardNum())
    //                    throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该活动优惠券已达到今日使用上限");
    //            }
    //        }
    //        return card;
    //    }
    
    // 检验卡券是否可以用 下订单时候使用
    public MktMemberCard checkCard(Map<Integer, MktGoods> goodsMap, Map<Integer, MktGoodsSpace> spaceMap,
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
    
    public MktMemberCard checkCard(Integer pkey, Map<Integer, MktGoods> goodsMap, Map<Integer, MktGoodsSpace> spaceMap,
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
        // TODO 限制商户等需要调整
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
            System.out.println("cardMap: " + JsonUtil.toString(cardMap, true));
            System.out.println("cost: " + cost);
            //            cost = cost.add(postage);
            if (cost.compareTo(card.getLimitCost()) == -1) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        }
        return card;
    }
    
    // 添加info和card
    public void addInfoAndCard(OrderTotalV2Info dto, MktGoods goods, MktGoodsSpace space, String farmerKey, int num,
        Integer association)
    {
        OrderV2Info info = new OrderV2Info();
        info.setGoods(goods.getPkey().longValue());
        info.setGoodsName(goods.getTitle());
        String photo3 = goods.getPhoto3();
        if (StringUtils.isBlank(photo3))
        {
            List<String> photo1 = goods.getPhoto1();
            if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
        }
        info.setPhoto(photo3);
        info.setMType(goods.getMType());
        info.setSpace(space.getPkey().longValue());
        info.setSpaceName(space.getSpace());
        
        // 价格取现价 有会员价 在会员优惠里体现
        info.setPrice(space.getPrice());
        // 砍价商品 取原价
        if (goods.getMType().equals(MType.CUT_GOODS)) info.setPrice(space.getPriceOld());
        info.setNum(num);
        OrderGwcV2OnList line = new OrderGwcV2OnList();
        BeanUtils.copyProperties(info, line, "photo");
        line.setSpace(info.getSpace().intValue());
        if (StringUtils.isNotBlank(space.getPhoto1()))
            line.setPhoto(space.getPhoto1());
        else
            line.setPhoto(info.getPhoto());
        info.getLines().add(line);
        Integer appid = MobileSession.appid();
        if (goods.getFarmer().equals(Constant.Operation + appid))
        {
            dto.setPointInfo(Arrays.asList(info));
            dto.setPointNum(1);
            if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                dto.setPointPhoto(Arrays.asList(goods.getPhoto1().get(0)));
            else
                dto.setPointPhoto(Arrays.asList());
        }
        else
        {
            dto.setFarmerNum(1);
            if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                dto.setFarmerPhoto(Arrays.asList(goods.getPhoto1().get(0)));
            else
                dto.setFarmerPhoto(Arrays.asList());
            List<OrderV2Info> farmerInfo = new ArrayList<>();
            farmerInfo.add(info);
            dto.setFarmerInfo(farmerInfo);
            if (association != null)
            {
                addAssociation(association, dto);
            }
            if (goods.getMType().equals(MType.MARKET_GOODS) || goods.getMType().equals(MType.SPECIAL_GOODS)
                || goods.getMType().equals(MType.BOX_GOODS))
            {
                List<MemberCardV2OnList> cards = cardV2Manager.listCard(dto);
                if (CollectionUtil.isNotEmpty(cards))
                {
                    MemberCardV2OnList card = cards.get(0);
                    dto.setCardAmt(card.getCost());
                    dto.setCard(card.getPkey());
                    dto.setCardUsable(true);
                }
            }
        }
        if (dto.getPostage() != null && dto.getPostage().compareTo(BigDecimal.ZERO) > 0)
        {
            dto.setType(CardCouponType.POSTAGE_COUPON);
            List<MemberCardV2OnList> cards = cardV2Manager.listCard(dto);
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
                    dto.setCardPostageAmt(dto.getOldPostage());
                    dto.setPostage(BigDecimal.ZERO);
                }
                else
                {
                    BigDecimal postage = dto.getOldPostage().subtract(card.getCost());
                    dto.setCardPostageAmt(card.getCost());
                    if (postage.compareTo(BigDecimal.ZERO) < 0)
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
        }
    }
    
    private void addAssociation(Integer association, OrderTotalV2Info dto)
    {
        MktGoodsSpace space = goodsSpaceDao.get(association);
        if (space == null) return;
        MktGoods goods = goodsDao.get(space.getGoods());
        OrderV2Info info = new OrderV2Info();
        info.setGoods(goods.getPkey().longValue());
        info.setGoodsName(goods.getTitle());
        OrderV2Info orderV2Info = dto.getFarmerInfo().get(0);
        
        info.setAssociation(orderV2Info.getGoods().intValue());
        info.setAssociationName(orderV2Info.getGoodsName());
        
        String photo3 = goods.getPhoto3();
        if (StringUtils.isBlank(photo3))
        {
            List<String> photo1 = goods.getPhoto1();
            if (photo1 != null && photo1.size() > 0) photo3 = photo1.get(0);
        }
        info.setPhoto(photo3);
        info.setMType(goods.getMType());
        info.setSpace(space.getPkey().longValue());
        info.setSpaceName(space.getSpace());
        // 价格取现价 有会员价 在会员优惠里体现
        info.setPrice(space.getPrice());
        // 砍价商品 取原价
        if (goods.getMType().equals(MType.CUT_GOODS)) info.setPrice(space.getPriceOld());
        info.setNum(1);
        OrderGwcV2OnList line = new OrderGwcV2OnList();
        BeanUtils.copyProperties(info, line, "photo");
        if (StringUtils.isNotBlank(space.getPhoto1()))
            line.setPhoto(space.getPhoto1());
        else
            line.setPhoto(info.getPhoto());
        info.getLines().add(line);
        if (dto.getFarmerInfo() != null)
        {
            dto.getFarmerInfo().add(info);
            dto.setFarmerNum(2);
        }
        
    }
    
    public void assemblePostage(OrderTotalV2Info dto, MktGoods goods, MktGoodsSpace space, int num,
        SysFarmerConfig farmerConfig, Integer addressPkey, Integer qrCode)
    {
        // 获取默认地址
        if (goods.getMType().equals(MType.GIFT_GOODS) || goods.getMType().equals(MType.COUPON_GOODS))
        {
            dto.setAddr(null);
        }
        else if (goods.getMType().equals(MType.BOX_GOODS))
        {
            MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
            MktAppAddrDTO addr = new MktAppAddrDTO();
            addr.setAddr(goodsBox.getDesktopName());
            addr.setPkey(goodsBox.getDesktop());
            dto.setAddr(addr);
        }
        else if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
        {
            MktAppAddrDTO addr = new MktAppAddrDTO();
            if (qrCode != null)
            {
                MktDesktop mktDesktop = desktopDao.get(qrCode);
                if (mktDesktop != null)
                {
                    addr.setAddr(mktDesktop.getName());
                    addr.setPkey(mktDesktop.getPkey());
                }
            }
            else if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktDesktop mktDesktop = desktopDao.get(dto.getAddr().getPkey());
                if (mktDesktop != null)
                {
                    addr.setAddr(mktDesktop.getName());
                    addr.setPkey(mktDesktop.getPkey());
                }
            }
            dto.setAddr(addr);
        }
        else
        {
            dto.setAddr(loadAddr(dto.getMember(),
                farmerConfig,
                Boolean.TRUE.equals(dto.getPickupType()) ? AddrType.PICKUP : AddrType.DELIVERY,
                addressPkey));
        }
        dto.setPostage(BigDecimal.ZERO);
        // 获取配送时间
        MktDeliveryTimeConfig deliveryTimeConfig =
            deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
        if (OrderType.PRESALE_ORDER.equals(dto.getOrderType()))
        {
            MktGoodsPresale gp = goodsPresaleDao.get(goods.getPkey());
            if (gp != null)
            {
                dto.setPstime(DateUtil.formatDate(gp.getStartDate(), "yyyy-MM-dd") + "~"
                    + DateUtil.formatDate(gp.getEndDate(), "yyyy-MM-dd"));
                if (gp.getStartDate().compareTo(gp.getEndDate()) == 0)
                    dto.setPstime(DateUtil.formatDate(gp.getStartDate(), "yyyy-MM-dd"));
            }
        }
        else
            dto.setPstime(getImPsTime(farmerConfig, deliveryTimeConfig));
        // 礼品券 不计算重量和运费
        if (goods.getMType().equals(MType.GIFT_GOODS))
        {
            dto.setWeight(BigDecimal.ZERO);
            dto.setPostage(BigDecimal.ZERO);
            dto.setStartingPrice(BigDecimal.ZERO);
            dto.setAmtall(dto.getAmto());
            dto.setAmtn(dto.getAmto());
            dto.setPickupAmt(dto.getAmto());
        }
        else
        {
            dto.setWeight(space.getWeight().multiply(new BigDecimal(num)));
            // 不是积分商品, 统一配送,不免邮的商品 计算统一配送费用
            if (!goods.getMType().equals(MType.INTEGRAL_GOODS) && farmerConfig.getDistributionConfig() != null
                && !farmerConfig.getDistributionConfig() && Boolean.FALSE.equals(goods.getIsPostage()))
            {
                BigDecimal fee = loadPostageFee(farmerConfig, dto.getAmto().subtract(dto.getReducePrice()));
                dto.setFee(fee);
                dto.setPostage(fee);
            }
            else
            {
                // 计算邮费
                if (Boolean.FALSE.equals(goods.getIsPostage()))
                {
                    loadPostage(dto, farmerConfig, dto.getWeight(), dto.getAmto().subtract(dto.getReducePrice()));
                }
            }
            if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
            {
                dto.setPostage(BigDecimal.ZERO);
                dto.setFee(BigDecimal.ZERO);
            }
            dto.setAmtall(dto.getAmto().add(dto.getPostage()));
            dto.setAmtn(dto.getAmtall().subtract(dto.getReducePrice()));
            dto.setPickupAmt(dto.getAmtn().subtract(dto.getPostage()));
            // 设置起步价
            dto.setStartingPrice(farmerConfig.getStartingPrice());
        }
    }
    
    public OrderType getOrderType(Integer index)
    {
        OrderType res = OrderType.MARKET_ORDER;
        switch (index)
        {
            case 0:// 积分
                res = OrderType.INTEGRAL_ORDER;
                break;
            case 1:// 市场
                res = OrderType.MARKET_ORDER;
                break;
            case 2:// 会员
                res = OrderType.MARKET_ORDER;
                break;
            case 3:// 特价
                res = OrderType.MARKET_ORDER;
                break;
            case 4:// 分享
                res = OrderType.SHARE_ORDER;
                break;
            case 5:// 砍价
                res = OrderType.CUT_ORDER;
                break;
            case 6:// 团购
                res = OrderType.COLLAGE_ORDER;
                break;
            case 7:// 预售
                res = OrderType.PRESALE_ORDER;
                break;
            case 9:// 礼品券
                res = OrderType.GIFT_ORDER;
                break;
            case 10:// 优惠券
                res = OrderType.COUPON_ORDER;
                break;
            default:
                res = OrderType.MARKET_ORDER;
                break;
        }
        return res;
    }
    
    public void checkBugGoodsNum(List<OrderV2Info> list, Map<Integer, MktGoods> goodsMap)
    {
        Map<Integer, Integer> checkNumMap = new HashMap<>();
        for (OrderV2Info line : list)
        {
            for (OrderGwcV2OnList g : line.getLines())
            {
                if (checkNumMap.containsKey(line.getGoods().intValue()))
                {
                    checkNumMap.put(line.getGoods().intValue(), checkNumMap.get(line.getGoods().intValue()) + g.getNum());
                }
                else
                {
                    checkNumMap.put(line.getGoods().intValue(), g.getNum());
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
    
    public void checkBugGoodsNumOrderLine(List<MktOrderLine> lines, Map<Integer, MktGoods> goodsMap)
    {
        Map<Integer, Integer> checkNumMap = new HashMap<>();
        for (MktOrderLine gwc : lines)
        {
            if (checkNumMap.containsKey(gwc.getGoods().intValue()))
            {
                checkNumMap.put(gwc.getGoods().intValue(), checkNumMap.get(gwc.getGoods().intValue()) + gwc.getNum());
            }
            else
            {
                checkNumMap.put(gwc.getGoods().intValue(), gwc.getNum());
            }
        }
        for (Integer key : checkNumMap.keySet())
        {
            if (!goodsMap.containsKey(key)) continue;
            MktGoods goods = goodsMap.get(key);
            // 校验是否超出每日限购
            getBuyGoodsNum(goods, checkNumMap.get(key));
        }
    }
    
    public void checkBugGoodsNumGwc(List<MktGwc> gwcs, Map<Integer, MktGoods> goodsMap)
    {
        Map<Integer, Integer> checkNumMap = new HashMap<>();
        for (MktGwc gwc : gwcs)
        {
            if (checkNumMap.containsKey(gwc.getGoods()))
            {
                checkNumMap.put(gwc.getGoods(), checkNumMap.get(gwc.getGoods()) + gwc.getNum());
            }
            else
            {
                checkNumMap.put(gwc.getGoods(), gwc.getNum());
            }
        }
        for (Integer key : checkNumMap.keySet())
        {
            if (!goodsMap.containsKey(key)) continue;
            MktGoods goods = goodsMap.get(key);
            // 校验是否超出每日限购
            getBuyGoodsNum(goods, checkNumMap.get(key));
        }
    }
    
    /**
     * 判断今日限购 true 没有超过限制
     * <功能详细描述>
     * @return
     */
    public Boolean getBuyGoodsNum(MktGoods goods, int num)
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
    
    /**
     * 库存和下架校验
     * <功能详细描述>
     * @return
     */
    public void checkGoodsKcNum(MktGoods goods, MktGoodsSpace space, int num)
    {
        Long kcNum = spaceKcCache.getLong(String.valueOf(space.getPkey()));
        System.out.println("kcNum: " + kcNum);
        if (kcNum == null) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
        if (kcNum.intValue() < num) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
        if (!goods.getEnabled()) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED, goods.getTitle() + "已下架");
    }
    
    public String getImPsTime(SysFarmerConfig farmerConfig, MktDeliveryTimeConfig deliveryTimeConfig)
    {
        if (farmerConfig == null)
        {
            return "";
        }
        //        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String res = appOrderManager.getOrderPsTime(farmerConfig, deliveryTimeConfig);
        //        if (StringUtils.isNotBlank(farmerConfig.getYytb()) && StringUtils.isNotBlank(farmerConfig.getYyte()))
        //        {
        //            Date now = new Date();
        //            String yytb = farmerConfig.getYytb();
        //            String yyte = farmerConfig.getYyte();
        //            String strnow = formatter.format(now);
        //            Integer minute = 0;
        //            if (farmerConfig.getPhour() != null) minute = farmerConfig.getPhour() * 60;
        //            if (farmerConfig.getPminute() != null) minute = minute + farmerConfig.getPminute();
        //            String newTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
        //            if (newTime.compareTo(yytb) >= 0 && newTime.compareTo(yyte) <= 0)
        //            {
        //                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //                String day = sdf.format(now);
        //                res = day + " " + newTime;
        //                minute = minute + 30;
        //                String endTime = LejiaUtils.getNewTime(strnow, String.valueOf(minute));
        //                res = res + "~" + endTime;
        //            }
        //        }
        //        if()
        return res;
        
    }
    
    public BigDecimal loadPostageFee(SysFarmerConfig farmerConfig, BigDecimal amto)
    {
        
        if (farmerConfig.getIsFree() != null && farmerConfig.getIsFree() && farmerConfig.getFreeDelivery() != null)
        {
            if (amto.compareTo(farmerConfig.getFreeDelivery()) >= 0) return BigDecimal.ZERO;
        }
        BigDecimal res = farmerConfig.getFee() == null ? BigDecimal.ZERO : farmerConfig.getFee();
        
        if(farmerConfig.getIsReductionTwo() != null && farmerConfig.getIsReductionTwo() 
            && amto.compareTo(farmerConfig.getReachTwo()) >= 0 && farmerConfig.getReductionDeliveryTwo() != null)
        {
            System.out.println("loadPostageFee满减减免运费 2 ");
            res = res.subtract(farmerConfig.getReductionDeliveryTwo());
        }
        else if(farmerConfig.getIsReductionOne() != null && farmerConfig.getIsReductionOne() 
            && amto.compareTo(farmerConfig.getReachOne()) >= 0 && farmerConfig.getReductionDeliveryOne() != null)
        {
            System.out.println("loadPostageFee满减减免运费 1 ");
            res = res.subtract(farmerConfig.getReductionDeliveryOne());
        }
        if(res.compareTo(BigDecimal.ZERO) < 0)
        {
            res = BigDecimal.ZERO;
        }
        return res;
    }
    
    /*
     * 计算邮费
     */
    public void loadPostage(OrderTotalV2Info dto, SysFarmerConfig farmerConfig, BigDecimal weight, BigDecimal amto)
    {
        List<MktPostageConfig> list =
            postageConfigDao.select().eq("farmer", farmerConfig.getPkey()).sort("weight", true).exec();
        log.info("计算邮费传进来的数据:  weight: {}, amto: {}", weight, amto);
        if (farmerConfig.getIsFree() != null && farmerConfig.getIsFree() && farmerConfig.getFreeDelivery() != null
            && amto.compareTo(farmerConfig.getFreeDelivery()) >= 0)
        {
            dto.setPostFree(true);
            return;
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
            dto.setPostage(dto.getPostage().add(list.get(list.size() - 1).getPostage()));
            return;
        }
        
        if (j > 0)
        {
            j = j - 1;
        }
        dto.setPostage(dto.getPostage().add(list.get(j).getPostage()));
        // 满减减免运费
        if(farmerConfig.getIsReductionTwo() != null && farmerConfig.getIsReductionTwo() 
            && amto.compareTo(farmerConfig.getReachTwo()) >= 0 && farmerConfig.getReductionDeliveryTwo() != null)
        {
            System.out.println("满减减免运费 2 ");
            dto.setPostage(dto.getPostage().subtract(farmerConfig.getReductionDeliveryTwo()));
        }
        else if(farmerConfig.getIsReductionOne() != null && farmerConfig.getIsReductionOne() 
            && amto.compareTo(farmerConfig.getReachOne()) >= 0 && farmerConfig.getReductionDeliveryOne() != null)
        {
            System.out.println("满减减免运费 1 ");
            dto.setPostage(dto.getPostage().subtract(farmerConfig.getReductionDeliveryOne()));
        }
        if(dto.getPostage().compareTo(BigDecimal.ZERO) < 0)
        {
            dto.setPostage(BigDecimal.ZERO);
        }
    }
    
    /**
     * 读取默认地址
     * @param member
     * @param config
     * @return
     */
    public MktAppAddrDTO loadAddr(Integer member, SysFarmerConfig config, AddrType addrType, Integer addressPkey)
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
                addr = addrDao.selectOne().eq("member", member).eq("defaultAddr", true).exec();
                MktAddr a = new MktAddr();
                a.setType(AddrType.PICKUP);
                if(addr != null)
                {
                    BeanUtils.copyProperties(addr, a, "type", "pkey");
                }
                else
                {
                    addr = addrDao.selectOne().eq("member", member).eq("defaultAddr", false).exec();
                    if(addr != null)
                    {
                        BeanUtils.copyProperties(addr, a, "type", "pkey");
                    }
                    else
                    {
                        MktMember mktMember = MobileSession.member();
                        a.setMember(mktMember.getPkey());
                        a.setName(mktMember.getName());
                        a.setMobile(mktMember.getMobile());
                        a.setAddrCode("1");
                        a.setDefaultAddr(true);
                        a.setLatitude(BigDecimal.ZERO);
                        a.setLongitude(BigDecimal.ZERO);
                        a.setAscription(MobileSession.appid());
                    }
                }
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
    
    public void newChkOrder(OrderTotalV2Info dto, boolean isCommit)
    {
        Integer appid = MobileSession.appid();
        if (!(Constant.Operation + appid).equals(dto.getFarmer()))
        {
            // 营业时间校验
            SysFarmerConfig config = sysFarmerConfigDao.get(dto.getFarmer());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String dd = sdf.format(new Date());
            //            String de = dd;
            MktDeliveryTimeConfig deliveryTimeConfig =
                deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(dto.getFarmer(), dto.getAddr());
            if (deliveryTimeConfig.getHour() != null && deliveryTimeConfig.getMinute() != null)
            {
                Integer m = deliveryTimeConfig.getHour() * 60 + deliveryTimeConfig.getMinute();
                dd = LejiaUtils.getNewTime(dd, String.valueOf(m));
            }
            if (Boolean.FALSE.equals(config.getYStatus())) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
            
            // 配送范围检验
            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
                if (DistributionType.PICKUP.equals(dto.getDistributionType()))
                {
                    if(isCommit && StringUtils.isBlank(dto.getPstime()))
                        throw TofocusException.of(LejiaErrCode.PICKUP_TIME_ERROR);
                    if (AddrType.DELIVERY.equals(addrObj.getType()))
                        throw TofocusException.of(LejiaErrCode.DELIVERY_ADDR_ERROR);
                }
                else if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
                {
                    
                }
                else
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
                if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
                {
                    Integer qrCode = MobileSession.qrCode();
                    if (qrCode == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择桌位");
                }
                else
                {
                    String addrColumName = DistributionType.PICKUP.equals(dto.getDistributionType()) ? "自提人" : "收货地址";
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择" + addrColumName);
                }
            }
            SysFarmer farmer = sysFarmerDao.get(dto.getFarmer());
            if (farmer.getIdDel() || !farmer.getEnabled()) throw TofocusException.of(LejiaErrCode.FARMER_OVERTIME);
        }
        // 检验订单金额不可为零  由原来的 订单不可为零 修改为   订单金额是0 或者小于0 的 默认为0.01元
//        if (dto.getAmtn().compareTo(BigDecimal.ZERO) <= 0) dto.setAmtn(new BigDecimal(0.01));
        dto.setMyPoints(pointManager.loadPoints(dto.getMember()));
        // 积分余额不足
        if (dto.getMyPoints().compareTo(dto.getPointn()) < 0) throw TofocusException.of(LejiaErrCode.NO_P0INTS);
        dto.setMyCommn(commManager.loadComm(dto.getMember()));
        // 电子帐户余额不足
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) 
            && dto.getMyCommn().compareTo(dto.getAmtn()) < 0)
        {
            throw TofocusException.of(LejiaErrCode.NO_COMMS);
        }
        // 热力豆不足
        if (dto.getPayType().equals(PayType.ORDER_MSD))
        {
            BigDecimal loadMsd = getMsdBalance(dto.getMember());
            if(loadMsd.compareTo(dto.getAmtn()) < 0)
                throw TofocusException.of(LejiaErrCode.NO_COMMS);
        }
        // 心安食足 会员积分不足
        if (dto.getPayType().equals(PayType.NM_MEMBER))
        {
            MktMember member = MobileSession.member();
            BigDecimal xaszComms = saasTokenPublicManager.getAccountBalance(member.getMobile(), member.getOpenid1());
            System.out.println("xaszComms: " + xaszComms);
            System.out.println("dto.getAmtn(): " + dto.getAmtn());
            if (xaszComms.compareTo(dto.getAmtn()) < 0) throw TofocusException.of(LejiaErrCode.NO_COMMS);
        }
        if (dto.getOrderType() != null && dto.getOrderType().equals(OrderType.COLLAGE_ORDER))
        {
            MktOrderGroup group = orderGroupDao.selectOne()
                .eq("goods", dto.getFarmerInfo().get(0).getGoods())
                .eq("status", OrderGroupStatus.NOT_GROUPS)
                .exec();
            if (group != null)
            {
                List<String> list = group.getOrderList();
                if (!list.isEmpty())
                {
                    long count =
                        orderDao.aggregation().in("pkey", list.toArray()).eq("member", dto.getMember()).execCount();
                    if (count > 0) throw TofocusException.of(LejiaErrCode.ALREADYHERE_COLLAGEORDER);
                }
            }
        }
//        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
//            dto.setPickupAmt(new BigDecimal("0.01"));
        // 备注字数限制
        if (StringUtils.isNotBlank(dto.getRemark()) && dto.getRemark().length() > 50)
            throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT);
        // 市场商品起送费用校验
        if (isCommit && dto.getDistributionType() != DistributionType.PICKUP
            && dto.getDistributionType() != DistributionType.DINE_IN && dto.getFarmerInfo() != null
            && !dto.getFarmerInfo().isEmpty() && dto.getStartingPrice() != null
            && dto.getStartingPrice().compareTo(BigDecimal.ZERO) > 0)
        {
            MType mType = dto.getFarmerInfo().get(0).getMType();
            if (MType.MARKET_GOODS.equals(mType) || MType.SPECIAL_GOODS.equals(mType))
            {
                BigDecimal price = BigDecimal.ZERO;
                for (OrderV2Info oi : dto.getFarmerInfo())
                {
                    for (OrderGwcV2OnList og : oi.getLines())
                    {
                        BigDecimal multiply = og.getPrice().multiply(new BigDecimal(og.getNum()));
                        price = price.add(multiply);
                    }
                }
                if (price.compareTo(dto.getStartingPrice()) < 0)
                    throw TofocusException.of(LejiaErrCode.STARTINGPRICE_ERROR);
            }
        }
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
    
    private Boolean getWeek(SysFarmerConfig config)
    {
        LocalDate now = LocalDate.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
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
    
    public MktOrder insOrderOne(OrderTotalV2Info dto, String farmer, String company, String orderNumber,
        List<OrderV2Info> list, OrderType orderType, String body, BigDecimal longitude, BigDecimal latitude)
    {
        MktOrder order = new MktOrder();
        order.setCode(orderNumber);
        order.setMember(MobileSession.memberPkey());
        order.setStatus(OrderStatus.UNPAID_ORDER);
        Map<MType, SysFarmerMtype> mapMType = null;
        Integer appid = MobileSession.appid();
        if ((Constant.Operation + appid).equals(farmer))
        {
            order.setOrderOir(OrderOir.POINTS_MALL);
        }
        else
        {
            order.setOrderOir(OrderOir.MARKET_MALL);
            mapMType = sysFarmerMtypeDao.mapMType(farmer);
        }
        SysFarmerConfig config = sysFarmerConfigDao.get(farmer);
        orderDao.generateID(order);
        order.setOrderType(orderType);
        order.setCgCheck(0);
        order.setPayType(dto.getPayType());
        order.setPstime(dto.getPstime());
        order.setSettlementType(SettlementType.NOT_START);
        order.setCommissionType(config.getCommissionType());
        
        order.setTjr(dto.getTjr());
        order.setFarmer(farmer);
        order.setCompany(company);
        order.setAscription(appid);
        order.setDistributionType(dto.getDistributionType());
        order.setIsBox(false);
        
        BigDecimal amto = BigDecimal.ZERO;
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal postageWeight = BigDecimal.ZERO;
        Boolean isPostage = true;
        List<Integer> gwcIds = new ArrayList<>();
        Boolean isCard = false;
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        list.forEach(e -> {
            gkeys.add(e.getGoods().intValue());
            e.getLines().forEach(l -> {
                skeys.add(l.getSpace());
            });
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        // 商品限购校验
        checkBugGoodsNum(list, goodsMap);
        
        List<MktOrderLine> addOrderlines = new ArrayList<>();
        BigDecimal reducePrice = BigDecimal.ZERO;
        for (OrderV2Info line : list)
        {
            if (!goodsMap.containsKey(line.getGoods().intValue()))
            {
                throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
            }
            MktGoods goods = goodsMap.get(line.getGoods().intValue());
            if(order.getSupplier() == null && goods.getSupplier() != null)
                order.setSupplier(goods.getSupplier());
                
            // 判断商品的配送方式和市场设置的 是否有冲突
            checkMtypeGoods(goods, mapMType, dto.getDistributionType());
            for (OrderGwcV2OnList og : line.getLines())
            {
                if (!spaceMap.containsKey(og.getSpace()))
                {
                    throw TofocusException.of(LejiaErrCode.GWC_SPACE_NOTEXIST);
                }
                MktOrderLine orderLine = new MktOrderLine();
                orderLine.setStatus(order.getStatus());
                orderLine.setOrderPkey(order.getPkey());
                orderLine.setGoods((long)line.getGoods());
                orderLine.setSpace((long)og.getSpace());
                orderLine.setSpaceName(og.getSpaceName());
                orderLine.setGoodsName(line.getGoodsName());
                orderLine.setAscription(appid);
                MktGoodsSpace space = spaceMap.get(og.getSpace());
                orderLine.setPoint(space.getPoint());
                if(space.getWeight() != null)
                    orderLine.setWeight(space.getWeight().multiply(new BigDecimal(og.getNum())));
                if (goods.getMType().equals(MType.COUPON_GOODS))
                    orderLine.setCard(Integer.valueOf(goods.getExtendCon()));
                if (goods.getMType().equals(MType.BOX_GOODS))
                {
                    MktGoodsBox goodsBox = goodsBoxDao.selectOne().eq("goods", goods.getPkey()).exec();
                    if (goodsBox != null)
                    {
                        order.setLockId(goodsBox.getLockId());
                    }
                    order.setBoxSd(space.getBoxSd());
                    order.setBoxEd(space.getBoxEd());
                    order.setIsBox(true);
                    order.setBoxTime(orderLine.getSpaceName());
                    order.setBoxName(orderLine.getGoodsName());
                }
                body = body + line.getGoodsName() + " ";
                
                // 校验库存及下架 
                checkGoodsKcNum(goods, space, og.getNum());
                orderLine.setPrice(space.getPriceOld());
                if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
                    && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
                {
                    orderLine.setPricen(space.getPriceMember());
                    if (space.getPriceMember().compareTo(BigDecimal.ZERO) == 1)
                    {
                        BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
                        reducePrice = reducePrice.add(subtract.multiply(new BigDecimal(og.getNum())));
                    }
                }
                else
                {
                    orderLine.setPricen(space.getPrice());
                }
                orderLine.setNum(og.getNum());
                BigDecimal num = new BigDecimal(orderLine.getNum());
                orderLine.setFarmer(farmer);
                orderLine.setCompany(company);
                orderLine.setCouponPrice(orderLine.getPricen());
                orderLine.setCouponAmt(orderLine.getPricen().multiply(num));
                orderLine.setGtype(goods.getGtype());
                if (goods.getMType().equals(MType.PROCESS_GOODS))
                {
                    orderLine.setAssociation(line.getAssociation());
                    orderLine.setAssociationName(line.getAssociationName());
                }
                if (goods.getMType().equals(MType.MARKET_GOODS) || goods.getMType().equals(MType.SPECIAL_GOODS)
                    || goods.getMType().equals(MType.BOX_GOODS) || goods.getMType().equals(MType.PROCESS_GOODS))
                    isCard = true;
                if (goods.getMType().equals(MType.CUT_GOODS))
                    amto = amto.add(orderLine.getPrice().multiply(num));
                else
                {
                    amto = amto.add(orderLine.getPricen().multiply(num)).setScale(2);
                }
                if (space.getWeight() != null) weight = weight.add(space.getWeight().multiply(num));
                if ((DistributionType.IMMEDIATELY.equals(order.getDistributionType())
                    || DistributionType.ORDERED.equals(order.getDistributionType())) && goods.getIsPostage() != null
                    && !goods.getMType().equals(MType.GIFT_GOODS)
                    && !goods.getIsPostage())
                {
                    postageWeight = postageWeight.add(space.getWeight().multiply(num));
                    isPostage = false;
                }
                if (og.getPkey() != null) gwcIds.add(og.getPkey());
                addOrderlines.add(orderLine);
            }
        }
        order.setReducePrice(reducePrice);
        order.setWeight(weight);
        order.setAmto(amto);
        
       
        BigDecimal postage = BigDecimal.ZERO;
        if (!isPostage)
        {
            if (config == null || config.getPkey().equals(Constant.Operation + appid)
                || config.getDistributionConfig() == null || Boolean.TRUE.equals(config.getDistributionConfig()))
            {
                dto.setWeight(postageWeight);
                dto.setPostage(BigDecimal.ZERO);
                loadPostage(dto, config, postageWeight, amto.subtract(reducePrice));
                postage = dto.getPostage();
            }
            else
            {
                postage = loadPostageFee(config, amto.subtract(reducePrice));
            }
        }
        order.setOldPostage(postage);
        order.setPostage(postage);
        order.setCardPostageAmt(BigDecimal.ZERO);
        if (dto.getCardPostage() != null && postage.compareTo(BigDecimal.ZERO) > 0)
        {
            order.setCardPostage(dto.getCardPostage());
            //            MktMemberCard card = checkCardPostage(dto.getMember(), farmer, dto.getCardPostage(), postage);
            MktMemberCard card = checkCard(goodsMap,
                spaceMap,
                dto.getMember(),
                order.getCardPostage(),
                addOrderlines,
                farmer,
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
        if (DistributionType.DINE_IN.equals(dto.getDistributionType())) order.setPostage(BigDecimal.ZERO);
        
        log.info("ins_order_postage: {}", order.getPostage());
        if (dto.getDistributionType() != null && DistributionType.PICKUP.equals(dto.getDistributionType()))
            order.setAmtall(order.getAmto());
        else
        {
            order.setAmtall(order.getAmto().add(order.getOldPostage()));
        }
        if (order.getOrderType().equals(OrderType.INTEGRAL_ORDER) || order.getOrderType().equals(OrderType.GIFT_ORDER)
            || order.getOrderType().equals(OrderType.COUPON_ORDER) || order.getOrderType().equals(OrderType.INTEGRAL_BNYP_ORDER)
            || order.getOrderType().equals(OrderType.INTEGRAL_PRESALE_ORDER))
            order.setPointn(dto.getPointn());
        else
            order.setPointn(0);
        order.setCommn(dto.getCommn());
        if (Boolean.TRUE.equals(isCard)) order.setCard(dto.getCard());
        order.setCutAmt(BigDecimal.ZERO);
        if (order.getCard() != null)
        {
            // 校验该卡券是否可用
            MktMemberCard checkCard = checkCard(goodsMap,
                spaceMap,
                dto.getMember(),
                order.getCard(),
                addOrderlines,
                farmer,
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
        // 根据支付方式填写各自需要的金额
        order.setWeixinAmt(order.getAmtn());
        order.setOtherAmt(BigDecimal.ZERO);
        
        for (OrderV2Info line : list)
        {
            for (OrderGwcV2OnList og : line.getLines())
            {
                spaceKcCache.decrement(String.valueOf(og.getSpace()), og.getNum(), null);
            }
        }
        order = orderDao.add(order);
        // 2024-03-16 zdw 有优惠金额.将每个商品优惠后的价格记录一下
        if (order.getCardAmt().compareTo(BigDecimal.ZERO) > 0)
        {
            calculateCouponAmt(addOrderlines, order.getCardAmt());
        }
        orderLineDao.addAll(addOrderlines);
        MktOrderDesc desc = new MktOrderDesc();
        desc.setPkey(order.getPkey());
        desc.setLatitude(BigDecimal.ZERO);
        desc.setLongitude(BigDecimal.ZERO);
        desc.setAscription(appid);
        Integer qrCode = MobileSession.qrCode();
        if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
        {
            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktDesktop mktDesktop = desktopDao.get(dto.getAddr().getPkey());
                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
            }
            else if (qrCode != null)
            {
                MktDesktop mktDesktop = desktopDao.get(qrCode);
                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
            }
            MktMember member = memberDao.get(MobileSession.memberPkey());
            if(member != null)
            {
                desc.setName(member.getName());
                desc.setMobile(member.getMobile());
            }
            desc.setDistance(BigDecimal.ZERO);
        }
        else if (dto.getAddr() != null)
        {
            MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
            desc.setAddr(addrObj.getAddr());
            if (StringUtils.isNotBlank(addrObj.getAddrDetail())) desc.setAddr(desc.getAddr() + addrObj.getAddrDetail());
            desc.setName(addrObj.getName());
            desc.setMobile(addrObj.getMobile());
            desc.setLatitude(addrObj.getLatitude());
            desc.setLongitude(addrObj.getLongitude());
            desc.setPro(addrObj.getPro());
            desc.setCity(addrObj.getCity());
            desc.setArea(addrObj.getArea());
            desc.setRemark(dto.getRemark());
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
            if(dto.getPickupPkey() != null)
            {
                SysFarmerPickupLocation sysFarmerPickupLocation =
                    farmerPickupLocationDao.get(dto.getPickupPkey());
                if (sysFarmerPickupLocation != null)
                {
                    desc.setAddr(sysFarmerPickupLocation.getAddress());
                }
            }
            else if(DistributionType.PICKUP.equals(dto.getDistributionType()))
            {
                desc.setAddr(config.getAddr());
            }
        }
        orderDescDao.add(desc);
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
        return order;
    }
    
    public void calculateCouponAmt(List<MktOrderLine> addOrderlines, BigDecimal zCardAmt)
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
    
    public OrderTotalV2Info updOrderOne(OrderTotalV2Info dto, int pkey)
    {
        BigDecimal longitude = null;
        BigDecimal latitude = null;
        MktOrder order = orderDao.get(pkey);
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (!order.getStatus().equals(OrderStatus.UNPAID_ORDER)) throw TofocusException.of(LejiaErrCode.WRONG_STATUS);
        
        List<MktOrderLine> lines = orderLineDao.select().in("orderPkey", pkey).exec();
        String body = "";
        String payNumber = numberUtils.createOrderNumber();
        MktOrderCode oc = new MktOrderCode();
        oc.setOrderPkey(order.getPkey());
        oc.setCode(order.getCode());
        orderCodeDao.add(oc);
        if (dto.getFarmerInfo() != null && !dto.getFarmerInfo().isEmpty())
            order.setCode(payNumber + "2");
        else
            order.setCode(payNumber + "1");
        if(!OrderType.INTEGRAL_JD_ORDER.equals(order.getOrderType()))
        {
            updOrderOne(order, dto, lines, body, longitude, latitude);
        }
        
        if (dto.getPayType().equals(PayType.ORDER_ELECTRONIC_ACCOUNT) || dto.getPayType().equals(PayType.ORDER_MSD))
        {
            appOrderManager.payAfterOrder(order);
        }
        try
        {
            if (dto.getPayType().equals(PayType.ORDER_WEIXIN) ||
                dto.getPayType().equals(PayType.MSD_COMBINATION) ||
                dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
            {
                Integer appid = MobileSession.appid();
                BigDecimal amtn = order.getAmtn();
                if(dto.getPayType().equals(PayType.MSD_COMBINATION) ||
                dto.getPayType().equals(PayType.ELECTRONIC_ACCOUNT_COMBINATION))
                    amtn = order.getWeixinAmt();
                if(amtn.compareTo(BigDecimal.ZERO) <= 0)
                {
                    appOrderManager.payAfterOrder(order);
                    return dto;
                }
                if (appid.equals(1))
                {
                    WxPayData payData = nsPayManager.topayIvc(MobileSession.openid(), payNumber, amtn);
                    dto.setWxPayData(payData);
                }
                else if (appid.equals(13))
                {
                    WxPayData payData = chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payNumber, amtn);
                    dto.setWxPayData(payData);
                }
                else
                {
                    WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
                    PayJs js = wxPayManger.topayIvc(MobileSession.billIp(),
                        MobileSession.openid(),
                        order.getCode().substring(0, order.getCode().length() - 1),
                        amtn,
                        wxc);
                    dto.setWxPayData(BeanUtil.beanFrom(WxPayData.class, js));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
        }
        return dto;
    }
    
    private MktOrder updOrderOne(MktOrder order, OrderTotalV2Info dto, List<MktOrderLine> lines, String body,
        BigDecimal longitude, BigDecimal latitude)
    {
        order.setPayType(dto.getPayType());
        order.setPstime(dto.getPstime());
        if (!order.getDistributionType().equals(dto.getDistributionType()))
            throw TofocusException.of(LejiaErrCode.ORDER_DISTRIBUTIONTYPE_ERROR);
        //        order.setDistributionType(dto.getDistributionType());
        
        BigDecimal amto = BigDecimal.ZERO;
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal postageWeight = BigDecimal.ZERO;
        Boolean isPostage = true;
        Boolean isCard = false;
        Boolean flagCut = false;
        if (OrderType.CUT_ORDER.equals(order.getOrderType())) flagCut = true;
        
        List<Integer> gkeys = new ArrayList<>();
        List<Integer> skeys = new ArrayList<>();
        lines.forEach(e -> {
            gkeys.add(e.getGoods().intValue());
            skeys.add(e.getSpace().intValue());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(skeys);
        
        Map<MType, SysFarmerMtype> mapMType = null;
        Integer appid = MobileSession.appid();
        if ((Constant.Operation + appid).equals(order.getFarmer()))
        {
            order.setOrderOir(OrderOir.POINTS_MALL);
        }
        else
        {
            order.setOrderOir(OrderOir.MARKET_MALL);
            mapMType = sysFarmerMtypeDao.mapMType(order.getFarmer());
        }
        // 商品限购校验
        checkBugGoodsNumOrderLine(lines, goodsMap);
        BigDecimal reducePrice = BigDecimal.ZERO;
        for (MktOrderLine line : lines)
        {
            if (!goodsMap.containsKey(line.getGoods().intValue()))
            {
                throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
            }
            MktGoods goods = goodsMap.get(line.getGoods().intValue());
            body = body + line.getGoodsName() + " ";
            MktGoodsSpace space = spaceMap.get(line.getSpace().intValue());
            line.setPrice(space.getPriceOld());
            if (Boolean.TRUE.equals(flagCut)) line.setPrice(order.getAmto());
            
            // 判断商品的配送方式和市场设置的 是否有冲突
            checkMtypeGoods(goods, mapMType, dto.getDistributionType());
            
            BigDecimal num = new BigDecimal(line.getNum());
            if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0
                && MobileSession.member().getLevel().equals(LevelType.PAID_MEMBER))
            {
                line.setPricen(space.getPriceMember());
                if (space.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    BigDecimal subtract = space.getPrice().subtract(space.getPriceMember());
                    reducePrice = reducePrice.add(subtract.multiply(num));
                }
            }
            else
            {
                line.setPricen(space.getPrice());
            }
            line.setCouponPrice(line.getPricen());
            line.setCouponAmt(line.getPricen().multiply(num));
            if (goods.getMType().equals(MType.MARKET_GOODS)) isCard = true;
            if (goods.getMType().equals(MType.CUT_GOODS))
                amto = amto.add(line.getPrice().multiply(num));
            else
            {
                amto = amto.add(line.getPricen().multiply(num)).setScale(2);
            }
            weight = weight.add(space.getWeight().multiply(num));
            if ((DistributionType.IMMEDIATELY.equals(order.getDistributionType())
                || DistributionType.ORDERED.equals(order.getDistributionType())) && goods.getIsPostage() != null
                && !goods.getIsPostage())
            {
                postageWeight = postageWeight.add(space.getWeight().multiply(num));
                isPostage = false;
            }
        }
        order.setWeight(weight);
        order.setAmto(amto);
        order.setReducePrice(reducePrice);
        
        SysFarmerConfig config = sysFarmerConfigDao.get(order.getFarmer());
        BigDecimal postage = BigDecimal.ZERO;
        if (!isPostage)
        {
            if (config == null || config.getPkey().equals(Constant.Operation + appid)
                || config.getDistributionConfig() == null || Boolean.TRUE.equals(config.getDistributionConfig()))
            {
                dto.setPostage(BigDecimal.ZERO);
                dto.setWeight(postageWeight);
                loadPostage(dto, config, postageWeight, amto.subtract(reducePrice));
                postage = dto.getPostage();
            }
            else
            {
                postage = loadPostageFee(config, amto.subtract(reducePrice));
            }
        }
        order.setOldPostage(postage);
        order.setPostage(postage);
        log.info("ins_order_postage: {}", order.getPostage());
        if (dto.getDistributionType() != null && DistributionType.PICKUP.equals(dto.getDistributionType()))
            order.setAmtall(order.getAmto());
        else
        {
            order.setAmtall(order.getAmto().add(order.getPostage()));
        }
        if (order.getOrderType().equals(OrderType.MARKET_ORDER) || order.getOrderType().equals(OrderType.GIFT_ORDER))
            order.setPointn(dto.getPointn());
        else
            order.setPointn(0);
        order.setCommn(dto.getCommn());
        if (isCard) order.setCard(dto.getCard());
        // TODO 2022-07-21 注释掉  
        //        order.setCutAmt(BigDecimal.ZERO);
        
        if (dto.getCardPostage() != null && postage.compareTo(BigDecimal.ZERO) > 0)
        {
            //            MktMemberCard card = checkCardPostage(dto.getMember(), order.getFarmer(), dto.getCardPostage(), postage);
            MktMemberCard card = checkCard(goodsMap,
                spaceMap,
                dto.getMember(),
                order.getCardPostage(),
                lines,
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
                        order.setCardPostageAmt(postage);
                    }
                    else
                        order.setCardPostageAmt(card.getCost());
                    order.setPostage(postage);
                }
            }
        }
        if (DistributionType.DINE_IN.equals(dto.getDistributionType())) order.setPostage(BigDecimal.ZERO);
        
        if (order.getCard() != null)
        {
            // 校验该卡券是否可用
            MktMemberCard checkCard = checkCard(goodsMap,
                spaceMap,
                dto.getMember(),
                order.getCard(),
                lines,
                order.getFarmer(),
                order.getDistributionType(),
                CardCouponType.GOODS_COUPON);
            order.setCardAmt(checkCard.getCost());
        }
        else
        {
            order.setCardAmt(BigDecimal.ZERO);
        }
        BigDecimal subtract = order.getAmtall().subtract(order.getCardAmt()).subtract(order.getCardPostageAmt());
//        if (subtract.compareTo(BigDecimal.ZERO) <= 0) subtract = new BigDecimal(0.01);
        order.setAmtn(subtract);
        order = orderDao.update(order);
        if (order.getCardAmt().compareTo(BigDecimal.ZERO) > 0)
        {
            calculateCouponAmt(lines, order.getCardAmt());
        }
//        if (lines.size() == 1)
//        {
//            MktOrderLine orderLine = lines.get(0);
//            if (orderLine.getCouponAmt().compareTo(BigDecimal.ZERO) == 0)
//                orderLine.setCouponAmt(new BigDecimal("0.01"));
//        }
        orderLineDao.updateAll(lines);
        MktOrderDesc desc = new MktOrderDesc();
        desc.setPkey(order.getPkey());
        desc.setLatitude(BigDecimal.ZERO);
        desc.setLongitude(BigDecimal.ZERO);
        desc.setAscription(appid);
        Integer qrCode = MobileSession.qrCode();
        if (DistributionType.DINE_IN.equals(dto.getDistributionType()))
        {
            if (dto.getAddr() != null && dto.getAddr().getPkey() != null)
            {
                MktDesktop mktDesktop = desktopDao.get(dto.getAddr().getPkey());
                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
            }
            else if (qrCode != null)
            {
                MktDesktop mktDesktop = desktopDao.get(qrCode);
                if (mktDesktop != null) desc.setAddr(mktDesktop.getName());
            }
            MktMember member = memberDao.get(MobileSession.memberPkey());
            if(member != null)
            {
                desc.setName(member.getName());
                desc.setMobile(member.getMobile());
            }
            desc.setDistance(BigDecimal.ZERO);
        }
        else if (dto.getAddr() != null)
        {
            MktAddr addrObj = addrDao.get(dto.getAddr().getPkey());
            desc.setAddr(addrObj.getAddr());
            if (StringUtils.isNotBlank(addrObj.getAddrDetail())) desc.setAddr(desc.getAddr() + addrObj.getAddrDetail());
            desc.setName(addrObj.getName());
            desc.setMobile(addrObj.getMobile());
            desc.setLatitude(addrObj.getLatitude());
            desc.setLongitude(addrObj.getLongitude());
            desc.setRemark(dto.getRemark());
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
        orderDescDao.put(desc);
        return order;
    }
    
    public void checkMtypes(OrderTotalV2Info dto, SysFarmer farmer, MType mtype)
    {
        List<SysFarmerMtype> types = farmer.getTypes();
        if (types == null || types.isEmpty())
        {
            dto.setDelivery(true);
            dto.setPickup(true);
            return;
        }
        Map<MType, SysFarmerMtype> map = new HashMap<MType, SysFarmerMtype>();
        for (SysFarmerMtype m : types)
        {
            map.put(m.getMType(), m);
        }
        switch (mtype.getIndex())
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 8:
                SysFarmerMtype fm = map.get(MType.MARKET_GOODS);
                dto.setDelivery(fm.getDelivery());
                dto.setPickup(fm.getPickup());
                break;
            case 5:
                SysFarmerMtype fmc = map.get(MType.CUT_GOODS);
                dto.setDelivery(fmc.getDelivery());
                dto.setPickup(fmc.getPickup());
                break;
            case 6:
                SysFarmerMtype fmco = map.get(MType.COLLAGE_GOODS);
                dto.setDelivery(fmco.getDelivery());
                dto.setPickup(fmco.getPickup());
                break;
            case 7:
                SysFarmerMtype fmp = map.get(MType.PRESALE_GOODS);
                dto.setDelivery(fmp.getDelivery());
                dto.setPickup(fmp.getPickup());
                break;
            case 0:
            case 9:
            case 10:
            case 12:
                dto.setDelivery(false);
                dto.setPickup(false);
                break;
            case 11:
                dto.setDelivery(true);
                dto.setPickup(true);
                break;
            default:
                SysFarmerMtype fmd = map.get(mtype);
                if(fmd != null)
                {
                    dto.setDelivery(fmd.getDelivery());
                    dto.setPickup(fmd.getPickup());
                }
                break;
        }
    }
    
    public void checkMtypesGwc(OrderTotalV2Info dto, SysFarmer farmer)
    {
        List<OrderV2Info> info = dto.getPointInfo();
        List<OrderV2Info> farmerInfo = dto.getFarmerInfo();
        if (info != null && !info.isEmpty() && (farmerInfo == null || farmerInfo.isEmpty()))
        {
            checkMtypes(dto, farmer, MType.INTEGRAL_GOODS);
        }
        else if ((info == null || info.isEmpty()) && farmerInfo != null && !farmerInfo.isEmpty())
        {
            checkMtypes(dto, farmer, MType.MARKET_GOODS);
        }
        else if (info != null && !info.isEmpty() && farmerInfo != null && !farmerInfo.isEmpty())
        {
            checkMtypes(dto, farmer, MType.MARKET_GOODS);
            if (!dto.getDelivery()) throw TofocusException.of(LejiaErrCode.GWC_DELIVERY_PICKUP_ERROR);
        }
    }
    
    public void checkMtypeGoods(MktGoods goods, Map<MType, SysFarmerMtype> mapMType, DistributionType dt)
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
    
    public GoodsGiftInfo getGoodsGiftInfo(Integer vendorKey, Integer goodsKey, SysFarmer farmer)
    {
        MktVendor vendor = vendorDao.get(vendorKey);
        MktGoodsGift gift = goodsGiftDao.getByGoods(goodsKey);
//        MktGoodsGift gift = goodsGiftDao.get(goodsKey);
        GoodsGiftInfo giftInfo = new GoodsGiftInfo();
//        MktGoods goods = goodsDao.get(goodsKey);
        giftInfo.setStartDate(gift.getStartDate());
        giftInfo.setEndDate(gift.getEndDate());
        Integer appid = MobileSession.appid();
        if ((Constant.Operation + appid).equals(farmer.getPkey()))
        {
            giftInfo.setFarmerName("积分市场");
            giftInfo.setAddress(vendor.getAddr());
        }
        else
        {
            giftInfo.setFarmerName(farmer.getName());
            giftInfo.setAddress(farmer.getConfig().getAddr());
        }
        giftInfo.setVerdorName(vendor.getDisplayName());
        giftInfo.setVerdorMobile(vendor.getMobile());
        return giftInfo;
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
    
}
