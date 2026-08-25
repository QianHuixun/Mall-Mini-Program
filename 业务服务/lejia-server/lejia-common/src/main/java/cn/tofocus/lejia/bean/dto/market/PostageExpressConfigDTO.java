package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostageExpressConfigDTO {

	private List<MktPostageConfigOnList> pcList = new ArrayList<>();
	
	/**
	 * 夜间时间配置
	 */
	@Schema(description = "夜间时间配置", required = true)
	private String yjTime;

	/**
	 * 夜间运费配置
	 */
	@Schema(description = "夜间运费配置", required = true)
	@Min(0)
	private Integer yjPos;

	/**
	 * DecimalMin
	 * value：最小值
	 * inclusive：是否可以等于最小值，默认true，>= 最小值
	 *
	 * Digits
	 * integer： 整数位最多几位
	 * fraction：小数位最多几位
	 */
	@Schema(description = "配送范围", required = true)
	@Digits(integer = 10, fraction = 1, message = "整数位最多10位，小数位最多1位")
	@DecimalMin(value = "0.0", message = "配送范围最小值为0.0")
	private BigDecimal deliveryRange;

	/**
	 * 配送时间
	 */
	@Schema(description = "配送时间", required = true)
	@Convert(converter = ListConverter.class)
	private List<String> psTime;
	
	private BigDecimal freeDelivery;
	
	private Boolean isFree;
	
	
    @Schema(description = "配送设置，true 按常规，false 按统一金额")
	private Boolean  distributionConfig;
	    
	@Schema(description = "统一设定金额")
	@Min(0)
	private BigDecimal fee;
	    
	@Schema(description = "起步价")
	@Min(0)
	private BigDecimal startingPrice;
	
	@Schema(description = "配送时间 时（废弃）")
	@Min(0)
	@Max(23)
	@Deprecated
    private Integer phour;
	    
    @Schema(description = "配送时间 分（废弃）")
	@Min(0)
	@Max(59)
    @Deprecated
	private Integer pminute;
    
	
    @Schema(description = "自提时间 时")
    @Min(0)
    @Max(23)
    private Integer pickupHour;
    
    @Schema(description = "自提时间分")
    @Min(0)
    @Max(59)
    private Integer pickupMinute;
    
    @Schema(description = "自提日期")
    private DeliveryDate pickupDeliveryDate;

    @Schema(description = "预计送达时间配置")
    private List<MktDeliveryTimeConfig> deliveryTimes;
    
    
    @Schema(description = "满减运费1")
    private BigDecimal reachOne;
    
    @Schema(description = "满减运费2")
    private BigDecimal reachTwo;
    
    @Schema(description = "减少运费1")
    private BigDecimal reductionDeliveryOne;
    
    @Schema(description = "减少运费2")
    private BigDecimal reductionDeliveryTwo;
    
    @Schema(description = "是否减少运费1")
    private Boolean isReductionOne;
    
    @Schema(description = "是否减少运费2")
    private Boolean isReductionTwo;
	
}
