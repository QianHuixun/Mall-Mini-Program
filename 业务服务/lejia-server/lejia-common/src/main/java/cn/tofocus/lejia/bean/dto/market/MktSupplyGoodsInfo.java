package cn.tofocus.lejia.bean.dto.market;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库——商品列表
 */
@Data
@Schema(description = "商品供应库——商品列表")
public class MktSupplyGoodsInfo
{
    /**
     * 商品pkey
     */
    @Schema(description = "商品pkey")
    private Integer pkey;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String title;

    /**
     * 是否可以选择
     */
    @Schema(description = "是否可以选择")
    private Boolean enabled;

    /**
     * 一级分类mkt_gtype
     */
    @JsonIgnore
    private Integer gtype;

}
