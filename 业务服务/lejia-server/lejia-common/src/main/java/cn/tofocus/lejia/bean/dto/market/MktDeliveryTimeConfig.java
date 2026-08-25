package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktDeliveryTimeConfig
{
    /**
    * 距离
    */
    @Schema(description = "距离")
    private BigDecimal distance;
    
    /**
    * 小时
    */
    @Schema(description = "小时")
    private Integer hour;
    
    /**
    * 分钟
    */
    @Schema(description = "分钟")
    private Integer minute;
    
}
