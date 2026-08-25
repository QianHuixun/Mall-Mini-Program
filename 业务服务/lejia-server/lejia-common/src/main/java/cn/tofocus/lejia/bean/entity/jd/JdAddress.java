package cn.tofocus.lejia.bean.entity.jd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2026/2/28]
 */
@Data
@Entity
@Schema(description = "京东四级地址")
@Table(name = "jd_address")
@FieldNameConstants(innerTypeName = "F")
public class JdAddress implements HasPkey<Long>
{
    @Id
    @Column
    @Schema(description = "区域ID")
    private Long areaId;
    
    @Size(max = 50)
    @Column(length = 50)
    @Schema(description = "区域名")
    private String areaName;

    @Column
    @Schema(description = "区域级别")
    private Integer areaLevel;

    @Column
    @Schema(description = "上级区域ID")
    private Long parent;

    @Size(max = 50)
    @Column(length = 50)
    @Schema(description = "客户端区域名称（用于匹配）")
    private String clientName;
    
    @Override
    public Long getPkey()
    {
        return this.areaId;
    }
    
    @Override
    public void setPkey(Long pkey)
    {
        this.areaId = pkey;
    }
}
