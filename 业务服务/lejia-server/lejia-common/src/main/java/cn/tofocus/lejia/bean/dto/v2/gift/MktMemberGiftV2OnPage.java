package cn.tofocus.lejia.bean.dto.v2.gift;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberGiftV2OnPage
{
    private Integer pkey;
    
    @Schema(description = "会员")
    private Integer member;
    
    @Schema(description = "会员名称")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "name")
    private String memberName;
    
    @Schema(description = "会员手机号")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    private String memberMobile;
    
    @Schema(description = "礼品券")
    private Integer gift;
    
    @Schema(description = "礼品券名称")
    @JoinProperty(dataQuery = "mktGoodsGiftDao", from = "gift", propertyName = "title")
    private String giftTitle;
    
    @Schema(description = "卡券编号")
    private String cardNumber;
    
    @Schema(description = "使用市场")
    private String userFarmer;
    
    @Schema(description = "使用市场名称")
    private String userFarmerName;
    
    @Schema(description = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date userTime;
    
    @Schema(description = "状态 初始/已使用/已过期")
    private CardStatus status;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    public String getStatusName()
    {
        if(Boolean.TRUE.equals(invalid))
            return "已失效";
        return status.getName();
    }
    
    @Schema(description = "领取时间")
    private Date createdTime;
}
