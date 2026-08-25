package cn.tofocus.lejia.bean.dto.market.recharge;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.RechargeStatus;
import cn.tofocus.lejia.bean.enums.member.RechargeCardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RechargeCardOnPage
{
    private String pkey;

    @Schema(description = "卡类型")
    private RechargeCardType type;

    @JoinEnum(from = "type")
    @Schema(description = "卡类型名称")
    private String typeName;
    
    @Schema(description = "状态")
    @JoinEnum(from = "status")
    private String statusName;

    @Schema(description = "状态")
    private RechargeStatus status;
    
    @Schema(description = "面值")
    private BigDecimal cost;
    
    @Schema(description = "使用人")
    private String mobile;
    
    @Schema(description = "卡号")
    private String cardNumber;
    
    @Schema(description = "卡密")
    private String cardPassword;

    @Schema(description = "标签")
    private Integer tag;

    @Schema(description = "标签名称")
    @JoinProperty(dataQuery = "mktTagDao", from = "tag", propertyName = "name")
    private String tagName;
    
    @Schema(description = "截止日期")
    private Date deadline;
    
    @Schema(description = "使用时间")
    private Date useTime;
    
    @Schema(description = "创建时间")
    private Date createdTime;
}
