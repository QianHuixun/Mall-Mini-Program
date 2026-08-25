package cn.tofocus.lejia.bean.dto.v3;

import java.util.Date;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PromoteOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "图片")
    private String photo;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "新增时间")
    private Date createdTime;
    
    private String farmer;
    
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
}
