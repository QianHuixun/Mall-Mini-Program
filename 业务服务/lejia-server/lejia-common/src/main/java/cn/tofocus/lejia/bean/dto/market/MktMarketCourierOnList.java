package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMarketCourierOnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "姓名")
    private String name;
    
    @Schema(description = "手机")
    private String mobile;
    
    @Schema(description = "姓名+手机")
    private String value;
    
    @Schema(description = "true 已选")
    private Boolean selected;
}
