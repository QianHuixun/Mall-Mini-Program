package cn.tofocus.file.db.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.Sequence;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.file.bean.FileRefLink;
import cn.tofocus.file.bean.FileRefLinkInfo;
import cn.tofocus.file.bean.FileReport;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.db.entity.FileRefLinkEntity;
import cn.tofocus.file.db.entity.FileRefLinkEntity.F;
import cn.tofocus.file.db.service.FileRefLinkService;

@Component
public class FileRefLinkDao extends JpaSpecificationDelegate<Long, FileRefLinkEntity> implements FileRefLinkService
{
    private Sequence sequence = new Sequence();

    @Override
    public void delByPkey(String domain, String db, String table, List<String> pkeys)
    {
        if (CollectionUtil.isNotEmpty(pkeys))
        {
            List<FileRefLinkEntity> list =
                this.select().eq(F.domain, domain).eq(F.db, db).eq(F.table, table).in(F.dataPkey, pkeys).exec();
            this.removeAll(list);
        }
    }

    @Override
    public void delByTable(String domain, String db, String table)
    {
        List<FileRefLinkEntity> list = this.select().eq(F.domain, domain).eq(F.db, db).eq(F.table, table).exec();
        this.removeAll(list);
    }
    
    public FileReport aggByDomain(StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.domain, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public FileReport aggByDb(String domain, StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .eq(F.domain, domain)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.db, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public FileReport aggByTable(String domain, String db, StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .eq(F.domain, domain)
            .eq(F.db, db)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.table, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public FileReport aggByOrg(String domain, StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .eq(F.domain, domain)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.org, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public FileReport aggByDept(String domain, StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .eq(F.domain, domain)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.dept, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public PageResult<FileRefLinkInfo> queryLink(Integer page, Integer pagesize, String domain, String db, String table,
        Long filePkey, String org, String dept, Long minSize, Long maxSize, StartDate startDate, EndDate endDate)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.domain, domain)
            .eq(F.db, db)
            .eq(F.table, table)
            .eq(F.filePkey, filePkey)
            .eq(F.org, org)
            .eq(F.dept, dept)
            .ge(F.size, minSize)
            .le(F.size, maxSize)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .sort(F.createdTime)
            .execDto(FileRefLinkInfo.class);
    }

    @Override
    @SlowQueryLog
    @Transactional(rollbackFor = Exception.class)
    public void putAllFileRefLink(List<FileRefLink> list)
    {
        for(FileRefLink ref : list)
        {
            if(ref.getPkey() == null)
                ref.setPkey((long)sequence.next());
        }
        List<FileRefLinkEntity> entitys = BeanUtil.beanListFrom(FileRefLinkEntity.class, list);
        this.putAll(entitys);
    }
    
}
