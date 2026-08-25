package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.Name;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_order")
@FieldNameConstants(innerTypeName = "F")
public class MktOrder implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code")
    @Name
    private String code;
    
    @Schema(description = "用户")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private OrderStatus status;
    
    @Schema(description = "第三方配送状态")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ThirdPartyStatus thirdPartyStatus;
    
    @Schema(description = "采购状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private PurchaseStatus purchaseStatus;
    
    @Schema(description = "结算状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SettlementType settlementType;
    
    @Schema(description = "订单来源 自营/积分商城/市场商城")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private OrderOir orderOir;
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/市场/积分")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private OrderType orderType;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private PayType payType;
    
    @Schema(description = "采购登记")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Integer cgCheck;
    
    @Schema(description = "配送时间")
    private String pstime;
    
    @Schema(description = "毛重")
    private BigDecimal weight;

    @Schema(description = "原配送费")
    private BigDecimal oldPostage;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "订单价格")
    private BigDecimal amto;

    @Schema(description = "总价")
    private BigDecimal amtall;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;
    
    @Schema(description = "微信支付金额")
    private BigDecimal weixinAmt;
    
    @Schema(description = "其他支付金额")
    private BigDecimal otherAmt;
    
    @Schema(description = "支付积分")
    private Integer pointn;
    
    @Schema(description = "支付佣金")
    private BigDecimal commn;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "支付卡券")
    private Integer card;
    
    @Schema(description = "配送卡券主键")
    private Integer cardPostage;

    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "砍价金额")
    private BigDecimal cutAmt;
    
    @Schema(description = "采购状态金额")
    private BigDecimal purchaseAmt;
    
    @Schema(description = "会员优惠")
    private BigDecimal reducePrice;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "推荐人")
    private Integer tjr;
    
    @Schema(description = "是否包厢订单,true:是")
    private Boolean isBox;
    
    @Schema(description = "包厢时间")
    private String boxTime;

    @Schema(description = "包厢名称")
    private String boxName;
    
    @Schema(description = "包厢门锁密码")
    private String boxPassword;
    
    @Schema(description = "包厢门锁ID")
    private String lockId;
    
    @Schema(description = "门锁密码时间-开始")
    private Date boxSd;
    
    @Schema(description = "门锁密码时间-结束")
    private Date boxEd;

    @Schema(description = "供应商")
    @Column
    private Integer supplier;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "配送类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private DistributionType distributionType;
    
    //@Schema(description = "骑手类型,可为空")
    @Schema(description = "发货方式")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private ExpressType expressType;
    
    @Schema(description = "自提码")
    @Column(length = 64)
    private String pickupCode;
    
    @Schema(description = "小票码")
    private Integer smallTicket;
    
    @Schema(description = "核销时间")
    private String pickupTime;
    
    @Schema(description = "是否核销")
    private Boolean pickupFlag;
    
    @Schema(description = "心安食足支付流水表主键")
    private Integer xaszConsumption;
    
    @Schema(description = "清分文件对应的pkey")
    private Integer filePkey;
    
    @Schema(description = "手续费承担")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CommissionType commissionType;
    
    @Schema(description = "归属主键")
    private Integer ascription;

    // 第三方派单号
    @Schema(description = "第三方快递单号")
    @Column(length = 64)
    private String thirdPartyOrderNo;

    @Schema(description = "到货时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date qrTime;

    @Schema(description = "完成时间")
    @Column
    private Date confirmTime;
    
    @Schema(description = "京东支付金额")
    private BigDecimal payDetailMoney;
    
    @Schema(description = "京东退款金额")
    private BigDecimal refundJd;
    
    @Schema(description = "重新退款")
    private Boolean againRefund;
}