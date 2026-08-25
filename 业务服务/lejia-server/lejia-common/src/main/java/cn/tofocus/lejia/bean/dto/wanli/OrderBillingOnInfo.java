package cn.tofocus.lejia.bean.dto.wanli;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 获取计价获取dto
 * 
 */
@Data
public class OrderBillingOnInfo
{
    @Schema(description = "状态 1:成功 2：失败 Integer(2)")
    private Integer status;
    
    @Schema(description = "异常原因 String(255)")
    private String errorMsg;
    
    @Schema(description = "运力图标 String(255)")
    private String icon;
    
    @Schema(description = "应付金额 Integer(11)")
    private Integer originalPrice;
    
    @Schema(description = "优惠金额 单位分 Integer(11)")
    private Integer discountPrice;
    
    @Schema(description = "实际应付金额 单位分 Integer(11)")
    private Integer estimatePrice;
    
    @Schema(description = "运力编号 Integer(11)")
    private Integer deliveryCode;
    
    @Schema(description = "快递名称")
    private String deliveryChannelName;
    
    @Schema(description = "总距离，拼接后的千米 String(32)")
    private String distance;
}


