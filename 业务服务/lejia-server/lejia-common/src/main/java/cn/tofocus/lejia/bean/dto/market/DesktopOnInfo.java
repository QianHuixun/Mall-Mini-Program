package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DesktopOnInfo
{
    private Integer pkey;
    
    @Schema(description = "桌位号")
    private String name;
    
    @Schema(description = "创建时间")
    private Date createdTime;
}
