package cn.tofocus.lejia.bean.dto.market;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CardStatisticsInfo
{
    @Schema(description = "总数")
    private Long sum;
    
    @Schema(description = "未使用")
    private Long unusedNum;
    
    @Schema(description = "已使用")
    private Long usedNum;
    
    @Schema(description = "已过期")
    private Long expiredNum;
    
    @Schema(description = "已失效")
    private Long invalidNum;
    
    @JsonIgnore
    private CardStatus status;
    
    @JsonIgnore
    private Boolean invalid;
}
