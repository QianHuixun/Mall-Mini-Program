package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class AppAdviseDetailsDTO {

    /**
     * pkey
     */
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;

    /**
     * 正文
     */
    @Schema(description = "正文")
    private String content;

    /**
     * 提交人
     */
    @Schema(description = "提交人")
    private Integer member;

    /**
     * 提交人姓名
     */
    @Schema(description = "提交人姓名", hidden = true)
    private String memberName;

    /**
     * 提交人手机
     */
    @Schema(description = "提交人手机")
    private String mobile;

    /**
     * 建档时间
     */
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;
}
