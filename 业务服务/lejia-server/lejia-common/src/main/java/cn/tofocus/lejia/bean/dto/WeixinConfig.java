package cn.tofocus.lejia.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeixinConfig
{
    private Integer pkey;
    
    private String APP_ID;
    
    private String MCH_ID;
    
    private String RE_URL;
    
    private String AB_NAME;
    
    private String FULL_NAME;
    
    @Schema(description = "密码", required = false)
    private String configPassword;
    
    @Schema(description = "密钥", required = false)
    private String configKey;
    
    @Schema(description = "路径", required = false)
    private String configLocalpath;
    
}
