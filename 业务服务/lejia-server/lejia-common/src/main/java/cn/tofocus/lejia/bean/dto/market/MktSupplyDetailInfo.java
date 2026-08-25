package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import cn.tofocus.db.dto.JoinDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库——单项明细信息
 */
@Data
@Schema(description = "商品供应库——单项明细列表")
public class MktSupplyDetailInfo
{
    @Schema(description = "主键，更新时请传递")
    private Integer pkey;
    
    @Schema(description = "规格")
    @NotNull(message = "规格不能为空")
    private Integer space;
    
    @Schema(description = "规格名称，仅仅detail查询时用，新增/更新时不用传递")
    private String spaceName;
    
    @Schema(description = "供应商pkey")
    @NotNull(message = "供应商pkey不能为空")
    private Integer vendor;
    
    @Schema(description = "供应商名称，仅仅detail查询时用，新增/更新时不用传递")
    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor")
    private String vendorName;
    
    @Schema(description = "采购价")
    private BigDecimal purchasingPrice;
    
    @Schema(description = "佣金费率1")
    private BigDecimal commissionRate1;
    
    @Schema(description = "佣金费率2")
    private BigDecimal commissionRate2;
    
    @Schema(description = "派送顺序")
    private Integer sort;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
}
