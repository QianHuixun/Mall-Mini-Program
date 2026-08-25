package cn.tofocus.lejia.bean.dto.app.supplier;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppSupplierOrderInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "收货地址")
    private AppSupplierOrderAddr addr;
    
    @Schema(description = "商品信息")
    private List<OrderV2Info> infos;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @Schema(description = "供应商")
    private Integer supplier;
    
    @Schema(description = "供应商名称")
    @JoinProperty(dataQuery = "mktSupplierDao", from = "supplier", propertyName = "name")
    private String supplierName;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "市场名称")
    private String farmerName;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "自提时间")
    private String pstime;
    
    @Schema(description = "自提码")
    private String pickupCode;
    
    @Schema(description = "核销时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy年MM月dd日 HH:mm", timezone = "GMT+8")
    private String pickupTime;
    
    @Schema(description = "是否核销")
    private Boolean pickupFlag;
}
