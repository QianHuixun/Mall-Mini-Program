package cn.tofocus.lejia.bean.entity.wx;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_gzh_associate")
@FieldNameConstants(innerTypeName = "F")
public class MktGzhAssociate implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_gzh_associate")
    private Integer pkey;
    
    @Schema(description = "对应公众号主键")
    private Integer gzh;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
