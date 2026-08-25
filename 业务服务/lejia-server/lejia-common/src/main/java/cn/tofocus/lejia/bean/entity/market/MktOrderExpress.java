package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.express.ExpressCompany;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 订单物流表
 * 一个订单可能有多个物流单
 * @author czy
 * @version [版本号, 2024/12/3]
 */
@Data
@Entity
@Schema(description = "订单物流表")
@Table(name = "mkt_order_express")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderExpress implements HasPkey<Long>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_express")
    private Long pkey;
    
    // 本地系统内生成的物流单号
    @Column(length = 20)
    @Schema(description = "物流单号")
    private String expressNo;
    
    @Column
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Column(length = 20)
    @Schema(description = "订单号")
    private String kcCode;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "快递公司")
    private ExpressCompany expressCompany;
    
    @Size(max = 20)
    @Column(length = 20)
    @Schema(description = "快递公司运单号")
    private String waybillNo;
    
    @Column
    @Schema(description = "上门取件时间")
    private Date pickupTime;
    
    @Column(length = 128)
    @Schema(description = "寄托物内容")
    private String sendContent;
    
    @Column
    @Schema(description = "寄托物数量")
    private Integer sendNum;

    @Column(length = 20)
    @Schema(description = "取件快递员手机号")
    private String pickupCourierMobile;

    @Column
    @Schema(description = "最晚上门时间")
    private Date latestPickupTime;
    
    @Schema(description = "顺丰月结卡号")
    @Column(length = 20)
    private String sfMonthlyCard;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "状态")
    private OrderExpressStatus status;

    @Schema(description = "异常描述")
    @Column(length = 100)
    private String errorMsg;
    
    @Column(length = 40)
    @Schema(description = "市场")
    private String farmer;
    
    @Column(length = 40)
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
}
