package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktSupplierOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "供应商名称")
    private String name;
    
    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
