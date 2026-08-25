package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  商户钱包账户
* @author zdw 2024-02-26
*/

@Entity
@Data
@Table(name = "mkt_vendor_wallet")
public class MktVendorWallet implements HasPkey<Integer>
{
    
    @Id
    @Schema(description = "对应mkt_vendor的pkey")
    private Integer pkey;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @Schema(description = "锁定金额，不能使用(待结算)")
    private BigDecimal lockAmount;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}