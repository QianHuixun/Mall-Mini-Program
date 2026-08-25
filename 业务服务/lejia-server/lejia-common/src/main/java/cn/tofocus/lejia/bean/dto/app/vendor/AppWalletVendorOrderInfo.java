package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWalletVendorOrderInfo
{
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "小票码")
    private Integer smallTicket;
    
    @Schema(description = "核销码")
    private String pickupCode;
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;
    
    @Schema(description = "下单时间")
    private Date orderTime;
    
    @Schema(description = "配送时间")
    private String pstime;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "应结金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "交易佣金")
    private BigDecimal commissions;
    
    @Schema(description = "手续费")
    private BigDecimal payComm;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
    
    @Schema(description = "是否显示打包费用 true:显示")
    private Boolean isPackingCharge;

    @Schema(description = "手续费承担")
    private CommissionType commissionType;
    
    @Schema(description = "订单明细")
    private List<VendorOrderWalletOnPage> listOrder;
}
