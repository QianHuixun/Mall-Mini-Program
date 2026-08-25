package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktSupplierOption
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "供应商名称")
    private String name;
}
