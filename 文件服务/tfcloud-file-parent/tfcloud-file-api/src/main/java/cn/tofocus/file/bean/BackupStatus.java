package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.common.util.NumUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BackupStatus extends BaseStatus
{
    @Schema(description = "总文件数")
    private int total;
    
    @Schema(description = "缺失文件数")
    private int miss;
    
    @Schema(description = "复制文件数")
    private int count;
    
    @Schema(description = "复制大小")
    private long size;
    
    @Schema(description = "复制大小")
    public String getSizeStr()
    {
        return NumUtil.byteSizeToStr(size);
    }
    
    @Schema(description = "已存在文件数")
    private int skip;
    
    @Schema(description = "创建时间")
    private Date createdTime = new Date();

    @Schema(description = "进度[范围 0到1]")
    public double getPercent()
    {
        return (miss + count + skip) * 1.0 / total;
    }
    
    @Schema(description = "进度")
    public String getProgress()
    {
        return NumUtil.percent(getPercent());
    }
    
}
