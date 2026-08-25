package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 选中的商户结算详情DTO
 */
@Data
public class SettlementDetailDTO
{
    
    /**
    * 商户订单主键列表
    */
    @Schema(description = "主键列表")
    private List<Integer> pkeys;

    /**
     * 采购开始日期
     */
    @Schema(description = "采购开始日期")
    private String startDate;

    /**
     * 采购结束日期
     */
    @Schema(description = "采购结束日期")
    private String endDate;
    
    /**
    * 总商户数
    */
    @Schema(description = "总商户数")
    private Integer vendorCount;

    @Schema(description = "总采购笔数")
    private Integer purchaseCount;

    @Schema(description = "总采购金额")
    private BigDecimal purchaseAmt;

    @Schema(description = "结算备注")
    private String settlementRemark;
    
}
