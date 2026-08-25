package cn.tofocus.lejia.api.v2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.OrderRefundExcel;
import cn.tofocus.lejia.bean.dto.SysOrderRefundExcel;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.PreUpdRefundOrderInfo;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderDetails;
import cn.tofocus.lejia.bean.dto.refund.RefundUpdOnInfo;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.pay.WxRefundManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v2/market/refund")
@RestController
public class RefundOrderApiImpl
{
    @Autowired
    private OrderRefundManager manager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private WxRefundManager wxRefundManager;
    
    @Operation(summary = "获取市场退款订单列表", tags = ApiTags.WEB_REFUND)
    @PostMapping("/query")
    public Result<OrderRefundOnInfo> queryOrderRefund(
        @RequestParam(value = "page", defaultValue = "0", required = false)
        int page, @RequestParam(value = "pagesize", defaultValue = "0", required = false)
        int pagesize, @RequestParam(value = "code", required = false)
        String code, @RequestParam(value = "status", required = false)
        List<RefundStatus> status, @RequestParam(value = "startDate", required = false)
        String startDate, @RequestParam(value = "endDate", required = false)
        String endDate)
    {
        return new Result<>(manager.queryOrderRefund(page, pagesize, code, status, startDate, endDate));
    }
    
    @Operation(summary = "同意退款", tags = ApiTags.WEB_REFUND)
    @PostMapping("/agree")
    public Result<Boolean> agreeRefund(@RequestParam(value = "pkey") @Parameter(description = "退款订单主键") Integer pkey, 
        @RequestParam(value = "delDesc", required = false) @Parameter(description = "处理意见,可不填") String delDesc)
    {
        return new Result<>(manager.agree(pkey, delDesc));
    }
    
    @Operation(summary = "微信退款重新退款", tags = ApiTags.WEB_REFUND)
    @PostMapping("/weixin/again")
    public Result<Boolean> weixinAgain(@RequestParam(value = "refundPkey",required = false) @Parameter(description = "退款订单主键") Integer refundPkey,
        @RequestParam(value = "orderPkey",required = false) @Parameter(description = "订单主键") Integer orderPkey)
    {
        return new Result<>(manager.weixinAgain(refundPkey, orderPkey));
    }
    
    @Operation(summary = "拒绝退款", tags = ApiTags.WEB_REFUND)
    @PostMapping("/refuse")
    public Result<Boolean> refuseRefund(@RequestParam(value = "pkey")
    @Parameter(description = "退款订单主键")
    Integer pkey, @RequestParam(value = "delDesc", required = false)
    @Parameter(description = "处理意见,可不填")
    String delDesc)
    {
        return new Result<>(manager.refuseRefund(pkey, delDesc));
    }
    
    @Operation(summary = "预计算修改退款金额", tags = ApiTags.WEB_REFUND)
    @PostMapping("/upd/line/pre")
    public Result<PreUpdRefundOrderInfo> preUpdRefundLine(@RequestBody
    RefundUpdOnInfo info)
    {
        return new Result<>(manager.preUpdRefundLine(info));
    }
    
    @Operation(summary = "修改退款金额", tags = ApiTags.WEB_REFUND)
    @PostMapping("/upd/line")
    public Result<Boolean> updRefundLine(@RequestBody
    RefundUpdOnInfo info)
    {
        return new Result<>(manager.updRefundLine(info));
    }
    
    @Operation(summary = "查看退款订单详情", tags = ApiTags.WEB_REFUND)
    @PostMapping("/get")
    public Result<RefundOrderDetails> getRefund(@RequestParam(value = "pkey")
    @Parameter(description = "退款订单主键")
    Integer pkey)
    {
        return new Result<>(manager.getRefundOrder(pkey));
    }
    
    @Operation(summary = "导出市场退款订单列表EXCEL", tags = ApiTags.WEB_REFUND)
    @PostMapping("/export")
    public Result<Boolean> queryOrderRefund(@RequestParam(value = "code", required = false)
    String code, @RequestParam(value = "status", required = false)
    List<RefundStatus> status, @RequestParam(value = "startDate", required = false)
    String startDate, @RequestParam(value = "endDate", required = false)
    String endDate, HttpServletResponse response)
    {
        OrderRefundOnInfo info = manager.queryOrderRefund(0, 50000, code, status, startDate, endDate);
        OutputStream out = null;
        try
        {
            String fileName = new String("市场退款订单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            if ((Constant.Operation + CurrentSession.ascriptionPkey()).equals(CurrentSession.marketPkey()))
            {
                excelHelper.exportExcel(BeanUtil.beanListFrom(SysOrderRefundExcel.class,
                    info.getOnPage().getContent()), "Sheet1", out, SysOrderRefundExcel.class, null);
            }
            else
                excelHelper.exportExcel(BeanUtil.beanListFrom(OrderRefundExcel.class,
                    info.getOnPage().getContent()), "Sheet1", out, OrderRefundExcel.class, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return new Result<>(true);
    }
    
    @PostMapping("/test")
    public Result<Boolean> test(String transactionId, String outRefundNo, Long refund, Long total, String merchantId,
        String certificateSerialNo, String configLocalpath, long payerTotal, long payerRefund, long settlementTotal,
        long settlementRefund, long discountRefund)
    {
        wxRefundManager.createRefundOrderV2(transactionId,
            outRefundNo,
            refund,
            total,
            merchantId,
            certificateSerialNo,
            configLocalpath,
            payerTotal,
            payerRefund,
            settlementTotal,
            settlementRefund,
            discountRefund);
        //        byte[] certData = null;
        //        try
        //        {
        //            InputStream certStream = new FileInputStream(new File(configLocalpath));
        //            certData = IOUtils.toByteArray(certStream);
        //            ByteArrayInputStream certBis = new ByteArrayInputStream(certData);
        //            System.out.println(certBis.toString());
        //        }
        //        catch (Exception e)
        //        {
        //            e.printStackTrace();
        //        }
        return new Result<>(true);
    }
}
