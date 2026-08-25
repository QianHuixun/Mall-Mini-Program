package cn.tofocus.lejia.bean.dto.v2.gift;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberGiftV2OnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "状态 初始/已使用/已过期")
    private CardStatus status;
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "市场")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "userFarmer")
    private String userFarmerName;
    
    @JsonIgnore
    private String userFarmer;
    
    @JsonIgnore
    private Integer userVendor;
    
    @Schema(description = "限制商户使用的商户名称")
    private String userVendorName;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    @JsonIgnore
    private Integer gift;
    
    @Schema(description = "卡券编号")
    private String cardNumber;
    
    @Schema(description = "图片")
    private String picture;

    @Schema(description = "介绍")
    private String content;
    
}
