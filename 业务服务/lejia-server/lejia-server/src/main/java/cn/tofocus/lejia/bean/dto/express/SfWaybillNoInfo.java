package cn.tofocus.lejia.bean.dto.express;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfWaybillNoInfo
{
    @Schema(description = "运单号类型 1：母单 2 :子单 3 : 签回单")
    private Integer waybillType;
    
    @Schema(description = "顺丰运单号")
    private String waybillNo;
    
    @Schema(description = "箱号")
    private String boxNo;
    
    @Schema(description = "长(cm)")
    private BigDecimal length;
    
    @Schema(description = "宽(cm)")
    private BigDecimal width;
    
    @Schema(description = "高(cm)")
    private BigDecimal height;
    
    @Schema(description = "重量(kg)")
    private BigDecimal weight;
    
    @Schema(description = "体积（立方厘米）")
    private BigDecimal volume;
    
    /**
     * @return 是否母单
     */
    public boolean isMaster()
    {
        return waybillType == 1;
    }
}
