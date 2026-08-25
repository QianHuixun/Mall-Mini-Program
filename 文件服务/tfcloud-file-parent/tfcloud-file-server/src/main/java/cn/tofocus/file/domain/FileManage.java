package cn.tofocus.file.domain;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.file.Constant;
import cn.tofocus.file.bean.ClearStatus;
import cn.tofocus.file.bean.OrphanFileRecord;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.cache.TaskStatusMap;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.dao.FileRef2Dao;
import cn.tofocus.file.db.dao.FileRefDao;
import cn.tofocus.file.db.dao.FileRefLinkDao;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.db.repository.FileRecordRepository;
import cn.tofocus.file.db.repository.FileRef2Repository;
import cn.tofocus.file.exception.FileErrCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileManage
{
    
    @Value("${tofocus.file.root}")
    private String root;
    
    @Autowired
    private FileRecordDao fileRecordCache;
    
    @Autowired
    private FileRefDao oldRefDao;
    
    @Autowired
    private FileRef2Dao fileRefCache;
    
    @Autowired
    private FileRefLinkDao fileRefLinkDao;
    
    @Autowired
    private FileRecordRepository fileRecordRepository;
    
    @Autowired
    private FileRef2Repository fileRef2Repository;
    
    @Autowired
    private RedisLockTemplate redisLockTemplate;
    
    @Autowired
    private TaskStatusMap taskStatusMap;

    @Autowired
    private FileServerV3 fileServer;
    
    private static final String LOCK = "clear";
    
    public void checkClearTask()
    {
        if (redisLockTemplate.checkLock(Constant.DOMAIN, Constant.APP, LOCK))
            throw TofocusException.of(FileErrCode.CLEAR_RUNNING);
    }
    
    public ClearStatus clearStaus()
    {
        return JsonUtil.getBean(taskStatusMap.get(LOCK), ClearStatus.class);
    }
    
    

    public OrphanFileRecord checkOrphanFileRecord()
    {
        long oldRefCount = oldRefDao.aggregation().execCount();
        long refCount = fileRefCache.aggregation().execCount();
        long refCount2 = fileRefLinkDao.aggregation().execCountDistinct("filePkey");
        
        long recCount = fileRecordCache.aggregation().execCount();
        long recCount2 = fileRefCache.aggregation().execCountDistinct("md5");
        
        long recSize = fileRecordCache.aggregation().execSum("size").longValue();
        
        OrphanFileRecord r = new OrphanFileRecord();
        r.setOrphanRec(recCount - recCount2);
        r.setOrphanRef(refCount - refCount2);
        r.setOldRefCount(oldRefCount);
        r.setRecCount(recCount);
        r.setRecSize(recSize);
        r.setRefCount(refCount);
        return r;
    }
    
    @SneakyThrows
    public void clearOrphanFileRecord()
    {
        redisLockTemplate.lock(Constant.DOMAIN, Constant.APP, LOCK);
        try
        {
            log.info("开始清理孤儿引用");
            if (oldRefDao.aggregation().execCount() <= 0)
            {
                ClearStatus status = new ClearStatus();
                status.setName("删除当前没有任何引用并且创建时间大于30天的文件记录及文件");
                //删除没有引用的FileRefEntity
                List<Object> list2 = fileRef2Repository.allOrphanFileRef();
                List<Long> refPkeys = new ArrayList<>();
                for (Object o : list2)
                {
                    refPkeys.add(((Number)o).longValue());
                }
                status.setRefCount(refPkeys.size());
                fileRefCache.removeAllById(refPkeys);
                log.info("删除{}个孤儿引用", list2.size());
                
                //删除没有引用的文件
                List<Map<String, Object>> list = fileRecordRepository.allOrphanFileRecord();
                //TODO 会内存溢出
                Collection<FileRecordKey> recPkeys = new ArrayList<>();
                for (Map<String, Object> o : list)
                {
                    String md5 = (String)o.get("md5");
                    long size = ((Number)o.get("size")).longValue();
                    recPkeys.add(new FileRecordKey(md5, size));
                    Files.deleteIfExists(new File(fileServer.thumbPath(md5, size, BaseFileServer.thumb2Size(ThumbType.small)).build()).toPath());
                    Files.deleteIfExists(new File(fileServer.thumbPath(md5, size, BaseFileServer.thumb2Size(ThumbType.big)).build()).toPath());
                    Files.deleteIfExists(new File(fileServer.filePath(md5, size).build()).toPath());
                    status.setFileCount(status.getFileCount() + 1);
                    status.setPercent(status.getFileCount() * 1.0 / list.size());
                    taskStatusMap.put(LOCK, JsonUtil.toString(status));
                }
                fileRecordCache.removeAllById(recPkeys);
                log.info("删除{}个孤儿文件记录", list.size());
            }
            else
            {
                log.info("旧引用未完成升级");
            }
            log.info("孤儿引用清理完成");
        }
        catch (Exception e)
        {
            throw TofocusException.of(FileErrCode.CLEAR_FAIL, e);
        }
        finally
        {
            taskStatusMap.remove(LOCK);
            redisLockTemplate.unlock(Constant.DOMAIN, Constant.APP, LOCK);
        }
    }
    
    @SneakyThrows
    public void clearOrphanFile()
    {
        redisLockTemplate.lock(Constant.DOMAIN, Constant.APP, LOCK);
        try
        {
            log.info("开始清理孤儿物理文件");
            ClearStatus status = new ClearStatus();
            status.setName("删除没有数据库文件记录的文件");
            //扫描所有文件
            File rootfile = new File(root);
            File[] dirs0 = rootfile.listFiles();
            System.out.println();
            for (int i = 0; i < dirs0.length; i++)
            {
                double pstep1 = 1.0 / dirs0.length;
                double p1 = i * pstep1;
                File dir0 = dirs0[i];
                File[] dirs1 = dir0.listFiles();
                for (int j = 0; j < dirs1.length; j++)
                {
                    double pstep2 = 1.0 / dirs1.length;
                    double p2 = p1 + pstep1 * (j * pstep2);
                    File dir1 = dirs1[j];
                    File[] files = dir1.listFiles();
                    for (int k = 0; k < files.length; k++)
                    {
                        double pstep3 = 1.0 / files.length;
                        double p3 = p2 + pstep2 * (k * pstep3);
                        File file = files[k];
                        String[] sss = file.getName().split("_");
                        if (sss.length == 2)
                        {
                            long size = Long.parseLong(sss[1]);
                            FileRecordKey key = new FileRecordKey(sss[0], size);
                            //如果 file_record 里没有就删掉
                            if (!fileRecordCache.isExistKey(key))
                            {
                                Files.deleteIfExists(
                                    new File(fileServer.thumbPath(sss[0], size, BaseFileServer.thumb2Size(ThumbType.small)).build()).toPath());
                                Files.deleteIfExists(
                                    new File(fileServer.thumbPath(sss[0], size, BaseFileServer.thumb2Size(ThumbType.big)).build()).toPath());
                                Files.deleteIfExists(new File(fileServer.filePath(sss[0], size).build()).toPath());
                                System.out.print("D");
                                status.setFileCount(status.getFileCount() + 1);
                            }
                        }
                        status.setPercent(p3);
                        taskStatusMap.put(LOCK, JsonUtil.toString(status));
                    }
                    System.out.print(".");
                }
            }
            log.info("孤儿物理文件清理完成");
        }
        catch (Exception e)
        {
            throw TofocusException.of(FileErrCode.CLEAR_FAIL, e);
        }
        finally
        {
            taskStatusMap.remove(LOCK);
            redisLockTemplate.unlock(Constant.DOMAIN, Constant.APP, LOCK);
        }
    }
}
