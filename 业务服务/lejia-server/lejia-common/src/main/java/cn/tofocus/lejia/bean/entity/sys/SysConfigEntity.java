package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  sys_config
* @author zdw 2021-09-28
*/

@Entity
@Data
@Table(name = "sys_config")
public class SysConfigEntity implements HasPkey<String>
{
    @Id
    @Schema(description = "pkey", required = true)
    private String pkey;
    
    @Schema(description = "name", required = false)
    private String name;
    
    @Schema(description = "value", required = false)
    private String value;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}