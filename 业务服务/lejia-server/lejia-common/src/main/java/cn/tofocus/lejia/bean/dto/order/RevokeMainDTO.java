package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
/**
 * 合作商户-撤销记录主DTO
 */
public class RevokeMainDTO
{
    @Schema(description = "总订单数")
    private Integer orderCount;

    @Schema(description = "总采购数")
    private Integer purchaseCount;

    @Schema(description = "总采购金额数")
    private BigDecimal purchaseAmt;

    @Schema(description = "分页数据")
    private PageResult<RevokeDTO> pageList;
}
