package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  商户提现记录
* @author zdw 2024-02-26
*/

@Entity
@Data
@Table(name = "mkt_vendor_withdrawal")
public class MktVendorWithdrawal implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_withdrawal")
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "对应钱包账户明细主键")
    private Integer lineKey;
    
    @Schema(description = "商户主键")
    private Integer vendorKey;
    
    @Schema(description = "打款状态")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private WithdrawalStatus status;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "银行")
    private String bankname;
    
    @Schema(description = "持卡人")
    private String bankuser;
    
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "开户支行名称")
    private String bankBranchName;
    
    @Schema(description = "申请时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}