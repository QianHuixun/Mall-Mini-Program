package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户结算主DTO
 */
@Data
public class SettlementMainDTO
{
    @Schema(description = "总采购/交易笔数")
    private Integer purchaseCount;

    @Schema(description = "总采购/交易金额")
    private BigDecimal purchaseAmt;

    @Schema(description = "已结算采购/交易笔数")
    private Integer alreadycount;

    @Schema(description = "已结算采购/交易金额")
    private BigDecimal alreadyAmt;

    @Schema(description = "未结算采购/交易笔数")
    private Integer awaitCount;

    @Schema(description = "未结算采购/交易金额")
    private BigDecimal awaitAmt;

    @Schema(description = "分页数据")
    private PageResult<SettlementDTO> pageList;
}
