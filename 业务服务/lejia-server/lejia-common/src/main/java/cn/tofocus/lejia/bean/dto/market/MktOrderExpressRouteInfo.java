package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderExpressRouteInfo
{
    @Schema(description = "主键")
    private Long pkey;
    
    @Schema(description = "路由节点产生的时间")
    private Date time;
    
    @Schema(description = "路由节点具体描述")
    private String description;
}
