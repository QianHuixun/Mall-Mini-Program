package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.db.AutoRedisID;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/4/25]
 */
@Data
@Entity
@Schema(description = "管理员表")
@Table(name = "mkt_manager")
@FieldNameConstants(innerTypeName = "F")
public class MktManager implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_manager")
    private Integer pkey;
    
    @Schema(description = "手机号")
    @Column(length = 20)
    private String mobile;
    
    @Schema(description = "名称")
    @Column(length = 30)
    private String name;
    
    @Schema(description = "是否启用")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean enabled;

    @Schema(description = "市场")
    @Column(length = 40, nullable = false)
    private String farmer;

    @Schema(description = "公司")
    @Column(length = 40, nullable = false)
    private String company;
    
    @Schema(description = "建档时间")
    @Column(nullable = false)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    @Column(nullable = false)
    private Integer ascription;
}
