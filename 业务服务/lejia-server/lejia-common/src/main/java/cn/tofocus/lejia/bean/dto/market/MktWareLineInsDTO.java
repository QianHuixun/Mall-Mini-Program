package cn.tofocus.lejia.bean.dto.market;


import java.math.BigDecimal;

import javax.persistence.Column;

import cn.tofocus.lejia.bean.enums.WareType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_ware_line
* @author zdw 2020-09-25
*/

@Data
public class MktWareLineInsDTO
{

	@Schema(description = "类型", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private WareType wareType;

	@Schema(description = "商品pkey", required = true)
    private Integer goods;

	@Schema(description = "商品名称", required = true)
    private String goodsName;

	@Schema(description = "规格pkey", required = true)
    private Integer space;

	@Schema(description = "规格名称", required = true)
    private String spaceName;

	@Schema(description = "批次号", required = false)
    private String orderNumber;

	@Schema(description = "采购价", required = false)
    private BigDecimal price;

	@Schema(description = "调整的数量", required = true)
    private Integer num;

	@Schema(description = "供应商", required = false)
    private String supplier;

	@Schema(description = "备注", required = false)
    private String remark;


}