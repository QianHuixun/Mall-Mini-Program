package cn.tofocus.file.es.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ShardType;
import cn.tofocus.db.es.dao.EsClusterDao;
import cn.tofocus.file.bean.FileAccessInfo;
import cn.tofocus.file.bean.FileDownloadReportItem;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.bean.ReportItem2Group;
import cn.tofocus.file.bean.ReportItemWithTime;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.es.dao.config.FileAccessLogConfigDao;
import cn.tofocus.file.es.doc.FileAccessLog;
import cn.tofocus.file.es.doc.FileAccessLog.F;
import cn.tofocus.file.log.FileAccessLogQuery;

@Component
public class FileAccessLogDao extends EsClusterDao<Long, FileAccessLog> implements FileAccessLogQuery
{
    @Autowired
    private FileAccessLogConfigDao configDao;
    
    @Override
    protected ShardType shardType()
    {
        return ShardType.year;
    }
    
    @Override
    protected boolean isPremierDb()
    {
        return true;
    }
    
    @Override
    protected void updateShardConfig(String shard, Long minkey, Long maxkey)
    {
        configDao.updateShardConfig(shard, minkey, maxkey);
    }
    
    @Override
    protected void resetShardConfig(String shard, Long minkey, Long maxkey)
    {
        configDao.resetShardConfig(shard, minkey, maxkey);
    }
    
    @Override
    protected List<String> findShardByPkey(Long key)
    {
        return configDao.findShardByPkey(key);
    }
    
    @Override
    protected Map<String, List<Long>> findShardByPkeys(Collection<Long> keys)
    {
        return configDao.findShardByPkeys(keys);
    }
    
    @Override
    public List<ReportItem> top10Count(Long filePkey, String group1, String group2, StartDate startDate,
        EndDate endDate)
    {
        List<ReportItem2Group> list = this.aggregation()
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .eq(F.filePkey, filePkey)
            .groupby(group1, "name")
            .groupby(group2, "group2")
            .count("*", "count")
            .sum(F.size, "size")
            .sort("count")
            .limit(10)
            .execList(ReportItem2Group.class);
        return item2Group(list);
    }

    @Override
    public List<ReportItem> top10Size(Long filePkey, String group1, String group2, StartDate startDate, EndDate endDate)
    {
        List<ReportItem2Group> list = this.aggregation()
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .eq(F.filePkey, filePkey)
            .groupby(group1, "name")
            .groupby(group2, "group2")
            .count("*", "count")
            .sum(F.size, "size")
            .sort("size")
            .limit(10)
            .execList(ReportItem2Group.class);
        return item2Group(list);
    }
    
    private List<ReportItem> item2Group(List<ReportItem2Group> list)
    {
        List<ReportItem> l = new ArrayList<>();
        for (ReportItem2Group g : list)
        {
            ReportItem item = new ReportItem();
            item.setSize(g.getSize());
            item.setCount(g.getCount());
            if (g.getGroup2() == null)
                item.setName(g.getName());
            else
                item.setName(g.getName() + " " + g.getGroup2());
            l.add(item);
        }
        return l;
    }

    @Override
    public List<FileDownloadReportItem> top10FileCount(StartDate startDate, EndDate endDate)
    {
        List<FileDownloadReportItem> list = this.aggregation()
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .groupby(F.filePkey, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .limit(10)
            .execList(FileDownloadReportItem.class);
        return list;
    }

    @Override
    public List<FileDownloadReportItem> top10FileSize(StartDate startDate, EndDate endDate)
    {
        List<FileDownloadReportItem> list = this.aggregation()
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .groupby(F.filePkey, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .sort("size")
            .limit(10)
            .execList(FileDownloadReportItem.class);
        return list;
    }

    @Override
    public ReportItemWithTime total(StartDate startDate, EndDate endDate)
    {
        ReportItemWithTime r;
        List<ReportItemWithTime> list = this.aggregation()
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .count("*", "count")
            .sum(F.size, "size")
            .min(F.accessTime, "minDate")
            .max(F.accessTime, "maxDate")
            .execList(ReportItemWithTime.class);
        if (!list.isEmpty())
        {
            r = list.get(0);
        }
        else
        {
            r = new ReportItemWithTime();
            r.setCount(0);
            r.setSize(0L);
        }
        r.setName("合计");
        return r;
    }

    @Override
    public PageResult<FileAccessInfo> query(Integer page, Integer pagesize, String md5, Long filePkey,
        ThumbType thumbType, String ip, String referer, String deviceType, String deviceName, String os,
        String osVersion, String agentType, String agentName, String agentVersion, String status, Long minSize,
        Long maxSize, StartDate startDate, EndDate endDate)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.fileMd5, md5)
            .eq(F.filePkey, filePkey)
            .eq(F.thumb, thumbType)
            .eq(F.ip, ip)
            .like(F.referer, referer)
            .eq(F.deviceType, deviceType)
            .eq(F.deviceName, deviceName)
            .eq(F.os, os)
            .eq(F.osVersion, osVersion)
            .eq(F.agentType, agentType)
            .eq(F.agentName, agentName)
            .eq(F.agentVersion, agentVersion)
            .eq(F.status, status)
            .ge(F.size, minSize)
            .le(F.size, maxSize)
            .ge(F.accessTime, startDate)
            .lt(F.accessTime, endDate)
            .sort(F.accessTime)
            .execDto(FileAccessInfo.class);
    }
    
}
