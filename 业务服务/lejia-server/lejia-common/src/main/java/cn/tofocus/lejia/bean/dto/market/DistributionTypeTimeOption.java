package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DistributionTypeTimeOption
{
    private String day;

    @Schema(description = "可选配送")
    private List<String> psOption;
}
