package cn.tofocus.file.log;

import java.util.ArrayList;
import java.util.List;

import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.file.bean.FileAccessInfo;
import cn.tofocus.file.bean.FileDownloadReportItem;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.bean.ReportItemWithTime;
import cn.tofocus.file.bean.ThumbType;

public interface FileAccessLogQuery
{
    
    default PageResult<FileAccessInfo> query(Integer page, Integer pagesize, String md5, Long filePkey,
        ThumbType thumbType, String ip, String referer, String deviceType, String deviceName, String os,
        String osVersion, String agentType, String agentName, String agentVersion, String status, Long minSize,
        Long maxSize, StartDate startDate, EndDate endDate)
    {
        return new PageResult<>(new ArrayList<>(), new PageParameter(page, pagesize));
    }
    
    default List<ReportItem> top10Count(Long filePkey, String group1, String group2, StartDate startDate,
        EndDate endDate)
    {
        return new ArrayList<>();
    }
    
    default List<ReportItem> top10Size(Long filePkey, String group1, String group2, StartDate startDate,
        EndDate endDate)
    {
        return new ArrayList<>();
    }
    
    default List<FileDownloadReportItem> top10FileCount(StartDate startDate, EndDate endDate)
    {
        return new ArrayList<>();
    }
    
    default List<FileDownloadReportItem> top10FileSize(StartDate startDate, EndDate endDate)
    {
        return new ArrayList<>();
    }
    
    default ReportItemWithTime total(StartDate startDate, EndDate endDate)
    {
        ReportItemWithTime r = new ReportItemWithTime();
        r.setCount(0);
        r.setSize(0L);
        r.setName("合计");
        return r;
    }
}
