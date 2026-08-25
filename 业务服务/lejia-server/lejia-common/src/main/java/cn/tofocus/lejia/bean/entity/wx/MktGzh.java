package cn.tofocus.lejia.bean.entity.wx;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_gzh")
@FieldNameConstants(innerTypeName = "F")
public class MktGzh implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_gzh")
    private Integer pkey;
    
    @Schema(description = "手机")
    private String mobile;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "openid")
    private String openid;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
