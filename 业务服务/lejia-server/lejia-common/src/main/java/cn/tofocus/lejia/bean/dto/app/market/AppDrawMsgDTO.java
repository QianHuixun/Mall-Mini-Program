package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AppDrawMsgDTO {

    /**
     * 剩余积分
     */
    @Schema(description = "剩余积分")
    private Integer points;

    /**
     * 单次抽奖消费
     */
    @Schema(description = "单次抽奖消费")
    private Integer singleDraw;

    /**
     * 积分值
     */
    @Schema(description = "奖品列表")
    private List<AppDrawPrizeDTO> prizeList;
    
    @Schema(description = "剩余抽奖次数")
    private Integer surplusNum;
}
