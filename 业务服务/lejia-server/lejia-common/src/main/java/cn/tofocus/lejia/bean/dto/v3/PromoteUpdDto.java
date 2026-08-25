package cn.tofocus.lejia.bean.dto.v3;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PromoteUpdDto
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    @Size(max = 20)
    private String title;
    
    @Schema(description = "内容")
    @Size(max = 20)
    private String content;
    
    @Schema(description = "图片")
    private String photo;
    
    @Schema(description = "市场")
    private String farmer;
    
}
