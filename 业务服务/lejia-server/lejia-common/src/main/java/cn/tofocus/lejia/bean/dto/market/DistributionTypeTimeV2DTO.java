package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import cn.tofocus.lejia.bean.enums.DistributionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DistributionTypeTimeV2DTO
{
    @Schema(description = "类型")
    private DistributionType type;
    
    @Schema(description = "立即配送")
    private String imPsTime;
    
    private List<DistributionTypeTimeOption> lines;
}
