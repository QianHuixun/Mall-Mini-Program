package cn.tofocus.file.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.ImageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.ImageUtil.ImageType;
import cn.tofocus.common.util.MediaTypeUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.common.util.security.MD5;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.db.file.RefChange;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.FileRecord;
import cn.tofocus.file.bean.FileRef;
import cn.tofocus.file.bean.FileRef2;
import cn.tofocus.file.bean.FileRefLink;
import cn.tofocus.file.bean.FileResponseV3;
import cn.tofocus.file.bean.MemoryMultipartFile;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.bean.UploadType;
import cn.tofocus.file.config.UeConfig;
import cn.tofocus.file.db.service.FileRecordService;
import cn.tofocus.file.db.service.FileRef2Service;
import cn.tofocus.file.db.service.FileRefLinkService;
import cn.tofocus.file.db.service.FileRefService;
import cn.tofocus.file.ueditor.ActionEnum;
import cn.tofocus.file.ueditor.StateEnum;
import cn.tofocus.file.ueditor.UeImgResult;
import cn.tofocus.file.ueditor.UeImgsResult;
import cn.tofocus.file.ueditor.UeResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public abstract class BaseFileServer
{
    @Value("${tofocus.file.root}")
    private String root;
    
    @Autowired
    private FileRecordService fileRecordCache;
    
    @Autowired
    private FileRefService oldRefDao;
    
    @Autowired
    private FileRef2Service fileRefCache;
    
    @Autowired
    private FileRefLinkService fileRefLinkDao;
    
    @Autowired
    private cn.tofocus.db.file.FileServer fileManageServer;
    
    @Autowired
    private UeConfig ueConfig;
    
    private static final String JPG_CONTENT_TYPE = "image/jpeg";
    
    protected abstract long newPkey();
    
    @SneakyThrows
    public FileInfoV3 uploadImage(MultipartFile file, String[] expectExts, String title, String memo)
    {
        //文件类型检测
        String contentType = null;
        try (InputStream fio = file.getInputStream();)
        {
            contentType = MediaTypeUtil.detect(fio, file.getOriginalFilename());
        }
        //扩展名判断
        if (expectExts != null && !expectType(expectExts).contains(contentType))
        {
            throw TofocusException.of(SysErrCode.FILETYPE_IS_NOT_EXPECTED);
        }
        
        //文件类型检测
        ImageType type = null;
        try (InputStream fio = file.getInputStream();)
        {
            type = ImageUtil.checkImgType(fio);
        }
        if (type == null)
            throw TofocusException.of(SysErrCode.FILE_IS_NOT_IMG);
        //保存文件
        byte[] fileContent = file.getBytes();
        FileRecord fileRecord = saveFile(type.getMime(), fileContent, type.getWidth(), type.getHeight());
        
        //保存FileRef2Entity
        FileRef2 ref =
            saveFileRef(null, fileRecord, file.getOriginalFilename(), title, memo, UploadType.image, null, null, null);
        FileInfoV3 info = ref.toFileInfo(fileManageServer.getFileBaseUrl());
        info.setContentType(fileRecord.getContentType());
        return info;
    }
    
    private Set<String> expectType(String[] exts)
    {
        Tika tika = new Tika();
        Set<String> set = new HashSet<>();
        for (String ext : exts)
        {
            set.add(tika.detect(ext));
        }
        return set;
    }
    
    @SneakyThrows
    public FileInfoV3 uploadFile(MultipartFile file, String[] expectExts, String title, String memo)
    {
        //文件类型检测
        String contentType = null;
        try (InputStream fio = file.getInputStream();)
        {
            contentType = MediaTypeUtil.detect(fio, file.getOriginalFilename());
        }
        //扩展名判断
        if (expectExts != null && !expectType(expectExts).contains(contentType))
        {
            throw TofocusException.of(SysErrCode.FILETYPE_IS_NOT_EXPECTED);
        }
        //保存文件
        byte[] fileContent = file.getBytes();
        FileRecord fileRecord = saveFile(contentType, fileContent, null, null);
        //保存FileRef2Entity
        FileRef2 ref =
            saveFileRef(null, fileRecord, file.getOriginalFilename(), title, memo, UploadType.file, null, null, null);
        FileInfoV3 info = ref.toFileInfo(fileManageServer.getFileBaseUrl());
        info.setContentType(fileRecord.getContentType());
        return info;
    }
    
    @SneakyThrows
    private FileRecord saveFile(String contentType, byte[] fileContent, Integer width, Integer height)
    {
        long filesize = fileContent.length;
        String md5 = MD5.getMD5(fileContent);
        //检查文件是否已存在
        FileRecord fileRecord = fileRecordCache.getFileRecord(md5, filesize);
        if (fileRecord == null)
        {
            PathBuilder pb = filePath(md5, filesize);
            //准备文件目录
            FileUtil.checkDirectory(pb.buildPath());
            
            //保存文件
            FileUtil.saveFile(pb.build(), fileContent);
            
            //增加文件记录
            fileRecord = new FileRecord();
            fileRecord.setMd5(md5);
            fileRecord.setSize(filesize);
            fileRecord.setWidth(width);
            fileRecord.setHeight(height);
            fileRecord.setContentType(contentType);
            fileRecord.setCreatedTime(new Date());
            fileRecordCache.addFileRecord(fileRecord);
        }
        return fileRecord;
    }
    
    private FileRef2 saveFileRef(Long pkey, FileRecord fileRecord, String originalFilename, String title, String memo,
        UploadType type, String appid, Long userkey, Date createdTime)
    {
        AuthenticationContext c = SecurityContextUtil.getAuthenticationContext();
        //保存FileRef2Entity
        if (pkey == null)
            pkey = newPkey();
        FileRef2 ref = new FileRef2();
        ref.setPkey(pkey);
        ref.setMd5(fileRecord.getMd5());
        ref.setSize(fileRecord.getSize());
        ref.setType(type);
        ref.setFileName(originalFilename);
        if (originalFilename != null)
        {
            ref.setExtName(Util.getExtfromFilename(originalFilename));
        }
        ref.setTitle(title);
        ref.setMemo(memo);
        if (appid == null)
            ref.setAppid(c.getClientId());
        else
            ref.setAppid(appid);
        if (userkey == null)
            ref.setUserkey(c.getUserkey());
        else
            ref.setUserkey(userkey);
        ref.setCreatedTime(createdTime);
        fileRefCache.addFileRef(ref);
        return ref;
    }
    
    @SneakyThrows
    private void createImgThumb(FileRecord fileRecord, byte[] fileContent, ThumbType thumb, boolean force)
    {
        int thumbSize = thumb2Size(thumb);
        if (thumbSize > 0)
        {
            String fullPath = thumbPath(fileRecord.getMd5(), fileRecord.getSize(), thumbSize).build();
            if (force || !FileUtil.isFileExsit(fullPath))
            {
                //保存缩略图
                byte[] thumbContent = ImageUtil.reSize(fileRecord.getContentType(), fileContent, thumbSize, thumbSize);
                if (thumbContent != null)
                {
                    FileUtil.saveFile(fullPath, thumbContent);
                }
            }
        }
    }
    
    public static int thumb2Size(ThumbType thumb)
    {
        int thumbSize;
        switch (thumb)
        {
            case small:
                thumbSize = 136;
                break;
            case big:
                thumbSize = 1920;
                break;
            case orgin:
            default:
                thumbSize = -1;
                break;
        }
        return thumbSize;
    }
    
    public PathBuilder filePath(String md5, long filesize)
    {
        PathBuilder pb = new PathBuilder().root(root).md5(md5).size(filesize);
        return pb;
    }
    
    public PathBuilder thumbPath(String md5, long filesize, int thumb)
    {
        PathBuilder pb = new PathBuilder().root(root).md5(md5).size(filesize).thumb(thumb);
        return pb;
    }
    
    public FileResponseV3 download(UploadType type, long id, String code, ThumbType thumb)
    {
        FileRef2 ref = fileRefCache.getFileRef(id);
        if (ref != null && ref.getMd5().equals(code) && ref.getType().equals(type))
        {
            FileRecord fileRecord = fileRecordCache.getFileRecord(ref.getMd5(), ref.getSize());
            if (fileRecord != null)
            {
                return findFile(thumb, fileRecord, ref.getFileName());
            }
        }
        return null;
    }
    
    @SneakyThrows
    private FileResponseV3 findFile(ThumbType thumb, FileRecord fileRecord, String fileName)
    {
        if (thumb == null)
            thumb = ThumbType.orgin;
        String md5 = fileRecord.getMd5();
        long filesize = fileRecord.getSize();
        if (fileRecord.getContentType().startsWith("image/")
            && (fileRecord.getWidth() == null || fileRecord.getHeight() == null))
        {
            //重新计算图片的长和宽
            File file = new File(filePath(md5, filesize).build());
            try (FileInputStream input = new FileInputStream(file);)
            {
                ImageType r = ImageUtil.checkImgType(input);
                if (r != null)
                {
                    fileRecord.setContentType(r.getMime());
                    fileRecord.setWidth(r.getWidth());
                    fileRecord.setHeight(r.getHeight());
                    fileRecordCache.updateFileRecord(fileRecord);
                }
            }
        }
        //如果原图尺寸小于缩略图，直接返回原图内容
        if (isReturnOrgin(thumb, fileRecord))
            thumb = ThumbType.orgin;
        if (ThumbType.orgin.equals(thumb))
        {
            //返回原图
            File file = new File(filePath(md5, filesize).build());
            if (file.exists())
            {
                return new FileResponseV3(file, fileName, fileRecord.getContentType());
            }
            else
                return null;
        }
        else
        {
            //返回缩略图
            File thumbfile = new File(thumbPath(md5, filesize, thumb2Size(thumb)).build());
            if (thumbfile.exists())
            {
                return new FileResponseV3(thumbfile, fileName, JPG_CONTENT_TYPE);
            }
            else
            {
                //缩略图不存在，创建缩略图
                File file = new File(filePath(md5, filesize).build());
                if (file.exists())
                {
                    byte[] fileContent = FileUtil.readFileContent(file.getAbsolutePath());
                    createImgThumb(fileRecord, fileContent, thumb, false);
                    thumbfile = new File(thumbPath(md5, filesize, thumb2Size(thumb)).build());
                    return new FileResponseV3(thumbfile, fileName, JPG_CONTENT_TYPE);
                }
                else
                    return null;
            }
        }
    }
    
    private boolean isReturnOrgin(ThumbType thumb, FileRecord fileRecord)
    {
        if (fileRecord.getHeight() == null || fileRecord.getWidth() == null)
            return true;
        else
        {
            int thumbSize = thumb2Size(thumb);
            return (fileRecord.getHeight() <= thumbSize && fileRecord.getWidth() <= thumbSize);
        }
    }
    
    public FileInfoV3 getFileInfo(Long pkey)
    {
        if (pkey != null)
        {
            FileRef2 ref = fileRefCache.getFileRef(pkey);
            if (ref != null)
            {
                return ref.toFileInfo(fileManageServer.getFileBaseUrl());
            }
        }
        return null;
    }
    
    @SneakyThrows
    public void createImageThumb(Long pkey, ThumbType thumb, boolean force)
    {
        FileRef2 ref = fileRefCache.getFileRef(pkey);
        //文件类型检测
        if (ref != null && UploadType.image.equals(ref.getType()))
        {
            FileRecord fileRecord = fileRecordCache.getFileRecord(ref.getMd5(), ref.getSize());
            if (fileRecord != null)
            {
                //读取文件
                byte[] fileContent = FileUtil.getBytes(filePath(ref.getMd5(), ref.getSize()).build());
                //保存缩略图
                createImgThumb(fileRecord, fileContent, thumb, force);
            }
        }
    }
    
    public void referencesFiles(String domain, String db, String table, List<RefChange> refs)
    {
        List<String> pkeys = new ArrayList<>();
        for (RefChange r : refs)
        {
            pkeys.add(r.getPkey());
        }
        fileRefLinkDao.delByPkey(domain, db, table, pkeys);
        saveRef(domain, db, table, refs);
    }
    
    public void referencesAllbyTable(String domain, String db, String table, List<RefChange> refs)
    {
        fileRefLinkDao.delByTable(domain, db, table);
        saveRef(domain, db, table, refs);
    }
    
    private void saveRef(String domain, String db, String table, List<RefChange> refs)
    {
        Set<Long> refPkeys = new HashSet<>();
        for (RefChange r : refs)
        {
            if (CollectionUtil.isNotEmpty(r.getId()))
            {
                for (Long l : r.getId())
                {
                    refPkeys.add(l);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(refPkeys))
        {
            List<FileRef2> refList = fileRefCache.listByPkeys(refPkeys);
            if (CollectionUtil.isNotEmpty(refList))
            {
                Map<Long, FileRef2> refMap = CollectionUtil.list2Map(refList);
                List<FileRefLink> list = new ArrayList<>();
                for (RefChange r : refs)
                {
                    if (CollectionUtil.isNotEmpty(r.getId()))
                    {
                        for (Long l : r.getId())
                        {
                            FileRef2 ref = refMap.get(l);
                            if (ref != null)
                            {
                                FileRefLink entity = new FileRefLink(r);
                                entity.setDomain(domain);
                                entity.setDb(db);
                                entity.setTable(table);
                                entity.setFilePkey(l);
                                entity.setSize(ref.getSize());
                                list.add(entity);
                            }
                        }
                    }
                }
                fileRefLinkDao.putAllFileRefLink(list);
            }
        }
    }
    
    public FileInfoV3 updateFileRef(Long pkey, String domain, String db, String table, String dataPkey, String org,
        String dept, UploadType upType)
    {
        //读取 FileRef
        FileRef oldRef = oldRefDao.getFileRef(pkey);
        if (oldRef != null)
        {
            FileRecord fileRecord = fileRecordCache.getFileRecord(oldRef.getMd5(), oldRef.getSize());
            if (fileRecord != null)
            {
                // 插入FileRefEntity
                FileRef2 ref = saveFileRef(oldRef.getPkey(),
                    fileRecord,
                    oldRef.getFileName(),
                    oldRef.getTitle(),
                    "旧数据升级",
                    upType,
                    oldRef.getAppid(),
                    oldRef.getUserkey(),
                    oldRef.getCreatedTime());
                FileInfoV3 info = ref.toFileInfo(fileManageServer.getFileBaseUrl());
                info.setContentType(fileRecord.getContentType());
                
                // 插入refLink
                RefChange r = new RefChange();
                r.setPkey(dataPkey);
                r.setOrg(org);
                r.setDept(dept);
                r.setId(Collections.singletonList(pkey));
                referencesFiles(domain, db, table, Collections.singletonList(r));
                
                // 删除 FileRef
                oldRefDao.removeById(pkey);
                return info;
            }
        }
        else
        {
            FileRef2 ref = fileRefCache.getFileRef(pkey);
            if (ref != null)
            {
                return ref.toFileInfo(fileManageServer.getFileBaseUrl());
            }
        }
        return null;
    }
    
    public Object ueditorAction(MultipartFile file, ActionEnum action, String referer)
    {
        switch (action)
        {
            case uploadimage:
                if (file == null)
                    return new UeResult(StateEnum.NOTFOUND_UPLOAD_DATA);
                String ext = Util.getExtfromFilename(file.getOriginalFilename());
                if (!ueConfig.checkImgExt(ext))
                    return new UeResult(StateEnum.NOT_ALLOW_FILE_TYPE);
                try
                {
                    if (referer == null)
                        referer = "ue";
                    else
                        referer = StringUtil.limitString(referer, 255);
                    
                    // 压缩图片最长边不大于配置的限制
                    int thumbSize = ueConfig.getImageCompressBorder();
                    byte[] fileContent = file.getBytes();
                    byte[] thumbContent = ImageUtil.reSize(null, fileContent, thumbSize, thumbSize);
                    MultipartFile tmpFile = null;
                    if (thumbContent != null)
                        tmpFile = new MemoryMultipartFile(file.getName(), file.getOriginalFilename(), "image/jpeg",
                            thumbContent);
                    FileInfoV3 info = uploadImage(tmpFile != null ? tmpFile : file, null, "ue", referer);
                    
                    UeImgResult result = new UeImgResult(info);
                    return result;
                }
                catch (Exception e)
                {
                    log.error("富文本图片上传异常", e);
                    return new UeResult(StateEnum.IO_ERROR);
                }
            default:
                return new UeResult(StateEnum.INVALID_ACTION);
        }
    }
    
    @SneakyThrows
    public Object ueditorUploadImages(MultipartFile[] files, ActionEnum action, String referer)
    {
        switch (action)
        {
            case uploadimage:
                if (referer == null)
                    referer = "ue";
                else
                    referer = StringUtil.limitString(referer, 255);
                if (files == null)
                    return new UeResult(StateEnum.NOTFOUND_UPLOAD_DATA);
                for (MultipartFile file : files)
                {
                    if (file == null)
                        return new UeResult(StateEnum.NOTFOUND_UPLOAD_DATA);
                    String ext = Util.getExtfromFilename(file.getOriginalFilename());
                    if (!ueConfig.checkImgExt(ext))
                        return new UeResult(StateEnum.NOT_ALLOW_FILE_TYPE);
                }
                UeImgsResult result = new UeImgsResult(StateEnum.SUCCESS);
                for (MultipartFile file : files)
                {
                    try
                    {
                        // 压缩图片最长边不大于配置的限制
                        int thumbSize = ueConfig.getImageCompressBorder();
                        byte[] fileContent = file.getBytes();
                        byte[] thumbContent = ImageUtil.reSize(null, fileContent, thumbSize, thumbSize);
                        MultipartFile tmpFile = null;
                        if (thumbContent != null)
                            tmpFile = new MemoryMultipartFile(file.getName(), file.getOriginalFilename(), "image/jpeg",
                                thumbContent);
                        FileInfoV3 info = uploadImage(tmpFile != null ? tmpFile : file, null, "ue", referer);
                        result.addUrl(info.getFileName(), info.getFileName(), info.getUrl());
                    }
                    catch (Exception e)
                    {
                        log.error("富文本图片上传异常", e);
                        return new UeResult(StateEnum.IO_ERROR);
                    }
                }
                return result;
            default:
                return new UeResult(StateEnum.INVALID_ACTION);
        }
    }
}
