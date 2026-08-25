package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberAddrFourArea
{
    @Schema(description = "省")
    private String pro;
    
    @Schema(description = "市")
    private String city;
    
    @Schema(description = "区")
    private String area;
    
    @Schema(description = "街道")
    private String town;
}
