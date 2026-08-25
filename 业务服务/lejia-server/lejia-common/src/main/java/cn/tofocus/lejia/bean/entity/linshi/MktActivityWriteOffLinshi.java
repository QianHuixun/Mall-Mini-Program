package cn.tofocus.lejia.bean.entity.linshi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/4/23]
 */
@Data
@Entity
@Schema(description = "活动核销临时表")
@Table(name = "mkt_activity_write_off_linshi", indexes = {@Index(name = "idx_name_member", columnList = "name,member")})
@FieldNameConstants(innerTypeName = "F")
public class MktActivityWriteOffLinshi implements HasPkey<String>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoUUID
    private String pkey;
    
    @Schema(description = "活动名称")
    @Column(length = 200)
    private String name;
    
    @Schema(description = "会员")
    @Column
    private Integer member;
    
    @Schema(description = "建档时间")
    @Column
    @CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    @Column
    private Integer ascription;
}
