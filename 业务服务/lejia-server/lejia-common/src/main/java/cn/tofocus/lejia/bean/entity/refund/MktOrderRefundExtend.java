package cn.tofocus.lejia.bean.entity.refund;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.jd.CourierType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_order_refund_extend")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderRefundExtend implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_refund_extend")
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "退款表主键")
    private Integer refundPkey;
    
    @Schema(description = "快递方式 上门取件或自己寄出")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CourierType courierType;
    
    @Schema(description = "预约取件开始时间")
    private String pickupTimeStart;
    
    @Schema(description = "预约取件结束时间")
    private String pickupTimeEnd;
    
    @Schema(description = "取件时间")
    private Date pickupTime;
    
    @Schema(description = "省")
    private String pro;
    
    @Schema(description = "市")
    private String city;
    
    @Schema(description = "区")
    private String area;
    
    @Schema(description = "街道")
    private String town;
    
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "寄件人")
    private String name;
    
    @Schema(description = "寄件人电话")
    private String mobile;
    
    @Schema(description = "快递公司")
    private String courierCompany;
    
    @Schema(description = "快递单号")
    private String courierNumber;
    
    @Schema(description = "运费")
    private BigDecimal postage;
    
    @Schema(description = "拒绝后快递公司")
    private String refuseCourierCompany;
    
    @Schema(description = "拒绝后快递单号")
    private String refuseCourierNumber;
    
    @Schema(description = "省")
    private String receiptPro;
    
    @Schema(description = "市")
    private String receiptCity;
    
    @Schema(description = "区")
    private String receiptArea;
    
    @Schema(description = "街道")
    private String receiptTown;
    
    @Schema(description = "收货地址")
    private String receiptAddr;
    
    @Schema(description = "收货名称")
    private String receiptName;
    
    @Schema(description = "收货人手机")
    private String receiptMobile;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
