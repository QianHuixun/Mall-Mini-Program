package cn.tofocus.file.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileDownloadReportItem extends ReportItem
{
    @Schema(description = "文件引用")
    private FileRefInfo ref;
}
