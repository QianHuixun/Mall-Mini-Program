package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  结算总表
* @author zdw 2021-12-07
*/

@Entity
@Data
@Table(name = "mkt_settlement_line_day")
public class MktSettlementLineDay implements HasPkey<Long>
{
    @Id
    @Column
    @AutoRedisID(domain = "zyysc", sequence = "mkt_settlement_line_day")
    @Schema(description = "pkey", required = true)
    private Long pkey;
    
    @Column
    private Integer settlementPkey;
    
    @Schema(description = "商户主键", required = false)
    @Column
    private Integer vendor;
    
    @Schema(description = "结算日期", required = false)
    private Date settlementDate;
    
    @Schema(description = "中信银行主键")
    @Column(length = 30)
    private String zxUserId;
    
    @Schema(description = "商户名称", required = false)
    @Column(length = 100)
    private String vendorName;
    
    @Schema(description = "开户银行名称", required = false)
    @Column(length = 200)
    private String bankname;
    
    @Schema(description = "开户人", required = false)
    @Column(length = 40)
    private String bankuser;
    
    @Schema(description = "银行卡号", required = false)
    @Column(length = 40)
    private String bankcard;
    
    @Schema(description = "开户支行名称", required = false)
    @Column(length = 40)
    private String bankBranchName;
    
    @Schema(description = "开户行大额行号", required = false)
    @Column(length = 40)
    private String bankNo;
    
    @Schema(description = "开户人身份证号码", required = false)
    @Column(length = 50)
    private String bankuserIdentity;
    
    @Schema(description = "银行卡绑定手机", required = false)
    @Column(length = 50)
    private String bankuserMoblie;
    
    @Schema(description = "交易总笔数", required = false)
    @Column
    private Integer orderCount;
    
    @Schema(description = "交易金额", required = false)
    @Column(precision = 11, scale = 2)
    private BigDecimal orderAmt;
    
    @Schema(description = "佣金费率", required = false)
    @Column(precision = 5, scale = 2)
    private BigDecimal commission;
    
    @Schema(description = "交易佣金", required = false)
    @Column(precision = 11, scale = 2)
    private BigDecimal orderCommission;
    
    @Schema(description = "优惠金额", required = false)
    private BigDecimal discountAmt;
    
    @Schema(description = "邮费", required = false)
    private BigDecimal postage;
    
    @Schema(description = "差额", required = false)
    private BigDecimal difference;
    
    @Schema(description = "结算金额", required = false)
    @Column(precision = 11, scale = 2)
    private BigDecimal amt;
    
    @Schema(description = "结算状态", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private SettlementType type;
    
    @Schema(description = "是否提现")
    private Boolean selfMention;
    
    @Schema(description = "原因")
    @Column(length = 200)
    private String rem;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40)
    private String company;
    
    @LastModifiedDate
    private Date updatedTime;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员", required = true)
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}