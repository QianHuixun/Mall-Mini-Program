package cn.tofocus.lejia.bean.dto.app.goods;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppGtypeGoodsSpaceDTO 
{
	@Schema(description = "规格pkey")
	private Integer space;
	@Schema(description = "价格")
	private BigDecimal price;
	@Schema(description = "原价")
    private BigDecimal priceOld;
}
