package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Schema(description = "动态属性")
@Table(name = "sys_dynamic_attribute")
@FieldNameConstants(innerTypeName = "F")
public class SysDynamicAttribute implements HasPkey<String>
{
    @Id
    @AutoUUID
    @Column(length = 40)
    @Schema(description = "主键")
    private String pkey;
    
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40)
    private String company;
    
    @Schema(description = "配置类")
    @Column(length = 100)
    private String configClass;
    
    @Schema(description = "属性")
    @Column(length = 100)
    private String property;
    
    @Schema(description = "值")
    @Column(length = 500)
    private String value;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
