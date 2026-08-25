package cn.tofocus.file.bean;

import cn.tofocus.common.util.NumUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReportItem
{
    @Schema(description = "说明")
    private String name;

    @Schema(description = "数量")
    private int count;

    @Schema(description = "大小")
    private long size;

    @Schema(description = "大小")
    public String getSizeStr()
    {
        return NumUtil.byteSizeToStr(size);
    }
}
