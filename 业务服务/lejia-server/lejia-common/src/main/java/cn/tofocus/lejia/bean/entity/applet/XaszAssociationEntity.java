package cn.tofocus.lejia.bean.entity.applet;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "xasz_association")
public class XaszAssociationEntity implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "xasz_association")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "saas市场主键")
    private String farmer;
    
    @Schema(description = "云农贸市场主键")
    private Integer market;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
