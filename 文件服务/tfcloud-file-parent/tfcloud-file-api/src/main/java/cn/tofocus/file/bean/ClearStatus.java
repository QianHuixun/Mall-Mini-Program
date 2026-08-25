package cn.tofocus.file.bean;

import cn.tofocus.common.util.NumUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClearStatus extends BaseStatus
{
    @Schema(description = "删除孤儿引用数")
    private int refCount;
    
    @Schema(description = "删除文件数")
    private int fileCount;

    @Schema(description = "进度[范围 0到1]")
    private double percent;
    
    @Schema(description = "进度")
    public String getProgress()
    {
        return NumUtil.percent(percent);
    }
}
