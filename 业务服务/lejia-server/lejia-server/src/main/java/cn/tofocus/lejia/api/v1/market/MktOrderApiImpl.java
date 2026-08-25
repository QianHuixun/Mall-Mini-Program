package cn.tofocus.lejia.api.v1.market;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tofocus.core.data.KeyValue;
import cn.tofocus.db.dto.DtoEnhance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.MktOrderMarketExcel;
import cn.tofocus.lejia.bean.dto.MktOrderOnListExcel;
import cn.tofocus.lejia.bean.dto.app.linshi.CardLinshiDto;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderVendorDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderGroupOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
import cn.tofocus.lejia.bean.dto.refund.RefundOnLine;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.refund.WebOrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.WebRefundOnLine;
import cn.tofocus.lejia.bean.dto.refund.WebRefundOrderOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.RefundType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.v2.AppOrderV2Manager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/order")
@RestController
public class MktOrderApiImpl implements MktOrderApi
{
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private AppOrderV2Manager manager;
    
    @Autowired
    private OrderRefundManager orderRefundManager;
    
    @Autowired(required = false)
    private ExcelHelper excelHelper;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private DtoEnhance dtoEnhance;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Override
    public Result<PageResult<MktOrderOnList>> queryOrder(int page, int pagesize, OrderOir orderOir, String startDate,
        String endDate, OrderStatus status, String code, String mobile, String memberMobile, OrderType orderType,
        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh, ExpressType expressType, List<PayType> payTypes, List<Integer> tags,
        DistributionType distributionType)
    {
        return new Result<>(orderManager.queryOrder(page,
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
            CurrentSession.marketPkey(),
            false,
            expressType,
            payTypes,
            tags,
            distributionType));
    }
    
    @Override
    public Result<Map<String, Object>> queryOrderSum(OrderOir orderOir, String startDate, String endDate,
        OrderStatus status, String code, String mobile, String memberMobile, OrderType orderType, Integer groupPkey,
        String vrifyCode, Boolean priceAbnormal, Boolean priceAbnormalFinsh, List<PayType> payTypes, List<Integer> tags,
        DistributionType distributionType)
    {
        return new Result<>(orderManager.queryOrderSum(orderOir,
            startDate,
            endDate,
            status,
            code,
            mobile,
            memberMobile,
            orderType,
            groupPkey,
            vrifyCode,
            priceAbnormal,
            priceAbnormalFinsh,
            CurrentSession.marketPkey(),
            payTypes,
            tags,
            distributionType));
    }
    
    @Override
    public Result<Boolean> sendOrder(Integer pkey, String logistics, String code)
    {
        orderManager.sendOrder(pkey, logistics, code);
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> sendOrderTest(Integer pkey, String logistics, String code)
    {
        orderManager.sendOrderTest(pkey, logistics, code);
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> paidan(Integer pkey, Integer courier)
    {
        orderManager.paidan(pkey, courier);
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> arrivedExpress(Integer pkey)
    {
        return new Result<>(orderManager.arrivedExpress(pkey));
    }
    
    @Override
    public Result<List<MktCourier>> queryCourier()
    {
        return new Result<>(orderManager.queryCourier());
    }
    
    @Override
    public Result<MktAppOrderDTO> loadOrder(Integer pkey)
    {
        MktAppOrderDTO order = appOrderManager.loadInitOrder(pkey, false);
        return new Result<>(order);
    }
    
    @Override
    public Result<List<WebOrderRefundOnInfo>> loadRefundOrder(Integer pkey)
    {
        List<WebOrderRefundOnInfo> res = orderRefundManager.loadRefundOrder(pkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> agreeRefund(WebRefundOrderOnInfo info)
    {
        MktOrder order = orderDao.get(info.getPkey());
        if (!OrderStatus.DELIVERED_ORDER.equals(order.getStatus())
            && !OrderStatus.SHIPPED_ORDER.equals(order.getStatus())
            && !OrderStatus.WAIT_ARRIVAL_ORDER.equals(order.getStatus())
            && !OrderStatus.ARRIVED_ORDER.equals(order.getStatus()))
            throw TofocusException.of(LejiaErrCode.ORDER_STATUS_REFUND_ERROR);
        RefundOrderOnInfo bean = new RefundOrderOnInfo();
        bean.setPkey(info.getPkey());
        bean.setReason(info.getReason());
        List<RefundOnLine> lines = new ArrayList<>();
        for (WebRefundOnLine wrl : info.getLines())
        {
            MktOrderLine orderLine = orderLineDao.get(wrl.getPkey());
            int sum = orderRefundLineDao.sumNumByOrderLinePkey(wrl.getPkey());
            BigDecimal amt = orderRefundLineDao.sumAmtByOrderLinePkey(wrl.getPkey());
            int num = 0;
            if (wrl.getRefundAmt().add(amt).subtract(orderLine.getCouponAmt()).compareTo(BigDecimal.ZERO) > 0)
                throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR);
            else if (wrl.getRefundAmt().add(amt).subtract(orderLine.getCouponAmt()).compareTo(BigDecimal.ZERO) == 0)
                num = orderLine.getNum() - sum;
            else
            {
                num = (int)(wrl.getRefundAmt().add(amt).divide(orderLine.getCouponAmt(),2,BigDecimal.ROUND_HALF_UP).doubleValue()
                    * orderLine.getNum() - sum);
                if (num < 0)
                    num = 0;
            }
            RefundOnLine rl = new RefundOnLine();
            rl.setPkey(wrl.getPkey());
            rl.setRefundAmt(wrl.getRefundAmt());
            rl.setNum(num);
            lines.add(rl);
        }
        bean.setLines(lines);
        Integer pkey =
            orderRefundManager.applyForOrderRefund(bean, RefundStatus.REFUND_APPLYING, RefundType.REFUND_FARMER);
        orderRefundManager.agree(pkey, null);
        return new Result<>(true);
    }
    
    @Override
    public Result<List<String>> listReasonDrop(OrderStatus status)
    {
        List<String> res = new ArrayList<>();
        if (OrderStatus.DELIVERED_ORDER.equals(status))
        {
            res.add("商品已售完");
            res.add("忘记填备注");
            res.add("买多/买错/买少");
            res.add("信息填写错误（地址/时间/联系方式）");
            res.add("用户不想要了");
        }
        if (OrderStatus.SHIPPED_ORDER.equals(status) 
            || OrderStatus.WAIT_ARRIVAL_ORDER.equals(status)
            || OrderStatus.WAIT_WRITEOFF_ORDER.equals(status))
        {
            res.add("用户不想要了");
            res.add("商品有异物/腐烂/变质");
            res.add("商品出现破损、变形、污渍");
            res.add("图片/产地/规格等描述不符");
            res.add("商品缺斤少两");
            res.add("收到的商品错了");
            res.add("收到的商品少了");
            res.add("生产日期/保质期与商品描述不符");
            res.add("质量问题");
            res.add("商家发错货");
            res.add("假冒品牌");
        }
        res.add("其他原因");
        return new Result<>(res);
    }
    
    @Override
    public Result<MktAppOrderVendorDTO> loadOrderVenodr(Integer pkey)
    {
        return new Result<>(appOrderManager.loadOrderVenodr(pkey));
    }
    
    @Override
    public Result<Boolean> getThirdPartyStatus(Integer pkey)
    {
        return new Result<>(appOrderManager.getThirdPartyStatus(pkey));
    }
    
    @Override
    public Result<PageResult<MktOrderGroupOnList>> queryOrderGroup(int page, int pagesize, Integer goods,
        OrderGroupStatus status)
    {
        return new Result<>(orderManager.queryOrderGroup(page, pagesize, goods, status));
    }
    
    @Operation(summary = "导出商城订单Excel", tags = ApiTags.custOrder)
    @GetMapping(value = "/export/orderexcel")
    public Result<Boolean> downOrder(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000", required = false) @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "orderOir", required = true) @Parameter(description = "订单来源") OrderOir orderOir,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机") String memberMobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "purchaseStatus", required = false) @Parameter(description = "采购状态") PurchaseStatus purchaseStatus,
        @RequestParam(value = "groupPkey", required = false) @Parameter(description = "团购订单主键") Integer groupPkey,
        @RequestParam(value = "farmer", required = false) String farmer,
        @RequestParam(value = "vrifyCode", required = false) String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false, defaultValue = "false") @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false, defaultValue = "false") @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh,
        @RequestParam(value = "expressType", required = false) @Parameter(description = "骑手类型") ExpressType expressType,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        @RequestParam(value = "distributionType", required = false) @Parameter(description = "配送类型") DistributionType distributionType,
        HttpServletResponse response)
    {
        OutputStream out = null;
        Integer ascription = CurrentSession.ascriptionPkey();
        try
        {
            String fileName = new String("商城订单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            PageResult<MktOrderOnList> order = orderManager.queryOrder(page,
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
                true,
                expressType,
                payTypes,
                tags,
                distributionType);
            //			PageResult<MktOrderOnList> order = orderManager.queryOrder(0, 10, OrderOir.POINTS_MALL, null, null, null, code, null, null, null);
            if ((Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
            {
                List<MktOrderOnListExcel> list = BeanUtil.beanListFrom(MktOrderOnListExcel.class, order.getContent());
                dtoEnhance.deal(MktOrderOnListExcel.class, list);
                excelHelper.exportExcel(list, "Sheet1", out, MktOrderOnListExcel.class, null);
            }
            else
            {
                List<MktOrderMarketExcel> list = BeanUtil.beanListFrom(MktOrderMarketExcel.class, order.getContent());
                excelHelper.exportExcel(list, "Sheet1", out, MktOrderMarketExcel.class, null);
            }
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return new Result<>(true);
    }
    
    @Operation(summary = "导出商城订单明细Excel", tags = ApiTags.custOrder)
    @GetMapping(value = "/export/orderLine")
    public Result<Boolean> exportOrderLine(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000", required = false) @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "orderOir", required = true) @Parameter(description = "订单来源") OrderOir orderOir,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "订单时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "订单时间-结束") String endDate,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") OrderStatus status,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "收货手机") String mobile,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机") String memberMobile,
        @RequestParam(value = "orderType", required = false) @Parameter(description = "订单类型") OrderType orderType,
        @RequestParam(value = "purchaseStatus", required = false) @Parameter(description = "采购状态") PurchaseStatus purchaseStatus,
        @RequestParam(value = "groupPkey", required = false) @Parameter(description = "团购订单主键") Integer groupPkey,
        @RequestParam(value = "farmer", required = false) String farmer,
        @RequestParam(value = "vrifyCode", required = false) String vrifyCode,
        @RequestParam(value = "priceAbnormal", required = false, defaultValue = "false") @Parameter(description = "价格异常") Boolean priceAbnormal,
        @RequestParam(value = "priceAbnormalFinsh", required = false, defaultValue = "false") @Parameter(description = "价格异常(已确认)") Boolean priceAbnormalFinsh,
        @RequestParam(value = "expressType", required = false) @Parameter(description = "骑手类型") ExpressType expressType,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes,
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags,
        HttpServletResponse response)
    {
        orderManager.exportOrderLine(page,
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
            true,
            expressType,
            payTypes,
            tags,
            response);
        return new Result<>(true);
    }
    
    @Operation(summary = "打印软件下载", tags = ApiTags.custOrder)
    @GetMapping(value = "/down/dy")
    public void downTemplate(HttpServletRequest request, HttpServletResponse response)
    {
        FileUtil.buildExcelDocument("qd.zip", "CLodop_Setup.zip", "/data/tofocus/server/zyysc/", request, response);
    }
    
    @Override
    public Result<Boolean> newOrder()
    {
        return new Result<>(orderManager.newOrder());
    }
    
    @Override
    public Result<Integer> voiceOrder()
    {
        return new Result<>(orderManager.voiceOrder());
    }
    
    @Override
    public Result<Integer> pendingOrder()
    {
        return new Result<>(orderManager.pendingOrder());
    }
    
    @Override
    public Result<Boolean> updatePickupCodeStatus(Integer pkey)
    {
        Boolean rb = orderManager.updatePickupCodeStatus(pkey);
        return new Result<>(rb);
    }
    
    @Override
    public Result<Boolean> printOrder(Integer pkey)
    {
        return new Result<>(orderManager.printOrder(pkey, false));
    }
    
    @Override
    public Result<Boolean> insMapHuodongLinshi(CardLinshiDto info)
    {
        return new Result<>(manager.insMapHuodongLinshi(info.getPkey(), info));
    }
    
    @Override
    public Result<Boolean> insMapHuodongLinshiReurl(String code)
    {
        Boolean res = cardManager.insMemberCardLinshi(code);
        return new Result<>(res);
    }
    
    @Operation(summary = "生成活动礼品核销二维码-临时接口", tags = ApiTags.custOrder)
    @PostMapping(value = "/activity/qrCode")
    public void activityQrCode(@RequestParam(value = "name") String name, HttpServletResponse response)
    {
        manager.activityQrCode(name, response);
    }
    
    @Override
    public Result<Boolean> deliverSf(Integer pkey, Date pickupTime, String sendContent)
    {
        boolean sign = orderManager.deliverSf(pkey, pickupTime, sendContent);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> cancelDelivery(Long pkey)
    {
        boolean sign = orderManager.cancelDelivery(pkey);
        return new Result<>(sign);
    }

    @Override
    public Result<List<KeyValue<Integer, String>>> listPickupLocation(Integer pkey)
    {
        List<KeyValue<Integer, String>> list = orderManager.listPickupLocation(pkey);
        return new Result<>(list);
    }
    
    @Override
    public Result<Boolean> updPickupLocation(Integer pkey, Integer pickupLocation)
    {
        boolean sign = orderManager.updPickupLocation(pkey, pickupLocation);
        return new Result<>(sign);
    }

    @Override
    public Result<Boolean> waitArrival(List<Integer> pkeys)
    {
        Boolean res = orderManager.waitArrival(pkeys);
        return new Result<>(res);
    }

    @Override
    public Result<Boolean> waitWriteoff(List<Integer> pkeys)
    {
        Boolean res = orderManager.waitWriteoff(pkeys);
        return new Result<>(res);
    }
}
