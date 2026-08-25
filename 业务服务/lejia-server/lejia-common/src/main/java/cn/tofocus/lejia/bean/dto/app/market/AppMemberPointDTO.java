package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberPointDTO {

    /**
     * 积分值
     */
    @Schema(description = "积分值")
    private Integer points;

    /**
     * 积分值
     */
    @Schema(description = "累计积分值")
    private Integer accumulatedPoints;
}
