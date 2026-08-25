package cn.tofocus.lejia.bean.dto.v2.gift;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGiftV2OnPage
{
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "图片")
    private String picture;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    @Schema(description = "有效期类型")
    private CouponExpireChoose expireChoose;
    
    @Schema(description = "使用市场")
    private String userFarmer;
    
    @Schema(description = "使用商户")
    private Integer userVendor;
    
    @Schema(description = "有效期(天)")
    private Integer effective;
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    
    // null表示不限量
    @Schema(description = "卡券数量")
    private Integer count;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum = 0;
    
    @Schema(description = "已使用数量")
    private Integer usedNum = 0;
    
    @Schema(description = "建档时间")
    private Date createdTime;

    @Schema(description = "是否介入卡券活动")
    private Boolean isInActivity;
}
