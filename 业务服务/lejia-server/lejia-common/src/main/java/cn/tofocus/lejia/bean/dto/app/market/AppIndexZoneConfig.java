package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppIndexZoneConfig
{
    @Schema(description = "特价商品专区显示名称")
    private String specialDisplayName;
    
    @Schema(description = "积分商城专区显示名称")
    private String integralDisplayName;
    
    @Schema(description = "预售专区显示名称")
    private String integralPresaleDisplayName;
    
    @Schema(description = "滨农优品专区显示名称")
    private String integralBNYPDisplayName;
    
    @Schema(description = "滨农民生豆专区显示名称")
    private String integralMsdDisplayName;

    @Schema(description = "京东优选专区显示名称")
    private String jdGoodsDisplayName;
}
