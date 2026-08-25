package cn.tofocus.lejia.bean.entity.applet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  常见问题分类
* @author zdw 2023-07-27
*/

@Entity
@Data
@Table(name = "mkt_problem_type")
public class MktProblemTypeEntity implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_problem_type")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "分类名称", required = false)
    private String name;
    
    @Schema(description = "排序", required = false)
    private Integer sort;
    
    @Schema(description = "归属主键", required = false)
    private Integer ascription;
    
}