package cn.tofocus.file.db.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.db.SlowQueryLog;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.file.bean.FileRecord;
import cn.tofocus.file.bean.FileReport;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.db.entity.FileRecordEntity;
import cn.tofocus.file.db.entity.FileRecordEntity.F;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.db.service.FileRecordService;

@Component
public class FileRecordDao extends JpaSpecificationDelegate<FileRecordKey, FileRecordEntity>
    implements FileRecordService
{
    
    public FileReport aggFileByMime(StartDate startDate, EndDate endDate)
    {
        List<ReportItem> list = this.aggregation()
            .groupby(F.contentType, "name")
            .ge(F.createdTime, startDate)
            .lt(F.createdTime, endDate)
            .count("*", "count")
            .sum(F.size, "size")
            .execList(ReportItem.class);
        FileReport r = new FileReport();
        r.setList(list);
        return r;
    }
    
    @Override
    public FileRecord getFileRecord(String md5, long size)
    {
        return BeanUtil.beanFrom(FileRecord.class, this.get(new FileRecordKey(md5, size)));
    }
    
    @Override
    @SlowQueryLog
    @Transactional(rollbackFor = Exception.class)
    public void addFileRecord(FileRecord fileRecord)
    {
        FileRecordEntity entity = BeanUtil.beanFrom(FileRecordEntity.class, fileRecord);
        this.add(entity);
    }

    @Override
    @SlowQueryLog
    @Transactional(rollbackFor = Exception.class)
    public void updateFileRecord(FileRecord fileRecord)
    {
        FileRecordEntity entity = BeanUtil.beanFrom(FileRecordEntity.class, fileRecord);
        this.update(entity);
    }
    
}
