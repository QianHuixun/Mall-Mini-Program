package cn.tofocus.lejia.bean.dto.pub;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberUsingGiftOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "状态 未核销/已核销/已过期")
    private String status;
    
    @Schema(description = "礼券")
    private Integer gift;
    
    @Schema(description = "礼券名称")
    private String giftName;
    
    @Schema(description = "礼券编号")
    private String giftNumber;
    
    @Schema(description = "礼品券图片")
    private String photo;
    
    @Schema(description = "价值")
    private BigDecimal cost;
    
    @Schema(description = "开始日期")
    private Date startDate;
    
    @Schema(description = "到期日期")
    private Date endDate;
    
    @Schema(description = "使用商户")
    private String userMerchant;
    
    @Schema(description = "使用日期")
    private Date userTime;
    
    @Schema(description = "结算日期")
    private Date settleDate;
    
    @Schema(description = "结算报表主键")
    private Integer settleKey;
    
    @Schema(description = "market")
    private Integer market;
    
    @Schema(description = "company")
    private Integer company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
}
