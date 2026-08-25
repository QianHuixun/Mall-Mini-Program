package cn.tofocus.lejia.bean.entity.applet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  常见问题
* @author zdw 2023-07-27
*/

@Entity
@Data
@Table(name = "mkt_problem")
public class MktProblemEntity implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_problem")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "问题名称", required = false)
    private String name;
    
    @Schema(description = "问题分类", required = false)
    private Integer type;
    
    @Schema(description = "回答", required = false)
    private String answer;
    
    @Schema(description = "启用标志", required = false)
    private Boolean enabled;
    
    @Schema(description = "排序", required = false)
    private Integer sort;
    
    @Schema(description = "默认问题", required = false)
    private Boolean isDefault;
    
    @Schema(description = "归属主键", required = false)
    private Integer ascription;
    
}