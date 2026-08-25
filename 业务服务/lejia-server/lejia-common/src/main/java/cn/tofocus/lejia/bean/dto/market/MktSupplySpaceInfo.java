package cn.tofocus.lejia.bean.dto.market;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库——规格列表
 */
@Data
@Schema(description = "商品供应库——供应商列表")
public class MktSupplySpaceInfo
{
    /**
     * 规格pkey
     */
    @Schema(description = "规格pkey")
    private Integer pkey;

    /**
     * 规格名称
     */
    @Schema(description = "规格名称")
    private String name;


}
