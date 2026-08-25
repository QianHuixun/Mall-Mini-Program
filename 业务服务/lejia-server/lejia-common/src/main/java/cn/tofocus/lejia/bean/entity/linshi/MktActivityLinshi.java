package cn.tofocus.lejia.bean.entity.linshi;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Deprecated
@Entity
@Data
@Table(name = "mkt_activity_linshi")
@FieldNameConstants(innerTypeName = "F")
public class MktActivityLinshi implements HasPkey<Integer>
{
    @Id
    //    @AutoRedisID(domain = "zyysc", sequence = "mkt_activity_linshi")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "优惠券")
    private Integer card;
}
