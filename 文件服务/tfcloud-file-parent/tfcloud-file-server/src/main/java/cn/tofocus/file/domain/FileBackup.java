package cn.tofocus.file.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.NumUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.file.Constant;
import cn.tofocus.file.bean.BackupInfo;
import cn.tofocus.file.bean.BackupStatus;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.cache.TaskStatusMap;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.entity.FileRecordEntity;
import cn.tofocus.file.exception.FileErrCode;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileBackup
{
    @Value("${tofocus.file.backup}")
    private String backup;
    
    @Value("${tofocus.file.backup.active:false}")
    private boolean active;
    
    @Autowired
    private FileRecordDao fileRecordCache;
    
    @Autowired
    private FileServerV3 server;
    
    @Autowired
    private RedisLockTemplate redisLockTemplate;
    
    @Autowired
    private TaskStatusMap taskStatusMap;
    
    private static final String LOCK = "backup";
    
    @Scheduled(cron = "0 0 1 1 * ?") //每月1日1点
    public void backupPerMonth()
    {
        if (active)
        {
            LocalDate d = LocalDate.now().minusMonths(1);
            backupByMonth(d.getYear(), d.getMonthValue());
        }
    }
    
    public BackupStatus backupStaus()
    {
        return JsonUtil.getBean(taskStatusMap.get(LOCK), BackupStatus.class);
    }
    
    public void checkBackupTask()
    {
        if (redisLockTemplate.checkLock(Constant.DOMAIN, Constant.APP, LOCK))
            throw TofocusException.of(FileErrCode.BACKUP_RUNNING);
    }
    
    public void backupByMonth(int year, int month)
    {
        redisLockTemplate.lock(Constant.DOMAIN, Constant.APP, LOCK);
        try
        {
            log.info("开始备份{}年{}月数据", year, month);
            int small = BaseFileServer.thumb2Size(ThumbType.small);
            int big = BaseFileServer.thumb2Size(ThumbType.big);
            BackupStatus status = new BackupStatus();
            //查数据
            List<FileRecordEntity> list = fileRecordCache.select()
                .ge("createdTime", DateUtil.atStartOfMonth(year, month))
                .lt("createdTime", DateUtil.atStartOfNextMonth(year, month))
                .exec();
            status.setName(new StringBuilder("正在备份").append(year).append("年").append(month).append("月").toString());
            status.setTotal(list.size());
            for (FileRecordEntity r : list)
            {
                String md5 = r.getMd5();
                long size = r.getSize();
                copyFile(server.filePath(md5, size), filePath(year, month, md5, size), status);
                copyFile(server.thumbPath(md5, size, big), thumbPath(year, month, md5, size, big), status);
                copyFile(server.thumbPath(md5, size, small), thumbPath(year, month, md5, size, small), status);
            }
            log.info("复制{}个文件共{}，跳过{}个文件，缺失{}个文件",
                status.getCount(),
                NumUtil.byteSizeToStr(status.getSize()),
                status.getSkip(),
                status.getMiss());
            log.info("{}年{}月数据备份完成", year, month);
            
            File dir = new File(monthPath(year, month));
            File info = new File(dir.getAbsolutePath() + File.separator + ".info");
            Files.deleteIfExists(info.toPath());
            checkStatus(dir, new BackupInfo());
        }
        catch (Exception e)
        {
            throw TofocusException.of(FileErrCode.BACKUP_FAIL, e);
        }
        finally
        {
            taskStatusMap.remove(LOCK);
            redisLockTemplate.unlock(Constant.DOMAIN, Constant.APP, LOCK);
        }
        
    }
    
    public void restoreByMonth(int year, int month)
    {
        redisLockTemplate.lock(Constant.DOMAIN, Constant.APP, LOCK);
        try
        {
            log.info("开始还原{}年{}月数据", year, month);
            int small = BaseFileServer.thumb2Size(ThumbType.small);
            int big = BaseFileServer.thumb2Size(ThumbType.big);
            BackupStatus status = new BackupStatus();
            //查数据
            List<FileRecordEntity> list = fileRecordCache.select()
                .ge("createdTime", DateUtil.atStartOfMonth(year, month))
                .lt("createdTime", DateUtil.atStartOfNextMonth(year, month))
                .exec();
            status.setName(new StringBuilder("正在还原").append(year).append("年").append(month).append("月").toString());
            status.setTotal(list.size());
            for (FileRecordEntity r : list)
            {
                String md5 = r.getMd5();
                long size = r.getSize();
                copyFile(filePath(year, month, md5, size), server.filePath(md5, size), status);
                copyFile(thumbPath(year, month, md5, size, big), server.thumbPath(md5, size, big), status);
                copyFile(thumbPath(year, month, md5, size, small), server.thumbPath(md5, size, small), status);
            }
            log.info("复制{}个文件共{}，跳过{}个文件，缺失{}个文件",
                status.getCount(),
                NumUtil.byteSizeToStr(status.getSize()),
                status.getSkip(),
                status.getMiss());
            log.info("{}年{}月数据还原完成", year, month);
        }
        catch (Exception e)
        {
            throw TofocusException.of(FileErrCode.RESTORE_FAIL, e);
        }
        finally
        {
            taskStatusMap.remove(LOCK);
            redisLockTemplate.unlock(Constant.DOMAIN, Constant.APP, LOCK);
        }
    }
    
    private void copyFile(PathBuilder ori, PathBuilder dest, BackupStatus status)
    {
        File oriFile = new File(ori.build());
        if (oriFile.exists())
        {
            File destFile = new File(dest.build());
            if (!destFile.exists())
            {
                FileUtil.checkDirectory(dest.buildPath());
                FileUtil.copyFile(ori.build(), dest.build());
                if (ori.getThumb() == null)
                {
                    status.setCount(status.getCount() + 1);
                    status.setSize(status.getSize() + oriFile.length());
                }
            }
            else
            {
                if (ori.getThumb() == null)
                    status.setSkip(status.getSkip() + 1);
            }
        }
        else
        {
            if (ori.getThumb() == null)
                status.setMiss(status.getMiss() + 1);
        }
        taskStatusMap.put(LOCK, JsonUtil.toString(status));
    }
    
    private String monthPath(int year, int month)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(backup);
        sb.append(File.separator);
        sb.append(year);
        sb.append(File.separator);
        sb.append(month);
        return sb.toString();
    }
    
    private PathBuilder filePath(int year, int month, String md5, long filesize)
    {
        
        PathBuilder pb = new PathBuilder().root(monthPath(year, month)).md5(md5).size(filesize);
        return pb;
    }
    
    private PathBuilder thumbPath(int year, int month, String md5, long filesize, int thumb)
    {
        PathBuilder pb = new PathBuilder().root(monthPath(year, month)).md5(md5).size(filesize).thumb(thumb);
        return pb;
    }
    
    public List<BackupInfo> list()
    {
        List<BackupInfo> list = new ArrayList<>();
        File root = new File(backup);
        //年
        File[] yearDirs = listFiles(root);
        for (File yearDir : yearDirs)
        {
            //月
            File[] mounthDirs = listFiles(yearDir);
            for (File mounthDir : mounthDirs)
            {
                if (mounthDir.isDirectory())
                {
                    BackupInfo info = new BackupInfo();
                    info.setYear(Integer.parseInt(yearDir.getName()));
                    info.setMonth(Integer.parseInt(mounthDir.getName()));
                    info.setCreatedTime(new Date(mounthDir.lastModified()));
                    checkStatus(mounthDir, info);
                    list.add(info);
                }
            }
        }
        Collections.sort(list);
        return new ArrayList<>(list);
    }
    
    private File[] listFiles(File dir)
    {
        File[] list = null;
        if (dir != null && dir.exists() && dir.isDirectory())
        {
            list = dir.listFiles();
        }
        if (list == null)
            list = new File[0];
        return list;
    }
    
    private void checkStatus(File dir, BackupInfo backup)
    {
        File info = new File(dir.getAbsolutePath() + File.separator + ".info");
        if (info.exists())
        {
            BackupInfo old = JsonUtil.getBean(FileUtil.readFileContent(info), BackupInfo.class);
            backup.setFileCount(old.getFileCount());
            backup.setThumbCount(old.getThumbCount());
            backup.setTotalSize(old.getTotalSize());
        }
        else
        {
            //L1
            int fileCount = 0;
            int thumbCount = 0;
            long totalSize = 0;
            File[] dirs1 = listFiles(dir);
            for (File dir1 : dirs1)
            {
                //L2
                File[] dirs2 = listFiles(dir1);
                for (File dir2 : dirs2)
                {
                    File[] files = listFiles(dir2);
                    for (File file : files)
                    {
                        if (file.getName().split("_").length > 2)
                        {
                            thumbCount++;
                        }
                        else
                        {
                            fileCount++;
                        }
                        totalSize = totalSize + file.length();
                    }
                }
            }
            backup.setFileCount(fileCount);
            backup.setThumbCount(thumbCount);
            backup.setTotalSize(NumUtil.byteSizeToStr(totalSize));
            FileUtil.saveFile(info.getAbsolutePath(), JsonUtil.toString(backup).getBytes());
        }
    }
    
    public void delBackup(int year, int month)
    {
        File dir = new File(monthPath(year, month));
        try
        {
            FileUtil.deleteDirectory(dir);
        }
        catch (IOException e)
        {
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
        }
    }
}
