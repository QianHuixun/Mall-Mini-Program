package cn.tofocus.file.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.file.IdUtil;
import cn.tofocus.file.bean.FileByteResponse;
import cn.tofocus.file.bean.FileResponseV3;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.bean.UploadType;
import cn.tofocus.file.domain.FileServerV3;

@Component
public class FileControlV3
{
    @Autowired
    private FileServerV3 server;
    
    /**
     * 获取图片
     * @param file
     * @param thumb
     * @param response
     */
    public FileByteResponse viewImage(@RequestParam("file") String file, @RequestParam("code") String code,
        @RequestParam(defaultValue = "big") ThumbType thumb)
    {
        return outputFile(file, code, UploadType.image, thumb, null);
    }
    
    /**
     * 下载图片文件
     * @param file
     * @param thumb
     * @param response
     */
    public FileByteResponse downloadImage(@RequestParam("file") String file, @RequestParam("code") String code,
        @RequestParam(defaultValue = "orgin") ThumbType thumb)
    {
        return outputFile(file, code, UploadType.image, thumb, "application/x-download");
    }
    
    /**
     * 下载文件
     * @param file
     * @param response
     */
    public FileByteResponse downloadFile(@RequestParam("file") String file, @RequestParam("code") String code)
    {
        return outputFile(file, code, UploadType.file, null, "application/x-download");
    }
    
    private FileByteResponse outputFile(String id, String code, UploadType type, ThumbType thumb, String contentType)
    {
        FileByteResponse r = new FileByteResponse();
        Long pkey = IdUtil.extractId(id);
        if (pkey == null)
        {
            r.setStatus("id err");
        }
        FileResponseV3 fileResponse;
        try
        {
            fileResponse = server.download(type, pkey, code, thumb);
            if (fileResponse != null)
            {
                r.setFileName(fileResponse.getFileName());
                if (contentType != null)
                    r.setContentType(contentType);
                else if (fileResponse.getContentType() != null)
                    r.setContentType(fileResponse.getContentType());
                else
                    r.setContentType("image/jpeg");
                r.setFileContent(FileUtil.getBytes(fileResponse.getFile()));
                r.setStatus("200");
            }
            else
            {
                r.setStatus("404");
            }
        }
        catch (TofocusException e1)
        {
            r.setStatus("TofocusException");
        }
        catch (Exception e1)
        {
            r.setStatus("Exception");
        }
        finally
        {
        }
        return r;
    }
    
}
