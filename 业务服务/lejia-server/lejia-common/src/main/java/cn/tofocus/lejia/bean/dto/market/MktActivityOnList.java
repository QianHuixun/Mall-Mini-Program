package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktActivityOnList
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "卡券数量")
    private Integer couponNum;
    
    @Schema(description = "套餐总数")
    private Integer num;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
}
