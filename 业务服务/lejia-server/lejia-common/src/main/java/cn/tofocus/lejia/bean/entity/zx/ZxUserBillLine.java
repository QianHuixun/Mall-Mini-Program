package cn.tofocus.lejia.bean.entity.zx;

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
import cn.tofocus.lejia.bean.enums.ZxBillType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

//@Entity
@Data
//@Table(name = "zx_user_bill_line")
//@FieldNameConstants(innerTypeName = "F")
public class ZxUserBillLine implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "zx_user_bill_line")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "中信用户表主键")
    private Integer userPkey;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code")
    private String code;
    
    @Schema(description = "类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxBillType type;
    
    @Schema(description = "交易金额")
    private BigDecimal amt;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
