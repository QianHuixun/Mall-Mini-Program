package cn.tofocus.file.db.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.file.bean.FileRef;
import cn.tofocus.file.db.entity.FileRefEntity;
import cn.tofocus.file.db.service.FileRefService;

@Component
@Deprecated
public class FileRefDao extends JpaSpecificationDelegate<Long, FileRefEntity> implements FileRefService
{
    @Autowired
    private RedisCounter redisCounter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileRefEntity add(FileRefEntity value)
    {
        Long id = redisCounter.increment("fileServer", "fileRef");
        value.setPkey(id);
        value.setCreatedTime(new Date());
        return super.add(value);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public synchronized void referencesFiles(List<Long> ids, String refUrl)
    {
        if(ids != null && !ids.isEmpty())
        {
            List<FileRefEntity> list = this.get(ids);
            for (FileRefEntity ref : list)
            {
                if (ref.getRefCount() == null)
                    ref.setRefCount(1);
                else
                    ref.setRefCount(ref.getRefCount() + 1);
                if (ref.getRefUrl() == null)
                    ref.setRefUrl(refUrl);
                else 
                {
                    if (refUrl != null && !ref.getRefUrl().contains(refUrl))
                        ref.setRefUrl(ref.getRefUrl() + "," + refUrl);
                }
            }
            this.updateAll(list);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public synchronized void unReferencesFiles(List<Long> ids)
    {
        if(ids != null && !ids.isEmpty())
        {
            List<FileRefEntity> list = this.get(ids);
            for (FileRefEntity ref : list)
            {
                if (ref.getRefCount() == null)
                    ref.setRefCount(0);
                else
                    ref.setRefCount(ref.getRefCount() - 1);
            }
            this.updateAll(list);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public synchronized void changeReferencesFiles(List<Long> oldIds, List<Long> ids, String refUrl)
    {
        if (oldIds != null && !oldIds.isEmpty())
        {
            List<FileRefEntity> oldList = this.get(oldIds);
            for (FileRefEntity ref : oldList)
            {
                if (ref.getRefCount() == null)
                    ref.setRefCount(0);
                else
                    ref.setRefCount(ref.getRefCount() - 1);
            }
            this.updateAll(oldList);
        }
        if (ids != null && !ids.isEmpty())
        {
            List<FileRefEntity> list = this.get(ids);
            for (FileRefEntity ref : list)
            {
                if (ref.getRefCount() == null)
                    ref.setRefCount(1);
                else
                    ref.setRefCount(ref.getRefCount() + 1);
                if (ref.getRefUrl() == null)
                    ref.setRefUrl(refUrl);
                else
                {
                    if (refUrl != null && !ref.getRefUrl().contains(refUrl))
                        ref.setRefUrl(ref.getRefUrl() + "," + refUrl);
                }
            }
            this.updateAll(list);
        }
    }

    @Override
    public FileRef getFileRef(long id)
    {
        return BeanUtil.beanFrom(FileRef.class, this.get(id));
    }
    
}
