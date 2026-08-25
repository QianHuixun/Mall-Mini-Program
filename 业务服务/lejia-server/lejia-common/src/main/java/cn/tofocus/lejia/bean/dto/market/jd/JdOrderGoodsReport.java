package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEntity;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "F")
public class JdOrderGoodsReport
{
    @Schema(description = "skuId")
    private Long pkey;
    
    @Schema(description = "商品名")
    private String goodsName;

    @Schema(description = "商品规格")
    private String spaceName;
    
    @Schema(description = "订单笔数")
    private Long orderCount;
    
    @Schema(description = "销售数量")
    private Long goodsCount;
    
    @Schema(description = "销售额")
    private BigDecimal amt;
}
