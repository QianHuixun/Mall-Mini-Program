package cn.tofocus.lejia.bean.entity.member;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.MsdOperationType;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2025/8/20]
 */
@Data
@Entity
@Schema(description = "会员民生豆账户明细")
@Table(name = "mkt_member_msd_line")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberMsdLine implements HasPkey<Long>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_member_msd_line")
    private Long pkey;

    @Schema(description = "会员")
    @Column(name = "member_key")
    private Integer member;
    
    @Column
    @Schema(description = "标签主键")
    private Integer tag;
    
    /**
     * 加减标志 true:加 false:减
     */
    @Schema(description = "加减标志")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean direct;

    @Schema(description = "操作金额")
    @Column(precision = 11, scale = 2)
    private BigDecimal amt;

    @Schema(description = "余额")
    @Column(precision = 11, scale = 2)
    private BigDecimal balance;

    @Schema(description = "操作类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private MsdOperationType operationType;

    @Schema(description = "备注")
    @Column(length = 100)
    private String remark;

    @Schema(description = "来源单据")
    @Column(length = 40)
    private String formId;

    @Schema(description = "建档时间")
    @Column
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    @Column
    private Integer ascription;
}
