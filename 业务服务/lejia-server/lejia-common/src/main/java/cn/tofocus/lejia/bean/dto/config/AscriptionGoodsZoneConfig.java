package cn.tofocus.lejia.bean.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AscriptionGoodsZoneConfig
{
    @Schema(description = "积分商城专区显示名称")
    private String integralDisplayName = "积分商城";
    
    @Schema(description = "预售专区显示名称")
    private String integralPresaleDisplayName = "预售专区";
    
    @Schema(description = "滨农优品专区显示名称")
    private String integralBNYPDisplayName = "滨农优品";
    
    @Schema(description = "民生豆专区显示名称")
    private String integralMsdDisplayName = "民生专区";
}
