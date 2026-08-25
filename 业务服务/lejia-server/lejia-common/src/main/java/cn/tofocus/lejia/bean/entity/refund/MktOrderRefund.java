package cn.tofocus.lejia.bean.entity.refund;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.RefundType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_order_refund")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderRefund implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_refund")
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "单据号")
    @Column(name = "kc_code")
    private String code;
    
    @Schema(description = "微信交互-退款单号")
    private String outRefundNo;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "状态 申请中/同意/已退款/拒绝")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private RefundStatus status;
    
    @Schema(description = "用户")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "类型,市场/用户 退款")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private RefundType type;

    @Schema(description = "类型,退货 换货")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private RefundJdType jdType;
    
    @Schema(description = "是否是京东退款订单")
    private Boolean isJd;

    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    @Column(name = "refund_describe")
    private String describe;
    
    @Schema(description = "照片")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 2000)
    private List<String> photo;
    
    @Schema(description = "商品金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal goodsAmt;
    
    @Schema(description = "优惠金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal preferentialAmt;
    
    @Schema(description = "配送费优惠金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal preferentialPostageAmt;
    
    @Schema(description = "原配送费")
    private BigDecimal oldPostage;
    
    @Schema(description = "配送费")
    @Column(precision = 11, scale = 2)
    private BigDecimal postage;
    
    @Schema(description = "订单金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal amtall;

    @Schema(description = "退款商品金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal refundGoodsAmt;

    @Schema(description = "退款配送费")
    @Column(precision = 11, scale = 2)
    private BigDecimal refundPostage;

    @Schema(description = "退还优惠券")
    @Column
    private Integer refundCard;
    
    @Schema(description = "退还配送优惠券")
    @Column
    private Integer refundCardPostage;
    
    @Schema(description = "退款金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal amtre;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;

    @Schema(description = "京东运费退款")
    private BigDecimal refundJdPostage;
    
    @Schema(description = "处理意见")
    @Column(length = 1000)
    private String delDesc;
    
    @Schema(description = "处理员")
    private Integer delBy;
    
    @Schema(description = "处理时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date delTime;
    
    @Schema(description = "退款时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reTime;

    // 目前主要用于京东售后取消、确认操作后等待京东处理
    @Schema(description = "外部系统处理中")
    @Column(columnDefinition = "tinyint")
    private Boolean outProcessing;
    
    @Schema(description = "重新退款 true:可以重新退款")
    private Boolean againRefund;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}
