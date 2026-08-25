package cn.tofocus.file.db.dao;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.file.bean.FileRef2;
import cn.tofocus.file.bean.FileRefInfo;
import cn.tofocus.file.bean.FileReport;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.bean.UploadType;
import cn.tofocus.file.db.entity.FileRef2Entity;
import cn.tofocus.file.db.entity.FileRef2Entity.F;
import cn.tofocus.file.db.service.FileRef2Service;

@Component
public class FileRef2Dao extends JpaSpecificationDelegate<Long, FileRef2Entity> implements FileRef2Service
{
    
    public FileReport aggRefByApp(StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.appid, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public FileReport aggRefByExt(StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .groupby(F.extName, "name")
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    public PageResult<FileRefInfo> queryRef(Integer page, Integer pagesize, String md5, String fileName, String extName,
        String title, String memo, String appid, Integer userkey, UploadType type, Long minSize, Long maxSize,
        StartDate startDate, EndDate endDate)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.md5, md5)
            .eq(F.fileName, fileName)
            .eq(F.extName, extName)
            .like(F.title, title)
            .like(F.memo, memo)
            .eq(F.appid, appid)
            .eq(F.userkey, userkey)
            .eq(F.type, type)
            .ge(F.size, minSize)
            .le(F.size, maxSize)
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .sort(F.createdTime)
            .execDto(FileRefInfo.class);
    }
    
    @Override
    @SlowQueryLog
    @Transactional(rollbackFor = Exception.class)
    public void addFileRef(FileRef2 ref)
    {
        FileRef2Entity entity = BeanUtil.beanFrom(FileRef2Entity.class, ref);
        this.add(entity);
    }
    
    @Override
    @SlowQueryLog
    public FileRef2 getFileRef(long id)
    {
        return BeanUtil.beanFrom(FileRef2.class, this.get(id));
    }
    
    @Override
    public List<FileRef2> listByPkeys(Set<Long> refPkeys)
    {
        List<FileRef2> list = this.select().in(FileRef2Entity.F.pkey, refPkeys).execDto(FileRef2.class);
        return list;
    }
    
}
