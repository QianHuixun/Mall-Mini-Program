package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReportOnList
{
    
    @Schema(description = "总采购笔数")
    private Integer purchaseNum;
    
    @JsonIgnore
    private BigDecimal purchaseAmt;
    
    @JsonIgnore
    private BigDecimal orderComm = BigDecimal.ZERO;
    
    @JsonIgnore
    private BigDecimal settlementAmt = BigDecimal.ZERO;
    
    @JsonIgnore
    private Integer vendor;
    
    @Schema(description = "优惠金额", required = false)
    @JsonIgnore
    private BigDecimal discountAmt;
    
    @Schema(description = "邮费", required = false)
    @JsonIgnore
    private BigDecimal postage;
    
    @Schema(description = "差额", required = false)
    @JsonIgnore
    private BigDecimal difference;
    
    @JoinDTO(from = "vendor", dataQuery = "mktVendorDao", type = VendorInfo.class)
    private VendorInfo vendorInfo;
    
    @Schema(description = "总采购金额")
    public String getPurchaseAmtStr()
    {
        if (purchaseAmt != null) return purchaseAmt.stripTrailingZeros().toPlainString();
        return "0";
    }
    
    @Schema(description = "交易佣金(元)")
    public String getOrderCommStr()
    {
        if (orderComm != null) return orderComm.stripTrailingZeros().toPlainString();
        return "0";
    }
    
    @Schema(description = "结算金额(元)")
    public String getSettlementAmtStr()
    {
        if (settlementAmt != null) return settlementAmt.stripTrailingZeros().toPlainString();
        return "0";
    }
    
}
