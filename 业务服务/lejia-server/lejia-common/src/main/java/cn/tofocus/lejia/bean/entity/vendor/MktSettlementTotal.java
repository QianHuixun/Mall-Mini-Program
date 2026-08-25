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
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  记录每天清分数据
* @author zdw 2021-12-29
*/

@Entity
@Data
@Table(name = "mkt_settlement_total")
public class MktSettlementTotal implements HasPkey<Long>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_settlement_total")
    @Schema(description = "pkey", required = true)
    private Long pkey;
    
    @Schema(description = "结算时间", required = false)
    private Date settlementDate;
    
    @Schema(description = "优惠金额", required = false)
    private BigDecimal discountAmt = BigDecimal.ZERO;
    
    @Schema(description = "邮费", required = false)
    private BigDecimal postage = BigDecimal.ZERO;
    
    @Schema(description = "清分金额", required = false)
    private BigDecimal clearingAmt = BigDecimal.ZERO;
    
    @Schema(description = "商品和采购 差额", required = false)
    private BigDecimal difference = BigDecimal.ZERO;
    
    @Schema(description = "手续费", required = false)
    private BigDecimal handlingFee = BigDecimal.ZERO;
    
    @Schema(description = "用户支付金额", required = false)
    private BigDecimal payAmt = BigDecimal.ZERO;
    
    @Schema(description = "平台金额", required = false)
    private BigDecimal platformAmt = BigDecimal.ZERO;
    
    @Schema(description = "结算状态", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private SettlementType type;

    @Schema(description = "created_time", required = false)
    @CreatedDate
    private Date createdTime;
    
    /**
     * 校验今天金额是否对的上
     * @return
     */
    public Boolean check()
    {
        // + 手续费 .add(handlingFee) + 邮费
//        用户支付的金额 = 清分金额 + 平台金额  - 优惠券
        BigDecimal sum = clearingAmt.add(platformAmt).subtract(discountAmt);
        if(payAmt.compareTo(sum) != 0)
            return false;
        return true;
    }
    
    @Schema(description = "归属主键")
    private Integer ascription;
}