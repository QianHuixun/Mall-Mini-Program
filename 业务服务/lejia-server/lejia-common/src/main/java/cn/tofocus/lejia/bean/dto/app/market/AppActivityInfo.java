package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppActivityInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "活动名称")
    private String name;
    
    @Schema(description = "活动宣传图")
    private String photo;
    
    @Schema(description = "会员福利展示图")
    private String welfarePhoto;
    
    @Schema(description = "售卖价格")
    private BigDecimal price;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "是否允许分享")
    private Boolean allowedShare = false;
}
