package cn.tofocus.lejia.bean.entity.zx;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "zx_withdraw")
@FieldNameConstants(innerTypeName = "F")
public class ZxWithdraw implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "zx_withdraw")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "清分文件对应的pkey")
    private Integer filePkey;
    
    @Schema(description = "账户类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxUserType type;
    
    @Schema(description = "云商城主键")
    private String value;
    
    @Schema(description = "中信银行主键")
    private String zxUserId;
    
    @Schema(description = "账单日期")
    private String billDate;
    
    @Schema(description = "需要打款的金额")
    private BigDecimal comms;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "提现状态")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxWithdrawStatus status;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "提现时间")
    private Date withdrawTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    
    public ZxWithdraw()
    {
        super();
    }
    
    public ZxWithdraw(ZxUserType type, String zxUserId, String value, Integer ascription)
    {
        this.type = type;
        this.zxUserId = zxUserId;
        this.value = value;
        this.status = ZxWithdrawStatus.NOT_MAKE_PAYMENT;
        this.comms = BigDecimal.ZERO;
        this.ascription = ascription;
    }
}
