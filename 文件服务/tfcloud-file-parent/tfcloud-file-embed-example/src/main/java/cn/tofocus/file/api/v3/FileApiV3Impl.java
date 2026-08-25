package cn.tofocus.file.api.v3;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.FileServer;
import cn.tofocus.db.file.FileUrlUtil;
import cn.tofocus.db.file.IdUtil;
import cn.tofocus.db.file.RefChangeList;
import cn.tofocus.file.bean.*;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.dao.FileRef2Dao;
import cn.tofocus.file.db.dao.FileRefLinkDao;
import cn.tofocus.file.db.entity.FileRecordEntity;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.domain.FileServerV3;

@Component
public class FileApiV3Impl
{
    @Autowired
    private FileServerV3 server;
    
    @Autowired
    private FileServer fileManageServer;

    @Autowired
    private FileRecordDao fileRecordDao;
    
    @Autowired
    private FileRef2Dao fileRefDao;
    
    @Autowired
    private FileRefLinkDao fileRefLinkDao;
    
    public Result<FileInfoV3> uploadImage(MultipartFile file, String title, String memo)
    {
        FileInfoV3 info = server.uploadImage(file, null, title, memo);
        return new Result<>(info);
    }
    
    public Result<?> createImageThumb(String file, ThumbType thumb, boolean force)
    {
        Long pkey = IdUtil.extractId(file);
        if (pkey != null)
        {
            server.createImageThumb(pkey, thumb, force);
        }
        return new Result<>();
    }
    
    public Result<FileInfoV3> uploadFile(MultipartFile file, String title, String memo)
    {
        FileInfoV3 info = server.uploadFile(file, null, title, memo);
        return new Result<>(info);
    }
    
    public Result<FileInfoV3> getFileInfo(String file)
    {
        Long pkey = IdUtil.extractId(file);
        FileInfoV3 info = server.getFileInfo(pkey);
        return new Result<>(info);
    }
    
    public Result<?> referencesFiles(RefChangeList ref)
    {
        server.referencesFiles(ref.getDomain(), ref.getDb(), ref.getTable(), ref.getRefs());
        return new Result<>();
    }
    
    public Result<?> referencesAllbyTable(RefChangeList ref)
    {
        server.referencesAllbyTable(ref.getDomain(), ref.getDb(), ref.getTable(), ref.getRefs());
        return new Result<>();
    }
    
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
    
    public Result<FileReport> aggFileByMime(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRecordDao.aggFileByMime(startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggRefByApp(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefDao.aggRefByApp(startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggRefByExt(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefDao.aggRefByExt(startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggByDomain(StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByDomain(startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggByDb(String domain, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByDb(domain, startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggByTable(String domain, String db, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByTable(domain, db, startDate, endDate);
        return new Result<>(info);
    }
    
    public Result<FileReport> aggByOrg(String domain, StartDate startDate, EndDate endDate)
    {
        FileReport info = fileRefLinkDao.aggByOrg(domain, startDate, endDate);
        return new Result<>(info);
    }
    
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
    
}
