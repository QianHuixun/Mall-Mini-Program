package cn.tofocus.file.domain;

import java.io.File;
import java.io.FileInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.file.bean.FileRecord;
import cn.tofocus.file.bean.FileRef;
import cn.tofocus.file.bean.FileResponse;
import cn.tofocus.file.db.service.FileRecordService;
import cn.tofocus.file.db.service.FileRefService;
import cn.tofocus.file.exception.FileErrCode;

@Component
@Deprecated
public class FileServer
{
    @Autowired
    private FileRefService fileRefCache;
    
    @Autowired
    private FileRecordService fileRecordCache;
    
    @Value("${tofocus.file.root}")
    private String root;
    
    @Deprecated
    private String makePath(String md5)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(root);
        sb.append(File.separator);
        sb.append(md5.substring(0, 2));
        sb.append(File.separator);
        sb.append(md5.substring(2, 4));
        return sb.toString();
    }
    
    @Deprecated
    private String makeFullPath(String path, String md5, long filesize)
    {
        StringBuilder filePath = new StringBuilder();
        filePath.append(path);
        filePath.append(File.separator);
        filePath.append(md5);
        filePath.append("_");
        filePath.append(filesize);
        return filePath.toString();
    }
    
    @Deprecated
    private String makeFullPath(String md5, long filesize)
    {
        return makeFullPath(makePath(md5), md5, filesize);
    }
    
    @Deprecated
    public FileResponse getFile(long id, String code)
        throws Exception
    {
        FileRef ref = fileRefCache.getFileRef(id);
        if (ref != null)
        {
            if (!ref.getMd5().equals(code))
                throw TofocusException.of(FileErrCode.FILE_DOWNLOAD_CHECK_FAIL);
            FileRecord fileRecord = fileRecordCache.getFileRecord(ref.getMd5(), ref.getSize());
            if (fileRecord != null)
            {
                FileResponse res = new FileResponse();
                res.setContentType(fileRecord.getContentType());
                res.setFileName(ref.getFileName());
                res.setInputStream(new FileInputStream(new File(makeFullPath(ref.getMd5(), ref.getSize()))));
                
                return res;
            }
        }
        return null;
    }
    
    @Deprecated
    public FileRef getFileRef(long id)
    {
        FileRef ref = fileRefCache.getFileRef(id);
        return ref;
    }
    
}
