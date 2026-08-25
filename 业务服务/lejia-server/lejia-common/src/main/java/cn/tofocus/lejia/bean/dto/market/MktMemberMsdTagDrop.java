package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberMsdTagDrop implements HasPkey<Integer>
{
    @Schema(description = "标签主键")
    private Integer pkey;
    
    @Schema(description = "标签名称")
    @JoinProperty(dataQuery = "mktTagDao", propertyName = "name")
    private String name;
}
