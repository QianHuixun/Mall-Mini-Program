package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cn.tofocus.lejia.bean.enums.MType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库——单项信息
 */
@Data
@Schema(description = "商品供应库——单项信息")
public class MktSupplyInfo
{
    @Schema(description = "市场pkey")
    private String marketPkey;
    
    @Schema(description = "市场名称，仅仅detail查询时用，新增/更新时不用传递")
    private String marketName;
    
    @Schema(description = "商品pkey")
    @NotNull(message = "商品pkey不能为空")
    private Integer goodsPkey;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    @NotNull(message = "商品属性不能为空")
    private MType mType;
    
    @Schema(description = "商品名称，仅仅detail查询时用，新增/更新时不用传递")
    private String goodsName;
    
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate2;
    
    @Schema(description = "采购信息")
    @NotEmpty(message = "采购信息不能为空")
    @Valid
    private List<MktSupplyDetailInfo> list;
    
}
