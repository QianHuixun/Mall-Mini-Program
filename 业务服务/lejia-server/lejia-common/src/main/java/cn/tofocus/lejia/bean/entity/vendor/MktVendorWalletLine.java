package cn.tofocus.lejia.bean.entity.vendor;

import io.swagger.v3.oas.annotations.media.Schema;

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
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
*  商户钱包账户明细
* @author zdw 2024-02-26
*/

@Entity
@Data
@Table(name = "mkt_vendor_wallet_line")
@FieldNameConstants(innerTypeName = "F")
public class MktVendorWalletLine implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_wallet_line")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendorKey;
    
    @Schema(description = "true:加  false:减")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean direct;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "锁定金额")
    private BigDecimal lockBalance;
    
    @Schema(description = "金额来源")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private VendorWalletSource source;
    
    @Schema(description = "结算状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SettlementType status;

    @Schema(description = "订单时间")
    private Date orderTime;

    @Schema(description = "结算时间")
    private Date settlementTime;
    
    @Schema(description = "来源单据")
    private String formId;
    
//    @Schema(description = "来源单据-商户订单主键")
//    private Integer vendorOrderKey;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}