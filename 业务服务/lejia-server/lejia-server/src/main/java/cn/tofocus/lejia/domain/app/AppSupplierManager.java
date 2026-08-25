package cn.tofocus.lejia.domain.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierInfo;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderAddr;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.v2.AppOrderV2Expand;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.utils.OrderVerifyCodeGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppSupplierManager
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private AppOrderV2Expand appOrderV2Expand;
    
    @Autowired
    private WxManager wxManager;
    
    public AppSupplierInfo get()
    {
        MktSupplier supplier = MobileSession.supplier();
        return BeanUtil.beanFrom(AppSupplierInfo.class, supplier);
    }
    
    public AppSupplierOrderInfo getOrderByScanVerifyCode(String kcCode, String verifyCode)
    {
        MktSupplier supplier = MobileSession.supplier();
        
        AppSupplierOrderInfo info = orderDao.selectOne()
            .eq(MktOrder.F.code, kcCode)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .eq(MktOrder.F.distributionType, DistributionType.PICKUP)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .execDto(AppSupplierOrderInfo.class);
        if (info == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (!supplier.getPkey().equals(info.getSupplier()))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_ORDER_NO_VERIFY_PERMISSION);
        if (info.getStatus() != OrderStatus.DELIVERED_ORDER && info.getStatus() != OrderStatus.SHIPPED_ORDER
            && info.getStatus() != OrderStatus.ARRIVED_ORDER && info.getStatus() != OrderStatus.CONFIRM_ORDER
            && info.getStatus() != OrderStatus.WAIT_ARRIVAL_ORDER && info.getStatus() != OrderStatus.WAIT_WRITEOFF_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前状态不支持核销");
        
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
//        List<Long> goodsPkeys = lines.stream().map(MktOrderLine::getGoods).collect(Collectors.toList());
        List<Integer> goodsPkeys = new ArrayList<>();
        for(MktOrderLine l : lines)
        {
            goodsPkeys.add(l.getGoods().intValue());
        }
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(goodsPkeys);
        
        List<OrderV2Info> list = appOrderV2Expand.supplierOrderInfos(lines, goodsMap, info.getPkey());
        info.setInfos(list);
        
        return info;
    }
    
    public boolean writeOffOrder(String kcCode, String verifyCode)
    {
        MktSupplier supplier = MobileSession.supplier();
        
        MktOrder order = orderDao.selectOne()
            .eq(MktOrder.F.code, kcCode)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .eq(MktOrder.F.distributionType, DistributionType.PICKUP)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .exec();
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (!supplier.getPkey().equals(order.getSupplier()))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_ORDER_NO_VERIFY_PERMISSION);
        if (order.getStatus() == OrderStatus.SHIPPED_ORDER || order.getStatus() == OrderStatus.ARRIVED_ORDER
            || order.getStatus() == OrderStatus.CONFIRM_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "已核销，无需重复操作");
        if (order.getStatus() != OrderStatus.DELIVERED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前状态不支持核销");
        
        // 验证核销码
        String realVerifyCode =
            OrderVerifyCodeGenerator.build(order.getCode(), order.getPkey(), order.getCreatedTime());
        if (!realVerifyCode.equals(verifyCode)) throw TofocusException.of(SysErrCode.ACCESS_DENIED);
        
        order.setStatus(OrderStatus.ARRIVED_ORDER);
        order.setPickupFlag(true);
        order.setPickupTime(DateUtil.formatDate(new Date()));
        orderDao.update(order);
        
        uploadPickupInfo2Wx(order.getPkey(), order.getCode(), order.getMember(), order.getAscription());
        return true;
    }
    
    /**
     * 上传自提信息到微信
     */
    public void uploadPickupInfo2Wx(Integer orderPkey, String code, Integer member, Integer ascription)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            List<MktOrderLine> listOrder = orderLineDao.listOrder(orderPkey);
            for (MktOrderLine s : listOrder)
            {
                sb.append(s.getGoodsName());
                sb.append("(");
                sb.append(s.getSpaceName());
                sb.append(")*");
                sb.append(s.getNum());
                sb.append(", ");
            }
            String itemDesc = sb.toString();
            if (!itemDesc.isEmpty()) itemDesc = itemDesc.substring(0, itemDesc.length() - 1);
            if (itemDesc.length() > 120) itemDesc = itemDesc.substring(0, 120);
            String openid = null;
            String mchid = null;
            SysAscription sysAscription = ascriptionDao.get(ascription);
            if (sysAscription != null)
            {
                mchid = sysAscription.getConfigMchid();
            }
            MktMember mktMember = memberDao.get(member);
            if (mktMember != null) openid = mktMember.getOpenid1();
            if (openid != null && mchid != null)
            {
                wxManager
                    .uploadShippingInfo(null, code, mchid, itemDesc, 4, null, null, null, null, openid, ascription);
            }
            else
            {
                log.info("缺少openid或mchid没有发货,订单号: {}", code);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            log.error("微信确认收货报错");
        }
    }
}
