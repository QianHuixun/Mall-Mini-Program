package cn.tofocus.file.bean;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BackupInfo implements Comparable<BackupInfo>
{
    @Schema(description = "年")
    private int year;

    @Schema(description = "月")
    private int month;

    @Schema(description = "文件数")
    private int fileCount;

    @Schema(description = "缩略图数")
    private int thumbCount;

    @Schema(description = "总大小数")
    private String totalSize;

    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Override
    public int compareTo(BackupInfo o)
    {
        if (this.getYear() == o.getYear())
            return o.getMonth() - this.getMonth();
        else
            return o.getYear() - this.getYear();
    }
}
