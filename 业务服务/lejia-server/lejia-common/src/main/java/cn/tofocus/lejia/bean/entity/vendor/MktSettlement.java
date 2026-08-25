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
@Table(name = "mkt_settlement")
public class MktSettlement implements HasPkey<Integer>
{
    @Id
    @Column
    @AutoRedisID(domain = "zyysc", sequence = "mkt_settlement")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "开始日期")
    @Column(nullable = false, length = 10)
    private String startDate; // 开始日期  DATE
    
    @Schema(description = "结束日期")
    @Column(nullable = false, length = 10)
    private String endDate; // 结束日期  DATE
    
    @Schema(description = "商户数")
    private Integer numMerchant = 0;
    
    @Schema(description = "合计 笔数")
    private Integer num = 0;
    
    @Schema(description = "合计 金额")
    @Column(precision = 16, scale = 2)
    private BigDecimal amt;
    
    @Schema(description = "优惠金额", required = false)
    private BigDecimal discountAmt;
    
    @Schema(description = "邮费", required = false)
    private BigDecimal postage;
    
    @Schema(description = "差额", required = false)
    private BigDecimal difference;
    
    @Schema(description = "合计 待结算金额")
    @Column(precision = 16, scale = 2)
    private BigDecimal awaitAmt;
    
    @Schema(description = "结算状态", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private SettlementType type;
    
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