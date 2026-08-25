package cn.tofocus.file.api.v3;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.api.ApiLog;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.FileUrlUtil;
import cn.tofocus.db.file.IdUtil;
import cn.tofocus.db.file.RefChangeList;
import cn.tofocus.file.FileManageServer;
import cn.tofocus.file.bean.*;
import cn.tofocus.file.config.UeConfig;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.dao.FileRef2Dao;
import cn.tofocus.file.db.dao.FileRefDao;
import cn.tofocus.file.db.dao.FileRefLinkDao;
import cn.tofocus.file.db.entity.FileRecordEntity;
import cn.tofocus.file.db.entity.FileRefEntity;
import cn.tofocus.file.db.entity.FileRef2Entity;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.domain.FileBackup;
import cn.tofocus.file.domain.FileManage;
import cn.tofocus.file.domain.FileServerV3;
import cn.tofocus.file.log.FileAccessLogQuery;
import cn.tofocus.file.ueditor.ActionEnum;
import cn.tofocus.file.ueditor.StateEnum;
import cn.tofocus.file.ueditor.UeResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/v3")
@RestController
@Slf4j
public class FileApiV3Impl implements FileApiV3
{
    @Autowired
    private FileServerV3 server;
    
    @Autowired
    private FileManageServer fileManageServer;
    
    @Autowired
    private FileManage fileManage;
    
    @Autowired
    private FileBackup fileBackup;
    
    @Autowired
    private UeConfig ueConfig;
    
    @Autowired
    private FileRecordDao fileRecordDao;
    
    @Autowired
    private FileRefDao fileOldRefDao;
    
    @Autowired
    private FileRef2Dao fileRefDao;
    
    @Autowired
    private FileRefLinkDao fileRefLinkDao;
    
    @Autowired(required = false)
    private FileAccessLogQuery fileAccessLogDao;
    
    @PostConstruct
    public void init()
    {
        if (fileAccessLogDao == null)
            fileAccessLogDao = new FileAccessLogQuery()
            {
            };
    }
    
    @Override
    public Result<FileInfoV3> uploadImage(MultipartFile file, String title, String memo)
    {
        FileInfoV3 info = server.uploadImage(file, null, title, memo);
        return new Result<>(info);
    }
    
    @Override
    public Result<?> createImageThumb(String file, ThumbType thumb, boolean force)
    {
        Long pkey = IdUtil.extractId(file);
        if (pkey != null)
        {
            server.createImageThumb(pkey, thumb, force);
        }
        return new Result<>();
    }
    
    @Override
    public Result<FileInfoV3> uploadVideo(MultipartFile file, String title, String memo)
    {
        throw TofocusException.of(SysErrCode.UNIMPLENT_FUNCTION);
    }
    
    @Override
    public Result<FileInfoV3> uploadDoc(MultipartFile file, String title, String memo)
    {
        throw TofocusException.of(SysErrCode.UNIMPLENT_FUNCTION);
    }
    
    @Override
    public Result<FileInfoV3> uploadFile(MultipartFile file, String title, String memo)
    {
        FileInfoV3 info = server.uploadFile(file, null, title, memo);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileInfoV3> getFileInfo(String file)
    {
        Long pkey = IdUtil.extractId(file);
        FileInfoV3 info = server.getFileInfo(pkey);
        return new Result<>(info);
    }
    
    @Override
    public Result<?> referencesFiles(RefChangeList ref)
    {
        server.referencesFiles(ref.getDomain(), ref.getDb(), ref.getTable(), ref.getRefs());
        return new Result<>();
    }
    
    @Override
    public Result<?> referencesAllbyTable(RefChangeList ref)
    {
        server.referencesAllbyTable(ref.getDomain(), ref.getDb(), ref.getTable(), ref.getRefs());
        return new Result<>();
    }
    
    @Override
    public Result<FileInfoV3> update(String file, String domain, String db, String table, String pkey, String org,
        String dept, UploadType upType)
    {
        Long refkey = IdUtil.extractId(file);
        if (refkey != null)
        {
            FileInfoV3 info = server.updateFileRef(refkey, domain, db, table, pkey, org, dept, upType);
            return new Result<>(info);
        }
        else
        {
            return new Result<>();
        }
    }
    
    @Override
    public Result<List<FileInfoV3>> updateList(@Valid List<UpdateParam> list)
    {
        List<FileInfoV3> r = new ArrayList<>();
        for (UpdateParam u : list)
        {
            Long refkey = IdUtil.extractId(u.getFile());
            if (refkey != null)
            {
                FileInfoV3 info = server.updateFileRef(refkey,
                    u.getDomain(),
                    u.getDb(),
                    u.getTable(),
                    u.getPkey(),
                    u.getOrg(),
                    u.getDept(),
                    u.getUpType());
                r.add(info);
            }
            else
            {
                r.add(null);
            }
        }
        return new Result<>(r);
    }
    
    /***********************
     * 
     *        统计报表
     * 
     ***********************/
    
    @Override
    public Result<FileReport> aggFileByMime(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRecordDao.aggFileByMime(startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggRefByApp(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefDao.aggRefByApp(startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggRefByExt(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefDao.aggRefByExt(startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggByDomain(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByDomain(startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggByDb(String domain, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByDb(domain, startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggByTable(String domain, String db, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByTable(domain, db, startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggByOrg(String domain, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByOrg(domain, startDate, endDate);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileReport> aggByDept(String domain, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByDept(domain, startDate, endDate);
        return new Result<>(info);
    }
    
    /***********************
     * 
     *        查询
     * 
     ***********************/
    @Override
    public Result<PageResult<FileRefInfo>> queryRef(Integer page, Integer pagesize, String md5, String fileName,
        String extName, String title, String memo, String appid, Integer userkey, UploadType type, Long minSize,
        Long maxSize, StartDate startDate, EndDate endDate)
    {
        PageResult<FileRefInfo> r = fileRefDao.queryRef(page,
            pagesize,
            md5,
            fileName,
            extName,
            title,
            memo,
            appid,
            userkey,
            type,
            minSize,
            maxSize,
            startDate,
            endDate);
        for (FileRefInfo info : r.getContent())
        {
            FileRecordEntity rec = fileRecordDao.get(new FileRecordKey(info.getMd5(), info.getSize()));
            if (rec != null)
                info.setContentType(rec.getContentType());
            info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                .getFileBaseUrl(), info.getType().viewUrl(), info.getPkey(), info.getExtName(), info.getMd5()));
        }
        return new Result<>(r);
    }
    
    @Override
    public Result<PageResult<FileRefLinkInfo>> queryLink(Integer page, Integer pagesize, String domain, String db,
        String table, Long filePkey, String org, String dept, Long minSize, Long maxSize, StartDate startDate,
        EndDate endDate)
    {
        PageResult<FileRefLinkInfo> r = fileRefLinkDao
            .queryLink(page, pagesize, domain, db, table, filePkey, org, dept, minSize, maxSize, startDate, endDate);
        for (FileRefLinkInfo info : r.getContent())
        {
            if (info.getRef() != null)
            {
                FileRefInfo ref = info.getRef();
                info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                    .getFileBaseUrl(), ref.getType().viewUrl(), ref.getPkey(), ref.getExtName(), ref.getMd5()));
            }
        }
        return new Result<>(r);
    }
    
    /***********************
     * 
     *        清理
     * 
     ***********************/
    
    @Override
    public Result<OrphanFileRecord> checkOrphanFileRecord()
    {
        OrphanFileRecord r = fileManage.checkOrphanFileRecord();
        return new Result<>(r);
    }
    
    @Override
    public Result<String> clearOrphanFileRecord()
    {
        fileManage.checkClearTask();
        Thread t = new Thread(() -> fileManage.clearOrphanFileRecord());
        t.start();
        return new Result<>("开始清理");
    }
    
    @Override
    public Result<String> clearOrphanFile()
    {
        fileManage.checkClearTask();
        Thread t = new Thread(() -> fileManage.clearOrphanFile());
        t.start();
        return new Result<>("开始清理");
    }
    
    @Override
    public Result<ClearStatus> clearStaus()
    {
        return new Result<>(fileManage.clearStaus());
    }
    
    /***********************
     * 
     *        备份
     * 
     ***********************/
    
    @Override
    public Result<PageResult<BackupInfo>> listBackup(Integer page, Integer pagesize)
    {
        PageResult<BackupInfo> list = PageUtil.page(fileBackup.list(), new PageParameter(page, pagesize));
        return new Result<>(list);
    }
    
    @Override
    public Result<String> backup(int year, Integer month)
    {
        fileBackup.checkBackupTask();
        Thread t = new Thread(() -> {
            if (month != null)
                fileBackup.backupByMonth(year, month);
            else
            {
                for (int i = 1; i <= 12; i++)
                {
                    fileBackup.backupByMonth(year, i);
                }
            }
        });
        t.start();
        return new Result<>("开始备份");
    }
    
    @Override
    public Result<String> restore(int year, Integer month)
    {
        fileBackup.checkBackupTask();
        Thread t = new Thread(() -> {
            if (month != null)
                fileBackup.restoreByMonth(year, month);
            else
            {
                for (int i = 1; i <= 12; i++)
                {
                    fileBackup.restoreByMonth(year, i);
                }
            }
        });
        t.start();
        return new Result<>("开始还原");
    }

    @Override
    public Result<BackupStatus> backupStaus()
    {
        return new Result<>(fileBackup.backupStaus());
    }

    @ApiLog
    @Operation(summary = "获取富文本上传图片配置")
    @GetMapping(value = "/ue/config")
    public Object getUeditorConfig(@RequestParam(value = "action") ActionEnum action)
    {
        if (ActionEnum.config.equals(action))
            return ueConfig;
        else
            return new UeResult(StateEnum.INVALID_ACTION);
    }

    @ApiLog
    @Operation(summary = "获取富文本上传图片配置")
    @GetMapping(value = "/ue/uploadImages")
    public Object getUeditorImagesConfig(@RequestParam(value = "action") ActionEnum action)
    {
        if (ActionEnum.config.equals(action))
            return ueConfig;
        else
            return new UeResult(StateEnum.INVALID_ACTION);
    }

    @ApiLog
    @Operation(summary = "富文本上传单张图片")
    @PostMapping(value = "/ue/config", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object ueditorAction(@RequestPart(value = "upfile", required = false) MultipartFile file,
        @RequestParam(value = "action") ActionEnum action, HttpServletRequest request)
    {
        return server.ueditorAction(file, action, request.getHeader("Referer"));
    }

    @ApiLog
    @Operation(summary = "富文本上传多张图片")
    @PostMapping(value = "/ue/uploadImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object ueditorUploadImages(@RequestPart(value = "upfile", required = false) MultipartFile[] files,
        @RequestParam(value = "action") ActionEnum action, HttpServletRequest request)
    {
        return server.ueditorUploadImages(files, action, request.getHeader("Referer"));
    }
    
    /***********************
     * 
     *        文件访问记录
     * 
     ***********************/
    
    @Override
    public Result<PageResult<FileAccessInfo>> queryAccess(Integer page, Integer pagesize, String md5, Long filePkey,
        ThumbType thumbType, String ip, String referer, String deviceType, String deviceName, String os,
        String osVersion, String agentType, String agentName, String agentVersion, String status, Long minSize,
        Long maxSize, StartDate startDate, EndDate endDate)
    {
        PageResult<FileAccessInfo> r = fileAccessLogDao.query(page,
            pagesize,
            md5,
            filePkey,
            thumbType,
            ip,
            referer,
            deviceType,
            deviceName,
            os,
            osVersion,
            agentType,
            agentName,
            agentVersion,
            status,
            minSize,
            maxSize,
            startDate,
            endDate);
        
        List<Long> keys = new ArrayList<>();
        r.getContent().forEach(e -> keys.add(e.getFilePkey()));
        
        Map<Long, FileRef2Entity> map = CollectionUtil.list2Map(fileRefDao.select().in("pkey", keys).exec());
        Map<Long, FileRefEntity> map2 = CollectionUtil.list2Map(fileOldRefDao.select().in("pkey", keys).exec());
        for (FileAccessInfo item : r.getContent())
        {
            FileRefEntity oldRef = map2.get(item.getFilePkey());
            if (oldRef != null)
            {
                FileRefInfo info = BeanUtil.beanFrom(FileRefInfo.class, oldRef);
                if (info != null)
                {
                    info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                        .getFileBaseUrl(), "/v2/image", info.getPkey(), info.getExtName(), info.getMd5()));
                    item.setRef(info);
                }
            }
            FileRef2Entity ref = map.get(item.getFilePkey());
            if (ref != null)
            {
                FileRefInfo info = BeanUtil.beanFrom(FileRefInfo.class, ref);
                if (info != null)
                {
                    info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                        .getFileBaseUrl(), info.getType().viewUrl(), info.getPkey(), info.getExtName(), info.getMd5()));
                    item.setRef(info);
                }
            }
        }
        
        return new Result<>(r);
    }
    
    @Override
    public Result<DownloadReport> accessReport(StartDate startDate, EndDate endDate)
    {
        DownloadReport r = new DownloadReport();
        //总共下载多少次，流量，日均流量
        r.setTotal(fileAccessLogDao.total(startDate, endDate));
        if (r.getTotal().getMaxDate() != null && r.getTotal().getMinDate() != null)
        {
            long days =
                DateUtil.timeInterval(r.getTotal().getMinDate(), r.getTotal().getMaxDate(), ChronoUnit.DAYS) + 1;
            if (days > 0)
            {
                ReportItem perDay = new ReportItem();
                perDay.setName("日平均");
                perDay.setCount((int)(r.getTotal().getCount() / days));
                perDay.setSize(r.getTotal().getSize() / days);
                r.setPerDay(perDay);
            }
        }
        return new Result<>(r);
    }
    
    @Override
    public Result<List<FileDownloadReportItem>> accessReportByFile(boolean orderBySize, StartDate startDate,
        EndDate endDate)
    {
        List<FileDownloadReportItem> list;
        if (orderBySize)
            list = fileAccessLogDao.top10FileSize(startDate, endDate);
        else
            list = fileAccessLogDao.top10FileCount(startDate, endDate);
        List<Long> keys = new ArrayList<>();
        list.forEach(e -> keys.add(Long.parseLong(e.getName())));
        
        Map<Long, FileRef2Entity> map = CollectionUtil.list2Map(fileRefDao.select().in("pkey", keys).exec());
        Map<Long, FileRefEntity> map2 = CollectionUtil.list2Map(fileOldRefDao.select().in("pkey", keys).exec());
        for (FileDownloadReportItem item : list)
        {
            FileRefEntity oldRef = map2.get(Long.parseLong(item.getName()));
            if (oldRef != null)
            {
                FileRefInfo info = BeanUtil.beanFrom(FileRefInfo.class, oldRef);
                if (info != null)
                {
                    info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                        .getFileBaseUrl(), "/v2/image", info.getPkey(), info.getExtName(), info.getMd5()));
                    item.setRef(info);
                }
            }
            FileRef2Entity ref = map.get(Long.parseLong(item.getName()));
            if (ref != null)
            {
                FileRefInfo info = BeanUtil.beanFrom(FileRefInfo.class, ref);
                if (info != null)
                {
                    info.setUrl(FileUrlUtil.buildUrl(fileManageServer
                        .getFileBaseUrl(), info.getType().viewUrl(), info.getPkey(), info.getExtName(), info.getMd5()));
                    item.setRef(info);
                }
            }
        }
        return new Result<>(list);
    }
    
    @Override
    public Result<List<ReportItem>> accessReportByRef(Long filePkey, boolean orderBySize, StartDate startDate,
        EndDate endDate)
    {
        if (orderBySize)
            return new Result<>(fileAccessLogDao.top10Size(filePkey, "referer", null, startDate, endDate));
        else
            return new Result<>(fileAccessLogDao.top10Count(filePkey, "referer", null, startDate, endDate));
    }
    
    @Override
    public Result<List<ReportItem>> accessReportByIp(Long filePkey, boolean orderBySize, StartDate startDate,
        EndDate endDate)
    {
        if (orderBySize)
            return new Result<>(fileAccessLogDao.top10Size(filePkey, "ip", null, startDate, endDate));
        else
            return new Result<>(fileAccessLogDao.top10Count(filePkey, "ip", null, startDate, endDate));
    }
    
    @Override
    public Result<List<ReportItem>> accessReportByDevice(Long filePkey, boolean orderBySize, StartDate startDate,
        EndDate endDate)
    {
        if (orderBySize)
            return new Result<>(fileAccessLogDao.top10Size(filePkey, "deviceName", null, startDate, endDate));
        else
            return new Result<>(fileAccessLogDao.top10Count(filePkey, "deviceName", null, startDate, endDate));
    }
    
    @Override
    public Result<List<ReportItem>> accessReportByOs(Long filePkey, boolean orderBySize, StartDate startDate,
        EndDate endDate)
    {
        if (orderBySize)
            return new Result<>(fileAccessLogDao.top10Size(filePkey, "os", "osVersion", startDate, endDate));
        else
            return new Result<>(fileAccessLogDao.top10Count(filePkey, "os", "osVersion", startDate, endDate));
    }
    
}
