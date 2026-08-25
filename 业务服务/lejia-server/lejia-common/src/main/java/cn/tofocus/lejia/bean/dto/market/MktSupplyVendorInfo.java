package cn.tofocus.lejia.bean.dto.market;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库——供应商列表
 */
@Data
@Schema(description = "商品供应库——供应商列表")
public class MktSupplyVendorInfo
{
    /**
     * 供应商pkey
     */
    @Schema(description = "供应商pkey")
    private Integer pkey;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称")
    private String name;

    /**
     * 是否可以选择
     */
    @Schema(description = "是否可以选择")
    private Boolean isExist;

}
