package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.db.dto.JoinDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 商品供应分页详情
 */
@Data
@Schema(description = "商品供应分页详情")
public class MktSupplyPageDetail
{
    /**
     * 商品供应主键 pkey
     */
    @Schema(description = "主键")
    private Integer pkey;

    /**
     * 规格
     */
    @Schema(description = "规格")
    private String space;

    /**
     * 规格名称
     */
    @Schema(description = "规格名称")
    private String spaceName;


    /**
     * 供应商pkey vendor
     */
    @Schema(description = "供应商pkey")
    //@JsonIgnore
    private Integer vendor;


    /**
     * 供应商名称 vendor
     */
    @Schema(description = "供应商名称")
//    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor", cascade = true)
    private String vendorName;


    /**
     * 采购价 purchasing_price
     */
    @Schema(description = "采购价")
    private BigDecimal purchasingPrice;

    @Schema(description = "佣金费率")
    public BigDecimal getCommissionRate()
    {
        if(commissionRate1 != null)
            return commissionRate1;
        else
            return commissionRate2;
    }
    
    @JsonIgnore
    private BigDecimal commissionRate1;
    
    @JsonIgnore
    private BigDecimal commissionRate2;
    
    /**
     * 派送顺序 sort
     */
    @Schema(description = "派送顺序")
    private Integer sort;


    /**
     * 是否启用 enabled
     */
    @Schema(description = "是否启用")
    private Boolean enabled;

    /**
     * 是否存在 isExist
     */
    @Schema(description = "是否存在")
    private Boolean isExist;
}
