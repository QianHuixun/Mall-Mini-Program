package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberDTO
{
    
    /**
     * avatarUrl
     */
    @Schema(description = "avatarUrl")
    private String avatarUrl;
    
    /**
     * 店名
     */
    @Schema(description = "gender")
    private String gender;
    
    /**
     * 负责人
     */
    @Schema(description = "nickName")
    private String nickName;
    
    /**
    * 推荐人
    */
    @Schema(description = "推荐人")
    private Integer tjr;

    @Schema(description = "商户主键")
    private Integer tjv;
    
    /**
    * 推荐人
    */
    @Schema(description = "推荐人")
    private String tjrOpenid;
    
    /**
     * 手机
     */
    @Schema(description = "encryptedData")
    private String encryptedData;
    
    @Schema(description = "session_key")
    private String session_key;
    
    @Schema(description = "iv")
    private String iv;
    
    @Schema(description = "openid")
    private String openid;
}
