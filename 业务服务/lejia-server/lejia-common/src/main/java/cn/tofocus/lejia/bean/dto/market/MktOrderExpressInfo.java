package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.express.ExpressCompany;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderExpressInfo
{
    @Schema(description = "主键")
    private Long pkey;
    
    // 本地系统内生成的物流单号
    @Schema(description = "物流单号")
    private String expressNo;
    
    @Schema(description = "快递公司")
    private ExpressCompany expressCompany;
    
    @JoinEnum(from = "expressCompany")
    @Schema(description = "快递公司名称")
    private String expressCompanyName;
    
    @Schema(description = "快递公司运单号")
    private String waybillNo;
    
    @Schema(description = "上门取件时间")
    private Date pickupTime;
    
    @Schema(description = "寄托物内容")
    private String sendContent;
    
    @Schema(description = "寄托物数量")
    private Integer sendNum;
    
    @Schema(description = "取件快递员手机号")
    private String pickupCourierMobile;
    
    @Schema(description = "最晚上门时间")
    private Date latestPickupTime;

    @Schema(description = "状态")
    private OrderExpressStatus status;

    @Schema(description = "状态名称")
    @JoinEnum(from = "status")
    private String statusName;
}
