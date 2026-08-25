package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppAuthCode2SessionDTO extends AppWxErrMsgDTO{
    /**
     * 用户唯一标识
     */
    @Schema(description = "用户唯一标识")
    private String openid;

    /**
     * 会话密钥
     */
    @Schema(description = "会话密钥")
    private String session_key;

    /**
     * 用户在开放平台的唯一标识符
     */
    @Schema(description = "用户在开放平台的唯一标识符")
    private String unionid;

}
