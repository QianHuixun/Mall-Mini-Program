package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppCheckFarmerRangInfo
{
    @Schema(description = "菜场名称")
    private String name;
    
    @Schema(description = "是否在配送范围内")
    private Boolean inRange;
    
    @Schema(description = "市场地址")
    private String addr;
    
    @Schema(description = "距离")
    private BigDecimal distance;
}
