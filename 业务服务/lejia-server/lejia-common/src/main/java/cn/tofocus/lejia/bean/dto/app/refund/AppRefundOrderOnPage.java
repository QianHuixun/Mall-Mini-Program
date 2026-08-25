package cn.tofocus.lejia.bean.dto.app.refund;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnList;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppRefundOrderOnPage
{
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "单据号")
    private String code;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "提交时间")
    private Date createdTime;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    private String describe;
    
    @Schema(description = "退款照片")
    private List<String> refundPhoto;
    
    @Schema(description = "售后电话")
    private String tel;
    
    private RefundStatus status;
    
    @JoinEnum(from = "status")
    private String statusName;
    
    @Schema(description = "合计商品数量")
    private Integer num;

    @Deprecated
    @Schema(description = "退款商品总价")
    private BigDecimal sumAmt;
    
    private DistributionType distributionType;
    
    @Schema(description = "退款明细")
    private List<VendorRefundOrderOnList> list;
}
