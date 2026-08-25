package cn.tofocus.lejia;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.excel.EasyExcel;

import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.join.db.SelectPageOps;
import cn.tofocus.db.join.db.SubSelectBuilder;
import cn.tofocus.lejia.bean.dto.MktOrderLineExcel;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@SpringBootTest
public class ManualOrderLineExportTest
{
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private MktOrderTagDao orderTagDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;
    
    @Test
    public void export()
    {
        String path = "D:\\Users\\czy\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        exportOrderLine(13,
            0,
            99999,
            OrderOir.POINTS_MALL,
            "2025-11-17",
            "2025-12-17",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            null,
            true,
            null,
            null,
            null,
            path);
    }
    
    public void exportOrderLine(Integer ascription, int page, int pagesize, OrderOir orderOir, String startDate,
        String endDate, OrderStatus status, String code, String mobile, String memberMobile, OrderType orderType,
        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh, String farmer, Boolean falg, ExpressType expressType, List<PayType> payTypes,
        List<Integer> tags, String path)
    {
        try
        {
            long t1 = System.currentTimeMillis();
            PageResult<MktOrderLineExcel> orders = queryOrderLineExcel(ascription,
                page,
                pagesize,
                orderOir,
                startDate,
                endDate,
                status,
                code,
                mobile,
                memberMobile,
                orderType,
                purchaseStatus,
                groupPkey,
                vrifyCode,
                priceAbnormal,
                priceAbnormalFinsh,
                farmer,
                falg,
                expressType,
                payTypes,
                tags);
            long t2 = System.currentTimeMillis();
            System.out.println("查询完成，耗时" + (t2 - t1) + "ms");
            EasyExcel.write(path, MktOrderLineExcel.class)
                //.registerWriteHandler(
                //    new ExcelMergeColStrategy(MktOrderLineExcel.class, 1, orders.getNumberOfElements()))
                .sheet("订单明细")
                .doWrite(orders.getContent());
            long t3 = System.currentTimeMillis();
            System.out.println("导出完成，耗时" + (t3 - t2) + "ms");
            System.out.println("总耗时" + (t3 - t1) + "ms");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private PageResult<MktOrderLineExcel> queryOrderLineExcel(Integer ascriptionPkey, int page, int pagesize,
        OrderOir orderOir, String startDate, String endDate, OrderStatus status, String code, String mobile,
        String memberMobile, OrderType orderType, PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode,
        Boolean priceAbnormal, Boolean priceAbnormalFinsh, String farmer, Boolean falg, ExpressType expressType,
        List<PayType> payTypes, List<Integer> tags)
    {
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> members = null;
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if (tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascriptionPkey);
            if (list == null || list.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (StringUtil.isNotBlank(memberMobile))
        {
            members = memberDao.select().like("mobile", memberMobile).execDto("pkey", Integer.class);
        }
        if (priceAbnormal && priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .or()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .close()
                .done()
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormal)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        
        SubSelectBuilder<SelectPageOps> builder = orderLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktOrderLine.F.pkey)
            .as(MktOrderLine.F.orderPkey)
            .as(MktOrderLine.F.goodsName)
            .as(MktOrderLine.F.spaceName)
            .as(MktOrderLine.F.num)
            .as(MktOrderLine.F.pricen)
            .as(MktOrderLine.F.refundAmt, MktOrderLineExcel.F.lineRefundAmt)
            .as(MktOrderLine.F.point)
            .as(MktOrderLine.F.couponAmt, MktOrderLineExcel.F.lineCouponAmt)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .as(MktOrder.F.code)
            .as(MktOrder.F.status)
            .as(MktOrder.F.distributionType)
            .as(MktOrder.F.createdTime)
            .as(MktOrder.F.payType)
            .as(MktOrder.F.orderType)
            .as(MktOrder.F.amto)
            .as(MktOrder.F.pointn)
            .as(MktOrder.F.postage)
            .as(MktOrder.F.cardAmt)
            .as(MktOrder.F.cardPostageAmt)
            .as(MktOrder.F.amtall)
            .as(MktOrder.F.amtn)
            .as(MktOrder.F.refundAmt)
            .as(MktOrder.F.refundPoint)
            .as(MktOrder.F.pstime)
            .as(MktOrder.F.pickupTime)
            .as(MktOrder.F.supplier)
            .as(MktOrder.F.member)
            .eq(MktOrder.F.farmer, farmer)
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .eq(MktOrder.F.expressType, expressType)
            .in(MktOrder.F.payType, payTypes)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .eq(MktOrder.F.status, status)
            .eq(MktOrder.F.orderOir, orderOir)
            .in(MktOrder.F.member, members)
            .eq(MktOrder.F.purchaseStatus, purchaseStatus)
            .like(MktOrder.F.pickupCode, vrifyCode);
        if (!orderIds.isEmpty())
            builder.in(MktOrder.F.pkey, orderIds.toArray());
        if (StringUtils.isNotBlank(code))
            builder.like(MktOrder.F.code, code);
        if (orderType != null)
            builder.eq(MktOrder.F.orderType, orderType);
        else
        {
            if (!falg)
                builder.notEq(MktOrder.F.orderType, OrderType.COLLAGE_ORDER);
        }
        if (StringUtil.isNotEmpty(startDate))
            builder.ge(MktOrder.F.createdTime, startDate + " 00:00:00");
        if (StringUtil.isNotEmpty(endDate))
        {
            builder.le(MktOrder.F.createdTime, endDate + " 23:59:59");
        }
        if (groupPkey != null)
        {
            MktOrderGroup group = orderGroupDao.get(groupPkey);
            if (group == null)
                throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
            List<String> orderList = group.getOrderList();
            if (!orderList.isEmpty())
                builder.in(MktOrder.F.pkey, orderList.toArray());
        }
        
        PageResult<MktOrderLineExcel> pageResult = builder.endJoin()
            .sort(MktOrderLine.F.orderPkey, true)
            .sort(MktOrderLine.F.pkey, false)
            .exec(MktOrderLineExcel.class);
        
        boolean flag = false;
        if ((Constant.Operation + ascriptionPkey).equals(CurrentSession.marketPkey()))
        {
            flag = true;
        }
        for (MktOrderLineExcel line : pageResult)
        {
            if (line.getRefundAmt() == null)
                line.setRefundAmt(BigDecimal.ZERO);
            if (line.getRefundPoint() == null)
                line.setRefundPoint(0);
            if (line.getStatus() != null && line.getStatus().getIndex() == 0)
            {
                line.setAmtn(BigDecimal.ZERO);
                line.setPstime("");
                line.setPayType(null);
            }
            if (flag)
            {
                if (!OrderType.INTEGRAL_PRESALE_ORDER.equals(line.getOrderType()))
                    line.setPstime(DateUtil.formatDate(line.getFhTime()));
                else if (line.getFhTime() != null)
                    line.setPstime(DateUtil.formatDate(line.getFhTime()));
            }
            if (line.getLineRefundAmt() != null && line.getLineRefundAmt().compareTo(line.getLineCouponAmt()) == 0)
            {
                List<MktOrderRefundLine> listOrderLinePkey = orderRefundLineDao.listOrderLinePkey(line.getPkey());
                int rp = 0;
                for (MktOrderRefundLine orl : listOrderLinePkey)
                {
                    if (orl.getRefundPoint() != null)
                    {
                        MktOrderRefund or = orderRefundDao.get(orl.getRefundPkey());
                        if (RefundStatus.REFUND_FINAL.equals(or.getStatus()))
                            rp += (orl.getRefundPoint());
                    }
                }
                line.setLineRefundPoint(rp);
            }
            else
            {
                line.setLineRefundPoint(0);
            }
            line.setTagName(orderTagDao.getTagName(line.getOrderPkey()));
        }
        return pageResult;
    }
}
