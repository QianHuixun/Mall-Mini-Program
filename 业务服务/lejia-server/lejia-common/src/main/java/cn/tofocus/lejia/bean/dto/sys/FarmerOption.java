package cn.tofocus.lejia.bean.dto.sys;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FarmerOption
{
    @Schema(description = "pkey")
    private String pkey;
    
    @Schema(description = "菜场名称")
    private String name;
    
    @Schema(description = "市场类别")
    private FarmerType type;
    
    @JoinEnum(from = "type")
    @Schema(description = "市场类别名称")
    private String typeName;
}
