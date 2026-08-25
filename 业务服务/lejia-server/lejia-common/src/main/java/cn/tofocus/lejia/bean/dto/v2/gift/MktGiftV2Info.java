package cn.tofocus.lejia.bean.dto.v2.gift;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGiftV2Info
{
    private Integer pkey;
    
    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @Schema(description = "使用规则")
    @NotBlank(message = "使用规则不能为空")
    private String content;
    
    @Schema(description = "图片")
    @NotBlank(message = "图片不能为空")
    private String picture;
    
    @Schema(description = "使用市场")
    private String userFarmer;
    
    @Schema(description = "有效期(天)")
    @Min(value = 1, message = "有效期天数不能小于1")
    private Integer effective;
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    
    @Schema(description = "优惠券数量")
    private Integer count;
}
