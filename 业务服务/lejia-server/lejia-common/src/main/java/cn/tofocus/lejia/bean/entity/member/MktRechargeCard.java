package cn.tofocus.lejia.bean.entity.member;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.member.RechargeCardType;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoUUID;
import cn.tofocus.lejia.bean.enums.RechargeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 充值卡密管理
 */
@Entity
@Data
@Table(name = "mkt_recharge_card")
@FieldNameConstants(innerTypeName = "F")
public class MktRechargeCard implements HasPkey<String>
{
    @Id
    @AutoUUID
    private String pkey;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "卡类型")
    private RechargeCardType type;
    
    @Schema(description = "状态")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private RechargeStatus status;
    
    @Schema(description = "价值")
    private BigDecimal cost;
    
    @Schema(description = "使用人手机号码")
    private String mobile;
    
    @Schema(description = "卡号")
    private String cardNumber;
    
    @Schema(description = "卡密")
    private String cardPassword;

    @Schema(description = "标签")
    private Integer tag;
    
    @Schema(description = "使用时间")
    private Date useTime;
    
    @Schema(description = "截止日期")
    private Date deadline;
    
    @Schema(description = "创建时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
