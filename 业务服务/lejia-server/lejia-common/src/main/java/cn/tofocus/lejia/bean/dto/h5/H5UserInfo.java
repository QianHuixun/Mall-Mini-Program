package cn.tofocus.lejia.bean.dto.h5;

import java.math.BigDecimal;

import cn.tofocus.core.json.MaskPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class H5UserInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "手机号码")
    @MaskPhone
    private String mobile;
    
    @Schema(description = "钱包金额")
    private BigDecimal money;
    
    @Schema(description = "等级")
    private Integer level;
}
