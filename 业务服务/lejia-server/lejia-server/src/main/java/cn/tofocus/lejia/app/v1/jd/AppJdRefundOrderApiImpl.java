package cn.tofocus.lejia.app.v1.jd;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.domain.jd.JdOrderRefundManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/app/jd/refund")
@RestController
public class AppJdRefundOrderApiImpl
{
    @Autowired
    private JdOrderRefundManager manager;
    
    @Operation(summary = "获取京东上门取件时间", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/generateTimeList")
    public Result<List<String>> generateTimeList()
    {
        return new Result<>(manager.generateTimeList());
    }
    
    @Operation(summary = "填写第三方寄回快递", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/updateSendInfo")
    public Result<Boolean> updateSendInfo(
        @RequestParam(value = "refundPkey")@Parameter(description = "退款订单主键")Integer refundPkey, 
        @RequestParam(value = "courierCompany")@Parameter(description = "快递公司")String courierCompany,
        @RequestParam(value = "courierNumber")@Parameter(description = "快递单号")String courierNumber,
        @RequestParam(value = "postage")@Parameter(description = "运费")BigDecimal postage)
    {
        return new Result<>(manager.updateSendInfo(refundPkey, courierCompany, courierNumber, postage));
    }
    
    @Operation(summary = "取消售后", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/cancelAfsApply")
    public Result<Boolean> cancelAfsApply(@RequestParam(value = "refundPkey")@Parameter(description = "退款订单主键")Integer refundPkey)
    {
        return new Result<>(manager.cancelAfsApply(refundPkey));
    }

    @Operation(summary = "售后确认完成", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/confirmed")
    public Result<Boolean> confirmed(@RequestParam(value = "refundPkey")@Parameter(description = "退款订单主键")Integer refundPkey)
    {
        return new Result<>(manager.confirmed(refundPkey));
    }
    
    @Operation(summary = "获取快递公司下拉", tags = AppTags.mobileJdOrderV2)
    @PostMapping("/courier/drop")
    public Result<List<String>> courierDrop()
    {
        return new Result<>(manager.courierDrop());
    }
}
