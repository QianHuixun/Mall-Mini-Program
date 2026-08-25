package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.bean.dto.EnumNameDTO;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.AppWxErrMsgDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsPriceDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderMainDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderParamDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorParamDTO;
import cn.tofocus.lejia.bean.dto.order.RevokeDTO;
import cn.tofocus.lejia.bean.dto.order.RevokeMainDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementDetailDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementMainDTO;
import cn.tofocus.lejia.bean.dto.order.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.order.VendorOrderOnList;
import cn.tofocus.lejia.bean.dto.order.VendorOrderReport;
import cn.tofocus.lejia.bean.dto.order.VendorOrderReportLine;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsPresale;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.market.MktMarketCourier;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorGoods;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrderPackingCharge;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPackingCharge;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.DataEnums;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PointType;
import cn.tofocus.lejia.bean.enums.PriceStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.VendorOrderType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsPresaleDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.market.MktMarketCourierDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktSupplyDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorGoodsDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderPackingChargeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPackingChargeDao;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VendorOrderManager
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private VendorWalletUpdManager vendorWalletManager;
    
    @Autowired
    private MktVendorGoodsDao vendorGoodsDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktMarketCourierDao marketCourierDao;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private RedisCounter counter;
    
    /**
     * 商品供应库的dao层
     */
    @Resource
    private MktSupplyDao mktSupplyDao;
    
    /**
     * sys_config表 管理类
     */
    @Resource
    private SysConfigManager sysConfigManager;
    
    /**
     * dto增强类
     */
    @Resource
    private DtoEnhance dtoEnhance;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private OrderRefundManager orderRefundManager;
    
    @Autowired
    private MktVendorPackingChargeDao vendorPackingChargeDao;
    
    @Autowired
    private MktVendorOrderPackingChargeDao vendorOrderPackingChargeDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    /*
     * 读取订单信息
     */
    public List<MktVendorOrderDTO> queryOrder(int pkey)
    {
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        List<MktVendorOrderDTO> reList = new ArrayList<>();
        MktOrder order = orderDao.get(pkey);
        for (MktOrderLine line : lineList)
        {
            MktVendorOrderDTO dto = new MktVendorOrderDTO();
            dto.setOrderPkey(pkey);
            dto.setOrderLinePkey(line.getPkey());
            dto.setGoods(line.getGoods().intValue());
            dto.setSpace(line.getSpace().intValue());
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace().intValue());
            if (space != null) dto.setSpaceName(space.getSpace());
            dto.setGoodsName(line.getGoodsName());
            dto.setNum(line.getNum());
            dto.setFarmer(order.getFarmer());
            dto.setCompany(order.getCompany());
            dto.setOrderPrice(line.getPricen());
            dto.setPrice(line.getPricen());
            reList.add(dto);
        }
        return reList;
    }
    
    public List<MktVendorGoodsDTO> loadVendor(int goodsPkey)
    {
        List<MktVendorGoods> vglist = vendorGoodsDao.select()
            .eq("goods", goodsPkey)
            .eq("farmer", CurrentSession.marketPkey())
            .sort("updateTime", true)
            .exec();
        List<Integer> vlist1 = new ArrayList<>();
        List<MktVendorGoodsDTO> rsList = new ArrayList<>();
        for (MktVendorGoods line : vglist)
        {
            MktVendorGoodsDTO dto = new MktVendorGoodsDTO();
            vlist1.add(line.getVendor());
            dto.setVendor(line.getVendor());
            if (dto.getVendor().intValue() == 0)
            {
                continue;
            }
            else
            {
                dto.setVendorName(vendorDao.get(dto.getVendor()).getDisplayName() + "*");
            }
            dto.setPrice(line.getPrice());
            rsList.add(dto);
        }
        SelectBuilder<Integer, MktVendor> build =
            vendorDao.select().eq("idDel", false).eq("farmer", CurrentSession.marketPkey());
        if (!vlist1.isEmpty())
        {
            build.notIn("pkey", vlist1.toArray());
        }
        List<MktVendor> vlist2 = build.exec();
        for (MktVendor line : vlist2)
        {
            MktVendorGoodsDTO dto = new MktVendorGoodsDTO();
            dto.setVendor(line.getPkey());
            dto.setVendorName(line.getDisplayName());
            rsList.add(dto);
        }
        return rsList;
    }
    
    public List<MktVendorGoodsPriceDTO> loadVendorV2(int goodsPkey)
    {
        List<MktSupply> exec = mktSupplyDao.select().eq("space", goodsPkey).exec();
        List<MktVendorGoodsPriceDTO> res = new ArrayList<>();
        MktVendorGoodsPriceDTO zc = new MktVendorGoodsPriceDTO();
        zc.setVendor(0);
        zc.setVendorName("自采");
        exec.forEach(e -> {
            MktVendorGoodsPriceDTO d = new MktVendorGoodsPriceDTO();
            d.setPrice(e.getPurchasingPrice());
            d.setVendor(e.getVendor());
            if (e.getVendor() != null && e.getVendor() != 0)
            {
                MktVendor vendor = vendorDao.get(e.getVendor());
                if (vendor != null) d.setVendorName(vendor.getDisplayName());
            }
            res.add(d);
        });
        return res;
    }
    
    /**
     * 自动采购逻辑
     * @param orderPkey 订单id
     */
    @Transactional(rollbackOn = Throwable.class)
    public Boolean autoPurchase(MktOrder order)
    {
        boolean result = false;
//        MktOrder order = orderDao.get(orderPkey);
        
        String farmerPkey = order.getFarmer();
        Integer appid = MobileSession.appid();
        if (appid == null) appid = CurrentSession.ascriptionPkey();
        if (Objects.nonNull(farmerPkey) && !(Constant.Operation + appid).equals(farmerPkey))
        {
            // 获取采购信息
            List<MktVendorOrderDTO> mktVendorOrders = queryOrder(order.getPkey());
            if (CollectionUtils.isEmpty(mktVendorOrders))
            {
                throw TofocusException.of(LejiaErrCode.NOT_FIND_VENDOR_ORDER);
            }
            int size = mktVendorOrders.size();
            
            // 获取存在的商品pkey列表
            List<Integer> goodsPkeyList = mktVendorOrders.stream()
                .map(MktVendorOrderDTO::getGoods)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            List<MktSupply> mktSupplyList = mktSupplyDao.select()
                .eq("farmer", farmerPkey)
                .in("good", goodsPkeyList)
                .eq("enabled", true)
                .sort("sort", false)
                .exec();
            // 供应商pkey -> name 映射关系型
            Map<Integer, String> vendorPkeyNameMap = vendorDao.select()
                .eq("ascription", CurrentSession.ascriptionPkey())
                .exec()
                .stream()
                .collect(Collectors.toMap(MktVendor::getPkey, MktVendor::getName));
            
            List<MktVendorOrderDTO> newVendorOrders = new ArrayList<>();
            mktVendorOrders.forEach(vendorOrder -> {
                // 当前商品的供应库数据
                List<MktSupply> goodSupplyList = new ArrayList<>();
                for (int i = 0; i < mktSupplyList.size(); i++)
                {
                    MktSupply supply = mktSupplyList.get(i);
                    // 当前商品
                    if (vendorOrder.getGoods().equals(supply.getGood()))
                    {
                        try
                        {
                            int spacePkey = Integer.parseInt(supply.getSpace());
                            // 规格也要匹配
                            if (vendorOrder.getSpace().equals(spacePkey))
                            {
                                // 必须深拷贝，否则后续会修改原列表的数据
                                goodSupplyList.add(supply.clone());
                            }
                        }
                        catch (Exception e)
                        {
                            throw TofocusException.of(LejiaErrCode.DEEP_CLONE_FAILURE);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(goodSupplyList))
                {
                    // 轮到自动采购的索引
                    int index = 0;
                    for (int i = 0; i < goodSupplyList.size(); i++)
                    {
                        MktSupply supply = goodSupplyList.get(i);
                        // 主要设置 vendor、price、amt、remark
                        if (supply.getFlag())
                        {
                            MktVendorOrderDTO newMktVendorOrder =
                                BeanUtil.beanFrom(MktVendorOrderDTO.class, vendorOrder);
                            newMktVendorOrder.setVendor(supply.getVendor());
                            if (supply.getPurchasingPrice() != null)
                                newMktVendorOrder.setPrice(supply.getPurchasingPrice());
                            newMktVendorOrder.setAmt(
                                newMktVendorOrder.getPrice().multiply(new BigDecimal(newMktVendorOrder.getNum())));
                            
                            log.info("========================系统自动采购======================== start");
                            log.info("系统自动采购简单信息：订单号：{}，商品pkey：{}，商品名：{}，供应商pkey：{}，供应商名称：{}",
                                order.getPkey(),
                                vendorOrder.getGoods(),
                                vendorOrder.getGoodsName(),
                                newMktVendorOrder.getVendor(),
                                vendorPkeyNameMap.get(newMktVendorOrder.getVendor()));
                            log.info("系统自动采购详细信息：{}", newMktVendorOrder);
                            log.info("========================系统自动采购======================== end");
                            newVendorOrders.add(newMktVendorOrder);
                            
                            // 记录一下索引
                            index = i;
                        }
                    }
                    
                    // goodSupplyList的size大于1，修改flag
                    if (goodSupplyList.size() > 1)
                    {
                        for (int i = 0; i < goodSupplyList.size(); i++)
                        {
                            goodSupplyList.get(i).setFlag(false);
                            if (index < goodSupplyList.size() - 1)
                            {
                                // 修改下一条标记为true
                                goodSupplyList.get(index + 1).setFlag(true);
                            }
                            // 如果是最后一项
                            else
                            {
                                // 第一条标记为true
                                goodSupplyList.get(0).setFlag(true);
                            }
                        }
                    }
                    mktSupplyDao.updateAll(goodSupplyList);
                }
            });
            if (!newVendorOrders.isEmpty() && size == newVendorOrders.size())
            {
                order.setPurchaseStatus(PurchaseStatus.PURCHASEING);
                orderDao.update(order);
                // 设置完数据，进入采购逻辑（订单pkey和设置过的vendor_order市场列表）
                List<MktVendorOrder> voList = checkOrder(order.getPkey(), newVendorOrders);
                // 采购结束 打印小票
                orderManager.printOrderWx(order.getPkey(), true, voList);
            }
            result = true;
        }
        return result;
    }
    
    /**
     * 重置商品供应库顺序
     */
    @Transactional(rollbackOn = Throwable.class)
    public Boolean resetSupplyOrder(Integer ascription)
    {
        boolean result = false;
        List<MktSupply> list = mktSupplyDao.select().eq("ascription", ascription).exec();
        if (CollectionUtils.isNotEmpty(list))
        {
            // 将所有的flag改为false
            list.forEach(item -> {
                item.setFlag(false);
            });
            mktSupplyDao.updateAll(list);
            
            // 去重的所有商品规格
            Set<String> spaces = list.stream().map(MktSupply::getSpace).collect(Collectors.toSet());
            
            List<MktSupply> updateTrue = new ArrayList<>();
            for (String space : spaces)
            {
                // 升序完取最第一项
                List<MktSupply> afterSortList = list.stream()
                    .filter(mktSupply -> mktSupply.getEnabled() && space.equals(mktSupply.getSpace()))
                    .sorted(Comparator.comparing(MktSupply::getSort))
                    .collect(Collectors.toList());
                
                if (CollectionUtils.isNotEmpty(afterSortList))
                {
                    updateTrue.add(afterSortList.get(0));
                }
            }
            if (CollectionUtils.isNotEmpty(updateTrue))
            {
                // 将每个商品sort号最小的数据置为true
                updateTrue.forEach(item -> {
                    item.setFlag(true);
                });
                mktSupplyDao.updateAll(updateTrue);
            }
        }
        result = true;
        return result;
    }
    
    /**
             * 采购
     * @param orderPkey     订单id
     * @param lines         订单列表
     */
    @Transactional(rollbackOn = Throwable.class)
    public List<MktVendorOrder> checkOrder(int orderPkey, List<MktVendorOrderDTO> lines)
    {
        // 获取订单信息
        MktOrder order = orderDao.get(orderPkey);
        String code = order.getCode();
        Long increment = counter.increment("zyysc", "order", code);
        counter.expire("zyysc", "order", code, 86400);
        if (increment != 1)
        {
            log.info("订单回调重复：{}", code);
            return null;
        }
        List<MktVendorOrder> res = purchaseOrder(order, lines);
        return res;
    }
    
    /**
     * 采购
    * @param orderPkey     订单id
    * @param lines         订单列表
    */
//    @Transactional(rollbackOn = Throwable.class)
    public void checkOrderUnlimited(int orderPkey, List<MktVendorOrderDTO> lines)
    {
        // 获取订单信息
        MktOrder order = orderDao.get(orderPkey);
        purchaseOrder(order, lines);
    }
    
    private List<MktVendorOrder> purchaseOrder(MktOrder order, List<MktVendorOrderDTO> lines)
    {
        List<MktVendorOrder> res = new ArrayList<>();
        // 订单类型
        OrderType orderType = order.getOrderType();
        // 获取微信公众号账号实体
        Integer ascription = order.getAscription();
        String farmerPkey = order.getFarmer();
        MktMember member = memberDao.get(order.getMember());
        
        
        SysFarmerConfig config = sysFarmerConfigDao.get(farmerPkey);
        Boolean mandatory = false;
        if (order.getAmtall().compareTo(order.getCardAmt()) == 0) mandatory = true;
        int i = 0;
        Map<Integer,List<MktVendorOrder>> map = new HashMap<>();
        for (MktVendorOrderDTO dto : lines)
        {
            // 商户订单DTO -> 商户订单实体
            MktVendorOrder bean = BeanUtil.beanFrom(MktVendorOrder.class, dto);
            if (Boolean.TRUE.equals(mandatory) && i == 0)
            {
                bean.setDifference(new BigDecimal("0.01"));
            }
            i++;
            bean.setAscription(ascription);
            // 判断  采购价是否超过商品单价
            MktOrderLine line = orderLineDao.get(dto.getOrderLinePkey());
            BigDecimal spacePrice = line.getPricen();
            bean.setRecommendPrice(bean.getPrice());
            if (bean.getPrice().compareTo(spacePrice) > 0)
            {
                bean.setPrice(spacePrice);
                bean.setPriceStatus(PriceStatus.ABNORMAL);
            }
            
            // 计算总价、佣金费率、交易佣金和结算金额
            // 总价 = 价格 X 数量
            BigDecimal numBig = new BigDecimal(bean.getNum());
            bean.setTotalPrice(spacePrice.multiply(numBig));
            
            Integer vendorPkey = dto.getVendor();
            // 商户
            MktVendor vendor = vendorDao.get(bean.getVendor());
            if (vendor == null)
            {
                vendor = new MktVendor();
            }
            boolean flag = vendorPkey == 0 || Objects.isNull(vendor.getSettlementMethod())
                || SettlementMethodType.PURCHASE_SETTLEMENT.equals(vendor.getSettlementMethod());
            log.info("采购-flag: {}", flag);
            bean.setCommissionType(config.getCommissionType());
            // 采购价结算/商户不存在-自采
            if (flag)
            {
                bean.setCommissionRate(BigDecimal.ZERO);
                BigDecimal sub = spacePrice.subtract(bean.getPrice());
                bean.setCommissions(sub.multiply(numBig));
                // 采购金额 = 总价
                bean.setAmt(bean.getPrice().multiply(numBig));
                bean.setTotalPrice(bean.getAmt());
            }
            else
            {
                bean.setRecommendPrice(dto.getOrderPrice());
                // 佣金费率
                MktSupply supply = mktSupplyDao.getSupply(vendor.getPkey(), bean.getSpace());
                BigDecimal cr1 = supply.getCommissionRate1();
                if (cr1 == null) cr1 = supply.getCommissionRate2();
                if (cr1 == null) throw TofocusException.of(LejiaErrCode.SETTLEMENTMETHODTYPE_COMMISSION_ERROR);
                bean.setCommissionRate(cr1);
                
                // 交易佣金数 = 总价 X 佣金费率（数据库没有记录百分号，所以要除100）
                BigDecimal comissions =
                    spacePrice.multiply(numBig).multiply(cr1).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                bean.setCommissions(comissions);
                // 民营企业抽成处理
                if(Boolean.TRUE.equals(config.getIsEnterprise()) && bean.getCommissionRate().compareTo(BigDecimal.ZERO) > 0)
                {
                    if(Boolean.TRUE.equals(member.getIsActivity()))
                    {
                        if(config.getMemberCommissionRate() == null)
                            bean.setSysCommissionRate(config.getCommissionRate());
                        else
                            bean.setSysCommissionRate(config.getMemberCommissionRate());
                    }
                    else
                        bean.setSysCommissionRate(config.getCommissionRate());
                    bean.setMarketCommissionRate(bean.getCommissionRate());
                    if(bean.getSysCommissionRate() != null)
                    {
                        bean.setMarketCommissionRate(bean.getCommissionRate().subtract(bean.getSysCommissionRate()));
                    }
                    BigDecimal marketComissions =
                        spacePrice.multiply(numBig).multiply(bean.getMarketCommissionRate())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    bean.setMarketCommissions(marketComissions);
                    if(bean.getSysCommissionRate() != null)
                    {
//                        BigDecimal sysComissions =
//                            spacePrice.multiply(numBig).multiply(bean.getSysCommissionRate())
//                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        bean.setSysCommissions(bean.getCommissions().subtract(bean.getMarketCommissions()));
                    }
                }
                else
                {
                    bean.setSysCommissionRate(bean.getCommissionRate());
                    bean.setSysCommissions(bean.getCommissions());
                }
                
                BigDecimal subtract = bean.getTotalPrice().subtract(comissions);
                // 采购金额 = 总价-交易佣金（结算金额小于0的设置为0）
                BigDecimal amt = subtract.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract;
                bean.setAmt(amt);
                
                BigDecimal divide = spacePrice.multiply(cr1).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                bean.setPrice(spacePrice.subtract(divide));
                bean.setTotalPrice(amt);
//                bean.setTotalPrice(bean.getPrice().multiply(numBig));
            }
            // 采购状态为采购中
            bean.setPurchaseStatus(PurchaseStatus.PURCHASEING);
            bean.setStatus(SettlementType.NOT_START);
            bean.setRowVension(1);
            // 订单类型
            bean.setType(orderType);
            
            // 预售配送时间
            MktGoodsPresale presale = goodsPresaleDao.get(bean.getGoods());
            if (presale != null)
            {
                bean.setStartDate(presale.getStartDate());
                bean.setEndDate(presale.getEndDate());
            }
            
            // 商品规格
            MktGoodsSpace goodsSpace = goodsSpaceDao.get(bean.getSpace());
            if (goodsSpace != null)
            {
                // 查询商品原价和毛重  毛重存 单个的毛重
                bean.setGoodsPrice(goodsSpace.getPrice());
                bean.setWeight(goodsSpace.getWeight());
            }
            MktOrderLine orderLine = orderLineDao.get(bean.getOrderLinePkey());
            if (orderLine != null)
            {
                bean.setGoodsPrice(orderLine.getPricen());
            }
            if (StringUtils.isBlank(CurrentSession.marketPkey()))
            {
                SysFarmer farmer = MobileSession.farmer();
                if (farmer != null)
                    bean.setUpdateBy(farmer.getManagerUser());
                else
                    bean.setUpdateBy(-1);
            }
            if (bean.getGoodsPrice() != null && bean.getPrice() != null)
            {
                BigDecimal subtract = bean.getGoodsPrice().subtract(bean.getPrice());
                BigDecimal difference = subtract.multiply(BigDecimal.valueOf(bean.getNum()));
                if (bean.getDifference() != null)
                    bean.setDifference(bean.getDifference().add(difference));
                else
                    bean.setDifference(difference);
            }
            // DTO列表中的订单号
//            MktOrder mktOrder = orderDao.get(dto.getOrderPkey());
            bean.setEndDate(order.getCreatedTime());
            // 新增商户订单数据
            MktVendorOrder add = vendorOrderDao.add(bean);
            res.add(add);
            if(!map.containsKey(add.getVendor()))
            {
                map.put(add.getVendor(), new ArrayList<MktVendorOrder>());
            }
            map.get(add.getVendor()).add(add);
            // 商户
            log.info("商户采购微信小程序消息推送: {}", JsonUtil.toString(vendor, true));
            // 获取openid
            try
            {
                if (vendor.getOpenid2() != null)
                {
                    AccountEntity wxAccount = wxManager.getAccountEntity(AccountType.VENDOR, order.getAscription());
                    SysConfigEntity sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_VENODR, ascription);
                    if (sysConfig != null)
                    {
                        sendWxMsgVendorYs(bean,
                            order.getCode(),
                            DateUtils.format(order.getCreatedTime()),
                            vendor.getOpenid2(),
                            wxAccount.getAccountAppid(),
                            sysConfig.getValue(),
                            order.getAscription());
                    }
                }
            }
            catch (Exception e)
            {
                log.info("发送微信推送失败，具体报错内容:  {}", e.getMessage());
            }
            // 查询当前商品当前商户的供应商商品信息
            MktVendorGoods vg =
                vendorGoodsDao.selectOne().eq("goods", bean.getGoods()).eq("vendor", bean.getVendor()).exec();
            // 不存在则新增，否则更新
            if (vg == null)
            {
                vg = new MktVendorGoods();
                vg.setGoods(bean.getGoods());
                vg.setPrice(bean.getPrice());
                vg.setVendor(bean.getVendor());
                vg.setFarmer(order.getFarmer());
                vg.setCompany(order.getCompany());
                vendorGoodsDao.add(vg);
            }
            else
            {
                vg.setUpdateTime(new Date());
                vg.setPrice(bean.getPrice());
                vendorGoodsDao.update(vg);
            }
        }
        // 计算打包费用
        SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(order.getFarmer());
        if(Boolean.TRUE.equals(farmerConfig.getIsPackingCharge()))
        {
            handlePackingCharge(map, order, false);
        }
        if(!qfAscription.equals(order.getAscription()))
        {
            for(Entry<Integer, List<MktVendorOrder>> entry : map.entrySet())
            {
                for(MktVendorOrder bean : entry.getValue())
                {
                    BigDecimal amt = bean.getAmt();
//                    if(CommissionType.MERCHANT.equals(bean.getCommissionType())
//                        && bean.getPayComm() != null
//                        && bean.getPayComm().compareTo(BigDecimal.ZERO) > 0)
//                    {
//                        amt = amt.subtract(bean.getPayComm());
//                    }
                    // 增加商户钱包明细
                    vendorWalletManager.updWalletLockAmount(bean
                        .getVendor(), amt, true, VendorWalletSource.CONSUME, order.getCode(), order.getCreatedTime());
                }
            }
        }
        
        // 支付类型-微信
        order.setCgCheck(1);
        // 订单采购状态-采购中
        order.setPurchaseStatus(PurchaseStatus.PURCHASEING);
        // 订单采购金额合计
        // 计算采购价格
        Number sum = vendorOrderDao.aggregation()
            .eq("orderPkey", order.getPkey())
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .execSum("amt");
        order.setPurchaseAmt(new BigDecimal(sum.toString()));
        orderDao.update(order);
        orderRefundManager.vendorOrderRefund(order.getPkey());
        // 处理手续费问题
        runVendorOrderPayComm(order);
        return res;
    }

    // 计算手续费
    private Boolean runVendorOrderPayComm(MktOrder order)
    {
        BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
        BigDecimal orderAmt = order.getAmtn();
        if(order.getRefundAmt() != null)
            orderAmt = orderAmt.subtract(order.getRefundAmt());
        BigDecimal payCommission = orderAmt.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);

        List<MktVendorOrder> list = vendorOrderDao.listOrder(order.getPkey());
//        List<MktVendorOrder> list = vendorOrderDao.select()
//            .eq("orderPkey", order.getPkey())
//            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
//            .exec();
        // 计算配送费和优惠券分配
        BigDecimal sum = BigDecimal.ZERO;
        for(MktVendorOrder vo : list)
        {
            sum = sum.add(vo.getTotalPrice());
        }
        if(sum.compareTo(BigDecimal.ZERO) > 0)
        {
            distributePayComm(list, order.getPostage(), order.getCardAmt(), payCommission, sum);
        }
        else
        {
            for(int i = 0; i < list.size(); i++)
            {
                MktVendorOrder vo = list.get(i);
                if(i != list.size() - 1)
                {
                    vo.setPayComm(BigDecimal.ZERO);
                }
                else
                {
                    vo.setPayComm(payCommission);
                }
            }
        }
        vendorOrderDao.updateAll(list);
        
        if(qfAscription.equals(order.getAscription()))
        {
            for(MktVendorOrder bean : list)
            {
                BigDecimal amt = bean.getAmt();
                if(CommissionType.MERCHANT.equals(bean.getCommissionType())
                    && bean.getPayComm() != null
                    && bean.getPayComm().compareTo(BigDecimal.ZERO) > 0)
                {
                    amt = amt.subtract(bean.getPayComm());
                }
                // 2026-05-09 ZDW 防止 抽佣100% 手续费还需要商户承担 出现负数  bug出现 文件表 主键 434
                if(amt.compareTo(BigDecimal.ZERO) > 0)
                {
                    // 增加商户钱包明细
                    vendorWalletManager.updWalletLockAmount(bean
                        .getVendor(), amt, true, VendorWalletSource.CONSUME, order.getCode(), order.getCreatedTime());
                }
            }
        }
        
        return true;
    }
    
    /**
     * 拆分手续费
     * @param list 商户订单列表
     * @param postage 订单配送费
     * @param cardAmt 订单卡券优惠
     * @param payCommission 总手续费
     * @param sum 总价合计
     */
    public static void distributePayComm(List<MktVendorOrder> list, BigDecimal postage, BigDecimal cardAmt,
        BigDecimal payCommission, BigDecimal sum)
    {
        // 处理优惠金额和邮费分配（保持原逻辑）
        Boolean cardB = (cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) > 0);
        Boolean postageB = (postage != null && postage.compareTo(BigDecimal.ZERO) > 0);
        BigDecimal postageSurplus = postage;
        BigDecimal cardAmtSurplus = cardAmt;
        
        for (int i = 0; i < list.size(); i++)
        {
            MktVendorOrder vo = list.get(i);
            if (i != list.size() - 1)
            {
                if (Boolean.TRUE.equals(cardB))
                {
                    BigDecimal discountAmt = vo.getTotalPrice()
                        .divide(sum, 6, RoundingMode.HALF_UP)
                        .multiply(cardAmt)
                        .setScale(2, RoundingMode.HALF_UP);
                    vo.setDiscountAmt(discountAmt);
                    cardAmtSurplus = cardAmtSurplus.subtract(discountAmt);
                }
                if (Boolean.TRUE.equals(postageB))
                {
                    BigDecimal postageAmt = vo.getTotalPrice()
                        .divide(sum, 6, RoundingMode.HALF_UP)
                        .multiply(postage)
                        .setScale(2, RoundingMode.HALF_UP);
                    vo.setPostage(postageAmt);
                    postageSurplus = postageSurplus.subtract(postageAmt);
                }
            }
            else
            {
                vo.setDiscountAmt(cardAmtSurplus);
                vo.setPostage(postageSurplus);
            }
        }
        
        // 计算整个列表的总金额（用于组间分配）
        BigDecimal voSum = BigDecimal.ZERO;
        for (MktVendorOrder vo : list)
        {
            BigDecimal amt = vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO;
            BigDecimal commissions = vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO;
            voSum = voSum.add(amt).add(commissions);
        }
        
        // 按vendor分组
        Map<Integer, List<MktVendorOrder>> vendorGroupMap = new HashMap<>();
        for (MktVendorOrder vo : list)
        {
            vendorGroupMap.computeIfAbsent(vo.getVendor(), k -> new ArrayList<>()).add(vo);
        }
        
        // 计算每组的总金额
        List<Map.Entry<Integer, BigDecimal>> groupSums = new ArrayList<>();
        for (Map.Entry<Integer, List<MktVendorOrder>> entry : vendorGroupMap.entrySet())
        {
            BigDecimal groupSum = entry.getValue()
                .stream()
                .map(vo -> (vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO)
                    .add(vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            groupSums.add(new AbstractMap.SimpleEntry<>(entry.getKey(), groupSum));
        }
        
        // 组间分配 - 第一轮：去尾法分配
        List<Map.Entry<Integer, BigDecimal>> groupAllocations = new ArrayList<>();
        BigDecimal totalAllocated = BigDecimal.ZERO;
        
        // 计算每个组的应分配金额（去尾法）
        for (Map.Entry<Integer, BigDecimal> group : groupSums)
        {
            BigDecimal groupPayComm = group.getValue().multiply(payCommission).divide(voSum, 2, RoundingMode.DOWN);
            groupAllocations.add(new AbstractMap.SimpleEntry<>(group.getKey(), groupPayComm));
            totalAllocated = totalAllocated.add(groupPayComm);
        }
        
        // 计算剩余手续费
        BigDecimal remainingCommission = payCommission.subtract(totalAllocated);
        int remainingCents = remainingCommission.multiply(BigDecimal.valueOf(100)).intValue();
        
        // 第二轮分配：按商户总金额倒序分配剩余手续费（每次0.01元）
        // 按商户总金额倒序排序
        groupSums.sort((g1, g2) -> g2.getValue().compareTo(g1.getValue()));
        
        // 分配剩余手续费（每个商户每次分0.01元）
        for (Map.Entry<Integer, BigDecimal> group : groupSums)
        {
            if (remainingCents <= 0)
                break;
            
            // 找到该组的分配记录
            for (Map.Entry<Integer, BigDecimal> allocation : groupAllocations)
            {
                if (allocation.getKey().equals(group.getKey()))
                {
                    allocation.setValue(allocation.getValue().add(BigDecimal.valueOf(0.01)));
                    remainingCents--;
                    break;
                }
            }
        }
        
        // 组内分配
        for (Map.Entry<Integer, BigDecimal> allocation : groupAllocations)
        {
            Integer vendor = allocation.getKey();
            BigDecimal groupPayComm = allocation.getValue();
            List<MktVendorOrder> group = vendorGroupMap.get(vendor);
            
            // 计算组内总金额
            BigDecimal groupSum = group.stream()
                .map(vo -> (vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO)
                    .add(vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 组内按amt+commissions升序排序
            group.sort(Comparator.comparing(vo -> (vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO)
                .add(vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO)));
            
            // 组内分配，最后一行处理剩余
            BigDecimal groupPayCommRemain = groupPayComm;
            for (int j = 0; j < group.size(); j++)
            {
                MktVendorOrder vo = group.get(j);
                BigDecimal voSumItem = (vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO)
                    .add(vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO);
                
                BigDecimal voPayComm;
                if (j < group.size() - 1)
                {
                    // 非最后一行：按比例计算并四舍五入
                    voPayComm = voSumItem.multiply(groupPayComm).divide(groupSum, 2, RoundingMode.HALF_UP);
                    groupPayCommRemain = groupPayCommRemain.subtract(voPayComm);
                }
                else
                {
                    // 最后一行：直接使用剩余手续费
                    voPayComm = groupPayCommRemain;
                }
                vo.setPayComm(voPayComm);
            }
        }
    }
    
    // 天津采购,用于中信清分  清分的时候调
    public MktVendorOrder addMarketVendorOrder(Integer orderPkey)
    {
        List<MktVendorOrder> list = vendorOrderDao.select()
            .eq("orderPkey", orderPkey)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .isNotNull("marketCommissions")
            .exec();
        if(list == null || list.isEmpty())
            return null;
        
        MktVendorOrder mvo = new MktVendorOrder();
        mvo.setPkey(-5);
        mvo.setAmt(BigDecimal.ZERO);
        for(MktVendorOrder vo : list)
        {
            mvo.setAmt(mvo.getAmt().add(vo.getMarketCommissions()));
        }
        return mvo;
    }
    
    public void handlePackingCharge(Map<Integer,List<MktVendorOrder>> map, MktOrder order, Boolean packingChargeFlag)
    {
//        SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(order.getFarmer());
//        if(Boolean.FALSE.equals(farmerConfig.getIsPackingCharge()) || farmerConfig.getIsPackingCharge() == null)
//            return;
        List<MktVendorOrderPackingCharge> vopcList = new ArrayList<>();
        Map<Integer, List<MktVendorPackingCharge>> mapByVendors = vendorPackingChargeDao.mapByVendors(map.keySet());
        for(Entry<Integer, List<MktVendorOrder>> entry : map.entrySet())
        {
            if(mapByVendors.containsKey(entry.getKey()))
            {
                BigDecimal orderAmt = BigDecimal.ZERO;
                for(MktVendorOrder vo : entry.getValue())
                {
                    orderAmt = orderAmt.add(vo.getTotalPrice());
                    if(RefundStatus.REFUND_FINAL.equals(vo.getRefundStatus()) && vo.getProcureRefundAmt() != null)
                    {
                        orderAmt = orderAmt.subtract(vo.getProcureRefundAmt());
                    }
                }
                BigDecimal packingCharge = BigDecimal.ZERO;
                for(MktVendorPackingCharge vpc : mapByVendors.get(entry.getKey()))
                {
                    if(orderAmt.compareTo(vpc.getOrderAmt()) >= 0)
                    {
                        packingCharge = vpc.getPackingCharge();
                        if(vpc.getGrade().equals(3))
                            break;
                    }
                }
                // 添加打包费记录
                MktVendorOrderPackingCharge vopc = vendorOrderPackingChargeDao.byOrderAndVendor(order.getPkey(), entry.getKey());
                if(vopc == null)
                    vopc = new MktVendorOrderPackingCharge();
                vopc.setOrderPkey(order.getPkey());
                vopc.setCode(order.getCode());
                vopc.setPaymentTime(order.getCreatedTime());
                vopc.setAscription(order.getAscription());
                vopc.setVendor(entry.getKey());
                vopc.setOrderAmt(orderAmt);
                vopc.setPackingCharge(packingCharge);
                vopc.setAmt(vopc.getOrderAmt().subtract(vopc.getPackingCharge()));
                MktVendor vendor = vendorDao.get(entry.getKey());
                vopc.setDisplayName(vendor.getDisplayName());
                vopc.setBooth(vendor.getBooth());
                vopc.setFarmer(vendor.getFarmer());
                vopcList.add(vopc);
                if(vopc.getOrderAmt().compareTo(BigDecimal.ZERO) != 0)
                {
                    BigDecimal s = new BigDecimal("1");
                    for(int k = 0; k < entry.getValue().size(); k++)
                    {
                        MktVendorOrder vo = entry.getValue().get(k);
                        if(k == (entry.getValue().size() - 1))
                            vo.setPackingCharge(s.multiply(packingCharge).setScale(2, BigDecimal.ROUND_HALF_DOWN));
                        else
                        {
                            BigDecimal divide = BigDecimal.ZERO;
                            if(RefundStatus.REFUND_FINAL.equals(vo.getRefundStatus()) && vo.getProcureRefundAmt() != null)
                            {
                                divide = (vo.getTotalPrice().subtract(vo.getProcureRefundAmt())).divide(vopc.getOrderAmt(), 2, BigDecimal.ROUND_HALF_DOWN);
                            }
                            else
                                divide = vo.getTotalPrice().divide(vopc.getOrderAmt(), 2, BigDecimal.ROUND_HALF_DOWN);
                            vo.setPackingCharge(packingCharge.multiply(divide).setScale(2, BigDecimal.ROUND_HALF_DOWN));
                            s = s.subtract(divide);
                        }
                        vo.setAmt(vo.getTotalPrice().subtract(vo.getPackingCharge()));
                        if(RefundStatus.REFUND_FINAL.equals(vo.getRefundStatus()) && vo.getProcureRefundAmt() != null)
                        {
                            vo.setAmt(vo.getAmt().subtract(vo.getProcureRefundAmt()));
                        }
                    }
                    vendorOrderDao.updateAll(entry.getValue());
                    if(Boolean.TRUE.equals(packingChargeFlag))
                    {
                        // 撤销
                        vendorWalletManager.updWalletLockRevoke(entry.getKey(), order.getCode());
                        for(MktVendorOrder bean : entry.getValue())
                        {
//                            BigDecimal subtract;
//                            if(RefundStatus.REFUND_FINAL.equals(bean.getRefundStatus()) && bean.getRefundAmt() != null)
//                            {
//                                subtract = bean.getAmt().subtract(bean.getRefundAmt());
//                            }
//                            else
//                                subtract = bean.getAmt();
//                            if(subtract.compareTo(BigDecimal.ZERO) > 0)
                            BigDecimal amt = bean.getAmt();
                            if(CommissionType.MERCHANT.equals(bean.getCommissionType())
                                && bean.getPayComm() != null
                                && bean.getPayComm().compareTo(BigDecimal.ZERO) > 0)
                            {
                                amt = amt.subtract(bean.getPayComm());
                            }
                                // 增加商户钱包明细
                                vendorWalletManager.updWalletLockAmount(bean
                                    .getVendor(), amt, true, VendorWalletSource.CONSUME, order.getCode(), order.getCreatedTime());
//                                vendorWalletManager.updWalletLockAmount(bean
//                                    .getVendor(), subtract, true, VendorWalletSource.CONSUME, order.getCode());
                        }
                    }
                }
                else
                {
                    for(int k = 0; k < entry.getValue().size(); k++)
                    {
                        MktVendorOrder vo = entry.getValue().get(k);
                        vo.setPackingCharge(BigDecimal.ZERO);
                    }
                    vendorOrderDao.updateAll(entry.getValue());
                }
            }
        }
        vendorOrderPackingChargeDao.putAll(vopcList);
    }
    
    /**
     * 发送微信小程序推送
     * @param code      订单号
     * @param time      年月日时分秒
     * @param openid    商户的openid
     * @param goodsName 商品名称
     * @param amt       订单价格
     * @param memberName 名称
     * @param account    微信公众号账号实体
     */
    private void sendWeapp(String code, String time, String openid, String goodsName, BigDecimal amt, String memberName,
        AccountEntity account)
    {
        /*
         * 数据格式
         * {
         *      "character_string1":{
         *          "value":"xxx"
         *      },
         *      "date2":{
         *          "value":"xxx"
         *      },
         *      "thing7":{
         *          "value":"xxx"
         *      },
         *      "amount8":{
         *          "value":"xxx"
         *      },
         *      "thing4":{
         *          "value":"xxx"
         *      }
         * }
         */
        String templateId = "TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g";
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // 订单号
        jsonObject.put("value", code);
        data.put("character_string1", jsonObject);
        // 时间
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", time);
        data.put("date2", jsonObject2);
        // 商品名称
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("value", goodsName);
        data.put("thing7", jsonObject3);
        // 下单金额
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("value", amt);
        data.put("amount8", jsonObject4);
        // 下单人
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("value", memberName);
        data.put("thing4", jsonObject5);
        
        AppWxErrMsgDTO dto =
            wxManager.sendWeappSubscribeMessage(account, openid, templateId, "/pages/order/index", data);
        System.out.println(JsonUtil.toString(dto, true));
    }
    
    public void sendWxMsgVendorYs(MktVendorOrder bean, String code, String time, String openid, String appid,
        String templateid, Integer ascription)
    {
        try
        {
            JSONObject data = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "您有新的采购订单,请注意查收!");
            data.put("first", jsonObject);
            
            JSONObject k1 = new JSONObject();
            k1.put("value", code);
            data.put("keyword1", k1);
            JSONObject k2 = new JSONObject();
            k2.put("value", bean.getGoodsName());
            data.put("keyword2", k2);
            JSONObject k3 = new JSONObject();
            k3.put("value", bean.getNum());
            data.put("keyword3", k3);
            JSONObject k4 = new JSONObject();
            k4.put("value", time);
            data.put("keyword4", k4);
            
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("value", "请尽快完成订单!");
            data.put("remark", jsonObject4);
            JSONObject miniprogram = new JSONObject();
            miniprogram.put("appid", appid);
            miniprogram.put("page", "pages/order/index");
            Boolean msg = wxManager.wechatSendMsgYs(templateid, openid, miniprogram, data, ascription);
            log.info("采购订单发送给商户微信公众号: {}", msg);
        }
        catch (Exception e)
        {
            log.info("sendWxMsgVendorYs-发送微信推送失败，具体报错内容:  {}", e.getMessage());
        }
        
    }
    
    /**
     * 商户对账分页数据
     * @param pkeys     主键列表
     * @param page      页号
     * @param pagesize  每页大小
     * @param vendor    商户pkey列表
     * @param startDate 订单时间-开始
     * @param endDate   订单时间-结束
     * @return 结果
     */
    public MktVendorOrderMainDTO queryVendorOrder(List<Integer> pkeys, int page, int pagesize, List<Integer> vendor,
        String startDate, String endDate, List<SettlementType> status, Boolean createTimeSort, Boolean flag,
        Integer ascrip)
    {
        // 校验权限
        //        if(flag != null && flag)
        //            judgeRight();
        MktVendorOrderMainDTO rr = new MktVendorOrderMainDTO();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<String> marketPkeys = new ArrayList<>();
        if (StringUtils.isNotBlank(CurrentSession.marketPkey())
            && !(Constant.Operation + ascrip).equals(CurrentSession.marketPkey()))
            marketPkeys.add(CurrentSession.marketPkey());
        if (marketPkeys.isEmpty()) marketPkeys = null;
        // 总金额
        BigDecimal amt = vendorOrderDao.sumTotalPrice(pkeys,
            vendor,
            startDate,
            endDate,
            marketPkeys,
            status,
            PurchaseStatus.PURCHASE_CONFIRM,
            ascription);
        // 分页数据
        PageResult<MktVendorOrder> pageResult = vendorOrderDao.query(pkeys,
            page,
            pagesize,
            vendor,
            startDate,
            endDate,
            marketPkeys,
            status,
            createTimeSort,
            PurchaseStatus.PURCHASE_CONFIRM,
            ascription);
        
        // 当前市场的采购方式
        if (StringUtils.isNotBlank(CurrentSession.marketPkey()))
        {
            SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(CurrentSession.marketPkey());
            List<MktVendorOrder> content = pageResult.getContent();
            content.forEach(vendorOrder -> {
                // 商户的结算方式
                SettlementMethodType method = sysFarmerConfig.getSettlementMethod();
                // 商户的结算方式不存在（自采）或者为采购价结算
                if (Objects.isNull(method) || SettlementMethodType.PURCHASE_SETTLEMENT.equals(method))
                {
                    vendorOrder.setTotalPrice(vendorOrder.getAmt());
                    if (vendorOrder.getCommissionRate().compareTo(BigDecimal.ZERO) == 0)
                        vendorOrder.setCommissionRate(null);
                }
            });
            
            pageResult.setContent(content);
        }
        
        // 转成dto列表
        PageResult<MktVendorOrderDTO> result = BeanUtil.beanPageFrom(MktVendorOrderDTO.class, pageResult);
        dtoEnhance.deal(MktVendorOrderDTO.class, result);
        
        for (MktVendorOrderDTO line : result.getContent())
        {
            // 先写成固定
            line.setVendorOrderType(VendorOrderType.MARKET_ORDER);
        }
        
        rr.setAmt(amt);
        rr.setPageList(result);
        // 订单总数
        rr.setOrderCount(vendorOrderDao.getOrderCount(pkeys,
            vendor,
            startDate,
            endDate,
            marketPkeys,
            status,
            PurchaseStatus.PURCHASE_CONFIRM,
            ascription));
        rr.setPurchaseCount(pageResult.getNumberOfElements());
        return rr;
    }
    
    public BigDecimal countAmtToday(Integer vendor)
    {
        BigDecimal amt = vendorOrderDao.countAmtDate(vendor, DateUtil.formatDate(new Date(), "yyyy-MM-dd 00:00:00"));
        return amt;
    }
    
    public BigDecimal countAmtMon(Integer vendor)
    {
        BigDecimal amt = vendorOrderDao.countAmtDate(vendor,
            DateUtil.formatDate(DateUtil.atStartOfThisMonth(), "yyyy-MM-dd 00:00:00"));
        return amt;
    }
    
    public void automaticCourier(int orderPkey)
    {
        MktOrder order = orderDao.get(orderPkey);
        String market = order.getFarmer();
        SysFarmerConfig config = sysFarmerConfigDao.get(market);
        if (config == null) return;
        if (config.getAutomaticCourier() != null && config.getAutomaticCourier())
        {
            MktMarketCourier courier = marketCourierDao.selectOne().eq("market", market).eq("flag", true).exec();
            if (courier == null) return;
            orderManager.paidan(order.getPkey(), courier.getCourierKey());
            int num = 0;
            if (courier.getNum() != null) num = courier.getNum();
            courier.setNum(++num);
            courier.setFlag(false);
            marketCourierDao.update(courier);
            List<MktMarketCourier> exec =
                marketCourierDao.select().eq("market", market).gt("id", courier.getId()).sort("id", false).exec();
            List<MktMarketCourier> updAll = new ArrayList<>();
            MktMarketCourier newCourier = null;
            if (exec.size() > 0)
            {
                newCourier = exec.get(0);
            }
            else
            {
                List<MktMarketCourier> exec2 = marketCourierDao.select().eq("market", market).sort("id", false).exec();
                newCourier = exec2.get(0);
            }
            
            newCourier.setFlag(true);
            updAll.add(newCourier);
            List<MktMarketCourier> falseExec =
                marketCourierDao.select().eq("market", market).notEq("id", newCourier.getId()).exec();
            for (MktMarketCourier c : falseExec)
            {
                c.setFlag(false);
                updAll.add(c);
            }
            marketCourierDao.updateAll(updAll);
        }
        
    }
    
    @Transactional
    public Boolean againPurchase(MktVendorOrderDTO info, MktVendorOrder vendorOrder)
    {
        vendorOrder.setStatus(null);
        vendorOrder.setPurchaseStatus(PurchaseStatus.PURCHASE_REVOKE);
        vendorOrder.setRevokeTime(new Date());
        vendorOrderDao.update(vendorOrder);
        MktOrder order = orderDao.get(info.getOrderPkey());
        BigDecimal amt = vendorOrder.getAmt();
        if(CommissionType.MERCHANT.equals(vendorOrder.getCommissionType())
            && vendorOrder.getPayComm() != null
            && vendorOrder.getPayComm().compareTo(BigDecimal.ZERO) > 0)
        {
            amt = amt.subtract(vendorOrder.getPayComm());
        }
        // 减少商户钱包明细
        vendorWalletManager.updWalletLockAmount(vendorOrder
            .getVendor(), amt, false, VendorWalletSource.REVOKE, order.getCode(), order.getCreatedTime());
        
        info.setPkey(null);
        info.setOrderLinePkey(vendorOrder.getOrderLinePkey());
        info.setFarmer(vendorOrder.getFarmer());
        info.setCompany(vendorOrder.getCompany());
        purchaseOrder(order, Arrays.asList(info));
        return true;
    }
    
    @Transactional(rollbackOn = Throwable.class)
    public Boolean confirmPurchase(List<Integer> pkeys)
    {
        List<MktVendorOrder> exec =
            vendorOrderDao.select().in("pkey", pkeys).notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE).exec();
        Integer orderPkey = null;
        Date farmerTime = new Date();
        for (MktVendorOrder e : exec)
        {
            orderPkey = e.getOrderPkey();
            PurchaseStatus purchaseStatus = e.getPurchaseStatus();
            if (purchaseStatus == null || purchaseStatus.getIndex() < 1 || purchaseStatus.getIndex() > 2)
            {
                throw TofocusException.of(LejiaErrCode.PURCHASESTATUS_ERROR2);
            }
            e.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
            e.setStatus(SettlementType.NOT_START);
            e.setFarmerTime(farmerTime);
            PriceStatus priceStatus = e.getPriceStatus();
            if (priceStatus != null && priceStatus.equals(PriceStatus.ABNORMAL))
                e.setPriceStatus(PriceStatus.ABNORMAL_FINISH);
        }
        vendorOrderDao.updateAll(exec);
        if (orderPkey != null)
        {
            MktOrder order = orderDao.get(orderPkey);
            if (order != null)
            {
                // order的采购状态以最低的一个商户状态为主
                List<MktVendorOrder> exec2 = vendorOrderDao.select()
                    .eq("orderPkey", orderPkey)
                    .in("purchaseStatus",
                        PurchaseStatus.PURCHASEING,
                        PurchaseStatus.PURCHASE_FINISH,
                        PurchaseStatus.PURCHASE_CONFIRM)
                    .sort("purchaseStatus", false)
                    .exec();
                if (!exec2.isEmpty())
                {
                    order.setPurchaseStatus(exec2.get(0).getPurchaseStatus());
                    orderDao.update(order);
                }
                MktExpress express = expressDao.selectOne().eq("orderId", orderPkey).exec();
                if (express != null)
                {
                    express.setStatusName("拣货完成");
                    expressDao.update(express);
                }
                BigDecimal sum = BigDecimal.ZERO;
                BigDecimal postage = order.getPostage();
                BigDecimal cardAmt = order.getCardAmt();
                if ((cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) > 0)
                    || (postage != null && postage.compareTo(BigDecimal.ZERO) > 0))
                {
                    for (MktVendorOrder v : exec2)
                    {
                        sum = sum.add(v.getTotalPrice());
                    }
                    if (postage == null) postage = BigDecimal.ZERO;
                    if (cardAmt == null) cardAmt = BigDecimal.ZERO;
                    
                    BigDecimal postageSurplus = postage;
                    BigDecimal cardAmtSurplus = cardAmt;
                    
                    for (int i = 0; i < exec2.size(); i++)
                    {
                        MktVendorOrder v = exec2.get(i);
                        if (i == exec2.size() - 1)
                        {
                            v.setDiscountAmt(cardAmtSurplus);
                            v.setPostage(postageSurplus);
                        }
                        else
                        {
                            BigDecimal discountAmt = v.getTotalPrice()
                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                                .multiply(cardAmt)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                            v.setDiscountAmt(discountAmt);
                            cardAmtSurplus = cardAmtSurplus.subtract(discountAmt);
                            
                            BigDecimal postageAmt = v.getTotalPrice()
                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                                .multiply(postage)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                            v.setPostage(postageAmt);
                            postageSurplus = postageSurplus.subtract(postageAmt);
                        }
                    }
                    
                    vendorOrderDao.updateAll(exec2);
                }
            }
        }
        return true;
    }
    
    // 自动确认
    public Boolean runVendorOrderConfirm()
    {
        List<Integer> ascriptions = new ArrayList<>();
        ascriptions.add(1);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -15);
        System.out.println("cal: " + DateUtil.formatDate(cal.getTime()));
        List<MktOrder> list = orderDao.select()
            .eq("status", OrderStatus.CONFIRM_ORDER)
            .in("purchaseStatus", PurchaseStatus.PURCHASEING, PurchaseStatus.PURCHASE_FINISH)
            .le("createdTime", cal.getTime())
            .in("ascription", ascriptions)
            .exec();
        batchConfirmPurchase(CollectionUtil.keyList(list));
        return true;
    }
    
    private Boolean batchConfirmPurchase(List<Integer> orderKeys)
    {
        Date farmerTime = new Date();
        System.out.println("orderKeys: " + orderKeys.size());
        List<MktVendorOrder> voList = vendorOrderDao.select()
            .in("orderPkey", orderKeys)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .exec();
        System.out.println("voList: " + voList.size());
        Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
        for (MktVendorOrder vo : voList)
        {
            if (!map.containsKey(vo.getOrderPkey()))
            {
                map.put(vo.getOrderPkey(), new ArrayList<>());
            }
            map.get(vo.getOrderPkey()).add(vo);
        }
        
        for (Map.Entry<Integer, List<MktVendorOrder>> entry : map.entrySet())
        {
            Integer orderPkey = entry.getKey();
            List<MktVendorOrder> exec = entry.getValue();
            for (MktVendorOrder e : exec)
            {
                PurchaseStatus purchaseStatus = e.getPurchaseStatus();
                if (purchaseStatus == null || purchaseStatus.getIndex() < 1 || purchaseStatus.getIndex() > 2)
                {
                    throw TofocusException.of(LejiaErrCode.PURCHASESTATUS_ERROR2);
                }
                e.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
                e.setStatus(SettlementType.NOT_START);
                e.setFarmerTime(farmerTime);
                PriceStatus priceStatus = e.getPriceStatus();
                if (priceStatus != null && priceStatus.equals(PriceStatus.ABNORMAL))
                    e.setPriceStatus(PriceStatus.ABNORMAL_FINISH);
            }
            // TODO
            //            vendorOrderDao.updateAll(exec);
            System.out.println("exec1: " + JsonUtil.toString(exec, true));
            MktOrder order = orderDao.get(orderPkey);
            if (order != null)
            {
                // 跑批里 所有的交易订单均为确定
                order.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
                orderDao.update(order);
                
                MktExpress express = expressDao.selectOne().eq("orderId", orderPkey).exec();
                if (express != null)
                {
                    express.setStatusName("拣货完成");
                    System.out.println("express: " + express.getPkey());
                    // TODO
                    //                    expressDao.update(express);
                }
                BigDecimal sum = BigDecimal.ZERO;
                BigDecimal postage = order.getPostage();
                BigDecimal cardAmt = order.getCardAmt();
                if ((cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) > 0)
                    || (postage != null && postage.compareTo(BigDecimal.ZERO) > 0))
                {
                    for (MktVendorOrder v : exec)
                    {
                        sum = sum.add(v.getTotalPrice());
                    }
                    if (postage == null) postage = BigDecimal.ZERO;
                    if (cardAmt == null) cardAmt = BigDecimal.ZERO;
                    
                    BigDecimal postageSurplus = postage;
                    BigDecimal cardAmtSurplus = cardAmt;
                    
                    for (int i = 0; i < exec.size(); i++)
                    {
                        MktVendorOrder v = exec.get(i);
                        if (i == exec.size() - 1)
                        {
                            v.setDiscountAmt(cardAmtSurplus);
                            v.setPostage(postageSurplus);
                        }
                        else
                        {
                            BigDecimal discountAmt = v.getTotalPrice()
                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                                .multiply(cardAmt)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                            v.setDiscountAmt(discountAmt);
                            cardAmtSurplus = cardAmtSurplus.subtract(discountAmt);
                            
                            BigDecimal postageAmt = v.getTotalPrice()
                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                                .multiply(postage)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                            v.setPostage(postageAmt);
                            postageSurplus = postageSurplus.subtract(postageAmt);
                        }
                    }
                    // TODO
                    System.out.println("exec: " + JsonUtil.toString(exec, true));
                    //                    vendorOrderDao.updateAll(exec);
                }
            }
        }
        return true;
    }
    //    private Boolean batchConfirmPurchase(Map<Integer,List<Integer>> map)
    //    {
    //        Date farmerTime = new Date();
    //        for(Map.Entry<Integer,List<Integer>> entry : map.entrySet())
    //        {
    //            Integer orderPkey = entry.getKey();
    //            List<Integer> pkeys = entry.getValue();
    //            List<MktVendorOrder> exec =
    //                vendorOrderDao.select().in("pkey", pkeys).notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE).exec();
    //            for (MktVendorOrder e : exec)
    //            {
    //                PurchaseStatus purchaseStatus = e.getPurchaseStatus();
    //                if (purchaseStatus == null || purchaseStatus.getIndex() < 1 || purchaseStatus.getIndex() > 2)
    //                {
    //                    throw TofocusException.of(LejiaErrCode.PURCHASESTATUS_ERROR2);
    //                }
    //                e.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
    //                e.setStatus(SettlementType.NOT_START);
    //                e.setFarmerTime(farmerTime);
    //                PriceStatus priceStatus = e.getPriceStatus();
    //                if (priceStatus != null && priceStatus.equals(PriceStatus.ABNORMAL))
    //                    e.setPriceStatus(PriceStatus.ABNORMAL_FINISH);
    //            }
    //            vendorOrderDao.updateAll(exec);
    //            MktOrder order = orderDao.get(orderPkey);
    //            if (order != null)
    //            {
    //                // 跑批里 所有的交易订单均为确定
    //                order.setPurchaseStatus(PurchaseStatus.PURCHASE_CONFIRM);
    //                orderDao.update(order);
    //                
    //                List<MktVendorOrder> exec2 = vendorOrderDao.select()
    //                    .eq("orderPkey", orderPkey)
    //                    .in("purchaseStatus",
    //                        PurchaseStatus.PURCHASEING,
    //                        PurchaseStatus.PURCHASE_FINISH,
    //                        PurchaseStatus.PURCHASE_CONFIRM)
    //                    .exec();
    //                
    //                MktExpress express = expressDao.selectOne().eq("orderId", orderPkey).exec();
    //                if (express != null)
    //                {
    //                    express.setStatusName("拣货完成");
    //                    expressDao.update(express);
    //                }
    //                BigDecimal sum = BigDecimal.ZERO;
    //                BigDecimal postage = order.getPostage();
    //                BigDecimal cardAmt = order.getCardAmt();
    //                if ((cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) > 0)
    //                    || (postage != null && postage.compareTo(BigDecimal.ZERO) > 0))
    //                {
    //                    for (MktVendorOrder v : exec2)
    //                    {
    //                        sum = sum.add(v.getTotalPrice());
    //                    }
    //                    if (postage == null) postage = BigDecimal.ZERO;
    //                    if (cardAmt == null) cardAmt = BigDecimal.ZERO;
    //                    
    //                    BigDecimal postageSurplus = postage;
    //                    BigDecimal cardAmtSurplus = cardAmt;
    //                    
    //                    for (int i = 0; i < exec2.size(); i++)
    //                    {
    //                        MktVendorOrder v = exec2.get(i);
    //                        if (i == exec2.size() - 1)
    //                        {
    //                            v.setDiscountAmt(cardAmtSurplus);
    //                            v.setPostage(postageSurplus);
    //                        }
    //                        else
    //                        {
    //                            BigDecimal discountAmt = v.getTotalPrice()
    //                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
    //                                .multiply(cardAmt)
    //                                .setScale(2, BigDecimal.ROUND_HALF_UP);
    //                            v.setDiscountAmt(discountAmt);
    //                            cardAmtSurplus = cardAmtSurplus.subtract(discountAmt);
    //                            
    //                            BigDecimal postageAmt = v.getTotalPrice()
    //                                .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
    //                                .multiply(postage)
    //                                .setScale(2, BigDecimal.ROUND_HALF_UP);
    //                            v.setPostage(postageAmt);
    //                            postageSurplus = postageSurplus.subtract(postageAmt);
    //                        }
    //                    }
    //                    
    //                    vendorOrderDao.updateAll(exec2);
    //                }
    //            }
    //            
    //        }
    //        
    //        return true;
    //    }
    
    @Transactional(rollbackOn = Throwable.class)
    public Boolean confirmPurchaseRun()
    {
        List<MktVendorOrder> exec =
            vendorOrderDao.select().notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE).exec();
        
        for (MktVendorOrder vo : exec)
        {
            BigDecimal subtract = vo.getGoodsPrice().subtract(vo.getPrice());
            BigDecimal difference = subtract.multiply(BigDecimal.valueOf(vo.getNum()));
            vo.setDifference(difference);
        }
        
        List<Integer> okey = new ArrayList<>();
        Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
        for (MktVendorOrder e : exec)
        {
            Integer orderPkey = e.getOrderPkey();
            if (!map.containsKey(orderPkey))
            {
                List<MktVendorOrder> value = new ArrayList<>();
                map.put(orderPkey, value);
            }
            map.get(orderPkey).add(e);
            okey.add(e.getOrderPkey());
        }
        List<Integer> pkeys = okey.stream().distinct().collect(Collectors.toList());
        List<MktOrder> orderList = orderDao.select().in("pkey", pkeys.toArray()).exec();
        for (MktOrder order : orderList)
        {
            if (!map.containsKey(order.getPkey())) continue;
            List<MktVendorOrder> list = map.get(order.getPkey());
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal postage = order.getPostage();
            BigDecimal cardAmt = order.getCardAmt();
            if ((cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) > 0)
                || (postage != null && postage.compareTo(BigDecimal.ZERO) > 0))
            {
                for (MktVendorOrder v : list)
                {
                    sum = sum.add(v.getTotalPrice());
                }
                
                if (postage == null) postage = BigDecimal.ZERO;
                if (cardAmt == null) cardAmt = BigDecimal.ZERO;
                BigDecimal postageSurplus = postage;
                BigDecimal cardAmtSurplus = cardAmt;
                for (int i = 0; i < list.size(); i++)
                {
                    MktVendorOrder v = list.get(i);
                    if (i == list.size() - 1)
                    {
                        v.setDiscountAmt(cardAmtSurplus);
                        v.setPostage(postageSurplus);
                    }
                    else
                    {
                        BigDecimal discountAmt = v.getTotalPrice()
                            .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                            .multiply(cardAmt)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                        v.setDiscountAmt(discountAmt);
                        cardAmtSurplus = cardAmtSurplus.subtract(discountAmt);
                        
                        BigDecimal postageAmt = v.getTotalPrice()
                            .divide(sum, 6, BigDecimal.ROUND_HALF_UP)
                            .multiply(postage)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                        v.setPostage(postageAmt);
                        postageSurplus = postageSurplus.subtract(postageAmt);
                    }
                }
                
                vendorOrderDao.updateAll(list);
            }
            
        }
        return true;
    }
    
    public VendorOrderInfo clistPurchase(Integer pkey)
    {
        VendorOrderInfo res = new VendorOrderInfo();
        MktOrder order = orderDao.get(pkey);
        if (order == null) return res;
        res.setCode(order.getCode());
        PurchaseStatus status = order.getPurchaseStatus();
        
        List<VendorOrderOnList> vendors = vendorOrderDao.select()
            .eq("orderPkey", pkey)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .execDto(VendorOrderOnList.class);
        for (VendorOrderOnList v : vendors)
        {
            if (v.getVendor() == 0)
                v.setVendorName("自采");
            else
            {
                MktVendor mktVendor = vendorDao.get(v.getVendor());
                if (mktVendor != null) v.setVendorName(mktVendor.getDisplayName());
            }
            if(v.getAmt().compareTo(v.getTotalPrice()) < 0)
                v.setAmt(v.getTotalPrice());
        }
        res.setVendors(vendors);
        /**
         * 因为老代码  自提的时候 会修改mkt_order表 将 purchaseStatus 修改成 PurchaseStatus.PURCHASE_CONFIRM
         * 因已经部署使用,所以修改 status的获取方式
         */
        if (!vendors.isEmpty())
        {
            for (VendorOrderOnList v : vendors)
            {
                if (status == null || (status != null && status.getIndex() > v.getPurchaseStatus().getIndex()))
                    status = v.getPurchaseStatus();
            }
        }
        String sname = "";
        if (status != null) sname = status.getName();
        res.setStatusName(sname);
        List<VendorOrderOnList> revokes = vendorOrderDao.select()
            .eq("orderPkey", pkey)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .execDto(VendorOrderOnList.class);
        for (VendorOrderOnList v : revokes)
        {
            if (v.getVendor() == 0)
                v.setVendorName("自采");
            else
            {
                MktVendor mktVendor = vendorDao.get(v.getVendor());
                if (mktVendor != null) v.setVendorName(mktVendor.getDisplayName());
            }
            if(v.getAmt().compareTo(v.getTotalPrice()) < 0)
                v.setAmt(v.getTotalPrice());
        }
        res.setRevokes(revokes);
        return res;
    }
    
    /**
     * 判断权限
     */
    private void judgeRight()
    {
        PointType pointType = sysConfigManager.judgePoint();
        if (!PointType.MARKET.equals(pointType))
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 商户列表
     * @return 结果
     */
    public List<PkeyNameDTO> vendorList()
    {
        judgeRight();
        List<MktVendor> vendorList =
            vendorDao.getValidVendor(CurrentSession.marketPkey(), CurrentSession.companyPkey());
        List<PkeyNameDTO> res = new ArrayList<>();
        for (MktVendor v : vendorList)
        {
            PkeyNameDTO dto = BeanUtil.beanFrom(PkeyNameDTO.class, v);
            dto.setName(v.getDisplayName());
            res.add(dto);
        }
        return res;
    }
    
    /**
     * 结算状态枚举列表
     * @return 结果
     */
    public List<EnumNameDTO> statusList()
    {
        judgeRight();
        return EnumNameDTO.getList(SettlementType.class);
    }
    
    /**
     * 运营端-采购方式
     * @return 结果
     */
    public EnumNameDTO settlementMethod(String pkey)
    {
        //        judgeRight();
        if (pkey == null) pkey = CurrentSession.marketPkey();
        if (pkey == null) return new EnumNameDTO();
        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(pkey);
        // 初始化结果
        EnumNameDTO enumNameDTO = new EnumNameDTO();
        if (Objects.nonNull(sysFarmerConfig))
        {
            SettlementMethodType settlementMethod = sysFarmerConfig.getSettlementMethod();
            if (Objects.nonNull(settlementMethod))
            {
                enumNameDTO.setEnglish(settlementMethod.toString());
                enumNameDTO.setChinese(settlementMethod.getName());
                return enumNameDTO;
            }
        }
        
        // 其余情况，默认采购价结算
        enumNameDTO.setEnglish(SettlementMethodType.PURCHASE_SETTLEMENT.toString());
        enumNameDTO.setChinese(SettlementMethodType.PURCHASE_SETTLEMENT.getName());
        return enumNameDTO;
    }
    
    /**
     * 商户结算分页数据
     * @param param 参数
     * @return 结果
     */
    public SettlementMainDTO settlementList(MktVendorOrderParamDTO param)
    {
        // 校验权限
        judgeRight();
        
        // 初始化结果
        SettlementMainDTO result = new SettlementMainDTO();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<String> marketPkeys = new ArrayList<>();
        marketPkeys.add(CurrentSession.marketPkey());
        // mkt_vendor_order分页数据
        List<MktVendorOrder> list = vendorOrderDao.list(null,
            param.getVendor(),
            param.getStartDate(),
            param.getEndDate(),
            marketPkeys,
            param.getStatus(),
            param.getCreateTimeSort(),
            ascription);
        
        // 获取明细列表
        List<SettlementDTO> contentRes = getcontentRes(list);
        
        Integer purchaseCount = contentRes.stream().mapToInt(SettlementDTO::getTradeCount).sum();
        result.setPurchaseCount(purchaseCount);
        BigDecimal purchaseAmt =
            contentRes.stream().map(SettlementDTO::getTradePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setPurchaseAmt(purchaseAmt);
        // 已结算
        List<SettlementDTO> already = contentRes.stream()
            .filter(item -> SettlementType.SUCCESS.equals(item.getStatus()))
            .collect(Collectors.toList());
        Integer alreadyCount = already.stream().mapToInt(SettlementDTO::getTradeCount).sum();
        result.setAlreadycount(alreadyCount);
        BigDecimal alreadyAmt =
            already.stream().map(SettlementDTO::getTradePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setAlreadyAmt(alreadyAmt);
        // 未结算
        List<SettlementDTO> await = contentRes.stream()
            .filter(item -> SettlementType.NOT_START.equals(item.getStatus()))
            .collect(Collectors.toList());
        Integer awaitCount = await.stream().mapToInt(SettlementDTO::getTradeCount).sum();
        result.setAwaitCount(awaitCount);
        BigDecimal awaitAmt = await.stream().map(SettlementDTO::getTradePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setAwaitAmt(awaitAmt);
        
        // 设置分页
        PageResult<SettlementDTO> pageResult =
            PageUtil.page(contentRes, PageParameter.of(param.getPage(), param.getPagesize()));
        result.setPageList(pageResult);
        
        return result;
    }
    
    /**
     * 商户结算明细列表
     * @param list mkt_vendor_order列表
     * @return 商户结算明细列表
     */
    private List<SettlementDTO> getcontentRes(List<MktVendorOrder> list)
    {
        // 商户结算明细列表(最终结果)
        List<SettlementDTO> contentRes = new ArrayList<>();
        
        // 格式化日期
        Set<String> dates = list.stream().map(i -> {
            return DateUtil.formatDate(i.getCreatedTime(), "yyyy-MM-dd");
        }).collect(Collectors.toSet());
        
        dates.forEach(date -> {
            // 获取同日期的数据
            List<MktVendorOrder> sameDates = list.stream()
                .filter(marketVendor -> date.equals(DateUtil.formatDate(marketVendor.getCreatedTime(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
            
            // 商户
            Set<Integer> vendors2 = sameDates.stream().map(MktVendorOrder::getVendor).collect(Collectors.toSet());
            
            // 单个商户
            vendors2.forEach(vendor -> {
                List<MktVendorOrder> sameVendor = sameDates.stream()
                    .filter(mktVendorOrder -> vendor.equals(mktVendorOrder.getVendor()))
                    .collect(Collectors.toList());
                
                // 商户当天不同佣金费率的数据
                Set<BigDecimal> rates =
                    sameVendor.stream().map(MktVendorOrder::getCommissionRate).collect(Collectors.toSet());
                
                rates.forEach(rate -> {
                    // 需要计算的数据
                    List<MktVendorOrder> needCal = sameVendor.stream()
                        .filter(mktVendorOrder -> vendor.equals(mktVendorOrder.getVendor()))
                        .collect(Collectors.toList());
                    SettlementDTO dto = new SettlementDTO();
                    List<Integer> pkeys = needCal.stream().map(MktVendorOrder::getPkey).collect(Collectors.toList());
                    
                    dto.setPkeys(pkeys);
                    dto.setCreatedTime(date);
                    dto.setVendor(vendor);
                    dto.setCommissionRate(rate);
                    
                    dto.setTradeCount(needCal.size());
                    BigDecimal totalPrice =
                        needCal.stream().map(MktVendorOrder::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    dto.setTradePrice(totalPrice);
                    BigDecimal commissions =
                        needCal.stream().map(MktVendorOrder::getCommissions).reduce(BigDecimal.ZERO, BigDecimal::add);
                    dto.setCommissions(commissions);
                    BigDecimal amt =
                        needCal.stream().map(MktVendorOrder::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    dto.setAmt(amt);
                    // 目前应用场景下的，多条mkt_vendor_order数据，结算状态和备注一致
                    // TODO 不一致的复杂场景暂时不考虑
                    dto.setSettlementRemark(needCal.get(0).getSettlementRemark());
                    dto.setStatus(needCal.get(0).getStatus());
                    // 处理其他信息
                    dtoEnhance.deal(SettlementDTO.class, dto);
                    contentRes.add(dto);
                });
            });
        });
        // 采购日期降序
        contentRes.sort((o1, o2) -> {
            Long l1 = DateUtil.parseTimeSecond(o1.getCreatedTime());
            Long l2 = DateUtil.parseTimeSecond(o2.getCreatedTime());
            return l2.compareTo(l1);
        });
        return contentRes;
    }
    
    /**
     * 选中的商户结算详情
     * @param pkeys     选中的数据主键
     * @return 结果
     */
    public SettlementDetailDTO settlementDetail(List<Integer> pkeys)
    {
        // 校验权限
        judgeRight();
        // 校验列表
        List<MktVendorOrder> list = validateSettleList(pkeys);
        
        // 商户结算明细列表
        List<SettlementDTO> settlementDTOS = getcontentRes(list);
        // 创建时间升序
        settlementDTOS.sort((o1, o2) -> {
            Long l1 = DateUtil.parseTimeSecond(o1.getCreatedTime());
            Long l2 = DateUtil.parseTimeSecond(o2.getCreatedTime());
            return l1.compareTo(l2);
        });
        
        // 初始化结果
        SettlementDetailDTO result = new SettlementDetailDTO();
        result.setPkeys(pkeys);
        
        result.setStartDate(settlementDTOS.get(0).getCreatedTime());
        result.setEndDate(settlementDTOS.get(settlementDTOS.size() - 1).getCreatedTime());
        
        result.setVendorCount(settlementDTOS.stream().map(SettlementDTO::getVendor).collect(Collectors.toSet()).size());
        // 总采购笔数
        result.setPurchaseCount(settlementDTOS.size());
        // 总采购金额
        BigDecimal purchaseAmt =
            settlementDTOS.stream().map(SettlementDTO::getTradePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setPurchaseAmt(purchaseAmt);
        return result;
    }
    
    /**
     * 商户结算
     * @param pkeys             选中的数据主键
     * @param settlementRemark  结算备注
     * @return 是否成功
     */
    @Transactional
    public Boolean settlement(List<Integer> pkeys, String settlementRemark)
    {
        // 校验权限
        judgeRight();
        Boolean result = false;
        if (Objects.nonNull(settlementRemark) && settlementRemark.length() > 100)
        {
            throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT, "结算备注最多100字");
        }
        
        List<MktVendorOrder> list = validateSettleList(pkeys);
        // 校验通过，修改数据值
        list.forEach(i -> {
            i.setStatus(SettlementType.SUCCESS);
            i.setSettlementRemark(settlementRemark);
        });
        
        vendorOrderDao.updateAll(list);
        
        // 设置结果标记为true
        result = true;
        return result;
    }
    
    /**
     * 校验商户结算结果列表
     * @param pkeys 数据pkey
     * @return 列表
     */
    private List<MktVendorOrder> validateSettleList(List<Integer> pkeys)
    {
        List<MktVendorOrder> list = vendorOrderDao.select().in("pkey", pkeys).exec();
        if (CollectionUtils.isEmpty(list))
        {
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
        }
        Set<SettlementType> statuses = list.stream().map(MktVendorOrder::getStatus).collect(Collectors.toSet());
        if (statuses.contains(SettlementType.AWAIT_CONFIRM) || statuses.contains(SettlementType.SUCCESS))
        {
            throw TofocusException.of(LejiaErrCode.DATA_NOT_ALLOWD, "有部分市场订单结算状态为“待确认”或者“已结算”");
        }
        return list;
    }
    
    /**
     * 撤销记录分页数据
     * @param param  参数
     * @return 结果
     */
    public RevokeMainDTO revokeList(MktVendorParamDTO param)
    {
        // 判断权限
        judgeRight();
        Integer ascription = CurrentSession.ascriptionPkey();
        // 初始化结果
        RevokeMainDTO result = new RevokeMainDTO();
        // 页码和每页条数
        Integer page = Objects.nonNull(param.getPage()) ? param.getPage() : 0;
        Integer pageSize = Objects.nonNull(param.getPagesize()) ? param.getPagesize() : 10;
        // 当前市场
        List<String> marketPkeys = new ArrayList<>();
        marketPkeys.add(CurrentSession.marketPkey());
        PageResult<MktVendorOrder> dtoPageResult = vendorOrderDao.query(param.getPkey(),
            page,
            pageSize,
            param.getVendor(),
            param.getStartDate(),
            param.getEndDate(),
            marketPkeys,
            null,
            param.getCreateTimeSort(),
            PurchaseStatus.PURCHASE_REVOKE,
            ascription);
        
        // 转换实体列表 -> DTO列表
        PageResult<RevokeDTO> pageResult = BeanUtil.beanPageFrom(RevokeDTO.class, dtoPageResult);
        
        // 订单类型，默认是为“市场订单”
        List<RevokeDTO> content = pageResult.getContent();
        content.forEach(c -> {
            c.setVendorOrderType(VendorOrderType.MARKET_ORDER);
        });
        
        dtoEnhance.deal(RevokeDTO.class, pageResult);
        result.setPageList(pageResult);
        
        // 订单总数
        result.setOrderCount(vendorOrderDao.getOrderCount(param.getPkey(),
            param.getVendor(),
            param.getStartDate(),
            param.getEndDate(),
            marketPkeys,
            null,
            PurchaseStatus.PURCHASE_REVOKE,
            ascription));
        // 总采购数
        result.setPurchaseCount(pageResult.getNumberOfElements());
        // 总金额
        BigDecimal amt = vendorOrderDao.sumTotalPrice(param.getPkey(),
            param.getVendor(),
            param.getStartDate(),
            param.getEndDate(),
            marketPkeys,
            null,
            PurchaseStatus.PURCHASE_REVOKE,
            ascription);
        result.setPurchaseAmt(amt);
        return result;
    }
    
    public VendorOrderReport purchaseReport(int page, int pagesize, DataEnums dataEnums, String startDate,
        String endDate, List<Integer> vendorKeys, SettlementType status, String createTimeSort)
    {
        String marketPkey = CurrentSession.marketPkey();
        VendorOrderReport res =
            new VendorOrderReport(0, "0", PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
        List<VendorOrderReportLine> report = vendorOrderDao
            .purchaseReport(dataEnums, status.getIndex(), vendorKeys, startDate, endDate, marketPkey, createTimeSort);
        List<Integer> keys = new ArrayList<>();
        report.forEach(e -> {
            keys.add(e.getVendor());
        });
        List<MktVendor> exec = vendorDao.select().in("pkey", keys).exec();
        Map<Integer, String> venMap = new HashMap<>();
        exec.forEach(e -> {
            venMap.put(e.getPkey(), e.getName());
        });
        Integer purchaseNum = 0;
        BigDecimal purchaseAmt = BigDecimal.ZERO;
        for (VendorOrderReportLine l : report)
        {
            purchaseNum = purchaseNum + Integer.valueOf(l.getNum());
            purchaseAmt = purchaseAmt.add(new BigDecimal(l.getAmt()));
            if (venMap.containsKey(l.getVendor())) l.setName(venMap.get(l.getVendor()));
            if (l.getVendor().intValue() == 0) l.setName("自采");
        }
        res.setPurchaseNum(purchaseNum);
        res.setPurchaseAmt(purchaseAmt.toString());
        if (dataEnums != null && dataEnums.getIndex() == DataEnums.SEASON.getIndex())
        {
            Map<String, List<VendorOrderReportLine>> map = new LinkedHashMap<>();
            for (VendorOrderReportLine l : report)
            {
                String date = l.getDate();
                Integer year = Integer.valueOf(date.substring(0, 4));
                date = date.substring(date.length() - 2, date.length());
                Integer of = Integer.valueOf(date);
                if (of > 9)
                {
                    String key = year + "第四季度" + l.getVendor();
                    if (!map.containsKey(key))
                    {
                        List<VendorOrderReportLine> value = new ArrayList<>();
                        map.put(key, value);
                    }
                    map.get(key).add(l);
                }
                else if (of > 6)
                {
                    String key = year + "第三季度" + l.getVendor();
                    if (!map.containsKey(key))
                    {
                        List<VendorOrderReportLine> value = new ArrayList<>();
                        map.put(key, value);
                    }
                    map.get(key).add(l);
                }
                else if (of > 3)
                {
                    String key = year + "第二季度" + l.getVendor();
                    if (!map.containsKey(key))
                    {
                        List<VendorOrderReportLine> value = new ArrayList<>();
                        map.put(key, value);
                    }
                    map.get(key).add(l);
                }
                else if (of > 0)
                {
                    String key = year + "第一季度" + l.getVendor();
                    if (!map.containsKey(key))
                    {
                        List<VendorOrderReportLine> value = new ArrayList<>();
                        map.put(key, value);
                    }
                    map.get(key).add(l);
                }
            }
            report.clear();
            for (String key : map.keySet())
            {
                VendorOrderReportLine add = new VendorOrderReportLine();
                add.setDate(key.substring(0, 8));
                add.setVendor(Integer.valueOf(key.substring(8, key.length())));
                int num = 0;
                BigDecimal amt = BigDecimal.ZERO;
                for (VendorOrderReportLine v : map.get(key))
                {
                    num = num + Integer.valueOf(v.getNum());
                    amt = amt.add(new BigDecimal(v.getAmt()));
                }
                add.setNum(num + "");
                add.setAmt(amt.toString());
                if (venMap.containsKey(add.getVendor())) add.setName(venMap.get(add.getVendor()));
                if (add.getVendor().intValue() == 0) add.setName("自采");
                report.add(add);
            }
        }
        res.setLines(PageUtil.page(report, PageParameter.of(page, pagesize)));
        return res;
    }
    
    
  
    
}
