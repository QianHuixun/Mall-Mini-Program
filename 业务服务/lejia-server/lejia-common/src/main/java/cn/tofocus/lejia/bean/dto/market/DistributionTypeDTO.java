package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import cn.tofocus.lejia.bean.enums.DistributionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DistributionTypeDTO
{
    @Schema(description = "类型")
    private DistributionType type;
    
    
    @Schema(description = "地址")
    private String  address;
    
    @Schema(description = "电话")
    private String mobile;
    
    
    @Schema(description = "营业开始时间")
    private String  yytb;
    
    
    @Schema(description = "营业结束时间")
    private String  yyte;
    
    @Schema(description = "时间 分")
    private Integer minute;
    
   
    @Schema(description = "经度", required = true)
    private BigDecimal longitude;
    

    @Schema(description = "纬度", required = true)
    private BigDecimal latitude;
}
