package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdAddressOption
{
    @Schema(description = "区域ID")
    private Long areaId;
    
    @Schema(description = "区域名")
    private String areaName;
}
