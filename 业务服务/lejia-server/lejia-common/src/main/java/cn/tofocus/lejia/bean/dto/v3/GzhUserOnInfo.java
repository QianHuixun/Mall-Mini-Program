package cn.tofocus.lejia.bean.dto.v3;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GzhUserOnInfo
{
    private Integer pkey;
    
    @Schema(description = "手机")
    private String mobile;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
}
