package cn.tofocus.file.bean;

import lombok.Data;

@Data
public class DownloadReport
{
    private ReportItemWithTime total;
    
    private ReportItem perDay;
}
