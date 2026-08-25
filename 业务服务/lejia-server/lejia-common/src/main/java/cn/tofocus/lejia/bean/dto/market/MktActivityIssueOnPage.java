package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktActivityIssueOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "会员")
    private Integer member;
    
    @Schema(description = "手机号")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    private String memberMobile;
    
    @Schema(description = "支付时间")
    private Date payTime;
    
    @Schema(description = "金额")
    private BigDecimal amt;
}
