package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorOrderMainDTO
{
    @Schema(description = "order订单id")
    private Integer orderPkey;

    @Schema(description = "商户订单DTO列表")
    private List<MktVendorOrderDTO> list;

    @Schema(description = "金额")
    private BigDecimal amt;

    @Schema(description = "总订单数")
    private Integer orderCount;

    @Schema(description = "总采购数")
    private Integer purchaseCount;

    @Schema(description = "分页数据")
    private PageResult<MktVendorOrderDTO> pageList;
}
