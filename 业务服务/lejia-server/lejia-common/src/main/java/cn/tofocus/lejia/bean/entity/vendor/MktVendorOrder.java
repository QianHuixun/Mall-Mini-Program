package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PriceStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_vendor_order")
@FieldNameConstants(innerTypeName = "F")
public class MktVendorOrder implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_order")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "订单明细主键")
    private Integer orderLinePkey;
    
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "goods")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "类型")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderType type;
    
    @Schema(description = "结算状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SettlementType status;
    
    @Schema(description = "采购状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private PurchaseStatus purchaseStatus;
    
    @Schema(description = "价格异常状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private PriceStatus priceStatus;
    
    @Schema(description = "退款状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private RefundStatus refundStatus;

    // 实际消费者退款金额
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;

    // 商户要从结算金额里退回来的金额（即不含佣金）
    @Schema(description = "采购退款金额")
    private BigDecimal procureRefundAmt;
    
    @Schema(description = "推荐采购价格")
    private BigDecimal recommendPrice;
    
    @Schema(description = "规格主键")
    private Integer space;
    
    @Schema(description = "规格")
    private String spaceName;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "采购价格")
    private BigDecimal price;

    @Schema(description = "总价")
    private BigDecimal totalPrice;
    
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;
    
    @Schema(description = "交易佣金")
    private BigDecimal commissions;
    
    @Schema(description = "市场佣金费率")
    private BigDecimal marketCommissionRate;
    
    @Schema(description = "市场交易佣金")
    private BigDecimal marketCommissions;
    
    @Schema(description = "集团方佣金费率")
    private BigDecimal sysCommissionRate;
    
    @Schema(description = "集团方交易佣金")
    private BigDecimal sysCommissions;
    
    @Schema(description = "支付渠道手续费")
    private BigDecimal payComm;
    
    @Schema(description = "手续费承担")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CommissionType commissionType;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;

    // 没扣手续费，扣了佣金，扣了打包费
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "优惠金额")
    private BigDecimal discountAmt;
    
    @Schema(description = "优惠退款金额")
    private BigDecimal discountRefundAmt;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "差额")
    private BigDecimal difference;
    
    @Schema(description = "采购备注")
    private String remark;
    
    @Schema(description = "结算备注")
    private String settlementRemark;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "撤销时间")
    private Date revokeTime;
    
    @Schema(description = "商户确认时间")
    private Date vendorTime;
    
    @Schema(description = "市场确认时间")
    private Date farmerTime;
    
    @Schema(description = "结算主表")
    private Integer settlementPkey;
    
    @Schema(description = "原先记录结算时间周期开始时间,现改为结算时间")
    private Date startDate;
    
    @Schema(description = "原先记录结算时间周期结束时间,现改为付款时间")
    private Date endDate;
    
    @Schema(description = "清分文件对应的pkey")
    private Integer filePkey;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @LastModifiedBy
    private Integer updateBy;
    
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}