package cn.tofocus.lejia.bean.dto.app;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorGtypeInfo
{
    @Schema(description = "主键")
    private Integer pkey;

    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "头像")
    private String headIcon;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
    
    private Integer gtype;
    
    private Integer gtypeSort;
    
    private String gtypeName;
    
    @JsonIgnore
    private Date createdTime;
}
