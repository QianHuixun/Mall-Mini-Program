package cn.tofocus.file.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BaseStatus
{
    @Schema(description = "任务名")
    private String name;
}
